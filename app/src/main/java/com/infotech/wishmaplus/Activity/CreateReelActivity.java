package com.infotech.wishmaplus.Activity;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Adapter.CreateReelAdapter;
import com.infotech.wishmaplus.Api.Response.MediaModel;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CreateReelActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<MediaModel> mediaList = new ArrayList<>();
    CreateReelAdapter adapter;
    LinearLayout galleryLayout;

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

            back.setOnClickListener(v1 -> finish());
            galleryLayout.setOnClickListener((view)->{
                loadFolders();
            });

            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            adapter = new CreateReelAdapter(this, mediaList);
            recyclerView.setAdapter(adapter);
            loadAllMedia();



            return insets;
        });
    }

    private void loadFolders() {

        HashSet<String> folderSet = new HashSet<>();
        List<String> folderList = new ArrayList<>();
        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projection = {
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME
        };

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {

            int folderIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME);

            while (cursor.moveToNext()) {

                String folder = cursor.getString(folderIndex);

                if (!folderSet.contains(folder)) {
                    folderSet.add(folder);
                    folderList.add(folder);
                }
            }

            cursor.close();
        }

        // show in dropdown (Spinner / BottomSheet)
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

    /*private void loadVideos() {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION
        };
        Cursor cursor = getContentResolver().query(
                uri,
                projection,
                null,
                null,
                MediaStore.Video.Media.DATE_ADDED + " DESC"
        );
        if (cursor != null) {
            int pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            int durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);

            while (cursor.moveToNext()) {
                String path = cursor.getString(pathIndex);
                long duration = cursor.getLong(durationIndex);
                videoList.add(new VideoModel(path, duration));
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }*/
}