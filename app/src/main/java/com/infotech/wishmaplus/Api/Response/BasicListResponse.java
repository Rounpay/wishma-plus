package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Vishnu Agarwal on 24-10-2024.
 */

public class BasicListResponse<T> extends BasicResponse{
    @SerializedName("result")
    @Expose
    private ArrayList<T> result;

    public ArrayList<T> getResult() {
        return result;
    }
}
