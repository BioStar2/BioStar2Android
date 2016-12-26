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
package com.supremainc.biostar2.sdk.datatype.v2.Preferrence;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class NotificationsSetting implements Cloneable, Serializable {
    public static final String TAG = NotificationsSetting.class.getSimpleName();
    private static final long serialVersionUID = 6724828159087582264L;
    @SerializedName("description")
    public String description;
    @SerializedName("subscribed")
    public boolean subscribed;
    @SerializedName("type")
    public String type;

    //		@SerializedName("DEVICE_REBOOT")
//		public boolean DEVICE_REBOOT;
//		@SerializedName("DEVICE_RS485_DISCONNECT")
//		public boolean DEVICE_RS485_DISCONNECT;
//		@SerializedName("DEVICE_TAMPERING")
//		public boolean DEVICE_TAMPERING;
//		@SerializedName("DOOR_FORCED_OPEN")
//		public boolean DOOR_FORCED_OPEN;
//		@SerializedName("DOOR_HELD_OPEN")
//		public boolean DOOR_HELD_OPEN;
//		@SerializedName("DOOR_OPEN_REQUEST")
//		public boolean DOOR_OPEN_REQUEST;
//		@SerializedName("ZONE_APB")
//		public boolean ZONE_APB;
//		@SerializedName("ZONE_FIRE")
//		public boolean ZONE_FIRE;
    public NotificationsSetting clone() throws CloneNotSupportedException {
        NotificationsSetting target = (NotificationsSetting) super.clone();
        return target;
    }
}