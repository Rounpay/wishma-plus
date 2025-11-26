package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Api.Response.MessageModel;
import com.infotech.wishmaplus.R;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    List<MessageModel> list;
    Context context;

    public MessageAdapter(Context context, List<MessageModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageModel model = list.get(position);
        holder.tvMessage.setText(model.getMessage());
        holder.tvUserTime.setText(model.getUserTime());
        if (position == list.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMessage, tvUserTime;
        ImageView imgProfile, imgRight;
        View divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvUserTime = itemView.findViewById(R.id.tvUserTime);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgRight = itemView.findViewById(R.id.imgRight);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}
