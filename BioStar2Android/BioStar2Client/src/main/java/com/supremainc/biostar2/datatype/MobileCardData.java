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
package com.supremainc.biostar2.datatype;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MobileCardData {
    public static class MobileCards implements Cloneable, Serializable {
        public static final String TAG = MobileCards.class.getSimpleName();
        private static final long serialVersionUID = -4759710282370331708L;
        @SerializedName("status_code")
        public String statusCode;
        @SerializedName("message")
        public String message;
        @SerializedName("mobile_credential_list")
        public ArrayList<MobileCard> records;
        @SerializedName("total")
        public int total;

        public MobileCards clone() throws CloneNotSupportedException {
            MobileCards target = (MobileCards) super.clone();
            if (records != null) {
                target.records = (ArrayList<MobileCard>) records.clone();
            }
            return target;
        }
    }

    public static class MobileCard implements Cloneable, Serializable {
        public static final String TAG = MobileCard.class.getSimpleName();
        private static final long serialVersionUID = -2641432256091875738L;

        public String cardID;
        public int templateCount;
        public boolean isExistPin;
        public int cardType; //sec or AOC
        //AOC
        public ArrayList<String> accessGroups;
        public String startDateTime;
        public String endDateTime;
        public String type; //발급대기중인지 아니면 발급된 device identi

        public MobileCard clone() throws CloneNotSupportedException {
            MobileCard target = (MobileCard) super.clone();
            if (accessGroups != null) {
                target.accessGroups = (ArrayList<String>) accessGroups.clone();
            }
            return target;
        }
    }


}
