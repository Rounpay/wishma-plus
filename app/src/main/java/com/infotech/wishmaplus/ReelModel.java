package com.infotech.wishmaplus;

import java.io.Serializable;

public class ReelModel implements Serializable {

    private String id;
    private String userName;
    private String userAvatar;
    private String videoPath;
    private String description;
    private String location;
    private long likes;
    private long comments;
    private long shares;
    private boolean isLiked;
    private boolean isFollowing;
    private String musicName;

    public ReelModel(String id, String userName, String userAvatar,
                     String videoPath, String description, String location,
                     long likes, long comments, long shares,
                     boolean isLiked, boolean isFollowing) {
        this.id = id;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.videoPath = videoPath;
        this.description = description;
        this.location = location;
        this.likes = likes;
        this.comments = comments;
        this.shares = shares;
        this.isLiked = isLiked;
        this.isFollowing = isFollowing;
        this.musicName = "Original Audio";
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public long getLikes() {
        return likes;
    }

    public long getComments() {
        return comments;
    }

    public long getShares() {
        return shares;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setLikes(long l) {
        this.likes = l;
    }

    public void setComments(long c) {
        this.comments = c;
    }

    public void setShares(long s) {
        this.shares = s;
    }

    public void setLiked(boolean b) {
        this.isLiked = b;
    }

    public void setFollowing(boolean b) {
        this.isFollowing = b;
    }

    public void setMusicName(String m) {
        this.musicName = m;
    }
}