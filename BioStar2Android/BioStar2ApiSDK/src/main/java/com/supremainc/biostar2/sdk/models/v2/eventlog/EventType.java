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
package com.supremainc.biostar2.sdk.models.v2.eventlog;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class EventType implements Cloneable, Serializable {
    public static final String TAG = EventType.class.getSimpleName();
    private static final long serialVersionUID = 3378092396980913551L;
    @SerializedName("code")
    public int code;
    @SerializedName("name")
    public String name;
    @SerializedName("description")
    public String description;
    @SerializedName("alertable")
    public boolean alertable;
    @SerializedName("enable_alert")
    public boolean enable_alert;
    @SerializedName("alert_name")
    public String alert_name;
    @SerializedName("alert_message")
    public String alert_message;

    public EventType clone() throws CloneNotSupportedException {
        EventType target = (EventType) super.clone();
        return target;
    }
}
