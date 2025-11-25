package com.infotech.wishmaplus.Utils.VideoEdit.AutoPlayVideo;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.infotech.wishmaplus.Utils.UtilMethods;

/**
 * Created by Vishnu Agarwal on 10-10-2024.
 */

public class DownloadManagerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the download ID from the broadcast
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

        // Check if the download completed matches the started download ID
        //if (UtilMethods.INSTANCE.downloadIdMap.containsKey(id)) {
            // Download completed, query the download manager
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(id);
            if(UtilMethods.INSTANCE.downloadManager!=null) {
                Cursor cursor = UtilMethods.INSTANCE.downloadManager.query(query);

                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                        // Download was successful, retrieve the file URI
                        int localUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                        int webUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_URI);
                        if (localUriIndex != -1) {
                            String localUri = cursor.getString(localUriIndex);
                            String webUri = cursor.getString(webUriIndex);
                           // Log.e("URI",localUri+" - "+webUri);
                           /* File downloadedFile = new File(localUri);
                            Log.e("WebUri",webUri);
                            Log.e("LocalURi",localUri);
                            Log.e("Path 1",downloadedFile.toString());
                            Log.e("Path 2",downloadedFile.getAbsolutePath());
                            Log.e("Path 3",downloadedFile.getPath());
                            Log.e("Path 4",downloadedFile.getAbsoluteFile().getPath());*/

                            VideoUtils.saveString(context, webUri, localUri.replace("file://",""));
                            // Use the file URI as needed
                          //  Toast.makeText(context, "Download successful! File saved at: " + downloadedFile.toString(), Toast.LENGTH_LONG).show();
                        } else {
                            // Handle missing local URI column
                           // Toast.makeText(context, "Download completed, but no local URI available.", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                cursor.close();
            }
        //}
    }

}
