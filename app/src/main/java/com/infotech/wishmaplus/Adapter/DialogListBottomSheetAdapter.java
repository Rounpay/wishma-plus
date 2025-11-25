package com.infotech.wishmaplus.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.infotech.wishmaplus.Api.Object.BankResult;
import com.infotech.wishmaplus.Api.Object.CityResult;
import com.infotech.wishmaplus.Api.Object.StateResult;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;

public class DialogListBottomSheetAdapter<T> extends RecyclerView.Adapter<DialogListBottomSheetAdapter.ViewHolder> implements Filterable {

    private List<T> listItem;
    private List<T> filterListItem;
    private OnClick<T> onClick;
    private BottomSheetDialog bottomSheetDialog;

    public DialogListBottomSheetAdapter(List<T> listItem, OnClick<T> onClick, BottomSheetDialog bottomSheetDialog) {
        this.listItem = listItem;
        this.filterListItem = listItem;
        this.onClick = onClick;
        this.bottomSheetDialog = bottomSheetDialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dialog_list_bottom_sheet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        T contentResult = filterListItem.get(position);

        if (contentResult instanceof StateResult) {
            holder.txt.setText(((StateResult) contentResult).getStateName());
        } else if (contentResult instanceof CityResult) {
            holder.txt.setText(((CityResult) contentResult).getCityName());
        } else if (contentResult instanceof BankResult) {
            holder.txt.setText(((BankResult) contentResult).getBranchName());
        } else {
            holder.txt.setText(contentResult + "");
        }


        holder.txt.setOnClickListener(view -> {
            if (onClick != null) {
                bottomSheetDialog.dismiss();
                onClick.onClick(contentResult);
            }
        });

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
                    ArrayList<T> filteredList = new ArrayList<>();
                    for (T row : listItem) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match


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
                filterListItem = (ArrayList<T>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.txt);
        }
    }


    public interface OnClick<T> {
        void onClick(T value);
    }
}
