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
import com.infotech.wishmaplus.Utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class LevelIncomeAdapter extends RecyclerView.Adapter<LevelIncomeAdapter.ViewHolder> implements Filterable {

    /*private final RequestOptions requestOptionsPlaceHolder;*/
    private List<LevelCountResult> listItem;
    private List<LevelCountResult> filterListItem;/*
    private OnClick onClick;*/
    private FragmentActivity mActivity;
    private BottomSheetDialog bottomSheetDialog;


    public LevelIncomeAdapter(FragmentActivity mActivity, List<LevelCountResult> listItem/*, OnClick onClick*/) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_level_income, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        LevelCountResult result = filterListItem.get(position);

        holder.levelCount.setText(result.getLevel() + "");
        holder.userIdTv.setText(result.getUserId());
        holder.userNameValue.setText(result.getUserName());
        holder.packageValue.setText(result.getPackage_());


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
        TextView levelCount, userIdTv, userNameValue, packageValue;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            levelCount = itemView.findViewById(R.id.levelCount);
            userIdTv = itemView.findViewById(R.id.userIdTv);
            userNameValue = itemView.findViewById(R.id.userNameValue);
            packageValue = itemView.findViewById(R.id.packageValue);

        }
    }


    public interface OnClick {
        void onClick(PackageResult value);
    }


}
