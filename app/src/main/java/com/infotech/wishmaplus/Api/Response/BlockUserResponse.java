package com.infotech.wishmaplus.Api.Response;

public class BlockUserResponse {

    private int statusCode;
    private String responseText;

    public BlockUserResponse() {
        // Empty constructor
    }

    public BlockUserResponse(int statusCode, String responseText) {
        this.statusCode = statusCode;
        this.responseText = responseText;
    }

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
