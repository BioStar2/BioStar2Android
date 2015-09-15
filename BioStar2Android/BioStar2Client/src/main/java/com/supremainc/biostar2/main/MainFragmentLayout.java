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
package com.supremainc.biostar2.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.base.BaseFragment;
import com.supremainc.biostar2.base.BaseFragmentLayout;
import com.supremainc.biostar2.db.NotificationDBProvider;
import com.supremainc.biostar2.widget.OnSingleClickListener;
import com.supremainc.biostar2.widget.StyledTextView;

public class MainFragmentLayout extends BaseFragmentLayout {
    private final String TAG = getClass().getSimpleName();
    private MainFragmentLayoutEvent mLayoutEvent;
    // View
    private StyledTextView mMeridiem;
    private StyledTextView mDay;
    private StyledTextView mTime;
    private StyledTextView mBadge;

    // Listener
    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            onProcessViewClick(v);
        }
    };

    public MainFragmentLayout(BaseFragment fragment, MainFragmentLayoutEvent layoutEvent) {
        super(fragment);
        mLayoutEvent = layoutEvent;
    }

    public View initView(BaseFragment fragment, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.initView(fragment, inflater, container, savedInstanceState, R.layout.fragment_main);
        if (!isReUsedView()) {
            mMeridiem = (StyledTextView) v.findViewById(R.id.display_marker);
            mDay = (StyledTextView) v.findViewById(R.id.display_day);
            mTime = (StyledTextView) v.findViewById(R.id.display_time);
            mBadge = (StyledTextView) v.findViewById(R.id.main_alarm_badge);
            setAlarmCount();
            int[] ids = {R.id.main_user, R.id.main_door, R.id.main_monitor, R.id.main_alarm, R.id.main_menu, R.id.setting};
            for (int i : ids) {
                v.findViewById(i).setOnClickListener(mClickListener);
            }
        }
        return v;
    }

    private void onProcessViewClick(View v) {
        if (mLayoutEvent == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.main_user:
                mLayoutEvent.onClickUser();
                break;
            case R.id.main_door:
                mLayoutEvent.onClickDoor();
                break;
            case R.id.main_monitor:
                mLayoutEvent.onClickMonitor();
                break;
            case R.id.main_alarm:
                mLayoutEvent.onClickAlarm();
                break;
            case R.id.main_menu:
                mLayoutEvent.onClickMenu();
                break;
            case R.id.setting:
                mLayoutEvent.onClickSetting();
                break;
        }
    }

    public void setDateTimeWidget(String meridiem, String date, String time) {
        mMeridiem.setText(meridiem);
        mDay.setText(date);
        mTime.setText(time);
    }

    public void showAlarmMenu(boolean visible) {
        if (visible) {
            mRootView.findViewById(R.id.main_alarm).setVisibility(View.VISIBLE);
        } else {
            mRootView.findViewById(R.id.main_alarm).setVisibility(View.GONE);
        }
    }

    public void showDoorMenu(boolean visible) {
        if (visible) {
            mRootView.findViewById(R.id.main_door).setVisibility(View.VISIBLE);
        } else {
            mRootView.findViewById(R.id.main_door).setVisibility(View.GONE);
        }
    }

    public void showMonitorMenu(boolean visible) {
        if (visible) {
            mRootView.findViewById(R.id.main_monitor).setVisibility(View.VISIBLE);
        } else {
            mRootView.findViewById(R.id.main_monitor).setVisibility(View.GONE);
        }
    }

    public void showUserMenu(boolean visible) {
        if (visible) {
            mRootView.findViewById(R.id.main_user).setVisibility(View.VISIBLE);
        } else {
            mRootView.findViewById(R.id.main_user).setVisibility(View.GONE);
        }
    }

    public void setAlarmCount() {
        int count = NotificationDBProvider.getInstance(mContext).getUnReadMessageCount();
        if (count < 1) {
            mBadge.setText("0");
            mBadge.setVisibility(View.GONE);
            return;
        }
        String content;
        mBadge.setVisibility(View.VISIBLE);
        if (count > 1000) {
            content = "999+";
        } else {
            content = String.valueOf(count);
        }
        mBadge.setText(content);
    }

    public interface MainFragmentLayoutEvent {
        public void onClickAlarm();
        public void onClickDoor();
        public void onClickMenu();
        public void onClickMonitor();
        public void onClickUser();
        public void onClickSetting();
    }
}