package com.infotech.wishmaplus.Api.Object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FollowerResult implements Parcelable {


    @SerializedName("userID")
    @Expose
    private String userID;
    @SerializedName("profilePic")
    @Expose
    private String profilePic;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("packageName")
    @Expose
    private String packageName;
    @SerializedName("packageId")
    @Expose
    private int packageId;
    @SerializedName("isSubscribed")
    @Expose
    private boolean isSubscribed;


    protected FollowerResult(Parcel in) {
        userID = in.readString();
        profilePic = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        packageName = in.readString();
        packageId = in.readInt();
        isSubscribed = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(profilePic);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(packageName);
        dest.writeInt(packageId);
        dest.writeByte((byte) (isSubscribed ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public static final Creator<FollowerResult> CREATOR = new Creator<FollowerResult>() {
        @Override
        public FollowerResult createFromParcel(Parcel in) {
            return new FollowerResult(in);
        }

        @Override
        public FollowerResult[] newArray(int size) {
            return new FollowerResult[size];
        }
    };

    public String getUserID() {
        return userID;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getPackageId() {
        return packageId;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }
}
