package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.infotech.wishmaplus.Api.Object.FollowerResult;
import com.infotech.wishmaplus.Api.Object.UpgradePackageData;

public class UpgradePackageResponse{

    @SerializedName("pgid")
    @Expose
    private int pgid;
    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("isPgActive")
    @Expose
    private boolean isPgActive;
    @SerializedName("responseText")
    @Expose
    private String responseText;
    @SerializedName("data")
    @Expose
    private UpgradePackageData data;

    public int getPgid() {
        return pgid;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isPgActive() {
        return isPgActive;
    }

    public String getResponseText() {
        return responseText;
    }

    public UpgradePackageData getData() {
        return data;
    }
}