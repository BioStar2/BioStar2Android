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
public class ScheduleContainer implements Cloneable, Serializable {
	public static final String TAG = ScheduleContainer.class.getSimpleName();
	private static final long serialVersionUID = 8747607329193354977L;

	@SerializedName("Schedule")
	public Schedule mSchedule;

	public ScheduleContainer(Schedule data) {
		mSchedule = data;
	}

	@SuppressWarnings("unchecked")
	public ScheduleContainer clone() throws CloneNotSupportedException {
		ScheduleContainer target = (ScheduleContainer) super.clone();
		if (mSchedule != null) {
			target.mSchedule = (Schedule) mSchedule.clone();
		}
		return target;
	}
}