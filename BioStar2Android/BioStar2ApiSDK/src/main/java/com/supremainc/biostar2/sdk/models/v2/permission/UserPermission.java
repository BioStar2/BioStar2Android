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
package com.supremainc.biostar2.sdk.models.v2.permission;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class UserPermission implements Cloneable, Serializable {
    public static final String TAG = UserPermission.class.getSimpleName();
    private static final long serialVersionUID = -245744667891266499L;

    @SerializedName("status_code")
    public String status_code;
    @SerializedName("message")
    public String message;
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("permissions")
    public ArrayList<PermissionItem> permissions;
    public UserPermission() {

    }
    public UserPermission clone() throws CloneNotSupportedException {
        UserPermission target = (UserPermission) super.clone();
        if (permissions != null) {
            target.permissions = (ArrayList<PermissionItem>) permissions.clone();
        }
        return target;
    }
}
