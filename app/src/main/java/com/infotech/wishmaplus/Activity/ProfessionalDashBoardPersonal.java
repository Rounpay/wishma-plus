package com.infotech.wishmaplus.Activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.R;

public class ProfessionalDashBoardPersonal extends AppCompatActivity {

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
        back_button.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
        AppCompatTextView tvPercent = findViewById(R.id.tv_percent);
        ProgressBar progressCircle = findViewById(R.id.progress_circle);
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
        findViewById(R.id.creator_support_card).setOnClickListener(v -> {
            Intent intent = new Intent(ProfessionalDashBoardPersonal.this,
                    CreatorSupportActivity.class);
            startActivity(intent);
        });
    }
}