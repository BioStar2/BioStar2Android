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
import com.supremainc.biostar2.adapter.base.BaseDoorAdapter;
import com.supremainc.biostar2.sdk.models.v2.door.ListDoor;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.widget.popup.Popup;

import java.util.ArrayList;

public class DoorAdapter extends BaseDoorAdapter {


    public DoorAdapter(Activity activity, ArrayList<ListDoor> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(activity, items, listView, itemClickListener, popup, onItemsListener);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
            convertView = mInflater.inflate(R.layout.list_item_door, parent, false);
            ItemViewHolder viewHolder = new ItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        ItemViewHolder vh = (ItemViewHolder) convertView.getTag();
        if (vh == null) {
            vh = new ItemViewHolder(convertView);
            convertView.setTag(vh);
        }
        ListDoor item = mItems.get(position);
        if (item != null) {
            vh.mName.setText(item.name);
            if (item.description == null) {
                vh.mContent.setVisibility(View.GONE);
            } else {
                vh.mContent.setText(item.description);
                vh.mContent.setVisibility(View.VISIBLE);
            }
            if (item.status != null) {
                if (item.status.forced_open) {
                    vh.mPicture.setImageResource(R.drawable.door_ic_2);
                } else if (item.status.held_opened) {
                    vh.mPicture.setImageResource(R.drawable.door_ic_1);
                } else if (item.status.disconnected) {
                    vh.mPicture.setImageResource(R.drawable.door_ic_2);
                } else if (item.status.unlocked) {
                    vh.mPicture.setImageResource(R.drawable.door_ic_1);
                } else if (item.status.locked) {
                    vh.mPicture.setImageResource(R.drawable.door_ic_1);
                } else if (item.status.scheduleLocked) {
                    vh.mPicture.setImageResource(R.drawable.door_ic_1);
                } else if (item.status.scheduleUnlocked) {
                    vh.mPicture.setImageResource(R.drawable.door_ic_1);
                } else if (item.status.emergencyLocked) {
                    vh.mPicture.setImageResource(R.drawable.door_ic_2);
                } else if (item.status.emergencyUnlocked) {
                    vh.mPicture.setImageResource(R.drawable.door_ic_2);
                } else if (item.status.operatorLocked) {
                    vh.mPicture.setImageResource(R.drawable.door_ic_1);
                } else if (item.status.operatorUnlocked) {
                    vh.mPicture.setImageResource(R.drawable.door_ic_1);
                } else {
                    vh.mPicture.setImageResource(R.drawable.door_ic_3);
                }
            }
            setSelector(vh.mRoot, vh.mLink, position);
        }
        return vh.mRoot;
    }

    private class ItemViewHolder {
        public ImageView mLink;
        public StyledTextView mContent;
        public StyledTextView mName;
        public ImageView mPicture;
        public View mRoot;

        public ItemViewHolder(View root) {
            mRoot = root;
            mName = (StyledTextView) root.findViewById(R.id.name);
            mContent = (StyledTextView) root.findViewById(R.id.content);
            mPicture = (ImageView) root.findViewById(R.id.picture);
            mLink = (ImageView) root.findViewById(R.id.info);
        }
    }
}
