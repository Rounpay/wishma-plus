package com.infotech.wishmaplus.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Activity.CreateReelActivity;
import com.infotech.wishmaplus.Api.Response.MediaModel;
import com.infotech.wishmaplus.R;

import java.util.List;

public class CreateReelAdapter extends RecyclerView.Adapter<CreateReelAdapter.MyViewHolder> {
    CreateReelActivity context;
    List<MediaModel> videoList;


    public CreateReelAdapter(CreateReelActivity context, List<MediaModel> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public CreateReelAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_video, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateReelAdapter.MyViewHolder holder, int position) {
        MediaModel model = videoList.get(position);


        Glide.with(context)
                .load(model.getPath())
                .into(holder.thumbnail);


        if (model.isVideo()) {
            holder.duration.setVisibility(View.VISIBLE);
            holder.duration.setText(formatDuration(model.getDuration()));
        } else {
            holder.duration.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return videoList == null ? 0 : videoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView duration;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            duration = itemView.findViewById(R.id.duration);
        }
    }
    private String formatDuration(long duration) {
        long sec = duration / 1000;
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }
}
