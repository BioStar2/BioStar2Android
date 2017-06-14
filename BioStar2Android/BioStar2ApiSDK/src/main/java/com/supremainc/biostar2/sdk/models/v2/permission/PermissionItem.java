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

public class PermissionItem implements Cloneable, Serializable {
    public static final String TAG = PermissionItem.class.getSimpleName();
    private static final long serialVersionUID = 359366958341332427L;
    public static final String ROLE_READ = "read";
    public static final String ROLE_WRITE = "write";
    @SerializedName("allowed_group_id_list")
    public ArrayList<String> allowed_group_id_list;
    @SerializedName("module")
    public String module;
    @SerializedName("read")
    public boolean read;
    @SerializedName("write")
    public boolean write;

    public PermissionItem() {

    }
    public PermissionItem clone() throws CloneNotSupportedException {
        PermissionItem target = (PermissionItem) super.clone();
        if (allowed_group_id_list != null) {
            target.allowed_group_id_list = (ArrayList<String>) allowed_group_id_list.clone();
        }
        return target;
    }
}
