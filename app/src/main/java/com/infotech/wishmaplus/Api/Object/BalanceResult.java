package com.infotech.wishmaplus.Api.Object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BalanceResult {
    @SerializedName("balance")
    @Expose
    private double balance;
    @SerializedName("wallet")
    @Expose
    private String wallet;

    public double getBalance() {
        return balance;
    }

    public String getWallet() {
        return wallet;
    }
}
