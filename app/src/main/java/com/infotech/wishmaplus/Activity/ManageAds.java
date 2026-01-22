package com.infotech.wishmaplus.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.infotech.wishmaplus.R;

import java.util.Objects;

public class ManageAds extends AppCompatActivity {
    BottomSheetDialog bottomFilterDialogReport;
    TextView tvFilter;
    String currentlyView = "Last 60 days";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_ads);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvFilter = findViewById(R.id.tvFilter);
        tvFilter.setText(currentlyView);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.layoutFilter).setOnClickListener(v -> openFilterBottomSheetDialog(this));
    }

    public void openFilterBottomSheetDialog(Activity context) {

        if (bottomFilterDialogReport != null && bottomFilterDialogReport.isShowing()) return;

        bottomFilterDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_filter, null);

        RadioButton rb7 = view.findViewById(R.id.rbLast7Days);
        RadioButton rb30 = view.findViewById(R.id.rbLast30Days);
        RadioButton rb60 = view.findViewById(R.id.rbLast60Days);
        RadioButton rb90 = view.findViewById(R.id.rbLast90Days);

        // Restore selection
        checkRadio(currentlyView, rb7, rb30, rb60, rb90);

        view.findViewById(R.id.layout7).setOnClickListener(v -> selectFilter(rb7, "Last 7 days"));
        view.findViewById(R.id.layout30).setOnClickListener(v -> selectFilter(rb30, "Last 30 days"));
        view.findViewById(R.id.layout60).setOnClickListener(v -> selectFilter(rb60, "Last 60 days"));
        view.findViewById(R.id.layout90).setOnClickListener(v -> selectFilter(rb90, "Last 90 days"));

        bottomFilterDialogReport.setContentView(view);
        BottomSheetBehavior.from(Objects.requireNonNull(bottomFilterDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet))).setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomFilterDialogReport.show();
    }

    private void selectFilter(RadioButton selectedRb, String value) {
        selectedRb.setChecked(true);
        currentlyView = value;
        tvFilter.setText(value);
        bottomFilterDialogReport.dismiss();
    }

    private void checkRadio(String value, RadioButton rb7, RadioButton rb30, RadioButton rb60, RadioButton rb90) {

        rb7.setChecked("Last 7 days".equals(value));
        rb30.setChecked("Last 30 days".equals(value));
        rb60.setChecked("Last 60 days".equals(value));
        rb90.setChecked("Last 90 days".equals(value));
    }


}