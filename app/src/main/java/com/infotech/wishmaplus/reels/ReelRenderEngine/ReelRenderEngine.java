package com.infotech.wishmaplus.reels.ReelRenderEngine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.arthenica.mobileffmpeg.LogMessage;
import com.arthenica.mobileffmpeg.Statistics;
import com.infotech.wishmaplus.Utils.VideoEdit.CallBackOfQuery;
import com.infotech.wishmaplus.Utils.VideoEdit.interfaces.FFmpegCallBack;

import java.io.File;
import java.io.FileOutputStream;

public class ReelRenderEngine {

    public interface RenderCallback {
        void onProgress(int percent, String message);
        void onComplete(String outputPath);
        void onError(String error);
    }

    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private CallBackOfQuery callBackOfQuery = new CallBackOfQuery();

    public ReelRenderEngine(Context context) {
        this.context = context;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MAIN RENDER METHOD
    // ─────────────────────────────────────────────────────────────────────────
    public void render(
            String mediaPath,
            boolean isImage,
            View overlayView,
            String musicPath,
            long musicStartMs,
            long musicEndMs,
            int durationSec,
            RenderCallback callback
    ) {
        callback.onProgress(5, "Capturing overlays...");
        Bitmap overlayBmp = captureView(overlayView);

        if (isImage) {
            // ── Image path ────────────────────────────────────────────────
            callback.onProgress(10, "Preparing image...");
            String mergedImagePath = mergeOverlayOnImage(mediaPath, overlayBmp);
            if (mergedImagePath == null) {
                callback.onError("Image prepare failed");
                return;
            }

            callback.onProgress(20, "Converting image to video...");
            String outVideoPath = new File(context.getCacheDir(),
                    "img_video_" + System.currentTimeMillis() + ".mp4").getAbsolutePath();

            String[] imgCmd = {
                    "-loop", "1",
                    "-i", mergedImagePath,
                    "-c:v", "libx264",
                    "-t", String.valueOf(durationSec),
                    "-pix_fmt", "yuv420p",
                    "-vf", "scale=1080:1920:force_original_aspect_ratio=decrease," +
                    "pad=1080:1920:(ow-iw)/2:(oh-ih)/2",
                    "-r", "30",
                    "-y", outVideoPath
            };

            callBackOfQuery.callQuery(imgCmd, new FFmpegCallBack() {
                @Override
                public void process(LogMessage logMessage) {
                    // log
                }

                @Override
                public void statisticsProcess(Statistics statistics) {
                    // image to video = ~30% to 60%
                    int pct = 20 + (int) Math.min(40, statistics.getTime() / (durationSec * 10f));
                    mainHandler.post(() -> callback.onProgress(pct, "Converting image..."));
                }

                @Override
                public void success() {
                    // Aage music mix ya direct complete
                    if (musicPath != null && !musicPath.isEmpty()) {
                        mixAudio(outVideoPath, musicPath, musicStartMs, musicEndMs, callback);
                    } else {
                        mainHandler.post(() -> {
                            callback.onProgress(100, "Done!");
                            callback.onComplete(outVideoPath);
                        });
                    }
                }

                @Override
                public void cancel() {
                    mainHandler.post(() -> callback.onError("Cancelled"));
                }

                @Override
                public void failed() {
                    mainHandler.post(() -> callback.onError("Image to video failed"));
                }
            });

        } else {
            // ── Video path ────────────────────────────────────────────────
            callback.onProgress(10, "Burning stickers on video...");
            burnOverlayOnVideo(mediaPath, overlayBmp, callback, musicPath, musicStartMs, musicEndMs);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 1A — Image + Overlay bitmap merge (Java Canvas — no FFmpeg needed)
    // ─────────────────────────────────────────────────────────────────────────
    private String mergeOverlayOnImage(String imagePath, Bitmap overlay) {
        try {
            Bitmap base = android.graphics.BitmapFactory.decodeFile(imagePath);
            if (base == null) return null;

            Bitmap merged = Bitmap.createBitmap(
                    base.getWidth(), base.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(merged);
            canvas.drawBitmap(base, 0, 0, null);

            if (overlay != null) {
                android.graphics.Matrix matrix = new android.graphics.Matrix();
                matrix.setScale(
                        (float) base.getWidth() / overlay.getWidth(),
                        (float) base.getHeight() / overlay.getHeight()
                );
                canvas.drawBitmap(overlay, matrix, null);
            }

            File out = new File(context.getCacheDir(),
                    "merged_img_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(out);
            merged.compress(Bitmap.CompressFormat.JPEG, 95, fos);
            fos.close();
            return out.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 1B — Video pe overlay PNG burn karo (FFmpeg overlay filter)
    // ─────────────────────────────────────────────────────────────────────────
    private void burnOverlayOnVideo(
            String videoPath,
            Bitmap overlay,
            RenderCallback callback,
            String musicPath,
            long musicStartMs,
            long musicEndMs
    ) {
        // Overlay null hai ya blank — skip burning, seedha music pe jao
        if (overlay == null) {
            if (musicPath != null && !musicPath.isEmpty()) {
                callback.onProgress(60, "Mixing audio...");
                mixAudio(videoPath, musicPath, musicStartMs, musicEndMs, callback);
            } else {
                callback.onProgress(100, "Done!");
                callback.onComplete(videoPath);
            }
            return;
        }

        try {
            // Overlay PNG save karo
            File overlayFile = new File(context.getCacheDir(),
                    "overlay_" + System.currentTimeMillis() + ".png");
            FileOutputStream fos = new FileOutputStream(overlayFile);
            overlay.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            String outPath = new File(context.getCacheDir(),
                    "burned_" + System.currentTimeMillis() + ".mp4").getAbsolutePath();

            // overlay ko video ke exact resolution pe scale karke upar rakh do
            String[] burnCmd = {
                    "-i", videoPath,
                    "-i", overlayFile.getAbsolutePath(),
                    "-filter_complex",
                    "[1:v]scale=iw:ih[ov];[0:v][ov]overlay=0:0",
                    "-c:v", "libx264",
                    "-c:a", "copy",
                    "-preset", "ultrafast",
                    "-y", outPath
            };

            callBackOfQuery.callQuery(burnCmd, new FFmpegCallBack() {
                @Override
                public void process(LogMessage logMessage) {
                    // log
                }

                @Override
                public void statisticsProcess(Statistics statistics) {
                    // burn = 10% to 55%
                    mainHandler.post(() ->
                            callback.onProgress(
                                    Math.min(55, 10 + (int)(statistics.getTime() / 200f)),
                                    "Burning overlays..."
                            ));
                }

                @Override
                public void success() {
                    String burnedPath = new File(outPath).exists() ? outPath : videoPath;
                    if (musicPath != null && !musicPath.isEmpty()) {
                        mainHandler.post(() -> callback.onProgress(60, "Mixing audio..."));
                        mixAudio(burnedPath, musicPath, musicStartMs, musicEndMs, callback);
                    } else {
                        mainHandler.post(() -> {
                            callback.onProgress(100, "Done!");
                            callback.onComplete(burnedPath);
                        });
                    }
                }

                @Override
                public void cancel() {
                    mainHandler.post(() -> callback.onError("Cancelled"));
                }

                @Override
                public void failed() {
                    // Burn fail — overlay ke bina original send
                    mainHandler.post(() -> {
                        if (musicPath != null && !musicPath.isEmpty()) {
                            callback.onProgress(60, "Mixing audio...");
                            mixAudio(videoPath, musicPath, musicStartMs, musicEndMs, callback);
                        } else {
                            callback.onProgress(100, "Done!");
                            callback.onComplete(videoPath);
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callback.onError("Overlay prepare failed: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 2 — Audio mix karo (FFmpeg amix)
    // ─────────────────────────────────────────────────────────────────────────
    @SuppressLint("DefaultLocale")
    private void mixAudio(
            String videoPath,
            String musicPath,
            long startMs,
            long endMs,
            RenderCallback callback
    ) {
        try {
            String outPath = new File(context.getCacheDir(),
                    "final_reel_" + System.currentTimeMillis() + ".mp4").getAbsolutePath();

            boolean videoHasAudio = hasAudioTrack(videoPath);

            String[] audioCmd;

            if (startMs > 0 || endMs > 0) {
                // with trim
                double startSec = startMs / 1000.0;
                double durSec   = endMs > startMs ? (endMs - startMs) / 1000.0 : 60;

                if (videoHasAudio) {
                    audioCmd = new String[]{
                            "-i", videoPath,
                            "-ss", String.format("%.2f", startSec),
                            "-t",  String.format("%.2f", durSec),
                            "-i", musicPath,
                            "-filter_complex",
                            "[0:a][1:a]amix=inputs=2:duration=shortest:dropout_transition=2[aout]",
                            "-map", "0:v",
                            "-map", "[aout]",
                            "-c:v", "copy",
                            "-c:a", "aac",
                            "-shortest",
                            "-y", outPath
                    };
                } else {
                    audioCmd = new String[]{
                            "-i", videoPath,
                            "-ss", String.format("%.2f", startSec),
                            "-t",  String.format("%.2f", durSec),
                            "-i", musicPath,
                            "-map", "0:v",
                            "-map", "1:a",
                            "-c:v", "copy",
                            "-c:a", "aac",
                            "-shortest",
                            "-y", outPath
                    };
                }
            } else {
                // Bina trim ke
                if (videoHasAudio) {
                    audioCmd = new String[]{
                            "-i", videoPath,
                            "-i", musicPath,
                            "-filter_complex",
                            "[0:a][1:a]amix=inputs=2:duration=shortest:dropout_transition=2[aout]",
                            "-map", "0:v",
                            "-map", "[aout]",
                            "-c:v", "copy",
                            "-c:a", "aac",
                            "-shortest",
                            "-y", outPath
                    };
                } else {
                    audioCmd = new String[]{
                            "-i", videoPath,
                            "-i", musicPath,
                            "-map", "0:v",
                            "-map", "1:a",
                            "-c:v", "copy",
                            "-c:a", "aac",
                            "-shortest",
                            "-y", outPath
                    };
                }
            }

            callBackOfQuery.callQuery(audioCmd, new FFmpegCallBack() {
                @Override
                public void process(LogMessage logMessage) {
                    // log
                }

                @Override
                public void statisticsProcess(Statistics statistics) {
                    // audio mix = 60% to 95%
                    mainHandler.post(() ->
                            callback.onProgress(
                                    Math.min(95, 60 + (int)(statistics.getTime() / 300f)),
                                    "Mixing audio..."
                            ));
                }

                @Override
                public void success() {
                    String finalPath = new File(outPath).exists() ? outPath : videoPath;
                    mainHandler.post(() -> {
                        callback.onProgress(100, "Done!");
                        callback.onComplete(finalPath);
                    });
                }

                @Override
                public void cancel() {
                    mainHandler.post(() -> callback.onError("Audio mix cancelled"));
                }

                @Override
                public void failed() {
                    // Audio mix fail — video without music send
                    mainHandler.post(() -> {
                        callback.onProgress(100, "Done (no audio)!");
                        callback.onComplete(videoPath);
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callback.onError("Audio mix error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────
    private boolean hasAudioTrack(String videoPath) {
       MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(videoPath);
            String has = mmr.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
            return "yes".equals(has);
        } catch (Exception e) {
            return false;
        } finally {
            try { mmr.release(); } catch (Exception ignored) {}
        }
    }

    private Bitmap captureView(View view) {
        if (view == null || view.getWidth() == 0) return null;
        try {
            Bitmap bmp = Bitmap.createBitmap(
                    view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            view.draw(canvas);
            return bmp;
        } catch (Exception e) {
            return null;
        }
    }

    public void cancel() {
        callBackOfQuery.cancelProcess();
    }
}