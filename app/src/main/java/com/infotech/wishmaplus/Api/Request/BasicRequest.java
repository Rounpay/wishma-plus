package com.infotech.wishmaplus.Api.Request;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BasicRequest implements Serializable {
    @SerializedName("fromUserId")
    @Expose
    private String fromUserId;
    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("userId")
    @Expose
    private int userId;

    @SerializedName("block")
    @Expose
    private boolean block;

    @SerializedName("mobileOTP")
    @Expose
    private String mobileOTP;


    public BasicRequest(String fromUserId, int status) {
        this.fromUserId = fromUserId;
        this.status = status;
    }

    public BasicRequest(int userId, boolean block) {
        this.userId = userId;
        this.block = block;
    }

    public BasicRequest(String mobileOTP) {
        this.mobileOTP = mobileOTP;
    }
}
