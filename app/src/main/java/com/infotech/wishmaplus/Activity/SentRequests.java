package com.infotech.wishmaplus.Activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

import com.infotech.wishmaplus.Adapter.FriendAdapter;
import com.infotech.wishmaplus.Adapter.FriendsListAdapter;
import com.infotech.wishmaplus.Adapter.SentRequestAdapter;
import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.Api.Response.FriendListResponse;
import com.infotech.wishmaplus.Api.Response.FriendRequestResponse;
import com.infotech.wishmaplus.Api.Response.FriendUserModel;
import com.infotech.wishmaplus.Api.Response.SentRequestResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;

public class SentRequests extends AppCompatActivity {
    RecyclerView recyclerView;
    private CustomLoader loader;
    SentRequestAdapter adapter;
    View noDataLayout;
    List<FriendRequestResponse> list;

    TextView tvTitle;
    public PreferencesManager tokenManager;
    UserDetailResponse userDetailResponse;

    SentRequestResponse sentRequestResponse = new SentRequestResponse();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sent_requests);
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        noDataLayout = findViewById(R.id.noDataLayout);
        tokenManager = new PreferencesManager(this,1);
        tvTitle = findViewById(R.id.tvTitle);
        recyclerView = findViewById(R.id.friendRecycler);
        userDetailResponse = UtilMethods.INSTANCE.getUserDetailResponse(tokenManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getSentRequests(false);
    }
    private void getSentRequests(boolean isRefresh) {

        loader.show();

        UtilMethods.INSTANCE.getSentRequest(new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null && loader.isShowing()) loader.dismiss();

                sentRequestResponse = (SentRequestResponse) object;

                if (sentRequestResponse.getStatusCode() == 1) {

                    adapter = new SentRequestAdapter(
                            SentRequests.this,
                            sentRequestResponse.getResult(),
                            new SentRequestAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(SentRequestResponse.ResultItem user, int pos) { }

                                @Override
                                public void onMoreClicked(View anchor, SentRequestResponse.ResultItem user, int pos) {

                                    UtilMethods.INSTANCE.openAcceptRequestBottomSheetDialog(
                                            SentRequests.this,
                                            user.getUserId(),
                                            user.getFullName(),
                                            new UtilMethods.ApiCallBackMulti() {
                                                @Override
                                                public void onSuccess(Object object) {
                                                    BasicResponse basic = (BasicResponse) object;
                                                    UtilMethods.INSTANCE.Success(SentRequests.this, basic.getResponseText());
                                                    getSentRequests(true);
                                                }

                                                @Override
                                                public void onError(String msg) { }
                                            },
                                            0
                                    );
                                }

                                @Override
                                public void onProfileClick(SentRequestResponse.ResultItem user, int position) {
                                    //                startActivity(new Intent(FriendRequest.this, ProfileActivity.class));
                                    profileActivityResultLauncher.launch(new Intent(SentRequests.this, ProfileActivity.class)
                                            .putExtra("userData", userDetailResponse)
                                            .putExtra("id", user.getUserId()));

                                }
                            }
                    );

                    recyclerView.setLayoutManager(new LinearLayoutManager(SentRequests.this));
                    recyclerView.setAdapter(adapter);
                    recyclerView.addItemDecoration(
                            new DividerItemDecoration(SentRequests.this, DividerItemDecoration.VERTICAL)
                    );
                }

                if (isRefresh && adapter != null) adapter.notifyDataSetChanged();
                updateEmptyView();
            }

            @Override
            public void onError(String msg) {
                if (loader != null && loader.isShowing()) loader.dismiss();
            }
        });
    }
    private void updateEmptyView() {
        if (sentRequestResponse.getResult().isEmpty()) {
            recyclerView.setVisibility(GONE);
            noDataLayout.setVisibility(VISIBLE);
        } else {
            tvTitle.setText(sentRequestResponse.getResult().size()+ " Sent Requests");
            recyclerView.setVisibility(VISIBLE);
            noDataLayout.setVisibility(GONE);
        }
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
}