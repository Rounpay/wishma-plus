package com.infotech.wishmaplus.Utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.infotech.wishmaplus.Activity.CreateNewProfilePage;
import com.infotech.wishmaplus.Adapter.DialogListBottomSheetAdapter;
import com.infotech.wishmaplus.Adapter.DialogReportBottomSheetAdapter;
import com.infotech.wishmaplus.Api.Object.CommentResult;
import com.infotech.wishmaplus.Api.Object.ReportReasonResult;
import com.infotech.wishmaplus.Api.Request.CommentRequest;
import com.infotech.wishmaplus.Api.Request.LikeRequest;
import com.infotech.wishmaplus.Api.Request.ReportPostRequest;
import com.infotech.wishmaplus.Api.Response.BasicListResponse;
import com.infotech.wishmaplus.Api.Response.BasicObjectResponse;
import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.Api.Response.Income;
import com.infotech.wishmaplus.Api.Response.LikeResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public enum UtilMethods {
    INSTANCE;

    public int TEXT_TYPE = 1;
    public int VIDEO_TYPE = 2;
    public int IMAGE_TYPE = 3;

    public ArrayList<ReportReasonResult> reportReasonResultList = new ArrayList<>();

    private RequestOptions requestOptions, requestOptionsPlaceHolder, requestOptionsUserIcon, requestOptionsUserIconSquare, requestOptionsCoverImage;
    private UserDetailResponse userDetailResponse;
    private PreferencesManager tokenManager;
    private Gson gson;
    public DownloadManager downloadManager;
    public BottomSheetDialog bottomSheetDialogList,
            personalInformation,bottomDateDialogDateRange,bottomSheetInsights;
    public BottomSheetDialog bottomSheetDialogReport, followDialog;
    public static BottomSheetDialog bottomSheetUser;
    int selectedDateRange = 28;


    //public HashMap<Long, String> downloadIdMap= new HashMap<>();
    public Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public void SuccessfulWithFinsh(final Activity context, boolean isCancelable, final String message, int typeId) {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context, true);
        customAlertDialog.SuccessfulWithFinsh(isCancelable, message, typeId);
    }

    public void SuccessfulWithDismiss(final Activity context, final String message) {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context, true);
        customAlertDialog.SuccessfulWithDismiss(message, context);
    }

    public void Successfulok(final String message, Activity activity) {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(activity, true);
        customAlertDialog.Successfulok(message, activity);
    }

    public void Error(final Activity context, final String message) {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context, true);
        customAlertDialog.Error(message);
    }

    public void Success(final Activity context, final String message) {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context, true);
        customAlertDialog.Successful(message);
    }

    public void NetworkError(final Activity context, String title, final String message) {
        new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .setCustomImage(R.drawable.ic_connection_lost_24dp)
                .show();
    }

    public boolean isNetworkAvialable(Activity context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void setRecentLogin(Activity context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(ApplicationConstant.INSTANCE.prefNamePref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ApplicationConstant.INSTANCE.regRecentLoginPref, key);
        editor.commit();
    }

    public String getRecentLogin(Activity context) {
        SharedPreferences myPrefs = context.getSharedPreferences(ApplicationConstant.INSTANCE.prefNamePref, Context.MODE_PRIVATE);
        String key = myPrefs.getString(ApplicationConstant.INSTANCE.regRecentLoginPref, null);
        return key;
    }

    public RequestOptions getRequestOption_With_UserIcon() {
        if (requestOptionsUserIcon != null) {
            return requestOptionsUserIcon;
        } else {
            requestOptionsUserIcon = new RequestOptions()
                    .placeholder(R.drawable.user_icon)
                    .error(R.drawable.user_icon)
                    //.diskCacheStrategy(DiskCacheStrategy.NONE)
                    //.skipMemoryCache(true)
                    .transform(new CircleCrop());
            return requestOptionsUserIcon;
        }
    }

    public RequestOptions getRequestOption_With_UserIcon_square() {
        if (requestOptionsUserIconSquare != null) {
            return requestOptionsUserIconSquare;
        } else {
            requestOptionsUserIconSquare = new RequestOptions()
                    .placeholder(R.drawable.user_icon)
                    .error(R.drawable.user_icon);
            //.diskCacheStrategy(DiskCacheStrategy.NONE)
            //.skipMemoryCache(true)
            return requestOptionsUserIconSquare;
        }
    }

    public RequestOptions getRequestOption_With_CoverImage() {
        if (requestOptionsCoverImage != null) {
            return requestOptionsCoverImage;
        } else {
            requestOptionsCoverImage = new RequestOptions()
                    .placeholder(R.drawable.dog_cover)
                    .error(R.drawable.dog_cover);
            //.diskCacheStrategy(DiskCacheStrategy.NONE)
            //.skipMemoryCache(true)
            return requestOptionsCoverImage;
        }
    }

    public RequestOptions getRequestOption_With_PlaceHolder() {
        if (requestOptionsPlaceHolder != null) {
            return requestOptionsPlaceHolder;
        } else {
            requestOptionsPlaceHolder = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.app_logo)
                    .placeholder(R.drawable.app_logo);
            return requestOptionsPlaceHolder;
        }
    }

    public RequestOptions getRequestOption_WithOut_PlaceHolder() {
        if (requestOptions != null) {
            return requestOptions;
        } else {
            requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            return requestOptions;
        }
    }

    public UserDetailResponse getUserDetailResponse(PreferencesManager mAppPreferences) {
        if (userDetailResponse != null) {
            return userDetailResponse;
        } else {
            userDetailResponse = getGson().fromJson(mAppPreferences.getString(ApplicationConstant.INSTANCE.ProfilePref), UserDetailResponse.class);
            return userDetailResponse;
        }
    }

    public void apiErrorHandle(Activity context, int code, String msg) {
        if (code == 401) {
            ErrorWithTitle(context, "UNAUTHENTICATED " + code, msg);
        } else if (code == 404) {
            ErrorWithTitle(context, "API ERROR " + code, msg);
        } else if (code >= 400 && code < 500) {
            ErrorWithTitle(context, "CLIENT ERROR " + code, msg);
        } else if (code >= 500 && code < 600) {

            ErrorWithTitle(context, "SERVER ERROR " + code, msg);
        } else {
            ErrorWithTitle(context, "FATAL/UNKNOWN ERROR " + code, msg);
        }
    }

    public void apiFailureError(Activity context, Throwable t) {
        if (t instanceof UnknownHostException || t instanceof IOException) {
            NetworkError(context);
        } else if (t instanceof SocketTimeoutException || t instanceof TimeoutException) {
            ErrorWithTitle(context, "TIME OUT ERROR", t.getMessage());
        } else {
            if (t.getMessage() != null && !t.getMessage().isEmpty()) {
                ErrorWithTitle(context, "FATAL ERROR", t.getMessage());
            } else {
                Error(context, context.getResources().getString(R.string.some_thing_error));
            }
        }
    }

    public void ErrorWithTitle(final Activity context, final String title, final String message) {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context, true);
        customAlertDialog.ErrorWithTitle(title, message);
    }

    public void NetworkError(final Activity context) {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context, true);
        customAlertDialog.NetworkError("Network Error!", "Slow or No Internet Connection.");
    }

    public void userDetail(Activity activity, String userID, final CustomLoader loader, PreferencesManager mAppPreferences, ApiCallBack apiCallBack) {
        try {
            tokenManager = new PreferencesManager(activity, 1);
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<UserDetailResponse> call = git.getUserDetail("Bearer " + tokenManager.getAccessToken(), userID);
            call.enqueue(new Callback<UserDetailResponse>() {
                @Override
                public void onResponse(Call<UserDetailResponse> call, Response<UserDetailResponse> response) {
                    try {

                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }
                        if (response.isSuccessful()) {
                            userDetailResponse = response.body();
                            mAppPreferences.set(ApplicationConstant.INSTANCE.ProfilePref, getGson().toJson(response.body()));
                            apiCallBack.onSuccess(userDetailResponse);
                        } else {
                            Toast.makeText(activity, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                    try {
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }
                        apiFailureError(activity, t);
                    } catch (IllegalStateException ise) {
                        Error(activity, ise.getMessage());
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
        }
    }

    public void fetchCommentsForPost(String postId, String replyId, ApiCallBackMulti apiCallBack) {
        EndPointInterface apiService = ApiClient.getClient().create(EndPointInterface.class);
        Call<ArrayList<CommentResult>> call = apiService.getComment("Bearer " + tokenManager.getAccessToken(), postId, replyId);

        call.enqueue(new Callback<ArrayList<CommentResult>>() {
            @Override
            public void onResponse(Call<ArrayList<CommentResult>> call, Response<ArrayList<CommentResult>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().size() > 0) {
                    if (apiCallBack != null) {
                        apiCallBack.onSuccess(response.body());
                    }
                    //showCommentsDialog(response.body(), postId, commentCountTextView);
                } else {
                    if (apiCallBack != null) {
                        apiCallBack.onError("Failed to fetch comments");
                    }
                    // Toast.makeText(context, "Failed to fetch comments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CommentResult>> call, Throwable t) {
                if (apiCallBack != null) {
                    apiCallBack.onError(t.getMessage());
                }
                // Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void postComment(Activity context, String postId, String replyId, String commentText, ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface apiService = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicObjectResponse<CommentResult>> call = apiService.commentPost("Bearer " + tokenManager.getAccessToken(), new CommentRequest(postId, replyId, commentText));

            call.enqueue(new Callback<BasicObjectResponse<CommentResult>>() {
                @Override
                public void onResponse(Call<BasicObjectResponse<CommentResult>> call, Response<BasicObjectResponse<CommentResult>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatusCode() == 1 && response.body().getResult() != null) {
                            if (apiCallBack != null) {
                                apiCallBack.onSuccess(response.body().getResult());
                            }
                        } else {
                            if (apiCallBack != null) {
                                apiCallBack.onError(response.body().getResponseText());
                            }
                            Toast.makeText(context, response.body().getResponseText(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        if (apiCallBack != null) {
                            apiCallBack.onError("Failed to post comment");
                        }
                        Toast.makeText(context, "Failed to post comment", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BasicObjectResponse<CommentResult>> call, Throwable t) {
                    // Handle failure
                    if (apiCallBack != null) {
                        apiCallBack.onError(t.getMessage());
                    }
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (apiCallBack != null) {
                apiCallBack.onError(e.getMessage());
            }
        }
    }


    public void triggerLikeApi(Activity activity, String postId, String commentId,/*ContentResult content, boolean liked, MaterialButton likeBtn, TextView likeCount, int position,*/ ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<LikeResponse> call = git.likePost("Bearer " + tokenManager.getAccessToken(), new LikeRequest(postId, commentId));
            call.enqueue(new Callback<LikeResponse>() {
                @Override
                public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatusCode() == 1) {
                            if (apiCallBack != null) {
                                apiCallBack.onSuccess(response.body().isLiked());
                            }
                        } else {
                            if (apiCallBack != null) {
                                apiCallBack.onError(response.body().getResponseText());
                            }
                            Toast.makeText(activity, response.body().getResponseText(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        if (apiCallBack != null) {
                            apiCallBack.onError("Failed to like " + (commentId.length() > 0 ? "Comment" : "post"));
                        }
                        Toast.makeText(activity, "Failed to like " + (commentId.length() > 0 ? "Comment" : "post"), Toast.LENGTH_SHORT).show();
                    }


                    // if (response.isSuccessful()) {
                    //   if(response.body()!=null){}
                    //updateLikeState(/*content,*/ liked, position, likeBtn, likeCount);

                    // }
                }

                @Override
                public void onFailure(Call<LikeResponse> call, Throwable t) {
                    if (apiCallBack != null) {
                        apiCallBack.onError(t.getMessage());
                    }
                    Toast.makeText(activity, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            if (apiCallBack != null) {
                apiCallBack.onError(e.getMessage());
            }
            e.printStackTrace();
        }
    }

    public void getReportReason(Activity activity, ProgressBar progress, ApiCallBack apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicListResponse<ReportReasonResult>> call = git.getReportReason("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<BasicListResponse<ReportReasonResult>>() {
                @Override
                public void onResponse(Call<BasicListResponse<ReportReasonResult>> call, Response<BasicListResponse<ReportReasonResult>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatusCode() == 1 && response.body().getResult() != null && response.body().getResult().size() > 0) {
                            if (apiCallBack != null) {
                                apiCallBack.onSuccess(response.body().getResult());
                            }
                        } else {
                            progress.setVisibility(View.GONE);
                            Error(activity, response.body().getResponseText());

                        }

                    } else {
                        progress.setVisibility(View.GONE);
                        apiErrorHandle(activity, response.code(), response.message());
                    }

                }

                @Override
                public void onFailure(Call<BasicListResponse<ReportReasonResult>> call, Throwable t) {
                    apiFailureError(activity, t);
                    progress.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Error(activity, e.getMessage());
            progress.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    public void getIncomeResponse(Activity activity, ApiCallBack apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicListResponse<Income>> call = git.getIncomeResponse("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<BasicListResponse<Income>>() {
                @Override
                public void onResponse(Call<BasicListResponse<Income>> call, Response<BasicListResponse<Income>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatusCode() == 1 && response.body().getResult() != null && response.body().getResult().size() > 0) {
                            if (apiCallBack != null) {
                                apiCallBack.onSuccess(response.body().getResult());
                            }
                        } else {
                            apiCallBack.onSuccess(new ArrayList<Income>());
                            Error(activity, response.body().getResponseText());
                        }

                    } else {
                        apiCallBack.onSuccess(new ArrayList<Income>());
                        apiErrorHandle(activity, response.code(), response.message());
                    }

                }

                @Override
                public void onFailure(Call<BasicListResponse<Income>> call, Throwable t) {
                    apiFailureError(activity, t);
                    apiCallBack.onSuccess(new ArrayList<Income>());
                }
            });
        } catch (Exception e) {
            Error(activity, e.getMessage());
            apiCallBack.onSuccess(new ArrayList<Income>());
            e.printStackTrace();
        }
    }

    public void submitReportReason(Activity activity, String postId, int reasonId, ApiCallBack apiCallBack) {
        try {
            CustomLoader loader = new CustomLoader(activity, android.R.style.Theme_Translucent_NoTitleBar);
            loader.show();
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicResponse> call = git.reportPost("Bearer " + tokenManager.getAccessToken(), new ReportPostRequest(postId, reasonId));
            call.enqueue(new Callback<BasicResponse>() {
                @Override
                public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatusCode() == 1) {
                            if (apiCallBack != null) {
                                apiCallBack.onSuccess(response.body());
                            }
                            Success(activity, response.body().getResponseText());
                        } else {
                            Error(activity, response.body().getResponseText());

                        }

                    } else {
                        apiErrorHandle(activity, response.code(), response.message());
                    }

                }

                @Override
                public void onFailure(Call<BasicResponse> call, Throwable t) {
                    apiFailureError(activity, t);
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                }
            });
        } catch (Exception e) {
            Error(activity, e.getMessage());

            e.printStackTrace();
        }
    }

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

    public void personalProfileBottomSheet(Activity context) {
        if (personalInformation != null && personalInformation.isShowing()) {
            return;
        }
        personalInformation = new BottomSheetDialog(context, R.style.DialogStyle);
        Objects.requireNonNull(personalInformation.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View sheetView = inflater.inflate(R.layout.personal_information_dialog, null);

        ImageButton closeBtn = sheetView.findViewById(R.id.closeBtn);
        AppCompatTextView nextButton = sheetView.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> personalInformation.dismiss());
        closeBtn.setOnClickListener(v -> personalInformation.dismiss());
        personalInformation.setCancelable(true);
        personalInformation.setContentView(sheetView);
        BottomSheetBehavior
                .from(Objects.requireNonNull(personalInformation.findViewById(com.google.android.material.R.id.design_bottom_sheet)))
                .setState(BottomSheetBehavior.STATE_EXPANDED);
        personalInformation.show();

    }

    public void ProfessionalProfileBottomSheet(Activity context) {
        if (personalInformation != null && personalInformation.isShowing()) {
            return;
        }
        personalInformation = new BottomSheetDialog(context, R.style.DialogStyle);
        Objects.requireNonNull(personalInformation.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View sheetView = inflater.inflate(R.layout.professional_information_dialog, null);

        ImageButton closeBtn = sheetView.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(v -> personalInformation.dismiss());
        personalInformation.setCancelable(true);
        personalInformation.setContentView(sheetView);
        BottomSheetBehavior
                .from(Objects.requireNonNull(personalInformation.findViewById(com.google.android.material.R.id.design_bottom_sheet)))
                .setState(BottomSheetBehavior.STATE_EXPANDED);
        personalInformation.show();

    }

    public void openFollowBottomSheetDialog(Activity context, String userId, ApiCallBackMulti apiCallBack) {

        if (followDialog != null && followDialog.isShowing())
            return;

        followDialog = new BottomSheetDialog(context, R.style.DialogStyle);
        followDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inflater.inflate(R.layout.dialog_follow_unfollow, null);

        MaterialButton unfollowBtn = sheetView.findViewById(R.id.unfollowBtn);
        MaterialButton cancelBtn = sheetView.findViewById(R.id.cancelBtn);

        unfollowBtn.setOnClickListener(v -> {
            doFollow(context, userId, apiCallBack);
            followDialog.dismiss();
        });

        cancelBtn.setOnClickListener(v -> followDialog.dismiss());

        followDialog.setCancelable(true);
        followDialog.setContentView(sheetView);

        BottomSheetBehavior.from(
                followDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        ).setState(BottomSheetBehavior.STATE_EXPANDED);

        followDialog.show();
    }


    public void doFollow(Activity context, String userId, ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<LikeResponse> call = git.DoFollow("Bearer " + tokenManager.getAccessToken(), userId);
            call.enqueue(new Callback<LikeResponse>() {
                @Override
                public void onResponse(@NonNull Call<LikeResponse> call, @NonNull Response<LikeResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatusCode() == 1 || response.body().getStatusCode() == -1) {
                            if (apiCallBack != null) {
                                apiCallBack.onSuccess(response.body().getStatusCode());
                            }
                        } else {
                            if (apiCallBack != null) {
                                apiCallBack.onError(response.body().getResponseText());
                            }
                            Toast.makeText(context, response.body().getResponseText(), Toast.LENGTH_SHORT).show();
                        }

                    }/* else {
                        if (apiCallBack != null) {
                            apiCallBack.onError("Failed to like " + (commentId.length() > 0 ? "Comment" : "post"));
                        }
                        Toast.makeText(context, "Failed to like " + (commentId.length() > 0 ? "Comment" : "post"), Toast.LENGTH_SHORT).show();
                    }*/
                }

                @Override
                public void onFailure(@NonNull Call<LikeResponse> call, @NonNull Throwable t) {
                    if (apiCallBack != null) {
                        apiCallBack.onError(t.getMessage());
                    }
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            if (apiCallBack != null) {
                apiCallBack.onError(e.getMessage());
            }
            e.printStackTrace();
        }
    }

    public void selectDateRangeBottomSheet(Activity context,AppCompatTextView tvDropdownText, OnDateRangeSelected callback) {

        if (bottomDateDialogDateRange != null && bottomDateDialogDateRange.isShowing())
            return;

        bottomDateDialogDateRange = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_date_range, null);

        RadioButton rToday = sheetView.findViewById(R.id.rbToday);
        RadioButton r7 = sheetView.findViewById(R.id.rbLast7);
        RadioButton r14 = sheetView.findViewById(R.id.rbLast14);
        RadioButton r28 = sheetView.findViewById(R.id.rbLast28);
        RadioButton r90 = sheetView.findViewById(R.id.rbLast90);
        switch (selectedDateRange) {
            case 1:
                rToday.setChecked(true);
                break;
            case 7:
                r7.setChecked(true);
                break;
            case 14:
                r14.setChecked(true);
                break;
            case 28:
                r28.setChecked(true);
                break;
            case 90:
                r90.setChecked(true);
                break;
        }

        bottomDateDialogDateRange.setContentView(sheetView);

        @SuppressLint("SetTextI18n") View.OnClickListener listener = v -> {
            int id = v.getId();

            rToday.setChecked(id == R.id.option_today);
            r7.setChecked(id == R.id.rbLast7);
            r14.setChecked(id == R.id.rbLast14);
            r28.setChecked(id == R.id.rbLast28);
            r90.setChecked(id == R.id.rbLast90);

            int idSelected = 28;
            if (id == R.id.option_today) {
                idSelected = 1;
                selectedDateRange = 1;
                tvDropdownText.setText("Today");
            } else if (id == R.id.rbLast7) {
                idSelected = 7;
                selectedDateRange = 7;
                tvDropdownText.setText("Last 7 days");
            } else if (id == R.id.rbLast14) {
                idSelected = 14;
                selectedDateRange = 14;
                tvDropdownText.setText("Last 14 days");
            } else if (id == R.id.rbLast28) {
                idSelected = 28;
                selectedDateRange = 28;
                tvDropdownText.setText("Last 28 days");
            } else if (id == R.id.rbLast90) {
                idSelected = 90;
                selectedDateRange = 90;
                tvDropdownText.setText("Last 90 days");
            }

            bottomDateDialogDateRange.dismiss();
            if (callback != null) {
                callback.onSelected(idSelected);
            }
        };

        sheetView.findViewById(R.id.option_today).setOnClickListener(listener);
        sheetView.findViewById(R.id.rbLast7).setOnClickListener(listener);
        sheetView.findViewById(R.id.rbLast14).setOnClickListener(listener);
        sheetView.findViewById(R.id.rbLast28).setOnClickListener(listener);
        sheetView.findViewById(R.id.rbLast90).setOnClickListener(listener);
        bottomDateDialogDateRange.show();
    }

    public void InsightsBottomSheetDialog(Activity context) {
        if (bottomSheetInsights != null && bottomSheetInsights.isShowing())
            return;
        bottomSheetInsights = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_insights_bottom_sheet, null);
        ImageButton closeBtn = sheetView.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(v -> bottomSheetInsights.dismiss());
        AppCompatTextView description =sheetView.findViewById(R.id.description1);

        SpannableString spannableString = getInsightsViewsDescription(context);

        description.setText(spannableString);
        description.setMovementMethod(LinkMovementMethod.getInstance());
        description.setHighlightColor(Color.TRANSPARENT);

        bottomSheetInsights.setCancelable(true);
        bottomSheetInsights.setContentView(sheetView);
        BottomSheetBehavior
                .from(Objects.requireNonNull(bottomSheetInsights.findViewById(com.google.android.material.R.id.design_bottom_sheet)))
                .setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetInsights.show();
    }

    @NonNull
    private static SpannableString getInsightsViewsDescription(Activity context) {
        String fullText = "The number of times your content was viewed. Content includes reels, posts, stories and ads. Learn More";
        SpannableString spannableString = new SpannableString(fullText);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(context, "Learn More Clicked!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#0066FF"));
                ds.setUnderlineText(false);
            }
        };

        int start = fullText.indexOf("Learn More");
        int end = start + "Learn More".length();

        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public interface OnDateRangeSelected {
        void onSelected(int selectedId);
    }
    public interface ApiCallBackMulti {
        void onSuccess(Object object);

        void onError(String msg);
    }

    public interface ApiCallBack {
        void onSuccess(Object object);
    }

    public String covertTimeToText(String dataDate) {
        String convTime = "";
        if (dataDate != null && !dataDate.isEmpty()) {
            /*String prefix = "";*/
            String suffix = "Ago";

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date pasTime = dateFormat.parse(dataDate);

                Date nowTime = new Date();

                long dateDiff = nowTime.getTime() - pasTime.getTime();

                long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
                long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
                long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
                long day = TimeUnit.MILLISECONDS.toDays(dateDiff);

                if (second < 60) {
                    convTime = second == 0 ? "Just Now" : (second + (second > 1 ? " Seconds " : " Second ") + suffix);
                } else if (minute < 60) {
                    convTime = minute + (minute > 1 ? " Minutes " : " Minute ") + suffix;
                } else if (hour < 24) {
                    convTime = hour + (hour > 1 ? " Hours " : " Hour ") + suffix;
                } else if (day >= 7) {
                    if (day > 360) {
                        long year = day / 360;
                        convTime = year + (year > 1 ? " Years " : " Year ") + suffix;
                    } else if (day > 30) {
                        long month = day / 30;
                        convTime = month + (month > 1 ? " Months " : " Month ") + suffix;
                    } else {
                        long week = day / 7;
                        convTime = week + (week > 1 ? " Weeks " : " Week ") + suffix;
                    }
                } else if (day < 7) {
                    convTime = day + (day > 1 ? " Days " : " Day ") + suffix;
                }

            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("ConvTimeE", e.getMessage());
            }
        }
        return convTime;
    }


    public <T> void openListBottomSheetDialog(Activity context, String titleValue, ArrayList<T> list, DialogListBottomSheetAdapter.OnClick<T> onClick) {
        if (bottomSheetDialogList != null && bottomSheetDialogList.isShowing()) {
            return;
        }
        bottomSheetDialogList = new BottomSheetDialog(context, R.style.DialogStyle);
        bottomSheetDialogList.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inflater.inflate(R.layout.dialog_list_bottom_sheet, null);


        ImageButton back_button = sheetView.findViewById(R.id.back_button);
        TextView title = sheetView.findViewById(R.id.title);
        EditText searchEt = sheetView.findViewById(R.id.searchEt);
        RecyclerView recyclerView = sheetView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        DialogListBottomSheetAdapter adapter = new DialogListBottomSheetAdapter(list, onClick, bottomSheetDialogList);
        recyclerView.setAdapter(adapter);
        title.setText(titleValue);
        if (list.size() < 16) {
            searchEt.setVisibility(View.GONE);
        }
        back_button.setOnClickListener(v -> bottomSheetDialogList.dismiss());

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
       /* submitBtn.setOnClickListener(view -> {
            if (etDeviceName.getText().length() == 0) {
                etDeviceName.setError(context.getResources().getString(R.string.err_empty_field));
                etDeviceName.requestFocus();
            } else if (etDeviceSerialNo.getText().length() == 0) {
                etDeviceSerialNo.setError(context.getResources().getString(R.string.err_empty_field));
                etDeviceSerialNo.requestFocus();
            } else {
                bottomSheetDialogDeviceInfo.dismiss();
                if (mDialogDeviceInfoCallBack != null) {
                    mDialogDeviceInfoCallBack.onSubmitClick(etDeviceName.getText().toString().trim(), etDeviceSerialNo.getText().toString().trim());
                }

            }
        });*/


        bottomSheetDialogList.setCancelable(false);
        bottomSheetDialogList.setContentView(sheetView);
        BottomSheetBehavior
                .from(bottomSheetDialogList.findViewById(com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialogList.show();

    }

    public void openReportBottomSheetDialog(Activity context, String postId) {
        if (bottomSheetDialogReport != null && bottomSheetDialogReport.isShowing()) {
            return;
        }
        bottomSheetDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        bottomSheetDialogReport.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inflater.inflate(R.layout.dialog_report_bottom_sheet, null);


        ImageButton closeBtn = sheetView.findViewById(R.id.closeBtn);
        MaterialButton submitBtn = sheetView.findViewById(R.id.submitBtn);
        ProgressBar progress = sheetView.findViewById(R.id.progress);

        RecyclerView recyclerView = sheetView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        if (reportReasonResultList != null && reportReasonResultList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            submitBtn.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            DialogReportBottomSheetAdapter adapter = new DialogReportBottomSheetAdapter(reportReasonResultList, id -> {
                submitBtn.setTag(id);
            });
            recyclerView.setAdapter(adapter);
        } else {
            getReportReason(context, progress, object -> {
                recyclerView.setVisibility(View.VISIBLE);
                submitBtn.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);

                reportReasonResultList = (ArrayList<ReportReasonResult>) object;
                DialogReportBottomSheetAdapter adapter = new DialogReportBottomSheetAdapter(reportReasonResultList, id -> {
                    submitBtn.setTag(id);
                });
                recyclerView.setAdapter(adapter);
            });
        }


        closeBtn.setOnClickListener(v -> bottomSheetDialogReport.dismiss());


        submitBtn.setOnClickListener(view -> {
            if (submitBtn.getTag() != null) {
                submitReportReason(context, postId, (int) submitBtn.getTag(), object -> bottomSheetDialogReport.dismiss());
            } else {
                Toast.makeText(context, "Please select reason", Toast.LENGTH_SHORT).show();
            }
        });


        bottomSheetDialogReport.setCancelable(false);
        bottomSheetDialogReport.setContentView(sheetView);
        BottomSheetBehavior
                .from(bottomSheetDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialogReport.show();

    }


}
