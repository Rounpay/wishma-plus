package com.infotech.wishmaplus.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.UtilMethods;

public class SwitchPages extends AppCompatActivity {
    ImageView imageView;
    AppCompatTextView pageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_pages);
        EdgeToEdge.enable(this);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imageView = findViewById(R.id.profileImage);
        pageName = findViewById(R.id.tvUserName);

        // Receive data
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String name = getIntent().getStringExtra("pageName");

        // Set the name
        pageName.setText(name);

        // Load the image (Glide recommended)
        Glide.with(this)
                .load(imageUrl)
                .apply(UtilMethods.INSTANCE.getRequestOption_With_UserIcon())
                .into(imageView);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            Intent intent = new Intent(SwitchPages.this, MainActivity.class);

            // This clears ALL previous activities and makes MainActivity the root
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            finish(); // Close SwitchPages


        }, 1000); // 1000 ms = 1 second

    }
}