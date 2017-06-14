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

public class MobileCardRaw implements Cloneable, Serializable {
    public static final String TAG = MobileCardRaw.class.getSimpleName();
    private static final long serialVersionUID = -1447429878731340721L;

    @SerializedName("message")
    public String message;
    @SerializedName("status_code")
    public String status_code;
    @SerializedName("raw")
    public String raw;
    @SerializedName("smart_card_layout_primary_key")
    public String smart_card_layout_primary_key;
    @SerializedName("smart_card_layout_second_key")
    public String smart_card_layout_second_key;
    public MobileCardRaw() {

    }

    public MobileCardRaw clone() throws CloneNotSupportedException {
        MobileCardRaw target = (MobileCardRaw) super.clone();
        return target;
    }
}
