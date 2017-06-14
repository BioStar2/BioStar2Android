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
import com.supremainc.biostar2.sdk.models.v2.card.Card;
import com.supremainc.biostar2.sdk.models.v2.card.Cards;
import com.supremainc.biostar2.sdk.models.v2.card.SmartCardLayouts;
import com.supremainc.biostar2.sdk.models.v2.card.WiegandFormat;
import com.supremainc.biostar2.sdk.models.v2.card.WiegandFormats;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static com.supremainc.biostar2.sdk.models.v2.common.VersionData.getCloudVersionString;

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

    public Call<ResponseStatus> registerCSN(String cardID,Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,cardID)) {
            return null;
        }
        JsonObject object = new JsonObject();
        object.addProperty(Card.CARD_ID, cardID);
        Call<ResponseStatus> call = mApiInterface.post_cards_csn(getCloudVersionString(mContext),object);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> registerWiegand(WiegandFormat wiegandFormat, Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,wiegandFormat)) {
            return null;
        }
        Call<ResponseStatus> call = mApiInterface.post_wiegand_card(getCloudVersionString(mContext),wiegandFormat);
        call.enqueue(callback);
        return call;
    }


    public Call<ResponseStatus>  issueAccessOn(String deviceID,ArrayList<Integer> fingerPrintIndexs,String userID,Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,deviceID,userID,fingerPrintIndexs)) {
            return null;
        }
        JsonObject object = new JsonObject();
        object.addProperty("device_id", deviceID);
        object.addProperty("user_id", userID);
        JsonArray arr = new JsonArray();
        for (Integer index:fingerPrintIndexs) {
            arr.add(new JsonPrimitive(index));
        }
        object.add("fingerprint_index_list",arr);
        Call<ResponseStatus> call = mApiInterface.post_access_on_card(getCloudVersionString(mContext),object);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> issueSecureCredential(String deviceID,ArrayList<Integer> fingerPrintIndexs,String userID,String cardID,Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,deviceID,userID,fingerPrintIndexs)) {
            return null;
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
        Call<ResponseStatus> call = mApiInterface.post_secure_credential_card(getCloudVersionString(mContext),object);
        call.enqueue(callback);
        return call;
    }

    public Call<Cards> getUnassignedCards(int offset, int limit, String query, Callback<Cards> callback) {
        if (offset < -1 || limit < 1) {
            onParamError(callback);
            return null;
        }
        if (!checkAPI(callback)) {
            return null;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));
        if (query != null) {
            params.put("text", query);
        }
        Call<Cards> call = mApiInterface.get_cards_unassigned(getCloudVersionString(mContext),params);
        call.enqueue(callback);
        return call;
    }

    public Call<WiegandFormats> getWiegandFormats(Callback<WiegandFormats> callback) {
        if (!checkAPI(callback)) {
            return null;
        }
        Call<WiegandFormats> call = mApiInterface.get_cards_wiegand_cards_formats(getCloudVersionString(mContext));
        call.enqueue(callback);
        return call;
    }

    public Call<SmartCardLayouts> getSmartCardLayout(int offset, int limit, String query,Callback<SmartCardLayouts> callback) {
        if (offset < -1 || limit < 1) {
            onParamError(callback);
            return null;
        }
        if (!checkAPI(callback)) {
            return null;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));
        if (query != null) {
            params.put("text", query);
        }
        Call<SmartCardLayouts> call = mApiInterface.get_cards_smartcards_layouts(getCloudVersionString(mContext),params);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus>  block(String cardID, Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,cardID)) {
            return null;
        }
        Call<ResponseStatus> call = mApiInterface.post_cards_block(getCloudVersionString(mContext),cardID);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> unblock(String cardID, Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,cardID)) {
            return null;
        }
        Call<ResponseStatus> call = mApiInterface.post_cards_unblock(getCloudVersionString(mContext),cardID);
        call.enqueue(callback);
        return call;
    }
}
