package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GroupMembersResponse {

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("responseText")
    private String responseText;

    @SerializedName("result")
    private List<Result> result;

    // ===== Getters & Setters =====

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    // ===== Inner Model Class =====
    public static class Result {

        @SerializedName("isAdmin")
        private boolean isAdmin;

        @SerializedName("isActive")
        private boolean isActive;

        @SerializedName("joinedAt")
        private String joinedAt;

        @SerializedName("userId")
        private String userId;

        @SerializedName("fullName")
        private String fullName;

        @SerializedName("profilePictureUrl")
        private String profilePictureUrl;

        // Getters & Setters
        public boolean isAdmin() {
            return isAdmin;
        }

        public void setAdmin(boolean admin) {
            isAdmin = admin;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        public String getJoinedAt() {
            return joinedAt;
        }

        public void setJoinedAt(String joinedAt) {
            this.joinedAt = joinedAt;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getProfilePictureUrl() {
            return profilePictureUrl;
        }

        public void setProfilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
        }
    }
}
