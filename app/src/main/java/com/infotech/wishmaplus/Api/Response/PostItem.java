package com.infotech.wishmaplus.Api.Response;


import com.google.gson.annotations.SerializedName;
import com.infotech.wishmaplus.Utils.BoostStatus;

public class PostItem {

    @SerializedName("postId")
    private String postId;

    @SerializedName("contentTypeId")
    private int contentTypeId;    // 1 = text, 2 = video, 3 = image

    @SerializedName("postContent")
    private String postContent;

    @SerializedName("peopleReach")
    private int peopleReach;

    @SerializedName("engagement")
    private int engagement;
    @SerializedName("boostId")
    private int boostId;
    @SerializedName("boostStatus")
    private int boostStatus;

    @SerializedName("caption")
    private String caption;

    @SerializedName("createdDate")
    private String createdDate;

    public String getPostId() {
        return postId;
    }

    public int getContentTypeId() {
        return contentTypeId;
    }

    public String getPostContent() {
        return postContent;
    }

    public int getPeopleReach() {
        return peopleReach;
    }

    public int getEngagement() {
        return engagement;
    }
    public int getBoostId() {
        return boostId;
    }
    public BoostStatus getBoostStatus() {
        return BoostStatus.fromValue(boostStatus);
    }

    public String getCaption() {
        return caption;
    }

    public String getCreatedDate() {
        return createdDate;
    }
    public void setBoostStatus(int boostStatus) {
        this.boostStatus = boostStatus;
    }

}

