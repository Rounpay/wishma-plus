package com.infotech.wishmaplus.Api.Response;



public class PageData {

    private String pageId;
    private String pageName;

    private String categoryId;
    private String bio;
    private String website;
    private String email;
    private String phone;
    private String address;
    private String profileImageUrl;
    private String coverImageUrl;
    private String createdByUserId;
    private boolean isProfile;

    public PageData(String pageId, String pageName,Boolean isProfile, String categoryId, String bio, String website,
                    String email, String phone, String address, String profileImageUrl,
                    String coverImageUrl, String createdByUserId) {

        this.pageId = pageId;
        this.pageName = pageName;
        this.isProfile = isProfile;
        this.categoryId = categoryId;
        this.bio = bio;
        this.website = website;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.profileImageUrl = profileImageUrl;
        this.coverImageUrl = coverImageUrl;
        this.createdByUserId = createdByUserId;
    }

    public String getPageId() {
        return pageId;
    }

    public String getPageName() {
        return pageName;
    }


    public String getCategoryId() {
        return categoryId;
    }

    public String getBio() {
        return bio;
    }

    public String getWebsite() {
        return website;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public boolean isProfile() {
        return isProfile;
    }


}