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

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.base.BaseFragment;
import com.supremainc.biostar2.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.popup.SelectCustomData;
import com.supremainc.biostar2.popup.SelectPopup;
import com.supremainc.biostar2.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.popup.SelectPopup.SelectType;
import com.supremainc.biostar2.popup.ToastPopup;
import com.supremainc.biostar2.sdk.datatype.DeviceData.Device;
import com.supremainc.biostar2.sdk.datatype.DoorData.Door;
import com.supremainc.biostar2.sdk.datatype.EventLogData.EventLog;
import com.supremainc.biostar2.sdk.datatype.NotificationData.NotificationType;
import com.supremainc.biostar2.sdk.datatype.NotificationData.PushNotification;
import com.supremainc.biostar2.sdk.datatype.ResponseStatus;
import com.supremainc.biostar2.sdk.datatype.UserData.User;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.widget.ScreenControl;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmFragment extends BaseFragment {
    private AlarmFragmentLayout mLayout;
    private Device mDevice;
    private Door mDoor;
    private PushNotification mPushData;
    private ArrayList<EventLog> mEventLogList;

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
            String date = mTimeConvertProvider.convertCalendarToFormatter(Calendar.getInstance(),TimeConvertProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);

            if (errorDetail != null && !errorDetail.isEmpty()) {
                mToastPopup.show(ToastPopup.TYPE_DOOR, title, date + " / " + mDoor.name+"\n"+errorDetail);
            } else {
                mToastPopup.show(ToastPopup.TYPE_DOOR, title, date + " / " + mDoor.name);
            }
        }
    };
    private Response.Listener<User> mUserListener = new Response.Listener<User>() {
        @Override
        public void onResponse(User response, Object deliverParam) {
            if (mIsDestroy || !isAdded()) {
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
            mPopup.show(PopupType.ALERT, mContext.getString(R.string.fail_retry), Setting.getErrorMessage(error, mContext), new OnPopupClickListener() {
                @Override
                public void OnNegative() {
                    mLayout.setUser(true, false, null);
                }

                @Override
                public void OnPositive() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPopup.showWait(true);
                            mUserDataProvider.getUser(TAG, mPushData.user.user_id, mUserListener, mUserErrorListener, null);
                        }
                    });
                }
            }, mContext.getString(R.string.ok), mContext.getString(R.string.cancel));

        }
    };
    private Response.ErrorListener mDeviceErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isInValidCheck(error)) {
                return;
            }
            mPopup.dismissWiat();
            mPopup.show(PopupType.ALERT, mContext.getString(R.string.fail_retry), Setting.getErrorMessage(error, mContext), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPopup.showWait(true);
                            mDeviceDataProvider.getDevic(TAG, mPushData.device.id, mDeviceListener, mDeviceErrorListener, null);
                        }
                    });
                }

                @Override
                public void OnNegative() {
                    mLayout.setDisableActionBtn();

                }
            }, mContext.getString(R.string.ok), mContext.getString(R.string.cancel));
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
            mLayout.setTitle(mDevice.getName());
        }
    };
    private Response.ErrorListener mDoorErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isInValidCheck(error)) {
                return;
            }
            mPopup.dismissWiat();
            mPopup.show(PopupType.ALERT, mContext.getString(R.string.fail_retry), Setting.getErrorMessage(error, mContext), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPopup.showWait(true);
                            mDoorDataProvider.getDoor(TAG, mPushData.door.id, mDoorListener, mDoorErrorListener, null);
                        }
                    });
                }

                @Override
                public void OnNegative() {
                    mLayout.setDisableActionBtn();
                }
            }, mContext.getString(R.string.ok), mContext.getString(R.string.cancel));
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
            mLayout.setTitle(mDoor.getName());
        }
    };
    private Listener<ResponseStatus> mActionListener = new Response.Listener<ResponseStatus>() {
        @Override
        public void onResponse(ResponseStatus response, Object deliverParam) {
            if (mIsDestroy || !isAdded()) {
                return;
            }
            mPopup.dismissWiat();
            String title = (String) deliverParam;
            String date = mTimeConvertProvider.convertCalendarToFormatter(Calendar.getInstance(),TimeConvertProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);
            mToastPopup.show(ToastPopup.TYPE_DOOR, title,date + " / " + mDoor.name);
        }
    };
    private AlarmFragmentLayout.AlarmFragmentLayoutEvent mLayoutEvent = new AlarmFragmentLayout.AlarmFragmentLayoutEvent() {
        @Override
        public void onClickDoorAction() {
            if (mDoor != null) {
                openMenu();
            } else {
                mToastPopup.show(getString(R.string.none_door), null);
            }
        }

        @Override
        public void onClickUser() {
            mPopup.showWait(true);
            if (mPushData.code.equals(NotificationType.DOOR_OPEN_REQUEST.mName)) {
                mUserDataProvider.getUser(TAG, mPushData.user.user_id, mUserListener, mUserErrorListener, null);
            }
        }

        @Override
        public void onClickTelephone() {
            if (mPushData.contact_phone_number == null || mPushData.contact_phone_number.isEmpty()) {
                return;
            }
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mPushData.contact_phone_number));
            startActivity(intent);
        }

        @Override
        public void onClickLog() {
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
    };


    public AlarmFragment() {
        super();
        setType(ScreenType.ALARM);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void actionDoor(int id) {
        switch (id) {
            case R.id.action_open:
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.openDoor(TAG, mDoor.id, mActionListener, mActionErrorListener, getString(R.string.open));
                break;
            case R.id.action_lock:
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.lockDoor(TAG, mDoor.id, mActionListener, mActionErrorListener, getString(R.string.manual_lock));
                break;
            case R.id.action_unlock:
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.unlockDoor(TAG, mDoor.id, mActionListener, mActionErrorListener, getString(R.string.manual_unlock));
                break;
            case R.id.action_release:
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.releaseDoor(TAG, mDoor.id, mActionListener, mActionErrorListener, getString(R.string.release));
                break;
            case R.id.action_clear_apb:
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.clearAntiPassback(TAG, mDoor.id, mActionListener, mActionErrorListener, getString(R.string.clear_apb));
                break;
            case R.id.action_clear_alarm:
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.clearAlarm(TAG, mDoor.id, mActionListener, mActionErrorListener, getString(R.string.clear_alarm));
                break;
            default:
                break;
        }
    }

    private boolean initValue(Bundle savedInstanceState) {
        if (mPushData == null) {
            mPushData = getExtraData(PushNotification.TAG, savedInstanceState);
        }
        if (mPushData == null) {
            return false;
        }
        String date = mPushData.getTimeFormmat(mTimeConvertProvider, PushNotification.PushNotificationTimeType.request_timestamp,TimeConvertProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);
        if (date == null) {
            return false;
        }
        mLayout.setNotificationTime(date);
        String code = mPushData.code;
        mLayout.setPushMessage(mPushData.message);
        setIcon();
        setView();
        if (isLinkDoor(code)) {
            mPopup.showWait(mCancelListener);
            mDoorDataProvider.getDoor(TAG, mPushData.door.id, mDoorListener, mDoorErrorListener, null);
        } else if (isLinkDevice(code)) {
            mPopup.showWait(mCancelListener);
            mDeviceDataProvider.getDevic(TAG, mPushData.device.id, mDeviceListener, mDeviceErrorListener, null);
        } else {
            return false;
        }
        return true;
    }

    private boolean isLinkDevice(String code) {
        if (code.equals(NotificationType.DEVICE_REBOOT.mName) || code.equals(NotificationType.DEVICE_RS485_DISCONNECT.mName) || code.equals(NotificationType.DEVICE_TAMPERING.mName)
                || code.equals(NotificationType.ZONE_APB.mName) || code.equals(NotificationType.ZONE_FIRE.mName)) {
            return true;
        }
        return false;
    }

    private boolean isLinkDoor(String code) {
        if (code.equals(NotificationType.DOOR_FORCED_OPEN.mName) || code.equals(NotificationType.DOOR_HELD_OPEN.mName) || code.equals(NotificationType.DOOR_OPEN_REQUEST.mName)
                ) {
            return true;
        }

        if ((code.equals(NotificationType.ZONE_APB.mName) || code.equals(NotificationType.ZONE_FIRE.mName)) && mPushData.door != null && mPushData.door.id != null) {
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
        if (mLayout == null) {
            mLayout = new AlarmFragmentLayout(this, mLayoutEvent);
        }
        View view = mLayout.initView(this, inflater, container, savedInstanceState);
        initBaseValue(mLayout);
        if (!mLayout.isReUsedView()) {
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
        }
        return view;
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
            public void OnResult(ArrayList<SelectCustomData> selectedItem) {
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
            mLayout.setIcon(R.drawable.ic_event_device_01);
        } else if (code.equals(NotificationType.DEVICE_RS485_DISCONNECT.mName)) {
            mLayout.setIcon(R.drawable.ic_event_device_03);
        } else if (code.equals(NotificationType.DEVICE_TAMPERING.mName)) {
            mLayout.setIcon(R.drawable.ic_event_device_03);
        } else if (code.equals(NotificationType.DOOR_FORCED_OPEN.mName)) {
            mLayout.setIcon(R.drawable.ic_event_door_02);
        } else if (code.equals(NotificationType.DOOR_HELD_OPEN.mName)) {
            mLayout.setIcon(R.drawable.ic_event_door_03);
        } else if (code.equals(NotificationType.DOOR_OPEN_REQUEST.mName)) {
            mLayout.setIcon(R.drawable.ic_event_door_01);
        } else if (code.equals(NotificationType.ZONE_APB.mName)) {
            if (isLinkDoor(code)) {
                mLayout.setIcon(R.drawable.ic_event_door_03);
            } else {
                mLayout.setIcon(R.drawable.ic_event_zone_03);
            }
        } else if (code.equals(NotificationType.ZONE_FIRE.mName)) {
            mLayout.setIcon(R.drawable.ic_event_fire_alarm);
        } else {
            mLayout.setIcon(R.drawable.monitoring_ic1);
        }
    }

    private void setView() {
        String code = mPushData.code;
        if (code.equals(NotificationType.DOOR_OPEN_REQUEST.mName)) {
            if (mPushData.user != null) {
                String name = mPushData.user.user_id;
                if (mPushData.user.name == null) {
                    name = name + " / " + mPushData.user.user_id;
                } else {
                    name = name + " / " + mPushData.user.name;
                }
                mLayout.setUser(true, true, name);
            } else {
                mLayout.setUser(true, false, getString(R.string.none));
            }
            if (mPushData.contact_phone_number == null && mPushData.contact_phone_number.isEmpty()) {
                mLayout.setPhoneNumber(true, false, getString(R.string.none));
            } else {
                mLayout.setPhoneNumber(true, true, mPushData.contact_phone_number);
            }
            mLayout.setActionButtonName(getString(R.string.door_control));
        } else if (isLinkDoor(code)) {
            mLayout.setUser(false, false, null);
            mLayout.setPhoneNumber(false, false, getString(R.string.none));
        } else if (isLinkDevice(code)) {
            mLayout.setDevice();
        } else {
            mLayout.setDefault(mPushData.title);
        }
    }
}
