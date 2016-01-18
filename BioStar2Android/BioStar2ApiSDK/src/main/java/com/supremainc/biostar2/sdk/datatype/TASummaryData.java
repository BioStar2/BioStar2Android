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

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.supremainc.biostar2.sdk.datatype.UserData.BaseUser;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class TASummaryData {

	public static class RequestSummaryItems implements Cloneable, Serializable {
		public static final String TAG = RequestSummaryItems.class.getSimpleName();
		private static final long serialVersionUID = 2241398605963836892L;
		public enum RequestSummaryItemsTimeType {start_datetime,end_datetime};
		public enum RequestSummaryItemsType {DAILY,WEEKLY,MONTHLY,CUSTOM};
		public static final String DAILY = "DAILY";
		public static final String WEEKLY = "WEEKLY";
		public static final String MONTHLY = "MONTHLY";
		public static final String CUSTOM = "CUSTOM";

		/**
		 * 2015-10-21T07:35:57.551Z
		 */
		@SerializedName("end_datetime")
		public String end_datetime;
		/**
		 * 2015-10-21T07:35:57.551Z
		 */
		@SerializedName("start_datetime")
		public String start_datetime;
		@SerializedName("users")
		public ArrayList<String> users;
		/**
		 * DAILY
		 WEEKLY
		 MONTHLY
		 CUSTOM
		 */
		@SerializedName("type")
		public String type="DAILY";
		public RequestSummaryItems(RequestSummaryItemsType requestType) {
			if (requestType == null) {
				type="DAILY";
				return;
			}
			switch (requestType) {
				case DAILY:
					type=DAILY;
					break;
				case WEEKLY:
					type=WEEKLY;
					break;
				case MONTHLY:
					type=MONTHLY;
					break;
				case CUSTOM:
					type=CUSTOM;
					break;
				default:
					type=DAILY;
					break;
			}
		}

		public String getTimeType(RequestSummaryItemsTimeType type) {
			String src= null;
			switch (type) {
				case start_datetime:
					src = start_datetime;
					break;
				case end_datetime:
					src = end_datetime;
					break;
				default:
					break;
			}
			return src;
		}

		public boolean setTimeType(RequestSummaryItemsTimeType type,String src) {
			if (src == null || src.isEmpty()) {
				return false;
			}
			switch (type) {
				case start_datetime:
					start_datetime = src;
					break;
				case end_datetime:
					end_datetime = src;
					break;
				default:
					return false;
			}
			return true;
		}

		public Calendar getTimeCalendar(TimeConvertProvider convert,RequestSummaryItemsTimeType timeType) {
			return convert.convertServerTimeToCalendar(getTimeType(timeType),true);
		}

		public boolean setTimeCalendar(TimeConvertProvider convert,RequestSummaryItemsTimeType timeType, Calendar cal) {
			return setTimeType(timeType, convert.convertCalendarToServerTime(cal, true));
		}

		public String getTimeFormmat(TimeConvertProvider convert,RequestSummaryItemsTimeType timeType,TimeConvertProvider.DATE_TYPE type) {
			Calendar cal = getTimeCalendar(convert,timeType);
			return convert.convertCalendarToFormatter(cal, type);
		}

		public boolean setTimeFormmat(TimeConvertProvider convert,RequestSummaryItemsTimeType timeType,TimeConvertProvider.DATE_TYPE type,String src) {
			Calendar cal = convert.convertFormatterToCalendar(src,type);
			return setTimeCalendar(convert,timeType,cal);
		}

		public void setUserIdData(String users) {
			if (this.users == null) {
				this.users = new ArrayList<String>();
			}
			this.users.clear();
			this.users.add(users);
		}

		public void setUserIdData(ArrayList<String> users) {
			this.users = users;
		}
		public void setBaseUseDatar(ArrayList<BaseUser> users) {
			this.users = new ArrayList<String>();
			for (BaseUser user:users) {
				this.users.add(user.user_id);
			}
		}

		@SuppressWarnings("unchecked")
		public RequestSummaryItems clone() throws CloneNotSupportedException {
			RequestSummaryItems target = (RequestSummaryItems) super.clone();
			if (users != null) {
				target.users = (ArrayList<String>) users.clone();
			}
			return target;
		}
	}

	public static class SummaryItems implements Cloneable, Serializable {
		public static final String TAG = SummaryItems.class.getSimpleName();
		private static final long serialVersionUID = -4678578396378497094L;

		@SerializedName("status_code")
		public String statusCode;
		@SerializedName("summary_by_rule")
		public  ArrayList<SummaryByRule> summary_by_rule;
		@SerializedName("message")
		public String message;
		@SerializedName("records")
		public ArrayList<TimeCard> records;
		@SerializedName("total")
		public int total;

		public SummaryItems() {

		}

		public SummaryItems(ArrayList<TimeCard> rows, int total) {
			this.total = total;
			this.records = rows;
		}

		public SummaryItems(ArrayList<TimeCard> rows) {
			if (rows != null) {
				total = rows.size();
			}
			this.records = rows;
		}

		@SuppressWarnings("unchecked")
		public SummaryItems clone() throws CloneNotSupportedException {
			SummaryItems target = (SummaryItems) super.clone();
			if (records != null) {
				target.records = (ArrayList<TimeCard>) records.clone();
			}
			return target;
		}
	}

	public static class SummaryByRule implements Cloneable, Serializable {
		public static final String TAG = SummaryByRule.class.getSimpleName();
		private static final long serialVersionUID = -8207305549059069L;

		@SerializedName("break")
		public BreakTime breakTime;
		@SerializedName("exceptions")
		public String exceptions;
		@SerializedName("leave_time")
		public LeaveTime leave_time;
		@SerializedName("leaves")
		public String leaves;
		@SerializedName("meal_time")
		public MealTime meal_time;
		@SerializedName("normal")
		public NormalTime normal;
		@SerializedName("rule_type")
		public String rule_type;

		@SerializedName("summary_type")
		public String summary_type;
		@SerializedName("time_rate")
		public NormalTime time_rate;

		@SerializedName("total_work_time")
		public String total_work_time;
		@SerializedName("total_working_time_except_break_meal")
		public String total_working_time_except_break_meal;
		@SerializedName("total_working_time_except_break_meal_and_rule_overtime")
		public String total_working_time_except_break_meal_and_rule_overtime;

		@SerializedName("user ")
		public UserData.SimpleUserWithGroup user;


		public SummaryByRule() {

		}

		@SuppressWarnings("unchecked")
		public SummaryByRule clone() throws CloneNotSupportedException {
			SummaryByRule target = (SummaryByRule) super.clone();
			if (breakTime != null) {
				target.breakTime = (BreakTime) breakTime.clone();
			}
			if (leave_time != null) {
				target.leave_time = (LeaveTime) leave_time.clone();
			}
			if (normal != null) {
				target.normal = (NormalTime) normal.clone();
			}
			if (time_rate != null) {
				target.time_rate = (NormalTime) time_rate.clone();
			}
			return target;
		}
	}

	public static class TimeConverter implements Cloneable, Serializable {
		public static final String TAG = TimeConverter.class.getSimpleName();
		private static final long serialVersionUID = 5574609902296433146L;

		public TimeConverter() {

		}

		public long getHour(String src) {
			long result = Long.valueOf(src);
			result = result/60;
			return result/60;
		}

		public long getMin(String src) {
			long result = Long.valueOf(src);
			result = result/60;
			return result%60;
		}

		public long getSec(String src) {
			long result = Long.valueOf(src);
			return result%60;
		}


		public TimeConverter clone() throws CloneNotSupportedException {
			TimeConverter target = (TimeConverter) super.clone();
			return target;
		}
	}

	public static class BreakTime extends TimeConverter implements Cloneable, Serializable {
		public static final String TAG = BreakTime.class.getSimpleName();
		private static final long serialVersionUID = -1313614880295201136L;

		@SerializedName("over_break")
		public String over_break ;
		@SerializedName("punch_break")
		public String punch_break ;

		public BreakTime() {

		}

		@SuppressWarnings("unchecked")
		public BreakTime clone() throws CloneNotSupportedException {
			BreakTime target = (BreakTime) super.clone();
			return target;
		}
	}

	public static class LeaveTime extends TimeConverter implements Cloneable, Serializable {
		public static final String TAG = LeaveTime.class.getSimpleName();
		private static final long serialVersionUID = -6966003474168807426L;

		@SerializedName("non_worked")
		public String non_worked;
		@SerializedName("worked")
		public String worked;

		public LeaveTime() {

		}

		@SuppressWarnings("unchecked")
		public LeaveTime clone() throws CloneNotSupportedException {
			LeaveTime target = (LeaveTime) super.clone();
			return target;
		}
	}

	public static class MealTime extends TimeConverter implements Cloneable, Serializable {
		public static final String TAG = MealTime.class.getSimpleName();
		private static final long serialVersionUID = 8405212704052985604L;

		@SerializedName("auto")
		public String auto;
		@SerializedName("by_punch")
		public String by_punch;

		public MealTime() {

		}

		@SuppressWarnings("unchecked")
		public MealTime clone() throws CloneNotSupportedException {
			MealTime target = (MealTime) super.clone();
			return target;
		}
	}

	public static class NormalTime extends TimeConverter implements Cloneable, Serializable {
		public static final String TAG = NormalTime.class.getSimpleName();
		private static final long serialVersionUID = 424148641263645803L;

		@SerializedName("monthly_overtime")
		public String monthly_overtime;
		@SerializedName("overtime")
		public String overtime;
		@SerializedName("regular")
		public String regular;
		@SerializedName("weekly_overtime")
		public String weekly_overtime;

		public NormalTime() {

		}

		@SuppressWarnings("unchecked")
		public NormalTime clone() throws CloneNotSupportedException {
			NormalTime target = (NormalTime) super.clone();
			return target;
		}
	}

	public static class TimeCard implements Cloneable, Serializable {
		public static final String TAG = TimeCard.class.getSimpleName();
		private static final long serialVersionUID = 8270602407596467232L;
		public enum SummaryItemTimeType {summary_datetime};
		@SerializedName("status_code")
		public String statusCode;
		@SerializedName("message")
		public String message;

		@SerializedName("summary_datetime")
		public String summary_datetime;
		@SerializedName("users")
		public ArrayList<TimeCardAboutOneUser> users;

		public TimeCard() {

		}

		public String getTimeType(SummaryItemTimeType type) {
			String src= null;
			switch (type) {
				case summary_datetime:
					src = summary_datetime;
					break;
				default:
					break;
			}
			return src;
		}

		public boolean setTimeType(SummaryItemTimeType type,String src) {
			if (src == null || src.isEmpty()) {
				return false;
			}
			switch (type) {
				case summary_datetime:
					summary_datetime = src;
					break;
				default:
					return false;
			}
			return true;
		}

		public Calendar getTimeCalendar(TimeConvertProvider convert,SummaryItemTimeType timeType) {
			return convert.convertServerTimeToCalendar(getTimeType(timeType),true);
		}

		public boolean setTimeCalendar(TimeConvertProvider convert,SummaryItemTimeType timeType,Calendar cal) {
			return setTimeType(timeType, convert.convertCalendarToServerTime(cal, true));
		}

		public String getTimeFormmat(TimeConvertProvider convert,SummaryItemTimeType timeType,TimeConvertProvider.DATE_TYPE type) {
			Calendar cal = getTimeCalendar(convert, timeType);
			return convert.convertCalendarToFormatter(cal, type);
		}

		public boolean setTimeFormmat(TimeConvertProvider convert,SummaryItemTimeType timeType,TimeConvertProvider.DATE_TYPE type,String src) {
			Calendar cal = convert.convertFormatterToCalendar(src,type);
			return setTimeCalendar(convert,timeType,cal);
		}

		public TimeCard clone() throws CloneNotSupportedException {
			TimeCard target = (TimeCard) super.clone();
			if (users != null) {
				target.users = (ArrayList<TimeCardAboutOneUser>) users.clone();
			}
			return target;
		}
	}

	public static class TimeCardAboutOneUser implements Cloneable, Serializable {
		public static final String TAG = TimeCardAboutOneUser.class.getSimpleName();
		private static final long serialVersionUID = -8697920456933457858L;

		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;
		@SerializedName("summary")
		public TimeCardSummaryAboutOneDay summary ;
		@SerializedName("time_cards")
		public ArrayList<TimeCardAboutOneDay> time_cards;
		@SerializedName("user")
		public UserData.SimpleUserWithGroup user ;

		public TimeCardAboutOneUser() {

		}

		public TimeCardAboutOneUser clone() throws CloneNotSupportedException {
			TimeCardAboutOneUser target = (TimeCardAboutOneUser) super.clone();
			if (user != null) {
				target.user = (UserData.SimpleUserWithGroup) user.clone();
			}
			if (summary != null) {
				target.summary = (TimeCardSummaryAboutOneDay) summary.clone();
			}
			if (time_cards != null) {
				target.time_cards = (ArrayList<TimeCardAboutOneDay>) time_cards.clone();
			}
			return target;
		}
	}
	public static class TimeCardSummaryAboutOneDay  implements Cloneable, Serializable {
		public static final String TAG = TimeCardSummaryAboutOneDay.class.getSimpleName();
		private static final long serialVersionUID = 6230659707870949168L;

		@SerializedName("break")
		public BreakTime breakTime;
		@SerializedName("exceptions")
		public String exceptions;
		@SerializedName("leave_time")
		public LeaveTime leave_time;
		@SerializedName("leaves")
		public String leaves;
		@SerializedName("meal_time")
		public MealTime meal_time;
		@SerializedName("normal")
		public NormalTime normal;
		@SerializedName("time_rate")
		public NormalTime time_rate;
		@SerializedName("total_work_time")
		public String total_work_time ;

		public TimeCardSummaryAboutOneDay() {

		}

		public TimeCardSummaryAboutOneDay clone() throws CloneNotSupportedException {
			TimeCardSummaryAboutOneDay target = (TimeCardSummaryAboutOneDay) super.clone();
			return target;
		}
	}
	public static class TimeCardAboutOneDay extends TimeConverter implements Cloneable, Serializable {
		public static final String TAG = TimeCardAboutOneDay.class.getSimpleName();
		private static final long serialVersionUID = -8008894932289776022L;
		public enum UserTimeCardItemTimeType {in_time,out_time,time_card_datetime};

		@SerializedName("break")
		public BreakTime breakTime;
		/**
		 * WEEKDAY' or 'WEEKEND' or 'HOLIDAY
		 */
		@SerializedName("date_type")
		public String date_type;
		@SerializedName("exceptions")
		public ArrayList<ExceptionType> exceptions;
		@SerializedName("id")
		public String id;
		@SerializedName("in_time")
		public String in_time;
		@SerializedName("leave_time")
		public LeaveTime leave_time;
		@SerializedName("leaves")
		public ArrayList<TARequestData.RequestLeave> leaves;
		@SerializedName("meal_time")
		public MealTime meal_time;
		@SerializedName("normal")
		public NormalTime normal;
		@SerializedName("out_time")
		public String out_time;
		@SerializedName("pending_requests")
		public ArrayList<TARequestData.RequestParamDatas> pending_requests;
		@SerializedName("regular_schedule_time")
		public String regular_schedule_time;
		@SerializedName("requests")
		public ArrayList<TARequestData.RequestParamDatas> requests;
		@SerializedName("shift")
		public TARequestData.ShiftWithSimpleOptions shift;
		@SerializedName("time_card_datetime")
		public String time_card_datetime;
		@SerializedName("time_rate")
		public NormalTime time_rate;
		@SerializedName("total_work_time")
		public String total_work_time;
		@SerializedName("unfulfilled_work_time")
		public String unfulfilled_work_time;


		public TimeCardAboutOneDay() {

		}

		public String getTimeType(UserTimeCardItemTimeType type) {
			String src= null;
			switch (type) {
				case in_time:
					src = in_time;
					break;
				case out_time:
					src = out_time;
					break;
				case time_card_datetime:
					src = time_card_datetime;
					break;
				default:
					break;
			}
			return src;
		}

		public boolean setTimeType(UserTimeCardItemTimeType type,String src) {
			if (src == null || src.isEmpty()) {
				return false;
			}
			switch (type) {
				case in_time:
					in_time = src;
					break;
				case out_time:
					out_time = src;
					break;
				case time_card_datetime:
					time_card_datetime = src;
					break;
				default:
					return false;
			}
			return true;
		}

		public Calendar getTimeCalendar(TimeConvertProvider convert,UserTimeCardItemTimeType timeType) {
			return convert.convertServerTimeToCalendar(getTimeType(timeType), true);
		}

		public boolean setTimeCalendar(TimeConvertProvider convert,UserTimeCardItemTimeType timeType,Calendar cal) {
			return setTimeType(timeType, convert.convertCalendarToServerTime(cal, true));
		}

		public String getTimeFormmat(TimeConvertProvider convert,UserTimeCardItemTimeType timeType,TimeConvertProvider.DATE_TYPE type) {
			Calendar cal = getTimeCalendar(convert, timeType);
			return convert.convertCalendarToFormatter(cal, type);
		}

		public boolean setTimeFormmat(TimeConvertProvider convert,UserTimeCardItemTimeType timeType,TimeConvertProvider.DATE_TYPE type,String src) {
			Calendar cal = convert.convertFormatterToCalendar(src, type);
			return setTimeCalendar(convert,timeType,cal);
		}

		public String getRemainTime(Context context) {
			int time = Integer.valueOf(unfulfilled_work_time);
			time = time/60;
			return String.valueOf(time/60)+"h "+String.valueOf(time%60)+"m";
		}

		public TimeCardAboutOneDay clone() throws CloneNotSupportedException {
			TimeCardAboutOneDay target = (TimeCardAboutOneDay) super.clone();
			if (breakTime != null) {
				target.breakTime = breakTime.clone();
			}
			if (exceptions != null) {
				target.exceptions = (ArrayList<ExceptionType>) exceptions.clone();
			}
			if (leave_time != null) {
				target.leave_time = leave_time.clone();
			}
			if (leaves != null) {
				target.leaves = (ArrayList<TARequestData.RequestLeave>) leaves.clone();
			}
			if (normal != null) {
				target.normal = normal.clone();
			}
			if (pending_requests != null) {
				target.pending_requests = (ArrayList<TARequestData.RequestParamDatas> )pending_requests.clone();
			}
			if (requests != null) {
				target.requests = (ArrayList<TARequestData.RequestParamDatas>)requests.clone();
			}
			if (shift != null) {
				target.shift = shift.clone();
			}
			if (time_rate != null) {
				target.time_rate = time_rate.clone();
			}
			return target;
		}
	}

	public static class ExceptionType implements Cloneable, Serializable {
		private static final long serialVersionUID = 6325123415290278418L;
		public static final String TAG = ExceptionType.class.getSimpleName();

		@SerializedName("code")
		public String code ;
		/**
		 * 'Normal', 'Absence', 'Late In', 'Early Out', 'Missing Punch In', 'Missing Punch Out', 'Missing Meal Start', 'Missing Meal End', 'Missing Break Start', 'Missing Break End'
		 */
		@SerializedName("name")
		public String name;

		public ExceptionType() {

		}

		public ExceptionType clone() throws CloneNotSupportedException {
			ExceptionType target = (ExceptionType) super.clone();
			return target;
		}
	}
}
