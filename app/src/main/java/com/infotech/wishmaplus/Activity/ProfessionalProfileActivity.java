package com.infotech.wishmaplus.Activity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.UtilMethods;

public class ProfessionalProfileActivity extends AppCompatActivity {
    private AutoCompleteTextView pageName;
    private AppCompatTextView continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_professional_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        pageName = findViewById(R.id.pageName);
        continueBtn = findViewById(R.id.continueBtn);
        continueBtn.setEnabled(false);
        continueBtn.setAlpha(0.5f);
        pageName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!validatePage()) {
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        continueBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateProfessionalPage.class);
            intent.putExtra("pageName",pageName.getText().toString());
            startActivity(intent);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        });
        AppCompatImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> {
            finish();
        });
        TextView optionOneTitle = findViewById(R.id.optionOneTitle);
        SpannableString ss = getProfessionalDetail();
        optionOneTitle.setText(ss);
        optionOneTitle.setMovementMethod(LinkMovementMethod.getInstance());
        optionOneTitle.setHighlightColor(Color.TRANSPARENT);
    }

    private boolean validatePage() {
        String newPageName = pageName.getText().toString().trim();
        if (newPageName.isEmpty()) {
            pageName.setError("Please enter page name");
            pageName.requestFocus();
            continueBtn.setEnabled(false);
            continueBtn.setAlpha(0.5f);
            return false;
        }
        continueBtn.setEnabled(true);
        continueBtn.setAlpha(1f);
        return true;
    }


    @NonNull
    private SpannableString getProfessionalDetail() {
        String fullText = "Use the name of your business,brand or organisation,or name that helps explain your Page. Learn more";
        SpannableString ss = new SpannableString(fullText);
        int start = fullText.indexOf("Learn more");
        int end = start + "Learn more".length();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                UtilMethods.INSTANCE.ProfessionalProfileBottomSheet(ProfessionalProfileActivity.this);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setFakeBoldText(true); // Bold
                ds.setColor(Color.parseColor("#0A66C2"));
                ds.setUnderlineText(false); // No underline
            }
        };

        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }
}