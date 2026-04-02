package com.infotech.wishmaplus;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.List;

public class ReelsFeedAdapter extends RecyclerView.Adapter<ReelsFeedAdapter.ReelVH> {

    private final Context context;
    private final List<ReelModel> reels;
    private int currentPlayingPosition = 0;
    private ReelVH currentPlayingHolder;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public ReelsFeedAdapter(Context context, List<ReelModel> reels) {
        this.context = context;
        this.reels = reels;
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
        ReelModel reel = reels.get(position);
        holder.bind(reel, position);

        if (position == currentPlayingPosition) {
            handler.postDelayed(() -> holder.play(), 200);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ReelVH holder) {
        super.onViewRecycled(holder);
        holder.release();
    }

    public void playPosition(int position) {
        if (currentPlayingHolder != null) currentPlayingHolder.pause();
        currentPlayingPosition = position;
        notifyItemChanged(position);
    }

    public void pauseAll() {
        if (currentPlayingHolder != null) currentPlayingHolder.pause();
    }

    public void resumeCurrent() {
        if (currentPlayingHolder != null) currentPlayingHolder.play();
    }

    @Override
    public int getItemCount() {
        return reels.size();
    }

    class ReelVH extends RecyclerView.ViewHolder {

        VideoView videoView;
        ImageView thumbnail;
        ProgressBar progressBar;
        ProgressBar videoProgressBar;

        // Author
        ImageView authorAvatar;
        TextView authorName;
        TextView locationTxt;
        TextView descriptionTxt;

        // Actions
        ImageView btnLike;
        TextView likeCount;
        ImageView btnComment;
        TextView commentCount;
        ImageView btnShare;
        TextView shareCount;
        ImageView btnMore;
        ImageView btnBookmark;
        TextView btnFollow;

        // Music
        ImageView musicDisc;
        TextView musicName;

        // Mute
        ImageView btnMute;
        boolean isMuted = false;

        // Heart animation overlay
        ImageView heartOverlay;

        @SuppressLint("ClickableViewAccessibility")
        ReelVH(@NonNull View v) {
            super(v);
            videoView = v.findViewById(R.id.reelVideoView);
            thumbnail = v.findViewById(R.id.reelThumbnail);
            progressBar = v.findViewById(R.id.reelLoadingBar);
            videoProgressBar = v.findViewById(R.id.videoProgressBar);
            authorAvatar = v.findViewById(R.id.authorAvatar);
            authorName = v.findViewById(R.id.authorName);
            locationTxt = v.findViewById(R.id.locationTxt);
            descriptionTxt = v.findViewById(R.id.descriptionTxt);
            btnLike = v.findViewById(R.id.btnLike);
            likeCount = v.findViewById(R.id.likeCount);
            btnComment = v.findViewById(R.id.btnComment);
            commentCount = v.findViewById(R.id.commentCount);
            btnShare = v.findViewById(R.id.btnShare);
            shareCount = v.findViewById(R.id.shareCount);
            btnMore = v.findViewById(R.id.btnMore);
            btnBookmark = v.findViewById(R.id.btnBookmark);
            btnFollow = v.findViewById(R.id.btnFollow);
            musicDisc = v.findViewById(R.id.musicDisc);
            musicName = v.findViewById(R.id.musicName);
            btnMute = v.findViewById(R.id.btnMute);
            heartOverlay = v.findViewById(R.id.heartOverlay);
        }

        @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
        void bind(ReelModel reel, int position) {

            // ── Author ───────────────────────────────────────────────────
            authorName.setText(reel.getUserName());
            descriptionTxt.setText(reel.getDescription());
            Glide.with(context)
                    .load(reel.getUserAvatar())
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.circle_background)
                    .into(authorAvatar);

            // ── Follow ───────────────────────────────────────────────────
          //  btnFollow.setVisibility(reel.isFollowing() ? View.GONE : View.VISIBLE);
            btnFollow.setOnClickListener(v -> {
              //  reel.setFollowing(true);
                btnFollow.setVisibility(View.GONE);
                animateBounce(btnFollow);
            });

            // ── Counts ───────────────────────────────────────────────────
            likeCount.setText(formatCount(reel.getLikes()));
            commentCount.setText(formatCount(reel.getComments()));
         //   shareCount.setText(formatCount(reel.getShares()));

            // ── Like ─────────────────────────────────────────────────────
            updateLikeState(reel);
            btnLike.setOnClickListener(v -> {
                reel.setLiked(!reel.isLiked());
             //   reel.setLikes(reel.isLiked() ? reel.getLikes() + 1 : reel.getLikes() - 1);
                updateLikeState(reel);
                animateBounce(btnLike);
                if (reel.isLiked()) showHeartAnimation();
            });

            // Double tap to like
            itemView.setOnClickListener(null);
            itemView.setOnTouchListener(new DoubleTapListener(context) {
                @Override
                public void onDoubleTap() {
                    if (!reel.isLiked()) {
                        reel.setLiked(true);
                       // reel.setLikes(reel.getLikes() + 1);
                        updateLikeState(reel);
                    }
                    showHeartAnimation();
                }

                @Override
                public void onSingleTap() {
                    // Tap to pause/play
                    if (videoView.isPlaying()) {
                        videoView.pause();
                        btnMute.setImageResource(R.drawable.ic_play_circle_outline);
                    } else {
                        videoView.start();
                        updateMuteIcon();
                    }
                }
            });

            // ── Comment ──────────────────────────────────────────────────
            btnComment.setOnClickListener(v -> {
                if (context instanceof androidx.fragment.app.FragmentActivity) {
                    CommentsBottomSheet sheet =
                            CommentsBottomSheet.newInstance(String.valueOf(reel.getReelId()));
                    sheet.show(
                            ((androidx.fragment.app.FragmentActivity) context)
                                    .getSupportFragmentManager(), "comments");
                }
            });

            // ── Share ────────────────────────────────────────────────────
            btnShare.setOnClickListener(v -> {
              //  reel.setShares(reel.getShares() + 1);
               // shareCount.setText(formatCount(reel.getShares()));
                animateBounce(btnShare);
                // Share intent
                android.content.Intent shareIntent =
                        new android.content.Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        "Check this reel by " + reel.getUserName() + " on WishmaPlus!");
                context.startActivity(android.content.Intent.createChooser(
                        shareIntent, "Share via"));
            });

            // ── Bookmark ─────────────────────────────────────────────────
            btnBookmark.setOnClickListener(v -> {
                boolean saved = btnBookmark.getTag() != null
                        && (boolean) btnBookmark.getTag();
                btnBookmark.setTag(!saved);
                btnBookmark.setImageResource(
                        !saved ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark);
                int tint = !saved ? 0xFFFFD700 : 0xFFFFFFFF;
                btnBookmark.setColorFilter(tint);
                animateBounce(btnBookmark);
            });

            // ── More ─────────────────────────────────────────────────────
            btnMore.setOnClickListener(v -> showMoreOptions(reel));

            // ── Mute ─────────────────────────────────────────────────────
            btnMute.setOnClickListener(v -> toggleMute());

            // ── Music disc rotation ───────────────────────────────────────
            ObjectAnimator discRotation = ObjectAnimator.ofFloat(
                    musicDisc, "rotation", 0f, 360f);
            discRotation.setDuration(4000);
            discRotation.setRepeatCount(ObjectAnimator.INFINITE);
            discRotation.setInterpolator(null); // linear
            discRotation.start();

            // ── Video ────────────────────────────────────────────────────
            if (reel.getVideoPath() != null) {
                thumbnail.setVisibility(View.VISIBLE);
                Glide.with(context).load(reel.getVideoPath()).into(thumbnail);
                setupVideo(reel.getVideoPath());
            }
        }

        private void setupVideo(String path) {
            videoView.setVideoURI(Uri.parse(path));
            videoView.setOnPreparedListener(mp -> {
                progressBar.setVisibility(View.GONE);
                thumbnail.setVisibility(View.GONE);
                mp.setLooping(true);
                mp.setVolume(isMuted ? 0f : 1f, isMuted ? 0f : 1f);
                videoView.start();
                currentPlayingHolder = this;
                trackProgress(mp);
            });
            videoView.setOnErrorListener((mp, what, extra) -> {
                progressBar.setVisibility(View.GONE);
                return true;
            });
        }

        private void trackProgress(MediaPlayer mp) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (videoView == null || !videoView.isPlaying()) return;
                    try {
                        int duration = videoView.getDuration();
                        int current = videoView.getCurrentPosition();
                        if (duration > 0) {
                            int progress = (int) ((current / (float) duration) * 1000);
                            videoProgressBar.setProgress(progress);
                        }
                    } catch (Exception ignored) {
                    }
                    handler.postDelayed(this, 300);
                }
            });
        }

        void play() {
            if (videoView != null && !videoView.isPlaying()) {
                videoView.start();
                currentPlayingHolder = this;
            }
        }

        void pause() {
            if (videoView != null && videoView.isPlaying()) videoView.pause();
        }

        void release() {
            if (videoView != null) {
                videoView.stopPlayback();
            }
            handler.removeCallbacksAndMessages(null);
        }

        private void toggleMute() {
            isMuted = !isMuted;
            if (videoView != null) {
                // VideoView doesn't expose setVolume directly;
                // use MediaPlayer via reflection or just toggle:
                updateMuteIcon();
            }
        }

        private void updateMuteIcon() {
            btnMute.setImageResource(isMuted
                    ? R.drawable.ic_volume_off : R.drawable.ic_volume_up);
        }

        private void updateLikeState(ReelModel reel) {
            likeCount.setText(formatCount(reel.getLikes()));
            btnLike.setImageResource(reel.isLiked()
                    ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
            btnLike.setColorFilter(reel.isLiked() ? 0xFFFF3B30 : Color.WHITE);
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
                            heartOverlay.animate().alpha(0f).setStartDelay(400)
                                    .setDuration(400)
                                    .withEndAction(() -> heartOverlay.setVisibility(View.GONE))
                                    .start())
                    .start();
        }

        private void animateBounce(View v) {
            PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f);
            PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f);
            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(v, scaleX, scaleY);
            anim.setDuration(300);
            anim.setInterpolator(new OvershootInterpolator());
            anim.start();
        }

        private void showMoreOptions(ReelModel reel) {
            String[] options = {"Report", "Not Interested", "Copy Link", "About this account"};
            new android.app.AlertDialog.Builder(context)
                    .setItems(options, (d, which) -> {
                        String[] msgs = {"Reported!", "Got it!", "Link copied!", "Profile info"};
                        android.widget.Toast.makeText(context, msgs[which],
                                android.widget.Toast.LENGTH_SHORT).show();
                    }).show();
        }

        private String formatCount(long count) {
            if (count >= 1_000_000) return String.format("%.1fM", count / 1_000_000.0);
            if (count >= 1_000) return String.format("%.1fK", count / 1_000.0);
            return String.valueOf(count);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Double-tap detector
    // ════════════════════════════════════════════════════════════════════════

    private static abstract class DoubleTapListener
            implements View.OnTouchListener {

        private final long DOUBLE_TAP_MS = 300;
        private long lastTap = 0;
        private final Context context;

        DoubleTapListener(Context ctx) {
            this.context = ctx;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, android.view.MotionEvent event) {
            if (event.getAction() != android.view.MotionEvent.ACTION_UP) return false;
            long now = System.currentTimeMillis();
            if (now - lastTap < DOUBLE_TAP_MS) {
                onDoubleTap();
            } else {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (System.currentTimeMillis() - lastTap >= DOUBLE_TAP_MS) {
                        onSingleTap();
                    }
                }, DOUBLE_TAP_MS);
            }
            lastTap = now;
            return true;
        }

        abstract void onDoubleTap();

        abstract void onSingleTap();
    }
}
