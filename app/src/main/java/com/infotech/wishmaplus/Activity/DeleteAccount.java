package com.infotech.wishmaplus.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.Utility;

public class DeleteAccount extends AppCompatActivity {
    private PreferencesManager tokenManager;
    private UserDetailResponse userDetailResponse;

    ImageView profileIcon;
    TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvName = findViewById(R.id.tvName);
        profileIcon = findViewById(R.id.imgProfile1);
        tokenManager = new PreferencesManager(this,1);
        getUserDetail();
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        findViewById(R.id.cardAccount1).setOnClickListener(view -> {
            startActivity(new Intent(this, DeactivateOrDeleteAccount.class));
        });
    }
    private void getUserDetail() {
        UtilMethods.INSTANCE.userDetail(this,"0", null, tokenManager, object -> {
            userDetailResponse = (UserDetailResponse) object;
            setUserData();

        });
    }
    private void setUserData() {
        tvName.setText(userDetailResponse.getFisrtName()+" "+userDetailResponse.getLastName());

        Glide.with(this)
                .load(userDetailResponse.getProfilePictureUrl())
                .apply(UtilMethods.INSTANCE.getRequestOption_With_UserIcon())
                .into(profileIcon);
    }
}