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
import com.supremainc.biostar2.sdk.datatype.AccessLevelData.BaseAccessLevel;
import com.supremainc.biostar2.sdk.datatype.UserData.ListUser;
import com.supremainc.biostar2.sdk.datatype.UserGroupData.UserGroup;

import java.io.Serializable;
import java.util.ArrayList;

public class AccessGroupData {
	public static final String INCLUDEED_YES = "YES";
	public static final String INCLUDEED_NO = "NO";
	public static final String INCLUDEED_BOTH = "BOTH";
	public static class AccessGroups implements Cloneable, Serializable {
		private static final long serialVersionUID = 7402431015729884663L;
		public static final String TAG = AccessGroups.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;
		@SerializedName("records")
		public ArrayList<ListAccessGroup> records;
		@SerializedName("total")
		public int total;

		public AccessGroups() {

		}

		public AccessGroups(ArrayList<ListAccessGroup> rows, int total) {
			this.total = total;
			this.records = rows;
		}

		public AccessGroups(ArrayList<ListAccessGroup> rows) {
			if (rows != null) {
				total = rows.size();
			}
			this.records = rows;
		}

		@SuppressWarnings("unchecked")
		public AccessGroups clone() throws CloneNotSupportedException {
			AccessGroups target = (AccessGroups) super.clone();
			if (records != null) {
				target.records = (ArrayList<ListAccessGroup>) records.clone();
			}
			return target;
		}
	}

	public static class BaseAccessGroup implements Cloneable, Serializable {
		private static final long serialVersionUID = -4934827676407718433L;
		public static final String TAG = BaseAccessGroup.class.getSimpleName();
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

		public BaseAccessGroup() {

		}

		public BaseAccessGroup clone() throws CloneNotSupportedException {
			BaseAccessGroup target = (BaseAccessGroup) super.clone();
			return target;
		}
	}

	public static class ListAccessGroup extends BaseAccessGroup implements Cloneable, Serializable {
		private static final long serialVersionUID = -749247019171641814L;
		public static final String TAG = ListAccessGroup.class.getSimpleName();
		/**
		 * only include User
		 */
		@SerializedName("included_by_user_group")
		public String included_by_user_group; //YES , NO , BOTH
		
		public ListAccessGroup() {

		}

		public boolean isIncludedByUserGroup() {
			if (INCLUDEED_BOTH.equals(included_by_user_group)) {
				return true;
			}
			if (INCLUDEED_YES.equals(included_by_user_group)) {
				return true;
			}
			return false;
		}

		public ListAccessGroup clone() throws CloneNotSupportedException {
			ListAccessGroup target = (ListAccessGroup) super.clone();
			return target;
		}
	}

	public static class AccessGroup extends ListAccessGroup implements Cloneable, Serializable {
		private static final long serialVersionUID = -6280293723151230572L;
		public static final String TAG = ListAccessGroup.class.getSimpleName();
		@SerializedName("users")
		public ArrayList<ListUser> users;
		@SerializedName("user_groups")
		public ArrayList<UserGroup> user_groups;
		@SerializedName("access_levels")
		public ArrayList<BaseAccessLevel> access_levels;

		public AccessGroup() {

		}

		@SuppressWarnings("unchecked")
		public AccessGroup clone() throws CloneNotSupportedException {
			AccessGroup target = (AccessGroup) super.clone();
			if (users != null) {
				target.users = (ArrayList<ListUser>) users.clone();
			}
			if (user_groups != null) {
				target.user_groups = (ArrayList<UserGroup>) user_groups.clone();
			}

			if (access_levels != null) {
				target.access_levels = (ArrayList<BaseAccessLevel>) access_levels.clone();
			}
			return target;
		}
	}
}
