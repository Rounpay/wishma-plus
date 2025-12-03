package com.infotech.wishmaplus.Api.Response;

public class UserModelResponse {
    private String name;
    private int image;
    private boolean isVerified;

    public UserModelResponse(String name, int image, boolean isVerified) {
        this.name = name;
        this.image = image;
        this.isVerified = isVerified;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public boolean isVerified() {
        return isVerified;
    }
}
