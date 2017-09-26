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
import android.view.View;
import android.view.ViewGroup;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.adapter.DetailAdapter;
import com.supremainc.biostar2.datatype.DoorDetailData;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.models.v2.door.Door;
import com.supremainc.biostar2.sdk.models.v2.permission.PermissionModule;
import com.supremainc.biostar2.sdk.provider.DateTimeDataProvider;
import com.supremainc.biostar2.view.SummaryDoorView;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.SelectCustomData;
import com.supremainc.biostar2.widget.popup.SelectPopup;
import com.supremainc.biostar2.widget.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.widget.popup.SelectPopup.SelectType;
import com.supremainc.biostar2.widget.popup.ToastPopup;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoorFragment extends BaseFragment {
    private SummaryDoorView mSummaryDoorView;
    private ArrayList<DoorDetailData.DoorDetail> mListData;
    private DetailAdapter mDetailAdapter;
    private Door mDoor;
    private String mControlTitle = "";

    private Callback<ResponseStatus> mRequestDoorListener = new Callback<ResponseStatus>() {
        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            String date = mDateTimeDataProvider.convertCalendarToFormatter(Calendar.getInstance(), DateTimeDataProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);
            mPopup.show(Popup.PopupType.DOOR, getString(R.string.fail)+". "+ getString(R.string.request_open), mDoor.name + " / " + mDoor.id + "\n" + date+ "\n"+t.getMessage(), null, null, null);
        }

        @Override
        public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, false, false)) {
                String date = mDateTimeDataProvider.convertCalendarToFormatter(Calendar.getInstance(), DateTimeDataProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);
                mPopup.show(Popup.PopupType.DOOR, getString(R.string.fail)+". "+ getString(R.string.request_open), mDoor.name + " / " + mDoor.id + "\n" + date+ "\n"+getResponseErrorMessage(response), null, null, null);
                return;
            }
            String date = mDateTimeDataProvider.convertCalendarToFormatter(Calendar.getInstance(), DateTimeDataProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);
            mPopup.show(Popup.PopupType.DOOR, getString(R.string.request_open), mDoor.name + " / " + mDoor.id + "\n" + date, null, null, null);
        }
    };

    private Callback<ResponseStatus> mControlListener = new Callback<ResponseStatus>() {
        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            String date = mDateTimeDataProvider.convertCalendarToFormatter(Calendar.getInstance(), DateTimeDataProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);
            String body = t.getMessage();
            if (body != null && !body.isEmpty()) {
                body = mDoor.name + " / " + mDoor.id + "\n" + date + "\n" + body;
            } else {
                body = mDoor.name + " / " + mDoor.id + "\n" + date + "\n";
            }
            mPopup.show(Popup.PopupType.DOOR, getString(R.string.fail) + ". " + mControlTitle, body, null, null, null);
        }

        @Override
        public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true,false)) {
                return;
            }
            String date = mDateTimeDataProvider.convertCalendarToFormatter(Calendar.getInstance(), DateTimeDataProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);
            mPopup.show(Popup.PopupType.DOOR, mControlTitle, mDoor.name + " / " + mDoor.id + "\n" + date, null, null, null);
        }
    };

    private SummaryDoorView.SummaryDoorViewListener mSummaryDoorViewListener = new SummaryDoorView.SummaryDoorViewListener() {
        @Override
        public void onDoorAction() {
            if (mPermissionDataProvider.getPermission(PermissionModule.DOOR, true) || (mPermissionDataProvider.getPermission(PermissionModule.MONITORING, true) && mPermissionDataProvider
                    .getPermission(PermissionModule.DOOR, false))) {
                openMenu();
            } else {
                sendOpenRequest();
            }
        }

        @Override
        public void onStatus() {
            if (mDoor.status != null) {
                if (mDoor.status.forced_open) {
                    mToastPopup.show(ToastPopup.TYPE_DOOR, getString(R.string.forced_open), null);
                } else if (mDoor.status.held_opened) {
                    mToastPopup.show(ToastPopup.TYPE_DOOR, getString(R.string.held_opened), null);
                } else if (mDoor.status.disconnected) {
                    mToastPopup.show(ToastPopup.TYPE_DOOR, getString(R.string.disconnected), null);
                } else if (mDoor.status.unlocked || mDoor.status.operatorUnlocked || mDoor.status.emergencyUnlocked || mDoor.status.scheduleUnlocked) {
                    mToastPopup.show(ToastPopup.TYPE_DOOR, getString(R.string.manual_unlock), null);
                } else if (mDoor.status.locked || mDoor.status.operatorLocked || mDoor.status.emergencyLocked || mDoor.status.scheduleLocked) {
                    mToastPopup.show(ToastPopup.TYPE_DOOR, getString(R.string.manual_lock), null);
                }
            }
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
                mPopup.showWait(mCancelExitListener);
                mControlTitle = getString(R.string.door_is_open);
                request(mDoorDataProvider.openDoor(mDoor.id, mControlListener));
                break;
            case R.id.action_lock:
                mPopup.showWait(mCancelExitListener);
                mControlTitle = getString(R.string.manual_lock);
                request(mDoorDataProvider.lockDoor(mDoor.id, mControlListener));
                break;
            case R.id.action_unlock:
                mPopup.showWait(mCancelExitListener);
                mControlTitle = getString(R.string.manual_unlock);
                request(mDoorDataProvider.unlockDoor(mDoor.id, mControlListener));
                break;
            case R.id.action_release:
                mPopup.showWait(mCancelExitListener);
                mControlTitle = getString(R.string.release);
                request(mDoorDataProvider.releaseDoor(mDoor.id, mControlListener));
                break;
            case R.id.action_clear_apb:
                mPopup.showWait(mCancelExitListener);
                mControlTitle = getString(R.string.clear_apb);
                request(mDoorDataProvider.clearAntiPassback(mDoor.id, mControlListener));
                break;
            case R.id.action_clear_alarm:
                mPopup.showWait(mCancelExitListener);
                mControlTitle = getString(R.string.clear_alarm);
                request(mDoorDataProvider.clearAlarm(mDoor.id, mControlListener));
                break;
            default:
                break;
        }
    }

    private void applyPermission() {
        if (mSummaryDoorView != null) {
            if (mPermissionDataProvider.getPermission(PermissionModule.DOOR, true)) {
                mSummaryDoorView.setActionButtonName(getString(R.string.door_control));
            } else if (mPermissionDataProvider.getPermission(PermissionModule.DOOR, false) && mPermissionDataProvider.getPermission(PermissionModule.MONITORING, true)) {
                mSummaryDoorView.setActionButtonName(getString(R.string.door_control));
            } else {
                mSummaryDoorView.setActionButtonName(getString(R.string.request_open));
            }
        }
    }

    private boolean initValue(Bundle savedInstanceState) {
        mDoor = getExtraData(Door.TAG, savedInstanceState);
        if (mDoor == null) {
            return false;
        }
        if (mSummaryDoorView == null) {
            mSummaryDoorView = (SummaryDoorView) mRootView.findViewById(R.id.summary_door);
            mSummaryDoorView.init(mSummaryDoorViewListener);
        }
        if (mListData == null) {
            mListData = new ArrayList<DoorDetailData.DoorDetail>();
        } else {
            mListData.clear();
        }
        if (mDetailAdapter != null) {
            mDetailAdapter.notifyDataSetChanged();
        }
        if (mDetailAdapter == null) {
            mDetailAdapter = new DetailAdapter(mActivity, null, getListView(), null, mPopup, null);
        }
        applyPermission();
        setView();
        mDetailAdapter.setData(mListData);
        mDetailAdapter.notifyDataSetChanged();
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
        setResID(R.layout.fragment_door);
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
            initActionbar(mDoor.name);
            mRootView.invalidate();
        }
        return mRootView;
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
        if (Setting.IS_TEST_OPEN_DOOR_REQUEST) {
            linkType.add(new SelectCustomData(getString(R.string.request_open), -1, false));
        }
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

    private void sendOpenRequest() {
        String PhoneNumber = mDoorDataProvider.getLoginUserInfo().phone_number;
        mPopup.showWait(true);
        request(mDoorDataProvider.openRequestDoor(mDoor.id, PhoneNumber, mRequestDoorListener));
    }

    private void setDoorIcon() {
        if (mDoor != null && mDoor.status != null) {
            if (mDoor.status != null) {
                if (mDoor.status.forced_open) {
                    mSummaryDoorView.setIcon(R.drawable.door_ic_2);
                } else if (mDoor.status.held_opened) {
                    mSummaryDoorView.setIcon(R.drawable.door_ic_1);
                } else if (mDoor.status.disconnected) {
                    mSummaryDoorView.setIcon(R.drawable.door_ic_2);
                } else if (mDoor.status.unlocked) {
                    mSummaryDoorView.setIcon(R.drawable.door_ic_1);
                } else if (mDoor.status.locked) {
                    mSummaryDoorView.setIcon(R.drawable.door_ic_1);
                } else if (mDoor.status.scheduleLocked) {
                    mSummaryDoorView.setIcon(R.drawable.door_ic_1);
                } else if (mDoor.status.scheduleUnlocked) {
                    mSummaryDoorView.setIcon(R.drawable.door_ic_1);
                } else if (mDoor.status.emergencyLocked) {
                    mSummaryDoorView.setIcon(R.drawable.door_ic_2);
                } else if (mDoor.status.emergencyUnlocked) {
                    mSummaryDoorView.setIcon(R.drawable.door_ic_2);
                } else if (mDoor.status.operatorLocked) {
                    mSummaryDoorView.setIcon(R.drawable.door_ic_1);
                } else if (mDoor.status.operatorUnlocked) {
                    mSummaryDoorView.setIcon(R.drawable.door_ic_1);
                } else {
                    mSummaryDoorView.setIcon(R.drawable.door_ic_3);
                }
            }
        }
    }

    private void setView() {
        mSummaryDoorView.setTitle(mDoor.name);
        mSummaryDoorView.setContent(mDoor.description);
        setDoorIcon();
        String data = getString(R.string.none);
        if (mDoor.entry_device != null) {
            data = mDoor.entry_device.getName();
        }
        mListData.add(new DoorDetailData.DoorDetail(getString(R.string.entry_device), data, false, DoorDetailData.DoorDetailType.ENTRY_DEVICE));

        if (mDoor.exit_device != null) {
            data = mDoor.exit_device.getName();
        } else {
            data = getString(R.string.none);
        }
        mListData.add(new DoorDetailData.DoorDetail(getString(R.string.exit_device), data, false, DoorDetailData.DoorDetailType.EXIT_DEVICE));

        if (mDoor.door_relay != null) {
            data = getString(R.string.relay) + " " + mDoor.door_relay.index + "  " + mDoor.door_relay.getName();
        } else {
            data = getString(R.string.none);
        }
        mListData.add(new DoorDetailData.DoorDetail(getString(R.string.door_relay), data, false, DoorDetailData.DoorDetailType.RELAY));

        if (mDoor.exit_button != null) {
            data = getString(R.string.input_port) + " " + mDoor.exit_button.index + "  " + mDoor.exit_button.getName();
        } else {
            data = getString(R.string.none);
        }
        mListData.add(new DoorDetailData.DoorDetail(getString(R.string.exit_button), data, false, DoorDetailData.DoorDetailType.EXIT_BUTTON));

        if (mDoor.door_sensor != null) {
            data = getString(R.string.input_port) + " " + mDoor.door_sensor.index + "  " + mDoor.door_sensor.getName();
        } else {
            data = getString(R.string.none);
        }
        mListData.add(new DoorDetailData.DoorDetail(getString(R.string.door_sensor), data, false, DoorDetailData.DoorDetailType.RELAY));

        data = mDoor.getOpenDuration(getString(R.string.minute), getString(R.string.sec));
        mListData.add(new DoorDetailData.DoorDetail(getString(R.string.open_time), data, false, DoorDetailData.DoorDetailType.OPEN_TIME));
        if (mDetailAdapter != null) {
            mDetailAdapter.notifyDataSetChanged();
        }
    }

}
