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
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import com.supremainc.biostar2.sdk.models.v2.card.MobileCard;
import com.supremainc.biostar2.sdk.models.v2.card.MobileCardRaw;
import com.supremainc.biostar2.sdk.models.v2.card.MobileCards;
import com.supremainc.biostar2.sdk.provider.UserDataProvider;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileCardDataProvider {
    private final static String TAG = "MobileCardDataProvider";
    private static final byte[] FAIL = {(byte) 0x6F, (byte) 0x00};
    private static KeyStore.PrivateKeyEntry mPrivateKeyEntry;

    static {
        System.loadLibrary("native-lib");
    }

    private native boolean setCard(byte[] key, byte[] data, Context context, PrivateKey privateKey);

    private native boolean create(PublicKey publicKey, Context context);

    private native byte[] nProcessCommandApduNFC(byte[] cmd, Context context, PrivateKey privateKey);

    private native boolean verifyNFC(byte[] data, int type);

    private native byte[] nProcessCommandApduBLE(byte[] cmd, Context context, PrivateKey privateKey);

    private native boolean verifyBLE(byte[] data, int type);

    private native boolean nDeleteCard(Context context);

    private native int nVerifyCard(Context context, PrivateKey privateKey);

    private native boolean nRegister(Context context, String id);

    private native boolean nGetCard(Context context, String id);

    public boolean setCard(String key, String data, Context context) {
        if (data == null || data.isEmpty()) {
            return false;
        }
        try {
            if (!createNewKey(context)) {
                return false;
            }
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("master", null);
            boolean result;
            if (key != null) {
                result = setCard(key.getBytes("UTF-8"), data.getBytes("UTF-8"), context, privateKeyEntry.getPrivateKey());
            } else {
                result = setCard(null, data.getBytes("UTF-8"), context, privateKeyEntry.getPrivateKey());
            }
            Log.e(TAG, "setCard result:" + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "e:" + e.getMessage());
            return false;
        }
    }

    public byte[] processCommandApduNFC(byte[] cmd, Context context) {
        if (mPrivateKeyEntry == null && context != null) {
            getPrivateKey(context);
        }
        if (cmd == null || cmd.length < 4) {
            return FAIL;
        }
        if (context != null && mPrivateKeyEntry != null && cmd[1] == (byte) 0x84) {
            return nProcessCommandApduNFC(cmd, context, mPrivateKeyEntry.getPrivateKey());
        } else {
            return nProcessCommandApduNFC(cmd, context, null);
        }
    }

    public byte[] processCommandApduBLE(byte[] cmd, Context context) {
        if (mPrivateKeyEntry == null && context != null) {
            getPrivateKey(context);
        }
        if (cmd == null || cmd.length < 4) {
            return FAIL;
        }
        if (context != null && mPrivateKeyEntry != null && cmd[1] == (byte) 0x84) {
            return nProcessCommandApduBLE(cmd, context, mPrivateKeyEntry.getPrivateKey());
        } else {
            return nProcessCommandApduBLE(cmd, context, null);
        }
    }

    private boolean getPrivateKey(Context context) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            mPrivateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("master", null);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "e:" + e.getMessage());
            return false;
        }
    }

    public boolean createNewKey(Context context) {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            String alias = "master";
            if (!keyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                start.set(Calendar.YEAR, 2000);
                Calendar end = Calendar.getInstance();
                end.set(Calendar.YEAR, 3000);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        //                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSubject(new X500Principal("CN=" + alias))
                        .setSerialNumber(BigInteger.TEN)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                generator.initialize(spec);
                KeyPair keyPair = generator.generateKeyPair();
            }
        } catch (Exception e) {
            Log.e(TAG, "e:" + e.getMessage());
            return false;
        }
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("master", null);
            create(privateKeyEntry.getCertificate().getPublicKey(), context);
        } catch (Exception e) {
            Log.e(TAG, "e:" + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean deleteCard(Context context) {
        if (context == null) {
            return false;
        }
        return nDeleteCard(context);
    }

    public CARD_VERIFY Verify(Context context) {
        if (!createNewKey(context)) {
            return CARD_VERIFY.NONE;
        }
        if (mPrivateKeyEntry == null && context != null) {
            getPrivateKey(context);
        }

        int result = nVerifyCard(context, mPrivateKeyEntry.getPrivateKey());
        switch (result) {
            case 0:
                return CARD_VERIFY.VALID;
            case 1:
                return CARD_VERIFY.INVALID;
            case 2:
                return CARD_VERIFY.NONE;
        }
        return CARD_VERIFY.INVALID;
    }

    public void registerMobileCard(Context context, String cardID, Callback<MobileCardRaw> callback) {
        if (context == null || cardID == null) {
            return ;
        }
        UserDataProvider userDataProvider = UserDataProvider.getInstance(context);
        nRegister(context, cardID);
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
                        nGetCard(context, card.id);
                    } else {
                        nGetCard(context, "0");
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
