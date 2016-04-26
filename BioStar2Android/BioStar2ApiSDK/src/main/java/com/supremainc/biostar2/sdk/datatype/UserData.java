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
import com.supremainc.biostar2.sdk.datatype.CardData.ListCard;
import com.supremainc.biostar2.sdk.datatype.FingerPrintData.ListFingerprintTemplate;
import com.supremainc.biostar2.sdk.datatype.PermissionData.CloudPermission;
import com.supremainc.biostar2.sdk.datatype.PermissionData.CloudRole;
import com.supremainc.biostar2.sdk.datatype.UserGroupData.BaseUserGroup;
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class UserData {
	public static final String USER_STATUS_ACTIVE = "AC";
	public static final String USER_STATUS_INACTIVE = "IN";
	public static class Users implements Cloneable, Serializable {
		private static final long serialVersionUID = -3991391564591145362L;
		public static final String TAG = Users.class.getSimpleName();
		@SerializedName("records")
		public ArrayList<ListUser> records;
		@SerializedName("total")
		public int total;

		public Users() {

		}

		public Users(ArrayList<ListUser> rows, int total) {
			this.total = total;
			this.records = rows;
		}

		public Users(ArrayList<ListUser> rows) {
			if (rows != null) {
				total = rows.size();
			}
			this.records = rows;
		}

		@SuppressWarnings("unchecked")
		public Users clone() throws CloneNotSupportedException {
			Users target = (Users) super.clone();
			if (records != null) {
				target.records = (ArrayList<ListUser>) records.clone();
			}
			return target;
		}
	}

	public static class BaseUser implements Cloneable, Serializable {
		private static final long serialVersionUID = 5513138779390869308L;
		public static final String TAG = BaseAccessGroup.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;

		@SerializedName("user_id")
		public String user_id;
		@SerializedName("name")
		public String name;

		public BaseUser() {

		}

		public String getName() {
			if (name == null || name.isEmpty()) {
				return String.valueOf(user_id);
			}
			return name;
		}

		public BaseUser clone() throws CloneNotSupportedException {
			BaseUser target = (BaseUser) super.clone();
			return target;
		}
	}

	public static class SimpleUserWithGroup extends BaseUser implements Cloneable, Serializable {
		public static final String TAG = SimpleUserWithGroup.class.getSimpleName();
		private static final long serialVersionUID = -1401327780733660839L;

		@SerializedName("user_group")
		public BaseUserGroup user_group;

		public SimpleUserWithGroup() {

		}

		public SimpleUserWithGroup clone() throws CloneNotSupportedException {
			SimpleUserWithGroup target = (SimpleUserWithGroup) super.clone();
			return target;
		}
	}

	public static class ListUser extends BaseUser implements Cloneable, Serializable {
		private static final long serialVersionUID = 4761231297419148580L;
		public static final String TAG = ListUser.class.getSimpleName();

		@SerializedName("email")
		public String email;
		@SerializedName("user_group")
		public BaseUserGroup user_group;
		@SerializedName("access_groups")
		public ArrayList<ListAccessGroup> access_groups;
		@SerializedName("fingerprint_count")
		public int fingerprint_count;
		@SerializedName("card_count")
		public int card_count;
		@SerializedName("pin_exist")
		public boolean pin_exist;
		@SerializedName("photo_exist")
		public boolean photo_exist;
		@SerializedName("last_modify")
		public String last_modify;


		public ListUser() {

		}

		public ListUser clone() throws CloneNotSupportedException {
			ListUser target = (ListUser) super.clone();
			return target;
		}
	}
	
	public static class Photo implements Cloneable, Serializable {
		private static final long serialVersionUID = -8318807862090613877L;
		public static final String TAG = Photo.class.getSimpleName();
		@SerializedName("photo")
		public String photo;

		public Photo() {

		}

		public Photo clone() throws CloneNotSupportedException {
			Photo target = (Photo) super.clone();
			return target;
		}
	}

	public static class User extends ListUser implements Cloneable, Serializable {
		private static final long serialVersionUID = 8386218558305783948L;
		public static final String TAG = User.class.getSimpleName();
		public enum UserTimeType {start_datetime,expiry_datetime};
		@SerializedName("password")
		public String password;

		@SerializedName("security_level")
		public String security_level = "0";

		@SerializedName("pin")
		public String pin;

		@SerializedName("start_datetime")
		private String start_datetime;

		@SerializedName("expiry_datetime")
		private String expiry_datetime;

		@SerializedName("roles")
		public ArrayList<CloudRole> roles;

		@SerializedName("photo")
		public String photo;

		@SerializedName("login_id")
		public String login_id;
		/**
		 * ['0 = none' or '1 = male' or '2 = female']
		 */

		@SerializedName("phone_number")
		public String phone_number;
		/**
		 * ( 'AC' = Active, 'IN' = Inactive ),
		 */
		@SerializedName("status")
		public String status;

		@SerializedName("fingerprint_templates")
		public ArrayList<ListFingerprintTemplate> fingerprint_templates;

		@SerializedName("cards")
		public ArrayList<ListCard> cards;

		@SerializedName("password_exist")
		public boolean password_exist;
		
		/**
		 * only login,myprofile
		 */
		@SerializedName("permissions")
		public ArrayList<CloudPermission> permissions;

		@SerializedName("password_strength_level")
		public String password_strength_level;
		/*
		 * @SerializedName("date") public String date; public String finger;
		 * public String card;
		 */
		public User() {

		}

		public void setDefaultValue() {
			if (ConfigDataProvider.TEST_RELEASE_DELETE) {
				user_group = new BaseUserGroup();
				user_group.setDefaultValue();
			}
			setActive(true);
			roles = new ArrayList<CloudRole>();
			CloudRole role = new  CloudRole();
			role.code = "DEFAULT_USER";
		}

		public boolean isActive() {
			if (status == null) {
				return false;
			}
			if (status.equals(USER_STATUS_ACTIVE)) {
				return true;
			}
			return false;
		}

		public void setActive(boolean enable) {
			if (enable) {
				status = USER_STATUS_ACTIVE;
				return;
			}
			status = USER_STATUS_INACTIVE;
		}

		public ArrayList<ListFingerprintTemplate> getFingerprintTemplates() {
			if (fingerprint_templates != null) {
				return fingerprint_templates;
			}
			return null;
		}

		public void setFingerprintTemplates(ArrayList<ListFingerprintTemplate> fingerprint_templates) {
			this.fingerprint_templates = fingerprint_templates;
		}

		public String getTimeType(UserTimeType type) {
			String src= null;
			switch (type) {
				case start_datetime:
					src = start_datetime;
					break;
				case expiry_datetime:
					src = expiry_datetime;
					break;
				default:
					break;
			}
			return src;
		}

		public boolean setTimeType(UserTimeType type,String src) {
			if (src == null || src.isEmpty()) {
				return false;
			}
			switch (type) {
				case start_datetime:
					start_datetime = src;
					break;
				case expiry_datetime:
					expiry_datetime = src;
					break;
				default:
					return false;
			}
			return true;
		}

		public Calendar getTimeCalendar(TimeConvertProvider convert,UserTimeType timeType) {
			return convert.convertServerTimeToCalendar(getTimeType(timeType), false);
		}

		public boolean setTimeCalendar(TimeConvertProvider convert,UserTimeType timeType,Calendar cal) {
			switch (timeType) {
				case expiry_datetime:
					cal.set(Calendar.HOUR_OF_DAY,23);
					cal.set(Calendar.MINUTE,59);
					break;
				default:
					break;
			}
			return setTimeType(timeType, convert.convertCalendarToServerTime(cal, false));
		}

		public String getTimeFormmat(TimeConvertProvider convert,UserTimeType timeType,TimeConvertProvider.DATE_TYPE type) {
			Calendar cal = getTimeCalendar(convert, timeType);
			return convert.convertCalendarToFormatter(cal, type);
		}

		public boolean setTimeFormmat(TimeConvertProvider convert,UserTimeType timeType,TimeConvertProvider.DATE_TYPE type,String src) {
			Calendar cal = convert.convertFormatterToCalendar(src,type);
			return setTimeCalendar(convert,timeType,cal);
		}


		@SuppressWarnings("unchecked")
		public User clone() throws CloneNotSupportedException {
			User target = (User) super.clone();

			if (fingerprint_templates != null) {
				target.fingerprint_templates = (ArrayList<ListFingerprintTemplate>) fingerprint_templates.clone();
			}
			if (access_groups != null) {
				target.access_groups = (ArrayList<ListAccessGroup>) access_groups.clone();
			}
			if (user_group != null) {
				target.user_group = (BaseUserGroup) user_group.clone();
			}
			if (cards != null) {
				cards = (ArrayList<ListCard>) cards.clone();
			}
			if (roles != null) {
				target.roles = (ArrayList<CloudRole>) roles.clone();
			}
			if (permissions != null) {
				target.permissions = (ArrayList<CloudPermission>) permissions.clone();
			}
			return target;
		}
	}


}
