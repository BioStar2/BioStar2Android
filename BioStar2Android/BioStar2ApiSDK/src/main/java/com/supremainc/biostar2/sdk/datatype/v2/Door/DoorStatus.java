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
package com.supremainc.biostar2.sdk.datatype.v2.Door;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DoorStatus implements Cloneable, Serializable {
    public static final String TAG = DoorStatus.class.getSimpleName();
    private static final long serialVersionUID = -2906715192751679747L;
    /**
     * 'no_alarm', 'held_opened', 'forced_opened'),
     */
    @SerializedName("normal")
    public boolean normal;
    @SerializedName("locked")
    public boolean locked;
    @SerializedName("unlocked")
    public boolean unlocked;
    @SerializedName("forced_open")
    public boolean forced_open;
    @SerializedName("held_opened")
    public boolean held_opened;
    @SerializedName("forced_open")
    public boolean apb_failed;
    @SerializedName("disconnected")
    public boolean disconnected;
    @SerializedName("scheduleLocked")
    public boolean scheduleLocked;
    @SerializedName("scheduleUnlocked")
    public boolean scheduleUnlocked;
    @SerializedName("emergencyLocked")
    public boolean emergencyLocked;
    @SerializedName("emergencyUnlocked")
    public boolean emergencyUnlocked;
    @SerializedName("operatorLocked")
    public boolean operatorLocked;
    @SerializedName("operatorUnlocked")
    public boolean operatorUnlocked;

    public DoorStatus() {

    }
    public DoorStatus clone() throws CloneNotSupportedException {
        DoorStatus target = (DoorStatus) super.clone();
        return target;
    }
}


