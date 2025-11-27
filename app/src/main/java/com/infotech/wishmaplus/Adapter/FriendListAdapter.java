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
import com.infotech.wishmaplus.R;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    Context context;
    List<FriendModel> list;

    public FriendListAdapter(Context context, List<FriendModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_remove_friend_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendModel model = list.get(position);
        holder.userName.setText(model.getName());
        Glide.with(context)
                .load(model.getImageUrl())
                .placeholder(R.drawable.app_logo)
                .into(holder.profileImage);

        holder.btnAddFriend.setOnClickListener(v -> {
            model.setFriend(!model.isFriend());
            notifyItemChanged(position);
            if (model.isFriend()) {
                Toast.makeText(context, "Friend Added ✔", Toast.LENGTH_SHORT).show();
            }
        });
        holder.removeUserBtn.setOnClickListener(v -> {
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
            Toast.makeText(context, model.getName() + " removed", Toast.LENGTH_SHORT).show();
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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

