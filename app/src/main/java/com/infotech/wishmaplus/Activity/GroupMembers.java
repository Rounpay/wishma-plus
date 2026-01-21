package com.infotech.wishmaplus.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Adapter.MembersListAdapter;
import com.infotech.wishmaplus.Api.Request.UpdateGroupMemberRequest;
import com.infotech.wishmaplus.Api.Response.GroupMembersResponse;
import com.infotech.wishmaplus.Api.Response.GroupMembersUpdateResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.UtilMethods;

public class GroupMembers extends AppCompatActivity {

    private String groupId;
    private CustomLoader loader;
    RecyclerView rv;
    MembersListAdapter adapter;

    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_members);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        rv = findViewById(R.id.rvPeople);
        etSearch = findViewById(R.id.etSearch);
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        rv.setLayoutManager(new LinearLayoutManager(this));
        Intent intentParam = getIntent();
        if (intentParam != null && intentParam.hasExtra("groupId")) {
            groupId = intentParam.getStringExtra("groupId");
        }
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        getGroupsMembers();
    }
    public void getGroupsMembers(){
        loader.show();
        UtilMethods.INSTANCE.getGroupsMembers(groupId,new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }

                GroupMembersResponse groupMembersResponse=(GroupMembersResponse) object;
                if(groupMembersResponse.getStatusCode()==1){
                    adapter = new MembersListAdapter(
                            GroupMembers.this,
                            groupMembersResponse.getResult(),
                            (user, position) -> {
                                // Handle Add / Invite click
                                updateGroupMembers(user.getUserId());
                            }
                    );

                    rv.setAdapter(adapter);
                }


            }

            @Override
            public void onError(String msg) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }

            }
        });
    }
    public void updateGroupMembers(String userId){
        UpdateGroupMemberRequest request = new UpdateGroupMemberRequest (
                groupId,
                userId,
                false
        );
        loader.show();
        UtilMethods.INSTANCE.updateGroupMembers(request,new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }

                GroupMembersUpdateResponse groupMembersUpdateResponse=(GroupMembersUpdateResponse) object;
                if(groupMembersUpdateResponse.getStatusCode()==1){
                    Toast.makeText(GroupMembers.this, groupMembersUpdateResponse.getResponseText(), Toast.LENGTH_SHORT).show();
                    getGroupsMembers();
                }


            }

            @Override
            public void onError(String msg) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }

            }
        });
    }
}