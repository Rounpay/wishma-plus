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

import java.util.List;
import java.util.Locale;
public class ReelsFeedAdapter extends RecyclerView.Adapter<ReelsFeedAdapter.ReelVH> {

    private final Context context;
    private final List<ReelModel> reels;
    public int currentPlayingPosition = -1;
    private ReelVH currentPlayingHolder = null;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isMuted = false;


    // Callback to Activity for video end (NO auto-scroll, just notify)
    public interface OnVideoEndListener {
        void onVideoEnd(int currentPosition);
    }
    private OnVideoEndListener videoEndListener;
    public void setOnVideoEndListener(OnVideoEndListener l) {
        this.videoEndListener = l;
    }

    // Player pool — one player per visible item
    private final SparseArray<ExoPlayer> playerPool = new SparseArray<>();

    CustomLoader loader;

    public ReelsFeedAdapter(Context context, List<ReelModel> reels,CustomLoader loader) {
        this.context = context;
        this.reels = reels;
        this.loader = new CustomLoader(context);
    }

    @NonNull
    @Override
    public ReelVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_reel_feed, parent, false);
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

    @Override
    public void onViewDetachedFromWindow(@NonNull ReelVH holder) {
        super.onViewDetachedFromWindow(holder);
        holder.pausePlayer();
    }

    // Called from Activity scroll idle
    public void playPosition(int position) {
        // Pause all others
        for (int i = 0; i < playerPool.size(); i++) {
            if (playerPool.keyAt(i) != position) {
                playerPool.valueAt(i).pause();
            }
        }
        currentPlayingPosition = position;
        notifyItemChanged(position);
    }

    public void pauseAll() {
        for (int i = 0; i < playerPool.size(); i++) {
            playerPool.valueAt(i).pause();
        }
    }

    public void resumeCurrent() {
        if (currentPlayingPosition >= 0) {
            ExoPlayer p = playerPool.get(currentPlayingPosition);
            if (p != null && !p.isPlaying()) p.play();
        }
    }

    public void releaseAll() {
        for (int i = 0; i < playerPool.size(); i++) {
            playerPool.valueAt(i).release();
        }
        playerPool.clear();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public int getItemCount() { return reels.size(); }

    // ══════════════════════════════════════════════════════════════════════
    public class ReelVH extends RecyclerView.ViewHolder {

        PlayerView  playerView;
        ImageView   thumbnail, playPauseOverlay, heartOverlay;
        ImageView   btnLike, btnComment, btnShare, btnBookmark, btnMore, btnMute;
        ImageView   authorAvatar, btnSeekBack, btnSeekForward;
        FrameLayout seekBackZone, seekForwardZone;
        TextView    authorName, descriptionTxt, btnFollow;
        TextView    likeCount, commentCount, musicName;
        TextView    tvCurrentTime, tvTotalTime;
        ImageView   musicDisc;
        ProgressBar progress;
        SeekBar     videoSeekBar;

        private ExoPlayer exoPlayer;
        private boolean   isViewCounted  = false;
        private long      videoStartTime = 0;
        private boolean   isUserSeeking  = false;
        private ObjectAnimator discAnimator = null;

        ReelVH(@NonNull View v) {
            super(v);
            playerView       = v.findViewById(R.id.reelPlayerView);
            thumbnail        = v.findViewById(R.id.reelThumbnail);
            progress         = v.findViewById(R.id.reelLoadingBar);
            videoSeekBar     = v.findViewById(R.id.videoSeekBar);
            playPauseOverlay = v.findViewById(R.id.playPauseOverlay);
            heartOverlay     = v.findViewById(R.id.heartOverlay);
            seekBackZone     = v.findViewById(R.id.seekBackZone);
            seekForwardZone  = v.findViewById(R.id.seekForwardZone);
            btnSeekBack      = v.findViewById(R.id.btnSeekBack);
            btnSeekForward   = v.findViewById(R.id.btnSeekForward);
            authorAvatar     = v.findViewById(R.id.authorAvatar);
            authorName       = v.findViewById(R.id.authorName);
            descriptionTxt   = v.findViewById(R.id.descriptionTxt);
            btnFollow        = v.findViewById(R.id.btnFollow);
            btnLike          = v.findViewById(R.id.btnLike);
            likeCount        = v.findViewById(R.id.likeCount);
            btnComment       = v.findViewById(R.id.btnComment);
            commentCount     = v.findViewById(R.id.commentCount);
            btnShare         = v.findViewById(R.id.btnShare);
            btnBookmark      = v.findViewById(R.id.btnBookmark);
            btnMore          = v.findViewById(R.id.btnMore);
            btnMute          = v.findViewById(R.id.btnMute);
            musicDisc        = v.findViewById(R.id.musicDisc);
            musicName        = v.findViewById(R.id.musicName);
            tvCurrentTime    = v.findViewById(R.id.tvCurrentTime);
            tvTotalTime      = v.findViewById(R.id.tvTotalTime);
        }

        @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
        void bind(ReelModel reel, int position) {
            isViewCounted  = false;
            videoStartTime = 0;

            // ── Author ──────────────────────────────────────────────────
            authorName.setText(reel.getFullName());
            descriptionTxt.setText(reel.getCaption());
            Glide.with(context)
                    .load(reel.getProfilePictureUrl())
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.circle_background)
                    .into(authorAvatar);

            // ── Thumbnail ───────────────────────────────────────────────
            thumbnail.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(reel.getThumbnailUrl() != null
                            ? reel.getThumbnailUrl() : reel.getVideoUrl())
                    .centerCrop()
                    .into(thumbnail);

            // ── Reset UI ────────────────────────────────────────────────
            tvCurrentTime.setText("0:00");
            tvTotalTime.setText("0:00");
            videoSeekBar.setProgress(0);
            likeCount.setText(formatCount(reel.getLikeCount()));
            commentCount.setText(formatCount(reel.getCommentCount()));
            updateLikeState(reel);

            // ── Music disc spin (cancel old first) ───────────────────────
            startDiscSpin();

            // ── ExoPlayer ───────────────────────────────────────────────
            setupExoPlayer(reel, position);

            // ── Touch: centre tap = play/pause, double tap = like ────────
            playerView.setOnTouchListener(new DoubleTapListener(context) {
                @Override public void onDoubleTap() {
                    if (!reel.getLiked()) doLikeApi(reel);
                    showHeartAnimation();
                }
                @Override public void onSingleTap() { togglePlayPause(); }
            });

            // ── Seek Back 5s ─────────────────────────────────────────────
            seekBackZone.setOnClickListener(v -> {
                if (exoPlayer != null) {
                    long pos = Math.max(0,
                            exoPlayer.getCurrentPosition() - 5000);
                    exoPlayer.seekTo(pos);
                    flashSeek(btnSeekBack);
                }
            });

            // ── Seek Forward 5s ──────────────────────────────────────────
            seekForwardZone.setOnClickListener(v -> {
                if (exoPlayer != null) {
                    long dur = exoPlayer.getDuration();
                    long pos = exoPlayer.getCurrentPosition() + 5000;
                    if (dur > 0) pos = Math.min(pos, dur);
                    exoPlayer.seekTo(pos);
                    flashSeek(btnSeekForward);
                }
            });

            // ── SeekBar drag ─────────────────────────────────────────────
            videoSeekBar.setOnSeekBarChangeListener(
                    new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar sb, int prog,
                                                      boolean fromUser) {
                            if (fromUser && exoPlayer != null) {
                                long dur = exoPlayer.getDuration();
                                if (dur > 0) {
                                    long seekTo = (long) ((prog / 1000f) * dur);
                                    exoPlayer.seekTo(seekTo);
                                    tvCurrentTime.setText(formatTime(seekTo));
                                }
                            }
                        }
                        @Override public void onStartTrackingTouch(SeekBar sb) {
                            isUserSeeking = true;
                        }
                        @Override public void onStopTrackingTouch(SeekBar sb) {
                            isUserSeeking = false;
                        }
                    });

            // ── Like ────────────────────────────────────────────────────
            btnLike.setOnClickListener(v -> {
                animateBounce(btnLike);
                doLikeApi(reel);
            });

            // ── Follow ──────────────────────────────────────────────────
            btnFollow.setOnClickListener(v -> {
                btnFollow.setVisibility(View.GONE);
                animateBounce(btnFollow);
            });

            // ── Comment ─────────────────────────────────────────────────
            btnComment.setOnClickListener(v -> {
                if (context instanceof FragmentActivity) {
                    CommentsBottomSheet
                            .newInstance(reel.getReelId())
                            .show(((FragmentActivity) context)
                                    .getSupportFragmentManager(), "comments");
                }
            });

            // ── Share ───────────────────────────────────────────────────
            btnShare.setOnClickListener(v -> {
                animateBounce(btnShare);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,
                        "Check this reel by " + reel.getFullName()
                                + " on WishmaPlus!");
                context.startActivity(
                        Intent.createChooser(i, "Share via"));
            });

            // ── Bookmark ────────────────────────────────────────────────
            btnBookmark.setOnClickListener(v -> {
                boolean saved = btnBookmark.getTag() != null
                        && (boolean) btnBookmark.getTag();
                btnBookmark.setTag(!saved);
                btnBookmark.setImageResource(!saved
                        ? R.drawable.ic_bookmark_filled
                        : R.drawable.ic_bookmark);
                btnBookmark.setColorFilter(!saved ? 0xFFFFD700 : 0xFFFFFFFF);
                animateBounce(btnBookmark);
            });

            // ── More ────────────────────────────────────────────────────
            btnMore.setOnClickListener(v -> showMoreOptions(reel));

            // ── Mute ────────────────────────────────────────────────────
            updateMuteIcon();
            btnMute.setOnClickListener(v -> {
                isMuted = !isMuted;
                if (exoPlayer != null) exoPlayer.setVolume(isMuted ? 0f : 1f);
                updateMuteIcon();
            });
        }

        // ── ExoPlayer Setup ──────────────────────────────────────────────
        @OptIn(markerClass = UnstableApi.class)
        private void setupExoPlayer(ReelModel reel, int position) {
            // Release existing
            ExoPlayer old = playerPool.get(position);
            if (old != null) {
                old.release();
                playerPool.remove(position);
            }

            DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                            1500,   // minBuffer
                            5000,   // maxBuffer
                            800,    // playback start
                            1500)   // after rebuffer
                    .build();

            exoPlayer = new ExoPlayer.Builder(context)
                    .setLoadControl(loadControl)
                    .build();

            playerPool.put(position, exoPlayer);
            playerView.setPlayer(exoPlayer);
            playerView.setUseController(false);

            exoPlayer.setMediaItem(
                    MediaItem.fromUri(Uri.parse(reel.getVideoUrl())));

            // NO loop — video ends → notify only, user scrolls manually
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
            exoPlayer.setVolume(isMuted ? 0f : 1f);
            exoPlayer.prepare();

            if (position == currentPlayingPosition) {
                exoPlayer.setPlayWhenReady(true);
                videoStartTime = System.currentTimeMillis();
            } else {
                exoPlayer.setPlayWhenReady(false);
            }

            exoPlayer.addListener(new Player.Listener() {

                @Override
                public void onPlaybackStateChanged(int state) {
                    switch (state) {

                        case Player.STATE_BUFFERING:
                            progress.setVisibility(View.VISIBLE);
                            break;

                        case Player.STATE_READY:
                            progress.setVisibility(View.GONE);
                            thumbnail.setVisibility(View.GONE);
                            long dur = exoPlayer.getDuration();
                            if (dur > 0)
                                tvTotalTime.setText(formatTime(dur));
                            startProgressTracking();
                            break;

                        case Player.STATE_ENDED:
                            progress.setVisibility(View.GONE);
                            // ── Loop reel manually (replay icon shown) ───
                            showReplayState();
                            // Fire view API on completion
                            if (!isViewCounted) {
                                isViewCounted = true;
                                int watchSec = (int)
                                        ((System.currentTimeMillis()
                                                - videoStartTime) / 1000);
                                callAddReelView(reel.getReelId(), watchSec);
                            }
                            // Notify Activity — it can decide what to do
                            if (videoEndListener != null
                                    && position == currentPlayingPosition) {
                                videoEndListener.onVideoEnd(position);
                            }
                            break;

                        case Player.STATE_IDLE:
                        default:
                            progress.setVisibility(View.GONE);
                            break;
                    }
                }

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    if (isPlaying && videoStartTime == 0) {
                        videoStartTime = System.currentTimeMillis();
                    }
                }
            });
        }

        // ── Show replay state when video ends ────────────────────────────
        private void showReplayState() {
            // Show replay icon — tap to replay
            playPauseOverlay.setImageResource(R.drawable.replay_5_24dp);
            playPauseOverlay.setAlpha(1f);
            playPauseOverlay.setScaleX(1f);
            playPauseOverlay.setScaleY(1f);
            playPauseOverlay.setVisibility(View.VISIBLE);

            // On tap replay
            playPauseOverlay.setOnClickListener(v -> {
                playPauseOverlay.setVisibility(View.GONE);
                playPauseOverlay.setOnClickListener(null);
                if (exoPlayer != null) {
                    exoPlayer.seekTo(0);
                    exoPlayer.play();
                    videoStartTime = System.currentTimeMillis();
                    isViewCounted  = false; // reset for new watch
                }
            });
        }

        // ── Progress + view count tracking ──────────────────────────────
        private final Runnable progressRunnable = new Runnable() {
            @Override public void run() {
                if (exoPlayer != null && exoPlayer.isPlaying()
                        && !isUserSeeking) {
                    long dur  = exoPlayer.getDuration();
                    long curr = exoPlayer.getCurrentPosition();
                    if (dur > 0) {
                        videoSeekBar.setProgress(
                                (int) ((curr / (float) dur) * 1000));
                        tvCurrentTime.setText(formatTime(curr));

                        // Fire view API at 80% watch
                        if (!isViewCounted && curr >= dur * 0.8) {
                            isViewCounted = true;
                            int watchSec = (int)
                                    ((System.currentTimeMillis()
                                            - videoStartTime) / 1000);
                            int pos = getBindingAdapterPosition();
                            if (pos >= 0) {
                                callAddReelView(
                                        reels.get(pos).getReelId(),
                                        watchSec);
                            }
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

        // ── Player controls ──────────────────────────────────────────────
        void pausePlayer() {
            if (exoPlayer != null) exoPlayer.pause();
            handler.removeCallbacks(progressRunnable);
        }

        void releasePlayer() {
            handler.removeCallbacks(progressRunnable);
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

            // If ended → replay
            if (exoPlayer.getPlaybackState() == Player.STATE_ENDED) {
                exoPlayer.seekTo(0);
                exoPlayer.play();
                playPauseOverlay.setVisibility(View.GONE);
                playPauseOverlay.setOnClickListener(null);
                videoStartTime = System.currentTimeMillis();
                isViewCounted  = false;
                return;
            }

            if (exoPlayer.isPlaying()) {
                exoPlayer.pause();
                showCentreIcon(R.drawable.outline_pause_24, false);
            } else {
                exoPlayer.play();
                showCentreIcon(R.drawable.ic_play_circle_outline, true);
            }
        }

        // ── Mute icon sync ───────────────────────────────────────────────
        private void updateMuteIcon() {
            btnMute.setImageResource(isMuted
                    ? R.drawable.ic_volume_off : R.drawable.ic_volume_up);
        }

        // ── Music disc spin ──────────────────────────────────────────────
        private void startDiscSpin() {
            if (discAnimator != null) discAnimator.cancel();
            discAnimator = ObjectAnimator.ofFloat(
                    musicDisc, "rotation", 0f, 360f);
            discAnimator.setDuration(4000);
            discAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            discAnimator.setInterpolator(new LinearInterpolator());
            discAnimator.start();
        }

        // ── Seek flash ───────────────────────────────────────────────────
        private void flashSeek(ImageView icon) {
            icon.animate().cancel();
            icon.setAlpha(1f);
            icon.setScaleX(1.4f);
            icon.setScaleY(1.4f);
            icon.animate()
                    .alpha(0f).scaleX(1f).scaleY(1f)
                    .setDuration(500).start();
        }

        // ── Centre icon (play/pause/replay) ──────────────────────────────
        private void showCentreIcon(int res, boolean autoHide) {
            // Clear any replay listener first
            playPauseOverlay.setOnClickListener(null);

            playPauseOverlay.setImageResource(res);
            playPauseOverlay.animate().cancel();
            playPauseOverlay.setAlpha(1f);
            playPauseOverlay.setScaleX(0.7f);
            playPauseOverlay.setScaleY(0.7f);
            playPauseOverlay.setVisibility(View.VISIBLE);
            playPauseOverlay.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(200)
                    .setInterpolator(new OvershootInterpolator())
                    .withEndAction(() -> {
                        if (autoHide) {
                            playPauseOverlay.animate()
                                    .alpha(0f)
                                    .setStartDelay(600)
                                    .setDuration(300)
                                    .withEndAction(() ->
                                            playPauseOverlay.setVisibility(
                                                    View.GONE))
                                    .start();
                        }
                    }).start();
        }

        // ── APIs ─────────────────────────────────────────────────────────

        // POST /AddReelView?ReelId=X&WatchDurationInSec=Y
        private void callAddReelView(int reelId, int watchSec) {
            UtilMethods.INSTANCE.addReelView(
                    reelId,
                    watchSec,
                    loader, // no loader for background call
                    new UtilMethods.ApiCallBackMulti() {
                        @Override public void onSuccess(Object r) { }
                        @Override public void onError(String e) { }
                    });
        }

        // Like / Unlike
        private void doLikeApi(ReelModel reel) {
            if (!(context instanceof Activity)) return;
            UtilMethods.INSTANCE.doLikeUnLikeReel(
                    (Activity) context,
                    reel.getReelId(),
                    new UtilMethods.ApiCallBackMulti() {
                        @Override public void onSuccess(Object response) {
                            reel.setLiked(!reel.getLiked());
                            if (reel.getLiked()) {
                                reel.setLikeCount(reel.getLikeCount() + 1);
                                showHeartAnimation();
                            } else {
                                if (reel.getLikeCount() > 0)
                                    reel.setLikeCount(reel.getLikeCount() - 1);
                            }
                            updateLikeState(reel);
                        }
                        @Override public void onError(String e) {
                            Toast.makeText(context, e,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // ── UI helpers ───────────────────────────────────────────────────
        private void updateLikeState(ReelModel reel) {
            likeCount.setText(formatCount(reel.getLikeCount()));
            btnLike.setImageResource(reel.getLiked()
                    ? R.drawable.ic_favorite_filled
                    : R.drawable.ic_favorite_border);
            btnLike.setColorFilter(reel.getLiked() ? 0xFFFF3B30 : Color.WHITE);
        }

        private void showHeartAnimation() {
            heartOverlay.setVisibility(View.VISIBLE);
            heartOverlay.setScaleX(0f);
            heartOverlay.setScaleY(0f);
            heartOverlay.setAlpha(1f);
            heartOverlay.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(300)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .withEndAction(() ->
                            heartOverlay.animate().alpha(0f)
                                    .setStartDelay(400).setDuration(400)
                                    .withEndAction(() ->
                                            heartOverlay.setVisibility(View.GONE))
                                    .start())
                    .start();
        }

        private void animateBounce(View v) {
            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(v,
                    PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f),
                    PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f));
            anim.setDuration(300);
            anim.setInterpolator(new OvershootInterpolator());
            anim.start();
        }

        private void showMoreOptions(ReelModel reel) {
            new android.app.AlertDialog.Builder(context)
                    .setItems(
                            new String[]{"Report", "Not Interested",
                                    "Copy Link", "About this account"},
                            (d, w) -> Toast.makeText(context,
                                    new String[]{"Reported!", "Got it!",
                                            "Link copied!", "Profile info"}[w],
                                    Toast.LENGTH_SHORT).show())
                    .show();
        }

        private String formatTime(long ms) {
            long s = ms / 1000;
            return String.format(Locale.getDefault(),
                    "%d:%02d", s / 60, s % 60);
        }

        @SuppressLint("DefaultLocale")
        private String formatCount(long c) {
            if (c >= 1_000_000) return String.format("%.1fM", c / 1_000_000.0);
            if (c >= 1_000)     return String.format("%.1fK", c / 1_000.0);
            return String.valueOf(c);
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // Double-tap detector
    // ══════════════════════════════════════════════════════════════════════
    private static abstract class DoubleTapListener
            implements View.OnTouchListener {

        private static final long DOUBLE_TAP_MS = 280;
        private long lastTap = 0;
        private final Handler h = new Handler(Looper.getMainLooper());
        private Runnable singleRunnable;

        DoubleTapListener(Context ctx) { }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent e) {
            if (e.getAction() != MotionEvent.ACTION_UP) return false;
            long now = System.currentTimeMillis();
            if (now - lastTap < DOUBLE_TAP_MS) {
                h.removeCallbacks(singleRunnable);
                onDoubleTap();
            } else {
                singleRunnable = () -> {
                    if (System.currentTimeMillis() - lastTap >= DOUBLE_TAP_MS)
                        onSingleTap();
                };
                h.postDelayed(singleRunnable, DOUBLE_TAP_MS);
            }
            lastTap = now;
            return true;
        }

        abstract void onDoubleTap();
        abstract void onSingleTap();
    }
}