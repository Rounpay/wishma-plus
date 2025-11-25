package com.infotech.wishmaplus.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.infotech.wishmaplus.Adapter.GroupAdapter;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;


public class GroupActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<GroupModel> list;
    TextView tvYourGroups, tvJumpBack, manageGroup;
    LinearLayout layoutPosts,yourGroups;
    ScrollView layoutManage;
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
        showYourGroups();

        tvYourGroups.setOnClickListener(v -> showYourGroups());
        tvJumpBack.setOnClickListener(v -> showPosts());
        manageGroup.setOnClickListener(v -> showManage());
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        findViewById(R.id.addGroupButton).setOnClickListener(v -> openReportBottomSheetDialog(this));
        findViewById(R.id.sortSection).setOnClickListener(v -> openSortBottomSheetDialog(this));



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
    public  void openSortBottomSheetDialog(Activity context){
        if (bottomSortDialogReport != null && bottomSortDialogReport.isShowing()) {
            return;
        }
        bottomSortDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        bottomSortDialogReport.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inflater.inflate(R.layout.bottom_sheet_sort_groups, null);



        bottomSortDialogReport.setContentView(sheetView);
        BottomSheetBehavior
                .from(bottomSortDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSortDialogReport.show();

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

