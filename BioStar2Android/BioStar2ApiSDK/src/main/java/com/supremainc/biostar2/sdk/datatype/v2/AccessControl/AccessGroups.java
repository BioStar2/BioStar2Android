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


package com.supremainc.biostar2.sdk.datatype.v2.AccessControl;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

public class AccessGroups  implements Cloneable, Serializable {
    public static final String TAG = AccessGroups.class.getSimpleName();
    private static final long serialVersionUID = 7402431015729884663L;

    @SerializedName("status_code")
    public String statusCode;
    @SerializedName("message")
    public String message;
    @SerializedName("records")
    public ArrayList<ListAccessGroup> records;
    @SerializedName("total")
    public int total;

    public AccessGroups() {

    }

    public AccessGroups(ArrayList<ListAccessGroup> rows, int total) {
        this.total = total;
        this.records = rows;
    }

    public AccessGroups(ArrayList<ListAccessGroup> rows) {
        if (rows != null) {
            total = rows.size();
        }
        this.records = rows;
    }

    @SuppressWarnings("unchecked")
    public AccessGroups clone() throws CloneNotSupportedException {
        AccessGroups target = (AccessGroups) super.clone();
        if (records != null) {
            target.records = (ArrayList<ListAccessGroup>) records.clone();
        }
        return target;
    }
}
