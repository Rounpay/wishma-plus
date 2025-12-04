package com.infotech.wishmaplus.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    public String LoginPref = "LOGIN_PREF";
    private static final String PREF_NAME = "MyPrefs";
    private static final String PREF_NAME_NON_REMOVAL = "MyPrefsNonRemoval";
    private static final String TOKEN_KEY = "token";
    private static final String REFRESH_TOKEN_KEY = "refreshToken";
    private static final String USER_ID = "userId";
    private static final String USER_NAME = "userName";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";



    private SharedPreferences generalPreferences;
    private SharedPreferences generalNonRemovalPreferences ;

    public PreferencesManager(Context context, int type) {

        if(type==3){
            generalNonRemovalPreferences = context.getSharedPreferences(PREF_NAME_NON_REMOVAL, Context.MODE_PRIVATE);
        }else if(type ==2){
            generalPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            generalNonRemovalPreferences = context.getSharedPreferences(PREF_NAME_NON_REMOVAL, Context.MODE_PRIVATE);
        }else {
            generalPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            generalNonRemovalPreferences = context.getSharedPreferences(PREF_NAME_NON_REMOVAL, Context.MODE_PRIVATE);
        }
    }



    // General Preferences Methods
    public void set(String key, String value) {
        SharedPreferences.Editor editor = generalPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setNonRemoval(String key, String value) {
        SharedPreferences.Editor editor = generalNonRemovalPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setNonRemoval(String key, boolean value) {
        SharedPreferences.Editor editor = generalNonRemovalPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return generalPreferences.getString(key, "");
    }

    public String getStringNonRemoval(String key) {
        return generalNonRemovalPreferences.getString(key, "");
    }

    public boolean getBooleanNonRemoval(String key) {
        return generalNonRemovalPreferences.getBoolean(key, false);
    }

    // Token-related Preferences Methods
    public void saveTokens(String accessToken, String refreshToken, String userId,String username,String firstName,String lastName) {
        SharedPreferences.Editor editor = generalPreferences.edit();
        editor.putString(TOKEN_KEY, accessToken);
        editor.putString(REFRESH_TOKEN_KEY, refreshToken);
        editor.putString(USER_ID, userId);
        editor.putString(USER_NAME, username);
        editor.putString(FIRST_NAME, firstName);
        editor.putString(LAST_NAME, lastName);
        editor.apply();
    }


    public String getAccessToken() {
        return generalPreferences.getString(TOKEN_KEY, "");
    }

    public String getUserId() {
        return generalPreferences.getString(USER_ID, "");
    }

    public String getRefreshToken() {
        return generalPreferences.getString(REFRESH_TOKEN_KEY, "");
    }

    public String getUserName() {
        return generalPreferences.getString("username", ""); // Replace with your key
    }
    public void setLastName(String lastName) {
        set("lastName", lastName); // Save the last name
    }

    public String getLastName() {
        return getString("lastName"); // Retrieve the last name
    }
    public void setFirstName(String firstName) {
        set("firstName", firstName); // Save the last name
    }

    public String getFirstName() {
        return getString("firstName"); // Retrieve the last name
    }

    // Non-Removal Preferences Methods



    // Other General Preferences Methods
    public void clear() {
        SharedPreferences.Editor editor = generalPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void clearNonRemoval() {
        SharedPreferences.Editor editor = generalNonRemovalPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
