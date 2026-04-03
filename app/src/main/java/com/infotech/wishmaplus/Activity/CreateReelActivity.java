package com.infotech.wishmaplus.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.infotech.wishmaplus.Adapter.CreateReelAdapter;
import com.infotech.wishmaplus.Adapter.FolderDropdownAdapter;
import com.infotech.wishmaplus.Api.Response.FolderModel;
import com.infotech.wishmaplus.Api.Response.MediaModel;
import com.infotech.wishmaplus.reels.ui.CameraRecorderActivity;
import com.infotech.wishmaplus.MultiSelectBottomSheet;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.ReelEditorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CreateReelActivity extends AppCompatActivity {

    // Views
    private RecyclerView videoRecycler;
    private LinearLayout folderDropdownTrigger, cameraView;
    private LinearLayout multipleSelectBtn;
    private TextView folderName;
    private ImageView dropdownArrow, back;
    private View dropdownOverlay;
    private RecyclerView folderRecycler;
    private AppBarLayout appBarLayout;

    // Data
    private final List<MediaModel> mediaList = new ArrayList<>();
    private final List<FolderModel> folderList = new ArrayList<>();
    private CreateReelAdapter adapter;
    private FolderDropdownAdapter folderDropdownAdapter;

    // State
    private boolean isDropdownOpen = false;
    private String currentFolderPath = null; // null = all media
    private String currentFolderName = "Gallery";
    private int selectedFolderPosition = -1; // -1 = "Gallery" (All)

    // Permission launcher
    private final ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        boolean granted = false;
        for (Boolean val : result.values()) {
            if (val) {
                granted = true;
                break;
            }
        }
        if (granted) {
            loadAllMedia();
            loadFolders();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_reel);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        setupRecycler();
        setupDropdown();
        setupScrollBehavior();
        requestPermissionsIfNeeded();
    }

    // ─── Init ─────────────────────────────────────────────────────────────────

    private void initViews() {
        videoRecycler = findViewById(R.id.videoRecycler);
        folderDropdownTrigger = findViewById(R.id.folderDropdownTrigger);
        multipleSelectBtn = findViewById(R.id.multipleSelectBtn);
        cameraView = findViewById(R.id.cameraView);
        folderName = findViewById(R.id.folderName);
        dropdownArrow = findViewById(R.id.dropdownArrow);
        back = findViewById(R.id.back);
        dropdownOverlay = findViewById(R.id.dropdownOverlay);
        folderRecycler = findViewById(R.id.folderRecycler);
        appBarLayout = findViewById(R.id.appBarLayout);

        cameraView.setOnClickListener(v -> {
            // Open camera
            // ...
            Intent intent = new Intent(CreateReelActivity.this, CameraRecorderActivity.class);
            startActivity(intent);
        });
        back.setOnClickListener(v -> finish());

        // Close dropdown when tapping outside
        dropdownOverlay.setOnClickListener(v -> closeDropdown());

        // Open/close dropdown
        folderDropdownTrigger.setOnClickListener(v -> {
            if (isDropdownOpen) closeDropdown();
            else openDropdown();
        });

        // Open multi-select bottom sheet
        multipleSelectBtn.setOnClickListener(v -> openMultiSelectSheet());
    }

    private void setupRecycler() {
        GridLayoutManager gridLayout = new GridLayoutManager(this, 3);
        videoRecycler.setLayoutManager(gridLayout);
        adapter = new CreateReelAdapter(this, mediaList, media -> {
            ArrayList<MediaModel> singleList =
                    new ArrayList<>();
            singleList.add(media);
            Intent intent = new Intent(CreateReelActivity.this, ReelEditorActivity.class);
            intent.putExtra("media_list",
                    new ArrayList<>(singleList));
            startActivity(intent);
        });
        videoRecycler.setAdapter(adapter);
    }

    // ─── Scroll Behavior: hide icon row on scroll up ───────────────────────────

    private void setupScrollBehavior() {
        // AppBarLayout already handles this via layout_scrollFlags on the
        // HorizontalScrollView (scroll|exitUntilCollapsed).
        // We just animate the arrow indicator with AppBarLayout offset.
        appBarLayout.addOnOffsetChangedListener((appBar, verticalOffset) -> {
            float totalScroll = appBar.getTotalScrollRange();
            if (totalScroll == 0) return;
            float fraction = Math.abs(verticalOffset) / (float) totalScroll;
            // Fade/scale the icon strip based on collapse fraction (handled by CoordinatorLayout)
        });
    }

    // ─── Folder Dropdown ──────────────────────────────────────────────────────

    private void setupDropdown() {
        folderDropdownAdapter = new FolderDropdownAdapter(this, folderList, position -> {
            selectedFolderPosition = position;
            if (position == 0) {
                // "Gallery" = All
                currentFolderPath = null;
                currentFolderName = "Gallery";
                loadAllMedia();
            } else {
                FolderModel selected = folderList.get(position);
                currentFolderPath = selected.getFolderPath();
                currentFolderName = selected.getFolderName();
                loadMediaByFolder(currentFolderPath);
            }
            folderName.setText(currentFolderName);
            folderDropdownAdapter.setSelectedPosition(position);
            closeDropdown();
        });

        folderRecycler.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        folderRecycler.setAdapter(folderDropdownAdapter);
        // Limit height via nestedScrolling
        folderRecycler.setNestedScrollingEnabled(true);
    }

    private void openDropdown() {
        isDropdownOpen = true;
        dropdownOverlay.setVisibility(View.VISIBLE);
        dropdownOverlay.setAlpha(0f);
        dropdownOverlay.animate().alpha(1f).setDuration(200).start();
        dropdownArrow.animate().rotation(180f).setDuration(200).start();
    }

    private void closeDropdown() {
        isDropdownOpen = false;
        dropdownOverlay.animate().alpha(0f).setDuration(180).withEndAction(() -> dropdownOverlay.setVisibility(View.GONE)).start();
        dropdownArrow.animate().rotation(0f).setDuration(200).start();
    }

    // ─── Multi-Select Bottom Sheet ─────────────────────────────────────────────

    private void openMultiSelectSheet() {
        MultiSelectBottomSheet sheet = MultiSelectBottomSheet.newInstance(new ArrayList<>(mediaList), selectedItems -> {
            // User confirmed selection - handle here
            // e.g., start edit/next activity with selectedItems
        });
        sheet.show(getSupportFragmentManager(), "multi_select");
    }

    // ─── Permissions ──────────────────────────────────────────────────────────

    private void requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+: READ_MEDIA_VIDEO + READ_MEDIA_IMAGES
            boolean videoGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED;
            boolean imageGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;

            if (videoGranted && imageGranted) {
                loadAllMedia();
                loadFolders();
            } else {
                permissionLauncher.launch(new String[]{Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_IMAGES});
            }
        } else {
            // Below Android 13: READ_EXTERNAL_STORAGE
            boolean granted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            if (granted) {
                loadAllMedia();
                loadFolders();
            } else {
                permissionLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadAllMedia();
            loadFolders();
        }
    }

    // ─── Media Loading ────────────────────────────────────────────────────────

    private void loadFolders() {
        folderList.clear();

        // Add "Gallery" (All) as first item
        folderList.add(new FolderModel("Gallery", null, 0));

        Uri collection = MediaStore.Files.getContentUri("external");
        String[] projection = {MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.MEDIA_TYPE};

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Cursor cursor = getContentResolver().query(collection, projection, selection, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");

        HashSet<String> folderSet = new HashSet<>();

        if (cursor != null) {
            int folderNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME);
            int pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);

            // Count per folder
            java.util.HashMap<String, Integer> folderCount = new java.util.HashMap<>();
            java.util.HashMap<String, String> folderPaths = new java.util.HashMap<>();
            java.util.HashMap<String, String> folderThumbs = new java.util.HashMap<>();

            while (cursor.moveToNext()) {
                String fName = cursor.getString(folderNameIndex);
                String path = cursor.getString(pathIndex);
                if (fName == null) continue;

                folderCount.put(fName, folderCount.getOrDefault(fName, 0) + 1);
                if (!folderPaths.containsKey(fName)) {
                    File file = new File(path);
                    folderPaths.put(fName, file.getParent());
                    folderThumbs.put(fName, path); // first item as thumbnail
                }
            }
            cursor.close();

            for (String fName : folderPaths.keySet()) {
                folderList.add(new FolderModel(fName, folderPaths.get(fName), folderCount.getOrDefault(fName, 0), folderThumbs.get(fName)));
            }
        }

        // Update "Gallery" count
        folderList.get(0).setCount(mediaList.size());
        folderDropdownAdapter.notifyDataSetChanged();
    }

    private void loadAllMedia() {
        mediaList.clear();
        Uri collection = MediaStore.Files.getContentUri("external");

        String[] projection = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Video.Media.DURATION};

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Cursor cursor = getContentResolver().query(collection, projection, selection, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");

        if (cursor != null) {
            int pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            int typeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
            int durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);

            while (cursor.moveToNext()) {
                String path = cursor.getString(pathIndex);
                int type = cursor.getInt(typeIndex);
                boolean isVid = type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
                long dur = isVid ? cursor.getLong(durationIndex) : 0;
                mediaList.add(new MediaModel(path, isVid, dur));
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void loadMediaByFolder(String folderPath) {
        mediaList.clear();
        Uri collection = MediaStore.Files.getContentUri("external");

        String[] projection = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Video.Media.DURATION};

        String selection = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + ")" + " AND " + MediaStore.Files.FileColumns.DATA + " LIKE ?";

        Cursor cursor = getContentResolver().query(collection, projection, selection, new String[]{folderPath + "%"}, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");

        if (cursor != null) {
            int pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            int typeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
            int durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);

            while (cursor.moveToNext()) {
                String path = cursor.getString(pathIndex);
                int type = cursor.getInt(typeIndex);
                boolean isVid = type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
                long dur = isVid ? cursor.getLong(durationIndex) : 0;
                mediaList.add(new MediaModel(path, isVid, dur));
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }
}