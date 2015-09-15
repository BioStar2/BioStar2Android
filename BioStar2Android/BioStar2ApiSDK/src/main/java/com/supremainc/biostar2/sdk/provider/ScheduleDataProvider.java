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

import com.supremainc.biostar2.sdk.datatype.ScheduleData.Schedule;
import com.supremainc.biostar2.sdk.datatype.ScheduleData.Schedules;
import com.supremainc.biostar2.sdk.volley.Request.Method;
import com.supremainc.biostar2.sdk.volley.Response.ErrorListener;
import com.supremainc.biostar2.sdk.volley.Response.Listener;

public class ScheduleDataProvider extends BaseDataProvider {
	@SuppressWarnings("unused")
	private final String TAG = getClass().getSimpleName();
	private static ScheduleDataProvider mSelf = null;

	private ScheduleDataProvider(Context context) {
		super(context);
	}

	public static ScheduleDataProvider getInstance(Context context) {
		if (mSelf == null) {
			mSelf = new ScheduleDataProvider(context);
		}
		return mSelf;
	}

	public static ScheduleDataProvider getInstance() {
		if (mSelf != null) {
			return mSelf;
		}
		if (mContext != null) {
			mSelf = new ScheduleDataProvider(mContext);
			return mSelf;
		}
		return null;
	}

	public void getSchedules(String tag, final Listener<Schedules> listener, ErrorListener errorListener, Object deliverParam) {
		sendRequest(tag, Schedules.class, Method.GET, NetWork.PARAM_SCHEDULES, null, null, null, listener, errorListener, deliverParam);
	}

	public void getSchedule(String tag, String id, Listener<Schedule> listener, ErrorListener errorListener, Object deliverParam) {
		String url = NetWork.PARAM_SCHEDULES + "/" + String.valueOf(id);

		sendRequest(tag, Schedule.class, Method.GET, url, null, null, null, listener, errorListener, deliverParam);
	}
}
