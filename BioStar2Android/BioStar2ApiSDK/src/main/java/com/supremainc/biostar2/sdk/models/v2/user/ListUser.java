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
package com.supremainc.biostar2.sdk.models.v2.user;

import com.google.gson.annotations.SerializedName;
import com.supremainc.biostar2.sdk.models.v2.accesscontrol.ListAccessGroup;
import com.supremainc.biostar2.sdk.models.v2.permission.UserPermission;


import java.io.Serializable;
import java.util.ArrayList;


public class ListUser extends BaseUser implements Cloneable, Serializable {
    public static final String TAG = ListUser.class.getSimpleName();
    private static final long serialVersionUID = -3497693199597313368L;

    @SerializedName("email")
    public String email;
    @SerializedName("user_group")
    public BaseUserGroup user_group;
    @SerializedName("access_groups")
    public ArrayList<ListAccessGroup> access_groups;
    @SerializedName("fingerprint_count") // only 2.3.0
    public int fingerprint_count;
    @SerializedName("fingerprint_template_count")
    public int fingerprint_template_count;
    @SerializedName("card_count")
    public int card_count;
    @SerializedName("pin_exist")
    public boolean pin_exist;
    public boolean pin_exist_backup;
    @SerializedName("photo_exist")
    public boolean photo_exist;
    @SerializedName("last_modify")
    public String last_modify;
    @SerializedName("permission")
    public UserPermission permission;
    @SerializedName("face_template_count")
    public int face_template_count;

    public ListUser() {

    }

    public ListUser clone() throws CloneNotSupportedException {
        ListUser target = (ListUser) super.clone();
        if (user_group != null) {
            target.user_group = user_group.clone();
        }
        if (access_groups != null) {
            target.access_groups = (ArrayList<ListAccessGroup>)access_groups.clone();
        }
        if (permission != null) {
            target.permission = (UserPermission) permission.clone();
        }
        return target;
    }
}