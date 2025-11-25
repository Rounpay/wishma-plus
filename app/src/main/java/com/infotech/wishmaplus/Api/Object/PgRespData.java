package com.infotech.wishmaplus.Api.Object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PgRespData {
    @SerializedName("pgType")
    @Expose
    private int pgType;
    @SerializedName("keyVals")
    @Expose
    private PgKeyVals keyVals;
    @SerializedName("tid")
    @Expose
    private String tid;
    @SerializedName("url")
    @Expose
    private String url;

    public int getPgType() {
        return pgType;
    }

    public PgKeyVals getKeyVals() {
        return keyVals;
    }

    public String getTid() {
        return tid;
    }

    public String getUrl() {
        return url;
    }
}
