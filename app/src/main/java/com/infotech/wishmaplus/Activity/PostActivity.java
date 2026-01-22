package com.infotech.wishmaplus.Activity;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.exifinterface.media.ExifInterface;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.infotech.wishmaplus.Api.Object.PackageResult;
import com.infotech.wishmaplus.Api.Response.BasicObjectResponse;
import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.ApplicationConstant;
import com.infotech.wishmaplus.Utils.CustomAlertDialog;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.FileUtils;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.Utility;
import com.wishmaplus.image.picker.ImagePicker;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity implements CustomAlertDialog.CallBack {
    UserDetailResponse userDetailResponse;
    private RequestOptions requestOptionsUserImage;
    private TextView nameTv, dailyPostLimit, postTitle;
    private ImageView profileIv;
    private EditText textInputEt;
    private MaterialButton btn;
    private ImageButton clearImage;
    private ImageView playBtn, image;
    private PreferencesManager tokenManager;
    private CustomLoader loader;
    private int mediaType = UtilMethods.INSTANCE.TEXT_TYPE;
    private File selectedFile;
    private File captureFile;
    private boolean isDeleteFileAllow = false;
    private boolean isProfile = false;
    private String postId,pageId,groupId="";
    private PackageResult packageSetting;
    int postType = 1;  //1=> Post, 2=> Story, 3=> Reel
    private ImagePicker imagePicker;
    private int REQUEST_PERMISSIONS_CAMERA = 7676;
    private int REQUEST_PERMISSIONS_GALLERY = 4545;
    private Snackbar mSnackBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.postAC), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tokenManager = new PreferencesManager(this, 1);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        userDetailResponse = getIntent().getParcelableExtra("userData");
        postId = getIntent().getStringExtra("postId");
        isProfile = getIntent().getBooleanExtra("isProfile",false);

        if(getIntent().getStringExtra("pageId")!=null && !Objects.requireNonNull(getIntent().getStringExtra("pageId")).isEmpty()){
            pageId = getIntent().getStringExtra("pageId");
        }else{
            pageId ="";
        }
        if(getIntent().getStringExtra("groupId")!=null && !Objects.requireNonNull(getIntent().getStringExtra("groupId")).isEmpty()){
            groupId = getIntent().getStringExtra("groupId");
        }else{
            groupId ="";
        }
        postType = getIntent().getIntExtra("postType", 1);
        if (userDetailResponse == null) {
            userDetailResponse = UtilMethods.INSTANCE.getUserDetailResponse(tokenManager);
        }
        postTitle = findViewById(R.id.post_title);

        nameTv = findViewById(R.id.nameTv);
        dailyPostLimit = findViewById(R.id.dailyPostLimit);
        profileIv = findViewById(R.id.profile);
        btn = findViewById(R.id.postBTn);
        textInputEt = findViewById(R.id.textInputEt);
        clearImage = findViewById(R.id.clearImage);
        playBtn = findViewById(R.id.playBtn);
        image = findViewById(R.id.image);
        if (postType == 2) {
            postTitle.setText(R.string.create_story);
            btn.setText(R.string.post_story);
        }

        if (userDetailResponse != null) {
            setUserData();
        } else {
           if(pageId!=null && !isProfile){
               UtilMethods.INSTANCE.getPageDetail(this,pageId, loader, tokenManager, object -> {
                   userDetailResponse = (UserDetailResponse) object;
                   setUserData();
               });
           }else{
               UtilMethods.INSTANCE.userDetail(this,"0",groupId, loader, tokenManager, object -> {
                   userDetailResponse = (UserDetailResponse) object;
                   setUserData();
               });
           }
        }

        textInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0 || selectedFile != null) {
                    ViewCompat.setBackgroundTintList(btn, ContextCompat.getColorStateList(PostActivity.this, R.color.colorAccent));
                    btn.setEnabled(true);
                } else {
                    ViewCompat.setBackgroundTintList(btn, ContextCompat.getColorStateList(PostActivity.this, R.color.grey_4));
                    btn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        profileIv.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("userData", userDetailResponse);
            startActivity(intent);
        });

        findViewById(R.id.postBTn).setOnClickListener(view -> {
//            if (packageSetting != null) {
//                if (!packageSetting.isDailyPostLimitExceed()) {

                    if (mediaType == UtilMethods.INSTANCE.TEXT_TYPE && false/*!packageSetting.isTextCanPost()*/) {
                        CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, true);
                        customAlertDialog.Warning("Upgrade Package", "You can't post text, upgrade your package to post text", "Upgrade", this);
                    } else if (textInputEt.getText().toString().trim().length() > 0 && false/*!packageSetting.isTextCanPost()*/) {
                        CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, true);
                        customAlertDialog.Warning("Upgrade Package", "You can't post text, upgrade your package to post text", "Upgrade", this);
                    } else if (mediaType == UtilMethods.INSTANCE.IMAGE_TYPE && false/*!packageSetting.isImageCanPost()*/) {
                        CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, true);
                        customAlertDialog.Warning("Upgrade Package", "You can't post image, upgrade your package to post image", "Upgrade", this);
                    } else if (mediaType == UtilMethods.INSTANCE.VIDEO_TYPE && false/*!packageSetting.isVideoCanPost()*/) {
                        CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, true);
                        customAlertDialog.Warning("Upgrade Package", "You can't post video, upgrade your package to post video", "Upgrade", this);
                    } else {
                        if (mediaType == UtilMethods.INSTANCE.VIDEO_TYPE && postType == 2 && Utility.INSTANCE.getFileDurationLess42(PostActivity.this, selectedFile.getPath()) > 60000) {
                            UtilMethods.INSTANCE.Error(this, "You can't post story longer then 60 seconds");
                        } else {
                            if (textInputEt.getText().toString().trim().length() > 0 && selectedFile == null) {
                                mediaType = UtilMethods.INSTANCE.TEXT_TYPE;
                            }
                            postContent(mediaType, textInputEt.getText().toString().trim(), textInputEt.getText().toString().trim(), selectedFile);
                        }
                    }
//                } else {
//                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, true);
//                    customAlertDialog.Warning("Limit exceed", "Your daily limit has been exceeded, upgrade your package to increase daily limit", "Upgrade", this);
//                }
//            } else {
//                getPackageSetting();
//            }

        });

        findViewById(R.id.back_button).setOnClickListener(view -> {
            finish();
        });
        image.setOnClickListener(view -> {
//            if (packageSetting != null) {
//                if (!packageSetting.isDailyPostLimitExceed()) {
//                    if (packageSetting.isMixingAllowed()) {
                        /* ContentResolver cR = getContentResolver();
            String type = cR.getType(Uri.fromFile(selectedFile));*/
                        String type = URLConnection.guessContentTypeFromName(selectedFile.getPath());
                        if (type != null) {
                            if (type.toLowerCase().contains("video")) {
                                videoEditResultLauncher.launch(new Intent(this, VideoEditActivity.class)
                                        .putExtra("MusicFromSystemOnly", false/*packageSetting.isMusicFromSystemOnly()*/)
                                        .putExtra("postType", postType)
                                        .putExtra("VideoPath", selectedFile.getPath()));
                            } else {
                                videoEditResultLauncher.launch(new Intent(this, ImageEditActivity.class)
                                        .putExtra("MusicFromSystemOnly", false/*packageSetting.isMusicFromSystemOnly()*/)
                                        .putExtra("postType", postType)
                                        .putExtra("ImagePath", selectedFile.getPath()));
                            }
                        } else {
                            if (mediaType == UtilMethods.INSTANCE.VIDEO_TYPE) {
                                videoEditResultLauncher.launch(new Intent(this, VideoEditActivity.class)
                                        .putExtra("MusicFromSystemOnly", false/*packageSetting.isMusicFromSystemOnly()*/)
                                        .putExtra("postType", postType)
                                        .putExtra("VideoPath", selectedFile.getPath()));
                            } else if (mediaType == UtilMethods.INSTANCE.IMAGE_TYPE) {
                                videoEditResultLauncher.launch(new Intent(this, ImageEditActivity.class)
                                        .putExtra("MusicFromSystemOnly", false/*packageSetting.isMusicFromSystemOnly()*/)
                                        .putExtra("postType", postType)
                                        .putExtra("ImagePath", selectedFile.getPath()));
                            }
                        }

//                    } else {
//                        CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, true);
//                        customAlertDialog.Warning("Upgrade Package", "You can't edit post, upgrade your package to edit post", "Upgrade", this);
//                    }
//                } else {
//                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, true);
//                    customAlertDialog.Warning("Limit Exceed", "Your daily limit has been exceeded, upgrade your package to increase daily limit", "Upgrade", this);
//                }
//            } else {
//                getPackageSetting();
//            }

        });

        clearImage.setOnClickListener(view -> {
            if (isDeleteFileAllow && selectedFile != null && selectedFile.exists()) {
                selectedFile.delete();
            }
            isDeleteFileAllow = false;
            selectedFile = null;
            image.setVisibility(View.GONE);
            playBtn.setVisibility(View.GONE);
            clearImage.setVisibility(View.GONE);
            if (textInputEt.getText().toString().trim().isEmpty()) {
                ViewCompat.setBackgroundTintList(btn, ContextCompat.getColorStateList(PostActivity.this, R.color.grey_4));
                btn.setEnabled(false);
            }

        });

        findViewById(R.id.camera).setOnClickListener(view -> {
//            if (packageSetting != null) {
//                if (!packageSetting.isDailyPostLimitExceed()) {
//                    if (packageSetting.isImageCanPost()) {
                        mediaType = UtilMethods.INSTANCE.IMAGE_TYPE;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSIONS_CAMERA);
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                                (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CAMERA);
                        } else {
                            selectCamera();
                        }
//                    } else {
//                        CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, true);
//                        customAlertDialog.Warning("Upgrade Package", "You can't post image, upgrade your package to post image", "Upgrade", this);
//                    }
//                } else {
//                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, true);
//                    customAlertDialog.Warning("Limit Exceed", "Your daily limit has been exceeded, upgrade your package to increase daily limit", "Upgrade", this);
//                }
//            } else {
//                getPackageSetting();
//            }


        });
        findViewById(R.id.gallery).setOnClickListener(view -> {
//            if (packageSetting != null) {
//                if (!packageSetting.isDailyPostLimitExceed()) {
//                    if (packageSetting.isImageCanPost()) {
                        mediaType = UtilMethods.INSTANCE.IMAGE_TYPE;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSIONS_GALLERY);
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_GALLERY);
                        } else {
                            selectMedia();
                        }
//                    } else {
//                        CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, true);
//                        customAlertDialog.Warning("Upgrade Package", "You can't post image, upgrade your package to post image", "Upgrade", this);
//                    }
//                } else {
//                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, true);
//                    customAlertDialog.Warning("Limit Exceed", "Your daily limit has been exceeded, upgrade your package to increase daily limit", "Upgrade", this);
//                }
//            } else {
//                getPackageSetting();
//            }

        });

        findViewById(R.id.video).setOnClickListener(view -> {
//            if (packageSetting != null) {
//                if (!packageSetting.isDailyPostLimitExceed()) {
//                    if (packageSetting.isVideoCanPost()) {
                        mediaType = UtilMethods.INSTANCE.VIDEO_TYPE;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED)) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_VIDEO}, REQUEST_PERMISSIONS_CAMERA);
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                                (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CAMERA);
                        } else {
                            selectCamera();
                        }
//                    } else {
//                        CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, true);
//                        customAlertDialog.Warning("Upgrade Package", "You can't post video, upgrade your package to post video", "Upgrade", this);
//                    }
//                } else {
//                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, true);
//                    customAlertDialog.Warning("Limit Exceed", "Your daily limit has been exceeded, upgrade your package to increase daily limit", "Upgrade", this);
//                }
//            } else {
//                getPackageSetting();
//            }


        });

        findViewById(R.id.videoGallery).setOnClickListener(view -> {
//            if (packageSetting != null) {
//                if (!packageSetting.isDailyPostLimitExceed()) {
//                    if (packageSetting.isVideoCanPost()) {
                        mediaType = UtilMethods.INSTANCE.VIDEO_TYPE;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_VIDEO}, REQUEST_PERMISSIONS_GALLERY);
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_GALLERY);
                        } else {
                            selectMedia();
                        }
//                    } else {
//                        CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, true);
//                        customAlertDialog.Warning("Upgrade Package", "You can't post video, upgrade your package to post video", "Upgrade", this);
//                    }
//                } else {
//                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, true);
//                    customAlertDialog.Warning("Limit Exceed", "Your daily limit has been exceeded, upgrade your package to increase daily limit", "Upgrade", this);
//                }
//            } else {
//                getPackageSetting();
//            }

        });

        imagePicker = new ImagePicker(this, null, imageUri -> {

            if (imageUri != null) {
                if (isDeleteFileAllow && selectedFile != null && selectedFile.exists()) {
                    selectedFile.delete();
                }
                isDeleteFileAllow = false;
                selectedFile = new File(imageUri.getPath());
                ViewCompat.setBackgroundTintList(btn, ContextCompat.getColorStateList(PostActivity.this, R.color.colorAccent));
                btn.setEnabled(true);
                if (selectedFile != null) {
                    image.setVisibility(View.VISIBLE);
                    clearImage.setVisibility(View.VISIBLE);
                    image.performClick();
                    if (mediaType == UtilMethods.INSTANCE.VIDEO_TYPE) {

                        playBtn.setVisibility(View.VISIBLE);
                        Glide.with(PostActivity.this)
                                .load(selectedFile)
                                .into(image);

                    } else {
                        playBtn.setVisibility(View.GONE);
                        image.setImageURI(imageUri);
                    }


                }


            }
        }).setWithImageCrop();

//        getPackageSetting();
    }


    private void checkPermission(boolean isCamera) {

        if (isCamera) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSIONS_CAMERA);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CAMERA);
            } else {
                selectCamera();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSIONS_CAMERA);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CAMERA);
            } else {
                selectMedia();
            }
        }


    }

    private void selectMedia() {
        captureFile = null;
        if (mediaType == UtilMethods.INSTANCE.VIDEO_TYPE) {
            try {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_MIME_TYPES, "video/*");
                activityResultLauncher.launch(intent);
            } catch (Exception e) {
                try {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.setType("video/*");
                    activityResultLauncher.launch(intent);
                } catch (Exception e1) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("video/*");
                        activityResultLauncher.launch(intent);
                    } catch (Exception e2) {
                        Toast.makeText(PostActivity.this, "Application is not available", Toast.LENGTH_LONG).show();
                        e2.printStackTrace();
                    }
                }
            }
        } else {
            imagePicker.choosePictureWithoutPermission(false, true);
        }
    }

    private void selectCamera() {

        if (mediaType == UtilMethods.INSTANCE.VIDEO_TYPE) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        /*videoFile = OptiUtils.createVideoFile(context!!)
        Log.v(tagName, "videoPath1: " + videoFile!!.absolutePath)
        videoUri = FileProvider.getUriForFile(
                context!!,
                "com.obs.marveleditor.provider", videoFile!!
            )*/
       /* cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 240) ;//4 minutes
        cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoFile);*/
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            createVideoFile(cameraIntent, ".mp4");
            activityResultLauncher.launch(cameraIntent);
        } else {
            imagePicker.choosePictureWithoutPermission(true, false);
        }


    }

    void createVideoFile(Intent intent, String sufix) {
        String timeStamp = new SimpleDateFormat(ApplicationConstant.INSTANCE.DATE_FORMAT, Locale.getDefault()).format(new Date());
        String imageFileName = getString(R.string.app_name) + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (!storageDir.exists()) storageDir.mkdirs();
        try {
            captureFile = File.createTempFile(imageFileName, sufix, storageDir);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, getPackageName() + ".provider_smart_image_picker", captureFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == Activity.RESULT_OK) {
            if (isDeleteFileAllow && selectedFile != null && selectedFile.exists()) {
                selectedFile.delete();
            }
            isDeleteFileAllow = false;
            Uri selectedMediaUri = null;
            if (captureFile != null) {
                selectedMediaUri = Uri.fromFile(captureFile);
                ViewCompat.setBackgroundTintList(btn, ContextCompat.getColorStateList(PostActivity.this, R.color.colorAccent));
                btn.setEnabled(true);
                selectedFile = captureFile;
            } else if (result.getData() != null && result.getData().getData() != null) {
                selectedMediaUri = result.getData().getData();
                ViewCompat.setBackgroundTintList(btn, ContextCompat.getColorStateList(PostActivity.this, R.color.colorAccent));
                btn.setEnabled(true);
                String filePath = FileUtils.getPath(PostActivity.this, selectedMediaUri);
                if (filePath != null) {
                    selectedFile = new File(filePath);
                }
            }
            if (selectedFile != null) {
                image.setVisibility(View.VISIBLE);
                clearImage.setVisibility(View.VISIBLE);
                image.performClick();
                if (mediaType == UtilMethods.INSTANCE.VIDEO_TYPE) {

                    playBtn.setVisibility(View.VISIBLE);
                    Glide.with(PostActivity.this)
                            .load(selectedFile)
                            .into(image);

                } else {
                    playBtn.setVisibility(View.GONE);
                    image.setImageURI(selectedMediaUri);
                }


            }
        }

    });


    ActivityResultLauncher<Intent> videoEditResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == Activity.RESULT_OK) {
            if (result.getData() != null) {
                String videoPath = result.getData().getStringExtra("VideoPath");
                isDeleteFileAllow = result.getData().getBooleanExtra("IS_DELETE_ALLOW", false);
                selectedFile = new File(videoPath);
            }

            // Log.e("Video Path", videoPath);
            /*if(result.getData() != null && result.getData().getData()!=null){
                Uri selectedMediaUri = result.getData().getData();
                ViewCompat.setBackgroundTintList(btn, ContextCompat.getColorStateList(PostActivity.this, R.color.colorAccent));
                btn.setEnabled(true);
                String filePath = FileUtils.getPath(PostActivity.this, selectedMediaUri);
                selectedFile = new File(filePath);
            }*/
            if (selectedFile != null) {

                image.setVisibility(View.VISIBLE);
                clearImage.setVisibility(View.VISIBLE);

                /*ContentResolver cR = getContentResolver();
                String type = cR.getType(Uri.fromFile(selectedFile));*/
                String type = URLConnection.guessContentTypeFromName(selectedFile.getPath());

                if (type != null) {
                    if (type.toLowerCase().contains("video")) {
                        mediaType = UtilMethods.INSTANCE.VIDEO_TYPE;
                        playBtn.setVisibility(View.VISIBLE);
                    } else {
                        mediaType = UtilMethods.INSTANCE.IMAGE_TYPE;
                        playBtn.setVisibility(View.GONE);
                    }
                } else {
                    if (mediaType == UtilMethods.INSTANCE.VIDEO_TYPE) {
                        playBtn.setVisibility(View.VISIBLE);
                    } else {
                        playBtn.setVisibility(View.GONE);
                    }
                }


                Glide.with(PostActivity.this)
                        .load(selectedFile)
                        .into(image);

            }
        }

    });

    private void setUserData() {
        if (requestOptionsUserImage == null) {
            requestOptionsUserImage = UtilMethods.INSTANCE.getRequestOption_With_UserIcon();
        }
        Glide.with(this)
                .load(userDetailResponse.getProfilePictureUrl())
                .apply(requestOptionsUserImage)
                .into(profileIv);
        nameTv.setText(userDetailResponse.getFisrtName() + " " + userDetailResponse.getLastName());

    }


    private void postContent(int typeId, String content, String caption, File file) {
        loader.show();
        EndPointInterface service = ApiClient.getClient().create(EndPointInterface.class);
        RequestBody postIdPart = createPartFromString(postId);
        RequestBody pageIdPart = createPartFromString(pageId);
        RequestBody groupIdPart = createPartFromString(groupId);
        RequestBody typeIdPart = createPartFromString(String.valueOf(typeId));
        RequestBody contentPart = createPartFromString(content == null ? "" : content);
        RequestBody captionPart = createPartFromString(caption == null ? "" : caption);
        RequestBody heightPart;
        RequestBody widthPart;
        RequestBody durationPart;
        if (typeId == UtilMethods.INSTANCE.VIDEO_TYPE) {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                retriever.setDataSource(file.getPath());
                Bitmap thumbnail = retriever.getFrameAtTime(0);  // You can specify the time in microseconds
                int width = thumbnail.getWidth();
                int height = thumbnail.getHeight();
                heightPart = createPartFromString(height + "");
                widthPart = createPartFromString(width + "");
            } catch (Exception e) {
                e.printStackTrace();
                heightPart = createPartFromString("0");
                widthPart = createPartFromString("0");
            } finally {
                try {
                    retriever.release();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            durationPart = createPartFromString(Utility.INSTANCE.getFileDuration(this, file.getPath()) + "");
        }
        else if (typeId == UtilMethods.INSTANCE.IMAGE_TYPE) {
            /*BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;  // Only load the bounds (dimensions)

            BitmapFactory.decodeFile(file.getPath(), options);
///sdcard/.transforms/synthetic/picker_get_content/0/com.android.providers.media.photopicker/media/1000000481.jpg
///sdcard/.transforms/synthetic/picker_get_content/0/com.android.providers.media.photopicker/media/1000000481.jpg
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;*/
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(file.getPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            int imageWidth = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
            int imageHeight = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
            heightPart = createPartFromString(imageHeight + "");
            widthPart = createPartFromString(imageWidth + "");
            durationPart = createPartFromString("30000");
        }
        else {
            heightPart = createPartFromString("0");
            widthPart = createPartFromString("0");
            durationPart = createPartFromString("30000");
        }


        MultipartBody.Part extraParam = null;

        if (file != null) {
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            if (file.getName().endsWith(".mp4")) {
                mimeType = "video/mp4";
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            extraParam = MultipartBody.Part.createFormData("ExtraParam", file.getName(), requestFile);
        }
        Call<BasicResponse> call;
        if (postType == 2) {
            call = service.saveStory(
                    "Bearer " + tokenManager.getAccessToken(),
                    postIdPart,
                    typeIdPart,
                    contentPart,
                    captionPart,
                    heightPart,
                    widthPart,
                    durationPart,
                    extraParam
            );
        } else {
            call = service.postContent(
                    "Bearer " + tokenManager.getAccessToken(),
                    postIdPart,
                    typeIdPart,
                    contentPart,
                    captionPart,
                    heightPart,
                    widthPart,
                    pageIdPart,
                    groupIdPart,
                    durationPart,
                    extraParam
            );
        }

        call.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(@NonNull Call<BasicResponse> call, @NonNull Response<BasicResponse> response) {
                if (loader != null) {
                    if (loader.isShowing())
                        loader.dismiss();
                }
                if (response.isSuccessful()) {
                    BasicResponse basicResponse = response.body();
                    if (basicResponse != null) {
                        if (basicResponse.getStatusCode() == 1) {
                            if (isDeleteFileAllow && file != null && file.exists()) {
                                file.delete();
                            }
                            isDeleteFileAllow = false;
                            UtilMethods.INSTANCE.SuccessfulWithFinsh(PostActivity.this, false, basicResponse.getResponseText(), typeId,pageId);
                        } else {
                            UtilMethods.INSTANCE.Error(PostActivity.this, basicResponse.getResponseText());
                        }
                    }
                } else {
                    UtilMethods.INSTANCE.apiErrorHandle(PostActivity.this, response.code(), response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<BasicResponse> call, @NonNull Throwable t) {
                if (loader != null) {
                    if (loader.isShowing())
                        loader.dismiss();
                }
                UtilMethods.INSTANCE.apiFailureError(PostActivity.this, t);

            }
        });
    }


    private RequestBody createPartFromString(String description) {
        return RequestBody.create(description, MediaType.parse("multipart/form-data"));
    }

    private void getPackageSetting() {
        try {
            loader.show();
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicObjectResponse<PackageResult>> call = git.getUserPackageSetting("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<BasicObjectResponse<PackageResult>>() {
                @Override
                public void onResponse(@NonNull Call<BasicObjectResponse<PackageResult>> call, @NonNull Response<BasicObjectResponse<PackageResult>> response) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    try {
                        BasicObjectResponse<PackageResult> packageResponse = response.body();
                        if (packageResponse != null) {
                            if (packageResponse.getStatusCode() == 1) {
                                if (packageResponse.getResult() != null) {
                                    packageSetting = packageResponse.getResult();
                                    if (packageSetting.isDailyPostLimitExceed()) {
                                        textInputEt.setText("");
                                        textInputEt.setFocusable(false);
                                        textInputEt.setLongClickable(false);
                                        dailyPostLimit.setText(Html.fromHtml(getString(R.string.package_limit_exceed, packageSetting.getDailyPost(), packageSetting.getDailyPostAllowed()), Html.FROM_HTML_MODE_LEGACY));
                                        textInputEt.setOnClickListener(view -> {
                                            CustomAlertDialog customAlertDialog = new CustomAlertDialog(PostActivity.this, true);
                                            customAlertDialog.Warning("Limit exceed", "Your daily limit has been exceeded, upgrade your package to increase daily limit", "Upgrade", PostActivity.this);
                                        });

                                    } else {
                                        if (packageSetting.isTextCanPost()) {
                                            textInputEt.setFocusable(true);
                                            textInputEt.setLongClickable(true);
                                        } else {
                                            textInputEt.setText("");
                                            textInputEt.setFocusable(false);
                                            textInputEt.setLongClickable(false);
                                            textInputEt.setOnClickListener(view -> {
                                                CustomAlertDialog customAlertDialog = new CustomAlertDialog(PostActivity.this, true);
                                                customAlertDialog.Warning("Upgrade Package", "You can't post text, upgrade your package to post text", "Upgrade", PostActivity.this);
                                            });
                                        }
                                        dailyPostLimit.setText(Html.fromHtml(getString(R.string.package_limit, packageSetting.getDailyPost(), packageSetting.getDailyPostAllowed()), Html.FROM_HTML_MODE_LEGACY));

                                    }
                                }
                            } else {
                                UtilMethods.INSTANCE.Error(PostActivity.this, packageResponse.getResponseText());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilMethods.INSTANCE.Error(PostActivity.this, e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BasicObjectResponse<PackageResult>> call, @NonNull Throwable t) {
                    try {
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }
                        UtilMethods.INSTANCE.apiFailureError(PostActivity.this, t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(PostActivity.this, ise.getMessage());
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
            UtilMethods.INSTANCE.Error(PostActivity.this, e.getMessage());
            if (loader != null) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }
            }
        }
    }

    @Override
    public void onOkClick() {
        upgradePackage.launch(new Intent(this, PackageActivity.class));

    }

    @Override
    public void onCancelClick() {

    }

    ActivityResultLauncher<Intent> upgradePackage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == Activity.RESULT_OK) {
//            getPackageSetting();
        }

    });


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
        if (requestCode == REQUEST_PERMISSIONS_CAMERA) {
            int permissionCheck = PackageManager.PERMISSION_GRANTED;
            for (int permission : grantResults) {
                permissionCheck = permissionCheck + permission;
            }
            if ((grantResults.length > 0) && permissionCheck == PackageManager.PERMISSION_GRANTED) {
                selectCamera();
            } else {
                showWarningSnack(R.string.str_ShowOnPermisstionDenied, "Enable", true);
            }
        } else if (requestCode == REQUEST_PERMISSIONS_GALLERY) {
            int permissionCheck = PackageManager.PERMISSION_GRANTED;
            for (int permission : grantResults) {
                permissionCheck = permissionCheck + permission;
            }
            if ((grantResults.length > 0) && permissionCheck == PackageManager.PERMISSION_GRANTED) {
                selectMedia();
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
                        ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.CAMERA,
                                (android.os.Build.VERSION.SDK_INT >= 33) ? Manifest.permission.READ_MEDIA_IMAGES :
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CAMERA);
                    }

                });

        mSnackBar.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        TextView mainTextView = (TextView) (mSnackBar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
        mainTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(com.intuit.ssp.R.dimen._12ssp));
        mainTextView.setMaxLines(4);
        mSnackBar.show();

    }
}