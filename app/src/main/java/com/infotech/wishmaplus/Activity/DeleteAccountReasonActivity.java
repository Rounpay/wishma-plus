package com.infotech.wishmaplus.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.infotech.wishmaplus.Api.Response.CategoryResponse;
import com.infotech.wishmaplus.Api.Response.DeleteAccountResponse;
import com.infotech.wishmaplus.Api.Response.UserListFriends;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;

public class DeleteAccountReasonActivity extends AppCompatActivity {
    RadioGroup radioGroup;

    LinearLayout rowOne;
    LinearLayout rowTwo;
    LinearLayout rowThree;
    LinearLayout rowFour;
    LinearLayout rowFive;

    RadioButton rbOne;
    RadioButton rbTwo;
    RadioButton rbThree;
    RadioButton rbFour;
    RadioButton rbFive;
    MaterialButton btnContinue;
    private CustomLoader loader;
    private PreferencesManager tokenManager;
    DeleteAccountResponse deleteAccountResponse = new DeleteAccountResponse();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_account_reason);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tokenManager = new PreferencesManager(this,1);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        radioGroup = findViewById(R.id.radioGroup);
        btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setEnabled(false);
        btnContinue.setAlpha(0.5f);   // faded


        rowOne = findViewById(R.id.rowOne);
        rowTwo = findViewById(R.id.rowTwo);
        rowThree = findViewById(R.id.rowThree);
        rowFour = findViewById(R.id.rowFour);
        rowFive = findViewById(R.id.rowFive);
        rbOne = findViewById(R.id.rbOne);
        rbTwo = findViewById(R.id.rbTwo);
        rbThree = findViewById(R.id.rbThree);
        rbFour = findViewById(R.id.rbFour);
        rbFive = findViewById(R.id.rbFive);
        RadioButton[] radios = { rbOne, rbTwo, rbThree, rbFour, rbFive };

        View.OnClickListener rowClick = v -> {

            int id = v.getId();

            if (id == R.id.rowOne)       selectOnly(rbOne, radios);
            else if (id == R.id.rowTwo)  selectOnly(rbTwo, radios);
            else if (id == R.id.rowThree) selectOnly(rbThree, radios);
            else if (id == R.id.rowFour)  selectOnly(rbFour, radios);
            else if (id == R.id.rowFive)  selectOnly(rbFive, radios);

            enableContinueButton();
        };




        rowOne.setOnClickListener(rowClick);
        rowTwo.setOnClickListener(rowClick);
        rowThree.setOnClickListener(rowClick);
        rowFour.setOnClickListener(rowClick);
        rowFive.setOnClickListener(rowClick);
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        findViewById(R.id.btnCancel).setOnClickListener(view -> finish());
        btnContinue.setOnClickListener(view -> deleteUserAccount());
    }
    private void selectOnly(RadioButton selected, RadioButton[] all) {
        for (RadioButton rb : all) {
            rb.setChecked(rb == selected);
        }
    }
    private void enableContinueButton() {
        boolean isSelected = rbOne.isChecked() || rbTwo.isChecked() ||
                rbThree.isChecked() || rbFour.isChecked() ||
                rbFive.isChecked();

        btnContinue.setEnabled(isSelected);
        btnContinue.setAlpha(isSelected ? 1f : 0.5f);
    }
    private void deleteUserAccount() {
        loader.show();
        UtilMethods.INSTANCE.deleteAccountRequest(this, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                deleteAccountResponse = (DeleteAccountResponse) object;
                if(deleteAccountResponse.getStatusCode()==1) {
                    if (loader != null && loader.isShowing()) {
                        loader.dismiss();
                    }
                    Toast.makeText(DeleteAccountReasonActivity.this, deleteAccountResponse.getResponseText(), Toast.LENGTH_SHORT).show();
                    tokenManager.clear();
                    Intent intent = new Intent(DeleteAccountReasonActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finishAffinity();

                }
            }

            @Override
            public void onError(String msg) {
                if (loader != null && loader.isShowing()) {
                    loader.dismiss();
                }
                Toast.makeText(DeleteAccountReasonActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


}