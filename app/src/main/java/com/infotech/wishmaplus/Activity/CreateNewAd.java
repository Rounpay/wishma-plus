package com.infotech.wishmaplus.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.infotech.wishmaplus.Adapter.AudienceAdapter;
import com.infotech.wishmaplus.Adapter.BoostPostsAdapter;
import com.infotech.wishmaplus.Adapter.GoalAdapter;
import com.infotech.wishmaplus.Api.Response.EstimateResponse;
import com.infotech.wishmaplus.Api.Response.GetContentDetailsToBoostResponse;
import com.infotech.wishmaplus.Api.Response.PostItem;
import com.infotech.wishmaplus.Api.Response.PostsResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.Objects;

public class CreateNewAd extends AppCompatActivity {
    BottomSheetDialog bottomGoalDialogReport,bottomSpecialCatDialogReport,bottomChooseAudienceCatDialogReport,bottomIsSecureCatDialogReport,bottomPlacementsCatDialogReport,bottomBudgetCatDialogReport,bottomPaymentCatDialogReport;
    androidx.appcompat.widget.AppCompatImageView profile,containerImage;
    androidx.appcompat.widget.AppCompatTextView nameTv,timeTv,postTxt;
    View containerVideo;
    VideoView videoView;
    private CustomLoader loader;
    GetContentDetailsToBoostResponse getContentDetailsToBoostResponse = new GetContentDetailsToBoostResponse();

    EstimateResponse estimateResponse = new EstimateResponse();
    String postId ="";
    RecyclerView rvGoals,rvAudience;
    GoalAdapter adapter;
    AudienceAdapter audienceAdapter;
    TextView linkClicks,postEngagements,peopleReached,textView,tvPeopleReached,tvBudgetPrice,tvCostPrice,tvSubPrice,tvGstPrice,tvPrice;

    LinearLayout layoutCall,layoutUrl;
    SeekBar seekBar;

    int audienceId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_new_ad);
        postId = getIntent().getStringExtra("postId");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        rvGoals = findViewById(R.id.rvGoals);
        rvAudience = findViewById(R.id.rvAudience);
        seekBar = findViewById(R.id.budgetSeekBar);
        textView = findViewById(R.id.tvBudgetText);
        tvPeopleReached = findViewById(R.id.tvPeopleReached);
        tvBudgetPrice = findViewById(R.id.tvBudgetPrice);
        tvCostPrice = findViewById(R.id.tvCostPrice);
        tvSubPrice = findViewById(R.id.tvSubPrice);
        tvGstPrice = findViewById(R.id.tvGstPrice);
        tvPrice = findViewById(R.id.tvPrice);

        textView.setText("₹" + seekBar.getProgress());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText("₹" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getEstimateBoostReach((double) seekBar.getProgress(),1,audienceId);
            }
        });


        // RecyclerView setup
        rvGoals.setLayoutManager(new LinearLayoutManager(this));
        rvGoals.setHasFixedSize(true);
        rvAudience.setLayoutManager(new LinearLayoutManager(this));
        rvAudience.setHasFixedSize(true);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        profile = findViewById(R.id.profile);
        nameTv = findViewById(R.id.nameTv);
        timeTv = findViewById(R.id.timeTv);
        postTxt = findViewById(R.id.postTxt);
        containerVideo = findViewById(R.id.containerVideo);
        videoView = findViewById(R.id.videoView);
        containerImage = findViewById(R.id.containerImage);
        linkClicks = findViewById(R.id.linkClicks);
        postEngagements = findViewById(R.id.postEngagements);
        peopleReached = findViewById(R.id.peopleReached);
        layoutCall = findViewById(R.id.layoutCall);
        layoutUrl = findViewById(R.id.layoutUrl);
        Spinner spinner = findViewById(R.id.spCountry);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"India (+91)"}
        );

        spinner.setAdapter(adapter);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.imgInfoGoal).setOnClickListener(v -> openGoalBottomSheetDialog(this));
        findViewById(R.id.imgInfo).setOnClickListener(v -> openSpecialCatBottomSheetDialog(this));
        findViewById(R.id.infoAudience).setOnClickListener(v -> openChooseAudienceBottomSheetDialog(this));
//        findViewById(R.id.info).setOnClickListener(v -> openIsSecureBottomSheetDialog(this));
        findViewById(R.id.ivInfo).setOnClickListener(v -> openPlacementsBottomSheetDialog(this));
        findViewById(R.id.infoBudget).setOnClickListener(v -> openBudgetBottomSheetDialog(this));
        findViewById(R.id.ivInfoPayment).setOnClickListener(v -> openPaymentBottomSheetDialog(this));
        getContentDetailsToBoostResponse(postId);
    }
    public void getContentDetailsToBoostResponse(String postId){
        loader.show();
        UtilMethods.INSTANCE.getContentDetailsToBoost(postId, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                getContentDetailsToBoostResponse =(GetContentDetailsToBoostResponse) object;
                if(getContentDetailsToBoostResponse.getStatusCode()==1){
                    if (getContentDetailsToBoostResponse.getAudience() != null
                            && !getContentDetailsToBoostResponse.getAudience().isEmpty()) {

                        audienceId = getContentDetailsToBoostResponse
                                .getAudience()
                                .get(0)
                                .getAudienceId();
                    }
                    getEstimateBoostReach((double) seekBar.getProgress(),1,audienceId);
                    GetContentDetailsToBoostResponse.PostInsights postInsights = getContentDetailsToBoostResponse.getPostInsights();
                    Glide.with(CreateNewAd.this).load(postInsights.getProfilePictureUrl()).placeholder(R.drawable.user_icon).into(profile);
                    nameTv.setText(postInsights.getUserName());
                    timeTv.setText("Sponsored");
                    if(postInsights.getCaption()!=null) {
                        postTxt.setText(postInsights.getCaption());
                    }
                    if(postInsights.getContentTypeId()==1){//text
                        containerVideo.setVisibility(View.GONE);
                        containerImage.setVisibility(View.GONE);
                    }
                    else if(postInsights.getContentTypeId()==2) {//video
                        containerVideo.setVisibility(View.VISIBLE);
                        containerImage.setVisibility(View.GONE);
                        videoView.setVideoPath(postInsights.getPostContent());
                    }
                    else if(postInsights.getContentTypeId()==3) {//IMAGE
                        containerVideo.setVisibility(View.GONE);
                        containerImage.setVisibility(View.VISIBLE);
                        Glide.with(CreateNewAd.this).load(postInsights.getPostContent()).placeholder(R.drawable.app_logo).into(containerImage);
                    }
                    peopleReached.setText(""+getContentDetailsToBoostResponse.getPostInsights().getPeopleReach());
                    postEngagements.setText(""+getContentDetailsToBoostResponse.getPostInsights().getEngagement());

                    adapter = new GoalAdapter(CreateNewAd.this, getContentDetailsToBoostResponse.getGoal(), (position, goal) -> {
                        if(Objects.equals(goal.getIconName().toLowerCase(), "visitors")){
                            layoutCall.setVisibility(View.GONE);
                            layoutUrl.setVisibility(View.VISIBLE);
                        } else if (Objects.equals(goal.getIconName().toLowerCase(), "calls")) {
                            layoutCall.setVisibility(View.VISIBLE);
                            layoutUrl.setVisibility(View.GONE);
                        }
                        else{
                            layoutCall.setVisibility(View.GONE);
                            layoutUrl.setVisibility(View.GONE);
                        }
                    });

                    rvGoals.setAdapter(adapter);
                    audienceAdapter = new AudienceAdapter(CreateNewAd.this, getContentDetailsToBoostResponse.getAudience(), new AudienceAdapter.OnAudienceClickListener() {
                        @Override
                        public void onAudienceClick(int position, GetContentDetailsToBoostResponse.Audience goal) {
                            audienceId = goal.getAudienceId();
                            getEstimateBoostReach((double) seekBar.getProgress(),1,audienceId);

                        }

                        @Override
                        public void onAudienceEditClick(int position, GetContentDetailsToBoostResponse.Audience goal) {

                            startActivity(new Intent(CreateNewAd.this,EditAudience.class));


                        }
                    });
                    rvAudience.setAdapter(audienceAdapter);


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
    public void getEstimateBoostReach(double budget,int days,int audienceId){
        loader.show();
        UtilMethods.INSTANCE.getEstimateBoostReach(budget,days,audienceId, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                estimateResponse =(EstimateResponse) object;
                if(estimateResponse.getStatusCode()==1){
                    tvPeopleReached.setText(estimateResponse.getResult().getReach());
                    tvBudgetPrice.setText(estimateResponse.getResult().getBudget()+"");
                    tvCostPrice.setText(estimateResponse.getResult().getEstimatedCost()+"");
                    tvSubPrice.setText(estimateResponse.getResult().getSubTotal()+"");
                    tvGstPrice.setText(estimateResponse.getResult().getGst()+"");
                    tvPrice.setText(estimateResponse.getResult().getTotal()+"");

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
    public void openGoalBottomSheetDialog(Activity context) {

        if (bottomGoalDialogReport != null && bottomGoalDialogReport.isShowing())
            return;

        bottomGoalDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_goal_list, null);




        bottomGoalDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomGoalDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomGoalDialogReport.show();
    }

    public void openSpecialCatBottomSheetDialog(Activity context) {

        if (bottomSpecialCatDialogReport != null && bottomSpecialCatDialogReport.isShowing())
            return;

        bottomSpecialCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_special_category, null);




        bottomSpecialCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomSpecialCatDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSpecialCatDialogReport.show();
    }
    public void openChooseAudienceBottomSheetDialog(Activity context) {

        if (bottomChooseAudienceCatDialogReport != null && bottomChooseAudienceCatDialogReport.isShowing())
            return;

        bottomChooseAudienceCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_choose_audience, null);




        bottomChooseAudienceCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomChooseAudienceCatDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomChooseAudienceCatDialogReport.show();
    }
    public void openIsSecureBottomSheetDialog(Activity context) {

        if (bottomIsSecureCatDialogReport != null && bottomIsSecureCatDialogReport.isShowing())
            return;

        bottomIsSecureCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_is_secure, null);




        bottomIsSecureCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomIsSecureCatDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomIsSecureCatDialogReport.show();
    }
    public void openPlacementsBottomSheetDialog(Activity context) {

        if (bottomPlacementsCatDialogReport != null && bottomPlacementsCatDialogReport.isShowing())
            return;

        bottomPlacementsCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_placements, null);




        bottomPlacementsCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomPlacementsCatDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomPlacementsCatDialogReport.show();
    }
    public void openBudgetBottomSheetDialog(Activity context) {

        if (bottomBudgetCatDialogReport != null && bottomBudgetCatDialogReport.isShowing())
            return;

        bottomBudgetCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_budget, null);




        bottomBudgetCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomBudgetCatDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomBudgetCatDialogReport.show();
    }
    public void openPaymentBottomSheetDialog(Activity context) {

        if (bottomPaymentCatDialogReport != null && bottomPaymentCatDialogReport.isShowing())
            return;

        bottomPaymentCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_payment, null);




        bottomPaymentCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomPaymentCatDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomPaymentCatDialogReport.show();
    }
}