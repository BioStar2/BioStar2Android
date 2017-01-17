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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.supremainc.biostar2.sdk.datatype.v2.Card.BaseCard;
import com.supremainc.biostar2.sdk.datatype.v2.Card.Cards;
import com.supremainc.biostar2.sdk.datatype.v2.Card.CardsList;
import com.supremainc.biostar2.sdk.datatype.v2.Card.ListCard;
import com.supremainc.biostar2.sdk.datatype.v2.Common.ResponseStatus;
import com.supremainc.biostar2.sdk.datatype.v2.FingerPrint.FingerPrints;
import com.supremainc.biostar2.sdk.datatype.v2.FingerPrint.ListFingerprintTemplate;
import com.supremainc.biostar2.sdk.datatype.v2.User.ListUser;
import com.supremainc.biostar2.sdk.datatype.v2.User.User;
import com.supremainc.biostar2.sdk.datatype.v2.User.UserGroups;
import com.supremainc.biostar2.sdk.datatype.v2.User.Users;
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
        sendRequest(tag, User.class, Method.GET, createUrl(NetWork.PARAM_USERS, null, NetWork.PARAM_MYPROFILE), null, null, null, listener, errorListener, deliverParam);
    }

    public void getUser(String tag, String userId, Response.Listener<User> listener, Response.ErrorListener errorListener, Object deliverParam) {
        sendRequest(tag, User.class, Method.GET, createUrl(NetWork.PARAM_USERS, userId), null, null, null, listener, errorListener, deliverParam);
    }

    public String getUserPhotoUrl(String userId) {
        if (ConfigDataProvider.getFullURL(mContext) == null) {
            return null;
        }
        return ConfigDataProvider.getFullURL(mContext)+ createUrl(NetWork.PARAM_USERS, userId, NetWork.PARAM_PHOTO);
    }

    public void getUsers(String tag, Response.Listener<Users> listener, Response.ErrorListener errorListener, int offset, int limit, String groupId, String query, Object deliverParam) {
        sendRequest(tag, Users.class, Method.GET, NetWork.PARAM_USERS, null, createParams(offset, limit, groupId, query), null, listener, errorListener, deliverParam);
    }

    public void modifyUser(String tag, User user, Listener<ResponseStatus> listener, ErrorListener errorListener, Object deliverParam) {
        String json = mGson.toJson(user);
        sendRequest(tag, ResponseStatus.class, Method.PUT, NetWork.PARAM_USERS + "/" + user.user_id, null, null, json, listener, errorListener, deliverParam);
    }

    public void modifyMyProfile(String tag, User user, Listener<ResponseStatus> listener, ErrorListener errorListener, Object deliverParam) {
        String json = mGson.toJson(user);
        mNetwork.sendRequest(tag, ResponseStatus.class, Method.PUT, NetWork.PARAM_USERS + "/" + NetWork.PARAM_MYPROFILE, null, null, json, listener, errorListener, deliverParam);
    }

    public void modifyCards(String tag, String userId, ArrayList<ListCard> cards, Listener<ResponseStatus> listener, ErrorListener errorListener, Object deliverParam) {
        CardsList container = new CardsList(cards);
        String body = mGson.toJson(container);
//        JsonArray arr = new JsonArray();
//        for (ListCard card:cards) {
//            arr.add(new JsonPrimitive(card.id));
//        }
//        object.add("ids",arr);
//        String body = mGson.toJson(object);
//        Log.e(TAG,"modifyCards:"+body);
        mNetwork.sendRequest(tag, ResponseStatus.class, Method.PUT, createUrl(NetWork.PARAM_USERS, userId,NetWork.PARAM_CARDS), null, null, body, listener, errorListener, deliverParam);
    }

    public void getCards(String tag, String userId, Listener<CardsList> listener, ErrorListener errorListener, Object deliverParam) {
        mNetwork.sendRequest(tag, CardsList.class, Method.GET, createUrl(NetWork.PARAM_USERS, userId,NetWork.PARAM_CARDS), null, null, null, listener, errorListener, deliverParam);
    }

    public void getFingerPrints(String tag, String userId, Listener<FingerPrints> listener, ErrorListener errorListener, Object deliverParam) {
        mNetwork.sendRequest(tag, FingerPrints.class, Method.GET, createUrl(NetWork.PARAM_USERS, userId,NetWork.PARAM_FINGERPRINT), null, null, null, listener, errorListener, deliverParam);
    }

    public void modifyFingerPrints(String tag, String userId, ArrayList<ListFingerprintTemplate> fingerprints, Listener<ResponseStatus> listener, ErrorListener errorListener, Object deliverParam) {
        FingerPrints container = new FingerPrints(fingerprints);
        String body = mGson.toJson(container);
//        JsonArray arr = new JsonArray();
//        for (ListCard card:cards) {
//            arr.add(new JsonPrimitive(card.id));
//        }
//        object.add("ids",arr);
//        String body = mGson.toJson(object);
//        Log.e(TAG,"modifyCards:"+body);
        mNetwork.sendRequest(tag, ResponseStatus.class, Method.PUT, createUrl(NetWork.PARAM_USERS, userId,NetWork.PARAM_FINGERPRINT), null, null, body, listener, errorListener, deliverParam);
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
        int i = 0;
        for (; i < users.size(); i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append("\"");
            sb.append(users.get(i).user_id);
            sb.append("\"");
        }
        sb.append("]}");

        sendRequest(tag, ResponseStatus.class, Method.POST, createUrl(NetWork.PARAM_USERS,NetWork.PARAM_DELETE), null, null, sb.toString(), listener, errorListener, i);
        return i;
    }

    public void getUserGroups(String tag, Listener<UserGroups> listener, ErrorListener errorListener, int offset, int limit, String query, Object deliverParam) {
        mNetwork.sendRequest(tag, UserGroups.class, Method.GET, NetWork.PARAM_USER_GROUPS, null, createParams(offset, limit, null, query), null, listener, errorListener, deliverParam);
    }

    public void getUserPhoto(String tag, String userId, Response.Listener<String> listener, Response.ErrorListener errorListener, Object deliverParam) {
        sendRequest(tag, String.class, Method.GET, createUrl(NetWork.PARAM_USERS, userId, NetWork.PARAM_PHOTO), null, null, null, listener, errorListener, deliverParam);
    }
}
