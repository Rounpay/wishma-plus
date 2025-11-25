package com.infotech.wishmaplus.Utils;

public enum ApplicationConstant {
    INSTANCE;
    public final String Domain = "wishmaplus.com";
    public final String baseUrl = "https://" + Domain;
    public final String apiUrl = "https://api." + Domain + "/";
    public final String postUrl = baseUrl + "/post/";
    public final String prefNamePref = "wishmaPref";
    public final String regRecentLoginPref = "regRecentLoginPref";
    public final String LoginPref = "LOGIN_PREF";
    public final String ProfilePref = "PROFILE_PREF";
    public final String SomethingError = "Some thing error, please try after some time";
    public final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    public final String UserReferralPref = "UserReferralPref";
    public final String isUserReferralSetPref = "isUserReferralSetPref";
    public final String regFCMKeyPref = "regFCMkeyPref";
    public final String privacy_policy_url = baseUrl + "/privacy";
    public final String term_condition_url = baseUrl + "/term";
}
