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

import android.os.Build;
import android.provider.Settings;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TAPunchData {

	private static final String CHECK_IN = "CHECK_IN";
	private static final String CHECK_OUT = "CHECK_OUT";
	private static final String BREAK_START = "BREAK_START";
	private static final String BREAK_END = "BREAK_END";
	private static final String MEAL_START = "MEAL_START";
	private static final String MEAL_END = "MEAL_END";
	public enum PunchType {CHECK_IN,CHECK_OUT,BREAK_START,BREAK_END,MEAL_START,MEAL_END};

	public static class ParamPunch implements Cloneable, Serializable {
		public static final String TAG = ParamPunch.class.getSimpleName();
		private static final long serialVersionUID = -7408033502168328256L;
		@SerializedName("mobile_imei")
		public String mobile_imei;
		@SerializedName("mobile_phone")
		public String mobile_phone;
		/**
		 * CHECK_IN' or 'CHECK_OUT' or 'BREAK_START' or 'BREAK_END' or 'MEAL_START' or 'MEAL_END'
		 */
		@SerializedName("type")
		public String type;

		public ParamPunch() {
			//TODO unique ID
			mobile_imei = Settings.Secure.ANDROID_ID+ Build.SERIAL;
			mobile_phone = "01025334246";
		}

		public void setType(PunchType type) {
			switch (type) {
				case CHECK_IN:
					this.type = CHECK_IN;
					break;
				case CHECK_OUT:
					this.type = CHECK_OUT;
					break;
				case BREAK_START:
					this.type = BREAK_START;
					break;
				case BREAK_END:
					this.type = BREAK_END;
					break;
				case MEAL_START:
					this.type = MEAL_START;
					break;
				case MEAL_END:
					this.type = MEAL_END;
					break;
			}
		}

		public ParamPunch clone() throws CloneNotSupportedException {
			ParamPunch target = (ParamPunch) super.clone();
			return target;
		}
	}
}
