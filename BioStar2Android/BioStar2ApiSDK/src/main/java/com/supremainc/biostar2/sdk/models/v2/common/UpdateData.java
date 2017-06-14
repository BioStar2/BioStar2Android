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
package com.supremainc.biostar2.sdk.models.v2.common;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UpdateData implements Cloneable, Serializable {
    private static final long serialVersionUID = -7069121470356616723L;
    @SerializedName("name")
    public String name;
    @SerializedName("status_code")
    public String status_code;
    @SerializedName("latest_version")
    public int version;
    @SerializedName("force_update_version")
    public int forceVersion;
    @SerializedName("message")
    public String message;
    @SerializedName("version_message")
    public String version_message;
    @SerializedName("mobile_device_type")
    public String mobile_device_type;

    @SerializedName("app_store_download_url")
    public String url;
    @SerializedName("direct_download_url")
    public String url2;

    public UpdateData() {
        mobile_device_type = "ANDROID";
    }

    public UpdateData clone() throws CloneNotSupportedException {
        return (UpdateData) super.clone();
    }
}
