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
package com.supremainc.biostar2.view;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.sdk.models.v2.common.VersionData;
import com.supremainc.biostar2.sdk.models.v2.device.BaseDevice;
import com.supremainc.biostar2.sdk.models.v2.device.ListDevice;
import com.supremainc.biostar2.sdk.models.v2.door.ListDoor;
import com.supremainc.biostar2.sdk.models.v2.eventlog.EventType;
import com.supremainc.biostar2.sdk.models.v2.eventlog.Query;
import com.supremainc.biostar2.sdk.models.v2.user.ListUser;
import com.supremainc.biostar2.sdk.provider.DateTimeDataProvider;
import com.supremainc.biostar2.widget.DateTimePicker;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;
import com.supremainc.biostar2.widget.popup.SelectCustomData;
import com.supremainc.biostar2.widget.popup.SelectPopup;
import com.supremainc.biostar2.widget.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.widget.popup.SelectPopup.SelectType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class FilterView extends BaseView {
    FragmentActivity mActivity;
    private DateTimeDataProvider mTimeConvertProvider;
    private Calendar mCalendar;
    private StyledTextView mDateEnd;
    private StyledTextView mDateStart;
    private DateTimePicker mDateTimePicker;
    private StyledTextView mDeviceMoreView;
    private StyledTextView mDevicePlusView;
    private StyledTextView mDeviceView;
    private StyledTextView mTimeStart;
    private StyledTextView mTimeEnd;
    private ArrayList<BaseDevice> mDevices;
    private ArrayList<ListDoor> mDoors;
    private StyledTextView mUserMoreView;
    private StyledTextView mUserPlusView;
    private StyledTextView mUserView;
    private StyledTextView mDoorMoreView;
    private StyledTextView mDoorPlusView;
    private StyledTextView mDoorView;
    private ArrayList<ListUser> mUsers;
    private int mEndDay;
    private int mEndHour;
    private int mEndMinute;
    private int mEndMonth;
    private int mEndYear;
    private StyledTextView mEventMoreView;
    private StyledTextView mEventPlusView;
    private ArrayList<EventType> mEventTypes;
    private StyledTextView mEventView;
    private DecimalFormat mFormat;
    private Popup mPopup;
    private SelectPopup<ListDoor> mSelectDoorPopup;
    private SelectPopup<ListDevice> mSelectDevicePopup;
    private SelectPopup<EventType> mSelectEventPopup;
    private SelectPopup<ListUser> mSelectUserPopup;
    private int mStartDay;
    private int mStartHour;
    private int mStartMinute;
    private int mStartMonth;
    private int mStartYear;
    private OnDateSetListener mStartDateListener = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (mDateTimePicker.isErrorSetDate(year, monthOfYear, dayOfMonth, mEndYear, mEndMonth, mEndDay)) {
                mPopup.show(PopupType.ALERT, mContext.getString(R.string.info), mActivity.getString(R.string.error_set_date), null, null, null);
                return;
            }
            if (mDateTimePicker.isErrorSetDate(mStartHour, mStartMinute, 0, mEndHour, mEndMinute, 0) && year == mEndYear && monthOfYear == mEndMonth && dayOfMonth == mEndDay) {
                mPopup.show(PopupType.ALERT, mContext.getString(R.string.info), mActivity.getString(R.string.error_set_date), null, null, null);
                return;
            }
            mStartYear = year;
            mStartMonth = monthOfYear;
            mStartDay = dayOfMonth;
            mDateStart.setText(mDateTimePicker.getDateString(mStartYear, mStartMonth, mStartDay));
        }
    };
    private OnDateSetListener mEndDateListener = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (mDateTimePicker.isErrorSetDate(mStartYear, mStartMonth, mStartDay, year, monthOfYear, dayOfMonth)) {
                mPopup.show(PopupType.ALERT, mContext.getString(R.string.info), mActivity.getString(R.string.error_set_date), null, null, null);
                return;
            }
            if (mDateTimePicker.isErrorSetDate(mStartHour, mStartMinute, 0, mEndHour, mEndMinute, 0) && mStartYear == year && mStartMonth == monthOfYear && mStartDay == dayOfMonth) {
                mPopup.show(PopupType.ALERT, mContext.getString(R.string.info), mActivity.getString(R.string.error_set_date), null, null, null);
                return;
            }
            mEndYear = year;
            mEndMonth = monthOfYear;
            mEndDay = dayOfMonth;
            mDateEnd.setText(mDateTimePicker.getDateString(mEndYear, mEndMonth, mEndDay));
        }
    };

    private OnTimeSetListener mEndTimeListener = new OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (mDateTimePicker.isErrorSetDate(mStartHour, mStartMinute, 0, hourOfDay, minute, 0) && mStartYear == mEndYear && mStartMonth == mEndMonth && mStartDay == mEndDay) {
                mPopup.show(PopupType.ALERT, mContext.getString(R.string.info), mActivity.getString(R.string.error_set_date), null, null, null);
                return;
            }
            mEndHour = hourOfDay;
            mEndMinute = minute;
            mTimeEnd.setText(mFormat.format(hourOfDay) + " : " + mFormat.format(minute));
        }
    };

    private OnTimeSetListener mStartTimeListener = new OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (mDateTimePicker.isErrorSetDate(hourOfDay, minute, 0, mEndHour, mEndMinute, 0) && mStartYear == mEndYear && mStartMonth == mEndMonth && mStartDay == mEndDay) {
                mPopup.show(PopupType.ALERT, mContext.getString(R.string.info), mActivity.getString(R.string.error_set_date), null, null, null);
                return;
            }
            mStartHour = hourOfDay;
            mStartMinute = minute;
            mTimeStart.setText(mFormat.format(hourOfDay) + " : " + mFormat.format(minute));
        }
    };

    OnSingleClickListener mButtonClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()) {
                case R.id.filter_date_edit: {
                    selectDatePicker();
                    break;
                }
                case R.id.filter_date_start: {
                    mDateTimePicker.showDatePicker(mStartDateListener, mStartYear, mStartMonth, mStartDay);
                    break;
                }
                case R.id.filter_date_end: {
                    mDateTimePicker.showDatePicker(mEndDateListener, mEndYear, mEndMonth, mEndDay);
                    break;
                }
                case R.id.filter_time_edit: {
                    selectTimePicker();
                    break;
                }
                case R.id.filter_time_start: {
                    mDateTimePicker.showTimePicker(mStartTimeListener, mStartHour, mStartMinute);
                    break;
                }
                case R.id.filter_time_end: {
                    mDateTimePicker.showTimePicker(mEndTimeListener, mEndHour, mEndMinute);
                    break;
                }
                case R.id.filter_user_edit: {
                    selectUser();
                    break;
                }
                case R.id.filter_device_edit: {
                    selectDevice();
                    break;
                }
                case R.id.filter_door_edit: {
                    selectDoor();
                    break;
                }
                case R.id.filter_event_edit: {
                    selectEvent();
                    break;
                }

            }
        }
    };

    public FilterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public FilterView(Context context) {
        super(context);
        initView(context);
    }

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void init(FragmentActivity activity, Popup popup) {
        mActivity = activity;
        mPopup = popup;
        mDateTimePicker = new DateTimePicker(mActivity);
        mTimeConvertProvider = DateTimeDataProvider.getInstance(mActivity);
        initDateTime();
        mSelectDevicePopup = new SelectPopup<ListDevice>(mActivity, mPopup);
        mSelectUserPopup = new SelectPopup<ListUser>(mActivity, mPopup);
        mSelectEventPopup = new SelectPopup<EventType>(mActivity, mPopup);
        mSelectDoorPopup = new SelectPopup<ListDoor>(mActivity, mPopup);
    }

    private void initView(Context context) {
        mInflater.inflate(R.layout.view_filter, this, true);
        setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
            }
        });

        mFormat = new DecimalFormat("00");

        mDateStart = (StyledTextView) findViewById(R.id.filter_date_start);
        mDateEnd = (StyledTextView) findViewById(R.id.filter_date_end);
        mTimeStart = (StyledTextView) findViewById(R.id.filter_time_start);
        mTimeEnd = (StyledTextView) findViewById(R.id.filter_time_end);

        mUserView = (StyledTextView) findViewById(R.id.filter_user);
        mUserMoreView = (StyledTextView) findViewById(R.id.filter_user_more);
        mUserPlusView = (StyledTextView) findViewById(R.id.filter_user_plus);
        mDeviceView = (StyledTextView) findViewById(R.id.filter_device);
        mDeviceMoreView = (StyledTextView) findViewById(R.id.filter_device_more);
        mDevicePlusView = (StyledTextView) findViewById(R.id.filter_device_plus);
        mDoorView = (StyledTextView) findViewById(R.id.filter_door);
        mDoorMoreView = (StyledTextView) findViewById(R.id.filter_door_more);
        mDoorPlusView = (StyledTextView) findViewById(R.id.filter_door_plus);
        mEventView = (StyledTextView) findViewById(R.id.filter_event);
        mEventMoreView = (StyledTextView) findViewById(R.id.filter_event_more);
        mEventPlusView = (StyledTextView) findViewById(R.id.filter_event_plus);


        int[] ids = {R.id.filter_date_edit, R.id.filter_time_edit, R.id.filter_user_edit, R.id.filter_door_edit, R.id.filter_device_edit, R.id.filter_event_edit};
        for (int i : ids) {
            findViewById(i).setOnClickListener(mButtonClickListener);
        }

        View[] views = {mDateStart, mDateEnd, mTimeStart, mTimeEnd};
        for (View v : views) {
            v.setOnClickListener(mButtonClickListener);
        }
    }


    public void setDefault() {
        initDateTime();
        mUserView.setText(mContext.getString(R.string.all_users));
        mUserMoreView.setVisibility(View.GONE);
        mUserPlusView.setVisibility(View.GONE);
        mEventView.setText(mContext.getString(R.string.all_events));
        mEventMoreView.setVisibility(View.GONE);
        mEventPlusView.setVisibility(View.GONE);
        mDeviceView.setText(mContext.getString(R.string.all_devices));
        mDeviceMoreView.setVisibility(View.GONE);
        mDevicePlusView.setVisibility(View.GONE);
        mDoorView.setText(mContext.getString(R.string.none));
        mDoorMoreView.setVisibility(View.GONE);
        mDoorPlusView.setVisibility(View.GONE);
        if (mDevices != null) {
            mDevices.clear();
        }
        mDevices = null;
        if (mDoors != null) {
            mDoors.clear();
        }
        mDoors = null;
        if (mEventTypes != null) {
            mEventTypes.clear();
        }
        if (mEventTypes != null) {
            mEventTypes.clear();
        }
        mEventTypes = null;
        mUsers = null;
    }

    private void initDateTime() {
        mCalendar = Calendar.getInstance();
        mEndYear = mStartYear = mCalendar.get(Calendar.YEAR);
        mEndMonth = mStartMonth = mCalendar.get(Calendar.MONTH);
        mEndDay = mStartDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mDateStart.setText(mDateTimePicker.getDateString(mStartYear, mStartMonth, mStartDay));
        mDateEnd.setText(mDateTimePicker.getDateString(mEndYear, mEndMonth, mEndDay));

        mStartHour = 0;
        mStartMinute = 0;
        mEndHour = 23;
        mEndMinute = 59;
        mStartTimeListener.onTimeSet(null, mStartHour, mStartMinute);
        mEndTimeListener.onTimeSet(null, mEndHour, mEndMinute);
    }

    public int getDeviceCount() {
        if (mDevices != null && mDevices.size() > 0) {
            return mDevices.size();
        }
        return 0;
    }

    public int getDoorCount() {
        if (mDoors != null && mDoors.size() > 0) {
            return mDoors.size();
        }
        return 0;
    }

    public int getEventCount() {
        if (mEventTypes != null && mEventTypes.size() > 0) {
            return mEventTypes.size();
        }
        return 0;
    }

    public Query getQuery() {
        Query query = new Query();
        Calendar start = Calendar.getInstance();
        start.set(Calendar.YEAR, mStartYear);
        start.set(Calendar.MONTH, mStartMonth);
        start.set(Calendar.DAY_OF_MONTH, mStartDay);
        start.set(Calendar.HOUR_OF_DAY, mStartHour);
        start.set(Calendar.MINUTE, mStartMinute);
        Calendar end = Calendar.getInstance();
        end.set(Calendar.YEAR, mEndYear);
        end.set(Calendar.MONTH, mEndMonth);
        end.set(Calendar.DAY_OF_MONTH, mEndDay);
        end.set(Calendar.HOUR_OF_DAY, mEndHour);
        end.set(Calendar.MINUTE, mEndMinute);

        query.setTimeCalendar(mTimeConvertProvider, Query.QueryTimeType.start_datetime, start);
        query.setTimeCalendar(mTimeConvertProvider, Query.QueryTimeType.end_datetime, end);
        HashMap<String, String> mapDeviceID = new HashMap<String, String>();
        if (mDevices != null && mDevices.size() > 0) {
            for (BaseDevice device : mDevices) {
                mapDeviceID.put(device.id, device.id);
            }
        }
        if (mDoors != null && mDoors.size() > 0) {
            for (ListDoor door : mDoors) {
                if (door.door_relay != null && door.door_relay.device != null && door.door_relay.device.id != null) {
                    mapDeviceID.put(door.door_relay.device.id, door.door_relay.device.id);
                }
                if (door.entry_device != null && door.entry_device.id != null) {
                    mapDeviceID.put(door.entry_device.id, door.entry_device.id);
                }
                if (door.exit_device != null && door.exit_device.id != null) {
                    mapDeviceID.put(door.exit_device.id, door.exit_device.id);
                }
                if (door.door_sensor != null && door.door_sensor.device != null && door.door_sensor.device.id != null) {
                    mapDeviceID.put(door.door_sensor.device.id, door.door_sensor.device.id);
                }
                if (door.exit_button != null && door.exit_button.device != null && door.exit_button.device.id != null) {
                    mapDeviceID.put(door.exit_button.device.id, door.exit_button.device.id);
                }
            }

        }
        if (mUsers != null && mUsers.size() > 0) {
            ArrayList<String> usersId = new ArrayList<String>();
            for (ListUser user : mUsers) {
                usersId.add(String.valueOf(user.user_id));
            }
            query.user_id = usersId;
        }
        if (mEventTypes != null && mEventTypes.size() > 0) {
            ArrayList<String> eventId = new ArrayList<String>();
            for (EventType eventType : mEventTypes) {
                eventId.add(String.valueOf(eventType.code));
            }
            query.event_type_code = eventId;
        }
        query.limit = 100;
        if (mapDeviceID.size() > 0) {
            if (query.device_id == null) {
                query.device_id = new ArrayList<String>();
            }
            for (String key : mapDeviceID.keySet()) {
                query.device_id.add(key);
            }
        }
        mapDeviceID.clear();
        return query;
    }

    public String getUser() {
        return mUserView.toString2();
    }

    public int getUserCount() {
        if (mUsers != null && mUsers.size() > 0) {
            return mUsers.size();
        }
        return 0;
    }


    public boolean onSearch(String query) {
        if (getVisibility() != View.VISIBLE) {
            return false;
        }
        if (mSelectUserPopup != null && mSelectUserPopup.isExpand()) {
            return mSelectUserPopup.onSearch(query);
        }
        if (mSelectDevicePopup != null && mSelectDevicePopup.isExpand()) {
            return mSelectDevicePopup.onSearch(query);
        }
        if (mSelectEventPopup != null && mSelectEventPopup.isExpand()) {
            return mSelectEventPopup.onSearch(query);
        }
        if (mSelectDoorPopup != null && mSelectDoorPopup.isExpand()) {
            return mSelectDoorPopup.onSearch(query);
        }
        return false;
    }


    private void selectDatePicker() {
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mActivity, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        linkType.add(new SelectCustomData(mContext.getString(R.string.start_date), 1, false));
        linkType.add(new SelectCustomData(mContext.getString(R.string.end_date), 2, false));
        selectPopup.show(SelectType.CUSTOM, new OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem, boolean isPositive) {
                if (selectedItem == null) {
                    return;
                }
                switch (selectedItem.get(0).getIntId()) {
                    case 1: {
                        mDateTimePicker.showDatePicker(mStartDateListener, mStartYear, mStartMonth, mStartDay);
                        break;
                    }
                    case 2: {
                        mDateTimePicker.showDatePicker(mEndDateListener, mEndYear, mEndMonth, mEndDay);
                        break;
                    }
                    default:
                        break;
                }
            }
        }, linkType, mContext.getString(R.string.select_link), false, false);
    }

    private void selectDevice() {
        mSelectDevicePopup.show(SelectType.DEVICE, new OnSelectResultListener<ListDevice>() {
            @Override
            public void OnResult(ArrayList<ListDevice> selectedItem, boolean isPositive) {
                if (selectedItem == null) {
                    return;
                }
                setDeviceResult((ArrayList<BaseDevice>) selectedItem.clone(), false);
            }
        }, null, mContext.getString(R.string.select_device_orginal), true, true);
    }

    private void selectDoor() {
        mSelectDoorPopup.show(SelectType.DOOR, new OnSelectResultListener<ListDoor>() {
            @Override
            public void OnResult(ArrayList<ListDoor> selectedItem, boolean isPositive) {
                if (selectedItem == null) {
                    return;
                }
                setDoorResult((ArrayList<ListDoor>) selectedItem.clone(), false);
            }
        }, null, mContext.getString(R.string.door), true, true);
    }

    private void selectEvent() {
        mSelectEventPopup.show(SelectType.EVENT_TYPE, new OnSelectResultListener<EventType>() {
            @Override
            public void OnResult(ArrayList<EventType> selectedItem, boolean isPositive) {
                if (selectedItem == null) {
                    return;
                }
                setEventResult((ArrayList<EventType>) selectedItem.clone());
            }
        }, null, mContext.getString(R.string.select_event), true, true);
    }

    private void selectTimePicker() {
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mActivity, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        linkType.add(new SelectCustomData(mContext.getString(R.string.start_time), 1, false));
        linkType.add(new SelectCustomData(mContext.getString(R.string.end_time), 2, false));
        selectPopup.show(SelectType.CUSTOM, new OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem, boolean isPositive) {
                if (selectedItem == null) {
                    return;
                }
                switch (selectedItem.get(0).getIntId()) {
                    case 1: {
                        mDateTimePicker.showTimePicker(mStartTimeListener, mStartHour, mStartMinute);
                        break;
                    }
                    case 2: {
                        mDateTimePicker.showTimePicker(mEndTimeListener, mEndHour, mEndMinute);
                        break;
                    }
                    default:
                        break;
                }
            }
        }, linkType, mContext.getString(R.string.select_link), false, false);
    }

    private void selectUser() {
        mSelectUserPopup.show(SelectType.USER, new OnSelectResultListener<ListUser>() {
            @Override
            public void OnResult(ArrayList<ListUser> selectedItem, boolean isPositive) {
                if (selectedItem == null) {
                    return;
                }
                setUserResult((ArrayList<ListUser>) selectedItem.clone());
            }
        }, null, mContext.getString(R.string.select_user_original), true, true);
    }

    public void setDeviceResult(ArrayList<BaseDevice> selectedItem, boolean none) {
        mDevices = selectedItem;
        if (none) {
            mDevices = null;
            mDeviceView.setText(mContext.getString(R.string.none));
            mDeviceMoreView.setVisibility(View.GONE);
            mDevicePlusView.setVisibility(View.GONE);
            return;
        }
        if (mDevices == null || mDevices.size() < 1) {
            return;
        }
        setDoorResult(null, true);
        int size = mDevices.size();
        String name = mDevices.get(0).getName() + " / " + mDevices.get(0).id;
        if (mDevices.get(0).name != null && !mDevices.get(0).name.isEmpty()) {
            name = mDevices.get(0).name;
        }
        mDeviceView.setText(name);
        if (size > 1) {
            mDeviceMoreView.setText(String.valueOf((size - 1)));
            mDeviceMoreView.setVisibility(View.VISIBLE);
            mDevicePlusView.setVisibility(View.VISIBLE);
        } else if (size > 0) {
            mDeviceMoreView.setVisibility(View.GONE);
            mDevicePlusView.setVisibility(View.GONE);
        }
    }

    public void setDoorResult(ArrayList<ListDoor> selectedItem, boolean none) {
        mDoors = selectedItem;
        if (none) {
            mDoors = null;
            mDoorView.setText(mContext.getString(R.string.none));
            mDoorMoreView.setVisibility(View.GONE);
            mDoorPlusView.setVisibility(View.GONE);
            return;
        }
        if (mDoors == null || mDoors.size() < 1) {
            return;
        }
        setDeviceResult(null, true);
        int size = mDoors.size();
        String name = mDoors.get(0).getName() + " / " + mDoors.get(0).id;
        if (mDoors.get(0).name != null && !mDoors.get(0).name.isEmpty()) {
            name = mDoors.get(0).name;
        }
        mDoorView.setText(name);
        if (size > 1) {
            mDoorMoreView.setText(String.valueOf((size - 1)));
            mDoorMoreView.setVisibility(View.VISIBLE);
            mDoorPlusView.setVisibility(View.VISIBLE);
        } else if (size > 0) {
            mDoorMoreView.setVisibility(View.GONE);
            mDoorPlusView.setVisibility(View.GONE);
        }
    }

    public void setEventResult(ArrayList<EventType> selectedItem) {
        mEventTypes = selectedItem;
        if (mEventTypes == null || mEventTypes.size() < 1) {
            return;
        }
        int size = mEventTypes.size();
        if (VersionData.getCloudVersion(mContext) > 1) {
            mEventView.setText(mEventTypes.get(0).description);
        } else {
            mEventView.setText(mEventTypes.get(0).name);
        }
        if (size > 1) {
            mEventMoreView.setText(String.valueOf((size - 1)));
            mEventMoreView.setVisibility(View.VISIBLE);
            mEventPlusView.setVisibility(View.VISIBLE);
        } else if (size > 0) {
            mEventMoreView.setVisibility(View.GONE);
            mEventPlusView.setVisibility(View.GONE);
        }
    }

    public void setUserResult(ArrayList<ListUser> selectedItem) {
        mUsers = selectedItem;
        if (mUsers == null || mUsers.size() < 1) {
            return;
        }
        int size = mUsers.size();
        String name = mUsers.get(0).getName() + " / " + mUsers.get(0).user_id;
        if (mUsers.get(0).name != null && !mUsers.get(0).name.isEmpty()) {
            name = mUsers.get(0).name;
        }
        mUserView.setText(name);
        if (size > 1) {
            mUserMoreView.setText(String.valueOf((size - 1)));
            mUserMoreView.setVisibility(View.VISIBLE);
            mUserPlusView.setVisibility(View.VISIBLE);
        } else if (size > 0) {
            mUserMoreView.setVisibility(View.GONE);
            mUserPlusView.setVisibility(View.GONE);
        }
    }
}
