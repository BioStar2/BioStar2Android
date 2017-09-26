package com.supremainc.biostar2.service.nfc;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.provider.MobileCardDataProvider;

public class CardService extends HostApduService {
    private static final String TAG = "CardService";
    private static final byte[] UNKNOWN_CMD_SW = {0x6F, 0x00};
    private static MobileCardDataProvider mCardService;
    private static Context mContext;
    private static Toast mToast;
    private Handler mHandler;
    private Runnable mRunnableGuide = new Runnable() {
        @Override
        public void run() {
            if (mToast == null) {
                mToast = Toast.makeText(mContext, mContext.getString(R.string.nfc_on_guide), Toast.LENGTH_LONG);
            } else {
                mToast.setText(mContext.getString(R.string.nfc_on_guide));
            }
            mToast.show();
        }
    };

    private Runnable mInvalidToastRunnable = new Runnable() {
        @Override
        public void run() {
            if (mToast == null) {
                mToast = Toast.makeText(mContext,mContext.getString(R.string.invalid_card),Toast.LENGTH_LONG);
            } else {
                mToast.setText(mContext.getString(R.string.invalid_card));
            }
            mToast.show();
        }
    };
    private static String ByteArrayToHexString(byte[] bytes) {
        if (bytes == null) {
            Log.e(TAG, "ByteArrayToHexString is null");
        }
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "onStartCommand()");
            if (intent == null)
                Log.e("INTENT", "onStartCommand intent is null");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "!!!! Received APDU: " + ByteArrayToHexString(commandApdu));
        }

        if (mCardService == null) {
            mCardService = new MobileCardDataProvider();
        }
        if (mContext == null) {
            mContext = getApplicationContext();
        }

        if (!AppDataProvider.getInstance(getApplicationContext()).getBoolean(AppDataProvider.BooleanType.INDEPENDENT_SCREEN_LOCK,true)) {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (keyguardManager.inKeyguardRestrictedInputMode()) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Screen Lock");
                }
                return UNKNOWN_CMD_SW;
            }
        }
        if (!AppDataProvider.getInstance(getApplicationContext()).getBoolean(AppDataProvider.BooleanType.MOBILE_CARD_NFC,true)) {
            if (mHandler == null) {
                mHandler = new Handler(Looper.getMainLooper());
            }
            if (mHandler != null) {
                mHandler.post(mRunnableGuide);
            }
            return UNKNOWN_CMD_SW;
        }

        if (commandApdu.length > 3 && commandApdu[0] == (byte)0x00 && commandApdu[1] ==  (byte)0x84 && commandApdu[2] ==  (byte)0x00 && commandApdu[3] ==  (byte)0x00) {
            Intent intent = new Intent(Setting.BROADCAST_NFC_CONNECT);
            getApplicationContext().sendBroadcast(intent);
        }
        byte[] ret = mCardService.processCommandApduNFC(commandApdu, mContext);

        if (ret == null || (ret[0] == (byte) 0x8f && ret[1] == (byte) 0x8f)) {
            if (mHandler == null) {
                mHandler = new Handler(Looper.getMainLooper());
            }
            if (mHandler != null) {
                mHandler.post(mInvalidToastRunnable);
            }
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "!!!! send APDU: " + ByteArrayToHexString(UNKNOWN_CMD_SW));
            }
            return UNKNOWN_CMD_SW;
        }
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "!!!! send APDU: " + ByteArrayToHexString(ret));
        }
        return ret;
    }


    @Override
    public void onDeactivated(int reason) {
        if (BuildConfig.DEBUG) {
            Log.e("cardtest", "onDeactivated:" + reason);
        }
    }

}

