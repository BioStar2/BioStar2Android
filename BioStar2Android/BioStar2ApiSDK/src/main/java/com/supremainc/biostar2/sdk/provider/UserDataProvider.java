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

import com.supremainc.biostar2.sdk.datatype.ResponseStatus;
import com.supremainc.biostar2.sdk.datatype.UserData.ListUser;
import com.supremainc.biostar2.sdk.datatype.UserData.User;
import com.supremainc.biostar2.sdk.datatype.UserData.Users;
import com.supremainc.biostar2.sdk.datatype.UserGroupData.UserGroups;
import com.supremainc.biostar2.sdk.volley.Request.Method;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.ErrorListener;
import com.supremainc.biostar2.sdk.volley.Response.Listener;

import java.util.ArrayList;

public class UserDataProvider extends BaseDataProvider {
	private static UserDataProvider mSelf = null;
	private UserDataProvider(Context context) {
		super(context);
	}

	public static UserDataProvider getInstance(Context context) {
		if (mSelf == null) {
			mSelf = new UserDataProvider(context);
		}
		return mSelf;
	}

	public static UserDataProvider getInstance() {
		if (mSelf != null) {
			return mSelf;
		}
		if (mContext != null) {
			mSelf = new UserDataProvider(mContext);
			return mSelf;
		}
		return null;
	}

	public void createUser(String tag, User item, Response.Listener<ResponseStatus> listener, Response.ErrorListener errorListener, Object deliverParam) {
		String json = mGson.toJson(item);
		sendRequest(tag, ResponseStatus.class, Method.POST, NetWork.PARAM_USERS, null, null, json, listener, errorListener, deliverParam);
	}
	
	public void getMyProfile(String tag, Response.Listener<User> listener, Response.ErrorListener errorListener, Object deliverParam) {
		sendRequest(tag, User.class, Method.GET, createUrl(NetWork.PARAM_USERS, null,NetWork.PARAM_MYPROFILE), null, null, null, listener, errorListener, deliverParam);
	}	

	public void getUser(String tag, String userId, Response.Listener<User> listener, Response.ErrorListener errorListener, Object deliverParam) {
		sendRequest(tag, User.class, Method.GET, createUrl(NetWork.PARAM_USERS, userId), null, null, null, listener, errorListener, deliverParam);
	}

	public String getUserPhotoUrl(String userId) {
		return mNetwork.SERVER_ADDRESS+createUrl(NetWork.PARAM_USERS, userId, NetWork.PARAM_PHOTO);
	}
	
	public void getUsers(String tag, Response.Listener<Users> listener, Response.ErrorListener errorListener, int offset, int limit, String groupId, String query, Object deliverParam) {
		sendRequest(tag, Users.class, Method.GET, NetWork.PARAM_USERS, null, createParams(offset,limit,groupId,query), null, listener, errorListener, deliverParam);
	}

	public void modifyUser(String tag, User user, Listener<ResponseStatus> listener, ErrorListener errorListener, Object deliverParam) {
		String json = mGson.toJson(user);
		sendRequest(tag, ResponseStatus.class, Method.PUT, NetWork.PARAM_USERS + "/" + user.user_id, null, null, json, listener, errorListener, deliverParam);
	}
	
	public void modifyMyProfile(String tag, User user,Listener<ResponseStatus> listener, ErrorListener errorListener, final Object deliverParam) {
		String json = mGson.toJson(user);
		mNetwork.sendRequest(tag, ResponseStatus.class, Method.PUT, NetWork.PARAM_USERS + "/" + NetWork.PARAM_MYPROFILE, null, null, json, listener, errorListener, deliverParam);
	}

	public void deleteUser(String tag, String id, Listener<ResponseStatus> listener, ErrorListener errorListener, Object deliverParam) {
		sendRequest(tag, ResponseStatus.class, Method.DELETE, NetWork.PARAM_USERS + "/" + id, null, null, null, listener, errorListener, deliverParam);
	}

	public void deleteUsers(String tag, ArrayList<ListUser> users, Listener<ResponseStatus> listener, ErrorListener errorListener, Object deliverParam) {
		if (users == null || users.size() < 1) {
			return;
		}
		deleteDo(tag, users, listener, errorListener, deliverParam);
//		int i = 0;
//		while (true) {
//			i = deleteDo(i, tag, users, listener, errorListener, deliverParam);
//			if (i >= users.size()) {
//				break;
//			}
//		}
	}

	private int deleteDo(String tag, ArrayList<ListUser> users, Listener<ResponseStatus> listener, ErrorListener errorListener, Object deliverParam) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"ids\":[");
		int i=0;
		for (; i < users.size(); i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append(users.get(i).user_id);
			sb.append(" ");
//			if (sb.length() > 200) {
//				break;
//			}
		}
		sb.append("]}");

		sendRequest(tag, ResponseStatus.class, Method.POST, NetWork.PARAM_USERS_DELETE, null, null, sb.toString(), listener, errorListener, i);
		return i;
	}

	public void getUserGroups(String tag, Listener<UserGroups> listener, ErrorListener errorListener, int offset, int limit, String query, Object deliverParam) {
		mNetwork.sendRequest(tag, UserGroups.class, Method.GET, NetWork.PARAM_USER_GROUPS, null, createParams(offset, limit, null, query), null, listener, errorListener, deliverParam);
	}

	public void getUserPhoto(String tag, String userId, Response.Listener<String> listener, Response.ErrorListener errorListener, Object deliverParam) {
		sendRequest(tag, String.class, Method.GET, createUrl(NetWork.PARAM_USERS, userId,NetWork.PARAM_PHOTO), null, null, null, listener, errorListener, deliverParam);
	}
}
