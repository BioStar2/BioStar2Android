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
package com.supremainc.biostar2.sdk.models.v2.login;

public enum NotificationType {
    DEVICE_REBOOT("device_reboot"), DEVICE_RS485_DISCONNECT("device_rs485_disconnect"),
    DEVICE_TAMPERING("device_tampering"), DOOR_FORCED_OPEN("door_forced_open"), DOOR_HELD_OPEN("door_held_open"), DOOR_OPEN_REQUEST("door_open_request"), ZONE_APB("zone_apb"), ZONE_FIRE("zone_fire");
    public final String mName;

    private NotificationType(String name) {
        mName = name;
    }
}