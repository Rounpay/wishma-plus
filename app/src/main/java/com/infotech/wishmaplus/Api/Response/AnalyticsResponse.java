package com.infotech.wishmaplus.Api.Response;

public class AnalyticsResponse {

    private int statusCode;
    private String responseText;
    private Result result;

    // -------------------- Result --------------------
    public static class Result {
        private Profile profile;
        private Analytic analytic;
        private LatestPost latestPosts;

        public Profile getProfile() {
            return profile;
        }

        public void setProfile(Profile profile) {
            this.profile = profile;
        }

        public Analytic getAnalytic() {
            return analytic;
        }

        public void setAnalytic(Analytic analytic) {
            this.analytic = analytic;
        }

        public LatestPost getLatestPosts() {
            return latestPosts;
        }

        public void setLatestPosts(LatestPost latestPosts) {
            this.latestPosts = latestPosts;
        }
    }

    // -------------------- Profile --------------------
    public static class Profile {
        private String fullName;
        private String profilePictureUrl;
        private int weelkyProgress;

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

        public int getWeelkyProgress() {
            return weelkyProgress;
        }

        public void setWeelkyProgress(int weelkyProgress) {
            this.weelkyProgress = weelkyProgress;
        }
    }

    // -------------------- Analytic --------------------
    public static class Analytic {
        private int totalLikes;
        private int totalComments;
        private int totalShares;
        private int totalEngagement;

        public int getTotalLikes() {
            return totalLikes;
        }

        public void setTotalLikes(int totalLikes) {
            this.totalLikes = totalLikes;
        }

        public int getTotalComments() {
            return totalComments;
        }

        public void setTotalComments(int totalComments) {
            this.totalComments = totalComments;
        }

        public int getTotalShares() {
            return totalShares;
        }

        public void setTotalShares(int totalShares) {
            this.totalShares = totalShares;
        }

        public int getTotalEngagement() {
            return totalEngagement;
        }

        public void setTotalEngagement(int totalEngagement) {
            this.totalEngagement = totalEngagement;
        }
    }

    // -------------------- Latest Post --------------------
    public static class LatestPost {
        private int contentTypeId;
        private String postContent;
        private String caption; // nullable
        private int totalLikes;
        private int totalComments;
        private int totalShares;
        private int engagement;
        private int postEarning;

        public int getContentTypeId() {
            return contentTypeId;
        }

        public void setContentTypeId(int contentTypeId) {
            this.contentTypeId = contentTypeId;
        }

        public String getPostContent() {
            return postContent;
        }

        public void setPostContent(String postContent) {
            this.postContent = postContent;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public int getTotalLikes() {
            return totalLikes;
        }

        public void setTotalLikes(int totalLikes) {
            this.totalLikes = totalLikes;
        }

        public int getTotalComments() {
            return totalComments;
        }

        public void setTotalComments(int totalComments) {
            this.totalComments = totalComments;
        }

        public int getTotalShares() {
            return totalShares;
        }

        public void setTotalShares(int totalShares) {
            this.totalShares = totalShares;
        }

        public int getEngagement() {
            return engagement;
        }

        public void setEngagement(int engagement) {
            this.engagement = engagement;
        }

        public int getPostEarning() {
            return postEarning;
        }

        public void setPostEarning(int postEarning) {
            this.postEarning = postEarning;
        }
    }

    // -------------------- Main Getters --------------------
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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
