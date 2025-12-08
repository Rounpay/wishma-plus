package com.infotech.wishmaplus.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.infotech.wishmaplus.Adapter.Interfaces.CountChangeCallBack;
import com.infotech.wishmaplus.Api.Object.ContentResult;
import com.infotech.wishmaplus.Api.Response.BasicObjectResponse;
import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.Fragments.ShareDialogFragment;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.ApplicationConstant;
import com.infotech.wishmaplus.Utils.CommentDialog;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareRedirectActivity extends AppCompatActivity {

    private RequestOptions requestOptionsUserImage;
    TextView textView;
    MaterialButton likeBtn, commentBtn, whatsAppBtn, shareBtn;
    TextView nameTv, timeTv, like_count, comment_count, share_count;

    ImageView profile, moreBTn;
    ImageButton back_button;
    NestedScrollView scrollView;
    ProgressBar progress;
    TextView user_title;
    View line1;
    private PreferencesManager tokenManager;


    private boolean isDeleteFileAllow = false;
    private CommentDialog commentDialog;
    private String userId;
    private int position = -1;
    public static CountChangeCallBack callBack;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_share_redirect);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainS), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Uri data = getIntent().getData();
        if (data != null && ApplicationConstant.INSTANCE.Domain.equals(data.getHost()) && data.toString().contains("/post/")) {
            postId = data.getLastPathSegment();
          /*  Log.d("sdkjlsdjfhskdfkhsj",postId);*/
        }else {
            finishAffinity();
            startActivity(new Intent(this,WelcomeActivity.class));
        }
        scrollView = findViewById(R.id.scrollView);
        progress = findViewById(R.id.progress);
        user_title = findViewById(R.id.user_title);
        back_button = findViewById(R.id.back_button);
        line1 = findViewById(R.id.line1);
        moreBTn = findViewById(R.id.moreBTn);
        nameTv = findViewById(R.id.nameTv);
        timeTv = findViewById(R.id.timeTv);
        profile = findViewById(R.id.profile);
        textView = findViewById(R.id.textView);
        like_count = findViewById(R.id.like_count);
        comment_count = findViewById(R.id.comment_count);
        share_count = findViewById(R.id.share_count);
        likeBtn = findViewById(R.id.likeBtn);
        commentBtn = findViewById(R.id.commentBtn);
        whatsAppBtn = findViewById(R.id.whatsAppBtn);
        shareBtn = findViewById(R.id.shareBtn);
        requestOptionsUserImage= UtilMethods.INSTANCE.getRequestOption_With_UserIcon();
        tokenManager = new PreferencesManager(this,1);
        if (!tokenManager.getString(tokenManager.LoginPref).isEmpty()) {
            init();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finishAffinity();
        }


    }

    void init() {
        userId = tokenManager.getUserId();
        commentDialog = new CommentDialog(this, tokenManager);

        back_button.setOnClickListener(view -> {
            finishAffinity();
        });
        getPostData();
    }


    private void getPostData() {
        try {

            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicObjectResponse<ContentResult>> call = git.getPost("Bearer " + tokenManager.getAccessToken(),postId);
            call.enqueue(new Callback<BasicObjectResponse<ContentResult>>() {
                @Override
                public void onResponse(@NonNull Call<BasicObjectResponse<ContentResult>> call, @NonNull Response<BasicObjectResponse<ContentResult>> response) {

                    try {
                        BasicObjectResponse<ContentResult> packageResponse = response.body();
                        if (packageResponse != null) {
                            if (packageResponse.getStatusCode() == 1) {
                                if (packageResponse.getResult() != null) {
                                    ContentResult result =packageResponse.getResult();
                                    if(result.getContentTypeId()==UtilMethods.INSTANCE.VIDEO_TYPE){
                                        finishAffinity();
                                        startActivity(new Intent(ShareRedirectActivity.this, VideoViewActivity.class)
                                                .putExtra("Position", position)
                                                .putExtra("VideoData", result));
                                    }else if(result.getContentTypeId()==UtilMethods.INSTANCE.IMAGE_TYPE){
                                        finishAffinity();
                                        startActivity(new Intent(ShareRedirectActivity.this, ImageZoomViewActivity.class)
                                                .putExtra("Position", position)
                                                .putExtra("ImageData", result));
                                    }else {
                                        setTextTypeData(result);
                                    }
                                }else {
                                    finish();
                                    Toast.makeText(ShareRedirectActivity.this, "Post is not available", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                UtilMethods.INSTANCE.Error(ShareRedirectActivity.this, packageResponse.getResponseText());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilMethods.INSTANCE.Error(ShareRedirectActivity.this, e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BasicObjectResponse<ContentResult>> call, @NonNull Throwable t) {
                    try {

                        UtilMethods.INSTANCE.apiFailureError(ShareRedirectActivity.this, t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(ShareRedirectActivity.this, ise.getMessage());

                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            UtilMethods.INSTANCE.Error(ShareRedirectActivity.this, e.getMessage());

        }
    }

    void setTextTypeData(ContentResult content) {
        scrollView.setVisibility(View.VISIBLE);
        user_title.setVisibility(View.VISIBLE);
        back_button.setVisibility(View.VISIBLE);
        line1.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);

        nameTv.setText(content.getFisrtName() + " " + content.getLastName());
        timeTv.setText(UtilMethods.INSTANCE.covertTimeToText(content.getEntryAt()));


        Glide.with(this)
                .load(content.getProfilePictureUrl())
                .apply(requestOptionsUserImage)
                .into(profile);
        textView.setText((content.getPostContent() + "").trim());
        if (content.getTotalLikes() > 0) {
            like_count.setVisibility(View.VISIBLE);
            like_count.setText(content.getTotalLikes() + "");
        } else {
            like_count.setVisibility(View.GONE);
        }
        if (content.getTotalComments() > 0) {
            comment_count.setVisibility(View.VISIBLE);
            comment_count.setText(content.getTotalComments() + " Comments");
        } else {
            comment_count.setVisibility(View.GONE);
        }
        if (content.getTotalShares() > 0) {
            share_count.setVisibility(View.VISIBLE);
            share_count.setText(content.getTotalShares() + " Share");
        } else {
            share_count.setVisibility(View.GONE);
        }

        commentBtn.setOnClickListener(v -> {
            if (commentDialog == null && tokenManager != null) {
                commentDialog = new CommentDialog(this, tokenManager);
            }
            commentDialog.showCommentsDialog(content.getPostId(), size -> {
                if (size > 0) {
                    comment_count.setText(size + " Comments");
                    comment_count.setVisibility(View.VISIBLE);
                } else {
                    comment_count.setVisibility(View.GONE);
                }
                if (callBack != null) {
                    callBack.onChangeCallBack(null, null, size, -1, -1, position, -1);
                }
            }/*position, comment_count*/);
        });
        comment_count.setOnClickListener(v -> {
            if (commentDialog == null && tokenManager != null) {
                commentDialog = new CommentDialog(this, tokenManager);
            }
            commentDialog.showCommentsDialog(content.getPostId(), size -> {
                if (size > 0) {
                    comment_count.setText(size + " Comments");
                    comment_count.setVisibility(View.VISIBLE);
                } else {
                    comment_count.setVisibility(View.GONE);
                }
                if (callBack != null) {
                    callBack.onChangeCallBack(null, null, size, -1, -1, position, -1);
                }

            }/*position, comment_count*/);
        });


        if (content.isLiked()) {
            likeBtn.setIconTint(ContextCompat.getColorStateList(this, R.color.colorFwd));
            likeBtn.setTextColor(ContextCompat.getColor(this, R.color.colorFwd));
        } else {
            likeBtn.setIconTint(ContextCompat.getColorStateList(this, R.color.grey_5));
            likeBtn.setTextColor(ContextCompat.getColor(this, R.color.grey_5));
        }


        likeBtn.setOnClickListener(v -> {
            UtilMethods.INSTANCE.triggerLikeApi(this, content.getPostId(), "" /*,!content.isLiked(), likeBtn, like_count, position*/, new UtilMethods.ApiCallBackMulti() {
                @Override
                public void onSuccess(Object object) {
                    boolean isLiked = (boolean) object;
                    content.setLiked(isLiked);


                    int newLikesCount = isLiked ? content.getTotalLikes() + 1 : content.getTotalLikes() - 1;
                    content.setTotalLikes(newLikesCount);

                    if(callBack!=null){
                        callBack.onChangeCallBack(null,isLiked,-1,newLikesCount,-1,position,-1);
                    }

                    if (newLikesCount > 0) {
                        like_count.setVisibility(View.VISIBLE);
                        like_count.setText(newLikesCount + "");
                    } else {
                        like_count.setVisibility(View.GONE);
                    }
                    if (isLiked) {
                        likeBtn.setIconTint(ContextCompat.getColorStateList(ShareRedirectActivity.this, R.color.colorFwd));
                        likeBtn.setTextColor(ContextCompat.getColor(ShareRedirectActivity.this, R.color.colorFwd));
                    } else {
                        likeBtn.setIconTint(ContextCompat.getColorStateList(ShareRedirectActivity.this, R.color.grey_1));
                        likeBtn.setTextColor(ContextCompat.getColor(ShareRedirectActivity.this, R.color.grey_1));
                    }
                }

                @Override
                public void onError(String msg) {

                }
            });
            UserDetailResponse userDetailResponse = UtilMethods.INSTANCE.getUserDetailResponse(tokenManager);
            int accountType = userDetailResponse.isSelfProfile()?1:2;//Objects.equals(tokenManager.getString("ACTIVE_PAGE_ID"), userDetailResponse.getUserId()) ?2:1;
//                    InsightTypeID
//                    Impressions = 1,
//                    Viewed = 2,
//                    Clicked = 3
            UtilMethods.INSTANCE.addInsight(this, userDetailResponse.getUserId(),content.getPostId(), accountType ,2, new UtilMethods.ApiCallBackMulti() {
                @Override
                public void onSuccess(Object object) {

                }

                @Override
                public void onError(String msg) {

                }
            });
        });

        moreBTn.setOnClickListener(view -> showPopupMenu(view, content, position));
        shareBtn.setOnClickListener(view -> {
            ShareDialogFragment bottomSheetDialogFragment = ShareDialogFragment.newInstance(content, typeId -> {
                if(callBack!=null){
                    finish();
                    callBack.onRefresh(typeId);
                }
            });
            bottomSheetDialogFragment.show(getSupportFragmentManager(), "ShareBottomSheetDialog");


        });


        whatsAppBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://api.whatsapp.com/send?text=" + ApplicationConstant.INSTANCE.postUrl + content.getPostId()));
            startActivity(intent);
        });
    }

    private void showPopupMenu(View view, ContentResult content, int position) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.dialog_post_popup, null);

        // Initialize the PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView, (int) getResources().getDimension(com.intuit.sdp.R.dimen._160sdp), ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Set up views in popup layout
        TextView edit = popupView.findViewById(R.id.edit);
        TextView delete = popupView.findViewById(R.id.delete);
        TextView report = popupView.findViewById(R.id.report);
        TextView copyLink = popupView.findViewById(R.id.copyLink);
        View editLine = popupView.findViewById(R.id.editLine);
        View deleteLine = popupView.findViewById(R.id.deleteLine);
        View copyLinkLine = popupView.findViewById(R.id.copyLinkLine);

        if (content.getUserId().equalsIgnoreCase(userId) || content.getParsedSharedData()!=null && content.getParsedSharedData().getUserId().equalsIgnoreCase(userId)) {
            report.setVisibility(View.GONE);
            copyLinkLine.setVisibility(View.GONE);
        } else {
            edit.setVisibility(View.GONE);
            editLine.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            deleteLine.setVisibility(View.GONE);
        }
        edit.setOnClickListener(v -> {
            popupWindow.dismiss();
            if(callBack!=null){
                finish();
                callBack.onChangeCallBack(content.getPostId(),null,-1,-1,-1,position,-1);
            }else {
                Intent intent = new Intent(ShareRedirectActivity.this, PostActivity.class);
                intent.putExtra("userData", content.getUserDetail());
                intent.putExtra("postId", content.getPostId());
                intent.putExtra("postType", 1);
                postActivityResultLauncher.launch(intent);
            }
        });
        delete.setOnClickListener(v -> {
            popupWindow.dismiss();
            showDeleteConfirmationDialog(content, position);
        });
        report.setOnClickListener(v -> {
            popupWindow.dismiss();
            UtilMethods.INSTANCE.openReportBottomSheetDialog(ShareRedirectActivity.this,content.getPostId());
        });
        copyLink.setOnClickListener(v -> {
            popupWindow.dismiss();
            Utility.INSTANCE.setClipboard(this,ApplicationConstant.INSTANCE.postUrl+content.getPostId(),"Copy Link");
        });
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAsDropDown(view,  0, 0,Gravity.BOTTOM);

    }

    ActivityResultLauncher<Intent> postActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    getPostData();


                }
            });

    private void showDeleteConfirmationDialog(ContentResult content, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Content")
                .setMessage("Are you sure you want to delete this content?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteContentFromServer(content.getPostId(), position);  // Call API to delete content
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteContentFromServer(String postId, int position) {
        EndPointInterface apiService = ApiClient.getClient().create(EndPointInterface.class);
        Call<BasicResponse> call = apiService.deleteComment("Bearer " + tokenManager.getAccessToken(), postId);

        call.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(@NonNull Call<BasicResponse> call, @NonNull Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    // Content deleted successfully, remove it from the list and notify adapter


                   /* if (playerView != null) {
                        mRecyclerView.deleteVideo(playerView);
                    }
                    if (clickCallBack != null) {
                        clickCallBack.onDelete(position);
                    }*/
                    if(callBack!=null){
                        finish();
                        callBack.onChangeCallBack(null,null,-1,-1,-1,position,position);
                    }else{
                        finish();
                    }

                    Toast.makeText(ShareRedirectActivity.this, "Content deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    UtilMethods.INSTANCE.apiErrorHandle(ShareRedirectActivity.this, response.code(), response.message());
                    //Toast.makeText(ImageZoomViewActivity.this, "Failed to delete content", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BasicResponse> call, @NonNull Throwable t) {
                UtilMethods.INSTANCE.apiFailureError(ShareRedirectActivity.this, t);
                //Toast.makeText(ImageZoomViewActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}