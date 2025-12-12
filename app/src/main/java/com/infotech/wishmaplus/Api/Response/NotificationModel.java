package com.infotech.wishmaplus.Api.Response;

public class NotificationModel {
    private String profileUrl;
    private String message;
    private String time;
    private boolean showButtons;

    public NotificationModel(String profileUrl, String message, String time, boolean showButtons) {
        this.profileUrl = profileUrl;
        this.message = message;
        this.time = time;
        this.showButtons = showButtons;
    }

    public String getProfileUrl() { return profileUrl; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public boolean isShowButtons() { return showButtons; }
}
