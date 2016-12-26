/*
 * Copyright (C) 2011 The Android Open Source Project
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
// modified by Suprema 2015-09

package com.supremainc.biostar2.sdk.volley.toolbox;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;
import com.supremainc.biostar2.sdk.volley.NetworkResponse;
import com.supremainc.biostar2.sdk.volley.ParseError;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.ErrorListener;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyLog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * A request for retrieving a {@link JSONObject} response body at a given URL,
 * allowing for an optional {@link JSONObject} to be passed in as part of the
 * request body.
 */
public class JsonObjectRequest<T> extends JsonRequest<T> {
    /**
     * Charset for request.
     */
    private static final String PROTOCOL_CHARSET = "utf-8";
    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    private static Gson mGson = new Gson();
    private final Class<T> mClazz;

    /**
     * Creates a new request.
     *
     * @param method        the HTTP method to use
     * @param url           URL to fetch the JSON from
     * @param jsonRequest   A {@link JSONObject} to post with the request. Null is allowed
     *                      and indicates no parameters will be posted along with request.
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public JsonObjectRequest(Class<T> clazz, int method, String url, Map<String, String> headers, Map<String, String> params, String body, Listener<T> listener, ErrorListener errorListener,
                             Object deliverParam) {
        super(method, url, headers, params, (body == null) ? null : body, listener, errorListener, deliverParam);
        mClazz = clazz;
        if (null != body) {
            mRequestBody = body;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String jsonString = null;
        try {
            if (response.data != null) {
                jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                if (ConfigDataProvider.DEBUG) {
                    Log.d("Receive:", getUrl() + " " + jsonString);
                }

                if (ConfigDataProvider.DEBUG_SDCARD) {
                    // Log.d("raw reponse", jsonString);
                    // FileUtil.WriteLog("sucess","response sucess\n"+mUrl+"\n"+jsonString);
                }

                if (mClazz == JSONObject.class) {
                    JSONObject result = new JSONObject(jsonString);
                    return Response.success((T) result, HttpHeaderParser.parseCacheHeaders(response));
                } else if (mClazz == String.class) {
                    return Response.success((T) jsonString, HttpHeaderParser.parseCacheHeaders(response));
                } else {
                    T result = (T) mGson.fromJson(jsonString, mClazz);
                    return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
                }
            }
            return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            if (ConfigDataProvider.DEBUG_SDCARD) {
                if (e.getMessage() != null) {
                    writeLog("error", "response error\n" + e.getMessage());
                }
                if (jsonString != null) {
                    writeLog("errorbody", "response error body\n" + jsonString);
                }
            }
            if (ConfigDataProvider.DEBUG) {
                Log.e("Receive:" + getUrl(), " " + e.getMessage());
            }
            return Response.error(new ParseError(e));
        }
    }

    /**
     * @deprecated Use {@link #getBodyContentType()}.
     */
    @Override
    public String getPostBodyContentType() {
        return getBodyContentType();
    }

    /**
     * @deprecated Use {@link #getBody()}.
     */
    @Override
    public byte[] getPostBody() {
        if (ConfigDataProvider.DEBUG) {
            Log.d("JSOBJECT REQ", "body" + mRequestBody);
        }
        return getBody();
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }

    private void writeLog(String filename, String data) {
        if (ConfigDataProvider.DEBUG_SDCARD) {
            if (data == null) {
                return;
            }
            if (filename == null) {
                filename = "";
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/err");
            if (!file.isDirectory()) {
                file.mkdirs();
                file.setWritable(true);
                file.setReadable(true);
            }
            bufferToFile(Environment.getExternalStorageDirectory() + "/err/w_" + filename + System.currentTimeMillis() + ".txt", data.getBytes());
        }
    }

    private boolean bufferToFile(String path, byte[] buffer) {
        if (ConfigDataProvider.DEBUG_SDCARD) {
            if (null == path) {
                return false;
            }
            File newFile = new File(path);
            try {
                newFile.createNewFile();
                try {
                    FileOutputStream savedFile = new FileOutputStream(newFile);
                    savedFile.write(buffer);
                    savedFile.close();
                } catch (FileNotFoundException e) {
                    return false;
                }
            } catch (IOException e) {
                return false;
            }
            return true;
        }
        return false;
    }
}
