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
package com.supremainc.biostar2.sdk.datatype.v2.User;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class UserGroup extends ListUserGroup implements Cloneable, Serializable {
    private static final long serialVersionUID = 2777183888876165497L;

    @SerializedName("user_total")
    public int user_total;
    @SerializedName("user_total_including_sub_groups")
    public int user_total_including_sub_groups;

    @SerializedName("parent")
    public UserGroup parent;

    public UserGroup() {

    }

    public UserGroup(String name, String id) {
        this.id = id;
        this.name = name;
    }

    public UserGroup clone() throws CloneNotSupportedException {
        UserGroup target = (UserGroup) super.clone();
        if (parent != null) {
            target.parent = (UserGroup) parent.clone();
        }
        return target;
    }

}
