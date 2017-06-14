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

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supremainc.biostar2.sdk.models.v2.user.UserGroup;
import com.supremainc.biostar2.sdk.models.v2.user.UserGroups;
import com.supremainc.biostar2.sdk.provider.UserDataProvider;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseUserGroupAdapter extends BaseListAdapter<UserGroup> {
    protected UserDataProvider mUserDataProvider;

    private Callback<UserGroups> mItemListener = new Callback<UserGroups>() {
        @Override
        public void onFailure(Call<UserGroups> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }

            showRetryPopup(t.getMessage(), new OnPopupClickListener() {
                @Override
                public void OnNegative() {

                }

                @Override
                public void OnPositive() {
                    getItems(mQuery);
                }
            });
        }

        @Override
        public void onResponse(Call<UserGroups> call, Response<UserGroups> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, false, false)) {
                mItemListener.onFailure(call, new Throwable(getResponseErrorMessage(response)));
                return;
            }
            UserGroups userGroups = response.body();
            if (userGroups.records == null || userGroups.records.size() < 1) {
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
            Collections.sort(userGroups.records, mComparator);
            setData(userGroups.records);
            mTotal = userGroups.total;
            if (mTotal < mItems.size()) {
                mTotal = mItems.size();
            }
            if (mOnItemsListener != null) {
                mOnItemsListener.onTotalReceive(mTotal);
            }
        }
    };

    Runnable mRunGetItems = new Runnable() {
        @Override
        public void run() {
            if (isInValidCheck()) {
                return;
            }
            request(mUserDataProvider.getUserGroups(0, 9999, null, mItemListener));
        }
    };


    private Comparator<UserGroup> mComparator = new Comparator<UserGroup>() {
        @Override
        public int compare(UserGroup lhs, UserGroup rhs) {
            if (lhs.name == null && rhs.name == null) {
                return 0;
            }
            if (lhs.name == null) {
                return -1;
            }
            if (rhs.name == null) {
                return -1;
            }
            return lhs.name.compareToIgnoreCase(rhs.name);
        }
    };

    public BaseUserGroupAdapter(Activity context, ArrayList<UserGroup> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        mUserDataProvider = UserDataProvider.getInstance(context);
    }

    @Override
    public void getItems(String query) {
        mQuery = query;
        mListView.removeCallbacks(mRunGetItems);
        clearRequest();
        showWait(SwipyRefreshLayoutDirection.TOP);
        mHandler.postDelayed(mRunGetItems, 100);
    }
}
