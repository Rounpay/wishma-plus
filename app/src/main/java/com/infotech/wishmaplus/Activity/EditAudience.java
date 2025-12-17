package com.infotech.wishmaplus.Activity;

import android.app.Activity;
import android.content.Intent;
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

import org.jspecify.annotations.NonNull;

import java.util.List;

public class EditAudience extends AppCompatActivity {
    int minAge;
    int maxAge;
    String gender,audience;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_audience);
        RangeSlider slider = findViewById(R.id.continuousRangeSlider);
        TextView tvAll = findViewById(R.id.tvAll);
        TextView tvMale = findViewById(R.id.tvMale);
        TextView tvFemale = findViewById(R.id.tvFemale);
        minAge = getIntent().getIntExtra("minAge", 18);
        maxAge = getIntent().getIntExtra("maxAge", 65);
        gender = getIntent().getStringExtra("gender");
        audience = getIntent().getStringExtra("audience");
        TextView tvAudienceName = findViewById(R.id.etAudienceName);
        tvAudienceName.setText(audience);
        slider.setLabelFormatter(value -> String.valueOf((int) value));
        slider.setValues((float) minAge, (float) maxAge);
        if (gender.equals("All")) {
            tvAll.setBackgroundResource(R.drawable.bg_segment_selected);
            tvMale.setBackgroundResource(R.drawable.bg_segment_unselected);
            tvFemale.setBackgroundResource(R.drawable.bg_segment_unselected);

            tvAll.setTextColor(Color.parseColor("#2196F3"));
        }
        else if (gender.equals("Male")) {
            tvAll.setBackgroundResource(R.drawable.bg_segment_unselected);
            tvMale.setBackgroundResource(R.drawable.bg_segment_selected);
            tvFemale.setBackgroundResource(R.drawable.bg_segment_unselected);

            tvMale.setTextColor(Color.parseColor("#2196F3"));
        }
        else if (gender.equals("Female")) {
            tvAll.setBackgroundResource(R.drawable.bg_segment_unselected);
            tvMale.setBackgroundResource(R.drawable.bg_segment_unselected);
            tvFemale.setBackgroundResource(R.drawable.bg_segment_selected);

            tvFemale.setTextColor(Color.parseColor("#2196F3"));
        }

        View.OnClickListener listener = v -> {
            tvAll.setBackgroundResource(R.drawable.bg_segment_unselected);
            tvMale.setBackgroundResource(R.drawable.bg_segment_unselected);
            tvFemale.setBackgroundResource(R.drawable.bg_segment_unselected);

            tvAll.setTextColor(Color.GRAY);
            tvMale.setTextColor(Color.GRAY);
            tvFemale.setTextColor(Color.GRAY);

            v.setBackgroundResource(R.drawable.bg_segment_selected);
            gender = ((TextView) v).getText().toString();
            ((TextView) v).setTextColor(Color.parseColor("#2196F3"));
        };

        tvAll.setOnClickListener(listener);
        tvMale.setOnClickListener(listener);
        tvFemale.setOnClickListener(listener);
        slider.setThumbTintList(ColorStateList.valueOf(Color.parseColor("#1A73E8")));
        slider.setTrackActiveTintList(ColorStateList.valueOf(Color.parseColor("#1A73E8")));
        slider.setTrackInactiveTintList(ColorStateList.valueOf(Color.parseColor("#D3D3D3")));
        findViewById(R.id.back_button).setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("minAge", minAge);
            resultIntent.putExtra("maxAge", maxAge);
            resultIntent.putExtra("gender", gender);

            setResult(Activity.RESULT_OK, resultIntent);
            finish();

        });
        slider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) { }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {

                List<Float> values = slider.getValues();

                minAge = Math.round(values.get(0));
                maxAge = Math.round(values.get(1));
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}