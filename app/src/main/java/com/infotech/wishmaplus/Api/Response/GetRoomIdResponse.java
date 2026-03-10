package com.infotech.wishmaplus.Api.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.processing.Generated;

@Generated("jsonschema2pojo")
public class GetRoomIdResponse {
    @SerializedName("statusCode")
        @Expose
        private Integer statusCode;
        @SerializedName("responseText")
        @Expose
        private String responseText;
        @SerializedName("result")
        @Expose
        private Result result;

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public String getResponseText() {
            return responseText;
        }

        public void setResponseText(String responseText) {
            this.responseText = responseText;
        }

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }


    public class Result {

        @SerializedName("roomId")
        @Expose
        private String roomId;

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

    }


}

