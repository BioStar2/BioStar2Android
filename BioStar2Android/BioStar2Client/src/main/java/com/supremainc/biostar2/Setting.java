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
package com.supremainc.biostar2;

import android.content.Context;

import com.supremainc.biostar2.sdk.volley.VolleyError;


public class Setting {
    public static final String ACTION_NOTIFICATION_START = "com.suprema.basic.NotificationStart";
    public static final String BROADCAST_ACCESS_GROUP = "com.suprema.basic.ListAccessGroup.BROADCAST";
    public static final String BROADCAST_ACCESS_LEVEL = "com.suprema.basic.AccessLevel.BROADCAST";
    public static final String BROADCAST_ALARM_UPDATE = "com.suprema.basic.Alarm.BROADCAST";
    public static final String BROADCAST_ALL_CLEAR = "com.suprema.basic.AllClear.BROADCAST";
    public static final String BROADCAST_CLEAR = "com.suprema.basic.Clear.BROADCAST";
    public static final String BROADCAST_DOOR_COUNT = "com.suprema.basic.DoorCount.BROADCAST";
    public static final String BROADCAST_PREFRENCE_REFRESH = "com.suprema.basic.PrefrenceRefresh.BROADCAST";
    public static final String BROADCAST_PUSH_TOKEN_UPDATE = "com.suprema.basic.TokenRefresh.BROADCAST";
    public static final String BROADCAST_REROGIN = "com.suprema.basic.RELOGIN.BROADCAST";
    public static final String BROADCAST_UPDATE_CARD = "com.suprema.basic.UpdateCard.BROADCAST";
    public static final String BROADCAST_UPDATE_DOOR = "com.suprema.basic.UpdateDoor.BROADCAST";
    public static final String BROADCAST_UPDATE_FINGER = "com.suprema.basic.UpdateFinger.BROADCAST";
    public static final String BROADCAST_UPDATE_PERMISSION = "com.suprema.basic.UpdatePermission.BROADCAST";
    public static final String BROADCAST_UPDATE_USER_ACCESS_GROUP = "com.suprema.basic.UpdateUserAccessGroup.BROADCAST";
    public static final String BROADCAST_GOTO_ALARMLIST = "com.suprema.basic.AlarmList.BROADCAST";
    public static final String BROADCAST_UPDATE_MYINFO = "com.suprema.basic.UpdateMyInfo.BROADCAST";

    public static final String BROADCAST_USER = "com.suprema.basic.User.BROADCAST";
    public static final String BROADCAST_USER_COUNT = "com.suprema.basic.UserCount.BROADCAST";
    public static final String DISABLE_MODIFY = "disable_modify";

    public static final boolean IS_AUTO_CREATE_USER = false;
    public static final boolean IS_AUTO_LOG_SCROLL = false;
    public static final boolean IS_DELETE_ALL_USER = false;
    public static final boolean IS_FAKE_PUSH_DATA = false;
    public static final boolean IS_GOOGPLAY_SERVICE = false;
    public static final boolean IS_TEST_OPEN_DOOR_REQUEST = false;
    public static final boolean IS_NOTIFICATION_NONE_RESTART = true;
    public static final boolean IS_CRASH_REPORT = true;
    public static final String UPDATE_CANCEL_VERSION = "update_cancel";
    public static final int REQUEST_EXTERNAL_STORAGE = 200;
    public static final int REQUEST_READ_PHONE_STATE = 201;

    public static final int USER_PROFILE_IMAGE_SIZE = 400;
    public static final int USER_PROFILE_IMAGE_SIZE_BYTE = 16000;
    public static final String CRITTERISM = "555e7af0b60a7d3e63908d21";
    // IOS (R.string.no_permission)
    public static String getDebugFlag() {
        String result = "";
        if (IS_AUTO_CREATE_USER) {
            result = result + "IS_AUTO_CREATE_USER\n";
        }
        if (IS_AUTO_LOG_SCROLL) {
            result = result + "IS_AUTO_LOG_SCROLL\n";
        }
        if (IS_TEST_OPEN_DOOR_REQUEST) {
            result = result + "IS_TEST_OPEN_DOOR_REQUEST\n";
        }
        if (IS_FAKE_PUSH_DATA) {
            result = result + "IS_FAKE_PUSH_DATA\n";
        }
        if (BuildConfig.DEBUG) {
            result = result + "DEBUG BUILD\n";
        }
        return result;
    }

    public static String getErrorMessage(VolleyError volleyError, Context context) {
        return getErrorMessage(volleyError, context.getString(R.string.error_network2));
    }

    public static String getErrorMessage(VolleyError volleyError, String defaultMessage) {
        if (volleyError == null) {
            return defaultMessage;
        }
        String result = volleyError.getMessage();
        if (result == null || result.equals("")) {
            return defaultMessage;
        }

        if (result.indexOf("UnknownHostException") > -1) {
            return defaultMessage;
        }

//		if (result.indexOf("Timeout") > -1) {
//			return result;
//		}

        if (result.indexOf("ECONNREFUSED") > -1 || result.indexOf("Exception") > -1) {
            int splite = result.lastIndexOf(":");
            if (splite > -1) {
                result = result.substring(splite + 1, result.length());
            }
        }


//		if (volleyError.networkResponse != null && volleyError.networkResponse.statusCode == 502) {
//			return context.getString();
//		}
        return result;
    }

    public static String getLoginErrorMessage(VolleyError volleyError, Context context) {
        return getErrorMessage(volleyError, context.getString(R.string.error_network));
    }
}