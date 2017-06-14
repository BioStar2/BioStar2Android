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
package com.supremainc.biostar2.sdk.models.v2.device;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class  Fingerprint implements Cloneable, Serializable {
    public static final String TAG = Fingerprint.class.getSimpleName();
    private static final long serialVersionUID = -1690483033898016711L;
    @SerializedName("security_level")
    int security_level;
    @SerializedName("fast_mode")
    int fast_mode;
    @SerializedName("sensitivity")
    int sensitivity;
    @SerializedName("show_image")
    boolean show_image;
    @SerializedName("scan_timeout")
    int scan_timeout;
    @SerializedName("detect_afterimage")
    boolean detect_afterimage;
    @SerializedName("template_format")
    int template_format;

    public Fingerprint() {

    }

    public Fingerprint clone() throws CloneNotSupportedException {
        Fingerprint target = (Fingerprint) super.clone();
        return target;
    }
}