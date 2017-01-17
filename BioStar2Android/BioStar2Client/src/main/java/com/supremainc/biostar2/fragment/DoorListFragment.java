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
package com.supremainc.biostar2.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.adapter.DoorAdapter;
import com.supremainc.biostar2.adapter.base.BaseListAdapter.OnItemsListener;
import com.supremainc.biostar2.sdk.datatype.v2.Door.Door;
import com.supremainc.biostar2.sdk.datatype.v2.Door.ListDoor;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.view.SubToolbar;
import com.supremainc.biostar2.widget.ScreenControl;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;

public class DoorListFragment extends BaseFragment {
    private DoorAdapter mDoorAdapter;
    private String mSearchText;
    private SubToolbar mSubToolbar;
    private int mTotal = 0;
    private int mSelectedDoorPosition = -1;
    private Listener<Door> mDoorListener = new Listener<Door>() {
        @Override
        public void onResponse(Door response, Object param) {
            if (isInValidCheck(null)) {
                return;
            }
            mPopup.dismissWiat();
            Bundle bundle = new Bundle();
            try {
                bundle.putSerializable(Door.TAG, response);
                if (mDoorAdapter != null && mSelectedDoorPosition > -1) {
                    mDoorAdapter.setData(mSelectedDoorPosition, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            ScreenControl screenControl = ScreenControl.getInstance();
            screenControl.addScreen(ScreenType.DOOR, bundle);
        }
    };
    private Response.ErrorListener mDoorErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, final Object deliverParam) {
            if (isInValidCheck(error)) {
                return;
            }
            mPopup.dismissWiat();
            mPopup.show(PopupType.ALERT, mContext.getString(R.string.fail_retry), Setting.getErrorMessage(error, mContext), new OnPopupClickListener() {
                @Override
                public void OnNegative() {

                }

                @Override
                public void OnPositive() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPopup.showWait(true);
                            mDoorDataProvider.getDoor(TAG, (String) deliverParam, mDoorListener, mDoorErrorListener, deliverParam);
                        }
                    });
                }
            }, mContext.getString(R.string.ok), mContext.getString(R.string.cancel));
        }
    };
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListDoor item = (ListDoor) mDoorAdapter.getItem(position);
            if (item == null) {
                return;
            }
            mPopup.showWait(true);
            mSelectedDoorPosition = position;
            mDoorDataProvider.getDoor(TAG, item.id, mDoorListener, mDoorErrorListener, item.id);
        }
    };

    private void setTotal(int total) {
        if (mTotal != total) {
            mSubToolbar.setTotal(total);
            mTotal = total;
            if (mSearchText == null) {
                sendLocalBroadcast(Setting.BROADCAST_DOOR_COUNT, total);
            }
        }
    }

    private OnItemsListener mOnItemsListener = new OnItemsListener()
    {
        @Override
        public void onSuccessNull(int total) {
            mIsDataReceived = true;
            setTotal(total);
        }

        @Override
        public void onNoMoreData() {
            mIsDataReceived = true;
            mToastPopup.show(getString(R.string.none_data), null);
            setTotal(0);
        }

        @Override
        public void onTotalReceive(int total) {
            mIsDataReceived = true;
            setTotal(total);
        }
    };

    public DoorListFragment() {
        super();
        setType(ScreenType.DOOR_LIST);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void initValue() {
        if (mDoorAdapter == null) {
            mDoorAdapter = new DoorAdapter(mContext, null, getListView(), mOnItemClickListener, mPopup, mOnItemsListener);
            mDoorAdapter.setSwipyRefreshLayout(getSwipeyLayout(), getFab());
        }
        if (mSubToolbar == null) {
            mSubToolbar = (SubToolbar) mRootView.findViewById(R.id.subtoolbar);
            mSubToolbar.init(getActivity());
            mSubToolbar.setVisibleSearch(true, new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    if (mSearchText != null) {
                        onSearch(null);
                    }
                    return false;
                }
            });
            mSubToolbar.showMultipleSelectInfo(false, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setResID(R.layout.fragment_list_swpiy);
        super.onCreateView(inflater, container, savedInstanceState);
        if (!mIsReUsed) {
            initValue();
            initActionbar(getString(R.string.all_door));
            mRootView.invalidate();
        }
        return mRootView;
    }

    @Override
    public boolean onBack() {
        if (mSubToolbar != null) {
            if (mSubToolbar.isExpandSearch()) {
                mSubToolbar.setSearchIconfied();
                return true;
            }
        }
        if (super.onBack()) {
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mIsDataReceived) {
            if (mPopup != null && mDoorAdapter != null) {
                mDoorAdapter.getItems(mSearchText);
                mDoorAdapter.setPostReceiveToLastPosition();
            }
        }
    }

    @Override
    public void onPause() {
        hideIme(mSubToolbar.mSearchViewEx.getEditTextView());
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mDoorAdapter != null) {
            mDoorAdapter.clearItems();
        }
        super.onDestroy();
    }

    @Override
    public boolean onSearch(String query) {
        if (super.onSearch(query)) {
            return true;
        }
        mSearchText = query;
        if (mDoorAdapter != null) {
            mDoorAdapter.getItems(query);
        }
        return true;
    }

    protected void registerBroadcast() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (mIsDestroy) {
                        return;
                    }
                    String action = intent.getAction();
                    if (action.equals(Setting.BROADCAST_UPDATE_DOOR)) {
                        if (isResumed()) {
                            if (mDoorAdapter != null) {
                                mDoorAdapter.getItems(mSearchText);
                                mDoorAdapter.setPostReceiveToLastPosition();
                            }
                        } else {
                            mIsDataReceived = false;
                            mTotal = 0;
                        }
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_UPDATE_DOOR);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
    }
}
