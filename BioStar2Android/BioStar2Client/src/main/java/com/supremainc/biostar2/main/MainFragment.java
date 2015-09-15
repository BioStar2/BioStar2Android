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
package com.supremainc.biostar2.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.base.BaseFragment;
import com.supremainc.biostar2.sdk.datatype.PermissionData.PERMISSION_MODULE;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainFragment extends BaseFragment {
    private MainFragmentLayout mLayout;
    private BroadcastReceiver mReceiverTick;
    private SimpleDateFormat mDateFormatter;
    private SimpleDateFormat mMarkerTimeFormatter;
    private SimpleDateFormat mTimeFormatter;
    private Locale mLocale;
    private Locale mkrLocale = Locale.KOREAN;
    private MainFragmentLayout.MainFragmentLayoutEvent mLayoutEvent = new MainFragmentLayout.MainFragmentLayoutEvent() {
        @Override
        public void onClickSetting() {
            mScreenControl.addScreen(ScreenType.PREFERENCE,null);
        }

        @Override
        public void onClickUser() {
            mScreenControl.gotoScreen(ScreenType.USER, null);
        }

        @Override
        public void onClickDoor() {
            mScreenControl.gotoScreen(ScreenType.DOOR_LIST, null);
        }

        @Override
        public void onClickMonitor() {
            mScreenControl.gotoScreen(ScreenType.MONITOR, null);
        }

        @Override
        public void onClickAlarm() {
            mScreenControl.gotoScreen(ScreenType.ALARM_LIST, null);
        }

        @Override
        public void onClickMenu() {
            mScreenControl.gotoScreen(ScreenType.OPEN_MENU, null);
        }
    };

    public MainFragment() {
        super();
        setType(ScreenType.MAIN);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void applyPermission() {
        if (mLayout == null) {
            return;
        }
        if (mPermissionDataProvider.getPermission(PERMISSION_MODULE.USER, false)) {
            mLayout.showUserMenu(true);
        } else {
            mLayout.showUserMenu(false);
        }

        if (mPermissionDataProvider.getPermission(PERMISSION_MODULE.DOOR, false)) {
            mLayout.showDoorMenu(true);
        } else {
            mLayout.showDoorMenu(false);
        }

        if (mPermissionDataProvider.getPermission(PERMISSION_MODULE.DOOR, true)) {
            mLayout.showAlarmMenu(true);
        } else {
            mLayout.showAlarmMenu(false);
        }
        if (mPermissionDataProvider.getPermission(PERMISSION_MODULE.MONITORING, false)) {
            mLayout.showMonitorMenu(true);
        } else {
            mLayout.showMonitorMenu(false);
        }
    }

    private void initValue(boolean isRefresh) {
        if (mLocale == null || isRefresh) {
            mLocale = getResources().getConfiguration().locale;
        }
        if (mMarkerTimeFormatter == null || isRefresh) {
            mMarkerTimeFormatter = new SimpleDateFormat("a", mLocale);
        }
        if (mTimeFormatter == null || isRefresh) {
            mTimeFormatter = new SimpleDateFormat("hh:mm", mLocale);
        }
        if (mDateFormatter == null || isRefresh) {
            String userConfig = mCommonDataProvider.getDateFormat(mContext);
            if (userConfig != null) {
                userConfig = userConfig.replaceAll("yyyy/", "");
                userConfig = userConfig.replaceAll("/yyyy", "");
                mDateFormatter = new SimpleDateFormat(userConfig + ", EEE", mLocale);
            } else {
                mDateFormatter = new SimpleDateFormat("MM/dd" + ", EEE", mLocale);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        setTime();
    }

    @Override
    public void onDestroy() {
        if (mReceiverTick != null) {
            getActivity().unregisterReceiver(mReceiverTick);
            mReceiverTick = null;
        }
        super.onDestroy();
    }

    protected void registerBroadcast() {
        if (mReceiverTick == null) {
            mReceiverTick = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (mIsDestroy) {
                        return;
                    }
                    final String action = intent.getAction();

                    if (action.equals(Intent.ACTION_TIME_TICK)) {
                        setTime();
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_TIME_TICK);
            getActivity().registerReceiver(mReceiverTick, intentFilter);
        }
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (isInValidCheck(null)) {
                        return;
                    }
                    String action = intent.getAction();
                    if (action.equals(Setting.BROADCAST_REROGIN)) {
                        applyPermission();
                    } else if (action.equals(Setting.BROADCAST_ALARM_UPDATE)) {
                        if (mLayout != null) {
                            mLayout.setAlarmCount();
                        }
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_REROGIN);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mLayout == null) {
            mLayout = new MainFragmentLayout(this, mLayoutEvent);
        }
        View view = mLayout.initView(this, inflater, container, savedInstanceState);
        initBaseValue(mLayout);
        if (!mLayout.isReUsedView()) {
            initValue(false);
            applyPermission();
        }
        return view;
    }

    private void setTime() {
        if (mLayout == null) {
            return;
        }
        Locale locale = getResources().getConfiguration().locale;
        String language = mLocale.getLanguage();
        Date date = new Date();

        if (language == null) {
            mLayout.setDateTimeWidget(mMarkerTimeFormatter.format(date), mDateFormatter.format(date), mTimeFormatter.format(date));
            return;
        }

        if (!language.equals(locale.getLanguage())) {
            initValue(true);
        }
        String dateString = mDateFormatter.format(date);
        if (mkrLocale != null) {
            if (language.equals(mkrLocale.getLanguage())) {
                dateString = mDateFormatter.format(date) + getString(R.string.korean_day);
            }
        }
        ;
        mLayout.setDateTimeWidget(mMarkerTimeFormatter.format(date), dateString, mTimeFormatter.format(date));
    }
}
