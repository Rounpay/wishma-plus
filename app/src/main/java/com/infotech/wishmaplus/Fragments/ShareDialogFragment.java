package com.infotech.wishmaplus.Fragments;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.infotech.wishmaplus.Api.Object.ContentResult;
import com.infotech.wishmaplus.Api.Request.SharePostRequest;
import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.ApplicationConstant;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.Utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vishnu Agarwal on 11-10-2024.
 */

public class ShareDialogFragment extends BottomSheetDialogFragment {
    UserDetailResponse userDetailResponse;
    ContentResult postData;
    private PreferencesManager tokenManager;
    private CustomLoader loader;
    static CallBack mCallBack;
    /* private BottomSheetBehavior bottomSheetBehavior;*/

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Apply the custom style
        return new BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialog);
    }

    public interface CallBack{
        void onRefresh(int typeId);
    }

    public static ShareDialogFragment newInstance(ContentResult postData, CallBack callBack) {
        ShareDialogFragment fragment = new ShareDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("PostData", postData);
        fragment.setArguments(args);
        mCallBack=callBack;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_share_dialog, container, false);

         tokenManager = new PreferencesManager(requireActivity(),1);
        loader = new CustomLoader(requireActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        userDetailResponse = UtilMethods.INSTANCE.getUserDetailResponse(tokenManager);
        if (getArguments() != null) {
            postData = getArguments().getParcelable("PostData");
        }

        // Set the data in the layout
        ImageView profileIv = view.findViewById(R.id.profile);
        TextView nameTv = view.findViewById(R.id.nameTv);
        EditText textInputEt = view.findViewById(R.id.textInputEt);
        MaterialButton postBTn = view.findViewById(R.id.postBTn);

       /* textInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0 ) {
                    ViewCompat.setBackgroundTintList(postBTn, ContextCompat.getColorStateList(requireActivity(), R.color.colorAccent));
                    postBTn.setEnabled(true);
                } else {
                    ViewCompat.setBackgroundTintList(postBTn, ContextCompat.getColorStateList(requireActivity(), R.color.grey_4));
                    postBTn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
        postBTn.setOnClickListener(view1 -> sharePost(textInputEt.getText().toString().trim()));
        RequestOptions requestOptionsUserImage = UtilMethods.INSTANCE.getRequestOption_With_UserIcon();

        if (userDetailResponse != null) {
            Glide.with(this)
                    .load(userDetailResponse.getProfilePictureUrl())
                    .apply(requestOptionsUserImage)
                    .into(profileIv);
            nameTv.setText(userDetailResponse.getFisrtName() + " " + userDetailResponse.getLastName());
        }



        /*textInputEt.setOnTouchListener((v, event) -> {
            // Check if the EditText is scrolled
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Disable dragging of the BottomSheet
                bottomSheetBehavior.setDraggable(false);
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // Re-enable dragging of the BottomSheet
                bottomSheetBehavior.setDraggable(true);
            }
            return false;
        });*/
        view.findViewById(R.id.whatsappIv).setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://api.whatsapp.com/send?text=" + ApplicationConstant.INSTANCE.postUrl + postData.getPostId()));
            startActivity(intent);
        });
        view.findViewById(R.id.whatsappTv).setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://api.whatsapp.com/send?text=" + ApplicationConstant.INSTANCE.postUrl + postData.getPostId()));
            startActivity(intent);
        });
        view.findViewById(R.id.facebookIv).setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://www.facebook.com/sharer/sharer.php?u=" + ApplicationConstant.INSTANCE.postUrl + postData.getPostId() + "&quote=" + postData.getCaption()));
            startActivity(intent);
        });
        view.findViewById(R.id.facebookTv).setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://www.facebook.com/sharer/sharer.php?u=" + ApplicationConstant.INSTANCE.postUrl + postData.getPostId() + "&quote=" + postData.getCaption()));
            startActivity(intent);
        });
        view.findViewById(R.id.messengerIv).setOnClickListener(v -> {
            dismiss();
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("fb-messenger://share?link=" + URLEncoder.encode(ApplicationConstant.INSTANCE.postUrl + postData.getPostId(), "UTF-8") ));
                startActivity(intent);
            } catch (ActivityNotFoundException anfe) {
                Toast.makeText(requireActivity(), "No App found to handle this option", Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });
        view.findViewById(R.id.messengerTv).setOnClickListener(v -> {
            dismiss();
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("fb-messenger://share?link=" + URLEncoder.encode(ApplicationConstant.INSTANCE.postUrl + postData.getPostId(), "UTF-8")));
                startActivity(intent);
            } catch (ActivityNotFoundException anfe) {
                Toast.makeText(requireActivity(), "No App found to handle this option", Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });
        view.findViewById(R.id.instagramIv).setOnClickListener(v -> {
            dismiss();
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,ApplicationConstant.INSTANCE.postUrl + postData.getPostId());
                shareIntent.setPackage("com.instagram.android");
                shareIntent .setType("text/plain");
                startActivity(shareIntent);
            } catch (ActivityNotFoundException anfe) {
                Toast.makeText(requireActivity(), "No App found to handle this option", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.instagramTv).setOnClickListener(v -> {
            dismiss();
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("instagram://sharesheet?text=" + ApplicationConstant.INSTANCE.postUrl + postData.getPostId()));
                startActivity(intent);
            } catch (ActivityNotFoundException anfe) {
                Toast.makeText(requireActivity(), "No App found to handle this option", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.twitterIv).setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://twitter.com/intent/tweet?text=" + ApplicationConstant.INSTANCE.postUrl + postData.getPostId()));
            startActivity(intent);
        });
        view.findViewById(R.id.twitterTv).setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://twitter.com/intent/tweet?text=" + ApplicationConstant.INSTANCE.postUrl + postData.getPostId()));
            startActivity(intent);
        });

        view.findViewById(R.id.copyIv).setOnClickListener(v -> {
            dismiss();
            Utility.INSTANCE.setClipboard(requireActivity(),ApplicationConstant.INSTANCE.postUrl + postData.getPostId(),"Share Link");

        });
        view.findViewById(R.id.copyTv).setOnClickListener(v -> {
            dismiss();
            Utility.INSTANCE.setClipboard(requireActivity(),ApplicationConstant.INSTANCE.postUrl + postData.getPostId(),"Share Link");
        });


        view.findViewById(R.id.moreIv).setOnClickListener(v -> {
            dismiss();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Share Link");
            i.putExtra(Intent.EXTRA_TEXT, ApplicationConstant.INSTANCE.postUrl + postData.getPostId());
            startActivity(Intent.createChooser(i, "Share Link"));

        });
        view.findViewById(R.id.moreTv).setOnClickListener(v -> {
            dismiss();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Share Link");
            i.putExtra(Intent.EXTRA_TEXT, ApplicationConstant.INSTANCE.postUrl + postData.getPostId());
            startActivity(Intent.createChooser(i, "Share Link"));

        });

        return view;
    }


    private void sharePost(String caption) {
        try {

            loader.show();
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicResponse> call = git.sharePost("Bearer " + tokenManager.getAccessToken(),new SharePostRequest(postData.getPostId(),caption));
            call.enqueue(new Callback<BasicResponse>() {
                @Override
                public void onResponse(@NonNull Call<BasicResponse> call, @NonNull Response<BasicResponse> response) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    try {
                        BasicResponse packageResponse = response.body();
                        if (packageResponse != null) {
                            if (packageResponse.getStatusCode() == 1) {
                                dismiss();
                                if(mCallBack!=null){
                                    mCallBack.onRefresh(postData.getContentTypeId());
                                }
                                Toast.makeText(requireActivity(), packageResponse.getResponseText(), Toast.LENGTH_SHORT).show();
                            } else {
                                UtilMethods.INSTANCE.Error(requireActivity(), packageResponse.getResponseText());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilMethods.INSTANCE.Error(requireActivity(), e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BasicResponse> call, @NonNull Throwable t) {
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
