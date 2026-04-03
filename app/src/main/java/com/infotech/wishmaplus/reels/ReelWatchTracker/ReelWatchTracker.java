package com.infotech.wishmaplus.reels.ReelWatchTracker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.TrackReelViewRequest;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReelWatchTracker {
    private static volatile ReelWatchTracker instance;
    public static ReelWatchTracker getInstance() {
        if (instance == null) {
            synchronized (ReelWatchTracker.class) {
                if (instance == null)
                    instance = new ReelWatchTracker();
            }
        }
        return instance;
    }
    private Context appContext;
    private int activeReelId = -1;
    private long startTimeMs = 0;
    private long accumulatedMs = 0;
    private boolean isPaused = false;
    private boolean isDestroyed = false;
    private PreferencesManager tokenManager;
    // Pending: reelId → total ms
    private final Map<Integer, Long> pending =
            Collections.synchronizedMap(new LinkedHashMap<>());

    private final Handler batchHandler = new Handler(Looper.getMainLooper());
    private static final long BATCH_MS = 10_000L; // 10 sec

    private final Runnable batchRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                flushBatch(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!isDestroyed)
                batchHandler.postDelayed(this, BATCH_MS);
        }
    };

    private ReelWatchTracker() {
    }

    public void init(Context ctx) {
        try {
            appContext = ctx.getApplicationContext();
            tokenManager = new PreferencesManager(appContext,1);
            isDestroyed = false;
            batchHandler.removeCallbacks(batchRunnable);
            batchHandler.postDelayed(batchRunnable, BATCH_MS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Called when reel becomes visible + starts playing ────────────────
    public void onReelStarted(int reelId) {
        try {
            stopCurrent();
            activeReelId = reelId;
            startTimeMs = System.currentTimeMillis();
            accumulatedMs = 0;
            isPaused = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Called on user tap pause / scroll away / app background ──────────
    public void onReelPaused() {
        try {
            if (activeReelId == -1 || isPaused) return;
            isPaused = true;
            accumulatedMs += System.currentTimeMillis() - startTimeMs;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onReelResumed() {
        try {
            if (activeReelId == -1 || !isPaused) return;
            isPaused = false;
            startTimeMs = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onReelScrolledAway() {
        try {
            stopCurrent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityPaused() {
        onReelPaused();
    }

    public void onActivityResumed() {
        onReelResumed();
    }

    public void onActivityDestroyed() {
        try {
            isDestroyed = true;
            batchHandler.removeCallbacks(batchRunnable);
            stopCurrent();
            flushBatch(true); // immediate flush
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void forceFlush() {
        try {
            stopCurrent();
            flushBatch(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Stop current session → add to pending ────────────────────────────
    private void stopCurrent() {
        if (activeReelId == -1) return;
        try {
            if (!isPaused) {
                accumulatedMs += System.currentTimeMillis() - startTimeMs;
            }
            if (accumulatedMs >= 1000) {
                long existing = pending.containsKey(activeReelId)
                        ? pending.get(activeReelId) : 0L;
                pending.put(activeReelId, existing + accumulatedMs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            activeReelId = -1;
            accumulatedMs = 0;
            isPaused = false;
        }
    }

    // ── Send batch as array ───────────────────────────────────────────────
    private void flushBatch(boolean immediate) {
        if (pending.isEmpty() || appContext == null) return;
        try {
            // Build list
            Map<Integer, Long> snap;
            synchronized (pending) {
                snap = new LinkedHashMap<>(pending);
                pending.clear();
            }

            List<TrackReelViewRequest> list = new ArrayList<>();
            for (Map.Entry<Integer, Long> e : snap.entrySet()) {
                int sec = Math.max(1, (int) (e.getValue() / 1000));
                list.add(new TrackReelViewRequest(e.getKey(), sec));
            }

            if (list.isEmpty()) return;

            // Send array to API
            EndPointInterface api = ApiClient.getClient()
                    .create(EndPointInterface.class);
            Call<BasicResponse> call = api.trackReelView("Bearer " + tokenManager.getAccessToken(),list);

            call.enqueue(new Callback<BasicResponse>() {
                @Override
                public void onResponse(
                        @NonNull Call<BasicResponse> call,
                        @NonNull Response<BasicResponse> response) {
                }

                @Override
                public void onFailure(
                        @NonNull Call<BasicResponse> call,
                        @NonNull Throwable t) {
                    try {
                        for (TrackReelViewRequest r : list) {
                            long existing = pending.containsKey(r.getReelId())
                                    ? pending.get(r.getReelId()) : 0L;
                            pending.put(r.getReelId(),
                                    existing + (long) r.getWatchDurationSec()
                                            * 1000);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}