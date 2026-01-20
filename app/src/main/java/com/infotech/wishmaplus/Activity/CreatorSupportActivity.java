package com.infotech.wishmaplus.Activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.infotech.wishmaplus.Adapter.BillingDetailsAdapter;
import com.infotech.wishmaplus.Adapter.ComplaintTypeAdapter;
import com.infotech.wishmaplus.Api.Request.ComplaintRequest;
import com.infotech.wishmaplus.Api.Request.UpdateGroupMemberRequest;
import com.infotech.wishmaplus.Api.Response.BoostBillingResponse;
import com.infotech.wishmaplus.Api.Response.ComplaintSubmitResponse;
import com.infotech.wishmaplus.Api.Response.ComplaintType;
import com.infotech.wishmaplus.Api.Response.GroupMembersUpdateResponse;
import com.infotech.wishmaplus.Api.Response.SupportCategoryResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;

public class CreatorSupportActivity extends AppCompatActivity {
    com.google.android.material.button.MaterialButton btcSubmit;
    RelativeLayout spinnerPrivacy;
    private CustomLoader loader;
    TextView tvSelected;
    private EditText complaintDetails;
    private int selectedCategoryId = -1;
    SupportCategoryResponse supportCategoryResponse = new SupportCategoryResponse();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_creator_support);
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        btcSubmit = findViewById(R.id.btcSubmit);
        spinnerPrivacy = findViewById(R.id.spinnerPrivacy);
        tvSelected = findViewById(R.id.tvSelected);
        complaintDetails = findViewById(R.id.etGroupName);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        btcSubmit.setText("Submit");
        btcSubmit.setBackgroundResource(R.drawable.bg_blue_rounded);
        btcSubmit.setTextColor(ContextCompat.getColor(this, R.color.color_white));
        btcSubmit.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.main_blue_color));
        spinnerPrivacy.setOnClickListener(view -> openComplaintTypeBottomSheet(this));

        btcSubmit.setOnClickListener(v -> validateAndSubmit());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getComplaintCategory();
    }
    private void validateAndSubmit() {

        String complaintType = tvSelected.getText().toString().trim();
        String complaintText = complaintDetails.getText().toString().trim();

        // 🔴 Complaint type validation
        if (complaintType.equalsIgnoreCase("Select") || selectedCategoryId == -1) {
            Toast.makeText(this, "Please select complaint type", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔴 Complaint text validation
        if (complaintText.isEmpty()) {
            complaintDetails.setError("Please enter complaint");
            complaintDetails.requestFocus();
            return;
        }

        if (complaintText.length() < 10) {
            complaintDetails.setError("Complaint must be at least 10 characters");
            complaintDetails.requestFocus();
            return;
        }

        // ✅ Valid data
        submitComplaint(selectedCategoryId, complaintText);
    }


    public void getComplaintCategory(){
        loader.show();
        UtilMethods.INSTANCE.getComplaintCategory( new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                supportCategoryResponse =(SupportCategoryResponse) object;
                if(supportCategoryResponse.getStatusCode()==1){

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
    public void submitComplaint(int selectedCategoryId, String complaintText){
        ComplaintRequest request = new ComplaintRequest( );
        request.setDescription(complaintText);
        request.setCategoryId(selectedCategoryId);
        loader.show();
        UtilMethods.INSTANCE.submitComplaint(request,new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }

                ComplaintSubmitResponse complaintSubmitResponse=(ComplaintSubmitResponse) object;
                if(complaintSubmitResponse.getStatusCode()==1){
                    Toast.makeText(CreatorSupportActivity.this, complaintSubmitResponse.getResponseText(), Toast.LENGTH_SHORT).show();
                    finish();
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

    public void openComplaintTypeBottomSheet(Activity context) {

        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.DialogStyle);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_complaint_type, null);

        RecyclerView recyclerView = view.findViewById(R.id.rvComplaintType);
        TextView tvDone = view.findViewById(R.id.tvDone);

//        List<ComplaintType> list = new ArrayList<>();
//        list.add(new ComplaintType("Spam", "Unwanted or repeated messages"));
//        list.add(new ComplaintType("Abuse", "Harassment or hate speech"));
//        list.add(new ComplaintType("Fraud", "Scam or misleading content"));

        ComplaintTypeAdapter adapter = new ComplaintTypeAdapter(supportCategoryResponse.getResult(),(item, position) -> {
            tvSelected.setText(item.getCategoryName());
            selectedCategoryId = item.getCategoryId();
            dialog.dismiss();

        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        tvDone.setOnClickListener(v -> {
//            ComplaintType selected = adapter.getSelectedItem();
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }

}