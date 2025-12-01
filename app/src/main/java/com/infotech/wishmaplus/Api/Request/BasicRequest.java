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

    public BasicRequest(String fromUserId, int status) {
        this.fromUserId = fromUserId;
        this.status = status;
    }
}
