package com.infotech.wishmaplus.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.R;

public class CreatePersonalProfileActivity extends AppCompatActivity {

    private AppCompatTextView tvLearnMore1, tvLearnMore2, tvLearnMore3,tvViewInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_persional_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainL), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.nextButton).setOnClickListener(view -> {
            Intent intent = new Intent(CreatePersonalProfileActivity.this, SettingUpYourPage.class);
            startActivity(intent);
        });

        findViewById(R.id.closeBtn).setOnClickListener(v -> finish());
        findViewById(R.id.cancel).setOnClickListener(v -> finish());
        tvLearnMore1 = findViewById(R.id.tvLearnMore1);
        tvLearnMore2 = findViewById(R.id.tvLearnMore2);
        tvLearnMore3 = findViewById(R.id.tvLearnMore3);
        tvViewInfo = findViewById(R.id.tvViewInfo);
        setAllClicks();
    }

    /**
     * Set Clickable Highlight + Open WebView
     */
    private void makeLearnMoreClickable(
            AppCompatTextView textView,
            String fullText,
            String clickablePart,
            String url) {

        SpannableString ss = new SpannableString(fullText);

        int start = fullText.indexOf(clickablePart);
        int end = start + clickablePart.length();

        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent i = new Intent(CreatePersonalProfileActivity.this, WebViewInformationActivity.class);
                i.putExtra("webViewUrl", url);
                startActivity(i);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.parseColor("#0066FF"));
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
    }

    private void setAllClicks() {


        makeLearnMoreClickable(
                tvLearnMore3,
                "Profile information is used to personalise your experience, recommendations and ads across your profiles and Meta Products.",
                "Meta Products.",
                "https://wishmaplus.com/about"
        );

        makeLearnMoreClickable(
                tvLearnMore2,
                "All of your profiles are under one Facebook account. Your main profile is the first profile created on Facebook. Learn more.",
                "Learn more.",
                "https://wishmaplus.com"
        );

        makeLearnMoreClickable(
                tvLearnMore1,
                "People may learn the profiles you create are connected based on what you say or do. Learn more.",
                "Learn more.",
                "https://wishmaplus.com/privacy"
        );

        makeLearnMoreClickable(
                tvViewInfo,
                "View Facebook's policies for additional profiles.",
                "Facebook's policies for additional profiles.",
                "https://wishmaplus.com/privacy"
        );

    }
}
