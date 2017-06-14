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

import com.supremainc.biostar2.sdk.models.v2.eventlog.EventLogs;
import com.supremainc.biostar2.sdk.models.v2.eventlog.EventType;
import com.supremainc.biostar2.sdk.models.v2.eventlog.EventTypes;
import com.supremainc.biostar2.sdk.models.v2.eventlog.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static com.supremainc.biostar2.sdk.models.v2.common.VersionData.getCloudVersionString;

public class MonitoringDataProvider extends BaseDataProvider {
    private static MonitoringDataProvider mSelf = null;
    private final String TAG = getClass().getSimpleName();
    private Map<Integer, EventType> mMssageTable = new HashMap<Integer, EventType>();


    private MonitoringDataProvider(Context context) {
        super(context);
    }

    public static MonitoringDataProvider getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new MonitoringDataProvider(context);
        }
        return mSelf;
    }

    public static MonitoringDataProvider getInstance() {
        if (mSelf != null) {
            return mSelf;
        }
        if (mContext != null) {
            mSelf = new MonitoringDataProvider(mContext);
            return mSelf;
        }
        return null;
    }

    public Call<EventLogs> searchEventLog(Query query, Callback<EventLogs> callback) {
        if (!checkParamAndAPI(callback,query)) {
            return null;
        }
        Call<EventLogs> call = mApiInterface.post_monitoring_search(getCloudVersionString(mContext), query);
        call.enqueue(callback);
        return call;
    }

    public String getEventMessage(int code) {
        String result = "";
        if (!haveMessage()) {
            return "";
        }
        try {
            EventType item = mMssageTable.get(code);
            if (item != null) {
                return item.description;
            }
        } catch (Exception e) {
            if (ConfigDataProvider.DEBUG) {
                Log.e(TAG, "getEventmessage error:" + e.getMessage());
            }
        }
        return result;
    }

    public ArrayList<EventType> getEventTypeList() {
        if (!haveMessage()) {
            return null;
        }
        ArrayList<EventType> valueList = Collections.list(Collections.enumeration(mMssageTable.values()));
        return valueList;
    }

    public boolean haveMessage() {
        if (mMssageTable.size() < 1) {
            try {
                @SuppressWarnings("unchecked")
                ArrayList<EventType> rows = (ArrayList<EventType>) mFileUtil.loadFileObj(mContext, "message");
                if (rows == null) {
                    return false;
                }
                setEventMessage(rows, false);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public void setEventMessage(ArrayList<EventType> message, boolean save) {
        for (EventType etype : message) {
            mMssageTable.put(etype.code, etype);
        }
        if (save) {
            mFileUtil.saveFileObj(mContext, "message", message);
        }
    }

    public Call<EventTypes> getEventMessage(final Callback<EventTypes> callback) {
        if (!checkAPI(callback)) {
            return null;
        }
        Callback<EventTypes> innerCallback = new Callback<EventTypes>() {
            @Override
            public void onResponse(Call<EventTypes> call, retrofit2.Response<EventTypes> response) {
                if (response.isSuccessful() && response.body().records != null) {
                    setEventMessage(response.body().records, true);
                }
                if (callback != null) {
                    callback.onResponse(call,response);
                }
            }

            @Override
            public void onFailure(Call<EventTypes> call, Throwable t) {
                if (callback != null) {
                    callback.onFailure(call,t);
                }
            }
        };
        Call<EventTypes> call = mApiInterface.get_reference_event_type(getCloudVersionString(mContext));
        call.enqueue(innerCallback);
        return call;
    }
}
