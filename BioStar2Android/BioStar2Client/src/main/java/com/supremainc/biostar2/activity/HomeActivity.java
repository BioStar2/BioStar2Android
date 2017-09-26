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
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.db.NotificationDBProvider;
import com.supremainc.biostar2.db.SearchSuggestionProvider;
import com.supremainc.biostar2.fragment.AlarmFragment;
import com.supremainc.biostar2.fragment.AlarmListFragment;
import com.supremainc.biostar2.fragment.BaseFragment;
import com.supremainc.biostar2.fragment.CardFragment;
import com.supremainc.biostar2.fragment.DoorFragment;
import com.supremainc.biostar2.fragment.DoorListFragment;
import com.supremainc.biostar2.fragment.FaceFragment;
import com.supremainc.biostar2.fragment.FingerprintFragment;
import com.supremainc.biostar2.fragment.MainFragment;
import com.supremainc.biostar2.fragment.MobileCardFragment;
import com.supremainc.biostar2.fragment.MonitorFragment;
import com.supremainc.biostar2.fragment.MyProfileFragment;
import com.supremainc.biostar2.fragment.PermisionFragment;
import com.supremainc.biostar2.fragment.PreferenceFragment;
import com.supremainc.biostar2.fragment.RegisterCardFragment;
import com.supremainc.biostar2.fragment.UserAccessGroupFragment;
import com.supremainc.biostar2.fragment.UserInquriyFragment;
import com.supremainc.biostar2.fragment.UserListFragment;
import com.supremainc.biostar2.fragment.UserModifyFragment;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.models.v2.common.SupportFeature;
import com.supremainc.biostar2.sdk.models.v2.common.VersionData;
import com.supremainc.biostar2.sdk.models.v2.door.Doors;
import com.supremainc.biostar2.sdk.models.v2.eventlog.EventTypes;
import com.supremainc.biostar2.sdk.models.v2.permission.PermissionModule;
import com.supremainc.biostar2.sdk.models.v2.preferrence.Preference;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.sdk.models.v2.user.Users;
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;
import com.supremainc.biostar2.service.push.GooglePush;
import com.supremainc.biostar2.view.DrawLayerMenuView;
import com.supremainc.biostar2.widget.ScreenControl;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {
    private static final int REQ_ACTIVITY_DOOR_MAP = 1;
    private static boolean mIsRunning;
    private BaseFragment mFragment;
    private GooglePush mGooglePush;
    private HomeActivity mActivity;
    private Handler mHandler = new Handler();
    private DrawLayerMenuView mDrawLayerMenuView;
    private DrawerLayout mDrawerLayout;
    private NotificationDBProvider mNotiProvider;
    private SearchRecentSuggestions mSuggestions;
    private boolean mIsGoAlarmList = false;
    private ScreenType mScreen = ScreenType.INIT;
    private long mMoveScreenTime = 0L;

    private Callback<Users> mUserCountCallback = new Callback<Users>() {
        @Override
        public void onFailure(Call<Users> call, Throwable t) {
        }

        @Override
        public void onResponse(Call<Users> call, Response<Users> response) {
            if (isIgnoreCallback(call,false)) {
                return;
            }
            if (response.isSuccessful() && response.body() != null) {
                setUserCount(response.body().total);
            }
        }
    };

    private Callback<Doors> mDoorCountCallback = new Callback<Doors>() {
        @Override
        public void onFailure(Call<Doors> call, Throwable t) {
        }

        @Override
        public void onResponse(Call<Doors> call, Response<Doors> response) {
            if (isIgnoreCallback(call,false)) {
                return;
            }
            if (response.isSuccessful() && response.body() != null) {
                setDoorCount(response.body().total);
            }
        }
    };
    private Runnable mCloseDrawer = new Runnable() {
        @Override
        public void run() {
            closeDrawer();
        }
    };
    private DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
        }

        @Override
        public void onDrawerOpened(View drawerView) {
        }

        @Override
        public void onDrawerClosed(View drawerView) {
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            if (newState == DrawerLayout.STATE_DRAGGING) {
                mDrawLayerMenuView.setUserCount(mAppDataProvider.getUserCount());
                mDrawLayerMenuView.setDoorCount(mAppDataProvider.getDoorCount());
                mDrawLayerMenuView.setAlarmCount(mNotiProvider.getUnReadMessageCount());
            }
        }
    };
    private Runnable mROnBack = new Runnable() {
        @Override
        public void run() {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0 && ScreenType.MAIN != mScreen) {
                try {
                    if (mCommonDataProvider != null) {
                        mCommonDataProvider.cancelAll();
                    }
                    fm.popBackStackImmediate();
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "@@@@@@@@@@@@@@@1:" + e.getMessage());
                    }
                }
                return;
            }
            if (ScreenType.MAIN == mScreen) {
                mPopup.show(PopupType.ALERT, getString(R.string.quit), getString(R.string.quit_question), new OnPopupClickListener() {
                    @Override
                    public void OnPositive() {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }

                    @Override
                    public void OnNegative() {
                    }
                }, getString(R.string.ok), getString(R.string.cancel));
            } else {
                gotoScreen(ScreenType.MAIN, null, false);
            }
        }
    };
    private long getMoveScreenDelayTime() {
        long current = SystemClock.elapsedRealtime();
        long delay = 10L;
        long diff = current - mMoveScreenTime;
        if (diff < 1000 && diff > -1 ) {
            delay = 1000 - diff;
        }
        mMoveScreenTime = current;
        return delay;
    }

    private DrawLayerMenuView.OnSelectionListener mDrawMenuSelectionListener = new DrawLayerMenuView.OnSelectionListener() {
        @Override
        public void addScreen(final ScreenType type, final Bundle args) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mActivity.addScreen(type, args, true);
                }
            }, getMoveScreenDelayTime());

        }

        @Override
        public void addScreenNoEffect(final ScreenType type, final Bundle args) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mActivity.addScreen(type, args, false);
                }
            }, getMoveScreenDelayTime());
        }

        @Override
        public void backScreen() {
            onBackPressed();
        }

        @Override
        public void drawMenu() {
            onDrawMenu();
        }

        @Override
        public void onResume(BaseFragment baseFragment) {
            mFragment = baseFragment;
            mScreen = baseFragment.getType();
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "screen:" + mScreen);
            }
        }

        @Override
        public void onSelected(final ScreenType type, final Bundle args) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gotoScreen(type, args, false);
                }
            }, 10);
        }
    };

    public static boolean isRunning() {
        return mIsRunning;
    }

    protected void onBackLogin(boolean isSplash) {
        if (!isFinishing()) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            if (mIsGoAlarmList) {
                mIsGoAlarmList = false;
                intent.setAction(Setting.ACTION_NOTIFICATION_START + String.valueOf(System.currentTimeMillis()));
            }
            if (!isSplash) {
                intent.putExtra("NoneSplash", "NoneSplash");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private boolean activityScreen(ScreenType type, Bundle args) {

        // if (type == ScreenType.MAP) {
        // String doorIndex = null;
        // if (args != null) {
        // doorIndex = args.getString(ConfigDataProvider.DOOR_INDEX);
        // }
        // Intent intent = new Intent(HomeActivity.this, DoorMapActivity.class);
        // if (doorIndex != null) {
        // intent.putExtra(ConfigDataProvider.DOOR_INDEX, doorIndex);
        // }
        //
        // startActivityForResult(intent, REQ_ACTIVITY_DOOR_MAP);
        // return true;
        // }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Setting.REQUEST_EXTERNAL_STORAGE:
            case Setting.REQUEST_READ_PHONE_STATE:
            case Setting.REQUEST_LOCATION:
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        mFragment.onDeny(requestCode);
                        return;
                    }
                }
                mFragment.onAllow(requestCode);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    public void closeDrawer() {
        if (mDrawerLayout.isDrawerOpen(mDrawLayerMenuView)) {
            mDrawerLayout.closeDrawer(mDrawLayerMenuView);
        }
    }

    public void addScreen(ScreenType type, Bundle args, boolean isEffect) {
        if (type == null) {
            return;
        }
        if (activityScreen(type, args)) {
            return;
        }
        closeDrawer();
        if (type == mScreen) {
            return;
        }

        BaseFragment fragment = createFragement(type, args);
        if (fragment == null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "fragment null addScreen:" + type);
            }
            return;
        }

        mFragment = fragment;

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "addScreen:" + type);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (isEffect) {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        }
        transaction.replace(R.id.content_frame, mFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("bug:fix", true);
        super.onSaveInstanceState(outState);
    }

    private void applyPermission() {
        if (mDrawLayerMenuView == null) {
            return;
        }
        mDrawLayerMenuView.showUserMenu(mPermissionDataProvider.getPermission(PermissionModule.USER, false));
        mDrawLayerMenuView.showDoorMenu(mPermissionDataProvider.getPermission(PermissionModule.DOOR, false));
        mDrawLayerMenuView.showAlarmMenu(mPermissionDataProvider.getPermission(PermissionModule.MONITORING, false));
        mDrawLayerMenuView.showMonitorMenu(mPermissionDataProvider.getPermission(PermissionModule.MONITORING, false));
        mDrawLayerMenuView.showMobileCard(false);
        if (VersionData.getCloudVersion(mContext) > 1 && VersionData.isSupportFeature(mContext, SupportFeature.MOBILE_CARD) && Build.VERSION.SDK_INT >= 21) {
            if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) || mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)) {
                mDrawLayerMenuView.showMobileCard(true);
            }
        }
    }

    private BaseFragment createFragement(ScreenType type, Bundle args) {
        BaseFragment fragemnt = null;
        switch (type) {
            case MAIN:
                fragemnt = new MainFragment();
                break;
            case ACCESS_CONTROL:
                // fragemnt = new AccessControlFragment();
                break;
            case ACCESS_GROUP_MODIFY:
                // fragemnt = new AccessGroupModifyFragment();
                break;
            case ACCESS_LEVEL_MODIFY:
                // fragemnt = new AccessLevelModifyFragment();
                break;
            // case TIME_ATTENDANCE_CHART:
            // mFragment = new UserListFragment();
            // // mFragment = new TaChartFragment();
            // break;
            case USER_MODIFY:
                fragemnt = new UserModifyFragment();
                break;
            case USER:
                fragemnt = new UserListFragment();
                break;
            case USER_INQURIY:
                fragemnt = new UserInquriyFragment();
                break;
            case USER_ACCESS_GROUP:
                fragemnt = new UserAccessGroupFragment();
                break;
            case DOOR_LIST:
                fragemnt = new DoorListFragment();
                break;
            case DOOR:
                fragemnt = new DoorFragment();
                break;
            case ALARM:
                fragemnt = new AlarmFragment();
                break;
            case ALARM_LIST:
                fragemnt = new AlarmListFragment();
                break;
            case TA:
                // fragemnt = new CalendarFragment();
                break;
            // case TA_USER:
            // fragemnt = new UserInquriyTAFragment();
            // break;
            case MONITOR:
                fragemnt = new MonitorFragment();
                break;
            case CARD:
                fragemnt = new CardFragment();
                break;
            case CARD_RIGISTER:
                fragemnt = new RegisterCardFragment();
                break;
            case FINGERPRINT_REGISTER:
                fragemnt = new FingerprintFragment();
                break;
            case FACE:
                if (!VersionData.isSupportFeature(mContext, SupportFeature.FACE)) {
                    String info = getString(R.string.need_latest_server_version);
                    if (info.contains("{0}")) {
                        info = info.replace("{0}", SupportFeature.FACE.version);
                    } else {
                        info = SupportFeature.FACE.version + " " + info;
                    }
                    mToastPopup.show(info, null);
                    return null;
                }
                fragemnt = new FaceFragment();
                break;
            case PREFERENCE:
                fragemnt = new PreferenceFragment();
                break;
            case ACCESS_GROUP_SELECT:
                // fragemnt = new AccessGroupSelectListFragment();
                break;
            case MYPROFILE:
                fragemnt = new MyProfileFragment();
                break;
            case USER_PERMISSION:
                fragemnt = new PermisionFragment();
                break;
            case MOBILE_CARD_LIST:
                if (!VersionData.isSupportFeature(mContext, SupportFeature.MOBILE_CARD)) {
                    String info = getString(R.string.need_latest_server_version);
                       if (info.contains("{0}")) {
                        info = info.replace("{0}", SupportFeature.MOBILE_CARD.version);
                    } else {
                        info = SupportFeature.MOBILE_CARD.version + " " + info;
                    }
                    mToastPopup.show(info, null);
                    return null;
                }
                fragemnt = new MobileCardFragment();
                break;
            // case DOOR_ALARM:
            // fragemnt = new DoorAlarmFragment();
            // break;
            // case TEST_PUSH:
            // fragemnt = new UserModifyFragment();
            // break;
            // case TEST_UPDATE:
            // fragemnt = new UserModifyFragment();
            // break;
            // case TEST_EVENT_VIEW:
            // fragemnt = new UserModifyFragment();
            // break;
            default:
                return null;
        }
        if (fragemnt != null) {
            mScreen = type;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (args != null) {
                fragemnt.setArguments(args);
            }
        }
        return fragemnt;
    }

    private void getEventMessage() {
        mPopup.showWait(false);
        mMonitoringDataProvider.getEventMessage(new Callback<EventTypes>() {
            @Override
            public void onResponse(Call<EventTypes> call, Response<EventTypes> response) {
                if (isIgnoreCallback(call,true)) {
                    return;
                }
                if (!response.isSuccessful() || response.body() == null) {
                    onErrorGetEventMessage();
                    return;
                }
                getPreference();
            }

            @Override
            public void onFailure(Call<EventTypes> call, Throwable t) {
                if (isIgnoreCallback(call,true)) {
                    return;
                }
                onErrorGetEventMessage();
            }
        });
    }

    private void getPreference() {
        mCommonDataProvider.getPreference(new Callback<Preference>() {
            @Override
            public void onResponse(Call<Preference> call, Response<Preference> response) {
                if (isIgnoreCallback(call,true)) {
                    return;
                }
                if (!response.isSuccessful() || response.body() == null) {
                    onErrorGetPreference();
                    return;
                }
                mPopup.dismissWiat();
            }

            @Override
            public void onFailure(Call<Preference> call, Throwable t) {
                if (isIgnoreCallback(call,true)) {
                    return;
                }
                onErrorGetPreference();
            }
        });
    }

    public void gotoScreen(ScreenType type, Bundle args, boolean skipAni) {
        if (type == null) {
            return;
        }

        if (activityScreen(type, args)) {
            return;
        }

        switch (type) {
            case LOG_OUT:
                logOut();
                return;
            case OPEN_MENU:
                onDrawMenu();
                return;
            default:
                break;
        }

        BaseFragment fragment = createFragement(type, args);
        if (fragment == null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "fragment null gotoScreen:" + type);
            }
            closeDrawer();
            return;
        }
        mFragment = fragment;

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "gotoScreen:" + type);
        }
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        for (int i = 0; i < count; ++i) {
            try {
                fm.popBackStack();
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "goto popBackStack:" + e.getMessage());
                }
            }
        }

        FragmentTransaction transaction = fm.beginTransaction();
        if (!skipAni) {
            if (type == ScreenType.MAIN) {
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            } else {
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            }
        }
        transaction.replace(R.id.content_frame, mFragment);
        transaction.commitAllowingStateLoss();
        if (type != ScreenType.MAIN) {
            mHandler.postDelayed(mCloseDrawer, 200);
        }
    }

    private void initValue() {
        mActivity = this;
        ScreenControl screenControl = ScreenControl.getInstance(getApplicationContext());
        screenControl.setScreenSelectionListeneer(mDrawMenuSelectionListener);
        mGooglePush = new GooglePush(this);
        mGooglePush.init();
        mNotiProvider = NotificationDBProvider.getInstance(mContext);
        mDrawerLayout = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerListener);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawLayerMenuView = (DrawLayerMenuView) mActivity.findViewById(R.id.drawer_menu);
        mDrawLayerMenuView.setUser(mUserDataProvider.getLoginUserInfo());
        mDrawLayerMenuView.setOnSelectionListener(mDrawMenuSelectionListener);
        mDrawLayerMenuView.setUserCount(mAppDataProvider.getUserCount());
        mDrawLayerMenuView.setDoorCount(mAppDataProvider.getDoorCount());
        mDrawLayerMenuView.setAlarmCount(mNotiProvider.getUnReadMessageCount());
    }

    private void onLogoutFail() {
        mPopup.dismissWiat();
        mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.fail), new OnPopupClickListener() {
            @Override
            public void OnNegative() {

            }

            @Override
            public void OnPositive() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mUserDataProvider.removeCookie();
                        mAppDataProvider.setDoorCount(0);
                        mAppDataProvider.setUserCount(0);
                        onBackLogin(false);
                    }
                });
            }


        }, getString(R.string.ok), getString(R.string.cancel));
    }

    private void logOut() {
        mPopup.showWait(false);
        mCommonDataProvider.logout(new Callback<ResponseStatus>() {
            @Override
            public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
                if (isIgnoreCallback(call,true)) {
                    return;
                }

                if (response.isSuccessful()) {
                    mUserDataProvider.removeCookie();
                    mAppDataProvider.setDoorCount(0);
                    mAppDataProvider.setUserCount(0);
                    mPopup.dismissWiat();
                    onBackLogin(false);
                } else {
                    onLogoutFail();
                }
            }

            @Override
            public void onFailure(Call<ResponseStatus> call, Throwable t) {
                if (isIgnoreCallback(call,true)) {
                    return;
                }
                onLogoutFail();

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult:" + requestCode + "resultcode:" + resultCode);

        if (null == data || Activity.RESULT_OK != resultCode)
            return;
        switch (requestCode) {
            case REQ_ACTIVITY_DOOR_MAP:
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                addScreen(ScreenType.DOOR, bundle, true);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_MENU:
                    onDrawMenu();
                    return true;
                case KeyEvent.KEYCODE_BACK:
                    if (mFragment != null && mFragment.onBack()) {
                        return true;
                    }
                default:
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        registerClear();
        super.onCreate(savedInstanceState);
        mIsRunning = true;
        setContentView(R.layout.activity_home);
        initValue();
        registerBroadcast();

        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "action:" + action);
            }
            if (action != null && action.startsWith(Setting.ACTION_NOTIFICATION_START)) {
                mIsGoAlarmList = true;
            }
        }

        if (mCommonDataProvider.isExistLoginedUser()) {
            applyPermission();
            getEventMessage();
            if (mIsGoAlarmList && (mPermissionDataProvider.getPermission(PermissionModule.MONITORING, false))) {
                mIsGoAlarmList = false;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gotoScreen(ScreenType.ALARM_LIST, null, true);
                    }
                }, 10);

            } else {
                mIsGoAlarmList = false;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gotoScreen(ScreenType.MAIN, null, true);
                    }
                }, 10);
            }
        } else {
            FragmentManager fm = getSupportFragmentManager();
            Log.e(TAG, "aa:" + fm.getBackStackEntryCount());
            if (fm.getBackStackEntryCount() > 0) {
                try {
                    fm.popBackStackImmediate();
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "@@@@@@@@@@@@@@@1:" + e.getMessage());
                    }
                }
            }
            onBackLogin(true);
            return;
        }
        if (mPermissionDataProvider.getPermission(PermissionModule.USER, false)) {
            mUserDataProvider.getUsers(0, 10, null, null, mUserCountCallback);
        }

        if (mPermissionDataProvider.getPermission(PermissionModule.DOOR, false)) {
            mDoorDataProvider.getDoors(0, 10, null, mDoorCountCallback);
        }
        if (mAppDataProvider.getBoolean(AppDataProvider.BooleanType.MOBILE_CARD_BLE,false) && VersionData.isSupportFeature(mActivity, SupportFeature.MOBILE_CARD)) {
            Intent i = new Intent();
            i.setClassName(Setting.APP_PACKAGE, Setting.BLE_SERVICE_RECO_PACKAGE);
            ComponentName name = startService(i);
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "name:" + name);
            }
        }

        if (!ConfigDataProvider.getDebugFlag().equals("") || !Setting.getDebugFlag().equals("")) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String content = ConfigDataProvider.getDebugFlag() + Setting.getDebugFlag();
                    mPopup.show(PopupType.ALERT, getString(R.string.info), content, null, null, null, true);
                }
            },3000);
        }
    }

    @Override
    public void onDestroy() {
        mIsRunning = false;
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
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (BuildConfig.DEBUG) {
                Log.i("search", "query:" + query);
            }
            if (query != null && query.length() > 0) {
                if (mSuggestions == null) {
                    mSuggestions = new SearchRecentSuggestions(this, SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
                }
                mSuggestions.saveRecentQuery(query, null);
            }
            mFragment.onSearch(query);
            return;
        }

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onNewIntent:");
        }

        if (intent != null) {
            String action = intent.getAction();
            if (action != null && action.startsWith(Setting.ACTION_NOTIFICATION_START) && mPermissionDataProvider != null && (mPermissionDataProvider.getPermission(PermissionModule.MONITORING, false))) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gotoScreen(ScreenType.ALARM_LIST, null, true);
                    }
                }, 10);

            }
        }
    }

    @Override
    protected void onResume() {
        ScreenControl screenControl = ScreenControl.getInstance(getApplicationContext());
        screenControl.setScreenSelectionListeneer(mDrawMenuSelectionListener);
        super.onResume();
        mGooglePush.checkNotification();
        if (mIsGoAlarmList) {
            mIsGoAlarmList = false;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    gotoScreen(ScreenType.ALARM_LIST, null, true);
                }
            });
        }
        if (ConfigDataProvider.getFullURL(mContext) == null) {
            onBackLogin(false);
            return;
        }
        mCommonDataProvider.simpleLoginCheck();
    }

    private void registerClear() {
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
                    if (action.equals(Setting.BROADCAST_CLEAR)) {
                        onBackLogin(false);
                    } else if (action.equals(Setting.BROADCAST_ALL_CLEAR)) {
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
    public void onStart() {
        registerClear();
        super.onStart();
    }

    private void onErrorGetEventMessage() {
        mPopup.dismissWiat();
        if (!mMonitoringDataProvider.haveMessage()) {
            mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.retry_get_message), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
                    getEventMessage();
                }

                @Override
                public void OnNegative() {
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }, getString(R.string.ok), getString(R.string.cancel));
        }
    }

    private void onErrorGetPreference() {
        mPopup.dismissWiat();
        if (mDateTimeDataProvider.getSavedPrefrence()) {
            return;
        }
        mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.retry_get_preference), new OnPopupClickListener() {
            @Override
            public void OnPositive() {
                mPopup.showWait(false);
                getPreference();
            }

            @Override
            public void OnNegative() {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }, getString(R.string.ok), getString(R.string.cancel));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, "land");
        } else if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            Log.i(TAG, "port");
        }
    }

    @Override
    public void onBackPressed() {
        mHandler.removeCallbacks(mROnBack);
        mHandler.postDelayed(mROnBack, getMoveScreenDelayTime());
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MENU:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    onDrawMenu();
                }
                return true;
        }
        return super.dispatchKeyEvent(event);
    }

    protected void registerBroadcast() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (isFinishing()) {
                        return;
                    }
                    final String action = intent.getAction();

                    if (action.equals(Setting.BROADCAST_ALARM_UPDATE)) {
                        setAlarmCount();
                    } else if (action.equals(Setting.BROADCAST_USER_COUNT)) {
                        int total = intent.getIntExtra(Setting.BROADCAST_USER_COUNT, -1);
                        int oldTotal = mAppDataProvider.getUserCount();
                        if (total > -1 && total != oldTotal) {
                            mAppDataProvider.setUserCount(total);
                            setUserCount(total);
                        } else if (total == -1) {
                            mUserDataProvider.getUsers(0, 1, null, null, mUserCountCallback);
                        }
                    } else if (action.equals(Setting.BROADCAST_DOOR_COUNT)) {
                        int total = intent.getIntExtra(Setting.BROADCAST_DOOR_COUNT, -1);
                        int oldTotal = mAppDataProvider.getDoorCount();
                        if (total > -1 && total != oldTotal) {
                            mAppDataProvider.setDoorCount(total);
                            setDoorCount(total);
                        } else if (total == -1) {
                            mDoorDataProvider.getDoors(0, 1, null, mDoorCountCallback);
                        }
                    } else if (action.equals(Setting.BROADCAST_REROGIN)) {
                        setUser(mUserDataProvider.getLoginUserInfo());
                        applyPermission();
                    } else if (action.equals(Setting.BROADCAST_PUSH_TOKEN_UPDATE)) {
                        if (mGooglePush != null) {
                            mGooglePush.checkNotification();
                        }
                    } else if (action.equals(Setting.BROADCAST_GOTO_ALARMLIST)) {
                        if (mContext == null) {
                            return;
                        }
                        if (mPermissionDataProvider.getPermission(PermissionModule.MONITORING, false)) {
                            mIsGoAlarmList = true;
                        }
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_ALARM_UPDATE);
            intentFilter.addAction(Setting.BROADCAST_USER_COUNT);
            intentFilter.addAction(Setting.BROADCAST_DOOR_COUNT);
            intentFilter.addAction(Setting.BROADCAST_REROGIN);
            intentFilter.addAction(Setting.BROADCAST_PUSH_TOKEN_UPDATE);
            intentFilter.addAction(Setting.BROADCAST_GOTO_ALARMLIST);
            LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
        }
    }

    public void onDrawMenu() {
        if (mDrawerLayout.isDrawerOpen(mDrawLayerMenuView)) {
            mDrawerLayout.closeDrawer(mDrawLayerMenuView);
        } else {
            mDrawLayerMenuView.setAlarmCount(mNotiProvider.getUnReadMessageCount());
            mDrawerLayout.openDrawer(mDrawLayerMenuView);
        }
    }

    public void setAlarmCount() {
        mDrawLayerMenuView.setAlarmCount(mNotiProvider.getUnReadMessageCount());
    }

    public void setDoorCount(int total) {
        if (mAppDataProvider != null) {
            mAppDataProvider.setDoorCount(total);
        }
        if (mDrawLayerMenuView != null) {
            mDrawLayerMenuView.setDoorCount(total);
        }
    }

    public void setUser(User user) {
        mDrawLayerMenuView.setUser(user);
    }

    public void setUserCount(int total) {
        if (mAppDataProvider != null) {
            mAppDataProvider.setUserCount(total);
        }
        if (mDrawLayerMenuView != null) {
            mDrawLayerMenuView.setUserCount(total);
        }
    }


}