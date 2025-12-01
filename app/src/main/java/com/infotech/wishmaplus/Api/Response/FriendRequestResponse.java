package com.infotech.wishmaplus.Api.Response;

public class FriendRequestResponse {
    private String name;
    private String details;
    private int imageRes;

    public FriendRequestResponse(String name, String details, int imageRes) {
        this.name = name;
        this.details = details;
        this.imageRes = imageRes;
    }

    public String getName() { return name; }
    public String getDetails() { return details; }
    public int getImageRes() { return imageRes; }
}
