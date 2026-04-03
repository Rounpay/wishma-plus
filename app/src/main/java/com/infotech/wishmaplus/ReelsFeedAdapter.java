package com.infotech.wishmaplus;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.fragment.app.FragmentActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.reels.ReelWatchTracker.ReelWatchTracker;
import com.infotech.wishmaplus.reels.reels_comments.CommentsBottomSheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReelsFeedAdapter extends RecyclerView.Adapter<ReelsFeedAdapter.ReelVH> {

    private final Context context;
    private final List<ReelModel> reels;
    public int currentPlayingPosition = -1;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isMuted = false;
    private CustomLoader customLoader;
    private final SparseArray<ExoPlayer> playerPool = new SparseArray<>();

    public ReelsFeedAdapter(Context context, List<ReelModel> reels, CustomLoader loader) {
        this.context = context;
        this.reels = reels;
        this.customLoader = loader;
    }

    @NonNull
    @Override
    public ReelVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_reel_feed, parent, false);
        return new ReelVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReelVH holder, int position) {
        holder.bind(reels.get(position), position);
    }

    @Override
    public void onViewRecycled(@NonNull ReelVH holder) {
        super.onViewRecycled(holder);
        holder.releasePlayer();
    }


    //  FIX #3: Resume player when view re-attaches (e.g. after bottom sheet closes)
    @Override
    public void onViewDetachedFromWindow(@NonNull ReelVH holder) {
        super.onViewDetachedFromWindow(holder);
        // ★ Detach hone par explicitly pause + playWhenReady false
        holder.pausePlayer();
        int pos = holder.getBindingAdapterPosition();
        if (pos >= 0) {
            ExoPlayer p = playerPool.get(pos);
            if (p != null) {
                p.setPlayWhenReady(false); // ★ KEY FIX
            }
        }
    }

    public void playPosition(int position) {
        try {
            ReelWatchTracker.getInstance().onReelScrolledAway();

            // ★ Sab players explicitly pause + playWhenReady false karo
            for (int i = 0; i < playerPool.size(); i++) {
                ExoPlayer p = playerPool.valueAt(i);
                if (p != null && playerPool.keyAt(i) != position) {
                    p.setPlayWhenReady(false); // ★ KEY FIX
                    p.pause();
                }
            }

            currentPlayingPosition = position;

            // Current position ka player play karo
            ExoPlayer current = playerPool.get(position);
            if (current != null
                    && current.getPlaybackState() != Player.STATE_ENDED) {
                current.setPlayWhenReady(true);
                current.play();
            } else {
                // Player ready nahi — notifyItemChanged se fresh bind
                notifyItemChanged(position);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  FIX #2: Public method to refresh a single item (e.g. after comment count changes)
    public void refreshItem(int position) {
        if (position >= 0 && position < reels.size()) {
            notifyItemChanged(position);
        }
    }

    public void pauseAll() {
        for (int i = 0; i < playerPool.size(); i++) {
            try {
                ExoPlayer p = playerPool.valueAt(i);
                if (p != null && p.isPlaying()) {
                    p.pause();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Handler callbacks bhi rok do
        handler.removeCallbacksAndMessages(null);
    }

    public void resumeCurrent() {
        if (currentPlayingPosition < 0) return;
        ExoPlayer p = playerPool.get(currentPlayingPosition);
        if (p != null
                && p.getPlaybackState() != Player.STATE_ENDED
                && !p.isPlaying()) {
            p.play();
        }
    }

    public void releaseAll() {
        handler.removeCallbacksAndMessages(null);
        for (int i = 0; i < playerPool.size(); i++) {
            try {
                ExoPlayer p = playerPool.valueAt(i);
                if (p != null) {
                    p.stop();
                    p.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        playerPool.clear();
        currentPlayingPosition = -1;
    }

    @Override
    public int getItemCount() {
        return reels.size();
    }

    // ══════════════════════════════════════════════════════════════════════
    public class ReelVH extends RecyclerView.ViewHolder {

        PlayerView playerView;
        ImageView thumbnail, playPauseOverlay, heartOverlay;
        ImageView btnLike, btnComment, btnShare, btnBookmark, btnMore, btnMute;
        ImageView authorAvatar, btnSeekBack, btnSeekForward;
        FrameLayout touchLayer, seekBackZone, seekForwardZone;
        TextView authorName, descriptionTxt, btnFollow;
        TextView likeCount, commentCount, musicName;
        TextView tvCurrentTime, tvTotalTime;
        ImageView musicDisc;
        ProgressBar progress;
        SeekBar videoSeekBar;

        private ExoPlayer exoPlayer;
        private boolean isViewCounted = false;
        private long videoStartTime = 0;
        private boolean isUserSeeking = false;
        private ObjectAnimator discAnimator = null;

        //  FIX #1: Track ended state so replay UI survives re-attach
        private boolean isEnded = false;

        // Double-tap state
        private static final long DOUBLE_TAP_MS = 280;
        private long lastTapTime = 0;
        private Runnable singleTapRunnable = null;
        private Runnable dismissCallback;

        public void setOnDismissCallback(Runnable callback) {
            this.dismissCallback = callback;
        }

        ReelVH(@NonNull View v) {
            super(v);
            playerView = v.findViewById(R.id.reelPlayerView);
            thumbnail = v.findViewById(R.id.reelThumbnail);
            progress = v.findViewById(R.id.reelLoadingBar);
            videoSeekBar = v.findViewById(R.id.videoSeekBar);
            playPauseOverlay = v.findViewById(R.id.playPauseOverlay);
            heartOverlay = v.findViewById(R.id.heartOverlay);
            touchLayer = v.findViewById(R.id.touchLayer);
            seekBackZone = v.findViewById(R.id.seekBackZone);
            seekForwardZone = v.findViewById(R.id.seekForwardZone);
            btnSeekBack = v.findViewById(R.id.btnSeekBack);
            btnSeekForward = v.findViewById(R.id.btnSeekForward);
            authorAvatar = v.findViewById(R.id.authorAvatar);
            authorName = v.findViewById(R.id.authorName);
            descriptionTxt = v.findViewById(R.id.descriptionTxt);
            btnFollow = v.findViewById(R.id.btnFollow);
            btnLike = v.findViewById(R.id.btnLike);
            likeCount = v.findViewById(R.id.likeCount);
            btnComment = v.findViewById(R.id.btnComment);
            commentCount = v.findViewById(R.id.commentCount);
            btnShare = v.findViewById(R.id.btnShare);
            btnBookmark = v.findViewById(R.id.btnBookmark);
            btnMore = v.findViewById(R.id.btnMore);
            btnMute = v.findViewById(R.id.btnMute);
            musicDisc = v.findViewById(R.id.musicDisc);
            musicName = v.findViewById(R.id.musicName);
            tvCurrentTime = v.findViewById(R.id.tvCurrentTime);
            tvTotalTime = v.findViewById(R.id.tvTotalTime);
        }

        @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
        void bind(ReelModel reel, int position) {
            isViewCounted = false;
            videoStartTime = 0;
            isEnded = false; //  FIX #1: reset ended state on fresh bind

            // Author
            authorName.setText(reel.getFullName());
            descriptionTxt.setText(reel.getCaption());
            Glide.with(context).load(reel.getProfilePictureUrl()).transform(new CircleCrop()).placeholder(R.drawable.circle_background).into(authorAvatar);

            // Thumbnail
            thumbnail.setVisibility(View.VISIBLE);
            Glide.with(context).load(reel.getThumbnailUrl() != null ? reel.getThumbnailUrl() : reel.getVideoUrl()).centerCrop().into(thumbnail);

            // Reset UI
            tvCurrentTime.setText("0:00");
            tvTotalTime.setText("0:00");
            videoSeekBar.setProgress(0);
            likeCount.setText(formatCount(reel.getLikeCount()));
            commentCount.setText(formatCount(reel.getCommentCount()));
            updateLikeState(reel);
            startDiscSpin();
            setupExoPlayer(reel, position);

            // ── MAIN TOUCH HANDLER ────────────────────────────────────────
            touchLayer.setOnTouchListener((v, event) -> {
                if (event.getAction() != MotionEvent.ACTION_UP) return true;

                long now = System.currentTimeMillis();
                if (now - lastTapTime < DOUBLE_TAP_MS) {
                    handler.removeCallbacks(singleTapRunnable);
                    lastTapTime = 0;
                    if (!reel.getLiked()) doLikeApi(reel);
                    showHeartAnimation();
                } else {
                    lastTapTime = now;
                    singleTapRunnable = () -> togglePlayPause();
                    handler.postDelayed(singleTapRunnable, DOUBLE_TAP_MS);
                }
                return true;
            });

            // ── Seek Back ─────────────────────────────────────────────────
            seekBackZone.setOnClickListener(v -> {
                if (singleTapRunnable != null) {
                    handler.removeCallbacks(singleTapRunnable);
                    singleTapRunnable = null;
                }
                lastTapTime = 0;
                if (exoPlayer != null) {
                    long pos = Math.max(0, exoPlayer.getCurrentPosition() - 5000);
                    exoPlayer.seekTo(pos);
                    flashSeek(btnSeekBack);
                }
            });

            // ── Seek Forward ──────────────────────────────────────────────
            seekForwardZone.setOnClickListener(v -> {
                if (singleTapRunnable != null) {
                    handler.removeCallbacks(singleTapRunnable);
                    singleTapRunnable = null;
                }
                lastTapTime = 0;
                if (exoPlayer != null) {
                    long dur = exoPlayer.getDuration();
                    long pos = exoPlayer.getCurrentPosition() + 5000;
                    if (dur > 0) pos = Math.min(pos, dur);
                    exoPlayer.seekTo(pos);
                    flashSeek(btnSeekForward);
                }
            });

            // ── SeekBar ───────────────────────────────────────────────────
            videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar sb, int prog, boolean fromUser) {
                    if (fromUser && exoPlayer != null) {
                        long dur = exoPlayer.getDuration();
                        if (dur > 0) {
                            long seekTo = (long) ((prog / 1000f) * dur);
                            exoPlayer.seekTo(seekTo);
                            tvCurrentTime.setText(formatTime(seekTo));
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar sb) {
                    isUserSeeking = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar sb) {
                    isUserSeeking = false;
                }
            });

            // ── Action buttons ────────────────────────────────────────────
            btnLike.setOnClickListener(v -> {
                animateBounce(btnLike);
                doLikeApi(reel);
            });

            btnFollow.setOnClickListener(v -> {
                btnFollow.setVisibility(View.GONE);
                animateBounce(btnFollow);
            });

            //  FIX #2: Refresh comment count after bottom sheet closes
            btnComment.setOnClickListener(v -> {
                if (context instanceof FragmentActivity) {
                    CommentsBottomSheet sheet = CommentsBottomSheet.newInstance(reel.getReelId());
                    sheet.setOnDismissListener(() -> {
                        int pos = getBindingAdapterPosition();
                        if (pos >= 0) {
                            // Optionally fetch fresh comment count from API here,
                            // or just refresh the UI with current data
                            notifyItemChanged(pos);
                        }
                    });
                    sheet.setOnCommentCountChanged((reelId, newCount) -> {
                        try {
                            // UI thread pe update karo
                            ((Activity) context).runOnUiThread(() -> {
                                try {
                                    // Reel model update karo
                                    reel.setCommentCount(newCount);

                                    // TextView directly update karo
                                    commentCount.setText(formatCount(newCount));

                                    // Animation — count change pe bounce
                                    if (newCount > 0) {
                                        animateBounce(commentCount);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    sheet.show(((FragmentActivity) context).getSupportFragmentManager(), "comments");
                }
            });

            btnShare.setOnClickListener(v -> {
                animateBounce(btnShare);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, "Check this reel by " + reel.getFullName() + " on WishmaPlus!");
                context.startActivity(Intent.createChooser(i, "Share via"));
            });

            btnBookmark.setOnClickListener(v -> {
                boolean saved = btnBookmark.getTag() != null && (boolean) btnBookmark.getTag();
                btnBookmark.setTag(!saved);
                btnBookmark.setImageResource(!saved ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark);
                btnBookmark.setColorFilter(!saved ? 0xFFFFD700 : 0xFFFFFFFF);
                animateBounce(btnBookmark);
            });

            btnMore.setOnClickListener(v -> showMoreOptions(reel));

            updateMuteIcon();
            btnMute.setOnClickListener(v -> {
                isMuted = !isMuted;
                if (exoPlayer != null) exoPlayer.setVolume(isMuted ? 0f : 1f);
                updateMuteIcon();
            });

            descriptionTxt.setOnClickListener(v -> {
                if (descriptionTxt.getMaxLines() == 2) {
                    descriptionTxt.setMaxLines(Integer.MAX_VALUE);
                    descriptionTxt.setEllipsize(null);
                } else {
                    descriptionTxt.setMaxLines(2);
                    descriptionTxt.setEllipsize(android.text.TextUtils.TruncateAt.END);
                }
            });
        }

        // ── ExoPlayer ────────────────────────────────────────────────────
        @OptIn(markerClass = UnstableApi.class)
        private void setupExoPlayer(ReelModel reel, int position) {
            // Pehle wala release karo
            ExoPlayer old = playerPool.get(position);
            if (old != null) {
                old.stop();
                old.release();
                playerPool.remove(position);
            }

            DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                    .setBufferDurationsMs(1500, 5000, 800, 1500)
                    .build();

            exoPlayer = new ExoPlayer.Builder(context)
                    .setLoadControl(loadControl)
                    .build();

            playerPool.put(position, exoPlayer);
            playerView.setPlayer(exoPlayer);
            playerView.setUseController(false);

            exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(reel.getVideoUrl())));
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
            exoPlayer.setVolume(isMuted ? 0f : 1f);
            exoPlayer.prepare();

            // ★ KEY FIX — sirf current position pe play karo
            exoPlayer.setPlayWhenReady(position == currentPlayingPosition);

            if (position == currentPlayingPosition) {
                videoStartTime = System.currentTimeMillis();
            }

            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int state) {
                    try {
                        switch (state) {
                            case Player.STATE_BUFFERING:
                                progress.setVisibility(View.VISIBLE);
                                break;

                            case Player.STATE_READY:
                                progress.setVisibility(View.GONE);
                                thumbnail.setVisibility(View.GONE);
                                long dur = exoPlayer.getDuration();
                                if (dur > 0) tvTotalTime.setText(formatTime(dur));
                                startProgressTracking();
                                if (position == currentPlayingPosition) {
                                    ReelWatchTracker.getInstance()
                                            .onReelStarted(reel.getReelId());
                                }
                                break;

                            case Player.STATE_ENDED:
                                progress.setVisibility(View.GONE);
                                isEnded = true;
                                // ★ FIX — ended pe playWhenReady false karo
                                exoPlayer.setPlayWhenReady(false);
                                showReplayState();
                                ReelWatchTracker.getInstance().forceFlush();
                                break;

                            default:
                                progress.setVisibility(View.GONE);
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    try {
                        if (isPlaying) {
                            ReelWatchTracker.getInstance().onReelResumed();
                            if (videoStartTime == 0)
                                videoStartTime = System.currentTimeMillis();
                        } else {
                            if (exoPlayer != null
                                    && exoPlayer.getPlaybackState() != Player.STATE_ENDED) {
                                ReelWatchTracker.getInstance().onReelPaused();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        // ── Replay state ─────────────────────────────────────────────────
        private void showReplayState() {
            playPauseOverlay.setOnClickListener(null);
            playPauseOverlay.setImageResource(R.drawable.pause_circle);
            playPauseOverlay.setAlpha(1f);
            playPauseOverlay.setScaleX(1f);
            playPauseOverlay.setScaleY(1f);
            playPauseOverlay.setVisibility(View.VISIBLE);
            playPauseOverlay.setClickable(true);
            playPauseOverlay.setOnClickListener(v -> {
                playPauseOverlay.setOnClickListener(null);
                playPauseOverlay.setClickable(false);
                playPauseOverlay.setVisibility(View.GONE);
                if (exoPlayer != null) {
                    isEnded = false; //  FIX #1: clear ended state on replay
                    exoPlayer.seekTo(0);
                    exoPlayer.play();
                    videoStartTime = System.currentTimeMillis();
                    isViewCounted = false;
                }
            });
        }

        // ── Progress tracking ─────────────────────────────────────────────
        private final Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null && exoPlayer.isPlaying() && !isUserSeeking) {
                    long dur = exoPlayer.getDuration();
                    long curr = exoPlayer.getCurrentPosition();
                    if (dur > 0) {
                        videoSeekBar.setProgress((int) ((curr / (float) dur) * 1000));
                        tvCurrentTime.setText(formatTime(curr));

                        if (!isViewCounted && curr >= dur * 0.8) {
                            isViewCounted = true;
                            int sec = (int) ((System.currentTimeMillis() - videoStartTime) / 1000);
                            int pos = getBindingAdapterPosition();
                            if (pos >= 0) callAddReelView(reels.get(pos).getReelId(), sec);
                        }
                    }
                }
                handler.postDelayed(this, 200);
            }
        };

        private void startProgressTracking() {
            handler.removeCallbacks(progressRunnable);
            handler.post(progressRunnable);
        }

        // ── Controls ─────────────────────────────────────────────────────
        void pausePlayer() {
            handler.removeCallbacks(progressRunnable);
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(false); // ★ KEY FIX
                exoPlayer.pause();
            }
        }

        //  FIX #3: Resume player and restore UI state on re-attach
        void resumePlayer() {
            if (exoPlayer == null) return;

            // If video had ended, show replay button instead of resuming
            if (isEnded) {
                showReplayState();
                return;
            }

            // Resume playback if not already playing
            if (exoPlayer.getPlaybackState() != Player.STATE_ENDED && !exoPlayer.isPlaying()) {
                exoPlayer.play();
                startProgressTracking();
            }
        }

        void releasePlayer() {
            handler.removeCallbacks(progressRunnable);
            if (singleTapRunnable != null) {
                handler.removeCallbacks(singleTapRunnable);
                singleTapRunnable = null;
            }
            if (discAnimator != null) discAnimator.cancel();
            if (exoPlayer != null) {
                int pos = getBindingAdapterPosition();
                if (pos >= 0) playerPool.remove(pos);
                exoPlayer.release();
                exoPlayer = null;
                playerView.setPlayer(null);
            }
        }

        private void togglePlayPause() {
            if (exoPlayer == null) return;

            if (exoPlayer.getPlaybackState() == Player.STATE_ENDED) {
                playPauseOverlay.setOnClickListener(null);
                playPauseOverlay.setClickable(false);
                playPauseOverlay.setVisibility(View.GONE);
                isEnded = false; //  FIX #1: clear ended on manual replay
                exoPlayer.seekTo(0);
                exoPlayer.play();
                videoStartTime = System.currentTimeMillis();
                isViewCounted = false;
                return;
            }

            if (exoPlayer.isPlaying()) {
                exoPlayer.pause();
                showCentreIcon(R.drawable.pause_circle, false);
            } else {
                exoPlayer.play();
                showCentreIcon(R.drawable.ic_play_circle_outline, true);
            }
        }

        private void updateMuteIcon() {
            btnMute.setImageResource(isMuted ? R.drawable.ic_volume_off : R.drawable.ic_volume_up);
        }

        private void startDiscSpin() {
            if (discAnimator != null) discAnimator.cancel();
            discAnimator = ObjectAnimator.ofFloat(musicDisc, "rotation", 0f, 360f);
            discAnimator.setDuration(4000);
            discAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            discAnimator.setInterpolator(new LinearInterpolator());
            discAnimator.start();
        }

        private void flashSeek(ImageView icon) {
            icon.animate().cancel();
            icon.setAlpha(1f);
            icon.setScaleX(1.4f);
            icon.setScaleY(1.4f);
            icon.animate().alpha(0f).scaleX(1f).scaleY(1f).setDuration(500).start();
        }

        private void showCentreIcon(int res, boolean autoHide) {
            playPauseOverlay.setOnClickListener(null);
            playPauseOverlay.setClickable(false);
            playPauseOverlay.setImageResource(res);
            playPauseOverlay.animate().cancel();
            playPauseOverlay.setAlpha(1f);
            playPauseOverlay.setScaleX(0.7f);
            playPauseOverlay.setScaleY(0.7f);
            playPauseOverlay.setVisibility(View.VISIBLE);
            playPauseOverlay.animate().scaleX(1f).scaleY(1f).setDuration(200).setInterpolator(new OvershootInterpolator()).withEndAction(() -> {
                if (autoHide) {
                    playPauseOverlay.animate().alpha(0f).setStartDelay(600).setDuration(300).withEndAction(() -> playPauseOverlay.setVisibility(View.GONE)).start();
                }
            }).start();
        }

        // ── APIs ─────────────────────────────────────────────────────────
        private void callAddReelView(int reelId, int watchSec) {
            TrackReelViewRequest session = new TrackReelViewRequest(reelId, watchSec);
            List<TrackReelViewRequest> sessions = new ArrayList<>();
            sessions.add(session);
            UtilMethods.INSTANCE.trackReelViewBatch(sessions, new UtilMethods.ApiCallBackMulti() {
                @Override
                public void onSuccess(Object r) {
                }

                @Override
                public void onError(String e) {
                }
            });
        }

        private void doLikeApi(ReelModel reel) {
            if (!(context instanceof Activity)) return;
            UtilMethods.INSTANCE.doLikeUnLikeReel((Activity) context, reel.getReelId(), new UtilMethods.ApiCallBackMulti() {
                @Override
                public void onSuccess(Object response) {
                    reel.setLiked(!reel.getLiked());
                    if (reel.getLiked()) {
                        reel.setLikeCount(reel.getLikeCount() + 1);
                        showHeartAnimation();
                    } else {
                        if (reel.getLikeCount() > 0) reel.setLikeCount(reel.getLikeCount() - 1);
                    }
                    updateLikeState(reel);
                }

                @Override
                public void onError(String e) {
                    Toast.makeText(context, e, Toast.LENGTH_SHORT).show();
                }
            });
        }

        // ── UI helpers ────────────────────────────────────────────────────
        private void updateLikeState(ReelModel reel) {
            likeCount.setText(formatCount(reel.getLikeCount()));
            btnLike.setImageResource(reel.getLiked() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
            btnLike.setColorFilter(reel.getLiked() ? 0xFFFF3B30 : Color.WHITE);
        }

        private void showHeartAnimation() {
            heartOverlay.setVisibility(View.VISIBLE);
            heartOverlay.setScaleX(0f);
            heartOverlay.setScaleY(0f);
            heartOverlay.setAlpha(1f);
            heartOverlay.animate().scaleX(1f).scaleY(1f).setDuration(300).setInterpolator(new OvershootInterpolator(1.5f)).withEndAction(() -> heartOverlay.animate().alpha(0f).setStartDelay(400).setDuration(400).withEndAction(() -> heartOverlay.setVisibility(View.GONE)).start()).start();
        }

        private void animateBounce(View v) {
            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(v, PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f), PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f));
            anim.setDuration(300);
            anim.setInterpolator(new OvershootInterpolator());
            anim.start();
        }

        private void showMoreOptions(ReelModel reel) {
            String[] options = {"Report", "Not Interested", "Copy Link", "About this account", "Delete Reel"};

            new android.app.AlertDialog.Builder(context)
                    .setItems(options, (d, which) -> {
                        switch (which) {
                            case 0:
                                Toast.makeText(context, "Reported!", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(context, "Got it!", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(context, "Link copied!", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                Toast.makeText(context, "Profile info", Toast.LENGTH_SHORT).show();
                                break;
                            case 4:
                                showDeleteConfirmDialog(reel);
                                break;
                        }
                    })
                    .show();
        }

        private void showDeleteConfirmDialog(ReelModel reel) {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Delete Reel?")
                    .setMessage("Are you want to delete this reel")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int position = getBindingAdapterPosition();
                        if (position < 0) return;
                        if (customLoader != null) customLoader.show();
                        UtilMethods.INSTANCE.deleteReel(
                                reel.getReelId(),
                                customLoader,
                                new UtilMethods.ApiCallBackMulti() {
                                    @Override
                                    public void onSuccess(Object response) {
                                        ((Activity) context).runOnUiThread(() -> {
                                            if (customLoader != null) customLoader.dismiss();
                                            releasePlayer();
                                            int pos = getBindingAdapterPosition();
                                            if (pos >= 0 && pos < reels.size()) {
                                                reels.remove(pos);
                                                notifyItemRemoved(pos);
                                                notifyItemRangeChanged(pos, reels.size());
                                                if (currentPlayingPosition == pos) {
                                                    currentPlayingPosition = -1;
                                                } else if (currentPlayingPosition > pos) {
                                                    currentPlayingPosition--;
                                                }
                                            }
                                            Toast.makeText(context,
                                                    "Reel deleted !",
                                                    Toast.LENGTH_SHORT).show();
                                        });
                                    }

                                    @Override
                                    public void onError(String error) {
                                        ((Activity) context).runOnUiThread(() -> {
                                            if (customLoader != null) customLoader.dismiss();
                                            Toast.makeText(context,
                                                    "Delete failed: " + error,
                                                    Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                }
                        );
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setCancelable(true)
                    .show();
        }

        private String formatTime(long ms) {
            long s = ms / 1000;
            return String.format(Locale.getDefault(), "%d:%02d", s / 60, s % 60);
        }

        @SuppressLint("DefaultLocale")
        private String formatCount(long c) {
            if (c >= 1_000_000) return String.format("%.1fM", c / 1_000_000.0);
            if (c >= 1_000) return String.format("%.1fK", c / 1_000.0);
            return String.valueOf(c);
        }
    }
}