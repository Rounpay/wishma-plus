package com.infotech.wishmaplus.Activity;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Adapter.CreateReelAdapter;
import com.infotech.wishmaplus.Adapter.FolderSpinnerAdapter;
import com.infotech.wishmaplus.Api.Response.FolderModel;
import com.infotech.wishmaplus.Api.Response.MediaModel;
import com.infotech.wishmaplus.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CreateReelActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<MediaModel> mediaList = new ArrayList<>();
    ArrayList<FolderModel> folderList = new ArrayList<>();
    CreateReelAdapter adapter;
    LinearLayout galleryLayout;
    CardView multipleSelectBtn;
    Spinner spinnerTxt;
    private boolean isMultiSelect = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_reel);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_VIDEO}, 100);
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }

             recyclerView = findViewById(R.id.videoRecycler);
            ImageView back = findViewById(R.id.back);
             galleryLayout = findViewById(R.id.galleryLayout);
            spinnerTxt = findViewById(R.id.spinnerTxt);
            multipleSelectBtn = findViewById(R.id.multipleSelectBtn);

            back.setOnClickListener(v1 -> finish());
            galleryLayout.setOnClickListener((view)->{
                loadFolders();
            });

           /* multipleSelectBtn.setOnClickListener((v1 -> {
                isMultiSelect = true;
                mediaList.clear();
                adapter.setMultiSelect(true);
            }));*/

            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            adapter = new CreateReelAdapter(this, mediaList);
            recyclerView.setAdapter(adapter);
            loadAllMedia();

            return insets;
        });
    }

    private void loadFolders() {

        folderList.clear();

        Uri collection = MediaStore.Files.getContentUri("external");

        String[] projection = {
                MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATA
        };

        Cursor cursor = getContentResolver().query(
                collection,
                projection,
                null,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );

        HashSet<String> folderSet = new HashSet<>();

        if (cursor != null) {
            int folderNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME);
            int pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);

            while (cursor.moveToNext()) {

                String folderName = cursor.getString(folderNameIndex);
                String path = cursor.getString(pathIndex);

                if (!folderSet.contains(folderName)) {
                    folderSet.add(folderName);

                    File file = new File(path);
                    String folderPath = file.getParent();

                    folderList.add(new FolderModel(folderName, folderPath));
                }
            }

            cursor.close();
        }

        setupSpinner();
    }


    private void setupSpinner() {

        List<String> folderNames = new ArrayList<>();

        for (FolderModel folder : folderList) {
            folderNames.add(folder.getFolderName());
        }

        /*ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                folderNames
        );*/
        FolderSpinnerAdapter adapterSpinner = new FolderSpinnerAdapter(this, folderList);
        spinnerTxt.setAdapter(adapterSpinner);


        spinnerTxt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedPath = folderList.get(position).getFolderPath();
                loadMediaByFolder(selectedPath);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadMediaByFolder(String folderPath) {

        mediaList.clear();

        Uri collection = MediaStore.Files.getContentUri("external");

        String[] projection = {
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Video.Media.DURATION
        };

        String selection =
                "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE +
                        " OR " +
                        MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + ")"
                        + " AND " +
                        MediaStore.Files.FileColumns.DATA + " LIKE ?";

        String[] selectionArgs = new String[]{ folderPath + "%" };

        Cursor cursor = getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );

        if (cursor != null) {

            int pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            int typeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
            int durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);

            while (cursor.moveToNext()) {

                String path = cursor.getString(pathIndex);
                int type = cursor.getInt(typeIndex);

                boolean isVideo = type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
                long duration = isVideo ? cursor.getLong(durationIndex) : 0;

                mediaList.add(new MediaModel(path, isVideo, duration));
            }

            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }
    private void loadAllMedia() {

        mediaList.clear();
        Uri collection = MediaStore.Files.getContentUri("external");

        String[] projection = {
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Video.Media.DURATION
        };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Cursor cursor = getContentResolver().query(
                collection,
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );

        if (cursor != null) {
            int pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            int typeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
            int durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);

            while (cursor.moveToNext()) {

                String path = cursor.getString(pathIndex);
                int type = cursor.getInt(typeIndex);

                boolean isVideo = type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

                long duration = isVideo ? cursor.getLong(durationIndex) : 0;

                mediaList.add(new MediaModel(path, isVideo, duration));
            }

            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }


}