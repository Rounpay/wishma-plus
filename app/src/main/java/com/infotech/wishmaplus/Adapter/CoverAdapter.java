package com.infotech.wishmaplus.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.R;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class CoverAdapter extends RecyclerView.Adapter<CoverAdapter.CoverViewHolder> {

    private List<Integer> coverList;
    private OnCoverClickListener listener;
    private int selectedPosition = 0;

    public interface OnCoverClickListener {
        void onCoverClick(int imageRes);
    }

    public CoverAdapter(List<Integer> coverList, OnCoverClickListener listener) {
        this.coverList = coverList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CoverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cover, parent, false);
        return new CoverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoverViewHolder holder, int position) {

        holder.ivCover.setImageResource(coverList.get(position));

        holder.itemView.setAlpha(selectedPosition == position ? 1f : 0.5f);

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
            listener.onCoverClick(coverList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return coverList.size();
    }

    static class CoverViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCover;

        public CoverViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivItemCover);
        }
    }
}

