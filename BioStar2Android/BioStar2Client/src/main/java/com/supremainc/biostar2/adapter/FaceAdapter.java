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
import com.supremainc.biostar2.sdk.models.v2.face.Face;
import com.supremainc.biostar2.widget.popup.Popup;

import java.util.ArrayList;


public class FaceAdapter extends BaseListAdapter<Face> {
    private boolean mIsEditEnable;

    public FaceAdapter(Activity activity, ArrayList<Face> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener, boolean editEnable) {
        super(activity, items, listView, itemClickListener, popup, onItemsListener);
        mIsEditEnable = editEnable;
//        mDefaultSelectColor = mActivity.getResources().getColor(R.color.gray_10);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mIsEditEnable) {
//            if (mToastPopup != null) {
//                mToastPopup.show(-1, R.string.inherited_not_change);
//            }
            return;
        }
        SimpleItemViewHolder viewHolder = (SimpleItemViewHolder) view.getTag();
        setSelector(view, viewHolder.mLink, position);
        super.onItemClick(parent, view, position, id);
    }

    public String getName(int i) {
        String name = String.valueOf(i + 1);
        switch (i) {
            case 0:
                name = name + mActivity.getString(R.string.st);
                break;
            case 1:
                name = name + mActivity.getString(R.string.nd);
                break;
            case 2:
                name = name + mActivity.getString(R.string.rd);
                break;
            default:
                name = name + mActivity.getString(R.string.th);
                break;
        }
        return name + " " + mActivity.getString(R.string.face);
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

        Face item = mItems.get(position);
        if (item != null) {
            vh.mName.setText(getName(position));
        }
        if (mIsEditEnable) {
            setSelector(vh.mRoot, vh.mLink, position, false);
        } else {
            setSelector(vh.mRoot, vh.mLink, position, true);
        }
        return vh.mRoot;
    }
}
