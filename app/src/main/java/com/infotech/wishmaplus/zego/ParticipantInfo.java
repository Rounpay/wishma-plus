package com.infotech.wishmaplus.zego;


public class ParticipantInfo {
    public String userID;
    public String userName;
    public boolean isCoHost;

    public ParticipantInfo(String userID, String userName, boolean isCoHost) {
        this.userID = userID;
        this.userName = userName;
        this.isCoHost = isCoHost;
    }
}