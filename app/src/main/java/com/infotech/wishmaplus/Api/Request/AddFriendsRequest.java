package com.infotech.wishmaplus.Api.Request;

public class AddFriendsRequest {

    private String groupId;
    private String friendUserIds;

    public AddFriendsRequest(String groupId, String friendUserIds) {
        this.groupId = groupId;
        this.friendUserIds = friendUserIds;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getFriendUserIds() {
        return friendUserIds;
    }

    public void setFriendUserIds(String friendUserIds) {
        this.friendUserIds = friendUserIds;
    }
}

