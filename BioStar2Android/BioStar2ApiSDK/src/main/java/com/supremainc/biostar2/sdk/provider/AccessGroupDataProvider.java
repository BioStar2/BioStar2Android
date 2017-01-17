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
package com.supremainc.biostar2.sdk.provider;

import android.content.Context;

import com.supremainc.biostar2.sdk.datatype.v2.AccessControl.AccessGroup;
import com.supremainc.biostar2.sdk.datatype.v2.AccessControl.AccessGroups;
import com.supremainc.biostar2.sdk.datatype.v2.AccessControl.ListAccessGroup;
import com.supremainc.biostar2.sdk.volley.Request.Method;
import com.supremainc.biostar2.sdk.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AccessGroupDataProvider extends BaseDataProvider {
    private static AccessGroupDataProvider mSelf = null;

    private AccessGroupDataProvider(Context context) {
        super(context);
    }

    public static AccessGroupDataProvider getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new AccessGroupDataProvider(context);
        }
        return mSelf;
    }

    public static AccessGroupDataProvider getInstance() {
        if (mSelf != null) {
            return mSelf;
        }
        if (mContext != null) {
            mSelf = new AccessGroupDataProvider(mContext);
            return mSelf;
        }
        return null;
    }

    public void createAccessGroup(String tag, ListAccessGroup item, Response.Listener<ListAccessGroup> listener,
                                  Response.ErrorListener errorListener, Object deliverParam) {
        String json = mGson.toJson(item);

        sendRequest(tag, ListAccessGroup.class, Method.POST, NetWork.PARAM_ACCESS_GROUPS, null, null, json, listener, errorListener, deliverParam);
    }
//    private static int test_count=0;
    public void getAccessGroups(String tag, Response.Listener<AccessGroups> listener, Response.ErrorListener errorListener, int offset, int limit,String text,
                                Object deliverParam) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));
        if (text != null) {
            params.put("text",text);
        }
        sendRequest(tag, AccessGroups.class, Method.GET, NetWork.PARAM_ACCESS_GROUPS, null, params, null, listener, errorListener, deliverParam);

//        AccessGroups items = new AccessGroups();
//        ArrayList<ListAccessGroup> list = new ArrayList<ListAccessGroup>();
//        if (test_count > 500) {
//            listener.onResponse(null,deliverParam);
//            return;
//        }
//        int count = test_count +limit;
//        for (int i=test_count; i < count ; i++) {
//            ListAccessGroup item = new ListAccessGroup();
//            item.id = String.valueOf(test_count);
//            item.name = String.valueOf(test_count);
//            test_count++;
//            list.add(item);
//        }
//        items.total = 500;
//        items.records = list;
//        listener.onResponse(items,deliverParam);
    }

    public void modifyAccessGroup(String tag, ListAccessGroup item, Response.Listener<ListAccessGroup> listener,
                                  Response.ErrorListener errorListener, Object deliverParam) {
        String json = mGson.toJson(item);

        sendRequest(tag, ListAccessGroup.class, Method.PUT, NetWork.PARAM_ACCESS_GROUPS + "/" + item.id, null, null, json, listener,
                errorListener, deliverParam);
    }
}
