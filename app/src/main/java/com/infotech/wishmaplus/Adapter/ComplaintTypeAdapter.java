package com.infotech.wishmaplus.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Api.Response.ComplaintType;
import com.infotech.wishmaplus.Api.Response.SupportCategoryResponse;
import com.infotech.wishmaplus.R;

import java.util.List;

public class ComplaintTypeAdapter extends RecyclerView.Adapter<ComplaintTypeAdapter.ViewHolder> {

    private final List<SupportCategoryResponse.Result> list;
    private int selectedPosition = -1;

    private final OnComplaintTypeClickListener listener;

    public interface OnComplaintTypeClickListener {
        void onComplaintTypeSelected(SupportCategoryResponse.Result item, int position);
    }
    public ComplaintTypeAdapter(List<SupportCategoryResponse.Result> list,
                                OnComplaintTypeClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_complaint_type, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SupportCategoryResponse.Result item = list.get(position);

        holder.tvTitle.setText(item.getCategoryName());
//        holder.tvDesc.setText(item.description);
//        holder.ivIcon.setImageResource(item.iconRes);
//        holder.rbSelect.setChecked(position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();

            if (listener != null) {
                listener.onComplaintTypeSelected(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public SupportCategoryResponse.Result getSelectedItem() {
        return selectedPosition != -1 ? list.get(selectedPosition) : null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;

        ViewHolder(View itemView) {
            super(itemView);
//            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
//            rbSelect = itemView.findViewById(R.id.rbSelect);
        }
    }
}
