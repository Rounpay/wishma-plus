package com.infotech.wishmaplus.Api.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportPostRequest {
    @SerializedName("postId")
    @Expose
    public String postId;
    @SerializedName("reasonId")
    @Expose
    public int reasonId;

    public ReportPostRequest(String postId, int reasonId) {
        this.postId = postId;
        this.reasonId = reasonId;
    }
}
