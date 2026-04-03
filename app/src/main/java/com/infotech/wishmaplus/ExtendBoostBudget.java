package com.infotech.wishmaplus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ExtendBoostBudget implements Serializable {
    @SerializedName("boostId")
    @Expose
    private int boostId;
    @SerializedName("tid")
    @Expose
    private String tid;
    @SerializedName("salt")
    @Expose
    private String salt;
    @SerializedName("phoneNo")
    @Expose
    private String phoneNo;
    @SerializedName("budget")
    @Expose
    private int budget;
    @SerializedName("estimatedCost")
    @Expose
    private int estimatedCost;
    @SerializedName("subTotal")
    @Expose
    private int subTotal;
    @SerializedName("gstAmount")
    @Expose
    private int gstAmount;
    @SerializedName("total")
    @Expose
    private int total;

    public ExtendBoostBudget(int boostId, String tid, String salt,
                             String phoneNo, int budget,
                             int estimatedCost, int subTotal,
                             int gstAmount, int total) {
        this.boostId = boostId;
        this.tid = tid;
        this.salt = salt;
        this.phoneNo = phoneNo;
        this.budget = budget;
        this.estimatedCost = estimatedCost;
        this.subTotal = subTotal;
        this.gstAmount = gstAmount;
        this.total = total;
    }
}
