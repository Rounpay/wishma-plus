package com.infotech.wishmaplus;

import java.io.Serializable;
import java.util.List;

public class GetReelResponse implements Serializable {

    public List<ReelModel> result;
    public int statusCode;
    public String responseText;

    public List<ReelModel> getResult() {
        return result;
    }

    public void setResult(List<ReelModel> result) {
        this.result = result;
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