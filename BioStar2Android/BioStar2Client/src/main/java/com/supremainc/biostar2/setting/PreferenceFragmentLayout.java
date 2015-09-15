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
package com.supremainc.biostar2.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.base.BaseFragment;
import com.supremainc.biostar2.base.BaseFragmentLayout;
import com.supremainc.biostar2.widget.OnSingleClickListener;
import com.supremainc.biostar2.widget.StyledTextView;
import com.supremainc.biostar2.widget.SwitchView;

import java.util.ArrayList;

public class PreferenceFragmentLayout extends BaseFragmentLayout {
    private final String TAG = getClass().getSimpleName();
    private PreferenceFragmentLayoutEvent mLayoutEvent;
    private StyledTextView mDateFormat;
    private LinearLayout mNotification;
    private ArrayList<SwitchView> mSwitchViewList;
    private StyledTextView mTimeFormat;
    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            if (mLayoutEvent == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.go_version_mobile:
                    mLayoutEvent.onClickUpdate();
                    break;
                case R.id.go_date:
                    mLayoutEvent.onClickDate();
                    break;
                case R.id.go_time:
                    mLayoutEvent.onClickTime();
                    break;
            }
        }
    };

    public PreferenceFragmentLayout(BaseFragment fragment, PreferenceFragmentLayoutEvent layoutEvent) {
        super(fragment);
        mLayoutEvent = layoutEvent;
    }

    public void addSwitchView(String description, boolean subscribed, String type) {
        if (mSwitchViewList == null) {
            mSwitchViewList = new ArrayList<SwitchView>();
        }
        LinearLayout mainLayout = (LinearLayout) mLayoutInflater.inflate(R.layout.view_push_switch, null);
        StyledTextView descrptionView = (StyledTextView) mainLayout.findViewById(R.id.descrption);
        setTextView(descrptionView, description);
        SwitchView switchView = (SwitchView) mainLayout.findViewById(R.id.push_switch);
        switchView.init(mActivity, null, false);
        switchView.setSwitch(subscribed);
        switchView.setTag(type);
        mSwitchViewList.add(switchView);
        mNotification.addView(mainLayout);
    }

    public String getDateFormat() {
        return (String) getTag(mDateFormat);
    }

    public void setDateFormat(String content) {
        setTag(mDateFormat, content);
    }

    public ArrayList<SwitchView> getSwitchViewList() {
        return mSwitchViewList;
    }

    public String getTimeFormat() {
        return (String) getTag(mTimeFormat);
    }

    public void setTimeFormat(String content) {
        setTag(mTimeFormat, content);
    }

    public View initView(BaseFragment fragment, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.initView(fragment, inflater, container, savedInstanceState, R.layout.fragment_prefernce);
        mNotification = (LinearLayout) v.findViewById(R.id.notification);
        mDateFormat = (StyledTextView) v.findViewById(R.id.dateformat);
        mTimeFormat = (StyledTextView) v.findViewById(R.id.timeformat);
        int[] ids = {R.id.go_version_mobile, R.id.go_date, R.id.go_time};
        for (int i : ids) {
            v.findViewById(i).setOnClickListener(mClickListener);
        }
        return v;
    }

    public void setAppVersion(String content) {
        setTextView(R.id.version_mobile, content);
    }

    public void setDateDevider(String content) {
        setTextView(R.id.devider_date, content);
    }

    public void setDateFormatName(String content) {
        setTextView(mDateFormat, content);
    }

    public void setNewVersion() {
        mRootView.findViewById(R.id.version_mobile_new).setVisibility(View.VISIBLE);
    }

    public void setTimeFormatName(String content) {
        setTextView(mTimeFormat, content);
    }

    public void setTimezone(String content) {
        setTextView(R.id.timezone, content);
    }

    public void showNotification(boolean isVisible) {
        int visible;
        if (isVisible) {
            visible = View.VISIBLE;
        } else {
            visible = View.GONE;
        }
        mNotification.setVisibility(visible);
    }

    public interface PreferenceFragmentLayoutEvent {
        public void onClickDate();

        public void onClickTime();

        public void onClickUpdate();
    }
}