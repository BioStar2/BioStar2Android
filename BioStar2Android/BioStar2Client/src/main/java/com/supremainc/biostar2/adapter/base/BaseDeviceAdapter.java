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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.datatype.v2.Device.Devices;
import com.supremainc.biostar2.sdk.datatype.v2.Device.ListDevice;
import com.supremainc.biostar2.sdk.provider.DeviceDataProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;

import java.util.ArrayList;

public abstract class BaseDeviceAdapter extends BaseListAdapter<ListDevice> {
    protected DeviceDataProvider mDeviceDataProvider;
    protected int mDeviceSupport;
    protected boolean misWithOutSlave = false;
    private ShowType mType=ShowType.DEVICE;
    private int mAddedCount =0;
    private int mRemovedCount =0;
    protected boolean mIsLastItemVisible = false;
    protected int mLimit = FIRST_LIMIT;
    protected int mOffset = 0;
    protected static final int FIRST_LIMIT = 50;

    public enum ShowType {
        DEVICE,DEVICE_CARD, DEVICE_CARD_CSN, DEVICE_CARD_WIEGAND, DEVICE_CARD_SMARTCARD
    }
    Listener<Devices> mItemListener = new Listener<Devices>() {
        @Override
        public void onResponse(Devices response, Object deliverParam) {
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
            if (mItems == null) {
                mItems = new ArrayList<ListDevice>();
            }

            if (response.records.size() < 1) {
                return;
            }
            int i = response.records.size() - 1;
            mOffset = mOffset + response.records.size();
            for (; i >= 0; i--) {
                ListDevice device = response.records.get(i);
                if (!device.isSupport(misWithOutSlave, mDeviceSupport)) {
                    response.records.remove(i);
                    mRemovedCount++;
                    continue;
                }
                switch (mType) {
                    case  DEVICE_CARD_CSN:
                        if (device.wiegand_format != null) {
                            response.records.remove(i);
                            mRemovedCount++;
                            continue;
                        }
                        if (device.wiegand_format_list  != null && device.wiegand_format_list.size() > 0) {
                            response.records.remove(i);
                            mRemovedCount++;
                            continue;
                        }
                        break;
                    case DEVICE_CARD_SMARTCARD:
                        if (device.smart_card_layout == null) {
                            response.records.remove(i);
                            mRemovedCount++;
                            continue;
                        }
                        break;
                    case DEVICE_CARD_WIEGAND:
                        if (device.wiegand_format == null && (device.wiegand_format_list == null||device.wiegand_format_list.size() < 1 )) {
                            response.records.remove(i);
                            mRemovedCount++;
                            continue;
                        }
                        break;
                }
            }
            for (ListDevice listDevice : response.records) {
                mItems.add(listDevice);
                mAddedCount++;
            }
            setData(mItems);
            mTotal = response.total-mRemovedCount;
            if (mTotal < 0) {
                mTotal = 0;
            }

            if (mAddedCount < mLimit) {
                mPopup.showWait(mCancelExitListener);
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
                    mHandler.removeCallbacks(mRunGetItems);
                    mHandler.post(mRunGetItems);
                }
            }, mActivity.getString(R.string.ok), mActivity.getString(R.string.cancel), false);
        }
    };
    Runnable mRunGetItems = new Runnable() {
        @Override
        public void run() {
            if (isMemoryPoor()) {
                mPopup.dismiss();
                if (mSwipyRefreshLayout != null) {
                    mSwipyRefreshLayout.setRefreshing(false);
                }
                mToastPopup.show(mActivity.getString(R.string.memory_poor), null);
                return;
            }
            mDeviceDataProvider.getDevices(TAG, mItemListener, mItemErrorListener,mOffset, mLimit,mQuery, null);
        }
    };

    public BaseDeviceAdapter(Activity context, ArrayList<ListDevice> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        mDeviceDataProvider = DeviceDataProvider.getInstance(context);
        setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mIsLastItemVisible && mTotal - 1 > mOffset) {
                    mPopup.showWait(mCancelExitListener);
                    mHandler.removeCallbacks(mRunGetItems);
                    mHandler.postDelayed(mRunGetItems, 100);
                } else {
                    mPopup.dismissWiat();
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
        mDeviceDataProvider.cancelAll(TAG);
        mPopup.showWait(mCancelExitListener);
        if (mItems != null) {
            mItems.clear();
            notifyDataSetChanged();
        }
        mHandler.postDelayed(mRunGetItems, 500);
    }

    public void setShowType(ShowType type) {
        mType = type;
    }

    public void setDeviceSupport(int deviceSupport) {
        mDeviceSupport = deviceSupport;
    }

    public void setMasterOnly(boolean b) {
        misWithOutSlave = b;
    }
}
