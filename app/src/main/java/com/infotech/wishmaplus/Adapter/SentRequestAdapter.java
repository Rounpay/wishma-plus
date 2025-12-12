package com.infotech.wishmaplus.Adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Api.Response.SentRequestResponse;
import com.infotech.wishmaplus.Api.Response.UserListFriends;
import com.infotech.wishmaplus.R;

import java.util.List;
import java.util.Objects;

public class SentRequestAdapter extends RecyclerView.Adapter<SentRequestAdapter.UserVH> {

    private final List<SentRequestResponse.ResultItem> list;
    private final Context ctx;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SentRequestResponse.ResultItem user, int pos);
        void onMoreClicked(View anchor, SentRequestResponse.ResultItem user, int pos);
        void onProfileClick(SentRequestResponse.ResultItem user, int position);
    }

    public SentRequestAdapter(Context ctx, List<SentRequestResponse.ResultItem> list, OnItemClickListener listener) {
        this.ctx = ctx;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_remove_friend_layout, parent, false);
        return new UserVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserVH holder, int position) {
        SentRequestResponse.ResultItem u = list.get(position);
        holder.name.setText(u.getFullName());


        // load avatar (Glide recommended); fallback to placeholder
        Glide.with(ctx).load(u.getProfilePictureUrl()).placeholder(R.drawable.user_icon).into(holder.profileImage);

        // If you don't want Glide, use a resource if avatarUrl encodes resource id

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(u, holder.getAdapterPosition());
        });
        holder.btnConfirm.setVisibility(GONE);
        holder.btnDelete.setText("Cancel Request");
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        holder.btnDelete.setLayoutParams(params);

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onMoreClicked(holder.btnDelete, u, holder.getAdapterPosition());
//            else {
//                // default popup
//                PopupMenu pm = new PopupMenu(ctx, holder.ivMore);
//                pm.getMenu().add("View profile");
//                pm.getMenu().add("Unfriend");
//                pm.setOnMenuItemClickListener(item -> {
//                    // handle
//                    return true;
//                });
//                pm.show();
//            }
        });
        holder.profileImage.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProfileClick(u, position);
            }
        });
        holder.name.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProfileClick(u, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class UserVH extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView name;
        Button btnConfirm, btnDelete;

        public UserVH(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            name = itemView.findViewById(R.id.userName);
            btnConfirm = itemView.findViewById(R.id.btnAddFriend);
            btnDelete = itemView.findViewById(R.id.removeUserBtn);
        }
    }
}