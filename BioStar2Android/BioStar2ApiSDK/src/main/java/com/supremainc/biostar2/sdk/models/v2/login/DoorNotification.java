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
package com.supremainc.biostar2.sdk.models.v2.login;

import com.google.gson.annotations.SerializedName;
import com.supremainc.biostar2.sdk.models.v2.door.BaseDoor;


import java.io.Serializable;
public class DoorNotification implements Cloneable, Serializable {
    public static final String TAG = DoorNotification.class.getSimpleName();
    private static final long serialVersionUID = 3012490700070629402L;
    @SerializedName("door")
    public BaseDoor door;
    @SerializedName("message")
    public String message;
    @SerializedName("request_timestamp")
    public String request_timestamp;
    @SerializedName("title")
    public String title;

    public DoorNotification clone() throws CloneNotSupportedException {
        DoorNotification target = (DoorNotification) super.clone();
        if (door != null) {
            target.door = door.clone();
        }
        return target;
    }
}