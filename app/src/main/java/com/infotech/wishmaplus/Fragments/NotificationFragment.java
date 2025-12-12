package com.infotech.wishmaplus.Fragments;

import android.app.Activity;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.infotech.wishmaplus.Adapter.NotificationAdapter;
import com.infotech.wishmaplus.Api.Response.NotificationModel;
import com.infotech.wishmaplus.Api.Response.NotificationResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private TextView tvTitle, tvMessage, tvTime, tvUrl;
    private ImageView ivNotificationImage;

    private String title, message, imageUrl, url, time, type;
    private int notificationId;
    BottomSheetDialog bottomSheetNotification;

    NotificationAdapter adapter;
    CustomLoader loader;
    RecyclerView rvNotifications;



    NotificationResponse notificationResponse = new NotificationResponse();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        loader = new CustomLoader(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//
//        // Initialize views
//        tvTitle = view.findViewById(R.id.tv_title);
//        tvMessage = view.findViewById(R.id.tv_message);
//        tvTime = view.findViewById(R.id.tv_time);
//        tvUrl = view.findViewById(R.id.tv_url);
//        ivNotificationImage = view.findViewById(R.id.iv_notification_image);
//
//        // Get data from arguments
//        if (getArguments() != null) {
//            title = getArguments().getString("Title", "");
//            message = getArguments().getString("Message", "");
//            imageUrl = getArguments().getString("Image", "");
//            url = getArguments().getString("Url", "");
//            time = getArguments().getString("Time", "");
//            type = getArguments().getString("Type", "");
//            notificationId = getArguments().getInt("NotificationId", -1);
//            // Display notification data
//            displayNotificationData();
//        }
//        List<NotificationModel> notifications = new ArrayList<>();
//
//        notifications.add(new NotificationModel(
//                "https://example.com/profile1.jpg",
//                "Rukminee Sharma sent you a friend request.",
//                "6 hours ago",
//                true
//        ));
//
//        notifications.add(new NotificationModel(
//                "",
//                "You have a new friend suggestion.",
//                "3d",
//                false
//        ));



        rvNotifications = view.findViewById(R.id.rvNotifications);
        getNotification();

        return view;
    }
    public void openGoalBottomSheetDialog(Activity context) {

        if (bottomSheetNotification != null && bottomSheetNotification.isShowing())
            return;

        bottomSheetNotification = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_notification, null);




        bottomSheetNotification.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomSheetNotification.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetNotification.show();
    }

    /**
     * Display notification data in UI
     */

    public void  getNotification(){
        loader.show();
        UtilMethods.INSTANCE.getNotifications(new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null && loader.isShowing()) loader.dismiss();
                notificationResponse = (NotificationResponse) object;
                if(notificationResponse.getStatusCode()==1){
                    adapter = new NotificationAdapter(
                            requireContext(),
                            notificationResponse.getResult(),
                            new NotificationAdapter.OnNotificationClick() {
                                @Override
                                public void onConfirm(int position) {
                                    Toast.makeText(requireActivity(), "Confirmed", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onDelete(int position) {
                                    Toast.makeText(requireActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onMore(int position) {
                                    openGoalBottomSheetDialog(requireActivity());
                                }
                            }
                    );
                    rvNotifications.setAdapter(adapter);
                }

            }

            @Override
            public void onError(String msg) {
                if (loader != null && loader.isShowing()) loader.dismiss();


            }
        });


    }
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