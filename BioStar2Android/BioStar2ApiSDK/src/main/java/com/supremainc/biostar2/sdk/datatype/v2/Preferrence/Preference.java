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
import java.util.ArrayList;
public class Preference implements Cloneable, Serializable {
    public static final String TAG = Preference.class.getSimpleName();
    private static final long serialVersionUID = -6788336586649309987L;
    @SerializedName("status_code")
    public String statusCode;
    @SerializedName("message")
    public String message;


    @SerializedName("date_format")
    public String date_format;
    @SerializedName("time_format")
    public String time_format;

    @SerializedName("notifications")
    public ArrayList<NotificationsSetting> notifications;

    public Preference clone() throws CloneNotSupportedException {
        Preference target = (Preference) super.clone();
        if (notifications != null) {
            target.notifications = (ArrayList<NotificationsSetting>) notifications.clone();
        }
        return target;
    }
}