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
package com.supremainc.biostar2.sdk.models.v2.card;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Cards implements Cloneable, Serializable {
    public static final String TAG = Cards.class.getSimpleName();
    private static final long serialVersionUID = 6325123415290278418L;
    @SerializedName("status_code")
    public String status_code;
    @SerializedName("message")
    public String message;
    @SerializedName("records")
    public ArrayList<ListCard> records;
    @SerializedName("total")
    public int total;

    public Cards() {

    }

    public Cards(ArrayList<ListCard> rows, int total) {
        this.total = total;
        this.records = rows;
    }

    public Cards(ArrayList<ListCard> rows) {
        if (rows != null) {
            total = rows.size();
        }
        this.records = rows;
    }

    @SuppressWarnings("unchecked")
    public Cards clone() throws CloneNotSupportedException {
        Cards target = (Cards) super.clone();
        if (records != null) {
            target.records = (ArrayList<ListCard>) records.clone();
        }
        return target;
    }
}
