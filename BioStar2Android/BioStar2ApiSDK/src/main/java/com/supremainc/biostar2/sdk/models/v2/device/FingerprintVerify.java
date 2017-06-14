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
package com.supremainc.biostar2.sdk.models.v2.device;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FingerprintVerify implements Cloneable, Serializable {
    private static final long serialVersionUID = -7857943932763810738L;
    @SerializedName("status_code")
    public String status_code;
    @SerializedName("message")
    public String message;
    @SerializedName("verify_result")
    public boolean verify_result;

    public FingerprintVerify() {

    }

    public FingerprintVerify clone() throws CloneNotSupportedException {
        FingerprintVerify target = (FingerprintVerify) super.clone();
        return target;
    }
}