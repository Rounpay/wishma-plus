package com.infotech.wishmaplus.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.UtilMethods;

public class ProfessionalProfileActivity extends AppCompatActivity {

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
        TextView optionOneTitle = findViewById(R.id.optionOneTitle);
        SpannableString ss = getProfessionalDetail();
        optionOneTitle.setText(ss);
        optionOneTitle.setMovementMethod(LinkMovementMethod.getInstance());
        optionOneTitle.setHighlightColor(Color.TRANSPARENT);
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