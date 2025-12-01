package com.infotech.wishmaplus.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.infotech.wishmaplus.Activity.MainActivity;
import com.infotech.wishmaplus.Adapter.FriendListAdapter;
import com.infotech.wishmaplus.Api.Response.UserListFriends;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;


public class FriendListFragment extends Fragment {
    View noDataLayout;
    RecyclerView recyclerView;
    List<UserListFriends> list = new ArrayList<>();
    FriendListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.swipeRefreshLayout);
        pullToRefresh.setOnRefreshListener(() -> {
            hitApi();
            pullToRefresh.setRefreshing(false);
        });
        recyclerView = view.findViewById(R.id.recyclerView);
        noDataLayout = view.findViewById(R.id.noDataLayout);
        adapter = new FriendListAdapter(getContext(), list, new UtilMethods.FriendActionListener() {
            @Override
            public void onAddClicked(UserListFriends user, int position) {
                callAddFriendApi(user, position);
            }

            @Override
            public void onRemoveClicked(UserListFriends user, int position) {
                callRemoveFriendApi(user, position);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        updateEmptyView();
        return view;
    }

    private void updateEmptyView() {
        if (list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hitApi();
    }

    private void callAddFriendApi(UserListFriends user, int position) {
        if (getActivity() == null) return;
        UtilMethods.INSTANCE.createRequest(getActivity(), user.getUserId(), new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(getContext(), "Friend Added!", Toast.LENGTH_SHORT).show();
                if (adapter != null) {
                    adapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getContext(), "Error: " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callRemoveFriendApi(UserListFriends user, int position) {
        if (getActivity() == null) return;
        UtilMethods.INSTANCE.removeRequest(getActivity(), user.getUserId(), new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (position >= 0 && position < list.size()) {
                    list.remove(position);
                    if (adapter != null) {
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, list.size());
                        updateEmptyView();
                    }
                }
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getContext(), "Error: " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hitApi() {
        if (!isAdded()) return;
        UtilMethods.INSTANCE.getFriendRequest(requireActivity(), new UtilMethods.ApiCallBack() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(Object object) {
                list.clear();
                if (object instanceof List) {
                    List<UserListFriends> apiList = (List<UserListFriends>) object;
                    list.addAll(apiList);
                }
            }
        });
    }
}
