/*
 * Copyright (C) 2013 The Android Open Source Project
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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */

public class BluetoothLeServiceManager {

    private final static String TAG = BluetoothLeServiceManager.class.getSimpleName();
    private static BluetoothLeService mBluetoothLeService;
    private static final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if (com.supremainc.biostar2.BuildConfig.DEBUG) {
                Log.i(TAG, "onServiceDisconnected");
            }
            mBluetoothLeService = null;
        }
    };

    public static void setExist(boolean exist) {

    }

    public static void start(Activity a) {
        if (mBluetoothLeService == null) {
            Intent gattServiceIntent = new Intent(a, BluetoothLeService.class);
            a.bindService(gattServiceIntent, mServiceConnection, a.BIND_AUTO_CREATE);
        }
    }

    public static void stop(Activity a) {
        if (mBluetoothLeService != null) {
            try {
                a.unbindService(mServiceConnection);
            } catch (Exception e) {
                if (com.supremainc.biostar2.BuildConfig.DEBUG) {
                    Log.e(TAG, "e:" + e.getMessage());
                }
            }
        }
        mBluetoothLeService = null;
    }

    public static boolean isIdle() {
        if (com.supremainc.biostar2.BuildConfig.DEBUG) {
            Log.e(TAG, "isIdle isScanning:"+ isScanning()+" isConnected:"+isConnected()+" mBluetoothLeService:"+mBluetoothLeService);
        }
        if (mBluetoothLeService == null) {
            return true;
        }
        if (isScanning() || isConnected()) {
            return false;
        }
        return true;
    }

    public static boolean isScanning() {
        return false;
    }

    public static boolean isConnected() {
        return false;
    }

    public static boolean scan(boolean isScan) {
        return false;
    }

    public static void setRange(int range) {

    }


}
