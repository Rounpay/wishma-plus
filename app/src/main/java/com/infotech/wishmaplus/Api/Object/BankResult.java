package com.infotech.wishmaplus.Api.Object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankResult {
    @SerializedName(value = "bankId",alternate = {"id"})
    @Expose
    private int bankId;
    @SerializedName(value = "branchName",alternate = {"bank"})
    @Expose
    private String branchName;
    @SerializedName("accountNumber")
    @Expose
    private String accountNumber;

    @SerializedName("ifsc")
    @Expose
    private String ifsc;
    @SerializedName("accountHolder")
    @Expose
    private String accountHolder;

    public int getBankId() {
        return bankId;
    }

    public String getBranchName() {
        return branchName;
    }
    public String getAccountNumber() {return accountNumber;}
    public String getIfsc() {return ifsc;}
    public String getAccountHolder() {return accountHolder;}

}
