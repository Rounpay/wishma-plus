package com.infotech.wishmaplus;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.infotech.wishmaplus.Activity.MainActivity;
import com.infotech.wishmaplus.Api.Response.MediaModel;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ReelPostActivity extends AppCompatActivity {

    // ── Views ──────────────────────────────────────────────────────────────────
    private ImageView imageView;
    private EditText etCaption, etHashtag;
    private AppCompatButton btnPost;
    private ImageButton btnBack;
    private ProgressBar progressBar;
    private RecyclerView rvHashtagSuggestions;
    private ChipGroup chipGroupHashtags;
    private TextView tvDurationBadge, tvUserName;
    private SwitchCompat switchShareToFeed;

    // ── Data ───────────────────────────────────────────────────────────────────
    private String thumbPath;

    private String renderedVideoPath; // ★ final MP4 from ReelEditorActivity
    private ArrayList<MediaModel> mediaList = new ArrayList<>();
    private final List<String> selectedHashtags = new ArrayList<>();
    private HashtagSuggestionAdapter suggestionAdapter;
    private int durationSeconds = 0;
    private CustomLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reel_post);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();
        loadIntentData();
        setupHashtagInput();
        setupSuggestionRecycler();
        setupButtons();
    }

    // ── Bind ───────────────────────────────────────────────────────────────────
    private void bindViews() {
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        imageView = findViewById(R.id.imageView);
        etCaption = findViewById(R.id.etCaption);
        etHashtag = findViewById(R.id.etHashtag);
        btnPost = findViewById(R.id.btnPost);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
        rvHashtagSuggestions = findViewById(R.id.rvHashtagSuggestions);
        chipGroupHashtags = findViewById(R.id.chipGroupHashtags);
        tvDurationBadge = findViewById(R.id.tvDurationBadge);
        tvUserName = findViewById(R.id.tvUserName);
        switchShareToFeed = findViewById(R.id.switchShareToFeed);
    }

    // ── Intent data ────────────────────────────────────────────────────────────
/*    private void loadIntentData() {
        if(getIntent()!=null){
            thumbPath = getIntent().getStringExtra("thumbnail_path");
            renderedVideoPath  = getIntent().getStringExtra("rendered_video_path");
        }
        if (getIntent().hasExtra("media_list")) {
            Object obj = getIntent().getSerializableExtra("media_list");
            if (obj instanceof ArrayList) {
                mediaList = (ArrayList<MediaModel>) obj;
            }
        }
        // Thumbnail
        if (thumbPath != null) {
            Glide.with(this).load(thumbPath).centerCrop().into(imageView);
        }
        // Duration — auto from MediaModel (ms → seconds)
        if (!mediaList.isEmpty()) {
            durationSeconds = (int) (mediaList.get(0).getDuration() / 1000);
            tvDurationBadge.setText(formatDuration(durationSeconds));
        }
        // Username from your session manager
        // tvUserName.setText(SessionManager.getInstance().getUserName());
    }*/
    private void loadIntentData() {
        thumbPath = getIntent().getStringExtra("thumbnail_path");
        renderedVideoPath = getIntent().getStringExtra("rendered_video_path"); // ★
        if (getIntent().hasExtra("media_list")) {
            Object obj = getIntent().getSerializableExtra("media_list");
            if (obj instanceof ArrayList) {
                mediaList = (ArrayList<MediaModel>) obj;
            }
        }
        // Thumbnail
        if (thumbPath != null && new File(thumbPath).exists()) {
            Glide.with(this).load(thumbPath).centerCrop().into(imageView);
        } else if (!mediaList.isEmpty()) {
            Glide.with(this).load(mediaList.get(0).getPath()).centerCrop().into(imageView);
        }

        // Duration — rendered video se prefer karo
        if (renderedVideoPath != null && new File(renderedVideoPath).exists()) {
            android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
            try {
                mmr.setDataSource(renderedVideoPath);
                String durStr = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
                if (durStr != null) {
                    durationSeconds = (int) (Long.parseLong(durStr) / 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    mmr.release();
                } catch (Exception ignored) {
                }
            }
        } else if (!mediaList.isEmpty()) {
            durationSeconds = (int) (mediaList.get(0).getDuration() / 1000);
        }
        if (durationSeconds > 0) {
            tvDurationBadge.setText(formatDuration(durationSeconds));
        }
    }

    // ── Format seconds → "5:30" ───────────────────────────────────────────────
    private String formatDuration(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%d:%02d", m, s);
    }

    // ── Buttons ────────────────────────────────────────────────────────────────
    private void setupButtons() {
        btnBack.setOnClickListener(v -> onBackPressed());
        btnPost.setOnClickListener(v -> postReel());
    }

    // ── Hashtag input watcher ─────────────────────────────────────────────────
    private void setupHashtagInput() {
        etHashtag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim().replace("#", "");
                if (!query.isEmpty()) {
                    fetchSuggestions(query);
                } else {
                    hideSuggestions();
                }
            }
        });

        etHashtag.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String typed = etHashtag.getText().toString().trim().replace("#", "");
                if (!typed.isEmpty()) {
                    addChip("#" + typed);
                    etHashtag.setText("");
                    hideSuggestions();
                }
                return true;
            }
            return false;
        });
    }

    // ── Suggestions RecyclerView ──────────────────────────────────────────────
    private void setupSuggestionRecycler() {
        // Suggestion tap →
        suggestionAdapter = new HashtagSuggestionAdapter(tag -> {
            addChip("#" + tag);
            etHashtag.setText("");
            hideSuggestions();
        });
        rvHashtagSuggestions.setLayoutManager(new LinearLayoutManager(this));
        rvHashtagSuggestions.setAdapter(suggestionAdapter);
        rvHashtagSuggestions.setNestedScrollingEnabled(false);
    }

    // ── API: fetch suggestions ────────────────────────────────────────────────
    private void fetchSuggestions(String query) {
        UtilMethods.INSTANCE.getHashtagSuggestions(loader, query, 10, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object response) {
                HashtagResponse data = (HashtagResponse) response;
                runOnUiThread(() -> {
                    if (data.result != null && !data.result.isEmpty()) {
                        suggestionAdapter.submitList(data.result);
                        rvHashtagSuggestions.setVisibility(View.VISIBLE);
                    } else {
                        hideSuggestions();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(this::hideSuggestions);
            }

            private void hideSuggestions() {
                rvHashtagSuggestions.setVisibility(View.GONE);
            }
        });
    }

    private void hideSuggestions() {
        rvHashtagSuggestions.setVisibility(View.GONE);
    }

    // ── Add chip ──────────────────────────────────────────────────────────────
    private void addChip(String tag) {
        if (selectedHashtags.contains(tag)) return; // duplicate
        selectedHashtags.add(tag);

        Chip chip = new Chip(this);
        chip.setText(tag);
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColorResource(R.color.colorPrimary);  // #1877F2 with alpha
        chip.setTextColor(getColor(R.color.colorAccent));
        chip.setCloseIconTint(android.content.res.ColorStateList.valueOf(getColor(android.R.color.white)));
        chip.setOnCloseIconClickListener(v -> {
            chipGroupHashtags.removeView(chip);
            selectedHashtags.remove(tag);
        });
        chipGroupHashtags.addView(chip);
    }

    // ── Post Reel ─────────────────────────────────────────────────────────────
    /*private void postReel() {
        loader.show();
        String caption = etCaption.getText().toString().trim();
        // hashtags comma-separated without #
        String hashtags = String.join(",", selectedHashtags).replace("#", "");

        if (caption.isEmpty()) {
            etCaption.setError("Please write a caption");
            etCaption.requestFocus();
            return;
        }
        if (mediaList.isEmpty()) {
            Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show();
            return;
        }
        // Video part
        File videoFile = new File(mediaList.get(0).getPath());
        RequestBody videoBody = RequestBody.create(videoFile, MediaType.parse("video/*"));
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("Video", videoFile.getName(), videoBody);
        // Thumbnail part
        MultipartBody.Part thumbPart = null;
        if (thumbPath != null) {
            File thumbFile = new File(thumbPath);
            RequestBody thumbBody = RequestBody.create(thumbFile, MediaType.parse("image/*"));
            thumbPart = MultipartBody.Part.createFormData("Thumbnail", thumbFile.getName(), thumbBody);
        }
        progressBar.setVisibility(View.VISIBLE);
        btnPost.setEnabled(false);
        final MultipartBody.Part finalThumbPart = thumbPart;
        UtilMethods.INSTANCE.saveReel(loader, caption, durationSeconds,  // auto from MediaModel
                hashtags, videoPart, finalThumbPart, new UtilMethods.ApiCallBackMulti() {
                    @Override
                    public void onSuccess(Object response) {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            btnPost.setEnabled(true);
                            Toast.makeText(ReelPostActivity.this, "Reel shared!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ReelPostActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            btnPost.setEnabled(true);
                            Toast.makeText(ReelPostActivity.this, error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }*/
    private void postReel() {
        String caption = etCaption.getText().toString().trim();
        String hashtags = String.join(",", selectedHashtags).replace("#", "");

        if (caption.isEmpty()) {
            etCaption.setError("Please write a caption");
            etCaption.requestFocus();
            return;
        }

        // ★ Rendered video use karo — original nahi
        String videoFilePath = null;
        if (renderedVideoPath != null && new File(renderedVideoPath).exists()) {
            videoFilePath = renderedVideoPath;
        } else if (!mediaList.isEmpty()) {
            // Fallback — direct original (render nahi hua toh)
            videoFilePath = mediaList.get(0).getPath();
        }

        if (videoFilePath == null || !new File(videoFilePath).exists()) {
            Toast.makeText(this, "Video file not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Video multipart
        File videoFile = new File(videoFilePath);
        RequestBody videoBody = RequestBody.create(videoFile, MediaType.parse("video/mp4"));
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("Video", videoFile.getName(), videoBody);

        // Thumbnail multipart
        MultipartBody.Part thumbPart = null;
        if (thumbPath != null && new File(thumbPath).exists()) {
            File thumbFile = new File(thumbPath);
            RequestBody tb = RequestBody.create(thumbFile, MediaType.parse("image/jpeg"));
            thumbPart = MultipartBody.Part.createFormData("Thumbnail", thumbFile.getName(), tb);
        }

        progressBar.setVisibility(View.VISIBLE);
        btnPost.setEnabled(false);

        final MultipartBody.Part finalThumbPart = thumbPart;
        final String finalVideoPath = videoFilePath;

        UtilMethods.INSTANCE.saveReel(loader, caption, durationSeconds, hashtags, videoPart, finalThumbPart, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object response) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnPost.setEnabled(true);

                    // ★ Upload ke baad cleanup
                    cleanupTempFiles(finalVideoPath);

                    Toast.makeText(ReelPostActivity.this, "Reel shared!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ReelPostActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnPost.setEnabled(true);
                    Toast.makeText(ReelPostActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void cleanupTempFiles(String renderedPath) {
        try {
            if (renderedPath != null) new File(renderedPath).delete();
            if (thumbPath != null) new File(thumbPath).delete();

            File[] files = getCacheDir().listFiles();
            if (files == null) return;
            for (File f : files) {
                String n = f.getName();
                if (n.startsWith("merged_") || n.startsWith("burned_") || n.startsWith("img_video_") || n.startsWith("final_reel_") || n.startsWith("music_") || n.startsWith("overlay_") || n.startsWith("thumb_final_") || n.startsWith("merged_img_")) {
                    f.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}