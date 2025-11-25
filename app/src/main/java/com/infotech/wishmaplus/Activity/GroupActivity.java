package com.infotech.wishmaplus.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.infotech.wishmaplus.Adapter.DialogReportBottomSheetAdapter;
import com.infotech.wishmaplus.Adapter.GroupAdapter;
import com.infotech.wishmaplus.Api.Object.ReportReasonResult;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<GroupModel> list;
    TextView tvYourGroups, tvJumpBack, manageGroup,tvSort;
    LinearLayout layoutPosts,yourGroups;
    ScrollView layoutManage;
    int selectedFilter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvYourGroups = findViewById(R.id.tvYourGroups);
        tvJumpBack = findViewById(R.id.tvJumpBack);
        manageGroup = findViewById(R.id.manageGroup);
        layoutPosts = findViewById(R.id.layoutPosts);
        layoutManage = findViewById(R.id.layoutManage);
        yourGroups = findViewById(R.id.yourGroups);
        tvSort = findViewById(R.id.tvSort);
        showYourGroups();

        tvYourGroups.setOnClickListener(v -> showYourGroups());
        tvJumpBack.setOnClickListener(v -> showPosts());
        manageGroup.setOnClickListener(v -> showManage());
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        findViewById(R.id.addGroupButton).setOnClickListener(v -> openReportBottomSheetDialog(this));
        findViewById(R.id.sortSection).setOnClickListener(v -> openSortBottomSheetDialog(this));
        findViewById(R.id.groupBottom).setOnClickListener(v -> openGroupsBottomSheetDialog(this));



        // Default: show Your Groups


        recyclerView = findViewById(R.id.recyclerGroups);

        list = new ArrayList<>();
        list.add(new GroupModel("Office Group", "25+ new posts", R.drawable.user_icon));
        list.add(new GroupModel("The Wishma", "25+ new posts", R.drawable.user_icon));
        list.add(new GroupModel("Family Group", "25+ new posts", R.drawable.user_icon));
        list.add(new GroupModel("Tuition Group", "25+ new posts", R.drawable.user_icon));
        list.add(new GroupModel("College Group", "25+ new posts", R.drawable.user_icon));

        GroupAdapter adapter = new GroupAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }
    BottomSheetDialog bottomSheetDialogReport;
    BottomSheetDialog bottomSortDialogReport;
    BottomSheetDialog bottomGroupsDialogReport;

    public  void openReportBottomSheetDialog(Activity context){
        if (bottomSheetDialogReport != null && bottomSheetDialogReport.isShowing()) {
            return;
        }
        bottomSheetDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        bottomSheetDialogReport.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inflater.inflate(R.layout.bottom_sheet, null);
        LinearLayout btnCreatePost = sheetView.findViewById(R.id.btnCreatePost);
        LinearLayout btnCreateGroup = sheetView.findViewById(R.id.btnCreateGroup);
        btnCreatePost.setOnClickListener(v -> {
            bottomSheetDialogReport.dismiss();
        });

        btnCreateGroup.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateGroupActivity.class));
            bottomSheetDialogReport.dismiss();
        });



        bottomSheetDialogReport.setContentView(sheetView);
        BottomSheetBehavior
                .from(bottomSheetDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialogReport.show();

    }
    public void openSortBottomSheetDialog(Activity context) {

        if (bottomSortDialogReport != null && bottomSortDialogReport.isShowing()) return;

        bottomSortDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_sort_groups, null);

        RadioButton radioMostVisited = sheetView.findViewById(R.id.radioMostVisited);
        RadioButton radioAZ = sheetView.findViewById(R.id.radioAZ);
        RadioButton radioRecentlyJoined = sheetView.findViewById(R.id.radioRecentlyJoined);

        // Pre-select based on saved filter
        switch (selectedFilter) {
            case 1: radioMostVisited.setChecked(true); break;
            case 2: radioAZ.setChecked(true); break;
            case 3: radioRecentlyJoined.setChecked(true); break;
        }

        bottomSortDialogReport.setContentView(sheetView);

        // Common click listener to avoid code repetition
        View.OnClickListener clickListener = v -> {
            int id = v.getId();

            radioMostVisited.setChecked(id == R.id.option_most_visited);
            radioAZ.setChecked(id == R.id.option_a_z);
            radioRecentlyJoined.setChecked(id == R.id.option_recent);

            if (id == R.id.option_most_visited) {
                selectedFilter = 1;
                tvSort.setText("Sort by: Most visited");
            } else if (id == R.id.option_a_z) {
                selectedFilter = 2;
                tvSort.setText("Sort by: Groups A-Z");
            } else if (id == R.id.option_recent) {
                selectedFilter = 3;
                tvSort.setText("Sort by: Recently joined");
            }

            bottomSortDialogReport.dismiss();
        };

        sheetView.findViewById(R.id.option_most_visited).setOnClickListener(clickListener);
        sheetView.findViewById(R.id.option_a_z).setOnClickListener(clickListener);
        sheetView.findViewById(R.id.option_recent).setOnClickListener(clickListener);

        BottomSheetBehavior.from(
                bottomSortDialogReport.findViewById(
                        com.google.android.material.R.id.design_bottom_sheet
                )
        ).setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSortDialogReport.show();
    }

    public  void openGroupsBottomSheetDialog(Activity context){
        if (bottomGroupsDialogReport != null && bottomGroupsDialogReport.isShowing()) {
            return;
        }
        bottomGroupsDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        bottomGroupsDialogReport.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inflater.inflate(R.layout.bottom_sheet_groups, null);



        bottomGroupsDialogReport.setContentView(sheetView);
        BottomSheetBehavior
                .from(bottomGroupsDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomGroupsDialogReport.show();

    }
    private void showYourGroups() {
        yourGroups.setVisibility(View.VISIBLE);
        layoutPosts.setVisibility(View.GONE);
        layoutManage.setVisibility(View.GONE);

        highlightTab(tvYourGroups);
    }

    private void showPosts() {
        yourGroups.setVisibility(View.GONE);
        layoutPosts.setVisibility(View.VISIBLE);
        layoutManage.setVisibility(View.GONE);

        highlightTab(tvJumpBack);
    }

    private void showManage() {
        yourGroups.setVisibility(View.GONE);
        layoutPosts.setVisibility(View.GONE);
        layoutManage.setVisibility(View.VISIBLE);

        highlightTab(manageGroup);
    }

    private void highlightTab(TextView selectedTab) {

        // Reset all tabs
        tvYourGroups.setTextColor(Color.parseColor("#888888"));
        tvJumpBack.setTextColor(Color.parseColor("#888888"));
        manageGroup.setTextColor(Color.parseColor("#888888"));

        tvYourGroups.setTypeface(null, Typeface.NORMAL);
        tvJumpBack.setTypeface(null, Typeface.NORMAL);
        manageGroup.setTypeface(null, Typeface.NORMAL);

        // Highlight selected tab
        selectedTab.setTextColor(Color.BLACK);
        selectedTab.setTypeface(null, Typeface.BOLD);
    }



}
