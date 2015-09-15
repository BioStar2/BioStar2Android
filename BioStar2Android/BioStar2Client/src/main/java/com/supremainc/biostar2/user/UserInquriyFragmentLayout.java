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
import android.widget.ImageView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.base.BaseFragment;
import com.supremainc.biostar2.base.BaseFragmentLayout;
import com.supremainc.biostar2.widget.OnSingleClickListener;
import com.supremainc.biostar2.widget.StyledTextView;

public class UserInquriyFragmentLayout extends BaseFragmentLayout {
    private final String TAG = getClass().getSimpleName();
    private UserInquriyFragmentLayoutEvent mLayoutEvent;
    private StyledTextView mName;
    private StyledTextView mUserId;
    private StyledTextView mTitleName;
    private StyledTextView mTitleUserId;
    private StyledTextView mPeroid;
    private View mEmailLink;
    private View mTelephoneLink;
    private ImageView mBlurBackgroundView;
    private StyledTextView mCard;
    private StyledTextView mFingerprint;
    private StyledTextView mAccessGroup;
    private StyledTextView mGroup;
    private ImageView mPhoto;
    private StyledTextView mStatus;

    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            if (mLayoutEvent == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.user_viewlog:
                    mLayoutEvent.showUserViewLog();
                    break;
                case R.id.email_go:
                    mLayoutEvent.showEmail();
                    break;
                case R.id.telephone_go:
                    mLayoutEvent.showTelephone();
                    break;
            }
        }
    };

    public UserInquriyFragmentLayout(BaseFragment fragment, UserInquriyFragmentLayoutEvent layoutEvent) {
        super(fragment);
        mLayoutEvent = layoutEvent;
    }

    public String getCard() {
        return mCard.toString2();
    }

    private View getView(int resID, boolean isClickListener) {
        View view = mRootView.findViewById(resID);
        if (isClickListener) {
            view.setOnClickListener(mClickListener);
        }
        return view;
    }

    public View initView(BaseFragment fragment, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.initView(fragment, inflater, container, savedInstanceState, R.layout.fragment_user_inquiry);
        if (!isReUsedView()) {
            mTitleUserId = (StyledTextView) getView(R.id.title_user_id, false);
            mTitleName = (StyledTextView) getView(R.id.title_user_name, false);
            mUserId = (StyledTextView) getView(R.id.user_id, false);
            mName = (StyledTextView) getView(R.id.name, false);
            mEmailLink = v.findViewById(R.id.email_go);
            mTelephoneLink = v.findViewById(R.id.telephone_go);
            mGroup = (StyledTextView) getView(R.id.group, false);
            mStatus = (StyledTextView) v.findViewById(R.id.status_name);
            mPeroid = (StyledTextView) v.findViewById(R.id.period);
            mAccessGroup = (StyledTextView) getView(R.id.access_group, false);
            mFingerprint = (StyledTextView) getView(R.id.fingerprint, false);
            mCard = (StyledTextView) getView(R.id.card, false);
            mPhoto = (ImageView) getView(R.id.profile_image, true);
            mPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
            mBlurBackgroundView = (ImageView) mRootView.findViewById(R.id.background_img);


            int[] ids = {R.id.user_viewlog};
            for (int i : ids) {
                v.findViewById(i).setOnClickListener(mClickListener);
            }
        }
        return v;
    }

    public void setAccessGroup(String content) {
        setTextView(mAccessGroup, content);
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

    public void setFingerCount(String content) {
        setTextView(mFingerprint, content);
        setTextView(R.id.title_finger_count, content);

    }

    public void setNewUser(boolean isNewUser) {
        if (isNewUser) {
            mRootView.findViewById(R.id.user_id_edit).setOnClickListener(mClickListener);
            mUserId.setFocusable(true);
            mUserId.setEnabled(true);
            mUserId.setTextColor(mFragment.getResources().getColor(R.color.subtext));
            mRootView.findViewById(R.id.user_id_arrow).setVisibility(View.VISIBLE);
            showUserViewLog(false);
        } else {
            mRootView.findViewById(R.id.user_id_edit).setOnClickListener(null);
            mUserId.setFocusable(false);
            mUserId.setEnabled(false);
            mUserId.setTextColor(mFragment.getResources().getColor(R.color.content_text));
            mRootView.findViewById(R.id.user_id_arrow).setVisibility(View.INVISIBLE);
        }
    }

    public void setOperator(boolean isShow, String content) {
        setTextView(R.id.operator, content);
    }

    public void setPeroid(String content) {
        setTextView(mPeroid, content);
    }

    public void setStatus(String content) {
        setTextView(mStatus, content);
    }

    public void setUserGroup(String content) {
        setTextView(mGroup, content);
    }

    public void setUserGroupID(String content) {
        setTag(mGroup, content);
    }

    public void setUserID(String content) {
        setTextView(mUserId, content);
        setTextView(mTitleUserId, content);
    }

    public void setUserName(String content) {
        setTextView(mName, content);
        setTextView(mTitleName, content);
    }

    public void setUserPhoto(Bitmap bmp) {
        mPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
        mPhoto.setImageBitmap(bmp);
    }

    public void setUserPhotoDefault() {
        mPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
        mPhoto.setImageResource(R.drawable.user_photo_bg);
    }

    public void showEmailLink(boolean isVisible, String content) {
        if (isVisible) {
            mEmailLink.setOnClickListener(mClickListener);
            mEmailLink.setBackgroundResource(R.drawable.selector_list_default_mode);
            mRootView.findViewById(R.id.email_link).setVisibility(View.VISIBLE);
            setTextView(R.id.email, content);
        } else {
            mEmailLink.setOnClickListener(null);
            mEmailLink.setBackgroundColor(mFragment.getResources().getColor(R.color.transparent));
            mRootView.findViewById(R.id.email_link).setVisibility(View.INVISIBLE);
            setTextView(R.id.email, content);
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
        mRootView.findViewById(R.id.pin).setVisibility(visible);
    }

    public void showTelephoneLink(boolean isVisible, String content) {
        if (isVisible) {
            mTelephoneLink.setOnClickListener(mClickListener);
            mTelephoneLink.setBackgroundResource(R.drawable.selector_list_default_mode);
            mRootView.findViewById(R.id.telephone_link).setVisibility(View.VISIBLE);
            setTextView(R.id.telephone, content);
        } else {
            mTelephoneLink.setOnClickListener(null);
            mTelephoneLink.setBackgroundColor(mFragment.getResources().getColor(R.color.transparent));
            mRootView.findViewById(R.id.telephone_link).setVisibility(View.INVISIBLE);
            setTextView(R.id.telephone, content);
        }
    }

    public void showUserViewLog(boolean isShow) {
        View view = mRootView.findViewById(R.id.user_viewlog);
        if (isShow) {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(mClickListener);
        } else {
            view.setVisibility(View.INVISIBLE);
            view.setOnClickListener(null);
        }
    }

    public interface UserInquriyFragmentLayoutEvent {
        public void showEmail();
        public void showTelephone();
        public void showUserViewLog();
    }
}