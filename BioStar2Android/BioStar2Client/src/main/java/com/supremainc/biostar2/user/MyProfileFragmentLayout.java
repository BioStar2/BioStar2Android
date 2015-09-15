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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.base.BaseFragment;
import com.supremainc.biostar2.base.BaseFragmentLayout;
import com.supremainc.biostar2.util.TextWatcherFilter;
import com.supremainc.biostar2.widget.OnSingleClickListener;
import com.supremainc.biostar2.widget.StyledEditTextView;
import com.supremainc.biostar2.widget.StyledTextView;
import com.supremainc.biostar2.widget.SwitchView;

public class MyProfileFragmentLayout extends BaseFragmentLayout {
    private final String TAG = getClass().getSimpleName();
    private MyProfileFragmentLayoutEvent mLayoutEvent;
    private ImageView mBlurBackgroundView;
    private StyledTextView mTitleName;
    private StyledTextView mTitleUserId;
    private StyledTextView mCard;
    private StyledTextView mDateEnd;
    private StyledTextView mDateStart;
    private StyledTextView mAccessGroup;
    private StyledEditTextView mEmail;
    private StyledTextView mFingerprint;
    private StyledTextView mGroup;
    private StyledEditTextView mLoginId;
    private StyledEditTextView mName;
    private StyledTextView mOperator;
    private View mOperatorExpand;
    private StyledTextView mPassword;
    private ImageView mPhoto;
    private StyledTextView mPin;
    private StyledTextView mStatus;
    private SwitchView mStatusSwitch;
    private StyledEditTextView mTelephone;
    private StyledEditTextView mUserId;
    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            if (mLayoutEvent == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.name_edit:
                    mLayoutEvent.showIME(mName);
                    mName.setSelection(mName.toString2().length());
                    break;
                case R.id.user_id_edit:
                    mLayoutEvent.showIME(mUserId);
                    mUserId.setSelection(mUserId.toString2().length());
                    break;
                case R.id.email_edit:
                    mLayoutEvent.showIME(mEmail);
                    mEmail.setSelection(mEmail.toString2().length());
                    break;
                case R.id.telephone_edit:
                    mLayoutEvent.showIME(mTelephone);
                    mTelephone.setSelection(mTelephone.toString2().length());
                    break;
                case R.id.login_id_edit:
                    mLayoutEvent.showIME(mLoginId);
                    mLoginId.setSelection(mLoginId.toString2().length());
                    break;
                case R.id.password_edit:
                case R.id.password:
                    mLayoutEvent.showPasswodPopup();
                    break;
                case R.id.pin_edit:
                case R.id.pin:
                    mLayoutEvent.showPinPasswodPopup();
                    break;
                case R.id.fingerprint_edit:
                    mLayoutEvent.editFingerPrint();
                    break;
                case R.id.card_edit:
                    mLayoutEvent.editCard();
                    break;
                case R.id.profile_image:
                    mLayoutEvent.editUserImage();
                    break;
                case R.id.access_group_edit:
                    mLayoutEvent.editAccessGroup();
                    break;
                case R.id.operator_edit:
                    mLayoutEvent.editOperator();
                    break;

            }
        }
    };

    public MyProfileFragmentLayout(BaseFragment fragment, MyProfileFragmentLayoutEvent layoutEvent) {
        super(fragment);
        mLayoutEvent = layoutEvent;
    }

    public String getAccessGroup() {
        return mAccessGroup.toString2();
    }

    public void setAccessGroup(String content) {
        setTextView(mAccessGroup, content);
    }

    public String getCard() {
        return mCard.toString2();
    }

    public String getEmail() {
        return mEmail.toString2();
    }

    public void setEmail(String content) {
        setEditTextView(mEmail, content);
    }

    public SwitchView getPinSwitchView() {
        return (SwitchView) mRootView.findViewById(R.id.pin_switch);
    }

    public String getTelephone() {
        return mTelephone.toString2();
    }

    public void setTelephone(String content) {
        setEditTextView(mTelephone, content);
    }

    public String getUserGroup() {
        return mGroup.toString2();
    }

    public void setUserGroup(String content) {
        setTextView(mGroup, content);
    }

    public String getUserGroupID() {
        return (String) getTag(mGroup);
    }

    public void setUserGroupID(String content) {
        setTag(mGroup, content);
    }

    public String getUserID() {
        return mUserId.toString2();
    }

    public void setUserID(String content) {
        setEditTextView(mUserId, content);
        setTextView(mTitleUserId, content);
    }

    public StyledEditTextView getUserIDView() {
        return mUserId;
    }

    public String getUserLoginID() {
        return mLoginId.toString2();
    }

    public String getUserName() {
        return mName.toString2();
    }

    public void setUserName(String content) {
        setTextView(mTitleName, content);
        setEditTextView(mName, content);
    }

    private View getView(int resID, boolean isClickListener) {
        View view = mRootView.findViewById(resID);
        if (isClickListener) {
            view.setOnClickListener(mClickListener);
        }
        return view;
    }

    public View initView(BaseFragment fragment, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.initView(fragment, inflater, container, savedInstanceState, R.layout.fragment_user_modify);
        if (!isReUsedView()) {
            mPhoto = (ImageView) getView(R.id.profile_image, true);
            mUserId = (StyledEditTextView) getView(R.id.user_id, false);
            mUserId.addTextChangedListener(new TextWatcherFilter(mUserId, TextWatcherFilter.EDIT_TYPE.USER_ID, mActivity, 10));
            mUserId.setFocusable(false);
            mUserId.setEnabled(false);
            mUserId.setTextColor(mFragment.getResources().getColor(R.color.content_text));
            mTitleUserId = (StyledTextView) getView(R.id.title_user_id, false);
            mTitleName = (StyledTextView) getView(R.id.title_user_name, false);
            v.findViewById(R.id.user_id_arrow).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.user_viewlog).setVisibility(View.INVISIBLE);
            mName = (StyledEditTextView) getView(R.id.name, false);
            mName.addTextChangedListener(new TextWatcherFilter(mName, TextWatcherFilter.EDIT_TYPE.USER_NAME, mActivity, 48));
            mEmail = (StyledEditTextView) getView(R.id.email, false);
            mEmail.addTextChangedListener(new TextWatcherFilter(mEmail, TextWatcherFilter.EDIT_TYPE.EMAIL, mActivity, 128));
            // mEmail.setFilters(new InputFilter[]{mUtilProvider.mFilterEmail});
            mTelephone = (StyledEditTextView) getView(R.id.telephone, false);
            mTelephone.addTextChangedListener(new TextWatcherFilter(mTelephone, TextWatcherFilter.EDIT_TYPE.TELEPHONE, mActivity, 128));
            mOperator = (StyledTextView) getView(R.id.operator, false);
            mOperatorExpand = getView(R.id.operator_expand, false);
            mLoginId = (StyledEditTextView) getView(R.id.login_id, false);
            mLoginId.addTextChangedListener(new TextWatcherFilter(mLoginId, TextWatcherFilter.EDIT_TYPE.LOGIN_ID, mActivity, 32));
            mPassword = (StyledTextView) getView(R.id.password, false);
            mGroup = (StyledTextView) getView(R.id.group, false);
            mGroup.setTextColor(mFragment.getResources().getColor(R.color.content_text));

            mStatusSwitch = (SwitchView) v.findViewById(R.id.status_switch);
            mStatusSwitch.setVisibility(View.INVISIBLE);
            mStatus = (StyledTextView) v.findViewById(R.id.status_name);
            v.findViewById(R.id.group_arrow).setVisibility(View.INVISIBLE);
            mDateStart = (StyledTextView) v.findViewById(R.id.date_start);
            mDateStart.setTextColor(mFragment.getResources().getColor(R.color.content_text));
            mDateEnd = (StyledTextView) v.findViewById(R.id.date_end);
            mDateEnd.setTextColor(mFragment.getResources().getColor(R.color.content_text));
            v.findViewById(R.id.date_arrow).setVisibility(View.INVISIBLE);
            mAccessGroup = (StyledTextView) getView(R.id.access_group, false);
            mFingerprint = (StyledTextView) getView(R.id.fingerprint, false);
            mCard = (StyledTextView) getView(R.id.card, false);
            mPin = (StyledTextView) getView(R.id.pin, true);
            mPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
            mBlurBackgroundView = (ImageView) mRootView.findViewById(R.id.background_img);

            int[] ids = {R.id.name_edit, R.id.email_edit, R.id.telephone_edit, R.id.operator_edit, R.id.login_id_edit, R.id.password_edit, R.id.password, R.id.status, R.id.access_group_edit,
                    R.id.fingerprint_edit, R.id.card_edit, R.id.pin_edit};
            for (int i : ids) {
                v.findViewById(i).setOnClickListener(mClickListener);
            }
        }
        return v;
    }

    public boolean isOperator() {
        if (mOperatorExpand.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    public void setBlurBackGroud(Bitmap blur) {
        mBlurBackgroundView.setImageBitmap(blur);
    }

    public void setBlurBackGroudDefault() {
        mBlurBackgroundView.setImageResource(R.drawable.background1);
    }

    public void setCardCount(String content) {
        setTextView(mCard, content);
        setTextView(R.id.title_card_count, content);
    }

    public void setDateEnd(String content) {
        setTextView(mDateEnd, content);
    }

    public void setDateStart(String content) {
        setTextView(mDateStart, content);
    }

    public void setFingerCount(String content) {
        setTextView(mFingerprint, content);
        setTextView(R.id.title_finger_count, content);
    }

    public void setLoginID(String content) {
        setEditTextView(mLoginId, content);
    }

    public void setPassword(String content) {
        setTextView(mPassword, content);
    }

    public void setPin(String content) {
        setTextView(mPin, content);
    }

    public void setStatus(String content) {
        setTextView(mStatus, content);
    }

    public void setStatusSwitch(boolean isActive) {
        mStatusSwitch.setSwitch(isActive);
    }

    public void setUserPhoto(Bitmap bmp) {
        mPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
        mPhoto.setImageBitmap(bmp);
    }

    public void setUserPhotoDefault() {
        mPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mPhoto.setImageResource(R.drawable.selector_btn_camera);
    }

    public void showOperator(boolean isShow, String content) {
        mOperator.setText(content);
        if (isShow) {
            mOperatorExpand.setVisibility(View.VISIBLE);
        } else {
            mOperatorExpand.setVisibility(View.GONE);
        }
    }

    public void showPin(boolean isVisible) {
        int visible;
        if (isVisible) {
            visible = View.VISIBLE;
        } else {
            visible = View.GONE;
        }
        mRootView.findViewById(R.id.title_pin_exist).setVisibility(visible);
        mRootView.findViewById(R.id.title_pin_exist_devider).setVisibility(visible);
    }

    public interface MyProfileFragmentLayoutEvent {
        public void editAccessGroup();
        public void editCard();
        public void editFingerPrint();
        public void editOperator();
        public void editUserImage();
        public void showIME(EditText view);
        public void showPasswodPopup();
        public void showPinPasswodPopup();
    }
}