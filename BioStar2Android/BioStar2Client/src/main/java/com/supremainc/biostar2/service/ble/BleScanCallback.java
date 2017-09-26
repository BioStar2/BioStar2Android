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

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.provider.AppDataProvider;

import java.util.List;

// unpublished
public class BleScanCallback extends ScanCallback {
    private final static String TAG = BleScanCallback.class.getSimpleName();

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        super.onBatchScanResults(results);
        for (ScanResult result:results) {
            if (onScanResultProcess(result)) {
                break;
            }
        }
    }

    @Override
    public void onScanFailed(int errorCode) {
        super.onScanFailed(errorCode);
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "onScanFailed:" + errorCode);
        }
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);
        onScanResultProcess(result);
    }

    public BleScanCallback(Context context,BleConnectImpl connectImpl) {
        super();
        mContext = context;
        mBleConnectImpl = connectImpl;
    }

    private boolean onScanResultProcess(ScanResult result) {
        return true;
    }


}
