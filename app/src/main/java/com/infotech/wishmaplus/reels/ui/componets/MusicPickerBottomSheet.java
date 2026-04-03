package com.infotech.wishmaplus.reels.ui.componets;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.infotech.wishmaplus.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicPickerBottomSheet extends BottomSheetDialogFragment {

    // ── Callback ──────────────────────────────────────────────────────────────
    public interface OnMusicSelected {
        void onSelected(String localPath, long startMs, long endMs, String title);
    }

    // ── Track model ───────────────────────────────────────────────────────────
    public static class MusicTrack {
        public String title, artist, previewUrl, duration;
        public boolean isLocal; // offline track flag

        public MusicTrack(String title, String artist,
                          String previewUrl, String dur, boolean isLocal) {
            this.title = title;
            this.artist = artist;
            this.previewUrl = previewUrl;
            this.duration = dur;
            this.isLocal = isLocal;
        }
    }

    // ── Jamendo API ───────────────────────────────────────────────────────────
    private static final String JAMENDO_CLIENT_ID = "00dd056d";
    private static final String JAMENDO_URL =
            "https://api.jamendo.com/v3.0/tracks/?client_id=" + JAMENDO_CLIENT_ID +
                    "&format=json&limit=20&search=%s&include=musicinfo&audioformat=mp31";

    // ── Views ─────────────────────────────────────────────────────────────────
    private TabLayout tabLayout;
    private EditText searchInput;
    private RecyclerView trackList;
    private LinearLayout trimPanel;
    private SeekBar seekTrimStart, seekTrimEnd;
    private TextView tvTrimStart, tvTrimEnd, tvSelectedTrack;
    private ImageButton btnPlayPreview;
    private Button btnApplyMusic;
    private ProgressBar searchProgress;
    private View emptyView;

    // ── State ─────────────────────────────────────────────────────────────────
    private OnMusicSelected callback;
    private MusicTrackAdapter adapter;
    private MusicTrack selectedTrack;
    private String downloadedPath;
    private long trimStartMs = 0, trimEndMs = 30000;
    private MediaPlayer previewPlayer;
    private boolean isOnlineTab = true;

    // ── Threading ─────────────────────────────────────────────────────────────
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ExecutorService executor;

    // ── Seekbar update runnable ───────────────────────────────────────────────
    private Runnable seekUpdateRunnable;

    public static MusicPickerBottomSheet newInstance(OnMusicSelected cb) {
        MusicPickerBottomSheet sheet = new MusicPickerBottomSheet();
        sheet.callback = cb;
        return sheet;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LIFECYCLE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executor = Executors.newCachedThreadPool();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_music_picker, container, false);
        bindViews(v);
        setupTabs();
        setupSearch();
        setupTrimControls();
        setupApplyButton();
        loadOnlineTracks("happy"); // default load
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // ── Cleanup — memory leak prevent karo ───────────────────────────
        handler.removeCallbacksAndMessages(null);
        stopSeekUpdate();
        releasePreviewPlayer();
        if (adapter != null) adapter.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BIND
    // ─────────────────────────────────────────────────────────────────────────

    private void bindViews(View v) {
        tabLayout = v.findViewById(R.id.musicTabLayout);
        searchInput = v.findViewById(R.id.musicSearchInput);
        trackList = v.findViewById(R.id.musicTrackList);
        trimPanel = v.findViewById(R.id.trimPanel);
        seekTrimStart = v.findViewById(R.id.seekTrimStart);
        seekTrimEnd = v.findViewById(R.id.seekTrimEnd);
        tvTrimStart = v.findViewById(R.id.tvTrimStart);
        tvTrimEnd = v.findViewById(R.id.tvTrimEnd);
        tvSelectedTrack = v.findViewById(R.id.tvSelectedTrack);
        btnPlayPreview = v.findViewById(R.id.btnPlayPreview);
        btnApplyMusic = v.findViewById(R.id.btnApplyMusic);
        searchProgress = v.findViewById(R.id.searchProgress);
        emptyView = v.findViewById(R.id.emptyView);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TABS — Online / Offline
    // ─────────────────────────────────────────────────────────────────────────

    private void setupTabs() {
        // XML mein 2 tabs hone chahiye: "Online" aur "Device"
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isOnlineTab = tab.getPosition() == 0;
                resetSelection();
                if (isOnlineTab) {
                    searchInput.setVisibility(View.VISIBLE);
                    String q = searchInput.getText().toString().trim();
                    loadOnlineTracks(q.isEmpty() ? "happy" : q);
                } else {
                    searchInput.setVisibility(View.GONE);
                    loadOfflineTracks();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEARCH
    // ─────────────────────────────────────────────────────────────────────────

    private void setupSearch() {
        // RecyclerView setup
        adapter = new MusicTrackAdapter(new ArrayList<>(), this::onTrackSelected);
        trackList.setLayoutManager(new LinearLayoutManager(getContext()));
        trackList.setAdapter(adapter);
        trackList.setItemAnimator(null); // flicker prevent

        // Search input
        searchInput.addTextChangedListener(new TextWatcher() {
            private Runnable searchRunnable;

            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (searchRunnable != null) handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    String q = s.toString().trim();
                    if (!q.isEmpty()) loadOnlineTracks(q);
                };
                handler.postDelayed(searchRunnable, 500); // debounce 500ms
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ONLINE TRACKS — Jamendo API
    // ─────────────────────────────────────────────────────────────────────────

    private void loadOnlineTracks(String query) {
        if (!isAdded()) return;
        showProgress(true);

        executor.execute(() -> {
            try {
                String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name());
                String urlStr = String.format(JAMENDO_URL, encoded);

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                InputStream is = conn.getInputStream();
                byte[] data = readAllBytes(is);
                String json = new String(data, StandardCharsets.UTF_8);
                conn.disconnect();

                JSONObject obj = new JSONObject(json);
                JSONArray results = obj.getJSONArray("results");
                List<MusicTrack> tracks = new ArrayList<>();

                for (int i = 0; i < results.length(); i++) {
                    JSONObject hit = results.getJSONObject(i);
                    String title = hit.optString("name", "Unknown");
                    String artist = hit.optString("artist_name", "Artist");
                    String audioUrl = hit.optString("audio", "");
                    int dur = hit.optInt("duration", 0);
                    @SuppressLint("DefaultLocale")
                    String durStr = String.format("%d:%02d", dur / 60, dur % 60);

                    if (!audioUrl.isEmpty()) {
                        tracks.add(new MusicTrack(title, artist, audioUrl, durStr, false));
                    }
                }

                handler.post(() -> {
                    if (!isAdded()) return;
                    showProgress(false);
                    showEmptyView(tracks.isEmpty());
                    adapter.updateData(tracks);
                });

            } catch (Exception e) {
                handler.post(() -> {
                    if (!isAdded()) return;
                    showProgress(false);
                    // ── Online fail → offline suggest karo ───────────────
                    showOfflineSuggestion();
                });
            }
        });
    }

    // ── Online fail hone par offline suggest karo ─────────────────────────────
    private void showOfflineSuggestion() {
        if (!isAdded()) return;
        // Offline tracks automatically load karo
        loadOfflineTracks();
        Toast.makeText(getContext(),
                "Online music unavailable — showing device music",
                Toast.LENGTH_SHORT).show();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // OFFLINE TRACKS — Device storage se
    // ─────────────────────────────────────────────────────────────────────────

    private void loadOfflineTracks() {
        if (!isAdded()) return;
        showProgress(true);

        executor.execute(() -> {
            List<MusicTrack> tracks = new ArrayList<>();
            try {
                ContentResolver cr = requireContext().getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] projection = {
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA
                };
                String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
                String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

                try (Cursor cursor = cr.query(uri, projection,
                        selection, null, sortOrder)) {
                    if (cursor != null) {
                        int idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                        int titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                        int artCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                        int durCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                        int dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

                        while (cursor.moveToNext()) {
                            long id = cursor.getLong(idCol);
                            String title = cursor.getString(titleCol);
                            String artist = cursor.getString(artCol);
                            long durMs = cursor.getLong(durCol);
                            String path = cursor.getString(dataCol);

                            if (path == null || path.isEmpty()) {
                                // Android 10+ ke liye content URI use karo
                                Uri contentUri = ContentUris.withAppendedId(
                                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                                path = contentUri.toString();
                            }

                            @SuppressLint("DefaultLocale")
                            String durStr = String.format("%d:%02d",
                                    (durMs / 1000) / 60, (durMs / 1000) % 60);

                            tracks.add(new MusicTrack(
                                    title != null ? title : "Unknown",
                                    artist != null ? artist : "Unknown Artist",
                                    path, durStr, true));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                if (!isAdded()) return;
                showProgress(false);
                showEmptyView(tracks.isEmpty());
                adapter.updateData(tracks);
            });
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TRACK SELECTED
    // ─────────────────────────────────────────────────────────────────────────

    private void onTrackSelected(MusicTrack track) {
        selectedTrack = track;
        downloadedPath = null; // reset

        tvSelectedTrack.setText(track.title + " — " + track.artist);
        trimPanel.setVisibility(View.VISIBLE);
        btnApplyMusic.setVisibility(View.VISIBLE);
        btnApplyMusic.setEnabled(false);

        resetTrimUI();
        releasePreviewPlayer();

        if (track.isLocal) {
            // Local file — seedha use karo, download nahi
            downloadedPath = track.previewUrl;
            btnApplyMusic.setEnabled(true);
            initPreviewPlayer(downloadedPath);
        } else {
            // Online — download karo cache mein
            downloadTrack(track);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DOWNLOAD ONLINE TRACK
    // ─────────────────────────────────────────────────────────────────────────

    private void downloadTrack(MusicTrack track) {
        if (!isAdded()) return;
        showProgress(true);
        btnApplyMusic.setEnabled(false);

        executor.execute(() -> {
            try {
                File out = new File(requireContext().getCacheDir(),
                        "music_" + System.currentTimeMillis() + ".mp3");

                URL url = new URL(track.previewUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
                conn.connect();

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new IOException("HTTP " + conn.getResponseCode());
                }

                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(out);
                byte[] buf = new byte[8192];
                int n;
                while ((n = is.read(buf)) != -1) fos.write(buf, 0, n);
                fos.flush();
                fos.close();
                conn.disconnect();

                downloadedPath = out.getAbsolutePath();

                handler.post(() -> {
                    if (!isAdded()) return;
                    showProgress(false);
                    btnApplyMusic.setEnabled(true);
                    initPreviewPlayer(downloadedPath);
                });

            } catch (Exception e) {
                handler.post(() -> {
                    if (!isAdded()) return;
                    showProgress(false);
                    btnApplyMusic.setEnabled(false);
                    Toast.makeText(getContext(),
                            "Download failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TRIM CONTROLS
    // ─────────────────────────────────────────────────────────────────────────

    private void setupTrimControls() {
        seekTrimStart.setMax(1000);
        seekTrimEnd.setMax(1000);
        seekTrimEnd.setProgress(1000);

        seekTrimStart.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int p, boolean fromUser) {
                if (!fromUser) return;
                long totalMs = getTotalDuration();
                trimStartMs = (long) (p / 1000f * totalMs);

                // Start > End nahi ho sakta
                if (trimStartMs >= trimEndMs) {
                    trimStartMs = Math.max(0, trimEndMs - 1000);
                    sb.setProgress((int) (trimStartMs / (float) totalMs * 1000));
                }
                tvTrimStart.setText(formatMs(trimStartMs));
                if (previewPlayer != null) {
                    try {
                        previewPlayer.seekTo((int) trimStartMs);
                    } catch (Exception ignored) {
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb) {
                stopSeekUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar sb) {
            }
        });

        seekTrimEnd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int p, boolean fromUser) {
                if (!fromUser) return;
                long totalMs = getTotalDuration();
                trimEndMs = (long) (p / 1000f * totalMs);

                // End > Start hona chahiye
                if (trimEndMs <= trimStartMs) {
                    trimEndMs = Math.min(totalMs, trimStartMs + 1000);
                    sb.setProgress((int) (trimEndMs / (float) totalMs * 1000));
                }
                tvTrimEnd.setText(formatMs(trimEndMs));
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar sb) {
            }
        });

        btnPlayPreview.setOnClickListener(v -> togglePreview());
    }

    private void setupApplyButton() {
        btnApplyMusic.setOnClickListener(v -> {
            if (downloadedPath == null) {
                Toast.makeText(getContext(),
                        "Please wait, music loading...", Toast.LENGTH_SHORT).show();
                return;
            }
            if (callback != null) {
                callback.onSelected(
                        downloadedPath,
                        trimStartMs,
                        trimEndMs,
                        selectedTrack != null ? selectedTrack.title : "");
            }
            dismiss();
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PREVIEW PLAYER
    // ─────────────────────────────────────────────────────────────────────────

    private void initPreviewPlayer(String path) {
        releasePreviewPlayer();
        try {
            previewPlayer = new MediaPlayer();

            // Local content URI ya file path handle karo
            if (path.startsWith("content://")) {
                previewPlayer.setDataSource(requireContext(),
                        Uri.parse(path));
            } else {
                previewPlayer.setDataSource(path);
            }

            previewPlayer.setOnPreparedListener(mp -> {
                // Trim seekbars ko actual duration se set karo
                long totalMs = mp.getDuration();
                trimEndMs = Math.min(trimEndMs, totalMs);
                seekTrimEnd.setProgress(1000);
                updateTrimLabels(totalMs);
                startSeekUpdate();
            });

            previewPlayer.setOnCompletionListener(mp -> {
                btnPlayPreview.setImageResource(R.drawable.ic_play_circle_outline);
                stopSeekUpdate();
            });

            previewPlayer.setOnErrorListener((mp, what, extra) -> {
                btnPlayPreview.setImageResource(R.drawable.ic_play_circle_outline);
                stopSeekUpdate();
                releasePreviewPlayer();
                return true;
            });

            previewPlayer.prepareAsync();

        } catch (Exception e) {
            e.printStackTrace();
            releasePreviewPlayer();
        }
    }

    private void togglePreview() {
        if (previewPlayer == null) return;
        try {
            if (previewPlayer.isPlaying()) {
                previewPlayer.pause();
                stopSeekUpdate();
                btnPlayPreview.setImageResource(R.drawable.ic_play_circle_outline);
            } else {
                previewPlayer.seekTo((int) trimStartMs);
                previewPlayer.start();
                startSeekUpdate();
                btnPlayPreview.setImageResource(R.drawable.outline_pause_circle_24);

                // TrimEnd pe auto stop
                scheduleAutoStop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── TrimEnd tak pahunche toh auto stop ────────────────────────────────────
    private void scheduleAutoStop() {
        handler.removeCallbacks(autoStopRunnable);
        long delay = trimEndMs - trimStartMs;
        if (delay > 0) handler.postDelayed(autoStopRunnable, delay);
    }

    private final Runnable autoStopRunnable = () -> {
        if (previewPlayer != null && previewPlayer.isPlaying()) {
            previewPlayer.pause();
            stopSeekUpdate();
            btnPlayPreview.setImageResource(R.drawable.ic_play_circle_outline);
        }
    };

    // ── SeekBar live update ───────────────────────────────────────────────────
    private void startSeekUpdate() {
        stopSeekUpdate();
        seekUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (previewPlayer != null) {
                    try {
                        int curr = previewPlayer.getCurrentPosition();
                        int total = previewPlayer.getDuration();
                        if (total > 0) {
                            // TrimStart seekbar position update (as playhead)
                            seekTrimStart.setProgress((int) (curr / (float) total * 1000));
                            tvTrimStart.setText(formatMs(curr));
                        }
                    } catch (Exception ignored) {
                    }
                }
                handler.postDelayed(this, 200);
            }
        };
        handler.post(seekUpdateRunnable);
    }

    private void stopSeekUpdate() {
        if (seekUpdateRunnable != null) {
            handler.removeCallbacks(seekUpdateRunnable);
            seekUpdateRunnable = null;
        }
        handler.removeCallbacks(autoStopRunnable);
    }

    private void releasePreviewPlayer() {
        stopSeekUpdate();
        if (previewPlayer != null) {
            try {
                if (previewPlayer.isPlaying()) previewPlayer.stop();
                previewPlayer.release();
            } catch (Exception ignored) {
            }
            previewPlayer = null;
        }
        if (btnPlayPreview != null) {
            btnPlayPreview.setImageResource(R.drawable.ic_play_circle_outline);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private long getTotalDuration() {
        if (previewPlayer != null) {
            try {
                return previewPlayer.getDuration();
            } catch (Exception ignored) {
            }
        }
        return 120000; // fallback 2 min
    }

    private void resetSelection() {
        selectedTrack = null;
        downloadedPath = null;
        trimPanel.setVisibility(View.GONE);
        btnApplyMusic.setVisibility(View.GONE);
        releasePreviewPlayer();
    }

    private void resetTrimUI() {
        trimStartMs = 0;
        trimEndMs = 30000;
        seekTrimStart.setProgress(0);
        seekTrimEnd.setProgress(1000);
        tvTrimStart.setText("0:00");
        tvTrimEnd.setText("0:30");
    }

    private void updateTrimLabels(long totalMs) {
        trimEndMs = Math.min(30000, totalMs);
        tvTrimStart.setText("0:00");
        tvTrimEnd.setText(formatMs(trimEndMs));
        seekTrimEnd.setProgress((int) (trimEndMs / (float) totalMs * 1000));
    }

    private void showProgress(boolean show) {
        if (searchProgress != null)
            searchProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showEmptyView(boolean show) {
        if (emptyView != null)
            emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        if (trackList != null)
            trackList.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @SuppressLint("DefaultLocale")
    private String formatMs(long ms) {
        long sec = ms / 1000;
        return String.format("%d:%02d", sec / 60, sec % 60);
    }

    private byte[] readAllBytes(InputStream is) throws IOException {
        byte[] buf = new byte[8192];
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        int n;
        while ((n = is.read(buf)) != -1) baos.write(buf, 0, n);
        return baos.toByteArray();
    }
}