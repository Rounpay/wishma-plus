package com.infotech.wishmaplus.Activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;

public class ProfessionalDashBoardPersonal extends AppCompatActivity {
    LinearLayout analyticsHead,contentHead;
    private CustomLoader loader;
    private PreferencesManager tokenManager;
    androidx.appcompat.widget.AppCompatImageView profile_image;
    androidx.appcompat.widget.AppCompatTextView profile_name;

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
        AppCompatImageButton back_button = findViewById(R.id.back_button);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        profile_image = findViewById(R.id.profile_image);
        profile_name = findViewById(R.id.profile_name);

        tokenManager = new PreferencesManager(this, 1);
        back_button.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
        AppCompatTextView tvPercent = findViewById(R.id.tv_percent);
        ProgressBar progressCircle = findViewById(R.id.progress_circle);
        analyticsHead = findViewById(R.id.analyticsHead);
        contentHead = findViewById(R.id.contentHead);
        int progress = 60;
        ObjectAnimator anim = ObjectAnimator.ofInt(progressCircle, "progress", progress);
        anim.setDuration(700);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
        tvPercent.setText(progress + "%");
        progressCircle.setProgress(progress);
        findViewById(R.id.profileCard).setOnClickListener(v -> {
            Intent intent = new Intent(ProfessionalDashBoardPersonal.this,
                    DashBoardPersonalInformation.class);
            startActivity(intent);
        });
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
//        getUserDetail();
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
}