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

import android.util.Log;

import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;
import com.supremainc.biostar2.sdk.volley.NetworkResponse;
import com.supremainc.biostar2.sdk.volley.Request;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.ErrorListener;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * A request for retrieving a T type response body at a given URL that also
 * optionally sends along a JSON body in the request specified.
 *
 * @param <T> JSON type of response expected
 */
public abstract class JsonRequest<T> extends Request<T> {
    private static final String TAG = "JsonRequest";
    /**
     * Charset for request.
     */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /**
     * Content type for request.
     */
    public static final String PROTOCOL_CONTENT_TYPE = String.format(
            "application/json; charset=%s", PROTOCOL_CHARSET);

    private final Listener<T> mListener;
    protected String session = "";
    private SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss.SSS",
            Locale.ENGLISH);
    private Date date;


    public JsonRequest(int method, String url, Map<String, String> headers,
                       Map<String, String> params, String requestBody,
                       Listener<T> listener, ErrorListener errorListener, Object deliverParam) {
        super(method, url, errorListener, deliverParam);
        setHeader(headers);
        setparam(params);
        mListener = listener;
        date = new Date();

        if (ConfigDataProvider.DEBUG) {
            logRequestinfo(null, null, false);
            Log.i(TAG, "requestBody: " + requestBody);
        }
    }

    @Override
    protected void deliverResponse(T response) {
        if (ConfigDataProvider.DEBUG) {
            logRequestinfo(response, null, true);
        }
        if (mListener != null) {
            mListener.onResponse(response, mDeliverParam);
        }
        mDeliverParam = null;

    }

    /**
     * Delivers error message to the ErrorListener that the Request was
     * initialized with.
     *
     * @param error Error details
     */
    @Override
    public void deliverError(VolleyError error) {
        if (ConfigDataProvider.DEBUG) {
            if (error == null) {
                error = new VolleyError("null error");
            }
            logRequestinfo(null, error, true);
        }
        if (mErrorListener != null) {
            mErrorListener.onErrorResponse(error, mDeliverParam);
        }
        mDeliverParam = null;
    }

    @Override
    abstract protected Response<T> parseNetworkResponse(NetworkResponse response);

    private void innerlogRequestinfo(String tag, String content, VolleyError error) {
        if (ConfigDataProvider.DEBUG) {
            if (error != null) {
                Log.e(tag, content);
            } else {
                Log.i(tag, content);
            }
        }
    }

    private void logRequestinfo(T reponse, VolleyError error, boolean isResponse) {
        if (ConfigDataProvider.DEBUG) {
            String isType = " Send: ";
            if (isResponse) {
                isType = " Receive: ";
            }
            if (error != null) {
                isType = isType + "fail";
            } else {
                isType = isType + "success";
            }
            String content = "";

            switch (mMethod) {
                case Method.GET:
                    content = "GET: " + getUrl();
                    break;
                case Method.PUT:
                    content = "PUT: " + getUrl();
                    break;
                case Method.DELETE:
                    content = "DELETE: " + getUrl();
                    break;
                case Method.POST:
                    content = "POST: " + getUrl();
                    break;
                default:
                    break;
            }
            String tag = TAG + isType + formatter.format(date);

            innerlogRequestinfo(tag, content, error);

            if (error != null) {
                Log.e(tag, "request body:" + mRequestBody);
                Log.e(tag, "has error reponse body:" + error.responseString);
            }
        }
    }
}
