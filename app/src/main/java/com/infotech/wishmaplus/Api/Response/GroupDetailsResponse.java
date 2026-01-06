package com.infotech.wishmaplus.Api.Response;
import com.infotech.wishmaplus.Api.Object.GroupResult;

public class GroupDetailsResponse {
    private int statusCode;
    private String responseText;
    private GroupResult result;

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

    public GroupResult getResult() {
        return result;
    }

    public void setResult(GroupResult result) {
        this.result = result;
    }
}
