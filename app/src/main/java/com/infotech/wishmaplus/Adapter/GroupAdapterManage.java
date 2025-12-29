package com.infotech.wishmaplus.Adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Api.Response.GroupListResponse;
import com.infotech.wishmaplus.Api.Response.PostItem;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.PreferencesManager;

import java.util.List;
import java.util.Objects;

public class GroupAdapterManage extends RecyclerView.Adapter<GroupAdapterManage.GroupViewHolder> {

    private List<GroupListResponse.Result> list;
    private Context context;
    PreferencesManager tokenManager;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(GroupListResponse.Result item, int pos);
    }

    public GroupAdapterManage(Context context, List<GroupListResponse.Result> list, PreferencesManager tokenManager, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.tokenManager = tokenManager;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        String savedPageId = tokenManager.getString("ACTIVE_GROUP_ID");
        GroupListResponse.Result model = list.get(position);
        holder.tvGroupName.setText(model.getTitle());
        holder.txtGroupInfo.setText("Admin"+" · "+1+" members");
        if(Objects.equals(savedPageId, model.getGroupId()))
        {
            holder.imgSelected.setVisibility(VISIBLE);
        }
        else{
            holder.imgSelected.setVisibility(GONE);
        }
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) listener.onItemClick(model, holder.getAdapterPosition());
        });
//        holder.tvPosts.setText(model.getPosts());

//        holder.imgGroup.setImageResource(model.getImage()); // for drawable

        // for image from url
        Glide.with(context).load(model.getCoverImageUrl()).placeholder(R.drawable.user_icon).into(holder.imgGroup);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {

        ImageView imgGroup,imgSelected;
        TextView tvGroupName, tvPosts,txtGroupInfo;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);

            imgGroup = itemView.findViewById(R.id.imgGroup);
            imgSelected = itemView.findViewById(R.id.imgSelected);
            tvGroupName = itemView.findViewById(R.id.txtGroupName);
            tvPosts = itemView.findViewById(R.id.tvPosts);
            txtGroupInfo = itemView.findViewById(R.id.txtGroupInfo);
        }
    }
}
