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
package com.supremainc.biostar2.provider;

import android.content.Context;

import com.supremainc.biostar2.datatype.MobileCardData;
import com.supremainc.biostar2.sdk.datatype.v2.AccessControl.ListAccessGroup;
import com.supremainc.biostar2.sdk.datatype.v2.User.User;
import com.supremainc.biostar2.sdk.provider.BaseDataProvider;
import com.supremainc.biostar2.sdk.provider.NetWork;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;
import com.supremainc.biostar2.sdk.volley.Request;
import com.supremainc.biostar2.sdk.volley.Response;


import java.util.ArrayList;


public class MobileCardDataProvider extends BaseDataProvider {
    private static MobileCardDataProvider mSelf = null;

    private MobileCardDataProvider(Context context) {
        super(context);
    }

    public static MobileCardDataProvider getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new MobileCardDataProvider(context);
        }
        return mSelf;
    }

    public static MobileCardDataProvider getInstance() {
        if (mSelf != null) {
            return mSelf;
        }
        if (mContext != null) {
            mSelf = new MobileCardDataProvider(mContext);
            return mSelf;
        }
        return null;
    }

    public void getServerMobileCard(String tag, Response.Listener<MobileCardData.MobileCards> listener,
                                    Response.ErrorListener errorListener,String userID, Object deliverParam) {
        String url  = createUrl(NetWork.PARAM_USERS,userID,NetWork.PARAM_CARDS_MOBILE_CREDENTIAL);
        sendRequest(tag, MobileCardData.MobileCards.class, Request.Method.GET, url, null, null, null, listener, errorListener, deliverParam);
    }
}
