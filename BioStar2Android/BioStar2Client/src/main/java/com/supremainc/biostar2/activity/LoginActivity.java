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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.provider.MobileCardDataProvider;
import com.supremainc.biostar2.sdk.models.enumtype.LocalStorage;
import com.supremainc.biostar2.sdk.models.v2.common.UpdateData;
import com.supremainc.biostar2.sdk.models.v2.login.Login;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.sdk.provider.CommonDataProvider;
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;
import com.supremainc.biostar2.service.push.GooglePush;
import com.supremainc.biostar2.util.FileUtil;
import com.supremainc.biostar2.util.InvalidChecker;
import com.supremainc.biostar2.view.LoginView;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends Activity {
    private static boolean mIsRunning;
    private final String TAG = getClass().getSimpleName();
    private LoginActivity mActivity;
    private Context mContext;
    private AppDataProvider mAppDataProvider;
    private CommonDataProvider mCommonDataProvider;
    private BroadcastReceiver mClearReceiver;
    private GooglePush mGooglePush;
    private InvalidChecker mInvalidChecker;
    private Popup mPopup;
    private String mID;
    private String mPw;
    private String mSubDomain;
    private String mURL;
    private boolean mIsGoAlarmList = false;
    private boolean mIsInit = false;
    private LoginView mLoginView;
    private Handler mHandler;
    private UpdateData mUpdateData;
    private boolean mIsLoginClick = false;
    Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (isFinishing()) {
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUpdateData.url));
            startActivity(intent);
            finish();
        }
    };
    private Runnable mSplash = new Runnable() {
        @Override
        public void run() {
            changeScreen();
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
    private DialogInterface.OnCancelListener mCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface mDialog) {
            if (mCommonDataProvider != null) {
                mCommonDataProvider.cancelAll();
            }
        }
    };
    private Callback<User> mLoginCallback = new Callback<User>() {
        @Override
        public void onFailure(Call<User> call, Throwable t) {
            onLoginFailure(t);
        }

        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (isFinishing()) {
                return;
            }
            mPopup.dismissWiat();
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (mIsGoAlarmList) {
                mIsGoAlarmList = false;
                intent.setAction(Setting.ACTION_NOTIFICATION_START + String.valueOf(System.currentTimeMillis()));
            }
            mLoginView.setPassword("");
            startActivity(intent);
            overridePendingTransition(R.anim.hold, R.anim.fade_out);
            finish();
        }
    };
    private Callback<UpdateData> mUpdateCheckCallback = new Callback<UpdateData>() {
        @Override
        public void onResponse(Call<UpdateData> call, Response<UpdateData> response) {
            try {
                if (isFinishing()) {
                    return;
                }
                mPopup.dismissWiat();
                if (response.body() == null) {
                    nextProcess();
                    return;
                }
                mUpdateData = response.body();
                if (!BuildConfig.DEBUG) {
                    FileUtil.saveFileObj(mContext.getFilesDir() + "/up.dat", response);
                }
                PackageManager manager = getPackageManager();
                PackageInfo packInfo = manager.getPackageInfo(getPackageName(), 0);
                final int clientVersion = packInfo.versionCode;
                String cancel = null;
                if (mUpdateData.forceVersion > clientVersion) {
                    cancel = null;
                } else {
                    cancel = getString(R.string.cancel);
                }
                String content = "";
                if (mUpdateData.message != null) {
                    content = mUpdateData.message;
                }
                if (mUpdateData.version > clientVersion) {
                    mPopup.show(PopupType.CONFIRM, getString(R.string.info), getString(R.string.new_version) + "\n" + content, new OnPopupClickListener() {
                        @Override
                        public void OnNegative() {
                            mPopup.show(PopupType.CONFIRM, getString(R.string.info), getString(R.string.update_guide), new OnPopupClickListener() {
                                @Override
                                public void OnNegative() {
                                    nextProcess();
                                }

                                @Override
                                public void OnPositive() {
                                    nextProcess();
                                }
                            }, null, null);
                        }

                        @Override
                        public void OnPositive() {
                            mHandler.post(mUpdateRunnable);
                        }


                    }, getString(R.string.ok), cancel);
                } else {
                    nextProcess();
                }
            } catch (Exception e) {
                nextProcess();
            }
        }

        @Override
        public void onFailure(Call<UpdateData> call, Throwable t) {
            nextProcess();
        }
    };

    private LoginView.LoginViewListener mLoginViewListener = new LoginView.LoginViewListener() {
        @Override
        public void onClickLogin(String url, String subDomain, String id, String pw) {
            if (mInvalidChecker.isEmptyString(getString(R.string.info), getString(R.string.login_empty), subDomain, id, pw)) {
                return;
            }
            HttpUrl httpUrl = HttpUrl.parse(url);
            if (httpUrl == null) {
                mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.invalid_input_data), null, null, null);
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
//            ConfigDataProvider.setLatestUserID(mContext, mID);
//            ConfigDataProvider.setLatestDomain(mContext, mSubDomain);
//            ConfigDataProvider.setLatestURL(mContext, mURL);
            ConfigDataProvider.setLocalStorage(LocalStorage.SUBDOMAIN,mSubDomain);
            ConfigDataProvider.setLocalStorage(LocalStorage.DOMAIN,mURL);
            ConfigDataProvider.setLocalStorage(LocalStorage.USER_LOGIN_ID,mID);
            mPopup.showWait(mCancelListener);
            mIsLoginClick = true;
            mCommonDataProvider.init(mContext);
            nextProcess();
//            mCommonDataProvider.getAppVersion(mUpdateCheckCallback);
        }
    };

    private void onLoginFailure(Throwable t) {
        mPopup.dismissWiat();
        mIsLoginClick = false;
        String error = getString(R.string.error_network2);
        if (t != null) {
            error = t.getMessage();
        }
        if (error == null || error.isEmpty()) {
            error = getString(R.string.error_network2);
        } else {
            if (error.contains("scode: 10") || error.contains("hcode: 401")) {
                error = getString(R.string.login_expire);
            }
        }
        changeScreen();

        if (error.contains(Setting.ERROR_MESSAGE_SPLITE)) {
            String[] temp = error.split(Setting.ERROR_MESSAGE_SPLITE);
            if (temp[0].isEmpty() || temp[0].equals("null")) {
                error= mContext.getString(R.string.error_network2)+Setting.ERROR_MESSAGE_SPLITE+temp[1];
            }
        }
        mPopup.show(PopupType.ALERT, getString(R.string.fail), error, null, getString(R.string.ok), null);
    }

    private void nextProcess() {
        if (mCommonDataProvider.isLogined() && !mIsLoginClick) {
            mCommonDataProvider.simpleLogin(mLoginCallback);
        } else {
            mIsLoginClick = false;
            LinearLayout loginView = (LinearLayout) findViewById(R.id.splash_after);
            if (mID != null && mPw != null && mSubDomain != null && loginView.getVisibility() == View.VISIBLE) {
                String token = mGooglePush.getRegistrationId();
                Login login = new Login(mContext, token);
                login.user_id = mID;
                login.password = mPw;
                login.name = mSubDomain;
                if (token != null && !token.isEmpty()) {
                    login.notification_token = token;
                }
                mPopup.showWait(mCancelListener);
                mCommonDataProvider.login(mLoginCallback, mURL, login);
            } else {
                mHandler.removeCallbacks(mSplash);
                mHandler.postDelayed(mSplash, 300);
            }
        }
    }

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
        if (!BuildConfig.DEBUG && Setting.IS_CRASH_REPORT && !Setting.CRITTERISM.isEmpty()) {
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
        mAppDataProvider.set(mActivity);
        mLoginView = (LoginView) findViewById(R.id.login_view);
        mLoginView.setListener(mLoginViewListener);
        mLoginView.setAddress((String)mCommonDataProvider.getLocalStorage(LocalStorage.DOMAIN));
        mLoginView.setSubDomain((String)mCommonDataProvider.getLocalStorage(LocalStorage.SUBDOMAIN));
        mLoginView.setID((String)mCommonDataProvider.getLocalStorage(LocalStorage.USER_LOGIN_ID));
        findViewById(R.id.quick_guide).setOnClickListener(mClickListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        mIsRunning = true;
        mCommonDataProvider = CommonDataProvider.getInstance(getApplicationContext());
        mCommonDataProvider.init(getApplicationContext()); //app once init
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initCrashReport();
        initValue();
        checkNotificationStart();
        registerBroadcast();
        if (mHandler == null) {
            mHandler = new Handler();
        }
        if (Build.VERSION.SDK_INT >= 19) {
            MobileCardDataProvider card = new MobileCardDataProvider();
            card.createNewKey(getApplicationContext());
        }
        String splash = getIntent().getStringExtra("NoneSplash");
        if (splash != null) {
            changeScreen();
        }
//        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)) {
   //             mAppDataProvider.setBoolean(AppDataProvider.BooleanType.MOBILE_CARD_NFC, false);
  //          }
  //      }
        mAppDataProvider.setInitValue();

        String url = (String) mCommonDataProvider.getLocalStorage(LocalStorage.DOMAIN);
        if ((url == null || url.isEmpty())) {
            mCommonDataProvider.getAppVersion(mUpdateCheckCallback);
        } else if (((url.contains(":") && !url.contains("biostar2")))) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nextProcess();
                }
            },1000);
        } else {
            mCommonDataProvider.getAppVersion(mUpdateCheckCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mIsInit) {
            initValue();
            mIsInit = true;
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        if (mCommonDataProvider !=null) {
            mCommonDataProvider.resetLocale();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "onNewIntent");
        }
        super.onNewIntent(intent);
        setIntent(intent);
        String splash = intent.getStringExtra("NoneSplash");
        if (splash != null) {
            changeScreen();
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Setting.BROADCAST_CLEAR));
        initValue();
        checkNotificationStart();
        registerBroadcast();
    }

    @Override
    public void onBackPressed() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onBackPressed");
        }
        moveTaskToBack(true);
        finish();
    }

    @Override
    public void onDestroy() {
        if (mClearReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mClearReceiver);
            mClearReceiver = null;
        }
        mIsRunning = false;
        super.onDestroy();
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
                    } else if (action.equals(Setting.BROADCAST_GOTO_ALARMLIST)) {
                        mIsGoAlarmList = true;
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_ALL_CLEAR);
            intentFilter.addAction(Setting.BROADCAST_GOTO_ALARMLIST);
            LocalBroadcastManager.getInstance(this).registerReceiver(mClearReceiver, intentFilter);
        }
    }
    public static boolean isRunning() {
        return mIsRunning;
    }
}