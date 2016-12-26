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
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.SelectCustomData;

import java.util.ArrayList;

public class SimpleCustomAdapter extends BaseListAdapter<SelectCustomData> {

    public SimpleCustomAdapter(Activity context, ArrayList<SelectCustomData> items, ListView listView, OnItemClickListener listener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, listener, popup, onItemsListener);
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
        SimpleItemViewHolder viewHolder = (SimpleItemViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new SimpleItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        SelectCustomData item = mItems.get(position);
        if (item != null) {
            viewHolder.mName.setText(item.mTitle);
        }
        setSelector(viewHolder.mRoot, viewHolder.mLink, position);
        return viewHolder.mRoot;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SimpleItemViewHolder viewHolder = (SimpleItemViewHolder) view.getTag();
        setSelector(view, viewHolder.mLink, position);
        super.onItemClick(parent, view, position, id);
    }


}
