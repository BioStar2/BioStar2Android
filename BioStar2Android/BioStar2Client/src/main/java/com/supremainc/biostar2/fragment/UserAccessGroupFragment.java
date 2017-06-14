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

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.adapter.UserAccessGroupAdapter;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.models.v2.accesscontrol.ListAccessGroup;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.view.SubToolbar;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;
import com.supremainc.biostar2.widget.popup.SelectPopup;
import com.supremainc.biostar2.widget.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.widget.popup.SelectPopup.SelectType;

import java.io.Serializable;
import java.util.ArrayList;

public class UserAccessGroupFragment extends BaseFragment {
    private static final int MODE_DELETE = 1;
    private UserAccessGroupAdapter mItemAdapter;
    private SelectPopup<ListAccessGroup> mSelectAccessGroupPopup;
    private SubToolbar mSubToolbar;
    private User mUserInfo;
    private boolean mIsDisableModify;
    private int mReplacePosition = -1;

    private SubToolbar.SubToolBarListener mSubToolBarEvent = new SubToolbar.SubToolBarListener() {
        @Override
        public void onClickSelectAll() {
            if (mSubToolbar.showReverseSelectAll()) {
                if (mItemAdapter != null) {
                    mItemAdapter.selectChoices();
                    mSubToolbar.setSelectedCount(mItemAdapter.getCheckedItemCount());
                }
            } else {
                if (mItemAdapter != null) {
                    mItemAdapter.clearChoices();
                    mSubToolbar.setSelectedCount(0);
                }
            }
        }
    };

    public UserAccessGroupFragment() {
        super();
        setType(ScreenType.USER_ACCESS_GROUP);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void clearValue() {
        mReplacePosition = -1;
    }

    private void deleteConfirm(int selectedCount) {
        mPopup.show(PopupType.ALERT, getString(R.string.delete_confirm_question), getString(R.string.selected_count) + " " + selectedCount, new OnPopupClickListener() {
            @Override
            public void OnNegative() {
            }

            @Override
            public void OnPositive() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        int i = mItemAdapter.getCount() - 1;
                        for (; i >= 0; i--) {
                            boolean isCheck = mItemAdapter.isItemChecked(i);
                            if (isCheck) {
                                mUserInfo.access_groups.remove(i);
                            }
                        }
                        refreshValue();
                    }
                });
            }
        }, getString(R.string.ok), getString(R.string.cancel));
    }

    private boolean initValue(Bundle savedInstanceState) {
        if (mSubToolbar == null) {
            mSubToolbar = (SubToolbar) mRootView.findViewById(R.id.subtoolbar);
            mSubToolbar.init(mSubToolBarEvent, getActivity());
            mSubToolbar.setVisibleSearch(false, null);
            mSubToolbar.showMultipleSelectInfo(false, 0);
        }
        if (mUserInfo == null) {
            mUserInfo = getExtraData(User.TAG, savedInstanceState);
        }
        if (mUserInfo == null) {
            return false;
        }
        Boolean disable = getExtraData(Setting.DISABLE_MODIFY, savedInstanceState);
        if (disable != null) {
            mIsDisableModify = disable;
        }
        if (mUserInfo.access_groups == null) {
            mUserInfo.access_groups = new ArrayList<ListAccessGroup>();
        }
        if (mItemAdapter == null) {
            mItemAdapter = new UserAccessGroupAdapter(mActivity, mUserInfo.access_groups, getListView(), new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mSubToolbar == null) {
                        return;
                    }
                    if (mSubMode == MODE_DELETE) {
                        mSubToolbar.setSelectAllViewOff();
                        int count = mItemAdapter.getCheckedItemCount();
                        mSubToolbar.setSelectedCount(count);
                        if (count == mItemAdapter.getAvailableTotal()) {
                            if (!mSubToolbar.getSelectAll()) {
                                mSubToolbar.showReverseSelectAll();
                            }
                        }
                        return;
                    }
                    ListAccessGroup item = (ListAccessGroup) mItemAdapter.getItem(position);
                    if (item.isIncludedByUserGroup()) {
                        return;
                    }
                    mReplacePosition = position;
                    showSelectItem();
                }
            }, mPopup, null, mIsDisableModify);
        }

        refreshValue();
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (mUserInfo.access_groups != null) {
            sendLocalBroadcast(Setting.BROADCAST_UPDATE_USER_ACCESS_GROUP, (Serializable) mUserInfo.access_groups.clone());
        }
        if (mItemAdapter != null) {
            mItemAdapter.clearItems();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_delete_confirm:
                int selectedCount = mItemAdapter.getCheckedItemCount();
                if (selectedCount < 1) {
                    mToastPopup.show(getString(R.string.selected_none), null);
                    return true;
                }
                deleteConfirm(selectedCount);
                break;
            case R.id.action_add:
                if (mUserInfo.access_groups.size() >= 16) {
                    mToastPopup.show(getString(R.string.max_size), null);
                    return true;
                }
                mReplacePosition = mUserInfo.access_groups.size();
                showSelectItem();
                break;
            case R.id.action_delete:
                setSubMode(MODE_DELETE);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void setSubMode(int mode) {
        mSubMode = mode;
        switch (mode) {
            case MODE_NORMAL:
                mItemAdapter.setChoiceMode(ListView.CHOICE_MODE_NONE);
                mSubToolbar.showMultipleSelectInfo(false, 0);
                break;
            case MODE_DELETE:
                mItemAdapter.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                mSubToolbar.showMultipleSelectInfo(true, mItemAdapter.getCheckedItemCount());
                break;
        }
        mActivity.invalidateOptionsMenu();
    }

    private void onCreateMenu(Menu menu, MenuInflater inflater) {
        switch (mSubMode) {
            default:
            case MODE_NORMAL:
                initActionbar(getString(R.string.access_group));
                inflater.inflate(R.menu.add_delete, menu);
                break;
            case MODE_DELETE:
                initActionbar(getString(R.string.delete) + " " + getString(R.string.access_group));
                inflater.inflate(R.menu.delete_confirm, menu);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setResID(R.layout.fragment_list);
        super.onCreateView(inflater, container, savedInstanceState);
        if (!mIsReUsed) {
            initValue(savedInstanceState);
            initActionbar(getString(R.string.access_group));
            mRootView.invalidate();
        }

        if (mUserInfo == null) {
            Log.e(TAG, "data is null");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mToastPopup.show(getString(R.string.none_data), null);
                    mScreenControl.backScreen();
                }
            }, 1000);
            return null;
        }
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState");
        User bundleItem = null;
        try {
            bundleItem = (User) mUserInfo.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return;
        }
        outState.putSerializable(User.TAG, bundleItem);
        outState.putSerializable(Setting.DISABLE_MODIFY, mIsDisableModify);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (mIsDisableModify) {
            return;
        }
        onCreateMenu(menu, mActivity.getMenuInflater());
        super.onPrepareOptionsMenu(menu);
    }

    private void refreshValue() {
        clearValue();
        if (mUserInfo.access_groups == null) {
            mUserInfo.access_groups = new ArrayList<ListAccessGroup>();
        }
        if (mItemAdapter != null) {
            mItemAdapter.setData(mUserInfo.access_groups);
            mItemAdapter.clearChoices();
        }
        if (mSelectAccessGroupPopup == null) {
            mSelectAccessGroupPopup = new SelectPopup<ListAccessGroup>(mActivity, mPopup);
        }
        if (mSubToolbar != null) {
            mSubToolbar.setSelectedCount(mItemAdapter.getCheckedItemCount());
            if (mItemAdapter != null) {
                mSubToolbar.setTotal(mItemAdapter.getCount());
            }
        }
    }

    private void showSelectItem() {
        boolean isMultiple = false;
        if (mReplacePosition == -1 || mReplacePosition > mUserInfo.access_groups.size() - 1) {
            isMultiple = true;
            int limit = Setting.LIMIT_USER_ACCESS_GROUP_SIZE - mUserInfo.access_groups.size();
            mSelectAccessGroupPopup.setLimit(limit);
        }
        mSelectAccessGroupPopup.setDuplicateItems(mUserInfo.access_groups);
        mSelectAccessGroupPopup.show(SelectType.ACCESS_GROUPS, new OnSelectResultListener<ListAccessGroup>() {
            @Override
            public void OnResult(ArrayList<ListAccessGroup> selectedItem, boolean isPositive) {
                if (isInValidCheck()) {
                    return;
                }
                if (selectedItem == null) {
                    clearValue();
                    return;
                }

                if (mReplacePosition == -1 || mReplacePosition >= mUserInfo.access_groups.size()) {
                    for (ListAccessGroup item : selectedItem) {
                        boolean isExist = false;
                        for (ListAccessGroup oldItem : mUserInfo.access_groups) {
                            if (item.id.equals(oldItem.id)) {
                                isExist = true;
                                break;
                            }
                        }
                        if (isExist) {
                            mToastPopup.show(getString(R.string.already_assigned), item.name);
                        } else {
                            if (mUserInfo.access_groups.size() >= 16) {
                                mToastPopup.show(getString(R.string.max_size), null);
                            } else {
                                mUserInfo.access_groups.add(item);
                            }
                        }
                    }
                } else {
                    for (ListAccessGroup item : mUserInfo.access_groups) {
                        if (item.id.equals(selectedItem.get(0).id)) {
                            mToastPopup.show(getString(R.string.already_assigned), null);
                            return;
                        }
                    }
                    mUserInfo.access_groups.set(mReplacePosition, selectedItem.get(0));
                }
                refreshValue();
            }
        }, null, getString(R.string.select_access_group), isMultiple, true);
    }
}
