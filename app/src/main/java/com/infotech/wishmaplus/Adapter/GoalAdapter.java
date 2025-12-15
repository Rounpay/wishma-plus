package com.infotech.wishmaplus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Api.Response.GetContentDetailsToBoostResponse;
import com.infotech.wishmaplus.R;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {

    Context context;
    List<GetContentDetailsToBoostResponse.Goal> list;
    OnGoalClickListener listener;

    public GoalAdapter(Context context, List<GetContentDetailsToBoostResponse.Goal> list, OnGoalClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public interface OnGoalClickListener {
        void onGoalClick(GetContentDetailsToBoostResponse.Goal goal);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.goal_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GetContentDetailsToBoostResponse.Goal model = list.get(position);

        holder.txtGoalName.setText(model.getGoalName());
        holder.txtGoalDescription.setText(model.getGoalDescription());

        // Set icon dynamically (if required)

        int iconRes = 1;
        if(Objects.equals(model.getIconName(), "engagement")){
            iconRes = R.drawable.ic_liked_thumbs;
        }
        if(Objects.equals(model.getIconName(), "visitors")){
            iconRes = R.drawable.ic_my_team;
        }
        if(Objects.equals(model.getIconName(), "calls")){
            iconRes = R.drawable.ic_call;
        }
        holder.imgIcon.setImageResource(iconRes);

        holder.itemView.setOnClickListener(v -> listener.onGoalClick(model));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgIcon;
        TextView txtGoalName, txtGoalDescription;
        RadioButton radioGoal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtGoalName = itemView.findViewById(R.id.txtTitle);
            txtGoalDescription = itemView.findViewById(R.id.txtDesc);
            radioGoal = itemView.findViewById(R.id.radioGoal);
        }
    }
}
