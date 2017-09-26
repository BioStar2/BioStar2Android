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

import com.supremainc.biostar2.sdk.models.v2.card.MobileCard;
import com.supremainc.biostar2.sdk.models.v2.card.MobileCardRaw;
import com.supremainc.biostar2.sdk.models.v2.card.MobileCards;
import com.supremainc.biostar2.sdk.provider.UserDataProvider;

import java.security.KeyStore;
import java.security.PrivateKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// contact to ts team
public class MobileCardDataProvider {
    private final static String TAG = "MobileCardDataProvider";
    private static final byte[] FAIL = {(byte) 0x6F, (byte) 0x00};
    private static KeyStore.PrivateKeyEntry mPrivateKeyEntry;

    static {
        System.loadLibrary("native-lib");
    }

    private native byte[] nProcessCommandApduNFC(byte[] cmd, Context context, PrivateKey privateKey);


    public boolean setCard(String key, String data, Context context) {
        return false;
    }

    public byte[] processCommandApduNFC(byte[] cmd, Context context) {
        return FAIL;
    }

    public byte[] processCommandApduBLE(byte[] cmd, Context context) {
        return FAIL;
    }

    private boolean getPrivateKey(Context context) {
        return false;
    }

    public boolean createNewKey(Context context) {
        return false;
    }

    public boolean deleteCard(Context context) {
        return false;
    }

    public CARD_VERIFY Verify(Context context) {
        return CARD_VERIFY.INVALID;
    }

    public void registerMobileCard(Context context, String cardID, Callback<MobileCardRaw> callback) {
        if (context == null || cardID == null) {
            return ;
        }
        UserDataProvider userDataProvider = UserDataProvider.getInstance(context);
        userDataProvider.registerMobileCard(cardID, callback);
    }

    public void getMobileCards(final Context context, final Callback<MobileCards> callback) {
        UserDataProvider userDataProvider = UserDataProvider.getInstance(context);
        userDataProvider.getMobileCards(new Callback<MobileCards>() {
            @Override
            public void onResponse(Call<MobileCards> call, Response<MobileCards> response) {
                if (!call.isCanceled() && response.isSuccessful() && response.body() != null) {
                    MobileCards cards = response.body();
                    if (cards.records != null && cards.records.size() > 0) {
                        MobileCard card = cards.records.get(0);
                    }
                }
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<MobileCards> call, Throwable t) {
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    public enum CARD_VERIFY {
        VALID, INVALID, NONE
    }
}
