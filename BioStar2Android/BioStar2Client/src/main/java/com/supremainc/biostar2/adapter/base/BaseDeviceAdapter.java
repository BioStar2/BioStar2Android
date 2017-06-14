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

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.sdk.models.v2.device.Devices;
import com.supremainc.biostar2.sdk.models.v2.device.ListDevice;
import com.supremainc.biostar2.sdk.provider.DeviceDataProvider;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseDeviceAdapter extends BaseListAdapter<ListDevice> {
    protected static final int FIRST_LIMIT = 50;
    protected DeviceDataProvider mDeviceDataProvider;
    protected int mDeviceSupport;
    protected boolean misWithOutSlave = false;
    protected boolean mIsLastItemVisible = false;
    protected int mLimit = FIRST_LIMIT;
    protected int mOffset = 0;
    private ShowType mType = ShowType.DEVICE;
    private int mAddedCount = 0;
    private int mRemovedCount = 0;
    private Callback<Devices> mItemListener = new Callback<Devices>() {
        @Override
        public void onFailure(Call<Devices> call, Throwable t) {
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
        public void onResponse(Call<Devices> call, Response<Devices> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, false, false)) {
                mItemListener.onFailure(call, new Throwable(getResponseErrorMessage(response)));
                return;
            }
            if (mItems == null) {
                mItems = new ArrayList<ListDevice>();
            }
            if (mType == ShowType.DEVICE_FINGERPRINT_BIOMINI) {
                if (mItems.size() < 1) {
                    ListDevice listDevice = new ListDevice();
                    listDevice.name = mActivity.getString(R.string.portable);
                    listDevice.id = "-10";
                    mItems.add(listDevice);
                }
            }
            Devices devices = response.body();
            if (devices.records == null || devices.records.size() < 1) {
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

            if (devices.records.size() < 1) {
                return;
            }
            int i = devices.records.size() - 1;
            mOffset = mOffset + devices.records.size();
            for (; i >= 0; i--) {
                ListDevice device = devices.records.get(i);
                if (!device.isSupport(misWithOutSlave, mDeviceSupport)) {
                    devices.records.remove(i);
                    mRemovedCount++;
                    continue;
                }
                switch (mType) {
                    case DEVICE_CARD_CSN:
                        if (device.isSupportCSNWiegand()) {
                            devices.records.remove(i);
                            mRemovedCount++;
                            continue;
                        }
//                        if (device.wiegand_format_list  != null && device.wiegand_format_list.size() > 0) {
//                            response.records.remove(i);
//                            mRemovedCount++;
//                            continue;
//                        }
                        break;
                    case DEVICE_CARD_SMARTCARD:
                        if (device.smart_card_layout == null) {
                            devices.records.remove(i);
                            mRemovedCount++;
                            continue;
                        }
                        break;
                    case DEVICE_CARD_WIEGAND:
                        if (!(device.isSupportWiegand() || device.isSupportCSNWiegand())) {
                            devices.records.remove(i);
                            mRemovedCount++;
                            continue;
                        }
                        break;
                    case DEVICE_FINGERPRINT_BIOMINI:

                        break;
                }
            }
            for (ListDevice listDevice : devices.records) {
                mItems.add(listDevice);
                mAddedCount++;
            }
            setData(mItems);
            mTotal = devices.total - mRemovedCount;
            if (mTotal < 0) {
                mTotal = 0;
            }
            if (mTotal < mItems.size()) {
                mTotal = mItems.size();
            }
            if (mAddedCount < mLimit) {
                showWait(null);
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
            request(mDeviceDataProvider.getDevices(mOffset, mLimit, mQuery, mItemListener));
        }
    };

    public BaseDeviceAdapter(Activity context, ArrayList<ListDevice> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        mDeviceDataProvider = DeviceDataProvider.getInstance(context);
        setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mIsLastItemVisible && mTotal - 1 > mOffset) {
                    showWait(null);
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

    public void setShowType(ShowType type) {
        mType = type;
    }

    public void setDeviceSupport(int deviceSupport) {
        mDeviceSupport = deviceSupport;
    }

    public void setMasterOnly(boolean b) {
        misWithOutSlave = b;
    }

    public enum ShowType {
        DEVICE, DEVICE_CARD, DEVICE_CARD_CSN, DEVICE_CARD_WIEGAND, DEVICE_CARD_SMARTCARD, DEVICE_FINGERPRINT_BIOMINI
    }
}
