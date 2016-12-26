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
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
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
    protected DoorDataProvider mDoorDataProvider;
    Response.ErrorListener mItemErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isDestroy(error)) {
                return;
            }
            if (mPopup != null) {
                mPopup.dismiss();
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
            mDoorDataProvider.getDoors(TAG, mItemListener, mItemErrorListener, 0, -1, "1", mQuery, null);
        }
    };
    private int mSetFirstVisible = 0;
    Listener<Doors> mItemListener = new Listener<Doors>() {
        @Override
        public void onResponse(Doors response, Object deliverParam) {
            if (isDestroy()) {
                return;
            }
            if (mSwipyRefreshLayout != null) {
                mSwipyRefreshLayout.setRefreshing(false);
            }
            if (mPopup != null) {
                mPopup.dismiss();
            }
            if (response == null || response.records == null || response.records.size() < 1) {
                if (mOnItemsListener != null) {
                    mOnItemsListener.onSuccessNull();
                }
                return;
            }
            if (mOnItemsListener != null) {
                mOnItemsListener.onTotalReceive(response.total);
            }
            setData(response.records);
            if (mSetFirstVisible != 0) {
                mListView.setSelection(mSetFirstVisible);
                mSetFirstVisible = 0;
            }
        }
    };
    private int mOldFirstVisible = 0;
    private BaseListViewScroll mOnScroll;

    public BaseDoorAdapter(Activity context, ArrayList<ListDoor> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        mDoorDataProvider = DoorDataProvider.getInstance(context);
    }

    @Override
    public void getItems(String query) {
        mQuery = query;
        mHandler.removeCallbacks(mRunGetItems);
        mDoorDataProvider.cancelAll(TAG);
        if (mSwipyRefreshLayout != null) {
            mSwipyRefreshLayout.onRefresh(SwipyRefreshLayoutDirection.TOP, false);
        } else {
            mPopup.showWait(mCancelExitListener);
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
                        mSwipyRefreshLayout.setRefreshing(false);
                        mToastPopup.show(mActivity.getString(R.string.no_more_data), null);
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
