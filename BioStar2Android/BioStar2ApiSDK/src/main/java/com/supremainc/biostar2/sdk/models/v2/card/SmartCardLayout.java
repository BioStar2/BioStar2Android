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

public class SmartCardLayout implements Cloneable, Serializable {
    public static final String TAG = SmartCardLayout.class.getSimpleName();
    private static final long serialVersionUID = 6406974587143731016L;

    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("max_template_in_card")
    public int max_template_in_card ;
    @SerializedName("max_template_length")
    public String max_template_length;


    public SmartCardLayout() {

    }

    public SmartCardLayout clone() throws CloneNotSupportedException {
        SmartCardLayout target = (SmartCardLayout) super.clone();
        return target;
    }
}