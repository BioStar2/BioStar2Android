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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.adapter.MonitorAdapter;
import com.supremainc.biostar2.adapter.base.BaseListAdapter.OnItemsListener;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.models.v2.eventlog.Query;
import com.supremainc.biostar2.sdk.models.v2.user.ListUser;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.view.FilterView;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.popup.ToastPopup;

import java.util.ArrayList;

public class MonitorFragment extends BaseFragment {
    private static final int MODE_FILTER = 1;
    private FilterView mFilterView;
    private MonitorAdapter mMonitorAdapter;
    private Query mQuery;
    private User mUser;
    private OnItemsListener mOnItemsListener = new OnItemsListener() {
        @Override
        public void onSuccessNull(int total) {
            mIsDataReceived = true;
        }

        @Override
        public void onNoneData() {
            mIsDataReceived = true;
            mToastPopup.show(getString(R.string.none_data), null);
        }

        @Override
        public void onTotalReceive(int total) {
            mIsDataReceived = true;
        }
    };


    public MonitorFragment() {
        super();
        setType(ScreenType.MONITOR);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    @Override
    public boolean onBack() {
        if (super.onBack()) {
            return true;
        }
        return false;
    }

    private boolean initValue(Bundle savedInstanceState) {
        boolean clickEnable = true;
        if (mFilterView == null) {
            mFilterView = (FilterView) mRootView.findViewById(R.id.filter_view);
            mFilterView.init(getActivity(), mPopup);
        }
        if (mUser == null) {
            mUser = getExtraData(User.TAG, savedInstanceState);
        }
        if (mMonitorAdapter == null) {
            mMonitorAdapter = new MonitorAdapter(getActivity(), null, getListView(), null, clickEnable, mPopup, mOnItemsListener);
            mMonitorAdapter.setSwipyRefreshLayout(getSwipeyLayout(), getFab());

        }
        if (mUser != null) {
            ArrayList<ListUser> usersIdArray = new ArrayList<ListUser>();
            usersIdArray.add(mUser);
            mFilterView.setUserResult(usersIdArray);
            mQuery = mFilterView.getQuery();
        }
        return true;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setResID(R.layout.fragment_monitor);
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
            initActionbar(getString(R.string.monitoring));
            mRootView.invalidate();
        }
        return mRootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = mActivity.getMenuInflater();
        switch (mSubMode) {
            default:
            case MODE_NORMAL:
                initActionbar(getString(R.string.monitoring));
                inflater.inflate(R.menu.filter, menu);
                break;
            case MODE_FILTER:
                initActionbar(getString(R.string.filter));
                inflater.inflate(R.menu.filter_save, menu);
                break;
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mIsDataReceived && mMonitorAdapter != null) {
            mMonitorAdapter.getItems(mQuery);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMonitorAdapter != null) {
            mMonitorAdapter.clearItems();
        }
    }
//    private static int testCount=0;
//    Runnable mrun =new Runnable() {
//        @Override
//        public void run() {
//            mQuery = mFilterView.getQuery();
//
//            {
//                ArrayList<EventType> items = mEventDataProvider.getEventTypeList();
//                if (testCount >= items.size()) {
//                    testCount = 0;
//                    return;
//                }
//                ArrayList<String> eventId = new ArrayList<String>();
//                EventType type = items.get(testCount);
//                testCount++;
//                eventId.add(String.valueOf(type.code));
//                mQuery.event_type_code = eventId;
//                Log.e(TAG,"type:"+type.description+" code:"+type.code+" testCount:"+testCount);
//            }
//            mMonitorAdapter.getItems(mQuery);
//            mHandler.postDelayed(mrun,5000);
//        }
//    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_filter:
                setSubMode(MODE_FILTER);
                return true;
            case R.id.action_save:
                setSubMode(MODE_NORMAL);
                if (mFilterView == null) {
                    return true;
                }
//                testCount=0;
//                mHandler.postDelayed(mrun,3000);
                mQuery = mFilterView.getQuery();
                mMonitorAdapter.getItems(mQuery);
                String userCount = getString(R.string.all_users);
                if (mFilterView.getUserCount() != 0) {
                    userCount = String.valueOf(mFilterView.getUserCount());
                }
                String devicesCount = getString(R.string.all_devices);
                if (mFilterView.getDeviceCount() != 0) {
                    devicesCount = String.valueOf(mFilterView.getDeviceCount());
                } else if (mFilterView.getDoorCount() != 0) {
                    devicesCount = getString(R.string.none);
                }

                String eventsCount = getString(R.string.all_events);
                if (mFilterView.getEventCount() != 0) {
                    eventsCount = String.valueOf(mFilterView.getEventCount());
                }

                String doorCount = getString(R.string.all_door);
                if (mFilterView.getDoorCount() != 0) {
                    doorCount = String.valueOf(mFilterView.getDoorCount());
                } else if (mFilterView.getDeviceCount() != 0) {
                    doorCount = getString(R.string.none);
                }

                mToastPopup.show(ToastPopup.TYPE_LOG, getString(R.string.applied_filter), getString(R.string.user) + ": " + userCount + " / " + getString(R.string.device) + ": "
                        + devicesCount + " / " + getString(R.string.event) + ": " + eventsCount + " / " + getString(R.string.door) + ": " + doorCount);
                return true;
            case R.id.action_restore:
                mFilterView.setDefault();
                return true;
            default:
                break;
        }

        return false;
    }

    @Override
    public boolean onSearch(String query) {
        if (super.onSearch(query)) {
            return true;
        }
        if (mFilterView != null) {
            return mFilterView.onSearch(query);
        }
        return false;
    }

    protected void registerBroadcast() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();
                    if (mIsDestroy) {
                        return;
                    }
                    if (action.equals(Setting.BROADCAST_PREFRENCE_REFRESH) && mMonitorAdapter != null) {
                        mMonitorAdapter.notifyDataSetChanged();
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_PREFRENCE_REFRESH);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
    }

    @Override
    protected void setSubMode(int mode) {
        mSubMode = mode;
        switch (mode) {
            case MODE_NORMAL:
                if (mFilterView.getVisibility() == View.VISIBLE) {
                    mFilterView.setVisibility(View.GONE);
                }
                break;
            case MODE_FILTER:
                if (mFilterView.getVisibility() != View.VISIBLE) {
                    mFilterView.setVisibility(View.VISIBLE);
                }
                break;
        }
        mActivity.invalidateOptionsMenu();
    }

}
