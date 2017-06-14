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
package com.supremainc.biostar2.sdk.models.v2.door;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Door extends ListDoor implements Cloneable, Serializable {
    public static final String TAG = Door.class.getSimpleName();
    private static final long serialVersionUID = -6775679932087737969L;


    // /**
    // * trigger action
    // */
    // @SerializedName("alarms")
    // ArrayList<DoorAlarm> alarms;
    @SerializedName("apb_reset_time")
    public long apb_reset_time;
    /**
     * [NONE:not use anti-passback, SOFT_APB: soft anti-passback(If the exit
     * Device exists in the door), HARD_APB: hard anti-passback(If the exit
     * Device exists in the door)],
     */
    @SerializedName("apb_type")
    public String apb_type;
    @SerializedName("apb_when_disconnected")
    public String apb_when_disconnected;
    @SerializedName("held_open_timeout")
    public long held_open_timeout;
    /**
     * [ON:Lock when door(sensor) is closed / OFF:keep unlock],
     */
    @SerializedName("open_once")
    public String open_once;

    public Door() {

    }

    public String getName() {
        if (name == null || name.isEmpty()) {
            return String.valueOf(id);
        }
        return name;
    }

    public Door clone() throws CloneNotSupportedException {
        Door target = (Door) super.clone();
        if (status != null) {
            target.status = (DoorStatus) status.clone();
        }



        return target;
    }
}
