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
package com.supremainc.biostar2.door;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.base.BaseDoorAdapter;
import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.sdk.datatype.DoorData.ListDoor;
import com.supremainc.biostar2.widget.StyledTextView;

import java.util.ArrayList;

public class DoorAdapter extends BaseDoorAdapter {


    public DoorAdapter(Activity context, ArrayList<ListDoor> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, itemClickListener, popup, onItemsListener);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder viewHolder = getViewHolder(position, convertView, parent, R.layout.list_item_door);
        if (viewHolder == null) {
            return null;
        }
        if (convertView == null) {
            viewHolder.mExtend = new ExtraViewHolder(viewHolder.mRoot, R.id.picture, R.id.name, R.id.content);
        }
        ExtraViewHolder vh = (ExtraViewHolder) viewHolder.mExtend;
        if (vh == null) {
            return null;
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
        }
        return viewHolder.mRoot;
    }

    public class ExtraViewHolder {
        public StyledTextView mContent;
        public StyledTextView mName;
        public ImageView mPicture;

        public ExtraViewHolder(View root, int picture, int name, int content) {
            mPicture = (ImageView) root.findViewById(picture);
            mName = (StyledTextView) root.findViewById(name);
            mContent = (StyledTextView) root.findViewById(content);
        }
    }
}
