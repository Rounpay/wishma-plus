package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Api.Response.NotificationResponse;
import com.infotech.wishmaplus.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private List<NotificationResponse.NotificationItem> list;
    private OnNotificationClick listener;

    public interface OnNotificationClick {
        void onConfirm(int position);
        void onDelete(int position);
        void onMore(int position);
    }

    public NotificationAdapter(Context context, List<NotificationResponse.NotificationItem> list, OnNotificationClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationResponse.NotificationItem model = list.get(position);


        // Load profile image
        Glide.with(context)
                .load(model.getProfilePictureUrl())
                .placeholder(R.drawable.user_icon)
                .into(holder.imgProfile);

        holder.tvMessage.setText(model.getUserName()+" "+model.getTitle()+": "+model.getBody());
        holder.tvTime.setText(model.getTime());

        // Show or hide Confirm/Delete buttons
        holder.btnContainer.setVisibility(View.GONE);
        if (!model.getIsRead()) {
            holder.notificationCard.setBackground(
                    ContextCompat.getDrawable(context, R.drawable.notification_background)
            );
//            holder.btnContainer.setVisibility(View.VISIBLE);
        } else {
//            holder.btnContainer.setVisibility(View.GONE);

        }
        if (model.getContentTypeId() == 3) {  // IMAGE
            holder.imgBadge.setImageResource(R.drawable.ic_camera);
        }

        if (model.getContentTypeId() == 2) {  // VIDEO
            holder.imgBadge.setImageResource(R.drawable.ic_video);
        }

        if (model.getContentTypeId() == 1) {  // TEXT
            holder.imgBadge.setImageResource(R.drawable.ic_edit);
        }


        // Button click listeners
        holder.btnConfirm.setOnClickListener(v ->
                listener.onConfirm(position)
        );

        holder.btnDelete.setOnClickListener(v ->
                listener.onDelete(position)
        );
        holder.imgMenu.setOnClickListener(v ->
                listener.onMore(position)
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProfile,imgMenu,imgBadge;
        TextView tvMessage, tvTime;
        LinearLayout btnContainer;
        Button btnConfirm, btnDelete;
        View notificationCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgBadge = itemView.findViewById(R.id.imgBadge);
            notificationCard = itemView.findViewById(R.id.notificationCard);
            imgMenu = itemView.findViewById(R.id.imgMenu);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnContainer = itemView.findViewById(R.id.btnContainer);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
