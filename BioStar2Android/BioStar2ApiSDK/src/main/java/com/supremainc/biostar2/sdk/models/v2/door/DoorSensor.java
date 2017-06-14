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


public class DoorSensor implements Cloneable, Serializable {
    public static final String TAG = DoorSensor.class.getSimpleName();
    private static final long serialVersionUID = 2644031752253538672L;
    @SerializedName("device")
    public BaseDevice device;
    @SerializedName("index")
    public String index;
    /**
     * 'OPEN' = Opened, 'CLOSE' = Closed,
     */
    @SerializedName("default_status")
    public String default_status;

    public DoorSensor clone() throws CloneNotSupportedException {
        DoorSensor target = (DoorSensor) super.clone();
        if (device != null) {
            target.device =  device.clone();
        }
        return target;
    }

    public String getName() {
        if (device != null) {
            return device.getName();
        }
        return null;
    }
}
