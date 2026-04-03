package com.infotech.wishmaplus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GeetReelCommentsResponse implements Serializable {
    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("responseText")
    @Expose
    private String responseText;
    @SerializedName("result")
    @Expose
    private CommentData result;

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

    public CommentData getResult() {
        return result;
    }

    public void setResult(CommentData result) {
        this.result = result;
    }
}
