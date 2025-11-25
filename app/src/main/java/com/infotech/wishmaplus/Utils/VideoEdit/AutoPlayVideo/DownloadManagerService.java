package com.infotech.wishmaplus.Utils.VideoEdit.AutoPlayVideo;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.infotech.wishmaplus.R;

/**
 * Created by Vishnu Agarwal on 10-10-2024.
 */

public class DownloadManagerService extends Service {

    /*private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            // Handle download completion as in the previous example
        }
    };*/

    @Override
    public void onCreate() {
        super.onCreate();
        // Register receiver dynamically
        try {
            ContextCompat.registerReceiver(this, new DownloadManagerReceiver(), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), ContextCompat.RECEIVER_EXPORTED);
            //registerReceiver(new DownloadManagerReceiver(), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start your service logic here
        //startForeground(1, createNotification());

        // Return START_STICKY or START_NOT_STICKY depending on your needs
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister the receiver when the service is destroyed
        try {
            unregisterReceiver(new DownloadManagerReceiver());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "download_channel")
                .setContentTitle("Download in progress")
                .setSmallIcon(R.drawable.app_logo)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        return builder.build();
    }
}
