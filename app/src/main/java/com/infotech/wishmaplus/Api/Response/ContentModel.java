package com.infotech.wishmaplus.Api.Response;

public class ContentModel {
    private int image;
    private String stats, date,textTitle;


    public ContentModel(int image, String stats, String date,String textTitle) {
        this.image = image;
        this.stats = stats;
        this.date = date;
        this.textTitle = textTitle;
    }


    public int getImage() { return image; }
    public String getStats() { return stats; }
    public String getDate() { return date; }
    public String getTextTitle() { return textTitle; }
}
