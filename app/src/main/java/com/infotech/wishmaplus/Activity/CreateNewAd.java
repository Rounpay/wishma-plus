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

public class CreateNewAd extends AppCompatActivity {
    BottomSheetDialog bottomGoalDialogReport;
    BottomSheetDialog bottomSpecialCatDialogReport;
    BottomSheetDialog bottomChooseAudienceCatDialogReport;
    BottomSheetDialog bottomIsSecureCatDialogReport;
    BottomSheetDialog bottomPlacementsCatDialogReport;
    BottomSheetDialog bottomBudgetCatDialogReport;
    BottomSheetDialog bottomPaymentCatDialogReport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_new_ad);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.imgInfoGoal).setOnClickListener(v -> openGoalBottomSheetDialog(this));
        findViewById(R.id.imgInfo).setOnClickListener(v -> openSpecialCatBottomSheetDialog(this));
        findViewById(R.id.infoAudience).setOnClickListener(v -> openChooseAudienceBottomSheetDialog(this));
        findViewById(R.id.info).setOnClickListener(v -> openIsSecureBottomSheetDialog(this));
        findViewById(R.id.ivInfo).setOnClickListener(v -> openPlacementsBottomSheetDialog(this));
        findViewById(R.id.infoBudget).setOnClickListener(v -> openBudgetBottomSheetDialog(this));
        findViewById(R.id.ivInfoPayment).setOnClickListener(v -> openPaymentBottomSheetDialog(this));
    }
    public void openGoalBottomSheetDialog(Activity context) {

        if (bottomGoalDialogReport != null && bottomGoalDialogReport.isShowing())
            return;

        bottomGoalDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_goal_list, null);




        bottomGoalDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomGoalDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomGoalDialogReport.show();
    }

    public void openSpecialCatBottomSheetDialog(Activity context) {

        if (bottomSpecialCatDialogReport != null && bottomSpecialCatDialogReport.isShowing())
            return;

        bottomSpecialCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_special_category, null);




        bottomSpecialCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomSpecialCatDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSpecialCatDialogReport.show();
    }
    public void openChooseAudienceBottomSheetDialog(Activity context) {

        if (bottomChooseAudienceCatDialogReport != null && bottomChooseAudienceCatDialogReport.isShowing())
            return;

        bottomChooseAudienceCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_choose_audience, null);




        bottomChooseAudienceCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomChooseAudienceCatDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomChooseAudienceCatDialogReport.show();
    }
    public void openIsSecureBottomSheetDialog(Activity context) {

        if (bottomIsSecureCatDialogReport != null && bottomIsSecureCatDialogReport.isShowing())
            return;

        bottomIsSecureCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_is_secure, null);




        bottomIsSecureCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomIsSecureCatDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomIsSecureCatDialogReport.show();
    }
    public void openPlacementsBottomSheetDialog(Activity context) {

        if (bottomPlacementsCatDialogReport != null && bottomPlacementsCatDialogReport.isShowing())
            return;

        bottomPlacementsCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_placements, null);




        bottomPlacementsCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomPlacementsCatDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomPlacementsCatDialogReport.show();
    }
    public void openBudgetBottomSheetDialog(Activity context) {

        if (bottomBudgetCatDialogReport != null && bottomBudgetCatDialogReport.isShowing())
            return;

        bottomBudgetCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_budget, null);




        bottomBudgetCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomBudgetCatDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomBudgetCatDialogReport.show();
    }
    public void openPaymentBottomSheetDialog(Activity context) {

        if (bottomPaymentCatDialogReport != null && bottomPaymentCatDialogReport.isShowing())
            return;

        bottomPaymentCatDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_payment, null);




        bottomPaymentCatDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomPaymentCatDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomPaymentCatDialogReport.show();
    }
}