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

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supremainc.biostar2.sdk.models.v2.accesscontrol.AccessGroups;
import com.supremainc.biostar2.sdk.models.v2.accesscontrol.ListAccessGroup;
import com.supremainc.biostar2.sdk.provider.AccessControlDataProvider;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseAccessGroupAdapter extends BaseListAdapter<ListAccessGroup> {
    protected static final int FIRST_LIMIT = 50;
    protected AccessControlDataProvider mAccessControlDataProvider;
    protected boolean mIsLastItemVisible = false;
    protected int mLimit = FIRST_LIMIT;
    protected int mOffset = 0;
    protected Map<String, ListAccessGroup> mDuplicateItemMap = new HashMap<String, ListAccessGroup>();


    private Callback<AccessGroups> mItemListener = new Callback<AccessGroups>() {
        @Override
        public void onFailure(Call<AccessGroups> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showRetryPopup(t.getMessage(), new OnPopupClickListener() {
                @Override
                public void OnNegative() {

                }

                @Override
                public void OnPositive() {
                    getItems(mQuery);
                }
            });
        }

        @Override
        public void onResponse(Call<AccessGroups> call, Response<AccessGroups> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, false, false)) {
                mItemListener.onFailure(call, new Throwable(getResponseErrorMessage(response)));
                return;
            }
            AccessGroups accessGroups = response.body();
            if (accessGroups.records == null || accessGroups.records.size() < 1) {
                if (mOnItemsListener != null) {
                    if (mItems == null || mItems.size() < 1) {
                        mTotal = 0;
                        mOnItemsListener.onNoneData();
                    } else {
                        mTotal = mItems.size();
                        mOnItemsListener.onSuccessNull(mItems.size());
                    }
                }
                return;
            }
            mOffset = mOffset + accessGroups.records.size();
            mTotal = accessGroups.total;
            if (mDuplicateItems != null && mDuplicateItems.size() > 0) {
                mDuplicateItemMap.clear();
                for (ListAccessGroup item : mDuplicateItems) {
                    mDuplicateItemMap.put(item.id, item);
                }
                mTotal = accessGroups.total - mDuplicateItems.size();
                for (Iterator<ListAccessGroup> it = accessGroups.records.iterator(); it.hasNext(); ) {
                    ListAccessGroup value = it.next();
                    if (mDuplicateItemMap.get(value.id) != null) {
                        it.remove();
                    }
                }
            }
            if (mTotal < accessGroups.records.size()) {
                mTotal = accessGroups.records.size();
            }
            if (mOnItemsListener != null) {
                mOnItemsListener.onTotalReceive(mTotal);
            }
            setData(accessGroups.records);
        }
    };

    private Runnable mRunGetItems = new Runnable() {
        @Override
        public void run() {
//            mAccessGroupDataProvider.getAccessGroups(TAG, mItemListener, mItemErrorListener,mOffset,mLimit,null, null);
            if (isInValidCheck()) {
                return;
            }
            request(mAccessControlDataProvider.getAccessGroups(mOffset, 5000, null, mItemListener));
        }
    };


    public BaseAccessGroupAdapter(Activity context, ArrayList<ListAccessGroup> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        mAccessControlDataProvider = AccessControlDataProvider.getInstance(context);
        setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mIsLastItemVisible && mTotal - 1 > mOffset) {
                    showWait(SwipyRefreshLayoutDirection.BOTTOM);
                    mHandler.removeCallbacks(mRunGetItems);
                    mHandler.postDelayed(mRunGetItems, 100);
                } else {
                    dismissWait();
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
        clearRequest();
        showWait(SwipyRefreshLayoutDirection.TOP);
        if (mItems != null) {
            mItems.clear();
            notifyDataSetChanged();
        }
        mHandler.postDelayed(mRunGetItems, 100);
    }
}
