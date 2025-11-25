package com.infotech.wishmaplus.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.infotech.wishmaplus.Activity.MainActivity;
import com.infotech.wishmaplus.Adapter.FollowersAdapter;
import com.infotech.wishmaplus.Api.Object.FollowerResult;
import com.infotech.wishmaplus.Api.Response.FollowersResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UsersFragment extends Fragment {


    private PreferencesManager tokenManager;
    private CustomLoader loader;
    private ArrayList<FollowerResult> freeList = new ArrayList<>();
    private ArrayList<FollowerResult> subscribeList = new ArrayList<>();
    RecyclerView recyclerView;
    TextView noData;
    MaterialButton freeUserBtn, subscribeUserBtn;
    private int type = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        loader = ((MainActivity)requireActivity()).loader;
        if(loader==null) {
            loader = new CustomLoader(requireActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        }
        tokenManager = ((MainActivity)requireActivity()).tokenManager;
        if(tokenManager==null) {
            tokenManager = new PreferencesManager(requireActivity(),1);
        }
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        freeUserBtn = view.findViewById(R.id.freeUserBtn);
        subscribeUserBtn = view.findViewById(R.id.subscribeUserBtn);
        noData = view.findViewById(R.id.noData);
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            getFollowers();
            ((MainActivity) requireActivity()).getBalance();
            pullToRefresh.setRefreshing(false);
        });
        freeUserBtn.setOnClickListener(view1 -> {
            if (type != 1) {
                type = 1;
                ViewCompat.setBackgroundTintList(freeUserBtn, ContextCompat.getColorStateList(requireActivity(), R.color.colorAccent));
                ViewCompat.setBackgroundTintList(subscribeUserBtn, ContextCompat.getColorStateList(requireActivity(), R.color.grey_4));
                if (freeList.size() > 0) {
                    noData.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(new FollowersAdapter(requireActivity(), freeList, null));
                } else {
                    noData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
        subscribeUserBtn.setOnClickListener(view1 -> {
            if (type != 2) {
                type = 2;
                ViewCompat.setBackgroundTintList(subscribeUserBtn, ContextCompat.getColorStateList(requireActivity(), R.color.colorAccent));
                ViewCompat.setBackgroundTintList(freeUserBtn, ContextCompat.getColorStateList(requireActivity(), R.color.grey_4));
                if (subscribeList.size() > 0) {
                    noData.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(new FollowersAdapter(requireActivity(), subscribeList, null));
                } else {
                    noData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
        loader.show();
        getFollowers();
        return view;
    }


    private void getFollowers() {
        try {

            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<FollowersResponse> call = git.getFollower("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<FollowersResponse>() {
                @Override
                public void onResponse(@NonNull Call<FollowersResponse> call, @NonNull Response<FollowersResponse> response) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    try {
                        FollowersResponse followersResponse = response.body();
                        if (followersResponse != null) {
                            if (followersResponse.getStatusCode() == 1) {
                                if (followersResponse.getResult() != null && followersResponse.getResult().size() > 0) {
                                    freeList.clear();
                                    subscribeList.clear();
                                    freeUserBtn.setText("Free (" + followersResponse.getUnSub() + ")");
                                    subscribeUserBtn.setText("Subscribe (" + followersResponse.getSub() + ")");
                                    for (FollowerResult item : followersResponse.getResult()) {

                                        if (item.isSubscribed()) {
                                            subscribeList.add(item);
                                        } else {
                                            freeList.add(item);
                                        }


                                    }

                                    if (type == 1 && freeList.size() > 0) {
                                        noData.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        recyclerView.setAdapter(new FollowersAdapter(requireActivity(), freeList, null));
                                    } else if (type == 2 && subscribeList.size() > 0) {
                                        noData.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        recyclerView.setAdapter(new FollowersAdapter(requireActivity(), subscribeList, null));
                                    } else {
                                        noData.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    }


                                }
                            } else {
                                UtilMethods.INSTANCE.Error(requireActivity(), followersResponse.getResponseText());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilMethods.INSTANCE.Error(requireActivity(), e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<FollowersResponse> call, @NonNull Throwable t) {
                    try {
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }
                        UtilMethods.INSTANCE.apiFailureError(requireActivity(), t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(requireActivity(), ise.getMessage());
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            UtilMethods.INSTANCE.Error(requireActivity(), e.getMessage());
            if (loader != null) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }
            }
        }
    }
}