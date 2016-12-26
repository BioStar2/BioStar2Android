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
import com.supremainc.biostar2.sdk.datatype.v2.AccessControl.AccessGroups;
import com.supremainc.biostar2.sdk.datatype.v2.AccessControl.ListAccessGroup;
import com.supremainc.biostar2.sdk.provider.AccessGroupDataProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class BaseAccessGroupAdapter extends BaseListAdapter<ListAccessGroup> {
    protected AccessGroupDataProvider mAccessGroupDataProvider;
    protected Map<String, ListAccessGroup> mDuplicateItemMap = new HashMap<String, ListAccessGroup>();
    Listener<AccessGroups> mItemListener = new Response.Listener<AccessGroups>() {
        @Override
        public void onResponse(AccessGroups response, Object deliverParam) {
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
            if (mDuplicateItems != null && mDuplicateItems.size() > 0) {
                mDuplicateItemMap.clear();
                for (ListAccessGroup item:mDuplicateItems) {
                    mDuplicateItemMap.put(item.id,item);
                }
                for(Iterator<ListAccessGroup> it = response.records.iterator(); it.hasNext() ; )
                {
                    ListAccessGroup value = it.next();
                    if (mDuplicateItemMap.get(value.id) != null) {
                        it.remove();
                    }
                }
            }

            if (mOnItemsListener != null) {
                mOnItemsListener.onTotalReceive(response.total);
            }
            setData(response.records);
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
                    getItems(mQuery);
                }
            }, mActivity.getString(R.string.ok), mActivity.getString(R.string.cancel), false);
        }
    };
    Runnable mRunGetItems = new Runnable() {
        @Override
        public void run() {
            mAccessGroupDataProvider.getAccessGroups(TAG, mItemListener, mItemErrorListener, null);
        }
    };

    public BaseAccessGroupAdapter(Activity context, ArrayList<ListAccessGroup> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        mAccessGroupDataProvider = AccessGroupDataProvider.getInstance(context);
    }

    @Override
    public void getItems(String query) {
        mQuery = query;
        mHandler.removeCallbacks(mRunGetItems);
        mAccessGroupDataProvider.cancelAll(TAG);
        mPopup.showWait(mCancelExitListener);
        if (mItems != null) {
            mItems.clear();
            notifyDataSetChanged();
        }
        mHandler.postDelayed(mRunGetItems, 100);
    }
}
