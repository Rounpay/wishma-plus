package com.infotech.wishmaplus;

import java.io.Serializable;
import java.util.List;

public class CommentModel implements Serializable {

    private String id;
    private String userName;
    private String avatarUrl;
    private String text;
    private String timeAgo;
    private long likes;
    private boolean isLiked;
    private List<CommentModel> replies;

    // Without replies
    public CommentModel(String id, String userName, String avatarUrl,
                        String text, String timeAgo, long likes, boolean isLiked) {
        this.id = id;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
        this.text = text;
        this.timeAgo = timeAgo;
        this.likes = likes;
        this.isLiked = isLiked;
    }

    // With replies
    public CommentModel(String id, String userName, String avatarUrl,
                        String text, String timeAgo, long likes,
                        boolean isLiked, List<CommentModel> replies) {
        this(id, userName, avatarUrl, text, timeAgo, likes, isLiked);
        this.replies = replies;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getText() {
        return text;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public long getLikes() {
        return likes;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public List<CommentModel> getReplies() {
        return replies;
    }

    public void setLikes(long l) {
        this.likes = l;
    }

    public void setLiked(boolean b) {
        this.isLiked = b;
    }

    public void setReplies(List<CommentModel> r) {
        this.replies = r;
    }
}