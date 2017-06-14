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
import android.os.Build;
import android.provider.Settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.supremainc.biostar2.sdk.models.v2.card.Card;
import com.supremainc.biostar2.sdk.models.v2.card.CardsList;
import com.supremainc.biostar2.sdk.models.v2.card.ListCard;
import com.supremainc.biostar2.sdk.models.v2.card.MobileCardRaw;
import com.supremainc.biostar2.sdk.models.v2.card.MobileCards;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.models.v2.face.Faces;
import com.supremainc.biostar2.sdk.models.v2.fingerprint.FingerPrints;
import com.supremainc.biostar2.sdk.models.v2.fingerprint.ListFingerprintTemplate;
import com.supremainc.biostar2.sdk.models.v2.user.ListUser;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.sdk.models.v2.user.UserGroups;
import com.supremainc.biostar2.sdk.models.v2.user.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static com.supremainc.biostar2.sdk.models.v2.common.VersionData.getCloudVersionString;

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

    public Call<ResponseStatus> createUser(User item, Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,item)) {
            return null;
        }
        Call<ResponseStatus> call = mApiInterface.post_users(getCloudVersionString(mContext),item);
        call.enqueue(callback);
        return call;
    }

    public Call<User> getUser(String userId, Callback<User> callback) {
        if (!checkParamAndAPI(callback,userId)) {
            return null;
        }
        Call<User> call = mApiInterface.get_users_id(getCloudVersionString(mContext),userId);
        call.enqueue(callback);
        return call;
    }

    public String getUserPhotoUrl(String userId) {
        if (getServerUrl() == null || getCloudVersionString(mContext) == null || userId == null) {
            return null;
        }
        return getServerUrl()+getCloudVersionString(mContext)+"/users/"+userId+"/photo";
    }

    public Call<Users> getUsers(int offset, int limit, String groupId, String query,Callback<Users> callback) {
        if (limit > 100 ) {
            limit = 100;
        }
        if (limit < 0) {
            onParamError(callback);
            return null;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));
        if (query != null) {
            params.put("text", query);
        }
        if (groupId != null) {
            params.put("group_id", groupId);
        }
        Call<Users> call = mApiInterface.get_users(getCloudVersionString(mContext),params);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> modifyUser(User user,Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,user)) {
            return null;
        }
        Call<ResponseStatus> call = mApiInterface.put_users(getCloudVersionString(mContext),user.user_id,user);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> modifyMyProfile(User user,Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,user)) {
            return null;
        }
        Call<ResponseStatus> call = mApiInterface.put_my_profile(getCloudVersionString(mContext),user);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> modifyCards(String userId, ArrayList<ListCard> cards, Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,userId,cards)) {
            return null;
        }
        CardsList container = new CardsList(cards);
        Call<ResponseStatus> call = mApiInterface.put_users_id_cards(getCloudVersionString(mContext),userId,container);
        call.enqueue(callback);
        return call;
    }

    public Call<CardsList> getCards(String userId,Callback<CardsList> callback) {
        if (!checkParamAndAPI(callback,userId)) {
            return null;
        }
        Call<CardsList> call = mApiInterface.gut_users_id_cards(getCloudVersionString(mContext),userId);
        call.enqueue(callback);
        return call;
    }

    public Call<FingerPrints> getFingerPrints(String userId,Callback<FingerPrints> callback) {
        if (!checkParamAndAPI(callback,userId)) {
            return null;
        }
        Call<FingerPrints> call = mApiInterface.gut_users_id_fingerprint(getCloudVersionString(mContext),userId);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> modifyFingerPrints(String userId, ArrayList<ListFingerprintTemplate> fingerprints, Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,userId,fingerprints)) {
            return null;
        }
        FingerPrints container = new FingerPrints(fingerprints);
        Call<ResponseStatus> call = mApiInterface.put_users_id_fingerprint(getCloudVersionString(mContext),userId,container);
        call.enqueue(callback);
        return call;
    }

    public Call<Faces> getFace(String userId, Callback<Faces> callback) {
        if (!checkParamAndAPI(callback,userId)) {
            return null;
        }
        Call<Faces> call = mApiInterface.get_users_id_face_templates(getCloudVersionString(mContext),userId);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> modifyFaces(String userId, Faces faces, Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,userId,faces)) {
            return null;
        }
        Call<ResponseStatus> call = mApiInterface.put_users_id_face_templates(getCloudVersionString(mContext),userId,faces);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> deleteUser(String userId,Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,userId)) {
            return null;
        }
        Call<ResponseStatus> call = mApiInterface.delete_users_id(getCloudVersionString(mContext),userId);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> deleteUsers(ArrayList<ListUser> users,Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,users)) {
            return null;
        }
        if (users.size() < 1) {
            onParamError(callback);
            return null;
        }
        //TODO test 필요
        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();
        for (ListUser user:users) {
            array.add(user.user_id);
        }
        object.add("ids",array);
        Call<ResponseStatus> call = mApiInterface.delete_users(getCloudVersionString(mContext),object);
        call.enqueue(callback);
        return call;
    }

    public Call<UserGroups> getUserGroups(int offset, int limit, String query,Callback<UserGroups> callback) {
        if (limit > 100 ) {
            limit = 100;
        }
        if (limit < 0) {
            onParamError(callback);
            return null;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));
        if (query != null) {
            params.put("text", query);
        }

        Call<UserGroups> call = mApiInterface.get_user_group(getCloudVersionString(mContext),params);
        call.enqueue(callback);
        return call;
    }

    public Call<MobileCards> getMobileCards(String userId, Callback<MobileCards> callback) {
        if (!checkParamAndAPI(callback,userId)) {
            return null;
        }
        Call<MobileCards> call = mApiInterface.gut_users_id_mobile_credentials(getCloudVersionString(mContext),userId);
        call.enqueue(callback);
        return call;
    }

    public Call<MobileCards> getMobileCards(Callback<MobileCards> callback) {
        if (mUserInfo == null) {
            onParamError(callback);
            return null;
        }
        if (!checkAPI(callback)) {
            return null;
        }
        Call<MobileCards> call = mApiInterface.gut_users_my_profile_mobile_credentials(getCloudVersionString(mContext));
        call.enqueue(callback);
        return call;
    }

    public Call<MobileCardRaw> registerMobileCard(String cardID, Callback<MobileCardRaw> callback) {
        if (!checkParamAndAPI(callback,cardID)) {
            return null;
        }

        String udid =  Settings.Secure.getString(mContext.getContentResolver(),Settings.Secure.ANDROID_ID)+ Build.SERIAL;
        if (udid != null && udid.length() > 128) {
            udid = udid.substring(0,128);
        }
        JsonObject object = new JsonObject();
        object.addProperty("udid", udid);
        Call<MobileCardRaw> call = mApiInterface.post_users_my_profile_mobile_credentials(getCloudVersionString(mContext),cardID,object);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> issueMobileCard(String userID, String cardID, ArrayList<Integer> fingerprint, String layoutID, CardDataProvider.SmartCardType type, Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,userID,cardID,layoutID,type,fingerprint)) {
            return null;
        }

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
        Call<ResponseStatus> call = mApiInterface.post_users_id_mobile_credentials(getCloudVersionString(mContext),userID,object);
        call.enqueue(callback);
        return call;
    }


}
