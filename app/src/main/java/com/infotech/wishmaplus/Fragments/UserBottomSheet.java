package com.infotech.wishmaplus.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.Utility;

public class UserBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_USER = "arg_user";

    public static UserBottomSheet newInstance(UserDetailResponse user) {
        UserBottomSheet sheet = new UserBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_USER, user);
        sheet.setArguments(bundle);
        return sheet;
    }

    private UserDetailResponse user;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.bottom_sheet_user, container, false);

        if (getArguments() != null) {
            user = (UserDetailResponse) getArguments().getParcelable(ARG_USER);
        }

        TextView name = view.findViewById(R.id.userName);
        AppCompatImageView userImage = view.findViewById(R.id.userImage);
        if(user!=null){
            name.setText(user.getFisrtName()+" "+user.getLastName());
           /* if(user.getPackageDetail()!=null) {
                currentPackage.setText(" "+userDetailResponse.getPackageDetail().getPackageName() + " (" + Utility.INSTANCE.formattedAmountWithRupees(userDetailResponse.getPackageDetail().getPackageCost())+")");
            }*/
            Glide.with(requireActivity())
                    .load(user.getProfilePictureUrl())
                    .apply(UtilMethods.INSTANCE.getRequestOption_With_UserIcon())
                    .into(userImage);
        }
        return view;
    }
}

