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
import com.supremainc.biostar2.adapter.base.BaseCardAdapter;
import com.supremainc.biostar2.sdk.models.v2.card.ListCard;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.view.SwitchView;
import com.supremainc.biostar2.widget.popup.Popup;

import java.util.ArrayList;

public class CardAdapter extends BaseCardAdapter {
    private boolean mIsEditDisable;

    public CardAdapter(Activity activity, ArrayList<ListCard> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener, boolean editDisable) {
        super(activity, items, listView, itemClickListener, popup, onItemsListener);
        mIsEditDisable = editDisable;
        if (mIsEditDisable) {
            mDefaultSelectColor = mActivity.getResources().getColor(R.color.gray_10);
        }
    }

    public String getName(int i) {
        String name = String.valueOf(i + 1);
        switch (i) {
            case 0:
                name = name + mActivity.getString(R.string.st) + " " + mActivity.getString(R.string.card);
                break;
            case 1:
                name = name + mActivity.getString(R.string.nd) + " " + mActivity.getString(R.string.card);
                break;
            case 2:
                name = name + mActivity.getString(R.string.rd) + " " + mActivity.getString(R.string.card);
                break;
            default:
                name = name + mActivity.getString(R.string.th) + " " + mActivity.getString(R.string.card);
                break;
        }
        return name;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mIsEditDisable) {
            if (mToastPopup != null) {
                mToastPopup.show(-1, R.string.inherited_not_change);
            }
            return;
        }
        ItemViewHolder viewHolder = (ItemViewHolder) view.getTag();
        setSelector(view, viewHolder.mLink, position);
        super.onItemClick(parent, view, position, id);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mItems == null || mItems.size() < 1) {
            return null;
        }
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.list_item_card, parent, false);
            ItemViewHolder viewHolder = new ItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        ItemViewHolder vh = (ItemViewHolder) convertView.getTag();
        if (vh == null) {
            vh = new ItemViewHolder(convertView);
            convertView.setTag(vh);
        }

        ListCard item = mItems.get(position);
        if (item != null) {
            vh.mID.setText(getName(position) + " / " + mActivity.getString(R.string.id) + " " + item.card_id);
        }
        if (mIsEditDisable) {
            setSelector(vh.mRoot, vh.mLink, position, false);
        } else {
            setSelector(vh.mRoot, vh.mLink, position, true);
        }
        return vh.mRoot;
    }

    private class ItemViewHolder {
        public View mRoot;
        public StyledTextView mCardType;
        public StyledTextView mID;
        public ImageView mMobileCard;
        public ImageView mLink;
        public SwitchView mStatusSwitch;

        public ItemViewHolder(View root) {
            mRoot = root;
            mLink = (ImageView) root.findViewById(R.id.info);
            mMobileCard = (ImageView) root.findViewById(R.id.info_mobilecard);
            mMobileCard.setVisibility(View.GONE);
            mID = (StyledTextView) root.findViewById(R.id.card_id);
            mCardType = (StyledTextView) root.findViewById(R.id.card_type);
            mCardType.setVisibility(View.GONE);
            mStatusSwitch = (SwitchView) root.findViewById(R.id.status_switch);
            mStatusSwitch.setVisibility(View.GONE);
        }
    }
}
