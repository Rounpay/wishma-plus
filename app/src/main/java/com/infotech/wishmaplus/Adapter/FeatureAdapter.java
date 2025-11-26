package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Api.Response.FeatureItem;
import com.infotech.wishmaplus.R;

import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder> {

    List<FeatureItem> list;
    Context context;

    public FeatureAdapter(Context context, List<FeatureItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feature, parent, false);
        return new FeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder holder, int position) {
        FeatureItem item = list.get(position);

        holder.imgIcon.setImageResource(item.icon);
        holder.txtTitle.setText(item.title);
        holder.txtSubtitle.setText(item.subtitle);
        if (position == list.size() - 1) {
            holder.itemLayout.setVisibility(View.GONE);
            holder.moreLayout.setVisibility(View.VISIBLE);
        } else {
            holder.itemLayout.setVisibility(View.VISIBLE);
            holder.moreLayout.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FeatureViewHolder extends RecyclerView.ViewHolder {

        ImageView imgIcon;
        TextView txtTitle, txtSubtitle;

        View itemLayout;
        View moreLayout;

        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);

            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtSubtitle = itemView.findViewById(R.id.txtSubtitle);
            itemLayout = itemView.findViewById(R.id.featureLayout);
            moreLayout = itemView.findViewById(R.id.moreLayout);
        }
    }
}
