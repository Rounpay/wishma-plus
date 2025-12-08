package com.infotech.wishmaplus.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.R;

public class Advertisement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_advertisement);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.boostContent).setOnClickListener(v ->{
            startActivity(new Intent(this, BoostContent.class));
        });
        findViewById(R.id.createNewAd).setOnClickListener(v ->{
            startActivity(new Intent(this, CreateNewAd.class));
        });
        findViewById(R.id.moreBTn).setOnClickListener(v ->{
            startActivity(new Intent(this, ManageAds.class));
        });

    }
}