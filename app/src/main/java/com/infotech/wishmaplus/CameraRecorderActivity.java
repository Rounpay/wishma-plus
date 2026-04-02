package com.infotech.wishmaplus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.infotech.wishmaplus.Api.Response.MediaModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CameraRecorderActivity extends AppCompatActivity {

    // ─── Permission Request Codes ──────────────────────────────────────────────
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    // ─── Views ────────────────────────────────────────────────────────────────
    private PreviewView cameraPreview;
    private ImageButton btnClose, btnFlash, btnMore, btnRecord, btnGallery, btnFlipCamera;
    private LinearLayout btnAddMusic, btnSpeed, btnEffects, btnGreenScreen, btnFilter, btnTimer, btnBeauty;
    private View recordingIndicatorDot;
    private TextView tvRecordingTimer, tvRecordingLabel, tvSpeedValue;
    private LinearLayout recordingTopBar;
    private View progressBar;

    // ─── Camera & Recording State ─────────────────────────────────────────────
    private boolean isRecording = false;
    private boolean isFrontCamera = false;
    private boolean isFlashOn = false;
    private boolean isMuted = false;
    private ProcessCameraProvider cameraProvider;
    private VideoCapture<Recorder> videoCapture;
    private Recording activeRecording;
    private CameraControl cameraControl;

    // ─── Timer ────────────────────────────────────────────────────────────────
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private int recordingSeconds = 0;
    private static final int MAX_RECORDING_SECONDS = 60; // 60 sec max like Facebook Reels
    private Runnable timerRunnable;

    // ─── Speed ───────────────────────────────────────────────────────────────
    private float currentSpeed = 1.0f;
    private final float[] speedOptions = {0.3f, 0.5f, 1.0f, 2.0f, 3.0f};
    private final String[] speedLabels = {"0.3x", "0.5x", "1x", "2x", "3x"};
    private int speedIndex = 2; // default 1x

    // ─── Saved Video Path ─────────────────────────────────────────────────────
    private String savedVideoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Full screen - no status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera_recorder);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        setupTimerRunnable();
        setClickListeners();

        if (hasAllPermissions()) {
            startCamera();
        } else {
            requestPermissions();
        }
    }

    // ─── View Initialization ──────────────────────────────────────────────────

    private void initViews() {
        cameraPreview = findViewById(R.id.cameraPreview);
        btnClose = findViewById(R.id.btnClose);
        btnFlash = findViewById(R.id.btnFlash);
        btnMore = findViewById(R.id.btnMore);
        btnRecord = findViewById(R.id.btnRecord);
        btnGallery = findViewById(R.id.btnGallery);
        btnFlipCamera = findViewById(R.id.btnFlipCamera);
        btnAddMusic = findViewById(R.id.btnAddMusic);
        btnSpeed = findViewById(R.id.btnSpeed);
        btnEffects = findViewById(R.id.btnEffects);
        btnGreenScreen = findViewById(R.id.btnGreenScreen);
        btnFilter = findViewById(R.id.btnFilter);
        btnTimer = findViewById(R.id.btnTimer);
        btnBeauty = findViewById(R.id.btnBeauty);

        // Recording UI
        recordingIndicatorDot = findViewById(R.id.recordingIndicatorDot);
        tvRecordingTimer = findViewById(R.id.tvRecordingTimer);
        tvRecordingLabel = findViewById(R.id.tvRecordingLabel);
        recordingTopBar = findViewById(R.id.recordingTopBar);
        progressBar = findViewById(R.id.recordingProgressBar);
        tvSpeedValue = findViewById(R.id.tvSpeedValue);

        // Set initial speed label
        if (tvSpeedValue != null) tvSpeedValue.setText("1x");
    }

    // ─── Click Listeners ──────────────────────────────────────────────────────

    private void setClickListeners() {

        // Close button
        btnClose.setOnClickListener(v -> {
            if (isRecording) stopRecording();
            finish();
        });

        // Flash toggle
        btnFlash.setOnClickListener(v -> toggleFlash());

        // More options
        btnMore.setOnClickListener(v -> Toast.makeText(this, "More options", Toast.LENGTH_SHORT).show());

        // Record button - main action
        btnRecord.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        });

        // Flip camera
        btnFlipCamera.setOnClickListener(v -> {
            if (isRecording) {
                Toast.makeText(this, "Recording is progress please stop recording", Toast.LENGTH_SHORT).show();
                return;
            }
            isFrontCamera = !isFrontCamera;
            startCamera();
        });

        // Gallery
        btnGallery.setOnClickListener(v -> {
            Toast.makeText(this, "Gallery", Toast.LENGTH_SHORT).show();
        });

        // Add Music
        btnAddMusic.setOnClickListener(v -> Toast.makeText(this, "Music", Toast.LENGTH_SHORT).show());

        // Speed selector (cycles through speed options)
        btnSpeed.setOnClickListener(v -> cycleSpeed());

        // Effects
        btnEffects.setOnClickListener(v -> Toast.makeText(this, "Effects", Toast.LENGTH_SHORT).show());

        // Green Screen
        btnGreenScreen.setOnClickListener(v -> Toast.makeText(this, "Green Screen", Toast.LENGTH_SHORT).show());

        // Filter
        if (btnFilter != null)
            btnFilter.setOnClickListener(v -> Toast.makeText(this, "Filter", Toast.LENGTH_SHORT).show());

        // Timer
        if (btnTimer != null)
            btnTimer.setOnClickListener(v -> Toast.makeText(this, "Timer (3s / 10s)", Toast.LENGTH_SHORT).show());

        // Beauty
        if (btnBeauty != null)
            btnBeauty.setOnClickListener(v -> Toast.makeText(this, "Beauty Mode", Toast.LENGTH_SHORT).show());
    }

    // ─── Speed Cycle ─────────────────────────────────────────────────────────

    private void cycleSpeed() {
        speedIndex = (speedIndex + 1) % speedOptions.length;
        currentSpeed = speedOptions[speedIndex];
        if (tvSpeedValue != null) tvSpeedValue.setText(speedLabels[speedIndex]);
        Toast.makeText(this, "Speed: " + speedLabels[speedIndex], Toast.LENGTH_SHORT).show();
    }

    // ─── Flash ───────────────────────────────────────────────────────────────

    private void toggleFlash() {
        // Front camera me flash usually support nahi hota
        if (isFrontCamera) {

            Toast.makeText(
                    this,
                    "Flash not available on Front Camera",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Toggle flash state
        isFlashOn = !isFlashOn;

        if (cameraControl != null) {

            cameraControl.enableTorch(isFlashOn);
        }
        //  Change icon based on flash state
        if (isFlashOn) {
            btnFlash.setImageResource(
                    R.drawable.flash_on
            );
        }
        else {
            btnFlash.setImageResource(R.drawable.flash_off
            );
        }
        Toast.makeText(
                this,
                isFlashOn ? "Flash ON" : "Flash OFF",
                Toast.LENGTH_SHORT
        ).show();
    }

    // ─── CameraX Setup ───────────────────────────────────────────────────────

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);

        future.addListener(() -> {
            try {
                cameraProvider = future.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases(@NonNull ProcessCameraProvider provider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

        Recorder recorder = new Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.HD)).build();
        videoCapture = VideoCapture.withOutput(recorder);

        CameraSelector selector = isFrontCamera ? CameraSelector.DEFAULT_FRONT_CAMERA : CameraSelector.DEFAULT_BACK_CAMERA;

        provider.unbindAll();
        androidx.camera.core.Camera camera = provider.bindToLifecycle(this, selector, preview, videoCapture);

        cameraControl = camera.getCameraControl();
    }

    // ─── Recording: Start ─────────────────────────────────────────────────────

    @SuppressLint("MissingPermission")
    private void startRecording() {
        if (videoCapture == null) return;

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String fileName = "Reel_" + timestamp + ".mp4";

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Reels");

        MediaStoreOutputOptions outputOptions = new MediaStoreOutputOptions.Builder(getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI).setContentValues(contentValues).build();

        activeRecording = videoCapture.getOutput().prepareRecording(this, outputOptions).withAudioEnabled().start(ContextCompat.getMainExecutor(this), videoRecordEvent -> {

            if (videoRecordEvent instanceof VideoRecordEvent.Start) {
                // Recording
                isRecording = true;
                runOnUiThread(this::onRecordingStarted);

            } else if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                VideoRecordEvent.Finalize finalize = (VideoRecordEvent.Finalize) videoRecordEvent;
                isRecording = false;
                runOnUiThread(() -> {
                    if (!finalize.hasError()) {
                        savedVideoPath = finalize.getOutputResults().getOutputUri().toString();
                        onRecordingStopped(savedVideoPath);
                    } else {
                        Toast.makeText(this, "Recording error: " + finalize.getError(), Toast.LENGTH_SHORT).show();
                        onRecordingCancelled();
                    }
                });
            }
        });
    }

    // ─── Recording: Stop ─────────────────────────────────────────────────────

    private void stopRecording() {
        if (activeRecording != null) {
            activeRecording.stop();
            activeRecording = null;
        }
        stopTimer();
    }

    // ─── Recording UI: Started ────────────────────────────────────────────────

    private void onRecordingStarted() {
        // Record button → square (stop icon)
        btnRecord.setImageResource(android.R.drawable.ic_media_pause); // use ic_stop if available
        btnRecord.setBackgroundResource(R.drawable.record_inner_circle); // red square bg
        btnRecord.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF3B30));
        // Show recording top bar (REC label + timer)
        if (recordingTopBar != null) recordingTopBar.setVisibility(View.VISIBLE);

        // Blink the red dot
        startBlinkAnimation();

        // Start countdown timer
        recordingSeconds = 0;
        startTimer();

        // Disable side buttons during recording (like Instagram/FB)
        setSideButtonsEnabled(false);

        // Flash off when recording starts (optional)
        // cameraControl.enableTorch(false);
    }

    // ─── Recording UI: Stopped ────────────────────────────────────────────────

    private void onRecordingStopped(String videoPath) {
        // Reset record button to record state
        btnRecord.setImageResource(android.R.drawable.ic_media_play);
        btnRecord.setBackgroundResource(R.drawable.record_inner_circle);

        // Hide recording bar
        if (recordingTopBar != null) recordingTopBar.setVisibility(View.GONE);

        // Stop blink
        stopBlinkAnimation();

        // Re-enable side buttons
        setSideButtonsEnabled(true);

        // ───────────────────────────────────────────────────────────────────
        // Navigate to ReelEditorActivity
        // ───────────────────────────────────────────────────────────────────
        Intent intent = new Intent(this, ReelEditorActivity.class);
        MediaModel recordedClip = new MediaModel(videoPath, true, recordingSeconds * 1000L);
        ArrayList<MediaModel> mediaList = new ArrayList<>();
        mediaList.add(recordedClip);
        intent.putExtra("media_list", mediaList);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void onRecordingCancelled() {
        btnRecord.setImageResource(android.R.drawable.ic_media_play);
        btnRecord.setBackgroundResource(R.drawable.record_inner_circle);
        if (recordingTopBar != null) recordingTopBar.setVisibility(View.GONE);
        stopBlinkAnimation();
        setSideButtonsEnabled(true);
    }

    // ─── Timer Logic ─────────────────────────────────────────────────────────

    private void setupTimerRunnable() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                recordingSeconds++;

                // Update timer text
                int min = recordingSeconds / 60;
                int sec = recordingSeconds % 60;
                String timeStr = String.format(Locale.US, "%02d:%02d", min, sec);
                if (tvRecordingTimer != null) tvRecordingTimer.setText(timeStr);

                // Update progress bar
                if (progressBar != null) {
                    float fraction = (float) recordingSeconds / MAX_RECORDING_SECONDS;
                    progressBar.setScaleX(Math.min(fraction, 1f));
                }

                // Auto-stop at max duration
                if (recordingSeconds >= MAX_RECORDING_SECONDS) {
                    stopRecording();
                    return;
                }

                timerHandler.postDelayed(this, 1000);
            }
        };
    }

    private void startTimer() {
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    // ─── Blink Animation ──────────────────────────────────────────────────────

    private Runnable blinkRunnable;
    private boolean isDotVisible = true;

    private void startBlinkAnimation() {
        if (recordingIndicatorDot == null) return;
        blinkRunnable = new Runnable() {
            @Override
            public void run() {
                isDotVisible = !isDotVisible;
                recordingIndicatorDot.setVisibility(isDotVisible ? View.VISIBLE : View.INVISIBLE);
                timerHandler.postDelayed(this, 600);
            }
        };
        timerHandler.post(blinkRunnable);
    }

    private void stopBlinkAnimation() {
        if (blinkRunnable != null) timerHandler.removeCallbacks(blinkRunnable);
        if (recordingIndicatorDot != null) recordingIndicatorDot.setVisibility(View.GONE);
    }

    // ─── Enable/Disable Side Buttons ─────────────────────────────────────────

    private void setSideButtonsEnabled(boolean enabled) {
        float alpha = enabled ? 1.0f : 0.4f;
        if (btnAddMusic != null) {
            btnAddMusic.setEnabled(enabled);
            btnAddMusic.setAlpha(alpha);
        }
        if (btnEffects != null) {
            btnEffects.setEnabled(enabled);
            btnEffects.setAlpha(alpha);
        }
        if (btnGreenScreen != null) {
            btnGreenScreen.setEnabled(enabled);
            btnGreenScreen.setAlpha(alpha);
        }
        if (btnFilter != null) {
            btnFilter.setEnabled(enabled);
            btnFilter.setAlpha(alpha);
        }
        if (btnTimer != null) {
            btnTimer.setEnabled(enabled);
            btnTimer.setAlpha(alpha);
        }
        if (btnBeauty != null) {
            btnBeauty.setEnabled(enabled);
            btnBeauty.setAlpha(alpha);
        }
        if (btnFlipCamera != null) {
            btnFlipCamera.setEnabled(enabled);
            btnFlipCamera.setAlpha(alpha);
        }
    }

    // ─── Permissions ─────────────────────────────────────────────────────────

    private boolean hasAllPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissions()) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera or mic permission is required", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // ─── Lifecycle ───────────────────────────────────────────────────────────

    @Override
    protected void onPause() {
        super.onPause();
        if (isRecording) stopRecording();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        stopBlinkAnimation();
    }
}