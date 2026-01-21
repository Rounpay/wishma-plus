package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Api.Response.ComplaintModel;
import com.infotech.wishmaplus.R;

import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ViewHolder> {

    private Context context;
    private List<ComplaintModel> list;

    public ComplaintAdapter(Context context, List<ComplaintModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_complaint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ComplaintModel model = list.get(position);

        holder.tvComplaintId.setText("Complaint #" + model.getComplaintId());
        holder.tvCategory.setText(model.getCategoryName());
        holder.tvDescription.setText(model.getDescription());
        holder.tvStatus.setText(model.getStatusName());
        holder.tvDate.setText(model.getCreatedOn());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvComplaintId, tvCategory, tvDescription, tvStatus, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvComplaintId = itemView.findViewById(R.id.tvComplaintId);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
