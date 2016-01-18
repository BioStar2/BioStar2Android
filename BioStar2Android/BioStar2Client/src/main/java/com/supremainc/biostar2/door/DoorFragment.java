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

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.base.BaseFragment;
import com.supremainc.biostar2.popup.SelectCustomData;
import com.supremainc.biostar2.popup.SelectPopup;
import com.supremainc.biostar2.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.popup.SelectPopup.SelectType;
import com.supremainc.biostar2.popup.ToastPopup;
import com.supremainc.biostar2.sdk.datatype.DoorData.Door;
import com.supremainc.biostar2.sdk.datatype.PermissionData.PERMISSION_MODULE;
import com.supremainc.biostar2.sdk.datatype.ResponseStatus;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;

import java.util.ArrayList;
import java.util.Calendar;

public class DoorFragment extends BaseFragment {
    private DoorFragmentLayout mLayout;
    private Door mDoor;

    private OnCancelListener mCancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            mCommonDataProvider.cancelAll(TAG);
            mToastPopup.show(getString(R.string.canceled), null);
            mScreenControl.backScreen();
        }
    };
    private Response.ErrorListener mRequestDoorErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isInValidCheck(error)) {
                return;
            }
            mPopup.dismissWiat();
            Log.e(TAG, "get mDoor:" + error.getMessage());
            mToastPopup.show(ToastPopup.TYPE_ALARM, getString(R.string.request_open_fail), null);
            // mToastPopup.show(ToastPopup.TYPE_DOOR,
            // null,Setting.getErrorMessage(error, mContext));
        }
    };
    private Listener<ResponseStatus> mControlListener = new Response.Listener<ResponseStatus>() {
        @Override
        public void onResponse(ResponseStatus response, Object deliverParam) {
            if (mIsDestroy || !isAdded()) {
                return;
            }
            mPopup.dismissWiat();
            int id = (Integer) deliverParam;
            String message = "";
            switch (id) {
                case R.id.action_open:
                    message = getString(R.string.door_is_open);
                    break;
                case R.id.action_lock:
                    message = getString(R.string.lock);
                    break;
                case R.id.action_unlock:
                    message = getString(R.string.unlock);
                    break;
                case R.id.action_clear_apb:
                    message = getString(R.string.clear_apb);
                    break;
                case R.id.action_clear_alarm:
                    message = getString(R.string.clear_alarm);
                    break;
            }
            String date = mTimeConvertProvider.convertCalendarToFormatter(Calendar.getInstance(), TimeConvertProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);
            mToastPopup.show(ToastPopup.TYPE_DOOR, message, date + " / " + mDoor.name);
        }
    };
    private Response.ErrorListener mControlErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isInValidCheck(error)) {
                return;
            }
            mPopup.dismissWiat();
            Log.e(TAG, "get mDoor:" + error.getMessage());
            int id = (Integer) deliverParam;
            String message = "";
            switch (id) {
                case R.id.action_open:
                    message = getString(R.string.door_open_fail);
                    break;
                case R.id.action_lock:
                    message = getString(R.string.lock) + " " + getString(R.string.fail);
                    break;
                case R.id.action_unlock:
                    message = getString(R.string.unlock) + " " + getString(R.string.fail);
                    break;
                case R.id.action_clear_apb:
                    message = getString(R.string.clear_apb) + " " + getString(R.string.fail);
                    break;
                case R.id.action_clear_alarm:
                    message = getString(R.string.clear_alarm) + " " + getString(R.string.fail);
                    break;
            }
            String date = mTimeConvertProvider.convertCalendarToFormatter(Calendar.getInstance(),TimeConvertProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);
            mToastPopup.show(ToastPopup.TYPE_DOOR, message, date + " / " + mDoor.name);
//			mToastPopup.show(ToastPopup.TYPE_DOOR, message, Setting.getErrorMessage(error, mContext));
        }
    };
    private Listener<ResponseStatus> mRequestDoorListener = new Response.Listener<ResponseStatus>() {
        @Override
        public void onResponse(ResponseStatus response, Object deliverParam) {
            if (mIsDestroy || !isAdded()) {
                return;
            }
            mPopup.dismissWiat();
            mToastPopup.show(ToastPopup.TYPE_ALARM, getString(R.string.request_open_sent), mDoor.name);
        }
    };
    private DoorFragmentLayout.DoorFragmentLayoutEvent mLayoutEvent = new DoorFragmentLayout.DoorFragmentLayoutEvent() {
        @Override
        public void onClickDoorAction() {
            if (mPermissionDataProvider.getPermission(PERMISSION_MODULE.DOOR, true)) {
                openMenu();
            } else {
                sendOpenRequest();
            }
        }

        @Override
        public void onClickDoorIcon() {
            if (mDoor.status != null) {
                if (mDoor.status.forced_open) {
                    mToastPopup.show(ToastPopup.TYPE_DOOR, getString(R.string.forced_open), null);
                } else if (mDoor.status.held_opened) {
                    mToastPopup.show(ToastPopup.TYPE_DOOR, getString(R.string.held_opened), null);
                } else if (mDoor.status.disconnected) {
                    mToastPopup.show(ToastPopup.TYPE_DOOR, getString(R.string.disconnected), null);
                } else if (mDoor.status.unlocked) {
                    mToastPopup.show(ToastPopup.TYPE_DOOR, getString(R.string.unlock), null);
                }
            }
        }

        @Override
        public void onClickLog() {
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
        }
    };

    public DoorFragment() {
        super();
        setType(ScreenType.DOOR);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void actionDoor(int id) {
        switch (id) {
            case R.id.action_open:
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.openDoor(TAG, mDoor.id, mControlListener, mControlErrorListener, R.id.action_open);
                break;
            case R.id.action_lock:
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.lockDoor(TAG, mDoor.id, mControlListener, mControlErrorListener, R.id.action_lock);
                break;
            case R.id.action_unlock:
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.unlockDoor(TAG, mDoor.id, mControlListener, mControlErrorListener, R.id.action_unlock);
                break;
            case R.id.action_clear_apb:
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.clearAntiPassback(TAG, mDoor.id, mControlListener, mControlErrorListener, R.id.action_clear_apb);
                break;
            case R.id.action_clear_alarm:
                mPopup.showWait(mCancelListener);
                mDoorDataProvider.clearAlarm(TAG, mDoor.id, mControlListener, mControlErrorListener, R.id.action_clear_alarm);
                break;
            default:
                break;
        }
    }

    private void applyPermission() {
        if (mLayout != null) {
            if (mPermissionDataProvider.getPermission(PERMISSION_MODULE.DOOR, true)) {
                mLayout.setActionButtonName(getString(R.string.door_control));
            } else {
                mLayout.setActionButtonName(getString(R.string.request_open));
            }
        }
    }

    private boolean initValue(Bundle savedInstanceState) {
        mDoor = getExtraData(Door.TAG, savedInstanceState);
        if (mDoor == null) {
            return false;
        }
        mLayout.setContent(mDoor.name, mDoor.description);
        applyPermission();
        setView();
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    protected void registerBroadcast() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (mIsDestroy) {
                        return;
                    }
                    String action = intent.getAction();
                    if (action.equals(Setting.BROADCAST_REROGIN)) {
                        applyPermission();
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_REROGIN);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mLayout == null) {
            mLayout = new DoorFragmentLayout(this, mLayoutEvent);
        }
        View view = mLayout.initView(this, inflater, container, savedInstanceState);
        if (!mLayout.isReUsedView()) {
            initBaseValue(mLayout);
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
            initActionbar(mDoor.name);
        }
        return view;
    }

    public void openMenu() {
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mContext, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        linkType.add(new SelectCustomData(getString(R.string.open), R.id.action_open, false));
        linkType.add(new SelectCustomData(getString(R.string.lock), R.id.action_lock, false));
        linkType.add(new SelectCustomData(getString(R.string.unlock), R.id.action_unlock, false));
        linkType.add(new SelectCustomData(getString(R.string.clear_apb), R.id.action_clear_apb, false));
        linkType.add(new SelectCustomData(getString(R.string.clear_alarm), R.id.action_clear_alarm, false));
        if (Setting.IS_TEST_OPEN_DOOR_REQUEST) {
            linkType.add(new SelectCustomData(getString(R.string.request_open), -1, false));
        }
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
                if (Setting.IS_TEST_OPEN_DOOR_REQUEST) {
                    if (type == -1) {
                        sendOpenRequest();
                        return;
                    }
                }
                actionDoor(type);
            }
        }, linkType, getString(R.string.door_control), false, true);
    }


    private Runnable mRunAllow = new Runnable() {
        @Override
        public void run() {
            sendOpenRequest();
        }
    };
    private Runnable mRunDeny = new Runnable() {
        @Override
        public void run() {
            mPopup.showWait(true);
            mDoorDataProvider.openRequestDoor(TAG, mDoor.id, mDoorDataProvider.getLoginUserInfo().phone_number, mRequestDoorListener, mRequestDoorErrorListener, null);
        }
    };

    @Override
    public void onAllow(int requestCode) {
        if (mHandler == null || requestCode != Setting.REQUEST_READ_PHONE_STATE) {
            return;
        }
        mHandler.removeCallbacks(mRunAllow);
        mHandler.postDelayed(mRunAllow,1000);
    }
    @Override
    public void onDeny(int requestCode) {
        if (mHandler == null || requestCode != Setting.REQUEST_READ_PHONE_STATE) {
            return;
        }
        mHandler.removeCallbacks(mRunDeny);
        mHandler.postDelayed(mRunDeny, 1000);
    }

    private void sendOpenRequest() {
        String PhoneNumber = null;
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.READ_PHONE_STATE},
                            Setting.REQUEST_READ_PHONE_STATE);
                    return;
                }
            }
            TelephonyManager telephony = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephony != null) {
                PhoneNumber = telephony.getLine1Number();
            }
            if (PhoneNumber != null) {
                PhoneNumber = PhoneNumberUtils.formatNumber(PhoneNumber);
            }
        } catch (Exception e) {
            PhoneNumber = null;
            if (BuildConfig.DEBUG) {
                Log.e(TAG," "+e.getMessage());
            }
        }

        if (PhoneNumber == null) {
            PhoneNumber = mDoorDataProvider.getLoginUserInfo().phone_number;
        }
        mPopup.showWait(true);
        mDoorDataProvider.openRequestDoor(TAG, mDoor.id, PhoneNumber, mRequestDoorListener, mRequestDoorErrorListener, null);
    }

    private void setView() {
        if (mDoor.status != null) {
            if (mDoor.status.forced_open) {
                mLayout.setIcon(R.drawable.door_ic_2);
            } else if (mDoor.status.held_opened) {
                mLayout.setIcon(R.drawable.door_ic_1);
            } else if (mDoor.status.disconnected) {
                mLayout.setIcon(R.drawable.door_ic_2);
            } else if (mDoor.status.unlocked) {
                mLayout.setIcon(R.drawable.door_ic_1);
            }
        }

        String data = getString(R.string.none);
        if (mDoor.entry_device != null) {
            data = mDoor.entry_device.getName();
        }
        mLayout.setEntryDeviceName(data);

        data = getString(R.string.none);
        if (mDoor.exit_device != null) {
            data = mDoor.exit_device.getName();
        }
        mLayout.setExitDeviceName(data);

        data = getString(R.string.none);
        if (mDoor.door_relay != null) {
            data = getString(R.string.relay) + " " + mDoor.door_relay.index + "  " + mDoor.door_relay.getName();
        }
        mLayout.setDoorRelayName(data);

        data = getString(R.string.none);
        if (mDoor.door_sensor != null) {
            data = getString(R.string.input_port) + " " + mDoor.door_sensor.index + "  " + mDoor.door_sensor.getName();
        }
        mLayout.setDoorSensorName(data);

        data = getString(R.string.none);
        if (mDoor.exit_button != null) {
            data = getString(R.string.input_port) + " " + mDoor.exit_button.index + "  " + mDoor.exit_button.getName();
        }
        mLayout.setExitBtnName(data);
        mLayout.setOpenDuration(mDoor.getOpenDuration(getString(R.string.minute), getString(R.string.sec)));
    }

}
