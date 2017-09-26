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

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.appyvet.rangebar.IRangeBarFormatter;
import com.appyvet.rangebar.RangeBar;
import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.models.v2.common.SupportFeature;
import com.supremainc.biostar2.sdk.models.v2.common.UpdateData;
import com.supremainc.biostar2.sdk.models.v2.common.VersionData;
import com.supremainc.biostar2.sdk.models.v2.permission.PermissionModule;
import com.supremainc.biostar2.sdk.models.v2.preferrence.NotificationsSetting;
import com.supremainc.biostar2.sdk.models.v2.preferrence.Preference;
import com.supremainc.biostar2.util.FileUtil;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.view.SwitchView;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;
import com.supremainc.biostar2.widget.popup.SelectCustomData;
import com.supremainc.biostar2.widget.popup.SelectPopup;
import com.supremainc.biostar2.widget.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.widget.popup.SelectPopup.SelectType;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.supremainc.biostar2.R.string.preference;

@SuppressLint("InflateParams")
public class PreferenceFragment extends BaseFragment {
    private UpdateData mUpdateData;
    private StyledTextView mDateFormat;
    private LinearLayout mNotification;
    private ArrayList<SwitchView> mSwitchViewList;
    private StyledTextView mTimeFormat;
    private RangeBar mRangeBar;
    private SwitchView mBLESwtich;

    private Callback<UpdateData> mUpdateCheckCallback = new Callback<UpdateData>() {
        @Override
        public void onResponse(Call<UpdateData> call, Response<UpdateData> response) {
            try {
                if (isInValidCheck()) {
                    return;
                }
                mPopup.dismissWiat();
                mUpdateData = response.body();
                if (!BuildConfig.DEBUG) {
                    FileUtil.saveFileObj(mActivity.getFilesDir() + "/up.dat", response);
                }
                PackageManager manager = mActivity.getPackageManager();
                PackageInfo packInfo = manager.getPackageInfo(mActivity.getPackageName(), 0);
                final int clientVersion = packInfo.versionCode;
                if (mUpdateData.version > clientVersion) {
                    setNewVersion();
                }
            } catch (Exception e) {
            }
        }

        @Override
        public void onFailure(Call<UpdateData> call, Throwable t) {

        }
    };


    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()) {
                case R.id.go_version_mobile:
                    onUpdate();
                    break;
                case R.id.go_date:
                    setDateFormat();
                    break;
                case R.id.go_time:
                    setTimeFormat();
                    break;
            }
        }
    };

    private Callback<ResponseStatus> mModifyPreferListener = new Callback<ResponseStatus>() {
        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showErrorPopup(t.getMessage(), false);
        }

        @Override
        public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true, false)) {
                return;
            }
            sendLocalBroadcast(Setting.BROADCAST_PREFRENCE_REFRESH, null);
            applied();
        }
    };

    private Callback<Preference> mPreferenceListener = new Callback<Preference>() {
        @Override
        public void onFailure(Call<Preference> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showRetryPopup(t.getMessage(), new OnPopupClickListener() {
                @Override
                public void OnNegative() {
                    mScreenControl.backScreen();
                }

                @Override
                public void OnPositive() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPopup.showWait(true);
                            request(mCommonDataProvider.getPreference(mPreferenceListener));
                        }
                    });
                }
            });
        }

        @Override
        public void onResponse(Call<Preference> call, Response<Preference> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, false, false)) {
                mPreferenceListener.onFailure(call,new Throwable(getResponseErrorMessage(response)));
                return;
            }
            setView();
            if (response.body().notifications == null) {
                return;
            }
            for (NotificationsSetting item : response.body().notifications) {
                addSwitchView(item.description, item.subscribed, item.type);
            }
        }
    };


    public PreferenceFragment() {
        super();
        setType(ScreenType.PREFERENCE);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void applied() {
        mAppDataProvider.setBoolean(AppDataProvider.BooleanType.MOBILE_CARD_NFC, !mBLESwtich.getOn());
        mAppDataProvider.setBleRange(Integer.valueOf(mRangeBar.getRightPinValue()));

        mPopup.dismissWiat();
        mPopup.show(PopupType.CONFIRM, getString(R.string.info), getString(R.string.success), new OnPopupClickListener() {
            @Override
            public void OnNegative() {

            }

            @Override
            public void OnPositive() {
                mScreenControl.backScreen();
            }


        }, "Ok", null);
    }

    private void applyPermission() {
        if (mPermissionDataProvider.getPermission(PermissionModule.MONITORING, false)) {
            showNotification(true);
        } else {
            showNotification(false);
        }
    }

    private void initValue() {
        mNotification = (LinearLayout) mRootView.findViewById(R.id.notification);
        mNotification.setOnClickListener(mClickListener);
        mDateFormat = (StyledTextView) mRootView.findViewById(R.id.dateformat);
        mTimeFormat = (StyledTextView) mRootView.findViewById(R.id.timeformat);
        mRootView.findViewById(R.id.go_version_mobile).setOnClickListener(mClickListener);
        mRootView.findViewById(R.id.go_date).setOnClickListener(mClickListener);
        mRootView.findViewById(R.id.go_time).setOnClickListener(mClickListener);
        mRootView.findViewById(R.id.ble_card).setVisibility(View.GONE);
        if (VersionData.getCloudVersion(mActivity) > 1 && VersionData.isSupportFeature(mActivity, SupportFeature.MOBILE_CARD) && Build.VERSION.SDK_INT >= 21) {
            if (mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                mRootView.findViewById(R.id.ble_card).setVisibility(View.VISIBLE);
            }
        }


        if (mBLESwtich == null) {
            mBLESwtich = (SwitchView) mRootView.findViewById(R.id.ble_switch);
            mBLESwtich.init(mActivity, new SwitchView.OnChangeListener() {
                @Override
                public boolean onChange(boolean on) {
                    if (on) {
                        if (Setting.IS_AT_THE_SAME_BLE_NFC) {
                            mToastPopup.show(getString(R.string.ble_on_guide2), null);
                        } else {
                            mToastPopup.show(getString(R.string.ble_on_guide), getString(R.string.ble_on_guide2));
                        }
                    }
                    return true;
                }
            }, !mAppDataProvider.getBoolean(AppDataProvider.BooleanType.MOBILE_CARD_NFC));
        }
        mUpdateData = (UpdateData) FileUtil.loadFileObj(mActivity.getFilesDir() + "/up.dat");
        if (mUpdateData == null) {
            isUpdate();
        } else {
            try {
                PackageManager manager = mActivity.getPackageManager();
                PackageInfo packInfo = manager.getPackageInfo(mActivity.getPackageName(), 0);
                final int clientVersion = packInfo.versionCode;

                if (mUpdateData.version > clientVersion) {
                    setNewVersion();
                }
            } catch (Exception e) {
                Log.e(TAG, " " + e.getMessage());
            }
        }
        if (mRangeBar == null) {
            mRangeBar = (RangeBar) mRootView.findViewById(R.id.rangebar);
            mRangeBar.setSeekPinByValue(mAppDataProvider.getBleRange());
            mRangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
                @Override
                public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                                  int rightPinIndex,
                                                  String leftPinValue, String rightPinValue) {
//                    mAppDataProvider.setBleRange(Integer.valueOf(rightPinValue));
                }
            });
            mRangeBar.setFormatter(new IRangeBarFormatter() {
                @Override
                public String format(String value) {
                    int range = Integer.valueOf(value);
                    if (range < 101) {
                        return getString(R.string.distance_immediate);
                    } else if (range < 301) {
                        return getString(R.string.distance_close);
                    } else {
                        return getString(R.string.distance_near);
                    }
                }
            });
        }
        setView();
        mPopup.showWait(true);
        request(mCommonDataProvider.getPreference(mPreferenceListener));
    }

    private void isUpdate() {
        mCommonDataProvider.getAppVersion(mUpdateCheckCallback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_save:
                setPreference();
                return true;
            default:
                break;
        }

        return false;
    }

    protected void registerBroadcast() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (mIsDestroy) {
                        return;
                    }
                    String action = intent.getAction();
                    if (action.equals(Setting.BROADCAST_REROGIN)) {
                        applyPermission();
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
        setResID(R.layout.fragment_prefernce);
        super.onCreateView(inflater, container, savedInstanceState);
        if (!mIsReUsed) {
            initValue();
            initActionbar(getString(preference));
            mRootView.invalidate();
        }
        return mRootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
    }

    private void onUpdate() {

        try {
            PackageManager manager = mActivity.getPackageManager();
            PackageInfo packInfo = manager.getPackageInfo(mActivity.getPackageName(), 0);
            final int clientVersion = packInfo.versionCode;
            isUpdate();
            if (mUpdateData == null) {
                return;
            }
            if (mUpdateData.version > clientVersion) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUpdateData.url));
                startActivity(intent);
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, " " + e.getMessage());
        }
        mToastPopup.show(getString(R.string.latest_version), null);
    }

    private void setDateFormat() {
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mActivity, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        ArrayList<String> srcList = mDateTimeDataProvider.getDateFormatList();
        for (String src : srcList) {
            linkType.add(new SelectCustomData(src.toLowerCase(), src, false));
        }
        selectPopup.show(SelectType.CUSTOM, new OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem, boolean isPositive) {
                if (isInValidCheck()) {
                    return;
                }
                if (selectedItem == null) {
                    return;
                }
                setDateFormatName(selectedItem.get(0).mTitle);
                setDateFormat(selectedItem.get(0).getStringId());
            }
        }, linkType, getString(R.string.date) + " " + getString(R.string.format), false, false);

    }

    private void setPreference() {
        mPopup.showWait(true);
        Preference preference = new Preference();
        preference.date_format = getDateFormat();
        preference.time_format = getTimeFormat();
        preference.notifications = new ArrayList<NotificationsSetting>();
        ArrayList<SwitchView> switchViewList = getSwitchViewList();
        if (switchViewList != null) {
            for (SwitchView item : switchViewList) {
                NotificationsSetting noti = new NotificationsSetting();
                noti.subscribed = item.getOn();
                noti.type = (String) item.getTag();
                preference.notifications.add(noti);
            }
        }
        mCommonDataProvider.setSetting(preference, mModifyPreferListener);
    }

    private void setTimeFormat() {
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mActivity, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        ArrayList<String> srcList = mDateTimeDataProvider.getTimeFormatList();
        for (String src : srcList) {
            linkType.add(new SelectCustomData(src.toLowerCase(), src, false));
        }
        selectPopup.show(SelectType.CUSTOM, new OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem, boolean isPositive) {
                if (isInValidCheck()) {
                    return;
                }
                if (selectedItem == null) {
                    return;
                }
                setTimeFormatName(selectedItem.get(0).mTitle);
                setTimeFormat(selectedItem.get(0).getStringId());
            }
        }, linkType, getString(R.string.time) + " " + getString(R.string.format), false, false);
    }

    private void setView() {
        setTimezone(mDateTimeDataProvider.getTimeZoneName());
        setDateFormat(mDateTimeDataProvider.getDateFormat());
        setTimeFormat(mDateTimeDataProvider.getTimeFormat());
        setDateFormatName(mDateTimeDataProvider.getDateFormat().toLowerCase());
        setTimeFormatName(mDateTimeDataProvider.getTimeFormat().toLowerCase());
        setAppVersion(getString(R.string.app_version));
        setDateDevider(getString(R.string.date) + " / " + getString(R.string.time) + " " + getString(R.string.format));
        applyPermission();
    }

    public void addSwitchView(String description, boolean subscribed, String type) {
        if (mSwitchViewList == null) {
            mSwitchViewList = new ArrayList<SwitchView>();
        }
        LinearLayout mainLayout = (LinearLayout) mInflater.inflate(R.layout.view_push_switch, null);
        StyledTextView descrptionView = (StyledTextView) mainLayout.findViewById(R.id.descrption);
        descrptionView.setText(description);
        SwitchView switchView = (SwitchView) mainLayout.findViewById(R.id.onoff);
        switchView.init(mActivity, null, false);
        switchView.setSwitch(subscribed);
        switchView.setTag(type);

        mSwitchViewList.add(switchView);
        mNotification.addView(mainLayout);
    }

    public String getDateFormat() {
        return (String) mDateFormat.getTag();
    }

    public void setDateFormat(String content) {
        mDateFormat.setTag(content);
    }

    public ArrayList<SwitchView> getSwitchViewList() {
        return mSwitchViewList;
    }

    public String getTimeFormat() {
        return (String) mTimeFormat.getTag();
    }

    public void setTimeFormat(String content) {
        mTimeFormat.setTag(content);
    }

    public void setAppVersion(String content) {
        setTextView(R.id.version_mobile, content);
    }

    public void setDateDevider(String content) {
        setTextView(R.id.devider_date, content);
    }

    public void setDateFormatName(String content) {
        mDateFormat.setText(content);
    }

    public void setNewVersion() {
        mRootView.findViewById(R.id.version_mobile_new).setVisibility(View.VISIBLE);
    }

    public void setTimeFormatName(String content) {
        mTimeFormat.setText(content);
    }

    public void setTimezone(String content) {
        setTextView(R.id.timezone, content);
    }

    public void showNotification(boolean isVisible) {
        int visible;
        if (isVisible) {
            visible = View.VISIBLE;
        } else {
            visible = View.GONE;
        }
        mNotification.setVisibility(visible);
    }
}
