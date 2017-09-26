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

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.sdk.models.v2.card.MobileCard;
import com.supremainc.biostar2.sdk.models.v2.card.MobileCards;
import com.supremainc.biostar2.sdk.models.v2.common.SupportFeature;
import com.supremainc.biostar2.sdk.models.v2.common.VersionData;
import com.supremainc.biostar2.sdk.models.v2.permission.PermissionModule;
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;
import com.supremainc.biostar2.util.Utils;
import com.supremainc.biostar2.view.MainMenuView;
import com.supremainc.biostar2.view.MenuItemView;
import com.supremainc.biostar2.view.RingTimeView;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private boolean mIsWaitCard;
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
    private Runnable mRunnableGuide = new Runnable() {
        @Override
        public void run() {
            if (isInValidCheck()) {
                return;
            }
            View v = mMainMenuView.getItemView(MenuItemView.MenuItemType.MOBILE_CARD_ALERT);
            if (v != null && mAppDataProvider.getBoolean(AppDataProvider.BooleanType.SHOW_GUIDE_MENU_CARD)) {
                if (mGuideView == null) {
                    int w = v.getWidth();
                    int h = v.getHeight();
                    if (w == 0 || h == 0) {
                        mHandler.removeCallbacks(mRunnableGuide);
                        mHandler.postDelayed(mRunnableGuide, 1000);
                        return;
                    }
                    int[] position = {0, 0};
                    v.getLocationOnScreen(position);
                    LinearLayout.LayoutParams containerParam = new LinearLayout.LayoutParams(v.getWidth() + 30, v.getHeight() + 60);
                    FrameLayout.LayoutParams itemParam = new FrameLayout.LayoutParams(w, h, Gravity.CENTER);

                    FrameLayout containerView = new FrameLayout(mActivity);
                    containerView.setBackgroundResource(R.drawable.dash);
                    View itemView = mMainMenuView.createMenuView(MenuItemView.MenuItemType.MOBILE_CARD_ALERT);
                    containerView.addView(itemView, itemParam);

                    mGuideView = (ViewGroup) mInflater.inflate(R.layout.view_guide, null, false);
                    StyledTextView guideText = (StyledTextView) mGuideView.findViewById(R.id.guide_text);
                    guideText.setText(getString(R.string.guide_register_mobile_card1) + "\n" + getString(R.string.guide_register_mobile_card2));
                    mGuideView.findViewById(R.id.close_guide).setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            mAppDataProvider.setBoolean(AppDataProvider.BooleanType.SHOW_GUIDE_MENU_CARD, false);
                            removeGuideView();
                        }
                    });
                    mGuideView.findViewById(R.id.close_alaways).setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            removeGuideView();
                            mAppDataProvider.setBoolean(AppDataProvider.BooleanType.SHOW_GUIDE_MENU_CARD, false);
                        }
                    });
                    mGuideView.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            removeGuideView();
                        }
                    });
                    mGuideView.addView(containerView, containerParam);
                    mRootView.addView(mGuideView);

                    containerView.setX(position[0]);
                    containerView.setY(position[1]);
                    v.setVisibility(View.INVISIBLE);
                }
            }
        }
    };
    private Callback<MobileCards> mMobileCardsCallback = new Callback<MobileCards>() {
        @Override
        public void onResponse(Call<MobileCards> call, Response<MobileCards> response) {
            if (isIgnoreCallback(call,false)) {
                return;
            }
            if (response == null || !response.isSuccessful() || response.body() == null) {
                return;
            }
            if (response.body().records == null || response.body().records.size() < 1) {
                mIsWaitCard = false;
                return;
            }
            for (MobileCard card : response.body().records) {
                if (!card.is_registered) {
                    mIsWaitCard = true;
                    applyPermission();
                    mHandler.removeCallbacks(mRunnableGuide);
                    mHandler.postDelayed(mRunnableGuide, 1000);
                }
            }
        }

        @Override
        public void onFailure(Call<MobileCards> call, Throwable t) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "" + t.getMessage());
            }
        }
    };

    public MainFragment() {
        super();
        setType(ScreenType.MAIN);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void judgeShowRing() {
        if (mRingTimeView == null || mIsDestroy) {
            return;
        }
        mMainMenuView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mTopMenuView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mLogoView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int otherSize = mTopMenuView.getMeasuredHeight() + mLogoView.getMeasuredHeight() + mMainMenuView.getMeasuredHeight();
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float deviceHeight = Utils.convertPixelsToDp(metrics.heightPixels, mActivity);
        float otherHeight = Utils.convertPixelsToDp(otherSize, mActivity) + (float) 134.69;
        float height = deviceHeight - otherHeight;
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "deviceHeight:" + deviceHeight + " otherHeight:" + otherHeight + " height:" + height);
        }
        mRingTimeView.setAdjustHeight(height);
    }

    private void applyPermission() {
        if (mMainMenuView == null) {
            return;
        }
        mMainMenuView.removeAllMenuItem();
        mMainMenuView.addMenu(MenuItemView.MenuItemType.MY_PROFILE);
        if (mPermissionDataProvider.getPermission(PermissionModule.USER, false)) {
            mMainMenuView.addMenu(MenuItemView.MenuItemType.USER);
        }

        if (mPermissionDataProvider.getPermission(PermissionModule.DOOR, false)) {
            mMainMenuView.addMenu(MenuItemView.MenuItemType.DOOR);
        }

        if (mPermissionDataProvider.getPermission(PermissionModule.MONITORING, false)) {
            mMainMenuView.addMenu(MenuItemView.MenuItemType.MONITORING);
        }

        if (mPermissionDataProvider.getPermission(PermissionModule.MONITORING, false)) {
            mMainMenuView.addMenu(MenuItemView.MenuItemType.ALARM);
        }


        if (VersionData.getCloudVersion(mActivity) > 1 && VersionData.isSupportFeature(mActivity, SupportFeature.MOBILE_CARD) && Build.VERSION.SDK_INT >= 21) {
            if (mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) || mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)) {
                if (mIsWaitCard) {
                    mMainMenuView.addMenu(MenuItemView.MenuItemType.MOBILE_CARD_ALERT);
                } else {
                    mMainMenuView.addMenu(MenuItemView.MenuItemType.MOBILE_CARD);
                }
            }
        }
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
            mContainerRingTimeView = mRootView.findViewById(R.id.ring_time_container);
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
            String userConfig = mDateTimeDataProvider.getDateFormat();
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
        if (ConfigDataProvider.TEST_DELETE && BuildConfig.DEBUG) {
            if (Build.VERSION.SDK_INT >= 23) {
                if ((ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            Setting.REQUEST_EXTERNAL_STORAGE);
                }
            }
        }
        judgeShowRing();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (VersionData.getCloudVersion(mActivity) > 1 && VersionData.isSupportFeature(mActivity, SupportFeature.MOBILE_CARD) && Build.VERSION.SDK_INT >= 21) {
            if (mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) || mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)) {
                mMobileCardDataProvider.getMobileCards(mActivity, mMobileCardsCallback);
            }
        }
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
                    if (isInValidCheck()) {
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
            intentFilter.addAction(Setting.BROADCAST_ALARM_UPDATE);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
    }

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
            applyPermission();
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
        mRingTimeView.setDateTime(mMarkerTimeFormatter.format(date), dateString, mTimeFormatter.format(date));
    }
}
