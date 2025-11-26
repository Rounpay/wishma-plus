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

public class MonetizationAdapter extends RecyclerView.Adapter<MonetizationAdapter.MonetizationViewHolder> {

    List<StatCard> list;

    public MonetizationAdapter(List<StatCard> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MonetizationAdapter.MonetizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.monetization_card, parent, false);
        return new MonetizationAdapter.MonetizationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonetizationAdapter.MonetizationViewHolder holder, int position) {
        StatCard card = list.get(position);
        holder.icon.setImageResource(card.icon);
        holder.value.setText(card.value);
        holder.label.setText(card.label);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MonetizationViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView value, label;

        public MonetizationViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            value = itemView.findViewById(R.id.value);
            label = itemView.findViewById(R.id.label);
        }
    }
}