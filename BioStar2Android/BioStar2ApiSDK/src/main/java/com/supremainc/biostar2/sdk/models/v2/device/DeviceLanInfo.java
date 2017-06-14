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

public class DeviceLanInfo implements Cloneable, Serializable {
    public static final String TAG = DeviceLanInfo.class.getSimpleName();
    private static final long serialVersionUID = -6866383826353376929L;
    @SerializedName("baseband")
    public String baseband;
    @SerializedName("connection_mode")
    public String connection_mode;
    @SerializedName("device_port")
    public String device_port;
    @SerializedName("enable_dhcp")
    public boolean enable_dhcp;
    @SerializedName("gateway")
    public String gateway;
    @SerializedName("ip")
    public String ip;
    @SerializedName("mtu_size")
    public String mtu_size;
    @SerializedName("subnet_mask")
    public String subnet_mask;
    @SerializedName("server_port")
    public String server_port;
    @SerializedName("server_ip")
    public String server_ip;

    public DeviceLanInfo() {

    }

    public DeviceLanInfo clone() throws CloneNotSupportedException {
        DeviceLanInfo target = (DeviceLanInfo) super.clone();
        return target;
    }
}