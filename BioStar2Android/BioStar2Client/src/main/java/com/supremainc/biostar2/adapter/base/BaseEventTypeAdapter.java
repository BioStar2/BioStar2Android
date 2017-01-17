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
package com.supremainc.biostar2.adapter.base;

import android.app.Activity;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.supremainc.biostar2.sdk.datatype.v2.EventLog.EventType;
import com.supremainc.biostar2.sdk.provider.EventDataProvider;
import com.supremainc.biostar2.widget.popup.Popup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseEventTypeAdapter extends BaseListAdapter<EventType> {
    protected EventDataProvider mEventDataProvider;
    private Comparator<EventType> mComparator = new Comparator<EventType>() {
        @Override
        public int compare(EventType lhs, EventType rhs) {
            return rhs.code - lhs.code;
        }
    };

    public BaseEventTypeAdapter(Activity context, ArrayList<EventType> items, ListView listView, OnItemClickListener listener, Popup popup, OnItemsListener onItemsListener) {
        super(context, items, listView, listener, popup, onItemsListener);
        mEventDataProvider = EventDataProvider.getInstance(context);
    }

    private boolean checkInclude(String target, Pattern query, EventType item, ArrayList<EventType> resultItems) {
        if (target == null || target.equals("")) {
            return false;
        }
        Matcher matcher = query.matcher(target);
        if (matcher.find()) {
            resultItems.add(item);
            return true;
        }
        return false;
    }

    private boolean checkInclude(String target, String query, EventType item, ArrayList<EventType> resultItems) {
        if (target == null || target.equals("")) {
            return false;
        }
        String diff = target.toUpperCase();
        if (diff.contains(query)) {
            resultItems.add(item);
            return true;
        }
        return false;
    }

    @Override
    public void getItems(String query) {
        ArrayList<EventType> items = mEventDataProvider.getEventTypeList();
        ArrayList<EventType> resultItems = null;
        if (query != null && !query.equals("")) {
            query = query.toUpperCase();
//            query = query.replace("?","\\?");
//            query = query.replace("(","\\(");
//            query = query.replace(")","\\)");
//            query = query.replace("[","\\[");
//            query = query.replace("]","\\]");
//            query = query.replace("\\","");
//            query = query.replace("^","\\^");
//            Pattern pattern = Pattern.compile("(?i)" + query);
            resultItems = new ArrayList<EventType>();
            for (EventType item : items) {
                if (checkInclude(item.name, query, item, resultItems)) {
                    continue;
                }
                if (checkInclude(item.description, query, item, resultItems)) {
                    continue;
                }
//                if (checkInclude(item.alert_message, query, item, resultItems)) {
//                    continue;
//                }
                if (checkInclude(String.valueOf(item.code), query, item, resultItems)) {
                    continue;
                }
            }
        }
        if (resultItems != null) {
            if (mItems != null) {
                mItems.clear();
            }
            items = resultItems;
        }
        Collections.sort(items, mComparator);
        setData(items);
        if (mOnItemsListener != null) {
            mOnItemsListener.onTotalReceive(getCount());
        }
    }
}
