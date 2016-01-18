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

public class TARequestData {
	private static final String CORRECTION = "CORRECTION";
	private static final String LEAVE = "LEAVE";
	private static final String REJECTED = "REJECTED";
	private static final String APPROVED = "APPROVED";
	private static final String PENDING = "PENDING";
	public enum RequestType {ALL,CORRECTION,LEAVE};
	public enum RequestResult {PENDING,APPROVED,REJECTED};
	public static class RequestRejectApprove implements Cloneable, Serializable {
		public static final String TAG = RequestRejectApprove.class.getSimpleName();
		private static final long serialVersionUID = -4681153459424522371L;
		@SerializedName("comment")
		public String comment =" ";
		@SerializedName("type")
		public String type;

		public RequestRejectApprove() {

		}
		public void setType(RequestType type) {
			switch (type) {
				case CORRECTION:
					this.type = CORRECTION;
					break;
				case LEAVE:
					this.type = LEAVE;
					break;
			}
		}
	}
	public static class RequestParamDatas implements Cloneable, Serializable {
		public static final String TAG = RequestParamDatas.class.getSimpleName();
		private static final long serialVersionUID = -8502345347279989895L;

		@SerializedName("correction_options")
		public RequestCorrection correction;
		@SerializedName("leave_options")
		public RequestLeave leave_options;
		/**
		 * CORRECTION or LEAVE
		 */
		@SerializedName("type")
		public String type;

		public RequestParamDatas() {

		}

		public RequestType getType() {
			if (CORRECTION.equals(type)) {
				return RequestType.CORRECTION;
			} else if (LEAVE.equals(type)) {
				return RequestType.LEAVE;
			}
			return null;
		}

		public void setType(RequestType type) {
			switch (type) {
				case CORRECTION:
					this.type = CORRECTION;
					break;
				case LEAVE:
					this.type = LEAVE;
					break;
			}
		}

		@SuppressWarnings("unchecked")
		public RequestParamDatas clone() throws CloneNotSupportedException {
			RequestParamDatas target = (RequestParamDatas) super.clone();
			if (correction != null) {
				target.correction = (RequestCorrection) correction.clone();
			}
			if (leave_options != null) {
				target.leave_options = (RequestLeave) leave_options.clone();
			}
			return target;
		}
	}

	public static class RequestCorrection implements Cloneable, Serializable {
		public static final String TAG = RequestCorrection.class.getSimpleName();
		private static final long serialVersionUID = -5555869782622316297L;
		public enum RequestCorrectionTimeType {event_datetime,in_time,out_time,request_in_time,request_out_time};
		@SerializedName("comments")
		public String comments;
		@SerializedName("event_datetime")
		public String event_datetime;
		@SerializedName("in_time")
		public String in_time;
		@SerializedName("out_time")
		public String out_time;
		@SerializedName("request_in_time")
		public String request_in_time;
		@SerializedName("request_out_time")
		public String request_out_time;
        @SerializedName("shift")
        public ShiftWithSimpleOptions  shift;
		//TODO delete
//		@SerializedName("to_user")
//		public UserData.BaseUser to_user;
		public RequestCorrection() {
//			to_user = new UserData.BaseUser();
//			to_user.user_id = "20141208";
		}

		public String getTimeType(RequestCorrectionTimeType type) {
			String src= null;
			switch (type) {
				case in_time:
					src = in_time;
					break;
				case out_time:
					src = out_time;
					break;
				case event_datetime:
					src = event_datetime;
					break;
				case request_in_time:
					src = request_in_time;
					break;
				case request_out_time:
					src = request_out_time;
					break;
				default:
					break;
			}
			return src;
		}

		public boolean setTimeType(RequestCorrectionTimeType type,String src) {
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
				case event_datetime:
					event_datetime = src;
					break;
				case request_in_time:
					request_in_time = src;
					break;
				case request_out_time:
					request_out_time = src;
					break;
				default:
					return false;
			}
			return true;
		}

		public Calendar getTimeCalendar(TimeConvertProvider convert,RequestCorrectionTimeType timeType) {
			return convert.convertServerTimeToCalendar(getTimeType(timeType), true);
		}

		public boolean setTimeCalendar(TimeConvertProvider convert,RequestCorrectionTimeType timeType,Calendar cal) {
			return setTimeType(timeType, convert.convertCalendarToServerTime(cal, true));
		}

		public String getTimeFormmat(TimeConvertProvider convert,RequestCorrectionTimeType timeType,TimeConvertProvider.DATE_TYPE type) {
			Calendar cal = getTimeCalendar(convert,timeType);
			return convert.convertCalendarToFormatter(cal, type);
		}

		public boolean setTimeFormmat(TimeConvertProvider convert,RequestCorrectionTimeType timeType,TimeConvertProvider.DATE_TYPE type,String src) {
			Calendar cal = convert.convertFormatterToCalendar(src,type);
			return setTimeCalendar(convert,timeType,cal);
		}

		@SuppressWarnings("unchecked")
		public RequestCorrection clone() throws CloneNotSupportedException {
			RequestCorrection target = (RequestCorrection) super.clone();
            if (shift != null) {
                target.shift = shift.clone();
            }
			return target;
		}
	}

	public static class RequestPayCode implements Cloneable, Serializable {
		public static final String TAG = RequestPayCode.class.getSimpleName();
		private static final long serialVersionUID = 5469559781591484041L;
		@SerializedName("status_code")
		public String message;
		@SerializedName("message")
		public String total;
		@SerializedName("records")
		public ArrayList<SimplePayCode> records;
		public RequestPayCode() {

		}

		public SimplePayCode getVacationID() {
			if (records != null) {
				for (SimplePayCode pay:records) {
					if (pay.name.equals("Vacation")) {
						return pay;
					}
				}
			}
			return null;
		}

		public RequestPayCode clone() throws CloneNotSupportedException {
			RequestPayCode target = (RequestPayCode) super.clone();
			if (records != null) {
				target.records = (ArrayList<SimplePayCode>)records.clone();
			}
			return target;
		}
	}

	public static class RequestLeave implements Cloneable, Serializable {
		public static final String TAG = RequestLeave.class.getSimpleName();
		private static final long serialVersionUID = -4480614051757187420L;
		public enum RequestLeaveTimeType {start_datetime,end_datetime};
		/**
		 *  ( ex. 2015-06-10 15:00 ),
		 */
		@SerializedName("end_datetime")
		public String end_datetime;
		@SerializedName("id")
		public String id;
		@SerializedName("name")
		public String name;
		@SerializedName("pay_code")
		public SimplePayCode pay_code;
		/**
		 *  ( ex. 2015-06-10 15:00 ),
		 */
		@SerializedName("start_datetime")
		public String start_datetime;
		@SerializedName("comments")
		public String comments;

		public RequestLeave() {

		}

		public void setPayCode(SimplePayCode id) {
			pay_code = id;
//			pay_code.id = id;
//			//pay_code.id = "561e479e5b60eeb70a42e9c6";
//			pay_code.name = "Vacation";
		}

		public String getTimeType(RequestLeaveTimeType type) {
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

		public boolean setTimeType(RequestLeaveTimeType type,String src) {
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

		public Calendar getTimeCalendar(TimeConvertProvider convert,RequestLeaveTimeType timeType) {
			return convert.convertServerTimeToCalendar(getTimeType(timeType),true);
		}

		public boolean setTimeCalendar(TimeConvertProvider convert,RequestLeaveTimeType timeType,Calendar cal) {
			return setTimeType(timeType, convert.convertCalendarToServerTime(cal,true));
		}

		public String getTimeFormmat(TimeConvertProvider convert,RequestLeaveTimeType timeType,TimeConvertProvider.DATE_TYPE type) {
			Calendar cal = getTimeCalendar(convert,timeType);
			return convert.convertCalendarToFormatter(cal, type);
		}

		public boolean setTimeFormmat(TimeConvertProvider convert,RequestLeaveTimeType timeType,TimeConvertProvider.DATE_TYPE type,String src) {
			Calendar cal = convert.convertFormatterToCalendar(src,type);
			return setTimeCalendar(convert,timeType,cal);
		}

		public RequestLeave clone() throws CloneNotSupportedException {
			RequestLeave target = (RequestLeave) super.clone();
			if (pay_code != null) {
				target.pay_code = pay_code.clone();
			}
			return target;
		}
	}
	public static class SimplePayCode implements Cloneable, Serializable {
		public static final String TAG = SimplePayCode.class.getSimpleName();
		private static final long serialVersionUID = -7542770758530685560L;

		@SerializedName("id")
		public String id ;
		@SerializedName("name")
		public String name;

		public SimplePayCode() {

		}

		public SimplePayCode clone() throws CloneNotSupportedException {
			SimplePayCode target = (SimplePayCode) super.clone();
			return target;
		}
	}

	public static class ShiftWithSimpleOptions  implements Cloneable, Serializable {
		public static final String TAG = SimplePayCode.class.getSimpleName();
		private static final long serialVersionUID = -8350180699019595117L;
		private static final String FIXED = "FIXED";
		private static final String FLEXIBLE = "FLEXIBLE";
		public enum ShiftWithSimpleOptionsType {FIXED,FLEXIBLE};
		@SerializedName("id")
		public String id ;
		@SerializedName("type")
		public String type ;
		@SerializedName("name")
		public String name;
		@SerializedName("fixed_shift_info")
		public FixedShiftInfo fixed_shift_info;
		@SerializedName("flexible_shift_info")
		public FlexibleShiftInfo flexible_shift_info;

		public ShiftWithSimpleOptions  () {

		}

		public ShiftWithSimpleOptionsType getType() {
			if (FIXED.equals(type)) {
				return ShiftWithSimpleOptionsType.FIXED;
			} else if (FLEXIBLE.equals(type)) {
				return ShiftWithSimpleOptionsType.FLEXIBLE;
			}
			return null;
		}

		public void setType(ShiftWithSimpleOptionsType type) {
			switch (type) {
				case FIXED:
					this.type = FIXED;
					break;
				case FLEXIBLE:
					this.type = FLEXIBLE;
					break;
			}
		}

		public ShiftWithSimpleOptions  clone() throws CloneNotSupportedException {
			ShiftWithSimpleOptions   target = (ShiftWithSimpleOptions  ) super.clone();
			if (fixed_shift_info != null) {
				target.fixed_shift_info = fixed_shift_info.clone();
			}
			if (flexible_shift_info != null) {
				target.flexible_shift_info = flexible_shift_info.clone();
			}
			return target;
		}
	}

	public static class FixedShiftInfo implements Cloneable, Serializable {
		public static final String TAG = FixedShiftInfo.class.getSimpleName();
		private static final long serialVersionUID = -1769575607070809434L;


		@SerializedName("end_time")
		public String end_time;
		@SerializedName("start_time")
		public String start_time;
		@SerializedName("pay_code")
		public SimplePayCode pay_code;

		public FixedShiftInfo() {

		}

		public FixedShiftInfo clone() throws CloneNotSupportedException {
			FixedShiftInfo target = (FixedShiftInfo) super.clone();
			if (pay_code != null) {
				target.pay_code = pay_code.clone();
			}
			return target;
		}
	}

	public static class FlexibleShiftInfo implements Cloneable, Serializable {
		public static final String TAG = FlexibleShiftInfo.class.getSimpleName();
		private static final long serialVersionUID = -8052841779358128145L;

		/**
		 * Shift working hours ( ex. '8.0', '5.5', '7' ),
		 */
		@SerializedName("hours")
		public String hours;
		@SerializedName("pay_code")
		public SimplePayCode pay_code;

		public FlexibleShiftInfo() {

		}

		public FlexibleShiftInfo clone() throws CloneNotSupportedException {
			FlexibleShiftInfo target = (FlexibleShiftInfo) super.clone();
			if (pay_code != null) {
				target.pay_code = pay_code.clone();
			}
			return target;
		}
	}

	public static class RequestRequestList extends TASummaryData.RequestSummaryItems implements Cloneable, Serializable {
		public static final String TAG = RequestRequestList.class.getSimpleName();
		private static final long serialVersionUID = 7902743570301531393L;

		@SerializedName("limit")
		public String limit;
		@SerializedName("offset")
		public String offset;
		@SerializedName("status")
		public String status;

		public RequestRequestList(RequestSummaryItemsType requestType) {
			super(requestType);
		}

		public void setType(RequestType type) {
			switch (type) {
				case CORRECTION:
					this.type = CORRECTION;
					break;
				case LEAVE:
					this.type = LEAVE;
					break;
			}
		}

		@SuppressWarnings("unchecked")
		public RequestRequestList clone() throws CloneNotSupportedException {
			RequestRequestList target = (RequestRequestList) super.clone();
			return target;
		}
	}

	public static class RequestList implements Cloneable, Serializable {
		public static final String TAG = RequestList.class.getSimpleName();
		private static final long serialVersionUID = -564334848647365356L;

		@SerializedName("message")
		public String message;
		@SerializedName("records")
		public ArrayList<RequestItem> records;
		@SerializedName("status_code")
		public String status_code;
		@SerializedName("total")
		public long total;

		public RequestList() {

		}

		@SuppressWarnings("unchecked")
		public RequestList clone() throws CloneNotSupportedException {
			RequestList target = (RequestList) super.clone();
			if (records != null) {
				target.records = (ArrayList<RequestItem>) records.clone();
			}
			return target;
		}
	}

	public static class RequestItem implements Cloneable, Serializable {
		public static final String TAG = RequestItem.class.getSimpleName();
		private static final long serialVersionUID = -4512949101787970550L;
		public enum RequestItemTimeType {accepted_datetime,request_datetime};
		@SerializedName("comments")
		public String comments;
		@SerializedName("accepted_datetime")
		public String accepted_datetime;
		@SerializedName("correction_options")
		public RequestCorrectionEx correction;
		@SerializedName("id")
		public String id;
		@SerializedName("leave_options")
		public RequestLeaveEx leave;
		@SerializedName("request_datetime")
		public String request_datetime;
		/**
		 * 'PENDING' or 'APPROVED' or 'REJECTED']
		 */
		@SerializedName("request_status")
		public String request_status;
		/**
		 * CORRECTION' or 'LEAVE
		 */
		@SerializedName("type")
		public String type;

		public RequestItem() {

		}
		public String getTimeType(RequestItemTimeType type) {
			String src= null;
			switch (type) {
				case accepted_datetime:
					src = accepted_datetime;
					break;
				case request_datetime:
					src = request_datetime;
					break;
				default:
					break;
			}
			return src;
		}

		public boolean setTimeType(RequestItemTimeType type,String src) {
			if (src == null || src.isEmpty()) {
				return false;
			}
			switch (type) {
				case accepted_datetime:
					accepted_datetime = src;
					break;
				case request_datetime:
					request_datetime = src;
					break;
				default:
					return false;
			}
			return true;
		}

		public Calendar getTimeCalendar(TimeConvertProvider convert,RequestItemTimeType timeType) {
			return convert.convertServerTimeToCalendar(getTimeType(timeType),true);
		}

		public boolean setTimeCalendar(TimeConvertProvider convert,RequestItemTimeType timeType,Calendar cal) {
			return setTimeType(timeType, convert.convertCalendarToServerTime(cal,true));
		}

		public String getTimeFormmat(TimeConvertProvider convert,RequestItemTimeType timeType,TimeConvertProvider.DATE_TYPE type) {
			Calendar cal = getTimeCalendar(convert,timeType);
			return convert.convertCalendarToFormatter(cal, type);
		}

		public boolean setTimeFormmat(TimeConvertProvider convert,RequestItemTimeType timeType,TimeConvertProvider.DATE_TYPE type,String src) {
			Calendar cal = convert.convertFormatterToCalendar(src,type);
			return setTimeCalendar(convert,timeType,cal);
		}

		public RequestResult getRequestResult() {
			if (PENDING.equals(request_status)) {
				return RequestResult.PENDING;
			}
			if (APPROVED.equals(request_status)) {
				return RequestResult.APPROVED;
			}
			if (REJECTED.equals(request_status)) {
				return RequestResult.REJECTED;
			}
			return null;
		}

		public RequestType getType() {
			if (CORRECTION.equals(type)) {
				return RequestType.CORRECTION;
			} else if (LEAVE.equals(type)) {
				return RequestType.LEAVE;
			}
			return null;
		}

//		public Calendar getRequestDate(Context context) {
//			CommonDataProvider commonDataProvider = CommonDataProvider.getInstance(context);
//			return commonDataProvider.convertServerTimeToClientCalendar(context, request_datetime);
//		}

		public RequestItem clone() throws CloneNotSupportedException {
			RequestItem target = (RequestItem) super.clone();
			if (correction != null) {
				target.correction = correction.clone();
			}
			if (leave != null) {
				target.leave = leave.clone();
			}
			return target;
		}
	}

	public static class RequestCorrectionEx extends TARequestData.RequestCorrection implements Cloneable, Serializable {
		public static final String TAG = RequestItem.class.getSimpleName();
		private static final long serialVersionUID = 5738079917053187593L;

		@SerializedName("request_user")
		public UserData.BaseUser request_user;
		@SerializedName("to_user")
		public UserData.BaseUser to_user;

		public RequestCorrectionEx() {

		}

		public RequestCorrectionEx clone() throws CloneNotSupportedException {
			RequestCorrectionEx target = (RequestCorrectionEx) super.clone();
			if (request_user != null) {
				target.request_user = request_user.clone();
			}
			if (to_user != null) {
				target.to_user = to_user.clone();
			}
			return target;
		}
	}

	public static class RequestLeaveEx extends TARequestData.RequestLeave implements Cloneable, Serializable {
		public static final String TAG = RequestItem.class.getSimpleName();
		private static final long serialVersionUID = -4612362853645242041L;

		@SerializedName("request_user")
		public UserData.BaseUser request_user;
		@SerializedName("to_user")
		public UserData.BaseUser to_user;

		public RequestLeaveEx() {

		}

		public RequestLeaveEx clone() throws CloneNotSupportedException {
			RequestLeaveEx target = (RequestLeaveEx) super.clone();
			if (request_user != null) {
				target.request_user = request_user.clone();
			}
			if (to_user != null) {
				target.to_user = to_user.clone();
			}
			return target;
		}
	}
}
