package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vishnu Agarwal on 24-10-2024.
 */

public class BasicResponse {
    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("responseText")
    @Expose
    private String responseText;

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseText() {
        return responseText;
    }


}
