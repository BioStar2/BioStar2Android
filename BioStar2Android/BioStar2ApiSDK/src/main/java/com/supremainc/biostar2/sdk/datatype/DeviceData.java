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
import com.supremainc.biostar2.sdk.datatype.DeviceTypeData.DeviceType;
import com.supremainc.biostar2.sdk.datatype.DoorData.BaseDoor;

import java.io.Serializable;
import java.util.ArrayList;

public class DeviceData {
	public static final String SCAN_FINGERPRINT_ENROLL_QUALITY = "enroll_quality";
	public static final String SCAN_FINGERPRINT_GET_IMAGE = "get_raw_image";
	
	public static class Devices implements Cloneable, Serializable {
		private static final long serialVersionUID = 412418435452173287L;
		public static final String TAG = Devices.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;
		@SerializedName("records")
		public ArrayList<ListDevice> records;
		@SerializedName("total")
		public int total;

		public Devices() {

		}

		public Devices(ArrayList<ListDevice> rows, int total) {
			this.total = total;
			this.records = rows;
		}

		public Devices(ArrayList<ListDevice> rows) {
			if (rows != null) {
				total = rows.size();
			}
			this.records = rows;
		}

		@SuppressWarnings("unchecked")
		public Devices clone() throws CloneNotSupportedException {
			Devices target = (Devices) super.clone();
			if (records != null) {
				target.records = (ArrayList<ListDevice>) records.clone();
			}
			return target;
		}
	}
	public static class BaseDeviceGroup implements Cloneable, Serializable {
		private static final long serialVersionUID = -7485629264807780971L;
		public static final String TAG = BaseDeviceGroup.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;

		@SerializedName("id")
		public String id;
		@SerializedName("name")
		public String name;

		public BaseDeviceGroup() {

		}

		public BaseDeviceGroup clone() throws CloneNotSupportedException {
			BaseDeviceGroup target = (BaseDeviceGroup) super.clone();
			return target;
		}
	}

	public static class BaseDevice implements Cloneable, Serializable {
		private static final long serialVersionUID = 5074124575377999655L;
		public static final String TAG = BaseDevice.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;

		@SerializedName("id")
		public String id;
		@SerializedName("name")
		public String name;
		@SerializedName("device_group")
		public BaseDeviceGroup device_group;
		@SerializedName("device_type")
		public DeviceType device_type;

		public BaseDevice() {

		}

		public BaseDevice(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public BaseDevice clone() throws CloneNotSupportedException {
			BaseDevice target = (BaseDevice) super.clone();
			return target;
		}

		public String getName() {
			if (name == null || name.isEmpty()) {
				return String.valueOf(id);
			}
			return name;
		}
	}

	public static class ListDevice extends BaseDevice implements Cloneable, Serializable {
		private static final long serialVersionUID = -5011790137340832929L;
		public static final String TAG = ListDevice.class.getSimpleName();

		@SerializedName("children")
		public ArrayList<BaseDevice> children;
		@SerializedName("lan")
		public DeviceLanInfo lan;
		/**
		 * 'PARENT', 'CHILD', 'DEFAULT'
		 */
		@SerializedName("mode")
		public String mode;
		@SerializedName("status")
		public String status;
		@SerializedName("wlan")
		public DeviceWlanInfo wlan;
		
		@SerializedName("used_by_doors")
		public ArrayList<BaseDoor> used_by_doors;

		public ListDevice() {

		}

		public ListDevice(String id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public boolean isSupport(boolean isWithOutSlave,int deviceSupport) {
			if (mode == null || device_type == null) {
				return false;
			}
			if (isWithOutSlave) {
				if (mode.equals("CHILD")) {
					return false;
				}
			}
			return device_type.isSupport(deviceSupport);
		}

		public ListDevice clone() throws CloneNotSupportedException {
			ListDevice target = (ListDevice) super.clone();
			if (children != null) {
				target.children = (ArrayList<BaseDevice>) children.clone();
			}
			if (lan != null) {
				target.lan = lan.clone();
			}
			if (wlan != null) {
				target.wlan = wlan.clone();
			}
			if (used_by_doors != null ) {
				target.used_by_doors = (ArrayList<BaseDoor>) used_by_doors.clone();
			}
			return target;
		}
	}

	public static class Device extends ListDevice implements Cloneable, Serializable {
		private static final long serialVersionUID = 4233045721199510646L;
		public static final String TAG = Device.class.getSimpleName();

		public Device() {

		}

		public Device clone() throws CloneNotSupportedException {
			Device target = (Device) super.clone();
			return target;
		}
	}

	public static class DeviceLanInfo implements Cloneable, Serializable {
		private static final long serialVersionUID = -6866383826353376929L;
		public static final String TAG = DeviceLanInfo.class.getSimpleName();
		@SerializedName("baseband")
		public String baseband;
		@SerializedName("connection_mode")
		public String connection_mode;
		@SerializedName("device_port")
		public String device_port;
		@SerializedName("enable_dhcp")
		public boolean enable_dhcp;
		@SerializedName("gateway")
		public String gateway;
		@SerializedName("ip")
		public String ip;
		@SerializedName("mtu_size")
		public String mtu_size;
		@SerializedName("subnet_mask")
		public String subnet_mask;
		@SerializedName("server_port")
		public String server_port;
		@SerializedName("server_ip")
		public String server_ip;

		public DeviceLanInfo() {

		}

		public DeviceLanInfo clone() throws CloneNotSupportedException {
			DeviceLanInfo target = (DeviceLanInfo) super.clone();
			return target;
		}
	}

	public static class DeviceWlanInfo implements Cloneable, Serializable {
		private static final long serialVersionUID = -6866383826353376929L;
		public static final String TAG = DeviceWlanInfo.class.getSimpleName();
		@SerializedName("enabled")
		public boolean enabled;
		@SerializedName("wireless_mode")
		public String wireless_mode;
		@SerializedName("security_mode")
		public String security_mode;
		@SerializedName("wpa_algorithm")
		public String wpa_algorithm;

		public DeviceWlanInfo() {

		}

		public DeviceWlanInfo clone() throws CloneNotSupportedException {
			DeviceWlanInfo target = (DeviceWlanInfo) super.clone();
			return target;
		}
	}

	public static class CardConfiguration implements Cloneable, Serializable {
		private static final long serialVersionUID = 5883759635406887685L;
		public static final String TAG = CardConfiguration.class.getSimpleName();
		@SerializedName("byte_order")
		int byte_order;
		@SerializedName("use_wiegand_format")
		boolean use_wiegand_format;
		@SerializedName("primary_key")
		String primary_key;
		@SerializedName("secondary_key")
		String secondary_key;
		@SerializedName("use_secondary_key")
		boolean use_secondary_key;
		@SerializedName("start_block_index")
		int start_block_index;
		@SerializedName("app_id")
		int app_id;
		@SerializedName("file_id")
		int file_id;
		@SerializedName("field_start")
		private ArrayList<Integer> field_start;
		@SerializedName("field_end")
		private ArrayList<Integer> field_end;
		@SerializedName("data_type")
		int data_type;

		@SuppressWarnings("unchecked")
		public CardConfiguration clone() throws CloneNotSupportedException {
			CardConfiguration target = (CardConfiguration) super.clone();
			if (field_start != null) {
				target.field_start = (ArrayList<Integer>) field_start.clone();
			}
			if (field_end != null) {
				target.field_end = (ArrayList<Integer>) field_end.clone();
			}
			return target;
		}
	}

	public static class Fingerprint implements Cloneable, Serializable {
		private static final long serialVersionUID = -1690483033898016711L;
		public static final String TAG = Fingerprint.class.getSimpleName();
		@SerializedName("security_level")
		int security_level;
		@SerializedName("fast_mode")
		int fast_mode;
		@SerializedName("sensitivity")
		int sensitivity;
		@SerializedName("show_image")
		boolean show_image;
		@SerializedName("scan_timeout")
		int scan_timeout;
		@SerializedName("detect_afterimage")
		boolean detect_afterimage;
		@SerializedName("template_format")
		int template_format;

		public Fingerprint clone() throws CloneNotSupportedException {
			Fingerprint target = (Fingerprint) super.clone();
			return target;
		}
	}

	public static class FingerprintVerify implements Cloneable, Serializable {
		private static final long serialVersionUID = -7857943932763810738L;
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;
		@SerializedName("verify_result")
		public boolean verify_result ;
		public FingerprintVerify clone() throws CloneNotSupportedException {
			FingerprintVerify target = (FingerprintVerify) super.clone();
			return target;
		}
	}
	public static class DeviceResponse implements Cloneable, Serializable {
		private static final long serialVersionUID = 6920422907130829979L;
		@SerializedName("result")
		public boolean result;
		public DeviceResponse clone() throws CloneNotSupportedException {
			DeviceResponse target = (DeviceResponse) super.clone();
			return target;
		}
	}

}
