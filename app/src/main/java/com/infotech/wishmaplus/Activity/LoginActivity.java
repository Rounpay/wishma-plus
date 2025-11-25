package com.infotech.wishmaplus.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infotech.wishmaplus.Api.Response.LoginResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.ApplicationConstant;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.InstallReferral;
import com.infotech.wishmaplus.Utils.PinEntryEditTextBox;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.Utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private View ll_fwdPass, ll_login;
    private AutoCompleteTextView userIdEt, passwordEt;
    private EditText fwpMobileEt, fwpPassEt, fwpPassConfEt;
    private TextInputLayout fwpPassTil, fwpPassConfTil;
    private PinEntryEditTextBox fwpOTPEt;
    private PreferencesManager tokenManager;
    CheckBox rememberCheck;
    HashMap<String, String> recentNumberMap = new HashMap<>();
    ArrayAdapter<String> adapter;
    ArrayList<String> recentNumber = new ArrayList<>();
    /* private GoogleSignInClient mGoogleSignInClient;*/
    CustomLoader loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginVI), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tokenManager = new PreferencesManager(getApplicationContext(), 2);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        rememberCheck = (CheckBox) findViewById(R.id.check_pass);
        userIdEt = findViewById(R.id.et_mobile);
        fwpMobileEt = findViewById(R.id.et_fwpMobile);
        fwpPassEt = findViewById(R.id.et_fwpPass);
        fwpPassTil = findViewById(R.id.til_fwpPass);
        fwpPassConfTil = findViewById(R.id.til_fwpPassConf);
        fwpPassConfEt = findViewById(R.id.et_fwpPassConf);
        fwpOTPEt = findViewById(R.id.et_fwpOTP);
        passwordEt = findViewById(R.id.et_pass);
        ll_fwdPass = findViewById(R.id.ll_fwdPass);
        TextView termAndPrivacyTxt = findViewById(R.id.term_and_privacy_txt);
        Utility.INSTANCE.setTerm_Privacy(this, termAndPrivacyTxt,
                32, 49, 53, 67);
        ll_login = findViewById(R.id.ll_login);
        if (!tokenManager.getBooleanNonRemoval(ApplicationConstant.INSTANCE.isUserReferralSetPref)) {
            new InstallReferral(this).InstllReferralData(tokenManager);
        }

      /*  GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestServerAuthCode("789309115643-j7brbm5rttmun7m2jbshl9p2o5gpc0n5.apps.googleusercontent.com")
                .requestIdToken("789309115643-j7brbm5rttmun7m2jbshl9p2o5gpc0n5.apps.googleusercontent.com")
                .requestScopes(new Scope(Scopes.EMAIL), new Scope("https://www.googleapis.com/auth/gmail.readonly"))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);*/


        MaterialButton googleSignInButton = findViewById(R.id.btn_google_sign_in);
        // Google Sign-In button click handler
        googleSignInButton.setOnClickListener(v -> {
            try {
                CredentialManager credentialManager = CredentialManager.create(this);
                String rawNonce = UUID.randomUUID().toString();
                byte[] bytes = rawNonce.getBytes();
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] digest = md.digest(bytes);
                StringBuilder hashedNonce = new StringBuilder();
                for (byte b : digest) {
                    hashedNonce.append(String.format("%02x", b));
                }

                GetGoogleIdOption gio = new GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false) // true - check if the user has any accounts that have previously been used to sign in to the app
                        .setServerClientId(getString(R.string.google_web_client_id))
                        .setAutoSelectEnabled(false) // true- Enable automatic sign-in for returning users
                        .setNonce(String.valueOf(hashedNonce))
                        .build();
                GetCredentialRequest googleSignRequest = new GetCredentialRequest.Builder()
                        .addCredentialOption(gio)
                        .build();

                signInWithGoogle(credentialManager, googleSignRequest);
            } catch (NoSuchAlgorithmException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        });

        RecentNumbers();
       /* userIdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!validateMobile()) {
                    return;
                }
            }
        });
        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!validatePass()) {
                    return;
                }
            }
        });*/
        findViewById(R.id.bt_login).setOnClickListener(v -> {
            if (!validateMobile()) {
                return;
            }
            if (!validatePass()) {
                return;
            }
            if (rememberCheck.isChecked()) {
                recentNumberMap.put(userIdEt.getText().toString(), passwordEt.getText().toString());
                recentNumber.add(userIdEt.getText().toString());
                adapter = new ArrayAdapter(LoginActivity.this, android.R.layout.simple_list_item_1, recentNumber);
                userIdEt.setAdapter(adapter);
                userIdEt.setThreshold(1);

                UtilMethods.INSTANCE.setRecentLogin(LoginActivity.this, new Gson().toJson(recentNumberMap));

            }
            if (UtilMethods.INSTANCE.isNetworkAvialable(LoginActivity.this)) {
                loader.show();
                loader.setCancelable(false);
                loader.setCanceledOnTouchOutside(false);
                secureLogin(LoginActivity.this, "0", 4, "", userIdEt.getText().toString(), passwordEt.getText().toString(), loader);
            } else {
                UtilMethods.INSTANCE.NetworkError(LoginActivity.this, getResources().getString(R.string.err_msg_network_title),
                        getResources().getString(R.string.err_msg_network));
            }
          /*  Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);*/
        });
        findViewById(R.id.tv_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.forgetPass).setOnClickListener(view -> {
            ll_fwdPass.setVisibility(View.VISIBLE);
            fwpPassTil.setVisibility(View.GONE);
            fwpPassConfTil.setVisibility(View.GONE);
            fwpOTPEt.setVisibility(View.GONE);
            ll_login.setVisibility(View.GONE);
            fwpPassEt.setText("");
            fwpPassConfEt.setText("");
            fwpOTPEt.setText("");

        });
        findViewById(R.id.bt_fwpCancel).setOnClickListener(view -> {
            ll_login.setVisibility(View.VISIBLE);
            ll_fwdPass.setVisibility(View.GONE);
            fwpPassTil.setVisibility(View.GONE);
            fwpPassConfTil.setVisibility(View.GONE);
            fwpOTPEt.setVisibility(View.GONE);
            fwpPassEt.setText("");
            fwpPassConfEt.setText("");
            fwpOTPEt.setText("");


        });
        findViewById(R.id.bt_fwpSubmit).setOnClickListener(view -> {
            if (!validateFP()) {
                return;
            }
            forgetPassword(this);

        });
    }

   /* private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        someActivityResultLauncher.launch(signInIntent);
    }*/

    private void signInWithGoogle(CredentialManager credentialManager, GetCredentialRequest googleSignRequest) {

        credentialManager.getCredentialAsync(LoginActivity.this, googleSignRequest,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleSignInResult(result.getCredential());
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        if (e instanceof NoCredentialException) {
                            Intent intent = new Intent(Settings.ACTION_ADD_ACCOUNT);
                            intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[]{"com.google"});
                            startActivity(intent);
                        } else {
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());

                        }
                    }
                });
    }


    private boolean validateMobile() {
        String mobile = userIdEt.getText().toString().trim();
        if (mobile.isEmpty()) {
            userIdEt.setError(getString(R.string.err_empty_field));
            userIdEt.requestFocus();
            return false;
        } else if (TextUtils.isDigitsOnly(mobile) && !mobile.matches("[6-9][0-9]{9}")) {
            userIdEt.setError(getString(R.string.err_invalid_mobile));
            userIdEt.requestFocus();
            return false;
        } else if (!TextUtils.isDigitsOnly(mobile) && !Patterns.EMAIL_ADDRESS.matcher(mobile).matches()) {
            userIdEt.setError(getString(R.string.err_invalid_email));
            userIdEt.requestFocus();
            return false;
        }
        userIdEt.setError(null);
        return true;
    }

    private boolean validateFP() {
        String mobile = fwpMobileEt.getText().toString().trim();
        if (mobile.isEmpty()) {
            fwpMobileEt.setError(getString(R.string.err_empty_field));
            fwpMobileEt.requestFocus();
            return false;
        } else if (TextUtils.isDigitsOnly(mobile) && !mobile.matches("[6-9][0-9]{9}")) {
            fwpMobileEt.setError(getString(R.string.err_invalid_mobile));
            fwpMobileEt.requestFocus();
            return false;
        } else if (!TextUtils.isDigitsOnly(mobile) && !Patterns.EMAIL_ADDRESS.matcher(mobile).matches()) {
            fwpMobileEt.setError(getString(R.string.err_invalid_email));
            fwpMobileEt.requestFocus();
            return false;
        } else if (fwpPassTil.getVisibility() == View.VISIBLE && fwpPassEt.getText().toString().trim().isEmpty()) {
            fwpPassEt.setError(getString(R.string.err_msg_pass));
            fwpPassEt.requestFocus();
            return false;
        } else if (fwpPassTil.getVisibility() == View.VISIBLE && !fwpPassEt.getText().toString().trim().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$")) {
            fwpPassEt.setError(getString(R.string.err_invalid_password));
            fwpPassEt.requestFocus();
            return false;
        } else if (fwpPassConfTil.getVisibility() == View.VISIBLE && !fwpPassEt.getText().toString().trim().equalsIgnoreCase(fwpPassConfEt.getText().toString().trim())) {
            fwpPassConfEt.setError("Password and confirm password should be same");
            fwpPassConfEt.requestFocus();
            return false;
        } else if (fwpOTPEt.getVisibility() == View.VISIBLE && fwpOTPEt.getText().toString().trim().length() != 6) {
            fwpOTPEt.setError("Please enter valid 6 digits OTP");
            fwpOTPEt.requestFocus();
            return false;
        }
        fwpMobileEt.setError(null);
        fwpPassEt.setError(null);
        fwpPassConfEt.setError(null);
        fwpOTPEt.setError(null);
        return true;
    }


    private boolean validatePass() {
        String password = passwordEt.getText().toString().trim();
        if (password.isEmpty()) {
            passwordEt.setError(getString(R.string.err_msg_pass));
            passwordEt.requestFocus();
            return false;
        } else if (!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$")) {
            passwordEt.setError(getString(R.string.err_invalid_password));
            passwordEt.requestFocus();
            return false;
        }
        passwordEt.setError(null);
        return true;
    }

    public void RecentNumbers() {
        String abcd = UtilMethods.INSTANCE.getRecentLogin(this);
        if (abcd != null && abcd.length() > 4) {
            recentNumberMap = new Gson().fromJson(abcd, new TypeToken<HashMap<String, String>>() {
            }.getType());
            for (Map.Entry<String, String> e : recentNumberMap.entrySet()) {
                String key = e.getKey();
                recentNumber.add(key);


            }

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recentNumber);
            userIdEt.setAdapter(adapter);
            userIdEt.setThreshold(1);
        }


        userIdEt.setOnItemClickListener((parent, view, position, id) -> passwordEt.setText(recentNumberMap.get(recentNumber.get(position))));
    }

   /* private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }*/

    private void secureLogin(Activity context, String referralId, int loginPlatformID, String gmailToken, String username, String password, final CustomLoader loader) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<LoginResponse> call = git.secureLogin(referralId, username, password, loginPlatformID, gmailToken);
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (loader != null) {
                        if (loader.isShowing())
                            loader.dismiss();
                    }
                    try {
                        if (response.body() != null) {
                            if (response.body().getStatusCode() == 1) {
                                if (response.body().getResult() != null) {
                                    tokenManager.saveTokens(response.body().getResult().getToken(),
                                            response.body().getResult().getRefreshToken(),
                                            response.body().getResult().getUserId(),
                                            response.body().getResult().getUsername(),
                                            response.body().getResult().getFisrtName(),
                                            response.body().getResult().getLastName());
                                    tokenManager.set(tokenManager.LoginPref, new Gson().toJson(response.body().getResult()));
                                    finishAffinity();
                                    Intent intent = new Intent(context, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    UtilMethods.INSTANCE.Error(context, ApplicationConstant.INSTANCE.SomethingError);
                                }
                            } else {
                                UtilMethods.INSTANCE.Error(context, response.body().getResponseText());
                            }
                        } else {
                            UtilMethods.INSTANCE.Error(context, ApplicationConstant.INSTANCE.SomethingError);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (loader != null) {
                            if (loader.isShowing())
                                loader.dismiss();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    if (loader != null) {
                        if (loader.isShowing())
                            loader.dismiss();
                    }
                    try {
                        if (t.getMessage() != null && !t.getMessage().isEmpty()) {

                            if (t.getMessage().contains("No address associated with hostname")) {
                                UtilMethods.INSTANCE.NetworkError(context, context.getResources().getString(R.string.err_msg_network_title),
                                        context.getResources().getString(R.string.err_msg_network));
                            } else {
                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            //   Toast(context, context.getResources().getString(R.string.some_thing_error), Toast.LENGTH_SHORT).show();

                        }
                    } catch (IllegalStateException ise) {
                        //  Toast(context, ise.getMessage(), Toast.LENGTH_SHORT).show();;

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void forgetPassword(Activity context) {
        try {
            loader.show();
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<LoginResponse> call = git.forgetPassword(fwpMobileEt.getText().toString().trim(), fwpPassEt.getText().toString().trim(), fwpOTPEt.getText().toString().trim());
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (loader != null) {
                        if (loader.isShowing())
                            loader.dismiss();
                    }
                    try {
                        if (response.body() != null) {
                            if (response.body().getStatusCode() == 1) {
                                if (fwpPassTil.getVisibility() == View.GONE) {
                                    fwpPassTil.setVisibility(View.VISIBLE);
                                    fwpPassConfTil.setVisibility(View.VISIBLE);
                                    fwpOTPEt.setVisibility(View.VISIBLE);
                                } else {
                                    fwpPassTil.setVisibility(View.GONE);
                                    fwpPassConfTil.setVisibility(View.GONE);
                                    fwpOTPEt.setVisibility(View.GONE);
                                    fwpPassEt.setText("");
                                    fwpPassConfEt.setText("");
                                    fwpOTPEt.setText("");
                                    ll_fwdPass.setVisibility(View.GONE);
                                    ll_login.setVisibility(View.VISIBLE);
                                }

                                UtilMethods.INSTANCE.Success(context, response.body().getResponseText());
                            } else {
                                UtilMethods.INSTANCE.Error(context, response.body().getResponseText());
                            }
                        } else {
                            UtilMethods.INSTANCE.Error(context, ApplicationConstant.INSTANCE.SomethingError);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (loader != null) {
                            if (loader.isShowing())
                                loader.dismiss();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    if (loader != null) {
                        if (loader.isShowing())
                            loader.dismiss();
                    }
                    try {
                        if (t.getMessage() != null && !t.getMessage().isEmpty()) {

                            if (t.getMessage().contains("No address associated with hostname")) {
                                UtilMethods.INSTANCE.NetworkError(context, context.getResources().getString(R.string.err_msg_network_title),
                                        context.getResources().getString(R.string.err_msg_network));
                            } else {
                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            //   Toast(context, context.getResources().getString(R.string.some_thing_error), Toast.LENGTH_SHORT).show();

                        }
                    } catch (IllegalStateException ise) {
                        //  Toast(context, ise.getMessage(), Toast.LENGTH_SHORT).show();;

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkUserExist(Activity context, String name, String email, String gmailToken, final CustomLoader loader) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<Boolean> call = git.checkUserExists(gmailToken);
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body()) {
                                    secureLogin(LoginActivity.this, "0", 1, gmailToken, email, name, loader);
                                } else {
                                    if (loader != null) {
                                        if (loader.isShowing())
                                            loader.dismiss();
                                    }
                                    dialogReferralInput(name, email, gmailToken);
                                }
                            } else {
                                if (loader != null) {
                                    if (loader.isShowing())
                                        loader.dismiss();
                                }
                                UtilMethods.INSTANCE.Error(context, ApplicationConstant.INSTANCE.SomethingError);
                            }
                        } else {
                            UtilMethods.INSTANCE.apiErrorHandle(context, response.code(), response.message());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (loader != null) {
                            if (loader.isShowing())
                                loader.dismiss();
                        }
                        UtilMethods.INSTANCE.Error(context, e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                    if (loader != null) {
                        if (loader.isShowing())
                            loader.dismiss();
                    }
                    UtilMethods.INSTANCE.apiFailureError(context, t);
                }
            });
        } catch (Exception e) {
            if (loader != null) {
                if (loader.isShowing())
                    loader.dismiss();
            }
            UtilMethods.INSTANCE.Error(context, e.getMessage());
            e.printStackTrace();
        }
    }

    /* @Override
     public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         // Handle Google Sign-In result
         if (requestCode == RC_SIGN_IN) {
             Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
             handleSignInResult(task);
         }
     }*/


    /*ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result != null && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                }
            });*/

   /* private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String personName = account.getDisplayName();
            String personEmail = account.getEmail();
            String token = account.getIdToken();
            mGoogleSignInClient.signOut();
            if (UtilMethods.INSTANCE.isNetworkAvialable(LoginActivity.this)) {
                loader.show();
                loader.setCancelable(false);
                loader.setCanceledOnTouchOutside(false);
                checkUserExist(LoginActivity.this, personName, personEmail, token, loader);
                //secureLogin(LoginActivity.this, 1, token, personEmail, personName, loader);
            } else {
                UtilMethods.INSTANCE.NetworkError(LoginActivity.this, getResources().getString(R.string.err_msg_network_title),
                        getResources().getString(R.string.err_msg_network));
            }


           *//* Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("name", personName);
            intent.putExtra("email", personEmail);
            startActivity(intent);
            finish();*//*
        } catch (ApiException e) {
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
        }
    }*/

    private void handleSignInResult(Credential credential) {

        if (credential.getType().equalsIgnoreCase(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {


            GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());


            String personName = googleIdTokenCredential.getDisplayName();
            String personEmail = googleIdTokenCredential.getId();
            String token = googleIdTokenCredential.getIdToken();
            // mGoogleSignInClient.signOut();
            runOnUiThread(() -> {
                if (UtilMethods.INSTANCE.isNetworkAvialable(LoginActivity.this)) {
                    loader.show();
                    loader.setCancelable(false);
                    loader.setCanceledOnTouchOutside(false);
                    checkUserExist(LoginActivity.this, personName, personEmail, token, loader);
                    //secureLogin(LoginActivity.this, 1, token, personEmail, personName, loader);
                } else {
                    UtilMethods.INSTANCE.NetworkError(LoginActivity.this, getResources().getString(R.string.err_msg_network_title),
                            getResources().getString(R.string.err_msg_network));
                }
            });


        } else {
            // Catch any unrecognized credential type here.
            Log.e("TAG", "Unexpected type of credential");
        }

    }

    private void dialogReferralInput(String name, String email, String gmailToken) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_referral_input, null);
        final EditText input = dialogView.findViewById(R.id.edit_text_input);
        View bt_cancel = dialogView.findViewById(R.id.bt_cancel);
        View bt_submit = dialogView.findViewById(R.id.bt_submit);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        bt_submit.setOnClickListener(view -> {
            if (input.getText().toString().trim().isEmpty()) {
                input.setError("Enter valid referral id");
                input.requestFocus();
            } else {
                dialog.dismiss();
                loader.show();
                loader.setCancelable(false);
                loader.setCanceledOnTouchOutside(false);
                secureLogin(LoginActivity.this, input.getText().toString().trim(), 1, gmailToken, email, name, loader);
            }
        });

        bt_cancel.setOnClickListener(view -> {
            dialog.dismiss();
            loader.show();
            loader.setCancelable(false);
            loader.setCanceledOnTouchOutside(false);
            secureLogin(LoginActivity.this, "0", 1, gmailToken, email, name, loader);
        });

        dialog.show();
    }
}