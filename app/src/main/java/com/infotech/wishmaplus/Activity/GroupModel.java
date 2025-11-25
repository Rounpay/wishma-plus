package com.infotech.wishmaplus.Activity;

public class GroupModel {
    private String name;
    private String posts;
    private int image;

    public GroupModel(String name, String posts, int image) {
        this.name = name;
        this.posts = posts;
        this.image = image;
    }

    public String getName() { return name; }
    public String getPosts() { return posts; }
    public int getImage() { return image; }
}
