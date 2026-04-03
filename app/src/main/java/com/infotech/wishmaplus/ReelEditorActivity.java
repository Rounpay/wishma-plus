package com.infotech.wishmaplus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech.wishmaplus.Api.Response.MediaModel;
import com.infotech.wishmaplus.reels.ReelRenderEngine.ReelRenderEngine;
import com.infotech.wishmaplus.reels.ui.componets.MusicPickerBottomSheet;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReelEditorActivity extends AppCompatActivity {

    // ── Font definitions ─────────────────────────────────────────────────────
    private static final String[] FONT_FAMILIES = {
            "sans-serif-medium", "serif", "sans-serif-condensed",
            "monospace", "sans-serif-light", "casual"
    };
    private static final String[] FONT_LABELS = {
            "Modern", "Serif", "Condensed", "Mono", "Light", "Casual"
    };

    // ── Color palette ─────────────────────────────────────────────────────────
    private static final int[] TEXT_COLORS = {
            0xFFFFFFFF, 0xFF000000, 0xFFFF3B30, 0xFFFF9500, 0xFFFFCC00,
            0xFF34C759, 0xFF00C7BE, 0xFF007AFF, 0xFF5856D6, 0xFFFF2D55,
            0xFFFF6B6B, 0xFFFFE66D, 0xFF4ECDC4, 0xFF45B7D1, 0xFF96CEB4,
            0xFFDDA0DD, 0xFFF7DC6F, 0xFFBB8FCE, 0xFFF0B27A, 0xFF82E0AA
    };
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    // ── Views ─────────────────────────────────────────────────────────────────
    private VideoView videoView;
    private ImageView imagePreview;
    private FrameLayout compositeCanvas;   // video + overlay — merge ke liye
    private FrameLayout overlayCanvas;     // text/emoji/sticker yahan
    private FrameLayout textEditorPanel;
    private FrameLayout emojiPanel;
    private FrameLayout stickerPanel;
    private FrameLayout trashZone;
    private LinearLayout clipStrip;
    private EditText textInput;
    private LinearLayout colorStripLayout;
    private LinearLayout fontStripLayout;
    private SeekBar textSizeSeekBar;
    private ImageView textAlignBtn;
    private RecyclerView emojiRecycler;
    private RecyclerView stickerRecycler;
    private LinearLayout emojiCategoryStrip;
    private LinearLayout stickerCategoryStrip;
    private EmojiAdapter emojiAdapter;
    private StickerAdapter stickerAdapter;
    private View playPauseIndicator;
    private ImageView playPauseIcon;
    // ── State ─────────────────────────────────────────────────────────────────
    private List<MediaModel> mediaList = new ArrayList<>();
    private int currentMediaIndex = 0;
    private int currentTextColor = Color.WHITE;
    private int currentTextAlign = Gravity.CENTER;
    private int currentTextBgMode = 0;
    private int currentFontIndex = 0;
    private float currentTextSize = 26f;
    private boolean isVideoPlaying = false;
    private boolean isTrashVisible = false;
    private TextView editingTextView = null;
    private String selectedMusicPath = null;
    private long musicStartMs = 0, musicEndMs = 0;
    private ReelRenderEngine renderEngine;
    // ─────────────────────────────────────────────────────────────────────────
    // LIFECYCLE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat ctrl = WindowCompat.getInsetsController(
                getWindow(), getWindow().getDecorView());
        ctrl.hide(WindowInsetsCompat.Type.systemBars());
        ctrl.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_reel_editor);

        if (getIntent().hasExtra("media_list")) {
            Object obj = getIntent().getSerializableExtra("media_list");
            if (obj instanceof ArrayList) {
                mediaList = (ArrayList<MediaModel>) obj;
            }
        }

        bindViews();
        setupTopBar();
        setupRightTools();
        setupTextEditor();
        buildColorStrip();
        buildFontStrip();
        buildClipStrip();
        setupEmojiPanel();
        setupStickerPanel();
        setupBottomActions();
        setupVideoTapToPause();
        loadMediaAtIndex(0);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BIND VIEWS
    // ─────────────────────────────────────────────────────────────────────────

    private void bindViews() {
        renderEngine = new ReelRenderEngine(this);
        videoView = findViewById(R.id.videoView);
        imagePreview = findViewById(R.id.imagePreview);
        compositeCanvas = findViewById(R.id.compositeCanvas);   // NEW
        overlayCanvas = findViewById(R.id.overlayCanvas);
        textEditorPanel = findViewById(R.id.textEditorPanel);
        emojiPanel = findViewById(R.id.emojiPanel);
        stickerPanel = findViewById(R.id.stickerPanel);
        trashZone = findViewById(R.id.trashZone);
        clipStrip = findViewById(R.id.clipStrip);
        textInput = findViewById(R.id.textInput);
        colorStripLayout = findViewById(R.id.colorStrip);
        fontStripLayout = findViewById(R.id.fontStyleStrip);
        textSizeSeekBar = findViewById(R.id.textSizeSeek);
        textAlignBtn = findViewById(R.id.textAlignBtn);
        emojiRecycler = findViewById(R.id.emojiRecycler);
        stickerRecycler = findViewById(R.id.stickerRecycler);
        emojiCategoryStrip = findViewById(R.id.emojiCategoryStrip);
        stickerCategoryStrip = findViewById(R.id.stickerCategoryStrip);
        playPauseIndicator = findViewById(R.id.playPauseIndicator);
        playPauseIcon = findViewById(R.id.playPauseIcon);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TOP BAR
    // ─────────────────────────────────────────────────────────────────────────

    private void setupTopBar() {
        findViewById(R.id.btnClose).setOnClickListener(v -> finish());
        findViewById(R.id.btnMusic).setOnClickListener(v -> {
            MusicPickerBottomSheet sheet = MusicPickerBottomSheet.newInstance(
                    (path, start, end, title) -> {
                        selectedMusicPath = path;
                        musicStartMs      = start;
                        musicEndMs        = end;
                        // Music icon blue highlight karo
                        ((ImageView) findViewById(R.id.musicIcon))
                                .setColorFilter(0xFF1877F2);
                        Toast.makeText(this,
                                "Music: " + title, Toast.LENGTH_SHORT).show();
                    });
            sheet.show(getSupportFragmentManager(), "music_picker");
        });
        findViewById(R.id.btnNext).setOnClickListener(v -> proceedToPost());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RIGHT TOOLS
    // ─────────────────────────────────────────────────────────────────────────

    private void setupRightTools() {
        findViewById(R.id.toolText).setOnClickListener(v -> {
            closeAllPanels();
            editingTextView = null;
            openTextEditorPanel();
        });
        findViewById(R.id.toolEmoji).setOnClickListener(v -> {
            closeAllPanels();
            slideUpPanel(emojiPanel);
        });
        findViewById(R.id.toolSticker).setOnClickListener(v -> {
            closeAllPanels();
            slideUpPanel(stickerPanel);
        });
        findViewById(R.id.toolDraw).setOnClickListener(v -> showToast("Draw — coming soon"));
        findViewById(R.id.toolFilter).setOnClickListener(v -> showToast("Filter — coming soon"));
        findViewById(R.id.toolTrim).setOnClickListener(v -> showToast("Trim — coming soon"));
        overlayCanvas.setOnClickListener(v -> closeAllPanels());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PANELS
    // ─────────────────────────────────────────────────────────────────────────

    private void slideUpPanel(View panel) {
        panel.setVisibility(View.VISIBLE);
        panel.setTranslationY(panel.getHeight() > 0 ? panel.getHeight() : 900f);
        panel.animate().translationY(0).setDuration(300)
                .setInterpolator(new OvershootInterpolator(0.7f)).start();
    }

    private void slideDownPanel(View panel) {
        if (panel.getVisibility() != View.VISIBLE) return;
        panel.animate().translationY(1200f).setDuration(240)
                .withEndAction(() -> panel.setVisibility(View.GONE)).start();
    }

    private void closeAllPanels() {
        emojiPanel.setVisibility(View.GONE);
        textEditorPanel.setVisibility(View.GONE);
        textEditorPanel.setTranslationY(0);
        slideDownPanel(textEditorPanel);
        slideDownPanel(emojiPanel);
        slideDownPanel(stickerPanel);
        dismissKeyboard();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VIDEO CENTER FIX — setVideoLayout via reflection / MediaPlayer listener
    // ─────────────────────────────────────────────────────────────────────────

    private void playVideo(String path) {
        videoView.setVisibility(View.VISIBLE);
        imagePreview.setVisibility(View.GONE);
        videoView.setVideoURI(Uri.parse(path));
        videoView.setOnPreparedListener(mp -> {
            // ── CENTER FIX: MediaPlayer se actual video dimensions lo ──────
            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();

            if (videoWidth > 0 && videoHeight > 0) {
                int screenW = videoView.getWidth();
                int screenH = videoView.getHeight();

                float videoRatio = (float) videoWidth / videoHeight;
                float screenRatio = (float) screenW / screenH;

                int finalW, finalH;
                if (videoRatio > screenRatio) {
                    // Video zyada wide hai — width fit karo
                    finalW = screenW;
                    finalH = (int) (screenW / videoRatio);
                } else {
                    // Video zyada tall hai (9:16 reels) — height fit karo
                    finalH = screenH;
                    finalW = (int) (screenH * videoRatio);
                }

                // VideoView ko center mein resize karo
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(finalW, finalH);
                lp.gravity = Gravity.CENTER;
                videoView.setLayoutParams(lp);
            }

            mp.setLooping(true);
            videoView.start();
            isVideoPlaying = true;
        });
        videoView.setOnErrorListener((mp, what, extra) -> {
            showToast("Video error: " + what);
            return true;
        });
    }

    private void showImage(String path) {
        imagePreview.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        if (videoView.isPlaying()) videoView.pause();
        Glide.with(this).load(path).centerCrop().into(imagePreview);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TAP TO PAUSE
    // ─────────────────────────────────────────────────────────────────────────

    private void setupVideoTapToPause() {
        View mediaArea = findViewById(R.id.mediaContainer);
        mediaArea.setOnClickListener(v -> {
            if (videoView.getVisibility() != View.VISIBLE) return;
            if (isVideoPlaying) {
                videoView.pause();
                isVideoPlaying = false;
                showPlayPauseHint(false);
            } else {
                videoView.start();
                isVideoPlaying = true;
                showPlayPauseHint(true);
            }
        });
    }

    private void showPlayPauseHint(boolean playing) {
        playPauseIcon.setImageResource(playing
                ? R.drawable.ic_play_circle_outline
                : R.drawable.outline_pause_circle_24);
        playPauseIndicator.setVisibility(View.VISIBLE);
        playPauseIndicator.setAlpha(1f);
        playPauseIndicator.animate().alpha(0f).setStartDelay(600).setDuration(300)
                .withEndAction(() -> playPauseIndicator.setVisibility(View.GONE)).start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEXT EDITOR
    // ─────────────────────────────────────────────────────────────────────────

    private void setupTextEditor() {
        findViewById(R.id.textDoneBtn).setOnClickListener(v -> commitTextOverlay());
        textAlignBtn.setOnClickListener(v -> cycleTextAlign());
        findViewById(R.id.textBgBtn).setOnClickListener(v -> cycleTextBg());
        textSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int p, boolean u) {
                currentTextSize = 12 + p;
                textInput.setTextSize(currentTextSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar sb) {
            }
        });
    }

    private void openTextEditorPanel() {
        textEditorPanel.setVisibility(View.VISIBLE);
        textEditorPanel.setTranslationY(0);
        if (editingTextView != null) {
            textInput.setText(editingTextView.getText().toString());
            textInput.setSelection(textInput.getText().length());
        } else {
            textInput.setText("");
        }
        ViewGroup.LayoutParams lp = textEditorPanel.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        textEditorPanel.setLayoutParams(lp);
        textInput.requestFocus();
        textInput.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(textInput, InputMethodManager.SHOW_IMPLICIT);
        }, 200);
    }

    private void commitTextOverlay() {
        if (editingTextView != null) {
            if (editingTextView.getParent() != null)
                overlayCanvas.removeView(editingTextView);
            editingTextView = null;
        }
        String text = textInput.getText().toString().trim();
        if (!text.isEmpty()) addDraggableTextView(text);
        closeAllPanels();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DRAGGABLE VIEWS
    // ─────────────────────────────────────────────────────────────────────────

    @SuppressLint("ClickableViewAccessibility")
    private void addDraggableTextView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(currentTextColor);
        tv.setTextSize(currentTextSize);
        tv.setGravity(currentTextAlign);
        tv.setTypeface(Typeface.create(FONT_FAMILIES[currentFontIndex], Typeface.NORMAL));
        tv.setPadding(dp(12), dp(6), dp(12), dp(6));
        applyTextBackground(tv);
        tv.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        makeDraggableWithTrash(tv);
        tv.setOnClickListener(v -> {
            if (editingTextView != null && editingTextView.getParent() != null)
                overlayCanvas.removeView(editingTextView);
            editingTextView = tv;
            textInput.setText(tv.getText());
            openTextEditorPanel();
        });
        tv.setScaleX(0f);
        tv.setScaleY(0f);
        overlayCanvas.addView(tv);
        tv.animate().scaleX(1f).scaleY(1f)
                .setDuration(320).setInterpolator(new OvershootInterpolator(1.3f)).start();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addDraggableEmoji(String emoji) {
        TextView tv = new TextView(this);
        tv.setText(emoji);
        tv.setTextSize(42f);
        tv.setClickable(true);
        tv.setFocusable(true);
        tv.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        makeDraggableWithTrash(tv);
        tv.setScaleX(0f);
        tv.setScaleY(0f);
        overlayCanvas.addView(tv);
        tv.animate().scaleX(1f).scaleY(1f)
                .setDuration(320).setInterpolator(new OvershootInterpolator(1.5f)).start();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addDraggableSticker(String sticker) {
        TextView tv = new TextView(this);
        tv.setText(sticker);
        tv.setTextSize(36f);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(dp(8), dp(8), dp(8), dp(8));
        tv.setClickable(true);
        tv.setFocusable(true);
        tv.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        makeDraggableWithTrash(tv);
        tv.setScaleX(0f);
        tv.setScaleY(0f);
        overlayCanvas.addView(tv);
        tv.animate().scaleX(1f).scaleY(1f)
                .setDuration(320).setInterpolator(new OvershootInterpolator(1.5f)).start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DRAG + TRASH + SCALE
    // ─────────────────────────────────────────────────────────────────────────

    @SuppressLint("ClickableViewAccessibility")
    private void makeDraggableWithTrash(View view) {
        final float[] dX = {0f}, dY = {0f};
        final float[] startRawX = {0f}, startRawY = {0f};
        final float[] originalScale = {1f};
        final boolean[] isDragging = {false};

        ScaleGestureDetector scaleDetector = new ScaleGestureDetector(this,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector d) {
                        float s = Math.max(0.3f, Math.min(view.getScaleX() * d.getScaleFactor(), 5f));
                        view.setScaleX(s);
                        view.setScaleY(s);
                        originalScale[0] = s;
                        return true;
                    }
                });

        view.setOnTouchListener((v, event) -> {
            scaleDetector.onTouchEvent(event);
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    dX[0] = v.getX() - event.getRawX();
                    dY[0] = v.getY() - event.getRawY();
                    startRawX[0] = event.getRawX();
                    startRawY[0] = event.getRawY();
                    originalScale[0] = v.getScaleX();
                    isDragging[0] = false;
                    v.animate().scaleX(originalScale[0] * 1.08f)
                            .scaleY(originalScale[0] * 1.08f).setDuration(80).start();
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (event.getPointerCount() == 1) {
                        float mx = event.getRawX(), my = event.getRawY();
                        float dist = (float) Math.hypot(mx - startRawX[0], my - startRawY[0]);
                        if (dist > 8 || isDragging[0]) {
                            isDragging[0] = true;
                            if (!isTrashVisible) showTrashZone();
                            v.setX(mx + dX[0]);
                            v.setY(my + dY[0]);
                            Rect tr = new Rect();
                            trashZone.getGlobalVisibleRect(tr);
                            boolean over = tr.contains((int) mx, (int) my);
                            trashZone.setAlpha(over ? 1f : 0.8f);
                            trashZone.setScaleX(over ? 1.2f : 1f);
                            trashZone.setScaleY(over ? 1.2f : 1f);
                            v.setAlpha(over ? 0.4f : 1f);
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    hideTrashZone();
                    if (isDragging[0]) {
                        Rect tr = new Rect();
                        trashZone.getGlobalVisibleRect(tr);
                        if (tr.contains((int) event.getRawX(), (int) event.getRawY())) {
                            v.animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(200)
                                    .withEndAction(() -> overlayCanvas.removeView(v)).start();
                        } else {
                            v.animate().scaleX(originalScale[0]).scaleY(originalScale[0])
                                    .alpha(1f).setDuration(180).start();
                        }
                        return true;
                    }
                    v.animate().scaleX(originalScale[0]).scaleY(originalScale[0])
                            .setDuration(100).start();
                    break;
            }
            return isDragging[0];
        });
    }

    private void showTrashZone() {
        isTrashVisible = true;
        trashZone.setVisibility(View.VISIBLE);
        trashZone.setAlpha(0f);
        trashZone.animate().alpha(0.8f).setDuration(200).start();
    }

    private void hideTrashZone() {
        isTrashVisible = false;
        trashZone.animate().alpha(0f).setDuration(180)
                .withEndAction(() -> trashZone.setVisibility(View.GONE)).start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEXT BG / ALIGN
    // ─────────────────────────────────────────────────────────────────────────

    private void applyTextBackground(View v) {
        android.graphics.drawable.GradientDrawable bg =
                new android.graphics.drawable.GradientDrawable();
        bg.setCornerRadius(dp(10));
        switch (currentTextBgMode) {
            case 0:
                v.setBackground(null);
                break;
            case 1:
                bg.setColor(Color.argb(190, 0, 0, 0));
                v.setBackground(bg);
                break;
            case 2:
                bg.setColor(Color.TRANSPARENT);
                bg.setStroke(dp(2), Color.WHITE);
                v.setBackground(bg);
                break;
        }
    }

    private void cycleTextAlign() {
        currentTextAlign = currentTextAlign == Gravity.CENTER ? Gravity.START :
                currentTextAlign == Gravity.START ? Gravity.END : Gravity.CENTER;
        textInput.setGravity(currentTextAlign);
        int[] icons = {R.drawable.ic_format_align_center,
                R.drawable.ic_format_align_left, R.drawable.ic_format_align_right};
        int idx = currentTextAlign == Gravity.CENTER ? 0 :
                currentTextAlign == Gravity.START ? 1 : 2;
        textAlignBtn.setImageResource(icons[idx]);
    }

    private void cycleTextBg() {
        currentTextBgMode = (currentTextBgMode + 1) % 3;
        applyTextBackground(textInput);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // COLOR + FONT STRIPS
    // ─────────────────────────────────────────────────────────────────────────

    private void buildColorStrip() {
        colorStripLayout.removeAllViews();
        for (int i = 0; i < TEXT_COLORS.length; i++) {
            final int color = TEXT_COLORS[i];
            final int index = i;
            FrameLayout dot = new FrameLayout(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(40), dp(40));
            lp.setMargins(dp(5), 0, dp(5), 0);
            dot.setLayoutParams(lp);
            android.graphics.drawable.GradientDrawable gd =
                    new android.graphics.drawable.GradientDrawable();
            gd.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            gd.setColor(color);
            gd.setStroke(dp(2), i == 0 ? 0x88FFFFFF : Color.WHITE);
            dot.setBackground(gd);
            dot.setOnClickListener(v -> {
                currentTextColor = color;
                textInput.setTextColor(color);
                highlightColorDot(index);
            });
            colorStripLayout.addView(dot);
        }
    }

    private void highlightColorDot(int selected) {
        for (int i = 0; i < colorStripLayout.getChildCount(); i++) {
            boolean sel = i == selected;
            colorStripLayout.getChildAt(i).animate()
                    .scaleX(sel ? 1.35f : 1f).scaleY(sel ? 1.35f : 1f).setDuration(150).start();
        }
    }

    private void buildFontStrip() {
        fontStripLayout.removeAllViews();
        for (int i = 0; i < FONT_FAMILIES.length; i++) {
            final int idx = i;
            TextView chip = new TextView(this);
            chip.setText(FONT_LABELS[i]);
            chip.setTextColor(i == 0 ? Color.WHITE : 0x88FFFFFF);
            chip.setTextSize(14f);
            chip.setTypeface(Typeface.create(FONT_FAMILIES[i], Typeface.NORMAL));
            chip.setPadding(dp(14), dp(5), dp(14), dp(5));
            if (i == 0)
                chip.setPaintFlags(chip.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
            chip.setOnClickListener(v -> {
                currentFontIndex = idx;
                textInput.setTypeface(Typeface.create(FONT_FAMILIES[idx], Typeface.NORMAL));
                updateFontSelection(idx);
            });
            fontStripLayout.addView(chip);
        }
    }

    private void updateFontSelection(int selected) {
        for (int i = 0; i < fontStripLayout.getChildCount(); i++) {
            TextView tv = (TextView) fontStripLayout.getChildAt(i);
            boolean sel = i == selected;
            tv.setTextColor(sel ? Color.WHITE : 0x88FFFFFF);
            int flags = tv.getPaintFlags();
            if (sel) flags |= android.graphics.Paint.UNDERLINE_TEXT_FLAG;
            else flags &= ~android.graphics.Paint.UNDERLINE_TEXT_FLAG;
            tv.setPaintFlags(flags);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EMOJI + STICKER PANELS
    // ─────────────────────────────────────────────────────────────────────────

    private void setupEmojiPanel() {
        String[] catIcons = {"😀", "❤️", "🎉", "🐶", "🍕", "⚽", "🌍", "✈️"};
        buildCategoryTabs(emojiCategoryStrip, catIcons, idx ->
                emojiAdapter.updateData(EmojiData.getCategory(idx)));
        emojiAdapter = new EmojiAdapter(this, EmojiData.getCategory(0), emoji -> {
            addDraggableEmoji(emoji);
            slideDownPanel(emojiPanel);
        });
        emojiRecycler.setLayoutManager(new GridLayoutManager(this, 8));
        emojiRecycler.setAdapter(emojiAdapter);
    }

    private void setupStickerPanel() {
        String[] catLabels = {"🔥 Trending", "💬 Text", "🎨 Fun", "✨ Glitter", "🌈 Vibes"};
        buildCategoryTabs(stickerCategoryStrip, catLabels, idx ->
                stickerAdapter.updateData(StickerData.getCategory(idx)));
        stickerAdapter = new StickerAdapter(this, StickerData.getCategory(0), sticker -> {
            addDraggableSticker(sticker);
            slideDownPanel(stickerPanel);
        });
        stickerRecycler.setLayoutManager(new GridLayoutManager(this, 4));
        stickerRecycler.setAdapter(stickerAdapter);
    }

    private void buildCategoryTabs(LinearLayout strip, String[] labels, OnCatSelected cb) {
        strip.removeAllViews();
        for (int i = 0; i < labels.length; i++) {
            final int idx = i;
            TextView tv = new TextView(this);
            tv.setText(labels[i]);
            tv.setTextSize(labels[i].length() > 4 ? 11f : 20f);
            tv.setTextColor(0xCCFFFFFF);
            tv.setPadding(dp(10), dp(5), dp(10), dp(5));
            tv.setAlpha(i == 0 ? 1f : 0.5f);
            if (i == 0) setTabSelected(tv);
            tv.setOnClickListener(v -> {
                for (int j = 0; j < strip.getChildCount(); j++) {
                    View c = strip.getChildAt(j);
                    c.setAlpha(j == idx ? 1f : 0.5f);
                    if (j == idx) setTabSelected((TextView) c);
                    else c.setBackground(null);
                }
                cb.onSelected(idx);
            });
            strip.addView(tv);
        }
    }

    private void setTabSelected(TextView tv) {
        android.graphics.drawable.GradientDrawable bg =
                new android.graphics.drawable.GradientDrawable();
        bg.setColor(0x33FFFFFF);
        bg.setCornerRadius(dp(20));
        tv.setBackground(bg);
    }

    private void buildClipStrip() {
        clipStrip.removeAllViews();
        for (int i = 0; i < mediaList.size(); i++) {
            final int idx = i;
            MediaModel m = mediaList.get(i);

            FrameLayout frame = new FrameLayout(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(52), dp(52));
            lp.setMargins(dp(3), 0, dp(3), 0);
            frame.setLayoutParams(lp);
            android.graphics.drawable.GradientDrawable bg =
                    new android.graphics.drawable.GradientDrawable();
            bg.setColor(0xFF222222);
            bg.setCornerRadius(dp(8));
            frame.setBackground(bg);
            frame.setClipToOutline(true);
            frame.setOutlineProvider(android.view.ViewOutlineProvider.BACKGROUND);

            ImageView thumb = new ImageView(this);
            thumb.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            frame.addView(thumb);

            // ── Thumbnail load ────────────────────────────────────────────
            if (m.isVideo()) {
                ThumbnailHelper.load(this, m.getPath(), thumb);
            } else {
                Glide.with(this).load(m.getPath()).centerCrop().into(thumb);
            }

            // ── Play badge for video ───────────────────────────────────────
            if (m.isVideo()) {
                ImageView badge = new ImageView(this);
                FrameLayout.LayoutParams blp =
                        new FrameLayout.LayoutParams(dp(16), dp(16), Gravity.BOTTOM | Gravity.START);
                blp.setMargins(dp(4), 0, 0, dp(4));
                badge.setLayoutParams(blp);
                badge.setImageResource(R.drawable.ic_play_circle_outline);
                badge.setColorFilter(Color.WHITE);
                frame.addView(badge);

                // Duration badge
                if (m.getDuration() > 0) {
                    TextView tvDur = new TextView(this);
                    FrameLayout.LayoutParams dlp = new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            Gravity.BOTTOM | Gravity.END);
                    dlp.setMargins(0, 0, dp(3), dp(3));
                    tvDur.setLayoutParams(dlp);
                    tvDur.setTextColor(Color.WHITE);
                    tvDur.setTextSize(9f);
                    tvDur.setBackgroundColor(0x88000000);
                    tvDur.setPadding(dp(2), 0, dp(2), 0);
                    long sec = m.getDuration() / 1000;
                    tvDur.setText(String.format(java.util.Locale.US, "%d:%02d", sec / 60, sec % 60));
                    frame.addView(tvDur);
                }
            }

            if (i == 0) setBorderActive(frame, true);
            frame.setOnClickListener(v -> {
                updateClipStripSelection(idx);
                loadMediaAtIndex(idx);
            });
            clipStrip.addView(frame);
        }

        // "+" add button
        FrameLayout addBtn = new FrameLayout(this);
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(dp(52), dp(52));
        alp.setMargins(dp(3), 0, dp(3), 0);
        addBtn.setLayoutParams(alp);
        android.graphics.drawable.GradientDrawable addBg =
                new android.graphics.drawable.GradientDrawable();
        addBg.setColor(Color.TRANSPARENT);
        addBg.setStroke(dp(1), 0x66FFFFFF);
        addBg.setCornerRadius(dp(8));
        addBtn.setBackground(addBg);
        ImageView addIcon = new ImageView(this);
        addIcon.setLayoutParams(new FrameLayout.LayoutParams(dp(24), dp(24), Gravity.CENTER));
        addIcon.setImageResource(R.drawable.ic_add);
        addIcon.setColorFilter(0xAAFFFFFF);
        addBtn.addView(addIcon);
        addBtn.setOnClickListener(v -> finish());
        clipStrip.addView(addBtn);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CLIP STRIP — thumbnail with ThumbnailHelper
    // ─────────────────────────────────────────────────────────────────────────

    private void updateClipStripSelection(int selected) {
        currentMediaIndex = selected;
        for (int i = 0; i < clipStrip.getChildCount() - 1; i++)
            setBorderActive((FrameLayout) clipStrip.getChildAt(i), i == selected);
    }

    private void setBorderActive(FrameLayout frame, boolean active) {
        android.graphics.drawable.GradientDrawable border =
                new android.graphics.drawable.GradientDrawable();
        border.setColor(Color.TRANSPARENT);
        if (active) border.setStroke(dp(2), Color.WHITE);
        border.setCornerRadius(dp(8));
        frame.setForeground(active ? border : null);
    }

    private void setupBottomActions() {
        findViewById(R.id.btnAddClip).setOnClickListener(v -> finish());
        findViewById(R.id.btnAudio).setOnClickListener(v -> showToast("Audio — coming soon"));
        findViewById(R.id.btnSpeed).setOnClickListener(v -> showToast("Speed — coming soon"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BOTTOM ACTIONS
    // ─────────────────────────────────────────────────────────────────────────

    private void loadMediaAtIndex(int index) {
        if (index < 0 || index >= mediaList.size()) return;
        MediaModel m = mediaList.get(index);
        if (m.isVideo()) playVideo(m.getPath());
        else showImage(m.getPath());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MEDIA LOADING
    // ─────────────────────────────────────────────────────────────────────────

   /* private void proceedToPost() {
        if (mediaList == null || mediaList.isEmpty()) {
            showToast("No media found");
            return;
        }

        // UI elements (topbar, tools
        hideEditorUIForCapture();

        // overlayCanvas ka screenshot lo — text/emoji/sticker positions capture
        overlayCanvas.post(() -> {
            // Overlay snapshot
            Bitmap overlayBitmap = captureViewBitmap(overlayCanvas);

            // UI
            showEditorUI();

            // Video thumbnail
            executor.execute(() -> {
                MediaModel m = mediaList.get(currentMediaIndex);
                String thumbPath = null;

                if (m.isVideo()) {
                    Bitmap videoFrame = extractVideoFrame(m.getPath());
                    // Video frame + overlay merge karo
                    if (videoFrame != null && overlayBitmap != null) {
                        thumbPath = mergeAndSave(videoFrame, overlayBitmap);
                    } else if (videoFrame != null) {
                        thumbPath = saveBitmap(videoFrame, "thumb_video_");
                    }
                } else {
                    // Image — as-is use karo
                    thumbPath = m.getPath();
                }
                final String finalThumbPath = thumbPath;
                mainHandler.post(() -> {
                    // Overlay snapshot bhi save
                    String overlayPath = null;
                    if (overlayBitmap != null) {
                        overlayPath = saveBitmap(overlayBitmap, "overlay_");
                    }
                    Intent intent = new Intent(this, ReelPostActivity.class);
                    intent.putExtra("media_list", new ArrayList<>(mediaList));
                    intent.putExtra("thumbnail_path", finalThumbPath);
                    intent.putExtra("overlay_path", overlayPath);   // optional
                    startActivity(intent);
                    overridePendingTransition(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right);
                });
            });
        });
    }*/
   private void proceedToPost() {
       if (mediaList == null || mediaList.isEmpty()) {
           showToast("No media found");
           return;
       }

       hideEditorUIForCapture();

       // Progress dialog
       android.app.ProgressDialog pd = new android.app.ProgressDialog(this);
       pd.setTitle("Preparing reel...");
       pd.setMessage("Starting...");
       pd.setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL);
       pd.setMax(100);
       pd.setCancelable(false);
       pd.show();

       MediaModel m      = mediaList.get(currentMediaIndex);
       boolean isImage   = !m.isVideo();
       int durationSec   = isImage ? 15 : (int)(m.getDuration() / 1000);

       renderEngine.render(
               m.getPath(),
               isImage,
               overlayCanvas,
               selectedMusicPath,
               musicStartMs,
               musicEndMs,
               durationSec,
               new ReelRenderEngine.RenderCallback() {

                   @Override
                   public void onProgress(int percent, String message) {
                       runOnUiThread(() -> {
                           pd.setProgress(percent);
                           pd.setMessage(message);
                       });
                   }

                   @Override
                   public void onComplete(String outputPath) {
                       runOnUiThread(() -> {
                           pd.dismiss();
                           showEditorUI();

                           // Thumbnail extract
                           String thumbPath = extractThumbFromVideo(outputPath);

                           // ★ Intent — rendered_video_path
                           Intent intent = new Intent(
                                   ReelEditorActivity.this, ReelPostActivity.class);
                           intent.putExtra("rendered_video_path", outputPath);
                           intent.putExtra("thumbnail_path", thumbPath);
                           intent.putExtra("media_list", new ArrayList<>(mediaList));
                           startActivity(intent);
                           overridePendingTransition(
                                   android.R.anim.slide_in_left,
                                   android.R.anim.slide_out_right);
                       });
                   }

                   @Override
                   public void onError(String error) {
                       runOnUiThread(() -> {
                           pd.dismiss();
                           showEditorUI();
                           showToast("Failed: " + error);
                       });
                   }
               }
       );
   }
    // ── Thumbnail extract helper ──────────────────────────────────────────────────
    private String extractThumbFromVideo(String videoPath) {
        android.media.MediaMetadataRetriever mmr =
                new android.media.MediaMetadataRetriever();
        try {
            mmr.setDataSource(videoPath);
            Bitmap frame = mmr.getFrameAtTime(
                    0, android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            if (frame == null) return null;
            File tf = new File(getCacheDir(),
                    "thumb_final_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(tf);
            frame.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.close();
            return tf.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try { mmr.release(); } catch (Exception ignored) {}
        }
    }

    /**
     * View ka Bitmap snapshot leta hai (text/emoji/sticker sab include)
     */
    private Bitmap captureViewBitmap(View view) {
        try {
            Bitmap bmp = Bitmap.createBitmap(
                    view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            view.draw(canvas);
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CAPTURE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Video ka pehla frame MediaMetadataRetriever se nikalna
     */
    private Bitmap extractVideoFrame(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            Uri uri = Uri.parse(videoPath);
            if ("content".equals(uri.getScheme())) {
                retriever.setDataSource(this, uri);
            } else {
                retriever.setDataSource(videoPath);
            }
            Bitmap frame = retriever.getFrameAtTime(
                    0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            if (frame == null) {
                frame = retriever.getFrameAtTime(
                        1_000_000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            }
            return frame;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                retriever.release();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Video frame + overlay bitmap merge karo
     * Result = thumbnail jisme text/emoji/sticker bhi dikhe
     */
    private String mergeAndSave(Bitmap videoFrame, Bitmap overlay) {
        try {
            // Overlay ko video frame ke size mein scale karo
            Bitmap scaledOverlay = Bitmap.createScaledBitmap(
                    overlay, videoFrame.getWidth(), videoFrame.getHeight(), true);

            Bitmap merged = Bitmap.createBitmap(
                    videoFrame.getWidth(), videoFrame.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(merged);
            canvas.drawBitmap(videoFrame, 0, 0, null);   // pehle video frame
            canvas.drawBitmap(scaledOverlay, 0, 0, null); // upar overlay

            return saveBitmap(merged, "merged_thumb_");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Bitmap ko cache mein save karo, path return karo
     */
    private String saveBitmap(Bitmap bmp, String prefix) {
        try {
            File file = new File(getCacheDir(), prefix + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Editor UI hide karo screenshot ke liye
     */
    private void hideEditorUIForCapture() {
        findViewById(R.id.topBar).setVisibility(View.INVISIBLE);
        // Right tools LinearLayout
        ViewGroup root = (ViewGroup) getWindow().getDecorView().getRootView();
        // Bottom bar — last child of root's first child
    }

    private void showEditorUI() {
        findViewById(R.id.topBar).setVisibility(View.VISIBLE);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UTILS
    // ─────────────────────────────────────────────────────────────────────────

    private void dismissKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView.getVisibility() == View.VISIBLE && !videoView.isPlaying()) {
            videoView.start();
            isVideoPlaying = true;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LIFECYCLE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView.isPlaying()) {
            videoView.pause();
            isVideoPlaying = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainHandler.removeCallbacksAndMessages(null);
        executor.shutdown();
        if (renderEngine != null) renderEngine.cancel();
    }

    interface OnCatSelected {
        void onSelected(int index);
    }
}
