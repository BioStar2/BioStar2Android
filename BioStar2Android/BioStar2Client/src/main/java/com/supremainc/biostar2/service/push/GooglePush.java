/*
 * Copyright 2015 Suprema(biostar2@suprema.co.kr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.supremainc.biostar2.service.push;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.provider.PushDataProvider;
import com.supremainc.biostar2.sdk.utils.PreferenceUtil;

import java.io.IOException;
import java.util.Random;

public class GooglePush {
    public static final String TAG = GooglePush.class.getSimpleName();
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1972;
    private static final String mPushID = "set your push id";
    private Activity mActivity = null;
    private PushDataProvider mPushDataProvider;
    private Random mRandom;
    private GoogleCloudMessaging mGcm;
    private String mRegID;

    public GooglePush(Activity activity) {
        mActivity = activity;
        mPushDataProvider = PushDataProvider.getInstance(mActivity.getApplicationContext());
    }

    public void checkNotification() {
        String regID = getRegistrationId();
        if (TextUtils.isEmpty(regID)) {
            registerInBackground();
            return;
        }
        mPushDataProvider.checkUpdateNotificationToken(regID);
    }


    private boolean checkPlayServices() {
//		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
//		if (resultCode != ConnectionResult.SUCCESS) {
//			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//				GooglePlayServicesUtil.getErrorDialog(resultCode, mActivity, REQUEST_GOOGLE_PLAY_SERVICES).show();
//			}
//			return false;
//		}
//        return true;
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(mActivity);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(mActivity, result,
                        REQUEST_GOOGLE_PLAY_SERVICES).show();
            }
            return false;
        }

        return true;
    }

    private int getAppVersion() {
        try {
            PackageInfo packageInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            return -1;
        }
    }

    public String getRegistrationId() {
        String registrationId = PreferenceUtil.getSharedPreference(mActivity, "registrationId");
        if (TextUtils.isEmpty(registrationId)) {
            return null;
        }
        int registeredVersion = PreferenceUtil.getIntSharedPreference(mActivity, "version");
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion || currentVersion == -1) {
            return null;
        }
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "getRegistrationId:" + registrationId);
        }
        return registrationId;
    }

    public void init() {
        if (Setting.IS_GOOGPLAY_SERVICE) {
            if (checkPlayServices()) {
                mGcm = GoogleCloudMessaging.getInstance(mActivity);

                mRegID = getRegistrationId();

                if (TextUtils.isEmpty(mRegID)) {
                    registerInBackground();
                }
            }
        } else {
            mRegID = getRegistrationId();
            if (TextUtils.isEmpty(mRegID)) {
                registerInBackground();
            }
        }
    }

    // get registration id
    private void registerInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (mPushID.equals("set your push id")) {
                    Log.e(TAG, "mPushID set your push id");
                    return null;
                }
                if (Setting.IS_GOOGPLAY_SERVICE) {
                    try {
                        if (mGcm == null) {
                            mGcm = GoogleCloudMessaging.getInstance(mActivity.getApplicationContext());
                        }
                        mRegID = mGcm.register(mPushID);
                        if (mRegID != null && !mRegID.isEmpty()) {
                            PreferenceUtil.putSharedPreference(mActivity, "registrationId", mRegID);
                            PreferenceUtil.putSharedPreference(mActivity, "version", getAppVersion());
                            mPushDataProvider.setNeedUpdateNotificationToken(mRegID);
                        }
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "registerInBackground:" + mRegID);
                        }
                    } catch (IOException ex) {
                        Log.e(TAG, "Error :" + ex.getMessage());
                    }
                } else {
                    Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
                    intent.setPackage("com.google.android.gsf");
                    intent.putExtra("sender", mPushID);
                    if (mRandom == null) {
                        mRandom = new Random();
                    }
                    PendingIntent appIntent = PendingIntent.getBroadcast(mActivity, mRandom.nextInt(), new Intent(), 0);
                    intent.putExtra("app", appIntent);
                    ComponentName name = null;
                    try {
                        name = mActivity.startService(intent);
                    } catch (SecurityException exception) {
                    }
                    if (name == null) {
                        Log.e(TAG, "NOT AVAILABLE GSF");
                    }
                }
                return null;
            }
        }.execute(null, null, null);
    }

}
