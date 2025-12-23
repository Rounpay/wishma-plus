package com.infotech.wishmaplus.Utils;

public enum BoostStatus {
    PENDING(1),
    BOOST_START(2),
    BOOST_STOP(3),
    NOT_RUNNING(0);

    private final int value;

    BoostStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public static BoostStatus fromValue(int value) {
        for (BoostStatus status : BoostStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return NOT_RUNNING;
    }


}
