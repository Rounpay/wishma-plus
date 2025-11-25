package com.infotech.wishmaplus.Api.Object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class StoryResult implements Parcelable {
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("profilePictureUrl")
    @Expose
    private String profilePictureUrl;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;

    @SerializedName("contentTypeId")
    @Expose
    private int contentTypeId;
    @SerializedName("stories")
    @Expose
    private ArrayList<ContentResult> stories;

    public StoryResult(int contentTypeId) {
        this.contentTypeId = contentTypeId;
    }

    protected StoryResult(Parcel in) {
        userId = in.readString();
        profilePictureUrl = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        contentTypeId = in.readInt();
        stories = in.createTypedArrayList(ContentResult.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(profilePictureUrl);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeInt(contentTypeId);
        dest.writeTypedList(stories);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public static final Creator<StoryResult> CREATOR = new Creator<StoryResult>() {
        @Override
        public StoryResult createFromParcel(Parcel in) {
            return new StoryResult(in);
        }

        @Override
        public StoryResult[] newArray(int size) {
            return new StoryResult[size];
        }
    };

    public int getContentTypeId() {
        return contentTypeId;
    }

    public String getUserId() {
        return userId;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ArrayList<ContentResult> getStories() {
        return stories;
    }
}
