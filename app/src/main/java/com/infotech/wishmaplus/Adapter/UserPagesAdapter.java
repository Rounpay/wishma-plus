package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Api.Response.UserModelResponse;
import com.infotech.wishmaplus.R;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class UserPagesAdapter extends RecyclerView.Adapter<UserPagesAdapter.ViewHolder> {

    Context context;
    List<UserModelResponse> list;

    public UserPagesAdapter(Context context, List<UserModelResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_pages, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModelResponse model = list.get(position);

        holder.userName.setText(model.getName());
        holder.userImage.setImageResource(model.getImage());

        // Show blue tick only if verified
        if (model.isVerified()) {
            holder.blueTick.setVisibility(View.VISIBLE);
        } else {
            holder.blueTick.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView userImage, blueTick;
        AppCompatTextView userName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            blueTick = itemView.findViewById(R.id.blueTick1);
            userName = itemView.findViewById(R.id.userName);
        }
    }
}
