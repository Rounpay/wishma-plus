package com.infotech.wishmaplus.zego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.infotech.wishmaplus.R;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.constants.ZegoVideoMirrorMode;
import im.zego.zegoexpress.constants.ZegoViewMode;
import im.zego.zegoexpress.entity.ZegoCanvas;

public class PreviewActivity extends AppCompatActivity {

    private String userID;
    private String userName;
    private String roomID;
    private boolean isHost;

    private ImageButton micButton;
    private ImageButton cameraButton;
    private ImageButton flipCameraButton;
    private MaterialButton startLiveButton;

    private boolean isMicEnabled = true;
    private boolean isCameraEnabled = true;
    private boolean isFrontCamera = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preview);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.previewRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get intent data
        userID = getIntent().getStringExtra("userID");
        userName = getIntent().getStringExtra("userName");
        roomID = getIntent().getStringExtra("roomID");
        isHost = getIntent().getBooleanExtra("isHost", false);

        initializeViews();
        setupClickListeners();

        // Initialize camera settings
        initializeCameraSettings();

        // Start preview
        startCameraPreview();
    }

    private void initializeViews() {
        micButton = findViewById(R.id.previewMicButton);
        cameraButton = findViewById(R.id.previewCameraButton);
        flipCameraButton = findViewById(R.id.previewFlipCameraButton);
        startLiveButton = findViewById(R.id.startLiveButton);

        // Close button
        findViewById(R.id.closePreviewButton).setOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        micButton.setOnClickListener(v -> toggleMicrophone());
        cameraButton.setOnClickListener(v -> toggleCamera());
        flipCameraButton.setOnClickListener(v -> flipCamera());
        startLiveButton.setOnClickListener(v -> startLive());
    }

    private void initializeCameraSettings() {
        // Set video mirror mode
        ZegoExpressEngine.getEngine().setVideoMirrorMode(ZegoVideoMirrorMode.ONLY_PREVIEW_MIRROR);

        // Set default audio and video config
        ZegoExpressEngine.getEngine().muteMicrophone(!isMicEnabled);
        ZegoExpressEngine.getEngine().enableCamera(isCameraEnabled);

        // Enable audio enhancements
        ZegoExpressEngine.getEngine().enableAEC(true);
        ZegoExpressEngine.getEngine().enableAGC(true);
        ZegoExpressEngine.getEngine().enableANS(true);

        // Set front camera
        ZegoExpressEngine.getEngine().useFrontCamera(isFrontCamera);
    }

    private void startCameraPreview() {
        ZegoCanvas previewCanvas = new ZegoCanvas(findViewById(R.id.previewView));
        previewCanvas.viewMode = ZegoViewMode.ASPECT_FILL;
        ZegoExpressEngine.getEngine().startPreview(previewCanvas);
    }

    private void toggleMicrophone() {
        isMicEnabled = !isMicEnabled;
        ZegoExpressEngine.getEngine().muteMicrophone(!isMicEnabled);

        if (isMicEnabled) {
            micButton.setImageResource(R.drawable.ic_mic_on);
            Toast.makeText(this, "Microphone enabled", Toast.LENGTH_SHORT).show();
        } else {
            micButton.setImageResource(R.drawable.ic_mic_off);
            Toast.makeText(this, "Microphone muted", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleCamera() {
        isCameraEnabled = !isCameraEnabled;
        ZegoExpressEngine.getEngine().enableCamera(isCameraEnabled);

        if (isCameraEnabled) {
            cameraButton.setImageResource(R.drawable.ic_video_on);
            Toast.makeText(this, "Camera enabled", Toast.LENGTH_SHORT).show();
        } else {
            cameraButton.setImageResource(R.drawable.ic_video_off);
            Toast.makeText(this, "Camera disabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void flipCamera() {
        isFrontCamera = !isFrontCamera;
        ZegoExpressEngine.getEngine().useFrontCamera(isFrontCamera);

        if (isFrontCamera) {
            Toast.makeText(this, "Switched to front camera", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Switched to back camera", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLive() {
        // Stop preview before starting live
        ZegoExpressEngine.getEngine().stopPreview();

        // Start live activity with current settings
        Intent intent = new Intent(this, LivePageActivity.class);
        intent.putExtra("userID", userID);
        intent.putExtra("userName", userName);
        intent.putExtra("roomID", roomID);
        intent.putExtra("isHost", isHost);
        intent.putExtra("isMicEnabled", isMicEnabled);
        intent.putExtra("isCameraEnabled", isCameraEnabled);
        intent.putExtra("isFrontCamera", isFrontCamera);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop preview when activity is destroyed
        ZegoExpressEngine.getEngine().stopPreview();
    }
}