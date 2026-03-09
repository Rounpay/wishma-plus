package com.infotech.wishmaplus.zego;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder> {

    private List<ParticipantInfo> participants = new ArrayList<>();
    private OnParticipantActionListener listener;

    public interface OnParticipantActionListener {
        void onRemoveParticipant(ParticipantInfo participant);
        void onPromoteToCoHost(ParticipantInfo participant);
        void onDemoteFromCoHost(ParticipantInfo participant);
    }

    public ParticipantAdapter(OnParticipantActionListener listener) {
        this.listener = listener;
    }

    public void setParticipants(List<ParticipantInfo> participants) {
        this.participants = participants;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.participant_item, parent, false);
        return new ParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        ParticipantInfo participant = participants.get(position);
        holder.bind(participant);
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    class ParticipantViewHolder extends RecyclerView.ViewHolder {
        TextView participantName;
        TextView coHostBadge;
        ImageButton moreOptionsButton;

        ParticipantViewHolder(@NonNull View itemView) {
            super(itemView);
            participantName = itemView.findViewById(R.id.participantName);
            coHostBadge = itemView.findViewById(R.id.coHostBadge);
            moreOptionsButton = itemView.findViewById(R.id.moreOptionsButton);
        }

        void bind(ParticipantInfo participant) {
            participantName.setText(participant.userName);
            coHostBadge.setVisibility(participant.isCoHost ? View.VISIBLE : View.GONE);

            moreOptionsButton.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), v);

//                if (participant.isCoHost) {
//                    popup.getMenu().add("Remove from Co-Host");
//                } else {
//                    popup.getMenu().add("Make Co-Host");
//                }
                popup.getMenu().add("Remove from Room");

                popup.setOnMenuItemClickListener(item -> {
                    String title = item.getTitle().toString();
                    if (title.equals("Make Co-Host")) {
                        listener.onPromoteToCoHost(participant);
                    } else if (title.equals("Remove from Co-Host")) {
                        listener.onDemoteFromCoHost(participant);
                    } else if (title.equals("Remove from Room")) {
                        listener.onRemoveParticipant(participant);
                    }
                    return true;
                });
                popup.show();
            });
        }
    }
}
