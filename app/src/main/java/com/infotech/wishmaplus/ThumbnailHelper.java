package com.infotech.wishmaplus;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ThumbnailHelper
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * PROBLEM:
 *   ReelEditorActivity ko video ka thumbnail chahiye clipStrip mein dikhane ke liye.
 *   Glide video se thumbnail bana sakta hai, lekin kabhi kabhi content URI
 *   ke saath kaam nahi karta.
 *   Isliye yeh helper MediaMetadataRetriever se khud frame extract karta hai.
 *
 * KAISE KAAM KARTA HAI:
 *   1. Background thread pe MediaMetadataRetriever se video ka pehla frame lao
 *   2. Main thread pe ImageView mein set karo
 *
 * USAGE:
 *   // Simple — seedha ImageView mein load karo
 *   ThumbnailHelper.load(context, videoUriString, imageView);
 *
 *   // Callback chahiye to
 *   ThumbnailHelper.extract(context, videoUriString, bitmap -> {
 *       // bitmap ready hai, jo chahiye karo
 *   });
 */
public class ThumbnailHelper {

    // ─── Callback Interface ───────────────────────────────────────────────────
    public interface OnThumbnailReady {
        void onReady(Bitmap bitmap); // null agar extract fail ho
    }

    private static final ExecutorService executor =
            Executors.newFixedThreadPool(2);
    private static final Handler mainHandler =
            new Handler(Looper.getMainLooper());

    // ─────────────────────────────────────────────────────────────────────────
    // METHOD 1: Seedha ImageView mein load karo (sabse easy)
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * @param context    Activity ya Application context
     * @param videoUri   video path ya content URI string
     *                   (jaise "content://media/external/video/media/123")
     * @param imageView  jis ImageView mein thumbnail dikhana hai
     */
    public static void load(Context context, String videoUri, ImageView imageView) {
        extract(context, videoUri, bitmap -> {
            if (bitmap != null && !isDestroyed(context)) {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // METHOD 2: Bitmap callback (jab khud handle karna ho)
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * @param context    Activity ya Application context
     * @param videoUri   video path ya content URI string
     * @param callback   OnThumbnailReady — main thread pe call hoga
     */
    public static void extract(Context context, String videoUri,
                               OnThumbnailReady callback) {
        if (videoUri == null || videoUri.isEmpty()) {
            callback.onReady(null);
            return;
        }

        executor.execute(() -> {
            Bitmap bitmap = null;
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();

            try {
                Uri uri = Uri.parse(videoUri);

                // Content URI aur File URI dono handle karo
                if ("content".equals(uri.getScheme())) {
                    retriever.setDataSource(context, uri);
                } else {
                    retriever.setDataSource(videoUri);
                }

                // Time = 0 microseconds = pehla frame
                // OPTION_CLOSEST_SYNC = nearest keyframe (fast)
                bitmap = retriever.getFrameAtTime(
                        0,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

                // Agar pehla frame null aaya toh 1 second try karo
                if (bitmap == null) {
                    bitmap = retriever.getFrameAtTime(
                            1_000_000L, // 1 second = 1,000,000 microseconds
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                }

            } catch (Exception e) {
                e.printStackTrace();
                // bitmap null rahega — caller null check karega
            } finally {
                try { retriever.release(); } catch (Exception ignored) {}
            }

            // Main thread pe callback
            final Bitmap finalBitmap = bitmap;
            mainHandler.post(() -> callback.onReady(finalBitmap));
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPER: activity destroy ?
    // ─────────────────────────────────────────────────────────────────────────
    private static boolean isDestroyed(Context context) {
        if (context instanceof android.app.Activity activity) {
            return activity.isDestroyed() || activity.isFinishing();
        }
        return false;
    }
}