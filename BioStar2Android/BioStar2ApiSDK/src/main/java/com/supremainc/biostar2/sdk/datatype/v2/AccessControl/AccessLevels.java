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

public class AccessLevels implements Cloneable, Serializable {
    public static final String TAG = AccessLevels.class.getSimpleName();
    private static final long serialVersionUID = -8589464130098194496L;

    @SerializedName("status_code")
    public String statusCode;
    @SerializedName("message")
    public String message;
    @SerializedName("records")
    public ArrayList<ListAccessLevel> records;
    @SerializedName("total")
    public int total;

    public AccessLevels() {

    }

    public AccessLevels(ArrayList<ListAccessLevel> rows, int total) {
        this.total = total;
        this.records = rows;
    }

    public AccessLevels(ArrayList<ListAccessLevel> rows) {
        if (rows != null) {
            total = rows.size();
        }
        this.records = rows;
    }

    @SuppressWarnings("unchecked")
    public AccessLevels clone() throws CloneNotSupportedException {
        AccessLevels target = (AccessLevels) super.clone();
        if (records != null) {
            target.records = (ArrayList<ListAccessLevel>) records.clone();
        }
        return target;
    }
}
