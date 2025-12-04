package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Api.Response.PageData;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.UtilMethods;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class UserPagesAdapter extends RecyclerView.Adapter<UserPagesAdapter.ViewHolder> {

    Context context;
    List<PageData> list;

    private final OnItemClickListener listener;
    String pageNumber;

    public interface OnItemClickListener {
        void onItemClick(PageData user, int pos);
        void onMoreClicked(View anchor, PageData user, int pos);
    }

    public UserPagesAdapter(Context context, List<PageData> list, OnItemClickListener listener,String pageNumber) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.pageNumber = pageNumber;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_pages, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PageData model = list.get(position);

        holder.userName.setText(model.getPageName());
//        holder.userImage.setImageResource();
        Glide.with(context)
                .load(model.getProfileImageUrl())
                .apply(UtilMethods.INSTANCE.getRequestOption_With_UserIcon())
                .into(holder.userImage);
        holder.itemHead.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(model, holder.getAdapterPosition());
        });
        // Show blue tick only if verified
        if(pageNumber.isEmpty() && position==0){
            holder.blueTick.setVisibility(View.VISIBLE);
        }
        else if((!pageNumber.isEmpty()) && model.getPageId().equals(pageNumber)) {
            holder.blueTick.setVisibility(View.VISIBLE);
        }
        else{
            holder.blueTick.setVisibility(View.GONE);
        }
//        if (false) {
//            holder.blueTick.setVisibility(View.VISIBLE);
//        } else {
//            holder.blueTick.setVisibility(View.GONE);
//        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView userImage, blueTick;
        AppCompatTextView userName;
        View itemHead;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            blueTick = itemView.findViewById(R.id.blueTick1);
            userName = itemView.findViewById(R.id.userName);
            itemHead = itemView.findViewById(R.id.itemHead);
        }
    }
}
