package com.infotech.wishmaplus.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Api.Object.ReportReasonResult;
import com.infotech.wishmaplus.R;

import java.util.List;

public class DialogReportBottomSheetAdapter extends RecyclerView.Adapter<DialogReportBottomSheetAdapter.ViewHolder> {

    private List<ReportReasonResult> listItem;
    int selectedIndex = -1;
    ClickCallBack clickCallBack;

    public DialogReportBottomSheetAdapter(List<ReportReasonResult> listItem, ClickCallBack clickCallBack) {
        this.listItem = listItem;
        this.clickCallBack = clickCallBack;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dialog_report_bottom_sheet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ReportReasonResult contentResult = listItem.get(position);


        holder.txt.setText(contentResult.getReason());
        holder.txt.setChecked(contentResult.isSelected());

        holder.txt.setOnClickListener(view -> {
            if (selectedIndex != -1) {
                listItem.get(selectedIndex).setSelected(false);
                notifyItemChanged(selectedIndex);
                selectedIndex = position;
                listItem.get(selectedIndex).setSelected(true);
                notifyItemChanged(selectedIndex);

                if(clickCallBack!=null){
                    clickCallBack.onClick(listItem.get(selectedIndex).getId());
                }
            }else {
                selectedIndex = position;
                listItem.get(selectedIndex).setSelected(true);
                notifyItemChanged(selectedIndex);

                if(clickCallBack!=null){
                    clickCallBack.onClick(listItem.get(selectedIndex).getId());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton txt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.txt);
        }
    }

    public interface ClickCallBack {
        void onClick(int id);
    }


}
