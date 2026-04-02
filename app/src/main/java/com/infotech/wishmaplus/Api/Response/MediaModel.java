package com.infotech.wishmaplus.Api.Response;

import java.io.Serializable;

public class MediaModel implements Serializable {

    private String path;
    private boolean isVideo;
    private long duration;
    private boolean isSelected = false;
    private int order = 0; // selection order (1-based), 0 = not selected

    public MediaModel(String path, boolean isVideo, long duration) {
        this.path = path;
        this.isVideo = isVideo;
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public int getOrder() {
        return order;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
