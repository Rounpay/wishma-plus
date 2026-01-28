package com.infotech.wishmaplus.Api.Request;

public enum TimeFilter {

    LIFETIME(0),
    TODAY(1),
    SEVEN_DAYS(2),
    FOURTEEN_DAYS(3),
    TWENTY_EIGHT_DAYS(4),
    NINETY_DAYS(5);

    private final int value;

    TimeFilter(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TimeFilter fromValue(int value) {
        for (TimeFilter filter : TimeFilter.values()) {
            if (filter.value == value) {
                return filter;
            }
        }
        return null; // or throw IllegalArgumentException
    }
}