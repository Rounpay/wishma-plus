package com.infotech.wishmaplus.Activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.infotech.wishmaplus.Adapter.UsersAdapter;
import com.infotech.wishmaplus.Api.Response.User;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;

public class YourFriends extends AppCompatActivity {

    View noDataLayout;
    RecyclerView recyclerView;
    UsersAdapter adapter;
    ArrayList<User> data = new ArrayList<>();
    BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_your_friends);
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        SwipeRefreshLayout pullToRefresh = findViewById(R.id.swipeRefreshLayout);
        pullToRefresh.setOnRefreshListener(() -> {
//            hitApi();
            pullToRefresh.setRefreshing(false);
        });
        recyclerView = findViewById(R.id.recyclerView);
        noDataLayout = findViewById(R.id.noDataLayout);
        data.add(new User("Anamika Singh", "", "https://i.pravatar.cc/150?img=1"));
        data.add(new User("Akhilesh Shukla", "1 mutual friend", "https://i.pravatar.cc/150?img=2"));
        data.add(new User("Tamanna Singh ", "", "https://i.pravatar.cc/150?img=3"));
        data.add(new User("Priya Verma", "1 mutual friend", "https://i.pravatar.cc/150?img=4"));
        adapter = new UsersAdapter(this, data, new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user, int pos) {
                // open profile
            }

            @Override
            public void onMoreClicked(View anchor, User user, int pos) {
                openBottomSheet(YourFriends.this);
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

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        updateEmptyView();
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
}