package com.infotech.wishmaplus.Api.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LikeRequest {
    @SerializedName("postId")
    @Expose
    public String postId;
    @SerializedName("commentId")
    @Expose
    public String commentId;

    public LikeRequest(String postId, String commentId) {
        this.postId = postId;
        this.commentId = commentId;
    }
}
