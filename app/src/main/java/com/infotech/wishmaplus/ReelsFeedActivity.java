package com.infotech.wishmaplus;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class ReelsFeedActivity extends AppCompatActivity {

    private RecyclerView reelsRecycler;
    private ReelsFeedAdapter adapter;
    private List<ReelModel> reelList = new ArrayList<>();

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

        reelsRecycler = findViewById(R.id.reelsRecycler);

        // Vertical pager snap
        androidx.recyclerview.widget.PagerSnapHelper snapHelper =
                new androidx.recyclerview.widget.PagerSnapHelper();
        snapHelper.attachToRecyclerView(reelsRecycler);

        reelsRecycler.setLayoutManager(
                new androidx.recyclerview.widget.LinearLayoutManager(this));

        loadDummyReels(); // Replace with API call

        adapter = new ReelsFeedAdapter(this, reelList);
        reelsRecycler.setAdapter(adapter);

        // Auto-play visible reel on scroll
        reelsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView rv, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    androidx.recyclerview.widget.LinearLayoutManager lm =
                            (androidx.recyclerview.widget.LinearLayoutManager)
                                    rv.getLayoutManager();
                    if (lm == null) return;
                    int visible = lm.findFirstCompletelyVisibleItemPosition();
                    if (visible != RecyclerView.NO_ID) {
                        adapter.playPosition(visible);
                    }
                }
            }
        });
    }

    private void loadDummyReels() {
        // Replace with real API data
        reelList.add(new ReelModel(
                "1", "Rahul Sharma", "https://randomuser.me/api/portraits/men/1.jpg",
                null, // videoPath — use real path
                "Sunset vibes 🌅 #reels #viral", "Mumbai",
                145000, 3200, 890, false, false
        ));
        reelList.add(new ReelModel(
                "2", "Priya Singh", "https://randomuser.me/api/portraits/women/2.jpg",
                null,
                "Dancing in the rain 🌧️ #dance #reels",
                "Delhi", 89000, 5600, 1200, true, false
        ));
        reelList.add(new ReelModel(
                "3", "Arjun Kapoor", "https://randomuser.me/api/portraits/men/3.jpg",
                null,
                "Morning workout motivation 💪 #fitness #gym", "Bangalore",
                234000, 12000, 2300, false, true
        ));
    }

    @Override protected void onPause() {
        super.onPause();
        adapter.pauseAll();
    }

    @Override protected void onResume() {
        super.onResume();
        adapter.resumeCurrent();
    }
}
