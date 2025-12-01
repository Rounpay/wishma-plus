package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.infotech.wishmaplus.Api.Object.FriendModel;
import com.infotech.wishmaplus.Api.Response.UserListFriends;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    Context context;
    List<UserListFriends> list;
    UtilMethods.FriendActionListener listener;

    boolean isFriendRequest;
    public FriendListAdapter(Context context, List<UserListFriends> list, UtilMethods.FriendActionListener listener,boolean isFriendRequest) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.isFriendRequest = isFriendRequest;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_remove_friend_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserListFriends model = list.get(position);
        String fullName = (model.getFirstName() != null ? model.getFirstName() : "") +
                " " +
                (model.getLastName() != null ? model.getLastName() : "");
        holder.userName.setText(fullName.trim());
        Glide.with(context)
                .load(model.getProfilePictureUrl())
                .placeholder(R.drawable.app_logo)
                .error(R.drawable.app_logo)
                .centerCrop()
                .into(holder.profileImage);
        if(isFriendRequest) {
            holder.btnAddFriend.setText("Confirm");
            holder.removeUserBtn.setText("Cancel");
        }

        holder.btnAddFriend.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddClicked(model, position);
            }
        });

        holder.removeUserBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveClicked(model, position);
            }
        });

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView userName;
        MaterialButton btnAddFriend,removeUserBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            userName = itemView.findViewById(R.id.userName);
            btnAddFriend = itemView.findViewById(R.id.btnAddFriend);
            removeUserBtn = itemView.findViewById(R.id.removeUserBtn);
        }
    }
}

