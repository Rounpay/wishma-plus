package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.infotech.wishmaplus.Api.Response.FolderModel;
import com.infotech.wishmaplus.R;

import java.util.List;

public class FolderDropdownAdapter extends RecyclerView.Adapter<FolderDropdownAdapter.VH> {

    public interface OnFolderClickListener {
        void onFolderClick(int position);
    }

    private final Context context;
    private final List<FolderModel> folders;
    private final OnFolderClickListener listener;
    private int selectedPosition = 0;

    public FolderDropdownAdapter(Context context, List<FolderModel> folders, OnFolderClickListener listener) {
        this.context  = context;
        this.folders  = folders;
        this.listener = listener;
    }

    public void setSelectedPosition(int pos) {
        int old = selectedPosition;
        selectedPosition = pos;
        notifyItemChanged(old);
        notifyItemChanged(pos);
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_folder_dropdown, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        FolderModel folder = folders.get(position);

        holder.folderName.setText(folder.getFolderName());

        String countStr = folder.getCount() + " items";
        holder.folderCount.setText(countStr);

        // Load thumbnail
        if (folder.getThumbnailPath() != null) {
            Glide.with(context)
                .load(folder.getThumbnailPath())
                .transform(new RoundedCorners(10))
                .placeholder(R.drawable.rounded_grey_bg)
                .into(holder.folderThumb);
        } else {
            // "Gallery" default icon
            holder.folderThumb.setImageResource(R.drawable.gallery);
            holder.folderThumb.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.folderThumb.setBackgroundResource(R.drawable.rounded_grey_bg);
        }

        // Active check
        holder.activeCheck.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);

        // Bold if selected
        holder.folderName.setTypeface(null,
            position == selectedPosition
                ? android.graphics.Typeface.BOLD
                : android.graphics.Typeface.NORMAL
        );

        holder.itemView.setOnClickListener(v -> listener.onFolderClick(position));
    }

    @Override
    public int getItemCount() {
        return folders == null ? 0 : folders.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView folderThumb, activeCheck;
        TextView folderName, folderCount;

        VH(@NonNull View itemView) {
            super(itemView);
            folderThumb  = itemView.findViewById(R.id.folderThumb);
            activeCheck  = itemView.findViewById(R.id.activeCheck);
            folderName   = itemView.findViewById(R.id.folderNameTxt);
            folderCount  = itemView.findViewById(R.id.folderCountTxt);
        }
    }
}
