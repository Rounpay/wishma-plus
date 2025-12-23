package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.SerializedName;

public class BoostedPostStatusChangeResponse {

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("responseText")
    private String responseText;

    // Default constructor
    public BoostedPostStatusChangeResponse() {
    }

    // Parameterized constructor
    public BoostedPostStatusChangeResponse(int statusCode, String responseText) {
        this.statusCode = statusCode;
        this.responseText = responseText;
    }

    // Getter and Setter
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
}

