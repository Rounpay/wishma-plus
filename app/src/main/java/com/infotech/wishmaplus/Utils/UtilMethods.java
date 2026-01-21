package com.infotech.wishmaplus.Utils;

import static android.view.View.GONE;

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
import com.infotech.wishmaplus.Adapter.FriendSuggestionResponse;
import com.infotech.wishmaplus.Api.Object.CommentResult;
import com.infotech.wishmaplus.Api.Object.ReportReasonResult;
import com.infotech.wishmaplus.Api.Request.AddFriendsRequest;
import com.infotech.wishmaplus.Api.Request.BasicRequest;
import com.infotech.wishmaplus.Api.Request.CommentRequest;
import com.infotech.wishmaplus.Api.Request.ComplaintRequest;
import com.infotech.wishmaplus.Api.Request.InitiateBoostRequest;
import com.infotech.wishmaplus.Api.Request.LikeRequest;
import com.infotech.wishmaplus.Api.Request.ReportPostRequest;
import com.infotech.wishmaplus.Api.Request.UpdateGroupMemberRequest;
import com.infotech.wishmaplus.Api.Response.AddPeopleResponse;
import com.infotech.wishmaplus.Api.Response.BasicListResponse;
import com.infotech.wishmaplus.Api.Response.BasicObjectResponse;
import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.Api.Response.BoostBillingResponse;
import com.infotech.wishmaplus.Api.Response.BoostResponse;
import com.infotech.wishmaplus.Api.Response.BoostedPostStatusChangeResponse;
import com.infotech.wishmaplus.Api.Response.CategoryResponse;
import com.infotech.wishmaplus.Api.Response.ComplaintResponse;
import com.infotech.wishmaplus.Api.Response.ComplaintSubmitResponse;
import com.infotech.wishmaplus.Api.Response.CreateGroupResponse;
import com.infotech.wishmaplus.Api.Response.DeleteAccountResponse;
import com.infotech.wishmaplus.Api.Response.EligibilityModel;
import com.infotech.wishmaplus.Api.Response.EnableDashboardResponse;
import com.infotech.wishmaplus.Api.Response.EstimateResponse;
import com.infotech.wishmaplus.Api.Response.FriendListResponse;
import com.infotech.wishmaplus.Api.Response.GetContentDetailsToBoostResponse;
import com.infotech.wishmaplus.Api.Response.GetUserListResponse;
import com.infotech.wishmaplus.Api.Response.GroupDetailsResponse;
import com.infotech.wishmaplus.Api.Response.GroupListResponse;
import com.infotech.wishmaplus.Api.Response.GroupMembersResponse;
import com.infotech.wishmaplus.Api.Response.GroupMembersUpdateResponse;
import com.infotech.wishmaplus.Api.Response.Income;
import com.infotech.wishmaplus.Api.Response.InsightResponse;
import com.infotech.wishmaplus.Api.Response.InsightsStatsResponse;
import com.infotech.wishmaplus.Api.Response.LikeResponse;
import com.infotech.wishmaplus.Api.Response.LinkClickResponse;
import com.infotech.wishmaplus.Api.Response.NotificationResponse;
import com.infotech.wishmaplus.Api.Response.PagesResponse;
import com.infotech.wishmaplus.Api.Response.PostsResponse;
import com.infotech.wishmaplus.Api.Response.ReadNotificationResponse;
import com.infotech.wishmaplus.Api.Response.SentRequestResponse;
import com.infotech.wishmaplus.Api.Response.SupportCategoryResponse;
import com.infotech.wishmaplus.Api.Response.UploadGroupCoverResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.Api.Response.UserListFriends;
import com.infotech.wishmaplus.R;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
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
            personalInformation, bottomDateDialogDateRange, bottomSheetInsights;
    public BottomSheetDialog bottomSheetDialogReport, AcceptRequestDialog;
    public static BottomSheetDialog bottomSheetUser;
    int selectedDateRange = 28;
    int selectedDateRangeNew = 0;
    int selectedPostType = 0;
    int selectedMetricType = 1;


    //public HashMap<Long, String> downloadIdMap= new HashMap<>();
    public Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public void SuccessfulWithFinsh(final Activity context, boolean isCancelable, final String message, int typeId, String pageId) {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context, true);
        customAlertDialog.SuccessfulWithFinsh(isCancelable, message, typeId,pageId);
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
    public void SuccessWithOkay(final Activity context, final String message, boolean isCancelable) {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context, true);
        customAlertDialog.SuccessfulWithOkay(isCancelable,message);
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

    public void getPageDetail(Activity activity, String pageID, final CustomLoader loader, PreferencesManager mAppPreferences, ApiCallBack apiCallBack) {
        try {
            tokenManager = new PreferencesManager(activity, 1);
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<UserDetailResponse> call = git.getPageDetails("Bearer " + tokenManager.getAccessToken(), pageID);
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

    public void userDetail(Activity activity, String userID,String GroupId, final CustomLoader loader, PreferencesManager mAppPreferences, ApiCallBack apiCallBack) {
        try {
            tokenManager = new PreferencesManager(activity, 1);
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<UserDetailResponse> call = git.getUserDetail("Bearer " + tokenManager.getAccessToken(), userID,GroupId);
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
                            progress.setVisibility(GONE);
                            Error(activity, response.body().getResponseText());

                        }

                    } else {
                        progress.setVisibility(GONE);
                        apiErrorHandle(activity, response.code(), response.message());
                    }

                }

                @Override
                public void onFailure(Call<BasicListResponse<ReportReasonResult>> call, Throwable t) {
                    apiFailureError(activity, t);
                    progress.setVisibility(GONE);
                }
            });
        } catch (Exception e) {
            Error(activity, e.getMessage());
            progress.setVisibility(GONE);
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


    public interface FriendActionListener {
        void onAddClicked(UserListFriends user, int position);

        void onProfileClick(UserListFriends user, int position);

        void onRemoveClicked(UserListFriends user, int position);
    }

    public void getFriendRequest(Activity activity, ApiCallBackMulti apiCallBack) {
        EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
        Call<List<UserListFriends>> call = git.getFriendRequest("Bearer " + tokenManager.getAccessToken());
        call.enqueue(new Callback<List<UserListFriends>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserListFriends>> call, @NonNull Response<List<UserListFriends>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    apiCallBack.onSuccess(response.body());
                } else {
                    apiCallBack.onSuccess(new ArrayList<UserListFriends>());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserListFriends>> call, @NonNull Throwable t) {
                apiCallBack.onSuccess(new ArrayList<UserListFriends>());
            }
        });
    }

    public void getNotifications(ApiCallBackMulti apiCallBack) {
        EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
        Call<NotificationResponse> call = git.getNotifications("Bearer " + tokenManager.getAccessToken());
        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<NotificationResponse> call, @NonNull Response<NotificationResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    apiCallBack.onSuccess(response.body());
                } else {
                    apiCallBack.onSuccess(new NotificationResponse());
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationResponse> call, @NonNull Throwable t) {
                apiCallBack.onSuccess(new NotificationResponse());
            }
        });
    }

    public void getPagesResponse(Activity activity, ApiCallBackMulti apiCallBack) {
        EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
        Call<PagesResponse> call = git.getPagesResponse("Bearer " + tokenManager.getAccessToken());
        call.enqueue(new Callback<PagesResponse>() {

            @Override
            public void onResponse(@NonNull Call<PagesResponse> call, @NonNull Response<PagesResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    apiCallBack.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PagesResponse> call, @NonNull Throwable t) {
                apiCallBack.onSuccess(null);
            }
        });
    }


    public void createRequest(Activity activity, String userId, ApiCallBackMulti apiCallBack) {

        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);

            Call<BasicResponse> call = git.createRequest("Bearer " + tokenManager.getAccessToken(), userId);

            call.enqueue(new Callback<BasicResponse>() {
                @Override
                public void onResponse(@NonNull Call<BasicResponse> call, @NonNull Response<BasicResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BasicResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void removeRequest(Activity activity, String userId, ApiCallBackMulti apiCallBack) {

        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);

            Call<BasicResponse> call = git.removeRequest("Bearer " + tokenManager.getAccessToken(), userId);

            call.enqueue(new Callback<BasicResponse>() {
                @Override
                public void onResponse(@NonNull Call<BasicResponse> call, @NonNull Response<BasicResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BasicResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void deleteAccountRequest(Activity activity, ApiCallBackMulti apiCallBack,int AccountType,String AccountId ) {

        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);

            Call<DeleteAccountResponse> call = git.deleteAccount("Bearer " + tokenManager.getAccessToken(),AccountType,AccountId);

            call.enqueue(new Callback<DeleteAccountResponse>() {
                @Override
                public void onResponse(@NonNull Call<DeleteAccountResponse> call, @NonNull Response<DeleteAccountResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DeleteAccountResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void AcceptOrRejectRequest(Activity activity, String userId, int statusCode, ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicResponse> call = git.AcceptOrRejectRequest("Bearer " + tokenManager.getAccessToken(), new BasicRequest(userId, statusCode));
            call.enqueue(new Callback<BasicResponse>() {
                @Override
                public void onResponse(@NonNull Call<BasicResponse> call, @NonNull Response<BasicResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BasicResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }


    public void setProfileType(Activity activity, String otp, CustomLoader loader, ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicResponse> call = git.setProfileType("Bearer " + tokenManager.getAccessToken(), new BasicRequest(otp));
            call.enqueue(new Callback<BasicResponse>() {
                @Override
                public void onResponse(@NonNull Call<BasicResponse> call, @NonNull Response<BasicResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getStatusCode() == 1) {
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }
                        apiCallBack.onSuccess(response.body());
                    } else {
                        if (response.body() != null && response.body().getStatusCode() == -1) {
                            if (loader != null) {
                                if (loader.isShowing()) {
                                    loader.dismiss();
                                }
                            }
                            apiCallBack.onError(response.body().getResponseText());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BasicResponse> call, @NonNull Throwable t) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (loader != null) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }
            }
            apiCallBack.onError(e.getMessage());
        }
    }

    public void getPageCategories(Activity activity, ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<List<CategoryResponse>> call = git.getPageCategories("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<List<CategoryResponse>>() {
                @Override
                public void onResponse(@NonNull Call<List<CategoryResponse>> call, @NonNull Response<List<CategoryResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<CategoryResponse>> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void getFriendList(ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<FriendListResponse> call = git.getFriendList("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<FriendListResponse>() {
                @Override
                public void onResponse(@NonNull Call<FriendListResponse> call, @NonNull Response<FriendListResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<FriendListResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void getFriendSuggestionList(ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<FriendSuggestionResponse> call = git.getFriendSuggestionList("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<FriendSuggestionResponse>() {
                @Override
                public void onResponse(@NonNull Call<FriendSuggestionResponse> call, @NonNull Response<FriendSuggestionResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<FriendSuggestionResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void getSentRequest(ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<SentRequestResponse> call = git.getFriendRequests("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<SentRequestResponse>() {
                @Override
                public void onResponse(@NonNull Call<SentRequestResponse> call, @NonNull Response<SentRequestResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SentRequestResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void checkEligibilityForProfessional(Activity activity, ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<EligibilityModel> call = git.checkEligibilityForProfessional("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<EligibilityModel>() {
                @Override
                public void onResponse(@NonNull Call<EligibilityModel> call, @NonNull Response<EligibilityModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<EligibilityModel> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void enableProfessionalDashBoard(Activity activity, ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<EnableDashboardResponse> call = git.enableProfessionalDashBoard("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<EnableDashboardResponse>() {
                @Override
                public void onResponse(@NonNull Call<EnableDashboardResponse> call, @NonNull Response<EnableDashboardResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<EnableDashboardResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void getContentToBoost(String pageId, Activity activity, ApiCallBackMulti apiCallBack,int DateRange,int ContentType) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<PostsResponse> call = git.getContentToBoost(pageId,DateRange,ContentType,"Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<PostsResponse>() {
                @Override
                public void onResponse(@NonNull Call<PostsResponse> call, @NonNull Response<PostsResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PostsResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }
    public void getMarkNotificationRead(int notificationId, ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<ReadNotificationResponse> call = git.getMarkNotificationRead(notificationId,"Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<ReadNotificationResponse>() {
                @Override
                public void onResponse(@NonNull Call<ReadNotificationResponse> call, @NonNull Response<ReadNotificationResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ReadNotificationResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void getContentDetailsToBoost(String PostId, ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<GetContentDetailsToBoostResponse> call = git.getContentDetailsToBoost(PostId,"Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<GetContentDetailsToBoostResponse>() {
                @Override
                public void onResponse(@NonNull Call<GetContentDetailsToBoostResponse> call, @NonNull Response<GetContentDetailsToBoostResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GetContentDetailsToBoostResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void getEstimateBoostReach(double budget,int days,int audienceId, ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<EstimateResponse> call = git.getEstimateBoostReach(budget,days,audienceId,"Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<EstimateResponse>() {
                @Override
                public void onResponse(@NonNull Call<EstimateResponse> call, @NonNull Response<EstimateResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<EstimateResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void initiateBoostPost(InitiateBoostRequest request, ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BoostResponse> call = git.initiateBoostPost("Bearer " + tokenManager.getAccessToken(),request);
            call.enqueue(new Callback<BoostResponse>() {
                @Override
                public void onResponse(@NonNull Call<BoostResponse> call, @NonNull Response<BoostResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BoostResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void updateBoostStatus(int boostId,int status, ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BoostedPostStatusChangeResponse> call = git.updateBoostStatus("Bearer " + tokenManager.getAccessToken(),boostId,status);
            call.enqueue(new Callback<BoostedPostStatusChangeResponse>() {
                @Override
                public void onResponse(@NonNull Call<BoostedPostStatusChangeResponse> call, @NonNull Response<BoostedPostStatusChangeResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BoostedPostStatusChangeResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void createUpdateGroup(String GroupId,String Title,String Description, boolean IsPrivate, Boolean IsVisible,ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<CreateGroupResponse> call = git.createUpdateGroup("Bearer " + tokenManager.getAccessToken(),GroupId,Title,Description,IsPrivate,IsVisible);
            call.enqueue(new Callback<CreateGroupResponse>() {
                @Override
                public void onResponse(@NonNull Call<CreateGroupResponse> call, @NonNull Response<CreateGroupResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CreateGroupResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void updateGroupProfilePicture(String groupId, boolean isCoverPicture, MultipartBody.Part model,ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<UploadGroupCoverResponse> call = git.updateGroupProfilePicture("Bearer " + tokenManager.getAccessToken(),groupId,isCoverPicture,model);
            call.enqueue(new Callback<UploadGroupCoverResponse>() {
                @Override
                public void onResponse(@NonNull Call<UploadGroupCoverResponse> call, @NonNull Response<UploadGroupCoverResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UploadGroupCoverResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }
    public void getUsersList(ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<GetUserListResponse> call = git.getUsersList("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<GetUserListResponse>() {
                @Override
                public void onResponse(@NonNull Call<GetUserListResponse> call, @NonNull Response<GetUserListResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GetUserListResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }
    public void getGroupById(String groupId,ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<GroupDetailsResponse> call = git.getGroupById("Bearer " + tokenManager.getAccessToken(),groupId);
            call.enqueue(new Callback<GroupDetailsResponse>() {
                @Override
                public void onResponse(@NonNull Call<GroupDetailsResponse> call, @NonNull Response<GroupDetailsResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GroupDetailsResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void getGroupsListing(boolean OnlyMyGroups,Boolean OrderByName, Boolean OrderByJoinDate,ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<GroupListResponse> call = git.getGroupsListing("Bearer " + tokenManager.getAccessToken(),OnlyMyGroups,OrderByName,OrderByJoinDate);
            call.enqueue(new Callback<GroupListResponse>() {
                @Override
                public void onResponse(@NonNull Call<GroupListResponse> call, @NonNull Response<GroupListResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GroupListResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void getGroupsMembers(String groupId,ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<GroupMembersResponse> call = git.getGroupsMembers("Bearer " + tokenManager.getAccessToken(),groupId);
            call.enqueue(new Callback<GroupMembersResponse>() {
                @Override
                public void onResponse(@NonNull Call<GroupMembersResponse> call, @NonNull Response<GroupMembersResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GroupMembersResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void getPostStats(String postId,int dateRange,ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<InsightsStatsResponse> call = git.getPostStats("Bearer " + tokenManager.getAccessToken(),postId,dateRange);
            call.enqueue(new Callback<InsightsStatsResponse>() {
                @Override
                public void onResponse(@NonNull Call<InsightsStatsResponse> call, @NonNull Response<InsightsStatsResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<InsightsStatsResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void getBoostBillingInfo(String postId,ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BoostBillingResponse> call = git.getBoostBillingInfo("Bearer " + tokenManager.getAccessToken(),postId);
            call.enqueue(new Callback<BoostBillingResponse>() {
                @Override
                public void onResponse(@NonNull Call<BoostBillingResponse> call, @NonNull Response<BoostBillingResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BoostBillingResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void getMyComplaint(ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<ComplaintResponse> call = git.getMyComplaint("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<ComplaintResponse>() {
                @Override
                public void onResponse(@NonNull Call<ComplaintResponse> call, @NonNull Response<ComplaintResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ComplaintResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }
    public void getComplaintCategory(ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<SupportCategoryResponse> call = git.getComplaintCategory("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<SupportCategoryResponse>() {
                @Override
                public void onResponse(@NonNull Call<SupportCategoryResponse> call, @NonNull Response<SupportCategoryResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SupportCategoryResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void getDownloadBillingPdf(int boostId,ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<ResponseBody> call = git.getDownloadBillingPdf("Bearer " + tokenManager.getAccessToken(),boostId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void updateGroupMembers(UpdateGroupMemberRequest updateGroupMemberRequest , ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<GroupMembersUpdateResponse> call = git.updateGroupMembers("Bearer " + tokenManager.getAccessToken(),updateGroupMemberRequest);
            call.enqueue(new Callback<GroupMembersUpdateResponse>() {
                @Override
                public void onResponse(@NonNull Call<GroupMembersUpdateResponse> call, @NonNull Response<GroupMembersUpdateResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GroupMembersUpdateResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }
    public void submitComplaint(ComplaintRequest complaintRequest , ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<ComplaintSubmitResponse> call = git.submitComplaint("Bearer " + tokenManager.getAccessToken(),complaintRequest);
            call.enqueue(new Callback<ComplaintSubmitResponse>() {
                @Override
                public void onResponse(@NonNull Call<ComplaintSubmitResponse> call, @NonNull Response<ComplaintSubmitResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ComplaintSubmitResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void insertLinkClick(String PostId,int ClickType, ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<LinkClickResponse> call = git.insertLinkClick("Bearer " + tokenManager.getAccessToken(),PostId.toString(),ClickType);
            call.enqueue(new Callback<LinkClickResponse>() {
                @Override
                public void onResponse(@NonNull Call<LinkClickResponse> call, @NonNull Response<LinkClickResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LinkClickResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
        }
    }

    public void addPeopleInGroup(AddFriendsRequest request,ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<AddPeopleResponse> call = git.addMultipleFriendsToGroup("Bearer " + tokenManager.getAccessToken(),request);
            call.enqueue(new Callback<AddPeopleResponse>() {
                @Override
                public void onResponse(@NonNull Call<AddPeopleResponse> call, @NonNull Response<AddPeopleResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        apiCallBack.onSuccess(response.body());
                    } else {
                        apiCallBack.onError("Server returned error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AddPeopleResponse> call, @NonNull Throwable t) {
                    apiCallBack.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            apiCallBack.onError(e.getMessage());
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

    @SuppressLint("SetTextI18n")
    public void openAcceptRequestBottomSheetDialog(Activity context, String userId, String name, ApiCallBackMulti apiCallBack, int type) {

        if (AcceptRequestDialog != null && AcceptRequestDialog.isShowing())
            return;

        AcceptRequestDialog = new BottomSheetDialog(context, R.style.DialogStyle);
        Objects.requireNonNull(AcceptRequestDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inflater.inflate(R.layout.dialog_follow_unfollow, null);

        TextView confirmMessage = sheetView.findViewById(R.id.confirmMessage);
        MaterialButton unfollowBtn = sheetView.findViewById(R.id.unfollowBtn);
        MaterialButton cancelBtn = sheetView.findViewById(R.id.cancelBtn);
        if (type == 1) {
            confirmMessage.setText("Are you sure want to unfollow this user?");
            unfollowBtn.setText("Unfollow");
            cancelBtn.setText("Cancel");
        } else if (type == 3) {
            confirmMessage.setText(name + "sent you a friend request");
            unfollowBtn.setText("Confirm");
            cancelBtn.setText("Delete");
        } else if (type == 4) {
            confirmMessage.setVisibility(GONE);
            unfollowBtn.setText("Cancel Request");
            cancelBtn.setText("Edit friend list");
        } else {
            confirmMessage.setText("Are you sure want to cancel this friend request?");
            unfollowBtn.setText("Cancel friend request");
            cancelBtn.setText("Cancel");
        }

        unfollowBtn.setOnClickListener(v -> {
            if (type == 1) {
                doFollow(context, userId, apiCallBack);
            } else if (type == 3) {
                AcceptOrRejectRequest(context, userId, 2, apiCallBack);
            } else {
                removeRequest(context, userId, apiCallBack);
            }
            AcceptRequestDialog.dismiss();
        });

        cancelBtn.setOnClickListener(v -> {
            if (type == 3) {
                AcceptOrRejectRequest(context, userId, 3, apiCallBack);
            }
            if (AcceptRequestDialog != null && AcceptRequestDialog.isShowing()) {
                AcceptRequestDialog.dismiss();
            }
        });


        AcceptRequestDialog.setCancelable(true);
        AcceptRequestDialog.setContentView(sheetView);

        BottomSheetBehavior.from(
                AcceptRequestDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        ).setState(BottomSheetBehavior.STATE_EXPANDED);

        AcceptRequestDialog.show();
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

                    }
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

    public void addInsight(Activity context, String userId,String postId,int accountType,int insightTypeID, ApiCallBackMulti apiCallBack) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<InsightResponse> call = git.addInsight("Bearer " + tokenManager.getAccessToken(), userId,postId,accountType,insightTypeID);
            call.enqueue(new Callback<InsightResponse>() {
                @Override
                public void onResponse(@NonNull Call<InsightResponse> call, @NonNull Response<InsightResponse> response) {
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

                    }
                }

                @Override
                public void onFailure(@NonNull Call<InsightResponse> call, @NonNull Throwable t) {
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

    public void selectDateRangeBottomSheet(Activity context, AppCompatTextView tvDropdownText, OnDateRangeSelected callback,boolean showLifetime) {

        if (bottomDateDialogDateRange != null && bottomDateDialogDateRange.isShowing())
            return;

        bottomDateDialogDateRange = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_date_range, null);
        if (showLifetime) {
            sheetView.findViewById(R.id.option_last_lifetime).setVisibility(View.VISIBLE);
        }

        RadioButton rToday = sheetView.findViewById(R.id.rbToday);
        RadioButton r7 = sheetView.findViewById(R.id.rbLast7);
        RadioButton r14 = sheetView.findViewById(R.id.rbLast14);
        RadioButton r28 = sheetView.findViewById(R.id.rbLast28);
        RadioButton r90 = sheetView.findViewById(R.id.rbLast90);
        RadioButton rbLastLifetime = sheetView.findViewById(R.id.rbLastLifetime);
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
            case 100:
                rbLastLifetime.setChecked(true);
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
            rbLastLifetime.setChecked(id == R.id.rbLastLifetime);

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
            }else if (id == R.id.rbLastLifetime) {
                idSelected = 100;
                selectedDateRange = 100;
                tvDropdownText.setText("Lifetime");
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
        sheetView.findViewById(R.id.rbLastLifetime).setOnClickListener(listener);
        bottomDateDialogDateRange.show();
    }
    public void selectDateRangeBottomSheetNew(Activity context, AppCompatTextView tvDropdownText, OnDateRangeSelected callback,boolean showLifetime) {

        if (bottomDateDialogDateRange != null && bottomDateDialogDateRange.isShowing())
            return;

        bottomDateDialogDateRange = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_date_range, null);
        if (showLifetime) {
            sheetView.findViewById(R.id.option_last_lifetime).setVisibility(View.VISIBLE);
        }

        RadioButton rToday = sheetView.findViewById(R.id.rbToday);
        RadioButton r7 = sheetView.findViewById(R.id.rbLast7);
        RadioButton r14 = sheetView.findViewById(R.id.rbLast14);
        RadioButton r28 = sheetView.findViewById(R.id.rbLast28);
        RadioButton r90 = sheetView.findViewById(R.id.rbLast90);
        RadioButton rbLastLifetime = sheetView.findViewById(R.id.rbLastLifetime);
        switch (selectedDateRangeNew) {
            case 1:
                rToday.setChecked(true);
                break;
            case 2:
                r7.setChecked(true);
                break;
            case 3:
                r14.setChecked(true);
                break;
            case 4:
                r28.setChecked(true);
                break;
            case 5:
                r90.setChecked(true);
                break;
            case 0:
                rbLastLifetime.setChecked(true);
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
            rbLastLifetime.setChecked(id == R.id.rbLastLifetime);

            int idSelected = 0;
            if (id == R.id.option_today) {
                idSelected = 1;
                selectedDateRangeNew = 1;
                tvDropdownText.setText("Today");
            } else if (id == R.id.rbLast7) {
                idSelected = 2;
                selectedDateRangeNew = 2;
                tvDropdownText.setText("Last 7 days");
            } else if (id == R.id.rbLast14) {
                idSelected = 3;
                selectedDateRangeNew = 3;
                tvDropdownText.setText("Last 14 days");
            } else if (id == R.id.rbLast28) {
                idSelected = 4;
                selectedDateRangeNew = 4;
                tvDropdownText.setText("Last 28 days");
            } else if (id == R.id.rbLast90) {
                idSelected = 5;
                selectedDateRangeNew = 5;
                tvDropdownText.setText("Last 90 days");
            }else if (id == R.id.rbLastLifetime) {
                idSelected = 0;
                selectedDateRangeNew = 0;
                tvDropdownText.setText("Lifetime");
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
        sheetView.findViewById(R.id.rbLastLifetime).setOnClickListener(listener);
        bottomDateDialogDateRange.show();
    }

    public void selectMetricBottomSheet(Activity context, AppCompatTextView tvDropdownText, OnDateRangeSelected callback) {

        if (bottomDateDialogDateRange != null && bottomDateDialogDateRange.isShowing())
            return;

        bottomDateDialogDateRange = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_metric, null);

        RadioButton rToday = sheetView.findViewById(R.id.rbToday);
        RadioButton r7 = sheetView.findViewById(R.id.rbLast7);
        RadioButton r14 = sheetView.findViewById(R.id.rbLast14);
        RadioButton r28 = sheetView.findViewById(R.id.rbLast28);
        switch (selectedMetricType) {
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
        }

        bottomDateDialogDateRange.setContentView(sheetView);

        @SuppressLint("SetTextI18n") View.OnClickListener listener = v -> {
            int id = v.getId();

            rToday.setChecked(id == R.id.option_today);
            r7.setChecked(id == R.id.rbLast7);
            r14.setChecked(id == R.id.rbLast14);
            r28.setChecked(id == R.id.rbLast28);

            int idSelected = 28;
            if (id == R.id.option_today) {
                idSelected = 1;
                selectedMetricType = 1;
                tvDropdownText.setText("Views");
            } else if (id == R.id.rbLast7) {
                idSelected = 7;
                selectedMetricType = 7;
                tvDropdownText.setText("Viewers");
            } else if (id == R.id.rbLast14) {
                idSelected = 14;
                selectedMetricType = 14;
                tvDropdownText.setText("Engagement");
            } else if (id == R.id.rbLast28) {
                idSelected = 28;
                selectedMetricType = 28;
                tvDropdownText.setText("Date");
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
        bottomDateDialogDateRange.show();
    }
    public void selectPostTypeBottomSheet(Activity context, AppCompatTextView tvDropdownText, OnDateRangeSelected callback) {

        if (bottomDateDialogDateRange != null && bottomDateDialogDateRange.isShowing())
            return;

        bottomDateDialogDateRange = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_post, null);

        RadioButton rToday = sheetView.findViewById(R.id.rbToday);
        RadioButton r7 = sheetView.findViewById(R.id.rbLast7);
        RadioButton r14 = sheetView.findViewById(R.id.rbLast14);
        RadioButton r28 = sheetView.findViewById(R.id.rbLast28);
        RadioButton r90 = sheetView.findViewById(R.id.rbLast90);
        RadioButton rbLastLifetime = sheetView.findViewById(R.id.rbLastLifetime);
        RadioButton rbLastText = sheetView.findViewById(R.id.rbLastText);
        RadioButton rbLastStory = sheetView.findViewById(R.id.rbLastStory);
        RadioButton rbLastAbTest = sheetView.findViewById(R.id.rbLastAbTest);
        switch (selectedPostType) {
            case 0:
                rToday.setChecked(true);
                break;
            case 1:
                r7.setChecked(true);
                break;
            case 2:
                r14.setChecked(true);
                break;
            case 3:
                r28.setChecked(true);
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
            rbLastLifetime.setChecked(id == R.id.rbLastLifetime);
            rbLastText.setChecked(id == R.id.rbLastText);
            rbLastStory.setChecked(id == R.id.rbLastStory);
            rbLastAbTest.setChecked(id == R.id.rbLastAbTest);

            int idSelected = 0;
            if (id == R.id.option_today) {
                idSelected = 0;
                selectedPostType = 0;
                tvDropdownText.setText("All posts");
            } else if (id == R.id.rbLast7) {
                idSelected = 1;
                selectedPostType = 1;
                tvDropdownText.setText("Text");
            } else if (id == R.id.rbLast14) {
                idSelected = 2;
                selectedPostType = 2;
                tvDropdownText.setText("Video");
            } else if (id == R.id.rbLast28) {
                idSelected = 3;
                selectedPostType = 3;
                tvDropdownText.setText("Images");
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
        sheetView.findViewById(R.id.rbLastLifetime).setOnClickListener(listener);
        sheetView.findViewById(R.id.rbLastText).setOnClickListener(listener);
        sheetView.findViewById(R.id.rbLastStory).setOnClickListener(listener);
        sheetView.findViewById(R.id.rbLastAbTest).setOnClickListener(listener);
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
        AppCompatTextView description = sheetView.findViewById(R.id.description1);

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
            searchEt.setVisibility(GONE);
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
            progress.setVisibility(GONE);
            DialogReportBottomSheetAdapter adapter = new DialogReportBottomSheetAdapter(reportReasonResultList, id -> {
                submitBtn.setTag(id);
            });
            recyclerView.setAdapter(adapter);
        } else {
            getReportReason(context, progress, object -> {
                recyclerView.setVisibility(View.VISIBLE);
                submitBtn.setVisibility(View.VISIBLE);
                progress.setVisibility(GONE);

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
