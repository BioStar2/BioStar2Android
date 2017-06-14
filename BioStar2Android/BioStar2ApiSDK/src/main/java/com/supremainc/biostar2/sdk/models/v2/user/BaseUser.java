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
package com.supremainc.biostar2.sdk.models.v2.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public  class BaseUser implements Cloneable, Serializable {
    public static final String TAG = BaseUser.class.getSimpleName();
    private static final long serialVersionUID = -2556697698243014612L;

    @SerializedName("status_code")
    public String status_code;
    @SerializedName("message")
    public String message;

    @SerializedName("user_id")
    public String user_id;
    @SerializedName("name")
    public String name;

    public BaseUser() {

    }

    public String getName() {
        if (name == null || name.isEmpty()) {
            if (user_id == null) {
                return null;
            }
            return String.valueOf(user_id);
        }
        return name;
    }

    public BaseUser clone() throws CloneNotSupportedException {
        BaseUser target = (BaseUser) super.clone();
        return target;
    }
}