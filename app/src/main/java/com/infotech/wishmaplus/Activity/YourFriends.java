package com.infotech.wishmaplus.Activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
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
import com.infotech.wishmaplus.Adapter.UsersAdapter;
import com.infotech.wishmaplus.Api.Response.FriendListResponse;
import com.infotech.wishmaplus.Api.Response.FriendUserModel;
import com.infotech.wishmaplus.Api.Response.User;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;

public class YourFriends extends AppCompatActivity {

    View noDataLayout;
    RecyclerView recyclerView;
    FriendsListAdapter adapter;
    private CustomLoader loader;
    FriendListResponse friendListResponse = new FriendListResponse();
    BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_your_friends);
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        SwipeRefreshLayout pullToRefresh = findViewById(R.id.swipeRefreshLayout);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
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
                    adapter = new FriendsListAdapter(YourFriends.this, friendListResponse.getResult(), new FriendsListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(FriendUserModel user, int pos) {

                        }

                        @Override
                        public void onMoreClicked(View anchor, FriendUserModel user, int pos) {

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
    public void openBottomSheet(Activity context) {

        if (bottomSheetDialog != null && bottomSheetDialog.isShowing())
            return;

        bottomSheetDialog = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_profile_options, null);

        View unfollow = sheetView.findViewById(R.id.unfollow);
        View block = sheetView.findViewById(R.id.block);
        View unfriend = sheetView.findViewById(R.id.unfriend);

        unfollow.setOnClickListener(v -> bottomSheetDialog.dismiss());
        block.setOnClickListener(v -> bottomSheetDialog.dismiss());
        unfriend.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomSheetDialog.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetDialog.show();
    }
}