package com.infotech.wishmaplus.Api.Object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LevelCountResult implements Parcelable{


    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("level")
    @Expose
    private int level;
    @SerializedName("activeCount")
    @Expose
    private int activeCount;
    @SerializedName("deActiveCount")
    @Expose
    private int deActiveCount;
    @SerializedName("isMultiLayerTeamDisplay")
    @Expose
    private boolean isMultiLayerTeamDisplay;
    @SerializedName("maxReportDisplayLevel")
    @Expose
    private int maxReportDisplayLevel;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("package")
    @Expose
    private String package_;
    @SerializedName("packageCost")
    @Expose
    private double packageCost;
    @SerializedName("comm")
    @Expose
    private double comm;


    protected LevelCountResult(Parcel in) {
        status = in.readByte() != 0;
        count = in.readInt();
        level = in.readInt();
        activeCount = in.readInt();
        deActiveCount = in.readInt();
        isMultiLayerTeamDisplay = in.readByte() != 0;
        maxReportDisplayLevel = in.readInt();
        userId = in.readString();
        userName = in.readString();
        package_ = in.readString();
        packageCost = in.readDouble();
        comm = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt((byte) (status ? 1 : 0));
        dest.writeInt(count);
        dest.writeInt(level);
        dest.writeInt(activeCount);
        dest.writeInt(deActiveCount);
        dest.writeByte((byte) (isMultiLayerTeamDisplay ? 1 : 0));
        dest.writeInt(maxReportDisplayLevel);
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(package_);
        dest.writeDouble(packageCost);
        dest.writeDouble(comm);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public static final Creator<LevelCountResult> CREATOR = new Creator<LevelCountResult>() {
        @Override
        public LevelCountResult createFromParcel(Parcel in) {
            return new LevelCountResult(in);
        }

        @Override
        public LevelCountResult[] newArray(int size) {
            return new LevelCountResult[size];
        }
    };

    public boolean getStatus() {
        return status;
    }

    public int getCount() {
        return count;
    }

    public int getLevel() {
        return level;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public int getDeActiveCount() {
        return deActiveCount;
    }

    public boolean isMultiLayerTeamDisplay() {
        return isMultiLayerTeamDisplay;
    }

    public int getMaxReportDisplayLevel() {
        return maxReportDisplayLevel;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPackage_() {
        return package_;
    }

    public double getPackageCost() {
        return packageCost;
    }

    public double getComm() {
        return comm;
    }
}
