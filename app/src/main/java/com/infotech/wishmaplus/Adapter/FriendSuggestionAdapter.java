package com.infotech.wishmaplus.Adapter;

import static android.view.View.GONE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.R;

import java.util.List;

public class FriendSuggestionAdapter extends RecyclerView.Adapter<FriendSuggestionAdapter.UserVH> {

    private final List<FriendSuggestionItem> list;
    private final Context ctx;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FriendSuggestionItem user, int pos);
        void onMoreClicked(View anchor, FriendSuggestionItem user, int pos);
    }

    public FriendSuggestionAdapter(Context ctx, List<FriendSuggestionItem> list, OnItemClickListener listener) {
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
        FriendSuggestionItem u = list.get(position);
        holder.name.setText(u.getFullName());


        // load avatar (Glide recommended); fallback to placeholder
        Glide.with(ctx).load(u.getProfilePictureUrl()).placeholder(R.drawable.user_icon).into(holder.profileImage);

        // If you don't want Glide, use a resource if avatarUrl encodes resource id

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(u, holder.getAdapterPosition());
        });
        holder.btnDelete.setVisibility(GONE);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        holder.btnConfirm.setLayoutParams(params);

        holder.btnConfirm.setOnClickListener(v -> {
            if (listener != null) listener.onMoreClicked(holder.btnDelete, u, holder.getAdapterPosition());

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