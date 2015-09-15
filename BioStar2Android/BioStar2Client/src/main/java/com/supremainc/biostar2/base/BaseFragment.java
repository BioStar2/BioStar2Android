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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.popup.ToastPopup;
import com.supremainc.biostar2.sdk.provider.AccessGroupDataProvider;
import com.supremainc.biostar2.sdk.provider.AccessLevelDataProvider;
import com.supremainc.biostar2.sdk.provider.CommonDataProvider;
import com.supremainc.biostar2.sdk.provider.DeviceDataProvider;
import com.supremainc.biostar2.sdk.provider.DoorDataProvider;
import com.supremainc.biostar2.sdk.provider.EventDataProvider;
import com.supremainc.biostar2.sdk.provider.PermissionDataProvider;
import com.supremainc.biostar2.sdk.provider.PushDataProvider;
import com.supremainc.biostar2.sdk.provider.UserDataProvider;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.widget.ActionbarTitle;
import com.supremainc.biostar2.widget.OnSingleClickListener;
import com.supremainc.biostar2.widget.ScreenControl;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;

import java.io.Serializable;

public class BaseFragment extends Fragment {
    protected static final int MODE_NORMAL = 0;
    protected String TAG;
    protected Activity mContext;
    protected Handler mHandler = new Handler();
    protected LayoutInflater mInflater;
    protected Resources mResouce;
    private InputMethodManager mImm;
    //data provider
    protected AccessGroupDataProvider mAccessGroupDataProvider;
    protected AccessLevelDataProvider mAccessLevelDataProvider;
    protected CommonDataProvider mCommonDataProvider;
    protected DeviceDataProvider mDeviceDataProvider;
    protected DoorDataProvider mDoorDataProvider;
    protected EventDataProvider mEventDataProvider;
    protected PermissionDataProvider mPermissionDataProvider;
    protected PushDataProvider mPushDataProvider;
    protected UserDataProvider mUserDataProvider;
    // OBJECT
    private BaseFragmentLayout mBaseFragmentLayout;
    protected Popup mPopup;
    protected BroadcastReceiver mReceiver;
    protected ScreenControl mScreenControl;
    protected ToastPopup mToastPopup;
    private ActionbarTitle mActionbarTitle;
    private Toolbar mToolbar;
    //
    protected boolean mIsDataReceived = false;
    protected boolean mIsDestroy = false;
    protected int mSubMode = MODE_NORMAL;
    protected ScreenType mType = ScreenType.INIT;
    // listener
    private OnSingleClickListener mHomeListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            onOptionsItemSelected(null);
        }
    };

    protected OnCancelListener mCancelExitListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            CommonDataProvider.getInstance(mContext).cancelAll(TAG);
            ScreenControl.getInstance().backScreen();
        }
    };

    public BaseFragment() {
        super();
    }

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
        if (bundle == null) {
            return null;
        }
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
        if (intent == null) {
            return null;
        }
        Bundle bundle = getBundle(intent);
        if (bundle == null) {
            return null;
        }
        return getBundleData(tag, bundle);
    }

    protected <T> T getExtraData(String tag, Bundle savedInstanceState) {
        Bundle bundle = savedInstanceState;
        T result = getBundleData(tag, bundle);
        if (result != null) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "getExtraData savedInstanceState used");
            }
            return result;
        }

        bundle = getArguments();
        result = getBundleData(tag, bundle);
        if (result != null) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "getExtraData argument used");
            }
            return result;
        }

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "getExtraData null");
        }
        return null;
    }

    public ScreenType getType() {
        return mType;
    }

    public void setType(ScreenType type) {
        mType = type;
    }

    protected void hideIme(EditText view) {
        if (mImm != null && view != null) {
            mImm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void initActionbar(int homeResId, String title, int backgroundRes) {
        if (mBaseFragmentLayout == null || mToolbar == null) {
            return;
        }
        if (mActionbarTitle == null) {
            ActionBarActivity activity = (ActionBarActivity) getActivity();
            if (mToolbar == null) {
                mToolbar = mBaseFragmentLayout.getToolbar();
            }
            mActionbarTitle = new ActionbarTitle(activity, mToolbar, ScreenControl.getInstance(), homeResId, title, backgroundRes, mHomeListener);
        } else {
            if (title.equals(mActionbarTitle.getTitle())) {
                return;
            }
            mActionbarTitle.init(homeResId, title, backgroundRes);
        }
    }

    protected void initActionbar(String title) {
        if (title == null) {
            return;
        }
        initActionbar(R.drawable.selector_back, title, -1);
    }

    protected void initActionbar(String title, int backgroundRes) {
        initActionbar(R.drawable.selector_back, title, backgroundRes);
    }

    protected void initActionbar(int homeResId, String title) {
        initActionbar(homeResId, title, -1);
    }

    protected void initBaseValue(BaseFragmentLayout layout) {
        mBaseFragmentLayout = layout;
        mToolbar = layout.getToolbar();
        if (mReceiver == null) {
            registerBroadcast();
        }
        if (mContext == null) {
            mContext = getActivity();
        }
        if (mResouce == null) {
            mResouce = mContext.getResources();
        }
        if (mScreenControl == null) {
            mScreenControl = ScreenControl.getInstance();
        }
        if (mPopup == null) {
            mPopup = new Popup(getActivity());
        }
        if (mToastPopup == null) {
            mToastPopup = new ToastPopup(getActivity());
        }
        if (mUserDataProvider == null) {
            mUserDataProvider = UserDataProvider.getInstance(getActivity());
        }
        if (mDeviceDataProvider == null) {
            mDeviceDataProvider = DeviceDataProvider.getInstance(getActivity());
        }
        if (mDoorDataProvider == null) {
            mDoorDataProvider = DoorDataProvider.getInstance(getActivity());
        }
        if (mEventDataProvider == null) {
            mEventDataProvider = EventDataProvider.getInstance(getActivity());
        }
        if (mAccessGroupDataProvider == null) {
            mAccessGroupDataProvider = AccessGroupDataProvider.getInstance(getActivity());
        }
        if (mAccessLevelDataProvider == null) {
            mAccessLevelDataProvider = AccessLevelDataProvider.getInstance(getActivity());
        }
        if (mPermissionDataProvider == null) {
            mPermissionDataProvider = PermissionDataProvider.getInstance(getActivity());
        }
        if (mCommonDataProvider == null) {
            mCommonDataProvider = CommonDataProvider.getInstance(getActivity());
        }
        if (mPushDataProvider == null) {
            mPushDataProvider = PushDataProvider.getInstance(getActivity());
        }
        if (mImm == null) {
            mImm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
        }
    }

    protected boolean isInValidCheck(VolleyError error) {
        if (mIsDestroy || !isAdded()) {
            return true;
        }
        if (error == null) {
            return false;
        }
        if (error.getSessionExpire()) {
            mContext = getActivity();
            if (mPopup == null) {
                mPopup = new Popup(mContext);
            } else {
                mPopup.dismiss();
            }
            mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.login_expire), new OnPopupClickListener() {
                @Override
                public void OnNegative() {

                }

                @Override
                public void OnPositive() {
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Setting.BROADCAST_CLEAR));
                }
            }, getString(R.string.ok), null, false);
            return true;
        }
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onAttach");
        }
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            if (savedInstanceState == null) {
                Log.e(TAG, "onCreate savedInstanceState is null");
            } else {
                Log.e(TAG, "onCreate savedInstanceState is not null");
            }
        }
        super.onCreate(savedInstanceState);
        mIsDestroy = false;
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onActivityCreated");
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onStart");
        }
        if (mActionbarTitle != null) {
            mActionbarTitle.start();
        }
        super.onStart();
    }

    @Override
    public void onResume() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onResume");
        }
        if (mScreenControl != null) {
            mScreenControl.onResume(this);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onPause");
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onDestroy");
        }
        if (mCommonDataProvider != null) {
            mCommonDataProvider.cancelAll(TAG);
        }
        if (mBaseFragmentLayout != null) {
            mBaseFragmentLayout.onDestroy();
        }
        if (mPopup != null) {
            mPopup.dismiss();
        }
        mIsDestroy = true;
        unRegisterBroadcast();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == null) {
            onOptionItemHome();
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                onOptionItemHome();
                return true;
            case R.id.action_menu:
                mScreenControl.drawMenu();
                return true;
        }
        return false;
    }

    public boolean onBack() {
        if (mSubMode != MODE_NORMAL) {
            mSubMode = MODE_NORMAL;
            mContext.invalidateOptionsMenu();
            setSubMode(MODE_NORMAL);
            return true;
        }
        return false;
    }

    private void onOptionItemHome() {
        if (mSubMode != MODE_NORMAL) {
            mSubMode = MODE_NORMAL;
            setSubMode(MODE_NORMAL);
        } else {
            mScreenControl.backScreen();
        }
    }

    public boolean onSearch(String query) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onSearch:" + query);
        }
        if (mIsDestroy || !isAdded()) {
            return true;
        }
        return false;
    }

    protected void registerBroadcast() {

    }

    protected void sendLocalBroadcast(String key, Serializable value) {
        Intent intent = new Intent(key);
        if (value != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(key, value);
            intent.putExtras(bundle);
        }
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    protected void setSubMode(int mode) {

    }

    protected void showIme(EditText view) {
        if (mImm != null && view != null) {
            view.requestFocus();
            mImm.showSoftInput(view, 0);
        }
    }

    protected void unRegisterBroadcast() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }
}
