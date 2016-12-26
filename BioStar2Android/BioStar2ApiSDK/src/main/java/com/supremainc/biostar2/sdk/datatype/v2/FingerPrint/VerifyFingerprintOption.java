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
package com.supremainc.biostar2.sdk.datatype.v2.FingerPrint;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class  VerifyFingerprintOption implements Cloneable, Serializable {
    private static final long serialVersionUID = 867494139108960788L;
    @SerializedName("security_level")
    public String security_level;
    @SerializedName("template0")
    public String template0;
    @SerializedName("template1")
    public String template1;

    public VerifyFingerprintOption() {

    }

    public VerifyFingerprintOption(String security_level, String template0, String template1) {
        this.security_level = security_level;
        this.template0 = template0;
        this.template1 = template1;
    }

    public VerifyFingerprintOption clone() throws CloneNotSupportedException {
        return (VerifyFingerprintOption) super.clone();
    }
}

