package com.infotech.wishmaplus.Api.Response;


import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import com.google.gson.Gson;

public class EligibilityModel implements Parcelable {

    @SerializedName("followers")
    private int followers;

    @SerializedName("minFollowersRequired")
    private String minFollowersRequired;

    @SerializedName("monthlyPosts")
    private int monthlyPosts;

    @SerializedName("minMonthlyPostsRequired")
    private String minMonthlyPostsRequired;

    @SerializedName("engagementRate")
    private double engagementRate;

    @SerializedName("minEngagementRateRequired")
    private String minEngagementRateRequired;

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("responseText")
    private String responseText;

    // Empty constructor
    public EligibilityModel() {}

    // Full constructor
    public EligibilityModel(int followers,
                            String minFollowersRequired,
                            int monthlyPosts,
                            String minMonthlyPostsRequired,
                            double engagementRate,
                            String minEngagementRateRequired,
                            int statusCode,
                            String responseText) {
        this.followers = followers;
        this.minFollowersRequired = minFollowersRequired;
        this.monthlyPosts = monthlyPosts;
        this.minMonthlyPostsRequired = minMonthlyPostsRequired;
        this.engagementRate = engagementRate;
        this.minEngagementRateRequired = minEngagementRateRequired;
        this.statusCode = statusCode;
        this.responseText = responseText;
    }

    // Parcelable constructor
    protected EligibilityModel(Parcel in) {
        followers = in.readInt();
        minFollowersRequired = in.readString();
        monthlyPosts = in.readInt();
        minMonthlyPostsRequired = in.readString();
        engagementRate = in.readDouble();
        minEngagementRateRequired = in.readString();
        statusCode = in.readInt();
        responseText = in.readString();
    }

    public static final Creator<EligibilityModel> CREATOR = new Creator<EligibilityModel>() {
        @Override
        public EligibilityModel createFromParcel(Parcel in) {
            return new EligibilityModel(in);
        }

        @Override
        public EligibilityModel[] newArray(int size) {
            return new EligibilityModel[size];
        }
    };

    // Getters & Setters
    public int getFollowers() { return followers; }
    public void setFollowers(int followers) { this.followers = followers; }

    public String getMinFollowersRequired() { return minFollowersRequired; }
    public void setMinFollowersRequired(String minFollowersRequired) { this.minFollowersRequired = minFollowersRequired; }

    public int getMonthlyPosts() { return monthlyPosts; }
    public void setMonthlyPosts(int monthlyPosts) { this.monthlyPosts = monthlyPosts; }

    public String getMinMonthlyPostsRequired() { return minMonthlyPostsRequired; }
    public void setMinMonthlyPostsRequired(String minMonthlyPostsRequired) { this.minMonthlyPostsRequired = minMonthlyPostsRequired; }

    public double getEngagementRate() { return engagementRate; }
    public void setEngagementRate(double engagementRate) { this.engagementRate = engagementRate; }

    public String getMinEngagementRateRequired() { return minEngagementRateRequired; }
    public void setMinEngagementRateRequired(String minEngagementRateRequired) { this.minEngagementRateRequired = minEngagementRateRequired; }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getResponseText() { return responseText; }
    public void setResponseText(String responseText) { this.responseText = responseText; }

    @Override
    public String toString() {
        return "EligibilityModel{" +
                "followers=" + followers +
                ", minFollowersRequired='" + minFollowersRequired + '\'' +
                ", monthlyPosts=" + monthlyPosts +
                ", minMonthlyPostsRequired='" + minMonthlyPostsRequired + '\'' +
                ", engagementRate=" + engagementRate +
                ", minEngagementRateRequired='" + minEngagementRateRequired + '\'' +
                ", statusCode=" + statusCode +
                ", responseText='" + responseText + '\'' +
                '}';
    }

    // Gson helper methods
    public static EligibilityModel fromJson(String json) {
        return new Gson().fromJson(json, EligibilityModel.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    // Parcelable implementations
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(followers);
        parcel.writeString(minFollowersRequired);
        parcel.writeInt(monthlyPosts);
        parcel.writeString(minMonthlyPostsRequired);
        parcel.writeDouble(engagementRate);
        parcel.writeString(minEngagementRateRequired);
        parcel.writeInt(statusCode);
        parcel.writeString(responseText);
    }
}
