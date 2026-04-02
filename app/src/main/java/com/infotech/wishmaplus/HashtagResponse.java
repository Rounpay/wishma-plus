package com.infotech.wishmaplus;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HashtagResponse {

    @SerializedName("statusCode")
    public int statusCode;

    @SerializedName("responseText")
    public String responseText;

    @SerializedName("result")
    public List<HashtagItem> result;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public List<HashtagItem> getResult() {
        return result;
    }

    public void setResult(List<HashtagItem> result) {
        this.result = result;
    }
}
