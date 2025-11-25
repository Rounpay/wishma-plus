package com.infotech.wishmaplus.Api.Object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpgradePackageData {
    @SerializedName("orderID")
    @Expose
    private String orderID;
    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("responseText")
    @Expose
    private String responseText;
    @SerializedName("pgResponse")
    @Expose
    private PgRespData pgResponse;

    public String getOrderID() {
        return orderID;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public PgRespData getPgResponse() {
        return pgResponse;
    }
}
