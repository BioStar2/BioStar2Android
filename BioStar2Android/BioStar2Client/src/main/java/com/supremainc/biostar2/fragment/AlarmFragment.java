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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.adapter.DetailAdapter;
import com.supremainc.biostar2.datatype.DoorDetailData;
import com.supremainc.biostar2.datatype.DoorDetailData.DoorDetailType;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.models.v2.common.VersionData;
import com.supremainc.biostar2.sdk.models.v2.device.Device;
import com.supremainc.biostar2.sdk.models.v2.door.Door;
import com.supremainc.biostar2.sdk.models.v2.login.NotificationType;
import com.supremainc.biostar2.sdk.models.v2.login.PushNotification;
import com.supremainc.biostar2.sdk.models.v2.permission.PermissionModule;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.sdk.provider.DateTimeDataProvider;
import com.supremainc.biostar2.view.SummaryDoorView;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.SelectCustomData;
import com.supremainc.biostar2.widget.popup.SelectPopup;
import com.supremainc.biostar2.widget.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.widget.popup.SelectPopup.SelectType;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlarmFragment extends BaseFragment {
    private Device mDevice;
    private Door mDoor;
    private PushNotification mPushData;
    private SummaryDoorView mSummaryDoorView;
    private DetailAdapter mDetailAdapter;
    private ArrayList<DoorDetailData.DoorDetail> mListData;
    private String mActionTitle = "";
    private Callback<User> mUserListener = new Callback<User>() {
        @Override
        public void onFailure(Call<User> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showErrorPopup(t.getMessage(),false);
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
                User arg = response.body().clone();
                Bundle bundle = new Bundle();
                bundle.putSerializable(User.TAG, arg);
                mScreenControl.addScreen(ScreenType.USER_INQURIY, bundle);
            } catch (Exception e) {
                showErrorPopup(e.getMessage(),false);
            }
        }
    };
    private Callback<Device> mDeviceListener = new Callback<Device>() {
        @Override
        public void onFailure(Call<Device> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
        }

        @Override
        public void onResponse(Call<Device> call, Response<Device> response) {
            if (isIgnoreCallback(call,response,true)) {
                return;
            }
            if (isInvalidResponse(response, false, false)) {
                return;
            }
            mDevice = response.body();
            mSummaryDoorView.setTitle(mDevice.getName());
        }
    };

    private Callback<Door> mDoorListener = new Callback<Door>() {
        @Override
        public void onFailure(Call<Door> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            mSummaryDoorView.showActionBtn(true, false);
        }

        @Override
        public void onResponse(Call<Door> call, Response<Door> response) {
            if (isIgnoreCallback(call,response,true)) {
                return;
            }
            if (isInvalidResponse(response, false, false)) {
                return;
            }
            mDoor = response.body();
            mSummaryDoorView.setTitle(mDoor.getName());
        }
    };

    private Callback<ResponseStatus> mActionListener = new Callback<ResponseStatus>() {
        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            String errorDetail = t.getMessage();
            String title = getString(R.string.fail)+". "+mActionTitle;
            String date = mDateTimeDataProvider.convertCalendarToFormatter(Calendar.getInstance(), DateTimeDataProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);

            String doorName = "";
            if (mDoor != null) {
                doorName = mDoor.name;
            } else if (mPushData.door != null) {
                doorName = mPushData.door.name;
            }
            String body = "";
            if (errorDetail != null && !errorDetail.isEmpty()) {
                body = mDoor.name + " / " + mDoor.id + "\n" + date + "\n" + errorDetail;
            } else {
                body = mDoor.name + " / " + mDoor.id + "\n" + date + "\n";
            }
            mPopup.show(Popup.PopupType.DOOR, title, body, null, null, null);
        }

        @Override
        public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
            if (isIgnoreCallback(call,response,true)) {
                return;
            }
            if (isInvalidResponse(response, true, false)) {
                return;
            }
            String date = mDateTimeDataProvider.convertCalendarToFormatter(Calendar.getInstance(), DateTimeDataProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);
            String body = "";
            if (mDoor != null) {
                body = mDoor.name + " / " + mDoor.id + "\n" + date;
            } else if (mPushData.door != null) {
                body = mPushData.door.name + " / " + mPushData.door.id + "\n" + date;
            }

            mPopup.show(Popup.PopupType.DOOR, mActionTitle, body, null, null, null);
        }
    };


    private SummaryDoorView.SummaryDoorViewListener mSummaryDoorViewListener = new SummaryDoorView.SummaryDoorViewListener() {
        @Override
        public void onDoorAction() {
            if (mDoor != null || (mPushData.door != null && mPushData.door.id != null && !mPushData.door.id.isEmpty())) {
                openMenu();
            } else {
                mToastPopup.show(getString(R.string.none_door), null);
            }

        }

        @Override
        public void onStatus() {

        }
    };

    public AlarmFragment() {
        super();
        setType(ScreenType.ALARM);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void actionDoor(int id) {
        String doorID = null;
        if (mDoor != null) {
            doorID = mDoor.id;
        } else if (mPushData.door != null && mPushData.door.id != null && !mPushData.door.id.isEmpty()) {
            doorID = mPushData.door.id;
        } else {
            return;
        }
        switch (id) {
            case R.id.action_open:
                mPopup.showWait(mCancelExitListener);
                mActionTitle = getString(R.string.door_is_open);
                request(mDoorDataProvider.openDoor(doorID, mActionListener));
                break;
            case R.id.action_lock:
                mPopup.showWait(mCancelExitListener);
                mActionTitle = getString(R.string.manual_lock);
                request(mDoorDataProvider.lockDoor(doorID, mActionListener));
                break;
            case R.id.action_unlock:
                mPopup.showWait(mCancelExitListener);
                mActionTitle = getString(R.string.manual_unlock);
                request(mDoorDataProvider.unlockDoor(doorID, mActionListener));
                break;
            case R.id.action_release:
                mPopup.showWait(mCancelExitListener);
                mActionTitle = getString(R.string.release);
                request(mDoorDataProvider.releaseDoor(doorID, mActionListener));
                break;
            case R.id.action_clear_apb:
                mPopup.showWait(mCancelExitListener);
                mActionTitle = getString(R.string.clear_apb);
                request(mDoorDataProvider.clearAntiPassback(doorID, mActionListener));
                break;
            case R.id.action_clear_alarm:
                mPopup.showWait(mCancelExitListener);
                mActionTitle = getString(R.string.clear_alarm);
                request(mDoorDataProvider.clearAlarm(doorID, mActionListener));
                break;
            default:
                break;
        }
    }

    private boolean initValue(Bundle savedInstanceState) {
        if (mPushData == null) {
            mPushData = getExtraData(PushNotification.TAG, savedInstanceState);
        }
        if (mListData == null) {
            mListData = new ArrayList<DoorDetailData.DoorDetail>();
        } else {
            mListData.clear();
        }
        if (mDetailAdapter != null) {
            mDetailAdapter.notifyDataSetChanged();
        }
        if (mPushData == null || TextUtils.isEmpty(mPushData.code)) {
            return false;
        }
        if (mSummaryDoorView == null) {
            mSummaryDoorView = (SummaryDoorView) mRootView.findViewById(R.id.summary_door);
            mSummaryDoorView.init(mSummaryDoorViewListener);
        }
        if (mDetailAdapter == null) {
            mDetailAdapter = new DetailAdapter(mActivity, null, getListView(), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DoorDetailData.DoorDetail data = (DoorDetailData.DoorDetail) mDetailAdapter.getItem(position);
                    if (data.link) {
                        switch (data.type) {
                            case USER:
                                mPopup.showWait(true);
                                if (mPushData.code.equals(NotificationType.DOOR_OPEN_REQUEST.mName)) {
                                    mUserDataProvider.getUser(mPushData.user.user_id, mUserListener);
                                }
                                break;
                            case TELEPHONE:
                                if (TextUtils.isEmpty(mPushData.contact_phone_number)) {
                                    return;
                                }
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mPushData.contact_phone_number));
                                startActivity(intent);
                                break;
                        }
                    }
                }
            }, mPopup, null);
        }
        String date = mPushData.getTimeFormmat(mDateTimeDataProvider, PushNotification.PushNotificationTimeType.request_timestamp, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);
        if (date == null) {
            return false;
        }
        setIcon();
        mSummaryDoorView.setContent(mPushData.message);
        mListData.add(new DoorDetailData.DoorDetail(getString(R.string.notification_time), date, false, DoorDetailType.NOTIFICATION_TIME));
        String code = mPushData.code;
        if (code.equals(NotificationType.DOOR_OPEN_REQUEST.mName)) {
            if (mPushData.user != null) {
                String name = mPushData.user.user_id;
                if (mPushData.user.name == null) {
                    name = name + " / " + mPushData.user.user_id;
                } else {
                    name = name + " / " + mPushData.user.name;
                }
                if (VersionData.getCloudVersion(mActivity) > 1) {
                    if (mPermissionDataProvider.getPermission(PermissionModule.USER, false)) {
                        mListData.add(new DoorDetailData.DoorDetail(getString(R.string.user), name, true, DoorDetailType.USER));
                    } else {
                        mListData.add(new DoorDetailData.DoorDetail(getString(R.string.user), name, false, DoorDetailType.USER));
                    }
                } else {
                    mListData.add(new DoorDetailData.DoorDetail(getString(R.string.user), name, true, DoorDetailType.USER));
                }
            } else {
                mListData.add(new DoorDetailData.DoorDetail(getString(R.string.user), getString(R.string.none), false, DoorDetailType.USER));
            }
            if (TextUtils.isEmpty(mPushData.contact_phone_number)) {
                mListData.add(new DoorDetailData.DoorDetail(getString(R.string.telephone), getString(R.string.none), false, DoorDetailType.TELEPHONE));
            } else {
                mListData.add(new DoorDetailData.DoorDetail(getString(R.string.telephone), mPushData.contact_phone_number, true, DoorDetailType.TELEPHONE));
            }
        }
        mDetailAdapter.setData(mListData);
        mDetailAdapter.notifyDataSetChanged();
        if (isLinkDoor(code)) {
            mSummaryDoorView.setActionButtonName(getString(R.string.door_control));

            if (mPushData.door != null) {
                mSummaryDoorView.setTitle(mPushData.door.name);
            }
            if (VersionData.getCloudVersion(mActivity) > 1) {
                if (mPermissionDataProvider.getPermission(PermissionModule.DOOR, true)) {
                    mSummaryDoorView.showActionBtn(true, true);
                } else if (mPermissionDataProvider.getPermission(PermissionModule.MONITORING, true) && mPermissionDataProvider.getPermission(PermissionModule.DOOR, false)) {
                    mSummaryDoorView.showActionBtn(true, true);
                } else {
                    mSummaryDoorView.showActionBtn(true, false);
                }
                if (mPermissionDataProvider.getPermission(PermissionModule.DOOR, false)) {
                    mPopup.showWait(mCancelExitListener);
                    request(mDoorDataProvider.getDoor(mPushData.door.id, mDoorListener));
                }
                return true;
            }
            mSummaryDoorView.showActionBtn(true);
            mPopup.showWait(mCancelExitListener);
            request(mDoorDataProvider.getDoor(mPushData.door.id, mDoorListener));
        } else if (isLinkDevice(code)) {
            if (mPushData.device != null) {
                mSummaryDoorView.setTitle(mPushData.device.name);
            }
            if (VersionData.getCloudVersion(mActivity) > 1) {
                if (!mPermissionDataProvider.getPermission(PermissionModule.DEVICE, false)) {
                    mSummaryDoorView.showActionBtn(false);
                    return true;
                } else {
                    mPopup.showWait(mCancelExitListener);
                    request(mDeviceDataProvider.getDevice(mPushData.device.id, mDeviceListener));
                }
            }
            mSummaryDoorView.showActionBtn(false);
            mPopup.showWait(mCancelExitListener);
            request(mDeviceDataProvider.getDevice(mPushData.device.id, mDeviceListener));
        } else {
            mSummaryDoorView.showActionBtn(false);
        }
        return true;
    }


    private boolean isLinkDevice(String code) {
        if (mPushData.device == null || mPushData.device.id == null || mPushData.device.id.isEmpty()) {
            return false;
        }
        if (code.equals(NotificationType.DEVICE_REBOOT.mName) || code.equals(NotificationType.DEVICE_RS485_DISCONNECT.mName) || code.equals(NotificationType.DEVICE_TAMPERING.mName)
                || code.equals(NotificationType.ZONE_APB.mName) || code.equals(NotificationType.ZONE_FIRE.mName)) {
            return true;
        }
        return false;
    }

    private boolean isLinkDoor(String code) {
        if (mPushData.door == null || mPushData.door.id == null || mPushData.door.id.isEmpty()) {
            return false;
        }
        if (code.equals(NotificationType.DOOR_FORCED_OPEN.mName) || code.equals(NotificationType.DOOR_HELD_OPEN.mName) || code.equals(NotificationType.DOOR_OPEN_REQUEST.mName)
                ) {
            return true;
        }

        if (code.equals(NotificationType.ZONE_APB.mName) || code.equals(NotificationType.ZONE_FIRE.mName)) {
            return true;
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setResID(R.layout.fragment_alarm);
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
            initActionbar(mPushData.title);
            mRootView.invalidate();
        }
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState");
        PushNotification bundleItem = null;
        try {
            bundleItem = (PushNotification) mPushData.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return;
        }
        outState.putSerializable(PushNotification.TAG, bundleItem);
    }

    public void openMenu() {
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mActivity, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        linkType.add(new SelectCustomData(getString(R.string.open), R.id.action_open, false));
        linkType.add(new SelectCustomData(getString(R.string.manual_lock), R.id.action_lock, false));
        linkType.add(new SelectCustomData(getString(R.string.manual_unlock), R.id.action_unlock, false));
        linkType.add(new SelectCustomData(getString(R.string.release), R.id.action_release, false));
        linkType.add(new SelectCustomData(getString(R.string.clear_apb), R.id.action_clear_apb, false));
        linkType.add(new SelectCustomData(getString(R.string.clear_alarm), R.id.action_clear_alarm, false));
        selectPopup.show(SelectType.CUSTOM, new OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem, boolean isPositive) {
                if (isInValidCheck()) {
                    return;
                }
                if (selectedItem == null) {
                    return;
                }
                int type = selectedItem.get(0).getIntId();
                actionDoor(type);
            }
        }, linkType, getString(R.string.door_control), false, true);
    }

    private void setIcon() {
        String code = mPushData.code;
        if (code.equals(NotificationType.DEVICE_REBOOT.mName)) {
            mSummaryDoorView.setIcon(R.drawable.ic_event_device_01);
        } else if (code.equals(NotificationType.DEVICE_RS485_DISCONNECT.mName)) {
            mSummaryDoorView.setIcon(R.drawable.ic_event_device_03);
        } else if (code.equals(NotificationType.DEVICE_TAMPERING.mName)) {
            mSummaryDoorView.setIcon(R.drawable.ic_event_device_03);
        } else if (code.equals(NotificationType.DOOR_FORCED_OPEN.mName)) {
            mSummaryDoorView.setIcon(R.drawable.ic_event_door_02);
        } else if (code.equals(NotificationType.DOOR_HELD_OPEN.mName)) {
            mSummaryDoorView.setIcon(R.drawable.ic_event_door_03);
        } else if (code.equals(NotificationType.DOOR_OPEN_REQUEST.mName)) {
            mSummaryDoorView.setIcon(R.drawable.ic_event_door_01);
        } else if (code.equals(NotificationType.ZONE_APB.mName)) {
            if (isLinkDoor(code)) {
                mSummaryDoorView.setIcon(R.drawable.ic_event_door_03);
            } else {
                mSummaryDoorView.setIcon(R.drawable.ic_event_zone_03);
            }
        } else if (code.equals(NotificationType.ZONE_FIRE.mName)) {
            mSummaryDoorView.setIcon(R.drawable.ic_event_fire_alarm);
        } else {
            mSummaryDoorView.setIcon(R.drawable.monitoring_ic1);
        }
    }
}
