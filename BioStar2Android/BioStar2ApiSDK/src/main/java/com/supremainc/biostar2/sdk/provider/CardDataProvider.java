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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.supremainc.biostar2.sdk.datatype.v2.Card.BaseCard;
import com.supremainc.biostar2.sdk.datatype.v2.Card.Card;
import com.supremainc.biostar2.sdk.datatype.v2.Card.Cards;
import com.supremainc.biostar2.sdk.datatype.v2.Card.SmartCardLayouts;
import com.supremainc.biostar2.sdk.datatype.v2.Card.WiegandFormat;
import com.supremainc.biostar2.sdk.datatype.v2.Card.WiegandFormats;
import com.supremainc.biostar2.sdk.datatype.v2.Common.ResponseStatus;
import com.supremainc.biostar2.sdk.datatype.v2.Common.SimpleDatas;
import com.supremainc.biostar2.sdk.volley.Network;
import com.supremainc.biostar2.sdk.volley.Request.Method;
import com.supremainc.biostar2.sdk.volley.Response.ErrorListener;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CardDataProvider extends BaseDataProvider {
    private static CardDataProvider mSelf = null;
    @SuppressWarnings("unused")
    private final String TAG = getClass().getSimpleName();

    public enum SmartCardType {
        SECURE_CREDENTIAL,ACCESS_ON
     }

    private CardDataProvider(Context context) {
        super(context);
    }

    public static CardDataProvider getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new CardDataProvider(context);
        }
        return mSelf;
    }

    public static CardDataProvider getInstance() {
        if (mSelf != null) {
            return mSelf;
        }
        if (mContext != null) {
            mSelf = new CardDataProvider(mContext);
            return mSelf;
        }
        return null;
    }

    public void registerCSN(String tag, Listener<ResponseStatus> listener, ErrorListener errorListener, String cardID, Object deliverParam) {
        if (cardID == null || cardID.isEmpty()) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new VolleyError("param is null"), deliverParam);
            }
            return;
        }

        JsonObject object = new JsonObject();
        object.addProperty(Card.CARD_ID, cardID);
        String body = mGson.toJson(object);
        sendRequest(tag, ResponseStatus.class, Method.POST, createUrl(NetWork.PARAM_CARDS,NetWork.PARAM_CSN_CARD), null, null, body, listener, errorListener, deliverParam);
    }

    public void registerWiegand(String tag, Listener<ResponseStatus> listener, ErrorListener errorListener, WiegandFormat wiegandFormat, Object deliverParam) {
        if (wiegandFormat == null) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new VolleyError("param is null"), deliverParam);
            }
            return;
        }
        String body = mGson.toJson(wiegandFormat);
        sendRequest(tag, ResponseStatus.class, Method.POST, createUrl(NetWork.PARAM_CARDS,NetWork.PARAM_WIEGAND_CARDS), null, null, body, listener, errorListener, deliverParam);
    }


    public void issueAccessOn(String tag, Listener<ResponseStatus> listener, ErrorListener errorListener, String deviceID,ArrayList<Integer> fingerPrintIndexs,String userID, Object deliverParam) {
        if (deviceID == null || deviceID.isEmpty() || fingerPrintIndexs == null || userID == null || userID.isEmpty()) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new VolleyError("param is null"), deliverParam);
            }
            return;
        }

        JsonObject object = new JsonObject();
        object.addProperty("device_id", deviceID);
        object.addProperty("user_id", userID);
        JsonArray arr = new JsonArray();
        for (Integer index:fingerPrintIndexs) {
            arr.add(new JsonPrimitive(index));
        }
        object.add("fingerprint_index_list",arr);
        String body = mGson.toJson(object);
        sendRequest(tag, ResponseStatus.class, Method.POST, createUrl(NetWork.PARAM_CARDS,NetWork.PARAM_ACCESS_ON), null, null, body, listener, errorListener, deliverParam);
    }

    public void issueSecureCredential(String tag, Listener<ResponseStatus> listener, ErrorListener errorListener, String deviceID,ArrayList<Integer> fingerPrintIndexs,String userID,String cardID, Object deliverParam) {
        if (deviceID == null || deviceID.isEmpty() || fingerPrintIndexs == null || userID == null || userID.isEmpty()) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new VolleyError("param is null"), deliverParam);
            }
            return;
        }
        if (cardID == null) {
            cardID = userID;
        }

        JsonObject object = new JsonObject();
        object.addProperty("device_id", deviceID);
        object.addProperty("user_id", userID);
        object.addProperty("card_id", cardID);
        JsonArray arr = new JsonArray();
        for (Integer index:fingerPrintIndexs) {
            arr.add(new JsonPrimitive(index));
        }
        object.add("fingerprint_index_list",arr);
        String body = mGson.toJson(object);
        sendRequest(tag, ResponseStatus.class, Method.POST, createUrl(NetWork.PARAM_CARDS,NetWork.PARAM_CARDS_SECURE_CREDENTIAL), null, null, body, listener, errorListener, deliverParam);
    }

    public void getUnassignedCards(String tag, Listener<Cards> listener, ErrorListener errorListener, int offset, int limit, String query, Object deliverParam) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));
        if (query != null) {
            params.put("text", query);
        }

        sendRequest(tag, Cards.class, Method.GET, createUrl(NetWork.PARAM_CARDS,NetWork.PARAM_UNASSIGNED), null, params, null, listener, errorListener, deliverParam);
    }

    public void getWiegandFormats(String tag, Listener<WiegandFormats> listener, ErrorListener errorListener, Object deliverParam) {
        String url = createUrl(NetWork.PARAM_CARDS,NetWork.PARAM_WIEGAND_CARDS, NetWork.PARAM_FORMATS);
//        Map<String, String> params = new HashMap<String, String>();
////        params.put("limit", String.valueOf(limit));
////        params.put("offset", String.valueOf(offset));
////        if (query != null) {
////            params.put("text", query);
////        }
        sendRequest(tag, WiegandFormats.class, Method.GET, url, null, null, null, listener, errorListener, deliverParam);
    }

    public void getSmartCardLayout(String tag, Listener<SmartCardLayouts> listener, ErrorListener errorListener, int offset, int limit, String query, Object deliverParam) {
        String url = createUrl(NetWork.PARAM_CARDS,NetWork.PARAM_SMART_CARDS,NetWork.PARAM_LAYOUTS);
        Map<String, String> params = new HashMap<String, String>();
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));
        if (query != null) {
            params.put("text", query);
        }
        sendRequest(tag, SmartCardLayouts.class, Method.GET, url, null, params, null, listener, errorListener, deliverParam);
    }

    public void issueMobileCard(String tag, Listener<ResponseStatus> listener, ErrorListener errorListener,String userID, String cardID, ArrayList<Integer> fingerprint, String layoutID,SmartCardType type, Object deliverParam) {
        if (cardID == null || cardID.isEmpty() || fingerprint == null || layoutID == null || layoutID.isEmpty() ||  type == null || userID == null || userID.isEmpty()) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new VolleyError("param is null"), deliverParam);
            }
            return;
        }
        String url  = createUrl(NetWork.PARAM_USERS, userID,NetWork.PARAM_CARDS_MOBILE_CREDENTIAL,NetWork.PARAM_CARDS_ISSUE) ;

        JsonObject object = new JsonObject();
        object.addProperty("card_id", cardID);
        object.addProperty("layout_id", layoutID);
        switch (type) {
            case ACCESS_ON:
                object.addProperty("type", Card.ACCESS_ON);
                break;
            case SECURE_CREDENTIAL:
                object.addProperty("type", Card.SECURE_CREDENTIAL);
                  break;
        }
        JsonArray arr = new JsonArray();
        for (Integer index:fingerprint) {
            arr.add(new JsonPrimitive(index));
        }
        object.add("fingerprint_index_list",arr);
        String body = mGson.toJson(object);
        sendRequest(tag, ResponseStatus.class, Method.POST, url, null, null, body, listener, errorListener, deliverParam);
    }

    public void block(String tag, Listener<ResponseStatus> listener, ErrorListener errorListener, String cardID, Object deliverParam) {
        if (cardID == null) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new VolleyError("param is null"), deliverParam);
            }
            return;
        }
        String url  = createUrl(NetWork.PARAM_CARDS,cardID,NetWork.PARAM_CARDS_BLOCK);
        sendRequest(tag, ResponseStatus.class, Method.POST, url, null, null, null, listener, errorListener, deliverParam);
    }

    public void unblock(String tag, Listener<ResponseStatus> listener, ErrorListener errorListener, String cardID, Object deliverParam) {
        if (cardID == null) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new VolleyError("param is null"), deliverParam);
            }
            return;
        }
        String url  = createUrl(NetWork.PARAM_CARDS,cardID,NetWork.PARAM_CARDS_UNBLOCK);
        sendRequest(tag, ResponseStatus.class, Method.POST, url, null, null, null, listener, errorListener, deliverParam);
    }

    public void reissue(String tag, Listener<ResponseStatus> listener, ErrorListener errorListener,String userID, String cardID, Object deliverParam) {
        if (cardID == null) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new VolleyError("param is null"), deliverParam);
            }
            return;
        }
        String url  = createUrl(NetWork.PARAM_USERS,userID,NetWork.PARAM_CARDS_MOBILE_CREDENTIAL,cardID,NetWork.PARAM_CARDS_REISSUE);
        sendRequest(tag, ResponseStatus.class, Method.POST, url, null, null, null, listener, errorListener, deliverParam);
    }
}
