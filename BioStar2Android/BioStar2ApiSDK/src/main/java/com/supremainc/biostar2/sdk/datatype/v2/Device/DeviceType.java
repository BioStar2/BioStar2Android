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

import java.io.Serializable;

public class  DeviceType extends ListDeviceType implements Cloneable, Serializable {
    public static final String TAG = DeviceType.class.getSimpleName();
    private static final long serialVersionUID = 3103879226461355384L;

    // @SerializedName("max_connection")
    // public int max_connection;
    // @SerializedName("support_master")
    // public boolean support_master;
    // @SerializedName("support_slave")
    // public boolean support_slave;
    // @SerializedName("input_port_num")
    // public int input_port_num;
    // @SerializedName("output_port_num")
    // public int output_port_num ;
    // @SerializedName("relay_num")
    // public int relay_num ;
    // @SerializedName("support_tamper")
    // public boolean support_tamper ;
    // @SerializedName("tna_key_num")
    // public int tna_key_num;
    // @SerializedName("tna_extra_key_num")
    // public int tna_extra_key_num ;
    // @SerializedName("rs485_channel_num")
    // public int rs485_channel_num;
    // @SerializedName("wiegand_channel_num")
    // public int wiegand_channel_num;
    public DeviceType() {

    }

    public DeviceType clone() throws CloneNotSupportedException {
        DeviceType target = (DeviceType) super.clone();
        return target;
    }
}

