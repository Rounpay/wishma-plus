package com.infotech.wishmaplus.Activity;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Api.Object.ContentResult;
import com.infotech.wishmaplus.Api.Object.StoryResult;
import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.StoryProgressView.StoriesProgressView;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.VideoEdit.AutoPlayVideo.DownloadManagerService;
import com.infotech.wishmaplus.Utils.VideoEdit.AutoPlayVideo.VideoUtils;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryViewActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    StoryResult storyResult;
    StoriesProgressView progressView;
    ImageView profile;
    ImageView imageView;
    TextView nameTv;
    TextView timeTv;
    TextView postTxt;
    TextView contentView;
    PlayerView videoView;
    ImageButton deleteBtn;
    private AudioManager am;
    private ExoPlayer exoPlayer;
    private long positionVideo;
    private int currentPosition = 0;
    private ArrayList<StoryResult> allStoryList = new ArrayList<>();
    private int selectedPosition;
    private String userId;
    private PreferencesManager tokenManager;


    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_story_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        storyResult = getIntent().getParcelableExtra("Data");
        allStoryList = getIntent().getParcelableArrayListExtra("List");
        selectedPosition = getIntent().getIntExtra("SelectedPosition", 0);
        progressView = findViewById(R.id.stories);
        progressView.setStoriesListener(this);
        profile = findViewById(R.id.profile);
        imageView = findViewById(R.id.imageView);
        nameTv = findViewById(R.id.nameTv);
        timeTv = findViewById(R.id.timeTv);
        postTxt = findViewById(R.id.postTxt);
        contentView = findViewById(R.id.contentView);
        deleteBtn = findViewById(R.id.deleteBtn);
        videoView = findViewById(R.id.videoView);
         tokenManager = new PreferencesManager(this,1);
         userId = tokenManager.getUserId();
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                        /* minBufferMs= */ 2000,   // 1 seconds
                        /* maxBufferMs= */ 2000,   // 2 seconds
                        /* bufferForPlaybackMs= */ 1000,  // 1 second for playback start
                        /* bufferForPlaybackAfterRebufferMs= */ 2000  // 2 seconds after rebuffering
                )
                .build();

        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this)
                .setEnableDecoderFallback(true)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);
        exoPlayer = new ExoPlayer.Builder(this, renderersFactory).setLoadControl(loadControl).build();
        videoView.findViewById(androidx.media3.ui.R.id.exo_settings).setVisibility(View.GONE);
        videoView.setPlayer(exoPlayer);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                // if (am.isMusicActive()) {
                if (isPlaying) {
                    am.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                } else {
                    am.abandonAudioFocus(focusChangeListener);

                }
                //}
                //Player.Listener.super.onIsPlayingChanged(isPlaying);
            }
        });
        ViewGroup.MarginLayoutParams paramsPost = (ViewGroup.MarginLayoutParams) postTxt.getLayoutParams();
        videoView.setControllerVisibilityListener((PlayerView.ControllerVisibilityListener) visibility -> {
            if (visibility == View.VISIBLE) {
                if (postTxt.getVisibility() == View.VISIBLE) {
                    paramsPost.bottomMargin = (int) getResources().getDimension(com.intuit.sdp.R.dimen._65sdp);

                    postTxt.setLayoutParams(paramsPost);
                }
            } else {
                if (postTxt.getVisibility() == View.VISIBLE) {
                    paramsPost.bottomMargin = (int) getResources().getDimension(com.intuit.sdp.R.dimen._20sdp);

                    postTxt.setLayoutParams(paramsPost);
                }
            }
        });

        /*videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float screenWidth = v.getWidth();
                    float touchX = event.getX();

                    if (touchX < 300*//*screenWidth / 2*//*) {
                        // Left side of the screen
                        onPreviousClick();
                    } else  if (touchX > screenWidth-300){
                        // Right side of the screen
                        onNextClick();
                    }
                    return false;
                }
                return false;
            }
        });*/


        findViewById(R.id.mainView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float screenWidth = v.getWidth();
                    float touchX = event.getX();

                    if (touchX < 300/*screenWidth / 2*/) {
                        // Left side of the screen
                        onPreviousClick();
                    } else  if (touchX > screenWidth-300){
                        // Right side of the screen
                        onNextClick();
                    }
                    return true;
                }
                return false;
            }
        });
        deleteBtn.setOnClickListener(view -> {
            showDeleteConfirmationDialog(storyResult.getStories().get(currentPosition));
        });
        findViewById(R.id.back_button).setOnClickListener(view -> finish());

        setUi(0);

    }

    void setUi(int position) {
        currentPosition = position;
        if(userId.equalsIgnoreCase(storyResult.getUserId())){
            deleteBtn.setVisibility(View.VISIBLE);
        }else {
            deleteBtn.setVisibility(View.GONE);
        }
        Glide.with(this)
                .load(storyResult.getProfilePictureUrl())
                .apply(UtilMethods.INSTANCE.getRequestOption_With_UserIcon())
                .into(profile);
        nameTv.setText(storyResult.getFirstName() + " " + storyResult.getLastName());
        if (storyResult.getStories() != null && storyResult.getStories().size() > 0) {
            progressView.setStoriesCount(storyResult.getStories().size());// <- set stories
            for (int i = 0; i < storyResult.getStories().size(); i++) {
                long duration = storyResult.getStories().get(i).getDurationInMs();
                progressView.setStoryDuration(duration > 0 ? duration : 30000L, i); // <- set a story duration
            }
            progressView.startStories(currentPosition);
            changeUi(storyResult.getStories().get(currentPosition));
        }
    }

    void changeUi(ContentResult contentResult) {


        timeTv.setText(UtilMethods.INSTANCE.covertTimeToText(contentResult.getEntryAt()));
        if (contentResult.getContentTypeId() == UtilMethods.INSTANCE.VIDEO_TYPE) {
            String savePath = VideoUtils.getString(this, contentResult.getPostContent());
            if (savePath != null && new File(savePath).exists() ) {
                exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(savePath)));
            } else  {
                exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(contentResult.getPostContent())));
                Intent serviceIntent = new Intent(this, DownloadManagerService.class);
                startService(serviceIntent);
                new VideoUtils().startDownloadInBackground(this, contentResult.getPostContent(), getExternalCacheDir() + "/MyVideo/");
            }
            exoPlayer.prepare();
            exoPlayer.play();

            videoView.setVisibility(View.VISIBLE);
            contentView.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            if (contentResult.getCaption() != null && !contentResult.getCaption().trim().isEmpty()) {
                postTxt.setText(contentResult.getCaption().trim());
                postTxt.setVisibility(View.VISIBLE);
            } else {
                postTxt.setVisibility(View.GONE);
            }
        } else if (contentResult.getContentTypeId() == UtilMethods.INSTANCE.IMAGE_TYPE) {
            exoPlayer.pause();
            contentView.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            if (contentResult.getCaption() != null && !contentResult.getCaption().trim().isEmpty()) {
                postTxt.setText(contentResult.getCaption().trim());
                postTxt.setVisibility(View.VISIBLE);
            } else {
                postTxt.setVisibility(View.GONE);
            }
            Glide.with(this)
                    .load(contentResult.getPostContent())
                    .apply(UtilMethods.INSTANCE.getRequestOption_With_PlaceHolder())
                    .into(imageView);
        } else {
            exoPlayer.pause();
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
            contentView.setText(contentResult.getPostContent().trim());
            postTxt.setVisibility(View.GONE);
        }


    }

    private void showDeleteConfirmationDialog(ContentResult content) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Content")
                .setMessage("Are you sure you want to delete this content?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteContentFromServer(content.getPostId());  // Call API to delete content
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteContentFromServer(String postId) {
        EndPointInterface apiService = ApiClient.getClient().create(EndPointInterface.class);
        Call<BasicResponse> call = apiService.deleteStory("Bearer " + tokenManager.getAccessToken(), postId);

        call.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(@NonNull Call<BasicResponse> call, @NonNull Response<BasicResponse> response) {
                if (response.isSuccessful()) {

                    setResult(RESULT_OK);
                    storyResult.getStories().remove(currentPosition);
                    allStoryList.get(selectedPosition).getStories().remove(currentPosition);
                    progressView.setStoriesCount(storyResult.getStories().size());
                    if(exoPlayer.isPlaying()){
                        exoPlayer.pause();
                    }
                    if(currentPosition>0) {
                        currentPosition = currentPosition - 1;
                    }
                    if(currentPosition<storyResult.getStories().size()-1){
                        changeUi(storyResult.getStories().get(currentPosition));
                    }else {
                        if(selectedPosition<allStoryList.size()-1){
                            selectedPosition = selectedPosition + 1;
                            storyResult = allStoryList.get(selectedPosition);
                            setUi(0);
                        }else {
                            finish();
                        }
                    }
                    setUi(currentPosition);
                    Toast.makeText(StoryViewActivity.this, "Story deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    UtilMethods.INSTANCE.apiErrorHandle(StoryViewActivity.this,response.code(),response.message());
                    //Toast.makeText(ImageZoomViewActivity.this, "Failed to delete content", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BasicResponse> call, @NonNull Throwable t) {
                UtilMethods.INSTANCE.apiFailureError(StoryViewActivity.this,t);
                //Toast.makeText(ImageZoomViewActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNext() {

        if (currentPosition < storyResult.getStories().size() - 1) {
            currentPosition = currentPosition + 1;
            changeUi(storyResult.getStories().get(currentPosition));
        }

    }

    @Override
    public void onPrev() {


        if (currentPosition > 0) {
            currentPosition = currentPosition - 1;
            changeUi(storyResult.getStories().get(currentPosition));
        } else {
            if (selectedPosition > 0) {
                selectedPosition = selectedPosition - 1;
                storyResult = allStoryList.get(selectedPosition);
                setUi(storyResult.getStories().size() - 1);
            } else {
                finish();
            }
        }
    }

    @Override
    public void onComplete() {


        if (selectedPosition < allStoryList.size() - 1) {
            selectedPosition = selectedPosition + 1;
            storyResult = allStoryList.get(selectedPosition);
            setUi(0);

        } else {
            finish();
        }

    }

    void onNextClick() {
        if (currentPosition < storyResult.getStories().size() - 1) {
            progressView.skip();
        } else {
            onComplete();
        }

    }

    void onPreviousClick() {
        if (currentPosition > 0) {
            progressView.reverse();
        } else {
            onPrev();
        }
    }

    private final AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                if (exoPlayer != null) {
                    exoPlayer.pause();
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if (exoPlayer != null) {
                    exoPlayer.play();
                }
            }
        }
    };

    @Override
    protected void onPause() {
        if (exoPlayer != null) {
            positionVideo = exoPlayer.getCurrentPosition();
            exoPlayer.pause();
        }


        super.onPause();
    }

    @Override
    protected void onResume() {
        if (exoPlayer != null) {
            exoPlayer.seekTo(positionVideo);
            exoPlayer.play();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        progressView.destroy();
        if (exoPlayer != null) {
            exoPlayer.pause();
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
        //if (am.isMusicActive()) {
        am.abandonAudioFocus(focusChangeListener);
        // }
        super.onDestroy();
    }
}