package com.infotech.wishmaplus.Api.Response;

public class AddPeopleResponse {
    private int statusCode;
    private String responseText;

    public AddPeopleResponse() {
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
