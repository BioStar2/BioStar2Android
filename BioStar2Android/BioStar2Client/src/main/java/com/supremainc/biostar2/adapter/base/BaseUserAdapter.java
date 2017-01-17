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

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.datatype.v2.User.ListUser;
import com.supremainc.biostar2.sdk.datatype.v2.User.User;
import com.supremainc.biostar2.sdk.datatype.v2.User.Users;
import com.supremainc.biostar2.sdk.provider.UserDataProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;
import com.tekinarslan.material.sample.FloatingActionButton;

import java.util.ArrayList;

public abstract class BaseUserAdapter extends BaseListAdapter<ListUser> {
    protected static final int FIRST_LIMIT = 25;
    protected static final int SECOND_LIMIT = 50;
    protected boolean mIsLastItemVisible = false;
    protected int mLimit = FIRST_LIMIT;
    protected int mOffset = 0;
    protected UserDataProvider mUserDataProvider;
    Listener<Users> mUsersListener = new Listener<Users>() {
        @Override
        public void onResponse(Users response, Object deliverParam) {
            if (mPopup != null) {
                mPopup.dismiss();
            }
            if (isDestroy()) {
                return;
            }
            if (mSwipyRefreshLayout != null) {
                mSwipyRefreshLayout.setRefreshing(false);
            }
            onUserListener(response, deliverParam);
        }
    };
    private String mGroupId = "1";
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
            mUserDataProvider.getUsers(TAG, mUsersListener, mUsersErrorListener, mOffset, mLimit, mGroupId, mQuery, null);
        }
    };
    Response.ErrorListener mUsersErrorListener = new Response.ErrorListener() {
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
                    }

                    @Override
                    public void OnPositive() {
                        if (mSwipyRefreshLayout != null) {
                            mSwipyRefreshLayout.setRefreshing(true);
                        } else {
                            mPopup.showWait(mCancelExitListener);
                        }
                        mHandler.removeCallbacks(mRunGetItems);
                        mHandler.post(mRunGetItems);
                    }
                }, mActivity.getString(R.string.ok), mActivity.getString(R.string.cancel), false);
            }
        }
    };
    private BaseListViewScroll mOnScroll;

    public BaseUserAdapter(Activity context, ArrayList<ListUser> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onUsersListener) {
        super(context, items, listView, itemClickListener, popup, onUsersListener);
        mUserDataProvider = UserDataProvider.getInstance();
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && mIsLastItemVisible && mTotal - 1 > mOffset) {
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

    public boolean modifyItem(ListUser user) {
        if (user == null || user.user_id == null || mItems == null) {
            return false;
        }
        for (int i = 0; i < mItems.size(); i++) {
            ListUser itemUser = mItems.get(i);
            if (itemUser.user_id.equals(user.user_id)) {
                user.last_modify = String.valueOf(System.currentTimeMillis());
                try {
                    mItems.set(i, user.clone());
                    notifyDataSetChanged();
                    return true;
                } catch (Exception e) {

                }
            }
        }
        return false;
    }

    public boolean modifyCardItem(String userID,int cardCount) {
        if (userID == null || mItems == null) {
            return false;
        }
        for (int i = 0; i < mItems.size(); i++) {
            ListUser itemUser = mItems.get(i);
            if (itemUser.user_id.equals(userID)) {
                itemUser.card_count = cardCount;
                notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

    public boolean modifyFingerPrintItem(String userID,int fingerCount) {
        if (userID == null || mItems == null) {
            return false;
        }
        for (int i = 0; i < mItems.size(); i++) {
            ListUser itemUser = mItems.get(i);
            if (itemUser.user_id.equals(userID)) {
                itemUser.fingerprint_count = fingerCount;
                itemUser.fingerprint_template_count = fingerCount;
                notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

    public void clearItems() {
        if (mUserDataProvider != null) {
            mUserDataProvider.cancelAll(TAG);
        }
        super.clearItems();
    }

    @Override
    public void getItems(String query) {
        mQuery = query;
        mLimit = FIRST_LIMIT;
        mHandler.removeCallbacks(mRunGetItems);
        mUserDataProvider.cancelAll(TAG);
//        mLastModify = String.valueOf(System.currentTimeMillis());
        mOffset = 0;
        mTotal = 0;
        if (mItems != null) {
            mItems.clear();
            notifyDataSetChanged();
        }
        if (mSwipyRefreshLayout != null) {
            mSwipyRefreshLayout.setRefreshing(false);
            mSwipyRefreshLayout.setEnableBottom(true);
            mSwipyRefreshLayout.onRefresh(SwipyRefreshLayoutDirection.TOP, false);
        } else {
            if (mPopup != null) {
                mPopup.showWait(mCancelExitListener);
            }
        }
        mHandler.removeCallbacks(mRunGetItems);
        mHandler.postDelayed(mRunGetItems, 500);
    }


    public String getuserGroupId() {
        return mGroupId;
    }

    private void onUserListener(Users response, Object deliverParam) {
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
            if (mTotal <= getCount() && mSwipyRefreshLayout != null) {
                mSwipyRefreshLayout.setEnableBottom(false);
            }
            return;
        }

        if (mItems == null) {
            mItems = new ArrayList<ListUser>();
        }

        for (ListUser user : response.records) {
            mItems.add(user);
        }
        setData(mItems);
        mOffset = mItems.size();

        mLimit = SECOND_LIMIT;
        mTotal = response.total;
        if (mTotal < mItems.size()) {
            mTotal = mItems.size();
        }
        if (mOnItemsListener != null) {
            mOnItemsListener.onTotalReceive(mTotal);
        }
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "mTotal:" + mTotal + " mOffset:" + mOffset + " getCount():" + getCount());
        }
        if (mTotal <= getCount() && mSwipyRefreshLayout != null) {
            mSwipyRefreshLayout.setEnableBottom(false);
        }
    }


    public void setSwipyRefreshLayout(SwipyRefreshLayout swipyRefreshLayout, FloatingActionButton fab) {
        mOnScroll = new BaseListViewScroll();
        mOnScroll.setFloatingActionButton(fab, mListView, this);
        setOnScrollListener(mOnScroll);
        mSwipyRefreshLayout = swipyRefreshLayout;
        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.e(TAG, "SwipyRefreshLayoutDirection:" + direction);
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

    public void setUserGroupId(String id) {
        mGroupId = id;
        getItems(mQuery);
    }
}
