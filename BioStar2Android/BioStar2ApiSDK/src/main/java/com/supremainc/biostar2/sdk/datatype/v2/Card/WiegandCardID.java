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

public class WiegandCardID implements Cloneable, Serializable {
    public static final String TAG = WiegandCardID.class.getSimpleName();
    private static final long serialVersionUID = -4952287661504771970L;

    @SerializedName("card_id_max_num") // use only get
    public long max;
    @SerializedName("card_id") // use only post
    public String card_id;

    public WiegandCardID() {

    }

    public WiegandCardID(String cardID) {
        card_id = cardID;
    }

    public WiegandCardID(int cardID) {
        card_id = String.valueOf(cardID);
    }

    public int getLength() {
        String s = String.valueOf(max);
        return s.length();
    }


    public WiegandCardID clone() throws CloneNotSupportedException {
        WiegandCardID target = (WiegandCardID) super.clone();
        return target;
    }
}
