package com.infotech.wishmaplus.reels.ui.componets;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.R;

import java.util.List;

public class MusicTrackAdapter extends RecyclerView.Adapter<MusicTrackAdapter.VH> {

    public interface OnTrackClick {
        void onClick(MusicPickerBottomSheet.MusicTrack track);
    }

    private List<MusicPickerBottomSheet.MusicTrack> items;
    private final OnTrackClick listener;
    private int selectedPos = -1;

    // ── Active preview player track karo — memory leak avoid ke liye ─────────
    private MediaPlayer activePlayer = null;
    private int playingPos = -1;

    public MusicTrackAdapter(List<MusicPickerBottomSheet.MusicTrack> items, OnTrackClick l) {
        this.items = items;
        this.listener = l;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<MusicPickerBottomSheet.MusicTrack> newItems) {
        this.items = newItems;
        selectedPos = -1;
        stopActivePlayer();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music_track, parent, false);
        return new VH(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        MusicPickerBottomSheet.MusicTrack t = items.get(pos);
        h.tvTitle.setText(t.title);
        h.tvArtist.setText(t.artist + " • " + t.duration);

        // Selected highlight
        h.itemView.setAlpha(selectedPos == pos ? 1f : 0.85f);
        h.itemView.setBackgroundColor(selectedPos == pos ? 0x221877F2 : 0x00000000);

        // Preview button state
        boolean isPlaying = playingPos == pos && activePlayer != null
                && activePlayer.isPlaying();
        h.btnPreview.setImageResource(isPlaying
                ? R.drawable.outline_pause_circle_24
                : R.drawable.ic_play_circle_outline);

        // Row click — track select karo
        h.itemView.setOnClickListener(v -> {
            int old = selectedPos;
            selectedPos = h.getAdapterPosition();
            if (old >= 0) notifyItemChanged(old);
            notifyItemChanged(selectedPos);
            listener.onClick(t);
        });

        // Preview button click — inline preview
        h.btnPreview.setOnClickListener(v -> {
            int clickPos = h.getAdapterPosition();
            if (clickPos < 0) return;

            if (playingPos == clickPos && activePlayer != null
                    && activePlayer.isPlaying()) {
                // Same track — pause karo
                activePlayer.pause();
                notifyItemChanged(clickPos);
            } else {
                // Pehle wala stop karo
                stopActivePlayer();
                playingPos = clickPos;
                playPreview(t.previewUrl, clickPos);
            }
        });
    }

    // ── Inline preview — URL se seedha play (download nahi) ──────────────────
    private void playPreview(String url, int pos) {
        try {
            activePlayer = new MediaPlayer();
            activePlayer.setDataSource(url);
            activePlayer.setOnPreparedListener(mp -> {
                mp.start();
                notifyItemChanged(pos);
            });
            activePlayer.setOnCompletionListener(mp -> {
                playingPos = -1;
                notifyItemChanged(pos);
                releaseActivePlayer();
            });
            activePlayer.setOnErrorListener((mp, what, extra) -> {
                playingPos = -1;
                notifyItemChanged(pos);
                releaseActivePlayer();
                return true;
            });
            activePlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            playingPos = -1;
            releaseActivePlayer();
        }
    }

    private void stopActivePlayer() {
        if (activePlayer != null) {
            try {
                if (activePlayer.isPlaying()) activePlayer.stop();
            } catch (Exception ignored) {
            }
        }
        int old = playingPos;
        playingPos = -1;
        releaseActivePlayer();
        if (old >= 0) notifyItemChanged(old);
    }

    private void releaseActivePlayer() {
        if (activePlayer != null) {
            try {
                activePlayer.release();
            } catch (Exception ignored) {
            }
            activePlayer = null;
        }
    }

    // ── Adapter destroy hone par call karo (BottomSheet onDestroyView se) ────
    public void release() {
        stopActivePlayer();
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────
    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvArtist;
        ImageButton btnPreview;

        VH(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvMusicTitle);
            tvArtist = v.findViewById(R.id.tvMusicArtist);
            btnPreview = v.findViewById(R.id.btnMusicPreview);
        }
    }
}