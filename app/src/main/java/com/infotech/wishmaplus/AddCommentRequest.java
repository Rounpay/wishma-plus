package com.infotech.wishmaplus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddCommentRequest implements Serializable {
    @SerializedName("reelId")
    @Expose
    private int reelId;
    @SerializedName("commentText")
    @Expose
    private String commentText;
    @SerializedName("parentCommentId")
    @Expose
    private int parentCommentId;
    public AddCommentRequest(int reelId, String commentText, int parentCommentId) {
        this.reelId = reelId;
        this.commentText = commentText;
        this.parentCommentId = parentCommentId;
    }

}
