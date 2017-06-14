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
package com.supremainc.biostar2.sdk.models.v2.eventlog;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;


public class Conditions implements Cloneable, Serializable {
    public static final String TAG = "data_conditions";
    private static final long serialVersionUID = -4457837901052058482L;
    @SerializedName("column")
    public String column;
    @SerializedName("operator")
    public int operator;
    @SerializedName("total")
    public boolean total;
    @SerializedName("values")
    private ArrayList<String> values;

    public Conditions(String column, int operator, ArrayList<String> values) {
        this.column = column;
        this.operator = operator;
        this.values = values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    @SuppressWarnings("unchecked")
    public Conditions clone() throws CloneNotSupportedException {
        Conditions target = (Conditions) super.clone();
        if (values != null) {
            target.values = (ArrayList<String>) values.clone();
        }
        return target;
    }
}
