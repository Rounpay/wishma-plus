package com.infotech.wishmaplus.Api.Response;

import java.util.List;

public class GetUserListResponse {
    private int statusCode;
    private String responseText;
    private List<UserResult> result;

    // Getter & Setter
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

    public List<UserResult> getResult() {
        return result;
    }

    public void setResult(List<UserResult> result) {
        this.result = result;
    }
}
