package com.infotech.wishmaplus.Utils;

import android.app.Activity;
import android.os.RemoteException;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
/**
 * Created by Vishnu Agarwal on 07,November,2024
 */

public class InstallReferral {

    InstallReferrerClient referrerClient;
    Activity mActivity;

    public InstallReferral(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void InstllReferralData(PreferencesManager mAppPreferences) {
        referrerClient = InstallReferrerClient.newBuilder(mActivity).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        ReferrerDetails response = null;

                        try {
                            response = referrerClient.getInstallReferrer();
                            if (response != null) {
                                String referrerUrl = response.getInstallReferrer();
                                // long referrerClickTime = response.getReferrerClickTimestampSeconds();
                                //long appInstallTime = response.getInstallBeginTimestampSeconds();
                                //  boolean instantExperienceLaunched = response.getGooglePlayInstantParam();

                                if (referrerUrl != null && !referrerUrl.isEmpty() & !referrerUrl.contains("utm") && !referrerUrl.contains("google") && !referrerUrl.contains("chrome")) {

                                    try {
                                        String referrerCode = referrerUrl.substring(referrerUrl.lastIndexOf("/") + 1);
                                       // int referrerCode = Integer.parseInt(referrerUrl.trim().replaceAll(" ", ""));
                                        mAppPreferences.setNonRemoval(ApplicationConstant.INSTANCE.UserReferralPref, referrerCode + "");

                                    } catch (NumberFormatException nfe) {

                                    }

                                }
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mAppPreferences.setNonRemoval(ApplicationConstant.INSTANCE.isUserReferralSetPref, true);
                        referrerClient.endConnection();
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        mAppPreferences.setNonRemoval(ApplicationConstant.INSTANCE.isUserReferralSetPref, true);
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }
}
