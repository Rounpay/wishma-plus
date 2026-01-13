package com.infotech.wishmaplus.Api.Response;


import java.util.List;

public class BoostBillingResponse {

    private int statusCode;
    private String responseText;
    private List<Result> result;

    // Getters and Setters
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

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    // Inner Result Class
    public static class Result {

        private int boostId;
        private String placement;
        private double budget;
        private double estimatedCost;
        private int gstPercent;
        private double subTotal;
        private double gst;
        private double totalCost;
        private String billingDate;

        // Getters and Setters
        public int getBoostId() {
            return boostId;
        }

        public void setBoostId(int boostId) {
            this.boostId = boostId;
        }

        public String getPlacement() {
            return placement;
        }

        public void setPlacement(String placement) {
            this.placement = placement;
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

        public int getGstPercent() {
            return gstPercent;
        }

        public void setGstPercent(int gstPercent) {
            this.gstPercent = gstPercent;
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

        public double getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }

        public String getBillingDate() {
            return billingDate;
        }

        public void setBillingDate(String billingDate) {
            this.billingDate = billingDate;
        }
    }
}
