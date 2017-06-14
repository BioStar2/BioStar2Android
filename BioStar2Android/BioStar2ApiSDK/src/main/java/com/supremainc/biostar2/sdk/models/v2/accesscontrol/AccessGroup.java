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


package com.supremainc.biostar2.sdk.models.v2.accesscontrol;


import com.google.gson.annotations.SerializedName;
import com.supremainc.biostar2.sdk.models.v2.user.ListUser;
import com.supremainc.biostar2.sdk.models.v2.user.UserGroup;

import java.io.Serializable;
import java.util.ArrayList;

public class AccessGroup extends ListAccessGroup implements Cloneable, Serializable {
    public static final String TAG = AccessGroup.class.getSimpleName();
    private static final long serialVersionUID = -6280293723151230572L;

    @SerializedName("users")
    public ArrayList<ListUser> users;
    @SerializedName("user_groups")
    public ArrayList<UserGroup> user_groups;
    @SerializedName("access_levels")
    public ArrayList<BaseAccessLevel> access_levels;

    public AccessGroup() {

    }

    @SuppressWarnings("unchecked")
    public AccessGroup clone() throws CloneNotSupportedException {
        AccessGroup target = (AccessGroup) super.clone();
        if (users != null) {
            target.users = (ArrayList<ListUser>) users.clone();
        }
        if (user_groups != null) {
            target.user_groups = (ArrayList<UserGroup>) user_groups.clone();
        }

        if (access_levels != null) {
            target.access_levels = (ArrayList<BaseAccessLevel>) access_levels.clone();
        }
        return target;
    }
}
