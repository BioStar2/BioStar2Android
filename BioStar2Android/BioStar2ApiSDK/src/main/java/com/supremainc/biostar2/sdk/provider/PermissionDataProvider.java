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
import android.util.Log;

import com.supremainc.biostar2.sdk.datatype.PermissionData;
import com.supremainc.biostar2.sdk.datatype.PermissionData.CloudRoles;
import com.supremainc.biostar2.sdk.datatype.PermissionData.PERMISSION_MODULE;
import com.supremainc.biostar2.sdk.volley.Request.Method;
import com.supremainc.biostar2.sdk.volley.Response;

public class PermissionDataProvider extends BaseDataProvider {
	@SuppressWarnings("unused")
	private final String TAG = getClass().getSimpleName();
	private static PermissionDataProvider mSelf = null;

	private PermissionDataProvider(Context context) {
		super(context);
	}

	public static PermissionDataProvider getInstance(Context context) {
		if (mSelf == null) {
			mSelf = new PermissionDataProvider(context);
		}
		return mSelf;
	}

	public static PermissionDataProvider getInstance() {
		if (mSelf != null) {
			return mSelf;
		}
		if (mContext != null) {
			mSelf = new PermissionDataProvider(mContext);
			return mSelf;
		}
		return null;
	}

//	public void getPermissions(String tag, Response.Listener<Permissions> listener, Response.ErrorListener errorListener, Object deliverParam) {
//
//		sendRequest(tag, Permissions.class, Method.GET, NetWork.PARAM_PERMISSIONS, null, null, null, listener, errorListener, deliverParam);
//	}
//
//	public void getPermissionIds(String tag, Response.Listener<PermissionIds> listener, Response.ErrorListener errorListener, Object deliverParam) {
//
//		sendRequest(tag, PermissionIds.class, Method.GET, NetWork.PARAM_PERMISSIONS, null, null, null, listener, errorListener, deliverParam);
//	}
//
//	public void modifyPermissions(String tag, PermissionContainer item, Listener<ResponseStatus> listener, ErrorListener errorListener, Object deliverParam) {
//		String json = mGson.toJson(item);
//
//		if (item == null || item.mPermission == null) {
//			if (errorListener != null) {
//				errorListener.onErrorResponse(new VolleyError("PermissionContainer/Permission Param is null"), deliverParam);
//			}
//			return;
//		}
//
//		sendRequest(tag, ResponseStatus.class, Method.PUT, NetWork.PARAM_PERMISSIONS + "/" + item.mPermission.id, null, null, json, listener, errorListener, deliverParam);
//	}
	
	public boolean getPermission(PERMISSION_MODULE module,boolean isWriteAllow) {
		if (mPermissionMap == null) {
			Log.e(TAG,"map is null");
			return false;
		}
		String key;
		if (isWriteAllow) {
			key = module.mName + PermissionData.ROLE_WRITE;
		} else {
			key = module.mName + PermissionData.ROLE_READ;
		}
		Boolean result = mPermissionMap.get(key);
		if (ConfigDataProvider.DEBUG) {
			Log.i(TAG,key+":"+String.valueOf(result));
		}
		if (result == null || result == false) {
			return false;
		}
		return true;
	}
	
	public void getCloudRoles(String tag, Response.Listener<CloudRoles> listener, Response.ErrorListener errorListener, Object deliverParam) {
		sendRequest(tag, CloudRoles.class, Method.GET, NetWork.PARAM_REFERENCE_CODES, null, null, null, listener, errorListener, deliverParam);
	}
}
