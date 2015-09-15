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
package com.supremainc.biostar2.alarm;

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

public class AlarmFragmentLayout extends BaseFragmentLayout {
    private final String TAG = getClass().getSimpleName();
    private AlarmFragmentLayoutEvent mLayoutEvent;
    private StyledTextView mActionButton;
    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            if (mLayoutEvent == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.door_control:
                    mLayoutEvent.onClickDoorAction();
                    break;
                case R.id.go_user:
                    mLayoutEvent.onClickUser();
                    break;
                case R.id.go_telephone:
                    mLayoutEvent.onClickTelephone();
                    break;
                case R.id.go_view:
                    mLayoutEvent.onClickLog();
                    break;
            }
        }
    };

    public AlarmFragmentLayout(BaseFragment fragment, AlarmFragmentLayoutEvent layoutEvent) {
        super(fragment);
        mLayoutEvent = layoutEvent;
    }

    public View initView(BaseFragment fragment, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.initView(fragment, inflater, container, savedInstanceState, R.layout.fragment_alarm);
        mActionButton = ((StyledTextView) v.findViewById(R.id.door_control));
        mActionButton.setOnClickListener(mClickListener);

        int[] ids = {R.id.go_view,R.id.go_telephone,R.id.go_user};
        for (int i : ids) {
            v.findViewById(i).setOnClickListener(mClickListener);
        }
        return v;
    }

    public void setActionButtonName(String name) {
        setTextView(mActionButton, name);
    }

    public void setDefault(String title) {
        setTitle(title);
        (mRootView.findViewById(R.id.door_open_request)).setVisibility(View.GONE);
        (mRootView.findViewById(R.id.door_control)).setVisibility(View.GONE);
        (mRootView.findViewById(R.id.go_view)).setVisibility(View.GONE);
    }

    public void setDevice() {
        (mRootView.findViewById(R.id.door_open_request)).setVisibility(View.GONE);
        (mRootView.findViewById(R.id.door_control)).setVisibility(View.GONE);
    }

    public void setDisableActionBtn() {
        mActionButton.setOnClickListener(null);
        mActionButton.setBackgroundResource(R.drawable.selector_list_gray);
        mRootView.findViewById(R.id.go_view).setOnClickListener(null);
        mRootView.findViewById(R.id.go_view).setVisibility(View.GONE);
    }

    public void setIcon(int resID) {
        ((ImageView) mRootView.findViewById(R.id.icon)).setImageResource(resID);
    }

    private void setLink(boolean isLink, View Container, View link) {
        if (isLink) {
            link.setVisibility(View.VISIBLE);
            Container.setBackgroundResource(R.drawable.selector_list_default_mode);
            Container.setOnClickListener(mClickListener);
        } else {
            link.setVisibility(View.GONE);
            Container.setOnClickListener(null);
        }
    }

    public void setNotificationTime(String time) {
        setTextView(R.id.notification_time, time);
    }

    public void setPhoneNumber(boolean isVisible, boolean isLink, String number) {
        View telephoneContainer = mRootView.findViewById(R.id.go_telephone);
        View telephoneLink = mRootView.findViewById(R.id.telephone_link);
        View devider = mRootView.findViewById(R.id.go_telephone_devider);

        if (isVisible) {
            telephoneContainer.setVisibility(View.VISIBLE);
            devider.setVisibility(View.VISIBLE);
            setLink(isLink, telephoneContainer, telephoneLink);
            if (number != null) {
                setTextView(R.id.telephone, number);
            }
        } else {
            telephoneContainer.setVisibility(View.GONE);
            devider.setVisibility(View.GONE);
        }
    }

    public void setPushMessage(String message) {
        setTextView(R.id.title_content, message);
    }

    public void setTitle(String title) {
        setTextView(R.id.title_name, title);
    }

    public void setUser(boolean isVisible, boolean isLink, String name) {
        View userContianer = mRootView.findViewById(R.id.go_user);
        View userLink = mRootView.findViewById(R.id.user_link);
        View devider = mRootView.findViewById(R.id.go_user_devider);
        if (isVisible) {
            userContianer.setVisibility(View.VISIBLE);
            devider.setVisibility(View.VISIBLE);
            setLink(isLink, userContianer, userLink);
            if (name != null) {
                setTextView(R.id.user, name);
            }
        } else {
            userContianer.setVisibility(View.GONE);
            devider.setVisibility(View.GONE);
        }
    }

    public interface AlarmFragmentLayoutEvent {
        public void onClickDoorAction();
        public void onClickLog();
        public void onClickTelephone();
        public void onClickUser();
    }
}