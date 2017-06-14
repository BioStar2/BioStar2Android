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
import com.supremainc.biostar2.sdk.models.v2.accesscontrol.ListAccessGroup;
import com.supremainc.biostar2.sdk.models.v2.user.BaseUser;
import com.supremainc.biostar2.sdk.provider.DateTimeDataProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class MobileCard implements Cloneable, Serializable {
    public static final String TAG = MobileCard.class.getSimpleName();
    private static final long serialVersionUID = 83776171965072428L;

    @SerializedName("id")
    public String id;
    @SerializedName("card_id")
    public String card_id;
    @SerializedName("issue_count")
    public String issue_count;
    @SerializedName("type")  // ACCESS_ON , SECURE
    public String type;
    @SerializedName("smart_card_layout")
    public SmartCardLayout smart_card_layout;

    @SerializedName("user")
    public BaseUser user;
    @SerializedName("expiry_datetime")
    private String expiry_datetime ;
    @SerializedName("start_datetime")
    private String start_datetime ;
    @SerializedName("access_groups")
    public ArrayList<ListAccessGroup> access_groups;
    @SerializedName("pin_exist")
    public boolean pin_exist  ;
    @SerializedName("fingerprint_index_list")
    public ArrayList<Integer> fingerprint_index_list;
    @SerializedName("is_registered")
    public boolean is_registered ;

    public static final String SECURE_CREDENTIAL = "SECURE_CREDENTIAL";
    public static final String ACCESS_ON = "ACCESS_ON";

    public MobileCard() {

    }
    public enum TimeType {start_datetime, expiry_datetime}

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

    public Calendar getTimeCalendar(DateTimeDataProvider convert, TimeType timeType) {
        return convert.convertServerTimeToCalendar(getTimeType(timeType), false);
    }


    public String getTimeFormmat(DateTimeDataProvider convert, TimeType timeType, DateTimeDataProvider.DATE_TYPE type) {
        Calendar cal = getTimeCalendar(convert, timeType);
        return convert.convertCalendarToFormatter(cal, type);
    }
    public MobileCard clone() throws CloneNotSupportedException {
        MobileCard target = (MobileCard) super.clone();
        if (access_groups != null) {
            target.access_groups =  (ArrayList<ListAccessGroup>)access_groups.clone();
        }
        if (user != null) {
            target.user =  user.clone();
        }
        if (smart_card_layout != null) {
            target.smart_card_layout =  smart_card_layout.clone();
        }
        if (fingerprint_index_list != null) {
            target.fingerprint_index_list =  (ArrayList<Integer>)fingerprint_index_list.clone();
        }
        return target;
    }
}
