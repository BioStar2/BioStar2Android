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
import android.content.pm.PackageManager;

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
    private static final int mVersion = 1;

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
        if (result < 100) {
            return 100;
        } else if (result > 1000) {
            return 1000;
        }
        return result;
        //      return result;
    }

    public void setBleRange(int total) {
        if (total < 100) {
            total = 100;
        } else if (total > 1000) {
            total = 1000;
        }
        PreferenceUtil.putSharedPreference(mContext, BLE_RANGE, total);
    }

    public void setBoolean(BooleanType type, boolean set) {
        PreferenceUtil.putSharedPreference(mContext, type.mName, set);
    }

    public boolean getBoolean(BooleanType type,boolean defaultValue) {
        return PreferenceUtil.getBooleanSharedPreference(mContext, type.mName, defaultValue);
    }

    public void setInitValue() {
        if (PreferenceUtil.getIntSharedPreference(mContext,"init_value") >= mVersion) {
            return;
        }
        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)) {
            setBoolean(AppDataProvider.BooleanType.MOBILE_CARD_NFC, true);
        } else if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            setBoolean(BooleanType.MOBILE_CARD_BLE, true);
        }
        setBoolean(AppDataProvider.BooleanType.KNOCK_KNOCK,true);
        setBoolean(AppDataProvider.BooleanType.INDEPENDENT_SCREEN_LOCK,true);
        PreferenceUtil.putSharedPreference(mContext,"init_value",mVersion);
    }

    public enum BooleanType {
        SHOW_GUIDE_MENU_CARD("SHOW_GUIDE_MENU_CARD"), SHOW_GUIDE_DETAIL_CARD("SHOW_GUIDE_DETAIL_CARD"), MOBILE_CARD_NFC("MOBILE_CARD_NFC"),NEED_UPDATE_TOKEN("NEED_UPDATE_TOKEN"),
        INDEPENDENT_SCREEN_LOCK("INDEPENDENT_SCREEN_LOCK"),MOBILE_CARD_BLE("MOBILE_CARD_BLE"),KNOCK_KNOCK("KNOCK_KNOCK");
        public final String mName;

        private BooleanType(String name) {
            mName = name;
        }
    }
}
