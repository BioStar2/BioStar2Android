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
package com.supremainc.biostar2.sdk.datatype.v2.AccessControl;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

public class ScheduleCollection implements Cloneable, Serializable {
	public static final String TAG = ScheduleCollection.class.getSimpleName();
	private static final long serialVersionUID = 7044476609933899402L;

	@SerializedName("total")
	public int total;

	@SerializedName("rows")
	public ArrayList<Schedule> rows;

	public ScheduleCollection(ArrayList<Schedule> rows) {
		if (rows != null) {
			total = rows.size();
		}
		this.rows = rows;
	}

	@SuppressWarnings("unchecked")
	public ScheduleCollection clone() throws CloneNotSupportedException {
		ScheduleCollection target = (ScheduleCollection) super.clone();
		if (rows != null) {
			target.rows = (ArrayList<Schedule>) rows.clone();
		}
		return target;
	}
}