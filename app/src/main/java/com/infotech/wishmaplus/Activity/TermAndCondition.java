package com.infotech.wishmaplus.Activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.infotech.wishmaplus.R;

public class TermAndCondition extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_term_and_condition);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        CheckBox cbAgree = findViewById(R.id.cb_agree);
        TextView tvAgree = findViewById(R.id.tv_agreeText);
        MaterialButton btSave = findViewById(R.id.bt_save);

        btSave.setEnabled(false); // Initially disabled

        String text = "I agree to the Privacy Policy and Terms & Conditions";
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan privacyClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(TermAndCondition.this, "Privacy Policy Clicked", Toast.LENGTH_SHORT).show();
            }
        };

        ClickableSpan termsClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(TermAndCondition.this, "Terms & Conditions Clicked", Toast.LENGTH_SHORT).show();
            }
        };

// Index positions
        spannableString.setSpan(privacyClick, 15, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(termsClick, 34, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvAgree.setText(spannableString);
        tvAgree.setMovementMethod(LinkMovementMethod.getInstance());
        tvAgree.setHighlightColor(Color.TRANSPARENT);

// Enable button only when checkbox checked
        cbAgree.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btSave.setEnabled(isChecked);

            if (isChecked) {
                // When checked → Enable + Accent Color
                btSave.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.colorAccent)
                ));
            } else {
                // When unchecked → Disable + Gray Color
                btSave.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.grey_1)
                ));
            }
        });




    }
}