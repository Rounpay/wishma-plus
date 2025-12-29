package com.infotech.wishmaplus.Activity;

import static android.view.View.GONE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.infotech.wishmaplus.Adapter.DialogReportBottomSheetAdapter;
import com.infotech.wishmaplus.Adapter.GroupAdapter;
import com.infotech.wishmaplus.Adapter.GroupAdapterManage;
import com.infotech.wishmaplus.Api.Object.ReportReasonResult;
import com.infotech.wishmaplus.Api.Response.AddPeopleResponse;
import com.infotech.wishmaplus.Api.Response.GroupDetailsResponse;
import com.infotech.wishmaplus.Api.Response.GroupListResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class GroupActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<GroupModel> list;
    TextView tvYourGroups, tvJumpBack, manageGroup,tvSort,groupName,groupType;
    LinearLayout layoutPosts,yourGroups,members,activeGroup;
    ScrollView layoutManage;
    int selectedFilter = 0;
    GroupAdapter adapter;
    private CustomLoader loader;
    GroupListResponse groupListResponse;
    ImageView groupImage;
    PreferencesManager tokenManager;

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
        groupImage = findViewById(R.id.groupImage);
        groupName = findViewById(R.id.groupName);
        groupType = findViewById(R.id.groupType);
        members = findViewById(R.id.members);
        tvSort = findViewById(R.id.tvSort);
        activeGroup = findViewById(R.id.activeGroup);
        tokenManager = new PreferencesManager(GroupActivity.this, 1);
        showYourGroups();
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        tvYourGroups.setOnClickListener(v -> showYourGroups());
        tvJumpBack.setOnClickListener(v -> showPosts());
        manageGroup.setOnClickListener(v -> showManage());
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        findViewById(R.id.addGroupButton).setOnClickListener(v -> openReportBottomSheetDialog(this));
        findViewById(R.id.sortSection).setOnClickListener(v -> openSortBottomSheetDialog(this));
        findViewById(R.id.groupBottom).setOnClickListener(v -> openGroupsBottomSheetDialog(this));
        members.setOnClickListener(view -> {
            String savedPageId = tokenManager.getString("ACTIVE_GROUP_ID");
            Intent intent = new Intent(GroupActivity.this, GroupMembers.class);
            intent.putExtra("groupId",savedPageId);
            startActivity(intent);
        });
        activeGroup.setOnClickListener(view -> {
            String savedPageId = tokenManager.getString("ACTIVE_GROUP_ID");
            Intent intent = new Intent(GroupActivity.this, GroupDashboard.class);
            intent.putExtra("groupId", savedPageId);
            startActivity(intent);
        });



        // Default: show Your Groups


        recyclerView = findViewById(R.id.recyclerGroups);



    }

    @Override
    protected void onResume() {
        getGroupsListing(false);
        getGroupsListingMy(true);
        super.onResume();
    }

    public void getGroupsListing(boolean OnlyMyGroups){
        loader.show();
        UtilMethods.INSTANCE.getGroupsListing(OnlyMyGroups,new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                GroupListResponse groupListResponse=(GroupListResponse) object;
                if(groupListResponse.getStatusCode()==1){
                    adapter = new GroupAdapter(GroupActivity.this, groupListResponse.getResult(),(item, pos) -> {
                        Intent intent = new Intent(GroupActivity.this, GroupDashboard.class);
                        intent.putExtra("groupId", item.getGroupId());
                        startActivity(intent);
                    });
                    recyclerView.setLayoutManager(new LinearLayoutManager(GroupActivity.this));
                    recyclerView.setAdapter(adapter);

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
    }
    public void getGroupsListingMy(boolean OnlyMyGroups){
        loader.show();
        UtilMethods.INSTANCE.getGroupsListing(OnlyMyGroups,new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                groupListResponse=(GroupListResponse) object;
                if(groupListResponse.getStatusCode()==1){
                    if (!groupListResponse.getResult().isEmpty())
                    {
                        String savedPageId = tokenManager.getString("ACTIVE_GROUP_ID");
                        int index = IntStream.range(0, groupListResponse.getResult().size())
                                .filter(i -> savedPageId.equals(
                                        groupListResponse.getResult().get(i).getGroupId()))
                                .findFirst()
                                .orElse(0);
                        String groupTypeValue = groupListResponse.getResult().get(index).isPrivate() ? "Private Group" : "Public Group";
                        groupName.setText(groupListResponse.getResult().get(index).getTitle());
                        groupType.setText(groupTypeValue);
                        Glide.with(GroupActivity.this).load(groupListResponse.getResult().get(index).getCoverImageUrl()).placeholder(R.drawable.user_icon).into(groupImage);
                        tokenManager.set("ACTIVE_GROUP_ID", groupListResponse.getResult().get(index).getGroupId());

                    }
                    else{
                        manageGroup.setVisibility(GONE);
                    }
                }
                else {
                    manageGroup.setVisibility(GONE);
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
        RecyclerView recyclerGroupsManage = sheetView.findViewById(R.id.recyclerGroupsManage);
        recyclerGroupsManage.setLayoutManager(new LinearLayoutManager(this));
        recyclerGroupsManage.setAdapter(new GroupAdapterManage(this, groupListResponse.getResult(),tokenManager,(item, pos) -> {
            tokenManager.set("ACTIVE_GROUP_ID", item.getGroupId());
            getGroupsListingMy(true);
            bottomGroupsDialogReport.dismiss();
        }));



        bottomGroupsDialogReport.setContentView(sheetView);
        BottomSheetBehavior
                .from(bottomGroupsDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomGroupsDialogReport.show();

    }
    private void showYourGroups() {
        yourGroups.setVisibility(View.VISIBLE);
        layoutPosts.setVisibility(GONE);
        layoutManage.setVisibility(GONE);

        highlightTab(tvYourGroups);
    }

    private void showPosts() {
        yourGroups.setVisibility(GONE);
        layoutPosts.setVisibility(View.VISIBLE);
        layoutManage.setVisibility(GONE);

        highlightTab(tvJumpBack);
    }

    private void showManage() {
        getGroupsListing(true);
        yourGroups.setVisibility(GONE);
        layoutPosts.setVisibility(GONE);
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
