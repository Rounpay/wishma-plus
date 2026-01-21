package com.infotech.wishmaplus.Activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Adapter.ComplaintAdapter;
import com.infotech.wishmaplus.Api.Response.ComplaintResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.UtilMethods;

public class ComplaintList extends AppCompatActivity {
    RecyclerView rvComplaints;
    ComplaintAdapter adapter;
    private CustomLoader loader;
    View noDataLayout, add_button;
    private ActivityResultLauncher<Intent> addComplaintLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_complaint_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        add_button = findViewById(R.id.add_button);
        noDataLayout = findViewById(R.id.noDataLayout);
        rvComplaints = findViewById(R.id.rvComplaints);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        addComplaintLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                getMyComplaint();
            }
        });
        add_button.setOnClickListener(view -> {
            Intent intent = new Intent(ComplaintList.this, CreatorSupportActivity.class);
            addComplaintLauncher.launch(intent);
        });
        getMyComplaint();


    }

    public void getMyComplaint() {
        loader.show();
        UtilMethods.INSTANCE.getMyComplaint(new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                ComplaintResponse complaintResponse = (ComplaintResponse) object;
                if (complaintResponse.getStatusCode() == 1) {
                    if (!complaintResponse.getResult().isEmpty()) {
                        rvComplaints.setVisibility(VISIBLE);
                        noDataLayout.setVisibility(GONE);
                        adapter = new ComplaintAdapter(ComplaintList.this, complaintResponse.getResult());
                        rvComplaints.setAdapter(adapter);
                    } else {
                        rvComplaints.setVisibility(GONE);
                        noDataLayout.setVisibility(VISIBLE);
                    }
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