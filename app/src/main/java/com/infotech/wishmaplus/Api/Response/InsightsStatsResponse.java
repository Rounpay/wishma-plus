package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class InsightsStatsResponse {

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("responseText")
    private String responseText;

    @SerializedName("result")
    private Result result;

    // -------- GETTERS --------
    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public Result getResult() {
        return result;
    }

    // ================= INNER CLASSES =================

    public static class Result {

        @SerializedName("totalInsights")
        private TotalInsights totalInsights;

        @SerializedName("insightsDateWise")
        private List<InsightDateWise> insightsDateWise;

        public TotalInsights getTotalInsights() {
            return totalInsights;
        }

        public List<InsightDateWise> getInsightsDateWise() {
            return insightsDateWise;
        }
    }

    // -------------------------------------------------

    public static class TotalInsights {

        @SerializedName("click")
        private int click;

        @SerializedName("engagement")
        private int engagement;

        @SerializedName("totalLikes")
        private int totalLikes;

        @SerializedName("totalComments")
        private int totalComments;

        @SerializedName("totalShares")
        private int totalShares;

        @SerializedName("totalViews")
        private int totalViews;

        @SerializedName("totalEarning")
        private int totalEarning;

        public int getClick() {
            return click;
        }

        public int getEngagement() {
            return engagement;
        }

        public int getTotalLikes() {
            return totalLikes;
        }

        public int getTotalComments() {
            return totalComments;
        }

        public int getTotalShares() {
            return totalShares;
        }

        public int getTotalViews() {
            return totalViews;
        }

        public int getTotalEarning() {
            return totalEarning;
        }
    }

    // -------------------------------------------------

    public static class InsightDateWise {

        @SerializedName("insightTypeID")
        private int insightTypeID;

        @SerializedName("insightName")
        private String insightName;

        @SerializedName("insightDate")
        private String insightDate;

        @SerializedName("insightCount")
        private int insightCount;

        public int getInsightTypeID() {
            return insightTypeID;
        }

        public String getInsightName() {
            return insightName;
        }

        public String getInsightDate() {
            return insightDate;
        }

        public int getInsightCount() {
            return insightCount;
        }
    }
}
