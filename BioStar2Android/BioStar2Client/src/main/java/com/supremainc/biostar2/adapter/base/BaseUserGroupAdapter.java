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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.datatype.v2.User.UserGroup;
import com.supremainc.biostar2.sdk.datatype.v2.User.UserGroups;
import com.supremainc.biostar2.sdk.provider.UserDataProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;

import java.util.ArrayList;

public abstract class BaseUserGroupAdapter extends BaseListAdapter<UserGroup> {
    protected UserDataProvider mUserDataProvider;
    Listener<UserGroups> mItemListener = new Listener<UserGroups>() {
        @Override
        public void onResponse(UserGroups response, Object deliverParam) {
            if (mPopup != null) {
                mPopup.dismiss();
            }
            if (isDestroy()) {
                return;
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
            setData(response.records);
            mTotal = response.total;
            if (mTotal < mItems.size()) {
                mTotal = mItems.size();
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
            mUserDataProvider.getUserGroups(TAG, mItemListener, mItemErrorListener, 0, 5000, null, null);
        }
    };

    public BaseUserGroupAdapter(Activity context, ArrayList<UserGroup> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        mUserDataProvider = UserDataProvider.getInstance(context);
    }

    @Override
    public void getItems(String query) {
        mQuery = query;
        mListView.removeCallbacks(mRunGetItems);
        mUserDataProvider.cancelAll(TAG);
        if (mPopup != null) {
            mPopup.showWait(mCancelExitListener);
        }
        if (mItems != null) {
            mItems.clear();
            notifyDataSetChanged();
        }
        mHandler.postDelayed(mRunGetItems, 100);
    }
}
