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
package com.supremainc.biostar2.sdk.datatype.v2.Login;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
public class NotificationToken implements Cloneable, Serializable {
    public static final String TAG = NotificationToken.class.getSimpleName();
    private static final long serialVersionUID = 4710311620408243918L;
    @SerializedName("mobile_app_version")
    public String mobile_app_version;
    @SerializedName("mobile_app_name")
    public String mobile_app_name;
    @SerializedName("mobile_device_type")
    public String mobile_device_type;
    @SerializedName("mobile_os_version")
    public String mobile_os_version;
    @SerializedName("notification_token")
    public String notification_token;

    public NotificationToken(Context context, String notificationToken) {
        mobile_os_version = android.os.Build.VERSION.RELEASE;
        mobile_device_type = "android";
        PackageInfo i;
        try {
            i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            mobile_app_version = String.valueOf(i.versionCode);
            mobile_app_name = i.packageName;
        } catch (NameNotFoundException e) {
            mobile_app_version = "0";
            e.printStackTrace();
        }
        notification_token = notificationToken;
    }

    public NotificationToken clone() throws CloneNotSupportedException {
        NotificationToken target = (NotificationToken) super.clone();
        return target;
    }
}
