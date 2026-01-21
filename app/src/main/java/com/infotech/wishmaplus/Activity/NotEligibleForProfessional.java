package com.infotech.wishmaplus.Activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.Api.Response.EligibilityModel;
import com.infotech.wishmaplus.R;

public class NotEligibleForProfessional extends AppCompatActivity {
    EligibilityModel eligibilityModel = new EligibilityModel();
    TextView tvTitle,followersYouHave,followersMinimum,postsYouHave,postsMinimum,engagementYouHave,engagementMinimum;

    com.google.android.material.button.MaterialButton btnTurnOn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_not_eligible_for_professional);
        eligibilityModel = getIntent().getParcelableExtra("eligibilityData");

        if (eligibilityModel == null) {
            Toast.makeText(this, "No eligibility", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        tvTitle = findViewById(R.id.tvTitle);
        followersYouHave = findViewById(R.id.followersYouHave);
        followersMinimum = findViewById(R.id.followersMinimum);
        postsYouHave = findViewById(R.id.postsYouHave);
        postsMinimum = findViewById(R.id.postsMinimum);
        engagementYouHave = findViewById(R.id.engagementYouHave);
        engagementMinimum = findViewById(R.id.engagementMinimum);
        btnTurnOn = findViewById(R.id.btnTurnOn);

        tvTitle.setText(eligibilityModel.getResponseText());
        followersYouHave.setText("You currently have " + eligibilityModel.getFollowers() + " followers.");
        followersMinimum.setText(eligibilityModel.getMinFollowersRequired());
        postsYouHave.setText("You currently have " + eligibilityModel.getMonthlyPosts() + " posts.");
        postsMinimum.setText(eligibilityModel.getMinMonthlyPostsRequired());
        engagementYouHave.setText("Your engagement rate is " + eligibilityModel.getEngagementRate() + "%.");
        engagementMinimum.setText(eligibilityModel.getMinEngagementRateRequired());

        btnTurnOn.setOnClickListener(view -> finish());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }
}