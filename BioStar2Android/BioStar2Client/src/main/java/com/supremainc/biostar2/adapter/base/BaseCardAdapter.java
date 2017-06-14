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

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.sdk.models.v2.card.Card;
import com.supremainc.biostar2.sdk.models.v2.card.Cards;
import com.supremainc.biostar2.sdk.models.v2.card.ListCard;
import com.supremainc.biostar2.sdk.provider.CardDataProvider;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseCardAdapter extends BaseListAdapter<ListCard> {
    protected static final int FIRST_LIMIT = 50;
    protected CardDataProvider mCardDataProvider;
    protected boolean mIsLastItemVisible = false;
    protected int mLimit = FIRST_LIMIT;
    protected int mOffset = 0;
    private ShowType mType = ShowType.CARD;
    private int mAddedCount = 0;
    private int mRemovedCount = 0;
    private Callback<Cards> mItemListener = new Callback<Cards>() {
        @Override
        public void onFailure(Call<Cards> call, Throwable t) {
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
        public void onResponse(Call<Cards> call, Response<Cards> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, false, false)) {
                mItemListener.onFailure(call, new Throwable(getResponseErrorMessage(response)));
                return;
            }
            Cards cards = response.body();
            if (cards.records == null || cards.records.size() < 1) {
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
                mItems = new ArrayList<ListCard>();
            }
            int i = cards.records.size() - 1;
            mOffset = mOffset + cards.records.size();
            for (; i >= 0; i--) {
                ListCard card = cards.records.get(i);
                switch (mType) {
                    case CARD_CSN:
                        if (!Card.CSN.equals(card.type)) {
                            cards.records.remove(i);
                            mRemovedCount++;
                            continue;
                        }
                        break;
                    case CARD_WIEGAND:
                        if (!(Card.WIEGAND.equals(card.type) || Card.CSN_WIEGAND.equals(card.type))) {
                            cards.records.remove(i);
                            mRemovedCount++;
                            continue;
                        }
                        break;
                    case CARD_SMARTCARD:
                        if (!(Card.SECURE_CREDENTIAL.equals(card.type) || Card.ACCESS_ON.equals(card.type))) {
                            cards.records.remove(i);
                            mRemovedCount++;
                            continue;
                        }
                        break;
                }
            }

            for (ListCard card : cards.records) {
                mItems.add(card);
                mAddedCount++;
            }
            setData(mItems);
            mTotal = cards.total - mRemovedCount;
            if (mTotal < 0) {
                mTotal = 0;
            }
            if (mTotal < mItems.size()) {
                mTotal = mItems.size();
            }
            if (mAddedCount < mLimit && mTotal != 0) {
                if (mPopup != null) {
                    mPopup.showWait(mCancelStayListener);
                }
                mHandler.removeCallbacks(mRunGetItems);
                mHandler.postDelayed(mRunGetItems, 100);
            }
            if (mOnItemsListener != null) {
                mOnItemsListener.onTotalReceive(mTotal);
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
            request(mCardDataProvider.getUnassignedCards(mOffset, mLimit, mQuery, mItemListener));
        }
    };


    public BaseCardAdapter(Activity context, ArrayList<ListCard> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        mCardDataProvider = CardDataProvider.getInstance(context);
        setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && mIsLastItemVisible && mTotal - 1 > mOffset) {
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
        mAddedCount = 0;
        mRemovedCount = 0;
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

    @Override
    public boolean selectChoices() {
        if (mListView == null) {
            return false;
        }
        ListCard card = null;
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
            return false;
        }
        for (int i = 0; i < getCount(); i++) {
            if (mItems != null) {
                card = mItems.get(i);
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

    public int getAvailableTotal() {
        if (mListView == null) {
            return -1;
        }
        ListCard card = null;
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
            return -1;
        }
        int total = getCount();
        for (int i = 0; i < getCount(); i++) {
            if (mItems != null) {
                card = mItems.get(i);
                if (card != null && Card.ACCESS_ON.equals(card.type) && !card.is_blocked) {
                    total--;
                }
            }
        }
        return total;
    }

    public void setShowType(ShowType type) {
        mType = type;
    }

    public enum ShowType {
        CARD, CARD_CSN, CARD_WIEGAND, CARD_SMARTCARD
    }
}
