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
package com.supremainc.biostar2.sdk.models.v1.permission;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
public class CloudRoles implements Cloneable, Serializable {
    public static final String TAG = CloudRoles.class.getSimpleName();
    private static final long serialVersionUID = -6066815057274171202L;

    @SerializedName("status_code")
    public String status_code;
    @SerializedName("message")
    public String message;
    @SerializedName("records")
    public ArrayList<CloudRole> records;
    @SerializedName("total")
    public int total;

    public CloudRoles() {

    }

    public CloudRoles(ArrayList<CloudRole> rows, int total) {
        this.total = total;
        this.records = rows;
    }

    public CloudRoles(ArrayList<CloudRole> rows) {
        if (rows != null) {
            total = rows.size();
        }
        this.records = rows;
    }

    @SuppressWarnings("unchecked")
    public CloudRoles clone() throws CloneNotSupportedException {
        CloudRoles target = (CloudRoles) super.clone();
        if (records != null) {
            target.records = (ArrayList<CloudRole>) records.clone();
        }
        return target;
    }
}
