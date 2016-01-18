/*
 * Copyright (C) 2012 The Android Open Source Project
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
package com.supremainc.biostar2.sdk.volley.toolbox;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;

import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;
import com.supremainc.biostar2.sdk.provider.PersistentCookieStore;
import com.supremainc.biostar2.sdk.volley.Network;
import com.supremainc.biostar2.sdk.volley.RequestQueue;

import java.io.File;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class Volley {
	/** Default on-disk cache directory. */
	private static final String DEFAULT_CACHE_DIR = "volley";
	private static CookieManager mCookieManager;
	/**
	 * Creates a default instance of the worker pool and calls
	 * {@link RequestQueue#start()} on it.
	 *
	 * @param context
	 *            A {@link Context} to use for creating the cache dir.
	 * @param stack
	 *            An {@link HttpStack} to use for the network, or null for
	 *            default.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
		File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

		String userAgent = "volley/0";
		try {
			String packageName = context.getPackageName();
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
			userAgent = packageName + "/" + info.versionCode;
		} catch (NameNotFoundException e) {
		}
		OkHttpStack okHttpStack = null;
		if (stack == null) {
			switch (ConfigDataProvider.mNetworkType) {
				case HURL :
					stack = new HurlStack();
					break;
				case HTTP_CLIENT :
					stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
					// stack = new HttpClientStack();
					break;
				case OK_HTTP :
					if (mCookieManager == null) {
						mCookieManager = new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);
					}

					okHttpStack = new OkHttpStack(mCookieManager);
					stack = okHttpStack;
					break;
			}
			/*
			 * if (Build.VERSION.SDK_INT >= 9) { stack = new HurlStack(); } elseqordls
			 * { // Prior to Gingerbread, HttpUrlConnection was unreliable. //
			 * See:
			 * http://android-developers.blogspot.com/2011/09/androids-http-
			 * clients.html stack = new
			 * HttpClientStack(AndroidHttpClient.newInstance(userAgent)); }
			 */

			//
		}

		Network network = new BasicNetwork(stack);

		RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
		if (ConfigDataProvider.mNetworkType == ConfigDataProvider.NetworkType.OK_HTTP) {
			queue.setOkHttpClient(okHttpStack.getClient());
		}
		queue.start();

		return queue;
	}

	/**
	 * Creates a default instance of the worker pool and calls
	 * {@link RequestQueue#start()} on it.
	 *
	 * @param context
	 *            A {@link Context} to use for creating the cache dir.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue(Context context) {
		return newRequestQueue(context, null);
	}

	private static PersistentCookieStore getCookieStore(Context context) {
		PersistentCookieStore cookiestore = null;
		if (mCookieManager == null) {
			cookiestore = new PersistentCookieStore(context);
			mCookieManager = new CookieManager(cookiestore, CookiePolicy.ACCEPT_ALL);
		} else {
			cookiestore = (PersistentCookieStore) mCookieManager.getCookieStore();
			if (cookiestore == null) {
				cookiestore = new PersistentCookieStore(context);
				mCookieManager = new CookieManager(cookiestore, CookiePolicy.ACCEPT_ALL);
			}
		}
		return cookiestore;
	}

	public static boolean isValid(Context context) {
		PersistentCookieStore cookiestore = getCookieStore(context);
		return cookiestore.isValid();
	}

	public static void removeCookie(Context context) {
		PersistentCookieStore cookiestore = getCookieStore(context);
		cookiestore.removeAll();
	}
}
