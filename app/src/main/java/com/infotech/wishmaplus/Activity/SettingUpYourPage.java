package com.infotech.wishmaplus.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.R;

public class SettingUpYourPage extends AppCompatActivity {

    AutoCompleteTextView etBio, etWebsite, etEmail, etPhone, etAddress;
    String selectedNames,selectedIDs,pageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting_up_your_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getIntent() != null) {
            selectedNames = getIntent().getStringExtra("selectedNames");
            selectedIDs = getIntent().getStringExtra("selectedIDs");
            pageName = getIntent().getStringExtra("pageName");
        }
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        etBio = findViewById(R.id.etBio);
        etWebsite = findViewById(R.id.etWebsite);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        AppCompatTextView btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(v -> validateAndProceed());
    }

    private void validateAndProceed() {
        String bio = etBio.getText().toString().trim();
        String website = etWebsite.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        if (bio.isEmpty() || bio.length() < 3) {
            etBio.setError("Please enter at least 3 characters");
            etBio.requestFocus();
            return;
        }
        if (!website.isEmpty() && !website.matches("^(https?://).+")) {
            etWebsite.setError("Website must start with http:// or https://");
            etWebsite.requestFocus();
            return;
        }
        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email address");
            etEmail.requestFocus();
            return;
        }
        if (!phone.matches("^[6-9][0-9]{9}$")) {
            etPhone.setError("Enter valid 10-digit mobile number starting with 6, 7, 8, or 9");
            etPhone.requestFocus();
            return;
        }
        if (address.isEmpty() || address.length() < 4) {
            etAddress.setError("Enter valid address");
            etAddress.requestFocus();
            return;
        }
        goToNextScreen(bio, website, email, phone, address);
    }

    private void goToNextScreen(String bio, String website, String email, String phone, String address) {
        Intent intent = new Intent(this, PageProfilePicture.class);
        intent.putExtra("bio", bio);
        intent.putExtra("website", website);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("address", address);
        intent.putExtra("pageName", pageName);
        intent.putExtra("selectedIDs", selectedIDs);
        startActivity(intent);
    }
}