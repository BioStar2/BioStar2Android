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
package com.supremainc.biostar2.sdk.datatype.v2.Common;

import android.content.Context;
import android.util.Log;


import com.google.gson.annotations.SerializedName;
import com.supremainc.biostar2.sdk.BuildConfig;
import com.supremainc.biostar2.sdk.utils.PreferenceUtil;

public class VersionData {
    public static final String TAG = VersionData.class.getSimpleName();
    private static final long serialVersionUID = -6468886950746546333L;
    private static int CLOUD_VERSION = -1;
    private static String LOCAL_VERSION;
    private static int MAX_VERSION = 2;
    @SerializedName("status_code")
    public String statusCode;
    @SerializedName("message")
    public String message;
    @SerializedName("biostar_ac_version")
    public String biostar_ac_version;
    @SerializedName("biostar_ta_version")
    public String biostar_ta_version ;

    @SerializedName("cloud_version")
    public int cloud_version;

    public VersionData() {

    }

    public static int calcCloudVersion(String localVersion) {
        if (localVersion == null || (localVersion.indexOf(".") == -1) || localVersion.isEmpty()) {
            return -1;
        }
        String verions[] = localVersion.split("\\.");
        Integer intVerions[] = new Integer[4];
        for (int i=0; i < verions.length; i++) {
            if (i > 3) {
                break;
            }
            String ver = verions[i];
            ver = ver.replaceAll("[a-zA-Z\\@\\.\\-\\_]+","");
            intVerions[i] = Integer.valueOf(ver);
        }
        if (intVerions[0] < 2) {
            return -1;
        }
        if (intVerions[1] > 3) {
            return 2;
        } else {
            return 1;
        }
    }

    public boolean init(Context context) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG,"version:"+biostar_ac_version);
        }
        int cloudVersion = calcCloudVersion(biostar_ac_version);
        if (cloudVersion == -1) {
            return false;
        }
        if (!setCloudVersion(context,cloud_version)) {
            Log.e(TAG,"Version invalid"+cloud_version);
            cloud_version = MAX_VERSION;
            setCloudVersion(context,cloud_version);
        }
        setLocalVersion(context,biostar_ac_version);
        return true;
    }

    public static int getCloudVersion(Context context) {
        if (CLOUD_VERSION == -1) {
            CLOUD_VERSION = PreferenceUtil.getIntSharedPreference(context,"cloud_version");
        }
        return CLOUD_VERSION;
    }

    public static String getCloudVersionString(Context context) {
        if (CLOUD_VERSION == -1) {
            CLOUD_VERSION = getCloudVersion(context);
        }
        if (CLOUD_VERSION == -1) {
            return null;
        }
        if (CLOUD_VERSION > MAX_VERSION) {
            CLOUD_VERSION = MAX_VERSION;
            setCloudVersion(context,CLOUD_VERSION);
        }
        return "v"+CLOUD_VERSION+"/";
    }

    public static boolean setCloudVersion(Context context,int version) {
        try {
            int v =  Integer.valueOf(version);
            if (v < 1 || v > MAX_VERSION) {
                return false;
            }
            CLOUD_VERSION = v;
            PreferenceUtil.putSharedPreference(context, "cloud_version",CLOUD_VERSION);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getLocalVersion(Context context) {
        if (LOCAL_VERSION == null) {
            LOCAL_VERSION = PreferenceUtil.getSharedPreference(context,"local_version");
        }
        return LOCAL_VERSION;
    }


    public static boolean setLocalVersion(Context context,String version) {
        if (version == null || version.isEmpty()) {
            return false;
        }
        LOCAL_VERSION = version;
        PreferenceUtil.putSharedPreference(context, "local_version",LOCAL_VERSION);
        return true;

    }
}
