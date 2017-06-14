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


package com.supremainc.biostar2.sdk.models.v2.accesscontrol;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ListAccessGroup extends BaseAccessGroup implements Cloneable, Serializable {
    public static final String TAG = ListAccessGroup.class.getSimpleName();
    private static final long serialVersionUID = -749247019171641814L;

    @SerializedName("included_by_user_group")
    public String included_by_user_group; //YES , NO , BOTH

    public static final String INCLUDEED_YES = "YES";
    public static final String INCLUDEED_NO = "NO";
    public static final String INCLUDEED_BOTH = "BOTH";

    public ListAccessGroup() {

    }

    public boolean isIncludedByUserGroup() {
        if (INCLUDEED_BOTH.equals(included_by_user_group)) {
            return true;
        }
        if (INCLUDEED_YES.equals(included_by_user_group)) {
            return true;
        }
        return false;
    }

    public ListAccessGroup clone() throws CloneNotSupportedException {
        ListAccessGroup target = (ListAccessGroup) super.clone();
        return target;
    }
}
