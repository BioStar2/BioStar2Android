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
import android.util.Log;

import com.supremainc.biostar2.sdk.models.v2.preferrence.Preference;
import com.supremainc.biostar2.sdk.utils.PreferenceUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DateTimeDataProvider extends BaseDataProvider {
    public static final String PREF_DATE_FORMAT = "date_format";
    public static final String PREF_TIME_FORMAT = "time_format";
    public static final SimpleDateFormat mFormatterServer = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'", Locale.ENGLISH);
    /**
     * yyyy-MM-dd'T'HH:mm:ss.SS'Z'
     */
    protected static SimpleDateFormat mFormmaterWeek = new SimpleDateFormat("EEE");
    private static DateTimeDataProvider mSelf = null;
    @SuppressWarnings("unused")
    private final String TAG = getClass().getSimpleName();
    /**
     * a hh:mm:ss : default
     * HH:mm:ss
     * hh:mm:ss a
     */
    protected SimpleDateFormat mFormmaterHourMinSec;
    /**
     * a hh:mm : default
     * HH:mm
     * hh:mm a
     */
    protected SimpleDateFormat mFormmaterHourMin;
    /**
     * Date + a hh:mm:ss : default
     * Date + HH:mm:ss
     * Date + hh:mm:ss a
     */
    protected SimpleDateFormat mFormmaterDateHourMinSec;
    /**
     * Date + a hh:mm : default
     * Date + HH:mm
     * Date + hh:mm a
     */
    protected SimpleDateFormat mFormmaterDateHourMin;
    /**
     * yyyy/MM/dd : default
     * MM/dd/yyyy
     */
    protected SimpleDateFormat mFormmaterDate;
    Map<String, String> mMssageTable = new HashMap<String, String>();
    private long TIME_ZONE_ADJUST = -1;
    private int TIME_ZONE_INDEX = -1;
    private String DATE_FORMAT;
    private String TIME_FORMAT;

    private DateTimeDataProvider(Context context) {
        super(context);
    }

    public static DateTimeDataProvider getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new DateTimeDataProvider(context);
        }
        return mSelf;
    }

    public static DateTimeDataProvider getInstance() {
        if (mSelf != null) {
            return mSelf;
        }
        if (mContext != null) {
            mSelf = new DateTimeDataProvider(mContext);
            return mSelf;
        }
        return null;
    }

    public SimpleDateFormat getClientTimeFormat(DATE_TYPE type) {
        generatorFormatter();
        SimpleDateFormat clientFormatter = null;
        switch (type) {
            case FORMAT_DATE:
                clientFormatter = mFormmaterDate;
                break;
            case FORMAT_DATE_HOUR_MIN:
                clientFormatter = mFormmaterDateHourMin;
                break;
            case FORMAT_DATE_HOUR_MIN_SEC:
                clientFormatter = mFormmaterDateHourMinSec;
                break;
            case FORMAT_HOUR_MIN:
                clientFormatter = mFormmaterHourMin;
                break;
            case FORMAT_HOUR_MIN_SEC:
                clientFormatter = mFormmaterHourMinSec;
                break;
            case FORMAT_WEEK:
                clientFormatter = mFormmaterWeek;
                break;
            default:
                break;
        }
        return clientFormatter;
    }

    public Calendar convertServerTimeToCalendar(String src, boolean isApplyTimeZone) {
        Date date = null;
        long timZone = 0;
        try {
            date = DateTimeDataProvider.mFormatterServer.parse(src);
        } catch (java.text.ParseException e) {
            Log.e(TAG, "e:" + e.getMessage());
            return null;
        } catch (java.lang.NullPointerException e) {
            Log.e(TAG, "e:" + e.getMessage());
            return null;
        }
        if (isApplyTimeZone) {
            timZone = getTimeZoneAdjust();
        }
        date.setTime(date.getTime() + timZone);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public String convertCalendarToServerTime(Calendar calendar, boolean isApplyTimeZone) {
        long timZone = 0;
        if (isApplyTimeZone) {
            timZone = getTimeZoneAdjust() * -1;
        }
        Date date = new Date(calendar.getTimeInMillis() + timZone);
        return DateTimeDataProvider.mFormatterServer.format(date);
    }

    public String convertCalendarToFormatter(Calendar cal, SimpleDateFormat formmatter) {
        if (cal == null || formmatter == null) {
            return null;
        }
        return formmatter.format(cal.getTime());
    }

    public String convertCalendarToFormatter(Calendar cal, DATE_TYPE type) {
        if (cal == null) {
            return null;
        }
        SimpleDateFormat formatter = getClientTimeFormat(type);
        return convertCalendarToFormatter(cal, formatter);
    }

    public Calendar convertFormatterToCalendar(String src, SimpleDateFormat formatter) {
        if (src == null || src.isEmpty() || formatter == null) {
            return null;
        }
        Date date = null;
        try {
            date = formatter.parse(src);
        } catch (java.text.ParseException e) {
            Log.e(TAG, "e:" + e.getMessage());
            return null;
        } catch (java.lang.NullPointerException e) {
            Log.e(TAG, "e:" + e.getMessage());
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public Calendar convertFormatterToCalendar(String src, DATE_TYPE type) {
        SimpleDateFormat formatter = getClientTimeFormat(type);
        return convertFormatterToCalendar(src, formatter);
    }

    public enum DATE_TYPE {
        FORMAT_DATE, FORMAT_DATE_HOUR_MIN, FORMAT_DATE_HOUR_MIN_SEC, FORMAT_HOUR_MIN, FORMAT_HOUR_MIN_SEC, FORMAT_WEEK
    }

    public ArrayList<String> getDateFormatList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("yyyy/MM/dd");
        list.add("MM/dd/yyyy");
        list.add("dd/MM/yyyy");
        return list;
    }

    public ArrayList<String> getTimeFormatList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("HH:mm");
        list.add("a hh:mm");
        list.add("hh:mm a");
        return list;
    }


    public boolean getSavedPrefrence() {
        Preference content = new Preference();
        getTimeZoneAdjust();
        content.date_format = "yyyy/MM/dd";
        content.time_format = "a hh:mm";
        setDateTimeFormat(content.date_format, content.time_format);

        if (!generatorFormatter()) {
            return false;
        }
        return true;
    }

    public long getTimeZoneAdjust() {
        if (TIME_ZONE_ADJUST == -1) {
            setTimeZoneAdjust();
        }
        return TIME_ZONE_ADJUST;
    }

    public String getTimeZoneName() {
        TimeZone tz = TimeZone.getDefault();
        TIME_ZONE_ADJUST = tz.getRawOffset();
        return tz.getDisplayName();
    }

    public void setTimeZoneAdjust() {
        TimeZone tz = TimeZone.getDefault();
        TIME_ZONE_ADJUST = tz.getRawOffset();
    }

    public String getDateFormat() {
        if (DATE_FORMAT == null) {
            DATE_FORMAT = PreferenceUtil.getSharedPreference(mContext, PREF_DATE_FORMAT);
        }
        return DATE_FORMAT;
    }

    public String getTimeFormat() {
        if (TIME_FORMAT == null) {
            TIME_FORMAT = PreferenceUtil.getSharedPreference(mContext, PREF_TIME_FORMAT);
        }
        return TIME_FORMAT;
    }

    public void setDateTimeFormat(String date, String time) {
        DATE_FORMAT = date;
        TIME_FORMAT = time;
        PreferenceUtil.putSharedPreference(mContext, PREF_DATE_FORMAT, date);
        PreferenceUtil.putSharedPreference(mContext, PREF_TIME_FORMAT, time);
        mFormmaterDate = null;
        mFormmaterHourMin = null;
        mFormmaterHourMinSec = null;
        mFormmaterHourMin = null;
        mFormmaterHourMinSec = null;
        generatorFormatter();
    }

    protected boolean generatorFormatter() {
        getTimeZoneAdjust();
        getDateFormat();
        getTimeFormat();

        if (DATE_FORMAT == null || TIME_FORMAT == null) {
            return false;
        }
        if (mFormmaterDate == null) {
            mFormmaterDate = new SimpleDateFormat(DATE_FORMAT);
        }
        if (mFormmaterDateHourMin == null || mFormmaterHourMin == null) {
            String format = TIME_FORMAT;
            format = format.replaceAll(" ", "");
            if (format.startsWith("a")) {
                format = format.replace("a", "");
                format = "a " + format;
            } else if (format.endsWith("a")) {
                format = format.replace("a", "");
                format = format + " a";
            }
            mFormmaterDateHourMin = new SimpleDateFormat(DATE_FORMAT + " " + format);
            mFormmaterHourMin = new SimpleDateFormat(format);
        }
        if (mFormmaterDateHourMinSec == null || mFormmaterHourMinSec == null) {
            String format = TIME_FORMAT;
            if (format.startsWith("a")) {
                format = format.replace("a", "");
                format = "a " + format + ":ss";
            } else if (format.endsWith("a")) {
                format = format.replace("a", "");
                format = format + ":ss" + " a";
            } else {
                format = format + ":ss";
            }
            mFormmaterDateHourMinSec = new SimpleDateFormat(DATE_FORMAT + " " + format);
            mFormmaterHourMinSec = new SimpleDateFormat(format);
        }
        return true;
    }
}
