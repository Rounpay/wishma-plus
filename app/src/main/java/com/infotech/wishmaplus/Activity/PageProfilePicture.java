package com.infotech.wishmaplus.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.wishmaplus.image.picker.ImagePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PageProfilePicture extends AppCompatActivity {

    private String bio, website, email, phone, address, selectedIDs, selectedNames;
    private ImagePicker imagePicker;
    private Snackbar mSnackBar;

    private static final int REQUEST_PERMISSIONS_IMAGE = 7676;

    private File profileImageFile = null;
    private File coverImageFile = null;

    private int isCoverPhoto = 0;

    private AppCompatImageView cover_photo, profile_picture;
    private AppCompatTextView tvName;
    private CustomLoader loader;
    private PreferencesManager tokenManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_profile_picture);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        receiveIntentData();
        setupImagePicker();
        setupClickListeners();
    }

    private void initViews() {
        tokenManager = new PreferencesManager(this, 1);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        cover_photo = findViewById(R.id.cover_photo);
        tvName = findViewById(R.id.tvName);
        profile_picture = findViewById(R.id.profile_picture);
        AppCompatTextView btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(v -> uploadImagesToApi());

    }

    private void receiveIntentData() {
        Intent intent = getIntent();
        bio = getSafeString(intent.getStringExtra("bio"));
        website = getSafeString(intent.getStringExtra("website"));
        email = getSafeString(intent.getStringExtra("email"));
        phone = getSafeString(intent.getStringExtra("phone"));
        address = getSafeString(intent.getStringExtra("address"));
        selectedIDs = getSafeString(intent.getStringExtra("selectedIDs"));
        selectedNames = getSafeString(intent.getStringExtra("pageName"));
        tvName.setText(selectedNames);
    }

    private String getSafeString(String data) {
        return data != null ? data : "";
    }

    private void setupClickListeners() {
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.edit_cover_icon).setOnClickListener(v -> selectCoverImage());
        findViewById(R.id.edit_profile_icon).setOnClickListener(v -> selectProfileImage());
    }

    private void setupImagePicker() {

        imagePicker = new ImagePicker(this, null, imageUri -> {
            if (imageUri == null) return;

            // Convert Uri → Permanent File
            File selectedFile = makeFileFromUri(imageUri);

            if (selectedFile == null || !selectedFile.exists()) {
                Toast.makeText(this, "Image loading failed", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update UI & assign file
            if (isCoverPhoto == 1) {
                coverImageFile = selectedFile;
                Glide.with(this).load(selectedFile).into(cover_photo);
            } else {
                profileImageFile = selectedFile;
                Glide.with(this).load(selectedFile).into(profile_picture);
            }

        }).setWithImageCrop(); // cropping enabled
    }
    /* -----------------------------------------------------------
     *        Convert Uri → Permanent File (NO ENOENT EVER)
     * ----------------------------------------------------------- */
    private File makeFileFromUri(Uri uri) {
        try {
            File directory = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "picked"
            );
            if (!directory.exists()) directory.mkdirs();

            File outFile = new File(directory, "IMG_" + System.currentTimeMillis() + ".jpg");
            // Try InputStream first
            try (InputStream in = getContentResolver().openInputStream(uri)) {
                if (in != null) {
                    try (OutputStream out = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[4096];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                    }
                    return outFile;
                }
            }
            // Fallback for Camera URIs
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r");
            if (pfd != null) {
                try (FileInputStream fis = new FileInputStream(pfd.getFileDescriptor());
                     FileOutputStream fos = new FileOutputStream(outFile)) {

                    byte[] buffer = new byte[4096];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                }
                pfd.close();
                return outFile;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



    public void selectProfileImage() {
        isCoverPhoto = 0;
        checkPermissionAndOpenPicker();
    }

    public void selectCoverImage() {
        isCoverPhoto = 1;
        checkPermissionAndOpenPicker();
    }

    private void checkPermissionAndOpenPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (isDenied(Manifest.permission.CAMERA) ||
                    isDenied(Manifest.permission.READ_MEDIA_IMAGES)) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_PERMISSIONS_IMAGE
                );
                return;
            }
        } else {
            if (isDenied(Manifest.permission.CAMERA) ||
                    isDenied(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    isDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        REQUEST_PERMISSIONS_IMAGE
                );
                return;
            }
        }

        imagePicker.choosePictureWithoutPermission(true, true);
    }

    private boolean isDenied(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_PERMISSIONS_IMAGE)
         if (imagePicker != null) {
             imagePicker.handleActivityResult(resultCode, requestCode, data);
           }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS_IMAGE) {
            boolean granted = true;
            for (int result : grantResults)
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            if (granted) {
                imagePicker.choosePictureWithoutPermission(true, true);
            } else {
                showWarningSnack(R.string.str_ShowOnPermisstionDenied);
            }
        }
    }

    private void showWarningSnack(int msg) {
        if (mSnackBar != null && mSnackBar.isShown()) return;

        mSnackBar = Snackbar.make(findViewById(android.R.id.content), msg,
                Snackbar.LENGTH_INDEFINITE).setAction("Enable", v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        });

        mSnackBar.show();
    }

    /* --------------------- API MULTIPART --------------------- */

    public void uploadImagesToApi() {
        try {
            // ------------------ LOADER SHOW ------------------
            if (loader != null && !loader.isShowing()) {
                loader.show();
            }

            EndPointInterface apiService = ApiClient.getClient().create(EndPointInterface.class);

            // ----------- Text Fields Convert to RequestBody -----------
            RequestBody rbPageName = createText(selectedNames);
            RequestBody rbCategoryId = createText(selectedIDs);
            RequestBody rbBio = createText(bio);
            RequestBody rbWebsite = createText(website);
            RequestBody rbEmail = createText(email);
            RequestBody rbPhone = createText(phone);
            RequestBody rbAddress = createText(address);

            // ------------------ FILE CHECK ------------------
            if (profileImageFile == null && coverImageFile == null) {
                hideLoader();
                Toast.makeText(this, "Please select images", Toast.LENGTH_SHORT).show();
                return;
            }

            // ------------------ MULTIPART FILES ------------------
            MultipartBody.Part profilePart = null;
            MultipartBody.Part coverPart = null;

            try {
                if (profileImageFile != null) {
                    RequestBody reqProfile = RequestBody.create(MediaType.parse("image/*"), profileImageFile);
                    profilePart = MultipartBody.Part.createFormData(
                            "ProfileImageFile",
                            profileImageFile.getName(),
                            reqProfile
                    );
                }

                if (coverImageFile != null) {
                    RequestBody reqCover = RequestBody.create(MediaType.parse("image/*"), coverImageFile);
                    coverPart = MultipartBody.Part.createFormData(
                            "CoverImageFile",
                            coverImageFile.getName(),
                            reqCover
                    );
                }
            } catch (Exception fileEx) {
                hideLoader();
                fileEx.printStackTrace();
                Toast.makeText(this, "Image processing failed", Toast.LENGTH_SHORT).show();
                return;
            }

            // ------------------ API CALL ------------------
            Call<BasicResponse> call = apiService.createPage(
                    "Bearer " + tokenManager.getAccessToken(),
                    rbPageName,
                    rbCategoryId,
                    rbBio,
                    rbWebsite,
                    rbEmail,
                    rbPhone,
                    rbAddress,
                    profilePart,
                    coverPart
            );

            call.enqueue(new Callback<BasicResponse>() {
                @Override
                public void onResponse(@NonNull Call<BasicResponse> call, @NonNull Response<BasicResponse> response) {
                    hideLoader();
                    if (response.isSuccessful()) {
                        Toast.makeText(PageProfilePicture.this, "Page Created Successfully", Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(PageProfilePicture.this,MainActivity.class);
                       startActivity(intent);
                       finish();
                    } else {

                        Toast.makeText(PageProfilePicture.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BasicResponse> call, @NonNull Throwable t) {
                    hideLoader();
                    Log.e("fdadafafa", "onFailureImage: "+t.getMessage() );
                    Log.d("imageUploadError", "onResponse: "+t.getMessage());
                    Toast.makeText(PageProfilePicture.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            hideLoader();
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideLoader() {
        try {
            if (loader != null && loader.isShowing()) {
                loader.dismiss();
            }
        } catch (Exception ignored) {
        }
    }

    private RequestBody createText(String data) {
        return RequestBody.create(MediaType.parse("text/plain"), data == null ? "" : data);
    }
}
