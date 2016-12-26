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
import android.widget.ImageView;
import android.widget.ListView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.adapter.base.BaseListAdapter;
import com.supremainc.biostar2.datatype.DoorDetailData.DoorDetail;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.widget.popup.Popup;

import java.util.ArrayList;

public class DetailAdapter extends BaseListAdapter<DoorDetail> {


    public DetailAdapter(Activity activity, ArrayList<DoorDetail> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(activity, items, listView, itemClickListener, popup, onItemsListener);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ItemViewHolder viewHolder = (ItemViewHolder) view.getTag();
        setSelector(view, viewHolder.mLink, position, getItemData(position).link);
        super.onItemClick(parent, view, position, id);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mItems == null || mItems.size() < 1) {
            return null;
        }
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.list_item_detail, parent, false);
            ItemViewHolder viewHolder = new ItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        ItemViewHolder viewHolder = (ItemViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        DoorDetail item = mItems.get(position);
        if (item != null) {
            viewHolder.mTitle.setText(item.title);
            viewHolder.mContent.setText(item.content);
            setSelector(viewHolder.mRoot, viewHolder.mLink, position, item.link);
        }
        return viewHolder.mRoot;
    }

    private class ItemViewHolder {
        public StyledTextView mTitle;
        public StyledTextView mContent;
        public ImageView mLink;
        public View mRoot;

        public ItemViewHolder(View root) {
            mRoot = root;
            mTitle = (StyledTextView) root.findViewById(R.id.item_title);
            mContent = (StyledTextView) root.findViewById(R.id.item_content);
            mLink = (ImageView) root.findViewById(R.id.item_link);
        }
    }
}
