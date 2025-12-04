package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Api.Response.PageData;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.List;

public class UserProfilesAdapter extends RecyclerView.Adapter<UserProfilesAdapter.ViewHolder> {

    Context context;
    List<PageData> list;
    private final OnItemClickListener listener;

    public UserProfilesAdapter(Context context, List<PageData> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(PageData user, int pos);
        void onMoreClicked(View anchor, PageData user, int pos);
    }

    @NonNull
    @Override
    public UserProfilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_profiles_item, parent, false);
        return new UserProfilesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfilesAdapter.ViewHolder holder, int position) {

        PageData model = list.get(position);

        holder.tvName.setText(model.getPageName());

        Glide.with(context)
                .load(model.getProfileImageUrl())
                .apply(UtilMethods.INSTANCE.getRequestOption_With_UserIcon())
                .into(holder.imgProfile);

        holder.userItem.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(model, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();   // FIXED
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProfile;
        TextView tvName;
        View userItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile1);
            tvName = itemView.findViewById(R.id.tvName);
            userItem = itemView.findViewById(R.id.userItem);
        }
    }
}

