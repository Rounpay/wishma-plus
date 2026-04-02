package com.infotech.wishmaplus;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;


public class ReelsFeedActivity extends AppCompatActivity {

    private RecyclerView reelsRecycler;
    private ReelsFeedAdapter adapter;
    private List<ReelModel> reelList = new ArrayList<>();
    Spinner spinnerSort;
    private CustomLoader loader;
    private int pageNumber = 1;
    private int pageSize = 10;

    private boolean isLoading = false;
    private boolean isLastPage = false;

    private String currentSort = "Latest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full-screen immersive
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat ctrl = WindowCompat.getInsetsController(
                getWindow(), getWindow().getDecorView());
        ctrl.hide(WindowInsetsCompat.Type.systemBars());
        ctrl.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        setContentView(R.layout.activity_reels_feed);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);

        spinnerSort = findViewById(R.id.spinnerSort);
        reelsRecycler = findViewById(R.id.reelsRecycler);

        // Vertical pager snap
        androidx.recyclerview.widget.PagerSnapHelper snapHelper =
                new androidx.recyclerview.widget.PagerSnapHelper();
        snapHelper.attachToRecyclerView(reelsRecycler);

        reelsRecycler.setLayoutManager(
                new androidx.recyclerview.widget.LinearLayoutManager(this));

        loadReelsFromApi("Latest");
        setupSpinner();
        adapter = new ReelsFeedAdapter(this, reelList);
        reelsRecycler.setAdapter(adapter);

        // Auto-play visible reel on scroll
        reelsRecycler.addOnScrollListener(
                new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrolled(
                            @NonNull RecyclerView rv,
                            int dx,
                            int dy) {

                        LinearLayoutManager lm =
                                (LinearLayoutManager)
                                        rv.getLayoutManager();

                        if (lm == null) return;

                        int totalItemCount =
                                lm.getItemCount();

                        int lastVisibleItem =
                                lm.findLastVisibleItemPosition();

                        // Load next page
                        if (!isLoading &&
                                !isLastPage &&
                                lastVisibleItem >= totalItemCount - 2) {

                            loadReelsFromApi(currentSort);
                        }
                    }
                });
    }

    private void setupSpinner() {

        String[] options = {
                "Latest",
                "Popular",
                "Trending"
        };

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        options);

        spinnerSort.setAdapter(spinnerAdapter);

        spinnerSort.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {
                        pageNumber = 1;
                        isLastPage = false;
                        if (position == 0)
                            loadReelsFromApi("Latest");

                        if (position == 1)
                            loadReelsFromApi("Popular");

                        if (position == 2)
                            loadReelsFromApi("Trending");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    private void loadReelsFromApi(String selectedString) {

        if (isLoading || isLastPage)
            return;

        isLoading = true;

        UtilMethods.INSTANCE.getReels(
                loader,
                pageNumber,
                pageSize,
                selectedString,
                new UtilMethods.ApiCallBackMulti() {
                    @Override
                    public void onSuccess(Object response) {

                        GetReelResponse data =
                                (GetReelResponse) response;
                        runOnUiThread(() -> {
                            isLoading = false;

                            if (pageNumber == 1)
                                reelList.clear();

                            if (data.result != null) {

                                reelList.addAll(data.result);

                                if (data.result.size() < pageSize) {
                                    isLastPage = true;
                                }
                                adapter.notifyDataSetChanged();
                                // autoplay first reel
                                if (pageNumber == 1) {

                                    reelsRecycler.post(() ->
                                            adapter.playPosition(0));
                                }
                                pageNumber++; // next page
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            isLoading = false;
                            Toast.makeText(
                                    ReelsFeedActivity.this,
                                    error,
                                    Toast.LENGTH_SHORT
                            ).show();
                        });
                    }
                });
    }


    @Override
    protected void onPause() {
        super.onPause();
        adapter.pauseAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.resumeCurrent();
    }
}
