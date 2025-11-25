package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.infotech.wishmaplus.Api.Object.ResultResp;

public class LoginResponse {
    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("responseText")
    @Expose
    private String responseText;
    @SerializedName("result")
    @Expose
    private ResultResp result;

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

    public ResultResp getResult() {
        return result;
    }

    public void setResult(ResultResp result) {
        this.result = result;
    }
}