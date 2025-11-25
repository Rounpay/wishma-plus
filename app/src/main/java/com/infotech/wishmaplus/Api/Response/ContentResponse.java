package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.infotech.wishmaplus.Api.Object.ContentResult;

public class ContentResponse extends BasicListResponse<ContentResult>{

    @SerializedName("totalPost")
    @Expose
    private int totalPost;


    public int getTotalPost() {
        return totalPost;
    }





}