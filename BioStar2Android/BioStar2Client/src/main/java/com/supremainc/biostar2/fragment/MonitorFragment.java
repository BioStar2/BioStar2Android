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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.adapter.MonitorAdapter;
import com.supremainc.biostar2.adapter.base.BaseListAdapter.OnItemsListener;
import com.supremainc.biostar2.sdk.datatype.v2.Device.BaseDevice;
import com.supremainc.biostar2.sdk.datatype.v2.Device.Device;
import com.supremainc.biostar2.sdk.datatype.v2.Device.Devices;
import com.supremainc.biostar2.sdk.datatype.v2.Device.ListDevice;
import com.supremainc.biostar2.sdk.datatype.v2.Door.BaseDoor;
import com.supremainc.biostar2.sdk.datatype.v2.Door.Door;
import com.supremainc.biostar2.sdk.datatype.v2.EventLog.EventLog;
import com.supremainc.biostar2.sdk.datatype.v2.EventLog.EventType;
import com.supremainc.biostar2.sdk.datatype.v2.EventLog.Query;
import com.supremainc.biostar2.sdk.datatype.v2.User.ListUser;
import com.supremainc.biostar2.sdk.datatype.v2.User.User;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.view.FilterView;
import com.supremainc.biostar2.widget.ScreenControl;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;
import com.supremainc.biostar2.widget.popup.SelectCustomData;
import com.supremainc.biostar2.widget.popup.SelectPopup;
import com.supremainc.biostar2.widget.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.widget.popup.SelectPopup.SelectType;
import com.supremainc.biostar2.widget.popup.ToastPopup;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MonitorFragment extends BaseFragment {
    private static final int MODE_FILTER = 1;
    private EventLog mEventLog;
    private FilterView mFilterView;
    private MonitorAdapter mMonitorAdapter;
    private Query mQuery;
    private Device mDevice;
    private Door mDoor;
    private String mDoorId;
    private User mUser;
    private String mUserId;
    private int mTotal;

    private Response.Listener<Door> mDoorListener = new Response.Listener<Door>() {
        @Override
        public void onResponse(Door response, Object deliverParam) {
            if (isInValidCheck(null)) {
                return;
            }
            mPopup.dismissWiat();
            if (response == null) {
                return;
            }

            ScreenControl screenControl = ScreenControl.getInstance();
            Door arg = null;
            try {
                arg = response.clone();
            } catch (CloneNotSupportedException e) {
                Log.e(TAG, "selected door clone fail");
                e.printStackTrace();
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(Door.TAG, arg);
            screenControl.addScreen(ScreenType.DOOR, bundle);
        }
    };
    private Response.ErrorListener mDoorErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isInValidCheck(error)) {
                return;
            }
            mPopup.dismissWiat();
            if (error.networkResponse.statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                mToastPopup.show(getString(R.string.none_door), null);
                return;
            }
            mPopup.show(PopupType.ALERT, mContext.getString(R.string.fail_retry), Setting.getErrorMessage(error, mContext), new OnPopupClickListener() {
                @Override
                public void OnNegative() {

                }

                @Override
                public void OnPositive() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            launchDoor(mDoorId);
                        }
                    });
                }


            }, mContext.getString(R.string.ok), mContext.getString(R.string.cancel));

        }
    };
    private OnItemsListener mOnItemsListener = new OnItemsListener() {
        @Override
        public void onSuccessNull(int total) {
            mIsDataReceived = true;
            mTotal = total;
        }

        @Override
        public void onNoMoreData() {
            mIsDataReceived = true;
            mToastPopup.show(getString(R.string.none_data), null);
        }

        @Override
        public void onTotalReceive(int total) {
            mIsDataReceived = true;
            mTotal = total;
        }
    };
    private Response.Listener<User> mUserListener = new Response.Listener<User>() {
        @Override
        public void onResponse(User response, Object deliverParam) {
            mPopup.dismissWiat();
            if (response == null) {
                //TODO
                return;
            }

            ScreenControl screenControl = ScreenControl.getInstance();
            User arg = null;
            try {
                arg = response.clone();
            } catch (CloneNotSupportedException e) {
                Log.e(TAG, "selected user clone fail");
                e.printStackTrace();
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(User.TAG, arg);
            screenControl.addScreen(ScreenType.USER_INQURIY, bundle);
        }
    };

    private Response.ErrorListener mUserErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isInValidCheck(error)) {
                return;
            }
            mPopup.dismissWiat();
            if (error.networkResponse.statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                mToastPopup.show(getString(R.string.none_user), null);
                return;
            }

            mPopup.show(PopupType.ALERT, mContext.getString(R.string.fail_retry), Setting.getErrorMessage(error, mContext), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            launchUser(mUserId);
                        }
                    });
                }

                @Override
                public void OnNegative() {

                }
            }, mContext.getString(R.string.ok), mContext.getString(R.string.cancel));

        }
    };
    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mEventLog = (EventLog) mMonitorAdapter.getItem(position);
            if (mEventLog == null) {
                return;
            }
            boolean isUser = false;
            if (mEventLog.user != null && mEventLog.user.user_id != null && mEventLog.user.name != null && !mEventLog.user.name.isEmpty() && !mEventLog.user.user_id.isEmpty()) {
                isUser = true;
            }
            if (!isUser) {
                mToastPopup.show(getString(R.string.none_user), null);
                return;
            }
            launchUser(mEventLog.user.user_id);
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

        if (mMonitorAdapter != null && mQuery != null) {
            mQuery = null;
            mMonitorAdapter.getItems(mQuery);
            return true;
        }
        return false;
    }
    @Override
    protected void onOptionItemHome() {
        if (mMonitorAdapter != null && mQuery != null) {
            mQuery = null;
            mMonitorAdapter.getItems(mQuery);
            return;
        }
        super.onOptionItemHome();
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
        if (mDoor == null) {
            mDoor = getExtraData(Door.TAG, savedInstanceState);
        }
        if (mDevice == null) {
            mDevice = getExtraData(Device.TAG, savedInstanceState);
        }

        if (mUser != null || mDoor != null || mDevice != null) {
            clickEnable = false;
        }
        if (mMonitorAdapter == null) {
            mMonitorAdapter = new MonitorAdapter(getActivity(), null, getListView(), mItemClickListener, clickEnable, mPopup, mOnItemsListener);
            mMonitorAdapter.setSwipyRefreshLayout(getSwipeyLayout(), getFab());
            if (!mIsDataReceived) {
                getSwipeyLayout().setRefreshing(true);
            }
        }

        if (setQuery() == false) {
            return false;
        }

        mMonitorAdapter.getItems(mQuery);
        return true;
    }

    private void launchDoor(String id) {
        mDoorId = id;
        mPopup.showWait(true);
        mDoorDataProvider.getDoor(TAG, id, mDoorListener, mDoorErrorListener, null);
    }

    private void launchUser(String id) {
        mUserId = id;
        mPopup.showWait(true);
        mUserDataProvider.getUser(TAG, id, mUserListener, mUserErrorListener, null);
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
        MenuInflater inflater = mContext.getMenuInflater();
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
                }
                String eventsCount = getString(R.string.all_events);
                if (mFilterView.getEventCount() != 0) {
                    eventsCount = String.valueOf(mFilterView.getEventCount());
                }

                mToastPopup.show(ToastPopup.TYPE_LOG, getString(R.string.applied_filter), getString(R.string.user) + ": " +userCount + " / " + getString(R.string.device) + ": "
                        + devicesCount + " / " + getString(R.string.event) + ": " + eventsCount);
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
        mContext.invalidateOptionsMenu();
    }

    private boolean setQuery() {
        if (mUser != null || mDoor != null || mDevice != null) {
            mQuery = new Query();
            if (mUser != null) {
                ArrayList<String> usersId = new ArrayList<String>();
                usersId.add(String.valueOf(mUser.user_id));
                ArrayList<ListUser> usersIdArray = new ArrayList<ListUser>();
                usersIdArray.add(mUser);
                mFilterView.setUserResult(usersIdArray);
                mQuery.user_id = usersId;
            }
            if (mDoor != null) {
                ArrayList<BaseDevice> deviceIdArray = new ArrayList<BaseDevice>();
                ArrayList<String> devicesId = new ArrayList<String>();
                Map<String, String> mapId = new HashMap<String, String>();
//                if (mDoor.entry_device != null) {
//                    devicesId.add(mDoor.entry_device.id);
//                    deviceIdArray.add(mDoor.entry_device);
//                    mapId.put(mDoor.entry_device.id, "exist");
//                }
                if (mDoor.door_relay != null && mDoor.door_relay.device != null) {
                    String exist = mapId.get(mDoor.door_relay.device.id);
                    if (exist == null) {
                        devicesId.add(mDoor.door_relay.device.id);
                        deviceIdArray.add(mDoor.door_relay.device);
                        mapId.put(mDoor.door_relay.device.id, "exist");
                    }
                }
//                if (mDoor.door_sensor != null && mDoor.door_sensor.device != null) {
//                    String exist = mapId.get(mDoor.door_sensor.device.id);
//                    if (exist == null) {
//                        devicesId.add(mDoor.door_sensor.device.id);
//                        deviceIdArray.add(mDoor.door_sensor.device);
//                        mapId.put(mDoor.door_sensor.device.id, "exist");
//                    }
//                }
//                if (mDoor.exit_button != null && mDoor.exit_button.device != null) {
//                    String exist = mapId.get(mDoor.exit_button.device.id);
//                    if (exist == null) {
//                        devicesId.add(mDoor.exit_button.device.id);
//                        deviceIdArray.add(mDoor.exit_button.device);
//                        mapId.put(mDoor.exit_button.device.id, "exist");
//                    }
//                }
//                if (mDoor.exit_device != null) {
//                    String exist = mapId.get(mDoor.exit_device.id);
//                    if (exist == null) {
//                        devicesId.add(mDoor.exit_device.id);
//                        deviceIdArray.add(mDoor.exit_device);
//                        mapId.put(mDoor.exit_device.id, "exist");
//                    }
//                }
                mapId.clear();
                if (devicesId.size() > 0) {
                    mQuery.device_id = devicesId;
                }
                if (deviceIdArray.size() < 1) {
                    return false;
                }
                mFilterView.setDeviceResult(deviceIdArray);
            }
            if (mDevice != null) {
                ArrayList<String> deviceId = new ArrayList<String>();
                deviceId.add(String.valueOf(mDevice.id));
                ArrayList<BaseDevice> devicesIdArray = new ArrayList<BaseDevice>();
                devicesIdArray.add(mDevice);
                if (devicesIdArray.size() < 1) {
                    return false;
                }
                mFilterView.setDeviceResult(devicesIdArray);
                mQuery.device_id = deviceId;
            }
        }
        return true;
    }
}
