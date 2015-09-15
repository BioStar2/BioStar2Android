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

public class UserGroupData {
	public static class UserGroups implements Cloneable, Serializable {
		private static final long serialVersionUID = 6490802004340541794L;
		@SerializedName("records")
		public ArrayList<UserGroup> records;
		@SerializedName("total")
		public int total;
				
		public UserGroups() {
			
		}
		
		public UserGroups(ArrayList<UserGroup> rows,int total) {
			this.total = total;
			this.records = rows;
		}
		
		public UserGroups(ArrayList<UserGroup> rows) {
			if (rows != null) {
				total = rows.size();
			}
			this.records = rows;
		}
		
		@SuppressWarnings("unchecked")
		public UserGroups clone() throws CloneNotSupportedException {
			UserGroups target = (UserGroups) super.clone();
			if (records != null) {
				target.records = (ArrayList<UserGroup>) records.clone();
			}		
			return target;
		}
	}
	
	public static class BaseUserGroup implements Cloneable, Serializable {
		private static final long serialVersionUID = -7153641538447329685L;
		public static final String TAG = BaseUserGroup.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;
		
		@SerializedName("id")
		public String id;
		@SerializedName("name")
		public String name;
		
		public BaseUserGroup() {

		}
		
		public BaseUserGroup(String id,String name) {
			this.id = id;
			this.name = name;
		}

		public BaseUserGroup clone() throws CloneNotSupportedException {
			BaseUserGroup target = (BaseUserGroup) super.clone();
			return target;
		}

		public void setDefaultValue() {
			this.id = "1";			
		}
	}
		
	public static class ListUserGroup extends BaseUserGroup implements Cloneable, Serializable {
		private static final long serialVersionUID = -3120651381332246807L;
		public  static final String TAG = BaseUserGroup.class.getSimpleName();
		
		public ListUserGroup() {

		}

		public ListUserGroup clone() throws CloneNotSupportedException {
			ListUserGroup target = (ListUserGroup) super.clone();
			return target;
		}
	}
	
	public static class UserGroup extends ListUserGroup implements Cloneable, Serializable {
		private static final long serialVersionUID = 2777183888876165497L;

		@SerializedName("user_total")
		public int user_total;
		@SerializedName("user_total_including_sub_groups")
		public int user_total_including_sub_groups;

		@SerializedName("parent")
		public UserGroup parent;
			
		public UserGroup() {

		}
		
		public UserGroup(String name,String id) {
			this.id = id;
			this.name = name;
		}
		
		public UserGroup clone() throws CloneNotSupportedException {
			UserGroup target = (UserGroup) super.clone();
			if (parent != null) {
				target.parent = (UserGroup) parent.clone();
			}		
			return target;
		}
		
	}
}
