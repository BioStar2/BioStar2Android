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
import com.supremainc.biostar2.sdk.datatype.v2.Common.ResponseStatus;
import com.supremainc.biostar2.sdk.datatype.v2.Door.Door;
import com.supremainc.biostar2.sdk.datatype.v2.Door.Doors;
import com.supremainc.biostar2.sdk.datatype.v2.Door.ListDoor;
import com.supremainc.biostar2.sdk.volley.Request.Method;
import com.supremainc.biostar2.sdk.volley.Response.ErrorListener;
import com.supremainc.biostar2.sdk.volley.Response.Listener;

import java.util.ArrayList;

public class DoorDataProvider extends BaseDataProvider {
    private static DoorDataProvider mSelf = null;
    @SuppressWarnings("unused")
    private final String TAG = getClass().getSimpleName();

    private DoorDataProvider(Context context) {
        super(context);
    }

    public static DoorDataProvider getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new DoorDataProvider(context);
        }
        return mSelf;
    }

    public static DoorDataProvider getInstance() {
        if (mSelf != null) {
            return mSelf;
        }
        if (mContext != null) {
            mSelf = new DoorDataProvider(mContext);
            return mSelf;
        }
        return null;
    }
//    private static int test_count=0;
    public void getDoors(String tag, Listener<Doors> listener, ErrorListener errorListener, int offset, int limit, String query, Object deliverParam) {
        sendRequest(tag, Doors.class, Method.GET, NetWork.PARAM_DOORS, null, createParams(offset, limit,null, query), null, listener, errorListener, deliverParam);
//        Doors doors = new Doors();
//        ArrayList<ListDoor> list = new ArrayList<ListDoor>();
//        int count = test_count +limit;
//        for (int i=test_count; i < count ; i++) {
//            ListDoor item = new ListDoor();
//            item.id = String.valueOf(test_count);
//            item.name = String.valueOf(test_count)+"door";
//            item.description = String.valueOf(test_count)+"description";
//            list.add(item);
//            test_count++;
//        }
//        doors.total = 1000;
//        doors.records = list;
//        listener.onResponse(doors,deliverParam);
    }

    public void getDoor(String tag, String id, Listener<Door> listener, ErrorListener errorListener, Object deliverParam) {
        sendRequest(tag, Door.class, Method.GET, createUrl(NetWork.PARAM_DOORS, id), null, null, null, listener, errorListener, deliverParam);
    }

    public void openDoor(String tag, String id, Listener<ResponseStatus> listener, ErrorListener errorListener, Object deliverParam) {
        sendRequest(tag, ResponseStatus.class, Method.POST, createUrl(NetWork.PARAM_DOORS, id, NetWork.PARAM_OPEN), null, null, null, listener, errorListener, deliverParam);
    }

    public void unlockDoor(String tag, String id, Listener<ResponseStatus> listener, ErrorListener errorListener, Object deliverParam) {
        sendRequest(tag, ResponseStatus.class, Method.POST, createUrl(NetWork.PARAM_DOORS, id, NetWork.PARAM_UNLOCK), null, null, null, listener, errorListener, deliverParam);
    }

    public void lockDoor(String tag, String id, Listener<ResponseStatus> listener, ErrorListener errorListener, Object deliverParam) {
        sendRequest(tag, ResponseStatus.class, Method.POST, createUrl(NetWork.PARAM_DOORS, id, NetWork.PARAM_LOCK), null, null, null, listener, errorListener, deliverParam);
    }

    public void releaseDoor(String tag, String id, Listener<ResponseStatus> listener, ErrorListener errorListener, Object deliverParam) {
        sendRequest(tag, ResponseStatus.class, Method.POST, createUrl(NetWork.PARAM_DOORS, id, NetWork.PARAM_RELEASE), null, null, null, listener, errorListener, deliverParam);
    }

    public void clearAlarm(String tag, String id, Listener<ResponseStatus> listener, ErrorListener errorListener, Object deliverParam) {
        sendRequest(tag, ResponseStatus.class, Method.POST, createUrl(NetWork.PARAM_DOORS, id, NetWork.PARAM_CLEAR_ALARM), null, null, null, listener, errorListener, deliverParam);
    }

    public void clearAntiPassback(String tag, String id, Listener<ResponseStatus> listener, ErrorListener errorListener, Object deliverParam) {
        sendRequest(tag, ResponseStatus.class, Method.POST, createUrl(NetWork.PARAM_DOORS, id, NetWork.PARAM_CLEAR_APB), null, null, null, listener, errorListener, deliverParam);
    }

    public void openRequestDoor(String tag, String id, String phone, Listener<ResponseStatus> listener, ErrorListener errorListener, Object deliverParam) {
        String body = null;
        if (phone != null) {
            body = "{\"phone_number\":\"" + phone + "\"}";
        }

        sendRequest(tag, ResponseStatus.class, Method.POST, createUrl(NetWork.PARAM_DOORS, id, NetWork.PARAM_OPEN_REQUEST), null, null, body, listener, errorListener, deliverParam);
    }
}
