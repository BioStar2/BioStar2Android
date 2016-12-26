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
import com.supremainc.biostar2.sdk.datatype.v2.Door.BaseDoor;
import com.supremainc.biostar2.sdk.datatype.v2.EventLog.EventLogs;
import com.supremainc.biostar2.sdk.datatype.v2.EventLog.ListEventLog;
import com.supremainc.biostar2.sdk.datatype.v2.EventLog.Query;
import com.supremainc.biostar2.sdk.provider.EventDataProvider;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;
import com.tekinarslan.material.sample.FloatingActionButton;

import java.util.ArrayList;
import java.util.Map;

public abstract class BaseMonitorAdapter extends BaseListAdapter<ListEventLog> {
    protected EventDataProvider mEventDataProvider;
    protected boolean mIsClickEnable;
    protected boolean mIsLastItemVisible = false;
    protected int mLimit = 100;
    protected int mOffset = 0;
    protected boolean mIsExistMoreData = true;
    protected BaseListViewScroll mOnScroll;
    protected Query mQueryObject;
    protected TimeConvertProvider mTimeConvertProvider;

    Listener<EventLogs> mEventsListener = new Listener<EventLogs>() {
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
            mPopup.show(PopupType.ALERT, mActivity.getString(R.string.fail_retry), Setting.getErrorMessage(error, mActivity), new OnPopupClickListener() {
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
                    mHandler.removeCallbacks(mRunGetItems);
                    mHandler.post(mRunGetItems);
                }
            }, mActivity.getString(R.string.ok), mActivity.getString(R.string.cancel), false);

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
                mToastPopup.show(mActivity.getString(R.string.memory_poor), null);
                return;
            }
            if (mQueryObject == null) {
                mQueryObject = new Query(mOffset, mLimit, null, null, null);
            } else {
                mQueryObject.offset = mOffset;
                mQueryObject.limit = mLimit;
            }
            mEventDataProvider.searchEventLog(TAG, mQueryObject, mEventsListener, mEventsErrorListener, null);
        }
    };

    public BaseMonitorAdapter(Activity context, ArrayList<ListEventLog> items, ListView listView, OnItemClickListener itemClickListener, Popup popup,
                              OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        mTimeConvertProvider = TimeConvertProvider.getInstance(context);
        mEventDataProvider = EventDataProvider.getInstance();
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
        if (mSwipyRefreshLayout != null) {
            mSwipyRefreshLayout.setEnableBottom(true);
            mSwipyRefreshLayout.onRefresh(SwipyRefreshLayoutDirection.TOP, false);
        } else {
            mPopup.showWait(mCancelExitListener);
        }
        mHandler.removeCallbacks(mRunGetItems);
        mEventDataProvider.cancelAll(TAG);
        mOffset = 0;
        mTotal = 0;
        if (mItems != null) {
            mItems.clear();
            notifyDataSetChanged();
        }
        mHandler.postDelayed(mRunGetItems, 100);
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
                            mHandler.removeCallbacks(mRunGetItems);
                            mHandler.postDelayed(mRunGetItems, 100);
                        } else {
                            mSwipyRefreshLayout.setRefreshing(false);
                            mSwipyRefreshLayout.setEnableBottom(false);
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
