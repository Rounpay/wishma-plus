package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Api.Response.UserResult;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private List<UserResult> list;          // filtered list
    private List<UserResult> originalList;  // full list
    private Context context;
    private OnInviteClickListener listener;

    public UserListAdapter(Context context, List<UserResult> list, OnInviteClickListener listener) {
        this.context = context;
        this.list = new ArrayList<>(list);
        this.originalList = new ArrayList<>(list); // backup
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_person, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserResult user = list.get(position);

        holder.tvName.setText(user.getFullName());

        Glide.with(context)
                .load(user.getProfilePictureUrl())
                .placeholder(R.drawable.user_icon)
                .error(R.drawable.user_icon)
                .into(holder.ivProfile);

        holder.btnInvite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onInviteClick(user, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    // 🔍 SEARCH FILTER METHOD
    public void filter(String text) {
        list.clear();

        if (text == null || text.trim().isEmpty()) {
            list.addAll(originalList);
        } else {
            text = text.toLowerCase();
            for (UserResult user : originalList) {
                if (user.getFullName() != null &&
                        user.getFullName().toLowerCase().contains(text)) {
                    list.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfile;
        TextView tvName;
        Button btnInvite;

        ViewHolder(View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvName);
            btnInvite = itemView.findViewById(R.id.btnInvite);
        }
    }

    public interface OnInviteClickListener {
        void onInviteClick(UserResult user, int position);
    }
}
