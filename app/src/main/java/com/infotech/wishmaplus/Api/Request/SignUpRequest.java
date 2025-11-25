package com.infotech.wishmaplus.Api.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignUpRequest {

    @SerializedName("referralID")
    @Expose
    public String referralID;
    @SerializedName("password")
    @Expose
    public String password;
    @SerializedName("confirmPassword")
    @Expose
    public String confirmPassword;
    @SerializedName("phoneNumber")
    @Expose
    public String phoneNumber;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("fisrtName")
    @Expose
    public String fisrtName;
    @SerializedName("lastName")
    @Expose
    public String lastName;


    public SignUpRequest(String referralID, String password, String confirmPassword, String phoneNumber, String email, String fisrtName, String lastName) {
        this.referralID = referralID;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.fisrtName = fisrtName;
        this.lastName = lastName;
    }

}