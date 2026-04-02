package com.infotech.wishmaplus;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.VH> {

    public interface OnStickerClickListener {
        void onClick(String sticker);
    }

    private final Context context;
    private List<String> stickers;
    private final OnStickerClickListener listener;

    public StickerAdapter(Context context, List<String> stickers, OnStickerClickListener listener) {
        this.context  = context;
        this.stickers = stickers;
        this.listener = listener;
    }

    public void updateData(List<String> newData) {
        this.stickers = newData;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int dp = (int) context.getResources().getDisplayMetrics().density;
        int size = 80 * dp;

        FrameLayout frame = new FrameLayout(context);
        frame.setLayoutParams(new RecyclerView.LayoutParams(size, size));

        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setColor(Color.parseColor("#1A1A1A"));
        bg.setCornerRadius(12 * dp);
        frame.setBackground(bg);

        int margin = 4 * dp;
        ((RecyclerView.LayoutParams) frame.getLayoutParams()).setMargins(margin, margin, margin, margin);

        TextView tv = new TextView(context);
        tv.setTextSize(32f);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));
        frame.addView(tv);

        return new VH(frame, tv);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String sticker = stickers.get(position);
        holder.tv.setText(sticker);
        holder.frame.setOnClickListener(v -> {
            v.animate().scaleX(0.85f).scaleY(0.85f).setDuration(80)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(150)
                    .setInterpolator(new android.view.animation.OvershootInterpolator()).start())
                .start();
            listener.onClick(sticker);
        });
    }

    @Override
    public int getItemCount() { return stickers == null ? 0 : stickers.size(); }

    static class VH extends RecyclerView.ViewHolder {
        FrameLayout frame;
        TextView tv;
        VH(FrameLayout f, TextView t) {
            super(f);
            frame = f;
            tv    = t;
        }
    }
}
