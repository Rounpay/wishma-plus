package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.infotech.wishmaplus.Api.Response.FolderModel;
import com.infotech.wishmaplus.R;

import java.util.List;

public class FolderSpinnerAdapter extends ArrayAdapter<FolderModel> {

    Context context;
    List<FolderModel> list;

    public FolderSpinnerAdapter(Context context, List<FolderModel> list) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.gallery_item_spinner, parent, false);
        }

        TextView txt = convertView.findViewById(R.id.txtFolder);
        txt.setText(list.get(position).getFolderName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_spinner_dropdown, parent, false);
        }

        TextView txt = convertView.findViewById(R.id.txtFolder);
        txt.setText(list.get(position).getFolderName());

        return convertView;
    }
}
