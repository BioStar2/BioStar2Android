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
package com.supremainc.biostar2.sdk.datatype.v2.Device;

import com.google.gson.annotations.SerializedName;
import com.supremainc.biostar2.sdk.datatype.v2.Common.SimpleData;

import java.io.Serializable;

public class  BaseDevice implements Cloneable, Serializable {
    public static final String TAG = BaseDevice.class.getSimpleName();
    private static final long serialVersionUID = 5074124575377999655L;
    @SerializedName("status_code")
    public String statusCode;
    @SerializedName("message")
    public String message;

    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("device_group")
    public SimpleData device_group;
    @SerializedName("device_type")
    public DeviceType device_type;

    public static final String SCAN_FINGERPRINT_ENROLL_QUALITY = "enroll_quality";
    public static final String SCAN_FINGERPRINT_GET_IMAGE = "retrieve_raw_image";
    public static final String SCAN_QUALITY_IS_LOW = "SCAN_QUALITY_IS_LOW";
    public BaseDevice() {

    }

    public BaseDevice(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        if (name == null || name.isEmpty()) {
            return String.valueOf(id);
        }
        return name;
    }

    public BaseDevice clone() throws CloneNotSupportedException {
        BaseDevice target = (BaseDevice) super.clone();
        if (device_group != null) {
            target.device_group =  device_group.clone();
        }
        if (device_type != null) {
            target.device_type =  device_type.clone();
        }
        return target;
    }
}