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

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.models.v1.permission.CloudRole;
import com.supremainc.biostar2.sdk.models.v2.common.BioStarSetting;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.models.v2.common.VersionData;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.sdk.provider.CommonDataProvider;
import com.supremainc.biostar2.sdk.provider.DateTimeDataProvider;
import com.supremainc.biostar2.sdk.utils.ImageUtil;
import com.supremainc.biostar2.util.InvalidChecker;
import com.supremainc.biostar2.util.TextInputFilter;
import com.supremainc.biostar2.view.DetailEditItemView;
import com.supremainc.biostar2.view.DetailSwitchItemView;
import com.supremainc.biostar2.view.DetailTextItemView;
import com.supremainc.biostar2.view.SummaryUserView;
import com.supremainc.biostar2.view.SwitchView;
import com.supremainc.biostar2.widget.ScreenControl;
import com.supremainc.biostar2.widget.popup.PasswordPopup;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.SelectCustomData;
import com.supremainc.biostar2.widget.popup.SelectPopup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfileFragment extends BaseFragment {
    private static final int DELETE_PICTURE = 2;
    private static final int FROM_GALLERY = 1;
    private static final int REQ_ACTIVITY_CAMERA = 1;
    private static final int REQ_ACTIVITY_CAMERA_CROP = 2;
    private static final int TAKE_PICTURE = 0;

    private SwitchView mPinSwitch;
    private Bitmap bmp;
    private String mBackupPhoto = null;
    private Bitmap mBlurBmp;
    private InvalidChecker mInvalidChecker;
    private String mPasswordData;
    private PhotoStatus mPhotoStatus = PhotoStatus.NOT_MODIFY;
    private String mPinData;
    private Bitmap mRbmp;
    private User mUserInfo;

    private SummaryUserView mSummaryUserView;
    private DetailTextItemView mUserIDView;
    private DetailEditItemView mUserNameView;
    private DetailEditItemView mEmailView;
    private DetailEditItemView mTelephoneView;
    private DetailTextItemView mOperatorView;
    private DetailEditItemView mLoginIDView;
    private DetailTextItemView mLoginPasswordView;
    private DetailTextItemView mUserGroupView;
    private DetailTextItemView mStatusView;
    private DetailTextItemView mPeriodView;
    private DetailTextItemView mAccessGroupView;
    private DetailTextItemView mFingerPrintView;
    private DetailTextItemView mCardView;
    private DetailTextItemView mFaceView;
    private DetailSwitchItemView mPinView;
    private TextInputFilter mTextInputFilter;

    private SummaryUserView.SummaryUserViewListener mSummaryUserViewListener = new SummaryUserView.SummaryUserViewListener() {
        @Override
        public void editPhoto() {
            editUserImage();
        }
    };

    private Popup.OnPopupClickListener mPopupSucess = new Popup.OnPopupClickListener() {
        @Override
        public void OnPositive() {
            mUserInfo.photo = mBackupPhoto;
            if (mPhotoStatus == PhotoStatus.DELETE) {
                mBackupPhoto = null;
                mUserInfo.photo = null;
            }
            try {
                sendLocalBroadcast(Setting.BROADCAST_USER, mUserInfo.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return;
            }
            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(Setting.BROADCAST_REROGIN));
            mScreenControl.backScreen();
        }

        @Override
        public void OnNegative() {
        }
    };

    private Callback<ResponseStatus> mModifyUserListener = new Callback<ResponseStatus>() {
        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showErrorPopup(t.getMessage(), false);
        }

        @Override
        public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true, false)) {
                return;
            }
            mUserInfo.photo = mBackupPhoto;
            if (mPhotoStatus == PhotoStatus.DELETE) {
                mBackupPhoto = null;
                mUserInfo.photo = null;
            }
            try {
                mUserDataProvider.setLoginUserInfo(mUserInfo.clone());
            } catch (Exception e) {
                Log.e(TAG, " " + e.getMessage());
            }
            mCommonDataProvider.simpleLogin(null);
            mPopup.dismissWiat();
            mPopup.show(Popup.PopupType.CONFIRM, getString(R.string.info), getString(R.string.user_modify_success), mPopupSucess, null, null);
        }
    };

    private Callback<BioStarSetting> mSettingListener = new Callback<BioStarSetting>() {
        @Override
        public void onFailure(Call<BioStarSetting> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showPasswodPopup();
        }

        @Override
        public void onResponse(Call<BioStarSetting> call, Response<BioStarSetting> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            showPasswodPopup();
        }
    };

    private Callback<User> mSettingListener2 = new Callback<User>() {
        @Override
        public void onFailure(Call<User> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showPasswodPopup();
        }

        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            showPasswodPopup();
        }
    };
    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()) {
                case R.id.user_id:  // intent miss break
                case R.id.email:  // intent miss break
                case R.id.telephone:  // intent miss break
                case R.id.login_id:  // intent miss break
                case R.id.user_name:
                    break;
                default:
                    hideIme(mUserNameView.content);
            }
            switch (v.getId()) {
                case R.id.email:  // intent miss break
                case R.id.telephone:  // intent miss break
                case R.id.login_id:  // intent miss break
                case R.id.user_name: {
                    DetailEditItemView view = (DetailEditItemView) v;
                    view.content.setSelection(view.content.toString2().length());
                    showIme(view.content);
                    break;
                }
                case R.id.operator: {
                    editOperator();
                    break;
                }
                case R.id.login_password: {
                    mPopup.showWait(mCancelExitListener);
                    if (VersionData.getCloudVersion(mActivity) > 1) {
                        request(mCommonDataProvider.getBioStarSetting(mSettingListener));
                    } else {
                        mCommonDataProvider.simpleLogin(mSettingListener2);
                    }
                    break;
                }
                case R.id.access_group: {
                    editAccessGroup();
                    break;
                }
                case R.id.fingerprint: {
                    editFingerPrint();
                    break;
                }
                case R.id.card: {
                    editCard();
                    break;
                }
                case R.id.face: {
                    editFace();
                    break;
                }
                case R.id.pin: {
                    mPinSwitch.setSwitch(!mPinSwitch.getOn());
                    break;
                }
            }
        }
    };
    private Callback<User> mLoginListener = new Callback<User>() {
        @Override
        public void onFailure(Call<User> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showErrorPopup(t.getMessage(), true);
        }

        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true, true)) {
                return;
            }
            mUserInfo = mUserDataProvider.getLoginUserInfo();
            setView();
            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(Setting.BROADCAST_REROGIN));
        }
    };
    private Runnable mRunRditUserImage = new Runnable() {
        @Override
        public void run() {
            editUserImage();
        }
    };
    private Runnable mRunDeny = new Runnable() {
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= 23) {
                String permissionLabel = "";
                try {
                    PackageManager pm = mActivity.getPackageManager();
                    PermissionGroupInfo pg = pm.getPermissionGroupInfo(Manifest.permission_group.STORAGE, PackageManager.GET_META_DATA);
                    permissionLabel = pg.loadLabel(pm).toString();
                } catch (Exception e) {

                }
                if (!permissionLabel.isEmpty()) {
                    permissionLabel = "(" + permissionLabel + ")";

                }
                permissionLabel = getString(R.string.guide_feature_permission) + " " + getString(R.string.allow_permission) + permissionLabel;
                Snackbar snackbar = Snackbar
                        .make(mRootView, permissionLabel, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.permission_setting), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                                mActivity.startActivity(intent);
                            }
                        });
                //snackbar.setActionTextColor(Color.MAGENTA);
                View snackbarView = snackbar.getView();
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setMaxLines(5);
                snackbar.show();
            }
        }
    };

    public MyProfileFragment() {
        super();
        setType(ScreenControl.ScreenType.MYPROFILE);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void UpdateClone() {
        if (VersionData.getCloudVersion(mActivity) > 1) {
            if (mUserInfo.permission != null) {
                mUserInfo.password = mPasswordData;
                if (mLoginIDView.content.toString2().equals("")) {
                    mUserInfo.login_id = null;
                } else {
                    mUserInfo.login_id = mLoginIDView.content.toString2();
                }
            } else {
                mUserInfo.login_id = null;
                mUserInfo.password = null;
            }
        } else {
            if (mUserInfo.roles != null && mUserInfo.roles.size() > 0) {
                mUserInfo.password = mPasswordData;
                if (mLoginIDView.content.toString2().equals("")) {
                    mUserInfo.login_id = null;
                } else {
                    mUserInfo.login_id = mLoginIDView.content.toString2();
                }
            } else {
                mUserInfo.login_id = null;
                mUserInfo.password = null;
            }
        }

        mUserInfo.name = mUserNameView.content.toString2();
        mUserInfo.email = mEmailView.content.toString2();
        mUserInfo.phone_number = mTelephoneView.content.toString2();
        if (mPinSwitch.getOn()) {
            if (mPinData != null && mPinData.length() > 3) {
                mUserInfo.pin_exist = true;
                mUserInfo.pin = mPinData;
            }
        } else {
            mUserInfo.pin_exist = false;
            mUserInfo.pin = "";
        }


        switch (mPhotoStatus) {
            case NOT_MODIFY:
                if (mUserInfo.photo != null) {
                    mBackupPhoto = mUserInfo.photo;
                }
                mUserInfo.photo = null;
                break;
            case MODIFY:
                mUserInfo.photo_exist = true;
                break;
            case DELETE:
                mUserInfo.photo_exist = false;
                mUserInfo.photo = "";
                break;
        }
    }

    private void createUser() {
        Calendar cal = mUserInfo.getTimeCalendar(mDateTimeDataProvider, User.UserTimeType.expiry_datetime);
        if (cal == null) {
            cal = Calendar.getInstance();
        }
        int year = cal.get(Calendar.YEAR);
        if (year > 2030) {
            cal.set(Calendar.YEAR, 2030);
            mUserInfo.setTimeCalendar(mDateTimeDataProvider, User.UserTimeType.expiry_datetime, cal);
        }
        cal = mUserInfo.getTimeCalendar(mDateTimeDataProvider, User.UserTimeType.start_datetime);
        if (cal == null) {
            cal = Calendar.getInstance();
        }
        year = cal.get(Calendar.YEAR);
        if (year < 2000) {
            cal.set(Calendar.YEAR, 2000);
            mUserInfo.setTimeCalendar(mDateTimeDataProvider, User.UserTimeType.start_datetime, cal);
        }
    }

    private void editAccessGroup() {
        Bundle bundle = new Bundle();
        try {
            bundle.putSerializable(User.TAG, mUserInfo.clone());
            bundle.putSerializable(Setting.DISABLE_MODIFY, true);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return;
        }
        mScreenControl.addScreen(ScreenControl.ScreenType.USER_ACCESS_GROUP, bundle);
    }

    private void editCard() {
        Bundle bundle = new Bundle();
        try {
            bundle.putSerializable(User.TAG, mUserInfo.clone());
            bundle.putSerializable(Setting.DISABLE_MODIFY, true);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return;
        }
        mScreenControl.addScreen(ScreenControl.ScreenType.CARD, bundle);
    }

    private void editFace() {
        Bundle bundle = new Bundle();
        try {
            bundle.putSerializable(User.TAG, mUserInfo.clone());
            bundle.putSerializable(Setting.DISABLE_MODIFY, true);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return;
        }
        mScreenControl.addScreen(ScreenControl.ScreenType.FACE, bundle);
    }


    private void editFingerPrint() {
        Bundle bundle = new Bundle();
        try {
            bundle.putSerializable(User.TAG, mUserInfo.clone());
            bundle.putSerializable(Setting.DISABLE_MODIFY, true);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return;
        }
        mScreenControl.addScreen(ScreenControl.ScreenType.FINGERPRINT_REGISTER, bundle);
    }

    private void editOperator() {
//        Bundle bundle = new Bundle();
//        try {
//            bundle.putSerializable(User.TAG, mUserInfo.clone());
//            bundle.putSerializable(Setting.DISABLE_MODIFY, true);
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//            return;
//        }
//        mScreenControl.addScreen(ScreenType.USER_PERMISSION, bundle);
    }

    @Override
    public void onAllow(int requestCode) {
        if (mHandler == null || requestCode != Setting.REQUEST_EXTERNAL_STORAGE) {
            return;
        }
        mHandler.removeCallbacks(mRunRditUserImage);
        mHandler.postDelayed(mRunRditUserImage, 1000);
    }

    @Override
    public void onDeny(int requestCode) {
        if (mHandler == null || requestCode != Setting.REQUEST_EXTERNAL_STORAGE) {
            return;
        }
        mHandler.removeCallbacks(mRunDeny);
        mHandler.postDelayed(mRunDeny, 1000);
    }


    private void editUserImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if ((ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        Setting.REQUEST_EXTERNAL_STORAGE);
                return;
            }
        }
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mActivity, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        linkType.add(new SelectCustomData(getString(R.string.take_picture), TAKE_PICTURE, false));
        linkType.add(new SelectCustomData(getString(R.string.from_gallery), FROM_GALLERY, false));
        linkType.add(new SelectCustomData(getString(R.string.delete_picture), DELETE_PICTURE, false));
        selectPopup.show(SelectPopup.SelectType.CUSTOM, new SelectPopup.OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem, boolean isPositive) {
                if (isInValidCheck()) {
                    return;
                }
                if (selectedItem == null) {
                    return;
                }
                int type = selectedItem.get(0).getIntId();
                switch (type) {
                    case TAKE_PICTURE: {
                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, ImageUtil.getTempFileUri());
                        startActivityForResult(intent, REQ_ACTIVITY_CAMERA);
                        break;
                    }
                    case FROM_GALLERY: {
                        Intent intent = ImageUtil.getImageActionIntent(Intent.ACTION_PICK, false, Setting.USER_PROFILE_IMAGE_SIZE, Setting.USER_PROFILE_IMAGE_SIZE);
                        startActivityForResult(intent, REQ_ACTIVITY_CAMERA_CROP);
                        break;
                    }
                    case DELETE_PICTURE: {
                        mUserInfo.photo = "";
                        mPhotoStatus = PhotoStatus.DELETE;
                        mSummaryUserView.setUserPhotoDefault();
                        mSummaryUserView.setBlurBackGroudDefault();
                        mBackupPhoto = null;
                        break;
                    }
                }
            }
        }, linkType, getString(R.string.edit_photo), false);
    }

    public void getImageUrlWithAuthority(Uri uri) {
        if (uri == null) {
            return;
        }
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = mActivity.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                setImage(ImageUtil.resizeBitmap(bmp, Setting.USER_PROFILE_IMAGE_SIZE, true));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return;
    }

    private boolean initValue(Bundle savedInstanceState) {
        if (mUserInfo == null) {
            mUserInfo = mUserDataProvider.getLoginUserInfo();
        }
        if (mTextInputFilter == null) {
            mTextInputFilter = new TextInputFilter(mImm, mToastPopup);
        }
        if (savedInstanceState != null) {
            int photoMode = savedInstanceState.getInt("photoStatus");
            mPhotoStatus = PhotoStatus.values()[photoMode];

            if (mUserInfo == null) {
                mUserInfo = (User) savedInstanceState.getSerializable(User.TAG);
            }
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "mPhotoStatus restore:" + mPhotoStatus);
            }
        }
        mInvalidChecker = new InvalidChecker(mPopup);
        createUser();
        if (mUserInfo == null) {
            return false;
        }
        mSummaryUserView = (SummaryUserView) mRootView.findViewById(R.id.summray_user);
        mSummaryUserView.init(mSummaryUserViewListener);
        mUserIDView = (DetailTextItemView) mRootView.findViewById(R.id.user_id);
        mUserIDView.content.setTextColor(mActivity.getResources().getColor(R.color.subtext));
        mUserNameView = (DetailEditItemView) mRootView.findViewById(R.id.user_name);
        mTextInputFilter.setFilter(mUserNameView.content, TextInputFilter.EDIT_TYPE.USER_NAME);
        mEmailView = (DetailEditItemView) mRootView.findViewById(R.id.email);
//        mTextInputFilter.setFilter( mEmailView.content, TextInputFilter.EDIT_TYPE.EMAIL);
        mTelephoneView = (DetailEditItemView) mRootView.findViewById(R.id.telephone);
        mTextInputFilter.setFilter(mTelephoneView.content, TextInputFilter.EDIT_TYPE.TELEPHONE);
        mOperatorView = (DetailTextItemView) mRootView.findViewById(R.id.operator);
        mOperatorView.content.setTextColor(mActivity.getResources().getColor(R.color.subtext));
        mLoginIDView = (DetailEditItemView) mRootView.findViewById(R.id.login_id);
        mTextInputFilter.setFilter(mLoginIDView.content, TextInputFilter.EDIT_TYPE.LOGIN_ID);
        mLoginPasswordView = (DetailTextItemView) mRootView.findViewById(R.id.login_password);
        mUserGroupView = (DetailTextItemView) mRootView.findViewById(R.id.user_group);
        mUserGroupView.content.setTextColor(mActivity.getResources().getColor(R.color.subtext));
        mStatusView = (DetailTextItemView) mRootView.findViewById(R.id.status);
        mStatusView.content.setTextColor(mActivity.getResources().getColor(R.color.subtext));
        mPeriodView = (DetailTextItemView) mRootView.findViewById(R.id.period);
        mPeriodView.content.setTextColor(mActivity.getResources().getColor(R.color.subtext));
        mAccessGroupView = (DetailTextItemView) mRootView.findViewById(R.id.access_group);
        mFingerPrintView = (DetailTextItemView) mRootView.findViewById(R.id.fingerprint);
        mCardView = (DetailTextItemView) mRootView.findViewById(R.id.card);
        mFaceView = (DetailTextItemView) mRootView.findViewById(R.id.face);
        mPinView = (DetailSwitchItemView) mRootView.findViewById(R.id.pin);
        setView();
        return true;
    }

    private boolean isExistImageCheck() {
        File cropFile = new File(ImageUtil.getTempFilePath());
        if (cropFile.exists() == false) {
            return false;
        }
        if (bmp != null) {
            bmp.recycle();
            bmp = null;
        }
        bmp = BitmapFactory.decodeFile(ImageUtil.getTempFilePath());
        if (null == bmp) {
            cropFile.delete();
            return false;
        }
        if (mBlurBmp != null) {
            mBlurBmp.recycle();
            mBlurBmp = null;
        }
        cropFile.delete();
        setImage(bmp);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onActivityResult:" + requestCode + "code:" + resultCode);
        }

        if (Activity.RESULT_OK != resultCode)
            return;

        switch (requestCode) {
            case REQ_ACTIVITY_CAMERA: {
                Intent intent = ImageUtil.getImageActionIntent("com.android.camera.action.CROP", true, Setting.USER_PROFILE_IMAGE_SIZE, Setting.USER_PROFILE_IMAGE_SIZE);
                startActivityForResult(intent, REQ_ACTIVITY_CAMERA_CROP);
                break;
            }
            case REQ_ACTIVITY_CAMERA_CROP: {
                if (!isExistImageCheck() && data != null) {
                    getImageUrlWithAuthority(data.getData());
                }
                break;
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setResID(R.layout.fragment_myprofile);
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
            initActionbar(getString(R.string.myprofile), R.drawable.action_bar_bg);
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
        outState.putInt("photoStatus", mPhotoStatus.ordinal());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPopup == null) {
            mPopup = new Popup(getActivity());
        }
        if (mCommonDataProvider == null) {
            mCommonDataProvider = CommonDataProvider.getInstance(getActivity());
        }
        mPopup.showWait(mCancelExitListener);
        mCommonDataProvider.simpleLogin(mLoginListener);
    }

    @Override
    public void onPause() {
        if (mUserNameView != null) {
            hideIme(mUserNameView.content);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mTextInputFilter != null && mLoginIDView != null) {
            mTextInputFilter.setFilter(mLoginIDView.content, TextInputFilter.EDIT_TYPE.NONE);
        }
        super.onDestroy();
        if (bmp != null) {
            bmp.recycle();
            bmp = null;
        }
        if (mBlurBmp != null) {
            mBlurBmp.recycle();
            mBlurBmp = null;
        }
        if (mRbmp != null) {
            mRbmp.recycle();
            mRbmp = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_save:
                if (mInvalidChecker.isEmptyString(getString(R.string.info), getString(R.string.user_create_empty), mUserIDView.content.toString2())) {
                    return true;
                }

                if (mLoginIDView.getVisibility() == View.VISIBLE) {
                    if (mInvalidChecker.isEmptyString(getString(R.string.info), getString(R.string.user_create_empty_idpassword), mLoginIDView.content.toString2())) {
                        return true;
                    }
                    if (!mUserInfo.password_exist) {
                        if (mPasswordData == null) {
                            mPopup.show(Popup.PopupType.ALERT, getString(R.string.info), getString(R.string.user_create_empty_idpassword), null, null, null);
                            return true;
                        }
                    }
                }


                if (mInvalidChecker.isInvalidEmail(getString(R.string.info), getString(R.string.invalid_email), mEmailView.content.toString2())) {
                    return true;
                }

                if (mPinData != null && mPinData.length() > 0 && mPinData.length() < 4) {
                    mPopup.show(Popup.PopupType.ALERT, getString(R.string.info), getString(R.string.pincount), null, null, null);
                    return true;
                }

                UpdateClone();
                mPopup.showWait(true);
                User user = null;
                try {
                    user = mUserInfo.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    return true;
                }
                mUserDataProvider.modifyMyProfile(user, mModifyUserListener);
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void registerBroadcast() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();
                    if (mIsDestroy) {
                        return;
                    }

                    if (action.equals(Setting.BROADCAST_PREFRENCE_REFRESH)) {
                        if (mActivity == null) {
                            return;
                        }
                        mPeriodView.content.setText(mUserInfo.getTimeFormmat(mDateTimeDataProvider, User.UserTimeType.start_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE) +
                                "-" + mUserInfo.getTimeFormmat(mDateTimeDataProvider, User.UserTimeType.expiry_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE));
                        return;
                    }

                    if (action.equals(Setting.BROADCAST_UPDATE_PERMISSION)) {
                        ArrayList<CloudRole> roles = getExtraData(Setting.BROADCAST_UPDATE_PERMISSION, intent);
                        if (roles == null) {
                            return;
                        }
                        if (mUserInfo != null) {
                            mUserInfo.roles = roles;
                        }
                        setPermission();
                        return;
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_PREFRENCE_REFRESH);
            intentFilter.addAction(Setting.BROADCAST_UPDATE_PERMISSION);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
    }

    private void setAccessGroupCount() {
        if (mUserInfo.access_groups != null) {
            mAccessGroupView.content.setText(String.valueOf(mUserInfo.access_groups.size()));
        } else {
            mAccessGroupView.content.setText("0");
        }
    }

    private void setCardCount() {
        int count = 0;
        if (VersionData.getCloudVersion(mActivity) > 1) {
            count = mUserInfo.card_count;
        } else {
            if (mUserInfo.cards != null) {
                count = mUserInfo.cards.size();
            }
        }
        mCardView.content.setText(String.valueOf(count));
        mSummaryUserView.setCardCount(String.valueOf(count));
    }

    private void setFaceCount() {
        int count = mUserInfo.face_template_count;
        mFaceView.content.setText(String.valueOf(count));
        mSummaryUserView.setFaceCount(String.valueOf(count));
    }

    private void setFingerCount() {
        int count = 0;
        if (VersionData.getCloudVersion(mActivity) < 2) {
            if (mUserInfo.fingerprint_templates != null) {
                count = mUserInfo.fingerprint_templates.size();
            }
        } else {
            count = mUserInfo.fingerprint_template_count;
        }
        mFingerPrintView.content.setText(String.valueOf(count));
        mSummaryUserView.setFingerCount(String.valueOf(count));
    }


    private void setImage(Bitmap bmp) {
        if (bmp == null) {
            return;
        }
        mBlurBmp = ImageUtil.fastBlur(bmp, 32);
        mSummaryUserView.setBlurBackGroud(mBlurBmp);


        mPhotoStatus = PhotoStatus.MODIFY;
        Bitmap bmp2 = null;
        byte[] reSizeByte = ImageUtil.bitmapToByteArray(bmp, 20);
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "reSizeByte:" + reSizeByte.length);
        }
        if (reSizeByte.length > Setting.USER_PROFILE_IMAGE_SIZE_BYTE) {
            Log.e(TAG, "reSizeByte2:" + reSizeByte.length);
            reSizeByte = ImageUtil.bitmapToByteArray(bmp, 0);
            if (reSizeByte.length > Setting.USER_PROFILE_IMAGE_SIZE_BYTE) {
                bmp2 = ImageUtil.resizeBitmap(bmp, Setting.USER_PROFILE_IMAGE_SIZE / 2, false);
                reSizeByte = ImageUtil.bitmapToByteArray(bmp2, 0);
                Log.e(TAG, "reSizeByte3:" + reSizeByte.length);
            }
        }
        mUserInfo.photo = Base64.encodeToString(reSizeByte, 0);
        mUserInfo.photo = mUserInfo.photo.replaceAll("\n", "");
        mBackupPhoto = mUserInfo.photo;
        if (bmp2 != null) {
            bmp2.recycle();
            bmp2 = null;
        }
        mRbmp = ImageUtil.getRoundedBitmap(bmp, false);
        mSummaryUserView.setUserPhoto(mRbmp);
    }

    private void setPermission() {
        if (VersionData.getCloudVersion(mActivity) > 1) {
            if (mUserInfo.permission != null) {
                mLoginIDView.setVisibility(View.VISIBLE);
                mLoginPasswordView.setVisibility(View.VISIBLE);

                mOperatorView.content.setText(mUserInfo.permission.name);
                if (mUserInfo.password_exist || (mPasswordData != null && !mPasswordData.isEmpty())) {
                    mLoginPasswordView.content.setText(getString(R.string.password_display));
                } else {
                    mLoginPasswordView.content.setText("");
                }
            } else {
                mOperatorView.content.setText(getString(R.string.none));
                mUserInfo.password_exist = false;
                mLoginPasswordView.content.setText("");
                mLoginIDView.setVisibility(View.GONE);
                mLoginPasswordView.setVisibility(View.GONE);
            }
        } else {
            if (mUserInfo.roles == null || mUserInfo.roles.size() < 1) {
                mOperatorView.content.setText(getString(R.string.none));
                mUserInfo.password_exist = false;
                mLoginPasswordView.content.setText("");
                mLoginIDView.setVisibility(View.GONE);
                mLoginPasswordView.setVisibility(View.GONE);
            } else {
                mLoginIDView.setVisibility(View.VISIBLE);
                mLoginPasswordView.setVisibility(View.VISIBLE);

                int size = mUserInfo.roles.size();
                if (size == 1) {
                    mOperatorView.content.setText(mUserInfo.roles.get(0).description);
                } else if (size > 1) {
                    mOperatorView.content.setText(mUserInfo.roles.get(size).description + " + " + mUserInfo.roles.size());
                }
                if (mUserInfo.password_exist || (mPasswordData != null && !mPasswordData.isEmpty())) {
                    mLoginPasswordView.content.setText(getString(R.string.password_display));
                } else {
                    mLoginPasswordView.content.setText("");
                }
            }
        }
    }


    private void setView() {
        if (mUserInfo == null) {
            return;
        }
        mSummaryUserView.setUserID(mUserInfo.user_id);
        mSummaryUserView.setUserName(mUserInfo.name);
        mSummaryUserView.showPin(mUserInfo.pin_exist);

        mUserIDView.content.setText(mUserInfo.user_id);
        mUserIDView.content.setTextColor(mActivity.getResources().getColor(R.color.content_text));
        mUserNameView.setOnClickListener(mClickListener);
        mUserNameView.setContentText(mUserInfo.name);

        mEmailView.setContentText(mUserInfo.email);
        mEmailView.setOnClickListener(mClickListener);
        mTelephoneView.setContentText(mUserInfo.phone_number);
        mTelephoneView.setOnClickListener(mClickListener);

        setPermission();
        mOperatorView.content.setTextColor(mActivity.getResources().getColor(R.color.content_text));
        mLoginIDView.setContentText(mUserInfo.login_id);
        mLoginIDView.setOnClickListener(mClickListener);
        mLoginPasswordView.enableLink(true, mClickListener);

        if (mUserInfo.user_group != null) {
            mUserGroupView.content.setText(mUserInfo.user_group.name);
            mUserGroupView.content.setTag(mUserInfo.user_group.id);
        } else {
            mUserGroupView.content.setText(getString(R.string.all_users));
            mUserGroupView.content.setTag(String.valueOf(1));
        }
        mUserGroupView.content.setTextColor(mActivity.getResources().getColor(R.color.content_text));
        if (mUserInfo.isActive()) {
            mStatusView.content.setText(getString(R.string.active));
        } else {
            mStatusView.content.setText(getString(R.string.inactive));
        }

        mPeriodView.content.setText(mUserInfo.getTimeFormmat(mDateTimeDataProvider, User.UserTimeType.start_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE) +
                "-" + mUserInfo.getTimeFormmat(mDateTimeDataProvider, User.UserTimeType.expiry_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE));
        mPeriodView.content.setTextColor(mActivity.getResources().getColor(R.color.content_text));
        mAccessGroupView.enableLink(true, mClickListener);
        mAccessGroupView.content.setTextColor(mActivity.getResources().getColor(R.color.content_text));
        mFingerPrintView.enableLink(true, mClickListener);
        mFingerPrintView.content.setTextColor(mActivity.getResources().getColor(R.color.content_text));
        mCardView.enableLink(true, mClickListener);
        mCardView.content.setTextColor(mActivity.getResources().getColor(R.color.content_text));
        mFaceView.enableLink(true, mClickListener);
        mFaceView.content.setTextColor(mActivity.getResources().getColor(R.color.content_text));
        setAccessGroupCount();
        setFingerCount();
        setCardCount();
        setFaceCount();

        mPinView.setOnClickListener(mClickListener);
        mPinSwitch = mPinView.mSwitchView;

        if (mUserInfo.pin_exist || (mPinData != null && !mPinData.isEmpty())) {
            mPinView.mContent.setText(getString(R.string.password_display));
        } else {
            mPinView.mContent.setText("");
        }
        mPinSwitch.init(getActivity(), new SwitchView.OnChangeListener() {
            @Override
            public boolean onChange(boolean on) {
                if (on) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showPinPasswodPopup();
                        }
                    });
                } else {
                    mPinView.mContent.setText("");
                    mPinData = "";
                }
                return true;
            }
        }, (mUserInfo.pin_exist || (mPinData != null && !mPinData.isEmpty())));
        mPinSwitch.setSwitch((mUserInfo.pin_exist || (mPinData != null && !mPinData.isEmpty())));

        mSummaryUserView.setUserPhotoDefault();
        if (mRbmp != null) {
            mSummaryUserView.setUserPhoto(mRbmp);
        } else if (bmp != null) {
            mRbmp = ImageUtil.getRoundedBitmap(bmp, false);
            mSummaryUserView.setUserPhoto(mRbmp);
        } else {
            if (mUserInfo.photo != null && !mUserInfo.photo.isEmpty()) {
                byte[] photoByte = Base64.decode(mUserInfo.photo, 0);
                bmp = ImageUtil.byteArrayToBitmap(photoByte);
                if (bmp != null) {
                    mBlurBmp = ImageUtil.fastBlur(bmp, 32);
                    mSummaryUserView.setBlurBackGroud(mBlurBmp);
                    mRbmp = ImageUtil.getRoundedBitmap(bmp, false);
                    mSummaryUserView.setUserPhoto(mRbmp);
                }
            }
        }
        isExistImageCheck();
    }

    private void showPasswodPopup() {
        PasswordPopup passwordPopup = new PasswordPopup(mActivity);
        passwordPopup.show(false, getString(R.string.password), new PasswordPopup.OnPasswordResult() {
            @Override
            public void OnResult(String data) {
                if (isInValidCheck()) {
                    return;
                }
                if (data == null) {
                    if (mUserInfo.password_exist || mPasswordData != null) {
                        mLoginPasswordView.content.setText(getString(R.string.password_display));
                    } else {
                        mLoginPasswordView.content.setText("");
                    }
                    return;
                }
                mLoginPasswordView.content.setText(getString(R.string.password_display));
                mPasswordData = data;
            }
        });
    }

    private void showPinPasswodPopup() {
        PasswordPopup passwordPopup = new PasswordPopup(mActivity);
        passwordPopup.show(true, getString(R.string.pin_upper), new PasswordPopup.OnPasswordResult() {
            @Override
            public void OnResult(String data) {
                if (isInValidCheck()) {
                    return;
                }
                if (data == null) {
                    if (mUserInfo.pin_exist || (mPinData != null && !mPinData.isEmpty())) {
                        mPinView.mContent.setText(getString(R.string.password_display));
                    } else {
                        mPinSwitch.setSwitch(false);
                    }
                    return;
                }
                mPinView.mContent.setText(getString(R.string.password_display));
                mPinData = data;
            }
        });
    }

    private enum PhotoStatus {
        NOT_MODIFY, MODIFY, DELETE
    }


}
