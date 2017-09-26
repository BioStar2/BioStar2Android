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
package com.supremainc.biostar2;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.supremainc.biostar2.sdk.provider.BaseDataProvider;

import okhttp3.OkHttpClient;


public class SampleApplication extends Application {
    private BaseDataProvider.SetStetho mSetStetho = new BaseDataProvider.SetStetho() {
        @Override
        public void setStetho(OkHttpClient.Builder builder) {
            if (builder != null) {
                builder.addNetworkInterceptor(new StethoInterceptor());
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
            BaseDataProvider.setStehto(mSetStetho);
        }

    }
}
