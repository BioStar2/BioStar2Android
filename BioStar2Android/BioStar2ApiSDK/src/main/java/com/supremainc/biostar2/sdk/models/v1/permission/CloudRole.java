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
package com.supremainc.biostar2.sdk.models.v1.permission;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CloudRole implements Cloneable, Serializable {

    private static final long serialVersionUID = 2293738419761800316L;
    /**
     * ( 'ADMIN', 'USER_ADMIN', 'MONITORING' ,'DEFAULT_USER'),
     */
    @SerializedName("code")
    public String code;
    @SerializedName("description")
    public String description;

    public CloudRole clone() throws CloneNotSupportedException {
        CloudRole target = (CloudRole) super.clone();
        return target;
    }
}
