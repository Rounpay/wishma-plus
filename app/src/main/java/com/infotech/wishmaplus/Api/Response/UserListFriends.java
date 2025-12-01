package com.infotech.wishmaplus.Api.Response;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserListFriends implements Parcelable {
    @SerializedName("requestId")
    @Expose
    private Integer requestId;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("profilePictureUrl")
    @Expose
    private String profilePictureUrl;
    @SerializedName("time")
    @Expose
    private String time;

    protected UserListFriends(Parcel in) {
        if (in.readByte() == 0) {
            requestId = null;
        } else {
            requestId = in.readInt();
        }
        userId = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        profilePictureUrl = in.readString();
        time = in.readString();
    }

    public static final Creator<UserListFriends> CREATOR = new Creator<UserListFriends>() {
        @Override
        public UserListFriends createFromParcel(Parcel in) {
            return new UserListFriends(in);
        }

        @Override
        public UserListFriends[] newArray(int size) {
            return new UserListFriends[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (requestId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(requestId);
        }
        dest.writeString(userId);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(profilePictureUrl);
        dest.writeString(time);
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
