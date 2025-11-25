package com.infotech.wishmaplus.Api.Object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SharedData implements Parcelable {
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("profilePictureUrl")
    @Expose
    private String profilePictureUrl;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("entryAt")
    @Expose
    private String entryAt;
    @SerializedName("fisrtName")
    @Expose
    private String fisrtName;
    @SerializedName("lastName")
    @Expose
    private String lastName;

    protected SharedData(Parcel in) {
        userId = in.readString();
        profilePictureUrl = in.readString();
        caption = in.readString();
        entryAt = in.readString();
        fisrtName = in.readString();
        lastName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(profilePictureUrl);
        dest.writeString(caption);
        dest.writeString(entryAt);
        dest.writeString(fisrtName);
        dest.writeString(lastName);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public static final Creator<SharedData> CREATOR = new Creator<SharedData>() {
        @Override
        public SharedData createFromParcel(Parcel in) {
            return new SharedData(in);
        }

        @Override
        public SharedData[] newArray(int size) {
            return new SharedData[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getCaption() {
        return caption;
    }

    public String getEntryAt() {
        return entryAt;
    }

    public String getFisrtName() {
        return fisrtName;
    }

    public String getLastName() {
        return lastName;
    }
}
