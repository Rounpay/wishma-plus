package com.infotech.wishmaplus.Activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Adapter.BoostPostsAdapter;
import com.infotech.wishmaplus.Api.Response.BoostedPostStatusChangeResponse;
import com.infotech.wishmaplus.Api.Response.PostItem;
import com.infotech.wishmaplus.Api.Response.PostsResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;

public class BoostContent extends AppCompatActivity {
    RecyclerView recyclerView;

    View noDataLayout;
    BoostPostsAdapter adapter;
    private CustomLoader loader;
    private PreferencesManager tokenManager;
    PostsResponse postsResponse = new PostsResponse();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_boost_content);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        noDataLayout = findViewById(R.id.noDataLayout);
        tokenManager = new PreferencesManager(this,1);
        recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        boostContentList();

    }
    public void boostContentList(){
        UserDetailResponse userDetailResponse = UtilMethods.INSTANCE.getUserDetailResponse(tokenManager);
        String pageId = userDetailResponse.isSelfProfile()?"":userDetailResponse.getUserId();
        loader.show();
        UtilMethods.INSTANCE.getContentToBoost(pageId,this, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                postsResponse =(PostsResponse) object;
                if(postsResponse.getStatusCode()==1){

                    adapter = new BoostPostsAdapter(BoostContent.this, postsResponse.getResult(), new BoostPostsAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(PostItem user, int pos) {
                            Intent intent = new Intent(BoostContent.this, CreateNewAd.class);
                            intent.putExtra("postId", user.getPostId());
                            startActivity(intent);

                        }

                        @Override
                        public void onMoreClicked(View anchor, PostItem user, int pos) {

                        }

                        @Override
                        public void onBtnStopClicked(PostItem user, int pos) {
                            showConfirmationDialog(user,3,BoostContent.this);

                        }

                        @Override
                        public void onBtnRestartClicked(PostItem user, int pos) {
                            showConfirmationDialog(user,2,BoostContent.this);

                        }
                    });
                    recyclerView.setAdapter(adapter);


                }
                updateEmptyView();


            }

            @Override
            public void onError(String msg) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }

            }
        },0,0);
    }
    private void showConfirmationDialog(PostItem user,int status,Activity context) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to continue?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> updateBoostStatus(user.getBoostId(),status,context))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void updateBoostStatus(int boostId, int Status, Activity context){
        loader.show();
        UtilMethods.INSTANCE.updateBoostStatus(boostId, Status, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                BoostedPostStatusChangeResponse boostedPostStatusChangeResponse =(BoostedPostStatusChangeResponse) object;
                if(boostedPostStatusChangeResponse.getStatusCode()==1){
                    boostContentList();
                    Toast.makeText(context, boostedPostStatusChangeResponse.getResponseText(), Toast.LENGTH_SHORT).show();


                }else{
                    UtilMethods.INSTANCE.Error(context, boostedPostStatusChangeResponse.getResponseText());
                }


            }

            @Override
            public void onError(String msg) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                UtilMethods.INSTANCE.Error(context, msg);

            }
        });
    }

    private void updateEmptyView() {
        if (postsResponse.getResult().isEmpty()) {
            recyclerView.setVisibility(GONE);
            noDataLayout.setVisibility(VISIBLE);
        } else {
            recyclerView.setVisibility(VISIBLE);
            noDataLayout.setVisibility(GONE);
        }
    }
}