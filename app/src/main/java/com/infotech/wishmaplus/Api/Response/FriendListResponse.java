package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FriendListResponse {
    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("responseText")
    private String responseText;

    @SerializedName("result")
    private List<FriendUserModel> result;

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public List<FriendUserModel> getResult() {
        return result;
    }
}
