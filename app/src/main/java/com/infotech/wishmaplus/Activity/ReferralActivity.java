package com.infotech.wishmaplus.Activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApplicationConstant;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.Utility;

/**
 * Created by Vishnu Agarwal on 07,November,2024
 */
public class ReferralActivity extends AppCompatActivity {

    private PreferencesManager tokenManager;
    private CustomLoader loader;
    UserDetailResponse userDetailResponse;
    TextView referralCode, referralNoteTv;
    private String referralMsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_referral);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.referView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tokenManager = new PreferencesManager(this, 1);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        referralCode = findViewById(R.id.referralCode);
        referralNoteTv = findViewById(R.id.referralNoteTv);
        userDetailResponse = getIntent().getParcelableExtra("userData");
        if (userDetailResponse == null) {
            userDetailResponse = UtilMethods.INSTANCE.getUserDetailResponse(tokenManager);
            if (userDetailResponse == null) {
                getUserDetail();
            } else {
                setUiData();
            }
        } else {
            setUiData();
        }


        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        findViewById(R.id.whatsAppBtn).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://api.whatsapp.com/send?text=" + referralMsg));
            startActivity(intent);
        });

        findViewById(R.id.shareBtn).setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Share Link");
            i.putExtra(Intent.EXTRA_TEXT, referralMsg);
            startActivity(Intent.createChooser(i, "Share Link"));
        });
    }

    private void getUserDetail() {
        UtilMethods.INSTANCE.userDetail(this,"0", loader, tokenManager, object -> {
            userDetailResponse = (UserDetailResponse) object;
            setUiData();
        });
    }

    void setUiData() {
        referralCode.setText(userDetailResponse.getReferralIdStr());
        referralNoteTv.setText("Send referral link to invite your friends and you can earn upto " + Utility.INSTANCE.formattedAmountWithRupees(userDetailResponse.getReferralAmount()) + ".");
        referralMsg = "Hey! Check out " + getString(R.string.app_name) + " - a social media earning app. Use my code " + userDetailResponse.getReferralIdStr() + " to sign up and earn upto " + Utility.INSTANCE.formattedAmountWithRupees(userDetailResponse.getReferralAmount()) + "\nDownload now : " + userDetailResponse.getReferralUrl();

    }
}
