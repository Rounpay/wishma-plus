package com.infotech.wishmaplus;

import java.io.Serializable;

public class ReelModel implements Serializable {

    private int reelId;
    private String userId;
    private String fullName;
    private String profilePictureUrl;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private boolean isLiked;
    private String videoUrl;
    private String thumbnailUrl;
    private String caption;
    private int duration;
    private String hashtags;

    // Getter methods

    public int getReelId() {
        return reelId;
    }

    public String getUserName() {
        return fullName;
    }

    public String getUserAvatar() {
        return profilePictureUrl;
    }

    public String getVideoPath() {
        return videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getDescription() {
        return caption;
    }

    public long getLikes() {
        return likeCount;
    }

    public long getComments() {
        return commentCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

}