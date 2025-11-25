package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vishnu Agarwal on 24-10-2024.
 */

public class BasicObjectResponse<T> extends BasicResponse {

    @SerializedName("result")
    @Expose
    private T result;


    public T getResult() {
        return result;
    }
}
