package com.infotech.wishmaplus.Adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Api.Response.FriendUserModel;
import com.infotech.wishmaplus.R;

import java.util.List;
import java.util.Objects;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.UserVH> {

    private final List<FriendUserModel> list;
    private final Context ctx;
    private final boolean isFromBlockList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FriendUserModel user, int pos);
        void onMoreClicked(View anchor, FriendUserModel user, int pos);
        void onProfileClick(FriendUserModel user, int position);
        void onBlockClick(FriendUserModel user, int position);
    }

    public FriendsListAdapter(Context ctx, List<FriendUserModel> list,boolean isFromBlockList, OnItemClickListener listener) {
        this.ctx = ctx;
        this.list = list;
        this.listener = listener;
        this.isFromBlockList =isFromBlockList;
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserVH holder, int position) {
        FriendUserModel u = list.get(position);
        holder.tvName.setText(u.getFullName());
        if(Objects.equals("", "")){
            holder.tvSub.setVisibility(GONE);
        }
        else {
            holder.tvSub.setVisibility(VISIBLE);
            holder.tvSub.setText("");

        }
        if(isFromBlockList){
            holder.ivMore.setVisibility(GONE);
            holder.btnBlock.setVisibility(VISIBLE);
        }
        else {
            holder.btnBlock.setVisibility(GONE);
            holder.ivMore.setVisibility(VISIBLE);
        }


        // load avatar (Glide recommended); fallback to placeholder
        Glide.with(ctx).load(u.getProfilePictureUrl()).placeholder(R.drawable.user_icon).into(holder.img);

        // If you don't want Glide, use a resource if avatarUrl encodes resource id

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(u, holder.getAdapterPosition());
        });

        holder.btnBlock.setOnClickListener(view -> {
            if (listener != null) listener.onBlockClick(u, holder.getAdapterPosition());
        });

        holder.ivMore.setOnClickListener(v -> {
            if (listener != null) listener.onMoreClicked(holder.ivMore, u, holder.getAdapterPosition());
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
        holder.img.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProfileClick(u, position);
            }
        });
        holder.tvName.setOnClickListener(v -> {
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
        AppCompatImageView img;
        TextView tvName, tvSub,btnBlock;
        ImageView ivMore;

        public UserVH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            btnBlock = itemView.findViewById(R.id.btnBlock);
            tvSub = itemView.findViewById(R.id.tv_sub);
            ivMore = itemView.findViewById(R.id.iv_more);
        }
    }
}

