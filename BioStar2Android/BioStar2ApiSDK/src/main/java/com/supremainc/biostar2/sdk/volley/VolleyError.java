/*
 * Copyright (C) 2011 The Android Open Source Project
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
// modified by Suprema 2015-09
package com.supremainc.biostar2.sdk.volley;

import com.google.gson.Gson;
import com.supremainc.biostar2.sdk.datatype.ResponseStatus;
import com.supremainc.biostar2.sdk.volley.toolbox.HttpHeaderParser;

/**
 * Exception style class encapsulating Volley errors
 */
@SuppressWarnings("serial")
public class VolleyError extends Exception {
	public final NetworkResponse networkResponse;
	public String responseString;
	public ResponseStatus responseClass;
	private static Gson mGson = new Gson();
	private boolean isSessionExpire = false;

	public VolleyError() {
		networkResponse = null;
	}

	public VolleyError(NetworkResponse response) {
		networkResponse = response;
		try {
			responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			responseClass = (ResponseStatus) mGson.fromJson(responseString, ResponseStatus.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSessionExpire() {
		isSessionExpire = true;
	}

	public boolean getSessionExpire() {
		return isSessionExpire;
	}

	@Override
	public String getMessage() {
		String message;
		message = super.getMessage();
		if (responseClass != null && responseClass.message != null) {
			message = responseClass.message;
			message = message.replace("<br/>","\n");
		}

		if (message == null) {
			return "";
		}
		return message;
	}

	public String getCode() {
		if (responseClass != null) {
			if (responseClass.statusCode == null) {
				return "";
			}
			return responseClass.statusCode;
		}
		return "";
	}

	public VolleyError(String exceptionMessage) {
		super(exceptionMessage);
		networkResponse = null;
	}

	public VolleyError(String exceptionMessage, Throwable reason) {
		super(exceptionMessage, reason);
		networkResponse = null;
	}

	public VolleyError(Throwable cause) {
		super(cause);
		networkResponse = null;
	}
}
