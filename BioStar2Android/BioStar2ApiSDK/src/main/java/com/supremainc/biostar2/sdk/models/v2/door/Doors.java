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
package com.supremainc.biostar2.sdk.models.v2.door;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

public class  Doors implements Cloneable, Serializable {
    public static final String TAG = Doors.class.getSimpleName();
    private static final long serialVersionUID = -408251561486293077L;
    @SerializedName("status_code")
    public String status_code;
    @SerializedName("message")
    public String message;
    @SerializedName("records")
    public ArrayList<ListDoor> records;
    @SerializedName("total")
    public int total;

    public Doors() {

    }

    public Doors(ArrayList<ListDoor> rows, int total) {
        this.total = total;
        this.records = rows;
    }

    public Doors(ArrayList<ListDoor> rows) {
        if (rows != null) {
            total = rows.size();
        }
        this.records = rows;
    }

    @SuppressWarnings("unchecked")
    public Doors clone() throws CloneNotSupportedException {
        Doors target = (Doors) super.clone();
        if (records != null) {
            target.records = (ArrayList<ListDoor>) records.clone();
        }
        return target;
    }
}