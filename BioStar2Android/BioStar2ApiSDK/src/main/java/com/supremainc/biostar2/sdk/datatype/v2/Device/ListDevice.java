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
import com.supremainc.biostar2.sdk.datatype.v2.Card.SmartCardLayout;
import com.supremainc.biostar2.sdk.datatype.v2.Card.WiegandFormat;
import com.supremainc.biostar2.sdk.datatype.v2.Door.BaseDoor;

import java.io.Serializable;
import java.util.ArrayList;

public class ListDevice extends BaseDevice implements Cloneable, Serializable {
    public static final String TAG = ListDevice.class.getSimpleName();
    private static final long serialVersionUID = -5011790137340832929L;
    @SerializedName("children")
    public ArrayList<BaseDevice> children;
//    @SerializedName("lan")  // 2.3.0 diffrent 2.4.0
//    public DeviceLanInfo lan;
    /**
     * 'PARENT', 'CHILD', 'DEFAULT' 2.3.0
     */
    @SerializedName("mode")
    public String mode;
    @SerializedName("status")
    public String status;
    @SerializedName("rs485")
    public RS485 rs485;
        @SerializedName("wlan")
    public DeviceWlanInfo wlan;
    @SerializedName("csn_wiegand_format")
    public WiegandFormat wiegand_format;
    @SerializedName("wiegand_format_list")
    public ArrayList<WiegandFormat> wiegand_format_list;
    @SerializedName("smart_card_layout")
    public SmartCardLayout smart_card_layout ;
    @SerializedName("used_by_doors")
    public ArrayList<BaseDoor> used_by_doors;

    public ListDevice() {

    }

    public ListDevice(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean isSupport(boolean isWithOutSlave, int deviceSupport) {
        if (rs485 != null && rs485.mode != null && device_type != null) {
            if (isWithOutSlave) {
                if (rs485.mode.equals(RS485.SLAVE)) {
                    return false;
                }
            }
            return device_type.isSupport(deviceSupport);
        }
        if (mode == null || device_type == null) {
            return false;
        }
        if (isWithOutSlave) {
            if (mode.equals("CHILD")) {
                return false;
            }
        }
        return device_type.isSupport(deviceSupport);
    }

    public boolean isSupportCSNWiegand() {
        if (wiegand_format != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isSupportWiegand() {
        if (wiegand_format_list != null && wiegand_format_list.size() > 0) {
            return true;
        }
        return false;
    }

    public ListDevice clone() throws CloneNotSupportedException {
        ListDevice target = (ListDevice) super.clone();
        if (children != null) {
            target.children = (ArrayList<BaseDevice>) children.clone();
        }
//        if (lan != null) {
//            target.lan = lan.clone();
//        }
        if (wlan != null) {
            target.wlan = wlan.clone();
        }
        if (used_by_doors != null) {
            target.used_by_doors = (ArrayList<BaseDoor>) used_by_doors.clone();
        }
        if (wiegand_format != null) {
            target.wiegand_format = wiegand_format.clone();
        }
        if (smart_card_layout != null) {
            target.smart_card_layout = smart_card_layout.clone();
        }
        if (wiegand_format_list != null) {
            target.wiegand_format_list =(ArrayList<WiegandFormat>)  wiegand_format_list.clone();
        }
        if (rs485 != null) {
            target.rs485 = target.rs485.clone();
        }
        return target;
    }
}