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
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.datatype.v2.Card.WiegandFormat;
import com.supremainc.biostar2.sdk.datatype.v2.Card.WiegandFormats;
import com.supremainc.biostar2.sdk.datatype.v2.Common.SimpleData;
import com.supremainc.biostar2.sdk.datatype.v2.Common.SimpleDatas;
import com.supremainc.biostar2.sdk.provider.CardDataProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;

import java.util.ArrayList;

public abstract class BaseWiegandFormatAdapter extends BaseListAdapter<WiegandFormat> {
    protected static final int FIRST_LIMIT = 50;
    protected CardDataProvider mCardDataProvider;
    protected boolean mIsLastItemVisible = false;
    protected int mLimit = FIRST_LIMIT;
    protected int mOffset = 0;

    Listener<WiegandFormats> mItemListener = new Listener<WiegandFormats>() {
        @Override
        public void onResponse(WiegandFormats response, Object deliverParam) {
            if (isDestroy()) {
                return;
            }
            mPopup.dismiss();
            if (response == null || response.records == null || response.records.size() < 1) {
                if (mOnItemsListener != null) {
                    mOnItemsListener.onSuccessNull();
                }
                return;
            }
            if (mItems == null) {
                mItems = new ArrayList<WiegandFormat>();
            }
            if (mOnItemsListener != null) {
                mOnItemsListener.onTotalReceive(response.total);
            }
            for (WiegandFormat data : response.records) {
                mItems.add(data);
            }
            setData(mItems);
            mOffset = mItems.size() - 1;
            mTotal = response.total;
        }
    };
    Response.ErrorListener mItemErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isDestroy(error)) {
                return;
            }
            mPopup.dismiss();
            mPopup.show(PopupType.ALERT, mActivity.getString(R.string.fail_retry), Setting.getErrorMessage(error, mActivity), new OnPopupClickListener() {
                @Override
                public void OnNegative() {
                    mCancelExitListener.onCancel(null);
                }

                @Override
                public void OnPositive() {
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
            mCardDataProvider.getWiegandFormats(TAG, mItemListener, mItemErrorListener, null);
        }
    };

    public BaseWiegandFormatAdapter(Activity context, ArrayList<WiegandFormat> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        mCardDataProvider = CardDataProvider.getInstance(context);
//        setOnScrollListener(new OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && mIsLastItemVisible && mTotal - 1 > mOffset) {
//                    mPopup.showWait(true);
//                    mHandler.removeCallbacks(mRunGetItems);
//                    mHandler.postDelayed(mRunGetItems, 100);
//                } else {
//                    mPopup.dismissWiat();
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                mIsLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
//            }
//        });
    }


    @Override
    public void getItems(String query) {
        mQuery = query;
        mOffset = 0;
        mTotal = 0;
        mLimit = FIRST_LIMIT;
        mHandler.removeCallbacks(mRunGetItems);
        mCardDataProvider.cancelAll(TAG);
        mPopup.showWait(mCancelExitListener);
        if (mItems != null) {
            mItems.clear();
            notifyDataSetChanged();
        }
        mHandler.postDelayed(mRunGetItems, 500);
    }
}
