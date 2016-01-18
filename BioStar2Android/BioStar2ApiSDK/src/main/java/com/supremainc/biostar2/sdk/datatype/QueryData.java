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

import com.google.gson.annotations.SerializedName;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class QueryData {
	public static class Query implements Cloneable, Serializable {
		private static final long serialVersionUID = -183415165995480102L;
		public static final String TAG = Query.class.getSimpleName();
		public enum QueryTimeType {start_datetime,end_datetime};
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

		public String getTimeType(QueryTimeType type) {
			String src= null;
			switch (type) {
				case start_datetime:
					if (datetime == null || datetime.size() < 1) {
						return null;
					}
					src = datetime.get(0);
					break;
				case end_datetime:
					if (datetime == null || datetime.size() < 2) {
						return null;
					}
					src = datetime.get(1);
					break;
				default:
					break;
			}
			return src;
		}

		public boolean setTimeType(QueryTimeType type,String src) {
			if (src == null || src.isEmpty()) {
				return false;
			}
			switch (type) {
				case start_datetime:
					if (datetime == null || datetime.size() < 1) {
						datetime = new ArrayList<String>();
						datetime.add(src);
					} else {
						datetime.set(0,src);
					}
					break;
				case end_datetime:
					if (datetime == null || datetime.size() < 1) {
						datetime = new ArrayList<String>();
						datetime.add(src);
						datetime.add(src);
					} else if (datetime.size() < 2){
						datetime.add(src);;
					} else {
						datetime.set(1,src);
					}
					break;
				default:
					return false;
			}
			return true;
		}

		public Calendar getTimeCalendar(TimeConvertProvider convert,QueryTimeType timeType) {
			return convert.convertServerTimeToCalendar(getTimeType(timeType), true);
		}

		public boolean setTimeCalendar(TimeConvertProvider convert,QueryTimeType timeType,Calendar cal) {
			switch (timeType) {
				case end_datetime:
					cal.set(Calendar.HOUR_OF_DAY,23);
					cal.set(Calendar.MINUTE,59);
					break;
				default:
					break;
			}
			return setTimeType(timeType, convert.convertCalendarToServerTime(cal, true));
		}

		public String getTimeFormmat(TimeConvertProvider convert,QueryTimeType timeType,TimeConvertProvider.DATE_TYPE type) {
			Calendar cal = getTimeCalendar(convert, timeType);
			return convert.convertCalendarToFormatter(cal, type);
		}

		public boolean setTimeFormmat(TimeConvertProvider convert,QueryTimeType timeType,TimeConvertProvider.DATE_TYPE type,String src) {
			Calendar cal = convert.convertFormatterToCalendar(src,type);
			return setTimeCalendar(convert,timeType,cal);
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
