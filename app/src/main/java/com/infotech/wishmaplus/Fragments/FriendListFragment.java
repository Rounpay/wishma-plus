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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.infotech.wishmaplus.Activity.FriendRequest;
import com.infotech.wishmaplus.Activity.MainActivity;
import com.infotech.wishmaplus.Activity.ProfileActivity;
import com.infotech.wishmaplus.Activity.SentRequests;
import com.infotech.wishmaplus.Activity.SettingsAndPrivacy;
import com.infotech.wishmaplus.Activity.YourFriends;
import com.infotech.wishmaplus.Adapter.FriendListAdapter;
import com.infotech.wishmaplus.Adapter.FriendSuggestionAdapter;
import com.infotech.wishmaplus.Adapter.FriendSuggestionItem;
import com.infotech.wishmaplus.Adapter.FriendSuggestionResponse;
import com.infotech.wishmaplus.Adapter.SentRequestAdapter;
import com.infotech.wishmaplus.Adapter.UsersAdapter;
import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.Api.Response.SentRequestResponse;
import com.infotech.wishmaplus.Api.Response.User;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.Api.Response.UserListFriends;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;


public class FriendListFragment extends Fragment {
    View noDataLayout;
    RecyclerView recyclerView;
    List<UserListFriends> list = new ArrayList<>();
    private CustomLoader loader;
//    FriendListAdapter adapter;
    FriendSuggestionAdapter adapter;
    FriendSuggestionResponse friendSuggestionResponse = new FriendSuggestionResponse();
    ArrayList<User> data = new ArrayList<>();

    public PreferencesManager tokenManager;
    UserDetailResponse userDetailResponse;
    BottomSheetDialog bottomSheetDialog;
    View notificationDot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.swipeRefreshLayout);
        pullToRefresh.setOnRefreshListener(() -> {
            hitApi();
            getFriendSuggestionList(true);
            pullToRefresh.setRefreshing(false);
        });
        tokenManager = new PreferencesManager(requireContext(),1);
        userDetailResponse = UtilMethods.INSTANCE.getUserDetailResponse(tokenManager);
        recyclerView = view.findViewById(R.id.recyclerView);
        noDataLayout = view.findViewById(R.id.noDataLayout);
        notificationDot = view.findViewById(R.id.notification_dot);
        loader = new CustomLoader(requireContext(), android.R.style.Theme_Translucent_NoTitleBar);

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
        getFriendSuggestionList(false);
//        updateEmptyView();
        hitApi();
        return view;
    }
    private void getFriendSuggestionList(boolean isRefresh) {

        loader.show();

        UtilMethods.INSTANCE.getFriendSuggestionList(new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null && loader.isShowing()) loader.dismiss();

                friendSuggestionResponse = (FriendSuggestionResponse) object;

                if (friendSuggestionResponse.getStatusCode() == 1) {

                    adapter = new FriendSuggestionAdapter(
                            requireContext(),
                            friendSuggestionResponse.getResult(),
                            new FriendSuggestionAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(FriendSuggestionItem user, int pos) {

                                }

                                @Override
                                public void onMoreClicked(View anchor, FriendSuggestionItem user, int pos) {
                                    addFriend(user.getUserId());
                                }

                                @Override
                                public void onProfileClick(FriendSuggestionItem user, int position) {
                                    //                startActivity(new Intent(FriendRequest.this, ProfileActivity.class));
                                    profileActivityResultLauncher.launch(new Intent(requireContext(), ProfileActivity.class)
                                            .putExtra("userData", userDetailResponse)
                                            .putExtra("id", user.getUserId()));
                                }

                            }
                    );

                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    recyclerView.setAdapter(adapter);
                    recyclerView.addItemDecoration(
                            new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                    );
                    updateEmptyView();
                }

                if (isRefresh && adapter != null)
                    adapter.notifyDataSetChanged();



            }

            @Override
            public void onError(String msg) {
                if (loader != null && loader.isShowing()) loader.dismiss();
            }
        });
    }
    private void getUserDetail() {
        UtilMethods.INSTANCE.userDetail(requireActivity(), "0","", loader, tokenManager, object -> {
        });
    }
    private void addFriend(String userId) {
        loader.show();
        UtilMethods.INSTANCE.createRequest(requireActivity(), userId, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null && loader.isShowing()) loader.dismiss();
                BasicResponse basicResponse = (BasicResponse) object;
                if(basicResponse.getStatusCode()==1){
                    getFriendSuggestionList(true);

                }else{
                    UtilMethods.INSTANCE.Error(requireActivity(),basicResponse.getResponseText());
                }

            }

            @Override
            public void onError(String msg) {
                if (loader != null && loader.isShowing()) loader.dismiss();

            }
        });


    }

    private void updateEmptyView() {
        if (friendSuggestionResponse.getResult().isEmpty()) {
            recyclerView.setVisibility(GONE);
            noDataLayout.setVisibility(VISIBLE);
        } else {
            recyclerView.setVisibility(VISIBLE);
            noDataLayout.setVisibility(GONE);
        }
    }

    @Override
    public void onResume() {
        getUserDetail();
        hitApi();
        super.onResume();
    }

    ActivityResultLauncher<Intent> profileActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                    int refreshType = result.getData().getIntExtra("RefreshType", 0);
//                    if (refreshType == 1) {
//
//                    } else if (refreshType == 2) {
//
//                    } else {
//
//                    }
//
//
//                }
            });

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
//                        updateEmptyView();
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
