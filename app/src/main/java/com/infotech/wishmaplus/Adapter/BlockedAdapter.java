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

import com.infotech.wishmaplus.Api.Response.UserModel;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;

public class BlockedAdapter extends RecyclerView.Adapter<BlockedAdapter.ViewHolder> {

    Context context;
    ArrayList<UserModel> list;
    UnblockClickListener unblockClickListener;

    public BlockedAdapter(Context context, ArrayList<UserModel> list,UnblockClickListener listener) {
        this.context = context;
        this.list = list;
        this.unblockClickListener = listener;
    }
    public interface UnblockClickListener {
        void onUnblockClicked(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_blocked_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserModel model = list.get(position);

        holder.txtName.setText(model.getName());
        holder.imgUser.setImageResource(model.getImage());
        holder.btnUnblock.setOnClickListener(v -> {
            if (unblockClickListener != null) {
                unblockClickListener.onUnblockClicked(position);
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


