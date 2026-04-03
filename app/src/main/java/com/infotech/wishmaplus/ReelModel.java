package com.infotech.wishmaplus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReelModel implements Serializable {
    public int getReelId() {
        return reelId;
    }

    public void setReelId(int reelId) {
        this.reelId = reelId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean getLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    @SerializedName("reelId")
    @Expose
    private int reelId;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("fullName")
    @Expose
    private String fullName;
    @SerializedName("profilePictureUrl")
    @Expose
    private String profilePictureUrl;
    @SerializedName("viewCount")
    @Expose
    private int viewCount;
    @SerializedName("likeCount")
    @Expose
    private int likeCount;
    @SerializedName("commentCount")
    @Expose
    private int commentCount;
    @SerializedName("isLiked")
    @Expose
    private boolean isLiked;
    @SerializedName("totalCount")
    @Expose
    private int totalCount;
    @SerializedName("videoUrl")
    @Expose
    private String videoUrl;
    @SerializedName("thumbnailUrl")
    @Expose
    private String thumbnailUrl;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("duration")
    @Expose
    private int duration;
    @SerializedName("hashtags")
    @Expose
    private String hashtags;
}