package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.infotech.wishmaplus.Api.Response.BoostBillingResponse;
import com.infotech.wishmaplus.R;

import java.util.List;

public class BillingDetailsAdapter
        extends RecyclerView.Adapter<BillingDetailsAdapter.ViewHolder> {

    private final Context context;
    private final List<BoostBillingResponse.Result> billingList;

    public BillingDetailsAdapter(Context context, List<BoostBillingResponse.Result> billingList) {
        this.context = context;
        this.billingList = billingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_billing_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        BoostBillingResponse.Result item = billingList.get(position);

        holder.tvDateValue.setText(item.getBillingDate());
        holder.tvPlacementValue.setText(item.getPlacement());
        holder.tvBudgetPrice.setText("₹" + item.getBudget());
        holder.tvCostPrice.setText("₹" + item.getEstimatedCost());
        holder.tvSubPrice.setText("₹" + item.getSubTotal());
        holder.tvGstPrice.setText("₹" + item.getGst());
        holder.tvPrice.setText("₹" + item.getTotalCost());



        holder.btnDownload.setOnClickListener(v -> {
            // Handle PDF download here
            Toast.makeText(context, "We are working on it", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return billingList != null ? billingList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDateValue, tvPlacementValue, tvBudgetPrice,
                tvCostPrice, tvSubPrice, tvGstPrice, tvPrice;

        MaterialButton btnDownload;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDateValue = itemView.findViewById(R.id.tvDateValue);
            tvPlacementValue = itemView.findViewById(R.id.tvPlacementValue);
            tvBudgetPrice = itemView.findViewById(R.id.tvBudgetPrice);
            tvCostPrice = itemView.findViewById(R.id.tvCostPrice);
            tvSubPrice = itemView.findViewById(R.id.tvSubPrice);
            tvGstPrice = itemView.findViewById(R.id.tvGstPrice);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnDownload = itemView.findViewById(R.id.btnProfessionalDashboard);
        }
    }
}
