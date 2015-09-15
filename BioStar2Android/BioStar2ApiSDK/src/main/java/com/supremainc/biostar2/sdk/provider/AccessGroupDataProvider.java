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

import com.supremainc.biostar2.sdk.datatype.AccessGroupData.AccessGroups;
import com.supremainc.biostar2.sdk.datatype.AccessGroupData.ListAccessGroup;
import com.supremainc.biostar2.sdk.volley.Request.Method;
import com.supremainc.biostar2.sdk.volley.Response;

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

	public void getAccessGroups(String tag, Response.Listener<AccessGroups> listener, Response.ErrorListener errorListener, int offset, int limit,
			Object deliverParam) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("limit", String.valueOf(limit));
		params.put("nextEnabled", "true");
		params.put("offset", String.valueOf(offset));
		params.put("previousEnabled", "false");
		params.put("order_by", "start_datetime:true");

		sendRequest(tag, AccessGroups.class, Method.GET, NetWork.PARAM_ACCESS_GROUPS, null, params, null, listener, errorListener, deliverParam);
	}

	public void getAccessGroups(String tag, Response.Listener<AccessGroups> listener, Response.ErrorListener errorListener, Object deliverParam) {

		sendRequest(tag, AccessGroups.class, Method.GET, NetWork.PARAM_ACCESS_GROUPS, null, null, null, listener, errorListener, deliverParam);

	}

	public void modifyAccessGroup(String tag, ListAccessGroup item, Response.Listener<ListAccessGroup> listener,
			Response.ErrorListener errorListener, Object deliverParam) {
		String json = mGson.toJson(item);

		sendRequest(tag, ListAccessGroup.class, Method.PUT, NetWork.PARAM_ACCESS_GROUPS + "/" + item.id, null, null, json, listener,
				errorListener, deliverParam);
	}
}
