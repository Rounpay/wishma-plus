package com.infotech.wishmaplus.Notification.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.infotech.wishmaplus.Activity.MainActivity;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApplicationConstant;
import com.infotech.wishmaplus.Utils.PreferencesManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final String CHANNEL_ID = "wishmaplus_notification_channel";
    private static final String CHANNEL_NAME = "WishmaPlus Notifications";

    private Bitmap bitmap;
    private String image;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "FCM Token: " + token);
        PreferencesManager mAppPreferences = new PreferencesManager(this, 2);
        mAppPreferences.setNonRemoval(ApplicationConstant.INSTANCE.regFCMKeyPref, token);

        // Update token to server if user is logged in
        if (!mAppPreferences.getString(mAppPreferences.LoginPref).isEmpty()) {
            // Update FCM token to your server
            // ApiFintechUtilMethods.INSTANCE.updateFcm(this, mLoginDataResponse, mAppPreferences);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check for both data payload and notification payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            handleDataPayload(remoteMessage);
        }

        // Handle notification payload (sent from Firebase Console or Google APIs)
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            // IMPORTANT: When notification payload is present and app is in background/killed,
            // FCM automatically displays notification and custom PendingIntent is ignored.
            // We need to manually show notification with custom intent.
            handleNotificationPayload(remoteMessage);
        }

        // If no notification or data, just log
        if (remoteMessage.getData().size() == 0 && remoteMessage.getNotification() == null) {
            Log.d(TAG, "Empty notification received");
        }
    }

    /**
     * Handle data payload from Firebase or custom API
     */
    private void handleDataPayload(RemoteMessage remoteMessage) {
        try {
            // Extract data from payload
            String message = remoteMessage.getData().get("Message");
            String imageUrl = remoteMessage.getData().get("Image");
            String url = remoteMessage.getData().get("Url");
            String title = remoteMessage.getData().get("Title");
            String key = remoteMessage.getData().get("Key");
            String postDate = remoteMessage.getData().get("PostDate");
            String type = remoteMessage.getData().get("Type");

            // Handle image URL
            if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.startsWith("http")) {
                imageUrl = ApplicationConstant.INSTANCE.baseUrl + "/Image/Notification/" + imageUrl;
            }

            // Handle special notification types
            if (type != null && type.equalsIgnoreCase("order_key")) {
                String orderkey = remoteMessage.getData().get("orderkey");
                sendUPIOrderNotificationBroadcast(orderkey);
                return;
            }

            // Generate notification ID
            int notificationId = generateNotificationId(key);

            // Send broadcast for new notification
            sendNewNotificationBroadcast();

            // Load image and show notification
            loadImageAndShowNotification(message, imageUrl, url, title, key, postDate, type, notificationId);

        } catch (Exception e) {
            Log.e(TAG, "Error handling data payload: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle notification payload (from Firebase Console/Google APIs)
     */
    private void handleNotificationPayload(RemoteMessage remoteMessage) {
        try {
            Map<String, String> data = remoteMessage.getData();

            // --- Title ---
            String title = null;
            if (remoteMessage.getNotification() != null &&
                    remoteMessage.getNotification().getTitle() != null &&
                    !remoteMessage.getNotification().getTitle().isEmpty()) {
                title = remoteMessage.getNotification().getTitle();
            } else {
                title = getValueIgnoreCase(data, "Title","title");
            }

            // --- Message ---
            String message = null;
            if (remoteMessage.getNotification() != null &&
                    remoteMessage.getNotification().getBody() != null &&
                    !remoteMessage.getNotification().getBody().isEmpty()) {
                message = remoteMessage.getNotification().getBody();
            } else {
                message = getValueIgnoreCase(data, "Message", "Body","body","message");
            }

            // --- Image ---
            String image = null;
            if (remoteMessage.getNotification() != null &&
                    remoteMessage.getNotification().getImageUrl() != null) {
                image = remoteMessage.getNotification().getImageUrl().toString();
            } else {
                image = getValueIgnoreCase(data, "Image", "imageUrl", "image");
                if (image != null && !image.isEmpty()) {
                    image = ApplicationConstant.INSTANCE.baseUrl + "/Image/Notification/" + image;
                }
            }

            // --- Other fields ---
            String url = getValueIgnoreCase(data, "Url", "url", "imageUrl");
            String key = getValueIgnoreCase(data, "Key", "key");
            String postDate = getValueIgnoreCase(data, "PostDate", "postDate");
            String type = getValueIgnoreCase(data, "Type", "type");

            int notificationId = generateNotificationId(key);

            // Send broadcast for new notification
            sendNewNotificationBroadcast();

            // Load image and show notification
            loadImageAndShowNotification(message, image, url, title, key, postDate, type, notificationId);

        } catch (Exception e) {
            Log.e(TAG, "Error handling notification payload: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getValueIgnoreCase(Map<String, String> map, String... possibleKeys) {
        for (String key : possibleKeys) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(key)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Load image asynchronously and show notification
     */
    private void loadImageAndShowNotification(String message, String imageUrl, String url,
                                              String title, String key, String postDate,
                                              String type, int notificationId) {

        // Try to load image synchronously first (for quick display)
        bitmap = getBitmapFromUrl(imageUrl);

        if (bitmap != null) {
            // Show notification immediately with image
            showNotification(message, imageUrl, type, postDate, bitmap, url, title, notificationId);
        } else if (imageUrl != null && !imageUrl.isEmpty()) {
            // Load image asynchronously if not loaded
            new Handler(Looper.getMainLooper()).post(() ->
                    new GeneratePictureStyleNotification(message, imageUrl, url, title, key,
                            postDate, type, notificationId).execute()
            );
        } else {
            // Show notification without image
            showNotification(message, imageUrl, type, postDate, null, url, title, notificationId);
        }
    }

    /**
     * Generate notification ID from key
     */
    private int generateNotificationId(String key) {
        int notificationId = 1;
        try {
            if (key != null && !key.isEmpty()) {
                notificationId = Integer.parseInt(key);
            }
        } catch (NumberFormatException e) {
            notificationId = (int) System.currentTimeMillis();
        }
        return notificationId;
    }

    /**
     * Show notification with optional image
     */
    private void showNotification(String messageBody, String imageUrl, String type,
                                  String postDate, Bitmap image, String url,
                                  String contentTitle, int notificationId) {

        // Create notification channel for Android O and above
        createNotificationChannel();

        // Create intent to open NotificationFragment
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction("OPEN_NOTIFICATION_FRAGMENT");

        // Add all notification data as extras
        intent.putExtra("Title", contentTitle != null ? contentTitle : "");
        intent.putExtra("Message", messageBody != null ? messageBody : "");
        intent.putExtra("Image", imageUrl != null ? imageUrl : "");
        intent.putExtra("Url", url != null ? url : "");
        intent.putExtra("Time", postDate != null ? postDate : "");
        intent.putExtra("Type", type != null ? type : "");
        intent.putExtra("NotificationId", notificationId);

        // Create pending intent with unique request code
        PendingIntent pendingIntent;
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        pendingIntent = PendingIntent.getActivity(this, notificationId, intent, flags);

        // Default notification sound
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Build notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(contentTitle != null ? contentTitle : "New Notification")
                .setContentText(messageBody != null ? messageBody : "")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        // Add large icon and big picture style if image is available
        if (image != null) {
            notificationBuilder.setLargeIcon(image);
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(image)
                    .bigLargeIcon(image)
                    .setSummaryText(messageBody)
                    .setBigContentTitle(contentTitle));
        } else {
            // Use big text style if no image
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(messageBody));
        }

        // Show notification
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }

    /**
     * Create notification channel for Android O and above
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Receive important notifications from WishmaPlus");
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
            channel.setShowBadge(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Download bitmap from URL
     */
    public Bitmap getBitmapFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.connect();

            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            input.close();
            connection.disconnect();

            return bitmap;
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Send broadcast for new notification
     */
    private void sendNewNotificationBroadcast() {
        Intent intent = new Intent("New_Notification_Detect");
        intent.putExtra("message", "New Notification");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Send broadcast for UPI order notification
     */
    private void sendUPIOrderNotificationBroadcast(String orderkey) {
        Intent intent = new Intent("New_UPI_Order_Notification_Detect");
        intent.putExtra("ORDER_KEY", orderkey);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * AsyncTask to load image in background
     */
    public class GeneratePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private String url, message, image, title, key, postDate, type;
        private int notificationId;

        public GeneratePictureStyleNotification(String message, String image, String url,
                                                String title, String key, String postDate,
                                                String type, int notificationId) {
            this.url = url;
            this.notificationId = notificationId;
            this.message = message;
            this.image = image;
            this.title = title;
            this.key = key;
            this.postDate = postDate;
            this.type = type;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return getBitmapFromUrl(this.image);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            showNotification(message, image, type, postDate, result, url, title, notificationId);
        }
    }
}