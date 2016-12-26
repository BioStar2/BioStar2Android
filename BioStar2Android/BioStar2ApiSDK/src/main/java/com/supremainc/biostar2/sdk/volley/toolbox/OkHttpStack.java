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

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class OkHttpStack extends HurlStack {
    private final OkHttpClient client;
    private OkUrlFactory factory;

    public OkHttpStack(OkHttpClient client, CookieManager cookieManager) {
        if (client == null) {
            throw new NullPointerException("Client must not be null.");
        }

        if (ConfigDataProvider.DEBUG && ConfigDataProvider.SSL_ALL_PASS) {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }};
            try {
                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("SSL");

                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

                // Create an ssl socket factory with our all-trusting manager
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                client.setSslSocketFactory(sslSocketFactory);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // client.setSslSocketFactory(fake);
            client.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }
        this.client = client;
        this.client.setCookieHandler(cookieManager);

//		CookieManager cookieManager = new CookieManager();
//		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//		this.client.setCookieHandler(cookieManager);

//		this.client.setCookieHandler(new CookieManager(
//                new PersistentCookieStore(context),
//                CookiePolicy.ACCEPT_ALL));
        factory = new OkUrlFactory(client);
    }

    public OkHttpStack(CookieManager cookieManager) {
        this(new OkHttpClient(), cookieManager);
    }

    public OkHttpClient getClient() {
        return client;
    }


    @SuppressWarnings("deprecation")
    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        return factory.open(url);
//		return client.open(url);
    }
}
