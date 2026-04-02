package com.infotech.wishmaplus;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SaveReelResponse implements Serializable {

    @SerializedName("statusCode")
    public int statusCode;

    @SerializedName("responseText")
    public String responseText;

    @SerializedName("result")
    public Object result; // adjust type based on actual API response

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

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
