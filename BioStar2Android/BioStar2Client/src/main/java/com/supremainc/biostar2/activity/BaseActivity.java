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
package com.supremainc.biostar2.activity;

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

import com.google.gson.Gson;
import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.provider.MobileCardDataProvider;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.provider.AccessControlDataProvider;
import com.supremainc.biostar2.sdk.provider.CardDataProvider;
import com.supremainc.biostar2.sdk.provider.CommonDataProvider;
import com.supremainc.biostar2.sdk.provider.DateTimeDataProvider;
import com.supremainc.biostar2.sdk.provider.DeviceDataProvider;
import com.supremainc.biostar2.sdk.provider.DoorDataProvider;
import com.supremainc.biostar2.sdk.provider.MonitoringDataProvider;
import com.supremainc.biostar2.sdk.provider.PermissionDataProvider;
import com.supremainc.biostar2.sdk.provider.PushDataProvider;
import com.supremainc.biostar2.sdk.provider.UserDataProvider;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;
import com.supremainc.biostar2.widget.popup.ToastPopup;

import retrofit2.Call;
import retrofit2.Response;

public class BaseActivity extends ActionBarActivity {
    protected static Gson mGson = new Gson();
    protected final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    protected Activity mContext;
    protected AppDataProvider mAppDataProvider;
    protected AccessControlDataProvider mAccessGroupDataProvider;
    protected CardDataProvider mCardDataProvider;
    protected CommonDataProvider mCommonDataProvider;
    protected DeviceDataProvider mDeviceDataProvider;
    protected DoorDataProvider mDoorDataProvider;
    protected MonitoringDataProvider mMonitoringDataProvider;
    protected PermissionDataProvider mPermissionDataProvider;
    protected PushDataProvider mPushDataProvider;
    protected UserDataProvider mUserDataProvider;
    protected DateTimeDataProvider mDateTimeDataProvider;
    protected MobileCardDataProvider mMobileCardDataProvider;
    protected Popup mPopup;
    protected BroadcastReceiver mReceiver;
    //	protected Toast mToast = null;
    protected Resources mResouce;
    protected ToastPopup mToastPopup;

    protected boolean mIsDataReceived;
    protected BroadcastReceiver mClearReceiver;
    OnPopupClickListener mClearOnPopupClickListener = new OnPopupClickListener() {
        @Override
        public void OnPositive() {
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Setting.BROADCAST_CLEAR));
        }

        @Override
        public void OnNegative() {

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
    protected boolean isInValidCheck() {
        if (mContext == null) {
            return true;
        }
        if (isFinishing()) {
            return true;
        }
        return false;
    }
    protected boolean isIgnoreCallback(Call<?> call, boolean dismissPopup) {
        if (isInValidCheck()) {
            return true;
        }
        if (dismissPopup && mPopup != null) {
            mPopup.dismiss();
        }
        if (call != null && call.isCanceled()) {
            return true;
        }
        return false;
    }

    protected boolean isInvalidResponse(Response<?> response) {
        if (response.isSuccessful() && response.body() != null) {
            return false;
        } else if (response.errorBody() == null) {
            showErrorPopup(getString(R.string.fail) + "\nhcode:" + response.code());
            return true;
        } else {
            String error = "";
            try {
                ResponseStatus responseClass = (ResponseStatus) mGson.fromJson(response.errorBody().string(), ResponseStatus.class);
                error = responseClass.message + "\n" + "scode: " + responseClass.status_code;
            } catch (Exception e) {
                e.printStackTrace();
            }
            showErrorPopup(error + "\n" + "hcode: " + response.code());
            return true;
        }
    }

    protected void showErrorPopup(String msg) {
        if (msg == null || msg.isEmpty()) {
            msg = getString(R.string.fail);
        }
        if (mPopup == null) {
            return;
        }
        if (msg.contains("scode: 10") || msg.contains("hcode: 401")) {
            if (mCommonDataProvider != null) {
                mCommonDataProvider.removeCookie();
            }
            mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.login_expire), mClearOnPopupClickListener, getString(R.string.ok), null, false);
        } else {
            mPopup.show(PopupType.ALERT, getString(R.string.info), msg, null, getString(R.string.ok), null, true);
        }

    }

    protected boolean isIgnoreCallback(Call<?> call, Throwable error) {
        if (isFinishing() || call.isCanceled()) {
            return true;
        }

        String msg = error.getMessage();
        if (msg.contains("scode: 10") || msg.contains("hcode: 401")) {
            showErrorPopup(msg);
            return true;
        }
        return false;
    }

    private void init() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "init");
        }
        mContext = this;
        mResouce = mContext.getResources();
        mPopup = new Popup(mContext);
        mToastPopup = new ToastPopup(mContext);

        mAppDataProvider = AppDataProvider.getInstance(mContext);
        mAccessGroupDataProvider = AccessControlDataProvider.getInstance(mContext);
        mCardDataProvider = CardDataProvider.getInstance(mContext);
        mCommonDataProvider = CommonDataProvider.getInstance(mContext);
        mDeviceDataProvider = DeviceDataProvider.getInstance(mContext);
        mDoorDataProvider = DoorDataProvider.getInstance(mContext);
        mMonitoringDataProvider = MonitoringDataProvider.getInstance(mContext);
        mPermissionDataProvider = PermissionDataProvider.getInstance(mContext);
        mPushDataProvider = PushDataProvider.getInstance(mContext);
        mUserDataProvider = UserDataProvider.getInstance(mContext);
        mDateTimeDataProvider = DateTimeDataProvider.getInstance(mContext);
        mMobileCardDataProvider = new MobileCardDataProvider();

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

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onCreate");
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        init();
        if (BuildConfig.DEBUG) {
            if (savedInstanceState == null) {
                Log.e(TAG, "onCreate savedInstanceState is null");
            } else {
                Log.e(TAG, "onCreate savedInstanceState is not null");
            }
        }
    }

    @Override
    public void onDestroy() {
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
    }

    @Override
    public void onStart() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onStart");
        }
        super.onStart();
        if (mCommonDataProvider == null || mUserDataProvider == null) {
            init();
        }
    }
}