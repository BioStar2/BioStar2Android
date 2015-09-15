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
import com.supremainc.biostar2.sdk.datatype.CardData.Card;
import com.supremainc.biostar2.sdk.datatype.CardData.Cards;
import com.supremainc.biostar2.sdk.datatype.DeviceData;
import com.supremainc.biostar2.sdk.datatype.DeviceData.Device;
import com.supremainc.biostar2.sdk.datatype.DeviceData.Devices;
import com.supremainc.biostar2.sdk.datatype.DeviceData.FingerprintVerify;
import com.supremainc.biostar2.sdk.datatype.DeviceTypeData.DeviceTypes;
import com.supremainc.biostar2.sdk.datatype.FingerPrintData.ListFingerprintTemplate;
import com.supremainc.biostar2.sdk.datatype.FingerPrintData.ScanFingerprintTemplate;
import com.supremainc.biostar2.sdk.datatype.FingerPrintData.VerifyFingerprintOption;
import com.supremainc.biostar2.sdk.volley.Request.Method;
import com.supremainc.biostar2.sdk.volley.Response.ErrorListener;
import com.supremainc.biostar2.sdk.volley.Response.Listener;

import java.util.HashMap;
import java.util.Map;

public class DeviceDataProvider extends BaseDataProvider {
	@SuppressWarnings("unused")
	private final String TAG = getClass().getSimpleName();
	private static DeviceDataProvider mSelf = null;

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

	public void getDevices(String tag, Listener<Devices> listener, ErrorListener errorListener, Object deliverParam) {
		sendRequest(tag, Devices.class, Method.GET, NetWork.PARAM_DEVICES, null, null, null, listener, errorListener, deliverParam);
	}
	

	public void getDevices(String tag, Listener<Devices> listener, ErrorListener errorListener, String query, Object deliverParam) {
		Map<String, String> params = null;
		if (query != null) {
			params = new HashMap<String, String>();
			params.put("text", query);
		}
		sendRequest(tag, Devices.class, Method.GET, NetWork.PARAM_DEVICES, null, params, null, listener, errorListener, deliverParam);
	}
	

	public void getDevic(String tag,String id, Listener<Device> listener, ErrorListener errorListener, Object deliverParam) {
		sendRequest(tag, Device.class, Method.GET, NetWork.PARAM_DEVICES+ "/" + id, null, null, null, listener, errorListener, deliverParam);
	}
	
	
	public void getDeviceTypes(String tag, Listener<DeviceTypes> listener, ErrorListener errorListener, Object deliverParam) {
		sendRequest(tag, DeviceTypes.class, Method.GET, NetWork.PARAM_DEVICE_TYPES, null, null, null, listener, errorListener, deliverParam);
	}


	public void scanFingerprint(String tag, String deviceid,int quality, boolean getImage, Listener<ScanFingerprintTemplate> listener, ErrorListener errorListener, Object deliverParam) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("device_id", String.valueOf(deviceid));
		JsonObject object = new JsonObject();
		object.addProperty(DeviceData.SCAN_FINGERPRINT_ENROLL_QUALITY, quality);
		object.addProperty(DeviceData.SCAN_FINGERPRINT_GET_IMAGE, getImage);
		String body = mGson.toJson(object);
		String url = NetWork.PARAM_DEVICES + "/" + String.valueOf(deviceid) + "/" + NetWork.PARAM_DEVICE_SCAN_FINGERPRINT;
		sendRequest(tag, ScanFingerprintTemplate.class, Method.POST, url, null, params, body, listener, errorListener, deliverParam);
	}

	public void writeCard(String tag, String deviceid, Listener<Card> listener, ErrorListener errorListener, Object deliverParam) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("device_id", String.valueOf(deviceid));
		String url = NetWork.PARAM_DEVICES + "/" + String.valueOf(deviceid) + "/" + NetWork.PARAM_DEVICE_WRITE_CARD;

		sendRequest(tag, Card.class, Method.POST, url, null, params, null, listener, errorListener, deliverParam);
	}

	public void scanCard(String tag, String deviceid, Listener<Card> scanListener, ErrorListener errorListener, Object deliverParam) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("device_id", String.valueOf(deviceid));
		String url = NetWork.PARAM_DEVICES + "/" + String.valueOf(deviceid) + "/" + NetWork.PARAM_DEVICE_SCAN_CARD;

		sendRequest(tag, Card.class, Method.POST, url, null, params, null, scanListener, errorListener, deliverParam);
	}

	public void getCards(String tag, Listener<Cards> listener, ErrorListener errorListener, int offset, int limit, int groupId, String query,Object deliverParam) {
		String url = NetWork.PARAM_CARDS_UNASSIGNED;
		Map<String, String> params = new HashMap<String, String>();
		params.put("group_id", String.valueOf(groupId));
		params.put("limit", String.valueOf(limit));
		params.put("offset", String.valueOf(offset));
		if (query != null) {
			params.put("text", query);
		}
		// params.put("total", "2");

		sendRequest(tag, Cards.class, Method.GET, url, null, params, null, listener, errorListener, deliverParam);
	}
	
	public void verifyFingerprint(String tag, String deviceid, int security_leve,ListFingerprintTemplate fingerprintTemplate,Listener<FingerprintVerify> listener, ErrorListener errorListener, Object deliverParam) {
		String url = NetWork.PARAM_DEVICES + "/" + String.valueOf(deviceid) + "/" + NetWork.PARAM_DEVICE_VERIFY_FINGERPRINT;
		VerifyFingerprintOption verifyFingerprintOption = new VerifyFingerprintOption("DEFAULT",fingerprintTemplate.template0,fingerprintTemplate.template1);
		String json = mGson.toJson(verifyFingerprintOption);
		sendRequest(tag, FingerprintVerify.class, Method.POST, url, null, null, json, listener, errorListener, deliverParam);
	}
}
