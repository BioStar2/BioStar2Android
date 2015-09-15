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
package com.supremainc.biostar2.popup;

import java.io.Serializable;

public class SelectCustomData implements Cloneable, Serializable {
    private static final long serialVersionUID = 6226937693819768375L;
    public String mId;
    public boolean mIsSelected;
    public String mTitle;

    public SelectCustomData(String title, String id, boolean selected) {
        mTitle = title;
        mId = id;
        mIsSelected = selected;
    }

    public SelectCustomData(String title, int id, boolean selected) {
        mTitle = title;
        mId = String.valueOf(id);
        mIsSelected = selected;
    }

    public SelectCustomData clone() throws CloneNotSupportedException {
        SelectCustomData target = (SelectCustomData) super.clone();
        return target;
    }

    public int getIntId() {
        try {
            return Integer.valueOf(mId);
        } catch (Exception e) {
            return -1;
        }
    }

    public String getStringId() {
        return mId;
    }


}
