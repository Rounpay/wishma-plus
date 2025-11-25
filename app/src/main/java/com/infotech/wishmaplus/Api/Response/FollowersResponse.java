package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.infotech.wishmaplus.Api.Object.FollowerResult;

public class FollowersResponse extends BasicListResponse<FollowerResult>{

    @SerializedName("sub")
    @Expose
    private int sub;
    @SerializedName("unSub")
    @Expose
    private int unSub;


    public int getSub() {
        return sub;
    }

    public int getUnSub() {
        return unSub;
    }
}