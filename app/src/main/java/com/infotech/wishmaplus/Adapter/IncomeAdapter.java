package com.infotech.wishmaplus.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.infotech.wishmaplus.Api.Object.LevelCountResult;
import com.infotech.wishmaplus.Api.Object.PackageResult;
import com.infotech.wishmaplus.Api.Response.Income;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolder>  {

    private List<Income> list;

    public IncomeAdapter(List<Income> list ) {
        this.list = list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Income result = list.get(position);

        holder.tvUserName.setText(result.getUserName());
        holder.tvLevelNo.setText(result.getLevelNo());
        holder.tvTid.setText(result.getTid());
        holder.tvCommAmt.setText(result.getCommAmt());
        holder.tvSubscriberID.setText(result.getSubscriberID());
        holder.tvClosingDate.setText(result.getClosingDate());
        holder.tvIncomeType.setText(result.getIncomeType());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvLevelNo, tvTid, tvCommAmt, tvSubscriberID, tvClosingDate, tvIncomeType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvLevelNo = itemView.findViewById(R.id.tvLevelNo);
            tvTid = itemView.findViewById(R.id.tvTid);
            tvCommAmt = itemView.findViewById(R.id.tvCommAmt);
            tvSubscriberID = itemView.findViewById(R.id.tvSubscriberID);
            tvClosingDate = itemView.findViewById(R.id.tvClosingDate);
            tvIncomeType = itemView.findViewById(R.id.tvIncomeType);
        }
    }




}
