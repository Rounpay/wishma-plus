package com.infotech.wishmaplus.Api.Response;

public class EstimateResult {

    private String reach;
    private double budget;
    private double estimatedCost;
    private double subTotal;
    private double gst;
    private double total;
    private double userBalance;
    private int statusCode;
    private String responseText;

    // Getters and Setters
    public String getReach() {
        return reach;
    }

    public void setReach(String reach) {
        this.reach = reach;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getGst() {
        return gst;
    }

    public void setGst(double gst) {
        this.gst = gst;
    }

    public double getTotal() {
        return total;
    }
    public double getUserBalance() {
        return userBalance;
    }

    public void setTotal(double total) {
        this.total = total;
    }
    public void setUserBalance(double userBalance) {
        this.userBalance = userBalance;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }
}
