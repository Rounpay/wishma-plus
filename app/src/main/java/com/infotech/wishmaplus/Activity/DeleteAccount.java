package com.infotech.wishmaplus.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Adapter.UserPagesAdapter;
import com.infotech.wishmaplus.Adapter.UserProfilesAdapter;
import com.infotech.wishmaplus.Api.Response.PageData;
import com.infotech.wishmaplus.Api.Response.PagesResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class DeleteAccount extends AppCompatActivity {
    private PreferencesManager tokenManager;
    List<PageData> list = new ArrayList<>();
    UserProfilesAdapter adapter;
    RecyclerView rvProfiles;

    private CustomLoader loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        rvProfiles = findViewById(R.id.rvProfiles);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        tokenManager = new PreferencesManager(this,1);
        setRecyclerView();
        getPagesList();

        findViewById(R.id.back_button).setOnClickListener(view -> finish());
//        findViewById(R.id.cardAccount1).setOnClickListener(view -> {
//            startActivity(new Intent(this, DeactivateOrDeleteAccount.class));
//        });
    }
    private void getPagesList() {
        loader.show();

        UtilMethods.INSTANCE.getPagesResponse(this, new UtilMethods.ApiCallBackMulti() {

            @Override
            public void onSuccess(Object object) {
                if (loader != null && loader.isShowing()) loader.dismiss();

                list.clear();
                PagesResponse pagesResponse = (PagesResponse) object;

                if (!pagesResponse.getResult().isEmpty()) {
                    list.addAll(pagesResponse.getResult());
                    adapter.notifyDataSetChanged();  // <-- adapter already initialized
                }
            }

            @Override
            public void onError(String msg) {
                if (loader != null && loader.isShowing()) loader.dismiss();
            }
        });
    }
    private void setRecyclerView(){
        adapter = new UserProfilesAdapter(this,list,new UserProfilesAdapter.OnItemClickListener(){


            @Override
            public void onItemClick(PageData user, int pos) {
                Intent intent = new Intent(DeleteAccount.this, DeactivateOrDeleteAccount.class);
                intent.putExtra("pageId", user.getPageId());
                intent.putExtra("accountType", user.isProfile());
                startActivity(intent);

            }

            @Override
            public void onMoreClicked(View anchor, PageData user, int pos) {

            }
        });
        rvProfiles.setLayoutManager(new LinearLayoutManager(this));
        rvProfiles.setAdapter(adapter);
    }
}