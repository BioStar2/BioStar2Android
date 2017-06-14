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
package com.supremainc.biostar2.widget.popup;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.adapter.SimpleAccessGroupAdapter;
import com.supremainc.biostar2.adapter.SimpleCardAdapter;
import com.supremainc.biostar2.adapter.SimpleCustomAdapter;
import com.supremainc.biostar2.adapter.SimpleDeviceAdapter;
import com.supremainc.biostar2.adapter.SimpleDoorAdapter;
import com.supremainc.biostar2.adapter.SimpleEventTypeAdapter;
import com.supremainc.biostar2.adapter.SimplePermissionAdapter;
import com.supremainc.biostar2.adapter.SimplePermissionV2Adapter;
import com.supremainc.biostar2.adapter.SimpleSmartCardLayoutAdapter;
import com.supremainc.biostar2.adapter.SimpleStringAdapter;
import com.supremainc.biostar2.adapter.SimpleUserAdapter;
import com.supremainc.biostar2.adapter.SimpleUserGroupAdapter;
import com.supremainc.biostar2.adapter.WiegandDataAdapter;
import com.supremainc.biostar2.adapter.base.BaseCardAdapter;
import com.supremainc.biostar2.adapter.base.BaseDeviceAdapter;
import com.supremainc.biostar2.adapter.base.BaseListAdapter;
import com.supremainc.biostar2.adapter.base.BaseListAdapter.OnItemsListener;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.sdk.models.v1.permission.CloudRole;
import com.supremainc.biostar2.sdk.models.v2.accesscontrol.ListAccessGroup;
import com.supremainc.biostar2.sdk.models.v2.card.ListCard;
import com.supremainc.biostar2.sdk.models.v2.card.SmartCardLayout;
import com.supremainc.biostar2.sdk.models.v2.card.WiegandFormat;
import com.supremainc.biostar2.sdk.models.v2.device.DeviceType;
import com.supremainc.biostar2.sdk.models.v2.device.ListDevice;
import com.supremainc.biostar2.sdk.models.v2.door.ListDoor;
import com.supremainc.biostar2.sdk.models.v2.eventlog.EventType;
import com.supremainc.biostar2.sdk.models.v2.permission.UserPermission;
import com.supremainc.biostar2.sdk.models.v2.user.ListUser;
import com.supremainc.biostar2.sdk.models.v2.user.UserGroup;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.view.SubToolbar;
import com.supremainc.biostar2.widget.CustomDialog;

import java.util.ArrayList;

public class SelectPopup<T> {
    private final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    private Activity mActivity;
    private CustomDialog mDialog;
    private boolean mIsExpand;
    private boolean mIsMultiple;
    private BaseListAdapter mListAdapter;
    private ListView mListView;
    private View mMainView;
    private Popup mPopup;
    private String mSearchText;
    private SubToolbar mSubToolbar;
    private ArrayList<T> mDuplicateItems;
    private int mLimitSize;
    private int mTotal;

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mSubToolbar == null) {
                return;
            }
            mSubToolbar.setSelectedCount(mListAdapter.getCheckedItemCount());
        }
    };

    private ToastPopup mToastPopup;
    private boolean misInfoVisible;
    OnItemsListener mOnItemsListener = new OnItemsListener() {
        @Override
        public void onSuccessNull(int total) {
            setSize(total);
            mSubToolbar.setTotal(total);
            if (mMainView.getVisibility() != View.VISIBLE) {
                mMainView.setVisibility(View.VISIBLE);
                if (!mDialog.isShowing()) {
                    mDialog.show();
                }
            }
        }

        @Override
        public void onNoneData() {
            mSubToolbar.setTotal(0);
            setSize(0);
            mToastPopup.show(mActivity.getString(R.string.none_data), null);
            if (mMainView.getVisibility() != View.VISIBLE) {
                mMainView.setVisibility(View.VISIBLE);
                if (!mDialog.isShowing()) {
                    mDialog.show();
                }
            }
        }

        @Override
        public void onTotalReceive(int total) {
            setSize(total);
            mSubToolbar.setTotal(total);
            mMainView.setVisibility(View.VISIBLE);
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }
    };

    private SearchView.OnCloseListener mSearchClose = new SearchView.OnCloseListener() {
        @Override
        public boolean onClose() {
            onSearch(null);
            return false;
        }
    };

    public SelectPopup(Activity mActivity, Popup popup) {
        this.mActivity = mActivity;
        mPopup = popup;
        mToastPopup = new ToastPopup(mActivity);
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private int dpToPx(double dp) {
        float scale = mActivity.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void generatorAdapter(SelectType type, ArrayList<T> items, int listViewMode) {
        switch (type) {
            case STRING:
                mListAdapter = new SimpleStringAdapter(mActivity, (ArrayList<String>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                break;
            case USER:
                SimpleUserAdapter userAdapter = new SimpleUserAdapter(mActivity, (ArrayList<ListUser>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                mListAdapter = userAdapter;
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                break;
            case DOOR:
                mListAdapter = new SimpleDoorAdapter(mActivity, (ArrayList<ListDoor>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                break;
            case USER_GROUPS:
                mListAdapter = new SimpleUserGroupAdapter(mActivity, (ArrayList<UserGroup>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                mSubToolbar.setVisibleSearch(false, mSearchClose);
                mSubToolbar.showTotal(true);
                break;
            case DEVICE: {
                SimpleDeviceAdapter deviceAdapter = new SimpleDeviceAdapter(mActivity, (ArrayList<ListDevice>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                deviceAdapter.setMasterOnly(false);
                mListAdapter = deviceAdapter;
                mSubToolbar.setVisibleSearch(true, mSearchClose);
            }
            break;
            case DEVICE_FINGERPRINT: {
                SimpleDeviceAdapter deviceAdapter = new SimpleDeviceAdapter(mActivity, (ArrayList<ListDevice>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                deviceAdapter.setMasterOnly(true);
                deviceAdapter.setDeviceSupport(DeviceType.SUPPORT_FINGERPRINT);
                mListAdapter = deviceAdapter;
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                mSubToolbar.showTotal(false);
                break;
            }
            case DEVICE_FINGERPRINT_BIOMINI: {
                SimpleDeviceAdapter deviceAdapter = new SimpleDeviceAdapter(mActivity, (ArrayList<ListDevice>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                deviceAdapter.setMasterOnly(true);
                deviceAdapter.setDeviceSupport(DeviceType.SUPPORT_FINGERPRINT);
                deviceAdapter.setShowType(BaseDeviceAdapter.ShowType.DEVICE_FINGERPRINT_BIOMINI);
                mListAdapter = deviceAdapter;
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                mSubToolbar.showTotal(false);
                break;
            }
            case DEVICE_CARD: {
                SimpleDeviceAdapter deviceAdapter = new SimpleDeviceAdapter(mActivity, (ArrayList<ListDevice>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                deviceAdapter.setMasterOnly(false);
                deviceAdapter.setDeviceSupport(DeviceType.SUPPORT_CARD);
                deviceAdapter.setShowType(BaseDeviceAdapter.ShowType.DEVICE_CARD);
                mListAdapter = deviceAdapter;
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                mSubToolbar.showTotal(false);
                break;
            }
            case DEVICE_CARD_CSN: {
                SimpleDeviceAdapter deviceAdapter = new SimpleDeviceAdapter(mActivity, (ArrayList<ListDevice>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                deviceAdapter.setMasterOnly(false);
                deviceAdapter.setDeviceSupport(DeviceType.SUPPORT_CARD);
                deviceAdapter.setShowType(BaseDeviceAdapter.ShowType.DEVICE_CARD_CSN);
                mListAdapter = deviceAdapter;
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                mSubToolbar.showTotal(false);
                break;
            }
            case DEVICE_CARD_WIEGAND: {
                SimpleDeviceAdapter deviceAdapter = new SimpleDeviceAdapter(mActivity, (ArrayList<ListDevice>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                deviceAdapter.setMasterOnly(false);
                deviceAdapter.setDeviceSupport(DeviceType.SUPPORT_CARD);
                deviceAdapter.setShowType(BaseDeviceAdapter.ShowType.DEVICE_CARD_WIEGAND);
                mListAdapter = deviceAdapter;
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                mSubToolbar.showTotal(false);
                break;
            }
            case DEVICE_CARD_SMARTCARD: {
                SimpleDeviceAdapter deviceAdapter = new SimpleDeviceAdapter(mActivity, (ArrayList<ListDevice>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                deviceAdapter.setMasterOnly(false);
                deviceAdapter.setDeviceSupport(DeviceType.SUPPORT_CARD);
                deviceAdapter.setShowType(BaseDeviceAdapter.ShowType.DEVICE_CARD_SMARTCARD);
                mListAdapter = deviceAdapter;
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                mSubToolbar.showTotal(false);
                break;
            }
            case DEVICE_FACE: {
                SimpleDeviceAdapter deviceAdapter = new SimpleDeviceAdapter(mActivity, (ArrayList<ListDevice>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                deviceAdapter.setMasterOnly(true);
                deviceAdapter.setDeviceSupport(DeviceType.SUPPORT_FACE);
                mListAdapter = deviceAdapter;
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                mSubToolbar.showTotal(false);
                break;
            }
            case CARD:
                mListAdapter = new SimpleCardAdapter(mActivity, (ArrayList<ListCard>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                break;
            case CARD_CSN: {
                SimpleCardAdapter adapter = new SimpleCardAdapter(mActivity, (ArrayList<ListCard>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                adapter.setShowType(BaseCardAdapter.ShowType.CARD_CSN);
                mListAdapter = adapter;
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                break;
            }
            case CARD_WIEGAND: {
                SimpleCardAdapter adapter = new SimpleCardAdapter(mActivity, (ArrayList<ListCard>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                adapter.setShowType(BaseCardAdapter.ShowType.CARD_WIEGAND);
                mListAdapter = adapter;
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                break;
            }
            case CARD_SMARTCARD: {
                SimpleCardAdapter adapter = new SimpleCardAdapter(mActivity, (ArrayList<ListCard>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                adapter.setShowType(BaseCardAdapter.ShowType.CARD_SMARTCARD);
                mListAdapter = adapter;
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                break;
            }
            case CUSTOM:
                mListAdapter = new SimpleCustomAdapter(mActivity, (ArrayList<SelectCustomData>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                break;
            case CLOUD_ROLE:
                mListAdapter = new SimplePermissionAdapter(mActivity, (ArrayList<CloudRole>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                break;
            case V2_CLOUD_ROLE:
                mListAdapter = new SimplePermissionV2Adapter(mActivity, (ArrayList<UserPermission>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                break;
            case ACCESS_GROUPS:
                mListAdapter = new SimpleAccessGroupAdapter(mActivity, (ArrayList<ListAccessGroup>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                mListAdapter.setDuplicateItems(mDuplicateItems);
                mListAdapter.setLimit(mLimitSize);
                break;
            case EVENT_TYPE:
                mListAdapter = new SimpleEventTypeAdapter(mActivity, (ArrayList<EventType>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                break;
            case WIEGAND_FORMAT:
                mListAdapter = new WiegandDataAdapter(mActivity, (ArrayList<WiegandFormat>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                break;
            case SMARTCARD_LAYOUT:
                mListAdapter = new SimpleSmartCardLayoutAdapter(mActivity, (ArrayList<SmartCardLayout>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                break;
            default:
                return;
        }
        if (mLimitSize > 0) {
            mListAdapter.setLimit(mLimitSize);
        }
        if (mIsMultiple) {
            mListAdapter.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        } else {
            mListAdapter.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }

    public ArrayList<T> getResultItems() {
        if (mListAdapter != null) {
            return mListAdapter.getCheckedItems();
        }
        return null;
    }

    public boolean isExpand() {
        return mIsExpand;
    }

    private void onDismissPopup() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onDismissPopup");
        }
        if (mListAdapter != null) {
            mListAdapter.clearItems();
            mListAdapter = null;
        }
        if (mSubToolbar != null) {
            mSubToolbar.hideIme();
        }
    }

    public boolean onSearch(String query) {
        if (!mIsExpand || mListAdapter == null) {
            return false;
        }
        if (mSearchText == null && query == null) {
            return false;
        }
        mSearchText = query;
        mListAdapter.clearChoices();
        if (mSubToolbar != null) {
            mSubToolbar.setSelectedCount(0);
        }
        mListAdapter.getItems(query);
        return true;
    }

    private int pxToDp(int px) {
        DisplayMetrics displayMetrics = mActivity.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void setSize(int count) {
        mTotal = count;
        mSubToolbar.setVisible(misInfoVisible);

        if (count > 4 || count < 0) {
            count = 4;
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        if (params.height != dpToPx(count * 55.2)) {
            params.height = dpToPx(count * 55.2); // TODO change
            // adapter.getListItemHeight();
            mListView.setLayoutParams(params);
        }

    }

    public void show(SelectType type, final OnSelectResultListener<T> listener, ArrayList<T> srcItems, String title, boolean isMultiple) {
        show(type, listener, srcItems, title, isMultiple, false);
    }

    public void show(SelectType type, final OnSelectResultListener<T> listener, ArrayList<T> srcItems, String title, boolean isMultiple, boolean isInfoVisible) {
        if (mActivity.isFinishing()) {
            return;
        }
        onDismissPopup();
        mIsExpand = true;
        mTotal = 0;
        misInfoVisible = isInfoVisible;
        ArrayList<T> items = null;
        if (srcItems != null) {
            items = (ArrayList<T>) srcItems.clone();
        }
        mIsMultiple = isMultiple;
        mDialog = new CustomDialog(mActivity);
        mDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mIsExpand = false;
                onDismissPopup();
            }
        });
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.popup_select, null);
        mListView = (ListView) layout.findViewById(R.id.listview);

        StyledTextView titleView = (StyledTextView) layout.findViewById(R.id.title_text);
        if (title != null) {
            titleView.setText(title);
        }

        mSubToolbar = (SubToolbar) layout.findViewById(R.id.subtoolbar);
        mSubToolbar.init(mActivity);

        if (isMultiple) {
            generatorAdapter(type, items, ListView.CHOICE_MODE_MULTIPLE);
        } else {
            generatorAdapter(type, items, ListView.CHOICE_MODE_SINGLE);
        }
        mSubToolbar.setTotal(mListAdapter.getCount());
        mMainView = layout.findViewById(R.id.main_container);
        mMainView.setTag(type);
        mDialog.setLayout(layout);
        final OnSingleClickListener onClickListener = new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ArrayList<T> result = null;
                if (v.getId() == R.id.positive) {
                    result = getResultItems();
                }
                mDialog.dismiss();
                switch (v.getId()) {
                    case R.id.positive:
                        if (listener != null) {
                            listener.OnResult(result, true);
                        }
                        break;
                    case R.id.negative:
                        if (listener != null) {
                            listener.OnResult(null, false);
                        }
                        break;
                }
            }
        };
        StyledTextView positiveView = (StyledTextView) layout.findViewById(R.id.positive);
        positiveView.setOnClickListener(onClickListener);
        StyledTextView negativeView = (StyledTextView) layout.findViewById(R.id.negative);
        negativeView.setOnClickListener(onClickListener);
        if (isInfoVisible) {
            mSubToolbar.setVisibility(View.VISIBLE);
        } else {
            mSubToolbar.setVisibility(View.GONE);
        }
        if (mActivity.isFinishing()) {
            return;
        }
        if (items == null) {
            mMainView.setVisibility(View.INVISIBLE);
            mListAdapter.getItems(null);
        } else {
            mMainView.setVisibility(View.VISIBLE);
            int count = items.size();
            setSize(count);
            if ((count > 4 || isMultiple) && isInfoVisible) {
                mSubToolbar.setVisibility(View.VISIBLE);
            } else {
                mSubToolbar.setVisibility(View.GONE);
            }
            mDialog.show();
        }
    }

    public void setDuplicateItems(ArrayList<T> items) {
        mDuplicateItems = items;
    }

    public void setLimit(int limit) {
        mLimitSize = limit;
    }

    public enum SelectType {
        STRING, CUSTOM, USER_GROUPS, EVENT_TYPE, DEVICE, USER, DEVICE_FINGERPRINT, DEVICE_FINGERPRINT_BIOMINI, DEVICE_CARD, DEVICE_CARD_CSN, DEVICE_CARD_WIEGAND, DEVICE_CARD_SMARTCARD, DEVICE_FACE,
        CARD, CARD_CSN, CARD_WIEGAND, CARD_SMARTCARD, CARD_MOBILE, ACCESS_LEVEL, DOOR, SCHEDULE, ACCESS_GROUPS, CLOUD_ROLE, V2_CLOUD_ROLE, WIEGAND_FORMAT, SMARTCARD_LAYOUT, MAX
    }

    public interface OnSelectResultListener<T> {
        public void OnResult(ArrayList<T> selectedItem, boolean isPositive);
    }
}
