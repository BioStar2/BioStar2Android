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
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.datatype.v2.Door.Doors;
import com.supremainc.biostar2.sdk.datatype.v2.Door.ListDoor;
import com.supremainc.biostar2.sdk.provider.DoorDataProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;
import com.tekinarslan.material.sample.FloatingActionButton;

import java.util.ArrayList;

public abstract class BaseDoorAdapter extends BaseListAdapter<ListDoor> {
    protected static final int FIRST_LIMIT = 50;
    protected DoorDataProvider mDoorDataProvider;
    protected boolean mIsLastItemVisible = false;
    protected int mLimit = FIRST_LIMIT;
    protected int mOffset = 0;
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
            if (isDestroy()) {
                return;
            }
            mDoorDataProvider.getDoors(TAG, mItemListener, mItemErrorListener, mOffset, mLimit, mQuery, null);
        }
    };
    private int mSetFirstVisible = 0;
    Listener<Doors> mItemListener = new Listener<Doors>() {
        @Override
        public void onResponse(Doors response, Object deliverParam) {
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
                if (mItems == null || mItems.size() < 1) {
                    mTotal =0;
                    mOnItemsListener.onNoMoreData();
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
                mOnItemsListener.onTotalReceive(response.total);
            }

            if (mItems == null) {
                mItems = new ArrayList<ListDoor>();
            }
            for (ListDoor door : response.records) {
                mItems.add(door);
            }
            setData(mItems);
            if (mSetFirstVisible != 0) {
                mListView.setSelection(mSetFirstVisible);
                mSetFirstVisible = 0;
            }
            mOffset = mItems.size();
            mTotal = response.total;
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
    private int mOldFirstVisible = 0;
    private BaseListViewScroll mOnScroll;

    public BaseDoorAdapter(Activity context, ArrayList<ListDoor> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        mDoorDataProvider = DoorDataProvider.getInstance(context);
        setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mIsLastItemVisible && mTotal - 1 > mOffset) {
                    if (mPopup != null) {
                        mPopup.showWait(true);
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
        mHandler.removeCallbacks(mRunGetItems);
        mDoorDataProvider.cancelAll(TAG);
        mOffset = 0;
        mTotal = 0;
        if (mSwipyRefreshLayout != null) {
            mSwipyRefreshLayout.setRefreshing(false);
            mSwipyRefreshLayout.setEnableBottom(true);
            mSwipyRefreshLayout.onRefresh(SwipyRefreshLayoutDirection.TOP, false);
        } else {
            if (mPopup != null) {
                mPopup.showWait(mCancelExitListener);
            }
        }

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
                            mHandler.removeCallbacks(mRunGetItems);
                            mHandler.postDelayed(mRunGetItems, 100);
                        } else {
                            mSwipyRefreshLayout.setRefreshing(false);
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
