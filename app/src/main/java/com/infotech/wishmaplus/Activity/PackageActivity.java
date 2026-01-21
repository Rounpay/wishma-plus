package com.infotech.wishmaplus.Activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.infotech.wishmaplus.Adapter.PackageAdapter;
import com.infotech.wishmaplus.Api.Object.PackageResult;
import com.infotech.wishmaplus.Api.Object.PgKeyVals;
import com.infotech.wishmaplus.Api.Response.BasicListResponse;
import com.infotech.wishmaplus.Api.Response.PayUCheckProResponse;
import com.infotech.wishmaplus.Api.Response.UpgradePackageResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.CheckoutProWebChromeClient;
import com.infotech.wishmaplus.Utils.CheckoutProWebViewClient;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.payu.base.models.CardType;
import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUPaymentParams;
import com.payu.base.models.PaymentType;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.models.PayUCheckoutProConfig;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.custombrowser.Bank;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PackageActivity extends AppCompatActivity {


    private PreferencesManager tokenManager;
    private CustomLoader loader;

    private RecyclerView recyclerView;
    private ArrayList<PackageResult> packageList=new ArrayList<>();
    private PackageAdapter adapter;
    private long mLastClickTime;
    //private String hashPayUSDkPro;
    private int selectedPackageId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_package);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.clMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tokenManager = new PreferencesManager(this,1);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
       /* if (userDetailResponse == null) {
            userDetailResponse = UtilMethods.INSTANCE.getUserDetailResponse(tokenManager);
        }*/


        init();
        getPackage();

    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
       adapter= new PackageAdapter(PackageActivity.this, packageList, new PackageAdapter.OnClick() {
            @Override
            public void onClick(PackageResult value) {
               selectedPackageId= value.getPackageID();
                upgradePackage(null,null,null,null);
            }
        });
        recyclerView.setAdapter(adapter);
        findViewById(R.id.back_button).setOnClickListener(view -> finish());


    }


    public void upgradePackage(String tid, String hashData, String hashName,PayUHashGenerationListener hashGenerationListener) {
        try {

            loader.show();
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<UpgradePackageResponse> call = git.upgradePackage("Bearer " + tokenManager.getAccessToken(),selectedPackageId,tid,hashData);
            call.enqueue(new Callback<UpgradePackageResponse>() {
                @Override
                public void onResponse(@NonNull Call<UpgradePackageResponse> call, @NonNull Response<UpgradePackageResponse> response) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    try {
                        UpgradePackageResponse packageResponse = response.body();
                        if (packageResponse != null) {
                            if (packageResponse.getStatusCode() == 1) {

                                if(packageResponse.isPgActive() && packageResponse.getData()!=null){
                                    if(packageResponse.getData().getStatusCode()==1 && packageResponse.getData().getPgResponse()!=null){
                                        if(packageResponse.getData().getPgResponse().getKeyVals()!=null){
                                            if (hashData == null || hashData.isEmpty()) {
                                                startPayUPayment(packageResponse.getData().getPgResponse().getKeyVals());
                                            } else {
                                                if (packageResponse.getData().getPgResponse().getKeyVals().getHash() != null && !packageResponse.getData().getPgResponse().getKeyVals().getHash().isEmpty()){
                                                   // hashPayUSDkPro = packageResponse.getData().getPgResponse().getKeyVals().getHash();
                                                    HashMap<String, String> dataMap = new HashMap<>();
                                                    dataMap.put(hashName, packageResponse.getData().getPgResponse().getKeyVals().getHash());
                                                    /*Log.e("HashData", hashPayUSDkPro);*/
                                                    hashGenerationListener.onHashGenerated(dataMap);

                                                } else {
                                                    UtilMethods.INSTANCE.Error(PackageActivity.this, "Problem in Hash generation");
                                                }
                                            }



                                        }else{
                                            UtilMethods.INSTANCE.Error(PackageActivity.this, "Transaction data is not available");
                                        }
                                        //call Gatway
                                    }else {
                                        UtilMethods.INSTANCE.Error(PackageActivity.this, packageResponse.getData().getResponseText());
                                    }
                                }else {
                                    getPackage();
                                    Toast.makeText(PackageActivity.this, packageResponse.getResponseText(), Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                UtilMethods.INSTANCE.Error(PackageActivity.this, packageResponse.getResponseText());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilMethods.INSTANCE.Error(PackageActivity.this, e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UpgradePackageResponse> call, @NonNull Throwable t) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    try {

                        UtilMethods.INSTANCE.apiFailureError(PackageActivity.this, t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(PackageActivity.this, ise.getMessage());

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
            UtilMethods.INSTANCE.Error(PackageActivity.this, e.getMessage());

        }
    }

    private void startPayUPayment(PgKeyVals keyVals) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000)
            return;
        mLastClickTime = SystemClock.elapsedRealtime();
        if (validateSDKParams(keyVals)) {
            //hashPayUSDkPro = keyVals.getHash();
            initUiSdk(preparePayUBizParams(keyVals), keyVals);
        }
    }
    private PayUPaymentParams preparePayUBizParams(PgKeyVals mKeyVals) {

        HashMap<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(PayUCheckoutProConstants.CP_UDF1, "udf1");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF2, "udf2");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF3, "udf3");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF4, "udf4");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF5, "udf5");

        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setAmount(mKeyVals.getAmount())
                .setIsProduction(mKeyVals.isProdcution())
                .setProductInfo(mKeyVals.getProductinfo())
                .setKey(mKeyVals.getKey())
                .setPhone(mKeyVals.getPhone())
                .setTransactionId(mKeyVals.getTxnid())
                .setFirstName(mKeyVals.getFirstname())
                .setEmail(mKeyVals.getEmail())
                .setSurl(mKeyVals.getSurl())
                .setFurl(mKeyVals.getFurl())
                .setAdditionalParams(additionalParams)
                .setUserCredential(mKeyVals.getKey() + mKeyVals.getEmail())
                .setPayUSIParams(null);
        PayUPaymentParams payUPaymentParams = builder.build();
        return payUPaymentParams;
    }
    private void initUiSdk(PayUPaymentParams payUPaymentParams, PgKeyVals mKeyVals) {
        PayUCheckoutPro.open(
                this,
                payUPaymentParams,
                getCheckoutProConfig(mKeyVals),
                new PayUCheckoutProListener() {

                    @Override
                    public void onPaymentSuccess(@NotNull Object response) {

                        //HashMap<String, Object> result = (HashMap<String, Object>) response;
                        // Log.e("PAYUProResp", "Payu's Data : " + result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE) + "\n\n\n Merchant's Data: " + result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE));
                        // Object payuResponse = result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                        // PayUCheckProResponse itemResponse = new Gson().fromJson((String) payuResponse, PayUCheckProResponse.class);
                        //JSONObject payuCheckProJObject  = new JSONObject((String)payuResponse); // json
                        //Log.e("PAYUProResp", "Payu's Success : " + itemResponse);
                        payuStatusUpdate(mKeyVals.getTxnid()/*,itemResponse.getStatus()*/);
                        //PayUCheckProCallBackApi(PayUCheckProSuccessData(mKeyVals, payuResponse, 2));
                    }

                    @Override
                    public void onPaymentFailure(Object response) {
                      //  HashMap<String, Object> result = (HashMap<String, Object>) response;
                        //Log.e("PAYUProResp", "Payu's Data : " + result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE) + "\n\n\n Merchant's Data: " + result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE));
                        //Object payuResponse = result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                      //  try {
                           // PayUCheckProResponse itemResponse = new Gson().fromJson((String) payuResponse, PayUCheckProResponse.class);
                            payuStatusUpdate(mKeyVals.getTxnid()/*,itemResponse.getStatus()*/);
                            // Log.e("PAYUProResp", "Payu's Error : " + itemResponse);
                          //  PayUCheckProCallBackApi(PayUCheckProFailedData(mKeyVals, 3, itemResponse.getStatus(), itemResponse.getErrorMessage()));
                       // } catch (Exception exception) {
                            //  Log.e("PAYUProResp", "Payu's Error : " + exception.getMessage());
                       // }

                       /*   UtilMethods.INSTANCE.Balancecheck(AddMoneyActivity.this, loader, object -> {
                                        balanceCheckResponse = (BalanceResponse) object;
                                        if (balanceCheckResponse != null && balanceCheckResponse.getBalanceData() != null) {
                                            showWalletListPopupWindow();
                                        }
                                    });*/

                    }

                    @Override
                    public void onPaymentCancel(boolean isTxnInitiated) {
                        showSnackBar(getResources().getString(R.string.transaction_cancelled_by_user));
                    }

                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        String errorMessage = errorResponse.getErrorMessage();
                        if (TextUtils.isEmpty(errorMessage))
                            errorMessage = getResources().getString(R.string.some_thing_error);
                        showSnackBar(errorMessage);
                    }

                    @Override
                    public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
                        //For setting webview properties, if any. Check Customized Integration section for more details on this
                        webView.setWebChromeClient(new CheckoutProWebChromeClient((Bank) o));
                        webView.setWebViewClient(new CheckoutProWebViewClient((Bank) o, mKeyVals.getKey()));
                    }

                    @Override
                    public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                        String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                        String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                        if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                            // Log.e("CP_HASH_STRING", hashData);
                            // Log.e("CP_HASH_NAME", hashName);

                            //Generate Hash Key From Server End---
                            //GatewayTransaction(hashData, hashName, hashGenerationListener);
                           /* String hash = hashString;*/
                            upgradePackage(mKeyVals.getTxnid(),hashData, hashName, hashGenerationListener);
                            /*if (!TextUtils.isEmpty(hashPayUSDkPro)) {
                                HashMap hashMap = new HashMap();
                                hashMap.put(hashName, hashPayUSDkPro);
                                hashGenerationListener.onHashGenerated(hashMap);
                            }*/
                        }
                    }

                }
        );

        /*Log.e("PayUPaymentParams", new Gson().toJson(payUPaymentParams));*/
    }

    private PayUCheckoutProConfig getCheckoutProConfig(PgKeyVals mKeyVals) {
        PayUCheckoutProConfig checkoutProConfig = new PayUCheckoutProConfig();
        checkoutProConfig.setMerchantName(getString(R.string.app_name));
        //checkoutProConfig.setPaymentModesOrder(getCheckoutOrderList(mKeyVals));
        //checkoutProConfig.setOfferDetails(getOfferDetailsList());
        //checkoutProConfig.setPaymentModesOrder(getCheckoutOrderList(mKeyVals));
        checkoutProConfig.setEnforcePaymentList(getEnforcePaymentList(mKeyVals));
        checkoutProConfig.setShowCbToolbar(false);
        checkoutProConfig.setAutoSelectOtp(false);
        checkoutProConfig.setAutoApprove(false);
        checkoutProConfig.setMerchantSmsPermission(true);
        //checkoutProConfig.setSurePayCount(Integer.parseInt(binding.etSurePayCount.getText().toString()));
        checkoutProConfig.setShowExitConfirmationOnPaymentScreen(true);
        checkoutProConfig.setShowExitConfirmationOnCheckoutScreen(true);
        checkoutProConfig.setMerchantLogo(R.drawable.app_logo);
        checkoutProConfig.setMerchantResponseTimeout(10000); // for 10 seconds timeout
        checkoutProConfig.setWaitingTime(30000);// for 30 seconds read OTP Time
        //checkoutProConfig.setSurePayCount(3); //The Default value is 0.
        //checkoutProConfig.setCustomNoteDetails(getCustomeNoteList());
        /*if (reviewOrderAdapter != null)
            checkoutProConfig.setCartDetails(reviewOrderAdapter.getOrderDetailsList());*/
        return checkoutProConfig;
    }

    private ArrayList<HashMap<String, String>> getEnforcePaymentList(PgKeyVals mKeyVals) {
        ArrayList<HashMap<String, String>> enforceList = new ArrayList();


        if (mKeyVals.getEnforce_paymethod() != null && !mKeyVals.getEnforce_paymethod().isEmpty()) {
            HashMap<String, String> map = new HashMap<>();
            if (mKeyVals.getEnforce_paymethod().toLowerCase().contains("debitcard") || mKeyVals.getEnforce_paymethod().toLowerCase().contains("debit card") /*|| selectedMethod.toLowerCase().contains("debit")*/) {
                map.put(PayUCheckoutProConstants.CP_PAYMENT_TYPE, PaymentType.CARD.name());
                map.put(PayUCheckoutProConstants.CP_CARD_TYPE, CardType.DC.name());
                //map.put(PayUCheckoutProConstants.CP_CARD_SCHEME, CardScheme.RUPAY.name());
            } else if (mKeyVals.getEnforce_paymethod().toLowerCase().contains("creditcard") || mKeyVals.getEnforce_paymethod().toLowerCase().contains("credit card") /*|| selectedMethod.toLowerCase().contains("credit")*/) {
                map.put(PayUCheckoutProConstants.CP_PAYMENT_TYPE, PaymentType.CARD.name());
                map.put(PayUCheckoutProConstants.CP_CARD_TYPE, CardType.CC.name());
            } else if (mKeyVals.getEnforce_paymethod().toLowerCase().contains("upi") /*|| selectedMethod.toLowerCase().contains("upi")*/) {
                map.put(PayUCheckoutProConstants.CP_PAYMENT_TYPE, PaymentType.UPI_INTENT.name());
            } else if (mKeyVals.getEnforce_paymethod().toLowerCase().contains("net banking") || mKeyVals.getEnforce_paymethod().toLowerCase().contains("netbanking") /*|| selectedMethod.toLowerCase().contains("net")*/) {
                map.put(PayUCheckoutProConstants.CP_PAYMENT_TYPE, PaymentType.NB.name());
            } else if (mKeyVals.getEnforce_paymethod().toLowerCase().contains("wallet") /*|| selectedMethod.toLowerCase().contains("wallet")*/) {
                map.put(PayUCheckoutProConstants.CP_PAYMENT_TYPE, PaymentType.WALLET.name());
            }

            enforceList.add(map);
        }

        return enforceList;
    }

    private boolean validateSDKParams(PgKeyVals mKeyVals) {
        if (mKeyVals.getKey() == null || TextUtils.isEmpty(mKeyVals.getKey())) {
            UtilMethods.INSTANCE.Error(PackageActivity.this,"Invalid or empty Key");
            return false;
        } else if (mKeyVals.getHash() == null || TextUtils.isEmpty(mKeyVals.getHash())) {
            UtilMethods.INSTANCE.Error(PackageActivity.this,"Invalid or empty Hash");
            return false;
        } else if (mKeyVals.getTxnid() == null || TextUtils.isEmpty(mKeyVals.getTxnid())) {
            UtilMethods.INSTANCE.Error(PackageActivity.this,"Invalid or empty Transaction Id");
            return false;
        } else if (mKeyVals.getEmail() == null || TextUtils.isEmpty(mKeyVals.getEmail())) {
            UtilMethods.INSTANCE.Error(PackageActivity.this,"Invalid or empty Mail Id");
            return false;
        } else if (mKeyVals.getFirstname() == null || TextUtils.isEmpty(mKeyVals.getFirstname())) {
            UtilMethods.INSTANCE.Error(PackageActivity.this,"Invalid or empty Name");
            return false;
        } else if (mKeyVals.getSurl() == null || TextUtils.isEmpty(mKeyVals.getSurl())) {
            UtilMethods.INSTANCE.Error(PackageActivity.this,"Invalid or empty Success URL");
            return false;
        } else if (mKeyVals.getFurl() == null || TextUtils.isEmpty(mKeyVals.getFurl())) {
            UtilMethods.INSTANCE.Error(PackageActivity.this,"Invalid or empty Fail URL");
            return false;
        }

        return true;
    }

    private Bundle PayUCheckProSuccessData(PgKeyVals mKeyVals, Object payuResponse, int statusCode) {
        Bundle inResponse = new Bundle();
        try {
            PayUCheckProResponse itemResponse = new Gson().fromJson((String) payuResponse, PayUCheckProResponse.class);
            //JSONObject payuCheckProJObject  = new JSONObject((String)payuResponse); // json
            //Log.e("PAYUProResp", "Payu's Success : " + itemResponse);

            inResponse.putString("STATUS", itemResponse.getStatus());

            try {
                inResponse.putString("CHECKSUMHASH", itemResponse.getHash());
            } catch (Exception e) {
                e.printStackTrace();
                inResponse.putString("CHECKSUMHASH", "");
            }

            inResponse.putString("BANKNAME", "");
            try {
                inResponse.putString("ORDERID", String.valueOf(itemResponse.getId()));
            } catch (Exception e) {
                e.printStackTrace();
                inResponse.putString("ORDERID", mKeyVals.getTxnid());//ORDERID
            }

            try {
                inResponse.putString("TXNAMOUNT", itemResponse.getAmount());
            } catch (Exception e) {
                e.printStackTrace();
                inResponse.putString("TXNAMOUNT", mKeyVals.getAmount());//TXNAMOUNT
            }

            inResponse.putString("MID", "");

            try {
                inResponse.putString("TXNID", itemResponse.getTxnid());
            } catch (Exception e) {
                e.printStackTrace();
                inResponse.putString("TXNID", "");//TXNID
            }
            inResponse.putString("RESPCODE", statusCode + "");//RESPCODE
            try {
                inResponse.putString("PAYMENTMODE", itemResponse.getMode());
            } catch (Exception e) {
                e.printStackTrace();
                inResponse.putString("PAYMENTMODE", "" + mKeyVals.getMode());//PAYMENTMODE
            }
            try {
                inResponse.putString("BANKTXNID", itemResponse.getBankRefNo());
            } catch (Exception e) {
                e.printStackTrace();
                inResponse.putString("BANKTXNID", "");//BANKTXNID
            }

            inResponse.putString("CURRENCY", "INR");

            try {
                inResponse.putString("GATEWAYNAME", itemResponse.getPaymentSource());
            } catch (Exception e) {
                e.printStackTrace();
                inResponse.putString("GATEWAYNAME", "");//GATEWAYNAME
            }

            try {
                inResponse.putString("TXNDATE", itemResponse.getAddedon());//txTime
            } catch (Exception e) {
                e.printStackTrace();
                inResponse.putString("TXNDATE", "");//TXNDATE
            }

            try {
                inResponse.putString("RESPMSG", itemResponse.getField9());//txMsg
            } catch (Exception e) {
                e.printStackTrace();
                inResponse.putString("RESPMSG", "");//txMsg
            }

        } catch (Exception exception) {
            // Log.e("PAYUProResp", "Payu's Error : " + exception.getMessage());
        }
        return inResponse;
    }

    private Bundle PayUCheckProFailedData(PgKeyVals mKeyVals, int errorCode, String status, String errorMsg) {
        Bundle inResponse = new Bundle();
        inResponse.putString("STATUS", status);
        inResponse.putString("CHECKSUMHASH", "");
        inResponse.putString("BANKNAME", "");
        inResponse.putString("ORDERID", mKeyVals.getTxnid());
        inResponse.putString("TXNAMOUNT", String.valueOf(mKeyVals.getAmount()));
        inResponse.putString("MID", "");
        inResponse.putString("TXNID", "");
        inResponse.putString("RESPCODE", errorCode + "");
        inResponse.putString("PAYMENTMODE", "");
        inResponse.putString("BANKTXNID", "");
        inResponse.putString("CURRENCY", "INR");
        inResponse.putString("GATEWAYNAME", "");
        inResponse.putString("RESPMSG", errorMsg);

        return inResponse;
    }

    private void showSnackBar(String message) {
        Snackbar.make(findViewById(R.id.clMain), message, Snackbar.LENGTH_LONG).show();
    }

    public void payuStatusUpdate(String tid/*, String status*/) {
        try {

            loader.show();
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<UpgradePackageResponse> call = git.payUTransactionUpdate("Bearer " + tokenManager.getAccessToken(),
                    tid);
            call.enqueue(new Callback<UpgradePackageResponse>() {
                @Override
                public void onResponse(@NonNull Call<UpgradePackageResponse> call, @NonNull Response<UpgradePackageResponse> response) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    try {
                        UpgradePackageResponse packageResponse = response.body();
                        if (packageResponse != null) {
                            if (packageResponse.getStatusCode() == 1) {
                                getPackage();
                                UtilMethods.INSTANCE.Success(PackageActivity.this, packageResponse.getResponseText());
                            } else {
                                UtilMethods.INSTANCE.Error(PackageActivity.this, packageResponse.getResponseText());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilMethods.INSTANCE.Error(PackageActivity.this, e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UpgradePackageResponse> call, @NonNull Throwable t) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    try {

                        UtilMethods.INSTANCE.apiFailureError(PackageActivity.this, t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(PackageActivity.this, ise.getMessage());

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
            UtilMethods.INSTANCE.Error(PackageActivity.this, e.getMessage());

        }
    }

    private void getPackage() {
        try {
            loader.show();
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicListResponse<PackageResult>> call = git.getUserPackage("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<BasicListResponse<PackageResult>>() {
                @Override
                public void onResponse(@NonNull Call<BasicListResponse<PackageResult>> call, @NonNull Response<BasicListResponse<PackageResult>> response) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    try {
                        BasicListResponse<PackageResult> packageResponse = response.body();
                        if (packageResponse != null) {
                            if (packageResponse.getStatusCode() == 1) {
                                if(packageResponse.getResult()!=null && packageResponse.getResult().size()>0){
                                    packageList.clear();
                                    packageList.addAll(packageResponse.getResult());
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                UtilMethods.INSTANCE.Error(PackageActivity.this, packageResponse.getResponseText());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilMethods.INSTANCE.Error(PackageActivity.this, e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BasicListResponse<PackageResult>> call, @NonNull Throwable t) {
                    try {
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }
                        UtilMethods.INSTANCE.apiFailureError(PackageActivity.this, t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(PackageActivity.this, ise.getMessage());
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
            UtilMethods.INSTANCE.Error(PackageActivity.this, e.getMessage());
            if (loader != null) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }
            }
        }
    }


}