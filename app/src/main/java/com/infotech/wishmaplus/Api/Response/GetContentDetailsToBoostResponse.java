package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetContentDetailsToBoostResponse {

    @SerializedName("postInsights")
    private PostInsights postInsights;

    @SerializedName("goal")
    private List<Goal> goal;

    @SerializedName("audience")
    private List<Audience> audience;

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("responseText")
    private String responseText;

    // Getters & Setters
    public PostInsights getPostInsights() { return postInsights; }
    public void setPostInsights(PostInsights postInsights) { this.postInsights = postInsights; }

    public List<Goal> getGoal() { return goal; }
    public void setGoal(List<Goal> goal) { this.goal = goal; }

    public List<Audience> getAudience() { return audience; }
    public void setAudience(List<Audience> audience) { this.audience = audience; }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getResponseText() { return responseText; }
    public void setResponseText(String responseText) { this.responseText = responseText; }

    // -------- Nested Classes --------

    public static class PostInsights {

        @SerializedName("userName")
        private String userName;

        @SerializedName("profilePictureUrl")
        private String profilePictureUrl;

        @SerializedName("boostId")
        private int boostId;

        @SerializedName("postId")
        private String postId;

        @SerializedName("contentTypeId")
        private int contentTypeId;

        @SerializedName("postContent")
        private String postContent;

        @SerializedName("peopleReach")
        private int peopleReach;

        @SerializedName("engagement")
        private int engagement;

        @SerializedName("caption")
        private String caption;

        @SerializedName("createdDate")
        private String createdDate;

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public String getProfilePictureUrl() { return profilePictureUrl; }
        public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

        public String getPostId() { return postId; }
        public void setPostId(String postId) { this.postId = postId; }

        public int getContentTypeId() { return contentTypeId; }
        public void setContentTypeId(int contentTypeId) { this.contentTypeId = contentTypeId; }

        public String getPostContent() { return postContent; }
        public void setPostContent(String postContent) { this.postContent = postContent; }

        public int getPeopleReach() { return peopleReach; }
        public void setPeopleReach(int peopleReach) { this.peopleReach = peopleReach; }

        public int getEngagement() { return engagement; }
        public void setEngagement(int engagement) { this.engagement = engagement; }

        public String getCaption() { return caption; }
        public void setCaption(String caption) { this.caption = caption; }

        public String getCreatedDate() { return createdDate; }
        public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

        public int getBoostId() {
            return boostId;
        }

        public void setBoostId(int boostId) {
            this.boostId = boostId;
        }
    }

    public static class Goal {

        @SerializedName("goalId")
        private int goalId;

        @SerializedName("goalName")
        private String goalName;

        @SerializedName("goalDescription")
        private String goalDescription;

        @SerializedName("iconName")
        private String iconName;

        public int getGoalId() { return goalId; }
        public void setGoalId(int goalId) { this.goalId = goalId; }

        public String getGoalName() { return goalName; }
        public void setGoalName(String goalName) { this.goalName = goalName; }

        public String getGoalDescription() { return goalDescription; }
        public void setGoalDescription(String goalDescription) { this.goalDescription = goalDescription; }

        public String getIconName() { return iconName; }
        public void setIconName(String iconName) { this.iconName = iconName; }
    }

    public static class Audience {

        @SerializedName("audienceId")
        private int audienceId;

        @SerializedName("audienceName")
        private String audienceName;

        public int getAudienceId() { return audienceId; }
        public void setAudienceId(int audienceId) { this.audienceId = audienceId; }

        public String getAudienceName() { return audienceName; }
        public void setAudienceName(String audienceName) { this.audienceName = audienceName; }
    }
}
