package com.infotech.wishmaplus.Adapter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FriendSuggestionResponse {

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("responseText")
    private String responseText;

    @SerializedName("result")
    private List<FriendSuggestionItem> result;

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public List<FriendSuggestionItem> getResult() {
        return result;
    }
}
