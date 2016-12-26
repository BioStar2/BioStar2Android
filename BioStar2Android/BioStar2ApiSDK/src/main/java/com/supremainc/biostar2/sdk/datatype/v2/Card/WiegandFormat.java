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

public class WiegandFormat implements Cloneable, Serializable {
    public static final String TAG = WiegandFormat.class.getSimpleName();
    private static final long serialVersionUID = 3437063176531340715L;

    @SerializedName("id") // use only get
    public String id;
    @SerializedName("name") // use only get
    public String name;

    @SerializedName("use_facility_code")  // use only get
    public boolean use_facility_code;
    @SerializedName("wiegand_card_id_list")
    public ArrayList<WiegandCardID> wiegand_card_ids;

    public WiegandFormat() {

    }

    public WiegandFormat( ArrayList<WiegandCardID> wigand_card_ids) {
        this.wiegand_card_ids = wigand_card_ids;
    }

    public WiegandFormat clone() throws CloneNotSupportedException {
        WiegandFormat target = (WiegandFormat) super.clone();
        if (wiegand_card_ids != null) {
            target.wiegand_card_ids = (ArrayList<WiegandCardID>)wiegand_card_ids.clone();
        }
        return target;
    }
}
