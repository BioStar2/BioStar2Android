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
package com.supremainc.biostar2.sdk.provider;

import android.content.Context;

import com.supremainc.biostar2.sdk.BuildConfig;
import com.supremainc.biostar2.sdk.models.enumtype.LocalStorage;
import com.supremainc.biostar2.sdk.models.v2.common.VersionData;
import com.supremainc.biostar2.sdk.utils.PreferenceUtil;

import static com.supremainc.biostar2.sdk.provider.BaseDataProvider.mContext;

public class ConfigDataProvider  {
    private final String TAG = getClass().getSimpleName();
    public static final boolean TEST_RELEASE_DELETE = true;
    public static final boolean TEST_DELETE = false;
    public static final boolean DEBUG = false;
    public static final boolean DEBUG_SDCARD = false;
    public static final boolean SSL_ALL_PASS = false;
    public static final String LOGIN_EXPIRE = "authentication expire";

    public static final String URL = "https://api.biostar2.com/";
    public static final String V1 = "v1/";
    public static final String V2 = "v2/";
    private static int mComplie = 2;

    public static String getDebugFlag() {
        String result = "";
        if (TEST_DELETE) {
            result = result + "TEST CODE\n";
        }
        if (DEBUG) {
            result = result + "DEBUG\n";
            result = result + mComplie+ "\n";
        }
        if (DEBUG_SDCARD) {
            result = result + "SDCARD\n";
        }
        if (SSL_ALL_PASS) {
            result = result + "SSL_ALL_PASS\n";
        }
        if (BuildConfig.DEBUG) {
            result = result + "DEBUG BUILD\n";
        }
        return result;
    }

    public enum NetworkType {
        HURL, HTTP_CLIENT, OK_HTTP
    }

//    public static  String getLatestDomain(Context context) {
//        if (mSubDomain == null || mSubDomain.isEmpty()) {
//             mSubDomain = PreferenceUtil.getSharedPreference(context, LATEST_DOMAIN);
//            if (mSubDomain == null) {
//                return null;
//            }
//            setLatestDomain(context,mSubDomain);
//        }
//        return mSubDomain;
//    }

//    public static void setLatestDomain(Context context,String subDomain) {
//        mSubDomain = subDomain;
//        PreferenceUtil.putSharedPreference(context, LATEST_DOMAIN, subDomain);
//    }

//    public static String getLatestUserID(Context context) {
//        if (mUserID == null) {
//            mUserID = PreferenceUtil.getSharedPreference(context, LATEST_USERID);
//            if (mUserID == null) {
//                return null;
//            }
//            setLatestUserID(context,mUserID);
//        }
//        return mUserID;
//    }

//    public static void setLatestUserID(Context context,String id) {
//        mUserID = id;
//        PreferenceUtil.putSharedPreference(context, LATEST_USERID, mUserID);
//    }

//    public static String getLatestURL(Context context) {
//        String url = PreferenceUtil.getSharedPreference(context, LATEST_URL);
//        if (url == null || url.isEmpty()) {
//            return ConfigDataProvider.URL;
//        }
//        return url;
//    }

////    public static void setLatestURL(Context context,String url) {
//        PreferenceUtil.putSharedPreference(context,LATEST_URL,url);
//    }

    public static String getFullURL(Context context) {
        String url = (String) getLocalStorage(LocalStorage.DOMAIN);
        if (url == null || VersionData.getCloudVersionString(context) == null) {
            return null;
        }


        if (url.endsWith("/")) {
           return  url +VersionData.getCloudVersionString(context);
        } else {
           return  url +"/"+VersionData.getCloudVersionString(context);
        }
    }

    public static void setLocalStorage(LocalStorage type, String content) {
        switch (type.type) {
            case STRING:
                PreferenceUtil.putSharedPreference(mContext, type.name, content);
                break;
            default:
                break;
        }
    }

    public static Object getLocalStorage(LocalStorage type) {
        switch (type.type) {
            case STRING:
                return PreferenceUtil.getSharedPreference(mContext, type.name);
            default:
                break;
        }
        return null;
    }
}
