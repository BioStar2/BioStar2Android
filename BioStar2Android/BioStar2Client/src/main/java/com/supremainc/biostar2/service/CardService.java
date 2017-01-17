package com.supremainc.biostar2.service;

import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by jblee on 2016-05-11.
 */
public class CardService extends HostApduService {
    private static final String TAG = "CardService";
    // AID for our loyalty card service.
    private static final String SAMPLE_LOYALTY_CARD_AID = "F053555052454D410000000000000001";
    private static final byte[] SELECT_OK_SW = HexStringToByteArray("9000");
    // "UNKNOWN" status word sent in response to invalid APDU command (0x0000)
    private static final byte[] UNKNOWN_CMD_SW = HexStringToByteArray("6F00");

    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("cardTest", "onStartCommand()");

        if (intent == null)
            Log.e("INTENT", "onStartCommand intent is null");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        return UNKNOWN_CMD_SW;
    }


    @Override
    public void onDeactivated(int reason) {
        Log.e("cardtest", "onDeactivated:" + reason);
    }

}

