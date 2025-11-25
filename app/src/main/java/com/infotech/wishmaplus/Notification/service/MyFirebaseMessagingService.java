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
import com.infotech.wishmaplus.Notification.NotificationActivity;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApplicationConstant;
import com.infotech.wishmaplus.Utils.PreferencesManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private Bitmap bitmap;
    private String image;


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        PreferencesManager mAppPreferences = new PreferencesManager(this,2);
        mAppPreferences.setNonRemoval(ApplicationConstant.INSTANCE.regFCMKeyPref, s);
        //LoginResponse mLoginDataResponse = ApiFintechUtilMethods.INSTANCE.getLoginResponse(mAppPreferences);
        if(!mAppPreferences.getString(mAppPreferences.LoginPref).isEmpty()){
            //ApiFintechUtilMethods.INSTANCE.updateFcm(this, mLoginDataResponse, mAppPreferences);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {


        }


        //message will contain the Push Message
        final String message = remoteMessage.getData().get("Message");
        image = remoteMessage.getData().get("Image");
        if (image != null && !image.isEmpty()) {
            image = ApplicationConstant.INSTANCE.baseUrl + "/Image/Notification/" + image;
        }
        final String url = remoteMessage.getData().get("Url");
        final String title = remoteMessage.getData().get("Title");
        final String key = remoteMessage.getData().get("Key");
        final String postDate = remoteMessage.getData().get("PostDate");
        final String type = remoteMessage.getData().get("Type");
        /*final String sessionId = remoteMessage.getData().get("SessionId");
        final String isValidate = remoteMessage.getData().get("IsValidate");
        final String userId = remoteMessage.getData().get("UserId");*/
        if (type.equalsIgnoreCase("order_key")) {
            final String orderkey = remoteMessage.getData().get("orderkey");
            sendUPIOrderNotificationBrodcast(orderkey);

        } else {
            int notification_id = 1;
            try {
                notification_id = Integer.parseInt(key);
            } catch (NumberFormatException nfe) {
                notification_id = 1;
            }


            // if (type != null && !type.isEmpty() && type.equalsIgnoreCase("Browsable_Notification")) {
            sendNewNotificationBrodcast();
            bitmap = getBitmapfromUrl(image);

            if (bitmap != null) {
                // showNotification(message, bitmap, url, title, key, postDate, type, notification_id);
                showNotification(message, image, type, postDate, bitmap, url, title, notification_id);
            } else {

                final int finalNotification_id = notification_id;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        new generatePictureStyleNotification(message, image, url, title, key, postDate, type, finalNotification_id).execute();

                    }
                });
            }
            // }

        }
    }


    /**
     * Showing notification with text only
     */

    private void showNotification(String messageBody, String imageUrl, String type, String postDate, Bitmap image, String url, String contentTitle, int notification_id) {
        String CHANNEL_ID = getPackageName();

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Title", contentTitle);
        intent.putExtra("Message", messageBody);
        intent.putExtra("Image", imageUrl);
        intent.putExtra("Url", url);
        intent.putExtra("Time", postDate);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            pendingIntent = PendingIntent.getActivity(this, notification_id + 2 /* Request code */,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        }else {
            pendingIntent = PendingIntent.getActivity(this, notification_id + 2 /* Request code */,
                    intent,  PendingIntent.FLAG_IMMUTABLE);
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(contentTitle)
                .setAutoCancel(true)
                .setContentText(messageBody)
                .setTicker(messageBody)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setDefaults(Notification.DEFAULT_SOUND)
                .setGroup(this.getPackageName() + "." + type)
                .setChannelId(CHANNEL_ID)
                .setGroupSummary(true)
                .setSilent(false)
                .setContentIntent(pendingIntent);
        if (image != null) {
            notification.setLargeIcon(image);
            notification.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(image)
                    .setSummaryText(messageBody)
                    .setBigContentTitle(contentTitle));
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, getPackageName() + " BBPS_Service", importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(notification_id + 2, notification.build());
    }

    /*private void showNotification(String messageBody, String imageUrl, String type, String postDate, Bitmap image, String url, String contentTitle, int notification_id) {
        String CHANNEL_ID = getPackageName();

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Title", contentTitle);
        intent.putExtra("Message", messageBody);
        intent.putExtra("Image", imageUrl);
        intent.putExtra("Url", url);
        intent.putExtra("Time", postDate);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notification_id + 2 */
    /* Request code */
    /*, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(contentTitle)
                .setAutoCancel(true)
                .setContentText(messageBody)
                .setTicker(messageBody)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setDefaults(Notification.DEFAULT_SOUND)
                .setGroup(this.getPackageName() + "." + type)
                .setChannelId(CHANNEL_ID)
                .setGroupSummary(true)
                .setContentIntent(pendingIntent);
        if (image != null) {
            notification.setLargeIcon(image);
            notification.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(image)
                    .setSummaryText(messageBody)
                    .setBigContentTitle(contentTitle));
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, getPackageName() + " Service", importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(notification_id + 2, notification.build());
    }
*/

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }

    private void sendNewNotificationBrodcast() {
        Intent intent = new Intent("New_Notification_Detect");
        // You can also include some extra data.
        intent.putExtra("message", "New Notification");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendUPIOrderNotificationBrodcast(String orderkey) {
        Intent intent = new Intent("New_UPI_Order_Notification_Detect");
        intent.putExtra("ORDER_KEY", orderkey);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /*get bitmap image from the URL received in background*/
    public class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {


        private String url, message, image, title, key, postDate, type;
        private int notification_id = 1;

        public generatePictureStyleNotification(String message, String image, String url, String title, String key, String postDate, String type, int notification_id) {
            super();
            this.url = url;
            this.notification_id = notification_id;
            this.message = message;
            this.image = image;
            this.title = title;
            this.key = key;
            this.postDate = postDate;
            this.type = type;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = getBitmapfromUrl(this.image);
            if (bitmap != null) {
                return bitmap;
            } else {
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            //showNotification(message, result, url, title, key, postDate, type, notification_id);
            showNotification(message, image, type, postDate, result, url, title, notification_id);
        }
    }
}
