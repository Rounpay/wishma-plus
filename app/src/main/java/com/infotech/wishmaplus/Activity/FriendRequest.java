package com.infotech.wishmaplus.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.infotech.wishmaplus.Adapter.FriendAdapter;
import com.infotech.wishmaplus.Adapter.FriendListAdapter;
import com.infotech.wishmaplus.Api.Response.FriendRequestResponse;
import com.infotech.wishmaplus.Api.Response.UserListFriends;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;

public class FriendRequest extends AppCompatActivity {
    RecyclerView recyclerView;
    private CustomLoader loader;
//    List<FriendRequestResponse> list;
    BottomSheetDialog bottomSheetDialog;
    View noDataLayout;
    AppCompatTextView count,heading,sort;
    List<UserListFriends> list = new ArrayList<>();
    FriendListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_friend_request);
        findViewById(R.id.back_button).setOnClickListener(view -> finish());

        recyclerView = findViewById(R.id.friendRecycler);
        count = findViewById(R.id.count);
        noDataLayout = findViewById(R.id.noDataLayout);
        heading = findViewById(R.id.heading);
        sort = findViewById(R.id.sort);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);

        findViewById(R.id.moreBTn).setOnClickListener(view -> {
            openBottomSheet(this,true);
        });

        findViewById(R.id.sort).setOnClickListener(view -> {
            openBottomSheet(this,false);
        });
        adapter = new FriendListAdapter(this, list, new UtilMethods.FriendActionListener() {
            @Override
            public void onAddClicked(UserListFriends user, int position) {
//                callAddFriendApi(user, position);
            }

            @Override
            public void onRemoveClicked(UserListFriends user, int position) {
//                callRemoveFriendApi(user, position);
            }
        },true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        updateEmptyView();
        hitApi();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateEmptyView() {
        if (list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
            count.setText("");
            count.setVisibility(View.GONE);
            heading.setVisibility(View.GONE);
            sort.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
            count.setText(list.size() + "");
            count.setVisibility(View.VISIBLE);
            heading.setVisibility(View.VISIBLE);
            sort.setVisibility(View.VISIBLE);
        }
    }

    public void openBottomSheet(Activity context,boolean isSent) {

        if (bottomSheetDialog != null && bottomSheetDialog.isShowing())
            return;

        bottomSheetDialog = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_sort, null);

        View defaultOption = sheetView.findViewById(R.id.defaultOption);
        View newestOption = sheetView.findViewById(R.id.newestOption);
        View oldestOption = sheetView.findViewById(R.id.oldestOption);
        View sentRequest = sheetView.findViewById(R.id.sentRequest);
        View viewOne = sheetView.findViewById(R.id.viewOne);
        View viewTwo = sheetView.findViewById(R.id.viewTwo);

        if (isSent){
            sentRequest.setVisibility(View.VISIBLE);
            viewOne.setVisibility(View.GONE);
            viewTwo.setVisibility(View.GONE);
            defaultOption.setVisibility(View.GONE);
            newestOption.setVisibility(View.GONE);
            oldestOption.setVisibility(View.GONE);
        }


        sentRequest.setOnClickListener(v -> {
            startActivity(new Intent(this, SentRequests.class));
            bottomSheetDialog.dismiss();
        });
        defaultOption.setOnClickListener(v -> bottomSheetDialog.dismiss());
        newestOption.setOnClickListener(v -> bottomSheetDialog.dismiss());
        oldestOption.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(sheetView);
        BottomSheetBehavior.from(
                        bottomSheetDialog.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetDialog.show();
    }
    private void hitApi() {
        loader.show();
        try{
            UtilMethods.INSTANCE.getFriendRequest(this, new UtilMethods.ApiCallBackMulti() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onSuccess(Object object) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    list.clear();
                    if (object instanceof List) {
                        List<UserListFriends> apiList = (List<UserListFriends>) object;
                        list.addAll(apiList);
                        adapter.notifyDataSetChanged();
                        updateEmptyView();
                    }
                }

                @Override
                public void onError(String msg) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }

                }
            });
        } catch (Exception e) {
            if (loader != null) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }
            }
        }

    }
}