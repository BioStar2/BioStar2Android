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
package com.supremainc.biostar2.sdk.models.v2.permission;

public enum PermissionModule {
    USER("USER"), USER_GROUP("USER_GROUP"), DEVICE("DEVICE"), DEVICE_GROUP("DEVICE_GROUP"), DOOR("DOOR"), DOOR_GROUP("DOOR_GROUP"), ELEVATOR("ELEVATOR"), ZONE("ZONE"), ACCESS_GROUP(
            "ACCESS_GROUP"), ACCESS_LEVEL("ACCESS_LEVEL"), MONITORING("MONITORING"), TA("TA"), ADMIN("ADMIN"), ACCOUNT("ACCOUNT"),CARD("CARD"), HOLIDAY("HOLIDAY"), SETTING(
            "SETTING"), SCHEDULE("SCHEDULE"), PERMISSION("PERMISSION");


    public final String mName;

    private PermissionModule(String name) {
        this.mName = name;
    }
}