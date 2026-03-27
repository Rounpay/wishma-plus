package com.infotech.wishmaplus.Api.Response;

public class FolderModel {
    String folderName;
    String folderPath;

    public FolderModel(String folderName, String folderPath) {
        this.folderName = folderName;
        this.folderPath = folderPath;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getFolderPath() {
        return folderPath;
    }
}