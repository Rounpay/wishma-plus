package com.infotech.wishmaplus.Api.Response;

public class InsightResponse {

    private int statusCode;
    private String responseText;

    // Default constructor
    public InsightResponse() {}

    // Constructor
    public InsightResponse(int statusCode, String responseText) {
        this.statusCode = statusCode;
        this.responseText = responseText;
    }

    // Getter and Setter for statusCode
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    // Getter and Setter for responseText
    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "statusCode=" + statusCode +
                ", responseText='" + responseText + '\'' +
                '}';
    }
}
