package com.infotech.wishmaplus.zego;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.infotech.wishmaplus.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.callback.IZegoEventHandler;
import im.zego.zegoexpress.callback.IZegoIMSendCustomCommandCallback;
import im.zego.zegoexpress.constants.ZegoPlayerState;
import im.zego.zegoexpress.constants.ZegoPublisherState;
import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason;
import im.zego.zegoexpress.constants.ZegoStreamResourceMode;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.constants.ZegoVideoMirrorMode;
import im.zego.zegoexpress.constants.ZegoViewMode;
import im.zego.zegoexpress.entity.ZegoBeautifyOption;
import im.zego.zegoexpress.entity.ZegoCanvas;
import im.zego.zegoexpress.entity.ZegoPlayerConfig;
import im.zego.zegoexpress.entity.ZegoRoomConfig;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zegoexpress.entity.ZegoUser;

public class LivePageActivity extends AppCompatActivity {
    private String userID;
    private String userName;
    private String roomID;
    private boolean isHost;
    private boolean isCoHost = false;

    // Track multiple playing streams
    private Map<String, String> playingStreams = new HashMap<>(); // streamID -> userID
    private String mainStreamID = null; // Primary stream to show in hostView

    // UI Controls
    private ImageButton micButton;
    private ImageButton cameraButton;
    private ImageButton flipCameraButton;
    private ImageButton beautyFilterButton;
    private ImageButton speakerButton;
    private com.google.android.material.button.MaterialButton endLiveButton;
    private TextView viewerCountText;
    private LinearLayout layoutViewers;
    private LinearLayout liveIndicator;
    private LinearLayout waitingForHostText;

    // State tracking
    private boolean isMicEnabled = true;
    private boolean isCameraEnabled = true;
    private boolean isFrontCamera = true;
    private boolean isBeautyEnabled = false;
    private boolean isSpeakerEnabled = true;
    private Map<String, ParticipantInfo> participants = new HashMap<>();
    private Set<String> coHosts = new HashSet<>();
    private boolean isHostPresent = false;

    // Bottom sheet
    private BottomSheetDialog bottomSheetDialog;
    private ParticipantAdapter participantAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.live_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.liveView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userID = getIntent().getStringExtra("userID");
        userName = getIntent().getStringExtra("userName");
        roomID = getIntent().getStringExtra("roomID");
        isHost = getIntent().getBooleanExtra("isHost", false);

        initializeViews();
        setupClickListeners();
        startListenEvent();
        loginRoom();
    }

    private void initializeViews() {
        micButton = findViewById(R.id.micButton);
        cameraButton = findViewById(R.id.cameraButton);
        flipCameraButton = findViewById(R.id.flipCameraButton);
        beautyFilterButton = findViewById(R.id.beautyFilterButton);
        speakerButton = findViewById(R.id.speakerButton);
        endLiveButton = findViewById(R.id.endLiveButton);
        viewerCountText = findViewById(R.id.viewerCountText);
        layoutViewers = findViewById(R.id.layoutViewers);
        liveIndicator = findViewById(R.id.liveIndicator);
        waitingForHostText = findViewById(R.id.waitingForHostText);

        updateUIForRole();
    }

    private void updateUIForRole() {
        // Show controls for host and co-host
        if (isHost || isCoHost) {
            micButton.setVisibility(View.VISIBLE);
            cameraButton.setVisibility(View.VISIBLE);
            flipCameraButton.setVisibility(View.VISIBLE);
            beautyFilterButton.setVisibility(View.VISIBLE);

            if (isHost) {
                viewerCountText.setVisibility(View.VISIBLE);
                layoutViewers.setVisibility(View.VISIBLE);
                liveIndicator.setVisibility(View.VISIBLE);
                endLiveButton.setVisibility(View.VISIBLE);
            }

            // Hide waiting message for host/co-host
            waitingForHostText.setVisibility(View.GONE);
        } else {
            // Regular participant
            speakerButton.setVisibility(View.GONE);

            // Hide host controls
            micButton.setVisibility(View.GONE);
            cameraButton.setVisibility(View.GONE);
            flipCameraButton.setVisibility(View.GONE);
            beautyFilterButton.setVisibility(View.GONE);
            viewerCountText.setVisibility(View.GONE);
            layoutViewers.setVisibility(View.GONE);
            liveIndicator.setVisibility(View.GONE);
            endLiveButton.setVisibility(View.GONE);

            // Show waiting message if host not present
            updateWaitingState();
        }
    }

    private void updateWaitingState() {
        if (!isHost && !isCoHost) {
            if (!isHostPresent) {
                waitingForHostText.setVisibility(View.VISIBLE);
                speakerButton.setVisibility(View.GONE);
                liveIndicator.setVisibility(View.GONE);
                findViewById(R.id.hostView).setVisibility(View.GONE);
            } else {
                waitingForHostText.setVisibility(View.GONE);
                speakerButton.setVisibility(View.VISIBLE);
                liveIndicator.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupClickListeners() {
        // Leave button
        findViewById(R.id.leaveButton).setOnClickListener(view -> finish());

        // Microphone toggle
        micButton.setOnClickListener(view -> toggleMicrophone());

        // Camera toggle
        cameraButton.setOnClickListener(view -> toggleCamera());

        // Flip camera
        flipCameraButton.setOnClickListener(view -> flipCamera());

        // Beauty filter toggle
        beautyFilterButton.setOnClickListener(view -> toggleBeautyFilter());

        // Speaker toggle
        speakerButton.setOnClickListener(view -> toggleSpeaker());

        // Viewer count click - show participants
        layoutViewers.setOnClickListener(view -> showParticipantsBottomSheet());

        // End live button
        endLiveButton.setOnClickListener(view -> showEndLiveConfirmation());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            if (isHost || isCoHost) {
                stopPreview();
                stopPublish();
            }

            // Stop all playing streams
            for (String streamID : new ArrayList<>(playingStreams.keySet())) {
                stopPlayStream(streamID);
            }

            stopListenEvent();
            logoutRoom();
        }
    }

    // ========== Stream Management ==========

    void startPreview() {
        // Stop any existing preview first to clear old canvas
        ZegoExpressEngine.getEngine().stopPreview();

        new android.os.Handler(getMainLooper()).postDelayed(() -> {
            View hostView = findViewById(R.id.hostView);
            hostView.setVisibility(View.VISIBLE);

            ZegoCanvas previewCanvas = new ZegoCanvas(hostView);
            previewCanvas.viewMode = ZegoViewMode.ASPECT_FILL;
            ZegoExpressEngine.getEngine().startPreview(previewCanvas);
        }, 100);
    }

    void stopPreview() {
        ZegoExpressEngine.getEngine().stopPreview();
    }

    void loginRoom() {
        ZegoUser user = new ZegoUser(userID, userName);
        ZegoRoomConfig roomConfig = new ZegoRoomConfig();
        roomConfig.isUserStatusNotify = true;

        ZegoExpressEngine.getEngine().loginRoom(roomID, user, roomConfig, (int error, JSONObject extendedData) -> {
            if (error == 0) {
                Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show();

                if (isHost) {
                    isHostPresent = true;

                    new android.os.Handler(getMainLooper()).postDelayed(() -> {
                        initializeHostSettings();
                        startPreview();

                        new android.os.Handler(getMainLooper()).postDelayed(() -> {
                            startPublish();
                            notifyHostPresence(true);
                        }, 500);
                    }, 500);
                }
            } else {
                Toast.makeText(this, "Login failed. error = " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    void logoutRoom() {
        ZegoExpressEngine.getEngine().logoutRoom();
    }

    void startPublish() {
        String streamID = roomID + "_" + userID + "_call";
        ZegoExpressEngine.getEngine().startPublishingStream(streamID);
    }

    void stopPublish() {
        ZegoExpressEngine.getEngine().stopPublishingStream();
    }

    void startPlayStream(String streamID) {
        // Don't play your own stream
        String myStreamID = roomID + "_" + userID + "_call";
        if (streamID.equals(myStreamID)) {
            return;
        }

        // Check if already playing this stream
        if (playingStreams.containsKey(streamID)) {
            return;
        }

        ZegoCanvas playCanvas;
        View targetView;

        if (isHost || isCoHost) {
            // Host and co-host see other streams in participant view
            targetView = findViewById(R.id.participantView);
            targetView.setVisibility(View.VISIBLE);
            playCanvas = new ZegoCanvas(targetView);
        } else {
            // Regular participants see streams in host view
            if (mainStreamID == null) {
                // First stream goes to main view (hostView)
                targetView = findViewById(R.id.hostView);
                targetView.setVisibility(View.VISIBLE);
                mainStreamID = streamID;

                // Hide waiting message when stream starts
                waitingForHostText.setVisibility(View.GONE);
            } else {
                // Additional streams go to participant view
                targetView = findViewById(R.id.participantView);
                targetView.setVisibility(View.VISIBLE);
            }
            playCanvas = new ZegoCanvas(targetView);
        }

        playCanvas.viewMode = ZegoViewMode.ASPECT_FILL;

        ZegoPlayerConfig config = new ZegoPlayerConfig();
        config.resourceMode = ZegoStreamResourceMode.DEFAULT;

        ZegoExpressEngine.getEngine().startPlayingStream(streamID, playCanvas, config);

        // Extract userID from streamID (format: roomID_userID_call)
        String streamUserID = extractUserIDFromStreamID(streamID);
        playingStreams.put(streamID, streamUserID);

        // Check if this is the host's stream
        checkIfHostStream(streamID);
    }

    void stopPlayStream(String streamID) {
        if (!playingStreams.containsKey(streamID)) {
            return;
        }

        ZegoExpressEngine.getEngine().stopPlayingStream(streamID);
        playingStreams.remove(streamID);

        // If this was the main stream for participants, reassign
        if (streamID.equals(mainStreamID)) {
            mainStreamID = null;
            findViewById(R.id.hostView).setVisibility(View.GONE);

            // If there are other streams, promote one to main
            if (!playingStreams.isEmpty() && !(isHost || isCoHost)) {
                String nextStreamID = playingStreams.keySet().iterator().next();
                // Restart the stream in the main view
                stopPlayStream(nextStreamID);
                startPlayStream(nextStreamID);
            } else if (!isHost && !isCoHost) {
                // Show waiting message if no streams available
                updateWaitingState();
            }
        }

        // Hide participant view if no streams are using it
        if (isHost || isCoHost) {
            if (playingStreams.isEmpty()) {
                findViewById(R.id.participantView).setVisibility(View.GONE);
            }
        } else {
            // For participants, check if participant view is being used
            boolean hasSecondaryStream = playingStreams.size() > 1 ||
                    (playingStreams.size() == 1 && mainStreamID != null);
            if (!hasSecondaryStream) {
                findViewById(R.id.participantView).setVisibility(View.GONE);
            }
        }
    }

    private String extractUserIDFromStreamID(String streamID) {
        // StreamID format: roomID_userID_call
        String[] parts = streamID.split("_");
        if (parts.length >= 2) {
            // Return the userID part (second element)
            return parts[1];
        }
        return streamID;
    }

    private void checkIfHostStream(String streamID) {
        // Check if this stream belongs to the host (first user to join is typically host)
        // You might want to implement a more robust host detection mechanism
        if (!isHost && !isCoHost) {
            isHostPresent = true;
            runOnUiThread(() -> updateWaitingState());
        }
    }

    // ========== Host Feature Controls ==========

    private void initializeHostSettings() {
        ZegoExpressEngine.getEngine().setVideoMirrorMode(ZegoVideoMirrorMode.ONLY_PREVIEW_MIRROR);
        ZegoExpressEngine.getEngine().muteMicrophone(false);
        ZegoExpressEngine.getEngine().enableCamera(true);
        ZegoExpressEngine.getEngine().enableAEC(true);
        ZegoExpressEngine.getEngine().enableAGC(true);
        ZegoExpressEngine.getEngine().enableANS(true);
    }

    private void enableCoHostFeatures() {
        // Enable co-host to publish stream
        isCoHost = true;
        updateUIForRole();

        runOnUiThread(() -> {
            initializeHostSettings();
            startPreview();
            startPublish();
            Toast.makeText(this, "You can now stream as a co-host!", Toast.LENGTH_LONG).show();
        });
    }

    private void disableCoHostFeatures() {
        // Disable co-host streaming
        isCoHost = false;
        updateUIForRole();

        runOnUiThread(() -> {
            stopPreview();
            stopPublish();

            // Clear the hostView since we're not streaming anymore
            findViewById(R.id.hostView).setVisibility(View.GONE);

            Toast.makeText(this, "Co-host features disabled", Toast.LENGTH_LONG).show();

            // Restart as a regular viewer
            // The host's stream should still be playing if available
        });
    }

    private void toggleMicrophone() {
        isMicEnabled = !isMicEnabled;
        ZegoExpressEngine.getEngine().muteMicrophone(!isMicEnabled);

        if (isMicEnabled) {
            micButton.setImageResource(R.drawable.ic_mic_on);
            Toast.makeText(this, "Microphone enabled", Toast.LENGTH_SHORT).show();
        } else {
            micButton.setImageResource( R.drawable.ic_mic_off);
            Toast.makeText(this, "Microphone muted", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleCamera() {
        isCameraEnabled = !isCameraEnabled;
        ZegoExpressEngine.getEngine().enableCamera(isCameraEnabled);

        if (isCameraEnabled) {
            cameraButton.setImageResource( R.drawable.ic_video_on);
            Toast.makeText(this, "Camera enabled", Toast.LENGTH_SHORT).show();
        } else {
            cameraButton.setImageResource( R.drawable.ic_video_off);
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

    private void toggleBeautyFilter() {
        isBeautyEnabled = !isBeautyEnabled;

        ZegoBeautifyOption option = new ZegoBeautifyOption();
        if (isBeautyEnabled) {
            option.polishStep = 0.5;
            option.sharpenFactor = 0.3;
            option.whitenFactor = 0.3;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                beautyFilterButton.setBackgroundTintList(getColorStateList(android.R.color.holo_blue_light));
            }
            Toast.makeText(this, "Beauty filter enabled", Toast.LENGTH_SHORT).show();
        } else {
            option.polishStep = 0.0;
            option.sharpenFactor = 0.0;
            option.whitenFactor = 0.0;

            beautyFilterButton.setBackgroundTintList(null);
            Toast.makeText(this, "Beauty filter disabled", Toast.LENGTH_SHORT).show();
        }

        ZegoExpressEngine.getEngine().enableBeautify(option.polishStep > 0 ? 1 : 0);
    }

    private void toggleSpeaker() {
        isSpeakerEnabled = !isSpeakerEnabled;
        ZegoExpressEngine.getEngine().muteSpeaker(!isSpeakerEnabled);

        if (isSpeakerEnabled) {
            speakerButton.setImageResource( R.drawable.ic_speaker_on );
            Toast.makeText(this, "Speaker enabled", Toast.LENGTH_SHORT).show();
        } else {
            speakerButton.setImageResource( R.drawable.ic_speaker_off );
            Toast.makeText(this, "Speaker muted", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateViewerCount() {
        int viewerCount = participants.size();
        viewerCountText.setText(/*"👁 " + */viewerCount+"" /*+ (viewerCount == 1 ? " viewer" : " viewers")*/);
    }

    private void showEndLiveConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("End Live Stream")
                .setMessage("Are you sure you want to end the live stream? All participants will be removed from the room.")
                .setPositiveButton("End Live", (dialog, which) -> {
                    endLiveStream();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void endLiveStream() {
        // Notify all participants that the live stream is ending
        try {
            JSONObject commandData = new JSONObject();
            commandData.put("action", "END_LIVE");

            // Send to all users in the room using custom command to all participants
            for (ParticipantInfo participant : participants.values()) {
                ArrayList<ZegoUser> targetUsers = new ArrayList<>();
                targetUsers.add(new ZegoUser(participant.userID));

                ZegoExpressEngine.getEngine().sendCustomCommand(roomID, commandData.toString(), targetUsers, null);
            }

            Toast.makeText(this, "Live stream ended", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // End the stream and leave the room
        stopPreview();
        stopPublish();

        // Notify that host is leaving
        notifyHostPresence(false);

        // Close the activity after a brief delay to ensure messages are sent
        new android.os.Handler().postDelayed(this::finish, 500);
    }

    private void notifyHostPresence(boolean isPresent) {
        try {
            JSONObject commandData = new JSONObject();
            commandData.put("action", "HOST_PRESENCE");
            commandData.put("isPresent", isPresent);

            // Send to all participants
            for (ParticipantInfo participant : participants.values()) {
                ArrayList<ZegoUser> targetUsers = new ArrayList<>();
                targetUsers.add(new ZegoUser(participant.userID));

                ZegoExpressEngine.getEngine().sendCustomCommand(roomID, commandData.toString(), targetUsers, null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // ========== Participants Management ==========

    private void showParticipantsBottomSheet() {
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(this);
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_participants, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            RecyclerView recyclerView = bottomSheetView.findViewById(R.id.participantsRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            participantAdapter = new ParticipantAdapter(new ParticipantAdapter.OnParticipantActionListener() {
                @Override
                public void onRemoveParticipant(ParticipantInfo participant) {
                    showRemoveConfirmation(participant);
                }

                @Override
                public void onPromoteToCoHost(ParticipantInfo participant) {
                    promoteToCoHost(participant);
                }

                @Override
                public void onDemoteFromCoHost(ParticipantInfo participant) {
                    demoteFromCoHost(participant);
                }
            });

            recyclerView.setAdapter(participantAdapter);

            Button inviteButton = bottomSheetView.findViewById(R.id.inviteButton);
            inviteButton.setOnClickListener(v -> inviteParticipants());
        }

        updateParticipantList();
        bottomSheetDialog.show();
    }

    private void updateParticipantList() {
        List<ParticipantInfo> participantList = new ArrayList<>(participants.values());
        if (participantAdapter != null) {
            participantAdapter.setParticipants(participantList);
        }
    }

    private void inviteParticipants() {
        String inviteLink = "Join my live stream! Room ID: " + roomID;

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Room ID", inviteLink);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Invite link copied to clipboard!", Toast.LENGTH_LONG).show();
    }

    private void showRemoveConfirmation(ParticipantInfo participant) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Participant")
                .setMessage("Are you sure you want to remove " + participant.userName + " from the room?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    removeParticipant(participant);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeParticipant(ParticipantInfo participant) {
        try {
            JSONObject commandData = new JSONObject();
            commandData.put("action", "REMOVE");
            commandData.put("targetUserID", participant.userID);

            ArrayList<ZegoUser> targetUsers = new ArrayList<>();
            targetUsers.add(new ZegoUser(participant.userID));

            ZegoExpressEngine.getEngine().sendCustomCommand(roomID, commandData.toString(), targetUsers, new IZegoIMSendCustomCommandCallback() {
                @Override
                public void onIMSendCustomCommandResult(int errorCode) {
                    runOnUiThread(() -> {
                        if (errorCode == 0) {
                            participants.remove(participant.userID);
                            coHosts.remove(participant.userID);
                            updateViewerCount();
                            updateParticipantList();
                            Toast.makeText(LivePageActivity.this, participant.userName + " has been removed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LivePageActivity.this, "Failed to remove participant. Error: " + errorCode, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error removing participant", Toast.LENGTH_SHORT).show();
        }
    }

    private void promoteToCoHost(ParticipantInfo participant) {
        try {
            JSONObject commandData = new JSONObject();
            commandData.put("action", "PROMOTE_COHOST");
            commandData.put("targetUserID", participant.userID);

            ArrayList<ZegoUser> targetUsers = new ArrayList<>();
            targetUsers.add(new ZegoUser(participant.userID));

            ZegoExpressEngine.getEngine().sendCustomCommand(roomID, commandData.toString(), targetUsers, new IZegoIMSendCustomCommandCallback() {
                @Override
                public void onIMSendCustomCommandResult(int errorCode) {
                    runOnUiThread(() -> {
                        if (errorCode == 0) {
                            coHosts.add(participant.userID);
                            participant.isCoHost = true;
                            Toast.makeText(LivePageActivity.this, participant.userName + " is now a co-host", Toast.LENGTH_SHORT).show();
                            updateParticipantList();
                        } else {
                            Toast.makeText(LivePageActivity.this, "Failed to promote to co-host. Error: " + errorCode, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error promoting to co-host", Toast.LENGTH_SHORT).show();
        }
    }

    private void demoteFromCoHost(ParticipantInfo participant) {
        try {
            JSONObject commandData = new JSONObject();
            commandData.put("action", "DEMOTE_COHOST");
            commandData.put("targetUserID", participant.userID);

            ArrayList<ZegoUser> targetUsers = new ArrayList<>();
            targetUsers.add(new ZegoUser(participant.userID));

            ZegoExpressEngine.getEngine().sendCustomCommand(roomID, commandData.toString(), targetUsers, new IZegoIMSendCustomCommandCallback() {
                @Override
                public void onIMSendCustomCommandResult(int errorCode) {
                    runOnUiThread(() -> {
                        if (errorCode == 0) {
                            coHosts.remove(participant.userID);
                            participant.isCoHost = false;
                            Toast.makeText(LivePageActivity.this, participant.userName + " removed from co-host", Toast.LENGTH_SHORT).show();
                            updateParticipantList();
                        } else {
                            Toast.makeText(LivePageActivity.this, "Failed to remove co-host. Error: " + errorCode, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error removing co-host", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCustomCommand(String fromUserID, String content) {
        try {
            JSONObject commandData = new JSONObject(content);
            String action = commandData.getString("action");

            // Handle actions that don't require targetUserID
            if ("END_LIVE".equals(action)) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "The host has ended the live stream", Toast.LENGTH_LONG).show();
                    finish();
                });
                return;
            } else if ("HOST_PRESENCE".equals(action)) {
                boolean isPresent = commandData.getBoolean("isPresent");
                isHostPresent = isPresent;
                runOnUiThread(() -> updateWaitingState());
                return;
            }

            // Handle actions that require targetUserID
            if (!commandData.has("targetUserID")) {
                return;
            }

            String targetUserID = commandData.getString("targetUserID");

            // Only process if this command is for current user
            if (targetUserID.equals(userID)) {
                switch (action) {
                    case "REMOVE":
                        runOnUiThread(() -> {
                            Toast.makeText(this, "You have been removed from the room by the host", Toast.LENGTH_LONG).show();
                            finish();
                        });
                        break;

                    case "PROMOTE_COHOST":
                        enableCoHostFeatures();
                        break;

                    case "DEMOTE_COHOST":
                        disableCoHostFeatures();
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // ========== Event Listeners ==========

    void startListenEvent() {
        ZegoExpressEngine.getEngine().setEventHandler(new IZegoEventHandler() {
            @Override
            public void onRoomStreamUpdate(String roomID, ZegoUpdateType updateType, ArrayList<ZegoStream> streamList, JSONObject extendedData) {
                super.onRoomStreamUpdate(roomID, updateType, streamList, extendedData);

                if (updateType == ZegoUpdateType.ADD) {
                    // Start playing each new stream
                    for (ZegoStream stream : streamList) {
                        startPlayStream(stream.streamID);
                    }
                } else {
                    // Stop playing each removed stream
                    for (ZegoStream stream : streamList) {
                        stopPlayStream(stream.streamID);
                    }

                    // If all streams are removed and we're not host, show waiting state
                    if (!isHost && !isCoHost && playingStreams.isEmpty()) {
                        isHostPresent = false;
                        runOnUiThread(() -> updateWaitingState());
                    }
                }
            }

            @Override
            public void onRoomUserUpdate(String roomID, ZegoUpdateType updateType, ArrayList<ZegoUser> userList) {
                super.onRoomUserUpdate(roomID, updateType, userList);

                if (updateType == ZegoUpdateType.ADD) {
                    for (ZegoUser user : userList) {
                        // Don't add self to participants list
                        if (!user.userID.equals(userID)) {
                            boolean isCoHost = coHosts.contains(user.userID);
                            participants.put(user.userID, new ParticipantInfo(user.userID, user.userName, isCoHost));
                            String text = user.userName + " joined the room";
                            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (updateType == ZegoUpdateType.DELETE) {
                    for (ZegoUser user : userList) {
                        participants.remove(user.userID);
                        coHosts.remove(user.userID);
                        String text = user.userName + " left the room";
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    }
                }

                if (isHost) {
                    runOnUiThread(() -> {
                        updateViewerCount();
                        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                            updateParticipantList();
                        }
                    });
                }
            }

            @Override
            public void onIMRecvCustomCommand(String roomID, ZegoUser fromUser, String command) {
                super.onIMRecvCustomCommand(roomID, fromUser, command);
                handleCustomCommand(fromUser.userID, command);
            }

            @Override
            public void onRoomStateChanged(String roomID, ZegoRoomStateChangedReason reason, int i, JSONObject jsonObject) {
                super.onRoomStateChanged(roomID, reason, i, jsonObject);

                if (reason == ZegoRoomStateChangedReason.LOGIN_FAILED) {
                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                } else if (reason == ZegoRoomStateChangedReason.RECONNECT_FAILED) {
                    Toast.makeText(getApplicationContext(), "Reconnection failed", Toast.LENGTH_LONG).show();
                } else if (reason == ZegoRoomStateChangedReason.KICK_OUT) {
                    Toast.makeText(getApplicationContext(), "Kicked out", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPublisherStateUpdate(String streamID, ZegoPublisherState state, int errorCode, JSONObject extendedData) {
                super.onPublisherStateUpdate(streamID, state, errorCode, extendedData);

                if (errorCode != 0) {
                    Toast.makeText(getApplicationContext(), "Publishing error: " + errorCode, Toast.LENGTH_LONG).show();
                } else if (state == ZegoPublisherState.PUBLISHING) {
                    runOnUiThread(() -> {
                        if (isHost) {
                            liveIndicator.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onPlayerStateUpdate(String streamID, ZegoPlayerState state, int errorCode, JSONObject extendedData) {
                super.onPlayerStateUpdate(streamID, state, errorCode, extendedData);

                if (errorCode != 0) {
                    Toast.makeText(getApplicationContext(), "Playing error: " + errorCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPublisherQualityUpdate(String streamID, im.zego.zegoexpress.entity.ZegoPublishStreamQuality quality) {
                super.onPublisherQualityUpdate(streamID, quality);
            }

            @Override
            public void onPlayerQualityUpdate(String streamID, im.zego.zegoexpress.entity.ZegoPlayStreamQuality quality) {
                super.onPlayerQualityUpdate(streamID, quality);
            }
        });
    }

    void stopListenEvent() {
        ZegoExpressEngine.getEngine().setEventHandler(null);
    }
}