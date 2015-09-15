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

import com.google.gson.Gson;
import com.supremainc.biostar2.sdk.datatype.LoginData.Login;
import com.supremainc.biostar2.sdk.datatype.UserData.User;
import com.supremainc.biostar2.sdk.utils.FileUtil;
import com.supremainc.biostar2.sdk.utils.PreferenceUtil;
import com.supremainc.biostar2.sdk.volley.NetworkResponse;
import com.supremainc.biostar2.sdk.volley.Request.Method;
import com.supremainc.biostar2.sdk.volley.RequestQueue;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.ErrorListener;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.sdk.volley.toolbox.JsonObjectRequest;
import com.supremainc.biostar2.sdk.volley.toolbox.Volley;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NetWork {
	private static NetWork mSelf = null;
	private final String TAG = getClass().getSimpleName();
	private FileUtil mFileUtil;
	protected static RequestQueue mRequestQueue = null;
	private Context mContext;
//	protected static final String SERVER_ADDRESS = "https://api.biostar2.com/v1/";
	public static String SERVER_ADDRESS = "https://api.biostar2.com/v1/";
	protected static Gson mGson;

	protected static final String PARAM_TODO = "classes/Todo";

	protected static final String PARAM_USERS = "users";
	public static final String PARAM_USERS_DELETE = PARAM_USERS + "/"+"delete";
	protected static final String PARAM_PHOTO = "photo";
	protected static final String PARAM_MYPROFILE = "my_profile";
	protected static final String PARAM_USER_GROUPS = "user_groups";
	protected static final String PARAM_LOGIN = "login";

	protected static final String PARAM_ACCESS_GROUPS = "access_groups";
	protected static final String PARAM_ACCESS_LEVELS = "access_levels";

	protected static final String PARAM_CARDS = "cards";
	protected static final String PARAM_CARDS_UNASSIGNED = PARAM_CARDS + "/" + "unassigned";
	protected static final String PARAM_DEVICES = "devices";
	public static final String PARAM_DEVICE_TYPES = "device_types";
	/*
	 * PARAM_DEVICES + id + PARAM_SCAN_FINGERPRINT
	 */
	protected static final String PARAM_DEVICE_VERIFY_FINGERPRINT = "verify_fingerprint";
	protected static final String PARAM_DEVICE_SCAN_FINGERPRINT = "scan_fingerprint";
	protected static final String PARAM_DEVICE_WRITE_CARD = "write_card";
	protected static final String PARAM_DEVICE_SCAN_CARD = "scan_card";

	protected static final String PARAM_DOORS = "doors";
	protected static final String PARAM_OPEN = "open";
	protected static final String PARAM_UNLOCK = "unlock";
	protected static final String PARAM_LOCK = "lock";
	protected static final String PARAM_CLEAR_ALARM = "clear_alarm";
	protected static final String PARAM_CLEAR_APB = "clear_anti_pass_back";
	protected static final String PARAM_OPEN_REQUEST = "request_open";

	protected static final String PARAM_MONITORING = "monitoring";
	protected static final String PARAM_MONITORING_SEARCH = PARAM_MONITORING + "/event_log/search";

	protected static final String PARAM_SCHEDULES = "schedules";
	protected static final String PARAM_PERMISSIONS = "permissions";

	protected static final String PARAM_SETTING = "setting";
	protected static final String PARAM_SETTING_NOTIFICATIONTOKEN = PARAM_SETTING + "/updateNotificationToken";
	protected static final String PARAM_VERSION = "admin/app_versions";
	
	protected static final String PARAM_LOGOUT = "logout";
	
	protected static final String PARAM_REFERENCE = "references";
	protected static final String PARAM_REFERENCE_CODES = PARAM_REFERENCE+"/role_codes";
	protected static final String PARAM_REFERENCE_EVENT_TYPES = PARAM_REFERENCE+"/event_types";



	private NetWork(Context context) {
		if (null == this.mContext) {
			this.mContext = context;
		}
		if (null == mRequestQueue) {
			mRequestQueue = Volley.newRequestQueue(context);
		}
		if (null == mFileUtil) {
			mFileUtil = FileUtil.getInstance();
		}
		if (mGson == null) {
			mGson = new Gson();
		}
	}

	protected static NetWork getInstance(Context context) {
		if (mSelf == null) {
			mSelf = new NetWork(context);
		}
		return mSelf;
	}

	protected void cancelAll() {
		if (null != mRequestQueue) {
			mRequestQueue.cancelAll();
		}
	}

	protected void cancelAll(String tag) {
		if (null != mRequestQueue) {
			mRequestQueue.cancelAll(tag);
		}
	}

	protected boolean isValid() {
		if (mContext == null) {
			return false;
		}
		return Volley.isValid(mContext);
	}

	public void removeCookie() {
		Volley.removeCookie(mContext);
	}

	public void logOff() {
		// TODO LogoFF Listener
		Volley.removeCookie(mContext);
	}

	public RequestQueue getRequestQueue() {
		return mRequestQueue;
	}
	protected void simpleLogin(final Listener<User> listener, final ErrorListener errorListener, final Object deliverParam) {
		String url = PreferenceUtil.getSharedPreference(mContext, ConfigDataProvider.URL);
		if (url != null && !url.isEmpty()) {
			SERVER_ADDRESS = url;
		}
		if (SERVER_ADDRESS == null || SERVER_ADDRESS.isEmpty()) {
			SERVER_ADDRESS =  ConfigDataProvider.URL;
		}
		HashMap<String, String> headers = new HashMap<String, String>();
		Locale locale = mContext.getResources().getConfiguration().locale;	
		headers.put("Content-Language", locale.getISO3Language());

		Response.ErrorListener innerErrorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError, Object param) {
				NetworkResponse networkResponse = volleyError.networkResponse;
				if (null == networkResponse && volleyError.getMessage() != null && volleyError.getMessage().indexOf("authentication") != -1) {
					networkResponse = new NetworkResponse(HttpURLConnection.HTTP_UNAUTHORIZED, null, null, false);
				}
				if (networkResponse != null) {
					switch (networkResponse.statusCode) {
						case HttpURLConnection.HTTP_UNAUTHORIZED :
							// case HttpURLConnection.HTTP_FORBIDDEN):
							Log.e(TAG, "sessionid expire relogin");
							removeCookie();
							volleyError.setSessionExpire();
							break;
						default :
							break;
					}
				}
				if (errorListener != null) {
					errorListener.onErrorResponse(volleyError, param);
				}
			}
		};
		sendRequest(null, User.class, Method.GET, NetWork.PARAM_USERS + "/" + NetWork.PARAM_MYPROFILE, headers, null, null, listener, errorListener, deliverParam);
	}

	protected void login(String domain,String url, String id, String password, String token, final Listener<User> listener, final ErrorListener errorListener, final Object deliverParam) {
		Login login = new Login(mContext,token);
		login.user_id = id;
		login.password = password;
		login.name = domain;
		if (token != null && !token.isEmpty()) {
			login.notification_token = token;
		}
		password = "";

		url = url + PARAM_LOGIN;
		String body = null;
		try {
			body = mGson.toJson(login);
		} catch (Exception e) {
			errorListener.onErrorResponse(new VolleyError("LoginData Invalid"), deliverParam);
			return;
		}

		Response.ErrorListener innerErrorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError, Object param) {
				NetworkResponse networkResponse = volleyError.networkResponse;
				if (null == networkResponse && volleyError.getMessage() != null && volleyError.getMessage().indexOf("authentication") != -1) {
					networkResponse = new NetworkResponse(HttpURLConnection.HTTP_UNAUTHORIZED, null, null, false);
				}
				if (networkResponse != null) {
					switch (networkResponse.statusCode) {
						case HttpURLConnection.HTTP_UNAUTHORIZED :
							// case HttpURLConnection.HTTP_FORBIDDEN):
							Log.e(TAG, "sessionid expire relogin");
							removeCookie();
							volleyError.setSessionExpire();
							break;
						default :
							break;
					}
				}
				if (errorListener != null) {
					errorListener.onErrorResponse(volleyError, param);
				}
			}
		};

		HashMap<String, String> headers = new HashMap<String, String>();
		Locale locale = mContext.getResources().getConfiguration().locale;
		headers.put("Content-Language", locale.getISO3Language());
		JsonObjectRequest<User> req = new JsonObjectRequest<User>(User.class, Method.POST, url, headers, null, body, listener, innerErrorListener, deliverParam);
		mRequestQueue.add(req);
		login.password = "";
	}

	protected <T> void sendOuterRequest(String url, String tag, final Class<T> clazz, final int method, final String path, final Map<String, String> headers, final Map<String, String> params,
			final String body, final Listener<T> listener, final ErrorListener errorListener, Object deliverParam) {
		if (path != null) {
			url = url + path;
		}

		JsonObjectRequest<T> req = new JsonObjectRequest<T>(clazz, method, url, headers, params, body, listener, errorListener, deliverParam);
		req.setTag(tag);
		mRequestQueue.add(req);
	}

	protected <T> void sendRequest(String tag, Class<T> clazz, int method, String path, Map<String, String> headers, Map<String, String> params, String body, Listener<T> listener,
			final ErrorListener errorListener, Object deliverParam) {
		if (!isValid()) {
			if (errorListener != null) {
				VolleyError ve = new VolleyError();
				ve.setSessionExpire();
				errorListener.onErrorResponse(ve, deliverParam);
			}
			return;
		}
		if (SERVER_ADDRESS == null || SERVER_ADDRESS.isEmpty()) {
			SERVER_ADDRESS =  PreferenceUtil.getSharedPreference(mContext, ConfigDataProvider.URL);
		}
		if (SERVER_ADDRESS == null || SERVER_ADDRESS.isEmpty()) {
			SERVER_ADDRESS =  ConfigDataProvider.URL;
		}
		String url = null;
		if (path != null) {
			url = SERVER_ADDRESS + path;
		} else {
			url = SERVER_ADDRESS;
		}

		Response.ErrorListener innerErrorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError, Object param) {
				NetworkResponse networkResponse = volleyError.networkResponse;
				if (null == networkResponse && volleyError.getMessage() != null && volleyError.getMessage().indexOf("authentication") != -1) {
					networkResponse = new NetworkResponse(HttpURLConnection.HTTP_UNAUTHORIZED, null, null, false);
				}
				if (networkResponse != null) {
					switch (networkResponse.statusCode) {
						case HttpURLConnection.HTTP_UNAUTHORIZED :
							// case HttpURLConnection.HTTP_FORBIDDEN):
							Log.e(TAG, "sessionid expire relogin");
							removeCookie();
							volleyError.setSessionExpire();
							break;
						default :
							break;
					}
				}
				if (errorListener != null) {
					errorListener.onErrorResponse(volleyError, param);
				}
			}
		};

		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		Locale locale = mContext.getResources().getConfiguration().locale;
		headers.put("Content-Language", locale.getISO3Language());
		JsonObjectRequest<T> req = new JsonObjectRequest<T>(clazz, method, url, headers, params, body, listener, innerErrorListener, deliverParam);
		req.setTag(tag);
		mRequestQueue.add(req);
	}

	private String parsingNetworkResponseMessage(NetworkResponse networkResponse) {
		if (null == networkResponse) {
			Log.e(TAG, "networkResponse null");
			return null;
		}
		switch (networkResponse.statusCode) {
			case HttpURLConnection.HTTP_NOT_FOUND :
				Log.e(TAG, "errormsg:" + "SC_NOT_FOUND");
				return null;

			case HttpURLConnection.HTTP_INTERNAL_ERROR :
				Log.e(TAG, "errormsg:" + "SC_INTERNAL_SERVER_ERROR");
				return null;

			case HttpURLConnection.HTTP_UNAUTHORIZED :
				Log.e(TAG, "errormsg:" + "SC_UNAUTHORIZED");
				return null;
			case HttpURLConnection.HTTP_BAD_REQUEST :
				Log.e(TAG, "errormsg:" + "SC_BAD_REQUEST");
				return null;
			case HttpURLConnection.HTTP_FORBIDDEN :
				Log.e(TAG, "errormsg:" + "SC_FORBIDDEN");
				return null;
			default :
				if (networkResponse.data != null) {
					String errormsg = new String(networkResponse.data);
					errormsg = "errorCode:" + networkResponse.statusCode + " msg:" + errormsg;
					Log.e(TAG, "errormsg:" + errormsg);
				}
				return null;
		}
	}

	public void testRequest(String url) {
		Listener<String> listener = new Listener<String>() {

			@Override
			public void onResponse(String response, Object param) {
				Log.e("receive:", "receive:" + response);
			}

		};
		Response.ErrorListener innerErrorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError, Object param) {
				Log.e("receive error:", volleyError.getMessage());
			}
		};
		JsonObjectRequest<String> req = new JsonObjectRequest<String>(String.class, Method.GET, url, null, null, null, listener, innerErrorListener, null);
		req.setTag(null);
		mRequestQueue.add(req);

	}

}
