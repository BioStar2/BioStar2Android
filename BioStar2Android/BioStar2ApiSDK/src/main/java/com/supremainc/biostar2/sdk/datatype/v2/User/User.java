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
package com.supremainc.biostar2.sdk.datatype.v2.User;

import com.google.gson.annotations.SerializedName;
import com.supremainc.biostar2.sdk.datatype.v1.Permission.CloudPermission;
import com.supremainc.biostar2.sdk.datatype.v1.Permission.CloudRole;
import com.supremainc.biostar2.sdk.datatype.v2.Card.ListCard;
import com.supremainc.biostar2.sdk.datatype.v2.FingerPrint.ListFingerprintTemplate;
import com.supremainc.biostar2.sdk.datatype.v2.Permission.UserPermission;
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class User extends ListUser implements Cloneable, Serializable {
    public static final String TAG = User.class.getSimpleName();
    private static final long serialVersionUID = 2362983837465365019L;

    @SerializedName("roles") // 2.3.0
    public ArrayList<CloudRole> roles;
    @SerializedName("permissions") // 2.3.0
    public ArrayList<CloudPermission> permissions;

    @SerializedName("password")
    public String password;
    @SerializedName("security_level")
    public String security_level = "0";
    @SerializedName("pin")
    public String pin;
    @SerializedName("photo")
    public String photo;
    @SerializedName("login_id")
    public String login_id;
    /**
     * ['0 = none' or '1 = male' or '2 = female']
     */

    @SerializedName("phone_number")
    public String phone_number;
    /**
     * ( 'AC' = Active, 'IN' = Inactive ),
     */
    @SerializedName("status")
    public String status;
    @SerializedName("fingerprint_templates") // only 2.3.0
    public ArrayList<ListFingerprintTemplate> fingerprint_templates;
    @SerializedName("cards") // only 2.3.0
    public ArrayList<ListCard> cards;
    @SerializedName("password_exist")
    public boolean password_exist;

    @SerializedName("permission") // 2.4.0
    public UserPermission permission;
    @SerializedName("password_strength_level")
    public String password_strength_level;
    @SerializedName("start_datetime")
    private String start_datetime;
    @SerializedName("expiry_datetime")
    private String expiry_datetime;

    public static final String USER_STATUS_ACTIVE = "AC";
    public static final String USER_STATUS_INACTIVE = "IN";

    public User() {

    }

    public void setDefaultValue() {
        if (ConfigDataProvider.TEST_RELEASE_DELETE) {
            user_group = new BaseUserGroup();
            user_group.setDefaultValue();
        }
        setActive(true);
    }

    public void backup() {
        pin_exist_backup = pin_exist;
    }

    public boolean isActive() {
        if (status == null) {
            return false;
        }
        if (status.equals(USER_STATUS_ACTIVE)) {
            return true;
        }
        return false;
    }

    public void setActive(boolean enable) {
        if (enable) {
            status = USER_STATUS_ACTIVE;
            return;
        }
        status = USER_STATUS_INACTIVE;
    }

    public ArrayList<ListFingerprintTemplate> getFingerprintTemplates() {
        if (fingerprint_templates != null) {
            return fingerprint_templates;
        }
        return null;
    }

    public void setFingerprintTemplates(ArrayList<ListFingerprintTemplate> fingerprint_templates) {
        this.fingerprint_templates = fingerprint_templates;
    }

    public String getTimeType(UserTimeType type) {
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

    public boolean setTimeType(UserTimeType type, String src) {
        if (src == null || src.isEmpty()) {
            return false;
        }
        switch (type) {
            case start_datetime:
                start_datetime = src;
                break;
            case expiry_datetime:
                expiry_datetime = src;
                break;
            default:
                return false;
        }
        return true;
    }

    public Calendar getTimeCalendar(TimeConvertProvider convert, UserTimeType timeType) {
        return convert.convertServerTimeToCalendar(getTimeType(timeType), false);
    }

    public boolean setTimeCalendar(TimeConvertProvider convert, UserTimeType timeType, Calendar cal) {
        switch (timeType) {
            case expiry_datetime:
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                break;
            default:
                break;
        }
        return setTimeType(timeType, convert.convertCalendarToServerTime(cal, false));
    }

    public String getTimeFormmat(TimeConvertProvider convert, UserTimeType timeType, TimeConvertProvider.DATE_TYPE type) {
        Calendar cal = getTimeCalendar(convert, timeType);
        return convert.convertCalendarToFormatter(cal, type);
    }

    public boolean setTimeFormmat(TimeConvertProvider convert, UserTimeType timeType, TimeConvertProvider.DATE_TYPE type, String src) {
        Calendar cal = convert.convertFormatterToCalendar(src, type);
        return setTimeCalendar(convert, timeType, cal);
    }

    @SuppressWarnings("unchecked")
    public User clone() throws CloneNotSupportedException {
        User target = (User) super.clone();

        if (fingerprint_templates != null) {
            target.fingerprint_templates = (ArrayList<ListFingerprintTemplate>) fingerprint_templates.clone();
        }
        if (cards != null) {
            target.cards = (ArrayList<ListCard>) cards.clone();
        }
        if (permission != null) {
            target.permission = (UserPermission) permission.clone();
        }
        return target;
    }


    public enum UserTimeType {start_datetime, expiry_datetime}
}
