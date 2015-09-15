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
package com.supremainc.biostar2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.crittercism.app.Crittercism;
import com.crittercism.app.CrittercismConfig;
import com.supremainc.biostar2.guide.GuideActivity;
import com.supremainc.biostar2.main.HomeActivity;
import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.push.GooglePush;
import com.supremainc.biostar2.sdk.datatype.UserData.User;
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;
import com.supremainc.biostar2.sdk.provider.UserDataProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.util.InvalidChecker;

public class LoginActivity extends Activity {

    private final String TAG = getClass().getSimpleName();
    private LoginActivity mActivity;
    private Context mContext;
    private AppDataProvider mAppDataProvider;
    private UserDataProvider mUserDataProvider;
    private BroadcastReceiver mClearReceiver;
    private GooglePush mGooglePush;
    private InvalidChecker mInvalidChecker;
    private Popup mPopup;
    private LoginActivityLayout mLayout;
    private String mID;
    private String mPw;
    private String mSubDomain;
    private String mURL;
    private boolean mIsFirstStart;
    private boolean mIsGoAlarmList = false;

    private Runnable mSplash = new Runnable() {
        @Override
        public void run() {
            if (mLayout == null) {
                return;
            }
            mLayout.changeScreen();
        }
    };
    private Response.Listener<User> mLoginListener = new Response.Listener<User>() {
        @Override
        public void onResponse(User response, Object deliverParam) {
            onLoginListener(response, deliverParam);
        }
    };
    private Response.ErrorListener mSimpleLoginErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError, Object deliverParam) {
            onSimpleLoginErrorListener(volleyError, deliverParam);
        }
    };
    private Response.ErrorListener mLoginErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError, Object deliverParam) {
            onLoginErrorListener(volleyError, deliverParam);
        }
    };
    private LoginActivityLayout.LoginActivityLayoutEvent mLoginActivityLayoutEvent = new LoginActivityLayout.LoginActivityLayoutEvent() {
        @Override
        public void onClickQuickGuide() {
            Intent intent = new Intent(LoginActivity.this, GuideActivity.class);
            startActivity(intent);
        }

        @Override
        public void onClickLogin(String url, String subDomain, String id, String pw) {
            mActivity.onClickLogin(url, subDomain, id, pw);
        }
    };

    private void checkNotificationStart() {
        Intent startIntent = getIntent();
        if (startIntent != null) {
            String action = startIntent.getAction();
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "action:" + action);
            }
            if (action != null && action.startsWith(Setting.ACTION_NOTIFICATION_START)) {
                mIsGoAlarmList = true;
            }
        }
    }

    private void initCrashReport() {
        if (!BuildConfig.DEBUG && Setting.IS_CRASH_REPORT) {
            CrittercismConfig config = new CrittercismConfig();
            config.setCustomVersionName(getString(R.string.app_version));
            config.setVersionCodeToBeIncludedInVersionString(true);
            if (Build.VERSION.SDK_INT >= 16) {
                config.setLogcatReportingEnabled(true);
            }
            Crittercism.initialize(getApplicationContext(), Setting.CRITTERISM, config);
        }
    }

    private void initValue() {
        mIsFirstStart = true;
        mActivity = this;
        mContext = mActivity.getApplicationContext();
        mPopup = new Popup(this);
        mInvalidChecker = new InvalidChecker(mPopup);
        mGooglePush = new GooglePush(this);
        mAppDataProvider = AppDataProvider.getInstance(mContext);
    }

    private void onClickLogin(String url, String subDomain, String id, String pw) {
        if (mInvalidChecker.isEmptyString(getString(R.string.info), getString(R.string.login_empty), subDomain, id, pw)) {
            return;
        }
        while (url.endsWith(" ")) {
            url = url.substring(0,url.length()-1);
        }
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        mURL = url;
        mID = id;
        mPw = pw;
        mSubDomain = subDomain;
        mPopup.showWait(false);
        mUserDataProvider.login(subDomain, url, id, pw, mGooglePush.getRegistrationId(), mLoginListener, mLoginErrorListener, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        mUserDataProvider = UserDataProvider.getInstance(getApplicationContext());
        mUserDataProvider.init(getApplicationContext()); //app once init
        mLayout = new LoginActivityLayout(this, mLoginActivityLayoutEvent);
        mLayout.initView();
        initCrashReport();
        initValue();
        checkNotificationStart();
        registerBroadcast();

        if (mUserDataProvider.isLogined()) {
            mUserDataProvider.simpleLogin(mLoginListener, mSimpleLoginErrorListener, null);
        } else {
            Handler handler = new Handler();
            handler.removeCallbacks(mSplash);
            handler.postDelayed(mSplash, 1000);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsFirstStart) {
            mLayout.changeScreen();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Setting.BROADCAST_CLEAR));
    }

    @Override
    public void onDestroy() {
        if (mClearReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mClearReceiver);
            mClearReceiver = null;
        }
        if (mLayout != null) {
            mLayout.onDestroy();
            mLayout = null;
        }
        super.onDestroy();
    }

    private void onLoginErrorListener(VolleyError volleyError, Object deliverParam) {
        if (isFinishing()) {
            return;
        }
        mPopup.dismissWiat();

        if (mURL == null || !mURL.equals(ConfigDataProvider.URL)) {
            String error = volleyError.getMessage();
            if (volleyError.networkResponse != null && volleyError.networkResponse.statusCode == 404) {
               //TODO Cloud server address or Cloud Server setting invalid
            }
            if (error != null && (error.indexOf("UnknownHostException") > -1 || error.indexOf("ECONNREFUSED") > -1) || error.indexOf("certification") > -1) {
                //TODO Cloud server address or Cloud Server setting invalid
            }
        }

        mPopup.show(PopupType.ALERT, getString(R.string.login_fail), Setting.getLoginErrorMessage(volleyError, mContext), new OnPopupClickListener() {
            @Override
            public void OnNegative() {
                if (isFinishing()) {
                    return;
                }
                mUserDataProvider.removeCookie();
                // moveTaskToBack(true);
                // android.os.Process.killProcess(android.os.Process.myPid());
            }

            @Override
            public void OnPositive() {
                if (isFinishing()) {
                    return;
                }
                mPopup.showWait(false);
                mUserDataProvider.login(mSubDomain, mURL, mID, mPw, mGooglePush.getRegistrationId(), mLoginListener, mLoginErrorListener, null);
            }
        }, getString(R.string.retry), getString(R.string.cancel));
    }

    private void onLoginListener(User response, Object deliverParam) {
        if (isFinishing()) {
            return;
        }
        mPopup.dismissWiat();
        if (mSubDomain != null) {
            mAppDataProvider.setLatestDomain(mSubDomain);
        }
        if (mID != null) {
            mAppDataProvider.setLatestUserID(mID);
        }

        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        if (mIsGoAlarmList) {
            mIsGoAlarmList = false;
            intent.setAction(Setting.ACTION_NOTIFICATION_START + String.valueOf(System.currentTimeMillis()));
        }
        mIsFirstStart = false;
        mLayout.setPassword("");
        startActivity(intent);
        overridePendingTransition(R.anim.hold, R.anim.fade_out);
    }

    private void onSimpleLoginErrorListener(VolleyError volleyError, Object deliverParam) {
        if (isFinishing()) {
            return;
        }
        mPopup.dismissWiat();
        mUserDataProvider.isValidLogin();
        if (volleyError.getSessionExpire() || "10".equals(volleyError.getCode())) {
            mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.login_expire), null, getString(R.string.ok), null);
            mLayout.changeScreen();
        } else {
            mPopup.show(PopupType.ALERT, getString(R.string.login_fail), Setting.getLoginErrorMessage(volleyError, mContext), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
                    if (isFinishing()) {
                        return;
                    }
                    mUserDataProvider.simpleLogin(mLoginListener, mSimpleLoginErrorListener, null);
                }

                @Override
                public void OnNegative() {
                    if (isFinishing()) {
                        return;
                    }
                    mUserDataProvider.removeCookie();
                    mLayout.changeScreen();
                }
            }, getString(R.string.retry), getString(R.string.cancel));
        }
    }

    private void registerBroadcast() {
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
                    if (action.equals(Setting.BROADCAST_ALL_CLEAR)) {
                        finish();
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_ALL_CLEAR);
            LocalBroadcastManager.getInstance(this).registerReceiver(mClearReceiver, intentFilter);
        }
    }
}