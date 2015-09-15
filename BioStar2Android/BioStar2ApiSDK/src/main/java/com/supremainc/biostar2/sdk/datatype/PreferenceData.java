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

import java.io.Serializable;
import java.util.ArrayList;

public class PreferenceData {

	public static class Preference implements Cloneable, Serializable {
		private static final long serialVersionUID = -6788336586649309987L;
		public static final String TAG = Preference.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;


		@SerializedName("date_format")
		public String date_format;
		@SerializedName("time_format")
		public String time_format;

		@SerializedName("notifications")
		public ArrayList<NotificationsSetting> notifications;

		public Preference clone() throws CloneNotSupportedException {
			Preference target = (Preference) super.clone();
			if (notifications != null) {
				target.notifications = (ArrayList<NotificationsSetting>)notifications.clone();
			}
			return target;
		}
	}
	
	public static class NotificationsSetting implements Cloneable, Serializable {
		private static final long serialVersionUID = 6724828159087582264L;
		public static final String TAG = NotificationsSetting.class.getSimpleName();
		
		@SerializedName("description")
		public String description;
		@SerializedName("subscribed")
		public boolean subscribed;
		@SerializedName("type")
		public String type;
				
//		@SerializedName("DEVICE_REBOOT")
//		public boolean DEVICE_REBOOT;
//		@SerializedName("DEVICE_RS485_DISCONNECT")
//		public boolean DEVICE_RS485_DISCONNECT;
//		@SerializedName("DEVICE_TAMPERING")
//		public boolean DEVICE_TAMPERING;
//		@SerializedName("DOOR_FORCED_OPEN")
//		public boolean DOOR_FORCED_OPEN;
//		@SerializedName("DOOR_HELD_OPEN")
//		public boolean DOOR_HELD_OPEN;
//		@SerializedName("DOOR_OPEN_REQUEST")
//		public boolean DOOR_OPEN_REQUEST;
//		@SerializedName("ZONE_APB")
//		public boolean ZONE_APB;
//		@SerializedName("ZONE_FIRE")
//		public boolean ZONE_FIRE;
		public NotificationsSetting clone() throws CloneNotSupportedException {
			NotificationsSetting target = (NotificationsSetting) super.clone();
			return target;
		}
	}

}
