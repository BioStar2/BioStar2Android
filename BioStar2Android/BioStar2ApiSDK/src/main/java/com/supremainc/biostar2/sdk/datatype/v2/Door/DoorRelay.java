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
import com.supremainc.biostar2.sdk.datatype.v2.Device.BaseDevice;

import java.io.Serializable;


public class DoorRelay implements Cloneable, Serializable {
    public static final String TAG = DoorRelay.class.getSimpleName();
    private static final long serialVersionUID = 2878088017355057274L;
    @SerializedName("device")
    public BaseDevice device;
    @SerializedName("index")
    public String index;

    public String getName() {
        if (device != null) {
            return device.getName();
        }
        return null;
    }

    public DoorRelay clone() throws CloneNotSupportedException {
        DoorRelay target = (DoorRelay) super.clone();
        if (device != null) {
            target.device =  device.clone();
        }
        return target;
    }
}