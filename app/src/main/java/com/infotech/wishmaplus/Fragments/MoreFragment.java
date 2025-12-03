package com.infotech.wishmaplus.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.infotech.wishmaplus.Activity.ContactUsActivity;
import com.infotech.wishmaplus.Activity.CreateNewProfilePage;
import com.infotech.wishmaplus.Activity.GroupActivity;
import com.infotech.wishmaplus.Activity.ImageZoomViewActivity;
import com.infotech.wishmaplus.Activity.IncomeReportActivity;
import com.infotech.wishmaplus.Activity.LevelCountActivity;
import com.infotech.wishmaplus.Activity.MainActivity;
import com.infotech.wishmaplus.Activity.PackageActivity;
import com.infotech.wishmaplus.Activity.ProfessionalDashboardActivity;
import com.infotech.wishmaplus.Activity.ProfileActivity;
import com.infotech.wishmaplus.Activity.ReferralActivity;
import com.infotech.wishmaplus.Activity.SettingsAndPrivacy;
import com.infotech.wishmaplus.Activity.SwitchPages;
import com.infotech.wishmaplus.Activity.YourFriends;
import com.infotech.wishmaplus.Adapter.UserPagesAdapter;
import com.infotech.wishmaplus.Adapter.UsersAdapter;
import com.infotech.wishmaplus.Api.Response.PageData;
import com.infotech.wishmaplus.Api.Response.PagesResponse;
import com.infotech.wishmaplus.Api.Response.User;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.Api.Response.UserModelResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomAlertDialog;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MoreFragment extends Fragment {


    private PreferencesManager tokenManager;
    private UserDetailResponse userDetailResponse;
    ImageView profileIcon;
    private CustomLoader loader;
    TextView nameTv,currentPackage;
    ActivityResultLauncher<Intent> launcher;

    List<PageData> list = new ArrayList<>();
    RecyclerView userRecycler;
    UserPagesAdapter adapter;

    public static BottomSheetDialog bottomSheetUser;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_more, container, false);
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Close dialog if visible
                        if (UtilMethods.bottomSheetUser != null &&
                                UtilMethods.bottomSheetUser.isShowing()) {
                            UtilMethods.bottomSheetUser.dismiss();
                        }
                    }
                }
        );
        loader = new CustomLoader(requireActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        tokenManager = ((MainActivity)requireActivity()).tokenManager;
        if(tokenManager==null) {
            tokenManager = new PreferencesManager(requireActivity(),1);
        }
        getPagesList();

        profileIcon = v.findViewById(R.id.profileIcon);
        nameTv = v.findViewById(R.id.nameTv);
        currentPackage = v.findViewById(R.id.currentPackage);
        TextView termAndPrivacyTxt = v.findViewById(R.id.term_and_privacy_txt);
        Utility.INSTANCE.setTerm_Privacy(requireActivity(), termAndPrivacyTxt,
                0, 16, 21, 35);
        userDetailResponse= UtilMethods.INSTANCE.getUserDetailResponse(tokenManager);
        if(userDetailResponse==null){
            getUserDetail();
        }else {
            setUserData();
        }
        v.findViewById(R.id.groupView).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), GroupActivity.class));
        });
        v.findViewById(R.id.professionalView).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), ProfessionalDashboardActivity.class));
        });
        v.findViewById(R.id.profileIcon).setOnClickListener(view -> {
            profileActivityResultLauncher.launch(new Intent(requireActivity(), ProfileActivity.class)
                    .putExtra("userData", userDetailResponse));
        });
        v.findViewById(R.id.userArrow).setOnClickListener(v1 -> {
           openUserBottomSheetDialog(requireActivity(),
                    userDetailResponse,launcher);
//            startActivity(new Intent(requireActivity(), CreateNewProfilePage.class));
        });

        v.findViewById(R.id.packageView).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), PackageActivity.class));
        });
        v.findViewById(R.id.referralView).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), ReferralActivity.class)
                    .putExtra("userData", userDetailResponse));
        });
        v.findViewById(R.id.levelCountView).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), LevelCountActivity.class));
        });
        v.findViewById(R.id.contactUsView).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), ContactUsActivity.class));
        });
        v.findViewById(R.id.incomeView).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), IncomeReportActivity.class));
        });
        v.findViewById(R.id.logoutView).setOnClickListener(view -> {
            signOut();
        });
        v.findViewById(R.id.settingView).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), SettingsAndPrivacy.class));
        });
        return v;
    }

    private void getUserDetail() {
        UtilMethods.INSTANCE.userDetail(requireActivity(),"0", null, tokenManager, object -> {
            userDetailResponse = (UserDetailResponse) object;
            setUserData();

        });
    }
    private void getPagesList() {
        loader.show();
        UtilMethods.INSTANCE.getPagesResponse(requireActivity(), new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                list.clear();
                PagesResponse pagesResponse = (PagesResponse) object;
                if(!pagesResponse.getResult().isEmpty()){
                    list.addAll(pagesResponse.getResult());
//                    adapter.notifyDataSetChanged();
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


    private void signOut() {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(requireActivity(), true);
        customAlertDialog.Successfullogout("Do you really want to Logout?", requireActivity(), tokenManager);
       /* mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {

            // Toast.makeText(ProfileActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();

        });*/
    }
    private void setUserData() {
        nameTv.setText(userDetailResponse.getFisrtName()+" "+userDetailResponse.getLastName());
        if(userDetailResponse.getPackageDetail()!=null) {
            currentPackage.setText(" "+userDetailResponse.getPackageDetail().getPackageName() + " (" + Utility.INSTANCE.formattedAmountWithRupees(userDetailResponse.getPackageDetail().getPackageCost())+")");
        }
        Glide.with(requireActivity())
                .load(userDetailResponse.getProfilePictureUrl())
                .apply(UtilMethods.INSTANCE.getRequestOption_With_UserIcon())
                .into(profileIcon);
    }

    ActivityResultLauncher<Intent> profileActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData()!=null) {
                    int refreshType =  result.getData().getIntExtra("RefreshType", 0);
                    if(refreshType==1){
                        //UserDetails
                        getUserDetail();
                    }


                }
            });

    @SuppressLint("SetTextI18n")
    public void openUserBottomSheetDialog(Activity activity,
                                          UserDetailResponse userDetailResponse,
                                          ActivityResultLauncher<Intent> launcher) {



        if (bottomSheetUser != null && bottomSheetUser.isShowing()) {
            return;
        }
        bottomSheetUser = new BottomSheetDialog(activity, R.style.DialogStyle);
        Objects.requireNonNull(bottomSheetUser.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inflater.inflate(R.layout.dialog_create_user, null);
        LinearLayout createUser = sheetView.findViewById(R.id.createUser);
        AppCompatTextView userName = sheetView.findViewById(R.id.userName);
        AppCompatImageView userImage = sheetView.findViewById(R.id.userImage);
        userRecycler = sheetView.findViewById(R.id.userRecycler);

        adapter = new UserPagesAdapter(requireActivity(), list,new UserPagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PageData user, int pos) {
                bottomSheetUser.dismiss();
                Intent intent = new Intent(activity, SwitchPages.class);
                intent.putExtra("imageUrl", user.getProfileImageUrl());
                intent.putExtra("pageName", user.getPageName());
                launcher.launch(intent);

            }

            @Override
            public void onMoreClicked(View anchor, PageData user, int pos) {

            }
        });
        userRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        userRecycler.setAdapter(adapter);

        if (userDetailResponse != null) {
            userName.setText(userDetailResponse.getFisrtName() + userDetailResponse.getLastName());
            Glide.with(activity)
                    .load(userDetailResponse.getProfilePictureUrl())
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .apply(UtilMethods.INSTANCE.getRequestOption_With_UserIcon())
                    .into(userImage);
        }
        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetUser.dismiss();
                Intent intent = new Intent(activity, CreateNewProfilePage.class);
                launcher.launch(intent);
            }
        });
        bottomSheetUser.setCancelable(true);
        bottomSheetUser.setContentView(sheetView);
        BottomSheetBehavior
                .from(bottomSheetUser.findViewById(com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetUser.show();

    }
}