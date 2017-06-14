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
package com.supremainc.biostar2.sdk.models.v2.fingerprint;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ListFingerprintTemplate extends BaseFingerprintTemplate implements Cloneable, Serializable {
    public static final String TAG = ListFingerprintTemplate.class.getSimpleName();
    private static final long serialVersionUID = -6798684884348157526L;
    @SerializedName("is_prepare_for_duress")
    public boolean is_prepare_for_duress;
    @SerializedName("template0")
    public String template0;
    @SerializedName("template1")
    public String template1;

    public ListFingerprintTemplate() {

    }

    public ListFingerprintTemplate(boolean duress, String template0, String template1) {
        is_prepare_for_duress = duress;
        this.template0 = template0;
        this.template1 = template1;
    }

    public ListFingerprintTemplate clone() throws CloneNotSupportedException {
        return (ListFingerprintTemplate) super.clone();
    }
}