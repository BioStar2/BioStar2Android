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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SearchView;
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
import com.supremainc.biostar2.adapter.PhotoUserAdapter;
import com.supremainc.biostar2.adapter.base.BaseListAdapter.OnItemsListener;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.models.v2.common.BioStarSetting;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.models.v2.common.VersionData;
import com.supremainc.biostar2.sdk.models.v2.permission.PermissionModule;
import com.supremainc.biostar2.sdk.models.v2.user.ListUser;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.sdk.models.v2.user.UserGroup;
import com.supremainc.biostar2.sdk.models.v2.user.UserGroups;
import com.supremainc.biostar2.view.SubToolbar;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;
import com.supremainc.biostar2.widget.popup.SelectPopup;
import com.supremainc.biostar2.widget.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.widget.popup.SelectPopup.SelectType;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.supremainc.biostar2.R.string.all_users;

public class UserListFragment extends BaseFragment {
    protected static final int MODE_DELETE = 1;
    private String mSearchText = null;
    private SelectPopup<UserGroup> mSelectUserGroupsPopup;
    private SubToolbar mSubToolbar;
    private String mTitle;
    private PhotoUserAdapter mUserAdapter;
    private int mTotal = 0;
    private UserGroup mUserGroup;
    private int mRequestDeleteUserCount;
    private Callback<User> mItemClickListener = new Callback<User>() {
        @Override
        public void onFailure(Call<User> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showErrorPopup(t.getMessage(),false);
        }

        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true, false)) {
                return;
            }
            try {
                User arg = response.body().clone();
                Bundle bundle = new Bundle();
                bundle.putSerializable(User.TAG, arg);
                mScreenControl.addScreen(ScreenType.USER_INQURIY, bundle);
            } catch (Exception e) {
                showErrorPopup(e.getMessage(),false);
            }
        }
    };

    private Callback<UserGroups> mUserGroupsListener = new Callback<UserGroups>() {
        @Override
        public void onFailure(Call<UserGroups> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showErrorPopup(t.getMessage(),false);
        }

        @Override
        public void onResponse(Call<UserGroups> call, Response<UserGroups> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true, false)) {
                return;
            }
            try {
                Bundle bundle = new Bundle();
                bundle.putSerializable(UserGroup.TAG, response.body().records.get(0));
                mScreenControl.addScreen(ScreenType.USER_MODIFY, bundle);
            } catch (Exception e) {
                showErrorPopup(e.getMessage(),false);
            }
        }
    };

    private Callback<BioStarSetting> mSettingListener = new Callback<BioStarSetting>() {
        @Override
        public void onFailure(Call<BioStarSetting> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            addUser();
        }

        @Override
        public void onResponse(Call<BioStarSetting> call, Response<BioStarSetting> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            addUser();
        }
    };

    private OnItemsListener mOnUsersListener = new OnItemsListener() {
        @Override
        public void onSuccessNull(int total) {
            mIsDataReceived = true;
            setTotal(total);
        }

        @Override
        public void onNoneData() {
            mToastPopup.show(getString(R.string.none_data), null);
            setTotal(0);
        }

        @Override
        public void onTotalReceive(int total) {
            mIsDataReceived = true;
            setTotal(total);
        }
    };


    private Callback<ResponseStatus> mDeleteListener = new Callback<ResponseStatus>() {
        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showErrorPopup(t.getMessage(),false);
        }

        @Override
        public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true, false)) {
                return;
            }
            mUserAdapter.clearChoices();
            mUserAdapter.getItems(mSearchText);
            if (mSubToolbar != null) {
                mSubToolbar.setSelectedCount(0);
            }
            mPopup.show(PopupType.CONFIRM, getString(R.string.info), getString(R.string.deleted_user) + " " + mRequestDeleteUserCount, null, getString(R.string.ok), null);
        }
    };

    private SubToolbar.SubToolBarListener mSubToolBarEvent = new SubToolbar.SubToolBarListener() {
        @Override
        public void onClickSelectAll() {
            if (mSubToolbar.showReverseSelectAll()) {
                if (mUserAdapter != null) {
                    mUserAdapter.selectChoices();
                    mSubToolbar.setSelectedCount(mUserAdapter.getCheckedItemCount());
                }
            } else {
                if (mUserAdapter != null) {
                    mUserAdapter.clearChoices();
                    mSubToolbar.setSelectedCount(0);
                }
            }
        }
    };

    public UserListFragment() {
        super();
        setType(ScreenType.USER);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void addUser() {
        UserGroup userGroup = null;
        if (mUserGroup == null) {
            ArrayList<String> list = mPermissionDataProvider.getDefaultAllowUserGroupSize();
            for (String groupID : list) {
                if (groupID.equals("1")) {
                    userGroup = new UserGroup(getString(R.string.all_users), "1");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(UserGroup.TAG, userGroup);
                    mScreenControl.addScreen(ScreenType.USER_MODIFY, bundle);
                    return;
                }
            }
            if (list.size() == 1) {
                mPopup.showWait(mCancelExitListener);
                request(mUserDataProvider.getUserGroups( 0, 1, null, mUserGroupsListener));
                return;
            }
            mSelectUserGroupsPopup.show(SelectType.USER_GROUPS, new OnSelectResultListener<UserGroup>() {
                @Override
                public void OnResult(ArrayList<UserGroup> selectedItem, boolean isPositive) {
                    if (isInValidCheck()) {
                        return;
                    }
                    if (selectedItem == null) {
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(UserGroup.TAG, selectedItem.get(0));
                    mScreenControl.addScreen(ScreenType.USER_MODIFY, bundle);
                }
            }, null, getString(R.string.select_user_group), false, true);
        } else {
            try {
                userGroup = mUserGroup.clone();
            } catch (CloneNotSupportedException e) {
                Log.e(TAG, "selected user clone fail");
                e.printStackTrace();
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(UserGroup.TAG, userGroup);
            mScreenControl.addScreen(ScreenType.USER_MODIFY, bundle);
        }
    }

    private void setTotal(int total) {
        if (mTotal != total) {
            mSubToolbar.setTotal(total);
            mTotal = total;
            if (mSearchText == null && mUserAdapter != null && (mUserAdapter.getuserGroupId() == null)) {
                sendLocalBroadcast(Setting.BROADCAST_USER_COUNT, total);
            }
        }
    }

    private void applyPermission() {
        ActivityCompat.invalidateOptionsMenu(this.getActivity());
    }

    private void deleteConfirm(int selectedCount) {
        mPopup.show(PopupType.ALERT, getString(R.string.delete_confirm_question), getString(R.string.selected_count) + " " + selectedCount, new OnPopupClickListener() {
            @Override
            public void OnNegative() {
            }

            @Override
            public void OnPositive() {
                deleteDo();
            }


        }, getString(R.string.ok), getString(R.string.cancel));
    }

    private void deleteDo() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPopup.showWait(mCancelExitListener);
                ArrayList<ListUser> users = mUserAdapter.getCheckedItems();
                mRequestDeleteUserCount = users.size();
                request(mUserDataProvider.deleteUsers(users, mDeleteListener));
            }
        });
    }

    private void initValue() {
        mSelectUserGroupsPopup = new SelectPopup<UserGroup>(mActivity, mPopup);
        if (mSubToolbar == null) {
            mSubToolbar = (SubToolbar) mRootView.findViewById(R.id.subtoolbar);
            mSubToolbar.init(getActivity());
            mSubToolbar.setVisibleSearch(true, new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    if (mSearchText == null) {
                        return false;
                    }
                    onSearch(null);
                    return false;
                }
            });
            if (!Setting.IS_DELETE_ALL_USER) {
                mSubToolbar.setSelectAllViewGone(true);
            }
            mSubToolbar.showMultipleSelectInfo(false, 0);
        }

        if (mUserAdapter == null) {
            mUserAdapter = new PhotoUserAdapter(mActivity, null, getListView(), new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mSubToolbar == null) {
                        return;
                    }
                    if (mSubMode == MODE_DELETE) {
                        mSubToolbar.setSelectAllViewOff();
                        int count = mUserAdapter.getCheckedItemCount();
                        mSubToolbar.setSelectedCount(count);
                        if (count == mUserAdapter.getCount()) {
                            if (!mSubToolbar.getSelectAll()) {
                                mSubToolbar.showReverseSelectAll();
                            }
                        }
                    } else {
                        ListUser user = (ListUser) mUserAdapter.getItem(position);
                        if (user == null) {
                            return;
                        }
                        mPopup.showWait(mCancelStayListener);
                        request(mUserDataProvider.getUser(user.user_id, mItemClickListener));
                    }
                }
            }, mPopup, mOnUsersListener);
//            mUserAdapter.setUserGroupId(mPermissionDataProvider.getDefaultAllowUserGroup());
            mUserAdapter.setSwipyRefreshLayout(getSwipeyLayout(), getFab());
        }
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mIsDataReceived && mUserAdapter != null) {
            mUserAdapter.getItems(mSearchText);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSubToolbar != null) {
            mSubToolbar.hideIme();
        }
    }

    @Override
    public void onDestroy() {
        if (mUserAdapter != null) {
            mUserAdapter.clearItems();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        if (mSubToolbar != null) {
            mSubToolbar.hideIme();
        }
        switch (item.getItemId()) {
            case R.id.action_delete_confirm:
                int selectedCount = mUserAdapter.getCheckedItemCount();
                if (selectedCount < 1) {
                    mToastPopup.show(getString(R.string.selected_none), null);
                    return true;
                }
                deleteConfirm(selectedCount);
                break;
            case R.id.action_delete:
                setSubMode(MODE_DELETE);
                break;
            case R.id.action_add:
                if (VersionData.getCloudVersion(mActivity) > 1) {
//                    String message = "";
//                    if (!mPermissionDataProvider.getPermission(PermissionModule.ACCESS_GROUP, false)) {
//                        message = getString(R.string.guide_feature_permission)+"\n"+PermissionModule.ACCESS_GROUP.mName;
//                    }
//
//                    if (!message.isEmpty()) {
//                        mPopup.show(PopupType.ALERT, message, null, null, null);
//                        return true;
//                    }
                    mPopup.showWait(mCancelStayListener);
                    request(mCommonDataProvider.getBioStarSetting(mSettingListener));
                } else {
                    addUser();
                }
                return true;
            case R.id.action_filter:
                mSelectUserGroupsPopup.show(SelectType.USER_GROUPS, new OnSelectResultListener<UserGroup>() {
                    @Override
                    public void OnResult(ArrayList<UserGroup> selectedItem, boolean isPositive) {
                        if (isInValidCheck()) {
                            return;
                        }
                        if (selectedItem == null) {
                            return;
                        }
                        mUserGroup = selectedItem.get(0);
                        if (mUserAdapter != null) {
                            mUserAdapter.setUserGroupId(mUserGroup.id);
                            mUserAdapter.getItems(mSearchText);
                        }
                        mTitle = mUserGroup.name;
                        initActionbar(mUserGroup.name);
                    }
                }, null, getString(R.string.select_user_group), false, true);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onSearch(String query) {
        if (super.onSearch(query)) {
            return true;
        }
        mSearchText = query;
        if (mUserAdapter == null && mSubToolbar == null) {
            return true;
        }
        mUserAdapter.clearChoices();
        mSubToolbar.setSelectedCount(0);
        mUserAdapter.getItems(mSearchText);
        return true;
    }

    protected void registerBroadcast() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (mIsDestroy) {
                        return;
                    }
                    if (action.equals(Setting.BROADCAST_USER)) {
                        User user = (User) getExtraData(Setting.BROADCAST_USER, intent);
                        if (user != null) {
                            if (mUserAdapter.modifyItem(user)) {
                                return;
                            }
                        }
                        if (isResumed()) {
                            mUserAdapter.getItems(mSearchText);
                        } else {
                            mIsDataReceived = false;
                        }
                    } else if (action.equals(Setting.BROADCAST_REROGIN)) {
                        applyPermission();
                    } else if (action.equals(Setting.BROADCAST_UPDATE_CARD)) {
                        User user = getExtraData(Setting.BROADCAST_UPDATE_CARD, intent);
                        if (user == null) {
                            return;
                        }
                        mUserAdapter.modifyCardItem(user.user_id, user.card_count);
                        return;
                    } else if (action.equals(Setting.BROADCAST_UPDATE_FINGER)) {
                        User user = getExtraData(Setting.BROADCAST_UPDATE_FINGER, intent);
                        if (user == null) {
                            return;
                        }

                        Log.e(TAG, "user.user_id" + user.user_id);
                        Log.e(TAG, "user.fingerprint_count" + user.fingerprint_count);
                        Log.e(TAG, "user.fingerprint_template_count" + user.fingerprint_template_count);
                        if (VersionData.getCloudVersion(mActivity) < 2) {
                            mUserAdapter.modifyFingerPrintItem(user.user_id, user.fingerprint_count);
                        } else {
                            mUserAdapter.modifyFingerPrintItem(user.user_id, user.fingerprint_template_count);
                        }

                        return;
                    } else if (action.equals(Setting.BROADCAST_UPDATE_FACE)) {
                        User user = getExtraData(Setting.BROADCAST_UPDATE_FACE, intent);
                        if (user == null) {
                            return;
                        }
                        mUserAdapter.modifyFaceItem(user);
                        return;
                    }

                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_USER);
            intentFilter.addAction(Setting.BROADCAST_REROGIN);
            if (VersionData.getCloudVersion(mActivity) > 1) {
                intentFilter.addAction(Setting.BROADCAST_UPDATE_CARD);
            }
            if (VersionData.getCloudVersion(mActivity) > 1) {
                intentFilter.addAction(Setting.BROADCAST_UPDATE_FINGER);
            }
            intentFilter.addAction(Setting.BROADCAST_UPDATE_FACE);

            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
    }

    @Override
    protected void setSubMode(int mode) {
        mSubMode = mode;
        switch (mode) {
            case MODE_NORMAL:
                mUserAdapter.setChoiceMode(ListView.CHOICE_MODE_NONE);
                mSubToolbar.showMultipleSelectInfo(false, 0);
                break;
            case MODE_DELETE:
                mUserAdapter.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                mSubToolbar.showMultipleSelectInfo(true, 0);
                break;
        }
        ActivityCompat.invalidateOptionsMenu(this.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setResID(R.layout.fragment_list_swpiy);
        super.onCreateView(inflater, container, savedInstanceState);
        if (!mIsReUsed) {
            initValue();
            mTitle = getString(all_users);
            initActionbar(mTitle);
            mRootView.invalidate();
        }
        return mRootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = mActivity.getMenuInflater();
        if (mPermissionDataProvider.getPermission(PermissionModule.USER, true)) {
            switch (mSubMode) {
                default:
                case MODE_NORMAL:
                    initActionbar(mTitle);
                    inflater.inflate(R.menu.user_list_admin, menu);
                    break;
                case MODE_DELETE:
                    String lang = getString(R.string.language);
                    if ("ko".equals(lang) || "ja".equals(lang)) {
                        initActionbar(getString(R.string.user) + " " + getString(R.string.delete));
                    } else {
                        initActionbar(getString(R.string.delete) + " " + getString(R.string.user));
                    }
                    inflater.inflate(R.menu.delete_confirm, menu);
                    break;
            }
        } else {
            inflater.inflate(R.menu.filter, menu);
        }
        super.onPrepareOptionsMenu(menu);
    }
}
