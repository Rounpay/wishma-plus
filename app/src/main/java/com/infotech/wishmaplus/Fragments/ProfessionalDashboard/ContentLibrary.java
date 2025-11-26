package com.infotech.wishmaplus.Fragments.ProfessionalDashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infotech.wishmaplus.Adapter.ContentAdapter;
import com.infotech.wishmaplus.Adapter.FeatureAdapter;
import com.infotech.wishmaplus.Api.Response.ContentModel;
import com.infotech.wishmaplus.Api.Response.FeatureItem;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;

public class ContentLibrary extends Fragment {


    public ContentLibrary() {
        // Required empty public constructor
    }

    public static ContentLibrary newInstance() {
        ContentLibrary fragment = new ContentLibrary();
        return fragment;
    }

    RecyclerView featuresRecyclerView;
    FeatureAdapter adapter;
    List<FeatureItem> featuresList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_library, container, false);
        featuresRecyclerView = view.findViewById(R.id.recyclerViewFeatures);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerContent);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));


        List<ContentModel> list = new ArrayList<>();
        list.add(new ContentModel(R.drawable.user_icon, "27 reactions • 5 comments", "28 Jun 2020",""));
        list.add(new ContentModel(R.drawable.user_icon, "107 reactions • 45 comments", "10 Feb 2019",""));
        list.add(new ContentModel(R.drawable.user_icon, "47 reactions • 3 comments", "6 Apr 2018","Hii"));


        ContentAdapter adapter = new ContentAdapter(requireActivity(), list);
        recyclerView.setAdapter(adapter);
        // Inflate the layout for this fragment
        setFeaturesRecyclerView();
        return view;
    }
    public void setFeaturesRecyclerView(){
        featuresList = new ArrayList<>();
        featuresList.add(new FeatureItem(R.drawable.ic_calendar, "Calendar", "See your calendar events"));
        featuresList.add(new FeatureItem(R.drawable.user_profile, "Collaboration", "See your collaboration"));
        featuresList.add(new FeatureItem(R.drawable.ic_users, "Inspiration hub", "Discover fresh ideas"));
        featuresList.add(new FeatureItem(R.drawable.ic_users_big, "Explore people", "Similar Creators"));
        featuresList.add(new FeatureItem(R.drawable.ic_more, "more", "more"));
        // add more items as needed
        adapter = new FeatureAdapter(requireActivity(), featuresList);
        featuresRecyclerView.setAdapter(adapter);
    }
}