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

import com.supremainc.biostar2.sdk.datatype.LoginData.NotificationToken;
import com.supremainc.biostar2.sdk.datatype.ResponseStatus;
import com.supremainc.biostar2.sdk.utils.PreferenceUtil;
import com.supremainc.biostar2.sdk.volley.Request.Method;
import com.supremainc.biostar2.sdk.volley.Response;

public class PushDataProvider extends BaseDataProvider {
	private final String TAG = getClass().getSimpleName();
	private static PushDataProvider mSelf = null;
	private static boolean mIsRunning = false;
	private static String mToken;

	private static final String NOTIFICATION_TOKEN = "notifytoken";

	private PushDataProvider(Context context) {
		super(context);
	}

	public static PushDataProvider getInstance(Context context) {
		if (mSelf == null) {
			mSelf = new PushDataProvider(context);
		}
		return mSelf;
	}

	public void setNeedUpdateNotificationToken(final String token) {
		NotificationToken notificationToken = new NotificationToken(mContext, token);
		String json = null;
		try {
			json = mGson.toJson(notificationToken);
		} catch (Exception e) {
			return;
		}
		final String body = json;
		mIsRunning = true;
		final Response.Listener<ResponseStatus> listener = new Response.Listener<ResponseStatus>() {
			@Override
			public void onResponse(ResponseStatus response, Object param) {
				mIsRunning = false;
				String tempToken = mToken;
				mToken = null;

				if (response != null) {
					PreferenceUtil.putSharedPreference(mContext, NOTIFICATION_TOKEN, token);
				} else {
					if (ConfigDataProvider.DEBUG) {
						Log.e(TAG, "setNeedUpdateNotificationToken null");
					}
				}

				if (tempToken != null && !tempToken.equals(token)) {
					sendRequest(null, ResponseStatus.class, Method.PUT, NetWork.PARAM_SETTING_NOTIFICATIONTOKEN, null, null, body, this, null, null);
				}
			}
		};
		sendRequest(null, ResponseStatus.class, Method.PUT, NetWork.PARAM_SETTING_NOTIFICATIONTOKEN, null, null, body, listener, null, null);
	}

	public void checkUpdateNotificationToken(String token) {
		if (token == null || token.length() < 1) {
			return;
		}
		String savedToken = PreferenceUtil.getSharedPreference(mContext, NOTIFICATION_TOKEN);
		if (savedToken != null && savedToken.equals(token)) {
			return;
		}
		if (mIsRunning) {
			mToken = token;
			return;
		}
		setNeedUpdateNotificationToken(token);
	}
}
