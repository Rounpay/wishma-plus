package com.infotech.wishmaplus.Activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CreateNewAd extends AppCompatActivity {
    BottomSheetDialog bottomGoalDialogReport,bottomSpecialCatDialogReport,bottomChooseAudienceCatDialogReport,bottomIsSecureCatDialogReport,bottomPlacementsCatDialogReport,bottomBudgetCatDialogReport,bottomPaymentCatDialogReport;
    androidx.appcompat.widget.AppCompatImageView profile,containerImage;
    androidx.appcompat.widget.AppCompatTextView nameTv,timeTv,postTxt;
    View containerVideo,rbContinuous,rbChoose,goalTypeLayout;
    VideoView videoView;
    private CustomLoader loader;
    GetContentDetailsToBoostResponse getContentDetailsToBoostResponse = new GetContentDetailsToBoostResponse();

    EstimateResponse estimateResponse = new EstimateResponse();
    String postId ="";
    RecyclerView rvGoals,rvAudience;
    GoalAdapter adapter;
    AudienceAdapter audienceAdapter;
    TextView linkClicks,postEngagements,peopleReached,textView,tvPeopleReached,tvBudgetPrice,tvCostPrice,tvSubPrice,tvGstPrice,tvPrice,tvDials,userNameTitle,tvAdd;

    LinearLayout layoutCall,layoutUrl,tvBudgetValue;
    SeekBar seekBar;

    RadioButton rbChooseAd,rbRun;

    int audienceId = 1;


    private TextView tvDays, tvDate, tvInfo,tvLine1,textView3,textView2,tvSummarySubtitle;
    private ImageButton btnPlus, btnMinus;
    private LinearLayout layoutDate,daysPicker,llInfo,editTextLayout,callNow,bookNow;

    private int days = 1;
    private Calendar startDate;
    private Calendar endDate;
    EditText etBudget;
    int minAge = 18;
    int maxAge = 65;
    String gender = "All";

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
        rbRun = findViewById(R.id.rbRun);
        rbChooseAd = findViewById(R.id.rbChooseAd);
        daysPicker = findViewById(R.id.daysPicker);
        llInfo = findViewById(R.id.llInfo);
        tvLine1 = findViewById(R.id.tvLine1);
        rbChoose = findViewById(R.id.rbChoose);
        rbContinuous = findViewById(R.id.rbContinuous);
        etBudget = findViewById(R.id.etBudget);
        tvBudgetValue = findViewById(R.id.tvBudgetValue);
        textView3 = findViewById(R.id.textView3);
        textView2 = findViewById(R.id.textView2);
        editTextLayout = findViewById(R.id.editTextLayout);
        tvSummarySubtitle = findViewById(R.id.tvSummarySubtitle);
        goalTypeLayout = findViewById(R.id.goalTypeLayout);
        tvDials = findViewById(R.id.tvDials);
        userNameTitle = findViewById(R.id.userNameTitle);
        callNow = findViewById(R.id.callNow);
        bookNow = findViewById(R.id.bookNow);
        tvAdd = findViewById(R.id.tvAdd);

        goalTypeLayout.setVisibility(GONE);

        textView.setText("₹" + seekBar.getProgress());
        etBudget.setText(""+seekBar.getProgress());
        editTextLayout.setVisibility(GONE);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText("₹" + progress);
                etBudget.setText(""+seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getEstimateBoostReach((double) seekBar.getProgress(),days,audienceId);
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
        tvDays = findViewById(R.id.tvDays);
        tvDate = findViewById(R.id.tvDate);
        tvInfo = findViewById(R.id.tvInfo);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        layoutDate = findViewById(R.id.layoutDate);

        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        rbChooseAd.setChecked(true);
        rbRun.setChecked(false);
        daysPicker.setVisibility(VISIBLE);
        llInfo.setVisibility(GONE);
        tvSummarySubtitle.setVisibility(GONE);

        updateFromDays();

        btnPlus.setOnClickListener(v -> {
            days++;
            updateFromDays();
        });

        btnMinus.setOnClickListener(v -> {
            if (days > 1) { // prevent zero or negative
                days--;
                updateFromDays();
            }
        });

        layoutDate.setOnClickListener(v -> openDatePicker());

        rbChoose.setOnClickListener(view -> {
            daysPicker.setVisibility(VISIBLE);
            llInfo.setVisibility(GONE);
            tvSummarySubtitle.setVisibility(GONE);
            days = 1;
            updateFromDays();
            rbChooseAd.setChecked(true);
            rbRun.setChecked(false);

        });
        rbContinuous.setOnClickListener(view -> {
            llInfo.setVisibility(VISIBLE);
            tvSummarySubtitle.setVisibility(VISIBLE);
            daysPicker.setVisibility(GONE);
            days = -1;
            rbRun.setChecked(true);
            rbChooseAd.setChecked(false);
            getEstimateBoostReach((double) seekBar.getProgress(),days,audienceId);

        });
        tvBudgetValue.setOnClickListener(view -> {
            etBudget.setText(""+seekBar.getProgress());
            tvBudgetValue.setVisibility(GONE);
            textView3.setVisibility(GONE);
            seekBar.setVisibility(GONE);
            textView2.setVisibility(GONE);
            editTextLayout.setVisibility(VISIBLE);
        });
        etBudget.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                String value = etBudget.getText().toString().trim();

                if (value.isEmpty()) {
                    etBudget.setError("Required");
                    return true;
                }

                int budget = Integer.parseInt(value);
                if (budget >= 500 && budget<=10000) {
                    seekBar.setProgress(budget);
                    textView.setText("₹" + budget);
                }
                tvBudgetValue.setVisibility(VISIBLE);
                textView3.setVisibility(VISIBLE);
                seekBar.setVisibility(VISIBLE);
                textView2.setVisibility(VISIBLE);
                editTextLayout.setVisibility(GONE);
                getEstimateBoostReach((double) seekBar.getProgress(),days,audienceId);

                // Hide keyboard
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etBudget.getWindowToken(), 0);

                return true;
            }
            return false;
        });

    }
    ActivityResultLauncher<Intent> editAudienceLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                            Intent data = result.getData();

                            int minAge = data.getIntExtra("minAge", 18);
                            int maxAge = data.getIntExtra("maxAge", 65);
                            String gender = data.getStringExtra("gender");
                            audienceAdapter.updateAudience(minAge, maxAge, gender);
                        }
                    });


    private void updateFromDays() {
        tvDays.setText(String.valueOf(days));

        endDate = (Calendar) startDate.clone();
        endDate.add(Calendar.DAY_OF_YEAR, days);

        updateUI();
    }

    private void updateFromDate() {
        long diff = endDate.getTimeInMillis() - startDate.getTimeInMillis();
        days = (int) TimeUnit.MILLISECONDS.toDays(diff);

        if (days < 1) days = 1;

        tvDays.setText(String.valueOf(days));
        updateUI();
    }

    private void updateUI() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
        tvDate.setText(sdf.format(endDate.getTime()));
        getEstimateBoostReach((double) seekBar.getProgress(),days,audienceId);

        tvInfo.setText(
                "Your ad will be published today and run for "
                        + days + " day"
                        + (days > 1 ? "s" : "")
                        + " ending on "
                        + sdf.format(endDate.getTime())+ "."

        );
    }

    private void openDatePicker() {

        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_YEAR, 1); // tomorrow

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    endDate.set(year, month, dayOfMonth);

                    // Safety check (extra protection)
                    if (!endDate.after(startDate)) {
                        endDate = (Calendar) startDate.clone();
                        endDate.add(Calendar.DAY_OF_YEAR, 1);
                    }

                    updateFromDate();
                },
                endDate.get(Calendar.YEAR),
                endDate.get(Calendar.MONTH),
                endDate.get(Calendar.DAY_OF_MONTH)
        );

        // 🚫 Disable today and past dates
        dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        dialog.show();
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
                    getEstimateBoostReach((double) seekBar.getProgress(),days,audienceId);
                    GetContentDetailsToBoostResponse.PostInsights postInsights = getContentDetailsToBoostResponse.getPostInsights();
                    Glide.with(CreateNewAd.this).load(postInsights.getProfilePictureUrl()).placeholder(R.drawable.user_icon).into(profile);
                    nameTv.setText(postInsights.getUserName());
                    userNameTitle.setText(postInsights.getUserName());
                    timeTv.setText("Sponsored");
                    if(postInsights.getCaption()!=null) {
                        postTxt.setText(postInsights.getCaption());
                    }
                    if(postInsights.getContentTypeId()==1){//text
                        containerVideo.setVisibility(GONE);
                        containerImage.setVisibility(GONE);
                    }
                    else if(postInsights.getContentTypeId()==2) {//video
                        containerVideo.setVisibility(VISIBLE);
                        containerImage.setVisibility(GONE);
                        videoView.setVideoPath(postInsights.getPostContent());
                    }
                    else if(postInsights.getContentTypeId()==3) {//IMAGE
                        containerVideo.setVisibility(GONE);
                        containerImage.setVisibility(VISIBLE);
                        Glide.with(CreateNewAd.this).load(postInsights.getPostContent()).placeholder(R.drawable.app_logo).into(containerImage);
                    }
                    peopleReached.setText(""+getContentDetailsToBoostResponse.getPostInsights().getPeopleReach());
                    postEngagements.setText(""+getContentDetailsToBoostResponse.getPostInsights().getEngagement());

                    adapter = new GoalAdapter(CreateNewAd.this, getContentDetailsToBoostResponse.getGoal(), (position, goal) -> {
                        if(Objects.equals(goal.getIconName().toLowerCase(), "visitors")){
                            layoutCall.setVisibility(GONE);
                            layoutUrl.setVisibility(VISIBLE);
                            goalTypeLayout.setVisibility(VISIBLE);
                            tvDials.setText("VISIT");
                            callNow.setVisibility(GONE);
                            bookNow.setVisibility(VISIBLE);


                        } else if (Objects.equals(goal.getIconName().toLowerCase(), "calls")) {
                            layoutCall.setVisibility(VISIBLE);
                            layoutUrl.setVisibility(GONE);
                            goalTypeLayout.setVisibility(VISIBLE);
                            tvDials.setText("DIALS");
                            callNow.setVisibility(VISIBLE);
                            bookNow.setVisibility(GONE);
                        }
                        else{
                            layoutCall.setVisibility(GONE);
                            layoutUrl.setVisibility(GONE);
                            goalTypeLayout.setVisibility(GONE);
                        }
                    });

                    rvGoals.setAdapter(adapter);
                    audienceAdapter = new AudienceAdapter(CreateNewAd.this, getContentDetailsToBoostResponse.getAudience(), new AudienceAdapter.OnAudienceClickListener() {
                        @Override
                        public void onAudienceClick(int position, GetContentDetailsToBoostResponse.Audience goal) {
                            audienceId = goal.getAudienceId();
                            getEstimateBoostReach((double) seekBar.getProgress(),days,audienceId);

                        }

                        @Override
                        public void onAudienceEditClick(int position, GetContentDetailsToBoostResponse.Audience goal,int minAge, int maxAge, String gender) {
                            Intent intent = new Intent(CreateNewAd.this, EditAudience.class);
                            intent.putExtra("minAge", minAge);
                            intent.putExtra("maxAge", maxAge);
                            intent.putExtra("gender", gender);
                            intent.putExtra("audience", goal.getAudienceName());

                            editAudienceLauncher.launch(intent);


                        }
                    },minAge, maxAge, gender);
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
                    tvLine1.setText("Your ad will run continuously with a daily budget of ₹"+seekBar.getProgress()+". Actual amount spent daily may vary.");
                    tvPeopleReached.setText(estimateResponse.getResult().getReach());
                    tvAdd.setText("₹"+estimateResponse.getResult().getUserBalance());
                    tvBudgetPrice.setText("₹"+estimateResponse.getResult().getBudget()+"");
                    tvCostPrice.setText("₹"+estimateResponse.getResult().getEstimatedCost()+"");
                    tvSubPrice.setText("₹"+estimateResponse.getResult().getSubTotal()+"");
                    tvGstPrice.setText("₹"+estimateResponse.getResult().getGst()+"");
                    tvPrice.setText("₹"+estimateResponse.getResult().getTotal()+"");
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