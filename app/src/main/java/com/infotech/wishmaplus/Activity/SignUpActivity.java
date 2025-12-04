package com.infotech.wishmaplus.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.Api.Request.SignUpRequest;
import com.infotech.wishmaplus.Api.Response.SignUpResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.ApplicationConstant;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.InstallReferral;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private EditText etReferral, etName, etMobile, etLastName, etEmail, etPassword, etConfirmP;
    private CustomLoader loader;
    private PreferencesManager tokenManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signupS), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tokenManager = new PreferencesManager(this, 3);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        findViews();


    }

    private void findViews() {
        etReferral = findViewById(R.id.et_referral);
        etName = findViewById(R.id.et_name);
        etLastName = findViewById(R.id.et_LastName);
        etMobile = findViewById(R.id.et_mobile);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmP = findViewById(R.id.et_confirmP);
        if (!tokenManager.getBooleanNonRemoval(ApplicationConstant.INSTANCE.isUserReferralSetPref)) {
            new InstallReferral(this).InstllReferralData(tokenManager);
        }
        if (!tokenManager.getStringNonRemoval(ApplicationConstant.INSTANCE.UserReferralPref).isEmpty()) {
            etReferral.setText(tokenManager.getStringNonRemoval(ApplicationConstant.INSTANCE.UserReferralPref));
        }
        /*etName.addTextChangedListener(new TextWatcher() {

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
        });
        etMobile.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!validatePhoneNum()) {
                    return;
                }
            }
        });
        etEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!validateEmailId()) {
                    return;
                }
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!validatePassword()) {
                    return;
                }
            }
        });
        etConfirmP.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!validateConfirmP()) {
                    return;
                }
            }
        });*/
        findViewById(R.id.tv_login).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);*/
                finish();
            }
        });
        findViewById(R.id.bt_login).setOnClickListener(v -> {
            if (!validateFirstName()) {
                return;
            }
            if (!validateLastName()) {
                return;
            }
            if (!validatePhoneNum()) {
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
            }
            signUp(SignUpActivity.this);
        });
    }

    private boolean validateFirstName() {
        String firstName = etName.getText().toString().trim();
        if (firstName.isEmpty()) {
            etName.setError(getString(R.string.err_empty_field));
            etName.requestFocus();
            return false;
        } else if (!firstName.matches("^[A-Za-z]{2,30}$")) {
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
        } else if (!lastName.matches("^[A-Za-z]{2,30}$")) {
            etLastName.setError(getString(R.string.err_invalid_last_name));
            etLastName.requestFocus();
            return false;
        }
        etLastName.setError(null);
        return true;
    }

    private boolean validatePhoneNum() {
        String mobileNumber = etMobile.getText().toString().trim();
        if (mobileNumber.isEmpty()) {
            etMobile.setError(getString(R.string.err_empty_field));
            etMobile.requestFocus();
            return false;
        } else if (mobileNumber.length() != 10 || !mobileNumber.matches("[6-9][0-9]{9}")) {
            etMobile.setError(getString(R.string.err_invalid_mobile));
            etMobile.requestFocus();
            return false;
        }
        etMobile.setError(null);
        return true;
    }

    private boolean validateEmailId() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            etEmail.setError(getString(R.string.err_empty_field));
            etEmail.requestFocus();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.err_invalid_email));
            etEmail.requestFocus();
            return false;
        }
        etEmail.setError(null);
        return true;
    }

    private boolean validatePassword() {
        String password = etPassword.getText().toString().trim();
        if (password.isEmpty()) {
            etPassword.setError(getString(R.string.err_msg_pass));
            etPassword.requestFocus();
            return false;
        } else if (!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$")) {
            etPassword.setError(getString(R.string.err_invalid_password));
            etPassword.requestFocus();
            return false;
        }

        // If valid, clear the error
        etPassword.setError(null);
        return true;
    }

    private boolean validateConfirmP() {
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmP.getText().toString().trim();
        if (confirmPassword.isEmpty()) {
            etPassword.setError(getString(R.string.err_empty_field));
            etConfirmP.requestFocus();
            return false;
        } else if (!confirmPassword.equals(password)) {
            etPassword.setError(getString(R.string.err_password_mismatch));
            etConfirmP.requestFocus();
            return false;
        }
        etPassword.setError(null);
        return true;
    }

    private void signUp(Activity context) {
        try {
            loader.show();
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<SignUpResponse> call = git.userSignup(new SignUpRequest(etReferral.getText().toString(), etPassword.getText().toString(), etConfirmP.getText().toString(), etMobile.getText().toString(),
                    etEmail.getText().toString(), etName.getText().toString(), etLastName.getText().toString()));
            call.enqueue(new Callback<SignUpResponse>() {
                @Override
                public void onResponse(@NonNull Call<SignUpResponse> call, @NonNull Response<SignUpResponse> response) {
                    try {
                        if (loader != null && loader.isShowing()) {
                            loader.dismiss();
                        }
                        if (response.isSuccessful()) {
                            SignUpResponse signUpResponse = response.body();
                            if (signUpResponse != null) {
                                if (signUpResponse.getStatusCode() == 1) {
                                    tokenManager.setNonRemoval(ApplicationConstant.INSTANCE.UserReferralPref, "");
                                    UtilMethods.INSTANCE.SuccessfulWithFinsh(context, true, signUpResponse.getResponseText(), 0, "0");
                                } else {
                                    UtilMethods.INSTANCE.Error(context, signUpResponse.getResponseText());
                                }
                            }
                        } else {
                            UtilMethods.INSTANCE.apiErrorHandle(context, response.code(), response.message());
                        }
                    } catch (Exception e) {
                        if (loader != null && loader.isShowing()) {
                            loader.dismiss();
                        }
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SignUpResponse> call, @NonNull Throwable t) {
                    if (loader != null && loader.isShowing()) {
                        loader.dismiss();
                    }
                    UtilMethods.INSTANCE.apiFailureError(context, t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  /*  private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }*/
}