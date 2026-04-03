package com.infotech.wishmaplus;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TrackReelViewRequest implements Serializable {

    @SerializedName("reelId")
    private int reelId;

    @SerializedName("watchDurationSec")
    private int watchDurationSec;

    public TrackReelViewRequest(int reelId, int watchDurationSec) {
        this.reelId = reelId;
        this.watchDurationSec = watchDurationSec;
    }

    public int getReelId() {
        return reelId;
    }

    public int getWatchDurationSec() {
        return watchDurationSec;
    }

    public void setReelId(int reelId) {
        this.reelId = reelId;
    }

    public void setWatchDurationSec(int watchDurationSec) {
        this.watchDurationSec = watchDurationSec;
    }
}