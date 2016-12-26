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

package com.supremainc.biostar2.sdk.datatype.v2.AccessControl;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class BaseAccessLevel  implements Cloneable, Serializable {
    public static final String TAG = BaseAccessLevel.class.getSimpleName();
    private static final long serialVersionUID = 976825472383889913L;
    @SerializedName("status_code")
    public String statusCode;
    @SerializedName("message")
    public String message;

    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("door_description")
    public String door_description;
    @SerializedName("schedule_description")
    public String schedule_description;

    public BaseAccessLevel() {

    }

    public BaseAccessLevel clone() throws CloneNotSupportedException {
        BaseAccessLevel target = (BaseAccessLevel) super.clone();
        return target;
    }
}
