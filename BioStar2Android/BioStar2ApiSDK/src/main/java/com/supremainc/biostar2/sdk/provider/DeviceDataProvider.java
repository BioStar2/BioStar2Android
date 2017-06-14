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

import com.google.gson.JsonObject;
import com.supremainc.biostar2.sdk.models.v2.card.Card;
import com.supremainc.biostar2.sdk.models.v2.device.Device;
import com.supremainc.biostar2.sdk.models.v2.device.DeviceTypes;
import com.supremainc.biostar2.sdk.models.v2.device.Devices;
import com.supremainc.biostar2.sdk.models.v2.device.FingerprintVerify;
import com.supremainc.biostar2.sdk.models.v2.face.Face;
import com.supremainc.biostar2.sdk.models.v2.fingerprint.ListFingerprintTemplate;
import com.supremainc.biostar2.sdk.models.v2.fingerprint.ScanFingerprintTemplate;
import com.supremainc.biostar2.sdk.models.v2.fingerprint.VerifyFingerprintOption;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static com.supremainc.biostar2.sdk.models.v2.common.VersionData.getCloudVersionString;

public class DeviceDataProvider extends BaseDataProvider {
    private static DeviceDataProvider mSelf = null;
    @SuppressWarnings("unused")
    private final String TAG = getClass().getSimpleName();

    private DeviceDataProvider(Context context) {
        super(context);
    }

    public static DeviceDataProvider getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new DeviceDataProvider(context);
        }
        return mSelf;
    }

    public static DeviceDataProvider getInstance() {
        if (mSelf != null) {
            return mSelf;
        }
        if (mContext != null) {
            mSelf = new DeviceDataProvider(mContext);
            return mSelf;
        }
        return null;
    }

    public Call<Devices> getDevices(int offset, int limit, String query, Callback<Devices> callback) {
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
        if (query != null) {
            params.put("text", query);
        }
        Call<Devices> call = mApiInterface.get_devices(getCloudVersionString(mContext), params);
        call.enqueue(callback);
        return call;
    }

    public Call<Device> getDevice(String id, Callback<Device> callback) {
        if (!checkParamAndAPI(callback,id)) {
            return null;
        }
        Call<Device> call = mApiInterface.get_devices(getCloudVersionString(mContext), id);
        call.enqueue(callback);
        return call;
    }

    public Call<DeviceTypes> getDeviceTypes(Callback<DeviceTypes> callback) {
        if (!checkAPI(callback)) {
            return null;
        }
        Call<DeviceTypes> call = mApiInterface.get_devices_type(getCloudVersionString(mContext));
        call.enqueue(callback);
        return call;
    }

    public Call<ScanFingerprintTemplate> scanFingerprint(String deviceid, int quality, boolean getImage, Callback<ScanFingerprintTemplate> callback) {
        if (!checkParamAndAPI(callback,deviceid)) {
            return null;
        }
        JsonObject object = new JsonObject();
        object.addProperty(Device.SCAN_FINGERPRINT_ENROLL_QUALITY, quality);
        object.addProperty(Device.SCAN_FINGERPRINT_GET_IMAGE, getImage);
        Call<ScanFingerprintTemplate> call = mApiInterface.post_scan_fingerprint(getCloudVersionString(mContext), deviceid, object);
        call.enqueue(callback);
        return call;
    }

    public Call<FingerprintVerify> verifyFingerprint(String deviceid, ListFingerprintTemplate fingerprintTemplate, Callback<FingerprintVerify> callback) {
        if (!checkParamAndAPI(callback,deviceid,fingerprintTemplate)) {
            return null;
        }
        VerifyFingerprintOption verifyFingerprintOption = new VerifyFingerprintOption("DEFAULT", fingerprintTemplate.template0, fingerprintTemplate.template1);
        Call<FingerprintVerify> call = mApiInterface.post_verify_fingerprint(getCloudVersionString(mContext), deviceid, verifyFingerprintOption);
        call.enqueue(callback);
        return call;
    }

    public Call<Card> scanCard(String deviceid, Callback<Card> callback) {
        if (!checkParamAndAPI(callback,deviceid)) {
            return null;
        }
        Call<Card> call = mApiInterface.post_scan_card(getCloudVersionString(mContext), deviceid);
        call.enqueue(callback);
        return call;
    }

    public Call<Face> scanFace(String deviceid, int quality, Callback<Face> callback) {
        if (!checkParamAndAPI(callback,deviceid)) {
            return null;
        }
        JsonObject object = new JsonObject();
        if (quality > -1) {
            object.addProperty(Device.SCAN_FACE_SENSITIVITY, quality);
        } else {
            object.addProperty(Device.SCAN_FACE_SENSITIVITY, 4);
        }
        Call<Face> call = mApiInterface.post_scan_face(getCloudVersionString(mContext), deviceid, object);
        call.enqueue(callback);
        return call;
    }
}
