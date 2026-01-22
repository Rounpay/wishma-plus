package com.infotech.wishmaplus.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Adapter.BlockedAdapter;
import com.infotech.wishmaplus.Api.Request.BlockUserRequest;
import com.infotech.wishmaplus.Api.Request.InitiateBoostRequest;
import com.infotech.wishmaplus.Api.Request.UpdateGroupMemberRequest;
import com.infotech.wishmaplus.Api.Response.BlockUserResponse;
import com.infotech.wishmaplus.Api.Response.BlockedUserListResponse;
import com.infotech.wishmaplus.Api.Response.BoostResponse;
import com.infotech.wishmaplus.Api.Response.GroupMembersUpdateResponse;
import com.infotech.wishmaplus.Api.Response.UserModel;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.payu.ui.model.listeners.PayUHashGenerationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class BlockedUser extends AppCompatActivity {

    RecyclerView recyclerBlocked;
    ArrayList<UserModel> list;
    BlockedAdapter adapter;
    LinearLayout btnAddBlocked, noDataLayout;
    private CustomLoader loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blocked_user);
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerBlocked = findViewById(R.id.recyclerBlocked);
        btnAddBlocked = findViewById(R.id.btnAddBlocked);
        noDataLayout = findViewById(R.id.noDataLayout);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);

        list = new ArrayList<>();
        list.add(new UserModel("User Name 1", R.drawable.user_icon));
        list.add(new UserModel("User Name 2", R.drawable.user_icon));


        btnAddBlocked.setOnClickListener(view -> {
            Intent intent = new Intent(this, YourFriends.class);
            intent.putExtra("isFromBlockList", true);
            startActivity(intent);
        });

        getBlockedUserList();
    }
    public void showUnblockDialog(BlockedUserListResponse.Result model) {
        String userName = model.getFullName();

        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        blurBackground(rootView);

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_unblock);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView btnUnblock = dialog.findViewById(R.id.btnUnblock);
        TextView txtDescription = dialog.findViewById(R.id.txtDescription);
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        TextView textTag = dialog.findViewById(R.id.textTag);
        TextView textWait = dialog.findViewById(R.id.textWait);
        txtDescription.setText("If you unblock "+userName+" he/she may be able to see your Timeline or contact you, depending on your settings.");
        textTag.setText("Tags you and "+userName+" previously added to each other may be restored.");
        textWait.setText("You'll have to wait 48 hours if you want to block "+userName+" again.");
        txtTitle.setText("Unblock "+userName+"?");

        dialog.setOnDismissListener(dialogInterface -> removeBlur(rootView));
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnUnblock.setOnClickListener(v -> {
            unblockUser(model.getUserId(),model.getBlockId());
            dialog.dismiss();
        });

        dialog.show();
    }
    public void getBlockedUserList(){
        loader.show();
        UtilMethods.INSTANCE.getBlockedUserList(new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }

                BlockedUserListResponse blockedUserListResponse=(BlockedUserListResponse) object;
                if(blockedUserListResponse.getStatusCode()==1){
                    noDataLayout.setVisibility(View.GONE);
                    recyclerBlocked.setVisibility(View.VISIBLE);
                    if(blockedUserListResponse.getResult()!=null && blockedUserListResponse.getResult().size()>0){
                        adapter = new BlockedAdapter(BlockedUser.this, blockedUserListResponse.getResult(),(position, model) -> {
                            showUnblockDialog(model);
                        });
                        recyclerBlocked.setLayoutManager(new LinearLayoutManager(BlockedUser.this));
                        recyclerBlocked.setAdapter(adapter);
                    }
                    else {
                        noDataLayout.setVisibility(View.VISIBLE);
                        recyclerBlocked.setVisibility(View.GONE);
                    }
//                    getGroupsMembers();
                }
                else{
                    noDataLayout.setVisibility(View.VISIBLE);
                    recyclerBlocked.setVisibility(View.GONE);
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

    public void unblockUser(String userId, int blockedId){
        loader.show();
        BlockUserRequest request = new BlockUserRequest(userId,blockedId);
        UtilMethods.INSTANCE.blockUser(request, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                BlockUserResponse blockUserResponse =(BlockUserResponse) object;
                if(blockUserResponse.getStatusCode()==1){
                    Toast.makeText(BlockedUser.this, blockUserResponse.getResponseText(), Toast.LENGTH_SHORT).show();
                    getBlockedUserList();
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
    private void blurBackground(View rootView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RenderEffect blurEffect = RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP);
            rootView.setRenderEffect(blurEffect);
        }
    }
    private void removeBlur(View rootView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rootView.setRenderEffect(null);
        }
    }
}