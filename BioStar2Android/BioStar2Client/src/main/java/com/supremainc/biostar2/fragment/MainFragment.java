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
package com.supremainc.biostar2.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.sdk.datatype.v2.Common.VersionData;
import com.supremainc.biostar2.sdk.datatype.v2.Permission.PermissionModule;
import com.supremainc.biostar2.util.Utils;
import com.supremainc.biostar2.view.MainMenuView;
import com.supremainc.biostar2.view.MenuItemView;
import com.supremainc.biostar2.view.RingTimeView;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainFragment extends BaseFragment {
    private BroadcastReceiver mReceiverTick;
    private SimpleDateFormat mDateFormatter;
    private SimpleDateFormat mMarkerTimeFormatter;
    private SimpleDateFormat mTimeFormatter;
    private Locale mLocale;
    private Locale mkrLocale = Locale.KOREAN;
    private View mContainerRingTimeView;
    private View mTopMenuView;
    private View mLogoView;
    private RingTimeView mRingTimeView;
    private MainMenuView mMainMenuView;
    private ViewGroup mGuideView;
    private MainMenuView.MainMenuViewListener mMainMenuViewListener = new MainMenuView.MainMenuViewListener() {
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
        public void onClickMyProfile() {
            mScreenControl.gotoScreen(ScreenType.MYPROFILE, null);
        }

        @Override
        public void onClickMobileCard() {
            mScreenControl.gotoScreen(ScreenType.MOBILE_CARD_LIST, null);
        }
    };
    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()) {
                case R.id.side_menu:
                    mScreenControl.gotoScreen(ScreenType.OPEN_MENU, null);
                    break;
            }
        }
    };

    public MainFragment() {
        super();
        setType(ScreenType.MAIN);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void applyPermission() {
        if (mMainMenuView == null) {
            return;
        }
        mMainMenuView.removeAllMenuItem();
        mMainMenuView.addMenu(MenuItemView.MenuItemType.MY_PROFILE);
        if (mPermissionDataProvider.getPermission(PermissionModule.USER, false) || mPermissionDataProvider.getPermission(PermissionModule.USER_GROUP, false)) {
            mMainMenuView.addMenu(MenuItemView.MenuItemType.USER);
        }

        if (mPermissionDataProvider.getPermission(PermissionModule.DOOR, false) || mPermissionDataProvider.getPermission(PermissionModule.DOOR_GROUP, false)) {
            mMainMenuView.addMenu(MenuItemView.MenuItemType.DOOR);
          }

        if (mPermissionDataProvider.getPermission(PermissionModule.DOOR, true) || mPermissionDataProvider.getPermission(PermissionModule.DOOR_GROUP, true)) {
            mMainMenuView.addMenu(MenuItemView.MenuItemType.ALARM);
        }

        if (mPermissionDataProvider.getPermission(PermissionModule.MONITORING, false)) {
            mMainMenuView.addMenu(MenuItemView.MenuItemType.MONITORING);
        }

        //mMainMenuView.addMenu(MenuItemView.MenuItemType.MOBILE_CARD_ALERT);
//        if (VersionData.getCloudVersion(mContext) > 1) {
//            mMainMenuView.addMenu(MenuItemView.MenuItemType.MOBILE_CARD);
//        }
        mMainMenuView.showMenuItem();
        judgeShowRing();
    }

    private void initValue(boolean isRefresh) {
        if (mLogoView == null) {
            mLogoView = mRootView.findViewById(R.id.main_logo);
        }
        if (mTopMenuView == null) {
            mTopMenuView = mRootView.findViewById(R.id.top_menu);
        }
        if (mRingTimeView == null) {
            mRingTimeView = (RingTimeView) mRootView.findViewById(R.id.ring_time);
        }
        if (mContainerRingTimeView == null) {
            mContainerRingTimeView =  mRootView.findViewById(R.id.ring_time_container);
        }
        if (mMainMenuView == null) {
            mMainMenuView = (MainMenuView) mRootView.findViewById(R.id.main_menu);
            mMainMenuView.init(mMainMenuViewListener);
        }
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
            String userConfig = mCommonDataProvider.getDateFormat();
            if (userConfig != null) {
                userConfig = userConfig.replaceAll("yyyy/", "");
                userConfig = userConfig.replaceAll("/yyyy", "");
                mDateFormatter = new SimpleDateFormat(userConfig + ", EEE", mLocale);
            } else {
                mDateFormatter = new SimpleDateFormat("MM/dd" + ", EEE", mLocale);
            }
        }
        mRootView.findViewById(R.id.side_menu).setOnClickListener(mClickListener);
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
        if (mMainMenuView != null) {
            mMainMenuView.setAlarmCount(mNotificationDBProvider.getUnReadMessageCount());
        }
        judgeShowRing();
    }

    private void judgeShowRing() {
        if (mRingTimeView == null ||  mIsDestroy) {
            return;
        }
        mMainMenuView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mTopMenuView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mLogoView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int otherSize = mTopMenuView.getMeasuredHeight() + mLogoView.getMeasuredHeight() + mMainMenuView.getMeasuredHeight();
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float deviceHeight = Utils.convertPixelsToDp(metrics.heightPixels,mContext);
        float otherHeight = Utils.convertPixelsToDp(otherSize,mContext)+(float)134.69;
        float height = deviceHeight -  otherHeight;
        if (BuildConfig.DEBUG) {
            Log.i(TAG,"deviceHeight:"+deviceHeight+ " otherHeight:"+otherHeight+" height:"+height);
        }
        mRingTimeView.setAdjustHeight(height);
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
                        if (mMainMenuView != null) {
                            mMainMenuView.setAlarmCount(mNotificationDBProvider.getUnReadMessageCount());
                        }
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_REROGIN);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
    }

    private Runnable mRunnableGuide = new Runnable() {
        @Override
        public void run() {
            if (isInValidCheck(null)) {
                return;
            }
            View v = mMainMenuView.getItemView(MenuItemView.MenuItemType.MOBILE_CARD_ALERT);
            if (v != null && mAppDataProvider.getBoolean(AppDataProvider.BooleanType.SHOW_GUIDE_MENU_CARD)) {
                if (mGuideView == null) {
                    int w = v.getWidth();
                    int h = v.getHeight();
                    if (w == 0 || h == 0) {
                        mHandler.removeCallbacks(mRunnableGuide);
                        mHandler.postDelayed(mRunnableGuide,1000);
                        return;
                    }
                    int[] position = {0, 0};
                    v.getLocationOnScreen(position);
                    LinearLayout.LayoutParams containerParam = new LinearLayout.LayoutParams(v.getWidth() + 30, v.getHeight() + 60);
                    FrameLayout.LayoutParams itemParam = new FrameLayout.LayoutParams(w, h, Gravity.CENTER);

                    FrameLayout containerView = new FrameLayout(mContext);
                    containerView.setBackgroundResource(R.drawable.dash);
                    View itemView = mMainMenuView.createMenuView(MenuItemView.MenuItemType.MOBILE_CARD_ALERT);
                    containerView.addView(itemView, itemParam);

                    mGuideView = (ViewGroup) mInflater.inflate(R.layout.view_guide, null, false);
                    StyledTextView guideText = (StyledTextView)mGuideView.findViewById(R.id.guide_text);
                    guideText.setText(getString(R.string.guide_register_mobile_card1)+"\n"+getString(R.string.guide_register_mobile_card2));
                    mGuideView.findViewById(R.id.close_guide).setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            removeGuideView();
                        }
                    });
                    mGuideView.findViewById(R.id.close_alaways).setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            removeGuideView();
                            mAppDataProvider.setBoolean(AppDataProvider.BooleanType.SHOW_GUIDE_MENU_CARD,false);
                        }
                    });
                    mGuideView.setOnClickListener(new OnSingleClickListener(){
                        @Override
                        public void onSingleClick(View v) {

                        }
                    });
                    mGuideView.addView(containerView, containerParam);
                    mRootView.addView(mGuideView);

                    containerView.setX(position[0]);
                    containerView.setY(position[1]);
                    Log.e(TAG,"x:"+position[0]);
                    Log.e(TAG,"Y:"+position[1]);
//                            containerView.setX(position[0]);
//                            containerView.setY(0);
                    v.setVisibility(View.INVISIBLE);
                    containerView.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            removeGuideView();
                            mScreenControl.gotoScreen(ScreenType.MOBILE_CARD_LIST, null);
                        }
                    });
                }
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setResID(R.layout.fragment_main);
        super.onCreateView(inflater, container, savedInstanceState);

        if (!mIsReUsed) {
            initValue(false);
            mRootView.findViewById(R.id.setting).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    mScreenControl.addScreen(ScreenType.PREFERENCE, null);
                }
            });
            mRootView.findViewById(R.id.main_menu).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    mScreenControl.gotoScreen(ScreenType.OPEN_MENU, null);
                }
            });
            applyPermission();
            mHandler.removeCallbacks(mRunnableGuide);
            mHandler.postDelayed(mRunnableGuide,1000);
            mRootView.invalidate();
        }
        return mRootView;
    }

    private void removeGuideView() {
        View cardView = mMainMenuView.getItemView(MenuItemView.MenuItemType.MOBILE_CARD_ALERT);
        if (cardView != null) {
            cardView.setVisibility(View.VISIBLE);
        }
        if (mGuideView != null) {
            mRootView.removeView(mGuideView);
        }
    }

    private void setTime() {
        if (mRingTimeView == null) {
            return;
        }
        Locale locale = getResources().getConfiguration().locale;
        String language = mLocale.getLanguage();
        Date date = new Date();

        if (language == null) {
            mRingTimeView.setDateTime(mMarkerTimeFormatter.format(date), mDateFormatter.format(date), mTimeFormatter.format(date));
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
        mRingTimeView.setDateTime(mMarkerTimeFormatter.format(date), dateString, mTimeFormatter.format(date));
    }
}
