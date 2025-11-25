package com.infotech.wishmaplus.Adapter;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Activity.GroupModel;
import com.infotech.wishmaplus.R;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<GroupModel> list;
    private Context context;

    public GroupAdapter(Context context, List<GroupModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);
        return new GroupViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        GroupModel model = list.get(position);
        holder.tvGroupName.setText(model.getName());
        holder.tvPosts.setText(model.getPosts());

        holder.imgGroup.setImageResource(model.getImage()); // for drawable
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {

        ImageView imgGroup;
        TextView tvGroupName, tvPosts;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);

            imgGroup = itemView.findViewById(R.id.imgGroup);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvPosts = itemView.findViewById(R.id.tvPosts);
        }
    }
}

