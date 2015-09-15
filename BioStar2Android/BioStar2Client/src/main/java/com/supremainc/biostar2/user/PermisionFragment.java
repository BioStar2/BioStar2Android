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
package com.supremainc.biostar2.user;

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

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.base.BaseFragment;
import com.supremainc.biostar2.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.popup.SelectPopup;
import com.supremainc.biostar2.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.popup.SelectPopup.SelectType;
import com.supremainc.biostar2.sdk.datatype.PermissionData.CloudRole;
import com.supremainc.biostar2.sdk.datatype.UserData.User;
import com.supremainc.biostar2.view.SubToolbar;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;

import java.io.Serializable;
import java.util.ArrayList;

public class PermisionFragment extends BaseFragment {
    private static final int MODE_DELETE = 1;
    private boolean mIsDisableModify;
    private UserCloudRoleAdapter mItemAdapter;
    private UserSubDepthFragmentLayout mLayout;
    private SelectPopup<CloudRole> mSelectCloudRolePopup;
    private SubToolbar mSubToolbar;
    private User mUserInfo;
    private int mReplacePosition = -1;

    private SubToolbar.SubToolBarEvent mSubToolBarEvent = new SubToolbar.SubToolBarEvent() {
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

    public PermisionFragment() {
        super();
        setType(ScreenType.USER_PERMISSION);
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
                                mUserInfo.roles.remove(i);
                            }
                        }
                        refreshValue();
                    }
                });
            }
        }, getString(R.string.ok), getString(R.string.cancel));
    }

    private void initValue(Bundle savedInstanceState) {
        if (mUserInfo == null) {
            mUserInfo = getExtraData(User.TAG, savedInstanceState);
        }
        Boolean disable = getExtraData(Setting.DISABLE_MODIFY, savedInstanceState);
        if (disable != null) {
            mIsDisableModify = disable;
        }
        if (mSubToolbar == null) {
            mSubToolbar = mLayout.getSubToolbar(mSubToolBarEvent);
        }
        if (mUserInfo.roles == null) {
            mUserInfo.roles = new ArrayList<CloudRole>();
        }
        if (mItemAdapter == null) {
            mItemAdapter = new UserCloudRoleAdapter(mContext, mUserInfo.roles, mLayout.getListView(), new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mSubToolbar == null) {
                        return;
                    }
                    if (mSubMode == MODE_DELETE) {
                        mSubToolbar.setSelectAllViewOff();
                        mSubToolbar.setSelectedCount(mItemAdapter.getCheckedItemCount());
                        ;
                    } else {
                        mReplacePosition = position;
                        showSelectItem();
                    }
                }
            }, mPopup, null, mIsDisableModify);
        }
        refreshValue();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (mUserInfo.roles != null) {
            sendLocalBroadcast(Setting.BROADCAST_UPDATE_PERMISSION, (Serializable) mUserInfo.roles.clone());
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
    public boolean onSearch(String query) {
        if (super.onSearch(query)) {
            return true;
        }
        // if (mSelectCloudRolePopup != null &&
        // mSelectCloudRolePopup.isExpand()) {
        // mSelectCloudRolePopup.onSearch(query);
        // return true;
        // }
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
        mContext.invalidateOptionsMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mLayout == null) {
            mLayout = new UserSubDepthFragmentLayout(this, null);
        }
        View view = mLayout.initView(this, inflater, container, savedInstanceState);
        initBaseValue(mLayout);
        if (!mLayout.isReUsedView()) {
            initValue(savedInstanceState);
            initActionbar(getString(R.string.permission_setting));

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
        }
        return view;
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
        MenuInflater inflater = mContext.getMenuInflater();
        switch (mSubMode) {
            default:
            case MODE_NORMAL:
                initActionbar(getString(R.string.permission_setting));
                inflater.inflate(R.menu.add_delete, menu);
                break;
            case MODE_DELETE:
                initActionbar(getString(R.string.delete_permission));
                inflater.inflate(R.menu.delete_confirm, menu);
                break;
        }
        super.onPrepareOptionsMenu(menu);
    }

    private void refreshValue() {
        clearValue();
        if (mUserInfo.roles == null) {
            mUserInfo.roles = new ArrayList<CloudRole>();
        }
        if (mSelectCloudRolePopup == null) {
            mSelectCloudRolePopup = new SelectPopup<CloudRole>(mContext, mPopup);
        }
        if (mItemAdapter != null) {
            mItemAdapter.clearChoices();
        }
        if (mSubToolbar != null) {
            mSubToolbar.setSelectedCount(mItemAdapter.getCheckedItemCount());
            if (mItemAdapter != null) {
                mSubToolbar.setTotal(mItemAdapter.getCount());
            }
        }
    }

    private void setRole(CloudRole role) {
        for (CloudRole item : mUserInfo.roles) {
            if (item.code.equals(role.code)) {
                mToastPopup.show(getString(R.string.already_assigned), null);
                return;
            }
        }
        try {
            if (mReplacePosition == -1 || mReplacePosition >= mUserInfo.roles.size()) {
                mUserInfo.roles.add(role.clone());
            } else {
                mUserInfo.roles.set(mReplacePosition, role.clone());
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, " " + e.getMessage());
            }
        }
        refreshValue();
    }

    private void showSelectItem() {
        mSelectCloudRolePopup.show(SelectType.CLOUD_ROLE, new OnSelectResultListener<CloudRole>() {
            @Override
            public void OnResult(ArrayList<CloudRole> selectedItem) {
                if (isInValidCheck(null)) {
                    return;
                }
                if (selectedItem == null || selectedItem.size() < 1) {
                    clearValue();
                    return;
                }
                CloudRole role = selectedItem.get(0);
                setRole(role);
            }
        }, null, getString(R.string.select) + " " + getString(R.string.operator), false);
    }
}
