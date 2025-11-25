package com.infotech.wishmaplus.Api.Object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportReasonResult implements Parcelable{
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("isSelected")
    @Expose
    private boolean isSelected;







    protected ReportReasonResult(Parcel in) {
        id = in.readInt();
        reason = in.readString();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(reason);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public static final Creator<ReportReasonResult> CREATOR = new Creator<ReportReasonResult>() {
        @Override
        public ReportReasonResult createFromParcel(Parcel in) {
            return new ReportReasonResult(in);
        }

        @Override
        public ReportReasonResult[] newArray(int size) {
            return new ReportReasonResult[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
