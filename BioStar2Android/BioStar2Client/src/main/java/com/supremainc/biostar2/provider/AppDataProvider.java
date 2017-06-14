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

import android.app.Activity;
import android.content.Context;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.sdk.utils.PreferenceUtil;
import com.supremainc.biostar2.widget.popup.Popup;

public class AppDataProvider {
    public static final String DOOR_COUNT = "door_count";
    public static final String USER_COUNT = "user_count";
    public static final String BLE_RANGE = "ble_range";
    private final static String TAG = "AppDataProvider";
    private static final String LATEST_DOMAIN = "latest_domain";
    private static final String LATEST_ID = "latest_id";
    protected static Context mContext;
    protected static Activity mContext2;
    private static AppDataProvider mSelf = null;

    private AppDataProvider(Context context) {
        mContext = context;
    }

    public static AppDataProvider getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new AppDataProvider(context);
        }
        return mSelf;
    }

    public void set(Activity a) {
        mContext2 = a;
    }

    public void test(String tt) {
        Popup mPopup = new Popup(mContext2);
        mPopup.dismiss();
        mPopup.show(Popup.PopupType.ALERT, "test Popup", tt, null, mContext.getString(R.string.ok), null, false);
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

    public int getBleRange() {
        int result = PreferenceUtil.getIntSharedPreference(mContext, BLE_RANGE);
        if (result == -1) {
            return 300;
        }
        if (result < 101) {
            return 100;
        } else if (result < 301) {
            return 300;
        } else {
            return 500;
        }

        //      return result;
    }

    public void setBleRange(int total) {
        if (total < 101) {
            total = 100;
        } else if (total < 301) {
            total = 300;
        } else {
            total = 500;
        }
        PreferenceUtil.putSharedPreference(mContext, BLE_RANGE, total);
    }

    public void setBoolean(BooleanType type, boolean set) {
        PreferenceUtil.putSharedPreference(mContext, type.mName, set);
    }

    public boolean getBoolean(BooleanType type) {
        return PreferenceUtil.getBooleanSharedPreference(mContext, type.mName, true);
    }

    public enum BooleanType {
        SHOW_GUIDE_MENU_CARD("SHOW_GUIDE_MENU_CARD"), SHOW_GUIDE_DETAIL_CARD("SHOW_GUIDE_DETAIL_CARD"), MOBILE_CARD_NFC("MOBILE_CARD_NFC");
        public final String mName;

        private BooleanType(String name) {
            mName = name;
        }
    }
}
