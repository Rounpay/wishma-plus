package com.infotech.wishmaplus.Api.Object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CityResult {
    @SerializedName("cityId")
    @Expose
    private int cityId;
    @SerializedName("cityName")
    @Expose
    private String cityName;

    public int getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }
}
