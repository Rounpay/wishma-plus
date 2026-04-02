package com.infotech.wishmaplus.Api.Response;

import java.io.Serializable;

public class FolderModel implements Serializable {

    private String folderName;
    private String folderPath;   // null for "Gallery" (All)
    private int count;
    private String thumbnailPath; // first media path for preview

    // Constructor for "Gallery" (All)
    public FolderModel(String folderName, String folderPath, int count) {
        this.folderName = folderName;
        this.folderPath = folderPath;
        this.count = count;
        this.thumbnailPath = null;
    }

    // Constructor with thumbnail
    public FolderModel(String folderName, String folderPath, int count, String thumbnailPath) {
        this.folderName = folderName;
        this.folderPath = folderPath;
        this.count = count;
        this.thumbnailPath = thumbnailPath;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public int getCount() {
        return count;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setFolderName(String name) {
        this.folderName = name;
    }
}
