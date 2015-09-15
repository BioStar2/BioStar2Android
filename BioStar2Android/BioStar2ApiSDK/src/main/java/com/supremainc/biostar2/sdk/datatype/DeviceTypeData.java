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
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;

import java.io.Serializable;
import java.util.ArrayList;

public class DeviceTypeData {
	public static final int SUPPORT_DISPLAY = 0;
	public static final int SUPPORT_BLACKFIN = 1;
	public static final int SUPPORT_FINGERPRINT = 2;
	public static final int SUPPORT_CARD = 4;
	public static final int SUPPORT_CARD_ONLY = 8;
	public static final int SUPPORT_KEYPAD = 16;
	public static final int SUPPORT_VOLUME = 32;
	public static final int SUPPORT_ONLY_SLAVE = 64;

	public static class DeviceTypes implements Cloneable, Serializable {
		private static final long serialVersionUID = 8024785816132632948L;
		public static final String TAG = DeviceTypes.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;
		@SerializedName("records")
		public ArrayList<DeviceType> records;
		@SerializedName("total")
		public int total;

		public DeviceTypes() {

		}

		public DeviceTypes(ArrayList<DeviceType> rows, int total) {
			this.total = total;
			this.records = rows;
		}

		public DeviceTypes(ArrayList<DeviceType> rows) {
			if (rows != null) {
				total = rows.size();
			}
			this.records = rows;
		}

		@SuppressWarnings("unchecked")
		public DeviceTypes clone() throws CloneNotSupportedException {
			DeviceTypes target = (DeviceTypes) super.clone();
			if (records != null) {
				target.records = (ArrayList<DeviceType>) records.clone();
			}
			return target;
		}

	}

	public static class BaseDeviceType implements Cloneable, Serializable {
		private static final long serialVersionUID = 2005600564359187213L;
		public static final String TAG = BaseDeviceType.class.getSimpleName();
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

		public BaseDeviceType() {

		}

		public BaseDeviceType clone() throws CloneNotSupportedException {
			BaseDeviceType target = (BaseDeviceType) super.clone();
			return target;
		}
	}

	public static class ListDeviceType extends BaseDeviceType implements Cloneable, Serializable {
		private static final long serialVersionUID = 2292389938409217747L;
		@SerializedName("input_port_num")
		public long input_port_num;
		@SerializedName("relay_num")
		public long relay_num;
		@SerializedName("type")
		public int type;
		@SerializedName("scan_card")
		public boolean scan_card;
		@SerializedName("scan_fingerprint")
		public boolean scan_fingerprint;

		public ListDeviceType clone() throws CloneNotSupportedException {
			ListDeviceType target = (ListDeviceType) super.clone();
			return target;
		}

		public boolean isSupport(int deviceSupport) {
			if (ConfigDataProvider.TEST_RELEASE_DELETE) {
				int support = 0;
				int numberID = Integer.valueOf(id);
				switch (numberID) {
					case 1 :
					case 2 :
						support = DeviceTypeData.SUPPORT_BLACKFIN | DeviceTypeData.SUPPORT_FINGERPRINT | DeviceTypeData.SUPPORT_CARD;
						break;
					case 3 :
						support = DeviceTypeData.SUPPORT_DISPLAY | DeviceTypeData.SUPPORT_BLACKFIN | DeviceTypeData.SUPPORT_FINGERPRINT | DeviceTypeData.SUPPORT_CARD | DeviceTypeData.SUPPORT_KEYPAD;
						break;
					case 4 :
					case 5 :
						support = DeviceTypeData.SUPPORT_BLACKFIN | DeviceTypeData.SUPPORT_CARD | DeviceTypeData.SUPPORT_CARD_ONLY;
						break;
					case 6 :
					case 7 :
						support = DeviceTypeData.SUPPORT_ONLY_SLAVE;
						break;
					case 8 :
					case 9 :
					case 10 :
						support = DeviceTypeData.SUPPORT_DISPLAY | DeviceTypeData.SUPPORT_FINGERPRINT | DeviceTypeData.SUPPORT_CARD | DeviceTypeData.SUPPORT_KEYPAD | DeviceTypeData.SUPPORT_VOLUME;
						break;
					default:
						break;
				}
				if (scan_card) {
					support = support | DeviceTypeData.SUPPORT_CARD;
				}
				if (scan_fingerprint) {
					support = support | DeviceTypeData.SUPPORT_FINGERPRINT;
				}
				int result = support & deviceSupport;
				if (result == deviceSupport) {
					return true;
				} else {
					return false;
				}
			} else {
				int result = type & deviceSupport;
				if (result == deviceSupport) {
					return true;
				} else {
					return false;
				}
			}
			
		}
	}

	public static class DeviceType extends ListDeviceType implements Cloneable, Serializable {
		private static final long serialVersionUID = 3103879226461355384L;
		public static final String TAG = DeviceType.class.getSimpleName();

		// @SerializedName("max_connection")
		// public int max_connection;
		// @SerializedName("support_master")
		// public boolean support_master;
		// @SerializedName("support_slave")
		// public boolean support_slave;
		// @SerializedName("input_port_num")
		// public int input_port_num;
		// @SerializedName("output_port_num")
		// public int output_port_num ;
		// @SerializedName("relay_num")
		// public int relay_num ;
		// @SerializedName("support_tamper")
		// public boolean support_tamper ;
		// @SerializedName("tna_key_num")
		// public int tna_key_num;
		// @SerializedName("tna_extra_key_num")
		// public int tna_extra_key_num ;
		// @SerializedName("rs485_channel_num")
		// public int rs485_channel_num;
		// @SerializedName("wiegand_channel_num")
		// public int wiegand_channel_num;
		public DeviceType() {

		}
	}

	// TODO receive server
	public enum DEVICE_TYPE {
		DEFAULT(0, false, false, false, false, false, false, false, false), BIO_ENTRY_PLUS(1, false, true, true, true, false, false, false, false), BIO_ENTRY_W(2, false, true, true, true, false,
				false, false, false), BIO_LITE_NET(3, true, true, true, true, false, true, false, false), X_PASS(4, false, true, false, true, true, false, false, false), X_PASS_S2(5, false, true,
				false, true, true, false, false, false), SECURE_IO_2(6, false, false, false, false, false, false, false, true), DOOR_MODULE_20(7, false, false, false, false, false, false, false, true), BIO_STATION_2(
				8, true, false, true, true, false, true, true, false), BIO_STATION_T2(9, true, false, true, true, false, true, true, false), FACE_STATION_2(10, true, false, true, true, false, true,
				true, false);
		public final int mId;
		public final boolean mDisplay;
		public final boolean mBlackfin;
		public final boolean mFingerPrint;
		public final boolean mCard;
		public final boolean mCardOnly;
		public final boolean mKeypad;
		public final boolean mVolume;
		public final boolean mOnlySlave;
		private DEVICE_TYPE(int id, boolean display, boolean blackfin, boolean fingerPrint, boolean card, boolean cardOnly, boolean keypad, boolean volume, boolean onlySlave) {
			this.mId = id;
			this.mDisplay = display;
			this.mBlackfin = blackfin;
			this.mFingerPrint = fingerPrint;
			this.mCard = card;
			this.mCardOnly = cardOnly;
			this.mKeypad = keypad;
			this.mVolume = volume;
			this.mOnlySlave = onlySlave;
		}
	};

}
