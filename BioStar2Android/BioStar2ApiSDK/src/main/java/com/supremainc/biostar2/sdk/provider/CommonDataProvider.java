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

import com.supremainc.biostar2.sdk.datatype.v2.Common.BioStarSetting;
import com.supremainc.biostar2.sdk.datatype.v2.Common.ResponseStatus;
import com.supremainc.biostar2.sdk.datatype.v2.Common.UpdateData;
import com.supremainc.biostar2.sdk.datatype.v2.Preferrence.Preference;
import com.supremainc.biostar2.sdk.utils.PreferenceUtil;
import com.supremainc.biostar2.sdk.volley.Request.Method;
import com.supremainc.biostar2.sdk.volley.Response.ErrorListener;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CommonDataProvider extends BaseDataProvider {
    public static final String PREF_DATE_FORMAT = "date_format";
    public static final String PREF_TIME_FORMAT = "time_format";
    public static final String PREF_LANGUAGE = "language";
    public static final SimpleDateFormat mFormatterServer = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'", Locale.ENGLISH);
    /**
     * yyyy-MM-dd'T'HH:mm:ss.SS'Z'
     */
    protected static SimpleDateFormat mFormmaterWeek = new SimpleDateFormat("EEE");
    private static CommonDataProvider mSelf = null;
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

    private CommonDataProvider(Context context) {
        super(context);
    }

    public static CommonDataProvider getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new CommonDataProvider(context);
        }
        return mSelf;
    }

    public static CommonDataProvider getInstance() {
        if (mSelf != null) {
            return mSelf;
        }
        if (mContext != null) {
            mSelf = new CommonDataProvider(mContext);
            return mSelf;
        }
        return null;
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

    public void getAppVersion(String tag, Listener<UpdateData> listener, ErrorListener errorListener, String appName, Object deliverParam) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile_device_type", "ANDROID");
        sendRequest(tag, UpdateData.class, Method.GET, NetWork.PARAM_VERSION + "/" + appName, null, params, null, listener, errorListener, deliverParam);
    }

    public void setPreference(Preference content, final Listener<ResponseStatus> listener, ErrorListener errorListener, final Object deliverParam) {
        if (getLoginUserInfo() == null || content == null) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new VolleyError(), deliverParam);
            }
            return;
        }

        final String json = mGson.toJson(content);
        String url = NetWork.PARAM_SETTING;

        final Listener<ResponseStatus> innerListener = new Listener<ResponseStatus>() {
            @Override
            public void onResponse(ResponseStatus response, Object param) {
                Preference content = (Preference) param;
                setDateTimeFormat(mContext, content.date_format, content.time_format);
                if (listener != null) {
                    listener.onResponse(response, deliverParam);
                }
            }
        };

        // ErrorListener inErrorListerner = new ErrorListener() {
        // @Override
        // public void onErrorResponse(VolleyError error, Object param) {
        // if (error.getCode().equals(608)) {
        // Preference content = (Preference)param;
        // mNetwork.sendRequest(null, ResponseStatus.class, Method.POST,
        // NetWork.PARAM_SETTING, null, null, json, innerListener,
        // errorListener, content);
        // } else if (errorListener != null){
        // errorListener.onErrorResponse(error, deliverParam);
        // }
        // }
        // };

        mNetwork.sendRequest(null, ResponseStatus.class, Method.PUT, url, null, null, json, innerListener, errorListener, content);
    }

    public void getPreference(final Listener<Preference> listener, final ErrorListener errorListener, Object deliverParam) {
        Listener<Preference> inListener = new Listener<Preference>() {
            @Override
            public void onResponse(Preference response, Object param) {
                if (response == null) {
                    errorListener.onErrorResponse(new VolleyError(""), param);
                    return;
                }
                setDateTimeFormat(mContext, response.date_format, response.time_format);
                listener.onResponse(response, param);
            }
        };
        if (getLoginUserInfo() == null) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new VolleyError(), deliverParam);
            }
            return;
        }
        String url = NetWork.PARAM_SETTING;
        mNetwork.sendRequest(null, Preference.class, Method.GET, url, null, null, null, inListener, errorListener, deliverParam);
    }

    public void getBioStarSetting(final Listener<BioStarSetting> listener, final ErrorListener errorListener, Object deliverParam) {
        Listener<BioStarSetting> inListener = new Listener<BioStarSetting>() {
            @Override
            public void onResponse(BioStarSetting response, Object param) {
                if (response == null) {
                    if (errorListener != null) {
                        errorListener.onErrorResponse(new VolleyError(""), param);
                    }
                    return;
                }
                mPasswordLevel = response.password_strength_level;
                mAlphaNumericUserID = response.use_alphanumeric_user_id;
                if (listener != null) {
                    listener.onResponse(response, param);
                }
            }
        };

        mNetwork.sendRequest(null, BioStarSetting.class, Method.GET, createUrl( NetWork.PARAM_SETTING, NetWork.PARAM_BIOSTAR_AC), null, null, null, inListener, errorListener, deliverParam);
    }


    public boolean getSavedPrefrence() {
        Preference content = new Preference();
        getTimeZoneAdjust();
        content.date_format = "yyyy/MM/dd";
        content.time_format = "a hh:mm";
        setDateTimeFormat(mContext, content.date_format, content.time_format);

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

    public void setDateTimeFormat(Context context, String date, String time) {
        DATE_FORMAT = date;
        TIME_FORMAT = time;
        PreferenceUtil.putSharedPreference(context, PREF_DATE_FORMAT, date);
        PreferenceUtil.putSharedPreference(context, PREF_TIME_FORMAT, time);
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


//	@SuppressWarnings("deprecation")
//	private String convertTime(SimpleDateFormat srcFormat, SimpleDateFormat targetFormat, String srcTime, long timZone, boolean isDateEnd) {
//		try {
//			Date date = srcFormat.parse(srcTime);
//			if (isDateEnd) {
//				date.setHours(23);
//				date.setMinutes(59);
//				date.setSeconds(59);
//			}
//			date.setTime(date.getTime() + timZone);
//			return targetFormat.format(date);
//		} catch (Exception e) {
//			Log.e("convert time", "" + e.getMessage());
//		}
//		return null;
//	}
//
//	public String convertClientTimeToServerTime(DATE_TYPE clientTimetype, String clientTime, boolean isApplyTimeZone) {
//		SimpleDateFormat clientFormatter = getClientTimeFormat(mContext, clientTimetype);
//		long timZone = 0;
//		if (isApplyTimeZone) {
//			timZone = getTimeZoneAdjust() * -1;
//		}
//		return convertTime(clientFormatter, mFormatterServer, clientTime, timZone, false);
//	}
//
//	public String convertClientTimeToServerTimeDateEnd(DATE_TYPE clientTimetype, String clientTime, boolean isApplyTimeZone) {
//		SimpleDateFormat clientFormatter = getClientTimeFormat(mContext, clientTimetype);
//		long timZone = 0;
//		if (isApplyTimeZone) {
//			timZone = getTimeZoneAdjust() * -1;
//		}
//		return convertTime(clientFormatter, mFormatterServer, clientTime, timZone, true);
//	}
//
//	public String convertServerTimeToClientTime(DATE_TYPE clientTimetype, String serverTime, boolean isApplyTimeZone) {
//		SimpleDateFormat clientFormatter = getClientTimeFormat(mContext, clientTimetype);
//		long timZone = 0;
//		if (isApplyTimeZone) {
//			timZone = getTimeZoneAdjust();
//		}
//		return convertTime(mFormatterServer, clientFormatter, serverTime, timZone, false);
//	}

}
