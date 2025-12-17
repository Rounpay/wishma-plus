package com.infotech.wishmaplus.Adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Activity.CreateNewAd;
import com.infotech.wishmaplus.Activity.EditAudience;
import com.infotech.wishmaplus.Api.Response.GetContentDetailsToBoostResponse;
import com.infotech.wishmaplus.R;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;


public class AudienceAdapter extends RecyclerView.Adapter<AudienceAdapter.ViewHolder> {

    Context context;
    private int selectedPosition = 0;
    List<GetContentDetailsToBoostResponse.Audience> list;
    OnAudienceClickListener listener;
    private int minAge;
    private int maxAge;
    private String gender;

    public AudienceAdapter(Context context, List<GetContentDetailsToBoostResponse.Audience> list, OnAudienceClickListener listener,int minAge, int maxAge, String gender) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.gender = gender;
    }

    public interface OnAudienceClickListener {
        void onAudienceClick(int position,GetContentDetailsToBoostResponse.Audience goal);
        void onAudienceEditClick(int position,GetContentDetailsToBoostResponse.Audience goal,int minAge, int maxAge, String gender);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_audience, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GetContentDetailsToBoostResponse.Audience model = list.get(position);

        holder.tvAdvantageTitle.setText(model.getAudienceName());
        holder.tvAdvantageOn.setText(gender);
        holder.tvMinAge.setText(minAge+"");
        holder.tvMaxAge.setText(maxAge+"");

        if(Objects.equals(model.getAudienceName().toLowerCase(), "Advantage + Audience".toLowerCase())){
            holder.tvAdvantageSub.setVisibility(VISIBLE);
        }
        else {
            holder.tvAdvantageSub.setVisibility(GONE);
        }
        holder.boxAdvantage.setVisibility(position == selectedPosition?VISIBLE:GONE);

        holder.btnEdit.setOnClickListener(view -> {
            listener.onAudienceEditClick(
                    selectedPosition,
                    list.get(selectedPosition),minAge,maxAge,gender
            );
        });




        holder.rbAdvantage.setChecked(position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {

            int previous = selectedPosition;
            selectedPosition = position;

            if (previous != -1) {
                notifyItemChanged(previous);
            }
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onAudienceClick(
                        selectedPosition,
                        list.get(selectedPosition)
                );
            }
        });
    }
    public void updateAudience(int minAge, int maxAge, String gender) {
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.gender = gender;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View boxAdvantage;
        TextView tvAdvantageTitle, tvAdvantageSub,tvMinAge,tvMaxAge,tvAdvantageOn;
        RadioButton rbAdvantage;
        Button btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            boxAdvantage = itemView.findViewById(R.id.boxAdvantage);
            tvAdvantageTitle = itemView.findViewById(R.id.tvAdvantageTitle);
            tvAdvantageSub = itemView.findViewById(R.id.tvAdvantageSub);
            rbAdvantage = itemView.findViewById(R.id.rbAdvantage);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            tvMinAge = itemView.findViewById(R.id.tvMinAge);
            tvMaxAge = itemView.findViewById(R.id.tvMaxAge);
            tvAdvantageOn = itemView.findViewById(R.id.tvAdvantageOn);
        }
    }
}