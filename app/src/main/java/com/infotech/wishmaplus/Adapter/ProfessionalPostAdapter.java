package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Api.Response.PostItem;
import com.infotech.wishmaplus.R;

import java.util.List;

public class ProfessionalPostAdapter extends RecyclerView.Adapter<ProfessionalPostAdapter.UserVH> {

private final List<PostItem> list;
private final Context ctx;
private final ProfessionalPostAdapter.OnItemClickListener listener;

public interface OnItemClickListener {
    void onItemClick(PostItem user, int pos);
    void onMoreClicked(View anchor, PostItem user, int pos);
}

public ProfessionalPostAdapter(Context ctx, List<PostItem> list, ProfessionalPostAdapter.OnItemClickListener listener) {
    this.ctx = ctx;
    this.list = list;
    this.listener = listener;
}

@NonNull
@Override
public ProfessionalPostAdapter.UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_professional, parent, false);
    return new ProfessionalPostAdapter.UserVH(v);
}

@Override
public void onBindViewHolder(@NonNull ProfessionalPostAdapter.UserVH holder, int position) {
    PostItem item = list.get(position);

    // HIDE ALL FIRST
    holder.imgPost.setVisibility(View.GONE);
    holder.videoPost.setVisibility(View.GONE);

    if (item.getContentTypeId() == 3) {  // IMAGE
        holder.imgPost.setVisibility(View.VISIBLE);
        holder.tvSubTitle.setText("Photo");
        if(item.getCaption() != null)
            holder.tvPostTitle.setText(item.getCaption());
        Glide.with(ctx)
                .load(item.getPostContent())
                .placeholder(R.drawable.app_logo)
                .into(holder.imgPost);
    }

    else if (item.getContentTypeId() == 2) {  // VIDEO
        holder.videoPost.setVisibility(View.VISIBLE);
        holder.tvSubTitle.setText("Video");
        holder.videoPost.setVideoPath(item.getPostContent());
        holder.videoPost.seekTo(1); // show first frame
        if(item.getCaption() != null)
            holder.tvPostTitle.setText(item.getCaption());
    }

    else if (item.getContentTypeId() == 1) {  // TEXT
        holder.imgPost.setVisibility(View.VISIBLE);
        holder.tvSubTitle.setText("Post");
        holder.tvPostTitle.setText(item.getPostContent());
        Glide.with(ctx)
                .load(item.getPostContent())
                .placeholder(R.drawable.app_logo)
                .into(holder.imgPost);

    }

    // Date
    holder.tvPostDate.setText(item.getCreatedDate());
    holder.tvEngagementValue.setText(""+item.getEngagement());
    holder.cardPost.setOnClickListener(v -> {
        if (listener != null) listener.onItemClick(item, holder.getAdapterPosition());
    });

}

@Override
public int getItemCount() {
    return list.size();
}

static class UserVH extends RecyclerView.ViewHolder {
    ImageView imgPost;
    VideoView videoPost;
    TextView tvPostTitle, tvPostDate,tvSubTitle,tvEngagementValue;

    View cardPost;

    public UserVH(@NonNull View itemView) {
        super(itemView);
        imgPost = itemView.findViewById(R.id.imagePost);
        cardPost = itemView.findViewById(R.id.cardPost);
        videoPost = itemView.findViewById(R.id.videoPost);
        tvPostTitle = itemView.findViewById(R.id.tvName);
        tvPostDate = itemView.findViewById(R.id.tvDate);
        tvSubTitle = itemView.findViewById(R.id.tvSubTitle);
        tvEngagementValue = itemView.findViewById(R.id.tvEngagementValue);
    }
}
}
