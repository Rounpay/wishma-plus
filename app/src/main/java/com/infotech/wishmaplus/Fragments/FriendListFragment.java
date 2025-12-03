package com.infotech.wishmaplus.Fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.infotech.wishmaplus.Activity.FriendRequest;
import com.infotech.wishmaplus.Activity.MainActivity;
import com.infotech.wishmaplus.Activity.SentRequests;
import com.infotech.wishmaplus.Activity.SettingsAndPrivacy;
import com.infotech.wishmaplus.Activity.YourFriends;
import com.infotech.wishmaplus.Adapter.FriendListAdapter;
import com.infotech.wishmaplus.Adapter.UsersAdapter;
import com.infotech.wishmaplus.Api.Response.User;
import com.infotech.wishmaplus.Api.Response.UserListFriends;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;


public class FriendListFragment extends Fragment {
    View noDataLayout;
    RecyclerView recyclerView;
    List<UserListFriends> list = new ArrayList<>();
//    FriendListAdapter adapter;
    UsersAdapter adapter;
    ArrayList<User> data = new ArrayList<>();

    BottomSheetDialog bottomSheetDialog;
    View notificationDot;

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
        notificationDot = view.findViewById(R.id.notification_dot);
        data.add(new User("Anamika Singh", "", "https://i.pravatar.cc/150?img=1"));
        data.add(new User("Akhilesh Shukla", "1 mutual friend", "https://i.pravatar.cc/150?img=2"));
        data.add(new User("Tamanna Singh ", "", "https://i.pravatar.cc/150?img=3"));
        data.add(new User("Priya Verma", "1 mutual friend", "https://i.pravatar.cc/150?img=4"));
        adapter = new UsersAdapter(requireActivity(), data, new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user, int pos) {
                // open profile
            }

            @Override
            public void onMoreClicked(View anchor, User user, int pos) {
                openBottomSheet(requireActivity());
                //)
                // show popup - sample
//                androidx.appcompat.widget.PopupMenu pm = new androidx.appcompat.widget.PopupMenu(requireActivity(), anchor);
//                pm.getMenu().add("Unfriend");
//                pm.setOnMenuItemClickListener(menuItem -> {
//                    // action
//                    return true;
//                });
//                pm.show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));

        view.findViewById(R.id.content).setOnClickListener(view1 -> {
            startActivity(new Intent(requireActivity(), YourFriends.class));
        });
        view.findViewById(R.id.sentRequest).setOnClickListener(view1 -> {
            startActivity(new Intent(requireActivity(), SentRequests.class));
        });
        view.findViewById(R.id.insights).setOnClickListener(view1 -> {
            startActivity(new Intent(requireActivity(), FriendRequest.class));
        });
//        adapter = new FriendListAdapter(getContext(), list, new UtilMethods.FriendActionListener() {
//            @Override
//            public void onAddClicked(UserListFriends user, int position) {
//                callAddFriendApi(user, position);
//            }
//
//            @Override
//            public void onProfileClick(UserListFriends user, int position) {
//
//            }
//
//            @Override
//            public void onRemoveClicked(UserListFriends user, int position) {
//                callRemoveFriendApi(user, position);
//            }
//        },false);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(adapter);
        updateEmptyView();
        hitApi();
        return view;
    }

    private void updateEmptyView() {
        if (data.isEmpty()) {
            recyclerView.setVisibility(GONE);
            noDataLayout.setVisibility(VISIBLE);
        } else {
            recyclerView.setVisibility(VISIBLE);
            noDataLayout.setVisibility(GONE);
        }
    }

    @Override
    public void onResume() {
        hitApi();
        super.onResume();
    }

    public void openBottomSheet(Activity context) {

        if (bottomSheetDialog != null && bottomSheetDialog.isShowing())
            return;

        bottomSheetDialog = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_profile_options, null);

        View unfollow = sheetView.findViewById(R.id.unfollow);
        View block = sheetView.findViewById(R.id.block);
        View unfriend = sheetView.findViewById(R.id.unfriend);

        unfollow.setOnClickListener(v -> bottomSheetDialog.dismiss());
        block.setOnClickListener(v -> bottomSheetDialog.dismiss());
        unfriend.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomSheetDialog.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetDialog.show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        hitApi();
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
        UtilMethods.INSTANCE.getFriendRequest(requireActivity(), new UtilMethods.ApiCallBackMulti() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(Object object) {
                list.clear();
                if (object instanceof List) {
                    List<UserListFriends> apiList = (List<UserListFriends>) object;
                    list.addAll(apiList);
                    if (list.size()>0){
                        notificationDot.setVisibility(VISIBLE);
                    }
                    else{
                        notificationDot.setVisibility(GONE);
                    }
//                    adapter.notifyDataSetChanged();
//                    updateEmptyView();
                }
            }

            @Override
            public void onError(String msg) {

            }
        });
    }
}
