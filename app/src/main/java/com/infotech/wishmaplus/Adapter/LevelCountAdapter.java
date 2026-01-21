package com.infotech.wishmaplus.Adapter;

import android.content.Intent;
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
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.infotech.wishmaplus.Activity.LevelIncomeActivity;
import com.infotech.wishmaplus.Api.Object.LevelCountResult;
import com.infotech.wishmaplus.Api.Object.PackageResult;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;

public class LevelCountAdapter extends RecyclerView.Adapter<LevelCountAdapter.ViewHolder> implements Filterable {

    /*private final RequestOptions requestOptionsPlaceHolder;*/
    private List<LevelCountResult> listItem;
    private List<LevelCountResult> filterListItem;/*
    private OnClick onClick;*/
    private FragmentActivity mActivity;
    private BottomSheetDialog bottomSheetDialog;


    public LevelCountAdapter(FragmentActivity mActivity, List<LevelCountResult> listItem/*, OnClick onClick*/) {
        this.mActivity = mActivity;
        this.listItem = listItem;
        this.filterListItem = listItem;
        /*this.onClick = onClick;*/

       /* requestOptionsPlaceHolder = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_package)
                .placeholder(R.drawable.ic_package);*/
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_level_count, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        LevelCountResult result = filterListItem.get(position);

        holder.levelCount.setText(result.getLevel() + "");
        holder.totalCount.setText(result.getCount() + "");
        holder.activeCount.setText(result.getActiveCount() + "");
        holder.deactiveCount.setText(result.getDeActiveCount() + "");

        holder.viewIncome.setOnClickListener(view -> mActivity.startActivity(new Intent(mActivity, LevelIncomeActivity.class)
                .putExtra("LEVEL",result.getLevel())));
    }

    @Override
    public int getItemCount() {
        return filterListItem.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filterListItem = listItem;
                } else {
                    ArrayList<LevelCountResult> filteredList = new ArrayList<>();
                    for (LevelCountResult row : listItem) {

                        if (new Gson().toJson(row).toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filterListItem = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterListItem;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterListItem = (ArrayList<LevelCountResult>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView levelCount, totalCount, activeCount, deactiveCount;
        MaterialButton viewIncome;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            levelCount = itemView.findViewById(R.id.levelCount);
            totalCount = itemView.findViewById(R.id.totalCount);
            activeCount = itemView.findViewById(R.id.activeCount);
            deactiveCount = itemView.findViewById(R.id.deactiveCount);
            viewIncome = itemView.findViewById(R.id.viewIncome);
        }
    }


    public interface OnClick {
        void onClick(PackageResult value);
    }


}
