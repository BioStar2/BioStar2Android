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
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.sdk.models.v2.door.Doors;
import com.supremainc.biostar2.sdk.models.v2.door.ListDoor;
import com.supremainc.biostar2.sdk.provider.DoorDataProvider;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.tekinarslan.material.sample.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseDoorAdapter extends BaseListAdapter<ListDoor> {
    protected static final int FIRST_LIMIT = 50;
    protected DoorDataProvider mDoorDataProvider;
    protected boolean mIsLastItemVisible = false;
    protected int mLimit = FIRST_LIMIT;
    protected int mOffset = 0;
    private int mSetFirstVisible = 0;

    private Callback<Doors> mItemListener = new Callback<Doors>() {
        @Override
        public void onFailure(Call<Doors> call, Throwable t) {
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
        public void onResponse(Call<Doors> call, Response<Doors> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, false, false)) {
                mItemListener.onFailure(call, new Throwable(getResponseErrorMessage(response)));
                return;
            }
            Doors doors = response.body();
            if (doors.records == null || doors.records.size() < 1) {
                if (mItems == null || mItems.size() < 1) {
                    mTotal = 0;
                    mOnItemsListener.onNoneData();
                } else {
                    mTotal = mItems.size();
                    mOnItemsListener.onSuccessNull(mItems.size());
                }
                if (mTotal <= getCount() && mSwipyRefreshLayout != null) {
                    mSwipyRefreshLayout.setEnableBottom(false);
                }
                return;
            }
            if (mOnItemsListener != null) {
                mOnItemsListener.onTotalReceive(doors.total);
            }

            if (mItems == null) {
                mItems = new ArrayList<ListDoor>();
            }
            for (ListDoor door : doors.records) {
                mItems.add(door);
            }
            setData(mItems);
            if (mSetFirstVisible != 0) {
                mListView.setSelection(mSetFirstVisible);
                mSetFirstVisible = 0;
            }
            mOffset = mItems.size();
            mTotal = doors.total;
            if (mTotal < mItems.size()) {
                mTotal = mItems.size();
            }
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "mTotal:" + mTotal + " mOffset:" + mOffset + " getCount():" + getCount());
            }
            if (mTotal <= getCount() && mSwipyRefreshLayout != null) {
                mSwipyRefreshLayout.setEnableBottom(false);
            }
        }
    };


    private Runnable mRunGetItems = new Runnable() {
        @Override
        public void run() {
            if (isInValidCheck()) {
                return;
            }
            request(mDoorDataProvider.getDoors(mOffset, mLimit, mQuery, mItemListener));
        }
    };

    private int mOldFirstVisible = 0;
    private BaseListViewScroll mOnScroll;

    public BaseDoorAdapter(Activity context, ArrayList<ListDoor> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        mDoorDataProvider = DoorDataProvider.getInstance(context);
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
        mHandler.removeCallbacks(mRunGetItems);
        clearRequest();
        mOffset = 0;
        mTotal = 0;
        showWait(SwipyRefreshLayoutDirection.TOP);

        if (mItems != null) {
            mItems.clear();
            notifyDataSetChanged();
        }
        mHandler.postDelayed(mRunGetItems, 100);
    }

    public void setPostReceiveToLastPosition() {
        if (mOnScroll != null) {
            mSetFirstVisible = mOnScroll.getmOldFirstVisibleItemPosition();
        } else {
            mSetFirstVisible = mOldFirstVisible;
        }
    }

    public void setSwipyRefreshLayout(SwipyRefreshLayout swipyRefreshLayout, FloatingActionButton fab) {
        mOnScroll = new BaseListViewScroll();
        mOnScroll.setFloatingActionButton(fab, mListView, this);
        setOnScrollListener(mOnScroll);
        mSwipyRefreshLayout = swipyRefreshLayout;
        mSwipyRefreshLayout.setEnableBottom(false);
        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                switch (direction) {
                    case TOP:
                        getItems(mQuery);
                        break;
                    case BOTTOM:
                        if (mTotal - 1 > mOffset) {
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

    private class ItemViewHolder {
        public ImageView mLink;
        public StyledTextView mName;
        public View mRoot;

        public ItemViewHolder(View root) {
            mRoot = root;
            mName = (StyledTextView) root.findViewById(R.id.name);
            mLink = (ImageView) root.findViewById(R.id.info);
        }
    }
}
