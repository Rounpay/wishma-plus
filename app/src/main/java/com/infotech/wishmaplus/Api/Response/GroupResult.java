package com.infotech.wishmaplus.Api.Response;

public class GroupResult {

    private String groupId;
    private String ownerUserId;
    private String title;
    private String description;
    private String profileImageUrl;
    private String coverImageUrl;
    private boolean isPrivate;
    private boolean isVisible;
    private boolean isActive;
    private boolean isAdmin;
    private String createdAt;
    private String ownerName;
    private String ownerProfileImage;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isActive() {
        return isActive;
    }
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerProfileImage() {
        return ownerProfileImage;
    }

    public void setOwnerProfileImage(String ownerProfileImage) {
        this.ownerProfileImage = ownerProfileImage;
    }
}
