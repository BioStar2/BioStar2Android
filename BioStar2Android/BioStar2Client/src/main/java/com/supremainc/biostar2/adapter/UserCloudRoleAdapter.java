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
import com.supremainc.biostar2.sdk.models.v1.permission.CloudRole;
import com.supremainc.biostar2.widget.popup.Popup;

import java.util.ArrayList;

public class UserCloudRoleAdapter extends BaseListAdapter<CloudRole> {
    private boolean mIsEditEnable;

    public UserCloudRoleAdapter(Activity activity, ArrayList<CloudRole> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener, boolean editEnable) {
        super(activity, items, listView, itemClickListener, popup, onItemsListener);
        mIsEditEnable = editEnable;
        if (mIsEditEnable) {
            mDefaultSelectColor = mActivity.getResources().getColor(R.color.gray_10);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mIsEditEnable) {
            if (mToastPopup != null) {
                mToastPopup.show(-1, R.string.inherited_not_change);
            }
            return;
        }
//        CloudRole item = mItems.get(position);
//		if (item.code.equals("DEFAULT_USER")) {
//			if (mToastPopup != null) {
//				mToastPopup.show(R.string.inherited, R.string.inherited_not_change);
//			}
//			mListView.setItemChecked(position, false);
//			return;
//		}
        SimpleItemViewHolder viewHolder = (SimpleItemViewHolder) view.getTag();
        setSelector(view, viewHolder.mLink, position);
        super.onItemClick(parent, view, position, id);
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

        CloudRole item = mItems.get(position);
        if (item != null) {
            vh.mName.setText(item.description);
        }
        return vh.mRoot;
    }
}
