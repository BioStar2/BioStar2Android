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
import com.supremainc.biostar2.sdk.datatype.v2.AccessControl.ListAccessGroup;
import com.supremainc.biostar2.sdk.datatype.v2.Common.SimpleData;
import com.supremainc.biostar2.sdk.datatype.v2.FingerPrint.ListFingerprintTemplate;
import com.supremainc.biostar2.sdk.datatype.v2.User.BaseUser;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Card  extends ListCard implements Cloneable, Serializable {
    public static final String TAG = Card.class.getSimpleName();
    private static final long serialVersionUID = -4530271987334137218L;

    @SerializedName("user")
    public BaseUser user;
    @SerializedName("fingerprint_index_list")
    public ArrayList<Integer> fingerprint_index_list;
    @SerializedName("fingerprint_templates")
    public ArrayList<ListFingerprintTemplate> fingerprint_templates;
    @SerializedName("expiry_datetime")
    private String expiry_datetime ;
    @SerializedName("start_datetime")
    private String start_datetime ;
    @SerializedName("access_groups")
    public ArrayList<ListAccessGroup> access_groups;
    @SerializedName("pin_exist")
    public boolean pin_exist  ;

    public Card() {

    }

    public String getTimeType(TimeType type) {
        String src = null;
        switch (type) {
            case start_datetime:
                src = start_datetime;
                break;
            case expiry_datetime:
                src = expiry_datetime;
                break;
            default:
                break;
        }
        return src;
    }

    public Calendar getTimeCalendar(TimeConvertProvider convert, TimeType timeType) {
        return convert.convertServerTimeToCalendar(getTimeType(timeType), false);
    }


    public String getTimeFormmat(TimeConvertProvider convert, TimeType timeType, TimeConvertProvider.DATE_TYPE type) {
        Calendar cal = getTimeCalendar(convert, timeType);
        return convert.convertCalendarToFormatter(cal, type);
    }
    public Card clone() throws CloneNotSupportedException {
        Card target = (Card) super.clone();
        if (access_groups != null) {
            target.access_groups =  (ArrayList<ListAccessGroup>)access_groups.clone();
        }
        if (user != null) {
            target.user =  user.clone();
        }
        if (wiegand_format != null) {
            target.wiegand_format =  wiegand_format.clone();
        }
        return target;
    }
}
