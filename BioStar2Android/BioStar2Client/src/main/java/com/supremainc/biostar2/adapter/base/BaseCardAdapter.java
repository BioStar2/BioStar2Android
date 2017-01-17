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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.BuildConfig;
import com.supremainc.biostar2.sdk.datatype.v2.Card.Card;
import com.supremainc.biostar2.sdk.datatype.v2.Card.Cards;
import com.supremainc.biostar2.sdk.datatype.v2.Card.ListCard;
import com.supremainc.biostar2.sdk.provider.CardDataProvider;
import com.supremainc.biostar2.sdk.provider.DeviceDataProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;

import java.util.ArrayList;

public abstract class BaseCardAdapter extends BaseListAdapter<ListCard> {
    protected static final int FIRST_LIMIT = 50;
    protected CardDataProvider mCardDataProvider;
    protected boolean mIsLastItemVisible = false;
    protected int mLimit = FIRST_LIMIT;
    protected int mOffset = 0;
    private ShowType mType=ShowType.CARD;
    private int mAddedCount =0;
    private int mRemovedCount =0;
    public enum ShowType {
        CARD, CARD_CSN, CARD_WIEGAND, CARD_SMARTCARD
    }
    Listener<Cards> mItemListener = new Response.Listener<Cards>() {
        @Override
        public void onResponse(Cards response, Object deliverParam) {
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
            if (mItems == null) {
                mItems = new ArrayList<ListCard>();
            }
            int i = response.records.size() - 1;
            mOffset = mOffset + response.records.size();
            for (; i >= 0; i--) {
                ListCard card = response.records.get(i);
//                Log.e(TAG,"card:"+card.type);
                switch (mType) {
                    case CARD_CSN:
                        if (!Card.CSN.equals(card.type)) {
                            response.records.remove(i);
                            mRemovedCount++;
                            continue;
                        }
                        break;
                    case CARD_WIEGAND:
                        if (!(Card.WIEGAND.equals(card.type) || Card.CSN_WIEGAND.equals(card.type))) {
                            response.records.remove(i);
                            mRemovedCount++;
                            continue;
                        }
                        break;
                    case CARD_SMARTCARD:
                        if (!(Card.SECURE_CREDENTIAL.equals(card.type) || Card.ACCESS_ON.equals(card.type))) {
                            response.records.remove(i);
                            mRemovedCount++;
                            continue;
                        }
                        break;
                }
            }

            for (ListCard card : response.records) {
                mItems.add(card);
                mAddedCount++;
            }
            setData(mItems);
            mTotal = response.total-mRemovedCount;
//            Log.e(TAG,"mRemovedCount:"+mRemovedCount+" mTotal:"+mTotal+" mAddedCount:"+mAddedCount);
            if (mTotal < 0) {
                mTotal = 0;
            }
            if (mTotal < mItems.size()) {
                mTotal = mItems.size();
            }
            if (mAddedCount < mLimit && mTotal !=0) {
                if (mPopup != null) {
                    mPopup.showWait(mCancelExitListener);
                }
                mHandler.removeCallbacks(mRunGetItems);
                mHandler.postDelayed(mRunGetItems, 100);
            }
            if (mOnItemsListener != null) {
                mOnItemsListener.onTotalReceive(mTotal);
            }
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
                        mHandler.removeCallbacks(mRunGetItems);
                        mHandler.post(mRunGetItems);
                    }
                }, mActivity.getString(R.string.ok), mActivity.getString(R.string.cancel), false);
            }
        }
    };

    Runnable mRunGetItems = new Runnable() {
        @Override
        public void run() {
            if (isDestroy()) {
                return;
            }
            if (isMemoryPoor()) {
                if (mPopup != null) {
                    mPopup.dismiss();
                }
                if (mSwipyRefreshLayout != null) {
                    mSwipyRefreshLayout.setRefreshing(false);
                }
                mToastPopup.show(mActivity.getString(R.string.memory_poor), null);
                return;
            }
            mCardDataProvider.getUnassignedCards(TAG, mItemListener, mItemErrorListener, mOffset, mLimit,  mQuery, null);
        }
    };

    public BaseCardAdapter(Activity context, ArrayList<ListCard> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        mCardDataProvider = CardDataProvider.getInstance(context);
        setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && mIsLastItemVisible && mTotal - 1 > mOffset) {
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
        mAddedCount = 0;
        mRemovedCount =0;
        mLimit = FIRST_LIMIT;
        mHandler.removeCallbacks(mRunGetItems);
        mCardDataProvider.cancelAll(TAG);
        if (mPopup != null) {
            mPopup.showWait(mCancelExitListener);
        }
        if (mItems != null) {
            mItems.clear();
            notifyDataSetChanged();
        }
        mHandler.postDelayed(mRunGetItems, 500);
    }

    @Override
    public boolean selectChoices() {
        if (mListView == null) {
            return false;
        }
        ListCard card=null;
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
            return false;
        }
        for (int i = 0; i < getCount(); i++) {
            if (mItems != null) {
                card =  mItems.get(i);
                if (card != null && Card.ACCESS_ON.equals(card.type) && !card.is_blocked) {
                    mListView.setItemChecked(i, false);
                    continue;
                }
            }
            mListView.setItemChecked(i, true);
        }
        notifyDataSetChanged();
        return true;
        // mListView.invalidate();
    }

    public void setShowType(ShowType type) {
        mType = type;
    }
}
