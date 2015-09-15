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
import com.supremainc.biostar2.sdk.datatype.DeviceData.BaseDevice;
import com.supremainc.biostar2.sdk.datatype.DoorData.BaseDoor;
import com.supremainc.biostar2.sdk.datatype.UserData.BaseUser;

import java.io.Serializable;

public class NotificationData {
	
	public enum NotificationType {
		DEVICE_REBOOT("device_reboot"), DEVICE_RS485_DISCONNECT("device_rs485_disconnect"), 
		DEVICE_TAMPERING("device_tampering"), DOOR_FORCED_OPEN("door_forced_open"), DOOR_HELD_OPEN("door_held_open"), DOOR_OPEN_REQUEST("door_open_request"), ZONE_APB("zone_apb"), ZONE_FIRE("zone_fire");
		public final String mName;
		private NotificationType(String name) {
			mName = name;
		}
	}
	
	public static class PushNotification implements Cloneable, Serializable {
		private static final long serialVersionUID = -3331394917966008473L;
		public static final String TAG = PushNotification.class.getSimpleName();
		@SerializedName("device")
		public BaseDevice device;
		@SerializedName("door")
		public BaseDoor door;
		@SerializedName("request_user")
		public BaseUser user;
		@SerializedName("message")
		public String message;
		@SerializedName("request_timestamp")
		public String request_timestamp;
		@SerializedName("title")
		public String title;
		@SerializedName("contact_phone_number")
		public String contact_phone_number;
		public String code;
		public int dbID = -1;
		/**
		 * 0: read , 1: unread
		 */
		public int unread;
		
		public PushNotification clone() throws CloneNotSupportedException {
			PushNotification target = (PushNotification) super.clone();
			if (device != null) {
				target.device = device.clone();
			}
			if (door != null) {
				target.door = door.clone();
			}
			if (user != null) {
				target.user = user.clone();
			}
			return target;
		}
	}

	
	public static class DeviceNotification implements Cloneable, Serializable {
		private static final long serialVersionUID = -8413287596496311241L;
		public static final String TAG = DeviceNotification.class.getSimpleName();
		@SerializedName("device")
		public BaseDevice device;
		@SerializedName("message")
		public String message;
		@SerializedName("request_timestamp")
		public String request_timestamp;
		@SerializedName("title")
		public String title;
		
		public DeviceNotification clone() throws CloneNotSupportedException {
			DeviceNotification target = (DeviceNotification) super.clone();
			if (device != null) {
				target.device = device.clone();
			}
			return target;
		}
	}

	public static class DoorNotification implements Cloneable, Serializable {
		private static final long serialVersionUID = 3012490700070629402L;
		public static final String TAG = DoorNotification.class.getSimpleName();
		@SerializedName("door")
		public BaseDoor door;
		@SerializedName("message")
		public String message;
		@SerializedName("request_timestamp")
		public String request_timestamp;
		@SerializedName("title")
		public String title;
		
		public DoorNotification clone() throws CloneNotSupportedException {
			DoorNotification target = (DoorNotification) super.clone();
			if (door != null) {
				target.door = door.clone();
			}
			return target;
		}
	}

	public static class DoorOpenRequestNotification implements Cloneable, Serializable {
		private static final long serialVersionUID = -7275642580553178929L;
		public static final String TAG = DoorOpenRequestNotification.class.getSimpleName();

		@SerializedName("contact_phone_number")
		public String contact_phone_number;
		@SerializedName("door")
		public BaseDoor door;
		@SerializedName("request_user")
		public BaseUser user;
		@SerializedName("message")
		public String message;
		@SerializedName("request_timestamp")
		public String request_timestamp;
		@SerializedName("title")
		public String title;
		
		public DoorOpenRequestNotification clone() throws CloneNotSupportedException {
			DoorOpenRequestNotification target = (DoorOpenRequestNotification) super.clone();
			if (door != null) {
				target.door = door.clone();
			}
			if (user != null) {
				target.user = user.clone();
			}
			return target;
		}
	}

}
