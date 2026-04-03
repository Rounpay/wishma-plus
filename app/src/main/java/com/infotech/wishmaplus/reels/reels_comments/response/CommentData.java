package com.infotech.wishmaplus.reels.reels_comments.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommentData {
    @SerializedName("comments")
    @Expose
    private List<CommentItems> comments;
    @SerializedName("totalCount")
    @Expose
    private int totalCount;
    @SerializedName("pageNumber")
    @Expose
    private int pageNumber;
    @SerializedName("pageSize")
    @Expose
    private int pageSize;
    @SerializedName("totalPages")
    @Expose
    private int totalPages;
    @SerializedName("hasMore")
    @Expose
    private boolean hasMore;

    public List<CommentItems> getComments() {
        return comments;
    }

    public void setComments(List<CommentItems> comments) {
        this.comments = comments;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}