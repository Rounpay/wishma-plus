package com.infotech.wishmaplus.Activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.infotech.wishmaplus.Adapter.ProfessionalPostAdapter;
import com.infotech.wishmaplus.Api.Response.BoostedPostStatusChangeResponse;
import com.infotech.wishmaplus.Api.Response.PostItem;
import com.infotech.wishmaplus.Api.Response.PostsResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.Objects;

public class AnalyticsContent extends AppCompatActivity {
    RecyclerView recyclerView;
    View noDataLayout;
    ProfessionalPostAdapter adapter;
    private CustomLoader loader;
    private PreferencesManager tokenManager;
    PostsResponse postsResponse = new PostsResponse();
    BottomSheetDialog bottomFilterDialogReport;
    View chipFilterCount,lifeTime,postTypeCard,metricCard;
    androidx.appcompat.widget.AppCompatTextView dateRange,postType,metricType;

    int days=0;
    int postTypeFilter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_analytics_content);
        AppCompatImageButton back_button = findViewById(R.id.back_button);
        noDataLayout = findViewById(R.id.noDataLayout);
        tokenManager = new PreferencesManager(this,1);
        recyclerView = findViewById(R.id.recyclerView);
        chipFilterCount = findViewById(R.id.chipFilterCount);
        lifeTime = findViewById(R.id.lifeTime);
        dateRange = findViewById(R.id.dateRange);
        postType = findViewById(R.id.postType);
        postTypeCard = findViewById(R.id.postTypeCard);
        metricCard = findViewById(R.id.metricCard);
        metricType = findViewById(R.id.metricType);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        back_button.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        boostContentList(0,0);
        chipFilterCount.setOnClickListener(v -> openFilterBottomSheetDialog(this));
        lifeTime.setOnClickListener(view -> UtilMethods.INSTANCE.selectDateRangeBottomSheetNew(this, dateRange, this::updateDateFilter,true));
        postTypeCard.setOnClickListener(view -> UtilMethods.INSTANCE.selectPostTypeBottomSheet(this, postType, this::updatePostType));
        metricCard.setOnClickListener(view -> UtilMethods.INSTANCE.selectMetricBottomSheet(this, metricType, this::updatePostType));
    }
    @SuppressLint("SetTextI18n")
    private void updateDateFilter(int days) {
        this.days = days;
        boostContentList(days,postTypeFilter);


    }
    @SuppressLint("SetTextI18n")
    private void updatePostType(int postTypeFilter) {
        this.postTypeFilter = days;
        boostContentList(days,postTypeFilter);


    }
    public void boostContentList(int DateRange,
                                 int ContentType){
        UserDetailResponse userDetailResponse = UtilMethods.INSTANCE.getUserDetailResponse(tokenManager);
        String pageId = userDetailResponse.isSelfProfile()?"":userDetailResponse.getUserId();
        loader.show();
        UtilMethods.INSTANCE.getContentToBoost(pageId,this, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                postsResponse =(PostsResponse) object;
                if(postsResponse.getStatusCode()==1){

                    adapter = new ProfessionalPostAdapter(AnalyticsContent.this, postsResponse.getResult(), new ProfessionalPostAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(PostItem user, int pos) {
                            Intent intent = new Intent(AnalyticsContent.this, PostDetails.class);
                            intent.putExtra("postId", user.getPostId());
                            startActivity(intent);

                        }

                        @Override
                        public void onMoreClicked(View anchor, PostItem user, int pos) {

                        }
                        @Override
                        public void onBtnStopClicked(PostItem user, int pos) {
                            showConfirmationDialog(user,3,AnalyticsContent.this);

                        }

                        @Override
                        public void onBtnRestartClicked(PostItem user, int pos) {
                            showConfirmationDialog(user,2,AnalyticsContent.this);

                        }
                    });
                    recyclerView.setAdapter(adapter);


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
        },DateRange,ContentType);
    }
    private void showConfirmationDialog(PostItem user,int status,Activity context) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to continue?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    updateBoostStatus(user.getBoostId(),status,context);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }


    private void updateBoostStatus(int boostId, int Status, Activity context){
        loader.show();
        UtilMethods.INSTANCE.updateBoostStatus(boostId, Status, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                BoostedPostStatusChangeResponse boostedPostStatusChangeResponse =(BoostedPostStatusChangeResponse) object;
                if(boostedPostStatusChangeResponse.getStatusCode()==1){
                    boostContentList(0,0);
                    Toast.makeText(context, boostedPostStatusChangeResponse.getResponseText(), Toast.LENGTH_SHORT).show();


                }else{
                    UtilMethods.INSTANCE.Error(context, boostedPostStatusChangeResponse.getResponseText());
                }


            }

            @Override
            public void onError(String msg) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                UtilMethods.INSTANCE.Error(context, msg);

            }
        });
    }
    public void openFilterBottomSheetDialog(Activity context) {
        String dateRangeText = dateRange.getText().toString();
        String postTypeText = postType.getText().toString();
        if (bottomFilterDialogReport != null && bottomFilterDialogReport.isShowing())
            return;

        bottomFilterDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_filters, null);
        TextView tvSubtitle = sheetView.findViewById(R.id.tvSubtitle);
        TextView tvDateSubtitle = sheetView.findViewById(R.id.tvDateSubtitle);
        tvSubtitle.setText(dateRangeText);
        tvDateSubtitle.setText(postTypeText);




        bottomFilterDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        Objects.requireNonNull(bottomFilterDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet)))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomFilterDialogReport.show();
    }

    private void updateEmptyView() {
        if (postsResponse.getResult().isEmpty()) {
            recyclerView.setVisibility(GONE);
            noDataLayout.setVisibility(VISIBLE);
        } else {
            recyclerView.setVisibility(VISIBLE);
            noDataLayout.setVisibility(GONE);
        }
    }
}