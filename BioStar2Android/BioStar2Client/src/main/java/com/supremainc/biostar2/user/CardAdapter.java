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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.base.BaseCardAdapter;
import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.sdk.datatype.CardData.ListCard;

import java.util.ArrayList;

public class CardAdapter extends BaseCardAdapter {
    private boolean isAllDisable;

    public CardAdapter(Activity context, ArrayList<ListCard> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener, boolean isAllDisable) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
        listView.setAdapter(this);
        setOnItemClickListener(itemClickListener);
        this.isAllDisable = isAllDisable;
    }

    public String getName(int i) {
        String name = String.valueOf(i + 1);
        switch (i) {
            case 0:
                name = name + mContext.getString(R.string.st) + " " + mContext.getString(R.string.card);
                break;
            case 1:
                name = name + mContext.getString(R.string.nd) + " " + mContext.getString(R.string.card);
                break;
            case 2:
                name = name + mContext.getString(R.string.rd) + " " + mContext.getString(R.string.card);
                break;
            default:
                name = name + mContext.getString(R.string.th) + " " + mContext.getString(R.string.card);
                break;
        }
        return name;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isAllDisable) {
            if (mToastPopup != null) {
                mToastPopup.show(-1, R.string.inherited_not_change);
            }
            return;
        }
        super.onItemClick(parent, view, position, id);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder viewHolder = getViewHolder(position, convertView, parent, R.layout.list_item);
        if (viewHolder == null) {
            return null;
        }

        ListCard item = mItems.get(position);
        if (item != null) {
            viewHolder.mName.setText(getName(position) + " / " + mContext.getString(R.string.id) + " " + item.card_id);
        }
        return viewHolder.mRoot;
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
    }
}
