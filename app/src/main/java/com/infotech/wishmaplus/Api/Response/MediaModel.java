package com.infotech.wishmaplus.Api.Response;

public class MediaModel {
    String path;
    boolean isVideo;
    long duration;

    public MediaModel(String path, boolean isVideo, long duration) {
        this.path = path;
        this.isVideo = isVideo;
        this.duration = duration;
    }

    public String getPath() { return path; }
    public boolean isVideo() { return isVideo; }
    public long getDuration() { return duration; }
}