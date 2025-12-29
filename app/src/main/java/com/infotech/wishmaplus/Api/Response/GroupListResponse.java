package com.infotech.wishmaplus.Api.Response;


import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GroupListResponse {

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("responseText")
    private String responseText;

    @SerializedName("result")
    private List<Result> result;

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public List<Result> getResult() {
        return result;
    }

    // ================= INNER MODEL =================

    public static class Result {

        @SerializedName("groupId")
        private String groupId;

        @SerializedName("ownerUserId")
        private String ownerUserId;

        @SerializedName("title")
        private String title;

        @SerializedName("description")
        private String description;

        @SerializedName("profileImageUrl")
        private String profileImageUrl;

        @SerializedName("coverImageUrl")
        private String coverImageUrl;

        @SerializedName("isPrivate")
        private boolean isPrivate;

        @SerializedName("isVisible")
        private boolean isVisible;

        @SerializedName("isActive")
        private boolean isActive;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("ownerName")
        private String ownerName;

        @SerializedName("ownerProfileImage")
        private String ownerProfileImage;

        public String getGroupId() {
            return groupId;
        }

        public String getOwnerUserId() {
            return ownerUserId;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public String getCoverImageUrl() {
            return coverImageUrl;
        }

        public boolean isPrivate() {
            return isPrivate;
        }

        public boolean isVisible() {
            return isVisible;
        }

        public boolean isActive() {
            return isActive;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public String getOwnerProfileImage() {
            return ownerProfileImage;
        }
    }
}
