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
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
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

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.sdk.datatype.v2.Common.ResponseStatus;
import com.supremainc.biostar2.sdk.datatype.v2.Common.UpdateData;
import com.supremainc.biostar2.sdk.datatype.v2.Common.VersionData;
import com.supremainc.biostar2.sdk.datatype.v2.Permission.PermissionModule;
import com.supremainc.biostar2.sdk.datatype.v2.Preferrence.NotificationsSetting;
import com.supremainc.biostar2.sdk.datatype.v2.Preferrence.Preference;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
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

@SuppressLint("InflateParams")
public class PreferenceFragment extends BaseFragment {
    private UpdateData mUpdateData;
    private StyledTextView mDateFormat;
    private LinearLayout mNotification;
    private ArrayList<SwitchView> mSwitchViewList;
    private StyledTextView mTimeFormat;
    private Response.Listener<UpdateData> mUpdateListener = new Response.Listener<UpdateData>() {
        @Override
        public void onResponse(final UpdateData response, Object param) {
            try {
                if (isInValidCheck(null)) {
                    return;
                }
                if (response == null) {
                    return;
                }
                mUpdateData = response;
                FileUtil.saveFileObj(mContext.getFilesDir() + "/up.dat", response);
                PackageManager manager = mContext.getPackageManager();
                PackageInfo packInfo = manager.getPackageInfo(mContext.getPackageName(), 0);
                int clientVersion = packInfo.versionCode;
                if (mUpdateData.version > clientVersion) {
                    setNewVersion();
                }
            } catch (Exception e) {
                Log.e(TAG, " " + e.getMessage());
            }
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
    private Listener<ResponseStatus> mListener = new Listener<ResponseStatus>() {
        @Override
        public void onResponse(ResponseStatus response, Object param) {
            if (isInValidCheck(null)) {
                return;
            }
            sendLocalBroadcast(Setting.BROADCAST_PREFRENCE_REFRESH, null);
            applied();
        }
    };
    private Listener<Preference> mPreferenceListener = new Listener<Preference>() {
        @Override
        public void onResponse(Preference preference, Object param) {
            if (isInValidCheck(null)) {
                return;
            }
            mPopup.dismissWiat();
            setView();
            if (preference.notifications == null) {
                return;
            }
            for (NotificationsSetting item : preference.notifications) {
                addSwitchView(item.description, item.subscribed, item.type);
            }
        }
    };
    private Response.ErrorListener mPreferenceErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object param) {
            if (isInValidCheck(error)) {
                return;
            }
            mPopup.dismissWiat();
            mPopup.show(PopupType.ALERT, mContext.getString(R.string.fail_retry), Setting.getErrorMessage(error, mContext), new OnPopupClickListener() {
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
                            mCommonDataProvider.getPreference(mPreferenceListener, mPreferenceErrorListener, null);
                        }
                    });
                }
            }, mContext.getString(R.string.ok), mContext.getString(R.string.cancel));
        }
    };
    private Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isInValidCheck(error)) {
                return;
            }
            mPopup.dismissWiat();
            mPopup.show(PopupType.ALERT, getString(R.string.fail_retry), Setting.getErrorMessage(error, mContext), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setPreference();
                        }
                    });

                }

                @Override
                public void OnNegative() {

                }
            }, getString(R.string.ok), getString(R.string.cancel));
        }
    };


    public PreferenceFragment() {
        super();
        setType(ScreenType.PREFERENCE);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void applied() {
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
        if (VersionData.getCloudVersion(mContext) > 1) {
            if (mPermissionDataProvider.getPermission(PermissionModule.SETTING_NOTIFICATION, true)) {
                showNotification(true);
            } else {
                showNotification(false);
            }
        } else {
            if (mPermissionDataProvider.getPermission(PermissionModule.DOOR, true) || mPermissionDataProvider.getPermission(PermissionModule.DOOR_GROUP, true)) {
                showNotification(true);
            } else {
                showNotification(false);
            }
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

        mUpdateData = (UpdateData) FileUtil.loadFileObj(mContext.getFilesDir() + "/up.dat");
        if (mUpdateData == null) {
            isUpdate();
        } else {
            try {
                PackageManager manager = mContext.getPackageManager();
                PackageInfo packInfo = manager.getPackageInfo(mContext.getPackageName(), 0);
                final int clientVersion = packInfo.versionCode;

                if (mUpdateData.version > clientVersion) {
                    setNewVersion();
                }
            } catch (Exception e) {
                Log.e(TAG, " " + e.getMessage());
            }
        }
        setView();
        mPopup.showWait(true);
        mCommonDataProvider.getPreference(mPreferenceListener, mPreferenceErrorListener, null);
    }

    private void isUpdate() {
        String name = null;
        try {
            PackageInfo i = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            name = i.packageName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        mCommonDataProvider.getAppVersion(null, mUpdateListener, null, name, null);
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
            initActionbar(getString(R.string.preference));
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
            PackageManager manager = mContext.getPackageManager();
            PackageInfo packInfo = manager.getPackageInfo(mContext.getPackageName(), 0);
            final int clientVersion = packInfo.versionCode;
            isUpdate();
            if (mUpdateData == null) {
                return;
            }
            if (mUpdateData.version > clientVersion) {
                SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mContext, mPopup);
                ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
                linkType.add(new SelectCustomData(mContext.getString(R.string.playstore), 1, false));
                linkType.add(new SelectCustomData(mContext.getString(R.string.direct_download), 2, false));
                selectPopup.show(SelectType.CUSTOM, new OnSelectResultListener<SelectCustomData>() {
                    @Override
                    public void OnResult(ArrayList<SelectCustomData> selectedItem,boolean isPositive) {
                        if (isInValidCheck(null)) {
                            return;
                        }
                        if (selectedItem == null) {
                            return;
                        }
                        switch (selectedItem.get(0).getIntId()) {
                            case 1: {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUpdateData.url));
                                startActivity(intent);
                                break;
                            }
                            case 2: {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUpdateData.url2));
                                startActivity(intent);
                                break;
                            }
                            default:
                                break;
                        }
                    }
                }, linkType, mContext.getString(R.string.select_link), false, false);
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, " " + e.getMessage());
        }
        mToastPopup.show(getString(R.string.latest_version), null);
    }

    private void setDateFormat() {
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mContext, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        ArrayList<String> srcList = mCommonDataProvider.getDateFormatList();
        for (String src : srcList) {
            linkType.add(new SelectCustomData(src.toLowerCase(), src, false));
        }
        selectPopup.show(SelectType.CUSTOM, new OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem,boolean isPositive) {
                if (isInValidCheck(null)) {
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
        mCommonDataProvider.setPreference(preference, mListener, mErrorListener, null);
    }

    private void setTimeFormat() {
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mContext, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        ArrayList<String> srcList = mCommonDataProvider.getTimeFormatList();
        for (String src : srcList) {
            linkType.add(new SelectCustomData(src.toLowerCase(), src, false));
        }
        selectPopup.show(SelectType.CUSTOM, new OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem,boolean isPositive) {
                if (isInValidCheck(null)) {
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
        setTimezone(mCommonDataProvider.getTimeZoneName());
        setDateFormat(mCommonDataProvider.getDateFormat());
        setTimeFormat(mCommonDataProvider.getTimeFormat());
        setDateFormatName(mCommonDataProvider.getDateFormat().toLowerCase());
        setTimeFormatName(mCommonDataProvider.getTimeFormat().toLowerCase());
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
        switchView.init(mContext, null, false);
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
