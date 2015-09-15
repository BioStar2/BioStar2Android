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
package com.supremainc.biostar2.main;

import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.alarm.AlarmFragment;
import com.supremainc.biostar2.alarm.AlarmListFragment;
import com.supremainc.biostar2.base.BaseActivity;
import com.supremainc.biostar2.base.BaseFragment;
import com.supremainc.biostar2.db.SearchSuggestionProvider;
import com.supremainc.biostar2.door.DoorFragment;
import com.supremainc.biostar2.door.DoorListFragment;
import com.supremainc.biostar2.monitor.MonitorFragment;
import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.popup.SelectCustomData;
import com.supremainc.biostar2.popup.SelectPopup;
import com.supremainc.biostar2.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.popup.SelectPopup.SelectType;
import com.supremainc.biostar2.push.GooglePush;
import com.supremainc.biostar2.sdk.datatype.DoorData.Doors;
import com.supremainc.biostar2.sdk.datatype.EventLogData.EventTypes;
import com.supremainc.biostar2.sdk.datatype.PermissionData.PERMISSION_MODULE;
import com.supremainc.biostar2.sdk.datatype.PreferenceData.Preference;
import com.supremainc.biostar2.sdk.datatype.ResponseStatus;
import com.supremainc.biostar2.sdk.datatype.UpdateData;
import com.supremainc.biostar2.sdk.datatype.UserData.User;
import com.supremainc.biostar2.sdk.datatype.UserData.Users;
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;
import com.supremainc.biostar2.sdk.utils.PreferenceUtil;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.setting.PreferenceFragment;
import com.supremainc.biostar2.user.CardFragment;
import com.supremainc.biostar2.user.FingerprintFragment;
import com.supremainc.biostar2.user.MyProfileFragment;
import com.supremainc.biostar2.user.PermisionFragment;
import com.supremainc.biostar2.user.UserAccessGroupFragment;
import com.supremainc.biostar2.user.UserInquriyFragment;
import com.supremainc.biostar2.user.UserListFragment;
import com.supremainc.biostar2.user.UserModifyFragment;
import com.supremainc.biostar2.util.FileUtil;
import com.supremainc.biostar2.widget.DrawLayerMenuView;
import com.supremainc.biostar2.widget.ScreenControl;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity {
    private static final int REQ_ACTIVITY_DOOR_MAP = 1;
    private BaseFragment mFragment;
    private GooglePush mGooglePush;
    private HomeActivity mActivity;
    private Handler mHandler = new Handler();
    private HomeActivityLayout mLayout;
    private static boolean mIsRunning;
    Response.Listener<Users> mUserCountListener = new Response.Listener<Users>() {
        @Override
        public void onResponse(Users response, Object deliverParam) {
            if (isFinishing()) {
                return;
            }
            if (response != null && response.records != null) {
                mAppDataProvider.setUserCount(response.total);
                mLayout.setUserCount(response.total);
            }
        }
    };
    Response.Listener<Doors> mDoorCountListener = new Response.Listener<Doors>() {
        @Override
        public void onResponse(Doors response, Object deliverParam) {
            if (isFinishing()) {
                return;
            }
            if (response != null && response.records != null) {
                mAppDataProvider.setDoorCount(response.total);
                mLayout.setDoorCount(response.total);
            }
        }
    };
    Runnable mCloseDrawer = new Runnable() {
        @Override
        public void run() {
            mLayout.closeDrawer();
        }
    };
    private SearchRecentSuggestions mSuggestions;
    private UpdateData mUpdateData;
    // private static final int REQ_ACTIVITY_PLAY_SERVICE = 3;
    Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
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
                        try {
                            PackageManager manager = getPackageManager();
                            PackageInfo packInfo = manager.getPackageInfo(getPackageName(), 0);
                            int clientVersion = packInfo.versionCode;
                            if (mUpdateData.forceVersion > clientVersion) {
                                mToastPopup.show(getString(R.string.forceUpdate), null);
                                mHandler.post(mUpdateRunnable);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, " " + e.getMessage());
                        }
                        return;
                    }
                    switch (selectedItem.get(0).getIntId()) {
                        case 1: {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUpdateData.url));
                            startActivity(intent);
                            finish();
                            break;
                        }
                        case 2: {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUpdateData.url2));
                            startActivity(intent);
                            finish();
                            break;
                        }
                        default:
                            break;
                    }
                }
            }, linkType, mContext.getString(R.string.select_link), false, false);
        }
    };
    Response.Listener<UpdateData> mUpdateListener = new Response.Listener<UpdateData>() {
        @Override
        public void onResponse(final UpdateData response, Object param) {
            try {
                if (isFinishing() || response == null) {
                    return;
                }
                mUpdateData = response;
                if (!BuildConfig.DEBUG) {
                    FileUtil.saveFileObj(mContext.getFilesDir() + "/up.dat", response);
                }
                PackageManager manager = getPackageManager();
                PackageInfo packInfo = manager.getPackageInfo(getPackageName(), 0);
                final int clientVersion = packInfo.versionCode;
                Integer cancelVeriosn = PreferenceUtil.getIntSharedPreference(mContext, Setting.UPDATE_CANCEL_VERSION);
                if (cancelVeriosn != null && response.version <= cancelVeriosn) {
                    return;
                }
                String cancel = null;
                if (response.forceVersion > clientVersion) {
                    cancel = null;
                } else {
                    cancel = getString(R.string.cancel);
                }
                String content = "";
                if (response.message != null) {
                    content = response.message;
                }
                if (response.version > clientVersion) {
                    mPopup.show(PopupType.CONFIRM, getString(R.string.info), getString(R.string.new_version) + "\n" + content, new OnPopupClickListener() {
                        @Override
                        public void OnNegative() {
                            // TODO setting에서 업데이트 가능하다는 안내문 출력.
                            PreferenceUtil.putSharedPreference(mContext, Setting.UPDATE_CANCEL_VERSION, clientVersion);
                            mPopup.show(PopupType.CONFIRM, getString(R.string.info), getString(R.string.update_guide), null, null, null);
                        }

                        @Override
                        public void OnPositive() {
                            mHandler.post(mUpdateRunnable);
                        }


                    }, getString(R.string.ok), cancel);
                }
            } catch (Exception e) {

            }
        }
    };
    private boolean mIsGoAlarmList = false;
    private ScreenType mScreen = ScreenType.INIT;
    DrawLayerMenuView.OnSelectionListener mDrawMenuSelectionListener = new DrawLayerMenuView.OnSelectionListener() {
        @Override
        public void addScreen(ScreenType type, Bundle args) {
            mActivity.addScreen(type, args);
        }

        @Override
        public void backScreen() {
            onBackPressed();
        }

        @Override
        public void drawMenu() {
            mLayout.onDrawMenu();
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
        public void onSelected(ScreenType type, Bundle args) {
            gotoScreen(type, args, false);
        }
    };
    private Response.Listener<User> mLoginListener = new Response.Listener<User>() {
        @Override
        public void onResponse(User response, Object deliverParam) {
            if (isFinishing()) {
                return;
            }
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Setting.BROADCAST_REROGIN));
            mLayout.setUser(mUserDataProvider.getLoginUserInfo());
            getEventMessage();
            if (mIsGoAlarmList && mPermissionDataProvider.getPermission(PERMISSION_MODULE.DOOR, true)) {
                mIsGoAlarmList = false;
                gotoScreen(ScreenType.ALARM_LIST, null, true);
            } else {
                mIsGoAlarmList = false;
                gotoScreen(ScreenType.MAIN, null, true);
            }
        }
    };
    private Response.ErrorListener mSimpleLoginErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError, Object deliverParam) {
            if (isFinishing()) {
                return;
            }
            mPopup.dismissWiat();
            Popup popup = new Popup(mContext);
            if (volleyError.getSessionExpire() || volleyError.getCode().equals("10")) {
                popup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.login_expire), null, getString(R.string.ok), null);
                finish();
            } else {
                popup.show(PopupType.ALERT, getString(R.string.login_fail), Setting.getLoginErrorMessage(volleyError, mContext), new OnPopupClickListener() {
                    @Override
                    public void OnNegative() {
                        finish();
                    }

                    @Override
                    public void OnPositive() {
                        mUserDataProvider.simpleLogin(mLoginListener, mSimpleLoginErrorListener, null);
                    }


                }, getString(R.string.retry), getString(R.string.cancel));
            }
        }
    };

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

    public static boolean isRunning() {
        return mIsRunning;
    }

    public void addScreen(ScreenType type, Bundle args) {
        if (type == null) {
            return;
        }
        if (activityScreen(type, args)) {
            return;
        }
        mLayout.closeDrawer();
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
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.content_frame, mFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    private void applyPermission() {
        if (mLayout == null) {
            return;
        }
        if (mPermissionDataProvider.getPermission(PERMISSION_MODULE.USER, false)) {
            mLayout.showUserMenu(true);
        } else {
            mLayout.showUserMenu(false);
        }

        if (mPermissionDataProvider.getPermission(PERMISSION_MODULE.DOOR, false)) {
            mLayout.showDoorMenu(true);
        } else {
            mLayout.showDoorMenu(false);
        }

        if (mPermissionDataProvider.getPermission(PERMISSION_MODULE.DOOR, true)) {
            mLayout.showAlarmMenu(true);
        } else {
            mLayout.showAlarmMenu(false);
        }
        if (mPermissionDataProvider.getPermission(PERMISSION_MODULE.MONITORING, false)) {
            mLayout.showMonitorMenu(true);
        } else {
            mLayout.showMonitorMenu(false);
        }
    }

    private BaseFragment createFragement(ScreenType type, Bundle args) {
        BaseFragment fragemnt = null;
        switch (type) {
            // 메인화면
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
            case CARD_RIGISTER:
                fragemnt = new CardFragment();
                break;
            case FINGERPRINT_REGISTER:
                fragemnt = new FingerprintFragment();
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
        mEventDataProvider.getEventMessage(new Listener<EventTypes>() {
            @Override
            public void onResponse(EventTypes response, Object param) {
                if (isFinishing()) {
                    return;
                }
                if (response == null || response.records == null) {
                    onErrorGetEventMessage();
                    return;
                }
                getPreference();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error, Object param) {
                if (isInValidCheck(error)) {
                    return;
                }
                onErrorGetEventMessage();
            }
        }, null);
    }

    private void getPreference() {
        mCommonDataProvider.getPreference(new Listener<Preference>() {
            @Override
            public void onResponse(Preference response, Object param) {
                if (isFinishing()) {
                    return;
                }
                mPopup.dismissWiat();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error, Object param) {
                if (isInValidCheck(error)) {
                    return;
                }
                onErrorGetPreference();
            }
        }, null);
    }

    /**
     * 1 depth menu로 이동. stack을 모두 clear
     *
     * @param type
     * @param args
     */
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
                mLayout.onDrawMenu();
                return;
            default:
                break;
        }

        BaseFragment fragment = createFragement(type, args);
        if (fragment == null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "fragment null gotoScreen:" + type);
            }
            mLayout.closeDrawer();
            return;
        }
        mFragment = fragment;

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "gotoScreen:" + type);
        }

        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
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
    }

    private void isUpdate() {
            String name = null;
            try {
                PackageInfo i = getPackageManager().getPackageInfo(getPackageName(), 0);
                name = i.packageName;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            mCommonDataProvider.getAppVersion(null, mUpdateListener, null, name, null);
    }

    private void logOut() {
        mPopup.showWait(false);
        mUserDataProvider.logout(new Response.Listener<ResponseStatus>() {

            @Override
            public void onResponse(ResponseStatus response, Object param) {
                if (isFinishing()) {
                    return;
                }
                mPopup.dismissWiat();
                finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error, Object param) {
                if (isFinishing()) {
                    return;
                }
                mPopup.dismissWiat();
                if (error != null && error.getSessionExpire()) {
                    finish();
                    return;
                }
                mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.fail), new OnPopupClickListener() {
                    @Override
                    public void OnNegative() {

                    }

                    @Override
                    public void OnPositive() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                // logOut();
                            }
                        });
                    }


                }, getString(R.string.ok), getString(R.string.cancel));
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
                addScreen(ScreenType.DOOR, bundle);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_MENU:
                    mLayout.onDrawMenu();
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
        super.onCreate(savedInstanceState);
        mIsRunning = true;
        initValue();
        mLayout = new HomeActivityLayout(this);
        mLayout.initView(mDrawMenuSelectionListener);
        registerBroadcast();

        if (!ConfigDataProvider.getDebugFlag().equals("") || !Setting.getDebugFlag().equals("")) {
            String content = ConfigDataProvider.getDebugFlag() + Setting.getDebugFlag();
            mPopup.show(PopupType.ALERT, getString(R.string.info), content, null, null, null, true);
        }

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

        if (mCommonDataProvider.isValidLogin()) {
            applyPermission();
            getEventMessage();
            if (mIsGoAlarmList && mPermissionDataProvider.getPermission(PERMISSION_MODULE.DOOR, true)) {
                mIsGoAlarmList = false;
                gotoScreen(ScreenType.ALARM_LIST, null, true);
            } else {
                mIsGoAlarmList = false;
                gotoScreen(ScreenType.MAIN, null, true);
            }
        } else {
            mUserDataProvider.simpleLogin(mLoginListener, mSimpleLoginErrorListener, null);
        }
        mUserDataProvider.getUsers(TAG, mUserCountListener, null, 0, 1, "1", null, null);
        mDoorDataProvider.getDoors(TAG, mDoorCountListener, null, 0, 1, "1", null, null);
        isUpdate();
    }

    @Override
    public void onDestroy() {
        if (mLayout != null) {
            mLayout.onDestroy();
            mLayout = null;
        }
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
                Log.e("search", "query:" + query);
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
            if (action != null && action.startsWith(Setting.ACTION_NOTIFICATION_START) && mPermissionDataProvider != null && mPermissionDataProvider.getPermission(PERMISSION_MODULE.DOOR, true)) {
                gotoScreen(ScreenType.ALARM_LIST, null, true);
            }
        }
    }

    @Override
    protected void onResume() {
        ScreenControl screenControl = ScreenControl.getInstance(getApplicationContext());
        screenControl.setScreenSelectionListeneer(mDrawMenuSelectionListener);
        super.onResume();
        mGooglePush.checkNotification();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void onErrorGetEventMessage() {
        mPopup.dismissWiat();
        if (!mEventDataProvider.haveMessage()) {
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
        if (mCommonDataProvider.getSavedPrefrence()) {
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
        FragmentManager fm = getSupportFragmentManager();

        if (fm.getBackStackEntryCount() > 0) {
            super.onBackPressed();
            return;
        }

        if (ScreenType.MAIN == mScreen) {
            mPopup.show(PopupType.ALERT, getString(R.string.quit), getString(R.string.quit_question), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
                    // finish();
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }

                @Override
                public void OnNegative() {
                }
            }, getString(R.string.ok), getString(R.string.cancel));
        } else {
            if (fm.getBackStackEntryCount() > 0) {
                super.onBackPressed();
                // fm.popBackStack();
            } else {
                gotoScreen(ScreenType.MAIN, null, false);
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MENU:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mLayout.onDrawMenu();
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
                        if (mLayout != null) {
                            mLayout.setAlarmCount();
                        }
                    } else if (action.equals(Setting.BROADCAST_USER_COUNT)) {
                        if (mLayout != null) {
                            int total = intent.getIntExtra(Setting.BROADCAST_USER_COUNT, -1);
                            int oldTotal = mAppDataProvider.getUserCount();
                            if (total > -1 && total != oldTotal) {
                                mAppDataProvider.setUserCount(total);
                                mLayout.setUserCount(total);
                            } else if (total == -1) {
                                mUserDataProvider.getUsers(TAG, mUserCountListener, null, 0, 1, "1", null, null);
                            }
                        }
                    } else if (action.equals(Setting.BROADCAST_DOOR_COUNT)) {
                        if (mLayout != null) {
                            int total = intent.getIntExtra(Setting.BROADCAST_DOOR_COUNT, -1);
                            int oldTotal = mAppDataProvider.getDoorCount();
                            if (total > -1 && total != oldTotal) {
                                mAppDataProvider.setDoorCount(total);
                                mLayout.setDoorCount(total);
                            } else if (total == -1) {
                                mDoorDataProvider.getDoors(TAG, mDoorCountListener, null, 0, 1, "1", null, null);
                            }
                        }
                    } else if (action.equals(Setting.BROADCAST_REROGIN)) {
                        applyPermission();
                    } else if (action.equals(Setting.BROADCAST_PUSH_TOKEN_UPDATE)) {
                        if (mGooglePush != null) {
                            mGooglePush.checkNotification();
                        }
                    } else if (action.equals(Setting.BROADCAST_GOTO_ALARMLIST)) {
                        if (mContext == null) {
                            return;
                        }
                        if (mPermissionDataProvider.getPermission(PERMISSION_MODULE.DOOR, true)) {
                            mIsGoAlarmList = false;
                            gotoScreen(ScreenType.ALARM_LIST, null, true);
                        }
                    } else if (action.equals(Setting.BROADCAST_UPDATE_MYINFO)) {
                        mLayout.setUser(mUserDataProvider.getLoginUserInfo());
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Setting.BROADCAST_REROGIN));
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
            intentFilter.addAction(Setting.BROADCAST_UPDATE_MYINFO);
            LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
        }
    }
}