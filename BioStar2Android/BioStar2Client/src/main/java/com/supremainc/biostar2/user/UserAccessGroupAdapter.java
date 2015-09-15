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

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.base.BaseListAdapter;
import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.sdk.datatype.AccessGroupData.ListAccessGroup;

import java.util.ArrayList;

public class UserAccessGroupAdapter extends BaseListAdapter<ListAccessGroup> {
    private boolean isAllDisable;
    private OnItemsListener mOnItemsListener;

    public UserAccessGroupAdapter(Activity context, ArrayList<ListAccessGroup> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener, boolean isAllDisable) {
        super(context, items, listView, popup);
        listView.setAdapter(this);
        setOnItemClickListener(itemClickListener);
        mOnItemsListener = onItemsListener;
        this.isAllDisable = isAllDisable;
    }

    @Override
    public void clearItems() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "clearItems");
        }
        mIsDestoy = true;
        if (mItems != null) {
            mItems.clear();
        }
    }

    @Override
    public ArrayList<Integer> getCheckedItemPositions() {
        if ((mItems == null || mItems.size() < 1)) {
            return null;
        }
        ArrayList<Integer> selectedItem = new ArrayList<Integer>();
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
            int position = getCheckedItemPosition();
            if (position != ListView.INVALID_POSITION) {
                selectedItem.add(position);
            }
        } else {
            for (int i = 0; i < getCount(); i++) {
                if (isItemChecked(i)) {
                    selectedItem.add(i);
                }
            }
        }
        if (selectedItem.size() < 1) {
            return null;
        }
        return selectedItem;
    }

    @Override
    public ArrayList<ListAccessGroup> getCheckedItems() {
        if (mItems == null || mItems.size() < 1) {
            return null;
        }
        ArrayList<ListAccessGroup> selectedItem = new ArrayList<ListAccessGroup>();
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
            int position = getCheckedItemPosition();
            if (position != ListView.INVALID_POSITION) {
                selectedItem.add(mItems.get(position));
            }
        } else {
            for (int i = 0; i < mItems.size(); i++) {
                if (isItemChecked(i)) {
                    selectedItem.add(mItems.get(i));
                }
            }
        }
        if (selectedItem.size() < 1) {
            return null;
        }
        return selectedItem;
    }

    @Override
    public void getItems(String query) {

    }

    @Override
    protected ItemViewHolder getViewHolder(int position, View convertView, ViewGroup parent, int layoutId) {
        if ((mItems == null || mItems.size() < 1)) {
            return null;
        }
        if (null == convertView) {
            convertView = mInflater.inflate(layoutId, parent, false);
            ItemViewHolder viewHolder = new ItemViewHolder(convertView, R.id.name, R.id.info);
            convertView.setTag(viewHolder);
        }
        @SuppressWarnings("unchecked")
        ItemViewHolder viewHolder = (ItemViewHolder) convertView.getTag();
        setSelector(viewHolder, position);
        return viewHolder;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListView == null) {
            return;
        }
        if (isAllDisable) {
            if (mToastPopup != null) {
                mToastPopup.show(-1, R.string.inherited_not_change);
            }
            return;
        }
        ListAccessGroup item = mItems.get(position);
        if (item.isIncludedByUserGroup()) {
            if (mToastPopup != null) {
                mToastPopup.show(R.string.inherited, R.string.inherited_not_change);
            }
            mListView.setItemChecked(position, false);
        }
        super.onItemClick(parent, view, position, id);
    }

    @Override
    public boolean selectChoices() {
        if (mListView == null) {
            return false;
        }
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
            return false;
        }
        for (int i = 0; i < getCount(); i++) {
            mListView.setItemChecked(i, true);
            ListAccessGroup item = mItems.get(i);
            if (item.isIncludedByUserGroup()) {
                mListView.setItemChecked(i, false);
            }
        }
        notifyDataSetChanged();
        return true;
        // mListView.invalidate();
    }

    @Override
    protected void setSelector(Object viewHolderObject, int position) {
        super.setSelector(viewHolderObject, position);

        @SuppressWarnings("unchecked")
        ItemViewHolder viewHolder = (ItemViewHolder) viewHolderObject;
        if (isAllDisable) {
            viewHolder.mRoot.setBackgroundResource(R.drawable.selector_list_gray);
            if (viewHolder.mInfo != null) {
                viewHolder.mInfo.setImageResource(R.drawable.selector_color_transparent);
            }
            return;
        }
        ListAccessGroup item = mItems.get(position);
        if (item.isIncludedByUserGroup()) {
            viewHolder.mRoot.setBackgroundResource(R.drawable.selector_list_gray);
            if (viewHolder.mInfo != null) {
                viewHolder.mInfo.setImageResource(R.drawable.selector_color_transparent);
            }
            return;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder viewHolder = getViewHolder(position, convertView, parent, R.layout.list_item);
        if (viewHolder == null) {
            return null;
        }
        ListAccessGroup item = mItems.get(position);
        if (item != null) {
            viewHolder.mName.setText(item.name);
        }
        return viewHolder.mRoot;
    }
}
