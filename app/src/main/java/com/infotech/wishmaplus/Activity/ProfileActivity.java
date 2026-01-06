package com.infotech.wishmaplus.Activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.infotech.wishmaplus.Adapter.MultiContentAdapter;
import com.infotech.wishmaplus.Api.Object.BalanceResult;
import com.infotech.wishmaplus.Api.Object.ContentResult;
import com.infotech.wishmaplus.Api.Object.StoryResult;
import com.infotech.wishmaplus.Api.Response.BasicObjectResponse;
import com.infotech.wishmaplus.Api.Response.ContentResponse;
import com.infotech.wishmaplus.Api.Response.GroupDetailsResponse;
import com.infotech.wishmaplus.Api.Response.SignUpResponse;
import com.infotech.wishmaplus.Api.Response.UploadGroupCoverResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.CustomAlertDialog;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.Utility;
import com.infotech.wishmaplus.Utils.VideoEdit.AutoPlayVideo.CustomRecyclerView;
import com.wishmaplus.image.picker.ImagePicker;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileActivity extends AppCompatActivity {
    ImageView back_button;
    private PreferencesManager tokenManager;
    MaterialButton balance;
    private CustomLoader loader;
    CustomRecyclerView selfRecyclerView;
    List<ContentResult> contentList = new ArrayList<>();
    MultiContentAdapter adapter;
    ImageButton logout;
    AppCompatImageView moreBtn;
    UserDetailResponse userDetailResponse;
    GroupDetailsResponse groupDetailsResponse;
    private int pageNumber = 1;
    private int totalPost;
    public int buttonContentTypeId = 0;
    private ImagePicker imagePicker;
    private final int REQUEST_PERMISSIONS_IMAGE = 7676;
    private Snackbar mSnackBar;
    int isCoverPhoto;

    View profileView;

    String userId = "0";
    String pageId = null;
    boolean isProfile = false;
    private String groupId="";

    TextView user_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profileView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tokenManager = new PreferencesManager(this, 1);
        userDetailResponse = getIntent().getParcelableExtra("userData");
        if (getIntent() != null) {
            pageId = getIntent().getStringExtra("pageId");
            isProfile = getIntent().getBooleanExtra("isProfile", false);
        }
        if (isProfile) {
            pageId = "";
        }
        if (userDetailResponse == null) {
            userDetailResponse = UtilMethods.INSTANCE.getUserDetailResponse(tokenManager);
        }
        Intent intentParam = getIntent();
        if (intentParam != null && intentParam.hasExtra("groupId")) {
            groupId = intentParam.getStringExtra("groupId");
        }
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        back_button = findViewById(R.id.back_button);
        profileView = findViewById(R.id.profileView);
        logout = findViewById(R.id.logout);
        moreBtn = findViewById(R.id.moreBTn);
        balance = findViewById(R.id.balance);
        user_title = findViewById(R.id.user_title);
        moreBtn.setOnClickListener(view -> {
            showPopupMenu(view, this);
        });
        if (getIntent().getStringExtra("id") != null &&
                !Objects.equals(getIntent().getStringExtra("id"), "0")) {
            userId = getIntent().getStringExtra("id");
            balance.setVisibility(View.GONE);
            logout.setVisibility(View.GONE);
            moreBtn.setVisibility(VISIBLE);
            profileView.setVisibility(GONE);
        }
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            refresh();
            getUserDetail();
            pullToRefresh.setRefreshing(false);
        });

        selfRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        selfRecyclerView.setLayoutManager(layoutManager);


        selfRecyclerView.setActivity(this);

        //optional - to play only first visible video
        selfRecyclerView.setPlayOnlyFirstVideo(true); // false by default

        //optional - by default we check if url ends with ".mp4". If your urls do not end with mp4, you can set this param to false and implement your own check to see if video points to url
        selfRecyclerView.setCheckForMp4(false); //true by default

        //optional - download videos to local storage (requires "android.permission.WRITE_EXTERNAL_STORAGE" in manifest or ask in runtime)
        selfRecyclerView.setDownloadPath(getExternalCacheDir() + "/MyVideo"); // (Environment.getExternalStorageDirectory() + "/Video") by default

        selfRecyclerView.setVisiblePercent(70); // percentage of View that needs to be visible to start playing
        contentList.add(new ContentResult(MultiContentAdapter.VIEW_TYPE_PROFILE, userDetailResponse, null));
        setAdapter();
        selfRecyclerView.setScrollListener((dx, dy) -> {
            // Check if the user is scrolling downwards
            if (dy > 0 && contentList.get(contentList.size() - 1).getContentTypeId() != MultiContentAdapter.VIEW_TYPE_LOADING && (contentList.size() - 1) < totalPost) {
                // Get the current item count and the position of the last visible item
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                // If we are at the end of the list and more data is available, load more data
                if ((visibleItemCount + lastVisibleItemPosition) >= totalItemCount) {
                    contentList.add(new ContentResult(MultiContentAdapter.VIEW_TYPE_LOADING, null, null));
                    adapter.notifyItemInserted(contentList.size());
                    pageNumber = pageNumber + 1;
                    showContent(false);
                    //loadData(++currentPage);  // Increment page and load next set of data
                }
            }
        });
        /*selfRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Check if the user is scrolling downwards
                if (dy > 0 && contentlist.get(contentlist.size() - 1).getContentTypeId() != MultiContentAdapter.VIEW_TYPE_LOADING && (contentlist.size() - 1) < totalPost) {
                    // Get the current item count and the position of the last visible item
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    // If we are at the end of the list and more data is available, load more data
                    if ((visibleItemCount + lastVisibleItemPosition) >= totalItemCount) {
                        contentlist.add(new ContentResult(MultiContentAdapter.VIEW_TYPE_LOADING, null));
                        adapter.notifyItemInserted(contentlist.size());
                        pageNumber = pageNumber + 1;
                        showContent();
                        //loadData(++currentPage);  // Increment page and load next set of data
                    }
                }
            }
        });*/

        /*user_title = findViewById(R.id.user_title);
        user_name = findViewById(R.id.user_name);*/
       /* GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);*/


        /*selfRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    pauseAllVideos(); // Pause all videos when scrolling starts
                }else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    resumeVideos(); // Resume videos when scrolling stops
                }
            }
        });*/
        findViewById(R.id.back_button).setOnClickListener(view -> {
//            finish();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        });
//        back_button.setOnClickListener(v -> finish());
        logout.setOnClickListener(v ->
                signOut());
        /*editProfileIcon.setOnClickListener(v -> selectImage());*/
        imagePicker = new ImagePicker(this, null, imageUri -> {

            //File selectedImageFile = new File(imgUri.getPath());


            if (imageUri != null) {
                //String filePath = FileUtils.getPath(this, imageUri);
                //if (filePath != null) {
                // File file = new File(filePath);
                if(!(groupId==null) && !groupId.isEmpty()){
                    if (isCoverPhoto == 1) {
                        userDetailResponse.getResult().setCoverImageUrl(imageUri.getPath());
                    }
                }
                else {
                    if (isCoverPhoto == 1) {
                        userDetailResponse.setCoverPictureUrl(imageUri.getPath());
                    } else {
                        userDetailResponse.setProfilePictureUrl(imageUri.getPath());
                    }
                }

                contentList.get(0).setUserDetail(userDetailResponse);
                adapter.notifyItemChanged(0);
                if(!(groupId==null) && !groupId.isEmpty()){
                    updateGroupProfilePicture(new File(imageUri.getPath()));
                }else {
                    updateProfile(new File(imageUri.getPath()));
                }
                //}
            }
        }).setWithImageCrop();

        if(!(groupId==null) && !groupId.isEmpty())
        {
            balance.setVisibility(View.GONE);
            logout.setVisibility(View.GONE);
            user_title.setText("Group Dashboard");
            getGroupById();
        }

        loader.show();
        getUserDetail();
        showContent(false);
        getBalance();

    }
    public void getGroupById(){
        loader.show();
        UtilMethods.INSTANCE.getGroupById(groupId,new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }

                groupDetailsResponse=(GroupDetailsResponse) object;
                if(groupDetailsResponse.getStatusCode()==1){

                    userDetailResponse.setResult(groupDetailsResponse.getResult());

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

    public void updateGroupProfilePicture(File file){
        loader.show();
        MultipartBody.Part extraParam = null;

        if (file != null) {
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            extraParam = MultipartBody.Part.createFormData("model", file.getName(), requestFile);

        }
        UtilMethods.INSTANCE.updateGroupProfilePicture(groupId,true,extraParam,new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                UploadGroupCoverResponse uploadGroupCoverResponse = (UploadGroupCoverResponse) object;
                if(uploadGroupCoverResponse.getStatusCode()==1){
                    setResult(RESULT_OK, new Intent().putExtra("RefreshType", 1));
                    UtilMethods.INSTANCE.SuccessfulWithDismiss(ProfileActivity.this, uploadGroupCoverResponse.getResponseText());
                    refresh();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to Update Cover Picture: " + uploadGroupCoverResponse.getResponseText(), Toast.LENGTH_SHORT).show();
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

    private void showPopupMenu(View view, Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.dailog_action_button_pop_up, null);

        // Initialize the PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView, (int) context.getResources().getDimension(com.intuit.sdp.R.dimen._160sdp), ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Set up views in popup layout
        TextView block = popupView.findViewById(R.id.block);
        block.setOnClickListener(view1 -> {
            popupWindow.dismiss();
            openBlockDialog();
        });

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] + view.getHeight());

    }

    private void openBlockDialog() {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        blurBackground(rootView);
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_block_user);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView btnBlock = dialog.findViewById(R.id.btnBlock);
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        TextView txtDesc = dialog.findViewById(R.id.txtDesc);
        TextView friendUnfriend = dialog.findViewById(R.id.friendUnfriend);
        TextView takeABreak = dialog.findViewById(R.id.takeABreak);

        txtTitle.setText("Are you sure you want to block " + userDetailResponse.getFisrtName() + " " + userDetailResponse.getLastName() + "?");
        txtDesc.setText(userDetailResponse.getFisrtName() + " " + userDetailResponse.getLastName() + " will no longer be able to:");
        friendUnfriend.setText("If you're friends, blocking " + userDetailResponse.getFisrtName() + " " + userDetailResponse.getLastName() + " will also unfriend him/her.");
        takeABreak.setText("If you just want to limit what you share with " + userDetailResponse.getFisrtName() + " or see less of him, you can take a break instead. ");

        dialog.setOnDismissListener(dialogInterface -> removeBlur(rootView));
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnBlock.setOnClickListener(v -> {
            Toast.makeText(this, "User Blocked", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void blurBackground(View rootView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RenderEffect blurEffect = RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP);
            rootView.setRenderEffect(blurEffect);
        }
    }

    private void removeBlur(View rootView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rootView.setRenderEffect(null);
        }
    }

    private void setAdapter() {
        adapter = new MultiContentAdapter(userId, contentList, selfRecyclerView, tokenManager, this, new MultiContentAdapter.ClickCallBack() {
            @Override
            public void onClickCreatePost(String postId) {
                if(!(groupId==null) && !groupId.isEmpty()) {
                    Intent intent = new Intent(ProfileActivity.this, PostActivity.class);
                    intent.putExtra("userData", userDetailResponse);
                    intent.putExtra("postId", postId);
                    intent.putExtra("postType", 1);
                    intent.putExtra("groupId", groupId);
                    postActivityResultLauncher.launch(intent);
                }
                else {
                    Intent intent = new Intent(ProfileActivity.this, PostActivity.class);
                    intent.putExtra("userData", userDetailResponse);
                    intent.putExtra("postId", postId);
                    intent.putExtra("postType", 1);
                    postActivityResultLauncher.launch(intent);
                }

            }

            @Override
            public void onClickCreateStory(String storyId) {
                Intent intent = new Intent(ProfileActivity.this, PostActivity.class);
                intent.putExtra("userData", userDetailResponse);
                intent.putExtra("postId", storyId);
                intent.putExtra("postType", 2);
                storyActivityResultLauncher.launch(intent);
            }

            @Override
            public void onClickProfile(String userId, ContentResult content) {

            }

            @Override
            public void onOpenStory(ArrayList<StoryResult> list, int position, StoryResult result) {

            }

            @Override
            public void onDelete(int position) {
                contentList.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeRemoved((position > 0) ? (position - 1) : 0, contentList.size());
                totalPost = totalPost - 1;

            }
        },false);
        selfRecyclerView.setAdapter(adapter);
    }

    public void refresh() {
        /*selfRecyclerView.pauseVideo();
          selfRecyclerView.destroyVideo();*/
        if(!(groupId==null) && !groupId.isEmpty())
        {
            balance.setVisibility(View.GONE);
            logout.setVisibility(View.GONE);
            user_title.setText("Group Dashboard");
            getGroupById();
        }
        getUserDetail();
        pageNumber = 1;
        showContent(true);
        getBalance();
    }

    ActivityResultLauncher<Intent> postActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    setResult(RESULT_OK, new Intent().putExtra("RefreshType", 3));
                    refresh();
                }
            });

    ActivityResultLauncher<Intent> storyActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    setResult(RESULT_OK, new Intent().putExtra("RefreshType", 2));
                }
            });

    public void getBalance() {
        try {

            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicObjectResponse<BalanceResult>> call = git.getBalance("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<BasicObjectResponse<BalanceResult>>() {
                @Override
                public void onResponse(@NonNull Call<BasicObjectResponse<BalanceResult>> call, @NonNull Response<BasicObjectResponse<BalanceResult>> response) {
                    /*if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }*/
                    try {
                        BasicObjectResponse<BalanceResult> balanceResponse = response.body();
                        if (balanceResponse != null) {
                            if (balanceResponse.getStatusCode() == 1) {
                                if (balanceResponse.getResult() != null) {
                                    balance.setText(Utility.INSTANCE.formattedAmountWithRupees(balanceResponse.getResult().getBalance()));


                                }
                            } else {
                                UtilMethods.INSTANCE.Error(ProfileActivity.this, balanceResponse.getResponseText());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilMethods.INSTANCE.Error(ProfileActivity.this, e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BasicObjectResponse<BalanceResult>> call, @NonNull Throwable t) {
                    try {
                       /* if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }*/
                        UtilMethods.INSTANCE.apiFailureError(ProfileActivity.this, t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(ProfileActivity.this, ise.getMessage());
                        /*if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }*/
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            UtilMethods.INSTANCE.Error(ProfileActivity.this, e.getMessage());
           /* if (loader != null) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }
            }*/
        }
    }

    /*private void setUserData() {
        if (requestOptionsUserImage == null) {
            requestOptionsUserImage = UtilMethods.INSTANCE.getRequestOption_With_UserIcon();
        }
        Glide.with(ProfileActivity.this)
                .load(userDetailResponse.getProfilePictureUrl())
                .apply(requestOptionsUserImage)
                .into(profilePicture);
        user_title.setText(userDetailResponse.getFisrtName() + " " + userDetailResponse.getLastName());
        user_name.setText(userDetailResponse.getFisrtName() + " " + userDetailResponse.getLastName());
    }*/

    /* private void pauseAllVideos() {
         for (int i = 0; i < contentlist.size(); i++) {
             RecyclerView.ViewHolder holder = selfRecyclerView.findViewHolderForAdapterPosition(i);
             if (holder instanceof MultiContentAdapter.VideoViewHolder) {
                 MultiContentAdapter.VideoViewHolder videoHolder = (MultiContentAdapter.VideoViewHolder) holder;
                 if (videoHolder.videoView.isPlaying()) {
                     videoHolder.videoView.pause(); // Pause video
                 }
             }
         }
     }
     private void resumeVideos() {
         for (int i = 0; i < contentlist.size(); i++) {
             RecyclerView.ViewHolder holder = selfRecyclerView.findViewHolderForAdapterPosition(i);
             if (holder instanceof MultiContentAdapter.VideoViewHolder) {
                 MultiContentAdapter.VideoViewHolder videoHolder = (MultiContentAdapter.VideoViewHolder) holder;
                 if (!videoHolder.videoView.isPlaying() && isViewVisible(videoHolder.itemView)) {
                     //  videoHolder.videoView.seekTo(vi);
                     videoHolder.videoView.start(); // Resume video if visible
                 }
             }
         }
     }*/
    private boolean isViewVisible(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Rect rect = new Rect();
        rect.set(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight());
        return rect.intersects(0, 0, Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
    }

    public void selectProfileImage() {
        isCoverPhoto = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSIONS_IMAGE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_IMAGE);
        } else {
            imagePicker.choosePictureWithoutPermission(true, true);
        }
       /* Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        profileImageResultLauncher.launch(intent);*/
    }

    public void selectCoverImage() {
        isCoverPhoto = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSIONS_IMAGE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_IMAGE);
        } else {
            imagePicker.choosePictureWithoutPermission(true, true);
        }
        /*Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        coverImageResultLauncher.launch(intent);*/
    }


    public void updateUserDetails() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("UserDetail", userDetailResponse);
        updateUserResultLauncher.launch(intent);
    }

    private void signOut() {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, true);
        customAlertDialog.Successfullogout("Do you really want to Logout?", ProfileActivity.this, tokenManager);
       /* mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {

            // Toast.makeText(ProfileActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();

        });*/
    }

    private void getUserDetail() {
        loader.show();
        if (pageId != null && !isProfile) {
            UtilMethods.INSTANCE.getPageDetail(this, pageId, loader, tokenManager, object -> {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                profileView.setVisibility(VISIBLE);
                userDetailResponse = (UserDetailResponse) object;
                if(!(groupId==null) && !groupId.isEmpty()){
                    userDetailResponse.setResult(groupDetailsResponse.getResult());
                }
                contentList.set(0, new ContentResult(0, userDetailResponse, null));
                adapter.notifyItemChanged(0);
            });
        } else {
            String finalId = (userId != null && !userId.isEmpty())
                    ? userId
                    : pageId;
            UtilMethods.INSTANCE.userDetail(this, finalId,groupId, loader, tokenManager, object -> {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                profileView.setVisibility(VISIBLE);
                userDetailResponse = (UserDetailResponse) object;
                if(!(groupId==null) && !groupId.isEmpty()){
                    userDetailResponse.setResult(groupDetailsResponse.getResult());
                }
                contentList.set(0, new ContentResult(0, userDetailResponse, null));
                adapter.notifyItemChanged(0);
            });
        }
    }

    ActivityResultLauncher<Intent> updateUserResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == Activity.RESULT_OK) {
            setResult(RESULT_OK, new Intent().putExtra("RefreshType", 1));
            getUserDetail();
            refresh();
        }

    });
   /* ActivityResultLauncher<Intent> profileImageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == Activity.RESULT_OK) {
            if (result.getData() != null) {
                Uri imageUri = result.getData().getData();


                if (imageUri != null) {
                    String filePath = FileUtils.getPath(this, imageUri);
                    if (filePath != null) {
                        File file = new File(filePath);
                        userDetailResponse.setProfilePictureUrl(file.getPath());
                        contentList.get(0).setUserDetail(userDetailResponse);
                        adapter.notifyItemChanged(0);
                        updateProfile(file);
                    }
                }
            }
        }

    });*/

   /* ActivityResultLauncher<Intent> coverImageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == Activity.RESULT_OK) {
            if (result.getData() != null) {
                Uri imageUri = result.getData().getData();


                if (imageUri != null) {
                    String filePath = FileUtils.getPath(this, imageUri);
                    if (filePath != null) {
                        File file = new File(filePath);
                        userDetailResponse.setCoverPictureUrl(file.getPath());
                        contentList.get(0).setUserDetail(userDetailResponse);
                        adapter.notifyItemChanged(0);
                        updateProfile(file);
                    }

                }
            }
        }

    });*/

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            // Apply image to ImageView using Glide
            if (requestOptionsUserImage == null) {
                requestOptionsUserImage = UtilMethods.INSTANCE.getRequestOption_With_UserIcon();
            }
            Glide.with(this)
                    .load(imageUri)
                    .apply(requestOptionsUserImage)
                    .into(profilePicture);
            //Uri selectedMediaUri = data.getData();// Set image in ImageView
            if (imageUri != null) {
                String filePath = FileUtils.getPath(this, imageUri);
                File file = new File(filePath);
                updateProfile(file);
            }


        }
    }*/

    private void updateProfile(File file) {
        try {
            loader.show();
            MultipartBody.Part extraParam = null;

            if (file != null) {
                String mimeType = URLConnection.guessContentTypeFromName(file.getName());
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
                extraParam = MultipartBody.Part.createFormData("model", file.getName(), requestFile);

            }
            // RequestBody isCoverPhotoPart = RequestBody.create(String.valueOf(isCoverPhoto), MediaType.parse("multipart/form-data"));
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<SignUpResponse> call = git.updateProfilePicture("Bearer " + tokenManager.getAccessToken(), isCoverPhoto, extraParam);
            call.enqueue(new Callback<SignUpResponse>() {
                @Override
                public void onResponse(@NonNull Call<SignUpResponse> call, @NonNull Response<SignUpResponse> response) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatusCode() == 1) {
                                setResult(RESULT_OK, new Intent().putExtra("RefreshType", 1));
                                UtilMethods.INSTANCE.SuccessfulWithDismiss(ProfileActivity.this, response.body().getResponseText());
                                refresh();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed to Update Profile Picture: " + response.body().getResponseText(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            try {
                                if (response.errorBody() != null) {
                                    Toast.makeText(ProfileActivity.this, "Failed to Update Profile Picture: " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                                }

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }


                        }

                    } else {
                        UtilMethods.INSTANCE.apiErrorHandle(ProfileActivity.this, response.code(), response.message());
                    }

                }

                @Override
                public void onFailure(@NonNull Call<SignUpResponse> call, @NonNull Throwable t) {
                    try {
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }
                        UtilMethods.INSTANCE.apiFailureError(ProfileActivity.this, t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(ProfileActivity.this, ise.getMessage());
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
            if (loader != null) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }
            }
            Log.e("Exception", "Error: " + e.getMessage());
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void showContent(boolean isFromRefresh) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<ContentResponse> call = git.getContent("Bearer " + tokenManager.getAccessToken(), "", userId, pageNumber, 20, true, pageId,groupId, buttonContentTypeId,false);
            call.enqueue(new Callback<ContentResponse>() {
                @Override
                public void onResponse(@NonNull Call<ContentResponse> call, @NonNull Response<ContentResponse> response) {
                    if (loader != null) {
                        if (loader.isShowing())
                            loader.dismiss();
                    }
                    if (response.isSuccessful()) {
                        ContentResponse contentResponse = response.body();
                        if (contentResponse != null) {
                            totalPost = contentResponse.getTotalPost();
                            if (contentResponse.getResult() != null && contentResponse.getResult().size() > 0) {
                                List<ContentResult> resultList = contentResponse.getResult();
                                //extra - start downloading all videos in background before loading RecyclerView
                                List<String> urls = new ArrayList<>();
                                for (ContentResult object : resultList) {
                                    if (object.getContentTypeId() == 2 && object.getPostContent() != null && object.getPostContent().contains("http"))
                                        urls.add(object.getPostContent());
                                }
                                selfRecyclerView.preDownload(urls);

                                if (pageNumber == 1) {
                                    if (isFromRefresh) {
                                        selfRecyclerView.pauseVideo();
                                        selfRecyclerView.destroyVideo();
                                    }
                                    contentList = new ArrayList<>();
                                    contentList.add(new ContentResult(MultiContentAdapter.VIEW_TYPE_PROFILE, userDetailResponse, null));
                                    contentList.addAll(resultList);
                                    //adapter.notifyItemRangeChanged(0, contentlist.size());
                                    setAdapter();
                                } else {
                                    contentList.remove(contentList.size() - 1);
                                    adapter.notifyItemRemoved(contentList.size() - 1);
                                    int size = contentList.size();
                                    contentList.addAll(resultList);
                                    adapter.notifyItemRangeChanged(size, contentList.size());
                                }



                           /* if(pageNumber==1){
                                //call this functions when u want to start autoplay on loading async lists (eg firebase)
                                selfRecyclerView.smoothScrollBy(0, 1);
                                selfRecyclerView.smoothScrollBy(0, -1);
                            }*/
                            } else {
                                if (isFromRefresh) {
                                    selfRecyclerView.pauseVideo();
                                    selfRecyclerView.destroyVideo();
                                }
                                contentList = new ArrayList<>();
                                contentList.add(new ContentResult(MultiContentAdapter.VIEW_TYPE_PROFILE, userDetailResponse, null));
                                //adapter.notifyItemRangeChanged(0, contentlist.size());
                                setAdapter();
                            }
                        } else {
                            if (isFromRefresh) {
                                selfRecyclerView.pauseVideo();
                                selfRecyclerView.destroyVideo();
                            }
                            contentList = new ArrayList<>();
                            contentList.add(new ContentResult(MultiContentAdapter.VIEW_TYPE_PROFILE, userDetailResponse, null));
                            //adapter.notifyItemRangeChanged(0, contentlist.size());
                            setAdapter();
                        }
                    } else {
                        UtilMethods.INSTANCE.apiErrorHandle(ProfileActivity.this, response.code(), response.message());
                        //Toast.makeText(ProfileActivity.this, "Failed to fetch content", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ContentResponse> call, @NonNull Throwable t) {
                    //Toast.makeText(ProfileActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    UtilMethods.INSTANCE.apiFailureError(ProfileActivity.this, t);
                    if (loader != null) {
                        if (loader.isShowing())
                            loader.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ProfileActivity.this, "API call failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            if (loader != null) {
                if (loader.isShowing())
                    loader.dismiss();
            }
        }
    }
   /* private void showContent() {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            String postId = "1";
            int pageNumber = 1;
            int pageSize = 10;
            boolean IsSelf = true;
            Call<ContentResponse> call = git.getContent("Bearer " + tokenManager.getAccessToken(), postId, pageNumber, pageSize, IsSelf);
            call.enqueue(new Callback<ContentResponse>() {
                @Override
                public void onResponse(Call<ContentResponse> call, Response<ContentResponse> response) {
                    if (loader != null) {
                        if (loader.isShowing())
                            loader.dismiss();
                    }
                    if (response.isSuccessful()) {
                        ContentResponse contentResponse = response.body();
                        if (contentResponse != null && contentResponse.getResult() != null) {
                            List<ContentResult> resultList = contentResponse.getResult();
                            contentlist.clear();
                            contentlist.addAll(resultList);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to fetch content", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ContentResponse> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    if (loader != null) {
                        if (loader.isShowing())
                            loader.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (loader != null) {
                if (loader.isShowing())
                    loader.dismiss();
            }
        }
    }*/

   /* @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePicker != null) {
            imagePicker.handleActivityResult(resultCode, requestCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_IMAGE) {
            int permissionCheck = PackageManager.PERMISSION_GRANTED;
            for (int permission : grantResults) {
                permissionCheck = permissionCheck + permission;
            }
            if ((grantResults.length > 0) && permissionCheck == PackageManager.PERMISSION_GRANTED) {
                imagePicker.choosePictureWithoutPermission(true, true);
            } else {
                showWarningSnack(R.string.str_ShowOnPermisstionDenied, "Enable", true);
            }
        } else {
            if (imagePicker != null) {
                imagePicker.handlePermission(requestCode, grantResults);
            }
        }
    }

    void showWarningSnack(int stringId, String btn, final boolean isForSetting) {
        if (mSnackBar != null && mSnackBar.isShown()) {
            return;
        }

        mSnackBar = Snackbar.make(findViewById(android.R.id.content), stringId,
                Snackbar.LENGTH_INDEFINITE).setAction(btn,
                v -> {
                    if (isForSetting) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                    } else {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSIONS_IMAGE);
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_IMAGE);
                        } else {
                            imagePicker.choosePictureWithoutPermission(true, true);
                        }
                    }

                });

        mSnackBar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
        TextView mainTextView = (TextView) (mSnackBar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
        mainTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(com.intuit.ssp.R.dimen._12ssp));
        mainTextView.setMaxLines(4);
        mSnackBar.show();

    }

    @Override
    public void onDestroy() {
        selfRecyclerView.destroyVideo();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        selfRecyclerView.pauseVideo();
        super.onPause();
    }

    @Override
    public void onResume() {
        selfRecyclerView.playVideo();
        super.onResume();
    }
}
