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
import com.supremainc.biostar2.sdk.datatype.DoorData.BaseDoor;
import com.supremainc.biostar2.sdk.datatype.ScheduleData.Schedule;

import java.io.Serializable;
import java.util.ArrayList;

public class AccessLevelData {

	public static class AccessLevels  implements Cloneable, Serializable {
		private static final long serialVersionUID = -8589464130098194496L;
		public static final String TAG = AccessLevels.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;
		@SerializedName("records")
		public ArrayList<ListAccessLevel> records;
		@SerializedName("total")
		public int total;
		
		public AccessLevels() {
			
		}
				
		public AccessLevels(ArrayList<ListAccessLevel> rows,int total) {
			this.total = total;
			this.records = rows;
		}
		
		public AccessLevels(ArrayList<ListAccessLevel> rows) {
			if (rows != null) {
				total = rows.size();
			}
			this.records = rows;
		}
		
		@SuppressWarnings("unchecked")
		public AccessLevels clone() throws CloneNotSupportedException {
			AccessLevels target = (AccessLevels) super.clone();
			if (records != null) {
				target.records = (ArrayList<ListAccessLevel>) records.clone();
			}		
			return target;
		}
	}
	
	public static class BaseAccessLevel implements Cloneable, Serializable {
		private static final long serialVersionUID = 976825472383889913L;
		public static final String TAG = BaseAccessLevel.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;
		
		@SerializedName("id")
		public String id;
		@SerializedName("name")
		public String name;
		@SerializedName("door_description")
		public String door_description;			
		@SerializedName("schedule_description")
		public String schedule_description;		
		
		public BaseAccessLevel() {

		}

		public BaseAccessLevel clone() throws CloneNotSupportedException {
			BaseAccessLevel target = (BaseAccessLevel) super.clone();
			return target;
		}
	}
	
	public static class ListAccessLevel extends BaseAccessLevel implements Cloneable, Serializable {
		private static final long serialVersionUID = -5813512396116483134L;
		public static final String TAG = ListAccessLevel.class.getSimpleName();
	
		public ListAccessLevel() {

		}

		public ListAccessLevel clone() throws CloneNotSupportedException {
			ListAccessLevel target = (ListAccessLevel) super.clone();
			return target;
		}
	}

	public static class AccessLevel extends ListAccessLevel implements Cloneable, Serializable {
		private static final long serialVersionUID = -1976836178525638767L;
		public static final String TAG = AccessLevel.class.getSimpleName();
		@SerializedName("description")
		public String description;			
		@SerializedName("items")
		public ArrayList<AccessLevelItem> items;

		public AccessLevel() {

		}

		@SuppressWarnings("unchecked")
		public AccessLevel clone() throws CloneNotSupportedException {
			AccessLevel target = (AccessLevel) super.clone();
			if (items != null) {
				target.items = (ArrayList<AccessLevelItem>) items
						.clone();
			}
			return target;
		}
	}
	
	
	public static class AccessLevelItem implements Cloneable, Serializable {
		private static final long serialVersionUID = -8570778891470179002L;
		public static final String TAG = AccessLevelItem.class.getSimpleName();
		@SerializedName("id")
		public String id;
		@SerializedName("door_list")
		public ArrayList<BaseDoor> doors;
		@SerializedName("schedule_id")
		public Schedule schedule_id;
		
		@SuppressWarnings("unchecked")
		public AccessLevelItem clone() throws CloneNotSupportedException {
			AccessLevelItem target = (AccessLevelItem) super.clone();
			if (doors != null) {
				target.doors = (ArrayList<BaseDoor>) doors
						.clone();
			}
			if (schedule_id != null) {
				target.schedule_id = (Schedule) schedule_id
						.clone();
			}
			return target;
		}		
	}
}
