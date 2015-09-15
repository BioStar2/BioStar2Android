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

import com.supremainc.biostar2.sdk.BuildConfig;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ConfigDataProvider extends BaseDataProvider {
	// private final String TAG = getClass().getSimpleName();
	public static final boolean TEST_RELEASE_DELETE = true;
	public static final boolean TEST_DELETE = false;
	public static final boolean DEBUG = false;
	public static final boolean DEBUG_SDCARD = false;
	public static final boolean SSL_ALL_PASS = false;
	public static final NetworkType mNetworkType = NetworkType.OK_HTTP;
	public static final String URL = "https://api.biostar2.com/v1/";
	public static SimpleDateFormat mServerFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'", Locale.ENGLISH);

	public enum NetworkType {
		HURL, HTTP_CLIENT, OK_HTTP
	};
	protected ConfigDataProvider(Context context) {
		super(context);
	}

	public static String getDebugFlag() {
		String result = "";
		if (TEST_DELETE) {
			result = result + "TEST CODE\n";
		}
		if (DEBUG) {
			result = result + "DEBUG\n";
		}
		if (DEBUG_SDCARD) {
			result = result + "SDCARD\n";
		}
		if (SSL_ALL_PASS) {
			result = result + "SSL_ALL_PASS\n";
		}
		if (BuildConfig.DEBUG) {
			result = result + "DEBUG BUILD\n";
		}
		return result;
	}
}
