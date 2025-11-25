package com.infotech.wishmaplus.Activity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

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

import com.google.android.material.snackbar.Snackbar;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.wishmaplus.image.picker.ImagePicker;

public class PersonalProfileActivity extends AppCompatActivity {

    AppCompatImageView cameraIcon, profileImage;
    private AutoCompleteTextView profileName;
    private AppCompatTextView continueBtn, seeAll;

    private ImagePicker imagePicker;
    private String imagePath;
    private Snackbar mSnackBar;
    private int REQUEST_PERMISSIONS_CAMERA = 9090;
    private int REQUEST_PERMISSIONS_GALLERY = 7654;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView optionOneTitle = findViewById(R.id.optionOneTitle);
        profileName = findViewById(R.id.profileName);
        cameraIcon = findViewById(R.id.cameraIcon);
        profileImage = findViewById(R.id.profileImage);
        findViewById(R.id.back_button).setOnClickListener(view -> {
            finish();
        });
        cameraIcon.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSIONS_CAMERA);
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CAMERA);
            } else {
                imagePicker.choosePictureWithoutPermission(true, true);
            }

        });
        imagePicker = new ImagePicker(this, null, imageUri -> {
            if (imageUri != null) {
                imagePath = imageUri.getPath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    profileImage.setImageURI(imageUri);
                    profileImage.setVisibility(View.VISIBLE);
                }
            }
        }).setWithImageCrop();

        seeAll = findViewById(R.id.seeAll);
        continueBtn = findViewById(R.id.continueBtn);
        continueBtn.setEnabled(false);
        continueBtn.setAlpha(0.5f);
        profileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!validateName()) {
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        continueBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreatePersonalProfileActivity.class);
            startActivity(intent);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        });
        SpannableString ss = getPersonalDetail();
        optionOneTitle.setText(ss);
        optionOneTitle.setMovementMethod(LinkMovementMethod.getInstance());
        optionOneTitle.setHighlightColor(Color.TRANSPARENT);
    }

    private boolean validateName() {
        String name = profileName.getText().toString().trim();
        if (name.isEmpty()) {
            profileName.setError("Name cannot be empty");
            continueBtn.setEnabled(false);
            continueBtn.setAlpha(0.5f);
            return false;
        }
        continueBtn.setEnabled(true);
        continueBtn.setAlpha(1f);
        return true;
    }


    @NonNull
    private SpannableString getPersonalDetail() {
        String fullText = "Get creative by adding a profile picture and name. You can edit this later. Learn more";
        SpannableString ss = new SpannableString(fullText);
        int start = fullText.indexOf("Learn more");
        int end = start + "Learn more".length();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                UtilMethods.INSTANCE.personalProfileBottomSheet(PersonalProfileActivity.this);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setFakeBoldText(true); // Bold
                ds.setColor(Color.parseColor("#0A66C2"));
                ds.setUnderlineText(false); // No underline
            }
        };

        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

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
                imagePicker.choosePictureWithoutPermission(true, false);
            } else {
                showWarningSnack(R.string.str_ShowOnPermisstionDenied, "Enable", true);
            }
        } else if (requestCode == REQUEST_PERMISSIONS_GALLERY) {
            int permissionCheck = PackageManager.PERMISSION_GRANTED;
            for (int permission : grantResults) {
                permissionCheck = permissionCheck + permission;
            }
            if ((grantResults.length > 0) && permissionCheck == PackageManager.PERMISSION_GRANTED) {
                imagePicker.choosePictureWithoutPermission(false, true);
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
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                    } else {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSIONS_CAMERA);
                        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_GALLERY);
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
}