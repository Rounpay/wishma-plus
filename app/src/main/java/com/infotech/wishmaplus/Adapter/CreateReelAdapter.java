package com.infotech.wishmaplus.Adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.infotech.wishmaplus.Api.Response.MediaModel;
import com.infotech.wishmaplus.R;

import java.util.List;

public class CreateReelAdapter extends RecyclerView.Adapter<CreateReelAdapter.VH> {

    public interface OnItemClickListener {
        void onClick(MediaModel item);
    }

    private final Context context;
    private final List<MediaModel> mediaList;
    private final OnItemClickListener listener;

    public CreateReelAdapter(Context context, List<MediaModel> mediaList, OnItemClickListener listener) {
        this.context = context;
        this.mediaList = mediaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Reuse the same item_media_multiselect layout but without selection UI shown
        View v = LayoutInflater.from(context).inflate(R.layout.item_media_multiselect, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        MediaModel model = mediaList.get(position);

        Glide.with(context)
                .load(model.getPath())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.rounded_grey_bg)
                .into(holder.thumbnail);

        // Normal mode: hide selection UI
        holder.checkRing.setVisibility(View.GONE);
        holder.checkMark.setVisibility(View.GONE);
        holder.orderBadge.setVisibility(View.GONE);
        holder.selectedOverlay.setVisibility(View.GONE);

        // Video badge
        if (model.isVideo()) {
            holder.duration.setVisibility(View.VISIBLE);
            holder.videoIcon.setVisibility(View.VISIBLE);
            holder.duration.setText(formatDuration(model.getDuration()));
        } else {
            holder.duration.setVisibility(View.GONE);
            holder.videoIcon.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(model);
        });
    }

    @Override
    public int getItemCount() {
        return mediaList == null ? 0 : mediaList.size();
    }

    @SuppressLint("DefaultLocale")
    private String formatDuration(long ms) {
        long sec = ms / 1000;
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }

    public static class VH extends RecyclerView.ViewHolder {
        ImageView thumbnail, checkMark, videoIcon;
        TextView duration, orderBadge;
        View selectedOverlay,checkRing;

        VH(@NonNull View v) {
            super(v);
            thumbnail = v.findViewById(R.id.thumbnail);
            checkMark = v.findViewById(R.id.checkMark);
            checkRing = v.findViewById(R.id.checkRing);
            videoIcon = v.findViewById(R.id.videoIcon);
            duration = v.findViewById(R.id.duration);
            orderBadge = v.findViewById(R.id.orderBadge);
            selectedOverlay = v.findViewById(R.id.selectedOverlay);
        }
    }
}