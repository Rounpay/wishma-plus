package com.infotech.wishmaplus.Fragments.ProfessionalDashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infotech.wishmaplus.Adapter.FeatureAdapter;
import com.infotech.wishmaplus.Adapter.MessageAdapter;
import com.infotech.wishmaplus.Api.Response.FeatureItem;
import com.infotech.wishmaplus.Api.Response.MessageModel;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;


public class Engagement extends Fragment {


    public Engagement() {
    }

    public static Engagement newInstance() {
        Engagement fragment = new Engagement();
        return fragment;
    }
    RecyclerView featuresRecyclerView;
    FeatureAdapter adapter;
    List<FeatureItem> featuresList;
    List<MessageModel> list = new ArrayList<>();
    RecyclerView engagementRecyclerView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_engagement, container, false);
        featuresRecyclerView = view.findViewById(R.id.recyclerViewFeatures);
        engagementRecyclerView = view.findViewById(R.id.engagementRecyclerView);

        setFeaturesRecyclerView();
        setEngagementRecyclerView();
        return view;
    }
    public void setFeaturesRecyclerView(){
        featuresList = new ArrayList<>();
        featuresList.add(new FeatureItem(R.drawable.user_profile, "People to invite", "Grow your following"));
        featuresList.add(new FeatureItem(R.drawable.ic_bell, "Moderation Assist", "Hide certain comments"));
        featuresList.add(new FeatureItem(R.drawable.ic_users, "Events", "Organize an event."));
        featuresList.add(new FeatureItem(R.drawable.ic_users_big, "Explore people", "Similar Creators"));
        featuresList.add(new FeatureItem(R.drawable.ic_more, "more", "more"));
        // add more items as needed
        adapter = new FeatureAdapter(requireActivity(), featuresList);
        featuresRecyclerView.setAdapter(adapter);
    }
    public  void  setEngagementRecyclerView(){
        list.add(new MessageModel("Hello", "Test · 4 mins"));
        list.add(new MessageModel("Hii", "Test · 2 hrs"));

        MessageAdapter adapter = new MessageAdapter(requireActivity(), list);
        engagementRecyclerView.setAdapter(adapter);
    }
}