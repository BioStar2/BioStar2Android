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
package com.supremainc.biostar2.base;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.popup.Popup;

import java.util.ArrayList;

public class BaseStringAdapter extends BaseListAdapter<String> {
    protected OnItemsListener mOnItemsListener;

    public BaseStringAdapter(Activity context, ArrayList<String> items, ListView listView, OnItemClickListener listener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, popup);
        listView.setAdapter(this);
        setOnItemClickListener(listener);
        mOnItemsListener = onItemsListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder viewHolder = getViewHolder(position, convertView, parent, R.layout.list_item);
        if (viewHolder == null) {
            return null;
        }
        String item = mItems.get(position);
        if (item != null) {
            viewHolder.mName.setText(item);
        }
        return viewHolder.mRoot;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        Log.e(TAG, "child onItemClick");
    }


}
