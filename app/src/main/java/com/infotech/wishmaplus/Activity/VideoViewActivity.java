package com.infotech.wishmaplus.Activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Adapter.Interfaces.CountChangeCallBack;
import com.infotech.wishmaplus.Api.Object.ContentResult;
import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.Fragments.ShareDialogFragment;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.ApplicationConstant;
import com.infotech.wishmaplus.Utils.CommentDialog;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.Utility;
import com.infotech.wishmaplus.Utils.VideoEdit.AutoPlayVideo.DownloadManagerService;
import com.infotech.wishmaplus.Utils.VideoEdit.AutoPlayVideo.VideoUtils;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoViewActivity extends AppCompatActivity {

    /*private ImageView playBtn;*/
    private ExoPlayer exoPlayer;
    public static CountChangeCallBack callBack;
    private long positionVideo;
    private int position;
    private ContentResult videoData;
    private PreferencesManager tokenManager;
    private String userId;
    private AudioManager am;


    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.videoV), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        position = getIntent().getIntExtra("Position", -1);
        videoData = getIntent().getParcelableExtra("VideoData");
        tokenManager = new PreferencesManager(this,1);
        CommentDialog commentDialog = new CommentDialog(this, tokenManager);
        userId = tokenManager.getUserId();

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        String videoPath = videoData.getPostContent();
        String savePath = VideoUtils.getString(this, videoPath);
        if (savePath != null && new File(savePath).exists()) {
            videoPath = savePath;
        } else {
            Intent serviceIntent = new Intent(this, DownloadManagerService.class);
            startService(serviceIntent);
            new VideoUtils().startDownloadInBackground(this, videoPath, getExternalCacheDir() + "/MyVideo/");
        }

        /*  playBtn = findViewById(R.id.playBtn);*/
        PlayerView videoView = findViewById(R.id.videoView);
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
        Uri uri = Uri.parse(videoPath);
        MediaItem mediaItem = MediaItem.fromUri(uri);

        // Prepare the player with the media item
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();
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
        ImageView profile = findViewById(R.id.profile);
        TextView nameTv = findViewById(R.id.nameTv);
        TextView timeTv = findViewById(R.id.timeTv);
        TextView postTxt = findViewById(R.id.postTxt);
        ImageButton likeBtn = findViewById(R.id.likeBtn);
        ImageButton commentBtn = findViewById(R.id.commentBtn);
        ImageButton shareBtn = findViewById(R.id.shareBtn);
        ImageButton whatsAppBtn = findViewById(R.id.whatsAppBtn);
        TextView likeBtnTv = findViewById(R.id.likeBtnTv);
        TextView commentBtnTv = findViewById(R.id.commentBtnTv);
        TextView shareBtnTv = findViewById(R.id.shareBtnTv);
        TextView whatsappBtnTv = findViewById(R.id.whatsappBtnTv);
        ViewGroup.MarginLayoutParams paramsPost = (ViewGroup.MarginLayoutParams) postTxt.getLayoutParams();
        ViewGroup.MarginLayoutParams paramsImage = (ViewGroup.MarginLayoutParams) profile.getLayoutParams();
        ViewGroup.MarginLayoutParams paramsWhatsappTv = (ViewGroup.MarginLayoutParams) whatsappBtnTv.getLayoutParams();


        videoView.setControllerVisibilityListener((PlayerView.ControllerVisibilityListener) visibility -> {
            if (visibility == View.VISIBLE) {
                if (postTxt.getVisibility() == View.VISIBLE) {
                    paramsPost.bottomMargin = (int) getResources().getDimension(com.intuit.sdp.R.dimen._65sdp);

                    postTxt.setLayoutParams(paramsPost);
                } else {
                    paramsImage.bottomMargin = (int) getResources().getDimension(com.intuit.sdp.R.dimen._65sdp);

                    profile.setLayoutParams(paramsImage);
                }
                paramsWhatsappTv.bottomMargin = (int) getResources().getDimension(com.intuit.sdp.R.dimen._65sdp);

                whatsappBtnTv.setLayoutParams(paramsWhatsappTv);
            } else {
                if (postTxt.getVisibility() == View.VISIBLE) {
                    paramsPost.bottomMargin = (int) getResources().getDimension(com.intuit.sdp.R.dimen._20sdp);

                    postTxt.setLayoutParams(paramsPost);
                } else {
                    paramsImage.bottomMargin = (int) getResources().getDimension(com.intuit.sdp.R.dimen._20sdp);

                    profile.setLayoutParams(paramsImage);
                }
                paramsWhatsappTv.bottomMargin = (int) getResources().getDimension(com.intuit.sdp.R.dimen._20sdp);

                whatsappBtnTv.setLayoutParams(paramsWhatsappTv);
            }
        });
       /* videoView.setOnClickListener(view -> {

        });*/

        findViewById(R.id.back_button).setOnClickListener(view -> {
            finish();
        });


        Glide.with(this)
                .load(videoData.getProfilePictureUrl())
                .apply(UtilMethods.INSTANCE.getRequestOption_With_UserIcon())
                .into(profile);
        nameTv.setText(videoData.getFisrtName() + " " + videoData.getLastName());
        timeTv.setText(UtilMethods.INSTANCE.covertTimeToText(videoData.getEntryAt()));
        if (videoData.getCaption() != null && !videoData.getCaption().trim().isEmpty()) {
            postTxt.setVisibility(View.VISIBLE);
            postTxt.setText(videoData.getCaption().trim());
        } else {
            postTxt.setVisibility(View.GONE);
        }

        if (videoData.getTotalLikes() > 0) {
            likeBtnTv.setVisibility(View.VISIBLE);
            likeBtnTv.setText(videoData.getTotalLikes() + "");
        } else {
            likeBtnTv.setVisibility(View.GONE);
        }
        if (videoData.getTotalComments() > 0) {
            commentBtnTv.setVisibility(View.VISIBLE);
            commentBtnTv.setText(videoData.getTotalComments() + "");
        } else {
            commentBtnTv.setVisibility(View.GONE);
        }
        if (videoData.getTotalShares() > 0) {
            shareBtnTv.setVisibility(View.VISIBLE);
            shareBtnTv.setText(videoData.getTotalShares() + "");
        } else {
            shareBtnTv.setVisibility(View.GONE);
        }

        if (videoData.isLiked()) {


            likeBtn.setImageTintList(ContextCompat.getColorStateList(this, R.color.colorFwd));
            likeBtnTv.setTextColor(ContextCompat.getColor(this, R.color.colorFwd));
        } else {
            likeBtn.setImageTintList(ContextCompat.getColorStateList(this, android.R.color.white));
            likeBtnTv.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }

        commentBtn.setOnClickListener(v -> commentDialog.showCommentsDialog(videoData.getPostId(), size -> {
            if (size > 0) {
                commentBtnTv.setVisibility(View.VISIBLE);
                commentBtnTv.setText(size + "");
            } else {
                commentBtnTv.setVisibility(View.GONE);
            }
            if (callBack != null) {
                callBack.onChangeCallBack(null, null, size, -1, -1, position, -1);
            }

        }/*position, comment_count*/));

        commentBtnTv.setOnClickListener(v -> commentDialog.showCommentsDialog(videoData.getPostId(), size -> {
            if (size > 0) {
                commentBtnTv.setVisibility(View.VISIBLE);
                commentBtnTv.setText(size + "");
            } else {
                commentBtnTv.setVisibility(View.GONE);
            }
            if (callBack != null) {
                callBack.onChangeCallBack(null, null, size, -1, -1, position, -1);
            }

        }/*position, comment_count*/));


        likeBtn.setOnClickListener(v -> {
            UtilMethods.INSTANCE.triggerLikeApi(this, videoData.getPostId(), "" /*,!content.isLiked(), likeBtn, like_count, position*/, new UtilMethods.ApiCallBackMulti() {
                @Override
                public void onSuccess(Object object) {
                    boolean isLiked = (boolean) object;
                    //    updateLikeState(/*content,*/ !content.isLiked(), position, likeBtn, like_count);
                    videoData.setLiked(isLiked);


                    int newLikesCount = isLiked ? videoData.getTotalLikes() + 1 : videoData.getTotalLikes() - 1;
                    videoData.setTotalLikes(newLikesCount);


                    if (callBack != null) {
                        callBack.onChangeCallBack(null, isLiked, -1, newLikesCount, -1, position, -1);
                    }
                    if (newLikesCount > 0) {
                        likeBtnTv.setVisibility(View.VISIBLE);
                        likeBtnTv.setText(videoData.getTotalLikes() + "");
                    } else {
                        likeBtnTv.setVisibility(View.GONE);
                    }
                    if (isLiked) {
                        likeBtn.setImageTintList(ContextCompat.getColorStateList(VideoViewActivity.this, R.color.colorFwd));
                        likeBtnTv.setTextColor(ContextCompat.getColor(VideoViewActivity.this, R.color.colorFwd));
                    } else {
                        likeBtn.setImageTintList(ContextCompat.getColorStateList(VideoViewActivity.this, android.R.color.white));
                        likeBtnTv.setTextColor(ContextCompat.getColor(VideoViewActivity.this, android.R.color.white));
                    }
                }

                @Override
                public void onError(String msg) {

                }
            });
        });

        likeBtnTv.setOnClickListener(v -> {
            UtilMethods.INSTANCE.triggerLikeApi(this, videoData.getPostId(), "" /*,!content.isLiked(), likeBtn, like_count, position*/, new UtilMethods.ApiCallBackMulti() {
                @Override
                public void onSuccess(Object object) {
                    boolean isLiked = (boolean) object;
                    //    updateLikeState(/*content,*/ !content.isLiked(), position, likeBtn, like_count);
                    videoData.setLiked(isLiked);


                    int newLikesCount = isLiked ? videoData.getTotalLikes() + 1 : videoData.getTotalLikes() - 1;
                    videoData.setTotalLikes(newLikesCount);


                    if (callBack != null) {
                        callBack.onChangeCallBack(null, isLiked, -1, newLikesCount, -1, position, -1);
                    }
                    if (newLikesCount > 0) {
                        likeBtnTv.setVisibility(View.VISIBLE);
                        likeBtnTv.setText(videoData.getTotalLikes() + "");
                    } else {
                        likeBtnTv.setVisibility(View.GONE);
                    }
                    if (isLiked) {
                        likeBtn.setImageTintList(ContextCompat.getColorStateList(VideoViewActivity.this, R.color.colorFwd));
                        likeBtnTv.setTextColor(ContextCompat.getColor(VideoViewActivity.this, R.color.colorFwd));
                    } else {
                        likeBtn.setImageTintList(ContextCompat.getColorStateList(VideoViewActivity.this, android.R.color.white));
                        likeBtnTv.setTextColor(ContextCompat.getColor(VideoViewActivity.this, android.R.color.white));
                    }
                }

                @Override
                public void onError(String msg) {

                }
            });
        });

        findViewById(R.id.moreBTn).setOnClickListener(view -> showPopupMenu(view, videoData, position));
        shareBtn.setOnClickListener(view -> {
            ShareDialogFragment bottomSheetDialogFragment = ShareDialogFragment.newInstance(videoData, typeId -> {
                if(callBack!=null){
                    finish();
                    callBack.onRefresh(typeId);
                }
            });
            bottomSheetDialogFragment.show(getSupportFragmentManager(), "ShareBottomSheetDialog");


        });
        shareBtnTv.setOnClickListener(view -> {
            ShareDialogFragment bottomSheetDialogFragment = ShareDialogFragment.newInstance(videoData, typeId -> {
                if(callBack!=null){
                    finish();
                    callBack.onRefresh(typeId);
                }
            });
            bottomSheetDialogFragment.show(getSupportFragmentManager(), "ShareBottomSheetDialog");


        });

        whatsAppBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://api.whatsapp.com/send?text="+ ApplicationConstant.INSTANCE.postUrl+videoData.getPostId()));
            startActivity(intent);
        });
        whatsappBtnTv.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://api.whatsapp.com/send?text="+ ApplicationConstant.INSTANCE.postUrl+videoData.getPostId()));
            startActivity(intent);
        });
    }


    private void showPopupMenu(View view, ContentResult content, int position) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.dialog_post_popup, null);

        // Initialize the PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView, (int) getResources().getDimension(com.intuit.sdp.R.dimen._160sdp), ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Set up views in popup layout
        TextView edit = popupView.findViewById(R.id.edit);
        TextView delete = popupView.findViewById(R.id.delete);
        TextView report = popupView.findViewById(R.id.report);
        TextView copyLink = popupView.findViewById(R.id.copyLink);
        View editLine = popupView.findViewById(R.id.editLine);
        View deleteLine = popupView.findViewById(R.id.deleteLine);
        View copyLinkLine = popupView.findViewById(R.id.copyLinkLine);

        if (content.getUserId().equalsIgnoreCase(userId)|| content.getParsedSharedData()!=null && content.getParsedSharedData().getUserId().equalsIgnoreCase(userId)) {
            report.setVisibility(View.GONE);
            copyLinkLine.setVisibility(View.GONE);
        } else {
            edit.setVisibility(View.GONE);
            editLine.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            deleteLine.setVisibility(View.GONE);
        }
        edit.setOnClickListener(v -> {
            popupWindow.dismiss();
            if (callBack != null) {
                finish();
                callBack.onChangeCallBack(content.getPostId(), null, -1, -1, -1, position, -1);
            }
        });
        delete.setOnClickListener(v -> {
            popupWindow.dismiss();
            showDeleteConfirmationDialog(content, position);
        });
        report.setOnClickListener(v -> {
            popupWindow.dismiss();
            UtilMethods.INSTANCE.openReportBottomSheetDialog(VideoViewActivity.this,content.getPostId());
        });
        copyLink.setOnClickListener(v -> {
            popupWindow.dismiss();
            Utility.INSTANCE.setClipboard(this,ApplicationConstant.INSTANCE.postUrl+content.getPostId(),"Copy Link");
        });
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAsDropDown(view,  0, 0,Gravity.BOTTOM);
    }

    private void showDeleteConfirmationDialog(ContentResult content, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Content")
                .setMessage("Are you sure you want to delete this content?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteContentFromServer(content.getPostId(), position);  // Call API to delete content
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteContentFromServer(String postId, int position) {
        EndPointInterface apiService = ApiClient.getClient().create(EndPointInterface.class);
        Call<BasicResponse> call = apiService.deleteComment("Bearer " + tokenManager.getAccessToken(), postId);

        call.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(@NonNull Call<BasicResponse> call, @NonNull Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    // Content deleted successfully, remove it from the list and notify adapter


                   /* if (playerView != null) {
                        mRecyclerView.deleteVideo(playerView);
                    }
                    if (clickCallBack != null) {
                        clickCallBack.onDelete(position);
                    }*/
                    if (callBack != null) {
                        finish();
                        callBack.onChangeCallBack(null, null, -1, -1, -1, position, position);
                    }
                    Toast.makeText(VideoViewActivity.this, "Content deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VideoViewActivity.this, "Failed to delete content", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BasicResponse> call, @NonNull Throwable t) {
                Toast.makeText(VideoViewActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        if (exoPlayer != null) {
            exoPlayer.pause();
            exoPlayer.release();
            exoPlayer = null;
        }
        //if (am.isMusicActive()) {
        am.abandonAudioFocus(focusChangeListener);
       // }
        super.onDestroy();
    }


}