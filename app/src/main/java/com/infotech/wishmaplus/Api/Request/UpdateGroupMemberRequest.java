package com.infotech.wishmaplus.Api.Request;

public class UpdateGroupMemberRequest {

    private String groupId;
    private String friendUserIds;
    private boolean isActive;

    public UpdateGroupMemberRequest(String groupId, String friendUserIds, boolean isActive) {
        this.groupId = groupId;
        this.friendUserIds = friendUserIds;
        this.isActive = isActive;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

