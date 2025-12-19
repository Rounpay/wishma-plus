package com.infotech.wishmaplus.Api.Response;

import com.infotech.wishmaplus.Api.Object.PgRespData;

public class BoostData {

    private PgRespData pgResponse;
    private int statusCode;
    private String responseText;

    public PgRespData getPgResponse() {
        return pgResponse;
    }

    public void setPgResponse(PgRespData pgResponse) {
        this.pgResponse = pgResponse;
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
