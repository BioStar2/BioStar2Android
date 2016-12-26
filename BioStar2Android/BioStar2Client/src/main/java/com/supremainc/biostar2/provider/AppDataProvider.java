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
package com.supremainc.biostar2.provider;

import android.content.Context;
import com.supremainc.biostar2.sdk.utils.PreferenceUtil;

public class AppDataProvider {
    public static final String DOOR_COUNT = "door_count";
    public static final String USER_COUNT = "user_count";
    private static final String LATEST_DOMAIN = "latest_domain";
    private static final String LATEST_ID = "latest_id";
    protected static Context mContext;
    private static AppDataProvider mSelf = null;

    public enum BooleanType {
        SHOW_GUIDE_MENU_CARD("SHOW_GUIDE_MENU_CARD"),SHOW_GUIDE_DETAIL_CARD("SHOW_GUIDE_DETAIL_CARD");
        public final String mName;
        private BooleanType(String name) {
            mName = name;
        }
    }

    private AppDataProvider(Context context) {
        mContext = context;
    }

    public static AppDataProvider getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new AppDataProvider(context);
        }
        return mSelf;
    }

    public int getDoorCount() {
        return PreferenceUtil.getIntSharedPreference(mContext, DOOR_COUNT);
    }

    public void setDoorCount(int total) {
        PreferenceUtil.putSharedPreference(mContext, DOOR_COUNT, total);
    }

    public int getUserCount() {
        return PreferenceUtil.getIntSharedPreference(mContext, USER_COUNT);
    }

    public void setUserCount(int total) {
        PreferenceUtil.putSharedPreference(mContext, USER_COUNT, total);
    }

    public void setBoolean(BooleanType type,boolean set) {
        PreferenceUtil.putSharedPreference(mContext,type.mName,set);
    }
    public boolean getBoolean(BooleanType type) {
       return  PreferenceUtil.getBooleanSharedPreference(mContext,type.mName,true);
    }

}
