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
package com.supremainc.biostar2.door;

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

public class DoorFragmentLayout extends BaseFragmentLayout {
    private final String TAG = getClass().getSimpleName();
    private DoorFragmentLayoutEvent mLayoutEvent;
    private StyledTextView mActionButton;
    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            if (mLayoutEvent == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.go_view:
                    mLayoutEvent.onClickLog();
                    break;
                case R.id.door_control:
                    mLayoutEvent.onClickDoorAction();
                    break;
                case R.id.icon:
                    mLayoutEvent.onClickDoorIcon();
                    break;
            }
        }
    };

    public DoorFragmentLayout(BaseFragment fragment, DoorFragmentLayoutEvent layoutEvent) {
        super(fragment);
        mLayoutEvent = layoutEvent;
    }

    public View initView(BaseFragment fragment, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.initView(fragment, inflater, container, savedInstanceState, R.layout.fragment_door);
        mActionButton = ((StyledTextView) v.findViewById(R.id.door_control));
        mActionButton.setOnClickListener(mClickListener);

        int[] ids = {R.id.go_view};
        for (int i : ids) {
            v.findViewById(i).setOnClickListener(mClickListener);
        }
        return v;
    }

    public void setActionButtonName(String name) {
        setTextView(mActionButton, name);
    }

    public void setContent(String title, String content) {
        setTextView(R.id.title_name, title);
        setTextView(R.id.title_content, content);
    }

    public void setDoorRelayName(String name) {
        setTextView(R.id.door_relay, name);
    }

    public void setDoorSensorName(String name) {
        setTextView(R.id.door_sensor, name);
    }

    public void setEntryDeviceName(String name) {
        setTextView(R.id.entry_device, name);
    }

    public void setExitBtnName(String name) {
        setTextView(R.id.exit_button, name);
    }

    public void setExitDeviceName(String name) {
        setTextView(R.id.exit_device, name);
    }

    public void setIcon(int resID) {
        ImageView v = (ImageView) mRootView.findViewById(R.id.icon);
        v.setImageResource(resID);
        v.setOnClickListener(mClickListener);
    }

    public void setOpenDuration(String name) {
        setTextView(R.id.open_duration, name);
    }

    public interface DoorFragmentLayoutEvent {
        public void onClickDoorAction();

        public void onClickDoorIcon();

        public void onClickLog();
    }
}