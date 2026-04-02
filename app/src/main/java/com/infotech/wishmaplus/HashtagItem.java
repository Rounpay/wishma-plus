package com.infotech.wishmaplus;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public  class HashtagItem implements Serializable {
    @SerializedName("hashtagId")
    public int hashtagId;

    @SerializedName("tag")
    public String tag;

    @SerializedName("reelCount")
    public int reelCount;

    public int getHashtagId() {
        return hashtagId;
    }

    public void setHashtagId(int hashtagId) {
        this.hashtagId = hashtagId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getReelCount() {
        return reelCount;
    }

    public void setReelCount(int reelCount) {
        this.reelCount = reelCount;
    }
}