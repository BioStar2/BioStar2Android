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

import java.io.Serializable;
import java.util.ArrayList;

public class DoorData {

	public static class Doors implements Cloneable, Serializable {
		private static final long serialVersionUID = -408251561486293077L;
		public static final String TAG = Doors.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;
		@SerializedName("records")
		public ArrayList<ListDoor> records;
		@SerializedName("total")
		public int total;

		public Doors() {

		}

		public Doors(ArrayList<ListDoor> rows, int total) {
			this.total = total;
			this.records = rows;
		}

		public Doors(ArrayList<ListDoor> rows) {
			if (rows != null) {
				total = rows.size();
			}
			this.records = rows;
		}

		@SuppressWarnings("unchecked")
		public Doors clone() throws CloneNotSupportedException {
			Doors target = (Doors) super.clone();
			if (records != null) {
				target.records = (ArrayList<ListDoor>) records.clone();
			}
			return target;
		}
	}

	public static class BaseDoor implements Cloneable, Serializable {
		private static final long serialVersionUID = 2062149982981047835L;
		public static final String TAG = BaseDoor.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;

		@SerializedName("id")
		public String id;
		@SerializedName("name")
		public String name;
		@SerializedName("description")
		public String description;

		public BaseDoor() {

		}

		public BaseDoor clone() throws CloneNotSupportedException {
			BaseDoor target = (BaseDoor) super.clone();
			return target;
		}
	}

	public static class ListDoor extends BaseDoor implements Cloneable, Serializable {
		private static final long serialVersionUID = 7060951086894769022L;
		public static final String TAG = ListDoor.class.getSimpleName();

		@SerializedName("door_group_id")
		public String door_group_id;
		@SerializedName("entry_device")
		public BaseDevice entry_device;
		@SerializedName("exit_device")
		public BaseDevice exit_device;

		@SerializedName("open_duration")
		public String open_duration;
		@SerializedName("status")
		public DoorStatus status;

		public ListDoor() {

		}
		
		public String getOpenDuration(String minUnit,String secUnit) {
			long openTime = Long.valueOf(open_duration);
			long min = 0;
			long sec = 0;
			if (openTime > 59) {
				min = openTime/60;
				sec = openTime%60;
			} else {
				sec = openTime;
			}
			String openDuration="";
			if (min > 0) {
				openDuration = min + " " + minUnit;
			} 
			if (sec > 0) {
				if (!openDuration.isEmpty()) {
					openDuration = openDuration + " ";
				} openDuration = openDuration + sec + secUnit;
			}	
			return openDuration;
		}
		
		public ListDoor clone() throws CloneNotSupportedException {
			ListDoor target = (ListDoor) super.clone();
			if (entry_device != null) {
				target.entry_device = entry_device.clone();
			}
			if (exit_device != null) {
				target.exit_device = exit_device.clone();
			}
			return target;
		}
	}

	public static class Door extends ListDoor implements Cloneable, Serializable {
		private static final long serialVersionUID = -6775679932087737969L;
		public static final String TAG = Door.class.getSimpleName();

		@SerializedName("door_relay")
		public DoorRelay door_relay;
		@SerializedName("exit_button")
		public DoorSensor exit_button;
		@SerializedName("door_sensor")
		public DoorSensor door_sensor;
		// /**
		// * trigger action
		// */
		// @SerializedName("alarms")
		// ArrayList<DoorAlarm> alarms;
		@SerializedName("apb_reset_time")
		public long apb_reset_time;
		/**
		 * [NONE:not use anti-passback, SOFT_APB: soft anti-passback(If the exit
		 * Device exists in the door), HARD_APB: hard anti-passback(If the exit
		 * Device exists in the door)],
		 */
		@SerializedName("apb_type")
		public String apb_type;
		@SerializedName("apb_when_disconnected")
		public String apb_when_disconnected;
		@SerializedName("held_open_timeout")
		public long held_open_timeout;
		/**
		 * [ON:Lock when door(sensor) is closed / OFF:keep unlock],
		 */
		@SerializedName("open_once")
		public String open_once;

		public Door() {

		}

		public String getName() {
			if (name == null || name.isEmpty()) {
				return String.valueOf(id);
			}
			return name;
		}

		public Door clone() throws CloneNotSupportedException {
			Door target = (Door) super.clone();
			if (status != null) {
				target.status = (DoorStatus) status.clone();
			}

			if (door_relay != null) {
				target.door_relay = (DoorRelay) door_relay.clone();
			}
			if (door_sensor != null) {
				target.door_sensor = (DoorSensor) door_sensor.clone();
			}
			if (exit_button != null) {
				target.exit_button = (DoorSensor) exit_button.clone();
			}

			return target;
		}
	}

	public static class DoorRelay implements Cloneable, Serializable {
		private static final long serialVersionUID = 2878088017355057274L;
		public static final String TAG = DoorRelay.class.getSimpleName();
		@SerializedName("device")
		public BaseDevice device;
		@SerializedName("index")
		public String index;

		public String getName() {
			if (device != null) {
				return device.getName();
			}
			return null;
		}
		
		public DoorRelay clone() throws CloneNotSupportedException {
			DoorRelay target = (DoorRelay) super.clone();
			return target;
		}
	}

	public static class DoorSensor implements Cloneable, Serializable {
		private static final long serialVersionUID = 2644031752253538672L;
		public static final String TAG = DoorSensor.class.getSimpleName();
		@SerializedName("device")
		public BaseDevice device;
		@SerializedName("index")
		public String index;
		/**
		 * 'OPEN' = Opened, 'CLOSE' = Closed,
		 */
		@SerializedName("default_status")
		public String default_status;

		public DoorSensor clone() throws CloneNotSupportedException {
			DoorSensor target = (DoorSensor) super.clone();
			return target;
		}
		
		public String getName() {
			if (device != null) {
				return device.getName();
			}
			return null;
		}
	}

	public static class DoorStatus implements Cloneable, Serializable {
		private static final long serialVersionUID = -2906715192751679747L;
		public static final String TAG = DoorStatus.class.getSimpleName();
		/**
		 * 'no_alarm', 'held_opened', 'forced_opened'),
		 */
		@SerializedName("normal")
		public boolean normal;
		@SerializedName("locked")
		public boolean locked;
		@SerializedName("unlocked")
		public boolean unlocked;
		@SerializedName("forced_open")
		public boolean forced_open;
		@SerializedName("held_opened")
		public boolean held_opened;
		@SerializedName("forced_open")
		public boolean apb_failed;
		@SerializedName("disconnected")
		public boolean disconnected;

		public DoorStatus clone() throws CloneNotSupportedException {
			DoorStatus target = (DoorStatus) super.clone();
			return target;
		}
	}

}
