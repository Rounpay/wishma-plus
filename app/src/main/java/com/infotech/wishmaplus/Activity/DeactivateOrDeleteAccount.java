package com.infotech.wishmaplus.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.R;

public class DeactivateOrDeleteAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deactivate_or_delete_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        findViewById(R.id.btnContinue).setOnClickListener(view -> {
            startActivity(new Intent(this, DeleteAccountReasonActivity.class));
        });
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        Button btnContinue = findViewById(R.id.btnContinue);

//        btnContinue.setEnabled(false);
//        btnContinue.setAlpha(0.4f);
//
//        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
//            if (checkedId != -1) {
//                btnContinue.setEnabled(true);
//                btnContinue.setAlpha(1f);
//            }
//        });

    }
}