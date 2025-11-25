package com.infotech.wishmaplus.Api.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SharePostRequest {
    @SerializedName("postId")
    @Expose
    public String postId;
    @SerializedName("caption")
    @Expose
    public String caption;

    public SharePostRequest(String postId, String caption) {
        this.postId = postId;
        this.caption = caption;
    }
}
