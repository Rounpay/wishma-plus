package com.infotech.wishmaplus.Activity;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.R;

public class SettingUpYourPage extends AppCompatActivity {

    AutoCompleteTextView etBio, etWebsite, etEmail, etPhone, etAddress, etCity, etPostcode;
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting_up_your_page);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        etBio = findViewById(R.id.etBio);
        etWebsite = findViewById(R.id.etWebsite);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);

        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (!isValidEmail(email)) {
                etEmail.setError("Please enter a valid email");
                etEmail.requestFocus();
                return;
            }

            if (!isValidPhone(phone)) {
                etPhone.setError("Enter valid mobile number");
                etPhone.requestFocus();
                return;
            }

            Toast.makeText(this, "All OK!", Toast.LENGTH_SHORT).show();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidPhone(String phone) {
        return phone.matches("[6-9][0-9]{9}");
    }


}