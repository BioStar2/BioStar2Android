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
package com.supremainc.biostar2.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
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
import com.supremainc.biostar2.sdk.datatype.PermissionData.PERMISSION_MODULE;
import com.supremainc.biostar2.sdk.datatype.UserData.User;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;
import com.supremainc.biostar2.sdk.utils.ImageUtil;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;

public class UserInquriyFragment extends BaseFragment {
    private UserInquriyFragmentLayout mLayout;
    private User mUserInfo;

    private UserInquriyFragmentLayout.UserInquriyFragmentLayoutEvent mLayoutEvent = new UserInquriyFragmentLayout.UserInquriyFragmentLayoutEvent() {
        @Override
        public void showTelephone() {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mUserInfo.phone_number));
            startActivity(intent);
        }

        @Override
        public void showEmail() {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + mUserInfo.email));
            startActivity(intent);
        }

        @Override
        public void showUserViewLog() {
            User bundleItem = null;
            try {
                bundleItem = (User) mUserInfo.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(User.TAG, bundleItem);
            mScreenControl.addScreen(ScreenType.MONITOR, bundle);
        }
    };

    public UserInquriyFragment() {
        super();
        setType(ScreenType.USER_INQURIY);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void applyPermission() {
        ActivityCompat.invalidateOptionsMenu(getActivity());
    }

    private boolean initValue(Bundle savedInstanceState) {
        if (mUserInfo == null) {
            mUserInfo = getExtraData(User.TAG, savedInstanceState);
        }
        if (mUserInfo == null) {
            return false;
        }
        setView();
        return true;
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_edit:
                User arg = null;
                try {
                    arg = mUserInfo.clone();
                } catch (CloneNotSupportedException e) {
                    Log.e(TAG, "selected user clone fail");
                    e.printStackTrace();
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(User.TAG, arg);
                mScreenControl.addScreen(ScreenType.USER_MODIFY, bundle);
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
                    final String action = intent.getAction();
                    if (mIsDestroy) {
                        return;
                    }
                    if (action.equals(Setting.BROADCAST_USER)) {
                        User userInfo = getExtraData(Setting.BROADCAST_USER, intent);
                        if (userInfo == null) {
                            return;
                        }

                        try {
                            if (userInfo.user_id.equals(mUserInfo.user_id)) {
                                mUserInfo = userInfo;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "registerBroadcast error:" + e.getMessage());
                            return;
                        }
                        initActionbar(mUserInfo.name, R.drawable.action_bar_bg);
                        setView();
                    } else if (action.equals(Setting.BROADCAST_PREFRENCE_REFRESH)) {
                        setView();
                    } else if (action.equals(Setting.BROADCAST_REROGIN)) {
                        applyPermission();
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_USER);
            intentFilter.addAction(Setting.BROADCAST_PREFRENCE_REFRESH);
            intentFilter.addAction(Setting.BROADCAST_REROGIN);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mLayout == null) {
            mLayout = new UserInquriyFragmentLayout(this, mLayoutEvent);
        }
        View view = mLayout.initView(this, inflater, container, savedInstanceState);
        initBaseValue(mLayout);
        if (!mLayout.isReUsedView()) {
            if (initValue(savedInstanceState) == false) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mToastPopup.show(getString(R.string.none_data), null);
                        mScreenControl.backScreen();
                    }
                }, 1000);
                return null;
            }
            initActionbar(mUserInfo.name, R.drawable.action_bar_bg);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState");
        User bundleItem = null;
        try {
            bundleItem = (User) mUserInfo.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return;
        }
        outState.putSerializable(User.TAG, bundleItem);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = mContext.getMenuInflater();
        if (mPermissionDataProvider.getPermission(PERMISSION_MODULE.USER, true)) {
            inflater.inflate(R.menu.edit, menu);
        } else {
            inflater.inflate(R.menu.menu, menu);
        }
        super.onPrepareOptionsMenu(menu);
    }

    private void setPermission() {
        if (mUserInfo.roles == null || mUserInfo.roles.size() < 1) {
            mLayout.setOperator(false, getString(R.string.none));
        } else {
            int size = mUserInfo.roles.size();
            size--;
            if (size == 0) {
                mLayout.setOperator(true, mUserInfo.roles.get(0).description);
            } else if (size > 0) {
                mLayout.setOperator(true, mUserInfo.roles.get(size).description + " + " + mUserInfo.roles.size());
            }
        }
    }

    private void setView() {
        if (mLayout == null) {
            return;
        }
        mLayout.setUserID(mUserInfo.user_id);
        mLayout.setUserName(mUserInfo.name);
        mLayout.showPin(mUserInfo.pin_exist);

        if (mUserInfo.email == null || mUserInfo.email.equals("")) {
            mLayout.showEmailLink(false, "");
        } else {
            mLayout.showEmailLink(true, mUserInfo.email);
        }


        if (mUserInfo.phone_number == null || mUserInfo.phone_number.equals("")) {
            mLayout.showTelephoneLink(false, "");
        } else {
            mLayout.showTelephoneLink(true, mUserInfo.phone_number);
        }
        setPermission();
        if (mUserInfo.user_group != null) {
            mLayout.setUserGroup(mUserInfo.user_group.name);
            mLayout.setUserGroupID(mUserInfo.user_group.id);
        } else {
            mLayout.setUserGroup(getString(R.string.all_users));
            mLayout.setUserGroupID(String.valueOf(1));
        }
        if (mUserInfo.isActive()) {
            mLayout.setStatus(getString(R.string.active));
        } else {
            mLayout.setStatus(getString(R.string.inactive));
        }
        String sd = mUserInfo.getTimeFormmat(mTimeConvertProvider, User.UserTimeType.start_datetime, TimeConvertProvider.DATE_TYPE.FORMAT_DATE);
        String ed = mUserInfo.getTimeFormmat(mTimeConvertProvider, User.UserTimeType.expiry_datetime, TimeConvertProvider.DATE_TYPE.FORMAT_DATE);
        String pd = " ~ ";
        if (sd != null) {
            pd = sd + pd;
        }
        if (ed != null) {
            pd = pd + ed;
        }
        mLayout.setPeroid(pd);

        int count = 0;
        if (mUserInfo.access_groups != null) {
            count = mUserInfo.access_groups.size();
        }
        mLayout.setAccessGroup(String.valueOf(count));

        count = 0;
        if (mUserInfo.fingerprint_templates != null) {
            count = mUserInfo.fingerprint_templates.size();
        }
        mLayout.setFingerCount(String.valueOf(count));

        count = 0;
        if (mUserInfo.cards != null) {
            count = mUserInfo.cards.size();
        }
        mLayout.setCardCount(String.valueOf(count));

        if (mUserInfo.photo != null && !mUserInfo.photo.isEmpty()) {
            byte[] photoByte = Base64.decode(mUserInfo.photo, 0);
            final Bitmap bmp = ImageUtil.byteArrayToBitmap(photoByte);

            if (bmp != null) {
                mLayout.setBlurBackGroud(ImageUtil.fastBlur(bmp, 32));
                Bitmap rBmp = ImageUtil.getRoundedBitmap(bmp, true);
                mLayout.setUserPhoto(rBmp);
            }
        } else {
            mLayout.setUserPhotoDefault();
            mLayout.setBlurBackGroudDefault();
        }
    }
}
