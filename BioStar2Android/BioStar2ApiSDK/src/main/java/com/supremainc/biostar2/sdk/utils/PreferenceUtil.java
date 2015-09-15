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
package com.supremainc.biostar2.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {
	private static final String SETTING_PREF = "prefStore";
	public static void putSharedPreference(Context context, String key, String value) {
		SharedPreferences preferences = context.getSharedPreferences(SETTING_PREF, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void putSharedPreference(Context context, String key, boolean value) {
		SharedPreferences preferences = context.getSharedPreferences(SETTING_PREF, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static void putSharedPreference(Context context, String key, int value) {
		SharedPreferences preferences = context.getSharedPreferences(SETTING_PREF, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static String getSharedPreference(Context context, String key) {
		SharedPreferences preferences = context.getSharedPreferences(SETTING_PREF, Context.MODE_PRIVATE);
		try {
			return preferences.getString(key, null);
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean getBooleanSharedPreference(Context context, String key) {
		return getBooleanSharedPreference(context,key,false);
	}

	public static int getIntSharedPreference(Context context, String key) {
		return getIntSharedPreference(context,key,-1);
	}

	public static boolean getBooleanSharedPreference(Context context, String key,boolean defaultValue) {
		SharedPreferences preferences = context.getSharedPreferences(SETTING_PREF, Context.MODE_PRIVATE);
		try {
			return preferences.getBoolean(key, defaultValue);
		} catch (Exception e) {
			return false;
		}
	}

	public static int getIntSharedPreference(Context context, String key,int defaultValue) {
		SharedPreferences preferences = context.getSharedPreferences(SETTING_PREF, Context.MODE_PRIVATE);
		try {
			return preferences.getInt(key,defaultValue);
		} catch (Exception e) {
			return -1;
		}
	}
}