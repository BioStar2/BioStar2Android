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

import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.models.v2.login.NotificationToken;
import com.supremainc.biostar2.sdk.utils.PreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;

import static com.supremainc.biostar2.sdk.models.v2.common.VersionData.getCloudVersionString;

public class PushDataProvider extends BaseDataProvider {
    private static final String NOTIFICATION_TOKEN = "notifytoken";
    private static PushDataProvider mSelf = null;
    private static boolean mIsRunning = false;
    private static String mToken;
    private static String mTransferToken;
    private final String TAG = getClass().getSimpleName();

    private PushDataProvider(Context context) {
        super(context);
    }

    public static PushDataProvider getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new PushDataProvider(context);
        }
        return mSelf;
    }

    private Callback<ResponseStatus> mCallback = new Callback<ResponseStatus>() {
        @Override
        public void onResponse(Call<ResponseStatus> call, retrofit2.Response<ResponseStatus> response) {
            mIsRunning = false;
            if (response.isSuccessful() && mTransferToken != null) {
                PreferenceUtil.putSharedPreference(mContext, NOTIFICATION_TOKEN, mTransferToken);
                if (!mTransferToken.equals(mToken) && mToken != null) {
                    mTransferToken = mToken;
                    NotificationToken notificationToken = new NotificationToken(mContext, mTransferToken);
                    Call<ResponseStatus> reCall = mApiInterface.put_update_notification_token(getCloudVersionString(mContext), notificationToken);
                    reCall.enqueue(null);
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            mIsRunning = false;
        }
    };
    public Call<ResponseStatus> setNeedUpdateNotificationToken(String token) {
        if (mIsRunning) {
            return null;
        }
        mTransferToken = token;
        mIsRunning = true;
        NotificationToken notificationToken = new NotificationToken(mContext, token);
        Call<ResponseStatus> call = mApiInterface.put_update_notification_token(getCloudVersionString(mContext), notificationToken);
        call.enqueue(mCallback);
        return call;
    }

    public void checkUpdateNotificationToken(String token) {
        if (token == null || token.length() < 1) {
            return;
        }
        String savedToken = PreferenceUtil.getSharedPreference(mContext, NOTIFICATION_TOKEN);
        if (savedToken != null && savedToken.equals(token)) {
            return;
        }
        mToken = token;

        setNeedUpdateNotificationToken(token);
    }
}
