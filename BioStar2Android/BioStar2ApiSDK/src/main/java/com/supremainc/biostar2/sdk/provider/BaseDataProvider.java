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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.supremainc.biostar2.sdk.BuildConfig;
import com.supremainc.biostar2.sdk.models.enumtype.LocalStorage;
import com.supremainc.biostar2.sdk.models.v2.common.VersionData;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.sdk.utils.FileUtil;
import com.supremainc.biostar2.sdk.utils.PreferenceUtil;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.supremainc.biostar2.sdk.provider.ConfigDataProvider.LOGIN_EXPIRE;

public class BaseDataProvider {
    public static final int MAX_LIMIT = 100000;
    private static final int CONNECT_TIMEOUT = 15;
    private static final int WRITE_TIMEOUT = 60;
    private static final int READ_TIMEOUT = 60;
    protected static Handler mHandler;
    protected static Context mContext;
    protected static ApiInterface mApiInterface;
    protected static FileUtil mFileUtil;
    protected static Map<String, Boolean> mPermissionMap;
    protected static User mUserInfo;
    protected static long mSimpleLoginTick = 0L;
    protected static Gson mGson = new Gson();
    private static String SERVER_URL = null;// "https://apitest.biostar2.com/";
    private static OkHttpClient mClient;
    private static String mCauseErrorString = "function param invalid";
    private static Locale mLocale;
    private static PersistentCookieStore mCookie;
    private static SetStetho mStetho;
    protected final String TAG = getClass().getSimpleName();

    protected BaseDataProvider(Context context) {
        init(context);
    }

    public static void setStehto(SetStetho stetho) {
        mStetho = stetho;
    }


    public static Context getContext() {
        return mContext;
    }

    public void init(Context context) {
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
        if (mPermissionMap == null) {
            mPermissionMap = new HashMap<String, Boolean>();
        }
        if (mFileUtil == null) {
            mFileUtil = FileUtil.getInstance();
        }
        String url = (String) getLocalStorage(LocalStorage.DOMAIN);
        if (url == null || url.isEmpty() || HttpUrl.parse(url) == null) {
            removeCookie();
            createApiInterface("https://api.biostar2.com/");
        } else {
            createApiInterface(url);
        }
    }

    public void resetLocale() {
        mLocale = mContext.getResources().getConfiguration().locale;
    }

    private void createClient() {
        mLocale = mContext.getResources().getConfiguration().locale;
        if (mClient == null) {
            mCookie = new PersistentCookieStore(mContext);
            CookieManager cookieManager = new CookieManager(mCookie, CookiePolicy.ACCEPT_ALL);
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
            builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
            builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
            builder.cookieJar(new JavaNetCookieJar(cookieManager));
            builder.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();
                    if (mLocale != null) {
                          requestBuilder.header("Content-Language", mLocale.getISO3Language());
                    }
                    if (ConfigDataProvider.DEBUG) {
                        Calendar cal = Calendar.getInstance();
                        requestBuilder.header("timestamp", cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+" &"+cal.get(Calendar.MILLISECOND));
                    }
                    requestBuilder.method(original.method(), original.body());
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
            if (mStetho != null) {
                mStetho.setStetho(builder);
            }
            mClient = builder.build();
//                    .addNetworkInterceptor(new StethoInterceptor())

        }
    }

    public void cancelAll() {
        if (mClient != null) {
            mClient.dispatcher().cancelAll();
        }
    }

    protected String getServerUrl() {
        return SERVER_URL;
    }

    protected void createApiInterface(String url) {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        createClient();
        if (mApiInterface == null || !url.equals(SERVER_URL)) {
            SERVER_URL = url;
            mApiInterface = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .client(mClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ApiInterface.class);
        }
    }

    public void setLocalStorage(LocalStorage type, String content) {
        switch (type.type) {
            case STRING:
                PreferenceUtil.putSharedPreference(mContext, type.name, content);
                break;
            default:
                break;
        }
    }

    public Object getLocalStorage(LocalStorage type) {
        switch (type.type) {
            case STRING:
                String result = PreferenceUtil.getSharedPreference(mContext, type.name);
                if (LocalStorage.DOMAIN.name.equals(type.name)) {
                    if (result == null || result.isEmpty()) {
                        result = ConfigDataProvider.URL;
                    }
                }
                return result;
            default:
                break;
        }
        return null;
    }

    protected void onParamError(final Callback<?> callback) {
        if (callback != null && mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onFailure(null, new Throwable(mCauseErrorString));
                }
            });
        }
    }

    public boolean isLogined() {
        if (mContext == null || mApiInterface == null || mCookie == null) {
            return false;
        }
        return mCookie.isValid();
    }

    public boolean isExistLoginedUser() {
        if (mUserInfo == null) {
            return false;
        }
        return true;
    }

    public void removeCookie() {
        if (mCookie != null) {
            mCookie.removeAll();
        }
        mUserInfo = null;
    }

    protected boolean checkAPI(Callback<?> callback) {
        if (mContext == null) {
            if (callback != null) {
                callback.onFailure(null, new Throwable(""));
            }
            return false;
        }
        String url = VersionData.getCloudVersionString(mContext);
        if (url == null || url.isEmpty()) {
            removeCookie();
            if (callback != null) {
                callback.onFailure(null, new Throwable(LOGIN_EXPIRE));
            }
            return false;
        }
        if (mApiInterface == null) {
            createApiInterface(url);
        }
        return true;
    }

    public User getLoginUserInfo() {
        try {
            if (mUserInfo != null) {
                return (User) mUserInfo.clone();
            }
        } catch (Exception e) {

        }
        return null;
    }

    public void setLoginUserInfo(User user) {
        mUserInfo = user;
    }

    protected boolean checkObject(Object... args) {
        for (Object obj : args) {
            if (obj == null) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "checkObject:", new Throwable("stack dump"));
                }
                return false;
            }
            String target = obj.toString();
            if (target != null) {
                if (target.isEmpty()) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "checkObject:" , new Throwable("stack dump"));
                    }
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean checkParamAndAPI(Callback<?> callback, Object... args) {
        if (!checkObject(args)) {
            onParamError(callback);
            return false;
        }
        if (!checkAPI(callback)) {
            return false;
        }
        return true;
    }

    public interface SetStetho {
        public void setStetho(OkHttpClient.Builder builder);
    }

    public OkHttpClient getOkHttpClient() {
        if (mClient == null) {
            createClient();
        }
        return mClient;
    }
}
