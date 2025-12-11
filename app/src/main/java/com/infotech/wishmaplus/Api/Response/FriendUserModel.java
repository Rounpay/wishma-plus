package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.SerializedName;

public class FriendUserModel {
    @SerializedName("userId")
    private String userId;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("profilePictureUrl")
    private String profilePictureUrl;

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}
