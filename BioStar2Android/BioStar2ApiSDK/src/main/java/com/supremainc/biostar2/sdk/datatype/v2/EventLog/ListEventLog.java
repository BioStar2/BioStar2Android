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
package com.supremainc.biostar2.sdk.datatype.v2.EventLog;

import com.google.gson.annotations.SerializedName;
import com.supremainc.biostar2.sdk.datatype.v2.Device.BaseDevice;
import com.supremainc.biostar2.sdk.datatype.v2.Door.BaseDoor;
import com.supremainc.biostar2.sdk.datatype.v2.User.BaseUser;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;

import java.io.Serializable;
import java.util.Calendar;

public class ListEventLog extends BaseEventLog implements Cloneable, Serializable {
    public static final String TAG = ListEventLog.class.getSimpleName();
    private static final long serialVersionUID = -7033989203058201905L;
    @SerializedName("device")
    public BaseDevice device;
    @SerializedName("user")
    public BaseUser user;
    @SerializedName("door")
    public BaseDoor door;
    /**
     * type of event ('DEVICE', 'DOOR', 'ALERT'),
     */
    @SerializedName("type")
    public String type;
    @SerializedName("description")
    public String description;
    @SerializedName("datetime")
    public String datetime;
    @SerializedName("index")
    public String index;
    @SerializedName("server_datetime")
    public String server_datetime;
    /**
     * event log level('GREEN', 'YELLOW', 'RED'),
     */
    @SerializedName("level")
    public String level;
    public ListEventLog() {

    }

    public String getTimeType(ListEventLogTimeType timeType) {
        String src = null;
        switch (timeType) {
            case datetime:
                src = datetime;
                break;
            case server_datetime:
                src = server_datetime;
                break;
            default:
                break;
        }
        return src;
    }

    public boolean setTimeType(ListEventLogTimeType timeType, String src) {
        if (src == null || src.isEmpty()) {
            return false;
        }
        switch (timeType) {
            case datetime:
                datetime = src;
                break;
            case server_datetime:
                server_datetime = src;
                break;
            default:
                return false;
        }
        return true;
    }

    public Calendar getTimeCalendar(TimeConvertProvider convert, ListEventLogTimeType timeType) {
        return convert.convertServerTimeToCalendar(getTimeType(timeType), true);
    }

    public boolean setTimeCalendar(TimeConvertProvider convert, ListEventLogTimeType timeType, Calendar cal) {
        return setTimeType(timeType, convert.convertCalendarToServerTime(cal, true));
    }

    public String getTimeFormmat(TimeConvertProvider convert, ListEventLogTimeType timeType, TimeConvertProvider.DATE_TYPE type) {
        Calendar cal = getTimeCalendar(convert, timeType);
        return convert.convertCalendarToFormatter(cal, type);
    }

    public boolean setTimeFormmat(TimeConvertProvider convert, ListEventLogTimeType timeType, TimeConvertProvider.DATE_TYPE type, String src) {
        Calendar cal = convert.convertFormatterToCalendar(src, type);
        return setTimeCalendar(convert, timeType, cal);
    }

    public ListEventLog clone() throws CloneNotSupportedException {
        ListEventLog target = (ListEventLog) super.clone();
        if (device != null) {
            target.device = device.clone();
        }
        if (user != null) {
            target.user = user.clone();
        }
        if (door != null) {
            target.door = door.clone();
        }
        return target;
    }

    public enum ListEventLogTimeType {datetime, server_datetime}
}

