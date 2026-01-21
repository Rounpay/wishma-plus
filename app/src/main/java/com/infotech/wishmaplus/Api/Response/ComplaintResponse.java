package com.infotech.wishmaplus.Api.Response;

import java.util.List;

public class ComplaintResponse {

    private int statusCode;
    private String responseText;
    private List<ComplaintModel> result;

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

    public List<ComplaintModel> getResult() {
        return result;
    }

    public void setResult(List<ComplaintModel> result) {
        this.result = result;
    }
}

