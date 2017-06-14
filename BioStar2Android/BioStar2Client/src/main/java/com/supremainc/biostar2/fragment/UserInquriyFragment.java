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
package com.supremainc.biostar2.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.models.v2.common.BioStarSetting;
import com.supremainc.biostar2.sdk.models.v2.common.VersionData;
import com.supremainc.biostar2.sdk.models.v2.permission.PermissionModule;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.sdk.provider.DateTimeDataProvider;
import com.supremainc.biostar2.sdk.utils.ImageUtil;
import com.supremainc.biostar2.view.DetailTextItemView;
import com.supremainc.biostar2.view.SummaryUserView;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInquriyFragment extends BaseFragment {
    private User mUserInfo;
    private SummaryUserView mSummaryUserView;
    private DetailTextItemView mUserIDView;
    private DetailTextItemView mUserNameView;
    private DetailTextItemView mEmailView;
    private DetailTextItemView mTelephoneView;
    private DetailTextItemView mOperatorView;
    private DetailTextItemView mUserGroupView;
    private DetailTextItemView mStatusView;
    private DetailTextItemView mPeriodView;
    private DetailTextItemView mAccessGroupView;
    private DetailTextItemView mFingerPrintView;
    private DetailTextItemView mCardView;
    private DetailTextItemView mPinView;
    private DetailTextItemView mFaceView;
    private Bitmap mBmpRound;
    private Bitmap mBmpBlur;

    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()) {
                case R.id.email: {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + mUserInfo.email));
                    startActivity(intent);
                    break;
                }
                case R.id.telephone: {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mUserInfo.phone_number));
                    startActivity(intent);
                    break;
                }
                default:
                    break;
            }
        }
    };

    private SummaryUserView.SummaryUserViewListener mSummaryUserViewListener = new SummaryUserView.SummaryUserViewListener() {

        @Override
        public void editPhoto() {

        }
    };

    private void gotoUserModify() {
        User arg = null;
        try {
            arg = mUserInfo.clone();
            Bundle bundle = new Bundle();
            bundle.putSerializable(User.TAG, arg);
            mScreenControl.addScreen(ScreenType.USER_MODIFY, bundle);
        } catch (CloneNotSupportedException e) {
            showErrorPopup(e.getMessage(),false);
        }
    }

    private Callback<BioStarSetting> mSettingListener = new Callback<BioStarSetting>() {
        @Override
        public void onFailure(Call<BioStarSetting> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            gotoUserModify();
        }

        @Override
        public void onResponse(Call<BioStarSetting> call, Response<BioStarSetting> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true, false)) {
                return;
            }
            gotoUserModify();
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
        mSummaryUserView = (SummaryUserView) mRootView.findViewById(R.id.summray_user);
        mSummaryUserView.init(mSummaryUserViewListener);
        mUserIDView = (DetailTextItemView) mRootView.findViewById(R.id.user_id);
        mUserNameView = (DetailTextItemView) mRootView.findViewById(R.id.user_name);
        mEmailView = (DetailTextItemView) mRootView.findViewById(R.id.email);
        mTelephoneView = (DetailTextItemView) mRootView.findViewById(R.id.telephone);
        mOperatorView = (DetailTextItemView) mRootView.findViewById(R.id.operator);
        mUserGroupView = (DetailTextItemView) mRootView.findViewById(R.id.user_group);
        mStatusView = (DetailTextItemView) mRootView.findViewById(R.id.status);
        mPeriodView = (DetailTextItemView) mRootView.findViewById(R.id.period);
        mAccessGroupView = (DetailTextItemView) mRootView.findViewById(R.id.access_group);
        mFingerPrintView = (DetailTextItemView) mRootView.findViewById(R.id.fingerprint);
        mCardView = (DetailTextItemView) mRootView.findViewById(R.id.card);
        mFaceView = (DetailTextItemView) mRootView.findViewById(R.id.face);
        mPinView = (DetailTextItemView) mRootView.findViewById(R.id.pin);
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
        if (mBmpRound != null) {
            mBmpRound.recycle();
            mBmpRound = null;
        }
        if (mBmpBlur != null) {
            mBmpBlur.recycle();
            mBmpBlur = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_edit:
                if (VersionData.getCloudVersion(mActivity) > 1) {
//                    String message = "";
//                    if (!mPermissionDataProvider.getPermission(PermissionModule.ACCESS_GROUP, false)) {
//                        message = getString(R.string.guide_feature_permission)+"\n"+PermissionModule.ACCESS_GROUP.mName;
//                    }
//                    if (!message.isEmpty()) {
//                        mPopup.show(Popup.PopupType.ALERT, message, null, null, null);
//                        return true;
//                    }
                    mPopup.showWait(mCancelExitListener);
                    request(mCommonDataProvider.getBioStarSetting(mSettingListener));
                } else {
                    gotoUserModify();
                }
                return true;
            case R.id.action_log:
                User bundleItem = null;
                try {
                    bundleItem = (User) mUserInfo.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    return true;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(User.TAG, bundleItem);
                mScreenControl.addScreen(ScreenType.MONITOR, bundle);
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
                    } else if (action.equals(Setting.BROADCAST_UPDATE_CARD)) {
                        User user = getExtraData(Setting.BROADCAST_UPDATE_CARD, intent);
                        if (user == null || user.cards == null) {
                            return;
                        }
                        if (mUserInfo != null) {
                            mUserInfo.cards = user.cards;
                            mUserInfo.card_count = user.cards.size();
                        }
                        setView();
                        return;
                    } else if (action.equals(Setting.BROADCAST_UPDATE_FINGER)) {
                        User user = getExtraData(Setting.BROADCAST_UPDATE_FINGER, intent);
                        if (user == null || user.fingerprint_templates == null) {
                            return;
                        }
                        if (mUserInfo != null) {
                            mUserInfo.fingerprint_templates = user.fingerprint_templates;
                            mUserInfo.fingerprint_template_count = user.fingerprint_templates.size();
                            mUserInfo.fingerprint_count = user.fingerprint_templates.size();
                        }
                        setView();
                        return;
                    } else if (action.equals(Setting.BROADCAST_UPDATE_FACE)) {
                        User user = getExtraData(Setting.BROADCAST_UPDATE_FACE, intent);
                        if (user == null) {
                            return;
                        }
                        if (mUserInfo != null) {
                            mUserInfo.face_template_count = user.face_template_count;
                            if (user.photo != null) {
                                mUserInfo.photo = user.photo;
                                mUserInfo.photo_exist = user.photo_exist;
                            }
                        }
                        setView();
                        return;
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_USER);
            intentFilter.addAction(Setting.BROADCAST_PREFRENCE_REFRESH);
            intentFilter.addAction(Setting.BROADCAST_REROGIN);
            intentFilter.addAction(Setting.BROADCAST_UPDATE_FACE);
            if (VersionData.getCloudVersion(mActivity) > 1) {
                intentFilter.addAction(Setting.BROADCAST_UPDATE_CARD);
            }
            if (VersionData.getCloudVersion(mActivity) > 1) {
                intentFilter.addAction(Setting.BROADCAST_UPDATE_FINGER);
            }
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setResID(R.layout.fragment_user_inquiry);
        super.onCreateView(inflater, container, savedInstanceState);
        if (!mIsReUsed) {
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
            mRootView.invalidate();
        }
        return mRootView;
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
        MenuInflater inflater = mActivity.getMenuInflater();
        if (mPermissionDataProvider.getPermission(PermissionModule.MONITORING,false) && mPermissionDataProvider.isEnableModifyUser(mUserInfo)) {
            inflater.inflate(R.menu.edit_log, menu);
        } else if (mPermissionDataProvider.isEnableModifyUser(mUserInfo)) {
            inflater.inflate(R.menu.edit, menu);
        } else if (mPermissionDataProvider.getPermission(PermissionModule.MONITORING,false))  {
            inflater.inflate(R.menu.log, menu);
        } else {
            inflater.inflate(R.menu.empty, menu);
        }
        super.onPrepareOptionsMenu(menu);
    }

    private void setPermission() {
        mOperatorView.content.setText(getString(R.string.none));
        if (VersionData.getCloudVersion(mActivity) > 1) {
            if (mUserInfo.permission != null) {
                mOperatorView.content.setText(mUserInfo.permission.name);
            }
        } else {
            if (mUserInfo.roles != null && mUserInfo.roles.size() > 0) {
                int size = mUserInfo.roles.size();
                if (size == 1) {
                    mOperatorView.content.setText(mUserInfo.roles.get(0).description);
                } else if (size > 1) {
                    mOperatorView.content.setText(mUserInfo.roles.get(size).description + " + " + mUserInfo.roles.size());
                }
            }
        }
    }

    private void setView() {
        mSummaryUserView.setBlurBackGroudDefault();
        mSummaryUserView.setUserPhotoDefault();
        if (mUserInfo.photo != null && !mUserInfo.photo.isEmpty()) {
            if (mBmpRound != null) {
                mBmpRound.recycle();
                mBmpRound = null;
            }
            if (mBmpBlur != null) {
                mBmpBlur.recycle();
                mBmpBlur = null;
            }
            byte[] photoByte = Base64.decode(mUserInfo.photo, 0);
            Bitmap bmp = ImageUtil.byteArrayToBitmap(photoByte);

            if (bmp != null) {
                mBmpBlur = ImageUtil.fastBlur(bmp, 32);
                mSummaryUserView.setBlurBackGroud(mBmpBlur);
                mBmpRound = ImageUtil.getRoundedBitmap(bmp, true);
                mSummaryUserView.setUserPhoto(mBmpRound);
            }
        }
        mSummaryUserView.setUserID(mUserInfo.user_id);
        mSummaryUserView.setUserName(mUserInfo.name);
        mSummaryUserView.showPin(mUserInfo.pin_exist);

        mUserIDView.content.setText(mUserInfo.user_id);
        if (mUserInfo.name != null) {
            mUserNameView.content.setText(mUserInfo.name);
        }

        if (TextUtils.isEmpty(mUserInfo.email)) {
            mEmailView.enableLink(false, null);
            mEmailView.content.setText("");
        } else {
            mEmailView.enableLink(true, mClickListener);
            mEmailView.content.setText(mUserInfo.email);
        }

        if (TextUtils.isEmpty(mUserInfo.phone_number)) {
            mTelephoneView.enableLink(false, null);
            mTelephoneView.content.setText("");
        } else {
            mTelephoneView.enableLink(true, mClickListener);
            mTelephoneView.content.setText(mUserInfo.phone_number);
        }

        setPermission();
        if (mUserInfo.user_group != null) {
            mUserGroupView.content.setText(mUserInfo.user_group.name);
            mUserGroupView.content.setTag(mUserInfo.user_group.id);
        } else {
            mUserGroupView.content.setText(getString(R.string.all_users));
            mUserGroupView.content.setTag(String.valueOf(1));
        }
        if (mUserInfo.isActive()) {
            mStatusView.content.setText(getString(R.string.active));
        } else {
            mStatusView.content.setText(getString(R.string.inactive));
        }
        String sd = mUserInfo.getTimeFormmat(mDateTimeDataProvider, User.UserTimeType.start_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE);
        String ed = mUserInfo.getTimeFormmat(mDateTimeDataProvider, User.UserTimeType.expiry_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE);
        String pd = " - ";
        if (sd != null) {
            pd = sd + pd;
        }
        if (ed != null) {
            pd = pd + ed;
        }
        mPeriodView.content.setText(pd);

        int count = 0;
        if (mUserInfo.access_groups != null) {
            count = mUserInfo.access_groups.size();
        }
        mAccessGroupView.content.setText(String.valueOf(count));

        count = 0;
        if (VersionData.getCloudVersion(mActivity) < 2) {
            if (mUserInfo.fingerprint_templates != null) {
                count = mUserInfo.fingerprint_templates.size();
                mUserInfo.fingerprint_template_count = count;
            }
        } else {
            count = mUserInfo.fingerprint_template_count;
        }
        mFingerPrintView.content.setText(String.valueOf(count));
        mSummaryUserView.setFingerCount(String.valueOf(count));
        count = 0;
        if (VersionData.getCloudVersion(mActivity) > 1) {
            count = mUserInfo.card_count;
        } else {
            if (mUserInfo.cards != null) {
                count = mUserInfo.cards.size();
                mUserInfo.card_count = count;
            }
        }
        mSummaryUserView.setCardCount(String.valueOf(count));
        mCardView.content.setText(String.valueOf(count));
        mFaceView.content.setText(String.valueOf(mUserInfo.face_template_count));
        mSummaryUserView.setFaceCount(String.valueOf(mUserInfo.face_template_count));
        if (mUserInfo.pin_exist) {
            mPinView.setVisibility(View.VISIBLE);
            mPinView.content.setText(getString(R.string.password_display));
        } else {
            mPinView.setVisibility(View.GONE);
        }
    }
}
