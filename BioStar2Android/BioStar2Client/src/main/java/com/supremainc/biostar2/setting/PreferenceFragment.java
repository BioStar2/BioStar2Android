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
package com.supremainc.biostar2.setting;

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

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.base.BaseFragment;
import com.supremainc.biostar2.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.popup.SelectCustomData;
import com.supremainc.biostar2.popup.SelectPopup;
import com.supremainc.biostar2.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.popup.SelectPopup.SelectType;
import com.supremainc.biostar2.sdk.datatype.PermissionData.PERMISSION_MODULE;
import com.supremainc.biostar2.sdk.datatype.PreferenceData.NotificationsSetting;
import com.supremainc.biostar2.sdk.datatype.PreferenceData.Preference;
import com.supremainc.biostar2.sdk.datatype.ResponseStatus;
import com.supremainc.biostar2.sdk.datatype.UpdateData;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.util.FileUtil;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.SwitchView;

import java.util.ArrayList;

@SuppressLint("InflateParams")
public class PreferenceFragment extends BaseFragment {
    private PreferenceFragmentLayout mLayout;
    private UpdateData mUpdateData;

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
                    mLayout.setNewVersion();
                }
            } catch (Exception e) {
                Log.e(TAG, " " + e.getMessage());
            }
        }
    };
    private Listener<ResponseStatus> mListener = new Listener<ResponseStatus>() {
        @Override
        public void onResponse(ResponseStatus response, Object param) {
            if (mIsDestroy || !isAdded()) {
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
                mLayout.addSwitchView(item.description, item.subscribed, item.type);
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
    private PreferenceFragmentLayout.PreferenceFragmentLayoutEvent mLayoutEvent = new PreferenceFragmentLayout.PreferenceFragmentLayoutEvent() {
        @Override
        public void onClickUpdate() {
            onUpdate();
        }

        @Override
        public void onClickDate() {
            setDateFormat();
        }

        @Override
        public void onClickTime() {
            setTimeFormat();
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

            }            @Override
            public void OnPositive() {
                mScreenControl.backScreen();
            }


        }, "Ok", null);
    }

    private void applyPermission() {
        if (mLayout == null) {
            return;
        }
        if (!mPermissionDataProvider.getPermission(PERMISSION_MODULE.DOOR, true)) {
            mLayout.showNotification(false);
        } else {
            mLayout.showNotification(true);
        }
    }

    private void initValue() {
        mUpdateData = (UpdateData) FileUtil.loadFileObj(mContext.getFilesDir() + "/up.dat");
        if (mUpdateData == null) {
            isUpdate();
        } else {
            try {
                PackageManager manager = mContext.getPackageManager();
                PackageInfo packInfo = manager.getPackageInfo(mContext.getPackageName(), 0);
                final int clientVersion = packInfo.versionCode;

                if (mUpdateData.version > clientVersion) {
                    mLayout.setNewVersion();
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
        if (mLayout == null) {
            mLayout = new PreferenceFragmentLayout(this, mLayoutEvent);
        }
        View view = mLayout.initView(this, inflater, container, savedInstanceState);
        initBaseValue(mLayout);
        initValue();
        initActionbar(getString(R.string.preference));
        return view;
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
                    public void OnResult(ArrayList<SelectCustomData> selectedItem) {
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
            public void OnResult(ArrayList<SelectCustomData> selectedItem) {
                if (isInValidCheck(null)) {
                    return;
                }
                if (selectedItem == null) {
                    return;
                }
                mLayout.setDateFormatName(selectedItem.get(0).mTitle);
                mLayout.setDateFormat(selectedItem.get(0).getStringId());
            }
        }, linkType, getString(R.string.date) + " " + getString(R.string.format), false, false);

    }

    private void setPreference() {
        mPopup.showWait(true);
        Preference preference = new Preference();
        preference.date_format = mLayout.getDateFormat();
        preference.time_format = mLayout.getTimeFormat();
        preference.notifications = new ArrayList<NotificationsSetting>();
        ArrayList<SwitchView> switchViewList = mLayout.getSwitchViewList();
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
            public void OnResult(ArrayList<SelectCustomData> selectedItem) {
                if (isInValidCheck(null)) {
                    return;
                }
                if (selectedItem == null) {
                    return;
                }
                mLayout.setTimeFormatName(selectedItem.get(0).mTitle);
                mLayout.setTimeFormat(selectedItem.get(0).getStringId());
                ;
            }
        }, linkType, getString(R.string.time) + " " + getString(R.string.format), false, false);
    }

    private void setView() {
        mLayout.setTimezone(mCommonDataProvider.getTimeZoneName());
        mLayout.setDateFormat(mCommonDataProvider.getDateFormat());
        mLayout.setTimeFormat(mCommonDataProvider.getTimeFormat());
        mLayout.setDateFormatName(mCommonDataProvider.getDateFormat().toLowerCase());
        mLayout.setTimeFormatName(mCommonDataProvider.getTimeFormat().toLowerCase());
        mLayout.setAppVersion(getString(R.string.app_version));
        mLayout.setDateDevider(getString(R.string.date) + " / " + getString(R.string.time) + " " + getString(R.string.format));
        applyPermission();
    }
}
