package com.infotech.wishmaplus.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.infotech.wishmaplus.Api.Object.FollowerResult;
import com.infotech.wishmaplus.Api.Object.PackageResult;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.ViewHolder> implements Filterable {


    private  RequestOptions requestOptionsUserImage;
    private List <FollowerResult> listItem;
    private List<FollowerResult> filterListItem;
    private OnClick onClick;
    private FragmentActivity mActivity;


    public FollowersAdapter(FragmentActivity mActivity, List<FollowerResult> listItem, OnClick onClick) {
        this.mActivity = mActivity;
        this.listItem = listItem;
        this.filterListItem = listItem;
        this.onClick = onClick;

        if (requestOptionsUserImage == null) {
            requestOptionsUserImage = UtilMethods.INSTANCE.getRequestOption_With_UserIcon();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_follower_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FollowerResult result = filterListItem.get(position);


        if(result.isSubscribed()){
            holder.statusBtn.setText(R.string.subscribed);
            holder.statusBtn.setBackgroundTintList(ContextCompat.getColorStateList(mActivity, android.R.color.holo_green_dark));
        }else {
            holder.statusBtn.setText(R.string.free);
            holder.statusBtn.setBackgroundTintList(ContextCompat.getColorStateList(mActivity, android.R.color.holo_red_dark));
        }
        holder.packageTv.setText(result.getPackageName());
        holder.nameTv.setText(result.getFirstName()+" "+result.getLastName());
        Glide.with(mActivity)
                .load(result.getProfilePic())
                .apply(requestOptionsUserImage)
                .into(holder.userIcon);
       /* holder.packageTv.setOnClickListener(view -> {
            if (onClick != null) {
                onClick.onClick(result);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return filterListItem.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filterListItem = listItem;
                } else {
                    ArrayList<FollowerResult> filteredList = new ArrayList<>();
                    for (FollowerResult row : listItem) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match


                        if (new Gson().toJson(row).toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filterListItem = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterListItem;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterListItem = (ArrayList<FollowerResult>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView packageTv,nameTv;
        TextView statusBtn;
        ImageView userIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userIcon = itemView.findViewById(R.id.userIcon);
            packageTv = itemView.findViewById(R.id.packageTv);
            nameTv = itemView.findViewById(R.id.nameTv);
            statusBtn = itemView.findViewById(R.id.statusBtn);
        }
    }


    public interface OnClick {
        void onClick(PackageResult value);
    }
}
