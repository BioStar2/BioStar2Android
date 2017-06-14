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

import java.io.Serializable;
import java.util.ArrayList;

public class Users implements Cloneable, Serializable {
    public static final String TAG = Users.class.getSimpleName();
    private static final long serialVersionUID = 2877607278756258665L;

    @SerializedName("records")
    public ArrayList<ListUser> records;
    @SerializedName("total")
    public int total;

    public Users() {

    }

    public Users(ArrayList<ListUser> rows, int total) {
        this.total = total;
        this.records = rows;
    }

    public Users(ArrayList<ListUser> rows) {
        if (rows != null) {
            total = rows.size();
        }
        this.records = rows;
    }

    @SuppressWarnings("unchecked")
    public Users clone() throws CloneNotSupportedException {
        Users target = (Users) super.clone();
        if (records != null) {
            target.records = (ArrayList<ListUser>) records.clone();
        }
        return target;
    }
}