package com.infotech.wishmaplus.Adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Api.Response.User;
import com.infotech.wishmaplus.R;

import java.util.List;
import java.util.Objects;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserVH> {

    private final List<User> list;
    private final Context ctx;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(User user, int pos);
        void onMoreClicked(View anchor, User user, int pos);
    }

    public UsersAdapter(Context ctx, List<User> list, OnItemClickListener listener) {
        this.ctx = ctx;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserVH holder, int position) {
        User u = list.get(position);
        holder.tvName.setText(u.getName());
        if(Objects.equals(u.getSubtitle(), "")){
            holder.tvSub.setVisibility(GONE);
        }
        else {
            holder.tvSub.setVisibility(VISIBLE);
            holder.tvSub.setText(u.getSubtitle());

        }


        // load avatar (Glide recommended); fallback to placeholder
         Glide.with(ctx).load(u.getAvatarUrl()).placeholder(R.drawable.app_logo).into(holder.img);

        // If you don't want Glide, use a resource if avatarUrl encodes resource id

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(u, holder.getAdapterPosition());
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class UserVH extends RecyclerView.ViewHolder {
        AppCompatImageView img;
        TextView tvName, tvSub;
        ImageView ivMore;

        public UserVH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSub = itemView.findViewById(R.id.tv_sub);
            ivMore = itemView.findViewById(R.id.iv_more);
        }
    }
}

