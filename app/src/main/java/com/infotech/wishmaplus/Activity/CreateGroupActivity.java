package com.infotech.wishmaplus.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class CreateGroupActivity extends AppCompatActivity {

    EditText etGroupName;
    //    Spinner spinnerPrivacy;
    Button btnCreateGroup;

    TextView privacyTextView, learnMore, spinnerPrivacy,tvSelectedVisibility;

    LinearLayout visibilityLayout;

    BottomSheetDialog bottomPrivacyDialogReport;
    BottomSheetDialog bottomVisibilityDialogReport;
    int selectedPrivacy = 0;
    int selectedVisibility = 0;

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
        spinnerPrivacy = findViewById(R.id.tvSelected);
        privacyTextView = findViewById(R.id.privacyText);
        learnMore = findViewById(R.id.learnMore);
        visibilityLayout = findViewById(R.id.visibilitySpinner);
        tvSelectedVisibility = findViewById(R.id.tvSelectedVisibility);
        findViewById(R.id.spinnerPrivacy).setOnClickListener(view -> openPrivacyBottomSheetDialog(this));
        findViewById(R.id.visibility).setOnClickListener(view -> openVisibilityBottomSheetDialog(this));
        etGroupName = findViewById(R.id.etGroupName);
        findViewById(R.id.btnCreateGroup).setOnClickListener(view -> {
            Intent intent = new Intent(CreateGroupActivity.this, GroupAddPeople.class);
            startActivity(intent);
        });
//        spinnerPrivacy = findViewById(R.id.spinnerPrivacy);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);
        // Spinner Values
//        String[] privacyOptions = {"Choose privacy", "Public", "Private"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                android.R.layout.simple_spinner_dropdown_item, privacyOptions);
//        spinnerPrivacy.setAdapter(adapter);

        // Enable button only when valid
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validate();
            }

            public void afterTextChanged(Editable s) {
            }
        };
        etGroupName.addTextChangedListener(watcher);

//        spinnerPrivacy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                validate();
//            }
//            @Override public void onNothingSelected(AdapterView<?> parent) {}
//        });
    }

    public void openPrivacyBottomSheetDialog(Activity context) {

        if (bottomPrivacyDialogReport != null && bottomPrivacyDialogReport.isShowing())
            return;

        bottomPrivacyDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_choose_privacy, null);

        RadioButton rbPublic = sheetView.findViewById(R.id.rbPublic);
        RadioButton rbPrivate = sheetView.findViewById(R.id.rbPrivate);
        View optionPublic = sheetView.findViewById(R.id.optionPublic);
        View optionPrivate = sheetView.findViewById(R.id.optionPrivate);
        View tvDone = sheetView.findViewById(R.id.tvDone);

        // Update UI based on current selection
        updateSelection(rbPublic, rbPrivate);

        optionPublic.setOnClickListener(v -> selectPrivacy(1, rbPublic, rbPrivate));
        optionPrivate.setOnClickListener(v -> selectPrivacy(2, rbPublic, rbPrivate));
        tvDone.setOnClickListener(v -> bottomPrivacyDialogReport.dismiss());

        bottomPrivacyDialogReport.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomPrivacyDialogReport.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomPrivacyDialogReport.show();
    }

    private void selectPrivacy(int privacy,
                               RadioButton rbPublic,
                               RadioButton rbPrivate) {

        selectedPrivacy = privacy;
        updateSelection(rbPublic, rbPrivate);
        bottomPrivacyDialogReport.dismiss();
    }

    private void updateSelection(RadioButton rbPublic, RadioButton rbPrivate) {

        if (selectedPrivacy == 1) {
            rbPublic.setChecked(true);
            rbPrivate.setChecked(false);

            spinnerPrivacy.setText("Public");
            privacyTextView.setText(R.string.public_text);
            visibilityLayout.setVisibility(View.GONE);

            privacyTextView.setVisibility(View.VISIBLE);
            learnMore.setVisibility(View.VISIBLE);

        } else if (selectedPrivacy == 2) {
            rbPublic.setChecked(false);
            rbPrivate.setChecked(true);

            spinnerPrivacy.setText("Private");
            privacyTextView.setText(R.string.private_group_text);
            visibilityLayout.setVisibility(View.VISIBLE);

            privacyTextView.setVisibility(View.VISIBLE);
            learnMore.setVisibility(View.VISIBLE);

        } else {
            // Nothing selected initially
            rbPublic.setChecked(false);
            rbPrivate.setChecked(false);

            privacyTextView.setVisibility(View.GONE);
            learnMore.setVisibility(View.GONE);
            visibilityLayout.setVisibility(View.GONE);
        }
    }



    public void openVisibilityBottomSheetDialog(Activity context) {

        if (bottomVisibilityDialogReport != null && bottomVisibilityDialogReport.isShowing()) {
            return;
        }

        bottomVisibilityDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        bottomVisibilityDialogReport.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_visibility, null);
        bottomVisibilityDialogReport.setContentView(view);

        RadioButton rbVisible = view.findViewById(R.id.rbVisible);
        RadioButton rbHidden = view.findViewById(R.id.rbHidden);
        View vHidden = view.findViewById(R.id.hiddenOption);
        View vVisible = view.findViewById(R.id.visibleOption);

        // Handle pre-selected state
        if (selectedVisibility == 1) {
            rbVisible.setChecked(true);
        } else if (selectedVisibility == 2) {
            rbHidden.setChecked(true);
        }

        // Click Listeners
        vVisible.setOnClickListener(v -> updateVisibility(1, "Visible"));
        vHidden.setOnClickListener(v -> updateVisibility(2, "Hidden"));
        rbVisible.setOnClickListener(v -> updateVisibility(1, "Visible"));
        rbHidden.setOnClickListener(v -> updateVisibility(2, "Hidden"));

        BottomSheetBehavior.from(
                bottomVisibilityDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        ).setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomVisibilityDialogReport.show();
    }

    private void updateVisibility(int value, String label) {
        selectedVisibility = value;
        tvSelectedVisibility.setText(label);
        if (bottomVisibilityDialogReport != null) {
            bottomVisibilityDialogReport.dismiss();
        }
    }


    private void validate() {
        String name = etGroupName.getText().toString().trim();
        boolean isPrivacySelected = true; //spinnerPrivacy.getSelectedItemPosition() != 0;

        if (!name.isEmpty() && isPrivacySelected) {
            btnCreateGroup.setEnabled(true);
        } else {
            btnCreateGroup.setEnabled(false);
        }
    }
}