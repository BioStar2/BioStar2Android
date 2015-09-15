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
package com.supremainc.biostar2.base;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.sdk.datatype.DoorData.Doors;
import com.supremainc.biostar2.sdk.datatype.DoorData.ListDoor;
import com.supremainc.biostar2.sdk.provider.DoorDataProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.tekinarslan.material.sample.FloatingActionButton;

import java.util.ArrayList;

public class BaseDoorAdapter extends BaseListAdapter<ListDoor> {
    protected DoorDataProvider mDoorDataProvider;
    protected OnItemsListener mOnItemsListener;
    protected String mQuery;
    Listener<Doors> mItemListener = new Response.Listener<Doors>() {
        @Override
        public void onResponse(Doors response, Object deliverParam) {
            if (isDestroy()) {
                return;
            }
            if (mSwipyRefreshLayout != null) {
                mSwipyRefreshLayout.setRefreshing(false);
            }
            mPopup.dismiss();
            if (response == null || response.records == null) {
                if (mOnItemsListener != null) {
                    mOnItemsListener.onSuccessNull();
                }
                return;
            }
            if (mOnItemsListener != null) {
                mOnItemsListener.onTotalReceive(response.total);
            }
            if (response.records.size() < 1) {
                return;
            }
            setData(response.records);
        }
    };
    Runnable mRunGetItems = new Runnable() {
        @Override
        public void run() {
            mDoorDataProvider.getDoors(TAG, mItemListener, mItemErrorListener, 0, -1, "1", mQuery, null);
        }
    };
    Response.ErrorListener mItemErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isDestroy(error)) {
                return;
            }
            mPopup.dismiss();
            if (mSwipyRefreshLayout != null) {
                mSwipyRefreshLayout.setRefreshing(false);
            }
            mPopup.show(PopupType.ALERT, mContext.getString(R.string.fail_retry), Setting.getErrorMessage(error, mContext), new OnPopupClickListener() {
                @Override
                public void OnNegative() {
                    mCancelExitListener.onCancel(null);
                }

                @Override
                public void OnPositive() {
                    getItems(mQuery);
                }
            }, mContext.getString(R.string.ok), mContext.getString(R.string.cancel), false);
        }
    };

    public BaseDoorAdapter(Activity context, ArrayList<ListDoor> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, popup);
        listView.setAdapter(this);
        setOnItemClickListener(itemClickListener);
        mDoorDataProvider = DoorDataProvider.getInstance(context);
        mOnItemsListener = onItemsListener;
    }

    @Override
    public void getItems(String query) {
        mQuery = query;
        mListView.removeCallbacks(mRunGetItems);
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
        mListView.postDelayed(mRunGetItems, 100);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder viewHolder = getViewHolder(position, convertView, parent, R.layout.list_item);
        if (viewHolder == null) {
            return null;
        }

        ListDoor item = mItems.get(position);
        if (item != null) {
            viewHolder.mName.setText(item.name);
        }
        return viewHolder.mRoot;
    }

    public void setSwipyRefreshLayout(SwipyRefreshLayout swipyRefreshLayout, FloatingActionButton fab) {
        BaseListViewScroll onScroll = new BaseListViewScroll();
        onScroll.setFloatingActionButton(fab, mListView, this);
        setOnScrollListener(onScroll);
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
                        mToastPopup.show(mContext.getString(R.string.no_more_data), null);
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
