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

import com.google.gson.JsonObject;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.models.v2.door.Door;
import com.supremainc.biostar2.sdk.models.v2.door.Doors;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static com.supremainc.biostar2.sdk.models.v2.common.VersionData.getCloudVersionString;

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

    public Call<Doors> getDoors(int offset, int limit, String query, Callback<Doors> callback) {
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
        Call<Doors> call = mApiInterface.get_doors(getCloudVersionString(mContext), params);
        call.enqueue(callback);
        return call;
    }

    public Call<Door> getDoor(String id, Callback<Door> callback) {
        if (!checkParamAndAPI(callback,id)) {
            return null;
        }
        Call<Door> call = mApiInterface.get_door(getCloudVersionString(mContext), id);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> openDoor(String id, Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,id)) {
            return null;
        }
        Call<ResponseStatus> call = mApiInterface.post_doors_open(getCloudVersionString(mContext), id);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> unlockDoor(String id, Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,id)) {
            return null;
        }
        Call<ResponseStatus> call = mApiInterface.post_doors_unlock(getCloudVersionString(mContext), id);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> lockDoor(String id, Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,id)) {
            return null;
        }
        Call<ResponseStatus> call = mApiInterface.post_doors_lock(getCloudVersionString(mContext), id);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> releaseDoor(String id, Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,id)) {
            return null;
        }
        Call<ResponseStatus> call = mApiInterface.post_doors_release(getCloudVersionString(mContext), id);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> clearAlarm(String id, Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,id)) {
            return null;
        }
        Call<ResponseStatus> call = mApiInterface.post_doors_clear_alaram(getCloudVersionString(mContext), id);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> clearAntiPassback(String id, Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,id)) {
            return null;
        }
        Call<ResponseStatus> call = mApiInterface.post_doors_clear_antipassback(getCloudVersionString(mContext), id);
        call.enqueue(callback);
        return call;
    }

    public Call<ResponseStatus> openRequestDoor(String id,String phone, Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,id)) {
            return null;
        }
        JsonObject object = null;
        if (phone != null) {
            object = new JsonObject();
            object.addProperty("phone_number", phone);
        }
        //TODO test 필요
        Call<ResponseStatus> call = mApiInterface.post_doors_request_open(getCloudVersionString(mContext), id,object);
        call.enqueue(callback);
        return call;
    }
}
