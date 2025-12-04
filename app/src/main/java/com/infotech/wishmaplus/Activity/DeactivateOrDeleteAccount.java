package com.infotech.wishmaplus.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.R;

public class DeactivateOrDeleteAccount extends AppCompatActivity {
    Boolean isDelete = false;

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
        View card = findViewById(R.id.btnContinue);
        View rbDelete = findViewById(R.id.rbDelete);

        setCardState(card, false);

        View.OnClickListener deleteClick = v -> {
            isDelete = !isDelete;
            ((RadioButton) rbDelete).setChecked(isDelete);
            setCardState(card, isDelete);
        };
        findViewById(R.id.deleteLayout).setOnClickListener(deleteClick);
        findViewById(R.id.rbDelete).setOnClickListener(deleteClick);
        findViewById(R.id.deleteSubText).setOnClickListener(deleteClick);


        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        findViewById(R.id.btnContinue).setOnClickListener(view -> {
            startActivity(new Intent(this, DeleteAccountReasonActivity.class));
        });
        findViewById(R.id.btnCancel).setOnClickListener(view -> finish());
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
    private void setCardState(View view, boolean enable) {
        view.setEnabled(enable);
        view.setClickable(enable);
        view.setAlpha(enable ? 1f : 0.5f);
    }
}