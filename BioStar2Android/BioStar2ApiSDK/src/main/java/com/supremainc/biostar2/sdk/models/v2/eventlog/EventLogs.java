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
import java.util.ArrayList;

public class EventLogs implements Cloneable, Serializable {
    public static final String TAG = EventLogs.class.getSimpleName();
    private static final long serialVersionUID = -1440378695787901359L;
    @SerializedName("status_code")
    public String status_code;
    @SerializedName("message")
    public String message;
    @SerializedName("records")
    public ArrayList<EventLog> records;
    @SerializedName("is_next")
    public boolean isNext;

    public EventLogs() {

    }


    @SuppressWarnings("unchecked")
    public EventLogs clone() throws CloneNotSupportedException {
        EventLogs target = (EventLogs) super.clone();
        if (records != null) {
            target.records = (ArrayList<EventLog>) records.clone();
        }
        return target;
    }
}
