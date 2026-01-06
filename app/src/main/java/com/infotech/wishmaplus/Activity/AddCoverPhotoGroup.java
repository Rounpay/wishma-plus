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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.infotech.wishmaplus.Adapter.CoverAdapter;
import com.infotech.wishmaplus.Adapter.UserListAdapter;
import com.infotech.wishmaplus.Api.Response.GetUserListResponse;
import com.infotech.wishmaplus.Api.Response.UploadGroupCoverResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.wishmaplus.image.picker.ImagePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddCoverPhotoGroup extends AppCompatActivity {
    private ArrayList<Integer> coverList;
    private CoverAdapter coverAdapter;
    private ImagePicker imagePicker;
    private Snackbar mSnackBar;
    private static final int REQUEST_PERMISSIONS_IMAGE = 7676;
    private RecyclerView rvCovers;
    private int isCoverPhoto = 0;
    private File profileImageFile = null;
    private File coverImageFile = null;
    private ImageView cover_photo, profile_picture;
    private String groupId;
    private CustomLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_cover_photo_group);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intentParam = getIntent();
        if (intentParam != null && intentParam.hasExtra("groupId")) {
            groupId = intentParam.getStringExtra("groupId");
        }
        findViewById(R.id.back_button).setOnClickListener(view -> {
            finish();
        });
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        findViewById(R.id.btnEdit).setOnClickListener(view -> {
            selectCoverImage();
        });
        findViewById(R.id.search_button).setOnClickListener(view -> {
            Intent intent = new Intent(AddCoverPhotoGroup.this, ProfileActivity.class);
            intent.putExtra("groupId", groupId);
            startActivityForResult(
                    intent,
                    103
            );
//            startActivity(intent);
        });
        rvCovers = findViewById(R.id.rvCovers);
        setupRecyclerView();
        cover_photo = findViewById(R.id.ivCover);
        setupImagePicker();

    }
    public void updateGroupProfilePicture(MultipartBody.Part model){
        loader.show();
        UtilMethods.INSTANCE.updateGroupProfilePicture(groupId,true,model,new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                UploadGroupCoverResponse uploadGroupCoverResponse = (UploadGroupCoverResponse) object;
                if(uploadGroupCoverResponse.getStatusCode()==1){
                    Toast.makeText(AddCoverPhotoGroup.this, uploadGroupCoverResponse.getResponseText(), Toast.LENGTH_SHORT).show();

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
    public void selectCoverImage() {
        isCoverPhoto = 1;
        checkPermissionAndOpenPicker();
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


            MultipartBody.Part profilePart = null;
            // Update UI & assign file
            if (isCoverPhoto == 1) {
                coverImageFile = selectedFile;
                if (coverImageFile != null) {
                    RequestBody reqProfile = RequestBody.create(MediaType.parse("image/*"), coverImageFile);
                    profilePart = MultipartBody.Part.createFormData(
                            "model",
                            coverImageFile.getName(),
                            reqProfile
                    );
                    updateGroupProfilePicture(profilePart);
                }
                Glide.with(this).load(selectedFile).into(cover_photo);
            } else {
                profileImageFile = selectedFile;
                Glide.with(this).load(selectedFile).into(profile_picture);
            }

        }).setWithImageCrop(); // cropping enabled
    }

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
    private void setupRecyclerView() {

        coverList = new ArrayList<>();

        // Dummy drawable images
        coverList.add(R.drawable.dog_cover);
        coverList.add(R.drawable.dog_cover);
        coverList.add(R.drawable.dog_cover);
        coverList.add(R.drawable.dog_cover);
        coverList.add(R.drawable.dog_cover);

        rvCovers.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        coverAdapter = new CoverAdapter(coverList, imageRes -> {
//            ivCover.setImageResource(imageRes);
        });

        rvCovers.setAdapter(coverAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 103 && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
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

}