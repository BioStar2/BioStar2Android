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

public class PermissionData {
	public static final String ROLE_READ = "read";
	public static final String ROLE_WRITE = "write";

	public enum PERMISSION_MODULE {
		ACCESS_GROUP("ACCESS_GROUP"), ACCESS_LEVEL("ACCESS_LEVEL"), ACCOUNT("ACCOUNT"), ADMIN("ADMIN"), CARD("CARD"), DEVICE("DEVICE"), DEVICE_GROUP("DEVICE_GROUP"), DOOR("DOOR"), DOOR_GROUP(
				"DOOR_GROUP"), HOLIDAY("HOLIDAY"), MONITORING("MONITORING"), PRIVILEGE("PRIVILEGE"), SCHEDULE("SCHEDULE"), SETTING_PREFERENCE("SETTING_PREFERENCE"), USER("USER"), USER_GROUP(
				"USER_GROUP");
		public final String mName;

		private PERMISSION_MODULE(String name) {
			this.mName = name;
		}
	};


	public static class CloudRoles implements Cloneable, Serializable {
		private static final long serialVersionUID = -2668049315735043183L;
		public static final String TAG = CloudRoles.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;
		@SerializedName("records")
		public ArrayList<CloudRole> records;
		@SerializedName("total")
		public int total;

		public CloudRoles() {

		}

		public CloudRoles(ArrayList<CloudRole> rows, int total) {
			this.total = total;
			this.records = rows;
		}

		public CloudRoles(ArrayList<CloudRole> rows) {
			if (rows != null) {
				total = rows.size();
			}
			this.records = rows;
		}

		@SuppressWarnings("unchecked")
		public CloudRoles clone() throws CloneNotSupportedException {
			CloudRoles target = (CloudRoles) super.clone();
			if (records != null) {
				target.records = (ArrayList<CloudRole>) records.clone();
			}
			return target;
		}
	}

	public static class CloudRole implements Cloneable, Serializable {
		private static final long serialVersionUID = -6019806141137396620L;
		/**
		 * ( 'ADMIN', 'USER_ADMIN', 'MONITORING' ,'DEFAULT_USER'),
		 */
		@SerializedName("code")
		public String code;
		@SerializedName("description")					 
		public String description;		 

		public CloudRole clone() throws CloneNotSupportedException {
			CloudRole target = (CloudRole) super.clone();
			return target;
		}
	}

	public static class CloudPermission implements Cloneable, Serializable {
		private static final long serialVersionUID = -1180074002054418860L;
		@SerializedName("module")
		public String module;
		@SerializedName("read")
		public boolean read;
		@SerializedName("write")
		public boolean write;
		@SerializedName("url")
		public String url;

		public CloudPermission clone() throws CloneNotSupportedException {
			return (CloudPermission) super.clone();
		}
	}

}
