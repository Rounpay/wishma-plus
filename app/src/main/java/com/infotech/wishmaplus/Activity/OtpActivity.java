package com.infotech.wishmaplus.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.PinEntryEditTextBox;
import com.infotech.wishmaplus.Utils.UtilMethods;

public class OtpActivity extends AppCompatActivity {

    private String selectedNames, selectedIDs,pageName;
    private PinEntryEditTextBox otpEditText;
    private CustomLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        getIntentData();
        setupListeners();
    }

    private void initViews() {
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        otpEditText = findViewById(R.id.otpEditText);
    }

    private void getIntentData() {
        if (getIntent() != null) {
            selectedNames = getIntent().getStringExtra("selectedNames");
            selectedIDs = getIntent().getStringExtra("selectedIDs");
            pageName = getIntent().getStringExtra("pageName");
        }
    }

    private void setupListeners() {
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.bt_fwpCancel).setOnClickListener(v -> finish());
        findViewById(R.id.bt_fwpSubmit).setOnClickListener(v -> {
            String otp = otpEditText.getText().toString().trim();
            if (otp.isEmpty()) {
                otpEditText.setError("Enter OTP");
                otpEditText.requestFocus();
                return;
            }
            if (otp.length() < 6) {
                otpEditText.setError("Enter valid 6-digit OTP");
                otpEditText.requestFocus();
                return;
            }

            callOtpApi(otp);
        });
    }

    private void callOtpApi(String otp) {

        loader.show();
        UtilMethods.INSTANCE.setProfileType(
                OtpActivity.this,
                otp,
                loader,
                new UtilMethods.ApiCallBackMulti() {
                    @Override
                    public void onSuccess(Object object) {
                        loader.dismiss();
                        moveToNextScreen();
                    }

                    @Override
                    public void onError(String msg) {
                        loader.dismiss();
                        moveToNextScreen();
                        UtilMethods.INSTANCE.Error(OtpActivity.this, msg);
                    }
                }
        );
    }

    private void moveToNextScreen() {
        Intent intent = new Intent(OtpActivity.this, SettingUpYourPage.class);
        intent.putExtra("selectedNames", selectedNames);
        intent.putExtra("selectedIDs", selectedIDs);
        intent.putExtra("pageName", pageName);
        startActivity(intent);
        finish();
    }
}
