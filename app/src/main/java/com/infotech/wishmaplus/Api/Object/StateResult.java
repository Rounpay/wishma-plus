package com.infotech.wishmaplus.Api.Object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StateResult {
    @SerializedName("stateId")
    @Expose
    private int stateId;
    @SerializedName("stateName")
    @Expose
    private String stateName;

    public int getStateId() {
        return stateId;
    }

    public String getStateName() {
        return stateName;
    }
}
