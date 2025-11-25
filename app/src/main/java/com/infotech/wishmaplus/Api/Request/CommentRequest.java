package com.infotech.wishmaplus.Api.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentRequest {
    @SerializedName("postId")
    @Expose
    public String postId;
    @SerializedName("replyId")
    @Expose
    public String replyId;
    @SerializedName("comment")
    @Expose
    public String comment;

    public CommentRequest(String postId, String replyId,String comment) {
        this.postId = postId;
        this.replyId = replyId;
        this.comment = comment;
    }
}
