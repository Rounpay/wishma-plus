package com.infotech.wishmaplus.Api.Object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PackageResult implements Parcelable{
    @SerializedName("validityInDays")
    @Expose
    private int validityInDays;
    @SerializedName("dailyPostAllowed")
    @Expose
    private int dailyPostAllowed;
    @SerializedName("dailyPost")
    @Expose
    private int dailyPost;
    @SerializedName("packageID")
    @Expose
    private int packageID;
    @SerializedName("packageCost")
    @Expose
    private double packageCost;
    @SerializedName("packageName")
    @Expose
    private String packageName;
    @SerializedName("expiryInDays")
    @Expose
    private String expiryInDays;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("isActive")
    @Expose
    private boolean isActive;
    @SerializedName("isDailyPostLimitExceed")
    @Expose
    private boolean isDailyPostLimitExceed;
    @SerializedName("isMixingAllowed")
    @Expose
    private boolean isMixingAllowed;
    @SerializedName("isMusicFromSystemOnly")
    @Expose
    private boolean isMusicFromSystemOnly;
    @SerializedName("isTextCanPost")
    @Expose
    private boolean isTextCanPost;
    @SerializedName("isVideoCanPost")
    @Expose
    private boolean isVideoCanPost;
    @SerializedName("isImageCanPost")
    @Expose
    private boolean isImageCanPost;
    @SerializedName("isLifeTimeValidity")
    @Expose
    private boolean isLifeTimeValidity;







    protected PackageResult(Parcel in) {
        validityInDays = in.readInt();
        dailyPostAllowed = in.readInt();
        dailyPost = in.readInt();
        packageID = in.readInt();
        packageCost = in.readDouble();
        packageName = in.readString();
        expiryInDays = in.readString();
        imageUrl = in.readString();
        description = in.readString();
        isActive = in.readByte() != 0;
        isDailyPostLimitExceed = in.readByte() != 0;
        isMixingAllowed = in.readByte() != 0;
        isMusicFromSystemOnly = in.readByte() != 0;
        isTextCanPost = in.readByte() != 0;
        isVideoCanPost = in.readByte() != 0;
        isImageCanPost = in.readByte() != 0;
        isLifeTimeValidity = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(validityInDays);
        dest.writeInt(dailyPostAllowed);
        dest.writeInt(dailyPost);
        dest.writeInt(packageID);
        dest.writeDouble(packageCost);
        dest.writeString(packageName);
        dest.writeString(expiryInDays);
        dest.writeString(imageUrl);
        dest.writeString(description);
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeByte((byte) (isDailyPostLimitExceed ? 1 : 0));
        dest.writeByte((byte) (isMixingAllowed ? 1 : 0));
        dest.writeByte((byte) (isMusicFromSystemOnly ? 1 : 0));
        dest.writeByte((byte) (isTextCanPost ? 1 : 0));
        dest.writeByte((byte) (isVideoCanPost ? 1 : 0));
        dest.writeByte((byte) (isImageCanPost ? 1 : 0));
        dest.writeByte((byte) (isLifeTimeValidity ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public static final Creator<PackageResult> CREATOR = new Creator<PackageResult>() {
        @Override
        public PackageResult createFromParcel(Parcel in) {
            return new PackageResult(in);
        }

        @Override
        public PackageResult[] newArray(int size) {
            return new PackageResult[size];
        }
    };

    public int getValidityInDays() {
        return validityInDays;
    }

    public int getDailyPostAllowed() {
        return dailyPostAllowed;
    }

    public int getDailyPost() {
        return dailyPost;
    }

    public boolean isDailyPostLimitExceed() {
        return isDailyPostLimitExceed;
    }

    public boolean isMixingAllowed() {
        return isMixingAllowed;
    }

    public boolean isMusicFromSystemOnly() {
        return isMusicFromSystemOnly;
    }

    public boolean isTextCanPost() {
        return isTextCanPost;
    }

    public boolean isVideoCanPost() {
        return isVideoCanPost;
    }

    public boolean isImageCanPost() {
        return isImageCanPost;
    }

    public boolean isLifeTimeValidity() {
        return isLifeTimeValidity;
    }

    public int getPackageID() {
        return packageID;
    }

    public double getPackageCost() {
        return packageCost;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getExpiryInDays() {
        return expiryInDays;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return isActive;
    }
}
