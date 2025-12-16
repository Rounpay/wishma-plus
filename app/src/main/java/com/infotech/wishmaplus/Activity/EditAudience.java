package com.infotech.wishmaplus.Activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.slider.RangeSlider;
import com.infotech.wishmaplus.R;

public class EditAudience extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_audience);
        RangeSlider slider = findViewById(R.id.continuousRangeSlider);
        TextView tvAll = findViewById(R.id.tvAll);
        TextView tvMale = findViewById(R.id.tvMale);
        TextView tvFemale = findViewById(R.id.tvFemale);
        View.OnClickListener listener = v -> {
            tvAll.setBackgroundResource(R.drawable.bg_segment_unselected);
            tvMale.setBackgroundResource(R.drawable.bg_segment_unselected);
            tvFemale.setBackgroundResource(R.drawable.bg_segment_unselected);

            tvAll.setTextColor(Color.GRAY);
            tvMale.setTextColor(Color.GRAY);
            tvFemale.setTextColor(Color.GRAY);

            v.setBackgroundResource(R.drawable.bg_segment_selected);
            ((TextView) v).setTextColor(Color.parseColor("#2196F3"));
        };

        tvAll.setOnClickListener(listener);
        tvMale.setOnClickListener(listener);
        tvFemale.setOnClickListener(listener);
        slider.setThumbTintList(ColorStateList.valueOf(Color.parseColor("#1A73E8")));
        slider.setTrackActiveTintList(ColorStateList.valueOf(Color.parseColor("#1A73E8")));
        slider.setTrackInactiveTintList(ColorStateList.valueOf(Color.parseColor("#D3D3D3")));
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}