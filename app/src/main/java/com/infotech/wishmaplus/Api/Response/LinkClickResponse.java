package com.infotech.wishmaplus.Api.Response;

public class LinkClickResponse {
    private String url;
    private int statusCode;
    private String responseText;

    // Default constructor
    public LinkClickResponse() {
    }

    // Getters & Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
