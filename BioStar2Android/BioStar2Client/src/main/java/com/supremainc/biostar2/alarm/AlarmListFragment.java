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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ListView;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.base.BaseAlarmAdapter;
import com.supremainc.biostar2.base.BaseFragment;
import com.supremainc.biostar2.base.BaseListCursorAdapter.OnGetCheckedItem;
import com.supremainc.biostar2.base.BaseListCursorAdapter.OnItemsListener;
import com.supremainc.biostar2.db.NotificationDBProvider;
import com.supremainc.biostar2.db.NotificationDBProvider.OnTaskFinish;
import com.supremainc.biostar2.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.sdk.datatype.DeviceData.BaseDevice;
import com.supremainc.biostar2.sdk.datatype.DoorData.BaseDoor;
import com.supremainc.biostar2.sdk.datatype.NotificationData.NotificationType;
import com.supremainc.biostar2.sdk.datatype.NotificationData.PushNotification;
import com.supremainc.biostar2.sdk.datatype.PermissionData.PERMISSION_MODULE;
import com.supremainc.biostar2.sdk.datatype.UserData.BaseUser;
import com.supremainc.biostar2.view.SubToolbar;
import com.supremainc.biostar2.widget.ScreenControl;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmListFragment extends BaseFragment {
    protected static final int MODE_DELETE = 1;
    private AlarmListFragmentLayout mLayout;
    private BaseAlarmAdapter mAlarmAdapter;
    private SubToolbar mSubToolbar;
    private NotificationDBProvider mNotificationDBProvider;
    private int mTotal = -1;

    private OnGetCheckedItem mOnGetCheckedItemIds = new OnGetCheckedItem() {
        @Override
        public void onReceive(ArrayList<Integer> selectedItem) {
            if (isInValidCheck(null)) {
                return;
            }
            if (selectedItem != null) {
                mNotificationDBProvider.delete(selectedItem, mOnTaskFinish, getActivity());
            } else {
                mPopup.dismissWiat();
            }
        }
    };
    private OnTaskFinish mOnTaskFinish = new OnTaskFinish() {
        @Override
        public void onDeleteFinish(int count) {
            if (isInValidCheck(null)) {
                return;
            }
            refresh();
            mPopup.dismissWiat();
            mToastPopup.show(getString(R.string.delete) + " " + count, null);
        }
    };
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mSubToolbar == null) {
                return;
            }
            if (mSubMode == MODE_DELETE) {
                mSubToolbar.setSelectAllViewOff();
                mSubToolbar.setSelectedCount(mAlarmAdapter.getCheckedItemCount());
            } else {
                ScreenControl screenControl = ScreenControl.getInstance();
                Bundle bundle = new Bundle();
                PushNotification item = (PushNotification) mAlarmAdapter.getItem(position);
                if (item == null) {
                    return;
                }
                item.unread = 0;
                mNotificationDBProvider.modify(item);
                mAlarmAdapter.onRequry();
                try {
                    bundle.putSerializable(PushNotification.TAG, item.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    return;
                }
                screenControl.addScreen(ScreenType.ALARM, bundle);
            }
        }
    };
    private OnItemsListener mOnItemsListener = new OnItemsListener() {
        @Override
        public void onSuccessNull() {
            mIsDataReceived = true;
            mToastPopup.show(getString(R.string.none_data), null);
        }

        @Override
        public void onTotalReceive(int total) {
            if (mTotal != total) {
                mSubToolbar.setTotal(total);
                mIsDataReceived = true;
                mTotal = total;
            }
        }
    };
    private SubToolbar.SubToolBarEvent mSubToolBarEvent = new SubToolbar.SubToolBarEvent() {
        @Override
        public void onClickSelectAll() {
            if (mSubToolbar.showReverseSelectAll()) {
                if (mAlarmAdapter != null) {
                    mAlarmAdapter.selectChoices();
                    mSubToolbar.setSelectedCount(mAlarmAdapter.getCheckedItemCount());
                }
            } else {
                if (mAlarmAdapter != null) {
                    mAlarmAdapter.clearChoices();
                    mSubToolbar.setSelectedCount(0);
                }
            }
        }
    };

    public AlarmListFragment() {
        super();
        setType(ScreenType.ALARM_LIST);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void deleteConfirm(int selectedCount) {
        mPopup.show(PopupType.ALERT, getString(R.string.delete_confirm_question), getString(R.string.selected_count) + " " + selectedCount, new OnPopupClickListener() {
            @Override
            public void OnNegative() {
            }

            @Override
            public void OnPositive() {
                deleteDo();
            }
        }, getString(R.string.ok), getString(R.string.cancel));
    }

    private void deleteDo() {
        int deleteCount = mAlarmAdapter.getCheckedItemCount();
        if (deleteCount == mTotal) {
            mNotificationDBProvider.deleteAll();
            refresh();
        } else {
            mPopup.showWait(false);
            mAlarmAdapter.getCheckedItemIds(mOnGetCheckedItemIds, false);
        }
    }

    private void initValue() {
        if (mNotificationDBProvider == null) {
            mNotificationDBProvider = NotificationDBProvider.getInstance(mContext.getApplicationContext());
        }
        if (mSubToolbar == null) {
            mSubToolbar = mLayout.getSubToolbar(mSubToolBarEvent);
        }
        if (mAlarmAdapter == null) {
            mAlarmAdapter = new BaseAlarmAdapter(mContext, mLayout.getListView(), mOnItemClickListener, mPopup, mOnItemsListener);
            mAlarmAdapter.setSwipyRefreshLayout(mLayout.getSwipeyLayout(), mLayout.getFab());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mIsDataReceived) {
            refresh();
            mIsDataReceived = true;
        }
    }

    @Override
    public void onDestroy() {
        if (mAlarmAdapter != null) {
            mAlarmAdapter.clearItems();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        if (mSubToolbar != null) {
            mSubToolbar.hideIme();
        }
        switch (item.getItemId()) {
            case R.id.action_delete_confirm:
                int selectedCount = mAlarmAdapter.getCheckedItemCount();
                if (selectedCount < 1) {
                    mToastPopup.show(getString(R.string.selected_none), null);
                    return true;
                }
                deleteConfirm(selectedCount);
                break;
            case R.id.action_delete:
                setSubMode(MODE_DELETE);
                break;
            case R.id.action_add:
                if (Setting.IS_FAKE_PUSH_DATA) {
                    mPopup.showWait(false);
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            long start = System.currentTimeMillis();
                            if (BuildConfig.DEBUG) {
                                Log.i(TAG, "start IS_FAKE_PUSH_DATA");
                            }
                            PushNotification noti = new PushNotification();
                            int total = mAlarmAdapter.getCount();
                            for (int i = 0; i < 1000; i++) {
                                int type = i % 8;
                                switch (type) {
                                    case 0:
                                        noti.code = NotificationType.ZONE_FIRE.mName;
                                        break;
                                    case 1:
                                        noti.code = NotificationType.DEVICE_REBOOT.mName;
                                        break;
                                    case 2:
                                        noti.code = NotificationType.DEVICE_RS485_DISCONNECT.mName;
                                        break;
                                    case 3:
                                        noti.code = NotificationType.DEVICE_TAMPERING.mName;
                                        break;
                                    case 4:
                                        noti.code = NotificationType.DOOR_FORCED_OPEN.mName;
                                        break;
                                    case 5:
                                        noti.code = NotificationType.DOOR_HELD_OPEN.mName;
                                        break;
                                    case 6:
                                        noti.code = NotificationType.DOOR_OPEN_REQUEST.mName;
                                        break;
                                    case 7:
                                        noti.code = NotificationType.ZONE_APB.mName;
                                        break;
                                }
                                noti.unread = 1;
                                noti.contact_phone_number = "010-2533-4246";
                                noti.door = new BaseDoor();
                                noti.door.id = "3";
                                noti.door.name = "BLN DOOR";
                                noti.user = new BaseUser();
                                noti.user.user_id = "1";
                                noti.user.name = "test";
                                noti.message = (total + i) + " message " + noti.code;
                                noti.title = (total + i) + " title: " + noti.code;
                                noti.device = new BaseDevice();
                                noti.device.id = "302039949";
                                noti.device.name = "testDevice";
                                noti.request_timestamp = mTimeConvertProvider.convertCalendarToServerTime(Calendar.getInstance(), true);
                                mNotificationDBProvider.insert(noti);
                            }
                            if (BuildConfig.DEBUG) {
                                Log.i(TAG, "end IS_FAKE_PUSH_DATA:" + ((System.currentTimeMillis() - start) / 1000));
                            }
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mAlarmAdapter != null) {
                                        mAlarmAdapter.onRequry();
                                        mAlarmAdapter.clearChoices();
                                        mAlarmAdapter.notifyDataSetChanged();
                                        if (mSubToolbar != null) {
                                            mSubToolbar.setSelectedCount(0);
                                        }
                                    }
                                    if (mPopup != null) {
                                        mPopup.dismissWiat();
                                    }
                                }
                            });
                            return null;
                        }
                    }.execute(null, null, null);

                }
                break;
            default:
                break;
        }
        return false;
    }

    protected void registerBroadcast() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (mIsDestroy) {
                        return;
                    }
                    final String action = intent.getAction();
                    if (mIsDestroy) {
                        return;
                    }
                    if (action.equals(Setting.BROADCAST_ALARM_UPDATE) || action.equals(Setting.BROADCAST_PREFRENCE_REFRESH)) {
                        if (isResumed()) {

                            if (mAlarmAdapter != null) {
                                if (mSubMode != MODE_DELETE) {
                                    refresh();
                                } else {
                                    mIsDataReceived = false;
                                }
                            }
                        } else {
                            mIsDataReceived = false;
                        }
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_ALARM_UPDATE);
            intentFilter.addAction(Setting.BROADCAST_PREFRENCE_REFRESH);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
    }

    @Override
    protected void setSubMode(int mode) {
        mSubMode = mode;
        switch (mode) {
            case MODE_NORMAL:
                mAlarmAdapter.setChoiceMode(ListView.CHOICE_MODE_NONE);
                mSubToolbar.showMultipleSelectInfo(false, 0);
                break;
            case MODE_DELETE:
                mAlarmAdapter.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                mSubToolbar.showMultipleSelectInfo(true, 0);
                break;
        }
        ActivityCompat.invalidateOptionsMenu(this.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mLayout == null) {
            mLayout = new AlarmListFragmentLayout(this, null);
        }
        View view = mLayout.initView(this, inflater, container, savedInstanceState);
        initBaseValue(mLayout);
        if (!mLayout.isReUsedView()) {
            initValue();
            initActionbar(getString(R.string.alarm));
        }
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = mContext.getMenuInflater();
        if (mPermissionDataProvider.getPermission(PERMISSION_MODULE.DOOR, true) || mPermissionDataProvider.getPermission(PERMISSION_MODULE.USER, true)) {
            switch (mSubMode) {
                default:
                case MODE_NORMAL:
                    initActionbar(getString(R.string.alarm));
                    if (Setting.IS_FAKE_PUSH_DATA) {
                        inflater.inflate(R.menu.add_delete, menu);
                        return;
                    }
                    inflater.inflate(R.menu.delete, menu);
                    break;
                case MODE_DELETE:
                    initActionbar(getString(R.string.delete) + " " + getString(R.string.alarm));
                    inflater.inflate(R.menu.delete_confirm, menu);
                    break;
            }
        } else {
            inflater.inflate(R.menu.menu, menu);
        }
        super.onPrepareOptionsMenu(menu);
    }

    private void refresh() {
        if (mAlarmAdapter == null || mSubToolbar == null) {
            return;
        }
        mAlarmAdapter.onRequry();
        mAlarmAdapter.clearChoices();
        mAlarmAdapter.notifyDataSetChanged();
        mSubToolbar.setSelectedCount(0);
    }
}
