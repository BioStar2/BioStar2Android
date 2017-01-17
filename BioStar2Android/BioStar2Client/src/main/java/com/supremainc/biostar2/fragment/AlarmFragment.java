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

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.adapter.DetailAdapter;
import com.supremainc.biostar2.datatype.DoorDetailData;
import com.supremainc.biostar2.datatype.DoorDetailData.DoorDetailType;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.datatype.v2.Common.ResponseStatus;
import com.supremainc.biostar2.sdk.datatype.v2.Common.VersionData;
import com.supremainc.biostar2.sdk.datatype.v2.Device.Device;
import com.supremainc.biostar2.sdk.datatype.v2.Door.Door;
import com.supremainc.biostar2.sdk.datatype.v2.Login.NotificationType;
import com.supremainc.biostar2.sdk.datatype.v2.Login.PushNotification;
import com.supremainc.biostar2.sdk.datatype.v2.Permission.PermissionModule;
import com.supremainc.biostar2.sdk.datatype.v2.User.User;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.view.SummaryDoorView;
import com.supremainc.biostar2.widget.ScreenControl;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.SelectCustomData;
import com.supremainc.biostar2.widget.popup.SelectPopup;
import com.supremainc.biostar2.widget.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.widget.popup.SelectPopup.SelectType;
import com.supremainc.biostar2.widget.popup.ToastPopup;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmFragment extends BaseFragment {
    private Device mDevice;
    private Door mDoor;
    private PushNotification mPushData;
    private SummaryDoorView mSummaryDoorView;
    private DetailAdapter mDetailAdapter;
    private ArrayList<DoorDetailData.DoorDetail> mListData;

    private OnCancelListener mCancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            if (mCommonDataProvider != null) {
                mCommonDataProvider.cancelAll(TAG);
            }
            if (mToastPopup != null) {
                mToastPopup.show(getString(R.string.canceled), null);
            }
            ScreenControl.getInstance().backScreen();
        }
    };

    private Response.ErrorListener mActionErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError, Object deliverParam) {
            if (isInValidCheck(volleyError)) {
                return;
            }
            mPopup.dismissWiat();
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "get openErrorListener:" + volleyError.getMessage());
            }
            String errorDetail = volleyError.getMessage();
            String title = (String) deliverParam + " " + getString(R.string.fail);
            String date = mTimeConvertProvider.convertCalendarToFormatter(Calendar.getInstance(), TimeConvertProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);

            String doorName = "";
            if (mDoor != null) {
                doorName = mDoor.name;
            } else if (mPushData.door != null) {
                doorName = mPushData.door.name;
            }
            if (errorDetail != null && !errorDetail.isEmpty()) {
                mToastPopup.show(ToastPopup.TYPE_DOOR, title, date + " / " + doorName + "\n" + errorDetail);
            } else {
                mToastPopup.show(ToastPopup.TYPE_DOOR, title, date + " / " + doorName);
            }
        }
    };
    private Response.Listener<User> mUserListener = new Response.Listener<User>() {
        @Override
        public void onResponse(User response, Object deliverParam) {
            if (isInValidCheck(null)) {
                return;
            }
            mPopup.dismissWiat();
            if (response == null) {
                // TODO
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
            mPopup.show(Popup.PopupType.ALERT, mContext.getString(R.string.fail), Setting.getErrorMessage(error, mContext), new Popup.OnPopupClickListener() {
                @Override
                public void OnNegative() {

                }

                @Override
                public void OnPositive() {

                }
            }, mContext.getString(R.string.ok),null);

        }
    };
    private Response.Listener<Device> mDeviceListener = new Response.Listener<Device>() {
        @Override
        public void onResponse(Device response, Object deliverParam) {
            if (isInValidCheck(null)) {
                return;
            }
            mPopup.dismissWiat();
            if (response == null) {
                mDeviceErrorListener.onErrorResponse(new VolleyError("Server response is NUll"), null);
                return;
            }
            mDevice = response;
            mSummaryDoorView.setTitle(mDevice.getName());
        }
    };
    private Response.ErrorListener mDeviceErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isInValidCheck(error)) {
                return;
            }
            mPopup.dismissWiat();
            mSummaryDoorView.showGoLogBtn(false);
//            mPopup.show(PopupType.ALERT, mContext.getString(R.string.fail_retry), Setting.getErrorMessage(error, mContext), new OnPopupClickListener() {
//                @Override
//                public void OnPositive() {
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mPopup.showWait(true);
//                            mDeviceDataProvider.getDevic(TAG, mPushData.device.id, mDeviceListener, mDeviceErrorListener, null);
//                        }
//                    });
//                }
//
//                @Override
//                public void OnNegative() {
//                    mSummaryDoorView.showGoLogBtn(false);
//                }
//            }, mContext.getString(R.string.ok), mContext.getString(R.string.cancel));
        }
    };
    private Response.Listener<Door> mDoorListener = new Response.Listener<Door>() {
        @Override
        public void onResponse(Door response, Object deliverParam) {
            if (isInValidCheck(null)) {
                return;
            }
            if (response == null) {
                mDoorErrorListener.onErrorResponse(new VolleyError("Server response is NUll"), null);
                return;
            }
            mPopup.dismissWiat();
            mDoor = response;
            mSummaryDoorView.setTitle(mDoor.getName());
        }
    };
    private Response.ErrorListener mDoorErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isInValidCheck(error)) {
                return;
            }
            mPopup.dismissWiat();
            mSummaryDoorView.showGoLogBtn(false);
            mSummaryDoorView.showActionBtn(true, false);
//            mPopup.show(PopupType.ALERT, mContext.getString(R.string.fail_retry), Setting.getErrorMessage(error, mContext), new OnPopupClickListener() {
//                @Override
//                public void OnPositive() {
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mPopup.showWait(true);
//                            mDoorDataProvider.getDoor(TAG, mPushData.door.id, mDoorListener, mDoorErrorListener, null);
//                        }
//                    });
//                }
//
//                @Override
//                public void OnNegative() {
//                    mSummaryDoorView.showGoLogBtn(false);
//                    mSummaryDoorView.showActionBtn(true, false);
//                }
//            }, mContext.getString(R.string.ok), mContext.getString(R.string.cancel));
        }
    };
    private Listener<ResponseStatus> mActionListener = new Response.Listener<ResponseStatus>() {
        @Override
        public void onResponse(ResponseStatus response, Object deliverParam) {
            if (isInValidCheck(null)) {
                return;
            }
            mPopup.dismissWiat();
            String title = (String) deliverParam;
            String date = mTimeConvertProvider.convertCalendarToFormatter(Calendar.getInstance(), TimeConvertProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);
            if (mDoor != null) {
                mToastPopup.show(ToastPopup.TYPE_DOOR, title, date + " / " + mDoor.name);
            } else if (mPushData.door != null) {
                mToastPopup.show(ToastPopup.TYPE_DOOR, title, date + " / " + mPushData.door.name);
            }
        }
    };
    private SummaryDoorView.SummaryDoorViewListener mSummaryDoorViewListener = new SummaryDoorView.SummaryDoorViewListener() {
        @Override
        public void onDoorAction() {
            if (mDoor != null ||  (mPushData.door != null && mPushData.door.id != null && !mPushData.door.id.isEmpty()) ) {
                openMenu();
            } else {
                mToastPopup.show(getString(R.string.none_door), null);
            }

        }

        @Override
        public void onGoLog() {
            String code = mPushData.code;
            if (code.equals(NotificationType.DEVICE_REBOOT.mName) || code.equals(NotificationType.DEVICE_RS485_DISCONNECT.mName) || code.equals(NotificationType.DEVICE_TAMPERING.mName)) {
                if (mDevice == null) {
                    mToastPopup.show(getString(R.string.none_device), null);
                    return;
                }
                Device bundleItem = null;
                try {
                    bundleItem = (Device) mDevice.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(Device.TAG, bundleItem);
                mScreenControl.addScreen(ScreenType.MONITOR, bundle);
                return;
            } else if (code.equals(NotificationType.DOOR_FORCED_OPEN.mName) || code.equals(NotificationType.DOOR_HELD_OPEN.mName) || code.equals(NotificationType.DOOR_OPEN_REQUEST.mName)) {
                if (mDoor == null) {
                    mToastPopup.show(getString(R.string.none_door), null);
                    return;
                }
                Door bundleItem = null;
                try {
                    bundleItem = (Door) mDoor.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(Door.TAG, bundleItem);
                mScreenControl.addScreen(ScreenType.MONITOR, bundle);
                return;
            }
            if (mDoor != null) {
                Door bundleItem = null;
                try {
                    bundleItem = (Door) mDoor.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(Door.TAG, bundleItem);
                mScreenControl.addScreen(ScreenType.MONITOR, bundle);
                return;
            }
            if (mDevice != null) {
                Device bundleItem = null;
                try {
                    bundleItem = (Device) mDevice.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(Device.TAG, bundleItem);
                mScreenControl.addScreen(ScreenType.MONITOR, bundle);
                return;
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
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.openDoor(TAG, doorID, mActionListener, mActionErrorListener, getString(R.string.open));
                break;
            case R.id.action_lock:
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.lockDoor(TAG, doorID, mActionListener, mActionErrorListener, getString(R.string.manual_lock));
                break;
            case R.id.action_unlock:
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.unlockDoor(TAG, doorID, mActionListener, mActionErrorListener, getString(R.string.manual_unlock));
                break;
            case R.id.action_release:
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.releaseDoor(TAG, doorID, mActionListener, mActionErrorListener, getString(R.string.release));
                break;
            case R.id.action_clear_apb:
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.clearAntiPassback(TAG, doorID, mActionListener, mActionErrorListener, getString(R.string.clear_apb));
                break;
            case R.id.action_clear_alarm:
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.clearAlarm(TAG, doorID, mActionListener, mActionErrorListener, getString(R.string.clear_alarm));
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
        if (mPushData == null || TextUtils.isEmpty(mPushData.code)) {
            return false;
        }
        if (mSummaryDoorView == null) {
            mSummaryDoorView = (SummaryDoorView) mRootView.findViewById(R.id.summary_door);
            mSummaryDoorView.init(mSummaryDoorViewListener);
        }
        if (mDetailAdapter == null) {
            mDetailAdapter = new DetailAdapter(mContext, null, getListView(), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DoorDetailData.DoorDetail data = (DoorDetailData.DoorDetail) mDetailAdapter.getItem(position);
                    if (data.link) {
                        switch (data.type) {
                            case USER:
                                mPopup.showWait(true);
                                if (mPushData.code.equals(NotificationType.DOOR_OPEN_REQUEST.mName)) {
                                    mUserDataProvider.getUser(TAG, mPushData.user.user_id, mUserListener, mUserErrorListener, null);
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
        String date = mPushData.getTimeFormmat(mTimeConvertProvider, PushNotification.PushNotificationTimeType.request_timestamp, TimeConvertProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);
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
                if (VersionData.getCloudVersion(mContext) > 1) {
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
            if (VersionData.getCloudVersion(mContext) > 1) {
                if ( mPermissionDataProvider.getPermission(PermissionModule.DOOR, true)) {
                    mSummaryDoorView.showActionBtn(true,true);
                } else if (mPermissionDataProvider.getPermission(PermissionModule.MONITORING, true) && mPermissionDataProvider.getPermission(PermissionModule.DOOR, false)) {
                    mSummaryDoorView.showActionBtn(true,true);
                } else {
                    mSummaryDoorView.showActionBtn(true,false);
                }
                if (mPermissionDataProvider.getPermission(PermissionModule.DOOR, false)) {
                    mSummaryDoorView.showGoLogBtn(true);
                    mPopup.showWait(mCancelListener);
                    mDoorDataProvider.getDoor(TAG, mPushData.door.id, mDoorListener, mDoorErrorListener, null);
                } else {
                    mSummaryDoorView.showGoLogBtn(false);
                }
                return true;
            }
            mSummaryDoorView.showActionBtn(true);
            mSummaryDoorView.showGoLogBtn(true);
            mPopup.showWait(mCancelListener);
            mDoorDataProvider.getDoor(TAG, mPushData.door.id, mDoorListener, mDoorErrorListener, null);
        } else if (isLinkDevice(code)) {
            if (mPushData.device != null) {
                mSummaryDoorView.setTitle(mPushData.device.name);
            }
            if (VersionData.getCloudVersion(mContext) > 1) {
                if (!mPermissionDataProvider.getPermission(PermissionModule.DEVICE, false)) {
                    mSummaryDoorView.showActionBtn(false);
                    mSummaryDoorView.showGoLogBtn(false);
                    return true;
                } else {
                    mPopup.showWait(mCancelListener);
                    mDeviceDataProvider.getDevice(TAG, mPushData.device.id, mDeviceListener, mDeviceErrorListener, null);
                }
            }
            mSummaryDoorView.showActionBtn(false);
            mSummaryDoorView.showGoLogBtn(true);
            mPopup.showWait(mCancelListener);
            mDeviceDataProvider.getDevice(TAG, mPushData.device.id, mDeviceListener, mDeviceErrorListener, null);
        } else {
            mSummaryDoorView.showActionBtn(false);
            mSummaryDoorView.showGoLogBtn(false);
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
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mContext, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        linkType.add(new SelectCustomData(getString(R.string.open), R.id.action_open, false));
        linkType.add(new SelectCustomData(getString(R.string.manual_lock), R.id.action_lock, false));
        linkType.add(new SelectCustomData(getString(R.string.manual_unlock), R.id.action_unlock, false));
        linkType.add(new SelectCustomData(getString(R.string.release), R.id.action_release, false));
        linkType.add(new SelectCustomData(getString(R.string.clear_apb), R.id.action_clear_apb, false));
        linkType.add(new SelectCustomData(getString(R.string.clear_alarm), R.id.action_clear_alarm, false));
        selectPopup.show(SelectType.CUSTOM, new OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem,boolean isPositive) {
                if (isInValidCheck(null)) {
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
