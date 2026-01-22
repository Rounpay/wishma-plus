package com.infotech.wishmaplus.Api.Response;

import java.util.List;

public class BlockedUserListResponse {

    private int statusCode;
    private String responseText;
    private List<Result> result;

    // Getters & Setters
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

    // Inner class
    public static class Result {

        private String fullName;
        private String profilePictureUrl;
        private String userId;
        private int blockId;

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

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getBlockId() {
            return blockId;
        }

        public void setBlockId(int blockId) {
            this.blockId = blockId;
        }
    }
}
