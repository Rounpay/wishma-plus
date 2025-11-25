package com.infotech.wishmaplus.Notification;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.infotech.wishmaplus.R;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.notificationAX), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new Handler(Looper.getMainLooper()).post(() -> {

            String title = getIntent().getStringExtra("Title");
            String msg = getIntent().getStringExtra("Message");
            String imageUrl = getIntent().getStringExtra("Image");
            final String url = getIntent().getStringExtra("Url");
            final String time = getIntent().getStringExtra("Time");
            TextView titleTv = findViewById(R.id.title);
            TextView msgTv = findViewById(R.id.message);
            final ImageView imageIv = findViewById(R.id.banner);
            TextView detailBtn = findViewById(R.id.detailBtn);
            TextView timeTv = findViewById(R.id.time);
            if (time != null && !time.isEmpty()) {
                timeTv.setText(time + "");
            } else {
                findViewById(R.id.timeView).setVisibility(View.GONE);
            }
            titleTv.setText(title);
            msgTv.setText(msg);
            if (imageUrl != null && !imageUrl.isEmpty() && URLUtil.isValidUrl(imageUrl)) {
                imageIv.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(imageUrl)
                        .apply(RequestOptions.placeholderOf(R.drawable.app_logo))
                        .into(imageIv);
            } else {
                imageIv.setVisibility(View.GONE);
            }
            if (url != null && !url.isEmpty() && URLUtil.isValidUrl(url)) {
                detailBtn.setVisibility(View.VISIBLE);
                detailBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openBrowser(url);
                    }
                });
            } else {
                detailBtn.setVisibility(View.GONE);
            }

        });
    }

    void openBrowser(String url) {
        url = url.replaceAll(" ", "");
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException anfe) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch (ActivityNotFoundException anfe2) {
                anfe2.printStackTrace();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();

        }
        return super.onOptionsItemSelected(item);
    }
}
