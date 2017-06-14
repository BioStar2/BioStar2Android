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
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.sdk.models.v2.card.SmartCardLayout;
import com.supremainc.biostar2.sdk.models.v2.card.SmartCardLayouts;
import com.supremainc.biostar2.sdk.provider.CardDataProvider;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseSmartCardLayoutAdapter extends BaseListAdapter<SmartCardLayout> {
    protected static final int FIRST_LIMIT = 5000;
    protected CardDataProvider mCardDataProvider;
    protected boolean mIsLastItemVisible = false;
    protected int mLimit = FIRST_LIMIT;
    protected int mOffset = 0;
    private Callback<SmartCardLayouts> mItemListener = new Callback<SmartCardLayouts>() {
        @Override
        public void onFailure(Call<SmartCardLayouts> call, Throwable t) {
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
        public void onResponse(Call<SmartCardLayouts> call, Response<SmartCardLayouts> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, false, false)) {
                mItemListener.onFailure(call, new Throwable(getResponseErrorMessage(response)));
                return;
            }
            SmartCardLayouts smartCardLayouts = response.body();
            if (smartCardLayouts.records == null || smartCardLayouts.records.size() < 1) {
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
            if (mItems == null) {
                mItems = new ArrayList<SmartCardLayout>();
            }
            if (mOnItemsListener != null) {
                mOnItemsListener.onTotalReceive(smartCardLayouts.total);
            }
            for (SmartCardLayout data : smartCardLayouts.records) {
                mItems.add(data);
            }
            setData(mItems);
            mOffset = mItems.size();
            mTotal = smartCardLayouts.total;
            if (mTotal < mItems.size()) {
                mTotal = mItems.size();
            }
        }
    };

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
            request(mCardDataProvider.getSmartCardLayout(mOffset, mLimit, mQuery, mItemListener));
        }
    };


    public BaseSmartCardLayoutAdapter(Activity context, ArrayList<SmartCardLayout> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        mCardDataProvider = CardDataProvider.getInstance(context);
        setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mIsLastItemVisible && mTotal - 1 > mOffset) {
                    mPopup.showWait(true);
                    mHandler.removeCallbacks(mRunGetItems);
                    mHandler.postDelayed(mRunGetItems, 100);
                } else {
                    mPopup.dismissWiat();
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
        mLimit = FIRST_LIMIT;
        mHandler.removeCallbacks(mRunGetItems);
        clearRequest();
        showWait(SwipyRefreshLayoutDirection.TOP);
        if (mItems != null) {
            mItems.clear();
            notifyDataSetChanged();
        }
        mHandler.postDelayed(mRunGetItems, 500);
    }
}
