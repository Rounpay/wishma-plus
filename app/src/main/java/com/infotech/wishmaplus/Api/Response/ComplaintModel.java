package com.infotech.wishmaplus.Api.Response;

public class ComplaintModel {

    private int complaintId;
    private String description;
    private String categoryName;
    private String statusName;
    private String createdOn;

    public ComplaintModel(int complaintId, String description, String categoryName,
                          String statusName, String createdOn) {
        this.complaintId = complaintId;
        this.description = description;
        this.categoryName = categoryName;
        this.statusName = statusName;
        this.createdOn = createdOn;
    }

    public int getComplaintId() {
        return complaintId;
    }

    public String getDescription() {
        return description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getStatusName() {
        return statusName;
    }

    public String getCreatedOn() {
        return createdOn;
    }
}
