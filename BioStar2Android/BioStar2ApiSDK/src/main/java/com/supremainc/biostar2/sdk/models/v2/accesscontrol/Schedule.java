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
package com.supremainc.biostar2.sdk.models.v2.accesscontrol;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

public class Schedule implements Cloneable, Serializable {
	private static final long serialVersionUID = -1771577948813968340L;
	public static final String TAG = Schedule.class.getSimpleName();
	@SerializedName("id")
	public String id;
	@SerializedName("name")
	public String name;
	@SerializedName("description")
	public String description;
	@SerializedName("use_daily_iteration")
	public boolean use_daily_iteration;
	@SerializedName("days_of_iteration")
	public String days_of_iteration;
	@SerializedName("start_date")
	private String start_date;
	@SerializedName("daily_schedules")
	public ArrayList<DailySchedules> daily_schedules;

	public Schedule() {

	}

	@SuppressWarnings("unchecked")
	public Schedule clone() throws CloneNotSupportedException {
		Schedule target = (Schedule) super.clone();
		if (daily_schedules != null) {
			target.daily_schedules = (ArrayList<DailySchedules>) daily_schedules.clone();
		}
		return target;
	}
}
