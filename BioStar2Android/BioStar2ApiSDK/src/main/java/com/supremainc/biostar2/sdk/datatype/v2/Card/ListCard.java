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
import com.supremainc.biostar2.sdk.datatype.v2.Common.SimpleData;
import com.supremainc.biostar2.sdk.datatype.v2.AccessControl.ListAccessGroup;
import com.supremainc.biostar2.sdk.datatype.v2.User.BaseUser;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class ListCard  extends BaseCard implements Cloneable, Serializable {
    public static final String TAG = ListCard.class.getSimpleName();
    private static final long serialVersionUID = 1080742920196590586L;

    @SerializedName("is_mobile_credential")
    public boolean is_mobile_credential  ;
    @SerializedName("is_registered")
    public boolean is_registered  ;
    @SerializedName("issue_count")
    public long issue_count ;
    @SerializedName("status") //  ['ASSIGNED = Assigned to specific user' or 'UNASSIGNED = Not assigned to specific user' or 'BLOCKED = Blocked for usage'],
    public String status  ;
    @SerializedName("is_blocked")
    public boolean is_blocked ;
    @SerializedName("unassigned")
    public boolean unassigned;
    @SerializedName("wiegand_format")
    public WiegandFormat wiegand_format  ;
    public enum TimeType {start_datetime, expiry_datetime}

    public ListCard() {

    }

    public void clone(ListCard target) throws CloneNotSupportedException {
        target.is_mobile_credential = is_mobile_credential;
        target.is_registered = is_registered;
        target.issue_count = issue_count;
        target.status = status;
        target.is_blocked = is_blocked;
        target.unassigned = unassigned;
        target.card_id = card_id;
        target.id = id;
        target.type = type;

        if (wiegand_format != null) {
            target.wiegand_format = wiegand_format.clone();
        }
    }

    public ListCard clone() throws CloneNotSupportedException {
        ListCard target = (ListCard) super.clone();
        if (wiegand_format != null) {
            target.wiegand_format = wiegand_format.clone();
        }
        return target;
    }
}
