package com.infotech.wishmaplus.Api.Object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CommentResult {

    @SerializedName("commentId")
    @Expose
    private String commentId;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("commentedAt")
    @Expose
    private String commentedAt;
    @SerializedName("replyId")
    @Expose
    private String replyId;
    @SerializedName("fisrtName")
    @Expose
    public String fisrtName;
    @SerializedName("lastName")
    @Expose
    public String lastName;
    @SerializedName("profilePictureUrl")
    @Expose
    private String profilePictureUrl;
    @SerializedName("totalLikes")
    @Expose
    private String totalLikes;
    @SerializedName("totalComments")
    @Expose
    private int totalComments;
    @SerializedName("replyCount")
    @Expose
    private int replyCount;
    @SerializedName("lastCommentAt")
    @Expose
    private String lastCommentAt;
    @SerializedName("lastLikeAt")
    @Expose
    private String lastLikeAt;
    @SerializedName("isLiked")
    @Expose
    private boolean isLiked;
    @SerializedName("replies")
    @Expose
    private ArrayList<CommentResult> replies;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentedAt() {
        return commentedAt;
    }

    public void setCommentedAt(String commentedAt) {
        this.commentedAt = commentedAt;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getFisrtName() {
        return fisrtName;
    }

    public void setFisrtName(String fisrtName) {
        this.fisrtName = fisrtName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public String getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(String totalLikes) {
        this.totalLikes = totalLikes;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public String getLastCommentAt() {
        return lastCommentAt;
    }

    public void setLastCommentAt(String lastCommentAt) {
        this.lastCommentAt = lastCommentAt;
    }

    public String getLastLikeAt() {
        return lastLikeAt;
    }

    public void setLastLikeAt(String lastLikeAt) {
        this.lastLikeAt = lastLikeAt;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public void setReplies(ArrayList<CommentResult> replies) {
        this.replies = replies;
    }

    public ArrayList<CommentResult> getReplies() {
        return replies;
    }
}