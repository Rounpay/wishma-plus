package com.infotech.wishmaplus.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomAlertDialog;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.Utility;

public class MoreFragment extends Fragment {


    private PreferencesManager tokenManager;
    private UserDetailResponse userDetailResponse;
    ImageView profileIcon;
    TextView nameTv,currentPackage;
    ActivityResultLauncher<Intent> launcher;
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
        tokenManager = ((MainActivity)requireActivity()).tokenManager;
        if(tokenManager==null) {
            tokenManager = new PreferencesManager(requireActivity(),1);
        }

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
        /*    UtilMethods.INSTANCE.openUserBottomSheetDialog(requireActivity(),
                    userDetailResponse,launcher);*/
            startActivity(new Intent(requireActivity(), CreateNewProfilePage.class));
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
        return v;
    }

    private void getUserDetail() {
        UtilMethods.INSTANCE.userDetail(requireActivity(),"0", null, tokenManager, object -> {
            userDetailResponse = (UserDetailResponse) object;
            setUserData();

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
}