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
package com.supremainc.biostar2.datatype;


public class DoorDetailData {
    public enum DoorDetailType {
        NOTIFICATION_TIME, USER, TELEPHONE, ENTRY_DEVICE, EXIT_DEVICE, RELAY, EXIT_BUTTON, OPEN_TIME
    }

    public static class DoorDetail {
        public static final String TAG = DoorDetail.class.getSimpleName();
        public String title;
        public String content;
        public boolean link;
        public DoorDetailType type;

        public DoorDetail(String title, String content, boolean link, DoorDetailType type) {
            this.title = title;
            this.content = content;
            this.link = link;
            this.type = type;
        }
    }
}
