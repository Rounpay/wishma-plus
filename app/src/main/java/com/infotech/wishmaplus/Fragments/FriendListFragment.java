package com.infotech.wishmaplus.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infotech.wishmaplus.Adapter.FriendListAdapter;
import com.infotech.wishmaplus.Api.Object.FriendModel;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;


public class FriendListFragment extends Fragment {

    RecyclerView recyclerView;
    List<FriendModel> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        list.add(new FriendModel("Anurag Gupta", "", false));
        list.add(new FriendModel("Rohit Verma", "", true));
        list.add(new FriendModel("Priya Sharma", "", false));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new FriendListAdapter(getContext(), list));

        return view;
    }
}
