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

public class Login extends NotificationToken implements Cloneable, Serializable {
    public static final String TAG = Login.class.getSimpleName();
    private static final long serialVersionUID = 7916712333351434386L;
    @SerializedName("user_id")
    public String user_id;
    @SerializedName("password")
    public String password;
    @SerializedName("name")
    public String name;

    public Login(Context context, String notificationToken) {
        super(context, notificationToken);
    }

    public Login clone() throws CloneNotSupportedException {
        Login target = (Login) super.clone();
        return target;
    }
}