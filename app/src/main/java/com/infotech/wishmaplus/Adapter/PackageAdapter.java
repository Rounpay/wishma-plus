package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.infotech.wishmaplus.Api.Object.PackageResult;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> implements Filterable {

    private final RequestOptions requestOptionsPlaceHolder;
    private List<PackageResult> listItem;
    private List<PackageResult> filterListItem;
    private OnClick onClick;
    private FragmentActivity mActivity;
    private BottomSheetDialog bottomSheetDialog;


    public PackageAdapter(FragmentActivity mActivity, List<PackageResult> listItem, OnClick onClick) {
        this.mActivity = mActivity;
        this.listItem = listItem;
        this.filterListItem = listItem;
        this.onClick = onClick;

        requestOptionsPlaceHolder = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_package)
                .placeholder(R.drawable.ic_package);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_package_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PackageResult result = filterListItem.get(position);
        holder.packageAmt.setText(Utility.INSTANCE.formattedAmountWithRupees(result.getPackageCost()));
        holder.packageTime.setText(" /" + result.getValidityInDays() + " Days");

        if (result.isActive()) {
            holder.upgradeBtn.setVisibility(View.GONE);
            holder.activeStatus.setVisibility(View.VISIBLE);
        } else {
            holder.upgradeBtn.setVisibility(View.VISIBLE);
            holder.activeStatus.setVisibility(View.GONE);
        }
        holder.packageTv.setText(result.getPackageName());
        if(result.getExpiryInDays()!=null && !result.getExpiryInDays().isEmpty()){
            holder.expireIn.setText(result.getExpiryInDays());
            holder.expireIn.setVisibility(View.VISIBLE);
        }else{
            holder.expireIn.setVisibility(View.GONE);
        }

        holder.packageNoteTv.setText(result.getDescription());
        Glide.with(mActivity)
                .load(result.getImageUrl())
                .apply(requestOptionsPlaceHolder)
                .into(holder.packageIcon);
        holder.upgradeBtn.setOnClickListener(view -> {
            if (onClick != null && !result.isActive()) {
                onClick.onClick(result);
            }
        });

        holder.featureBtn.setOnClickListener(view -> {
            showFeaturesDialog(result);
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
                    ArrayList<PackageResult> filteredList = new ArrayList<>();
                    for (PackageResult row : listItem) {

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
                filterListItem = (ArrayList<PackageResult>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView packageTv, expireIn,packageNoteTv, packageAmt, packageTime, activeStatus, featureBtn;
        MaterialButton upgradeBtn;
        ImageView packageIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            packageIcon = itemView.findViewById(R.id.packageIcon);
            packageTv = itemView.findViewById(R.id.packageTv);
            expireIn = itemView.findViewById(R.id.expireIn);
            packageAmt = itemView.findViewById(R.id.packageAmt);
            packageTime = itemView.findViewById(R.id.packageTime);
            packageNoteTv = itemView.findViewById(R.id.packageNoteTv);
            activeStatus = itemView.findViewById(R.id.activeStatus);
            upgradeBtn = itemView.findViewById(R.id.upgradeBtn);
            featureBtn = itemView.findViewById(R.id.featureBtn);
        }
    }


    public interface OnClick {
        void onClick(PackageResult value);
    }


    void showFeaturesDialog(PackageResult packageResult) {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            return;
        }
        bottomSheetDialog = new BottomSheetDialog(mActivity, R.style.DialogStyle);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inflater.inflate(R.layout.dialog_package_features_bottom_sheet, null);


        ImageButton backBtn = sheetView.findViewById(R.id.back_button);
        //TextView title = sheetView.findViewById(R.id.title);
        ImageView packageIcon = sheetView.findViewById(R.id.packageIcon);
        TextView packageTv = sheetView.findViewById(R.id.packageTv);
        TextView expireIn = sheetView.findViewById(R.id.expireIn);
        TextView packageAmt = sheetView.findViewById(R.id.packageAmt);
        TextView packageTime = sheetView.findViewById(R.id.packageTime);
        TextView packageNoteTv = sheetView.findViewById(R.id.packageNoteTv);
        TextView dailyPostLimit = sheetView.findViewById(R.id.dailyPostLimit);
        TextView textPost = sheetView.findViewById(R.id.textPost);
        TextView imagePost = sheetView.findViewById(R.id.imagePost);
        TextView videoPost = sheetView.findViewById(R.id.videoPost);
        TextView mixing = sheetView.findViewById(R.id.mixing);
        TextView musicSystem = sheetView.findViewById(R.id.musicSystem);
        TextView activeStatus = sheetView.findViewById(R.id.activeStatus);
        MaterialButton upgradeBtn = sheetView.findViewById(R.id.upgradeBtn);
        packageAmt.setText(Utility.INSTANCE.formattedAmountWithRupees(packageResult.getPackageCost()));
        packageTime.setText(" /" + packageResult.getValidityInDays() + " Days");

        if (packageResult.isActive()) {
            upgradeBtn.setVisibility(View.GONE);
            activeStatus.setVisibility(View.VISIBLE);
        } else {
            upgradeBtn.setVisibility(View.VISIBLE);
            activeStatus.setVisibility(View.GONE);
        }

        if(packageResult.getExpiryInDays()!=null && !packageResult.getExpiryInDays().isEmpty()){
            expireIn.setText(packageResult.getExpiryInDays());
            expireIn.setVisibility(View.VISIBLE);
        }else{
            expireIn.setVisibility(View.GONE);
        }
        dailyPostLimit.setText(packageResult.getDailyPostAllowed() + "");
        textPost.setCompoundDrawablesWithIntrinsicBounds(0, 0, packageResult.isTextCanPost() ? R.drawable.ic_check_circle : R.drawable.ic_cross_circle, 0);
        imagePost.setCompoundDrawablesWithIntrinsicBounds(0, 0, packageResult.isImageCanPost() ? R.drawable.ic_check_circle : R.drawable.ic_cross_circle, 0);
        videoPost.setCompoundDrawablesWithIntrinsicBounds(0, 0, packageResult.isVideoCanPost() ? R.drawable.ic_check_circle : R.drawable.ic_cross_circle, 0);
        mixing.setCompoundDrawablesWithIntrinsicBounds(0, 0, packageResult.isMixingAllowed() ? R.drawable.ic_check_circle : R.drawable.ic_cross_circle, 0);
        musicSystem.setCompoundDrawablesWithIntrinsicBounds(0, 0, !packageResult.isMusicFromSystemOnly() ? R.drawable.ic_check_circle : R.drawable.ic_cross_circle, 0);

        packageTv.setText(packageResult.getPackageName());
        packageNoteTv.setText(packageResult.getDescription());
        Glide.with(mActivity)
                .load(packageResult.getImageUrl())
                .apply(requestOptionsPlaceHolder)
                .into(packageIcon);

        upgradeBtn.setOnClickListener(view -> {
            if (onClick != null && !packageResult.isActive()) {
                bottomSheetDialog.dismiss();
                onClick.onClick(packageResult);
            }
        });
        backBtn.setOnClickListener(v -> bottomSheetDialog.dismiss());


        //bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setContentView(sheetView);
        BottomSheetBehavior
                .from(bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.show();
    }
}
