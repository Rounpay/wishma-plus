package com.infotech.wishmaplus.Activity;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.infotech.wishmaplus.Adapter.FriendAdapter;
import com.infotech.wishmaplus.Adapter.FriendListAdapter;
import com.infotech.wishmaplus.Adapter.MultiContentAdapter;
import com.infotech.wishmaplus.Api.Object.ContentResult;
import com.infotech.wishmaplus.Api.Response.FriendRequestResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.Api.Response.UserListFriends;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.payu.ui.model.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendRequest extends AppCompatActivity {
    RecyclerView recyclerView;
    private CustomLoader loader;
//    List<FriendRequestResponse> list;
    BottomSheetDialog bottomSheetDialog;
    View noDataLayout;
    AppCompatTextView count,heading,sort;
    List<UserListFriends> list = new ArrayList<>();
    FriendListAdapter adapter;
    public PreferencesManager tokenManager;

    UserDetailResponse userDetailResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_friend_request);
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        tokenManager = new PreferencesManager(this,1);
        recyclerView = findViewById(R.id.friendRecycler);
        count = findViewById(R.id.count);
        noDataLayout = findViewById(R.id.noDataLayout);
        heading = findViewById(R.id.heading);
        sort = findViewById(R.id.sort);
        SwipeRefreshLayout pullToRefresh = findViewById(R.id.swipeRefreshLayout);
        pullToRefresh.setOnRefreshListener(() -> {
            hitApi();
            pullToRefresh.setRefreshing(false);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        userDetailResponse = UtilMethods.INSTANCE.getUserDetailResponse(tokenManager);
        findViewById(R.id.moreBTn).setOnClickListener(view -> {
            openBottomSheet(this,true);
        });

        findViewById(R.id.sort).setOnClickListener(view -> {
            openBottomSheet(this,false);
        });
        adapter = new FriendListAdapter(this, list, new UtilMethods.FriendActionListener() {
            @Override
            public void onAddClicked(UserListFriends user, int position) {
//                callAddFriendApi(user, position);
                loader.show();
                respondOnRequest(FriendRequest.this,user.getUserId(), new UtilMethods.ApiCallBackMulti() {
                    @Override
                    public void onSuccess(Object object) {
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }
                        hitApi();

                    }

                    @Override
                    public void onError(String msg) {
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }

                    }
                },1);
            }

            @Override
            public void onProfileClick(UserListFriends user, int position) {
//                startActivity(new Intent(FriendRequest.this, ProfileActivity.class));
                profileActivityResultLauncher.launch(new Intent(FriendRequest.this, ProfileActivity.class)
                        .putExtra("userData", userDetailResponse)
                        .putExtra("id", user.getUserId()));


            }

            @Override
            public void onRemoveClicked(UserListFriends user, int position) {
                loader.show();
                respondOnRequest(FriendRequest.this,user.getUserId(), new UtilMethods.ApiCallBackMulti() {
                    @Override
                    public void onSuccess(Object object) {
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }
                        hitApi();

                    }

                    @Override
                    public void onError(String msg) {
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }

                    }
                },2);
            }
        },true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        updateEmptyView();
        hitApi();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        hitApi();
        getUserDetail();
        super.onResume();
    }

    ActivityResultLauncher<Intent> profileActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                    int refreshType = result.getData().getIntExtra("RefreshType", 0);
//                    if (refreshType == 1) {
//
//                    } else if (refreshType == 2) {
//
//                    } else {
//
//                    }
//
//
//                }
            });

    private void updateEmptyView() {
        if (list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
            count.setText("");
            count.setVisibility(View.GONE);
            heading.setVisibility(View.GONE);
            sort.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
            count.setText(list.size() + "");
            count.setVisibility(View.VISIBLE);
            heading.setVisibility(View.VISIBLE);
            sort.setVisibility(View.GONE);
        }
    }

    public void openBottomSheet(Activity context,boolean isSent) {

        if (bottomSheetDialog != null && bottomSheetDialog.isShowing())
            return;

        bottomSheetDialog = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_sort, null);

        View defaultOption = sheetView.findViewById(R.id.defaultOption);
        View newestOption = sheetView.findViewById(R.id.newestOption);
        View oldestOption = sheetView.findViewById(R.id.oldestOption);
        View sentRequest = sheetView.findViewById(R.id.sentRequest);
        View viewOne = sheetView.findViewById(R.id.viewOne);
        View viewTwo = sheetView.findViewById(R.id.viewTwo);

        if (isSent){
            sentRequest.setVisibility(View.VISIBLE);
            viewOne.setVisibility(View.GONE);
            viewTwo.setVisibility(View.GONE);
            defaultOption.setVisibility(View.GONE);
            newestOption.setVisibility(View.GONE);
            oldestOption.setVisibility(View.GONE);
        }


        sentRequest.setOnClickListener(v -> {
            startActivity(new Intent(this, SentRequests.class));
            bottomSheetDialog.dismiss();
        });
        defaultOption.setOnClickListener(v -> bottomSheetDialog.dismiss());
        newestOption.setOnClickListener(v -> bottomSheetDialog.dismiss());
        oldestOption.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomSheetDialog.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetDialog.show();
    }
    private void hitApi() {
        loader.show();
        try{
            UtilMethods.INSTANCE.getFriendRequest(this, new UtilMethods.ApiCallBackMulti() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onSuccess(Object object) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    list.clear();
                    if (object instanceof List) {
                        List<UserListFriends> apiList = (List<UserListFriends>) object;
                        list.addAll(apiList);
                        adapter.notifyDataSetChanged();
                        updateEmptyView();
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
        } catch (Exception e) {
            if (loader != null) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }
            }
        }

    }
    private void getUserDetail() {
        UtilMethods.INSTANCE.userDetail(this, "0","", loader, tokenManager, object -> {
        });
    }
    public void respondOnRequest(Activity context, String userId, UtilMethods.ApiCallBackMulti apiCallBack, int type) {
        if(type==1){
            UtilMethods.INSTANCE.AcceptOrRejectRequest(context, userId, 2, apiCallBack);
        }
        else{
            UtilMethods.INSTANCE.AcceptOrRejectRequest(context, userId, 3, apiCallBack);
        }

    }
}