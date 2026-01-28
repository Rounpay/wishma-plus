package com.infotech.wishmaplus.Activity;

import static com.infotech.wishmaplus.Api.Request.TimeFilter.SEVEN_DAYS;
import static com.infotech.wishmaplus.Api.Request.TimeFilter.TODAY;
import static com.infotech.wishmaplus.Api.Request.TimeFilter.TWENTY_EIGHT_DAYS;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Api.Request.TimeFilter;
import com.infotech.wishmaplus.Api.Response.AnalyticsResponse;
import com.infotech.wishmaplus.Api.Response.SupportCategoryResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.Arrays;
import java.util.List;

public class ProfessionalDashBoardPersonal extends AppCompatActivity {
    LinearLayout analyticsHead,contentHead;
    private CustomLoader loader;
    private PreferencesManager tokenManager;
    androidx.appcompat.widget.AppCompatImageView profile_image;
    androidx.appcompat.widget.AppCompatTextView profile_name;
    TextView tab28, tab7, tabToday,tvLikeValue,tvCommentsValue,tvSharesValue,tvEngagementValue,postCaption,likesValue,earningsValue,engagementValue,commentsValue;
    List<TextView> tabs;
    AnalyticsResponse analyticsResponse = new AnalyticsResponse();
    AppCompatTextView tvPercent;
    ProgressBar progressCircle;
    ImageView imgPost;
    VideoView videoPost;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_professional_dash_board_personal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tab28 = findViewById(R.id.tab28);
        tab7 = findViewById(R.id.tab7);
        tabToday = findViewById(R.id.tabToday);
        tvLikeValue = findViewById(R.id.tvLikeValue);
        tvCommentsValue = findViewById(R.id.tvCommentsValue);
        tvSharesValue = findViewById(R.id.tvSharesValue);
        tvEngagementValue = findViewById(R.id.tvEngagementValue);
        imgPost = findViewById(R.id.imagePost);
        videoPost = findViewById(R.id.videoPost);
        postCaption = findViewById(R.id.postCaption);
        commentsValue = findViewById(R.id.commentsValue);
        engagementValue = findViewById(R.id.engagementValue);
        earningsValue = findViewById(R.id.earningsValue);
        likesValue = findViewById(R.id.likesValue);
        tabs = Arrays.asList(tab28, tab7, tabToday);
        selectTab(tab28);
        tab28.setOnClickListener(v -> {
            selectTab(tab28);
            onTabSelected(TWENTY_EIGHT_DAYS);
        });
        tab7.setOnClickListener(v -> {
            selectTab(tab7);
            onTabSelected(SEVEN_DAYS);
        });
        tabToday.setOnClickListener(v -> {
            selectTab(tabToday);
            onTabSelected(TODAY);
        });
        AppCompatImageButton back_button = findViewById(R.id.back_button);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        profile_image = findViewById(R.id.profile_image);
        profile_name = findViewById(R.id.profile_name);

        tokenManager = new PreferencesManager(this, 1);
        back_button.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
        tvPercent = findViewById(R.id.tv_percent);
        progressCircle = findViewById(R.id.progress_circle);
        analyticsHead = findViewById(R.id.analyticsHead);
        contentHead = findViewById(R.id.contentHead);
//        findViewById(R.id.profileCard).setOnClickListener(v -> {
//            Intent intent = new Intent(ProfessionalDashBoardPersonal.this,
//                    DashBoardPersonalInformation.class);
//            startActivity(intent);
//        });
        findViewById(R.id.analytics_tab).setOnClickListener(v -> {
            Intent intent = new Intent(ProfessionalDashBoardPersonal.this,
                    AnalyticsDashboard.class);
            startActivity(intent);
        });
        findViewById(R.id.content_tab).setOnClickListener(v -> {
            Intent intent = new Intent(ProfessionalDashBoardPersonal.this,
                    AnalyticsContent.class);
            startActivity(intent);
        });
        findViewById(R.id.creator_support_card).setOnClickListener(v -> {
            Intent intent = new Intent(ProfessionalDashBoardPersonal.this,
                    ComplaintList.class);
            startActivity(intent);
        });
        analyticsHead.setOnClickListener(v -> {
            Intent intent = new Intent(ProfessionalDashBoardPersonal.this,
                    AnalyticsDashboard.class);
            startActivity(intent);
        });
        contentHead.setOnClickListener(v -> {
            Intent intent = new Intent(ProfessionalDashBoardPersonal.this,
                    AnalyticsContent.class);
            startActivity(intent);
        });
        TimeFilter filter = TWENTY_EIGHT_DAYS;
        int dateRange = filter.getValue();
        getProfessionalDahboardAnalytic(dateRange);
//        getUserDetail();
    }
    private void selectTab(TextView selectedTab) {
        for (TextView tab : tabs) {
            tab.setBackgroundResource(R.drawable.bg_tab_unselected);
            tab.setTextColor(Color.BLACK);
        }

        selectedTab.setBackgroundResource(R.drawable.bg_tab_selected);
        selectedTab.setTextColor(Color.parseColor("#1A73E8"));
    }
    private void onTabSelected(TimeFilter filter) {
        getProfessionalDahboardAnalytic(filter.getValue());
    }
    private void getUserDetail() {
        loader.show();
            UtilMethods.INSTANCE.userDetail(this, "0","", loader, tokenManager, object -> {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                UserDetailResponse userDetailResponse = (UserDetailResponse) object;
                profile_name.setText(userDetailResponse.getFisrtName() + " " + userDetailResponse.getLastName());
                Glide.with(ProfessionalDashBoardPersonal.this)
                        .load(userDetailResponse.getProfilePictureUrl())
                        .apply(UtilMethods.INSTANCE.getRequestOption_With_UserIcon())
                        .into(profile_image);


            });
    }
    public void getProfessionalDahboardAnalytic(int dateRange){
        loader.show();
        UtilMethods.INSTANCE.getProfessionalDahboardAnalytic(dateRange, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                analyticsResponse = (AnalyticsResponse) object;
                if(analyticsResponse.getStatusCode()==1){
                    profile_name.setText(analyticsResponse.getResult().getProfile().getFullName());
                    Glide.with(ProfessionalDashBoardPersonal.this)
                            .load(analyticsResponse.getResult().getProfile().getProfilePictureUrl())
                            .apply(UtilMethods.INSTANCE.getRequestOption_With_UserIcon())
                            .into(profile_image);
                    int progress = analyticsResponse.getResult().getProfile().getWeelkyProgress();
                    ObjectAnimator anim = ObjectAnimator.ofInt(progressCircle, "progress", progress);
                    anim.setDuration(700);
                    anim.setInterpolator(new DecelerateInterpolator());
                    anim.start();
                    tvPercent.setText(progress + "%");
                    progressCircle.setProgress(progress);
                    tvLikeValue.setText(analyticsResponse.getResult().getAnalytic().getTotalLikes()+"");
                    tvCommentsValue.setText(analyticsResponse.getResult().getAnalytic().getTotalComments()+"");
                    tvSharesValue.setText(analyticsResponse.getResult().getAnalytic().getTotalShares()+"");
                    tvEngagementValue.setText(analyticsResponse.getResult().getAnalytic().getTotalEngagement()+"");
                    imgPost.setVisibility(View.GONE);
                    videoPost.setVisibility(View.GONE);

                    if (analyticsResponse.getResult().getLatestPosts().getContentTypeId() == 3) {  // IMAGE
                        imgPost.setVisibility(View.VISIBLE);
                        if(analyticsResponse.getResult().getLatestPosts().getCaption() != null)
                            postCaption.setText(analyticsResponse.getResult().getLatestPosts().getCaption());
                        Glide.with(ProfessionalDashBoardPersonal.this)
                                .load(analyticsResponse.getResult().getLatestPosts().getPostContent())
                                .placeholder(R.drawable.app_logo)
                                .into(imgPost);
                    }

                    else if (analyticsResponse.getResult().getLatestPosts().getContentTypeId() == 2) {  // VIDEO
                        videoPost.setVisibility(View.VISIBLE);
                        videoPost.setVideoPath(analyticsResponse.getResult().getLatestPosts().getPostContent());
                        videoPost.seekTo(1); // show first frame
                        if(analyticsResponse.getResult().getLatestPosts().getCaption() != null)
                            postCaption.setText(analyticsResponse.getResult().getLatestPosts().getCaption());
                    }

                    else if (analyticsResponse.getResult().getLatestPosts().getContentTypeId() == 1) {  // TEXT
                        imgPost.setVisibility(View.VISIBLE);
                        postCaption.setText(analyticsResponse.getResult().getLatestPosts().getCaption());
                        Glide.with(ProfessionalDashBoardPersonal.this)
                                .load(analyticsResponse.getResult().getLatestPosts().getPostContent())
                                .placeholder(R.drawable.app_logo)
                                .into(imgPost);

                    }

                    likesValue.setText(analyticsResponse.getResult().getLatestPosts().getTotalLikes()+"");
                    earningsValue.setText("₹"+analyticsResponse.getResult().getLatestPosts().getPostEarning()+"");
                    engagementValue.setText(analyticsResponse.getResult().getLatestPosts().getEngagement()+"");
                    commentsValue.setText(analyticsResponse.getResult().getLatestPosts().getTotalComments()+"");


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