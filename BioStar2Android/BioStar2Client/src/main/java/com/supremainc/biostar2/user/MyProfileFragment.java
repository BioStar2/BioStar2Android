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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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
import android.widget.EditText;
import android.widget.TextView;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.base.BaseFragment;
import com.supremainc.biostar2.popup.PasswordPopup;
import com.supremainc.biostar2.popup.PasswordPopup.OnPasswordResult;
import com.supremainc.biostar2.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.popup.SelectCustomData;
import com.supremainc.biostar2.popup.SelectPopup;
import com.supremainc.biostar2.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.popup.SelectPopup.SelectType;
import com.supremainc.biostar2.sdk.datatype.AccessGroupData.ListAccessGroup;
import com.supremainc.biostar2.sdk.datatype.CardData.ListCard;
import com.supremainc.biostar2.sdk.datatype.FingerPrintData.ListFingerprintTemplate;
import com.supremainc.biostar2.sdk.datatype.PermissionData.CloudRole;
import com.supremainc.biostar2.sdk.datatype.ResponseStatus;
import com.supremainc.biostar2.sdk.datatype.UserData.User;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;
import com.supremainc.biostar2.sdk.utils.ImageUtil;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.util.InvalidChecker;
import com.supremainc.biostar2.widget.ScreenControl;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.SwitchView;
import com.supremainc.biostar2.widget.SwitchView.OnChangeListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

@SuppressLint("InflateParams")
public class MyProfileFragment extends BaseFragment {
    private static final int DELETE_PICTURE = 2;
    private static final int FROM_GALLERY = 1;
    private final static int REQ_ACTIVITY_CAMERA = 1;
    private final static int REQ_ACTIVITY_CAMERA_CROP = 2;
    private static final int TAKE_PICTURE = 0;
    private MyProfileFragmentLayout mLayout;
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

    private OnCancelListener cancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            if (mCommonDataProvider != null) {
                mCommonDataProvider.cancelAll(TAG);
            }
            ScreenControl.getInstance().backScreen();
        }
    };
    private Response.ErrorListener mModifyUserErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isInValidCheck(error)) {
                return;
            }
            mPopup.dismissWiat();
            mPopup.show(PopupType.ALERT, getString(R.string.fail_retry), Setting.getErrorMessage(error, mContext), popupFail, getString(R.string.ok), getString(R.string.cancel));
        }
    };
    private OnPopupClickListener mPopupSucess = new OnPopupClickListener() {
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
            mScreenControl.backScreen();
        }

        @Override
        public void OnNegative() {
        }
    };
    private Response.Listener<ResponseStatus> mModifyUserListener = new Response.Listener<ResponseStatus>() {
        @Override
        public void onResponse(ResponseStatus response, Object deliverParam) {
            if (mIsDestroy || !isAdded()) {
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
                Log.e(TAG," "+e.getMessage());
            }
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Setting.BROADCAST_UPDATE_MYINFO));
            mPopup.dismissWiat();
            mPopup.show(PopupType.CONFIRM, getString(R.string.info), getString(R.string.user_modify_success), mPopupSucess, null, null);
        }
    };

    private OnPopupClickListener popupFail = new OnPopupClickListener() {
        @Override
        public void OnNegative() {
        }

        @Override
        public void OnPositive() {
            mUserDataProvider.modifyMyProfile(TAG, mUserInfo, mModifyUserListener, mModifyUserErrorListener, null);
        }
    };
    private MyProfileFragmentLayout.MyProfileFragmentLayoutEvent mLayoutEvent = new MyProfileFragmentLayout.MyProfileFragmentLayoutEvent() {
        @Override
        public void showIME(EditText view) {
            MyProfileFragment.this.showIme(view);
        }

        @Override
        public void showPasswodPopup() {
            hideIme(mLayout.getUserIDView());
            MyProfileFragment.this.showPasswodPopup();
        }

        @Override
        public void showPinPasswodPopup() {
            hideIme(mLayout.getUserIDView());
            if (mPinSwitch.getOn()) {
                MyProfileFragment.this.showPinPasswodPopup();
            } else {
                mPinSwitch.setSwitch(true);
            }
        }

        @Override
        public void editFingerPrint() {
            hideIme(mLayout.getUserIDView());
            MyProfileFragment.this.editFingerPrint();
        }

        @Override
        public void editCard() {
            hideIme(mLayout.getUserIDView());
            MyProfileFragment.this.editCard();
        }

        @Override
        public void editUserImage() {
            hideIme(mLayout.getUserIDView());
            MyProfileFragment.this.editUserImage();
        }

        @Override
        public void editAccessGroup() {
            hideIme(mLayout.getUserIDView());
            MyProfileFragment.this.editAccessGroup();
        }

        @Override
        public void editOperator() {
            hideIme(mLayout.getUserIDView());
            MyProfileFragment.this.editOperator();
        }
    };

    public MyProfileFragment() {
        super();
        setType(ScreenType.ALARM);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void UpdateClone() {
        if (mUserInfo.roles != null && mUserInfo.roles.size() > 0) {
            mUserInfo.password = mPasswordData;
            if (mLayout.getUserLoginID().equals("")) {
                mUserInfo.login_id = null;
            } else {
                mUserInfo.login_id = mLayout.getUserLoginID();
            }
        } else {
            mUserInfo.login_id = null;
            mUserInfo.password = null;
        }

        mUserInfo.name = mLayout.getUserName();
        mUserInfo.email = mLayout.getEmail();
        mUserInfo.phone_number = mLayout.getTelephone();

        if (mPinSwitch.getOn()) {
            if (mPinData != null && mPinData.length() > 3) {
                mUserInfo.pin_exist = true;
                mUserInfo.pin = mPinData;
            }
        } else {
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
                break;
            case DELETE:
                mUserInfo.photo = "";
                break;
        }
    }

    private void createUser() {
        Calendar cal = mUserInfo.getTimeCalendar(mTimeConvertProvider, User.UserTimeType.expiry_datetime);
        int year = cal.get(Calendar.YEAR);
        if (year > 2030) {
            cal.set(Calendar.YEAR, 2030);
            mUserInfo.setTimeCalendar(mTimeConvertProvider, User.UserTimeType.expiry_datetime, cal);
        }
        cal = mUserInfo.getTimeCalendar(mTimeConvertProvider, User.UserTimeType.start_datetime);
        year = cal.get(Calendar.YEAR);
        if (year < 2000) {
            cal.set(Calendar.YEAR, 2000);
            mUserInfo.setTimeCalendar(mTimeConvertProvider, User.UserTimeType.start_datetime, cal);
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
        mScreenControl.addScreen(ScreenType.USER_ACCESS_GROUP, bundle);
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
        mScreenControl.addScreen(ScreenType.CARD_RIGISTER, bundle);
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
        mScreenControl.addScreen(ScreenType.FINGERPRINT_REGISTER, bundle);
    }

    private void editOperator() {
        Bundle bundle = new Bundle();
        try {
            bundle.putSerializable(User.TAG, mUserInfo.clone());
            bundle.putSerializable(Setting.DISABLE_MODIFY, true);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return;
        }
        mScreenControl.addScreen(ScreenType.USER_PERMISSION, bundle);
    }
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
                    PackageManager pm = mContext.getPackageManager();
                    PermissionGroupInfo pg = pm.getPermissionGroupInfo(Manifest.permission_group.STORAGE, PackageManager.GET_META_DATA);
                    permissionLabel = pg.loadLabel(pm).toString();
                } catch (Exception e) {

                }
                if (!permissionLabel.isEmpty()) {
                    permissionLabel = "("+permissionLabel+")";

                }
                permissionLabel = getString(R.string.guide_feature_permission)+" "+ getString(R.string.allow_permission)+permissionLabel;
                Snackbar snackbar = Snackbar
                        .make(mLayout.getRootView(), permissionLabel, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.permission_setting), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                                mContext.startActivity(intent);
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

    @Override
    public void onAllow(int requestCode) {
        if (mHandler == null || requestCode != Setting.REQUEST_EXTERNAL_STORAGE) {
            return;
        }
        mHandler.removeCallbacks(mRunRditUserImage);
        mHandler.postDelayed(mRunRditUserImage,1000);
    }
    @Override
    public void onDeny(int requestCode) {
        if (mHandler == null || requestCode != Setting.REQUEST_EXTERNAL_STORAGE) {
            return;
        }
        mHandler.removeCallbacks(mRunDeny);
        mHandler.postDelayed(mRunDeny,1000);
    }


    private void editUserImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if ((ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        Setting.REQUEST_EXTERNAL_STORAGE);
                return;
            }
        }
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mContext, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        linkType.add(new SelectCustomData(getString(R.string.take_picture), TAKE_PICTURE, false));
        linkType.add(new SelectCustomData(getString(R.string.from_gallery), FROM_GALLERY, false));
        linkType.add(new SelectCustomData(getString(R.string.delete_picture), DELETE_PICTURE, false));
        selectPopup.show(SelectType.CUSTOM, new OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem) {
                if (isInValidCheck(null)) {
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
                        mLayout.setUserPhotoDefault();
                        mLayout.setBlurBackGroudDefault();
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
                is = mContext.getContentResolver().openInputStream(uri);
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
        mPinSwitch = mLayout.getPinSwitchView();
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
        if (mLayout == null) {
            mLayout = new MyProfileFragmentLayout(this, mLayoutEvent);
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
            initActionbar(getString(R.string.myprofile), R.drawable.action_bar_bg);
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
        outState.putInt("photoStatus", mPhotoStatus.ordinal());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        hideIme(mLayout.getUserIDView());
        super.onPause();
    }

    @Override
    public void onDestroy() {
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
                if (mInvalidChecker.isEmptyString(getString(R.string.info), getString(R.string.user_create_empty), mLayout.getUserID())) {
                    return true;
                }

                if (mLayout.isOperator()) {
                    if (mInvalidChecker.isEmptyString(getString(R.string.info), getString(R.string.user_create_empty_idpassword), mLayout.getUserLoginID())) {
                        return true;
                    }
                    if (!mUserInfo.password_exist) {
                        if (mPasswordData == null) {
                            mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.user_create_empty_idpassword), null, null, null);
                            return true;
                        }
                    }
                }

                if (mInvalidChecker.isInvalidEmail(getString(R.string.info), getString(R.string.invalid_email), mLayout.getEmail())) {
                    return true;
                }

                if (mPinData != null && mPinData.length() > 0 && mPinData.length() < 4) {
                    mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.pincount), null, null, null);
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
                user.setMyProfile();
                mUserDataProvider.modifyMyProfile(TAG, user, mModifyUserListener, mModifyUserErrorListener, null);
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
                    if (action.equals(Setting.BROADCAST_UPDATE_FINGER)) {
                        ArrayList<ListFingerprintTemplate> fingerTemplate = getExtraData(Setting.BROADCAST_UPDATE_FINGER, intent);
                        if (fingerTemplate == null) {
                            return;
                        }
                        if (mUserInfo != null) {
                            mUserInfo.fingerprint_templates = fingerTemplate;
                            mUserInfo.fingerprint_count = fingerTemplate.size();
                        }
                        setFingerCount();
                        return;
                    }

                    if (action.equals(Setting.BROADCAST_UPDATE_CARD)) {
                        ArrayList<ListCard> Cards = getExtraData(Setting.BROADCAST_UPDATE_CARD, intent);
                        if (Cards == null) {
                            return;
                        }
                        if (mUserInfo != null) {
                            mUserInfo.cards = Cards;
                            mUserInfo.card_count = Cards.size();
                        }
                        setCardCount();
                        return;
                    }

                    if (action.equals(Setting.BROADCAST_UPDATE_USER_ACCESS_GROUP)) {
                        ArrayList<ListAccessGroup> accessGroups = getExtraData(Setting.BROADCAST_UPDATE_USER_ACCESS_GROUP, intent);
                        if (accessGroups == null) {
                            return;
                        }
                        if (mUserInfo != null) {
                            mUserInfo.access_groups = accessGroups;
                        }
                        setAccessGroupCount();
                        return;
                    }

                    if (action.equals(Setting.BROADCAST_PREFRENCE_REFRESH)) {
                        if (mLayout == null || mContext == null) {
                            return;
                        }
                        mLayout.setDateStart(mUserInfo.getTimeFormmat(mTimeConvertProvider, User.UserTimeType.start_datetime, TimeConvertProvider.DATE_TYPE.FORMAT_DATE));
                        mLayout.setDateEnd(mUserInfo.getTimeFormmat(mTimeConvertProvider, User.UserTimeType.expiry_datetime, TimeConvertProvider.DATE_TYPE.FORMAT_DATE));
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
            intentFilter.addAction(Setting.BROADCAST_UPDATE_FINGER);
            intentFilter.addAction(Setting.BROADCAST_UPDATE_CARD);
            intentFilter.addAction(Setting.BROADCAST_UPDATE_USER_ACCESS_GROUP);
            intentFilter.addAction(Setting.BROADCAST_PREFRENCE_REFRESH);
            intentFilter.addAction(Setting.BROADCAST_UPDATE_PERMISSION);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
    }

    private void setAccessGroupCount() {
        if (mLayout == null) {
            return;
        }
        int accessGroupCount = 0;
        if (mUserInfo.access_groups != null) {
            accessGroupCount = accessGroupCount + mUserInfo.access_groups.size();
        }
        mLayout.setAccessGroup(String.valueOf(accessGroupCount));
    }

    private void setCardCount() {
        if (mLayout == null) {
            return;
        }
        if (mUserInfo.cards != null) {
            mLayout.setCardCount(String.valueOf(mUserInfo.cards.size()));
        } else {
            mLayout.setCardCount(String.valueOf("0"));
        }

    }

    private void setFingerCount() {
        if (mLayout == null) {
            return;
        }
        if (mUserInfo.fingerprint_templates != null) {
            mLayout.setFingerCount(String.valueOf(mUserInfo.fingerprint_templates.size()));
        } else {
            mLayout.setFingerCount("0");
        }
    }

    private void setImage(Bitmap bmp) {
        if (bmp == null) {
            return;
        }
        mBlurBmp = ImageUtil.fastBlur(bmp, 32);
        mLayout.setBlurBackGroud(mBlurBmp);
        mRbmp = ImageUtil.getRoundedBitmap(bmp, false);
        mLayout.setUserPhoto(mRbmp);

        mPhotoStatus = PhotoStatus.MODIFY;
        mUserInfo.photo = Base64.encodeToString(ImageUtil.bitmapToByteArray(bmp), 0);
        mUserInfo.photo = mUserInfo.photo.replaceAll("\n", "");
        mBackupPhoto = mUserInfo.photo;
    }

    private void setPermission() {
        if (mUserInfo.roles == null || mUserInfo.roles.size() < 1) {
            mLayout.showOperator(false, getString(R.string.none));
        } else {
            int size = mUserInfo.roles.size();
            size--;
            if (size == 0) {
                mLayout.showOperator(true, mUserInfo.roles.get(0).description);
            } else if (size > 0) {
                mLayout.showOperator(true, mUserInfo.roles.get(size).description + " + " + mUserInfo.roles.size());
            }
        }
    }

    private void setView() {
        if (mUserInfo == null) {
            return;
        }
        mLayout.setUserID(mUserInfo.user_id);
        mLayout.setUserName(mUserInfo.name);

        if (mUserInfo.pin_exist) {
            mLayout.showPin(true);
        } else {
            mLayout.showPin(false);
        }
        mLayout.setUserID(mUserInfo.user_id);
        mLayout.setUserName(mUserInfo.name);
        mLayout.setEmail(mUserInfo.email);
        mLayout.setTelephone(mUserInfo.phone_number);
        mLayout.setLoginID(mUserInfo.login_id);
        setPermission();

        if (mUserInfo.password_exist || (mPasswordData != null && !mPasswordData.isEmpty())) {
            mLayout.setPassword(getString(R.string.password_display));
        } else {
            mLayout.setPassword("");
        }


        if (mUserInfo.user_group != null) {
            mLayout.setUserGroup(mUserInfo.user_group.name);
            mLayout.setUserGroupID(mUserInfo.user_group.id);
        } else {
            //   mLayout.setUserGroup(getString(R.string.all_users));
            mLayout.setUserGroupID(String.valueOf(1));
        }

        if (mUserInfo.isActive()) {
            mLayout.setStatus(getString(R.string.status) + " " + getString(R.string.active));
        } else {
            mLayout.setStatus(getString(R.string.status) + " " + getString(R.string.inactive));
        }
        mLayout.setStatusSwitch(mUserInfo.isActive());
        mLayout.setDateStart(mUserInfo.getTimeFormmat(mTimeConvertProvider, User.UserTimeType.start_datetime, TimeConvertProvider.DATE_TYPE.FORMAT_DATE));
        mLayout.setDateEnd(mUserInfo.getTimeFormmat(mTimeConvertProvider, User.UserTimeType.expiry_datetime, TimeConvertProvider.DATE_TYPE.FORMAT_DATE));
        setAccessGroupCount();
        setFingerCount();
        setCardCount();


        if (mUserInfo.pin_exist || (mPinData != null && !mPinData.isEmpty())) {
            mLayout.setPin(getString(R.string.password_display));
        } else {
            mLayout.setPin("");
        }
        mPinSwitch.init(getActivity(), new OnChangeListener() {
            @Override
            public void onChange(boolean on) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "pin :" + on);
                }
                if (on) {
                    showPinPasswodPopup();
                } else {
                    mLayout.setPin("");
                    mPinData = null;
                }
            }
        }, (mUserInfo.pin_exist || (mPinData != null && !mPinData.isEmpty())));
        mPinSwitch.setSwitch((mUserInfo.pin_exist || (mPinData != null && !mPinData.isEmpty())));
        mLayout.setUserPhotoDefault();
        if (mRbmp != null) {
            mLayout.setUserPhoto(mRbmp);
        } else if (bmp != null) {
            mRbmp = ImageUtil.getRoundedBitmap(bmp, false);
            mLayout.setUserPhoto(mRbmp);
        } else {
            if (mUserInfo.photo != null && !mUserInfo.photo.isEmpty()) {
                byte[] photoByte = Base64.decode(mUserInfo.photo, 0);
                bmp = ImageUtil.byteArrayToBitmap(photoByte);
                if (bmp != null) {
                    mBlurBmp = ImageUtil.fastBlur(bmp, 32);
                    mLayout.setBlurBackGroud(mBlurBmp);
                    mRbmp = ImageUtil.getRoundedBitmap(bmp, false);
                    mLayout.setUserPhoto(mRbmp);
                }
            }
        }
        isExistImageCheck();
    }

    private void showPasswodPopup() {
        PasswordPopup passwordPopup = new PasswordPopup(mContext);
        passwordPopup.show(false, getString(R.string.password), new OnPasswordResult() {
            @Override
            public void OnResult(String data) {
                if (isInValidCheck(null)) {
                    return;
                }
                if (data == null) {
                    if (mUserInfo.password_exist || mPasswordData != null) {
                        mLayout.setPassword(getString(R.string.password_display));
                    } else {
                        mLayout.setPassword("");
                    }
                    return;
                }
                mLayout.setPassword(getString(R.string.password_display));
                mPasswordData = data;
            }
        });
    }

    private void showPinPasswodPopup() {
        PasswordPopup passwordPopup = new PasswordPopup(mContext);
        passwordPopup.show(true, getString(R.string.pin_upper), new OnPasswordResult() {
            @Override
            public void OnResult(String data) {
                if (isInValidCheck(null)) {
                    return;
                }
                if (data == null) {
                    if (mUserInfo.pin_exist || (mPinData != null && !mPinData.isEmpty())) {
                        mLayout.setPin(getString(R.string.password_display));
                    } else {
                        mPinSwitch.setSwitch(false);
                    }
                    return;
                }
                mLayout.setPin(getString(R.string.password_display));
                mPinData = data;
            }
        });
    }

    public enum PhotoStatus {
        NOT_MODIFY, MODIFY, DELETE
    }


}
