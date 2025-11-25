package com.infotech.wishmaplus.Utils.VideoEdit;


/**
 * Created by Vishnu Agarwal on 08-10-2024.
 */

public enum VideoEditConstant {
    INSTANCE;
    public int AUDIO_TRIM = 1;
    public int VIDEO_AUDIO_MERGE = 2;
    public int IMAGE_AUDIO_MERGE = 3;

    public String FLIRT = "filter";
    public String TRIM = "trim";
    public String MUSIC = "music";
    public String PLAYBACK = "playback";
    public String TEXT = "text";
    public String OBJECT = "object";
    public String MERGE = "merge";
    public String TRANSITION = "transition";
    public String TYPE_VIDEO = "video";

    public String SPEED_0_25 = "0.25x";
    public String SPEED_0_5 = "0.5x";
    public String SPEED_0_75 = "0.75x";
    public String SPEED_1_0 = "1.0x";
    public String SPEED_1_25 = "1.25x";
    public String SPEED_1_5 = "1.5x";

    public int VIDEO_GALLERY = 101;
    public int RECORD_VIDEO = 102;
    public int AUDIO_GALLERY = 103;
    public int VIDEO_MERGE_1 = 104;
    public int VIDEO_MERGE_2 = 105;
    public int ADD_ITEMS_IN_STORAGE = 106;
    public int MAIN_VIDEO_TRIM = 107;

   /* val PERMISSION_CAMERA = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)
    val PERMISSION_STORAGE = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)*/

    public String BOTTOM_LEFT = "BottomLeft";
    public String BOTTOM_RIGHT = "BottomRight";
    public String CENTRE = "Center";
    public String CENTRE_ALIGN = "CenterAlign";
    public String CENTRE_BOTTOM = "CenterBottom";
    public String TOP_LEFT = "TopLeft";
    public String TOP_RIGHT = "TopRight";

    public String CLIP_ARTS = ".ClipArts";
    public String FONT = ".Font";
    public String DEFAULT_FONT = "roboto_black.ttf";
    public String MY_VIDEOS = "MyVideos";

    public String DATE_FORMAT = "yyyyMMdd_HHmmss";
    public String VIDEO_FORMAT = ".mp4";
    public String AUDIO_FORMAT = ".mp3";
    public String AVI_FORMAT = ".avi";

    public int VIDEO_LIMIT = 4; //4 minutes
}
