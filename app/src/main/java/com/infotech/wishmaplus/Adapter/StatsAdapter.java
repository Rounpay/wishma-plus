package com.infotech.wishmaplus.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Api.Response.StatCard;
import com.infotech.wishmaplus.R;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatViewHolder> {

    List<StatCard> list;

    public StatsAdapter(List<StatCard> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public StatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stat_card, parent, false);
        return new StatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatViewHolder holder, int position) {
        StatCard card = list.get(position);
        holder.icon.setImageResource(card.icon);
        holder.value.setText(card.value);
        holder.label.setText(card.label);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class StatViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView value, label;

        public StatViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            value = itemView.findViewById(R.id.value);
            label = itemView.findViewById(R.id.label);
        }
    }
}

