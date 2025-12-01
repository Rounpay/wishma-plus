package com.infotech.wishmaplus.Adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Api.Response.FriendRequestResponse;
import com.infotech.wishmaplus.R;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private List<FriendRequestResponse> list;
    private int sent;

    public FriendAdapter(List<FriendRequestResponse> list,int sent) {
        this.list = list;
        this.sent = sent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_remove_friend_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendRequestResponse req = list.get(position);
        if(sent==1){
            holder.btnConfirm.setVisibility(GONE);
            holder.btnDelete.setText("Cancel Request");
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
            holder.btnDelete.setLayoutParams(params);
        }
        else {
            holder.btnConfirm.setVisibility(VISIBLE);
            holder.btnConfirm.setText("Confirm");
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
            holder.btnDelete.setLayoutParams(params);
            holder.btnDelete.setText("Delete");
        }

        holder.name.setText(req.getName());
        holder.profileImage.setImageResource(req.getImageRes());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView name;
        Button btnConfirm, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            name = itemView.findViewById(R.id.userName);
            btnConfirm = itemView.findViewById(R.id.btnAddFriend);
            btnDelete = itemView.findViewById(R.id.removeUserBtn);
        }
    }
}
