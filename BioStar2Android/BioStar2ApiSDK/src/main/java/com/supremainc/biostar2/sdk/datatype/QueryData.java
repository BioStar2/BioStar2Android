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
package com.supremainc.biostar2.sdk.datatype;

import android.app.Activity;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.supremainc.biostar2.sdk.provider.CommonDataProvider;
import com.supremainc.biostar2.sdk.provider.CommonDataProvider.DATE_TYPE;
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class QueryData {
	public static class Query implements Cloneable, Serializable {
		private static final long serialVersionUID = -183415165995480102L;
		public static final String TAG = Query.class.getSimpleName();
		@SerializedName("offset")
		public int offset;
		@SerializedName("limit")
		public int limit;
		@SerializedName("datetime")
		public ArrayList<String> datetime;
		@SerializedName("device_id")
		public ArrayList<String> device_id;
		@SerializedName("event_type_code")
		public ArrayList<String> event_type_code;
		@SerializedName("user_id")
		public ArrayList<String> user_id;

		public Query() {

		}

		public Query(int offset, int limit, ArrayList<String> device_id, ArrayList<String> event_type_code, ArrayList<String> user_id) {
			this.offset = offset;
			this.limit = limit;
			if (device_id != null) {
				if (device_id.size() < 1) {
					device_id = null;
				}
			}
			if (event_type_code != null) {
				if (event_type_code.size() < 1) {
					event_type_code = null;
				}
			}
			if (user_id != null) {
				if (user_id.size() < 1) {
					user_id = null;
				}
			}
			this.device_id = device_id;
			this.event_type_code = event_type_code;
			this.user_id = user_id;
		}

		public boolean setDateValue(Activity context, DATE_TYPE clientTimetype, String StartDate, String endDate) {
			try {
				datetime = new ArrayList<String>();
				CommonDataProvider commonDataProvider = CommonDataProvider.getInstance(context);
				String startTime = commonDataProvider.convertClientTimeToServerTime(context, clientTimetype, StartDate, true);
				String endTime = commonDataProvider.convertClientTimeToServerTimeDateEnd(context, clientTimetype, endDate, true);
				datetime.add(startTime);
				datetime.add(endTime);

			} catch (Exception e) {
				Log.e(TAG, " " + e.getMessage());
				return false;
			}
			return true;
		}

		public boolean setDateTickValue(Activity context, long startTick,long endTick) {
			try {
				datetime = new ArrayList<String>();
				CommonDataProvider commonDataProvider = CommonDataProvider.getInstance(context);
				String startTime = ConfigDataProvider.mServerFormatter.format(new Date(startTick - commonDataProvider.getTimeZoneAdjust()));
				String endTime =  ConfigDataProvider.mServerFormatter.format(new Date(endTick - commonDataProvider.getTimeZoneAdjust()));
				datetime.add(startTime);
				datetime.add(endTime);

			} catch (Exception e) {
				Log.e(TAG, " " + e.getMessage());
				return false;
			}
			return true;
		}

		@SuppressWarnings("unchecked")
		public Query clone() throws CloneNotSupportedException {
			Query target = (Query) super.clone();
			if (datetime != null) {
				target.datetime = (ArrayList<String>) datetime.clone();
			}
			if (device_id != null) {
				target.device_id = (ArrayList<String>) device_id.clone();
			}
			if (event_type_code != null) {
				target.event_type_code = (ArrayList<String>) event_type_code.clone();
			}
			if (user_id != null) {
				target.user_id = (ArrayList<String>) user_id.clone();
			}
			return target;
		}
	}

	public static class Conditions implements Cloneable, Serializable {
		private static final long serialVersionUID = -4457837901052058482L;
		public static final String TAG = "data_conditions";
		@SerializedName("column")
		public String column;
		@SerializedName("operator")
		public int operator;
		@SerializedName("values")
		private ArrayList<String> values;
		@SerializedName("total")
		public boolean total;

		public Conditions(String column, int operator, ArrayList<String> values) {
			this.column = column;
			this.operator = operator;
			this.values = values;
		}
		public void setValues(ArrayList<String> values) {
			this.values = values;
		}

		@SuppressWarnings("unchecked")
		public Conditions clone() throws CloneNotSupportedException {
			Conditions target = (Conditions) super.clone();
			if (values != null) {
				target.values = (ArrayList<String>) values.clone();
			}
			return target;
		}
	}
}
