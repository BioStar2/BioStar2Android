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

public class BaseCard implements Cloneable, Serializable {
    public static final String TAG = BaseCard.class.getSimpleName();
    private static final long serialVersionUID = -7803676161993959797L;
    @SerializedName("status_code")
    public String status_code;
    @SerializedName("message")
    public String message;

    @SerializedName("card_id")
    public String card_id;
    @SerializedName("id")
    public String id;
    @SerializedName("type") // = ['CSN' or 'WIEGAND' or 'SECURE CREDENTIAL' or 'ACCESS_ON'],
    public String type;

    public static final String CARD_ID = "card_id";
    public static final String CSN = "CSN";
    public static final String WIEGAND = "WIEGAND";
    public static final String CSN_WIEGAND = "CSN_WIEGAND";
    public static final String SECURE_CREDENTIAL = "SECURE_CREDENTIAL";
    public static final String ACCESS_ON = "ACCESS_ON";

    public BaseCard() {

    }

    public BaseCard clone() throws CloneNotSupportedException {
        BaseCard target = (BaseCard) super.clone();
        return target;
    }
}
