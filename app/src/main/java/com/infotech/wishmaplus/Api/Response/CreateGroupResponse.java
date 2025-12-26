package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.SerializedName;

public class CreateGroupResponse {
    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("responseText")
    private String responseText;
    @SerializedName("groupId")
    private String groupId;

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public String getGroupId() {
        return groupId;
    }
}
