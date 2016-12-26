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
package com.supremainc.biostar2.sdk.datatype.v2.Card;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SmartCardLayouts implements Cloneable, Serializable {
    public static final String TAG = SmartCardLayouts.class.getSimpleName();
    private static final long serialVersionUID = -1318998451021094100L;

    @SerializedName("status_code")
    public String statusCode;
    @SerializedName("message")
    public String message;
    @SerializedName("records")
    public ArrayList<SmartCardLayout> records;
    @SerializedName("total")
    public int total;

    public SmartCardLayouts() {

    }

    public SmartCardLayouts(ArrayList<SmartCardLayout> rows, int total) {
        this.total = total;
        this.records = rows;
    }

    public SmartCardLayouts(ArrayList<SmartCardLayout> rows) {
        if (rows != null) {
            total = rows.size();
        }
        this.records = rows;
    }

    @SuppressWarnings("unchecked")
    public SmartCardLayouts clone() throws CloneNotSupportedException {
        SmartCardLayouts target = (SmartCardLayouts) super.clone();
        if (records != null) {
            target.records = (ArrayList<SmartCardLayout>) records.clone();
        }
        return target;
    }
}
