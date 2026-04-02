package com.infotech.wishmaplus;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HashtagSuggestionAdapter
        extends RecyclerView.Adapter<HashtagSuggestionAdapter.ViewHolder> {

    public interface OnTagClickListener {
        void onTagClick(String tag);
    }

    private List<HashtagItem> items = new ArrayList<>();
    private final OnTagClickListener listener;

    public HashtagSuggestionAdapter(OnTagClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(List<HashtagItem> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Simple row layout — create programmatically or use a real XML layout
        TextView tv = new TextView(parent.getContext());
        tv.setPadding(32, 20, 32, 20);
        tv.setTextColor(0xFFFFFFFF);
        tv.setTextSize(14f);
        tv.setBackgroundColor(0xFF1E1E1E);
        tv.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashtagItem item = items.get(position);
        String label = "#" + item.tag + "  (" + item.reelCount + " reels)";
        holder.tvTag.setText(label);
        holder.itemView.setOnClickListener(v -> listener.onTagClick(item.tag));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag;
        ViewHolder(TextView tv) {
            super(tv);
            tvTag = tv;
        }
    }
}
