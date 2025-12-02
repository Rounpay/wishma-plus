package com.infotech.wishmaplus.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.R;

public class NotificationFragment extends Fragment {

    private TextView tvTitle, tvMessage, tvTime, tvUrl;
    private ImageView ivNotificationImage;

    private String title, message, imageUrl, url, time, type;
    private int notificationId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // Initialize views
        tvTitle = view.findViewById(R.id.tv_title);
        tvMessage = view.findViewById(R.id.tv_message);
        tvTime = view.findViewById(R.id.tv_time);
        tvUrl = view.findViewById(R.id.tv_url);
        ivNotificationImage = view.findViewById(R.id.iv_notification_image);

        // Get data from arguments
        if (getArguments() != null) {
            title = getArguments().getString("Title", "");
            message = getArguments().getString("Message", "");
            imageUrl = getArguments().getString("Image", "");
            url = getArguments().getString("Url", "");
            time = getArguments().getString("Time", "");
            type = getArguments().getString("Type", "");
            notificationId = getArguments().getInt("NotificationId", -1);
            // Display notification data
            displayNotificationData();
        }

        return view;
    }

    /**
     * Display notification data in UI
     */
    private void displayNotificationData() {
        // Set title
        if (title != null && !title.isEmpty()) {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.GONE);
        }

        // Set message
        if (message != null && !message.isEmpty()) {
            tvMessage.setText(message);
            tvMessage.setVisibility(View.VISIBLE);
        } else {
            tvMessage.setVisibility(View.GONE);
        }

        // Set time
        if (time != null && !time.isEmpty()) {
            tvTime.setText(time);
            tvTime.setVisibility(View.VISIBLE);
        } else {
            tvTime.setVisibility(View.GONE);
        }

        // Set URL
        if (url != null && !url.isEmpty()) {
            tvUrl.setText(url);
            tvUrl.setVisibility(View.VISIBLE);

            // Make URL clickable
            tvUrl.setOnClickListener(v -> {
                // Open URL in browser or webview
                Toast.makeText(getContext(), "Opening: " + url, Toast.LENGTH_SHORT).show();
                // Implement URL opening logic here
            });
        } else {
            tvUrl.setVisibility(View.GONE);
        }

        // Load image using Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            ivNotificationImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.user_icon)
                    .error(R.drawable.user_icon)
                    .into(ivNotificationImage);
        } else {
            ivNotificationImage.setVisibility(View.GONE);
        }
    }
}