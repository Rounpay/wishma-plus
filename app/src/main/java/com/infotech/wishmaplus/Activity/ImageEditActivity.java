package com.infotech.wishmaplus.Activity;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApplicationConstant;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.FileUtils;
import com.infotech.wishmaplus.Utils.Utility;
import com.infotech.wishmaplus.Utils.VideoEdit.Trimmer.TrimView;
import com.infotech.wishmaplus.Utils.VideoEdit.VideoEditConstant;
import com.infotech.wishmaplus.Utils.VideoEdit.VideoEditor;
import com.infotech.wishmaplus.Utils.VideoEdit.interfaces.OptiFFMpegCallback;
import com.wishmaplus.image.picker.ImagePicker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ImageEditActivity extends AppCompatActivity implements OptiFFMpegCallback {

    private String videoPath;
    private VideoView videoView;
    private ImageView playBtn, musicAlbum, imageView;
    private TextView musicName;
    private View musicDetails, imageGallery, camera, music, send, closeBtn;
    /*private RangeSlider seekBar;*/
    //private File captureFile;
    private int positionVideo;
    private Snackbar mSnackBar;
    private MediaPlayer musicMediaPlayer;
    private VideoEditor videoEditor;
    /*private long videoDuration;*/
    private File masterAudioFile;

    private CustomLoader loader;
    private File convertedAudioFile;
    private boolean isDeleteVideoAllow = false;
    TrimView trimView;
    private boolean isMusicFromSystemOnly;
    private int postType;
    private ImagePicker imagePicker;
    private final int REQUEST_PERMISSIONS_MUSIC = 8642;
    private int REQUEST_PERMISSIONS_CAMERA=9090;
    private int REQUEST_PERMISSIONS_GALLERY=7654;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_image_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.imageEd), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        isMusicFromSystemOnly = getIntent().getBooleanExtra("MusicFromSystemOnly",false);
        videoPath = getIntent().getStringExtra("ImagePath");
        postType = getIntent().getIntExtra("postType",1);
        videoEditor = new VideoEditor(this);
        musicDetails = findViewById(R.id.musicDetails);
        imageView = findViewById(R.id.imageView);
        closeBtn = findViewById(R.id.closeBtn);
        musicName = findViewById(R.id.musicName);
        musicAlbum = findViewById(R.id.musicAlbum);
        /*seekBar = findViewById(R.id.seekBar);*/
        playBtn = findViewById(R.id.playBtn);
        videoView = findViewById(R.id.videoView);
        imageGallery = findViewById(R.id.imageGallery);
        camera = findViewById(R.id.camera);
        music = findViewById(R.id.music);
        send = findViewById(R.id.send);
        imageView.setImageURI(Uri.parse(videoPath));
        //videoView.setVideoPath(videoPath);


        trimView = findViewById(R.id.trimmer);


        trimView.onTrimChangeListener = new TrimView.TrimChangeListener() {


            @Override
            public void onDragStarted(long trimStart, long trim) {
                trimView.setProgress(0);

            }

            @Override
            public void onLeftEdgeChanged(long trimStart, long trim) {
                trimView.setProgress(0);

                if (musicMediaPlayer != null) {
                    positionVideo = 0;
                    playBtn.setVisibility(View.GONE);
                    videoView.seekTo(positionVideo);
                    videoView.start();
                    musicMediaPlayer.seekTo((int) trimStart);
                    musicMediaPlayer.start();

                }
            }

            @Override
            public void onRightEdgeChanged(long trimStart, long trim) {
                trimView.setProgress(0);

                /*if (musicMediaPlayer != null) {
                    positionVideo = 0;
                    playBtn.setVisibility(View.GONE);
                    videoView.seekTo(positionVideo);
                    videoView.start();
                    musicMediaPlayer.seekTo((int) trimStart);
                    musicMediaPlayer.start();

                }*/
            }

            @Override
            public void onRangeChanged(long trimStart, long trim) {
                trimView.setProgress(0);

                if (musicMediaPlayer != null) {
                    positionVideo = 0;
                    playBtn.setVisibility(View.GONE);
                    videoView.seekTo(positionVideo);
                    videoView.start();
                    musicMediaPlayer.seekTo((int) trimStart);
                    musicMediaPlayer.start();

                }
            }

            @Override
            public void onDragStopped(long trimStart, long trim) {
                trimView.setProgress(0);
            }
        };


        // videoView.start();
        videoView.setOnCompletionListener(mediaPlayer -> {
            if (musicMediaPlayer != null && musicMediaPlayer.isPlaying()) {
                musicMediaPlayer.pause();
            }
        });
        videoView.setOnClickListener(view -> {
            if (videoView.isPlaying()) {
                playBtn.setVisibility(View.VISIBLE);
                positionVideo = videoView.getCurrentPosition();
                videoView.pause();
                if (musicMediaPlayer != null && musicMediaPlayer.isPlaying()) {
                    musicMediaPlayer.pause();
                }
            } else {
                playBtn.setVisibility(View.GONE);
                videoView.seekTo(positionVideo);
                videoView.start();
                if (musicMediaPlayer != null && !musicMediaPlayer.isPlaying()) {
                    musicMediaPlayer.start();
                }
            }
        });
        /*seekBar.setRangeChangeListener((view, leftPinIndex, rightPinIndex, totalCount) -> {
            if (musicMediaPlayer != null) {
                positionVideo = 0;
                playBtn.setVisibility(View.GONE);
                videoView.seekTo(positionVideo);
                videoView.start();
                musicMediaPlayer.seekTo((int) leftPinIndex);
                musicMediaPlayer.start();

            }
        });*/

        imageGallery.setOnClickListener(view -> {
           // selectMedia();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSIONS_GALLERY);
            }else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_GALLERY);
            }else {
                imagePicker.choosePictureWithoutPermission(false,true);
            }

        });

        camera.setOnClickListener(view -> {
           // selectCamera();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSIONS_CAMERA);
            }else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CAMERA);
            }else {
                imagePicker.choosePictureWithoutPermission(true,false);
            }

        });

        music.setOnClickListener(view -> {


            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, REQUEST_PERMISSIONS_MUSIC);
            }else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_MUSIC);
            }else {
                selectMusic();
            }



        });
        send.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("VideoPath", videoPath);
            intent.putExtra("IS_DELETE_ALLOW", isDeleteVideoAllow);
            setResult(RESULT_OK, intent);
            finish();
        });

        findViewById(R.id.back_button).setOnClickListener(view -> {
            finish();
        });

        closeBtn.setOnClickListener(view -> {

            musicMediaPlayer.pause();
            musicMediaPlayer.release();
            musicMediaPlayer = null;
            masterAudioFile = null;
            imageGallery.setVisibility(View.VISIBLE);
            camera.setVisibility(View.VISIBLE);
            music.setVisibility(View.VISIBLE);
            send.setVisibility(View.VISIBLE);
            musicDetails.setVisibility(View.GONE);
            closeBtn.setVisibility(View.GONE);

            positionVideo = videoView.getCurrentPosition();
            videoView.setVideoPath(videoPath);
            videoView.seekTo(positionVideo);
            videoView.start();
            videoView.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.setVolume(1f, 1f);
            });
        });

        findViewById(R.id.syncBtn).setOnClickListener(view -> {

            videoEditor.stopRunningProcess();
            //if (!videoEditor.isRunning()) {
            //long trimDuration = seekBar.getRightIndex() - seekBar.getLeftIndex();
            long trimDuration = trimView.trim /*- seekBar.getLeftIndex()*/;
            //String convertedSeekValue = convertDuration(videoDuration);
               /* Log.v(tagName, "seekToValue: $seekToValue")
                Log.v(tagName, "maxValue: $maxSeekValue")
                Log.v(tagName, "minValue: $minSeekValue")
                Log.v(tagName, "trimDuration: $trimDuration")

                Log.v(tagName, "convertedSeekValue: $convertedSeekValue")*/
                /*val trimDurationLong = OptiCommonMethods.convertDurationInSec(trimDuration.roundToLong())
                Log.v(tagName, "trimDurationLong: $trimDurationLong")*/

               /* if (trimDuration > videoDuration) {
                    Toast.makeText(this, "Please trim audio under " + convertDuration(videoDuration) + ".", Toast.LENGTH_SHORT).show();
                } else {*/
            //output file is generated and send to video processing
            File outputFile = createAudioFile();
            // Log.v(tagName, "outputFile: ${outputFile.absolutePath}")


            loader.setCancelable(false);
            loader.show();
            videoEditor.setType(VideoEditConstant.INSTANCE.AUDIO_TRIM);
            videoEditor.setAudioFile(masterAudioFile);
            videoEditor.setOutputPath(outputFile.getAbsolutePath());
            // videoEditor.setStartTime(secToTime(seekBar.getLeftIndex()));
            // videoEditor.setEndTime(secToTime(seekBar.getRightIndex()));
            videoEditor.setStartTime(secToTime(trimView.trimStart));
            videoEditor.setEndTime(secToTime(trimView.trimStart + trimView.trim));
            videoEditor.setCallback(this);
            videoEditor.main();
            /*}*/
           /* } else {
                videoEditor.showInProgressToast();
            }*/
        });

        imagePicker = new ImagePicker(this, null, imageUri -> {

            if (imageUri != null) {
                videoPath = imageUri.getPath();
                if (videoPath != null && !videoPath.isEmpty()) {
                    imageView.setImageURI(imageUri);
                    imageView.setVisibility(View.VISIBLE);
                    music.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                    playBtn.setVisibility(View.GONE);
                    isDeleteVideoAllow = false;
                    videoView.pause();
                    videoView.suspend();
                    positionVideo = 0;

                /*videoView.seekTo(positionVideo);
                videoView.start();*/


                }



            }
        }).setWithImageCrop();
    }


    private void selectMusic() {
        try{
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, "audio/*");
        audioResultLauncher.launch(intent);
        }catch (Exception e){
            try{
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("audio/*");
                audioResultLauncher.launch(intent);
            }catch (Exception e1){
                try{
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("audio/*");
                    audioResultLauncher.launch(intent);
                }catch (Exception e2){
                    Toast.makeText(ImageEditActivity.this,"Application is not available",Toast.LENGTH_LONG).show();
                    e2.printStackTrace();
                }


            }
        }
    }

   /* private void selectMedia() {
        captureFile = null;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    private void selectCamera() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        captureFile = createVideoFile(".jpg");
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, getPackageName() + ".provider_smart_image_picker", captureFile));
        activityResultLauncher.launch(cameraIntent);
    }*/

    File createVideoFile(String suffix) {
        String timeStamp = new SimpleDateFormat(ApplicationConstant.INSTANCE.DATE_FORMAT, Locale.getDefault()).format(new Date());
        String imageFileName = getString(R.string.app_name) + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (!storageDir.exists()) storageDir.mkdirs();
        try {
            return File.createTempFile(imageFileName, suffix, storageDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    File createAudioFile() {
        String timeStamp = new SimpleDateFormat(ApplicationConstant.INSTANCE.DATE_FORMAT, Locale.getDefault()).format(new Date());
        String imageFileName = getString(R.string.app_name) + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (!storageDir.exists()) storageDir.mkdirs();
        try {
            return File.createTempFile(imageFileName, VideoEditConstant.INSTANCE.AUDIO_FORMAT, storageDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /*Long getFileDuration(String path) {

        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, Uri.parse(path));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillis = Long.parseLong(time);
            retriever.release();
            return timeInMillis;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }*/




   /* ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == Activity.RESULT_OK) {

            if (captureFile != null) {
                videoPath = captureFile.getPath();
            } else if (result.getData() != null && result.getData().getData() != null) {
                Uri videoUri = result.getData().getData();
                videoPath = FileUtils.getPath(ImageEditActivity.this, videoUri);
            }
            if (videoPath != null && !videoPath.isEmpty()) {
                imageView.setImageURI(Uri.parse(videoPath));
                imageView.setVisibility(View.VISIBLE);
                music.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                playBtn.setVisibility(View.GONE);
                isDeleteVideoAllow = false;
                videoView.pause();
                videoView.suspend();
                positionVideo = 0;

                *//*videoView.seekTo(positionVideo);
                videoView.start();*//*


            }
        }

    });*/


    ActivityResultLauncher<Intent> audioResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == Activity.RESULT_OK) {
            if (result.getData() != null && result.getData().getData() != null) {
                try {

                    Uri selectedAudioUri = result.getData().getData();
                    String path = FileUtils.getPath(this,selectedAudioUri);
                    if(path!=null && !path .isEmpty()){
                        setResultAudio(path);
                    }else {
                        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
                        Cursor cursor = getContentResolver().query(selectedAudioUri, filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String filePath = cursor.getString(columnIndex);
                            cursor.close();

                            setResultAudio(filePath);

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            /*if (captureFile!=null){
                videoPath =  captureFile.getPath();
            } else if(result.getData() != null && result.getData().getData()!=null){
                Uri videoUri = result.getData().getData();
                videoPath = FileUtils.getPath(VideoEditActivity.this, videoUri);
            }
            if (videoPath != null && !videoPath.isEmpty()) {
                playBtn.setVisibility(View.GONE);

                videoView.setVideoPath(videoPath);
                positionVideo=0;
                videoView.seekTo(positionVideo);
                videoView.start();



            }*/
        }

    });

    void setResultAudio(String filePath){
        masterAudioFile = new File(filePath);
        //videoDuration = getFileDuration(videoPath);
        long audioDuration = Utility.INSTANCE.getFileDuration(ImageEditActivity.this,filePath);
                       /* if (videoDuration > audioDuration) {
                            Toast.makeText(this, "Audio duration is not matching with video duration", Toast.LENGTH_SHORT).show();
                        } else {*/
        imageGallery.setVisibility(View.GONE);
        camera.setVisibility(View.GONE);
        music.setVisibility(View.GONE);
        send.setVisibility(View.GONE);
        musicDetails.setVisibility(View.VISIBLE);
        closeBtn.setVisibility(View.VISIBLE);

        trimView.max = audioDuration;
        trimView.minTrim = 0;
        trimView.maxTrim = audioDuration > 60000 ? 60000 : audioDuration;//60 sec
        trimView.setTrim(trimView.maxTrim / 2);


                           /* seekBar.setTickCount((int) audioDuration);
                            seekBar.setRangeIndex(0, videoDuration);
                            seekBar.moveThumb(0, videoDuration);
                            seekBar.setVideoDuration(videoDuration);*/

        Glide.with(ImageEditActivity.this)
                .load(getSongCover(masterAudioFile.getPath()))
                .placeholder(R.drawable.music_ph)
                .error(R.drawable.music_ph)
                .dontAnimate()
                .into(musicAlbum);
        musicName.setText(masterAudioFile.getName());

        playBtn.setVisibility(View.GONE);
        positionVideo = 0;
        videoView.seekTo(0);
        videoView.setOnPreparedListener(mp -> mp.setVolume(0, 0));
        try {
            musicMediaPlayer = new MediaPlayer();
            musicMediaPlayer.setDataSource(masterAudioFile.getPath());
            musicMediaPlayer.prepare();
            musicMediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        /*}*/


        //Log.e("File Path",masterAudioFile.getPath());
                            /*masterAudioFile?.let { file ->
                                    tvSelectedAudio!!.text = masterAudioFile!!.name.toString();
                                //setFilePathFromSource(file)
                                setControls(true);

                                if (Util.SDK_INT <= 23) {
                                    initializePlayer();
                                }
                            }*/
    }

    public byte[] getSongCover(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        return mmr.getEmbeddedPicture();
    }

    public String convertDuration(long duration) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        //Log.v(tagName, "min: " + minutes);

        if (minutes > 0) {
            return minutes + " Minutes";
        } else {
            return "00:" + (TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        }
    }

    String secToTime(Long totalSeconds) {
        return String.format(
                "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totalSeconds),
                TimeUnit.MILLISECONDS.toMinutes(totalSeconds) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalSeconds)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(totalSeconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalSeconds))
        );
    }

    @Override
    protected void onPause() {

        positionVideo = videoView.getCurrentPosition();
        videoView.pause();

        if (musicMediaPlayer != null && musicMediaPlayer.isPlaying()) {
            musicMediaPlayer.pause();
        }
       /* if(mediaPlayer!=null) {
            mediaPlayer.pause();
        }*/
        super.onPause();
    }

    @Override
    protected void onResume() {
        videoView.seekTo(positionVideo);
        videoView.start();
        if (musicMediaPlayer != null && musicDetails.getVisibility() == View.VISIBLE && !musicMediaPlayer.isPlaying()) {
            musicMediaPlayer.start();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        videoView.suspend();
        videoView.stopPlayback();
        if (musicMediaPlayer != null) {
            musicMediaPlayer.pause();
            musicMediaPlayer.release();
        }

        super.onDestroy();
    }

    @Override
    public void onProgress(@NonNull String progress) {
        /* Log.e("Test",progress);*/
    }

    @Override
    public void onSuccess(@NonNull File convertedFile, int type) {
       /* loader.dismiss();
        Log.e("Test Loader"," Dismiss "+type);*/
        if (type == VideoEditConstant.INSTANCE.AUDIO_TRIM) {
            convertedAudioFile = convertedFile;
            videoEditor.setType(VideoEditConstant.INSTANCE.IMAGE_AUDIO_MERGE);
            videoEditor.setAudioFile(convertedFile);
            videoEditor.setAudioDuration((trimView.trimStart + trimView.trim) + "");
            videoEditor.setImagePath(videoPath);
            videoEditor.setOutputPath(createVideoFile(VideoEditConstant.INSTANCE.VIDEO_FORMAT).getAbsolutePath());
            videoEditor.setCallback(this);
            videoEditor.main();
        } else {
            if (convertedAudioFile != null && convertedAudioFile.exists()) {
                convertedAudioFile.delete();
            }
            loader.dismiss();
            isDeleteVideoAllow = true;
            musicMediaPlayer.pause();
            musicMediaPlayer.release();
            musicMediaPlayer = null;
            masterAudioFile = null;
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            playBtn.setVisibility(View.GONE);
            imageGallery.setVisibility(View.VISIBLE);
            camera.setVisibility(View.VISIBLE);
            music.setVisibility(View.GONE);
            send.setVisibility(View.VISIBLE);
            musicDetails.setVisibility(View.GONE);
            closeBtn.setVisibility(View.GONE);
            videoPath = convertedFile.getPath();
            videoView.setVideoPath(videoPath);
            positionVideo = 0;
            videoView.start();
            videoView.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.setVolume(1f, 1f);
            });


        }
    }

    @Override
    public void onFailure(@NonNull Exception error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        loader.dismiss();
    }

   /* @Override
    public void onNotAvailable(@NonNull Exception error) {
        loader.dismiss();
    }*/

    @Override
    public void onCancel() {
        loader.dismiss();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePicker != null) {
            imagePicker.handleActivityResult(resultCode, requestCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_MUSIC) {
            int permissionCheck = PackageManager.PERMISSION_GRANTED;
            for (int permission : grantResults) {
                permissionCheck = permissionCheck + permission;
            }
            if ((grantResults.length > 0) && permissionCheck == PackageManager.PERMISSION_GRANTED) {

                selectMusic();
            } else {
                showWarningSnack(R.string.str_ShowOnPermisstionDenied, "Enable", true);
            }
        }else if (requestCode == REQUEST_PERMISSIONS_CAMERA) {
            int permissionCheck = PackageManager.PERMISSION_GRANTED;
            for (int permission : grantResults) {
                permissionCheck = permissionCheck + permission;
            }
            if ((grantResults.length > 0) && permissionCheck == PackageManager.PERMISSION_GRANTED) {
                imagePicker.choosePictureWithoutPermission(true,false);
            } else {
                showWarningSnack(R.string.str_ShowOnPermisstionDenied, "Enable", true);
            }
        }else if (requestCode == REQUEST_PERMISSIONS_GALLERY) {
            int permissionCheck = PackageManager.PERMISSION_GRANTED;
            for (int permission : grantResults) {
                permissionCheck = permissionCheck + permission;
            }
            if ((grantResults.length > 0) && permissionCheck == PackageManager.PERMISSION_GRANTED) {
                imagePicker.choosePictureWithoutPermission(false,true);
            } else {
                showWarningSnack(R.string.str_ShowOnPermisstionDenied, "Enable", true);
            }
        }else {
            if (imagePicker != null) {
                imagePicker.handlePermission(requestCode, grantResults);
            }
        }
    }

    void showWarningSnack(int stringId, String btn, final boolean isForSetting) {
        if (mSnackBar != null && mSnackBar.isShown()) {
            return;
        }

        mSnackBar = Snackbar.make(findViewById(android.R.id.content), stringId,
                Snackbar.LENGTH_INDEFINITE).setAction(btn,
                v -> {
                    if (isForSetting) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                    } else {
                        ActivityCompat.requestPermissions(ImageEditActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_MUSIC);
                    }

                });

        mSnackBar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
        TextView mainTextView = (TextView) (mSnackBar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
        mainTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(com.intuit.ssp.R.dimen._12ssp));
        mainTextView.setMaxLines(4);
        mSnackBar.show();

    }
}