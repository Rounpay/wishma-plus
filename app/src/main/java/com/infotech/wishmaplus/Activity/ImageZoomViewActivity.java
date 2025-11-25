package com.infotech.wishmaplus.Activity;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.infotech.wishmaplus.Adapter.Interfaces.CountChangeCallBack;
import com.infotech.wishmaplus.Api.Object.ContentResult;
import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.Fragments.ShareDialogFragment;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.ApplicationConstant;
import com.infotech.wishmaplus.Utils.CommentDialog;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.TouchImageView;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageZoomViewActivity extends AppCompatActivity {

    public static CountChangeCallBack callBack;
    private ContentResult imageData;
    private TouchImageView imageView;
    private int position;
    private PreferencesManager tokenManager;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_image_zoom);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.zoomAC), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        position = getIntent().getIntExtra("Position", -1);
        imageData = getIntent().getParcelableExtra("ImageData");
        tokenManager = new PreferencesManager(this,1);
        CommentDialog commentDialog = new CommentDialog(this,tokenManager);
        userId = tokenManager.getUserId();
        imageView = findViewById(R.id.imageView);


        Glide.with(this)
                .load(imageData.getPostContent())
                .apply(UtilMethods.INSTANCE.getRequestOption_With_PlaceHolder())
                .into(imageView);


        View bottomBlurView = findViewById(R.id.bottomBlurView);
        View topBlurView = findViewById(R.id.topBlurView);
        imageView.setOnClickListener(view -> {
            if (bottomBlurView.getVisibility() == View.VISIBLE) {
                bottomBlurView.setVisibility(View.GONE);
            } else {
                bottomBlurView.setVisibility(View.VISIBLE);
            }
            if (topBlurView.getVisibility() == View.VISIBLE) {
                topBlurView.setVisibility(View.GONE);
            } else {
                topBlurView.setVisibility(View.VISIBLE);
            }
        });

        ImageView profile = findViewById(R.id.profile);
        TextView nameTv = findViewById(R.id.nameTv);
        TextView timeTv = findViewById(R.id.timeTv);
        TextView postTxt = findViewById(R.id.postTxt);
        TextView like_count = findViewById(R.id.like_count);
        TextView comment_count = findViewById(R.id.comment_count);
        TextView share_count = findViewById(R.id.share_count);
        MaterialButton likeBtn = findViewById(R.id.likeBtn);
        Glide.with(this)
                .load(imageData.getProfilePictureUrl())
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .apply(UtilMethods.INSTANCE.getRequestOption_With_UserIcon())
                .into(profile);
        nameTv.setText(imageData.getFisrtName() + " " + imageData.getLastName());
        timeTv.setText(UtilMethods.INSTANCE.covertTimeToText(imageData.getEntryAt()));
        if (imageData.getCaption() != null && !imageData.getCaption().trim().isEmpty()) {
            postTxt.setText(imageData.getCaption().trim());
            postTxt.setVisibility(View.VISIBLE);
        } else {
            postTxt.setVisibility(View.GONE);
        }

        if (imageData.getTotalLikes() > 0) {
            like_count.setVisibility(View.VISIBLE);
            like_count.setText(imageData.getTotalLikes() + "");
        } else {
            like_count.setVisibility(View.GONE);
        }
        if (imageData.getTotalComments() > 0) {
            comment_count.setVisibility(View.VISIBLE);
            comment_count.setText(imageData.getTotalComments() + " Comments");
        } else {
            comment_count.setVisibility(View.GONE);
        }
        if (imageData.getTotalShares() > 0) {
            share_count.setVisibility(View.VISIBLE);
            share_count.setText(imageData.getTotalShares() + " Share");
        } else {
            share_count.setVisibility(View.GONE);
        }

       findViewById(R.id.commentBtn).setOnClickListener(v -> commentDialog.showCommentsDialog(imageData.getPostId(), size -> {
           if(size>0) {
               comment_count.setText(size + " Comments");
               comment_count.setVisibility(View.VISIBLE);
           }else {
               comment_count.setVisibility(View.GONE);
           }
           if(callBack!=null){
               callBack.onChangeCallBack(null,null,size,-1,-1,position,-1);
           }

       }/*position, comment_count*/));
        comment_count.setOnClickListener(v -> commentDialog.showCommentsDialog(imageData.getPostId(), size -> {
            if(size>0) {
                comment_count.setText(size + " Comments");
                comment_count.setVisibility(View.VISIBLE);
            }else {
                comment_count.setVisibility(View.GONE);
            }
            if(callBack!=null){
                callBack.onChangeCallBack(null,null,size,-1,-1,position,-1);
            }

        }/*position, comment_count*/));
        if (imageData.isLiked()) {
            likeBtn.setIconTint(ContextCompat.getColorStateList(this, R.color.colorFwd));
            likeBtn.setTextColor(ContextCompat.getColor(this, R.color.colorFwd));
        } else {
            likeBtn.setIconTint(ContextCompat.getColorStateList(this, R.color.grey_1));
            likeBtn.setTextColor(ContextCompat.getColor(this, R.color.grey_1));
        }

        likeBtn.setOnClickListener(v -> {
            UtilMethods.INSTANCE. triggerLikeApi(this, imageData.getPostId(),"" /*,!content.isLiked(), likeBtn, like_count, position*/, new UtilMethods.ApiCallBackMulti() {
                @Override
                public void onSuccess(Object object) {
                    boolean isLiked = (boolean) object;
                //    updateLikeState(/*content,*/ !content.isLiked(), position, likeBtn, like_count);
                    imageData.setLiked(isLiked);



                    int newLikesCount = isLiked ? imageData.getTotalLikes() + 1 : imageData.getTotalLikes() - 1;
                     imageData.setTotalLikes(newLikesCount);


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
                        likeBtn.setIconTint(ContextCompat.getColorStateList(ImageZoomViewActivity.this, R.color.colorFwd));
                        likeBtn.setTextColor(ContextCompat.getColor(ImageZoomViewActivity.this, R.color.colorFwd));
                    } else {
                        likeBtn.setIconTint(ContextCompat.getColorStateList(ImageZoomViewActivity.this, R.color.grey_1));
                        likeBtn.setTextColor(ContextCompat.getColor(ImageZoomViewActivity.this, R.color.grey_1));
                    }
                }

                @Override
                public void onError(String msg) {

                }
            });
        });

        findViewById(R.id.moreBTn).setOnClickListener(view -> showPopupMenu(view, imageData, position));
        findViewById(R.id.shareBtn).setOnClickListener(view -> {
            ShareDialogFragment bottomSheetDialogFragment = ShareDialogFragment.newInstance(imageData, typeId -> {

                if(callBack!=null){
                    finish();
                    callBack.onRefresh(typeId);
                }
            });
                bottomSheetDialogFragment.show(getSupportFragmentManager(), "ShareBottomSheetDialog");



        });



        findViewById(R.id.back_button).setOnClickListener(view -> {
            finish();
        });
        findViewById(R.id.whatsAppBtn).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://api.whatsapp.com/send?text="+ ApplicationConstant.INSTANCE.postUrl+imageData.getPostId()));
            startActivity(intent);
        });
        /*if(callBack!=null){
            callBack.onChangeCallBack(true,1,1,1,position);
        }*/

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

        if (content.getUserId().equalsIgnoreCase(userId)|| content.getParsedSharedData()!=null && content.getParsedSharedData().getUserId().equalsIgnoreCase(userId)) {
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
            }
        });
        delete.setOnClickListener(v -> {
            popupWindow.dismiss();
            showDeleteConfirmationDialog(content, position);
        });
        report.setOnClickListener(v -> {
            popupWindow.dismiss();
            UtilMethods.INSTANCE.openReportBottomSheetDialog(ImageZoomViewActivity.this,content.getPostId());
        });
        copyLink.setOnClickListener(v -> {
            popupWindow.dismiss();
            Utility.INSTANCE.setClipboard(this,ApplicationConstant.INSTANCE.postUrl+content.getPostId(),"Copy Link");
        });
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAsDropDown(view,  0, 0,Gravity.BOTTOM);




    }
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
                    }
                    Toast.makeText(ImageZoomViewActivity.this, "Content deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    UtilMethods.INSTANCE.apiErrorHandle(ImageZoomViewActivity.this,response.code(),response.message());
                    //Toast.makeText(ImageZoomViewActivity.this, "Failed to delete content", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BasicResponse> call, @NonNull Throwable t) {
                UtilMethods.INSTANCE.apiFailureError(ImageZoomViewActivity.this,t);
                //Toast.makeText(ImageZoomViewActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}