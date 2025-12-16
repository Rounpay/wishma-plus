package com.infotech.wishmaplus.Api.Response;

public class EstimateResponse {

    private int statusCode;
    private String responseText;
    private EstimateResult result;

    // Getters and Setters
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

    public EstimateResult getResult() {
        return result;
    }

    public void setResult(EstimateResult result) {
        this.result = result;
    }
}

