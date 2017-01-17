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
package com.supremainc.biostar2.sdk.datatype.v2.Device;

import com.google.gson.annotations.SerializedName;
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;

import java.io.Serializable;
import java.util.ArrayList;

public class ListDeviceType extends BaseDeviceType implements Cloneable, Serializable {
    private static final long serialVersionUID = 2292389938409217747L;
    @SerializedName("input_port_num")
    public long input_port_num;
    @SerializedName("relay_num")
    public long relay_num;
    @SerializedName("type")
    public int type;
    @SerializedName("scan_card")
    public boolean scan_card;
    @SerializedName("scan_fingerprint")
    public boolean scan_fingerprint;
    @SerializedName("scan_face")
    public boolean scan_face;

    public static final int SUPPORT_DISPLAY = 0;
    public static final int SUPPORT_BLACKFIN = 1;
    public static final int SUPPORT_FINGERPRINT = 2;
    public static final int SUPPORT_CARD = 4;
    public static final int SUPPORT_CARD_ONLY = 8;
    public static final int SUPPORT_KEYPAD = 16;
    public static final int SUPPORT_VOLUME = 32;
    public static final int SUPPORT_ONLY_SLAVE = 64;
    public static final int SUPPORT_FACE = 128;

    public ListDeviceType clone() throws CloneNotSupportedException {
        ListDeviceType target = (ListDeviceType) super.clone();
        return target;
    }

    public boolean isSupport(int deviceSupport) {
        if (ConfigDataProvider.TEST_RELEASE_DELETE) {
            int support = 0;
            if (id != null) {
                int numberID = Integer.valueOf(id);
                switch (numberID) {
                    case 1:
                    case 2:
                        support = com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_BLACKFIN | com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_FINGERPRINT | com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_CARD;
                        break;
                    case 3:
                        support = com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_DISPLAY | com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_BLACKFIN | com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_FINGERPRINT | com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_CARD | com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_KEYPAD;
                        break;
                    case 4:
                    case 5:
                        support = com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_BLACKFIN | com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_CARD | com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_CARD_ONLY;
                        break;
                    case 6:
                    case 7:
                        support = com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_ONLY_SLAVE;
                        break;
                    case 8:
                    case 9:
                    case 10:
                        support = com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_DISPLAY | com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_FINGERPRINT | com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_CARD | com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_KEYPAD | com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_VOLUME;
                        break;
                    default:
                        break;
                }
            }
            if (scan_card) {
                support = support | com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_CARD;
            }
            if (scan_fingerprint) {
                support = support | com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_FINGERPRINT;
            }
            if (scan_face) {
                support = support | com.supremainc.biostar2.sdk.datatype.v2.Device.ListDeviceType.SUPPORT_FACE;
            }
            int result = support & deviceSupport;
            if (result == deviceSupport) {
                return true;
            } else {
                return false;
            }
        } else {
            int result = type & deviceSupport;
            if (result == deviceSupport) {
                return true;
            } else {
                return false;
            }
        }

    }
}