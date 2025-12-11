package com.infotech.wishmaplus.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.Api.Response.EligibilityModel;
import com.infotech.wishmaplus.Api.Response.EnableDashboardResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.UtilMethods;

public class ProfessionalTools extends AppCompatActivity {
    private CustomLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_professional_tools);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.btnNext).setOnClickListener(v -> {
            enableProfessionalDashboard();
//            Intent intent = new Intent(this, SuccessProfessional.class);
//            startActivity(intent);
        });
    }

    public void enableProfessionalDashboard(){
        loader.show();
        UtilMethods.INSTANCE.enableProfessionalDashBoard(this, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                EnableDashboardResponse enableDashboardResponse =(EnableDashboardResponse) object;
                if(enableDashboardResponse.getStatusCode()==1){
                    Intent intent = new Intent(ProfessionalTools.this, SuccessProfessional.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(ProfessionalTools.this, enableDashboardResponse.getResponseText(), Toast.LENGTH_SHORT).show();
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
}