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
package com.supremainc.biostar2.adapter.base;

import android.app.Activity;
import android.util.Log;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.models.v2.eventlog.EventLogs;
import com.supremainc.biostar2.sdk.models.v2.eventlog.ListEventLog;
import com.supremainc.biostar2.sdk.models.v2.eventlog.Query;
import com.supremainc.biostar2.sdk.provider.DateTimeDataProvider;
import com.supremainc.biostar2.sdk.provider.MonitoringDataProvider;
import com.supremainc.biostar2.sdk.provider.PermissionDataProvider;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.tekinarslan.material.sample.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseMonitorAdapter extends BaseListAdapter<ListEventLog> {
    protected MonitoringDataProvider mMonitoringDataProvider;
    protected boolean mIsClickEnable;
    protected boolean mIsLastItemVisible = false;
    protected int mLimit = 100;
    protected int mOffset = 0;
    protected boolean mIsExistMoreData = true;
    protected BaseListViewScroll mOnScroll;
    protected Query mQueryObject;
    protected DateTimeDataProvider mDateTimeDataProvider;
    protected PermissionDataProvider mPermissionDataProvider;
    private Runnable mRunGetItems = new Runnable() {
        @Override
        public void run() {
            if (isInValidCheck()) {
                return;
            }
            if (isMemoryPoor()) {
                dismissWait();
                mToastPopup.show(mActivity.getString(R.string.memory_poor), null);
                return;
            }

            if (mQueryObject == null) {
                mQueryObject = new Query(mOffset, mLimit, null, null, null);
            } else {
                mQueryObject.offset = mOffset;
                mQueryObject.limit = mLimit;
            }
            request(mMonitoringDataProvider.searchEventLog(mQueryObject, mItemListener));
        }
    };
    private Callback<EventLogs> mItemListener = new Callback<EventLogs>() {
        @Override
        public void onFailure(Call<EventLogs> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showRetryPopup(t.getMessage(), new OnPopupClickListener() {
                @Override
                public void OnNegative() {

                }

                @Override
                public void OnPositive() {
                    showWait(null);
                    mHandler.removeCallbacks(mRunGetItems);
                    mHandler.post(mRunGetItems);
                }
            });
        }

        @Override
        public void onResponse(Call<EventLogs> call, Response<EventLogs> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, false, false)) {
                mItemListener.onFailure(call, new Throwable(getResponseErrorMessage(response)));
                return;
            }

            EventLogs eventLogs = response.body();
            if (eventLogs.records == null || eventLogs.records.size() < 1) {
                if (mItems == null || mItems.size() < 1) {
                    mTotal = 0;
                    mOnItemsListener.onNoneData();
                } else {
                    mTotal = mItems.size();
                    mOnItemsListener.onSuccessNull(mItems.size());
                }
                mSwipyRefreshLayout.setEnableBottom(false);
                mTotal = getCount();
                mIsExistMoreData = false;
                return;
            }
            mIsExistMoreData = eventLogs.isNext;
            if (mItems == null) {
                mItems = new ArrayList<ListEventLog>();
            }
            mOffset = mOffset + eventLogs.records.size();

            for (ListEventLog log : eventLogs.records) {
                mItems.add(log);
            }
            setData(mItems);
            if (mIsExistMoreData) {
                mTotal = getCount() + 2;
            } else {
                mTotal = getCount();
                if (mSwipyRefreshLayout != null) {
                    mSwipyRefreshLayout.setEnableBottom(false);
                }
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


    public BaseMonitorAdapter(Activity context, ArrayList<ListEventLog> items, ListView listView, OnItemClickListener itemClickListener, Popup popup,
                              OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        mDateTimeDataProvider = DateTimeDataProvider.getInstance(context);
        mMonitoringDataProvider = MonitoringDataProvider.getInstance(context);
        mPermissionDataProvider = PermissionDataProvider.getInstance(context);
    }

    public void setClickEnable(boolean clickEnable) {
        mIsClickEnable = clickEnable;
        if (mIsClickEnable) {
            setOnItemClickListener(mOnItemClickListener);
        } else {
            mListView.setClickable(false);
        }
    }

    public void getItems(Query query) {
        mQueryObject = query;
        mHandler.removeCallbacks(mRunGetItems);
        clearRequest();
        mOffset = 0;
        mTotal = 0;
        if (mItems != null) {
            mItems.clear();
            notifyDataSetChanged();
        }
        showWait(SwipyRefreshLayoutDirection.TOP);
        mHandler.postDelayed(mRunGetItems, 500);
    }

    @Override
    public void getItems(String query) {
        getItems((Query) null);
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
                        getItems(mQueryObject);
                        break;
                    case BOTTOM:
                        if (mIsExistMoreData) {
                            showWait(SwipyRefreshLayoutDirection.BOTTOM);
                            mHandler.removeCallbacks(mRunGetItems);
                            mHandler.postDelayed(mRunGetItems, 100);
                        } else {
                            dismissWait();
                            mToastPopup.show(mActivity.getString(R.string.no_more_data), null);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
