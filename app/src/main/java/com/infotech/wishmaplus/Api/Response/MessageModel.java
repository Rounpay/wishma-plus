package com.infotech.wishmaplus.Api.Response;

public class MessageModel {

    String message, userTime;

    public MessageModel(String message, String userTime) {
        this.message = message;
        this.userTime = userTime;
    }

    public String getMessage() { return message; }
    public String getUserTime() { return userTime; }
}
