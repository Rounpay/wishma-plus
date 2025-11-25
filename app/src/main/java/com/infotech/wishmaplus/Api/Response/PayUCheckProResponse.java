package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayUCheckProResponse {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("unmappedstatus")
    @Expose
    private String unmappedstatus;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("txnid")
    @Expose
    private String txnid;
    @SerializedName("transaction_fee")
    @Expose
    private String transactionFee;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("cardCategory")
    @Expose
    private String cardCategory;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("addedon")
    @Expose
    private String addedon;
    @SerializedName("productinfo")
    @Expose
    private String productinfo;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("udf1")
    @Expose
    private String udf1;
    @SerializedName("udf2")
    @Expose
    private String udf2;
    @SerializedName("udf3")
    @Expose
    private String udf3;
    @SerializedName("udf4")
    @Expose
    private String udf4;
    @SerializedName("udf5")
    @Expose
    private String udf5;
    @SerializedName("hash")
    @Expose
    private String hash;
    @SerializedName("field2")
    @Expose
    private String field2;
    @SerializedName("field5")
    @Expose
    private String field5;
    @SerializedName("field6")
    @Expose
    private String field6;
    @SerializedName("field7")
    @Expose
    private String field7;
    @SerializedName("field9")
    @Expose
    private String field9;
    @SerializedName("payment_source")
    @Expose
    private String paymentSource;
    @SerializedName("PG_TYPE")
    @Expose
    private String pgType;
    @SerializedName("bank_ref_no")
    @Expose
    private String bankRefNo;
    @SerializedName("ibibo_code")
    @Expose
    private String ibiboCode;
    @SerializedName("error_code")
    @Expose
    private String errorCode;
    @SerializedName("Error_Message")
    @Expose
    private String errorMessage;
    @SerializedName("name_on_card")
    @Expose
    private String nameOnCard;
    @SerializedName("card_no")
    @Expose
    private String cardNo;
    @SerializedName("is_seamless")
    @Expose
    private int isSeamless;
    @SerializedName("surl")
    @Expose
    private String surl;
    @SerializedName("furl")
    @Expose
    private String furl;


    public long getId() {
        return id;
    }

    public String getMode() {
        return mode;
    }

    public String getStatus() {
        return status;
    }

    public String getUnmappedstatus() {
        return unmappedstatus;
    }

    public String getKey() {
        return key;
    }

    public String getTxnid() {
        return txnid;
    }

    public String getTransactionFee() {
        return transactionFee;
    }

    public String getAmount() {
        return amount;
    }

    public String getCardCategory() {
        return cardCategory;
    }

    public String getDiscount() {
        return discount;
    }

    public String getAddedon() {
        return addedon;
    }

    public String getProductinfo() {
        return productinfo;
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

    public String getUdf1() {
        return udf1;
    }

    public String getUdf2() {
        return udf2;
    }

    public String getUdf3() {
        return udf3;
    }

    public String getUdf4() {
        return udf4;
    }

    public String getUdf5() {
        return udf5;
    }

    public String getHash() {
        return hash;
    }

    public String getField2() {
        return field2;
    }

    public String getField5() {
        return field5;
    }

    public String getField6() {
        return field6;
    }

    public String getField7() {
        return field7;
    }

    public String getField9() {
        return field9;
    }

    public String getPaymentSource() {
        return paymentSource;
    }

    public String getPgType() {
        return pgType;
    }

    public String getBankRefNo() {
        return bankRefNo;
    }

    public String getIbiboCode() {
        return ibiboCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public String getCardNo() {
        return cardNo;
    }

    public int getIsSeamless() {
        return isSeamless;
    }

    public String getSurl() {
        return surl;
    }

    public String getFurl() {
        return furl;
    }
}
