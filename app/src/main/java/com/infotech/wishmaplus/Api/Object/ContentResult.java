package com.infotech.wishmaplus.Api.Object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;

import java.util.ArrayList;

public class ContentResult implements Parcelable {
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("profilePictureUrl")
    @Expose
    private String profilePictureUrl;
    @SerializedName("fisrtName")
    @Expose
    private String fisrtName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("entryAt")
    @Expose
    private String entryAt;
    @SerializedName("modifyAt")
    @Expose
    private String modifyAt;
    @SerializedName("totalComments")
    @Expose
    private int totalComments;
    @SerializedName("totalLikes")
    @Expose
    private int totalLikes;
    @SerializedName("totalShares")
    @Expose
    private int totalShares;
    @SerializedName("lastCommentAt")
    @Expose
    private String lastCommentAt;
    @SerializedName("lastLikeAt")
    @Expose
    private String lastLikeAt;
    @SerializedName("lastShareAt")
    @Expose
    private String lastShareAt;
    @SerializedName(value = "postId",alternate = "storyId")
    @Expose
    private String postId;
    @SerializedName("contentTypeId")
    @Expose
    private int contentTypeId;
    @SerializedName("durationInMs")
    @Expose
    private long durationInMs;
    @SerializedName("height")
    @Expose
    private double height;
    @SerializedName("width")
    @Expose
    private double width;
    @SerializedName(value = "postContent",alternate = "storyContent")
    @Expose
    private String postContent;
    @SerializedName("totalStory")
    @Expose
    private int totalStory;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("isLiked")
    @Expose
    private boolean isLiked;
    @SerializedName("userDetail")
    @Expose
    private UserDetailResponse userDetail;
    @SerializedName("parsedSharedData")
    @Expose
    private SharedData parsedSharedData;

    @SerializedName("storyList")
    @Expose
    private ArrayList<StoryResult> storyList;

    public ContentResult(int contentTypeId, UserDetailResponse userDetail,ArrayList<StoryResult> storyList) {
        this.contentTypeId = contentTypeId;
        this.userDetail = userDetail;
        this.storyList = storyList;
    }


    protected ContentResult(Parcel in) {
        userId = in.readString();
        profilePictureUrl = in.readString();
        fisrtName = in.readString();
        lastName = in.readString();
        entryAt = in.readString();
        modifyAt = in.readString();
        totalComments = in.readInt();
        totalLikes = in.readInt();
        totalShares = in.readInt();
        lastCommentAt = in.readString();
        lastLikeAt = in.readString();
        lastShareAt = in.readString();
        postId = in.readString();
        contentTypeId = in.readInt();
        durationInMs = in.readLong();
        height = in.readDouble();
        width = in.readDouble();
        postContent = in.readString();
        totalStory = in.readInt();
        caption = in.readString();
        isLiked = in.readByte() != 0;
        userDetail = in.readParcelable(UserDetailResponse.class.getClassLoader());
        parsedSharedData = in.readParcelable(SharedData.class.getClassLoader());
        storyList = in.createTypedArrayList(StoryResult.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(profilePictureUrl);
        dest.writeString(fisrtName);
        dest.writeString(lastName);
        dest.writeString(entryAt);
        dest.writeString(modifyAt);
        dest.writeInt(totalComments);
        dest.writeInt(totalLikes);
        dest.writeInt(totalShares);
        dest.writeString(lastCommentAt);
        dest.writeString(lastLikeAt);
        dest.writeString(lastShareAt);
        dest.writeString(postId);
        dest.writeInt(contentTypeId);
        dest.writeLong(durationInMs);
        dest.writeDouble(height);
        dest.writeDouble(width);
        dest.writeString(postContent);
        dest.writeInt(totalStory);
        dest.writeString(caption);
        dest.writeByte((byte) (isLiked ? 1 : 0));
        dest.writeParcelable(userDetail, flags);
        dest.writeParcelable(parsedSharedData, flags);
        dest.writeTypedList(storyList);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public static final Creator<ContentResult> CREATOR = new Creator<ContentResult>() {
        @Override
        public ContentResult createFromParcel(Parcel in) {
            return new ContentResult(in);
        }

        @Override
        public ContentResult[] newArray(int size) {
            return new ContentResult[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getFisrtName() {
        return fisrtName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEntryAt() {
        return entryAt;
    }

    public String getModifyAt() {
        return modifyAt;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public int getTotalShares() {
        return totalShares;
    }

    public String getLastCommentAt() {
        return lastCommentAt;
    }

    public String getLastLikeAt() {
        return lastLikeAt;
    }

    public long getDurationInMs() {
        return durationInMs;
    }

    public String getLastShareAt() {
        return lastShareAt;
    }

    public String getPostId() {
        return postId;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public int getContentTypeId() {
        return contentTypeId;
    }

    public String getPostContent() {
        return postContent;
    }

    public String getCaption() {
        return caption;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public UserDetailResponse getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetailResponse userDetail) {
        this.userDetail = userDetail;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public void setTotalShares(int totalShares) {
        this.totalShares = totalShares;
    }

    public int getTotalStory() {
        return totalStory;
    }

    public SharedData getParsedSharedData() {
        return parsedSharedData;
    }

    public ArrayList<StoryResult> getStoryList() {
        return storyList;
    }


}
