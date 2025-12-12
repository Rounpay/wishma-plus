package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NotificationResponse {

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("responseText")
    private String responseText;

    @SerializedName("result")
    private List<NotificationItem> result;

    // Getters and Setters
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

    public List<NotificationItem> getResult() {
        return result;
    }

    public void setResult(List<NotificationItem> result) {
        this.result = result;
    }

    // Inner Class
    public static class NotificationItem {

        @SerializedName("notificationId")
        private int notificationId;

        @SerializedName("contentTypeId")
        private int contentTypeId;

        @SerializedName("postId")
        private String postId;

        @SerializedName("userName")
        private String userName;

        @SerializedName("profilePictureUrl")
        private String profilePictureUrl;

        @SerializedName("title")
        private String title;

        @SerializedName("body")
        private String body;

        @SerializedName("time")
        private String time;

        @SerializedName("isRead")
        private boolean isRead;

        // Getters and Setters
        public int getNotificationId() {
            return notificationId;
        }

        public void setNotificationId(int notificationId) {
            this.notificationId = notificationId;
        }

        public int getContentTypeId() {
            return contentTypeId;
        }

        public void setContentTypeId(int contentTypeId) {
            this.contentTypeId = contentTypeId;
        }

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getProfilePictureUrl() {
            return profilePictureUrl;
        }

        public void setProfilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public String getTime() {
            return time;
        }

        public boolean getIsRead() {
            return isRead;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public void setTime(String time) {
            this.time = time;
        }
        public void setIsRead(boolean isRead) {
            this.isRead = isRead;
        }
    }
}
