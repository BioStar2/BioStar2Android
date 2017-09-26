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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.models.v1.permission.CloudRole;
import com.supremainc.biostar2.sdk.models.v2.accesscontrol.ListAccessGroup;
import com.supremainc.biostar2.sdk.models.v2.common.BioStarSetting;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.models.v2.common.VersionData;
import com.supremainc.biostar2.sdk.models.v2.permission.UserPermission;
import com.supremainc.biostar2.sdk.models.v2.user.BaseUserGroup;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.sdk.models.v2.user.UserGroup;
import com.supremainc.biostar2.sdk.provider.DateTimeDataProvider;
import com.supremainc.biostar2.sdk.utils.ImageUtil;
import com.supremainc.biostar2.util.InvalidChecker;
import com.supremainc.biostar2.util.TextInputFilter;
import com.supremainc.biostar2.view.DetailEditItemView;
import com.supremainc.biostar2.view.DetailSwitchItemView;
import com.supremainc.biostar2.view.DetailTextItemView;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.view.SummaryUserView;
import com.supremainc.biostar2.view.SwitchView;
import com.supremainc.biostar2.widget.DateTimePicker;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.popup.PasswordPopup;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;
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


@SuppressLint("InflateParams")
public class UserModifyFragment extends BaseFragment {
    private static final int DELETE_PICTURE = 2;
    private static final int FROM_GALLERY = 1;
    private static final int REQ_ACTIVITY_CAMERA = 1;
    private static final int REQ_ACTIVITY_CAMERA_CROP = 2;
    private static final int TAKE_PICTURE = 0;
    boolean mIsNewUser = false;
    private DateTimePicker mDateTimePicker;
    private InvalidChecker mInvalidChecker;
    private UserGroup mInitUserGroup;
    private String mBackupPhoto = null;
    private Bitmap mBmpBlur;
    private int mEndDay;
    private int mEndMonth;
    private int mEndYear;
    private String mPasswordData;
    private PhotoStatus mPhotoStatus = PhotoStatus.NOT_MODIFY;
    private String mPinData;
    private SwitchView mPinSwitch;
    private SwitchView mStatusSwitch;
    private Bitmap mBmpRound;
    private int mStartDay;
    private int mStartMonth;
    private int mStartYear;
    private User mUserInfo;
    private SummaryUserView mSummaryUserView;
    private DetailEditItemView mUserIDView;
    private DetailEditItemView mUserNameView;
    private DetailEditItemView mEmailView;
    private DetailEditItemView mTelephoneView;
    private DetailTextItemView mOperatorView;
    private DetailEditItemView mLoginIDView;
    private DetailTextItemView mLoginPasswordView;
    private DetailTextItemView mUserGroupView;
    private DetailSwitchItemView mStatusView;
    private StyledTextView mDateStartView;
    private StyledTextView mDateEndView;
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
        public void OnNegative() {
        }

        @Override
        public void OnPositive() {
            if (mIsNewUser) {
                sendLocalBroadcast(Setting.BROADCAST_USER, null);
            }
            mScreenControl.backScreen();
        }
    };
    private OnDateSetListener mStartDateListener = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (mDateTimePicker.isErrorSetDate(year, monthOfYear, dayOfMonth, mEndYear, mEndMonth, mEndDay)) {
                mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.error_set_date), null, null, null);
                return;
            }
            if (year < 2000) {
                mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.error_set_start_date), null, null, null);
                return;
            }
            mStartYear = year;
            mStartMonth = monthOfYear;
            mStartDay = dayOfMonth;
            mDateStartView.setText(mDateTimePicker.getDateString(mStartYear, mStartMonth, mStartDay));
        }
    };
    private OnDateSetListener mEndDateListener = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (mDateTimePicker.isErrorSetDate(mStartYear, mStartMonth, mStartDay, year, monthOfYear, dayOfMonth)) {
                mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.error_set_date), null, null, null);
                return;
            }
            if (year > 2030) {
                mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.error_set_end_date), null, null, null);
                year = 2030;
            }
            mEndYear = year;
            mEndMonth = monthOfYear;
            mEndDay = dayOfMonth;
            mDateEndView.setText(mDateTimePicker.getDateString(mEndYear, mEndMonth, mEndDay));
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
    private Callback<User> mLoginListener = new Callback<User>() {
        @Override
        public void onFailure(Call<User> call, Throwable t) {
            if (isIgnoreCallback(call, false)) {
                return;
            }
        }

        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (isIgnoreCallback(call, response, false)) {
                return;
            }
            if (isInvalidResponse(response, false, false)) {
                return;
            }
            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(Setting.BROADCAST_REROGIN));
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
                case R.id.user_id:  // intent miss break
                case R.id.email:  // intent miss break
                case R.id.telephone:  // intent miss break
                case R.id.login_id:  // intent miss break
                case R.id.user_name: {
                    Log.e(TAG, "onClick use edit");
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
                    mPopup.showWait(mCancelStayListener);
                    if (VersionData.getCloudVersion(mActivity) > 1) {
                        request(mCommonDataProvider.getBioStarSetting(mSettingListener));
                    } else {
                        request(mCommonDataProvider.simpleLogin(mSettingListener2));
                    }
                    break;
                }
                case R.id.user_group: {
                    editUserGroup();
                    break;
                }
                case R.id.status: {
                    mStatusView.mSwitchView.setSwitch(!mUserInfo.isActive());
                    break;
                }
                case R.id.date_edit:
                case R.id.date_arrow: {
                    selectDatePicker();
                    break;
                }
                case R.id.date_start: {
                    mDateTimePicker.showDatePicker(mStartDateListener, mStartYear, mStartMonth, mStartDay);
                    break;
                }
                case R.id.date_end: {
                    mDateTimePicker.showDatePicker(mEndDateListener, mEndYear, mEndMonth, mEndDay);
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
    private Callback<User> mUpdateUserListener = new Callback<User>() {
        @Override
        public void onFailure(Call<User> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            try {
                sendLocalBroadcast(Setting.BROADCAST_USER, mUserInfo.clone());
            } catch (CloneNotSupportedException e) {
                showErrorPopup(e.getMessage(), false);
                return;
            }
            mPopup.show(PopupType.CONFIRM, getString(R.string.info), getString(R.string.user_modify_success), mPopupSucess, null, null);
        }

        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true, false)) {
                return;
            }
            try {
                mUserInfo = response.body().clone();
                if (mUserInfo.fingerprint_templates != null) {
                    mUserInfo.fingerprint_count = mUserInfo.fingerprint_templates.size();
                }
                if (mUserInfo.cards != null) {
                    mUserInfo.fingerprint_count = mUserInfo.cards.size();
                    mUserInfo.card_count = mUserInfo.cards.size();
                }
                if (mUserInfo.photo != null && !mUserInfo.photo.isEmpty()) {
                    mUserInfo.photo_exist = true;
                }
                setView();
            } catch (Exception e) {
            }
            try {
                sendLocalBroadcast(Setting.BROADCAST_USER, mUserInfo.clone());
            } catch (CloneNotSupportedException e) {
                showErrorPopup(e.getMessage(), false);
                return;
            }
            mPopup.show(PopupType.CONFIRM, getString(R.string.info), getString(R.string.user_modify_success), mPopupSucess, null, null);
        }
    };
    private Popup.OnPopupClickListener mNextSuccess = new Popup.OnPopupClickListener() {
        @Override
        public void OnNegative() {
            sendLocalBroadcast(Setting.BROADCAST_USER, null);
            mScreenControl.backScreen();
        }

        @Override
        public void OnPositive() {
            mIsNewUser = false;
            mCardView.setVisibility(View.VISIBLE);
            mFingerPrintView.setVisibility(View.VISIBLE);
            mFaceView.setVisibility(View.VISIBLE);
            sendLocalBroadcast(Setting.BROADCAST_USER, null);
            setView();
            initActionbar(mUserInfo.name, R.drawable.action_bar_bg);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ScrollView sv = (ScrollView) mRootView.findViewById(R.id.scroll_container);
                    if (sv != null) {
                        sv.fullScroll(View.FOCUS_DOWN);
                    }
                }
            });
        }
    };

    private Callback<ResponseStatus> mCreateUserListener = new Callback<ResponseStatus>() {
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
            sendLocalBroadcast(Setting.BROADCAST_USER_COUNT, null);
            if (VersionData.getCloudVersion(mActivity) > 1) {
                mPopup.show(PopupType.CONFIRM, getString(R.string.info), getString(R.string.user_create_success) + "\n" + getString(R.string.add_credential), mNextSuccess, getString(android.R.string.yes), getString(android.R.string.no));
            } else {
                mPopup.show(PopupType.CONFIRM, getString(R.string.info), getString(R.string.user_create_success), mPopupSucess, null, null);
            }
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
            if (mUserDataProvider.getLoginUserInfo().user_id.equals(mUserInfo.user_id)) {
                try {
                    mUserDataProvider.setLoginUserInfo(mUserInfo.clone());
                } catch (Exception e) {
                    Log.e(TAG, " " + e.getMessage());
                }
                mCommonDataProvider.simpleLogin(mLoginListener);
            }
            if (mLoginPasswordView.getVisibility() == View.VISIBLE) {
                if (mPasswordData != null) {
                    mUserInfo.password_exist = true;
                }
            }
            mPopup.showWait(mCancelStayListener);
            request(mUserDataProvider.getUser(mUserInfo.user_id, mUpdateUserListener));
        }
    };
    private Callback<BioStarSetting> mSaveListener = new Callback<BioStarSetting>() {
        @Override
        public void onFailure(Call<BioStarSetting> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showErrorPopup(t.getMessage(), false);
        }

        @Override
        public void onResponse(Call<BioStarSetting> call, Response<BioStarSetting> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true, false)) {
                return;
            }
            save();
        }
    };

    public UserModifyFragment() {
        super();
        setType(ScreenType.USER_MODIFY);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void save() {
        if (mInvalidChecker.isEmptyString(getString(R.string.info), getString(R.string.user_create_empty), mUserIDView.content.toString2())) {
            mPopup.dismissWiat();
            return;
        }
        if (mCommonDataProvider.isAlphaNumericUserID() == false) {
            try {
                long userId = Long.valueOf(mUserIDView.content.toString2());

                if (userId < 1 || userId > 4294967294L || mUserIDView.content.toString2().startsWith("0")) {
                    mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.invalid_userid), null, null, null);
                    resetUserIDFilter();
                    return;
                }
            } catch (Exception e) {
                mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.invalid_userid), null, null, null);
                resetUserIDFilter();
                return;
            }
        }
        if (mLoginIDView.getVisibility() == View.VISIBLE) {
            if (mInvalidChecker.isEmptyString(getString(R.string.info), getString(R.string.user_create_empty_idpassword), mLoginIDView.content.toString2())) {
                mPopup.dismissWiat();
                return;
            }
            if (!mUserInfo.password_exist) {
                if (mPasswordData == null) {
                    mPopup.dismissWiat();
                    mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.user_create_empty_idpassword), null, null, null);
                    return;
                }
            }
        }

        if (mInvalidChecker.isInvalidEmail(getString(R.string.info), getString(R.string.invalid_email), mEmailView.content.toString2())) {
            mPopup.dismissWiat();
            return;
        }

        if (mPinData != null && mPinData.length() > 0 && mPinData.length() < 4) {
            mPopup.dismissWiat();
            mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.pincount), null, null, null);
            return;
        }

        UpdateClone();
        mPopup.showWait(mCancelStayListener);
        if (mIsNewUser) {
            request(mUserDataProvider.createUser(mUserInfo, mCreateUserListener));
        } else {
            request(mUserDataProvider.modifyUser(mUserInfo, mModifyUserListener));
        }
    }

    private void UpdateClone() {
        mUserInfo.user_id = mUserIDView.content.toString2();
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

        BaseUserGroup userGroup = mUserInfo.user_group;
        if (userGroup == null) {
            userGroup = new BaseUserGroup((String) mUserGroupView.getTag(), mUserGroupView.content.toString2());
        } else {
            userGroup.name = mUserGroupView.content.toString2();
            userGroup.id = (String) mUserGroupView.content.getTag();
        }
        mUserInfo.user_group = userGroup;
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

        mUserInfo.setTimeFormmat(mDateTimeDataProvider, User.UserTimeType.start_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE, mDateStartView.toString2());
        mUserInfo.setTimeFormmat(mDateTimeDataProvider, User.UserTimeType.expiry_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE, mDateEndView.toString2());

        switch (mPhotoStatus) {
            case NOT_MODIFY:
                if (mUserInfo.photo != null) {
                    mBackupPhoto = mUserInfo.photo;
                    mUserInfo.photo_exist = true;
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
        if (mUserInfo.photo != null && !mUserInfo.photo.isEmpty()) {
            mUserInfo.photo_exist = true;
        }
    }

    private void createUser() {
        if (mUserInfo == null) {
            mUserInfo = new User();
            mUserInfo.setDefaultValue();
            mUserInfo.access_groups = new ArrayList<ListAccessGroup>();
            mIsNewUser = true;
            Calendar cal = Calendar.getInstance();
            mStartYear = 2001;
            mStartMonth = 0;
            mStartDay = 1;
            mEndYear = 2030;
            mEndMonth = 11;
            mEndDay = 31;
            cal.set(Calendar.YEAR, mStartYear);
            cal.set(Calendar.MONTH, mStartMonth);
            cal.set(Calendar.DAY_OF_MONTH, mStartDay);
            mUserInfo.setTimeCalendar(mDateTimeDataProvider, User.UserTimeType.start_datetime, cal);

            cal.set(Calendar.YEAR, mEndYear);
            cal.set(Calendar.MONTH, mEndMonth);
            cal.set(Calendar.DAY_OF_MONTH, mEndDay);

            mUserInfo.setTimeCalendar(mDateTimeDataProvider, User.UserTimeType.expiry_datetime, cal);
            if (mInitUserGroup != null) {
                mUserInfo.user_group = mInitUserGroup;
            }
        } else {

            Calendar cal = mUserInfo.getTimeCalendar(mDateTimeDataProvider, User.UserTimeType.expiry_datetime);
            if (cal == null) {
                cal = Calendar.getInstance();
            }
            int year = cal.get(Calendar.YEAR);
            if (year > 2030) {
                cal.set(Calendar.YEAR, 2030);
                mUserInfo.setTimeCalendar(mDateTimeDataProvider, User.UserTimeType.expiry_datetime, cal);
            }

            mEndYear = cal.get(Calendar.YEAR);
            mEndMonth = cal.get(Calendar.MONTH);
            mEndDay = cal.get(Calendar.DAY_OF_MONTH);

            cal = mUserInfo.getTimeCalendar(mDateTimeDataProvider, User.UserTimeType.start_datetime);
            if (cal == null) {
                cal = Calendar.getInstance();
            }
            year = cal.get(Calendar.YEAR);
            if (year < 2000) {
                cal.set(Calendar.YEAR, 2000);
                mUserInfo.setTimeCalendar(mDateTimeDataProvider, User.UserTimeType.start_datetime, cal);
            }

            mStartYear = cal.get(Calendar.YEAR);
            mStartMonth = cal.get(Calendar.MONTH);
            mStartDay = cal.get(Calendar.DAY_OF_MONTH);
            if (mUserInfo.access_groups == null) {
                mUserInfo.access_groups = new ArrayList<ListAccessGroup>();
            }
        }
    }

    private void editAccessGroup() {
        Bundle bundle = new Bundle();
        try {
            bundle.putSerializable(User.TAG, mUserInfo.clone());
            bundle.putSerializable(Setting.DISABLE_MODIFY, false);
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
            bundle.putSerializable(Setting.DISABLE_MODIFY, false);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return;
        }
        mScreenControl.addScreen(ScreenType.CARD, bundle);
    }

    private void editFace() {
        if (VersionData.getCloudVersion(mActivity) > 1) {
            Bundle bundle = new Bundle();
            try {
                bundle.putSerializable(User.TAG, mUserInfo.clone());
                bundle.putSerializable(Setting.DISABLE_MODIFY, false);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return;
            }
            mScreenControl.addScreen(ScreenType.FACE, bundle);
        }
    }

    private void editFingerPrint() {
        Bundle bundle = new Bundle();
        try {
            bundle.putSerializable(User.TAG, mUserInfo.clone());
            bundle.putSerializable(Setting.DISABLE_MODIFY, false);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return;
        }
        mScreenControl.addScreen(ScreenType.FINGERPRINT_REGISTER, bundle);
    }

    private void editOperator() {
//        Bundle bundle = new Bundle();
//        try {
//            bundle.putSerializable(User.TAG, mUserInfo.clone());
//            bundle.putSerializable(Setting.DISABLE_MODIFY, false);
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//            return;
//        }
//        mScreenControl.addScreen(ScreenType.USER_PERMISSION, bundle);
        if (VersionData.getCloudVersion(mActivity) > 1) {
            //TODO
            SelectPopup<UserPermission> selectCloudRolePopup = new SelectPopup<UserPermission>(mActivity, mPopup);
            selectCloudRolePopup.show(SelectPopup.SelectType.V2_CLOUD_ROLE, new SelectPopup.OnSelectResultListener<UserPermission>() {
                @Override
                public void OnResult(ArrayList<UserPermission> selectedItem, boolean isPositive) {
                    if (isInValidCheck()) {
                        return;
                    }
                    if (selectedItem == null || selectedItem.size() < 1) {
                        return;
                    }
                    UserPermission item = null;
                    try {
                        item = selectedItem.get(0).clone();
                    } catch (Exception e) {

                    }
                    if (item.id.equals(Setting.NONE_ITEM)) {
                        mUserInfo.permission = null;
                    } else {
                        mUserInfo.permission = item;
                    }
                    setPermission();
                }
            }, null, getString(R.string.select) + " " + getString(R.string.operator), false);
        } else {
            SelectPopup<CloudRole> selectCloudRolePopup = new SelectPopup<CloudRole>(mActivity, mPopup);
            selectCloudRolePopup.show(SelectPopup.SelectType.CLOUD_ROLE, new SelectPopup.OnSelectResultListener<CloudRole>() {
                @Override
                public void OnResult(ArrayList<CloudRole> selectedItem, boolean isPositive) {
                    if (isInValidCheck()) {
                        return;
                    }
                    if (selectedItem == null || selectedItem.size() < 1) {
                        return;
                    }
                    if (selectedItem.get(0).code.equals(Setting.NONE_ITEM)) {
                        selectedItem = new ArrayList<CloudRole>();
                    }
                    if (mUserInfo != null) {
                        mUserInfo.roles = (ArrayList<CloudRole>) selectedItem.clone();
                    }
                    setPermission();
                }
            }, null, getString(R.string.select) + " " + getString(R.string.operator), false);
        }

    }

    private void editUserGroup() {
        SelectPopup<UserGroup> selectPopup = new SelectPopup<UserGroup>(mActivity, mPopup);
        selectPopup.show(SelectPopup.SelectType.USER_GROUPS, new SelectPopup.OnSelectResultListener<UserGroup>() {
            @Override
            public void OnResult(ArrayList<UserGroup> selectedItem, boolean isPositive) {
                if (isInValidCheck()) {
                    return;
                }
                if (selectedItem == null) {
                    return;
                }
                UserGroup userGroup = selectedItem.get(0);
                mUserGroupView.content.setText(userGroup.name);
                mUserGroupView.content.setTag(userGroup.id);
            }
        }, null, getString(R.string.select_user_group), false, true);
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
                        if (mBmpBlur != null) {
                            mBmpBlur.recycle();
                            mBmpBlur = null;
                        }
                        if (mBmpRound != null) {
                            mBmpRound.recycle();
                            mBmpRound = null;
                        }
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

    private void initValue(Bundle savedInstanceState) {
        if (mUserInfo == null) {
            mUserInfo = getExtraData(User.TAG, savedInstanceState);
            if (mUserInfo != null) {
                mUserInfo.backup();
            }
        }
        if (mTextInputFilter == null) {
            mTextInputFilter = new TextInputFilter(mImm, mToastPopup);
        }
        if (mInitUserGroup == null) {
            mInitUserGroup = getExtraData(UserGroup.TAG, savedInstanceState);
        }
        if (savedInstanceState != null) {
            int photoMode = savedInstanceState.getInt("photoStatus");
            mPhotoStatus = PhotoStatus.values()[photoMode];
            mIsNewUser = savedInstanceState.getBoolean("newUser");
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "mPhotoStatus restore:" + mPhotoStatus + " mIsNewUser:" + mIsNewUser);
            }
        }
        mDateTimePicker = new DateTimePicker(getActivity());
        mInvalidChecker = new InvalidChecker(mPopup);

        mSummaryUserView = (SummaryUserView) mRootView.findViewById(R.id.summray_user);
        mSummaryUserView.init(mSummaryUserViewListener);
        mUserIDView = (DetailEditItemView) mRootView.findViewById(R.id.user_id);
        mUserNameView = (DetailEditItemView) mRootView.findViewById(R.id.user_name);
        mTextInputFilter.setFilter(mUserNameView.content, TextInputFilter.EDIT_TYPE.USER_NAME);
        mEmailView = (DetailEditItemView) mRootView.findViewById(R.id.email);
//        mTextInputFilter.setFilter( mEmailView.content, TextInputFilter.EDIT_TYPE.EMAIL);
        mTelephoneView = (DetailEditItemView) mRootView.findViewById(R.id.telephone);
        mTextInputFilter.setFilter(mTelephoneView.content, TextInputFilter.EDIT_TYPE.TELEPHONE);
        mOperatorView = (DetailTextItemView) mRootView.findViewById(R.id.operator);
        mLoginIDView = (DetailEditItemView) mRootView.findViewById(R.id.login_id);
        mTextInputFilter.setFilter(mLoginIDView.content, TextInputFilter.EDIT_TYPE.LOGIN_ID);
        mLoginPasswordView = (DetailTextItemView) mRootView.findViewById(R.id.login_password);
        mDateStartView = (StyledTextView) mRootView.findViewById(R.id.date_start);
        mDateEndView = (StyledTextView) mRootView.findViewById(R.id.date_end);
        mUserGroupView = (DetailTextItemView) mRootView.findViewById(R.id.user_group);
        mStatusView = (DetailSwitchItemView) mRootView.findViewById(R.id.status);
        //  mPeriodView = (DetailEditItemView)mRootView.findViewById(R.id.period);
        mAccessGroupView = (DetailTextItemView) mRootView.findViewById(R.id.access_group);
        mFingerPrintView = (DetailTextItemView) mRootView.findViewById(R.id.fingerprint);
        mCardView = (DetailTextItemView) mRootView.findViewById(R.id.card);
        mFaceView = (DetailTextItemView) mRootView.findViewById(R.id.face);
        mPinView = (DetailSwitchItemView) mRootView.findViewById(R.id.pin);

        createUser();
        setView();
        if (mIsNewUser) {
            if (VersionData.getCloudVersion(mActivity) > 1) {
                mCardView.setVisibility(View.GONE);
                mFingerPrintView.setVisibility(View.GONE);
                mFaceView.setVisibility(View.GONE);
            }
        }
    }

    private boolean isExistImageCheck() {
        File cropFile = new File(ImageUtil.getTempFilePath());
        if (cropFile.exists() == false) {
            return false;
        }

        Bitmap bmp = BitmapFactory.decodeFile(ImageUtil.getTempFilePath());
        if (null == bmp) {
            cropFile.delete();
            return false;
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
        setResID(R.layout.fragment_user_modify);
        super.onCreateView(inflater, container, savedInstanceState);
        if (!mIsReUsed) {
            initValue(savedInstanceState);
            String title = mUserInfo.name;
            if (mIsNewUser) {
                title = getString(R.string.new_user);
            }
            initActionbar(title, R.drawable.action_bar_bg);
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
        outState.putBoolean("newUser", mIsNewUser);
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
        if (mTextInputFilter != null && mUserIDView != null) {
            mTextInputFilter.setFilter(mUserIDView.content, TextInputFilter.EDIT_TYPE.NONE);
        }
        super.onDestroy();
        if (mBmpBlur != null) {
            mBmpBlur.recycle();
            mBmpBlur = null;
        }
        if (mBmpRound != null) {
            mBmpRound.recycle();
            mBmpRound = null;
        }
    }

    private void resetUserIDFilter() {
        if (mIsNewUser) {
            mUserIDView.enableEdit(true);
            if (VersionData.getCloudVersion(mActivity) > 1) {
                if (mCommonDataProvider.isAlphaNumericUserID()) {
                    mUserIDView.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    mUserIDView.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            } else {
                mUserIDView.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            mTextInputFilter.setFilter(mUserIDView.content, TextInputFilter.EDIT_TYPE.USER_ID);
            mUserIDView.setOnClickListener(mClickListener);
        } else {
            mUserIDView.enableEdit(false);
            mUserIDView.setOnClickListener(null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_save:
                if (VersionData.getCloudVersion(mActivity) > 1) {
                    mPopup.showWait(mCancelStayListener);
                    request(mCommonDataProvider.getBioStarSetting(mSaveListener));
                } else {
                    save();
                }
                return true;
            default:
                break;
        }

        return false;
    }

    private void setProfileImage() {
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
                        User user = getExtraData(Setting.BROADCAST_UPDATE_FINGER, intent);
                        if (user == null || user.fingerprint_templates == null) {
                            return;
                        }
                        if (mUserInfo != null) {
                            mUserInfo.fingerprint_templates = user.fingerprint_templates;
                            mUserInfo.fingerprint_template_count = user.fingerprint_templates.size();
                            mUserInfo.fingerprint_count = user.fingerprint_templates.size();
                        }
                        setFingerCount();
                        return;
                    }

                    if (action.equals(Setting.BROADCAST_UPDATE_CARD)) {
                        User user = getExtraData(Setting.BROADCAST_UPDATE_CARD, intent);
                        if (user == null || user.cards == null) {
                            return;
                        }
                        if (mUserInfo != null) {
                            mUserInfo.cards = user.cards;
                            mUserInfo.card_count = user.cards.size();
                        }
                        setCardCount();
                        return;
                    }

                    if (action.equals(Setting.BROADCAST_UPDATE_FACE)) {
                        User user = getExtraData(Setting.BROADCAST_UPDATE_FACE, intent);
                        if (user == null) {
                            return;
                        }
                        if (mUserInfo != null) {
                            mUserInfo.face_template_count = user.face_template_count;
                        }
                        setFaceCount();

                        if (mUserInfo.last_modify != null && !mUserInfo.last_modify.equals(user.last_modify)) {
                            if (user.photo != null && !user.photo.isEmpty()) {
                                mPhotoStatus = PhotoStatus.MODIFY;
                                mUserInfo.photo = user.photo;
                                mUserInfo.photo_exist = true;
                                mBackupPhoto = user.photo;
                                setProfileImage();
                            }
                        }
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
                        if (mActivity == null) {
                            return;
                        }
                        mDateStartView.setText(mUserInfo.getTimeFormmat(mDateTimeDataProvider, User.UserTimeType.start_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE));
                        mDateEndView.setText(mUserInfo.getTimeFormmat(mDateTimeDataProvider, User.UserTimeType.expiry_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE));
                        return;
                    }


                    if (action.equals(Setting.BROADCAST_UPDATE_PERMISSION)) {
                        ArrayList<CloudRole> permissions = getExtraData(Setting.BROADCAST_UPDATE_PERMISSION, intent);
                        if (permissions == null) {
                            return;
                        }
                        if (mUserInfo != null) {
                            mUserInfo.roles = (ArrayList<CloudRole>) permissions.clone();
                        }
                        setPermission();
                        return;
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_UPDATE_FINGER);
            intentFilter.addAction(Setting.BROADCAST_UPDATE_CARD);
            intentFilter.addAction(Setting.BROADCAST_UPDATE_FACE);
            intentFilter.addAction(Setting.BROADCAST_UPDATE_USER_ACCESS_GROUP);
            intentFilter.addAction(Setting.BROADCAST_PREFRENCE_REFRESH);
            intentFilter.addAction(Setting.BROADCAST_UPDATE_PERMISSION);

            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
    }

    private void selectDatePicker() {
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mActivity, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        linkType.add(new SelectCustomData(mActivity.getString(R.string.start_date), 1, false));
        linkType.add(new SelectCustomData(mActivity.getString(R.string.end_date), 2, false));
        selectPopup.show(SelectPopup.SelectType.CUSTOM, new SelectPopup.OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem, boolean isPositive) {
                if (isInValidCheck()) {
                    return;
                }
                if (selectedItem == null) {
                    return;
                }
                switch (selectedItem.get(0).getIntId()) {
                    case 1: {
                        mDateTimePicker.showDatePicker(mStartDateListener, mStartYear, mStartMonth, mStartDay);
                        break;
                    }
                    case 2: {
                        mDateTimePicker.showDatePicker(mEndDateListener, mEndYear, mEndMonth, mEndDay);
                        break;
                    }
                    default:
                        break;
                }
            }
        }, linkType, mActivity.getString(R.string.select_link), false, false);
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
        mSummaryUserView.setBlurBackGroudDefault();
        mSummaryUserView.setUserPhotoDefault();
        if (mBmpRound != null) {
            mBmpRound.recycle();
            mBmpRound = null;
        }
        if (mBmpBlur != null) {
            mBmpBlur.recycle();
            mBmpBlur = null;
        }
        mBmpBlur = ImageUtil.fastBlur(bmp, 32);
        mSummaryUserView.setBlurBackGroud(mBmpBlur);
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
        mBmpRound = ImageUtil.getRoundedBitmap(bmp, true);
        mSummaryUserView.setUserPhoto(mBmpRound);
        if (bmp2 != null) {
            bmp2.recycle();
            bmp2 = null;
        }
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
        mSummaryUserView.setUserID(mUserInfo.user_id);
        mSummaryUserView.setUserName(mUserInfo.name);
        mSummaryUserView.showPin(mUserInfo.pin_exist);
        mUserIDView.setContentText(mUserInfo.user_id);
        resetUserIDFilter();
        mUserNameView.setContentText(mUserInfo.name);
        mUserNameView.setOnClickListener(mClickListener);
        mEmailView.setContentText(mUserInfo.email);
        mEmailView.setOnClickListener(mClickListener);
        mTelephoneView.setContentText(mUserInfo.phone_number);
        mTelephoneView.setOnClickListener(mClickListener);
        mOperatorView.enableLink(true, mClickListener);
        setPermission();
        mLoginIDView.setContentText(mUserInfo.login_id);
        mLoginIDView.setOnClickListener(mClickListener);
        mLoginPasswordView.enableLink(true, mClickListener);

        mUserGroupView.enableLink(true, mClickListener);
        if (mUserInfo.user_group != null) {
            mUserGroupView.content.setText(mUserInfo.user_group.name);
            mUserGroupView.content.setTag(mUserInfo.user_group.id);
        } else {
            mUserGroupView.content.setText(getString(R.string.all_users));
            mUserGroupView.content.setTag(String.valueOf(1));
        }

        mStatusView.mContent.setVisibility(View.GONE);
        mStatusView.setOnClickListener(mClickListener);
        if (mUserInfo.isActive()) {
            mStatusView.mIndex.setText(getString(R.string.status) + " " + getString(R.string.active));
        } else {
            mStatusView.mIndex.setText(getString(R.string.status) + " " + getString(R.string.inactive));
        }

        mStatusSwitch = mStatusView.mSwitchView;
        mStatusSwitch.init(getActivity(), new SwitchView.OnChangeListener() {
            @Override
            public boolean onChange(boolean on) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "status :" + on);
                }
                if (on) {
                    mUserInfo.setActive(true);
                    mStatusView.mIndex.setText(getString(R.string.status) + " " + getString(R.string.active));
                } else {
                    mUserInfo.setActive(false);
                    mStatusView.mIndex.setText(getString(R.string.status) + " " + getString(R.string.inactive));
                }
                return true;
            }
        }, mUserInfo.isActive());
        mStatusSwitch.setSwitch(mUserInfo.isActive());
        mRootView.findViewById(R.id.date_edit).setOnClickListener(mClickListener);
        mRootView.findViewById(R.id.date_arrow).setOnClickListener(mClickListener);
        mDateStartView.setOnClickListener(mClickListener);
        mDateEndView.setOnClickListener(mClickListener);
        mDateStartView.setText(mUserInfo.getTimeFormmat(mDateTimeDataProvider, User.UserTimeType.start_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE));
        mDateEndView.setText(mUserInfo.getTimeFormmat(mDateTimeDataProvider, User.UserTimeType.expiry_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE));
        mAccessGroupView.enableLink(true, mClickListener);
        mFingerPrintView.enableLink(true, mClickListener);
        mCardView.enableLink(true, mClickListener);
        mFaceView.enableLink(true, mClickListener);
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
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "pin :" + on);
                }
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
        if (mBmpRound != null) {
            mSummaryUserView.setUserPhoto(mBmpRound);
        } else {
            setProfileImage();
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

    private void showUserViewLog() {
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

    private enum PhotoStatus {
        NOT_MODIFY, MODIFY, DELETE
    }

}
