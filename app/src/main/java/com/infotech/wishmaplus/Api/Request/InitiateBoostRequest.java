package com.infotech.wishmaplus.Api.Request;

public class InitiateBoostRequest {

    private String tid;
    private String salt;
    private int boostId;
    private String postId;
    private String url;
    private String phoneNo;
    private String postXML;
    private double budget;
    private double estimatedCost;
    private double subTotal;
    private double gstAmount;
    private double total;
    private int durationDays;
    private String boostEndDate;
    private int audienceId;
    private int minAge;
    private int maxAge;
    private int gender;
    private String placement;

    // Constructor
    public InitiateBoostRequest(String tid,String salt,int boostId,String postId, String url, String phoneNo, String postXML,
                                double budget, double estimatedCost, double subTotal, double gstAmount,
                                double total, int durationDays, String boostEndDate,
                                int audienceId, int minAge, int maxAge,
                                int gender, String placement) {
        this.tid = tid;
        this.salt = salt;
        this.boostId = boostId;
        this.postId = postId;
        this.url = url;
        this.phoneNo = phoneNo;
        this.postXML = postXML;
        this.budget = budget;
        this.estimatedCost = estimatedCost;
        this.subTotal = subTotal;
        this.gstAmount = gstAmount;
        this.total = total;
        this.durationDays = durationDays;
        this.boostEndDate = boostEndDate;
        this.audienceId = audienceId;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.gender = gender;
        this.placement = placement;
    }
}

