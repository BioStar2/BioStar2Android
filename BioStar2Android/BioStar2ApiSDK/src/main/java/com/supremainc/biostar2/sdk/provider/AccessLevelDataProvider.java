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

import com.supremainc.biostar2.sdk.datatype.v2.AccessControl.AccessLevel;
import com.supremainc.biostar2.sdk.datatype.v2.AccessControl.AccessLevels;
import com.supremainc.biostar2.sdk.volley.Request.Method;
import com.supremainc.biostar2.sdk.volley.Response;

import java.util.HashMap;
import java.util.Map;

public class AccessLevelDataProvider extends BaseDataProvider {
    private static AccessLevelDataProvider mSelf = null;
    @SuppressWarnings("unused")
    private final String TAG = getClass().getSimpleName();

    private AccessLevelDataProvider(Context context) {
        super(context);
    }

    public static AccessLevelDataProvider getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new AccessLevelDataProvider(context);
        }
        return mSelf;
    }

    public static AccessLevelDataProvider getInstance() {
        if (mSelf != null) {
            return mSelf;
        }
        if (mContext != null) {
            mSelf = new AccessLevelDataProvider(mContext);
            return mSelf;
        }
        return null;
    }

    public void createAccessLevel(String tag, AccessLevel item, Response.Listener<AccessLevel> listener,
                                  Response.ErrorListener errorListener, Object deliverParam) {
        String json = mGson.toJson(item);

        sendRequest(tag, AccessLevel.class, Method.POST, NetWork.PARAM_ACCESS_LEVELS, null, null, json, listener, errorListener, deliverParam);
    }

    public void getAccessLevels(String tag, Response.Listener<AccessLevels> listener, Response.ErrorListener errorListener, int offset, int limit,
                                Object deliverParam) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("limit", String.valueOf(limit));
        params.put("nextEnabled", "true");
        params.put("offset", String.valueOf(offset));
        params.put("previousEnabled", "false");
        params.put("order_by", "start_datetime:true");
        sendRequest(tag, AccessLevels.class, Method.GET, NetWork.PARAM_ACCESS_LEVELS, null, params, null, listener, errorListener, deliverParam);
    }

    public void getAccessLevels(String tag, Response.Listener<AccessLevels> listener, Response.ErrorListener errorListener, Object deliverParam) {
        sendRequest(tag, AccessLevels.class, Method.GET, NetWork.PARAM_ACCESS_LEVELS, null, null, null, listener, errorListener, deliverParam);
    }

    public void modifyAccessLevel(String tag, AccessLevel item, Response.Listener<AccessLevel> listener,
                                  Response.ErrorListener errorListener, Object deliverParam) {
        String json = mGson.toJson(item);

        sendRequest(tag, AccessLevel.class, Method.PUT, NetWork.PARAM_ACCESS_LEVELS + "/" + item.id, null, null, json, listener,
                errorListener, deliverParam);
    }

}
