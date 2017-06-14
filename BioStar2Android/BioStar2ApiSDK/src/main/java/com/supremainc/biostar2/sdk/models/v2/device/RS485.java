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

public class RS485  implements Cloneable, Serializable {
    public static final String TAG = RS485.class.getSimpleName();
    private static final long serialVersionUID = 7903248897122880854L;
    @SerializedName("baud_rate") // ['9600' or '19200' or '38400' or '57600' or '115200'],
    public String baud_rate;
    @SerializedName("mode") //  ['MASTER' or 'SLAVE' or 'DEFAULT']
    public String mode;
    public static final String MASTER = "MASTER";
    public static final String SLAVE = "SLAVE";
    public static final String DEFAULT = "DEFAULT";

    public RS485() {

    }

    public RS485 clone() throws CloneNotSupportedException {
        RS485 target = (RS485) super.clone();
        return target;
    }
}