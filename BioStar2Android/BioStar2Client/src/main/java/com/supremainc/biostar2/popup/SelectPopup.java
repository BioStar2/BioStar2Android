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
package com.supremainc.biostar2.popup;

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
import com.supremainc.biostar2.base.BaseAccessGroupAdapter;
import com.supremainc.biostar2.base.BaseCardAdapter;
import com.supremainc.biostar2.base.BaseCustomAdapter;
import com.supremainc.biostar2.base.BaseDeviceAdapter;
import com.supremainc.biostar2.base.BaseDoorAdapter;
import com.supremainc.biostar2.base.BaseEventTypeAdapter;
import com.supremainc.biostar2.base.BaseListAdapter;
import com.supremainc.biostar2.base.BaseListAdapter.OnItemsListener;
import com.supremainc.biostar2.base.BasePermissionAdapter;
import com.supremainc.biostar2.base.BaseStringAdapter;
import com.supremainc.biostar2.base.BaseUserAdapter;
import com.supremainc.biostar2.base.BaseUserGroupAdapter;
import com.supremainc.biostar2.sdk.datatype.AccessGroupData.ListAccessGroup;
import com.supremainc.biostar2.sdk.datatype.CardData.ListCard;
import com.supremainc.biostar2.sdk.datatype.DeviceData.ListDevice;
import com.supremainc.biostar2.sdk.datatype.DeviceTypeData;
import com.supremainc.biostar2.sdk.datatype.DoorData.ListDoor;
import com.supremainc.biostar2.sdk.datatype.EventLogData.EventType;
import com.supremainc.biostar2.sdk.datatype.PermissionData.CloudRole;
import com.supremainc.biostar2.sdk.datatype.UserData.ListUser;
import com.supremainc.biostar2.sdk.datatype.UserGroupData.UserGroup;
import com.supremainc.biostar2.view.SubToolbar;
import com.supremainc.biostar2.widget.CustomDialog;
import com.supremainc.biostar2.widget.OnSingleClickListener;
import com.supremainc.biostar2.widget.StyledTextView;

import java.util.ArrayList;

public class SelectPopup<T> {
    private final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    private Activity mContext;
    private CustomDialog mDialog;
    private boolean mIsExpand;
    private boolean mIsMultiple;
    private BaseListAdapter mListAdapter;
    private ListView mListView;
    private View mMainView;
    private Popup mPopup;
    private String mSearchText;
    private SubToolbar mSubToolbar;

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
        public void onSuccessNull() {
            mToastPopup.show(mContext.getString(R.string.none_data), null);
        }

        @Override
        public void onTotalReceive(int total) {
            setSize(total);
            mSubToolbar.setTotal(total);
            mMainView.setVisibility(View.VISIBLE);
            mDialog.show();
        }
    };

    private SearchView.OnCloseListener mSearchClose = new SearchView.OnCloseListener() {
        @Override
        public boolean onClose() {
            onSearch(null);
            return false;
        }
    };

    public SelectPopup(Activity mContext, Popup popup) {
        this.mContext = mContext;
        mPopup = popup;
        mToastPopup = new ToastPopup(mContext);
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private int dpToPx(double dp) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void generatorAdapter(SelectType type, ArrayList<T> items, int listViewMode) {
        switch (type) {
            case STRING:
                mListAdapter = new BaseStringAdapter(mContext, (ArrayList<String>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                break;
            case USER:
                BaseUserAdapter userAdapter = new BaseUserAdapter(mContext, (ArrayList<ListUser>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                userAdapter.setHasphoto(false);
                mListAdapter = userAdapter;
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                break;
            case DOOR:
                mListAdapter = new BaseDoorAdapter(mContext, (ArrayList<ListDoor>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                break;
            case USER_GROUPS:
                mListAdapter = new BaseUserGroupAdapter(mContext, (ArrayList<UserGroup>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                break;
            case DEVICE: {
                BaseDeviceAdapter deviceAdapter = new BaseDeviceAdapter(mContext, (ArrayList<ListDevice>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                deviceAdapter.setMasterOnly(false);
                mListAdapter = deviceAdapter;
                mSubToolbar.setVisibleSearch(true, mSearchClose);
            }
            break;
            case DEVICE_FINGERPRINT: {
                BaseDeviceAdapter deviceAdapter = new BaseDeviceAdapter(mContext, (ArrayList<ListDevice>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                deviceAdapter.setMasterOnly(true);
                deviceAdapter.setDeviceSupport(DeviceTypeData.SUPPORT_FINGERPRINT);
                mListAdapter = deviceAdapter;
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                break;
            }
            case DEVICE_CARD: {
                BaseDeviceAdapter deviceAdapter = new BaseDeviceAdapter(mContext, (ArrayList<ListDevice>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                deviceAdapter.setMasterOnly(false);
                deviceAdapter.setDeviceSupport(DeviceTypeData.SUPPORT_CARD);
                mListAdapter = deviceAdapter;
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                break;
            }
            case CARD:
                mListAdapter = new BaseCardAdapter(mContext, (ArrayList<ListCard>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                mSubToolbar.setVisibleSearch(true, mSearchClose);
                break;
            case CUSTOM:
                mListAdapter = new BaseCustomAdapter(mContext, (ArrayList<SelectCustomData>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                break;
            case CLOUD_ROLE:
                mListAdapter = new BasePermissionAdapter(mContext, (ArrayList<CloudRole>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                break;
            case ACCESS_GROUPS:
                mListAdapter = new BaseAccessGroupAdapter(mContext, (ArrayList<ListAccessGroup>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                break;
            case EVENT_TYPE:
                mListAdapter = new BaseEventTypeAdapter(mContext, (ArrayList<EventType>) items, mListView, mOnItemClickListener, mPopup, mOnItemsListener);
                mSubToolbar.setVisibleSearch(true,mSearchClose);
                break;
            default:
                return;
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
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void setSize(int count) {
        if (count > 4 || misInfoVisible) {
            mSubToolbar.setVisible(true);
        } else {
            mSubToolbar.setVisible(false);
        }

        if (count > 4 || count < 0) {
            count = 4;
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = dpToPx(count * 55.2); // TODO change
        // adapter.getListItemHeight();
        mListView.setLayoutParams(params);

    }

    public void show(SelectType type, final OnSelectResultListener<T> listener, ArrayList<T> srcItems, String title, boolean isMultiple) {
        show(type, listener, srcItems, title, isMultiple, false);
    }

    public void show(SelectType type, final OnSelectResultListener<T> listener, ArrayList<T> srcItems, String title, boolean isMultiple, boolean isInfoVisible) {
        if (mContext.isFinishing()) {
            return;
        }
        onDismissPopup();
        mIsExpand = true;
        misInfoVisible = isInfoVisible;
        ArrayList<T> items = null;
        if (srcItems != null) {
            items = (ArrayList<T>) srcItems.clone();
        }
        mIsMultiple = isMultiple;
        mDialog = new CustomDialog(mContext);
        mDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mIsExpand = false;
                onDismissPopup();
            }
        });
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.popup_select, null);
        mListView = (ListView) layout.findViewById(R.id.listview);

        StyledTextView titleView = (StyledTextView) layout.findViewById(R.id.title_text);
        if (title != null) {
            titleView.setText(title);
        }
        View subToolbar = layout.findViewById(R.id.sub_toolbar);
        if (isInfoVisible) {
            subToolbar.setVisibility(View.VISIBLE);
        } else {
            subToolbar.setVisibility(View.GONE);
        }
        mSubToolbar = new SubToolbar(mContext, layout.findViewById(R.id.sub_toolbar), null);
        if (isMultiple) {
            generatorAdapter(type, items, ListView.CHOICE_MODE_MULTIPLE);
        } else {
            generatorAdapter(type, items, ListView.CHOICE_MODE_SINGLE);
        }
        mSubToolbar.setTotal(mListAdapter.getCount());
        mMainView = layout.findViewById(R.id.main_container);

        mDialog.setLayout(layout);
        final OnSingleClickListener onClickListener = new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                switch (v.getId()) {
                    case R.id.positive:
                        if (listener != null) {
                            listener.OnResult(getResultItems());
                        }
                        mDialog.dismiss();
                        break;
                    case R.id.negative:
                        if (listener != null) {
                            listener.OnResult(null);
                        }
                        mDialog.dismiss();
                        break;
                }
            }
        };
        StyledTextView positiveView = (StyledTextView) layout.findViewById(R.id.positive);
        positiveView.setOnClickListener(onClickListener);
        StyledTextView negativeView = (StyledTextView) layout.findViewById(R.id.negative);
        negativeView.setOnClickListener(onClickListener);

        if (mContext.isFinishing()) {
            return;
        }
        if (items == null) {
            mMainView.setVisibility(View.INVISIBLE);
            mListAdapter.getItems(null);
        } else {
            mMainView.setVisibility(View.VISIBLE);
            int count = items.size();
            setSize(count);
            if (count > 4) {
                subToolbar.setVisibility(View.VISIBLE);
            } else {
                subToolbar.setVisibility(View.GONE);
            }
            mDialog.show();
        }
    }

    public enum SelectType {
        STRING, CUSTOM, USER_GROUPS, EVENT_TYPE, DEVICE, USER, DEVICE_FINGERPRINT, DEVICE_CARD, CARD, ACCESS_LEVEL, DOOR, SCHEDULE, ACCESS_GROUPS, CLOUD_ROLE, MAX
    }

    public interface OnSelectResultListener<T> {
        public void OnResult(ArrayList<T> selectedItem);
    }
}
