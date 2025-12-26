package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.SerializedName;

public class UploadGroupCoverResponse {
    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("responseText")
    private String responseText;

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseText() {
        return responseText;
    }
}
