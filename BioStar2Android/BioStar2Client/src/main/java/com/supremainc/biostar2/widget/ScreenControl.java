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
package com.supremainc.biostar2.widget;


import android.content.Context;
import android.os.Bundle;

import com.supremainc.biostar2.fragment.BaseFragment;
import com.supremainc.biostar2.view.DrawLayerMenuView;

public class ScreenControl {
    private static DrawLayerMenuView.OnSelectionListener mDrawMenuSelectionListener;
    //	private final String TAG = getClass().getSimpleName();
    private static ScreenControl mSelf = null;

    private ScreenControl() {
    }

    public static ScreenControl getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new ScreenControl();
        }
        return mSelf;
    }

    public static ScreenControl getInstance() {
        if (mSelf == null) {
            mSelf = new ScreenControl();
        }
        return mSelf;
    }

    public void addScreen(ScreenType type, Bundle args) {
        if (mDrawMenuSelectionListener == null) {
            return;
        }
        mDrawMenuSelectionListener.addScreen(type, args);
    }

    public void addScreenNoEffect(ScreenType type, Bundle args) {
        if (mDrawMenuSelectionListener == null) {
            return;
        }
        mDrawMenuSelectionListener.addScreenNoEffect(type, args);
    }

    public void backScreen() {
        if (mDrawMenuSelectionListener == null) {
            return;
        }
        mDrawMenuSelectionListener.backScreen();
    }

    public void drawMenu() {
        if (mDrawMenuSelectionListener == null) {
            return;
        }
        mDrawMenuSelectionListener.drawMenu();
    }

    public ScreenType getTypeValue(int type) {
        ScreenType[] temp = ScreenType.values();
        if (type < 1) {
            return ScreenType.MAIN;
        }
        if (type >= temp.length) {
            return ScreenType.MAIN;
        }
        return temp[type];
    }

    public void gotoScreen(ScreenType type, Bundle args) {
        if (mDrawMenuSelectionListener == null) {
            return;
        }
        mDrawMenuSelectionListener.onSelected(type, args);
    }

    public void onResume(BaseFragment baseFragment) {
        if (mDrawMenuSelectionListener == null) {
            return;
        }
        mDrawMenuSelectionListener.onResume(baseFragment);
    }

    public void setScreenSelectionListeneer(DrawLayerMenuView.OnSelectionListener mDrawMenuSelectionListener) {
        this.mDrawMenuSelectionListener = mDrawMenuSelectionListener;
    }

    public enum ScreenType {
        INIT, MAIN, USER, USER_MODIFY, USER_INQURIY, USER_ACCESS_GROUP, DOOR_LIST, DOOR, MONITOR, CARD, CARD_RIGISTER, MOBILE_CARD_LIST, FACE,
        FINGERPRINT_REGISTER, ALARM, ALARM_LIST, TA, LOG_OUT, ACCESS_CONTROL, ACCESS_GROUP_MODIFY, ACCESS_LEVEL_MODIFY, OPEN_MENU, PREFERENCE, ACCESS_GROUP_SELECT, MYPROFILE, USER_PERMISSION
    }

}
