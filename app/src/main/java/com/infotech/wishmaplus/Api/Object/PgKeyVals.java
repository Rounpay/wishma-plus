package com.infotech.wishmaplus.Api.Object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PgKeyVals {
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("txnid")
    @Expose
    private String txnid;
    @SerializedName("isProdcution")
    @Expose
    private boolean isProdcution;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("productinfo")
    @Expose
    private String productinfo;
    @SerializedName("surl")
    @Expose
    private String surl;
    @SerializedName("furl")
    @Expose
    private String furl;
    @SerializedName("enforce_paymethod")
    @Expose
    private String enforce_paymethod;
    @SerializedName("hash")
    @Expose
    private String hash;
    @SerializedName("mode")
    @Expose
    public String mode;

    public String getKey() {
        return key;
    }

    public String getTxnid() {
        return txnid;
    }

    public boolean isProdcution() {
        return isProdcution;
    }

    public String getAmount() {
        return amount;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getProductinfo() {
        return productinfo;
    }

    public String getSurl() {
        return surl;
    }

    public String getFurl() {
        return furl;
    }

    public String getEnforce_paymethod() {
        return enforce_paymethod;
    }

    public String getHash() {
        return hash;
    }

    public String getMode() {
        return mode;
    }
}
