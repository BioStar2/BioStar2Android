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
package com.supremainc.biostar2.service.ble;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.provider.AppDataProvider;

public class BootBroadcastReceiver extends WakefulBroadcastReceiver {
    public static final String TAG = BootBroadcastReceiver.class.getSimpleName();
    private static EventListener mEventListener;

    public BootBroadcastReceiver() {
        super();
    }

    public BootBroadcastReceiver(EventListener eventListener) {
        super();
        mEventListener = eventListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "intent receiver:" + intent.getAction());
        }
        if (intent.getAction().equals(Setting.BROADCAST_BLE_CMD_SCAN)) {
            if (mEventListener != null) {
                AppDataProvider appDataProvider = AppDataProvider.getInstance(context);
                if (appDataProvider.getBoolean(AppDataProvider.BooleanType.MOBILE_CARD_BLE,false)) {
                    mEventListener.startScan(false);
                }
            }
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            AppDataProvider appDataProvider = AppDataProvider.getInstance(context);
//            Setting.knockknocTime = SystemClock.elapsedRealtime();
            if (mEventListener != null && appDataProvider.getBoolean(AppDataProvider.BooleanType.KNOCK_KNOCK,false)) {
                mEventListener.screen(true);
            }
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            AppDataProvider appDataProvider = AppDataProvider.getInstance(context);
            if (mEventListener != null && appDataProvider.getBoolean(AppDataProvider.BooleanType.KNOCK_KNOCK,false)) {
                mEventListener.screen(false);
            }
        } else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (AppDataProvider.getInstance(context).getBoolean(AppDataProvider.BooleanType.MOBILE_CARD_BLE,false)) {
                Intent i = new Intent();
                i.setClassName(Setting.APP_PACKAGE, Setting.BLE_SERVICE_RECO_PACKAGE);
                ComponentName name = context.startService(i);
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "name:" + name);
                }
            }
        } else if (intent.getAction().equals(Setting.BROADCAST_BLE_STARTED_SCAN)) {
            if (mEventListener != null) {
                mEventListener.startedScan();
            }
        } else if (intent.getAction().equals(Setting.BROADCAST_BLE_STOPED_SCAN)) {
            if (mEventListener != null) {
                mEventListener.stopedScan();
            }
        } else if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            if (mEventListener != null) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                mEventListener.onBleState(state);
            }
        } else if (intent.getAction().equals(Setting.BROADCAST_BLE_CMD_TURNON)) {
            if (mEventListener != null) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                mEventListener.turnOnBle();
            }
        } else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
//            KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
//            if (event == null) {
//                return;
//            }
//
//            if (KeyEvent.ACTION_DOWN == event.getAction()) {
//                if (mEventListener != null) {
//                    mEventListener.startScan();
//                }
//            }
        } else if (intent.getAction().equals(Setting.BROADCAST_BLE_CONNECT)) {
            if (mEventListener != null) {
                mEventListener.startVibrator(200);
            }
        } else if (intent.getAction().equals(Setting.BROADCAST_BLE_SUCESS)) {
            if (mEventListener != null) {
                mEventListener.startVibrator(1000);
                mEventListener.setWait();
                mEventListener.stopScan();
            }
        } else if (intent.getAction().equals(Setting.BROADCAST_BLE_ERROR_DEVICE)) {
            if (mEventListener != null) {
                mEventListener.startVibrator(200);
            }
        } else if (intent.getAction().equals(Setting.BROADCAST_BLE_ERROR_CONNECT)) {
            if (mEventListener != null) {
                mEventListener.startVibrator(200);
            }
        } else if (intent.getAction().equals(Setting.BROADCAST_BLE_ERROR_RESULT)) {
            if (mEventListener != null) {
                mEventListener.startVibrator(200);
            }
        }
    }

    public interface EventListener {
        void startScan(boolean checkLockScreen);
        void stopScan();
        void startedScan();
        void stopedScan();
        void setWait();
        void onBleState(int state);
        void turnOnBle();
        void screen(boolean on);
        void startVibrator(int time);
    }
}