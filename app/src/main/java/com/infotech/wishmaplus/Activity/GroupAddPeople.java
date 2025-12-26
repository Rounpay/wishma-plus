package com.infotech.wishmaplus.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Adapter.UserListAdapter;
import com.infotech.wishmaplus.Api.Request.AddFriendsRequest;
import com.infotech.wishmaplus.Api.Response.AddPeopleResponse;
import com.infotech.wishmaplus.Api.Response.GetUserListResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.UtilMethods;

public class GroupAddPeople extends AppCompatActivity {
    private String groupId;
    private CustomLoader loader;
    RecyclerView rv;
    UserListAdapter adapter;

    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_add_people);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        rv = findViewById(R.id.rvPeople);
        etSearch = findViewById(R.id.etSearch);
        findViewById(R.id.back_button).setOnClickListener(view -> {
            finish();
        });
        rv.setLayoutManager(new LinearLayoutManager(this));
        Intent intentParam = getIntent();
        if (intentParam != null && intentParam.hasExtra("groupId")) {
            groupId = intentParam.getStringExtra("groupId");
        }
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);


        findViewById(R.id.search_button).setOnClickListener(view -> {
            Intent intent = new Intent(GroupAddPeople.this, AddCoverPhotoGroup.class);
            intent.putExtra("groupId", groupId);
//            intent.putExtra("groupId", "31E1B0C1-B0B7-401B-95AD-36F9BDB07E40");
            startActivityForResult(
                    intent,
                    102
            );
//            startActivity(intent);
        });
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

        getUsersList();

    }
    public void getUsersList(){
        loader.show();
        UtilMethods.INSTANCE.getUsersList(new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }

                GetUserListResponse getUserListResponse=(GetUserListResponse) object;
                if(getUserListResponse.getStatusCode()==1){
                    adapter = new UserListAdapter(
                            GroupAddPeople.this,
                            getUserListResponse.getResult(),
                            (user, position) -> {
                                // Handle Add / Invite click
                                addPeopleInGroup(user.getUserId());
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
    public void addPeopleInGroup(String userId){
        AddFriendsRequest request = new AddFriendsRequest(
                groupId,
                userId
        );
        loader.show();
        UtilMethods.INSTANCE.addPeopleInGroup(request,new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }

                AddPeopleResponse addPeopleResponse=(AddPeopleResponse) object;
                if(addPeopleResponse.getStatusCode()==1){
                    Toast.makeText(GroupAddPeople.this, addPeopleResponse.getResponseText(), Toast.LENGTH_SHORT).show();
                    getUsersList();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}