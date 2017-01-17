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
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.datatype.v2.AccessControl.AccessGroups;
import com.supremainc.biostar2.sdk.datatype.v2.AccessControl.ListAccessGroup;
import com.supremainc.biostar2.sdk.provider.AccessGroupDataProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class BaseAccessGroupAdapter extends BaseListAdapter<ListAccessGroup> {
    protected static final int FIRST_LIMIT = 50;
    protected AccessGroupDataProvider mAccessGroupDataProvider;
    protected boolean mIsLastItemVisible = false;
    protected int mLimit = FIRST_LIMIT;
    protected int mOffset = 0;
    protected Map<String, ListAccessGroup> mDuplicateItemMap = new HashMap<String, ListAccessGroup>();
    Listener<AccessGroups> mItemListener = new Response.Listener<AccessGroups>() {
        @Override
        public void onResponse(AccessGroups response, Object deliverParam) {
            if (mPopup != null) {
                mPopup.dismiss();
            }
            if (isDestroy()) {
                return;
            }
             if (mSwipyRefreshLayout != null) {
                mSwipyRefreshLayout.setRefreshing(false);
            }
            if (response == null || response.records == null || response.records.size() < 1) {
                if (mOnItemsListener != null) {
                    if (mItems == null || mItems.size() < 1) {
                        mTotal =0;
                        mOnItemsListener.onNoMoreData();
                    } else {
                        mTotal = mItems.size();
                        mOnItemsListener.onSuccessNull(mItems.size());
                    }
                }
                return;
            }
            mOffset = mOffset + response.records.size();
            mTotal = response.total;
            if (mDuplicateItems != null && mDuplicateItems.size() > 0) {
                mDuplicateItemMap.clear();
                for (ListAccessGroup item:mDuplicateItems) {
                    mDuplicateItemMap.put(item.id,item);
                }
                mTotal = response.total - mDuplicateItems.size();
                for(Iterator<ListAccessGroup> it = response.records.iterator(); it.hasNext() ; )
                {
                    ListAccessGroup value = it.next();
                    if (mDuplicateItemMap.get(value.id) != null) {
                        it.remove();
                    }
                }
            }
            if (mTotal < response.records.size()) {
                mTotal = response.records.size();
            }
            if (mOnItemsListener != null) {
                mOnItemsListener.onTotalReceive(mTotal);
            }
            setData(response.records);
        }
    };
    Response.ErrorListener mItemErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (mPopup != null) {
                mPopup.dismiss();
            }
            if (isDestroy(error)) {
                return;
            }
            if (mSwipyRefreshLayout != null) {
                mSwipyRefreshLayout.setRefreshing(false);
            }
            if (mPopup != null) {
                mPopup.show(PopupType.ALERT, mActivity.getString(R.string.fail_retry), Setting.getErrorMessage(error, mActivity), new OnPopupClickListener() {
                    @Override
                    public void OnNegative() {
                        mCancelExitListener.onCancel(null);
                    }

                    @Override
                    public void OnPositive() {
                        getItems(mQuery);
                    }
                }, mActivity.getString(R.string.ok), mActivity.getString(R.string.cancel), false);
            }
        }
    };
    Runnable mRunGetItems = new Runnable() {
        @Override
        public void run() {
//            mAccessGroupDataProvider.getAccessGroups(TAG, mItemListener, mItemErrorListener,mOffset,mLimit,null, null);
            if (isDestroy()) {
                return;
            }
            mAccessGroupDataProvider.getAccessGroups(TAG, mItemListener, mItemErrorListener,mOffset,5000,null, null);
        }
    };

    public BaseAccessGroupAdapter(Activity context, ArrayList<ListAccessGroup> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        mAccessGroupDataProvider = AccessGroupDataProvider.getInstance(context);
        setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mIsLastItemVisible && mTotal - 1 > mOffset) {
                    if (mPopup != null) {
                        mPopup.showWait(mCancelExitListener);
                    }
                    mHandler.removeCallbacks(mRunGetItems);
                    mHandler.postDelayed(mRunGetItems, 100);
                } else {
                    if (mPopup != null) {
                        mPopup.dismissWiat();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mIsLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });
    }

    @Override
    public void getItems(String query) {
        mQuery = query;
        mOffset = 0;
        mTotal = 0;
        mHandler.removeCallbacks(mRunGetItems);
        mAccessGroupDataProvider.cancelAll(TAG);
        if (mPopup != null) {
            mPopup.showWait(mCancelExitListener);
        }
        if (mItems != null) {
            mItems.clear();
            notifyDataSetChanged();
        }
        mHandler.postDelayed(mRunGetItems, 100);
    }
}
