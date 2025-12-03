package com.infotech.wishmaplus.Api.Response;

import java.util.List;

public class PagesResponse {

    private int statusCode;
    private String responseText;
    private List<PageData> result;

    public PagesResponse(int statusCode, String responseText, List<PageData> result) {
        this.statusCode = statusCode;
        this.responseText = responseText;
        this.result = result;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public List<PageData> getResult() {
        return result;
    }
}


