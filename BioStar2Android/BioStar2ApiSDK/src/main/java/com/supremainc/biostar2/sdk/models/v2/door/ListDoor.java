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
import com.supremainc.biostar2.sdk.models.v2.device.BaseDevice;

import java.io.Serializable;


public class ListDoor extends BaseDoor implements Cloneable, Serializable {
    public static final String TAG = ListDoor.class.getSimpleName();
    private static final long serialVersionUID = 7060951086894769022L;
    @SerializedName("door_group_id")
    public String door_group_id;
    @SerializedName("entry_device")
    public BaseDevice entry_device;
    @SerializedName("exit_device")
    public BaseDevice exit_device;
    @SerializedName("door_relay")
    public DoorRelay door_relay;
    @SerializedName("open_duration")
    public String open_duration;
    @SerializedName("status")
    public DoorStatus status;
    @SerializedName("exit_button")
    public DoorSensor exit_button;
    @SerializedName("door_sensor")
    public DoorSensor door_sensor;

    public ListDoor() {

    }

    public String getOpenDuration(String minUnit, String secUnit) {
        long openTime = Long.valueOf(open_duration);
        long min = 0;
        long sec = 0;
        if (openTime > 59) {
            min = openTime / 60;
            sec = openTime % 60;
        } else {
            sec = openTime;
        }
        String openDuration = "";
        if (min > 0) {
            openDuration = min + " " + minUnit;
        }
        if (sec > 0) {
            if (!openDuration.isEmpty()) {
                openDuration = openDuration + " ";
            }
            openDuration = openDuration + sec + secUnit;
        }
        return openDuration;
    }

    public ListDoor clone() throws CloneNotSupportedException {
        ListDoor target = (ListDoor) super.clone();
        if (door_relay != null) {
            target.door_relay = (DoorRelay) door_relay.clone();
        }
        if (entry_device != null) {
            target.entry_device = entry_device.clone();
        }
        if (exit_device != null) {
            target.exit_device = exit_device.clone();
        }
        if (status != null) {
            target.status = status.clone();
        }

        if (door_sensor != null) {
            target.door_sensor = (DoorSensor) door_sensor.clone();
        }
        if (exit_button != null) {
            target.exit_button = (DoorSensor) exit_button.clone();
        }

        return target;
    }
}
