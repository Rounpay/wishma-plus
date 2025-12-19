package com.infotech.wishmaplus.Api.Response;

public class BoostResponse {

    private int boostId;
    private int pgid;
    private boolean isPgActive;
    private BoostData data;
    private int statusCode;
    private String responseText;

    // Getters & Setters
    public int getBoostId() {
        return boostId;
    }

    public void setBoostId(int boostId) {
        this.boostId = boostId;
    }

    public int getPgid() {
        return pgid;
    }

    public void setPgid(int pgid) {
        this.pgid = pgid;
    }

    public boolean isPgActive() {
        return isPgActive;
    }

    public void setPgActive(boolean pgActive) {
        isPgActive = pgActive;
    }

    public BoostData getData() {
        return data;
    }

    public void setData(BoostData data) {
        this.data = data;
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
