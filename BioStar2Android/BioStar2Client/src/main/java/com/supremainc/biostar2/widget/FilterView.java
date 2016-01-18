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
package com.supremainc.biostar2.widget;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.popup.SelectCustomData;
import com.supremainc.biostar2.popup.SelectPopup;
import com.supremainc.biostar2.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.popup.SelectPopup.SelectType;
import com.supremainc.biostar2.sdk.datatype.DeviceData.BaseDevice;
import com.supremainc.biostar2.sdk.datatype.DeviceData.ListDevice;
import com.supremainc.biostar2.sdk.datatype.EventLogData.EventType;
import com.supremainc.biostar2.sdk.datatype.QueryData.Query;
import com.supremainc.biostar2.sdk.datatype.UserData.ListUser;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

@SuppressLint("InflateParams")
public class FilterView {
    FragmentActivity mActivity;
    Fragment mContext;
    TimeConvertProvider mTimeConvertProvider;
    boolean mIsExapnd = false;
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
    private StyledTextView mUserMoreView;
    private StyledTextView mUserPlusView;
    private StyledTextView mUserView;
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
    private View mFilterView;
    private DecimalFormat mFormat;
    private ViewGroup mParentView;
    private Popup mPopup;
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
            mEndYear = year;
            mEndMonth = monthOfYear;
            mEndDay = dayOfMonth;
            mDateEnd.setText(mDateTimePicker.getDateString(mEndYear, mEndMonth, mEndDay));
        }
    };

    private OnTimeSetListener mEndTimeListener = new OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (mDateTimePicker.isErrorSetDate(mStartHour, mStartMinute, 0, hourOfDay, minute, 0) && mStartYear == mEndYear && mStartMonth == mEndMonth && mStartDay ==  mEndDay) {
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
            if (mDateTimePicker.isErrorSetDate(hourOfDay, minute, 0, mEndHour, mEndMinute, 0) && mStartYear == mEndYear && mStartMonth == mEndMonth && mStartDay ==  mEndDay) {
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
                case R.id.filter_event_edit: {
                    selectEvent();
                    break;
                }

            }
        }
    };

    // TODO피쳐별로 활성화 되도록 수정한다.
    public FilterView(LayoutInflater inflater, Fragment fragment, ViewGroup parentView, Popup popup) {
        mFilterView = (LinearLayout) inflater.inflate(R.layout.view_filter, null);
        mFilterView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
            }
        });
        this.mParentView = parentView;
        this.mContext = fragment;
        this.mActivity = fragment.getActivity();
        mDateTimePicker = new DateTimePicker(mActivity);
        mPopup = popup;
        mFormat = new DecimalFormat("00");
        mTimeConvertProvider = TimeConvertProvider.getInstance(mActivity);
        mDateStart = (StyledTextView) mFilterView.findViewById(R.id.filter_date_start);
        mDateEnd = (StyledTextView) mFilterView.findViewById(R.id.filter_date_end);
        mTimeStart = (StyledTextView) mFilterView.findViewById(R.id.filter_time_start);
        mTimeEnd = (StyledTextView) mFilterView.findViewById(R.id.filter_time_end);

        initDateTime();

        mUserView = (StyledTextView) mFilterView.findViewById(R.id.filter_user);
        mUserMoreView = (StyledTextView) mFilterView.findViewById(R.id.filter_user_more);
        mUserPlusView = (StyledTextView) mFilterView.findViewById(R.id.filter_user_plus);
        mDeviceView = (StyledTextView) mFilterView.findViewById(R.id.filter_device);
        mDeviceMoreView = (StyledTextView) mFilterView.findViewById(R.id.filter_device_more);
        mDevicePlusView = (StyledTextView) mFilterView.findViewById(R.id.filter_device_plus);
        mEventView = (StyledTextView) mFilterView.findViewById(R.id.filter_event);
        mEventMoreView = (StyledTextView) mFilterView.findViewById(R.id.filter_event_more);
        mEventPlusView = (StyledTextView) mFilterView.findViewById(R.id.filter_event_plus);

        mSelectDevicePopup = new SelectPopup<ListDevice>(mActivity, mPopup);
        mSelectUserPopup = new SelectPopup<ListUser>(mActivity, mPopup);
        mSelectEventPopup = new SelectPopup<EventType>(mActivity, mPopup);
        int[] ids = {R.id.filter_date_edit, R.id.filter_time_edit, R.id.filter_user_edit, R.id.filter_device_edit, R.id.filter_event_edit};
        for (int i : ids) {
            mFilterView.findViewById(i).setOnClickListener(mButtonClickListener);
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
        if (mDevices != null) {
            mDevices.clear();
        }
        mDevices = null;
        if (mEventTypes != null) {
            mEventTypes.clear();
        }
        if (mEventTypes != null) {
            mEventTypes.clear();
        }
        mEventTypes = null;
        mUsers=null;
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
        query.setTimeCalendar(mTimeConvertProvider,Query.QueryTimeType.end_datetime,end);

        if (mDevices != null && mDevices.size() > 0) {
            ArrayList<String> devicesId = new ArrayList<String>();
            for (BaseDevice device : mDevices) {
                devicesId.add(device.id);
            }
            query.device_id = devicesId;
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

    public boolean isExpand() {
        return mIsExapnd;
    }

    public void loadView() {
        if (!mIsExapnd) {
            mParentView.addView(mFilterView);
        }
        mIsExapnd = true;
    }

    public boolean onSearch(String query) {
        if (!mIsExapnd) {
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
        return false;
    }

    public void removeView() {
        if (mIsExapnd) {
            mParentView.removeView(mFilterView);
        }
        mIsExapnd = false;
    }

    private void selectDatePicker() {
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mActivity, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        linkType.add(new SelectCustomData(mContext.getString(R.string.start_date), 1, false));
        linkType.add(new SelectCustomData(mContext.getString(R.string.end_date), 2, false));
        selectPopup.show(SelectType.CUSTOM, new OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem) {
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
            public void OnResult(ArrayList<ListDevice> selectedItem) {
                if (selectedItem == null) {
                    return;
                }
                setDeviceResult((ArrayList<BaseDevice>) selectedItem.clone());
            }
        }, null, mContext.getString(R.string.select_device_orginal), true, true);
    }

    private void selectEvent() {
        mSelectEventPopup.show(SelectType.EVENT_TYPE, new OnSelectResultListener<EventType>() {
            @Override
            public void OnResult(ArrayList<EventType> selectedItem) {
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
            public void OnResult(ArrayList<SelectCustomData> selectedItem) {
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
            public void OnResult(ArrayList<ListUser> selectedItem) {
                if (selectedItem == null) {
                    return;
                }
                setUserResult((ArrayList<ListUser>) selectedItem.clone());
            }
        }, null, mContext.getString(R.string.select_user_original), true, true);
    }

    public void setDeviceResult(ArrayList<BaseDevice> selectedItem) {
        mDevices = selectedItem;
        if (mDevices == null || mDevices.size() < 1) {
            return;
        }
        int size = mDevices.size();
        String name =  mDevices.get(0).getName() + " / " + mDevices.get(0).id;
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

    public void setEventResult(ArrayList<EventType> selectedItem) {
        mEventTypes = selectedItem;
        if (mEventTypes == null || mEventTypes.size() < 1) {
            return;
        }
        int size = mEventTypes.size();
        mEventView.setText(mEventTypes.get(0).name);
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
        String name =  mUsers.get(0).getName() + " / " + mUsers.get(0).user_id;
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
