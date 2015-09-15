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

import com.supremainc.biostar2.sdk.datatype.PreferenceData.Preference;
import com.supremainc.biostar2.sdk.datatype.ResponseStatus;
import com.supremainc.biostar2.sdk.datatype.UpdateData;
import com.supremainc.biostar2.sdk.utils.PreferenceUtil;
import com.supremainc.biostar2.sdk.volley.Request.Method;
import com.supremainc.biostar2.sdk.volley.Response.ErrorListener;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CommonDataProvider extends BaseDataProvider {
	@SuppressWarnings("unused")
	private final String TAG = getClass().getSimpleName();
	private static CommonDataProvider mSelf = null;
	Map<String, String> mMssageTable = new HashMap<String, String>();

	public static final String PREF_DATE_FORMAT = "date_format";
	public static final String PREF_TIME_FORMAT = "time_format";
	public static final String PREF_LANGUAGE = "language";

	private long TIME_ZONE_ADJUST = -1;
	private int TIME_ZONE_INDEX = -1;
	private String DATE_FORMAT;
	private String TIME_FORMAT;

	private SimpleDateFormat mFormmaterSec;
	private SimpleDateFormat mFormmaterMin;
	private SimpleDateFormat mFormmaterDate;

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
		sendRequest(tag, UpdateData.class, Method.GET, NetWork.PARAM_VERSION + "/"+appName, null, params, null, listener, errorListener, deliverParam);
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
	public boolean getSavedPrefrence() {
		Preference content = new Preference();
		getTimeZoneAdjust();
		content.date_format = "yyyy/MM/dd";
		content.time_format = "hh:mm";
		setDateTimeFormat(mContext, content.date_format, content.time_format);

		if (!generatorFormatter(mContext)) {
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

	public String getDateFormat(Context context) {
		if (DATE_FORMAT == null) {
			DATE_FORMAT = PreferenceUtil.getSharedPreference(context, PREF_DATE_FORMAT);
		}
		return DATE_FORMAT;
	}

	public String getTimeFormat(Context context) {
		if (TIME_FORMAT == null) {
			TIME_FORMAT = PreferenceUtil.getSharedPreference(context, PREF_TIME_FORMAT);
		}
		return TIME_FORMAT;
	}

	public void setDateTimeFormat(Context context, String date, String time) {
		// DATE_FORMAT = date.replaceAll(" ", "");
		// TIME_FORMAT = time.replaceAll(" ", "");
		DATE_FORMAT = date;
		TIME_FORMAT = time;
		PreferenceUtil.putSharedPreference(context, PREF_DATE_FORMAT, date);
		PreferenceUtil.putSharedPreference(context, PREF_TIME_FORMAT, time);
		mFormmaterDate = null;
		mFormmaterMin = null;
		mFormmaterSec = null;
		generatorFormatter(context);
	}

	private boolean generatorFormatter(Context context) {
		getTimeZoneAdjust();
		getDateFormat(context);
		getTimeFormat(context);

		if (DATE_FORMAT == null || TIME_FORMAT == null) {
			return false;
		}
		if (mFormmaterDate == null) {
			mFormmaterDate = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
		}
		if (mFormmaterMin == null) {
			String format = TIME_FORMAT;
			format = format.replaceAll(" ", "");
			if (format.startsWith("a")) {
				format = format.replace("a", "");
				format = "a " + format;
			} else if (format.endsWith("a")) {
				format = format.replace("a", "");
				format = format + " a";
			}
			mFormmaterMin = new SimpleDateFormat(DATE_FORMAT + " " + format, Locale.ENGLISH);
		}
		if (mFormmaterSec == null) {
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
			mFormmaterSec = new SimpleDateFormat(DATE_FORMAT + " " + format, Locale.ENGLISH);
		}
		return true;

	}

	public enum DATE_TYPE {
		FORMAT_DATE, FORMAT_MIN, FORMAT_SEC
	};

	public SimpleDateFormat getClientTimeFormat(Context context, DATE_TYPE type) {
		generatorFormatter(context);
		SimpleDateFormat clientFormatter = null;
		switch (type) {
			case FORMAT_DATE :
				clientFormatter = mFormmaterDate;
				break;
			case FORMAT_MIN :
				clientFormatter = mFormmaterMin;
				break;
			case FORMAT_SEC :
				clientFormatter = mFormmaterSec;
				break;
			default :
				break;
		}
		return clientFormatter;
	}

	@SuppressWarnings("deprecation")
	private String convertTime(Context context, SimpleDateFormat srcFormat, SimpleDateFormat targetFormat, String srcTime, long timZone, boolean isDateEnd) {
		try {
			Date date = srcFormat.parse(srcTime);
			if (isDateEnd) {
				date.setHours(23);
				date.setMinutes(59);
				date.setSeconds(59);
			}
			date.setTime(date.getTime() + timZone);
			return targetFormat.format(date);
		} catch (Exception e) {
			Log.e("convert time", "" + e.getMessage());
		}
		return null;
	}

	public String convertClientTimeToServerTime(Context context, DATE_TYPE clientTimetype, String clientTime, boolean isApplyTimeZone) {
		SimpleDateFormat serverFormatter = ConfigDataProvider.mServerFormatter;
		SimpleDateFormat clientFormatter = getClientTimeFormat(context, clientTimetype);
		long timZone = 0;
		if (isApplyTimeZone) {
			timZone = getTimeZoneAdjust() * -1;
		}
		return convertTime(context, clientFormatter, serverFormatter, clientTime, timZone, false);
	}

	public String convertClientTimeToServerTimeDateEnd(Context context, DATE_TYPE clientTimetype, String clientTime, boolean isApplyTimeZone) {
		SimpleDateFormat serverFormatter = ConfigDataProvider.mServerFormatter;
		SimpleDateFormat clientFormatter = getClientTimeFormat(context, clientTimetype);
		long timZone = 0;
		if (isApplyTimeZone) {
			timZone = getTimeZoneAdjust() * -1;
		}
		return convertTime(context, clientFormatter, serverFormatter, clientTime, timZone, true);
	}

	public String convertServerTimeToClientTime(Context context, DATE_TYPE clientTimetype, String serverTime, boolean isApplyTimeZone) {
		SimpleDateFormat serverFormatter = ConfigDataProvider.mServerFormatter;
		SimpleDateFormat clientFormatter = getClientTimeFormat(context, clientTimetype);
		long timZone = 0;
		if (isApplyTimeZone) {
			timZone = getTimeZoneAdjust();
		}
		return convertTime(context, serverFormatter, clientFormatter, serverTime, timZone, false);
	}

}
