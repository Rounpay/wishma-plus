package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CategoryFilterAdapter extends ArrayAdapter<String> {

    private final List<String> originalList;
    private List<String> filteredList;

    public CategoryFilterAdapter(@NonNull Context context, int resource, @NonNull List<String> items) {
        super(context, resource, items);
        this.originalList = new ArrayList<>(items);
        this.filteredList = new ArrayList<>(items);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return filteredList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    // -----------------------------
      /*  CUSTOM SEARCH FILTER */
    // -----------------------------
    private final Filter nameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<String> resultList = new ArrayList<>();

            // FIXED FOR ALL APIs
            if (constraint == null || TextUtils.isEmpty(constraint)) {
                resultList = new ArrayList<>(originalList);
            } else {

                String query = constraint.toString().toLowerCase().trim();

                List<String> startsWithList = new ArrayList<>();
                List<String> containsList = new ArrayList<>();
                List<String> othersList = new ArrayList<>();

                for (String name : originalList) {

                    String low = name.toLowerCase();

                    if (low.startsWith(query)) {
                        startsWithList.add(name);  // Priority 1
                    } else if (low.contains(query)) {
                        containsList.add(name);    // Priority 2
                    } else {
                        othersList.add(name);      // Priority 3
                    }
                }

                resultList.addAll(startsWithList);
                resultList.addAll(containsList);
                resultList.addAll(othersList);
            }

            FilterResults results = new FilterResults();
            results.values = resultList;
            results.count  = resultList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (List<String>) results.values;
            notifyDataSetChanged();
        }
    };
}

