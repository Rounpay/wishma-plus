package com.infotech.wishmaplus;

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
import com.infotech.wishmaplus.Api.Response.MediaModel;

import java.util.List;

public class MultiSelectAdapter extends RecyclerView.Adapter<MultiSelectAdapter.VH> {

    public interface OnItemToggleListener {
        void onToggle(MediaModel item, int position);
    }

    private final Context context;
    private final List<MediaModel> items;
    private final OnItemToggleListener listener;

    public MultiSelectAdapter(Context context, List<MediaModel> items, OnItemToggleListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    public void clearSelections() {
        for (MediaModel m : items) {
            m.setSelected(false);
            m.setOrder(0);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_media_multiselect, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        MediaModel model = items.get(position);

        // Load thumbnail
        Glide.with(context)
                .load(model.getPath())
                .centerCrop()
                .placeholder(R.drawable.rounded_grey_bg)
                .into(holder.thumbnail);

        // Selection state
        boolean selected = model.isSelected();
        holder.selectedOverlay.setVisibility(selected ? View.VISIBLE : View.GONE);
        holder.checkRing.setVisibility(selected ? View.GONE : View.VISIBLE);
        holder.checkMark.setVisibility(View.GONE);
        holder.orderBadge.setVisibility(selected ? View.VISIBLE : View.GONE);

        if (selected && model.getOrder() > 0) {
            holder.orderBadge.setText(String.valueOf(model.getOrder()));
        }

        // Video duration
        if (model.isVideo()) {
            holder.duration.setVisibility(View.VISIBLE);
            holder.videoIcon.setVisibility(View.VISIBLE);
            holder.duration.setText(formatDuration(model.getDuration()));
        } else {
            holder.duration.setVisibility(View.GONE);
            holder.videoIcon.setVisibility(View.GONE);
        }

        // Click
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_ID) {
                listener.onToggle(items.get(pos), pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @SuppressLint("DefaultLocale")
    private String formatDuration(long duration) {
        long sec = duration / 1000;
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView thumbnail, checkMark, videoIcon;
        TextView duration, orderBadge;
        View selectedOverlay, checkRing;

        VH(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            checkMark = itemView.findViewById(R.id.checkMark);
            checkRing = itemView.findViewById(R.id.checkRing);
            videoIcon = itemView.findViewById(R.id.videoIcon);
            duration = itemView.findViewById(R.id.duration);
            orderBadge = itemView.findViewById(R.id.orderBadge);
            selectedOverlay = itemView.findViewById(R.id.selectedOverlay);
        }
    }
}
