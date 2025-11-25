package com.infotech.wishmaplus.Utils.VideoEdit.AutoPlayVideo;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.webkit.URLUtil;

import com.infotech.wishmaplus.Utils.UtilMethods;

import java.io.File;

public class VideoUtils {

    private static SharedPreferences sharedPrefs;

    protected static void initialize(Context context) {
        sharedPrefs = context.getSharedPreferences(
                "VIDEO_STORAGE", Context.MODE_PRIVATE);
    }

    public static String getString(Context context, String key) {
        if (sharedPrefs == null) {
            initialize(context);
        }
        return sharedPrefs.getString(key, null);
    }

    public static void saveString(Context context, String key, String value) {
        if (sharedPrefs == null) {
            initialize(context);
        }
        sharedPrefs.edit().putString(key, value).apply();
    }

    protected static void remove(Context context, String key) {
        if (sharedPrefs == null) {
            initialize(context);
        }
        sharedPrefs.edit().remove(key).apply();
    }

    public static boolean isVideoDownloaded(Context c, String url) {
        return getString(c, url) != null;

    }

    public static boolean isConnected(Context c) {
        NetworkInfo info = ((ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public void startDownloadInBackground(Activity activity,String url,String downloadPath) {
        if (!isConnected(activity)) return;
        /* Starting Download Service */
        String saveFilePath =getString(activity, url);
        if ((saveFilePath == null || !(new File(saveFilePath).exists())) && url != null && URLUtil.isValidUrl(url)) {
           /* Intent serviceIntent = new Intent(activity, DownloadService.class);
            activity.startService(serviceIntent);*/
            try {
                if(UtilMethods.INSTANCE.downloadManager==null) {
                    UtilMethods.INSTANCE.downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                }
                Uri uri = Uri.parse(url);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                //File rootDir = new File(downloadPath);
                  /* if (!rootDir.exists()) {
                       rootDir.mkdirs();
                   }*/
                // Log.e("URI End",uri.getLastPathSegment());

                File rootFile = new File(new File(downloadPath), uri.getLastPathSegment());
                   /*if (!rootFile.exists()) {
                       rootFile.createNewFile();
                   }*/
                request.setDestinationUri(Uri.fromFile(rootFile));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                /*long reference =*/ UtilMethods.INSTANCE.downloadManager.enqueue(request);
               // UtilMethods.INSTANCE.downloadIdMap.put(reference,url);
            }catch (Exception e){
                Intent intent = new Intent(Intent.ACTION_SYNC, null, activity, DownloadService.class);
                intent.putExtra("url", url);
                intent.putExtra("path", downloadPath);
                intent.putExtra("requestId", 101);
                activity.startService(intent);
            }
            /*Intent intent = new Intent(Intent.ACTION_SYNC, null, _act, DownloadService.class);
            intent.putExtra("url", url);
            intent.putExtra("path", downloadPath);
            intent.putExtra("requestId", 101);
            _act.startService(intent);*/


        }
    }

}
