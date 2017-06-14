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
package com.supremainc.biostar2.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.adapter.base.BaseListAdapter;
import com.supremainc.biostar2.sdk.models.v2.accesscontrol.ListAccessGroup;
import com.supremainc.biostar2.widget.popup.Popup;

import java.util.ArrayList;

public class UserAccessGroupAdapter extends BaseListAdapter<ListAccessGroup> {
    private boolean mIsEditEnable;


    public UserAccessGroupAdapter(Activity activity, ArrayList<ListAccessGroup> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener, boolean editEnable) {
        super(activity, items, listView, itemClickListener, popup, onItemsListener);
        mIsEditEnable = editEnable;
//        mDefaultSelectColor = mActivity.getResources().getColor(R.color.gray_10);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mIsEditEnable) {
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
            return;
        }
        SimpleItemViewHolder viewHolder = (SimpleItemViewHolder) view.getTag();
        setSelector(view, viewHolder.mLink, position);
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

    public int getAvailableTotal() {
        if (mListView == null) {
            return -1;
        }
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
            return -1;
        }
        int total = getCount();
        for (int i = 0; i < getCount(); i++) {
            if (mItems != null) {
                ListAccessGroup item = mItems.get(i);
                if (item.isIncludedByUserGroup()) {
                    total--;
                }
            }
        }
        return total;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mItems == null || mItems.size() < 1) {
            return null;
        }
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.list_item, parent, false);
            SimpleItemViewHolder viewHolder = new SimpleItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        SimpleItemViewHolder vh = (SimpleItemViewHolder) convertView.getTag();
        if (vh == null) {
            vh = new SimpleItemViewHolder(convertView);
            convertView.setTag(vh);
        }

        ListAccessGroup item = mItems.get(position);
        if (item != null) {
            vh.mName.setText(item.name);
        }
        if (mIsEditEnable) {
            setSelector(vh.mRoot, vh.mLink, position, false);
        } else {
            setSelector(vh.mRoot, vh.mLink, position, !item.isIncludedByUserGroup());
        }
        if (mListView.getChoiceMode() != ListView.CHOICE_MODE_NONE && item.isIncludedByUserGroup()) {
            vh.mRoot.setBackgroundResource(R.drawable.selector_list_gray);
        }
        return vh.mRoot;
    }
}
