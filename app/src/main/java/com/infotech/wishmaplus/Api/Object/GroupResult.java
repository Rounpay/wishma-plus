package com.infotech.wishmaplus.Api.Object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class GroupResult implements Parcelable {

    @SerializedName("groupId")
    private String groupId;

    @SerializedName("ownerUserId")
    private String ownerUserId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("profileImageUrl")
    private String profileImageUrl;

    @SerializedName("coverImageUrl")
    private String coverImageUrl;

    @SerializedName("isPrivate")
    private boolean isPrivate;

    @SerializedName("isVisible")
    private boolean isVisible;

    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("isAdmin")
    private boolean isAdmin;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("ownerName")
    private String ownerName;

    @SerializedName("ownerProfileImage")
    private String ownerProfileImage;

    // 🔹 Empty constructor
    public GroupResult() {
    }

    // 🔹 Parcelable constructor
    protected GroupResult(Parcel in) {
        groupId = in.readString();
        ownerUserId = in.readString();
        title = in.readString();
        description = in.readString();
        profileImageUrl = in.readString();
        coverImageUrl = in.readString();
        isPrivate = in.readByte() != 0;
        isVisible = in.readByte() != 0;
        isActive = in.readByte() != 0;
        isAdmin = in.readByte() != 0;
        createdAt = in.readString();
        ownerName = in.readString();
        ownerProfileImage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupId);
        dest.writeString(ownerUserId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(profileImageUrl);
        dest.writeString(coverImageUrl);
        dest.writeByte((byte) (isPrivate ? 1 : 0));
        dest.writeByte((byte) (isVisible ? 1 : 0));
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeByte((byte) (isAdmin ? 1 : 0));
        dest.writeString(createdAt);
        dest.writeString(ownerName);
        dest.writeString(ownerProfileImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupResult> CREATOR = new Creator<GroupResult>() {
        @Override
        public GroupResult createFromParcel(Parcel in) {
            return new GroupResult(in);
        }

        @Override
        public GroupResult[] newArray(int size) {
            return new GroupResult[size];
        }
    };

    // 🔹 Getters & Setters

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

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isAdmin() {
        return isAdmin;
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
