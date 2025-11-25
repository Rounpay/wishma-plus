package com.infotech.wishmaplus.Api.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateUserRequest {
    @SerializedName("fisrtName")
    @Expose
    public String fisrtName;
    @SerializedName("lastName")
    @Expose
    public String lastName;
    @SerializedName("cityId")
    @Expose
    public int cityId;
    @SerializedName("cityName")
    @Expose
    public String cityName;
    @SerializedName("stateId")
    @Expose
    public int stateId;
    @SerializedName("stateName")
    @Expose
    public String stateName;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("bio")
    @Expose
    public String bio;
    @SerializedName("gender")
    @Expose
    public int gender;
    @SerializedName("bankId")
    @Expose
    public int bankId;
    @SerializedName("branchName")
    @Expose
    public String branchName;
    @SerializedName("accountNumber")
    @Expose
    public String accountNumber;
    @SerializedName("ifsc")
    @Expose
    public String ifsc;
    @SerializedName("accountHolder")
    @Expose
    public String accountHolder;


    public UpdateUserRequest(String firstName, String lastName, int cityId, String city, int stateId, String state, String address, String bio,
                             int gender,int bankId,String branchName,String accountNumber,String ifsc,String accountHolder) {
        this.fisrtName = firstName;
        this.lastName = lastName;
        this.cityId = cityId;
        this.cityName = city;
        this.stateId = stateId;
        this.stateName = state;
        this.address = address;
        this.bio = bio;
        this.gender = gender;
        this.bankId = bankId;
        this.branchName = branchName;
        this.accountNumber = accountNumber;
        this.ifsc = ifsc;
        this.accountHolder = accountHolder;
    }
}
