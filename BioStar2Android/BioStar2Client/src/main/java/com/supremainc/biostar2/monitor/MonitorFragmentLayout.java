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
package com.supremainc.biostar2.monitor;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.base.BaseFragment;
import com.supremainc.biostar2.base.BaseFragmentLayout;
import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.view.SubToolbar;
import com.supremainc.biostar2.widget.FilterView;

public class MonitorFragmentLayout extends BaseFragmentLayout {
    private final String TAG = getClass().getSimpleName();
    private MonitorFragmentLayoutEvent mLayoutEvent;
    private SubToolbar mSubToolbar;
    private FilterView mFilterView;
    private SearchView.OnCloseListener mSearchClose = new SearchView.OnCloseListener() {
        @Override
        public boolean onClose() {
            if (mLayoutEvent != null) {
                mLayoutEvent.onSearchDefault();
            }
            return false;
        }
    };

    public MonitorFragmentLayout(BaseFragment fragment, MonitorFragmentLayoutEvent layoutEvent) {
        super(fragment);
        mLayoutEvent = layoutEvent;
    }

    public FilterView getFilterView(Popup popup) {
        if (mFilterView == null) {
            mFilterView = new FilterView(mLayoutInflater, mFragment, (ViewGroup) mRootView.findViewById(R.id.body), popup);
        }
        return mFilterView;
    }

    public SubToolbar getSubToolbar(SubToolbar.SubToolBarEvent event) {
        if (mSubToolbar == null) {
            mSubToolbar = new SubToolbar(mActivity, mRootView.findViewById(R.id.sub_toolbar), event);
            mSubToolbar.setVisibleSearch(true,mSearchClose);
            mSubToolbar.showMultipleSelectInfo(false, 0);
        }
        return mSubToolbar;
    }

    public View initView(BaseFragment fragment, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = initView(fragment, inflater, container, savedInstanceState, R.layout.fragment_monitor);
        return v;
    }


    public interface MonitorFragmentLayoutEvent {
        public void onSearchDefault();
    }
}