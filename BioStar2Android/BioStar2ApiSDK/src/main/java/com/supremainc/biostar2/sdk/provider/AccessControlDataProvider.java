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

import com.supremainc.biostar2.sdk.models.v2.accesscontrol.AccessGroups;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static com.supremainc.biostar2.sdk.models.v2.common.VersionData.getCloudVersionString;

public class AccessControlDataProvider extends BaseDataProvider {
    private static AccessControlDataProvider mSelf = null;

    private AccessControlDataProvider(Context context) {
        super(context);
    }

    public static AccessControlDataProvider getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new AccessControlDataProvider(context);
        }
        return mSelf;
    }

    public static AccessControlDataProvider getInstance() {
        if (mSelf != null) {
            return mSelf;
        }
        if (mContext != null) {
            mSelf = new AccessControlDataProvider(mContext);
            return mSelf;
        }
        return null;
    }

    public Call<AccessGroups> getAccessGroups(int offset, int limit,String text,Callback<AccessGroups> callback) {
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
        if (text != null) {
            params.put("text",text);
        }
        Call<AccessGroups> call = mApiInterface.get_access_groups(getCloudVersionString(mContext),params);
        call.enqueue(callback);
        return call;
    }
}
