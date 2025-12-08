package com.infotech.wishmaplus.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.infotech.wishmaplus.R;

public class ManageAds extends AppCompatActivity {
    BottomSheetDialog bottomFilterDialogReport;
    BottomSheetDialog bottomSpecialCatDialogReport;

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
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.layoutFilter).setOnClickListener(v -> openFilterBottomSheetDialog(this));
    }
    public void openFilterBottomSheetDialog(Activity context) {

        if (bottomFilterDialogReport != null && bottomFilterDialogReport.isShowing())
            return;

        bottomFilterDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_filter, null);




        bottomFilterDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomFilterDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomFilterDialogReport.show();
    }

}