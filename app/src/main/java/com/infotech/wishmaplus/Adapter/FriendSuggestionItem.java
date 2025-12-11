package com.infotech.wishmaplus.Adapter;

import com.google.gson.annotations.SerializedName;

public class FriendSuggestionItem {

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
