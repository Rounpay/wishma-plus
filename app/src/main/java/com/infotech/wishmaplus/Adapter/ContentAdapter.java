package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Api.Response.ContentModel;
import com.infotech.wishmaplus.R;

import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {


    private List<ContentModel> list;
    private Context context;


    public ContentAdapter(Context context, List<ContentModel> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_content, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContentModel model = list.get(position);
        if (model.getTextTitle().isEmpty()) {
            holder.textTitle.setText("No text content");
            holder.imageThumb.setVisibility(View.VISIBLE);
        } else {
            holder.textTitle.setText(model.getTextTitle());
            holder.imageThumb.setVisibility(View.GONE);

        }

        holder.imageThumb.setImageResource(model.getImage());
        holder.textStats.setText(model.getStats());
        holder.textDate.setText(model.getDate());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageThumb;
        TextView textStats, textDate,textTitle;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageThumb = itemView.findViewById(R.id.imageThumb);
            textStats = itemView.findViewById(R.id.textStats);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDate = itemView.findViewById(R.id.textDate);
        }
    }
}
