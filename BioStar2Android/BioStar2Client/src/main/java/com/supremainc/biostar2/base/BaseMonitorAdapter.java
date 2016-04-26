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
package com.supremainc.biostar2.base;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.sdk.datatype.DoorData.BaseDoor;
import com.supremainc.biostar2.sdk.datatype.EventLogData.EventLogs;
import com.supremainc.biostar2.sdk.datatype.EventLogData.ListEventLog;
import com.supremainc.biostar2.sdk.datatype.EventLogData.LogLevel;
import com.supremainc.biostar2.sdk.datatype.EventLogData.LogType;
import com.supremainc.biostar2.sdk.datatype.QueryData.Query;
import com.supremainc.biostar2.sdk.provider.EventDataProvider;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.widget.StyledTextView;
import com.tekinarslan.material.sample.FloatingActionButton;

import java.util.ArrayList;
import java.util.Map;

public class BaseMonitorAdapter extends BaseListAdapter<ListEventLog> {
    protected Map<String, ArrayList<BaseDoor>> mDoorsMap;
    protected EventDataProvider mEventDataProvider;
    protected boolean mIsClickEnable;
    protected boolean mIsLastItemVisible = false;
    protected int mLimit = 100;
    protected int mOffset = 0;
    protected boolean mIsExistMoreData = true;
    protected OnItemsListener mOnItemsListener;
    protected BaseListViewScroll mOnScroll;
    protected Query mQuery;
    protected TimeConvertProvider mTimeConvertProvider;

    Listener<EventLogs> mEventsListener = new Response.Listener<EventLogs>() {
        @Override
        public void onResponse(EventLogs response, Object deliverParam) {
            if (isDestroy()) {
                return;
            }
            mPopup.dismissWiat();
            if (mSwipyRefreshLayout != null) {
                mSwipyRefreshLayout.setRefreshing(false);
            }
            if (response == null || response.records == null || response.records.size() < 1) {
                if (mOnItemsListener != null) {
                    mOnItemsListener.onSuccessNull();
                }
                mSwipyRefreshLayout.setEnableBottom(false);
                mTotal = getCount();
                mIsExistMoreData = false;
                return;
            }
            mIsExistMoreData = response.isNext;
            if (mItems == null) {
                mItems = new ArrayList<ListEventLog>();
            }
            mOffset = mOffset + response.records.size();

            for (ListEventLog log : response.records) {
                mItems.add(log);
            }
            setData(mItems);
            if (mIsExistMoreData) {
                mTotal = getCount() + 2;
            } else {
                mTotal = getCount();
            }
            if (mOnItemsListener != null) {
                mOnItemsListener.onTotalReceive(mTotal);
            }

            if (Setting.IS_AUTO_LOG_SCROLL) {
                if (mOnScroll != null) {
                    mOnScroll.autoClick();
                }
            }
        }
    };
    Runnable mRunGetItems = new Runnable() {
        @Override
        public void run() {
            if (isMemoryPoor()) {
                mPopup.dismiss();
                if (mSwipyRefreshLayout != null) {
                    mSwipyRefreshLayout.setRefreshing(false);
                }
                mToastPopup.show(mContext.getString(R.string.memory_poor), null);
                return;
            }
            if (mQuery == null) {
                mQuery = new Query(mOffset, mLimit, null, null, null);
            } else {
                mQuery.offset = mOffset;
                mQuery.limit = mLimit;
            }
            mEventDataProvider.searchEventLog(TAG, mQuery, mEventsListener, mEventsErrorListener, null);
        }
    };
    Response.ErrorListener mEventsErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isDestroy(error)) {
                return;
            }
            mPopup.dismissWiat();
            if (mSwipyRefreshLayout != null) {
                mSwipyRefreshLayout.setRefreshing(false);
            }
            mPopup.show(PopupType.ALERT, mContext.getString(R.string.fail_retry), Setting.getErrorMessage(error, mContext), new OnPopupClickListener() {
                @Override
                public void OnNegative() {
                    // mCancelExitListener.onCancel(null);
                }

                @Override
                public void OnPositive() {
                    if (mSwipyRefreshLayout != null) {
                        mSwipyRefreshLayout.setRefreshing(true);
                    } else {
                        mPopup.showWait(mCancelExitListener);
                    }
                    mListView.removeCallbacks(mRunGetItems);
                    mListView.post(mRunGetItems);
                }
            }, mContext.getString(R.string.ok), mContext.getString(R.string.cancel), false);

        }
    };

    public BaseMonitorAdapter(Activity context, ArrayList<ListEventLog> items, ListView listView, OnItemClickListener itemClickListener, boolean clickEnable, Popup popup,
                              OnItemsListener onItemsListener) {
        super(context, items, listView, popup);
        mIsClickEnable = clickEnable;
        mTimeConvertProvider = TimeConvertProvider.getInstance(context);
        if (mIsClickEnable) {
            setOnItemClickListener(itemClickListener);
        } else {
            listView.setClickable(false);
        }
        listView.setAdapter(this);
        mEventDataProvider = EventDataProvider.getInstance();
        mOnItemsListener = onItemsListener;
    }

    private void displayDescription(ListEventLog item, ExtraViewHolder vh) {
        if (item.event_type == null) {
            vh.mTitle.setText(" ");
            return;
        }
        vh.mTitle.setText(item.event_type.name);
    }

    private boolean displayDevice(ListEventLog item, ExtraViewHolder vh) {
        if (item.device != null) {
            if (item.device.name == null) {
                vh.mDevice.setText(item.device.id + " / " + item.device.id);
            } else {
                vh.mDevice.setText(item.device.id + " / " + item.device.name);
            }
            vh.mDevice.setVisibility(View.VISIBLE);
            if (mDoorsMap != null) {
                ArrayList<BaseDoor> listDoor = mDoorsMap.get(item.device.id);
                if (listDoor != null && listDoor.size() > 0) {
                    return true;
                }
            }
            return false;
        }
        vh.mDevice.setVisibility(View.GONE);
        return false;
    }

    private boolean displayUser(ListEventLog item, ExtraViewHolder vh) {
        if (item.user != null) {
            vh.mUser.setText(item.user.user_id + " / " + item.user.getName());
            vh.mUser.setVisibility(View.VISIBLE);
            if (item.user.name == null || item.user.name.isEmpty()) {
                return false;
            }
            return true;
        }
        vh.mUser.setVisibility(View.GONE);
        return false;
    }

    private void displpayInfo(boolean isLink, ItemViewHolder viewHolder) {
        if (!mIsClickEnable) {
            return;
        }
        if (isLink) {
            viewHolder.mInfo.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mInfo.setVisibility(View.GONE);
        }
    }

    public void getItems(Query query) {
        mQuery = query;
        if (mSwipyRefreshLayout != null) {
            mSwipyRefreshLayout.setEnableBottom(true);
            mSwipyRefreshLayout.onRefresh(SwipyRefreshLayoutDirection.TOP, false);
        } else {
            mPopup.showWait(mCancelExitListener);
        }
        mListView.removeCallbacks(mRunGetItems);
        mEventDataProvider.cancelAll(TAG);
        mOffset = 0;
        mTotal = 0;
        if (mItems != null) {
            mItems.clear();
            notifyDataSetChanged();
        }
        mListView.postDelayed(mRunGetItems, 100);
    }

    @Override
    public void getItems(String query) {
        getItems((Query) null);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder viewHolder = getViewHolder(position, convertView, parent, R.layout.list_item_monitor);
        if (viewHolder == null) {
            return null;
        }
        if (convertView == null) {
            viewHolder.mExtend = new ExtraViewHolder(viewHolder.mRoot, R.id.picture, R.id.title, R.id.date, R.id.user, R.id.device);
            if (!mIsClickEnable) {
                viewHolder.mInfo.setVisibility(View.GONE);
            }
        }
        ExtraViewHolder vh = (ExtraViewHolder) viewHolder.mExtend;
        if (vh == null) {
            return null;
        }
        ListEventLog item = mItems.get(position);
        if (item != null) {
            displayDescription(item, vh);
            String deviceTime = item.getTimeFormmat(mTimeConvertProvider, ListEventLog.ListEventLogTimeType.datetime,TimeConvertProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN_SEC);
            vh.mDate.setText(deviceTime);
            boolean isUserLink = displayUser(item, vh);
            boolean isDeviceLink = displayDevice(item, vh);
            boolean isLink = false;
            if (isUserLink || isDeviceLink) {
                isLink = true;
            }
            displpayInfo(isLink, viewHolder);
            setIcon(item, vh.mIcon);
        }
        return viewHolder.mRoot;
    }

    public void setDoors(Map<String, ArrayList<BaseDoor>> doorsMap) {
        mDoorsMap = doorsMap;
    }

    private void setGreen(LogType type, ImageView view) {
        switch (type) {
            case DEFAULT:
                view.setImageResource(R.drawable.monitoring_ic3);
                break;
            case DEVICE:
                view.setImageResource(R.drawable.ic_event_device_01);
                break;
            case DOOR:
                view.setImageResource(R.drawable.ic_event_door_01);
                break;
            case USER:
                view.setImageResource(R.drawable.ic_event_user_01);
                break;
            case ZONE:
                view.setImageResource(R.drawable.ic_event_zone_01);
                break;
            case AUTHENTICATION:
                view.setImageResource(R.drawable.ic_event_auth_01);
                break;
        }
    }

    private void setIcon(ListEventLog item, ImageView view) {
        LogType type = LogType.DEFAULT;

        if (LogType.DEVICE.mName.equals(item.type)) {
            type = LogType.DEVICE;
        } else if (LogType.DOOR.mName.equals(item.type)) {
            type = LogType.DOOR;
        } else if (LogType.USER.mName.equals(item.type)) {
            type = LogType.USER;
        } else if (LogType.ZONE.mName.equals(item.type)) {
            type = LogType.ZONE;
        } else if (LogType.AUTHENTICATION.mName.equals(item.type)) {
            type = LogType.AUTHENTICATION;
        }
        if (type == LogType.DEFAULT) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "type is NULL:" + item.id + " code:" + item.event_type.code + " name:" + item.event_type.name);
            }
        }

        if (LogLevel.GREEN.mName.equals(item.level)) {
            setGreen(type, view);
        } else if (LogLevel.YELLOW.mName.equals(item.level)) {
            setYellow(type, view);
        } else if (LogLevel.RED.mName.equals(item.level)) {
            setRed(type, view);
        }

//		int code = item.event_type.code;
//		// VERIFY_SUCCESS
//		if (isCondiction(4096, 4111, code, view, R.drawable.monitoring_ic3)) {
//			return;
//		}
//		// VERIFY_FAIL
//		if (isCondiction(4352, 4359, code, view, R.drawable.monitoring_ic3)) {
//			return;
//		}
//		// VERIFY_DURESS
//		if (isCondiction(4608, 4623, code, view, R.drawable.monitoring_ic2)) {
//			return;
//		}
//		// IDENTIFY_SUCCESS
//		if (isCondiction(4864, 4868, code, view, R.drawable.monitoring_ic3)) {
//			return;
//		}
//		// IDENTIFY_FAIL
//		if (isCondiction(5120, 5127, code, view, R.drawable.monitoring_ic3)) {
//			return;
//		}
//		// IDENTIFY_DURESS
//		if (isCondiction(5376, 5380, code, view, R.drawable.monitoring_ic2)) {
//			return;
//		}
//		// AUTH_FAIL
//		if (isCondiction(5888, 6147, code, view, R.drawable.monitoring_ic1)) {
//			return;
//		}
//		// ACCESS_DENIED
//		if (isCondiction(6400, 6407, code, view, R.drawable.monitoring_ic8)) {
//			return;
//		}
//		// SYSTEM
//		if (isCondiction(12288, 17664, code, view, R.drawable.monitoring_ic4)) {
//			return;
//		}
//		// DOOR //TODO icon change
//		if (isCondiction(20480, 21248, code, view, R.drawable.door_ic_3)) {
//			return;
//		}
//		// DOOR //TODO icon change
//		if (isCondiction(21504, 27648, code, view, R.drawable.monitoring_ic6)) {
//			return;
//		}
//		view.setImageResource(R.drawable.monitoring_ic3);
    }

    private void setRed(LogType type, ImageView view) {
        switch (type) {
            case DEFAULT:
                view.setImageResource(R.drawable.monitoring_ic7);
                break;
            case DEVICE:
                view.setImageResource(R.drawable.ic_event_device_02);
                break;
            case DOOR:
                view.setImageResource(R.drawable.ic_event_door_02);
                break;
            case USER:
                view.setImageResource(R.drawable.ic_event_user_02);
                break;
            case ZONE:
                view.setImageResource(R.drawable.ic_event_zone_02);
                break;
            case AUTHENTICATION:
                view.setImageResource(R.drawable.ic_event_auth_02);
                break;
        }
    }

    public void setSwipyRefreshLayout(SwipyRefreshLayout swipyRefreshLayout, FloatingActionButton fab) {
        mOnScroll = new BaseListViewScroll();
        mOnScroll.setFloatingActionButton(fab, mListView, this);
        setOnScrollListener(mOnScroll);
        mSwipyRefreshLayout = swipyRefreshLayout;
        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.e(TAG, "SwipyRefreshLayoutDirection:" + direction);
                switch (direction) {
                    case TOP:
                        getItems(mQuery);
                        break;
                    case BOTTOM:
                        if (mIsExistMoreData) {
                            mListView.removeCallbacks(mRunGetItems);
                            mListView.postDelayed(mRunGetItems, 100);
                        } else {
                            mSwipyRefreshLayout.setRefreshing(false);
                            mSwipyRefreshLayout.setEnableBottom(false);
                            mToastPopup.show(mContext.getString(R.string.no_more_data), null);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void setYellow(LogType type, ImageView view) {
        switch (type) {
            case DEFAULT:
                view.setImageResource(R.drawable.monitoring_ic1);
                break;
            case DEVICE:
                view.setImageResource(R.drawable.ic_event_device_03);
                break;
            case DOOR:
                view.setImageResource(R.drawable.ic_event_door_03);
                break;
            case USER:
                view.setImageResource(R.drawable.ic_event_user_03);
                break;
            case ZONE:
                view.setImageResource(R.drawable.ic_event_zone_03);
                break;
            case AUTHENTICATION:
                view.setImageResource(R.drawable.ic_event_auth_03);
                break;
        }
    }

    public class ExtraViewHolder {
        public StyledTextView mDate;
        public StyledTextView mDevice;
        public ImageView mIcon;
        public StyledTextView mTitle;
        public StyledTextView mUser;

        public ExtraViewHolder(View root, int picture, int title, int date, int user, int device) {
            mIcon = (ImageView) root.findViewById(picture);
            mTitle = (StyledTextView) root.findViewById(title);
            mDate = (StyledTextView) root.findViewById(date);
            mUser = (StyledTextView) root.findViewById(user);
            mDevice = (StyledTextView) root.findViewById(device);
        }
    }
}
