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
package com.supremainc.biostar2.sdk.models.v2.fingerprint;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ScanFingerprintTemplate extends BaseFingerprintTemplate implements Cloneable, Serializable {
    public static final String TAG = ScanFingerprintTemplate.class.getSimpleName();
    private static final long serialVersionUID = -4788003349058297507L;
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