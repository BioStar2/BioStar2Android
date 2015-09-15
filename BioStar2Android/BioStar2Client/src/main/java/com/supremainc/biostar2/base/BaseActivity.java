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
package com.supremainc.biostar2.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.popup.ToastPopup;
import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.sdk.provider.AccessGroupDataProvider;
import com.supremainc.biostar2.sdk.provider.AccessLevelDataProvider;
import com.supremainc.biostar2.sdk.provider.CommonDataProvider;
import com.supremainc.biostar2.sdk.provider.DeviceDataProvider;
import com.supremainc.biostar2.sdk.provider.DoorDataProvider;
import com.supremainc.biostar2.sdk.provider.EventDataProvider;
import com.supremainc.biostar2.sdk.provider.PermissionDataProvider;
import com.supremainc.biostar2.sdk.provider.PushDataProvider;
import com.supremainc.biostar2.sdk.provider.ScheduleDataProvider;
import com.supremainc.biostar2.sdk.provider.UserDataProvider;
import com.supremainc.biostar2.sdk.volley.VolleyError;

public class BaseActivity extends ActionBarActivity {
    protected final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    protected Activity mContext;
    protected AccessGroupDataProvider mAccessGroupDataProvider;
    protected AccessLevelDataProvider mAccessLevelDataProvider;
    protected AppDataProvider mAppDataProvider;
    protected CommonDataProvider mCommonDataProvider;
    protected DeviceDataProvider mDeviceDataProvider;
    protected DoorDataProvider mDoorDataProvider;
    protected EventDataProvider mEventDataProvider;
    protected ScheduleDataProvider mScheduleDataProvider;
    protected PermissionDataProvider mPermissionDataProvider;
    protected PushDataProvider mPushProvider;
    protected UserDataProvider mUserDataProvider;

    protected Popup mPopup;
    protected BroadcastReceiver mReceiver;
    //	protected Toast mToast = null;
    protected Resources mResouce;
    protected ToastPopup mToastPopup;

    protected boolean mIsDataReceived;
    protected boolean mIsResumCheckSkip = false;
    private BroadcastReceiver mClearReceiver;

    protected OnPopupClickListener popupListener = new OnPopupClickListener() {
        @Override
        public void OnNegative() {
        }

        @Override
        public void OnPositive() {
            finish();
        }


    };

    protected Bundle getBundle(Intent intent) {
        if (intent == null) {
            Log.e(TAG, "REQ_ACTIVITY_SELECT intent null");
            return null;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            Log.e(TAG, "REQ_ACTIVITY_SELECT bundle null");
            return null;
        }
        return bundle;
    }

    @SuppressWarnings("unchecked")
    protected <T> T getBundleData(String tag, Bundle bundle) {
        try {
            T result = (T) bundle.getSerializable(tag);
            if (BuildConfig.DEBUG) {
                if (result == null) {
                    Log.e(TAG, "getExtraData null, tag:" + tag);
                }
            }
            return result;
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "getExtraData tag:" + tag + " error:" + e.getMessage());
            }
        }
        return null;
    }

    protected <T> T getExtraData(String tag, Intent intent) {
        Bundle bundle = getBundle(intent);
        if (bundle == null) {
            return null;
        }
        return getBundleData(tag, bundle);
    }

    protected boolean isInValidCheck(VolleyError error) {
        if (isFinishing()) {
            return true;
        }
        if (error == null) {
            return false;
        }
        if (error.getSessionExpire()) {
            if (mPopup == null) {
                mPopup = new Popup(mContext);
            } else {
                mPopup.dismiss();
            }
            mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.login_expire), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Setting.BROADCAST_CLEAR));
                }

                @Override
                public void OnNegative() {

                }
            }, getString(R.string.ok), null, false);
            return true;
        }
        return false;
    }

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        mContext = this;
        mResouce = mContext.getResources();
        mPopup = new Popup(mContext);
        mToastPopup = new ToastPopup(mContext);
        mUserDataProvider = UserDataProvider.getInstance(mContext);
        mDeviceDataProvider = DeviceDataProvider.getInstance(mContext);
        mDoorDataProvider = DoorDataProvider.getInstance(mContext);
        mEventDataProvider = EventDataProvider.getInstance(mContext);
        mAccessGroupDataProvider = AccessGroupDataProvider.getInstance(mContext);
        mAccessLevelDataProvider = AccessLevelDataProvider.getInstance(mContext);
        mPermissionDataProvider = PermissionDataProvider.getInstance(mContext);
        mCommonDataProvider = CommonDataProvider.getInstance(mContext);
        mPushProvider = PushDataProvider.getInstance(this);
        mAppDataProvider = AppDataProvider.getInstance(mContext);
        if (BuildConfig.DEBUG) {
            if (savedInstanceState == null) {
                Log.e(TAG, "onCreate savedInstanceState is null");
            } else {
                Log.e(TAG, "onCreate savedInstanceState is not null");
            }
        }

        if (!mCommonDataProvider.isValidLogin() && !mIsResumCheckSkip) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "onCreate memory is cleard");
            }
            //		mPopup.show(PopupType.ALERT, getString(R.string.info),getString(R.string.login_expire), popupListener, getString(R.string.ok), null);

        }

        if (mClearReceiver == null) {
            mClearReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (isFinishing()) {
                        return;
                    }
                    final String action = intent.getAction();
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "receive:" + action);
                    }
                    if (action.equals(Setting.BROADCAST_CLEAR) || action.equals(Setting.BROADCAST_ALL_CLEAR)) {
                        finish();
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_CLEAR);
            intentFilter.addAction(Setting.BROADCAST_ALL_CLEAR);
            LocalBroadcastManager.getInstance(this).registerReceiver(mClearReceiver, intentFilter);
        }

    }

    @Override
    public void onDestroy() {
        mCommonDataProvider.cancelAll(TAG);
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        if (mClearReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mClearReceiver);
            mClearReceiver = null;
        }
        mContext = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onResume");
        }
        super.onResume();
        if (mCommonDataProvider == null || mUserDataProvider == null || (!mCommonDataProvider.isValidLogin() && !mIsResumCheckSkip)) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "onResume invalid");
            }
            if (mCommonDataProvider == null || mUserDataProvider == null) {
                if (mPopup == null) {
                    mPopup = new Popup(this);
                }
                mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.login_expire), popupListener, getString(R.string.ok), null);
                return;
            }
//			mUserDataProvider.simpleLogin(mSimpleLoginListener, mSimpleLoginErrorListener, null);
            return;
        }
    }

    @Override
    public void onStart() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onResume");
        }
        super.onStart();
    }
}