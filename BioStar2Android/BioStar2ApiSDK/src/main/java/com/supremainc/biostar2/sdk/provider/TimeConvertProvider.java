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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeConvertProvider extends BaseDataProvider {
	private final String TAG = getClass().getSimpleName();
	private static TimeConvertProvider mSelf = null;
	private CommonDataProvider mCommonDataProvider;
	public enum DATE_TYPE {
		FORMAT_DATE, FORMAT_DATE_HOUR_MIN, FORMAT_DATE_HOUR_MIN_SEC, FORMAT_HOUR_MIN, FORMAT_HOUR_MIN_SEC, FORMAT_WEEK
	};

	private TimeConvertProvider(Context context) {
		super(context);
		mCommonDataProvider = CommonDataProvider.getInstance(context);
	}

	public static TimeConvertProvider getInstance(Context context) {
		if (mSelf == null) {
			mSelf = new TimeConvertProvider(context);
		}
		return mSelf;
	}

	public static TimeConvertProvider getInstance() {
		if (mSelf != null) {
			return mSelf;
		}
		if (mContext != null) {
			mSelf = new TimeConvertProvider(mContext);
			return mSelf;
		}
		return null;
	}

	public SimpleDateFormat getClientTimeFormat(DATE_TYPE type) {
		mCommonDataProvider.generatorFormatter();
		SimpleDateFormat clientFormatter = null;
		switch (type) {
			case FORMAT_DATE :
				clientFormatter = mCommonDataProvider.mFormmaterDate;
				break;
			case FORMAT_DATE_HOUR_MIN :
				clientFormatter = mCommonDataProvider.mFormmaterDateHourMin;
				break;
			case FORMAT_DATE_HOUR_MIN_SEC :
				clientFormatter = mCommonDataProvider.mFormmaterDateHourMinSec;
				break;
			case FORMAT_HOUR_MIN :
				clientFormatter = mCommonDataProvider.mFormmaterHourMin;
				break;
			case FORMAT_HOUR_MIN_SEC :
				clientFormatter = mCommonDataProvider.mFormmaterHourMinSec;
				break;
			case FORMAT_WEEK :
				clientFormatter = mCommonDataProvider.mFormmaterWeek;
				break;
			default :
				break;
		}
		return clientFormatter;
	}

	public Calendar convertServerTimeToCalendar(String src,boolean isApplyTimeZone) {
		Date date = null;
		long timZone = 0;
		try {
			date = CommonDataProvider.mFormatterServer.parse(src);
		} catch (java.text.ParseException e) {
			Log.e(TAG,"e:"+e.getMessage());
			return null;
		} catch (java.lang.NullPointerException e) {
			Log.e(TAG,"e:"+e.getMessage());
			return null;
		}
		if (isApplyTimeZone) {
			timZone = mCommonDataProvider.getTimeZoneAdjust();
		}
		date.setTime(date.getTime() + timZone);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public String convertCalendarToServerTime(Calendar calendar, boolean isApplyTimeZone) {
		long timZone = 0;
		if (isApplyTimeZone) {
			timZone = mCommonDataProvider.getTimeZoneAdjust() * -1;
		}
		Date date = new Date(calendar.getTimeInMillis()+timZone);
		return CommonDataProvider.mFormatterServer.format(date);
	}

	public String convertCalendarToFormatter(Calendar cal,SimpleDateFormat formmatter) {
		if (cal == null || formmatter == null) {
			return null;
		}
		return formmatter.format(cal.getTime());
	}

	public String convertCalendarToFormatter(Calendar cal,DATE_TYPE type) {
		SimpleDateFormat formatter = getClientTimeFormat(type);
		return convertCalendarToFormatter(cal, formatter);
	}

	public Calendar convertFormatterToCalendar(String src,SimpleDateFormat formatter) {
		if (src == null || src.isEmpty() || formatter == null) {
			return null;
		}
		Date date = null;
		try {
			date = formatter.parse(src);
		} catch (java.text.ParseException e) {
			Log.e(TAG,"e:"+e.getMessage());
			return null;
		} catch (java.lang.NullPointerException e) {
			Log.e(TAG,"e:"+e.getMessage());
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public Calendar convertFormatterToCalendar(String src,DATE_TYPE type) {
		SimpleDateFormat formatter = getClientTimeFormat(type);
		return convertFormatterToCalendar(src, formatter);
	}

}
