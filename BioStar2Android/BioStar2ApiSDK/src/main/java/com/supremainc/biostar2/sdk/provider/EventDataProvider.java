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

import com.supremainc.biostar2.sdk.datatype.EventLogData.EventLogs;
import com.supremainc.biostar2.sdk.datatype.EventLogData.EventType;
import com.supremainc.biostar2.sdk.datatype.EventLogData.EventTypes;
import com.supremainc.biostar2.sdk.datatype.QueryData.Query;
import com.supremainc.biostar2.sdk.volley.Request.Method;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.ErrorListener;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EventDataProvider extends BaseDataProvider {
	private final String TAG = getClass().getSimpleName();
	private static EventDataProvider mSelf = null;
	Map<Integer, EventType> mMssageTable = new HashMap<Integer, EventType>();

	
	private EventDataProvider(Context context) {
		super(context);
	}

	public static EventDataProvider getInstance(Context context) {
		if (mSelf == null) {
			mSelf = new EventDataProvider(context);
		}
		return mSelf;
	}

	public static EventDataProvider getInstance() {
		if (mSelf != null) {
			return mSelf;
		}
		if (mContext != null) {
			mSelf = new EventDataProvider(mContext);
			return mSelf;
		}
		return null;
	}

	public void searchEventLog(String tag, Query query, final Listener<EventLogs> listener, final ErrorListener errorListener, Object deliverParam) {
		String json = mGson.toJson(query);
		sendRequest(tag, EventLogs.class, Method.POST, NetWork.PARAM_MONITORING_SEARCH, null, null, json, listener, errorListener, deliverParam);
	}
	
	public String getEventMessage(int code) {
		String result = "";
		if (!haveMessage()) {
			return "";
		}
		try {
			EventType item = mMssageTable.get(code);
			if (item != null) {
				return item.description;
			}
		} catch (Exception e) {
			if (ConfigDataProvider.DEBUG) {
				Log.e(TAG, "getEventmessage error:" + e.getMessage());
			}
		}
		return result;
	}
	
	public ArrayList<EventType> getEventTypeList() {
		if (!haveMessage()) {
			return null;
		}
		ArrayList<EventType> valueList = Collections.list(Collections.enumeration(mMssageTable.values()));
		return  valueList;
	}

	public boolean haveMessage() {
		if (mMssageTable.size() < 1) {
			try {
				@SuppressWarnings("unchecked")
				ArrayList<EventType> rows = (ArrayList<EventType>) mFileUtil.loadFileObj(mContext, "message");
				if (rows == null) {
					return false;
				}
				setEventMessage(rows, false);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	public void setEventMessage(ArrayList<EventType> message, boolean save) {
		for (EventType etype : message) {
			mMssageTable.put(etype.code, etype);
		}
		if (save) {
			mFileUtil.saveFileObj(mContext, "message", message);
		}
	}

	public void getEventMessage(final Listener<EventTypes> listener, final ErrorListener errorListener, final Object deliverParam) {
		Listener<EventTypes> innerListener = new Response.Listener<EventTypes>() {
			@Override
			public void onResponse(EventTypes response, Object param) {
				if (response == null || response.records == null || response.records.size() < 1) {
					Log.e(TAG, "getEventMessage error");
					errorListener.onErrorResponse(new VolleyError("event message reponse is null"), deliverParam);
					return;
				}
				setEventMessage(response.records, true);
				listener.onResponse(response, deliverParam);
			}
		};

		sendRequest(null, EventTypes.class, Method.GET, NetWork.PARAM_REFERENCE_EVENT_TYPES, null, null, null, innerListener, errorListener, null);
	}
}
