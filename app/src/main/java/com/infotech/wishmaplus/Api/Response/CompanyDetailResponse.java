package com.infotech.wishmaplus.Api.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.infotech.wishmaplus.Api.Object.PackageResult;

import java.util.List;

public class CompanyDetailResponse implements Parcelable {

    @SerializedName("phoneNos")
    @Expose
    private List<String> phoneNos;
    @SerializedName("whatsAppNo")
    @Expose
    private String whatsAppNo;
    @SerializedName("emailID")
    @Expose
    private String emailID;
    @SerializedName("companyName")
    @Expose
    private String companyName;
    @SerializedName("address")
    @Expose
    private String address;


    protected CompanyDetailResponse(Parcel in) {
        phoneNos = in.createStringArrayList();
        whatsAppNo = in.readString();
        emailID = in.readString();
        companyName = in.readString();
        address = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(phoneNos);
        dest.writeString(whatsAppNo);
        dest.writeString(emailID);
        dest.writeString(companyName);
        dest.writeString(address);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public static final Creator<CompanyDetailResponse> CREATOR = new Creator<CompanyDetailResponse>() {
        @Override
        public CompanyDetailResponse createFromParcel(Parcel in) {
            return new CompanyDetailResponse(in);
        }

        @Override
        public CompanyDetailResponse[] newArray(int size) {
            return new CompanyDetailResponse[size];
        }
    };

    public List<String> getPhoneNos() {
        return phoneNos;
    }

    public String getWhatsAppNo() {
        return whatsAppNo;
    }

    public String getEmailID() {
        return emailID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getAddress() {
        return address;
    }
}