package com.infotech.wishmaplus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommentItems implements Serializable {
    @SerializedName("commentId")
    @Expose
    private int commentId;
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
    @SerializedName("commentText")
    @Expose
    private String commentText;
    @SerializedName("parentCommentId")
    @Expose
    private Object parentCommentId;
    @SerializedName("replyCount")
    @Expose
    private int replyCount;
    @SerializedName("isLiked")
    @Expose
    private boolean isLiked;
    @SerializedName("isOwner")
    @Expose
    private boolean isOwner;
    @SerializedName("isOwnerComment")
    @Expose
    private boolean isOwnerComment;
    @SerializedName("totalCount")
    @Expose
    private int totalCount;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

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

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Object getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Object parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public boolean isOwnerComment() {
        return isOwnerComment;
    }

    public void setOwnerComment(boolean ownerComment) {
        isOwnerComment = ownerComment;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
