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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.supremainc.biostar2.sdk.datatype.PermissionData;
import com.supremainc.biostar2.sdk.datatype.PermissionData.CloudPermission;
import com.supremainc.biostar2.sdk.datatype.ResponseStatus;
import com.supremainc.biostar2.sdk.datatype.UserData.User;
import com.supremainc.biostar2.sdk.utils.FileUtil;
import com.supremainc.biostar2.sdk.utils.PreferenceUtil;
import com.supremainc.biostar2.sdk.volley.Request.Method;
import com.supremainc.biostar2.sdk.volley.RequestQueue;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.ErrorListener;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

public class BaseDataProvider {
	protected final String TAG = getClass().getSimpleName();
	protected static Handler mHandler;
	protected static Context mContext;
	protected static NetWork mNetwork;
	protected static Gson mGson;
	protected static FileUtil mFileUtil;
	protected static Map<String, Boolean> mPermissionMap;
	protected static User mUserInfo;
	private static String mPasswordLevel;
	protected static String mSubDomain;
	protected BaseDataProvider(Context context) {
		mHandler = new Handler(Looper.getMainLooper());
		if (mGson == null) {
			mGson = new Gson();
		}
		if (mPermissionMap == null) {
			mPermissionMap = new HashMap<String, Boolean>();
		}
		mNetwork = NetWork.getInstance(context);
		mContext = context;
		mFileUtil = FileUtil.getInstance();
	}

	public void init(Context context) {
		mContext = context;
	}

	private void initPermission() {
		if (mPermissionMap != null) {
			mPermissionMap.clear();
		}
		if (mUserInfo == null || mUserInfo.permissions == null || mUserInfo.permissions.size() < 1) {
			return;
		}

		for (CloudPermission permission : mUserInfo.permissions) {
			if (permission.module != null) {
				String key = permission.module + PermissionData.ROLE_READ;
				if (permission.read) {
					if (ConfigDataProvider.DEBUG) {
						Log.i(TAG,key);
					}
					mPermissionMap.put(key, true);
				}
				key = permission.module + PermissionData.ROLE_WRITE;
				if (permission.write) {
					if (ConfigDataProvider.DEBUG) {
						Log.i(TAG,key);
					}
					mPermissionMap.put(key, true);
				}
			}
		}
	}

	public void simpleLogin(final Listener<User> listener, final ErrorListener errorListener, final Object deliverParam) {
		final Response.Listener<User> loginListener = new Response.Listener<User>() {
			@Override
			public void onResponse(User response, Object param) {
				if (response == null) {
					removeCookie();
					if (errorListener != null) {
						VolleyError ve = new VolleyError();
						ve.setSessionExpire();
						errorListener.onErrorResponse(ve, deliverParam);
					}
					return;
				}
				mUserInfo = response;
				mPasswordLevel = mUserInfo.password_strength_level;
				initPermission();
				if (listener != null) {
					listener.onResponse(response, deliverParam);
				}
			}
		};
		mNetwork.simpleLogin(loginListener, errorListener, deliverParam);
	}

	public boolean isStrongPassword() {
		if (mPasswordLevel == null) {
			return true;
		}
		if (mPasswordLevel.equals("STRONG")) {
			return true;
		}
		return false;
	}
	
	public boolean isLogined() {
		if (mNetwork == null) {
			return false;
		}
		return mNetwork.isValid();
	}

	public String getLatestUrl() {
		String url =  PreferenceUtil.getSharedPreference(mContext, ConfigDataProvider.URL);
		if (url == null || url.isEmpty()) {
			return ConfigDataProvider.URL;
		}
		return url;
	}

	public void login(String domain,final String url, String id, String password, String token,final Response.Listener<User> listener, final Response.ErrorListener errorListener, final Object deliverParam) {
		removeCookie();
		final Response.Listener<User> loginListener = new Response.Listener<User>() {
			@Override
			public void onResponse(User response, Object param) {
				if (response == null) {
					removeCookie();
					if (errorListener != null) {
						VolleyError ve = new VolleyError();
						ve.setSessionExpire();
						errorListener.onErrorResponse(ve, deliverParam);
					}
					return;
				}

				PreferenceUtil.putSharedPreference(ConfigDataProvider.getContext(), ConfigDataProvider.URL,url);
				NetWork.SERVER_ADDRESS = url;

				mUserInfo = response;
				mPasswordLevel = mUserInfo.password_strength_level;
				initPermission();
				if (listener != null) {
					listener.onResponse(response, deliverParam);
				}
			}
		};
		mNetwork.login(domain,url, id, password, token,loginListener, errorListener, deliverParam);
		password = "";
	}

	public void logout(final Response.Listener<ResponseStatus> listener, Response.ErrorListener errorListener) {		
		final Response.Listener<ResponseStatus> logoutListener = new Response.Listener<ResponseStatus>() {
			@Override
			public void onResponse(ResponseStatus response, Object param) {
				if (mPermissionMap != null) {
					mPermissionMap.clear();
				}
				removeCookie();
				if (listener != null) {
					listener.onResponse(response, null);
				}
			}
		};
		mNetwork.sendRequest(null, ResponseStatus.class, Method.GET, NetWork.PARAM_LOGOUT, null, null, null, logoutListener, errorListener, null);
	}

	public void removeCookie() {
		mNetwork.removeCookie();
	}

	public void logOff() {
		mNetwork.logOff();
	}

	public static Context getContext() {
		return mContext;
	}

	public void cancelAll(String tag) {
		if (ConfigDataProvider.DEBUG) {
			Log.i(TAG, "cancel request:" + tag);
		}
		mNetwork.cancelAll(tag);
	}

	public RequestQueue getRequestQueue() {
		return mNetwork.mRequestQueue;
	}

	public boolean isValidLogin() {
		if (mNetwork == null || getLoginUserInfo() == null) {
			if (ConfigDataProvider.DEBUG) {
				Log.e(TAG, "mNetwork:" + mNetwork + " getLoginUserInfo:" + getLoginUserInfo());
			}
			return false;
		}
		return true;
	}

	protected <T> void sendRequest(String tag, Class<T> clazz, int method, String path, Map<String, String> headers, Map<String, String> params, String body, Listener<T> listener,
			ErrorListener errorListener, Object deliverParam) {
		try {
			mNetwork.sendRequest(tag, clazz, method, path, headers, params, body, listener, errorListener, deliverParam);
		} catch (Exception e) {
			onError(e, errorListener, deliverParam);
		}
	}

	public <T> void sendOuterRequest(String url, String tag, Class<T> clazz, int method, String path, Map<String, String> headers, Map<String, String> params, String body, Listener<T> listener,
			ErrorListener errorListener, Object deliverParam) {
		try {
			mNetwork.sendOuterRequest(url, tag, clazz, method, path, headers, params, body, listener, errorListener, deliverParam);
		} catch (Exception e) {
			onError(e, errorListener, deliverParam);
		}
	}

	protected void onError(Exception e, Response.ErrorListener errorListener, Object deliverParam) {
		String message = null;
		if (e != null) {
			if (ConfigDataProvider.DEBUG) {
				e.printStackTrace();
				message = e.getMessage();
				Log.e(TAG, "onError:" + message);
			}
		}
		if (errorListener != null) {
			errorListener.onErrorResponse(new VolleyError(message), deliverParam);
		}
	}

	public User getLoginUserInfo() {
		try {
			if (mUserInfo != null) {
				return (User) mUserInfo.clone();
			}
		} catch (Exception e) {

		}
		return null;
	}

	public void setLoginUserInfo(User user) {
		mUserInfo = user;
	}

	protected Map<String, String> createParams(int offset, int limit, String groupId, String query) {
		Map<String, String> params = new HashMap<String, String>();
		if (limit == -1) {
			limit = 100000;
		}
		if (groupId != null) {
			params.put("group_id", String.valueOf(groupId));
		}
		params.put("limit", String.valueOf(limit));
		params.put("offset", String.valueOf(offset));
		if (query != null && !query.equals("")) {
			params.put("text", query);
		}
		return params;
	}

	protected String createUrl(String baseParam, String id, String... addParams) {
		String url = baseParam;
		if (id != null) {
			url = url + "/" + id;
		}
		if (addParams.length < 1) {
			return url;
		}
		for (String param : addParams) {
			if (param != null && !param.isEmpty()) {
				url = url + "/" + param;
			}
		}
		return url;
	}

	public void test(String url) {
		mNetwork.testRequest(url);
	}

	public String getServerAddress() {
		return NetWork.SERVER_ADDRESS;
	}

	public void setSubDomain(String subdomain) {
		mSubDomain = subdomain;
	}
}
