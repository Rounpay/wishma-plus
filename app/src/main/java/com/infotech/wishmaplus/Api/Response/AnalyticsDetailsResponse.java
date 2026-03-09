package com.infotech.wishmaplus.Api.Response;

import java.util.List;

public class AnalyticsDetailsResponse {

    private int statusCode;
    private String responseText;
    private Result result;

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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    // ================== INNER CLASSES ==================

    public static class Result {
        private Analytic analytic;
        private LatestPost latestPosts;
        private List<ContentSummary> content;

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

        public List<ContentSummary> getContent() {
            return content;
        }

        public void setContent(List<ContentSummary> content) {
            this.content = content;
        }
    }

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

    public static class LatestPost {
        private int contentTypeId;
        private String postContent;
        private String caption;
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

    public static class ContentSummary {
        private String contentType;
        private int total;

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
