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

import com.supremainc.biostar2.sdk.provider.UserDataProvider;
import com.supremainc.biostar2.sdk.utils.PreferenceUtil;

public class AppDataProvider {
    protected static Context mContext;
    private static AppDataProvider mSelf = null;
    private static final String LATEST_DOMAIN = "latest_domain";
    private static final String LATEST_ID = "latest_id";
    public static final String DOOR_COUNT = "door_count";
    public static final String USER_COUNT = "user_count";
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

    public String getLatestDomain() {
        String subDomain = PreferenceUtil.getSharedPreference(mContext, LATEST_DOMAIN);
        UserDataProvider.getInstance(mContext).setSubDomain(subDomain);
        return subDomain;
    }

    public void setLatestDomain(String subDomain) {
        UserDataProvider.getInstance(mContext).setSubDomain(subDomain);
        PreferenceUtil.putSharedPreference(mContext, LATEST_DOMAIN, subDomain);
    }

    public String getLatestUserID() {
        return PreferenceUtil.getSharedPreference(mContext, LATEST_ID);
    }

    public void setLatestUserID(String id) {
        PreferenceUtil.putSharedPreference(mContext, LATEST_ID, id);
    }

    public int getUserCount() {
        return PreferenceUtil.getIntSharedPreference(mContext, USER_COUNT);
    }

    public void setUserCount(int total) {
        PreferenceUtil.putSharedPreference(mContext, USER_COUNT, total);
    }
}
