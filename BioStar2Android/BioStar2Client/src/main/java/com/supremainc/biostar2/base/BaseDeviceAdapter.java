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

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.sdk.datatype.DeviceData.Devices;
import com.supremainc.biostar2.sdk.datatype.DeviceData.ListDevice;
import com.supremainc.biostar2.sdk.provider.DeviceDataProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;

import java.util.ArrayList;

public class BaseDeviceAdapter extends BaseListAdapter<ListDevice> {
    protected DeviceDataProvider mDeviceDataProvider;
    protected int mDeviceSupport;
    protected OnItemsListener mOnItemsListener;
    protected String mQuery;
    protected boolean misWithOutSlave = false;
    Listener<Devices> mItemListener = new Response.Listener<Devices>() {
        @Override
        public void onResponse(Devices response, Object deliverParam) {
            if (isDestroy()) {
                return;
            }
            mPopup.dismiss();
            if (response == null || response.records == null) {
                if (mOnItemsListener != null) {
                    mOnItemsListener.onSuccessNull();
                }
                return;
            }
            int i = response.records.size() - 1;
            for (; i >= 0; i--) {
                ListDevice device = response.records.get(i);
                if (!device.isSupport(misWithOutSlave, mDeviceSupport)) {
                    response.records.remove(i);
                    continue;
                }
            }

            if (mOnItemsListener != null) {
                mOnItemsListener.onTotalReceive(response.records.size());
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
            mDeviceDataProvider.getDevices(TAG, mItemListener, mItemErrorListener, mQuery, null);
        }
    };
    Response.ErrorListener mItemErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isDestroy(error)) {
                return;
            }
            mPopup.dismiss();
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

    public BaseDeviceAdapter(Activity context, ArrayList<ListDevice> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, popup);
        listView.setAdapter(this);
        setOnItemClickListener(itemClickListener);
        mDeviceDataProvider = DeviceDataProvider.getInstance(context);
        mOnItemsListener = onItemsListener;
    }

    @Override
    public void getItems(String query) {
        mQuery = query;
        mListView.removeCallbacks(mRunGetItems);
        mDeviceDataProvider.cancelAll(TAG);
        mPopup.showWait(mCancelExitListener);
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
        ListDevice item = mItems.get(position);
        if (item != null) {
            viewHolder.mName.setText(item.name);
        }
        return viewHolder.mRoot;
    }

    public void setDeviceSupport(int deviceSupport) {
        mDeviceSupport = deviceSupport;
    }

    public void setMasterOnly(boolean b) {
        misWithOutSlave = b;
    }
}
