package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.SerializedName;

public class EnableDashboardResponse {
    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("responseText")
    private String responseText;

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseText() {
        return responseText;
    }

    @Override
    public String toString() {
        return "SimpleStatusModel{" +
                "statusCode=" + statusCode +
                ", responseText='" + responseText + '\'' +
                '}';
    }
}
