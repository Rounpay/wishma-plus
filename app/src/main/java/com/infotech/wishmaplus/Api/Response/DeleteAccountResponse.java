package com.infotech.wishmaplus.Api.Response;

public class DeleteAccountResponse {
    private int statusCode;
    private String responseText;

    public DeleteAccountResponse(int statusCode, String responseText) {
        this.statusCode = statusCode;
        this.responseText = responseText;
    }


    public DeleteAccountResponse() {}

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseText() {
        return responseText;
    }


    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }
}
