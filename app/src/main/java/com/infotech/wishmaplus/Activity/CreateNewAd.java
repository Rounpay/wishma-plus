package com.infotech.wishmaplus.Activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.infotech.wishmaplus.Adapter.AudienceAdapter;
import com.infotech.wishmaplus.Adapter.GoalAdapter;
import com.infotech.wishmaplus.Api.Object.PgKeyVals;
import com.infotech.wishmaplus.Api.Request.InitiateBoostRequest;
import com.infotech.wishmaplus.Api.Response.BoostResponse;
import com.infotech.wishmaplus.Api.Response.EstimateResponse;
import com.infotech.wishmaplus.Api.Response.GetContentDetailsToBoostResponse;
import com.infotech.wishmaplus.Api.Response.UpgradePackageResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.CheckoutProWebChromeClient;
import com.infotech.wishmaplus.Utils.CheckoutProWebViewClient;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.payu.base.models.CardType;
import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUPaymentParams;
import com.payu.base.models.PaymentType;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.models.PayUCheckoutProConfig;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.custombrowser.Bank;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewAd extends AppCompatActivity {
    BottomSheetDialog bottomGoalDialogReport, bottomSpecialCatDialogReport, bottomChooseAudienceCatDialogReport, bottomIsSecureCatDialogReport, bottomPlacementsCatDialogReport, bottomBudgetCatDialogReport, bottomPaymentCatDialogReport;
    androidx.appcompat.widget.AppCompatImageView profile, containerImage;
    androidx.appcompat.widget.AppCompatTextView nameTv, timeTv, postTxt;
    View containerVideo, rbContinuous, rbChoose, goalTypeLayout;
    VideoView videoView;
    private CustomLoader loader;
    GetContentDetailsToBoostResponse getContentDetailsToBoostResponse = new GetContentDetailsToBoostResponse();

    BoostResponse boostResponse = new BoostResponse();

    EstimateResponse estimateResponse = new EstimateResponse();
    String postId = "";
    RecyclerView rvGoals, rvAudience;
    GoalAdapter adapter;
    AudienceAdapter audienceAdapter;
    TextView linkClicks, postEngagements, peopleReached, textView, tvPeopleReached, tvBudgetPrice, tvCostPrice, tvSubPrice, tvGstPrice, tvPrice, tvDials, userNameTitle, tvAdd;

    LinearLayout layoutCall, layoutUrl, tvBudgetValue;
    SeekBar seekBar;

    RadioButton rbChooseAd, rbRun;

    int audienceId = 1;
    private long mLastClickTime;
    private PreferencesManager tokenManager;


    private TextView tvDays, tvDate, tvInfo, tvLine1, textView3, textView2, tvSummarySubtitle;
    private ImageButton btnPlus, btnMinus;
    private LinearLayout layoutDate, daysPicker, llInfo, editTextLayout, callNow, bookNow;

    private int days = 1;
    private Calendar startDate;
    private Calendar endDate;
    EditText etBudget, etPhone, etUrl;
    int minAge = 18;
    int maxAge = 65;
    String gender = "All";
    String xmlType = "";
    double budgetGlobal = 0.0;
    double estimatedCost = 0.0;
    double gstAmount = 0.0;
    double subTotal = 0.0;
    double total = 0.0;
    Button btnPromoteNow;

    int boostId;

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
        tokenManager = new PreferencesManager(this, 1);
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
        etPhone = findViewById(R.id.etPhone);
        etUrl = findViewById(R.id.etUrl);
        btnPromoteNow = findViewById(R.id.btnPromoteNow);

        goalTypeLayout.setVisibility(GONE);

        textView.setText("₹" + seekBar.getProgress());
        etBudget.setText("" + seekBar.getProgress());
        editTextLayout.setVisibility(GONE);
        findViewById(R.id.tvGiveFeedback).setOnClickListener(v -> {
            Intent intent = new Intent(this, ComplaintList.class);
            startActivity(intent);
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText("₹" + progress);
                etBudget.setText("" + seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getEstimateBoostReach((double) seekBar.getProgress(), days, audienceId);
            }
        });
        btnPromoteNow.setOnClickListener(view -> getInitiatePostBoost(null, null, null, null));


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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"India (+91)"});

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
            getEstimateBoostReach((double) seekBar.getProgress(), days, audienceId);

        });
        tvBudgetValue.setOnClickListener(view -> {
            etBudget.setText("" + seekBar.getProgress());
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
                if (budget >= 500 && budget <= 10000) {
                    seekBar.setProgress(budget);
                    textView.setText("₹" + budget);
                }
                tvBudgetValue.setVisibility(VISIBLE);
                textView3.setVisibility(VISIBLE);
                seekBar.setVisibility(VISIBLE);
                textView2.setVisibility(VISIBLE);
                editTextLayout.setVisibility(GONE);
                getEstimateBoostReach((double) seekBar.getProgress(), days, audienceId);

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etBudget.getWindowToken(), 0);

                return true;
            }
            return false;
        });

    }

    ActivityResultLauncher<Intent> editAudienceLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

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
        getEstimateBoostReach((double) seekBar.getProgress(), days, audienceId);

        tvInfo.setText("Your ad will be published today and run for " + days + " day" + (days > 1 ? "s" : "") + " ending on " + sdf.format(endDate.getTime()) + "."

        );
    }

    private void openDatePicker() {

        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_YEAR, 1); // tomorrow

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {

            endDate.set(year, month, dayOfMonth);

            // Safety check (extra protection)
            if (!endDate.after(startDate)) {
                endDate = (Calendar) startDate.clone();
                endDate.add(Calendar.DAY_OF_YEAR, 1);
            }

            updateFromDate();
        }, endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));

        // 🚫 Disable today and past dates
        dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        dialog.show();
    }

    public void getContentDetailsToBoostResponse(String postId) {
        loader.show();
        UtilMethods.INSTANCE.getContentDetailsToBoost(postId, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                getContentDetailsToBoostResponse = (GetContentDetailsToBoostResponse) object;
                if (getContentDetailsToBoostResponse.getStatusCode() == 1) {
                    if (getContentDetailsToBoostResponse.getAudience() != null && !getContentDetailsToBoostResponse.getAudience().isEmpty()) {

                        audienceId = getContentDetailsToBoostResponse.getAudience().get(0).getAudienceId();
                    }
                    getEstimateBoostReach((double) seekBar.getProgress(), days, audienceId);
                    GetContentDetailsToBoostResponse.PostInsights postInsights = getContentDetailsToBoostResponse.getPostInsights();
                    Glide.with(CreateNewAd.this).load(postInsights.getProfilePictureUrl()).placeholder(R.drawable.user_icon).into(profile);
                    nameTv.setText(postInsights.getUserName());
                    userNameTitle.setText(postInsights.getUserName());
                    timeTv.setText("Sponsored");
                    if (postInsights.getCaption() != null) {
                        postTxt.setText(postInsights.getCaption());
                    }
                    if (postInsights.getContentTypeId() == 1) {//text
                        containerVideo.setVisibility(GONE);
                        containerImage.setVisibility(GONE);
                    } else if (postInsights.getContentTypeId() == 2) {//video
                        containerVideo.setVisibility(VISIBLE);
                        containerImage.setVisibility(GONE);
                        videoView.setVideoPath(postInsights.getPostContent());
                    } else if (postInsights.getContentTypeId() == 3) {//IMAGE
                        containerVideo.setVisibility(GONE);
                        containerImage.setVisibility(VISIBLE);
                        Glide.with(CreateNewAd.this).load(postInsights.getPostContent()).placeholder(R.drawable.app_logo).into(containerImage);
                    }
                    peopleReached.setText("" + getContentDetailsToBoostResponse.getPostInsights().getPeopleReach());
                    postEngagements.setText("" + getContentDetailsToBoostResponse.getPostInsights().getEngagement());

                    adapter = new GoalAdapter(CreateNewAd.this, getContentDetailsToBoostResponse.getGoal(), (position, goal) -> {
                        if (Objects.equals(goal.getIconName().toLowerCase(), "visitors")) {
                            layoutCall.setVisibility(GONE);
                            layoutUrl.setVisibility(VISIBLE);
                            goalTypeLayout.setVisibility(VISIBLE);
                            tvDials.setText("VISIT");
                            callNow.setVisibility(GONE);
                            bookNow.setVisibility(VISIBLE);
                            xmlType = "url";


                        } else if (Objects.equals(goal.getIconName().toLowerCase(), "calls")) {
                            layoutCall.setVisibility(VISIBLE);
                            layoutUrl.setVisibility(GONE);
                            goalTypeLayout.setVisibility(VISIBLE);
                            tvDials.setText("DIALS");
                            callNow.setVisibility(VISIBLE);
                            bookNow.setVisibility(GONE);
                            xmlType = "call";
                        } else {
                            layoutCall.setVisibility(GONE);
                            layoutUrl.setVisibility(GONE);
                            goalTypeLayout.setVisibility(GONE);
                            xmlType = "";
                        }
                    });

                    rvGoals.setAdapter(adapter);
                    audienceAdapter = new AudienceAdapter(CreateNewAd.this, getContentDetailsToBoostResponse.getAudience(), new AudienceAdapter.OnAudienceClickListener() {
                        @Override
                        public void onAudienceClick(int position, GetContentDetailsToBoostResponse.Audience goal) {
                            audienceId = goal.getAudienceId();
                            getEstimateBoostReach((double) seekBar.getProgress(), days, audienceId);

                        }

                        @Override
                        public void onAudienceEditClick(int position, GetContentDetailsToBoostResponse.Audience goal, int minAge, int maxAge, String gender) {
                            Intent intent = new Intent(CreateNewAd.this, EditAudience.class);
                            intent.putExtra("minAge", minAge);
                            intent.putExtra("maxAge", maxAge);
                            intent.putExtra("gender", gender);
                            intent.putExtra("audience", goal.getAudienceName());

                            editAudienceLauncher.launch(intent);


                        }
                    }, minAge, maxAge, gender);
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

    public static String formatDate(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }


    public void getInitiatePostBoost(String tid, String hashData, String hashName, PayUHashGenerationListener hashGenerationListener) {
        String phoneNo = "";
        if (!etPhone.getText().toString().isEmpty()) {
            phoneNo = "+91" + etPhone.getText().toString();
        } else {
            phoneNo = "";
        }
        String url = etUrl.getText().toString();
        String endDate = formatDate(tvDate.getText().toString());
        int genderType = 0;
        if (gender.equals("All")) {
            genderType = 0;
        } else if (gender.equals("Male")) {
            genderType = 1;
        } else if (gender.equals("Female")) {
            genderType = 1;
        }

        loader.show();
        InitiateBoostRequest request = new InitiateBoostRequest(tid, hashData, boostId, postId, url, phoneNo, xmlType, budgetGlobal, estimatedCost, subTotal, gstAmount, total, days, endDate, audienceId, minAge, maxAge, genderType, "Wishma Plus"

        );
        UtilMethods.INSTANCE.initiateBoostPost(request, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                boostResponse = (BoostResponse) object;
                if (boostResponse.getStatusCode() == 1) {

                }
                if (boostResponse != null) {
                    if (boostResponse.getStatusCode() == 1) {
                        boostId = boostResponse.getBoostId();

                        if (boostResponse.isPgActive() && boostResponse.getData() != null) {
                            if (boostResponse.getData().getStatusCode() == 1 && boostResponse.getData().getPgResponse() != null) {
                                if (boostResponse.getData().getPgResponse().getKeyVals() != null) {
                                    if (hashData == null || hashData.isEmpty()) {
                                        startPayUPayment(boostResponse.getData().getPgResponse().getKeyVals());
                                    } else {
                                        if (boostResponse.getData().getPgResponse().getKeyVals().getHash() != null && !boostResponse.getData().getPgResponse().getKeyVals().getHash().isEmpty()) {
                                            // hashPayUSDkPro = packageResponse.getData().getPgResponse().getKeyVals().getHash();
                                            HashMap<String, String> dataMap = new HashMap<>();
                                            dataMap.put(hashName, boostResponse.getData().getPgResponse().getKeyVals().getHash());
                                            /*Log.e("HashData", hashPayUSDkPro);*/
                                            hashGenerationListener.onHashGenerated(dataMap);

                                        } else {
                                            UtilMethods.INSTANCE.Error(CreateNewAd.this, "Problem in Hash generation");
                                        }
                                    }


                                } else {
                                    UtilMethods.INSTANCE.Error(CreateNewAd.this, "Transaction data is not available");
                                }
                                //call Gatway
                            } else {
                                UtilMethods.INSTANCE.Error(CreateNewAd.this, boostResponse.getData().getResponseText());
                            }
                        } else {
//                            getPackage();
                            Toast.makeText(CreateNewAd.this, boostResponse.getResponseText(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        UtilMethods.INSTANCE.Error(CreateNewAd.this, boostResponse.getResponseText());
                    }
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

    private void startPayUPayment(PgKeyVals keyVals) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
        mLastClickTime = SystemClock.elapsedRealtime();
        if (validateSDKParams(keyVals)) {
            //hashPayUSDkPro = keyVals.getHash();
            initUiSdk(preparePayUBizParams(keyVals), keyVals);
        }
    }

    private boolean validateSDKParams(PgKeyVals mKeyVals) {
        if (mKeyVals.getKey() == null || TextUtils.isEmpty(mKeyVals.getKey())) {
            UtilMethods.INSTANCE.Error(CreateNewAd.this, "Invalid or empty Key");
            return false;
        } else if (mKeyVals.getHash() == null || TextUtils.isEmpty(mKeyVals.getHash())) {
            UtilMethods.INSTANCE.Error(CreateNewAd.this, "Invalid or empty Hash");
            return false;
        } else if (mKeyVals.getTxnid() == null || TextUtils.isEmpty(mKeyVals.getTxnid())) {
            UtilMethods.INSTANCE.Error(CreateNewAd.this, "Invalid or empty Transaction Id");
            return false;
        } else if (mKeyVals.getEmail() == null || TextUtils.isEmpty(mKeyVals.getEmail())) {
            UtilMethods.INSTANCE.Error(CreateNewAd.this, "Invalid or empty Mail Id");
            return false;
        } else if (mKeyVals.getFirstname() == null || TextUtils.isEmpty(mKeyVals.getFirstname())) {
            UtilMethods.INSTANCE.Error(CreateNewAd.this, "Invalid or empty Name");
            return false;
        } else if (mKeyVals.getSurl() == null || TextUtils.isEmpty(mKeyVals.getSurl())) {
            UtilMethods.INSTANCE.Error(CreateNewAd.this, "Invalid or empty Success URL");
            return false;
        } else if (mKeyVals.getFurl() == null || TextUtils.isEmpty(mKeyVals.getFurl())) {
            UtilMethods.INSTANCE.Error(CreateNewAd.this, "Invalid or empty Fail URL");
            return false;
        }

        return true;
    }

    private PayUPaymentParams preparePayUBizParams(PgKeyVals mKeyVals) {

        HashMap<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(PayUCheckoutProConstants.CP_UDF1, "udf1");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF2, "udf2");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF3, "udf3");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF4, "udf4");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF5, "udf5");

        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setAmount(mKeyVals.getAmount()).setIsProduction(mKeyVals.isProdcution()).setProductInfo(mKeyVals.getProductinfo()).setKey(mKeyVals.getKey()).setPhone(mKeyVals.getPhone()).setTransactionId(mKeyVals.getTxnid()).setFirstName(mKeyVals.getFirstname()).setEmail(mKeyVals.getEmail()).setSurl(mKeyVals.getSurl()).setFurl(mKeyVals.getFurl()).setAdditionalParams(additionalParams).setUserCredential(mKeyVals.getKey() + mKeyVals.getEmail()).setPayUSIParams(null);
        PayUPaymentParams payUPaymentParams = builder.build();
        return payUPaymentParams;
    }

    private void initUiSdk(PayUPaymentParams payUPaymentParams, PgKeyVals mKeyVals) {
        PayUCheckoutPro.open(this, payUPaymentParams, getCheckoutProConfig(mKeyVals), new PayUCheckoutProListener() {

            @Override
            public void onPaymentSuccess(@NotNull Object response) {

                //HashMap<String, Object> result = (HashMap<String, Object>) response;
                // Log.e("PAYUProResp", "Payu's Data : " + result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE) + "\n\n\n Merchant's Data: " + result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE));
                // Object payuResponse = result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                // PayUCheckProResponse itemResponse = new Gson().fromJson((String) payuResponse, PayUCheckProResponse.class);
                //JSONObject payuCheckProJObject  = new JSONObject((String)payuResponse); // json
                //Log.e("PAYUProResp", "Payu's Success : " + itemResponse);
                payuStatusUpdate(mKeyVals.getTxnid()/*,itemResponse.getStatus()*/);
                //PayUCheckProCallBackApi(PayUCheckProSuccessData(mKeyVals, payuResponse, 2));
            }

            @Override
            public void onPaymentFailure(Object response) {
                //  HashMap<String, Object> result = (HashMap<String, Object>) response;
                //Log.e("PAYUProResp", "Payu's Data : " + result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE) + "\n\n\n Merchant's Data: " + result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE));
                //Object payuResponse = result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                //  try {
                // PayUCheckProResponse itemResponse = new Gson().fromJson((String) payuResponse, PayUCheckProResponse.class);
                payuStatusUpdate(mKeyVals.getTxnid()/*,itemResponse.getStatus()*/);
                // Log.e("PAYUProResp", "Payu's Error : " + itemResponse);
                //  PayUCheckProCallBackApi(PayUCheckProFailedData(mKeyVals, 3, itemResponse.getStatus(), itemResponse.getErrorMessage()));
                // } catch (Exception exception) {
                //  Log.e("PAYUProResp", "Payu's Error : " + exception.getMessage());
                // }

                       /*   UtilMethods.INSTANCE.Balancecheck(AddMoneyActivity.this, loader, object -> {
                                        balanceCheckResponse = (BalanceResponse) object;
                                        if (balanceCheckResponse != null && balanceCheckResponse.getBalanceData() != null) {
                                            showWalletListPopupWindow();
                                        }
                                    });*/

            }

            @Override
            public void onPaymentCancel(boolean isTxnInitiated) {
                showSnackBar(getResources().getString(R.string.transaction_cancelled_by_user));
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                String errorMessage = errorResponse.getErrorMessage();
                if (TextUtils.isEmpty(errorMessage))
                    errorMessage = getResources().getString(R.string.some_thing_error);
                showSnackBar(errorMessage);
            }

            @Override
            public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
                //For setting webview properties, if any. Check Customized Integration section for more details on this
                webView.setWebChromeClient(new CheckoutProWebChromeClient((Bank) o));
                webView.setWebViewClient(new CheckoutProWebViewClient((Bank) o, mKeyVals.getKey()));
            }

            @Override
            public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                    Log.e("CP_HASH_STRING", hashData);
                    Log.e("CP_HASH_NAME", hashName);

                    //Generate Hash Key From Server End---
                    //GatewayTransaction(hashData, hashName, hashGenerationListener);
                    /* String hash = hashString;*/
                    getInitiatePostBoost(mKeyVals.getTxnid(), hashData, hashName, hashGenerationListener);
                            /*if (!TextUtils.isEmpty(hashPayUSDkPro)) {
                                HashMap hashMap = new HashMap();
                                hashMap.put(hashName, hashPayUSDkPro);
                                hashGenerationListener.onHashGenerated(hashMap);
                            }*/
                }
            }

        });

        /*Log.e("PayUPaymentParams", new Gson().toJson(payUPaymentParams));*/
    }

    private PayUCheckoutProConfig getCheckoutProConfig(PgKeyVals mKeyVals) {
        PayUCheckoutProConfig checkoutProConfig = new PayUCheckoutProConfig();
        checkoutProConfig.setMerchantName(getString(R.string.app_name));
        //checkoutProConfig.setPaymentModesOrder(getCheckoutOrderList(mKeyVals));
        //checkoutProConfig.setOfferDetails(getOfferDetailsList());
        //checkoutProConfig.setPaymentModesOrder(getCheckoutOrderList(mKeyVals));
        checkoutProConfig.setEnforcePaymentList(getEnforcePaymentList(mKeyVals));
        checkoutProConfig.setShowCbToolbar(false);
        checkoutProConfig.setAutoSelectOtp(false);
        checkoutProConfig.setAutoApprove(false);
        checkoutProConfig.setMerchantSmsPermission(true);
        //checkoutProConfig.setSurePayCount(Integer.parseInt(binding.etSurePayCount.getText().toString()));
        checkoutProConfig.setShowExitConfirmationOnPaymentScreen(true);
        checkoutProConfig.setShowExitConfirmationOnCheckoutScreen(true);
        checkoutProConfig.setMerchantLogo(R.drawable.app_logo);
        checkoutProConfig.setMerchantResponseTimeout(10000); // for 10 seconds timeout
        checkoutProConfig.setWaitingTime(30000);// for 30 seconds read OTP Time
        //checkoutProConfig.setSurePayCount(3); //The Default value is 0.
        //checkoutProConfig.setCustomNoteDetails(getCustomeNoteList());
        /*if (reviewOrderAdapter != null)
            checkoutProConfig.setCartDetails(reviewOrderAdapter.getOrderDetailsList());*/
        return checkoutProConfig;
    }

    private ArrayList<HashMap<String, String>> getEnforcePaymentList(PgKeyVals mKeyVals) {
        ArrayList<HashMap<String, String>> enforceList = new ArrayList();


        if (mKeyVals.getEnforce_paymethod() != null && !mKeyVals.getEnforce_paymethod().isEmpty()) {
            HashMap<String, String> map = new HashMap<>();
            if (mKeyVals.getEnforce_paymethod().toLowerCase().contains("debitcard") || mKeyVals.getEnforce_paymethod().toLowerCase().contains("debit card") /*|| selectedMethod.toLowerCase().contains("debit")*/) {
                map.put(PayUCheckoutProConstants.CP_PAYMENT_TYPE, PaymentType.CARD.name());
                map.put(PayUCheckoutProConstants.CP_CARD_TYPE, CardType.DC.name());
                //map.put(PayUCheckoutProConstants.CP_CARD_SCHEME, CardScheme.RUPAY.name());
            } else if (mKeyVals.getEnforce_paymethod().toLowerCase().contains("creditcard") || mKeyVals.getEnforce_paymethod().toLowerCase().contains("credit card") /*|| selectedMethod.toLowerCase().contains("credit")*/) {
                map.put(PayUCheckoutProConstants.CP_PAYMENT_TYPE, PaymentType.CARD.name());
                map.put(PayUCheckoutProConstants.CP_CARD_TYPE, CardType.CC.name());
            } else if (mKeyVals.getEnforce_paymethod().toLowerCase().contains("upi") /*|| selectedMethod.toLowerCase().contains("upi")*/) {
                map.put(PayUCheckoutProConstants.CP_PAYMENT_TYPE, PaymentType.UPI_INTENT.name());
            } else if (mKeyVals.getEnforce_paymethod().toLowerCase().contains("net banking") || mKeyVals.getEnforce_paymethod().toLowerCase().contains("netbanking") /*|| selectedMethod.toLowerCase().contains("net")*/) {
                map.put(PayUCheckoutProConstants.CP_PAYMENT_TYPE, PaymentType.NB.name());
            } else if (mKeyVals.getEnforce_paymethod().toLowerCase().contains("wallet") /*|| selectedMethod.toLowerCase().contains("wallet")*/) {
                map.put(PayUCheckoutProConstants.CP_PAYMENT_TYPE, PaymentType.WALLET.name());
            }

            enforceList.add(map);
        }

        return enforceList;
    }

    public void payuStatusUpdate(String tid/*, String status*/) {
        try {

            loader.show();
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<UpgradePackageResponse> call = git.payUTransactionUpdate("Bearer " + tokenManager.getAccessToken(), tid);
            call.enqueue(new Callback<UpgradePackageResponse>() {
                @Override
                public void onResponse(@NonNull Call<UpgradePackageResponse> call, @NonNull Response<UpgradePackageResponse> response) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    try {
                        UpgradePackageResponse packageResponse = response.body();
                        if (packageResponse != null) {
                            if (packageResponse.getStatusCode() == 1) {
//                                getPackage();
                                UtilMethods.INSTANCE.SuccessWithOkay(CreateNewAd.this, packageResponse.getResponseText(), false);
                            } else {
                                UtilMethods.INSTANCE.Error(CreateNewAd.this, packageResponse.getResponseText());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilMethods.INSTANCE.Error(CreateNewAd.this, e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UpgradePackageResponse> call, @NonNull Throwable t) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    try {

                        UtilMethods.INSTANCE.apiFailureError(CreateNewAd.this, t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(CreateNewAd.this, ise.getMessage());

                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (loader != null) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }
            }
            UtilMethods.INSTANCE.Error(CreateNewAd.this, e.getMessage());

        }
    }

    public void getEstimateBoostReach(double budget, int days, int audienceId) {
        loader.show();
        UtilMethods.INSTANCE.getEstimateBoostReach(budget, days, audienceId, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                estimateResponse = (EstimateResponse) object;
                if (estimateResponse.getStatusCode() == 1) {
                    tvLine1.setText("Your ad will run continuously with a daily budget of ₹" + seekBar.getProgress() + ". Actual amount spent daily may vary.");
                    tvPeopleReached.setText(estimateResponse.getResult().getReach());
                    tvAdd.setText("₹" + estimateResponse.getResult().getUserBalance());
                    tvBudgetPrice.setText("₹" + estimateResponse.getResult().getBudget() + "");
                    tvCostPrice.setText("₹" + estimateResponse.getResult().getEstimatedCost() + "");
                    tvSubPrice.setText("₹" + estimateResponse.getResult().getSubTotal() + "");
                    tvGstPrice.setText("₹" + estimateResponse.getResult().getGst() + "");
                    tvPrice.setText("₹" + estimateResponse.getResult().getTotal() + "");

                    budgetGlobal = estimateResponse.getResult().getBudget();
                    estimatedCost = estimateResponse.getResult().getEstimatedCost();
                    gstAmount = estimateResponse.getResult().getGst();
                    subTotal = estimateResponse.getResult().getSubTotal();
                    total = estimateResponse.getResult().getTotal();
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

        if (bottomGoalDialogReport != null && bottomGoalDialogReport.isShowing()) return;

        bottomGoalDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_goal_list, null);


        bottomGoalDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(bottomGoalDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet)).setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomGoalDialogReport.show();
    }

    public void openSpecialCatBottomSheetDialog(Activity context) {

        if (bottomSpecialCatDialogReport != null && bottomSpecialCatDialogReport.isShowing())
            return;

        bottomSpecialCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_special_category, null);


        bottomSpecialCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(bottomSpecialCatDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet)).setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSpecialCatDialogReport.show();
    }

    public void openChooseAudienceBottomSheetDialog(Activity context) {

        if (bottomChooseAudienceCatDialogReport != null && bottomChooseAudienceCatDialogReport.isShowing())
            return;

        bottomChooseAudienceCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_choose_audience, null);


        bottomChooseAudienceCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(bottomChooseAudienceCatDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet)).setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomChooseAudienceCatDialogReport.show();
    }

    public void openIsSecureBottomSheetDialog(Activity context) {

        if (bottomIsSecureCatDialogReport != null && bottomIsSecureCatDialogReport.isShowing())
            return;

        bottomIsSecureCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_is_secure, null);


        bottomIsSecureCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(bottomIsSecureCatDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet)).setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomIsSecureCatDialogReport.show();
    }

    public void openPlacementsBottomSheetDialog(Activity context) {

        if (bottomPlacementsCatDialogReport != null && bottomPlacementsCatDialogReport.isShowing())
            return;

        bottomPlacementsCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_placements, null);


        bottomPlacementsCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(bottomPlacementsCatDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet)).setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomPlacementsCatDialogReport.show();
    }

    public void openBudgetBottomSheetDialog(Activity context) {

        if (bottomBudgetCatDialogReport != null && bottomBudgetCatDialogReport.isShowing()) return;

        bottomBudgetCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_budget, null);


        bottomBudgetCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(bottomBudgetCatDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet)).setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomBudgetCatDialogReport.show();
    }

    public void openPaymentBottomSheetDialog(Activity context) {

        if (bottomPaymentCatDialogReport != null && bottomPaymentCatDialogReport.isShowing())
            return;

        bottomPaymentCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_payment, null);


        bottomPaymentCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(bottomPaymentCatDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet)).setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomPaymentCatDialogReport.show();
    }

    private void showSnackBar(String message) {
        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_LONG).show();
    }
}