package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SentRequestResponse {

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("responseText")
    private String responseText;

    @SerializedName("result")
    private List<ResultItem> result;

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public List<ResultItem> getResult() {
        return result;
    }

    // ---------- Inner Model ----------
    public static class ResultItem {

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
}
