package com.infotech.wishmaplus.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Api.Response.AnalyticsDetailsResponse;
import com.infotech.wishmaplus.R;

import java.util.List;

public class ContentSummaryAdapter
        extends RecyclerView.Adapter<ContentSummaryAdapter.ViewHolder> {

    private final List<AnalyticsDetailsResponse.ContentSummary> list;
    private final int maxValue;

    public ContentSummaryAdapter(
            List<AnalyticsDetailsResponse.ContentSummary> list,
            int maxValue
    ) {
        this.list = list;
        this.maxValue = maxValue;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_content_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        AnalyticsDetailsResponse.ContentSummary item = list.get(position);

        holder.tvContentType.setText(toSentenceCase(item.getContentType()));
        holder.tvCount.setText(String.valueOf(item.getTotal()));

        // Calculate percentage for progress bar
        int progress = maxValue > 0
                ? (item.getTotal() * 100) / maxValue
                : 0;

        holder.progressBar.setProgress(item.getTotal());
    }
    private String toSentenceCase(String text) {
        if (text == null || text.trim().isEmpty()) return "";

        text = text.trim().toLowerCase();
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvContentType, tvCount;
        ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContentType = itemView.findViewById(R.id.tvContentType);
            tvCount = itemView.findViewById(R.id.tvImageCount);
            progressBar = itemView.findViewById(R.id.progressImage);
        }
    }
}