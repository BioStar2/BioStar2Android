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

public class FingerPrintData {
	
	public static class BaseFingerprintTemplate implements Cloneable, Serializable {
		private static final long serialVersionUID = 7137656508335021809L;
		public static final String TAG = BaseFingerprintTemplate.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;
				
		public BaseFingerprintTemplate() {

		}

		public BaseFingerprintTemplate clone() throws CloneNotSupportedException {
			BaseFingerprintTemplate target = (BaseFingerprintTemplate) super.clone();
			return target;
		}
	}
		
	public static class ListFingerprintTemplate extends BaseFingerprintTemplate implements Cloneable, Serializable {
		private static final long serialVersionUID = -6798684884348157526L;
		public static final String TAG = ListFingerprintTemplate.class.getSimpleName();
		@SerializedName("is_prepare_for_duress")
		public boolean is_prepare_for_duress;
		@SerializedName("template0")
		public String template0;
		@SerializedName("template1")
		public String template1;
		public ListFingerprintTemplate() {

		}
		
		public ListFingerprintTemplate(boolean duress, String template0, String template1) {
			is_prepare_for_duress=duress;
			this.template0 = template0;
			this.template1 = template1;
		}
		public ListFingerprintTemplate clone() throws CloneNotSupportedException {
			return (ListFingerprintTemplate) super.clone();
		}
	}
	
	public static class ScanFingerprintTemplate extends BaseFingerprintTemplate implements Cloneable, Serializable {
		private static final long serialVersionUID = -4788003349058297507L;
		public static final String TAG = ScanFingerprintTemplate.class.getSimpleName();
		@SerializedName("finger_index")
		public int finger_index;
		@SerializedName("finger_mask")
		public boolean finger_mask;
		@SerializedName("template0")
		public String template0;
		@SerializedName("template1")
		public String template1;
		@SerializedName("enroll_quality")
		public String enroll_quality;
		@SerializedName("template_image0")
		public String template_image0;
		@SerializedName("template_image1")
		public String template_image1;
		@SerializedName("raw_image0")
		public String raw_image0;
		@SerializedName("raw_image1")
		public String raw_image1;

		public ScanFingerprintTemplate clone() throws CloneNotSupportedException {
			return (ScanFingerprintTemplate) super.clone();
		}
	}

	public static class VerifyFingerprintOption implements Cloneable, Serializable {
		private static final long serialVersionUID = 867494139108960788L;
		@SerializedName("security_level")
		public String security_level;
		@SerializedName("template0")
		public String template0;
		@SerializedName("template1")
		public String template1;

		public VerifyFingerprintOption() {

		}
		public VerifyFingerprintOption(String security_level,String template0, String template1) {
			this.security_level = security_level;
			this.template0 = template0;
			this.template1 = template1;
		}

		public VerifyFingerprintOption clone() throws CloneNotSupportedException {
			return (VerifyFingerprintOption) super.clone();
		}
	}
}
