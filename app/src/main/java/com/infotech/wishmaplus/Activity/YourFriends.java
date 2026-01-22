package com.infotech.wishmaplus.Activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.infotech.wishmaplus.Adapter.FriendsListAdapter;
import com.infotech.wishmaplus.Api.Request.BlockUserRequest;
import com.infotech.wishmaplus.Api.Request.ComplaintRequest;
import com.infotech.wishmaplus.Api.Response.BlockUserResponse;
import com.infotech.wishmaplus.Api.Response.ComplaintSubmitResponse;
import com.infotech.wishmaplus.Api.Response.FriendListResponse;
import com.infotech.wishmaplus.Api.Response.FriendUserModel;
import com.infotech.wishmaplus.Api.Response.UnfriendResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.Objects;

public class YourFriends extends AppCompatActivity {

    View noDataLayout;
    RecyclerView recyclerView;
    FriendsListAdapter adapter;
    private CustomLoader loader;
    public PreferencesManager tokenManager;
    UserDetailResponse userDetailResponse;
    FriendListResponse friendListResponse = new FriendListResponse();
    BottomSheetDialog bottomSheetDialog;
    boolean isFromBlockList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_your_friends);
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        SwipeRefreshLayout pullToRefresh = findViewById(R.id.swipeRefreshLayout);
        tokenManager = new PreferencesManager(this,1);
        userDetailResponse = UtilMethods.INSTANCE.getUserDetailResponse(tokenManager);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        Intent intent = getIntent();
        if(intent.getBooleanExtra("isFromBlockList",false))
        {
            isFromBlockList = intent.getBooleanExtra("isFromBlockList",false);
        }
        pullToRefresh.setOnRefreshListener(() -> {
//            hitApi();
            pullToRefresh.setRefreshing(false);
        });
        recyclerView = findViewById(R.id.recyclerView);
        noDataLayout = findViewById(R.id.noDataLayout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getFriendList();
    }
    private void getFriendList(){
        loader.show();
        UtilMethods.INSTANCE.getFriendList(new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }

                friendListResponse = (FriendListResponse) object;
                if(friendListResponse.getStatusCode()==1){
                    adapter = new FriendsListAdapter(YourFriends.this, friendListResponse.getResult(),isFromBlockList, new FriendsListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(FriendUserModel user, int pos) {

                        }

                        @Override
                        public void onMoreClicked(View anchor, FriendUserModel user, int pos) {
                            openBottomSheet(YourFriends.this,user);
                        }

                        @Override
                        public void onProfileClick(FriendUserModel user, int position) {
                            //                startActivity(new Intent(FriendRequest.this, ProfileActivity.class));
                            profileActivityResultLauncher.launch(new Intent(YourFriends.this, ProfileActivity.class)
                                    .putExtra("userData", userDetailResponse)
                                    .putExtra("id", user.getUserId()));
                        }

                        @Override
                        public void onBlockClick(FriendUserModel user, int position) {
                            finish();

                        }
                    });
                    recyclerView.setLayoutManager(new LinearLayoutManager(YourFriends.this));
                    recyclerView.setAdapter(adapter);
                    recyclerView.addItemDecoration(new DividerItemDecoration(YourFriends.this, DividerItemDecoration.VERTICAL));

                }
                updateEmptyView();

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
    private void updateEmptyView() {
        if (friendListResponse.getResult().isEmpty()) {
            recyclerView.setVisibility(GONE);
            noDataLayout.setVisibility(VISIBLE);
        } else {
            recyclerView.setVisibility(VISIBLE);
            noDataLayout.setVisibility(GONE);
        }
    }
    public void openBottomSheet(Activity context,FriendUserModel user) {

        if (bottomSheetDialog != null && bottomSheetDialog.isShowing())
            return;

        bottomSheetDialog = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_profile_options, null);

        View unfollow = sheetView.findViewById(R.id.unfollow);
        View block = sheetView.findViewById(R.id.block);
        View unfriend = sheetView.findViewById(R.id.unfriend);

        unfollow.setOnClickListener(v -> bottomSheetDialog.dismiss());
        block.setOnClickListener(v -> {
            openBlockDialog(user);
            bottomSheetDialog.dismiss();
        });
        unfriend.setOnClickListener(v -> {
            showUnfriendDialog(context,user);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(sheetView);
        BottomSheetBehavior.from(
                        Objects.requireNonNull(bottomSheetDialog.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet)))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetDialog.show();
    }
    private void showUnfriendDialog(Context context,FriendUserModel user) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_unfriend);
        dialog.setCancelable(true);

        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView btnConfirm = dialog.findViewById(R.id.btnConfirm);
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);

        tvTitle.setText("Unfriend "+user.getFullName());
        tvMessage.setText("Are you sure you want to remove "+user.getFullName()+" as your friend?");


        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            unFriendUser(user.getUserId());
            dialog.dismiss();
        });

        dialog.show();
    }
    private void openBlockDialog(FriendUserModel user) {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        blurBackground(rootView);
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_block_user);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView btnBlock = dialog.findViewById(R.id.btnBlock);
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        TextView txtDesc = dialog.findViewById(R.id.txtDesc);
        TextView friendUnfriend = dialog.findViewById(R.id.friendUnfriend);
        TextView takeABreak = dialog.findViewById(R.id.takeABreak);

        txtTitle.setText("Are you sure you want to block " + user.getFullName() +"?");
        txtDesc.setText(user.getFullName() +" will no longer be able to:");
        friendUnfriend.setText("If you're friends, blocking " + user.getFullName() +" will also unfriend him/her.");
        takeABreak.setText("If you just want to limit what you share with " + user.getFullName() + " or see less of him, you can take a break instead. ");

        dialog.setOnDismissListener(dialogInterface -> removeBlur(rootView));
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnBlock.setOnClickListener(v -> {
            blockUser(user.getUserId(), 0);
            dialog.dismiss();
        });

        dialog.show();
    }

    public void unFriendUser(String userId){
        loader.show();
        UtilMethods.INSTANCE.unFriendUser(userId,new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }

                UnfriendResponse unfriendResponse=(UnfriendResponse) object;
                if(unfriendResponse.getStatusCode()==1){
                    Toast.makeText(YourFriends.this, unfriendResponse.getResponseText(), Toast.LENGTH_SHORT).show();
                    getFriendList();
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
    public void blockUser(String userId, int blockedId){
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
                    Toast.makeText(YourFriends.this, blockUserResponse.getResponseText(), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, new Intent().putExtra("RefreshType", 1));
                    finish();
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
    ActivityResultLauncher<Intent> profileActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
            });
}