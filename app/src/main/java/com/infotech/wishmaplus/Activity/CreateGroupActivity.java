package com.infotech.wishmaplus.Activity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.infotech.wishmaplus.R;

public class CreateGroupActivity extends AppCompatActivity {

    EditText etGroupName;
    //    Spinner spinnerPrivacy;
    Button btnCreateGroup;

    BottomSheetDialog bottomPrivacyDialogReport;
    BottomSheetDialog bottomVisibilityDialogReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_group);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        findViewById(R.id.spinnerPrivacy).setOnClickListener(view -> openPrivacyBottomSheetDialog(this));
        findViewById(R.id.visibility).setOnClickListener(view -> openVisibilityBottomSheetDialog(this));
        etGroupName = findViewById(R.id.etGroupName);

        btnCreateGroup = findViewById(R.id.btnCreateGroup);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validate();
            }
            public void afterTextChanged(Editable s) {}
        };
        etGroupName.addTextChangedListener(watcher);


    }
    public  void openPrivacyBottomSheetDialog(Activity context){
        if (bottomPrivacyDialogReport != null && bottomPrivacyDialogReport.isShowing()) {
            return;
        }
        bottomPrivacyDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        bottomPrivacyDialogReport.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inflater.inflate(R.layout.bottom_sheet_choose_privacy, null);
        RadioButton rbPublic = sheetView.findViewById(R.id.rbPublic);
        RadioButton rbPrivate = sheetView.findViewById(R.id.rbPrivate);
        sheetView.findViewById(R.id.optionPublic).setOnClickListener(v -> {
            rbPublic.setChecked(true);
            rbPrivate.setChecked(false);
        });

        sheetView.findViewById(R.id.optionPrivate).setOnClickListener(v -> {
            rbPrivate.setChecked(true);
            rbPublic.setChecked(false);
        });
        sheetView.findViewById(R.id.tvDone).setOnClickListener(v -> bottomPrivacyDialogReport.dismiss());

        bottomPrivacyDialogReport.setContentView(sheetView);
        BottomSheetBehavior
                .from(bottomPrivacyDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomPrivacyDialogReport.show();

    }

    public  void openVisibilityBottomSheetDialog(Activity context){
        if (bottomVisibilityDialogReport != null && bottomVisibilityDialogReport.isShowing()) {
            return;
        }
        bottomVisibilityDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        bottomVisibilityDialogReport.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inflater.inflate(R.layout.bottom_sheet_visibility, null);


        bottomVisibilityDialogReport.setContentView(sheetView);
        BottomSheetBehavior
                .from(bottomVisibilityDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomVisibilityDialogReport.show();

    }
    private void validate() {
        String name = etGroupName.getText().toString().trim();
        boolean isPrivacySelected = true; //spinnerPrivacy.getSelectedItemPosition() != 0;

        if(!name.isEmpty() && isPrivacySelected) {
            btnCreateGroup.setEnabled(true);
        } else {
            btnCreateGroup.setEnabled(false);
        }
    }
}