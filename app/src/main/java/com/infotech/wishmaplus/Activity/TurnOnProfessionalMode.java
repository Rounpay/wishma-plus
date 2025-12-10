package com.infotech.wishmaplus.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.R;

public class TurnOnProfessionalMode extends AppCompatActivity {

    TextView tvBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_turn_on_professional_mode);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvBottom = findViewById(R.id.tvBottom);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.btnTurnOn).setOnClickListener(v -> {
            Intent intent = new Intent(TurnOnProfessionalMode.this, WelcomeProfessional.class);
            startActivity(intent);
        });
        tvBottom.setText(Html.fromHtml("Facebook will show more information about profiles in professional mode. <font color='#1877F2'>Learn More</font>. By selecting &quot;Turn on&quot;, you agree to Meta’s <font color='#1877F2'>Commercial Terms</font>."));


    }
}