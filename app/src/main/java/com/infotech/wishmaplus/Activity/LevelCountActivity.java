package com.infotech.wishmaplus.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Adapter.LevelCountAdapter;
import com.infotech.wishmaplus.Api.Object.LevelCountResult;
import com.infotech.wishmaplus.Api.Response.BasicListResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LevelCountActivity extends AppCompatActivity {


    private PreferencesManager tokenManager;
    private CustomLoader loader;

    private RecyclerView recyclerView;
    private ArrayList<LevelCountResult> levelList = new ArrayList<>();
    private LevelCountAdapter adapter;
    private long mLastClickTime;
    //private String hashPayUSDkPro;
    private int selectedPackageId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level_count);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.clMain), (v, insets) -> {
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
        getCount();

    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LevelCountAdapter(LevelCountActivity.this, levelList/*, new PackageAdapter.OnClick() {
            @Override
            public void onClick(PackageResult value) {
               selectedPackageId= value.getPackageID();
                //upgradePackage(null,null,null,null);
            }
        }*/);
        recyclerView.setAdapter(adapter);
        findViewById(R.id.back_button).setOnClickListener(view -> finish());


    }


    private void getCount() {
        try {
            loader.show();
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicListResponse<LevelCountResult>> call = git.getLevelWiseCount("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<BasicListResponse<LevelCountResult>>() {
                @Override
                public void onResponse(@NonNull Call<BasicListResponse<LevelCountResult>> call, @NonNull Response<BasicListResponse<LevelCountResult>> response) {
                    if (loader != null) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                    }
                    try {
                        BasicListResponse<LevelCountResult> packageResponse = response.body();
                        if (packageResponse != null) {
                            if (packageResponse.getStatusCode() == 1) {
                                if (packageResponse.getResult() != null && packageResponse.getResult().size() > 0) {
                                    levelList.clear();
                                    levelList.addAll(packageResponse.getResult());
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                UtilMethods.INSTANCE.Error(LevelCountActivity.this, packageResponse.getResponseText());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilMethods.INSTANCE.Error(LevelCountActivity.this, e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BasicListResponse<LevelCountResult>> call, @NonNull Throwable t) {
                    try {
                        if (loader != null) {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }
                        }
                        UtilMethods.INSTANCE.apiFailureError(LevelCountActivity.this, t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(LevelCountActivity.this, ise.getMessage());
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
            UtilMethods.INSTANCE.Error(LevelCountActivity.this, e.getMessage());
            if (loader != null) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }
            }
        }
    }


}