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
import com.supremainc.biostar2.sdk.datatype.AccessGroupData.BaseAccessGroup;
import com.supremainc.biostar2.sdk.datatype.AccessGroupData.ListAccessGroup;
import com.supremainc.biostar2.sdk.datatype.DeviceData.BaseDevice;
import com.supremainc.biostar2.sdk.datatype.DoorData.BaseDoor;
import com.supremainc.biostar2.sdk.datatype.UserData.BaseUser;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class EventLogData {

	public enum LogType {		
		DEFAULT("DEFAULT"),DEVICE("DEVICE"), DOOR("DOOR"), USER("USER"),ZONE("ZONE"),AUTHENTICATION("AUTHENTICATION");
		public final String mName;
		private LogType(String name) {
			mName = name;
		}
	}
	public enum LogLevel {
		GREEN("GREEN"), YELLOW("YELLOW"), RED("RED");
		public final String mName;
		private LogLevel(String name) {
			mName = name;
		}
	}
	
	public static class EventTypes implements Cloneable, Serializable {
		private static final long serialVersionUID = -625472133162561019L;
		public static final String TAG = EventTypes.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;
		@SerializedName("records")
		public ArrayList<EventType> records;
		@SerializedName("total")
		public int total;
		
		public EventTypes() {
			
		}
				
		public EventTypes(ArrayList<EventType> rows,int total) {
			this.total = total;
			this.records = rows;
		}
		
		public EventTypes(ArrayList<EventType> rows) {
			if (rows != null) {
				total = rows.size();
			}
			this.records = rows;
		}
		
		@SuppressWarnings("unchecked")
		public EventTypes clone() throws CloneNotSupportedException {
			EventTypes target = (EventTypes) super.clone();
			if (records != null) {
				target.records = (ArrayList<EventType>) records.clone();
			}		
			return target;
		}
	}

	public static class EventLogs implements Cloneable, Serializable {
		private static final long serialVersionUID = -1440378695787901359L;
		public static final String TAG = EventLogs.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;
		@SerializedName("records")
		public ArrayList<EventLog> records;
		@SerializedName("total")
		public int total;
		
		public EventLogs() {
			
		}
				
		public EventLogs(ArrayList<EventLog> rows,int total) {
			this.total = total;
			this.records = rows;
		}
		
		public EventLogs(ArrayList<EventLog> rows) {
			if (rows != null) {
				total = rows.size();
			}
			this.records = rows;
		}
		
		@SuppressWarnings("unchecked")
		public EventLogs clone() throws CloneNotSupportedException {
			EventLogs target = (EventLogs) super.clone();
			if (records != null) {
				target.records = (ArrayList<EventLog>) records.clone();
			}		
			return target;
		}
	}
	
	public static class BaseEventLog implements Cloneable, Serializable {
		private static final long serialVersionUID = 2207955005422777002L;
		public  static final String TAG = BaseAccessGroup.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;
		
		@SerializedName("id")
		public String id;
		@SerializedName("event_type")
		public EventType event_type;
		@SerializedName("description")
		public String description;	
		
		public BaseEventLog() {

		}

		public BaseEventLog clone() throws CloneNotSupportedException {
			BaseEventLog target = (BaseEventLog) super.clone();
			return target;
		}
	}
	
	public static class ListEventLog extends BaseEventLog implements Cloneable, Serializable {
		private static final long serialVersionUID = -7033989203058201905L;
		public  static final String TAG = ListAccessGroup.class.getSimpleName();
		public enum ListEventLogTimeType {datetime,server_datetime};
		@SerializedName("device")
		public BaseDevice device;
		
		@SerializedName("user")
		public BaseUser user;
		
		@SerializedName("door")
		public BaseDoor door;

		/**
		 * type of event ('DEVICE', 'DOOR', 'ALERT'),
		 */
		@SerializedName("type")
		public String type;
		@SerializedName("description")
		public String description;
		@SerializedName("datetime")
		public String datetime;
		@SerializedName("index")
		public String index;
		@SerializedName("server_datetime")
		public String server_datetime;
		/**
		 * event log level('GREEN', 'YELLOW', 'RED'),
		 */
		@SerializedName("level")
		public String level;
		
		public ListEventLog() {

		}
		public String getTimeType(ListEventLogTimeType timeType) {
			String src= null;
			switch (timeType) {
				case datetime:
					src = datetime;
					break;
				case server_datetime:
					src = server_datetime;
					break;
				default:
					break;
			}
			return src;
		}

		public boolean setTimeType(ListEventLogTimeType timeType,String src) {
			if (src == null || src.isEmpty()) {
				return false;
			}
			switch (timeType) {
				case datetime:
					datetime = src;
					break;
				case server_datetime:
					server_datetime = src;
					break;
				default:
					return false;
			}
			return true;
		}

		public Calendar getTimeCalendar(TimeConvertProvider convert,ListEventLogTimeType timeType) {
			return convert.convertServerTimeToCalendar(getTimeType(timeType),true);
		}

		public boolean setTimeCalendar(TimeConvertProvider convert,ListEventLogTimeType timeType,Calendar cal) {
			return setTimeType(timeType, convert.convertCalendarToServerTime(cal, true));
		}

		public String getTimeFormmat(TimeConvertProvider convert,ListEventLogTimeType timeType,TimeConvertProvider.DATE_TYPE type) {
			Calendar cal = getTimeCalendar(convert, timeType);
			return convert.convertCalendarToFormatter(cal, type);
		}

		public boolean setTimeFormmat(TimeConvertProvider convert,ListEventLogTimeType timeType,TimeConvertProvider.DATE_TYPE type,String src) {
			Calendar cal = convert.convertFormatterToCalendar(src,type);
			return setTimeCalendar(convert,timeType,cal);
		}
		
		public ListEventLog clone() throws CloneNotSupportedException {
			ListEventLog target = (ListEventLog) super.clone();
			return target;
		}
	}

	public static class EventLog extends ListEventLog implements Cloneable, Serializable {
		private static final long serialVersionUID = 6497622087994498992L;
		public static final String TAG = EventLog.class.getSimpleName();

		public EventLog() {

		}

		public EventLog clone() throws CloneNotSupportedException {
			EventLog target = (EventLog) super.clone();
			return target;
		}
	}

	public static class EventType implements Cloneable, Serializable {
		private static final long serialVersionUID = 3378092396980913551L;
		public static final String TAG = EventType.class.getSimpleName();
		@SerializedName("code")
		public int code;
		@SerializedName("name")
		public String name;
		@SerializedName("description")
		public String description;
		@SerializedName("alertable")
		public boolean alertable;
		@SerializedName("enable_alert")
		public boolean enable_alert;
		@SerializedName("alert_name")
		public String alert_name;
		@SerializedName("alert_message")
		public String alert_message;

		public EventType clone() throws CloneNotSupportedException {
			EventType target = (EventType) super.clone();
			return target;
		}
	}
	
	public enum EVENT_TYPE {
		VERIFY_SUCCESS_AUTHENTICAION_MODE(4096),
		VERIFY_SUCCESS_ID_PIN(4097),
		VERIFY_SUCCESS_ID_FINGERPRINT(4098),
		VERIFY_SUCCESS_ID_FINGERPRINT_PIN(4099),
		VERIFY_SUCCESS_ID_FACE(4100),
		VERIFY_SUCCESS_ID_FACE_PIN(4101),
		VERIFY_SUCCESS_CARD(4102),
		VERIFY_SUCCESS_CARD_PIN(4103),
		VERIFY_SUCCESS_CARD_FINGERPRINT(4104),
		VERIFY_SUCCESS_CARD_FINGERPRINT_PIN(4105),
		VERIFY_SUCCESS_CARD_FACE(4106),
		VERIFY_SUCCESS_CARD_FACE_PIN(4107),
		VERIFY_SUCCESS_AOC(4108),
		VERIFY_SUCCESS_AOC_PIN(4109),
		VERIFY_SUCCESS_AOC_FINGERPRINT(4110),
		VERIFY_SUCCESS_AOC_FINGERPRINT_PIN(4111),
		
		VERIFY_FAIL_FAILED_CREDENTIAL(4352),
		VERIFY_FAIL_ID(4353),
		VERIFY_FAIL_CARD(4354),
		VERIFY_FAIL_PIN(4355),
		VERIFY_FAIL_FINGERPRINT(4356),
		VERIFY_FAIL_FACE(4357),
		VERIFY_FAIL_AOC_PIN(4358),
		VERIFY_FAIL_AOC_FINGERPRINT(4359),
			
		VERIFY_DURESS_AUTHENTICAION_MODE(4608),
		VERIFY_DURESS_ID_PIN(4609),
		VERIFY_DURESS_ID_FINGERPRINT(4610),
		VERIFY_DURESS_ID_FINGERPRINT_PIN(4611),
		VERIFY_DURESS_ID_FACE(4612),
		VERIFY_DURESS_ID_FACE_PIN(4613),
		VERIFY_DURESS_CARD(4614),
		VERIFY_DURESS_CARD_PIN(4615),
		VERIFY_DURESS_CARD_FINGERPRINT(4616),
		VERIFY_DURESS_CARD_FINGERPRINT_PIN(4617),
		VERIFY_DURESS_CARD_FACE(4618),
		VERIFY_DURESS_CARD_FACE_PIN(4619),
		VERIFY_DURESS_AOC(4620),
		VERIFY_DURESS_AOC_PIN(4621),
		VERIFY_DURESS_AOC_FINGERPRINT(4622),
		VERIFY_DURESS_AOC_FINGERPRINT_PIN(4623),		
		
		IDENTIFY_SUCCESS_AUTHENTICAION_MODE(4864),
		IDENTIFY_SUCCESS_FINGERPRINT(4865),
		IDENTIFY_SUCCESS_FINGERPRINT_PIN(4866),
		IDENTIFY_SUCCESS_FACE(4867),
		IDENTIFY_SUCCESS_FACE_PIN(4868),
		
		IDENTIFY_FAILED_CREDENTIAL(5120),
		IDENTIFY_FAIL_ID(5121),
		IDENTIFY_FAIL_CARD(5122),
		IDENTIFY_FAIL_PIN(5123),
		IDENTIFY_FAIL_FINGERPRINT(5124),
		IDENTIFY_FAIL_FACE(5125),
		IDENTIFY_FAIL_AOC_PIN(5126),
		IDENTIFY_FAIL_AOC_FINGER(5127),
		  
		IDENTIFY_DURESS_SAME_AS_SUCCESS(5376),
		IDENTIFY_DURESS_FINGERPRINT(5377),
		IDENTIFY_DURESS_FINGERPRINT_PIN(5378),
		IDENTIFY_DURESS_FACE(5379),
		IDENTIFY_DURESS_FACE_PIN(5380),
		
		DUAL_AUTH_SUCCESS(5632),
		
		DUAL_AUTH_FAIL_REASON_TO_BE_FAILED(5888),
		DUAL_AUTH_FAIL_TIMEOUT(5889),
		DUAL_AUTH_FAIL_ACCESS_GROUP(5890),
		
		AUTH_FAILED_REASON_TO_BE_DENIED(6144),
		AUTH_FAILED_INVALID_AUTH_MODE(6145),
		AUTH_FAILED_INVALID_CREDENTIAL(6146),
		AUTH_FAILED_TIMEOUT(6147),
		
		ACCESS_DENIED_REASON_TO_BE_DENIED(6400),
		ACCESS_DENIED_ACCESS_GROUP(6401),
		ACCESS_DENIED_DISABLED(6402),
		ACCESS_DENIED_EXPIRED(6403),
		ACCESS_DENIED_ON_BLACKLIST(6404),
		ACCESS_DENIED_APB(6405),
		ACCESS_DENIED_TIMED_APB(6406),
		ACCESS_DENIED_FORCED_LOCK_SCHEDULE(6407),
			
		USER_ENROLL_SUCCESS(8192),
		USER_ENROLL_FAIL(8448),
		USER_UPDATE_SUCCESS(8704),
		USER_UPDATE_FAIL(8960),
		USER_DELETE_SUCCESS(9216),
		USER_DELETE_FAIL(9472),
		USER_DELETE_ALL_SUCCESS(9728),
		USER_ISSUE_AOC_SUCCESS(9984),
		
		DEVICE_SYSTEM_RESET(12288),
		DEVICE_SYSTEM_STARTED(12544),
		DEVICE_TIME_SET(12800),
		DEVICE_LINK_CONNECTED(13056),
		DEVICE_LINK_DISCONNECTED(13312),
		DEVICE_DHCP_SUCCESS(13568),
		DEVICE_ADMIN_MENU(13824),
		DEVICE_UI_LOCKED(14080),
		DEVICE_UI_UNLOCKED(14336),
		DEVICE_COMM_LOCKED(14592),
		DEVICE_COMM_UNLOCKED(14848),
		DEVICE_TCP_CONNECTED(15104),
		DEVICE_TCP_DISCONNECTED(15360),
		DEVICE_RS485_CONNECTED(15616),
		DEVICE_RS485_DISCONNECTED(15872),
		DEVICE_INPUT_DETECTED(16128),
		DEVICE_TAMPER_ON(16384),
		DEVICE_TAMPER_OFF(16640),
		DEVICE_EVENT_LOG_CLEARED(16896),
		DEVICE_FIRMWARE_UPGRADED(17152),
		DEVICE_RESOURCE_UPGRADED(17408),
		DEVICE_CONFIG_RESET(17664),
						
		DOOR_UNLOCKED(20480),
		DOOR_LOCKED(20736),
		DOOR_OPEN(20992),
		DOOR_CLOSE(21248),
		DOOR_FORCED_OPEN(21504),
		DOOR_HELD_OPEN(21760),
		DOOR_FORCED_OPEN_ALARM(22016),
		DOOR_FORCED_OPEN_ALARM_CLEAR(22272),
		DOOR_HELD_OPEN_ALARM(22528),
		DOOR_HELD_OPEN_ALARM_CLEAR(22784),
		DOOR_APB_ALARM(23040),
		DOOR_APB_ALARM_CLEAR(23296),
		
		ZONE_APB_VIOLATION(24576),
		ZONE_APB_VIOLATION_HARD(24577),
		ZONE_APB_VIOLATION_SOFT(24578),		
		ZONE_APB_ALARM(24832),
		ZONE_APB_ALARM_CLEAR(25088),
		ZONE_TIMED_APB_VIOLATION(25344),
		ZONE_TIMED_APB_ALARM(25600),
		ZONE_TIMED_APB_ALARM_CLEAR(25856),
		ZONE_FIRE_ALARM_INPUT(26112),
		ZONE_FIRE_ALARM(26368),
		ZONE_FIRE_ALARM_CLEAR(26624),
		ZONE_FORCED_LOCK_START(26880),		
		ZONE_FORCED_LOCK_END(27136),
		ZONE_FORCED_UNLOCK_START(27392),
		ZONE_FORCED_UNLOCK_END(27648);

		public final int code;
		public final String strCode;
	
		private EVENT_TYPE(int code) {
			this.code = code;
			this.strCode = String.valueOf(code);
		}
	};

}
