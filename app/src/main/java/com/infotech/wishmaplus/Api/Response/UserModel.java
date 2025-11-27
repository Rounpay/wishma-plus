package com.infotech.wishmaplus.Api.Response;

public class UserModel {
    String name;
    int image;

    public UserModel(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }
}
