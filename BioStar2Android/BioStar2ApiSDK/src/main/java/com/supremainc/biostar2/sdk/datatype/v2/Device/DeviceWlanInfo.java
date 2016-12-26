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

import java.io.Serializable;

public class DeviceWlanInfo implements Cloneable, Serializable {
    public static final String TAG = DeviceWlanInfo.class.getSimpleName();
    private static final long serialVersionUID = -6866383826353376929L;
    @SerializedName("enabled")
    public boolean enabled;
    @SerializedName("wireless_mode")
    public String wireless_mode;
    @SerializedName("security_mode")
    public String security_mode;
    @SerializedName("wpa_algorithm")
    public String wpa_algorithm;

    public DeviceWlanInfo() {

    }

    public DeviceWlanInfo clone() throws CloneNotSupportedException {
        DeviceWlanInfo target = (DeviceWlanInfo) super.clone();
        return target;
    }
}