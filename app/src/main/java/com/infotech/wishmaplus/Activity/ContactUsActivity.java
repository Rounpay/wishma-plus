package com.infotech.wishmaplus.Activity;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.Api.Response.CompanyDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUsActivity extends AppCompatActivity {


    private PreferencesManager tokenManager;
    private CustomLoader loader;
    private TextView callUsLabel,callUsTv, whatsappLabel, whatsappTv, emailLabel, emailTv, addressLabel, addressTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_us);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contactUS), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tokenManager = new PreferencesManager(this, 1);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
       /* if (userDetailResponse == null) {
            userDetailResponse = UtilMethods.INSTANCE.getUserDetailResponse(tokenManager);
        }*/
        init();
        getDetail();

    }

    private void init() {
        callUsLabel = findViewById(R.id.callUsLabel);
        callUsTv = findViewById(R.id.callUsTv);

        whatsappLabel = findViewById(R.id.whatsappLabel);
        whatsappTv = findViewById(R.id.whatsappTv);
        emailLabel = findViewById(R.id.emailLabel);
        emailTv = findViewById(R.id.emailTv);
        addressLabel = findViewById(R.id.addressLabel);
        addressTv = findViewById(R.id.addressTv);
        findViewById(R.id.back_button).setOnClickListener(view -> finish());


    }


    private void getDetail() {
        try {
            loader.show();
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<CompanyDetailResponse> call = git.getCompanyDetails("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<CompanyDetailResponse>() {
                @Override
                public void onResponse(@NonNull Call<CompanyDetailResponse> call, @NonNull Response<CompanyDetailResponse> response) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    try {
                        CompanyDetailResponse companyDetailResponse = response.body();
                        if (companyDetailResponse != null) {

                            if(companyDetailResponse.getPhoneNos()!=null && companyDetailResponse.getPhoneNos().size()>0){
                                callUsLabel.setVisibility(View.VISIBLE);
                                callUsTv.setVisibility(View.VISIBLE);
                                callUsTv.setText(Html.fromHtml(getHtmlCallUs(companyDetailResponse.getPhoneNos()),Html.FROM_HTML_MODE_LEGACY));
                                callUsTv.setMovementMethod(LinkMovementMethod.getInstance());
                            }else {
                                callUsLabel.setVisibility(View.GONE);
                                callUsTv.setVisibility(View.GONE);
                            }

                            if(companyDetailResponse.getWhatsAppNo()!=null && !companyDetailResponse.getWhatsAppNo().isEmpty()){
                                whatsappLabel.setVisibility(View.VISIBLE);
                                whatsappTv.setVisibility(View.VISIBLE);
                                whatsappTv.setText(Html.fromHtml("<a href='https://api.whatsapp.com/send?phone="+companyDetailResponse.getWhatsAppNo()+"'>"+companyDetailResponse.getWhatsAppNo()+"</a>",Html.FROM_HTML_MODE_LEGACY));
                                whatsappTv.setMovementMethod(LinkMovementMethod.getInstance());
                            }else {
                                whatsappLabel.setVisibility(View.GONE);
                                whatsappTv.setVisibility(View.GONE);
                            }

                            if(companyDetailResponse.getEmailID()!=null && !companyDetailResponse.getEmailID().isEmpty()){
                                emailLabel.setVisibility(View.VISIBLE);
                                emailTv.setVisibility(View.VISIBLE);
                                emailTv.setText(Html.fromHtml("<a href='mailto:" + companyDetailResponse.getEmailID() + "'>" + companyDetailResponse.getEmailID() + "</a>",Html.FROM_HTML_MODE_LEGACY));
                                emailTv.setMovementMethod(LinkMovementMethod.getInstance());
                            }else {
                                emailLabel.setVisibility(View.GONE);
                                emailTv.setVisibility(View.GONE);
                            }

                            if(companyDetailResponse.getAddress()!=null && !companyDetailResponse.getAddress().isEmpty()){
                                addressLabel.setVisibility(View.VISIBLE);
                                addressTv.setVisibility(View.VISIBLE);
                                addressTv.setText(companyDetailResponse.getAddress());
                            }else {
                                addressLabel.setVisibility(View.GONE);
                                addressTv.setVisibility(View.GONE);
                            }
                        } else {
                            UtilMethods.INSTANCE.Error(ContactUsActivity.this, "Detail is not available");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilMethods.INSTANCE.Error(ContactUsActivity.this, e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CompanyDetailResponse> call, @NonNull Throwable t) {
                    try {
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }
                        UtilMethods.INSTANCE.apiFailureError(ContactUsActivity.this, t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(ContactUsActivity.this, ise.getMessage());
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
            UtilMethods.INSTANCE.Error(ContactUsActivity.this, e.getMessage());
            if (loader != null) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }
            }
        }
    }




    String getHtmlCallUs(List<String> list){

        StringBuilder htmlStr= new StringBuilder();
        for(String item : list){
            if(htmlStr.length() == 0){
                htmlStr = new StringBuilder("<a href='tel:" + item + "'>" + item + "</a>");
            }else {
                htmlStr.append(", ").append("<a href='tel:").append(item).append("'>").append(item).append("</a>");
            }
        }
        return htmlStr.toString();
    }

}