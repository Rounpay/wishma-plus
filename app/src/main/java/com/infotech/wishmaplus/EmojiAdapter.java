package com.infotech.wishmaplus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.VH> {

    public interface OnEmojiClickListener {
        void onClick(String emoji);
    }

    private final Context context;
    private List<String> emojis;
    private final OnEmojiClickListener listener;

    public EmojiAdapter(Context context, List<String> emojis, OnEmojiClickListener listener) {
        this.context  = context;
        this.emojis   = emojis;
        this.listener = listener;
    }

    public void updateData(List<String> newEmojis) {
        this.emojis = newEmojis;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView tv = new TextView(context);
        tv.setTextSize(28f);
        tv.setGravity(android.view.Gravity.CENTER);
        int size = (int) (52 * context.getResources().getDisplayMetrics().density);
        tv.setLayoutParams(new RecyclerView.LayoutParams(size, size));
        tv.setPadding(4, 4, 4, 4);
        return new VH(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String emoji = emojis.get(position);
        holder.tv.setText(emoji);
        holder.tv.setOnClickListener(v -> {
            // bounce animation
            v.animate().scaleX(1.3f).scaleY(1.3f).setDuration(100)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
            listener.onClick(emoji);
        });
    }

    @Override
    public int getItemCount() { return emojis == null ? 0 : emojis.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv;
        VH(TextView v) {
            super(v);
            tv = v;
        }
    }
}
