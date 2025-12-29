package com.infotech.wishmaplus.Adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Api.Response.GroupMembersResponse;
import com.infotech.wishmaplus.Api.Response.UserResult;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;

public class MembersListAdapter extends RecyclerView.Adapter<MembersListAdapter.ViewHolder> {

    private List<GroupMembersResponse.Result> list;          // filtered list
    private List<GroupMembersResponse.Result> originalList;  // full list
    private Context context;
    private OnInviteClickListener listener;

    public MembersListAdapter(Context context, List<GroupMembersResponse.Result> list, OnInviteClickListener listener) {
        this.context = context;
        this.list = new ArrayList<>(list);
        this.originalList = new ArrayList<>(list); // backup
        this.listener = listener;
    }

    @NonNull
    @Override
    public MembersListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_person, parent, false);
        return new MembersListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersListAdapter.ViewHolder holder, int position) {

        GroupMembersResponse.Result user = list.get(position);

        holder.tvName.setText(user.getFullName());
        if(user.isAdmin()){
            holder.userType.setVisibility(VISIBLE);
            holder.userType.setText("Admin");
            holder.btnInvite.setVisibility(GONE);
        }
        else{
            holder.userType.setVisibility(GONE);
            holder.btnInvite.setVisibility(VISIBLE);
        }
        holder.btnInvite.setText("Remove");
        holder.btnInvite.setTextColor(Color.parseColor("#000000"));;
        holder.btnInvite.setBackgroundTintList(
                ContextCompat.getColorStateList(context, R.color.grey_1));

        Glide.with(context)
                .load(user.getProfilePictureUrl())
                .placeholder(R.drawable.user_icon)
                .error(R.drawable.user_icon)
                .into(holder.ivProfile);

        holder.btnInvite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onInviteClick(user, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    public void filter(String text) {
        list.clear();

        if (text == null || text.trim().isEmpty()) {
            list.addAll(originalList);
        } else {
            text = text.toLowerCase();
            for (GroupMembersResponse.Result user : originalList) {
                if (user.getFullName() != null &&
                        user.getFullName().toLowerCase().contains(text)) {
                    list.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfile;
        TextView tvName,userType;
        Button btnInvite;

        ViewHolder(View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvName);
            userType = itemView.findViewById(R.id.userType);
            btnInvite = itemView.findViewById(R.id.btnInvite);
        }
    }

    public interface OnInviteClickListener {
        void onInviteClick(GroupMembersResponse.Result user, int position);
    }
}

