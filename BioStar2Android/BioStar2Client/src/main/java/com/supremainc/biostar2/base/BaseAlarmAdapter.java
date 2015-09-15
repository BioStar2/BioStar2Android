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
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.db.DBAdapter;
import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.sdk.datatype.NotificationData.NotificationType;
import com.supremainc.biostar2.sdk.datatype.NotificationData.PushNotification;
import com.supremainc.biostar2.sdk.provider.CommonDataProvider;
import com.supremainc.biostar2.sdk.provider.CommonDataProvider.DATE_TYPE;
import com.supremainc.biostar2.widget.StyledTextView;
import com.tekinarslan.material.sample.FloatingActionButton;

public class BaseAlarmAdapter extends BaseListCursorAdapter<PushNotification> {

    protected OnItemsListener mOnItemsListener;
    protected String mQuery;
    protected CommonDataProvider mCommonDataProvider;

    public BaseAlarmAdapter(Activity context, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        super(context, listView, popup);
        listView.setAdapter(this);
        setOnItemClickListener(itemClickListener);
        mOnItemsListener = onItemsListener;
        if (mOnItemsListener != null) {
            mOnItemsListener.onTotalReceive(getCount());
        }
        mCommonDataProvider = CommonDataProvider.getInstance();
    }

    @Override
    public View newView(Context arg0, Cursor cursor, ViewGroup arg2) {
        View v = mInflater.inflate(R.layout.list_item_alarm, arg2, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(v, R.id.name, R.id.info);
        ExtraViewHolder vh = (ExtraViewHolder) (itemViewHolder.mExtend = new ExtraViewHolder(itemViewHolder.mRoot, R.id.picture, R.id.name, R.id.content, R.id.unread));
        setView(vh, cursor);
        v.setTag(itemViewHolder);
        return v;
    }

    @Override
    public void bindView(View convertView, Context arg1, Cursor cursor) {
        ItemViewHolder viewHolder = (ItemViewHolder) convertView.getTag();

        ExtraViewHolder vh = null;
        if (viewHolder == null) {
            ItemViewHolder itemViewHolder = new ItemViewHolder(convertView, R.id.name, R.id.info);
            vh = (ExtraViewHolder) (itemViewHolder.mExtend = new ExtraViewHolder(itemViewHolder.mRoot, R.id.picture, R.id.name, R.id.content, R.id.unread));
        } else {
            vh = (ExtraViewHolder) viewHolder.mExtend;
        }
        setView(vh, cursor);
        int position = cursor.getPosition();
        setSelector(viewHolder, position);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (mOnItemsListener != null) {
            mOnItemsListener.onTotalReceive(getCount());
        }
        mSwipyRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ItemViewHolder viewHolder = (ItemViewHolder) view.getTag();
//        ExtraViewHolder vh = (ExtraViewHolder) viewHolder.mExtend;
//        vh.mUnread.setVisibility(View.GONE);
        super.onItemClick(parent, view, position, id);
    }

    public void setSwipyRefreshLayout(SwipyRefreshLayout swipyRefreshLayout, FloatingActionButton fab) {
        BaseListViewScroll onScroll = new BaseListViewScroll();
        onScroll.setFloatingActionButton(fab, mListView, this);
        setOnScrollListener(onScroll);
        mSwipyRefreshLayout = swipyRefreshLayout;
        mSwipyRefreshLayout.setEnableBottom(false);
        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                switch (direction) {
                    case TOP:
                        notifyDataSetChanged();
                        break;
                    case BOTTOM:
                        mSwipyRefreshLayout.setRefreshing(false);
                        mToastPopup.show(mContext.getString(R.string.no_more_data), null);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void setView(ExtraViewHolder vh, Cursor cursor) {
        String name = cursor.getString(DBAdapter.COLUMN_ALARM_TITLE);
        String time = cursor.getString(DBAdapter.COLUMN_ALARM_TIME);
        String code = cursor.getString(DBAdapter.COLUMN_ALARM_CODE);
        String doorId = cursor.getString(DBAdapter.COLUMN_ALARM_DOOR_ID);
        int read = cursor.getInt(DBAdapter.COLUMN_ALARM_UNREAD);
        vh.mName.setText(name);
        if (read == 1) {
            vh.mUnread.setVisibility(View.VISIBLE);
        } else {
            vh.mUnread.setVisibility(View.GONE);
        }
        if (time == null) {
            vh.mContent.setVisibility(View.GONE);
        } else {
            String convertTime = mCommonDataProvider.convertServerTimeToClientTime(mContext, DATE_TYPE.FORMAT_SEC, time, true);
            vh.mContent.setText(convertTime);
            vh.mContent.setVisibility(View.VISIBLE);
        }
        if (code.equals(NotificationType.DEVICE_REBOOT.mName)) {
            vh.mPicture.setImageResource(R.drawable.ic_event_device_01);
        } else if (code.equals(NotificationType.DEVICE_RS485_DISCONNECT.mName)) {
            vh.mPicture.setImageResource(R.drawable.ic_event_device_03);
        } else if (code.equals(NotificationType.DEVICE_TAMPERING.mName)) {
            vh.mPicture.setImageResource(R.drawable.ic_event_device_03);
        } else if (code.equals(NotificationType.DOOR_FORCED_OPEN.mName)) {
            vh.mPicture.setImageResource(R.drawable.ic_event_door_02);
        } else if (code.equals(NotificationType.DOOR_HELD_OPEN.mName)) {
            vh.mPicture.setImageResource(R.drawable.ic_event_door_03);
        } else if (code.equals(NotificationType.DOOR_OPEN_REQUEST.mName)) {
            vh.mPicture.setImageResource(R.drawable.ic_event_door_01);
        } else if (code.equals(NotificationType.ZONE_APB.mName)) {
            if (doorId != null && !doorId.isEmpty()) {
                vh.mPicture.setImageResource(R.drawable.ic_event_door_03);
            } else {
                vh.mPicture.setImageResource(R.drawable.ic_event_zone_03);
            }
        } else if (code.equals(NotificationType.ZONE_FIRE.mName)) {
            vh.mPicture.setImageResource(R.drawable.ic_event_fire_alarm);
        } else {
            vh.mPicture.setImageResource(R.drawable.monitoring_ic1);
        }
    }

    public class ExtraViewHolder {
        public StyledTextView mName;
        public ImageView mPicture;
        public StyledTextView mContent;
        public StyledTextView mUnread;

        public ExtraViewHolder(View root, int picture, int name, int content, int unread) {
            mPicture = (ImageView) root.findViewById(picture);
            mName = (StyledTextView) root.findViewById(name);
            mContent = (StyledTextView) root.findViewById(content);
            mUnread = (StyledTextView) root.findViewById(unread);
        }
    }
}
