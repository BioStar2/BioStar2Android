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
import java.util.ArrayList;

public class CloudPermission implements Cloneable, Serializable {

    private static final long serialVersionUID = 3317217458562384566L;
    @SerializedName("module")
    public String module;
    @SerializedName("read")
    public boolean read;
    @SerializedName("write")
    public boolean write;
    @SerializedName("url")
    public String url;
    @SerializedName("allowed_group_id_list")
    public ArrayList<String> allowed_group_id_list;

    public static final String ROLE_READ = "read";
    public static final String ROLE_WRITE = "write";

    public CloudPermission clone() throws CloneNotSupportedException {
        return (CloudPermission) super.clone();
    }
}