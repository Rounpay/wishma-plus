package com.infotech.wishmaplus.Api.Object;

public class FriendModel {
    String name;
    String imageUrl;
    boolean isFriend; // toggle state

    public FriendModel(String name, String imageUrl, boolean isFriend) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.isFriend = isFriend;
    }

    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public boolean isFriend() { return isFriend; }
    public void setFriend(boolean friend) { isFriend = friend; }
}
