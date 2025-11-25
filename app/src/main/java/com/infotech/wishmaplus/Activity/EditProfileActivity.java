package com.infotech.wishmaplus.Activity;

import static android.widget.Toast.LENGTH_SHORT;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.infotech.wishmaplus.Api.Object.BankResult;
import com.infotech.wishmaplus.Api.Object.CityResult;
import com.infotech.wishmaplus.Api.Object.StateResult;
import com.infotech.wishmaplus.Api.Request.UpdateUserRequest;
import com.infotech.wishmaplus.Api.Response.BasicListResponse;
import com.infotech.wishmaplus.Api.Response.SignUpResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.FileUtils;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.wishmaplus.image.picker.ImagePicker;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etGender, etLastName, etBio, etAddress, etState, etCity, etBank, etBranch, etIfsc, etAccountNo, etAccountName;

    private ImageView profile_picture;
    private ImageView cover_photo;
    UserDetailResponse userDetailResponse;
    private RequestOptions requestOptionsUserImage;
    private RequestOptions requestOptionsCoverImage;
    private PreferencesManager tokenManager;
    private int selectedGender;
    private int selectedStateId;
    private int selectedBankId= 0;
    private int selectedCityId;
    private CustomLoader loader;
    private ArrayList<StateResult> stateList = new ArrayList<>();
    private ArrayList<CityResult> cityList = new ArrayList<>();
    private ArrayList<BankResult> bankList = new ArrayList<>();

    private ImagePicker imagePicker;
    private final int REQUEST_PERMISSIONS_IMAGE = 7676;
    private Snackbar mSnackBar;
    int isCoverPhoto=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editProfileV), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userDetailResponse = getIntent().getParcelableExtra("UserDetail");
        tokenManager = new PreferencesManager(this, 1);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        if (userDetailResponse == null) {
            userDetailResponse = UtilMethods.INSTANCE.getUserDetailResponse(tokenManager);
        }
        if (requestOptionsUserImage == null) {
            requestOptionsUserImage = UtilMethods.INSTANCE.getRequestOption_With_UserIcon();
        }
        if (requestOptionsCoverImage == null) {
            requestOptionsCoverImage = UtilMethods.INSTANCE.getRequestOption_With_CoverImage();
        }
        findViews();
        init();
    }

    private void findViews() {
        etGender = findViewById(R.id.et_gender);
        etName = findViewById(R.id.et_name);
        etLastName = findViewById(R.id.et_LastName);
        etAddress = findViewById(R.id.et_address);
        etState = findViewById(R.id.et_state);
        etCity = findViewById(R.id.et_city);
        etBio = findViewById(R.id.et_bio);
        etBank = findViewById(R.id.et_bank);
        etBranch = findViewById(R.id.et_branch);
        etIfsc = findViewById(R.id.et_ifsc);
        etAccountNo = findViewById(R.id.et_account_number);
        etAccountName = findViewById(R.id.et_account_name);
        profile_picture = findViewById(R.id.profile_picture);
        cover_photo = findViewById(R.id.cover_photo);

        findViewById(R.id.edit_profile_icon).setOnClickListener(view -> {
            isCoverPhoto = 0;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSIONS_IMAGE);
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_IMAGE);
            }else {
                imagePicker.choosePictureWithoutPermission(true, true);
            }
           /* Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            profileImageResultLauncher.launch(intent);*/
        });

        findViewById(R.id.edit_cover_icon).setOnClickListener(view -> {
            isCoverPhoto = 1;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSIONS_IMAGE);
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_IMAGE);
            }else {
                imagePicker.choosePictureWithoutPermission(true, true);
            }
            /*Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            coverImageResultLauncher.launch(intent);*/
        });
        ArrayList<String> genderList = new ArrayList<String>(Arrays.asList("Male", "Female", "Prefer Not to Say"));
        etGender.setOnClickListener(view -> UtilMethods.INSTANCE.openListBottomSheetDialog(this, "Select Gender", genderList, value -> {
            etGender.setText(value);
            selectedGender = genderList.indexOf(value) + 1;
        }));

        etState.setOnClickListener(view -> UtilMethods.INSTANCE.openListBottomSheetDialog(this, "Select State", stateList, value -> {
            etState.setText(value.getStateName());
            selectedStateId = value.getStateId();
            getCity(this);
        }));


        etCity.setOnClickListener(view -> UtilMethods.INSTANCE.openListBottomSheetDialog(this, "Select City", cityList, value -> {
            etCity.setText(value.getCityName());
            selectedCityId = value.getCityId();
        }));
        findViewById(R.id.back_button).setOnClickListener(view -> finish());

        etBank.setOnClickListener(view -> UtilMethods.INSTANCE.openListBottomSheetDialog(this, "Select Bank", bankList, value -> {
            etBank.setText(value.getBranchName());
            selectedBankId = value.getBankId();
            etIfsc.setText(value.getIfsc());
        }));


       /* etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!validateFirstName()) {
                    return;
                }
            }
        });
        etLastName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!validateLastName()) {
                    return;
                }
            }
        });*/


        findViewById(R.id.bt_save).setOnClickListener(v -> {
            if (!validateFirstName()) {
                return;
            }
            if (!validateLastName()) {
                return;
            }
            if(selectedBankId!=0){
                if(etBranch.getText().toString().trim().length()<3){
                    Toast.makeText(this,"Enter Valid Branch name",LENGTH_SHORT).show();
                    return;
                }else  if(etIfsc.getText().toString().trim().length()<7){
                    Toast.makeText(this,"Enter Valid IFSC Code",LENGTH_SHORT).show();
                    return;
                } else if(etAccountNo.getText().toString().trim().length()<8){
                    Toast.makeText(this,"Enter Valid Account Number",LENGTH_SHORT).show();
                    return;
                }else if(etAccountName.getText().toString().trim().length()<3){
                    Toast.makeText(this,"Enter Valid Account Holder name",LENGTH_SHORT).show();
                    return;
                }

            }
           /* if (!validatePhoneNum()) {
                return;
            }
            if (!validateEmailId()) {
                return;
            }
            if (!validatePassword()) {
                return;
            }
            if (!validateConfirmP()) {
                return;
            }*/
            save(EditProfileActivity.this);
        });
    }

    void init() {
        Glide.with(this)
                .load(userDetailResponse.getProfilePictureUrl())
                .apply(requestOptionsUserImage)
                .into(profile_picture);

        Glide.with(this)
                .load(userDetailResponse.getCoverPictureUrl())
                .apply(requestOptionsCoverImage)
                .into(cover_photo);

        etName.setText(userDetailResponse.getFisrtName());
        etLastName.setText(userDetailResponse.getLastName());
        etBio.setText(userDetailResponse.getBio());
        etGender.setText(userDetailResponse.getGender());
        etAddress.setText(userDetailResponse.getAddress());
        etState.setText(userDetailResponse.getStateName());
        etCity.setText(userDetailResponse.getCityName());
        selectedGender = userDetailResponse.getGenderId();
        selectedCityId = userDetailResponse.getCityId();
        selectedStateId = userDetailResponse.getStateId();
        selectedBankId = userDetailResponse.getBankId();
        etAccountName.setText(userDetailResponse.getAccountHolder());
        etAccountNo.setText(userDetailResponse.getAccountNumber());
        etBranch.setText(userDetailResponse.getBranchName());
        etIfsc.setText(userDetailResponse.getIfsc());
        getState(this);
        getBank(this);
        if(selectedBankId> 0){
            getBank(this);
        }
        if (selectedStateId > 0) {
            getCity(this);
        }

        imagePicker = new ImagePicker(this, null, imageUri -> {
            if (imageUri != null) {
                String filePath = FileUtils.getPath(this, imageUri);
                if (filePath != null) {
                    if (isCoverPhoto == 1) {
                        userDetailResponse.setCoverPictureUrl(filePath);
                        Glide.with(this)
                                .load(filePath)
                                .apply(requestOptionsCoverImage)
                                .into(cover_photo);
                        updateProfile(new File(filePath));

                    } else {
                        userDetailResponse.setProfilePictureUrl(filePath);
                        Glide.with(this)
                                .load(filePath)
                                .apply(requestOptionsUserImage)
                                .into(profile_picture);
                        updateProfile(new File(filePath));

                    }
                }

            }
        }).setWithImageCrop();
    }

    private boolean validateFirstName() {
        String firstName = etName.getText().toString().trim();
        if (firstName.isEmpty()) {
            etName.setError(getString(R.string.err_empty_field));
            etName.requestFocus();
            return false;
        }
        if (!firstName.matches("^[A-Za-z]{2,30}$")) {
            etName.setError(getString(R.string.err_invalid_first_name));
            etName.requestFocus();
            return false;
        }
        etName.setError(null);
        return true;
    }


    private boolean validateLastName() {
        String lastName = etLastName.getText().toString().trim();
        if (lastName.isEmpty()) {
            etLastName.setError(getString(R.string.err_empty_field));
            etLastName.requestFocus();
            return false;
        }
        if (!lastName.matches("^[A-Za-z]{2,30}$")) {
            etLastName.setError(getString(R.string.err_invalid_last_name));
            etLastName.requestFocus();
            return false;
        }
        etLastName.setError(null);
        return true;
    }


    private void getState(Activity context) {
        try {
            loader.show();
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicListResponse<StateResult>> call = git.getState("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<BasicListResponse<StateResult>> call, @NonNull Response<BasicListResponse<StateResult>> response) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    try {
                        BasicListResponse<StateResult> stateResponse = response.body();
                        if (stateResponse != null) {
                            if (stateResponse.getStatusCode() == 1) {
                                if (stateResponse.getResult() != null && stateResponse.getResult().size() > 0) {
                                    stateList = stateResponse.getResult();
                                }
                            } else {
                                UtilMethods.INSTANCE.Error(context, stateResponse.getResponseText());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilMethods.INSTANCE.Error(context, e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BasicListResponse<StateResult>> call, @NonNull Throwable t) {
                    try {
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }
                        UtilMethods.INSTANCE.apiFailureError(context, t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(context, ise.getMessage());
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
        }
    }

    private void getCity(Activity context) {
        try {
            loader.show();
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicListResponse<CityResult>> call = git.getCity("Bearer " + tokenManager.getAccessToken(), selectedStateId);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<BasicListResponse<CityResult>> call, @NonNull Response<BasicListResponse<CityResult>> response) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    try {
                        BasicListResponse<CityResult> cityResponse = response.body();
                        if (cityResponse != null) {
                            if (cityResponse.getStatusCode() == 1) {
                                if (cityResponse.getResult() != null && cityResponse.getResult().size() > 0) {
                                    cityList = cityResponse.getResult();
                                }
                            } else {
                                UtilMethods.INSTANCE.Error(context, cityResponse.getResponseText());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilMethods.INSTANCE.Error(context, e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BasicListResponse<CityResult>> call, @NonNull Throwable t) {
                    try {
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }
                        UtilMethods.INSTANCE.apiFailureError(context, t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(context, ise.getMessage());
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
        }
    }


    private void getBank(Activity context) {
        try {
            loader.show();
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicListResponse<BankResult>> call = git.getBank("Bearer " + tokenManager.getAccessToken(),selectedBankId);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<BasicListResponse<BankResult>> call, @NonNull Response<BasicListResponse<BankResult>> response) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    try {
                        BasicListResponse<BankResult> bankResponse = response.body();
                        if (bankResponse != null) {
                            if (bankResponse.getStatusCode() == 1) {
                                if (bankResponse.getResult() != null && bankResponse.getResult().size() > 0) {
                                    bankList = bankResponse.getResult();
                                    if(selectedBankId!=0)
                                        etBank.setText(
                                                bankList.stream()
                                                        .filter(it -> it.getBankId() == selectedBankId)
                                                        .findFirst()
                                                        .map(BankResult::getBranchName)
                                                        .orElse(getString(R.string.select_bank))
                                        );
                                }
                            } else {
                                UtilMethods.INSTANCE.Error(context, bankResponse.getResponseText());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilMethods.INSTANCE.Error(context, e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BasicListResponse<BankResult>> call, @NonNull Throwable t) {
                    try {
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }
                        UtilMethods.INSTANCE.apiFailureError(context, t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(context, ise.getMessage());
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
        }
    }

    private void save(Activity context) {
        try {
            loader.show();
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<SignUpResponse> call = git.updateUser("Bearer " + tokenManager.getAccessToken(),
                    new UpdateUserRequest(etName.getText().toString().trim(), etLastName.getText().toString().trim(), selectedCityId, etCity.getText().toString().trim(),
                            selectedStateId, etState.getText().toString().trim(),
                            etAddress.getText().toString().trim(), etBio.getText().toString().trim(), selectedGender,selectedBankId,etBranch.getText().toString().trim()
                            ,etIfsc.getText().toString().trim(),etAccountNo.getText().toString().trim(),etAccountName.getText().toString().trim()));
            call.enqueue(new Callback<SignUpResponse>() {
                @Override
                public void onResponse(@NonNull Call<SignUpResponse> call, @NonNull Response<SignUpResponse> response) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    try {
                        SignUpResponse signUpResponse = response.body();
                        if (signUpResponse != null) {
                            if (signUpResponse.getStatusCode() == 1) {
                                setResult(RESULT_OK);
                                UtilMethods.INSTANCE.SuccessfulWithDismiss(context, signUpResponse.getResponseText());
                            } else {
                                UtilMethods.INSTANCE.Error(context, signUpResponse.getResponseText());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilMethods.INSTANCE.Error(context, e.getMessage());
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
                        UtilMethods.INSTANCE.apiFailureError(context, t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(context, ise.getMessage());
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
        }
    }


   /* ActivityResultLauncher<Intent> profileImageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == Activity.RESULT_OK) {
            if (result.getData() != null) {
                Uri imageUri = result.getData().getData();


                if (imageUri != null) {
                    String filePath = FileUtils.getPath(this, imageUri);
                    if (filePath != null) {
                        File file = new File(filePath);
                        userDetailResponse.setProfilePictureUrl(file.getPath());
                        Glide.with(this)
                                .load(filePath)
                                .apply(requestOptionsUserImage)
                                .into(profile_picture);
                        updateProfile(file, 0);
                    }
                }
            }
        }

    });

    ActivityResultLauncher<Intent> coverImageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == Activity.RESULT_OK) {
            if (result.getData() != null) {
                Uri imageUri = result.getData().getData();
                if (imageUri != null) {
                    String filePath = FileUtils.getPath(this, imageUri);
                    if (filePath != null) {
                        File file = new File(filePath);
                        userDetailResponse.setCoverPictureUrl(file.getPath());
                        Glide.with(this)
                                .load(filePath)
                                .apply(requestOptionsCoverImage)
                                .into(cover_photo);
                        updateProfile(file, 1);
                    }
                }
            }
        }

    });
*/

    private void updateProfile(File file) {
        try {
            loader.show();
            MultipartBody.Part extraParam = null;

            if (file != null) {
                String mimeType = URLConnection.guessContentTypeFromName(file.getName());
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                RequestBody requestFile = RequestBody.create(file, MediaType.parse(mimeType));
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
                                setResult(RESULT_OK);
                                UtilMethods.INSTANCE.SuccessfulWithDismiss(EditProfileActivity.this, response.body().getResponseText());
                            } else {
                                Toast.makeText(EditProfileActivity.this, "Failed to Update Profile Picture: " + response.body().getResponseText(), LENGTH_SHORT).show();
                            }

                        } else {

                            Toast.makeText(EditProfileActivity.this, "Failed to Update Profile Picture: Something error, please try after some time", LENGTH_SHORT).show();


                        }
                    } else {
                        UtilMethods.INSTANCE.apiErrorHandle(EditProfileActivity.this, response.code(), response.message());
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
                        UtilMethods.INSTANCE.apiFailureError(EditProfileActivity.this, t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(EditProfileActivity.this, ise.getMessage());
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
            Toast.makeText(this, "Error: " + e.getMessage(), LENGTH_SHORT).show();
        }
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

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)){
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSIONS_IMAGE);
                        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_IMAGE);
                        }else {
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