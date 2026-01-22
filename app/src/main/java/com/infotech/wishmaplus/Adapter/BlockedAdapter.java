package com.infotech.wishmaplus.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Api.Response.BlockedUserListResponse;
import com.infotech.wishmaplus.Api.Response.UserModel;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;

public class BlockedAdapter extends RecyclerView.Adapter<BlockedAdapter.ViewHolder> {

    Context context;
    List<BlockedUserListResponse.Result> list;
    UnblockClickListener unblockClickListener;

    public BlockedAdapter(Context context, List<BlockedUserListResponse.Result> list, UnblockClickListener listener) {
        this.context = context;
        this.list = list;
        this.unblockClickListener = listener;
    }
    public interface UnblockClickListener {
        void onUnblockClicked(int position, BlockedUserListResponse.Result model);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_blocked_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        BlockedUserListResponse.Result model = list.get(position);

        holder.txtName.setText(model.getFullName());
        Glide.with(context).load(model.getProfilePictureUrl()).placeholder(R.drawable.user_icon).into(holder.imgUser);
        holder.btnUnblock.setOnClickListener(v -> {
            if (unblockClickListener != null) {
                unblockClickListener.onUnblockClicked(position,model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUser;
        TextView txtName, btnUnblock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.imgUser);
            txtName = itemView.findViewById(R.id.txtName);
            btnUnblock = itemView.findViewById(R.id.btnUnblock);
        }
    }


}


