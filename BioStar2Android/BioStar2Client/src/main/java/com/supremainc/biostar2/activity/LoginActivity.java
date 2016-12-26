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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.crittercism.app.Crittercism;
import com.crittercism.app.CrittercismConfig;
import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.sdk.datatype.v2.Common.VersionData;
import com.supremainc.biostar2.sdk.datatype.v2.User.User;
import com.supremainc.biostar2.service.push.GooglePush;
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;
import com.supremainc.biostar2.sdk.provider.UserDataProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.util.InvalidChecker;
import com.supremainc.biostar2.view.LoginView;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;

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
    private String mID;
    private String mPw;
    private String mSubDomain;
    private String mURL;
    private boolean mIsShowSplash = false;
    private boolean mIsGoAlarmList = false;
    private boolean mIsInit =false;
    private LoginView mLoginView;
    private Handler mHandler;

    private Runnable mSplash = new Runnable() {
        @Override
        public void run() {
            changeScreen();
        }
    };
    private Response.Listener<VersionData> mVersionListener = new Response.Listener<VersionData>() {
        @Override
        public void onResponse(VersionData response, Object deliverParam) {
            if (ConfigDataProvider.getFullURL(mContext) == null) {
                mVersionErrorListener.onErrorResponse(null,null);
            } else {
                mUserDataProvider.login(mSubDomain,  mID, mPw, mGooglePush.getRegistrationId(), mLoginListener, mLoginErrorListener, null);
            }
        }
    };
    private Response.ErrorListener mVersionErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError, Object deliverParam) {
            mPopup.dismissWiat();
            mPopup.show(PopupType.ALERT, getString(R.string.retry_get_preference), Setting.getLoginErrorMessage(volleyError, mContext), new OnPopupClickListener() {
                @Override
                public void OnNegative() {
                    if (isFinishing()) {
                        return;
                    }
                    finish();
                }

                @Override
                public void OnPositive() {
                    if (isFinishing()) {
                        return;
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mUserDataProvider.getServerVersion(mSubDomain,mURL,mVersionListener,mVersionErrorListener,null);
                        }
                    });

                }
            }, getString(R.string.retry), getString(R.string.cancel));
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

    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()) {
                case R.id.quick_guide:
                    Intent intent = new Intent(LoginActivity.this, GuideActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    private LoginView.LoginViewListener mLoginViewListener = new LoginView.LoginViewListener() {
        @Override
        public void onClickLogin(String url, String subDomain, String id, String pw) {
            if (mInvalidChecker.isEmptyString(getString(R.string.info), getString(R.string.login_empty), subDomain, id, pw)) {
                return;
            }
            while (url.endsWith(" ")) {
                url = url.substring(0, url.length() - 1);
            }
            if (!url.endsWith("/")) {
                url = url + "/";
            }

            mURL = url;
            mID = id;
            mPw = pw;
            mSubDomain = subDomain;
            mPopup.showWait(false);
            mUserDataProvider.getServerVersion(mSubDomain,mURL,mVersionListener,mVersionErrorListener,null);
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
        mActivity = this;
        mContext = mActivity.getApplicationContext();
        mPopup = new Popup(this);
        mInvalidChecker = new InvalidChecker(mPopup);
        mGooglePush = new GooglePush(this);
        mAppDataProvider = AppDataProvider.getInstance(mContext);
        mLoginView = (LoginView) findViewById(R.id.login_view);
        mLoginView.setListener(mLoginViewListener);
        mLoginView.setAddress(ConfigDataProvider.getLatestURL(mContext));
        mLoginView.setSubDomain(ConfigDataProvider.getLatestDomain(mContext));
        mLoginView.setID(ConfigDataProvider.getLatestUserID(mContext));
        findViewById(R.id.quick_guide).setOnClickListener(mClickListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        mIsShowSplash = true;
        mUserDataProvider = UserDataProvider.getInstance(getApplicationContext());
        mUserDataProvider.init(getApplicationContext()); //app once init
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initCrashReport();
        initValue();
        checkNotificationStart();
        registerBroadcast();
        if (mHandler == null) {
            mHandler = new Handler();
        }
        if (mUserDataProvider.isLogined()) {
            mUserDataProvider.simpleLogin(mLoginListener, mSimpleLoginErrorListener, null);
        } else {
            mHandler.removeCallbacks(mSplash);
            mHandler.postDelayed(mSplash, 1000);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsShowSplash) {
            changeScreen();
        }
        if (!mIsInit) {
            initValue();
            mIsInit = true;
        }
        if (mHandler == null) {
            mHandler = new Handler();
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
        super.onDestroy();
    }

    private void onLoginErrorListener(VolleyError volleyError, Object deliverParam) {
        if (isFinishing()) {
            return;
        }
        mPopup.dismissWiat();

        if (mURL == null) {
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
                mUserDataProvider.removeCookie();
            }
        }, getString(R.string.ok),null);
    }

    private void onLoginListener(User response, Object deliverParam) {
        if (isFinishing()) {
            return;
        }
        mPopup.dismissWiat();

        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        if (mIsGoAlarmList) {
            mIsGoAlarmList = false;
            intent.setAction(Setting.ACTION_NOTIFICATION_START + String.valueOf(System.currentTimeMillis()));
        }
        mIsShowSplash = false;
        mLoginView.setPassword("");
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
            changeScreen();
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
                    changeScreen();
                }
            }, getString(R.string.retry), getString(R.string.cancel));
        }
    }

    private void changeScreen() {
        LinearLayout login = (LinearLayout) findViewById(R.id.splash_after);
        LinearLayout splash = (LinearLayout) findViewById(R.id.splash);
        if (login.getVisibility() != View.VISIBLE) {
            login.setVisibility(View.VISIBLE);
        }
        if (splash.getVisibility() != View.INVISIBLE) {
            splash.setVisibility(View.INVISIBLE);
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