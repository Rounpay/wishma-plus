package com.infotech.wishmaplus.Api.Response;

/**
 * @Created by akash on 04-02-2025.
 * Know more about author at https://akash.cloudemy.in
 */
public  class Income {
    private String subscriberID;
    private String userName;
    private int commAmt;
    private String levelNo;
    private String closingDate;
    private int tid;
    private String incomeType;

    // Getters and Setters
    public String getSubscriberID() {
        return "Subscriber ID: "+subscriberID;
    }

    public void setSubscriberID(String subscriberID) {
        this.subscriberID = subscriberID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommAmt() {
        return "₹"+commAmt;
    }

    public void setCommAmt(int commAmt) {
        this.commAmt = commAmt;
    }

    public String getLevelNo() {
        return "Level No: "+ levelNo;
    }

    public void setLevelNo(String levelNo) {
        this.levelNo = levelNo;
    }

    public String getClosingDate() {
        return "Closing Date: "+closingDate;
    }

    public void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }

    public String getTid() {
        return "TID: "+tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getIncomeType() {
        return "Income Type: "+incomeType;
    }

    public void setIncomeType(String incomeType) {
        this.incomeType = incomeType;
    }
}
