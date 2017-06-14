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
package com.supremainc.biostar2.sdk.models.v2.login;

import com.google.gson.annotations.SerializedName;
import com.supremainc.biostar2.sdk.models.v2.device.BaseDevice;
import com.supremainc.biostar2.sdk.models.v2.door.BaseDoor;
import com.supremainc.biostar2.sdk.models.v2.user.BaseUser;
import com.supremainc.biostar2.sdk.provider.DateTimeDataProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
public class PushNotification implements Cloneable, Serializable {
    public static final String TAG = PushNotification.class.getSimpleName();
    private static final long serialVersionUID = -3331394917966008473L;
    @SerializedName("device")
    public BaseDevice device;
    @SerializedName("door")
    public BaseDoor door;
    @SerializedName("request_user")
    public BaseUser user;
    @SerializedName("message")
    public String message;
    @SerializedName("request_timestamp")
    public String request_timestamp;
    @SerializedName("title")
    public String title;
    @SerializedName("contact_phone_number")
    public String contact_phone_number;
    public String code;
    public int dbID = -1;
    @SerializedName("title-loc-key")
    public String title_loc_key;
    @SerializedName("loc-key")
    public String loc_key;
    @SerializedName("loc-args")
    public ArrayList<String> loc_args;
    /**
     * 0: read , 1: unread
     */
    public int unread;

    public String getTimeType(PushNotificationTimeType timeType) {
        String src = null;
        switch (timeType) {
            case request_timestamp:
                src = request_timestamp;
                break;
            default:
                break;
        }
        return src;
    }

    public boolean setTimeType(PushNotificationTimeType timeType, String src) {
        if (src == null || src.isEmpty()) {
            return false;
        }
        switch (timeType) {
            case request_timestamp:
                request_timestamp = src;
                break;
            default:
                return false;
        }
        return true;
    }

    public Calendar getTimeCalendar(DateTimeDataProvider convert, PushNotificationTimeType timeType) {
        return convert.convertServerTimeToCalendar(getTimeType(timeType), true);
    }

    public boolean setTimeCalendar(DateTimeDataProvider convert, PushNotificationTimeType timeType, Calendar cal) {
        return setTimeType(timeType, convert.convertCalendarToServerTime(cal, true));
    }

    public String getTimeFormmat(DateTimeDataProvider convert, PushNotificationTimeType timeType, DateTimeDataProvider.DATE_TYPE type) {
        Calendar cal = getTimeCalendar(convert, timeType);
        return convert.convertCalendarToFormatter(cal, type);
    }

    public boolean setTimeFormmat(DateTimeDataProvider convert, PushNotificationTimeType timeType, DateTimeDataProvider.DATE_TYPE type, String src) {
        Calendar cal = convert.convertFormatterToCalendar(src, type);
        return setTimeCalendar(convert, timeType, cal);
    }

    public PushNotification clone() throws CloneNotSupportedException {
        PushNotification target = (PushNotification) super.clone();
        if (device != null) {
            target.device = device.clone();
        }
        if (door != null) {
            target.door = door.clone();
        }
        if (user != null) {
            target.user = user.clone();
        }
        if (loc_args != null) {
            target.loc_args = (ArrayList<String>) loc_args.clone();
        }
        return target;
    }

    public enum PushNotificationTimeType {request_timestamp}
}