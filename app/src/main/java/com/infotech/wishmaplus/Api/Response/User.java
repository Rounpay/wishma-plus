package com.infotech.wishmaplus.Api.Response;

public class User {
    private String name;
    private String subtitle;
    private String avatarUrl; // or local resource

    public User(String name, String subtitle, String avatarUrl) {
        this.name = name;
        this.subtitle = subtitle;
        this.avatarUrl = avatarUrl;
    }

    public String getName() { return name; }
    public String getSubtitle() { return subtitle; }
    public String getAvatarUrl() { return avatarUrl; }
}
