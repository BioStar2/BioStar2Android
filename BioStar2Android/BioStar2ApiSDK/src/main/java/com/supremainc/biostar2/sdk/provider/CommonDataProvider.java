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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.supremainc.biostar2.sdk.models.enumtype.LocalStorage;
import com.supremainc.biostar2.sdk.models.v1.permission.CloudPermission;
import com.supremainc.biostar2.sdk.models.v2.common.BioStarSetting;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.models.v2.common.UpdateData;
import com.supremainc.biostar2.sdk.models.v2.common.VersionData;
import com.supremainc.biostar2.sdk.models.v2.login.Login;
import com.supremainc.biostar2.sdk.models.v2.permission.PermissionItem;
import com.supremainc.biostar2.sdk.models.v2.preferrence.Preference;
import com.supremainc.biostar2.sdk.models.v2.user.User;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.supremainc.biostar2.sdk.models.v2.common.VersionData.getCloudVersionString;

public class CommonDataProvider extends BaseDataProvider{
    protected final String TAG = getClass().getSimpleName();
    private static CommonDataProvider mSelf = null;
    private static String mPasswordLevel;
    private static boolean mIsUseAlphaNumericUserID;
    private static boolean mIsSupportMobileCredential;

    private CommonDataProvider(Context context) {
        super(context);
    }

    public static CommonDataProvider getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new CommonDataProvider(context);
        }
        return mSelf;
    }

    private Call<VersionData> getServerVersion(final Callback<VersionData> callback, String domain,String subdomain) {
        if (!checkObject(subdomain,domain)) {
            onParamError(callback);
            return null;
        }
        createApiInterface(domain);
        Call<VersionData> call = mApiInterface.getServerVersion("v2",subdomain);

        Callback<VersionData> innerCallback = new Callback<VersionData>() {
            @Override
            public void onResponse(Call<VersionData> call, Response<VersionData> response) {
                VersionData versionData = response.body();
                if (response.isSuccessful() && versionData != null) {
                    if (!versionData.init(mContext)) {
                        onFailure(call,new Throwable("BioStar Server Version String Invalid"));
                        return;
                    }
                    if (callback != null) {
                        callback.onResponse(call,response);
                    }
                } else {
                    if (!call.isCanceled()) {
                        removeCookie();
                    }
                    ResponseBody body = response.errorBody();
                    if (body == null) {
                        onFailure(call,new Throwable("Request Fail:"+response.code()));
                    } else {
                        String error = "";
                        try {
                            ResponseStatus responseClass = (ResponseStatus) mGson.fromJson(body.string(), ResponseStatus.class);
                            error = responseClass.message;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //mGson.convert = 에러 변환 메세지 넣어서준다. 401이면 removeCookie
                        onFailure(call,new Throwable(error+"\n"+"code: "+response.code()));
                    }
                }
            }

            @Override
            public void onFailure(Call<VersionData> call, Throwable t) {
                //todo    callback.onFailure(new IOException("Canceled"));
                if (callback != null) {
                    callback.onFailure(call,t);
                }
            }
        };
        call.enqueue(innerCallback);
        return call;
    }

    public boolean login(final Callback<User> callback,String domain,final Login login) {
        if (login == null || login.isInvalid()) {
            onParamError(callback);
            return false;
        }
        if (mContext == null) {
            if (callback != null) {
                callback.onFailure(null, new Throwable(""));
            }
            return false;
        }
        if (mApiInterface == null) {
            init(mContext);
        }
        removeCookie();
        setLocalStorage(LocalStorage.DOMAIN,domain);
        setLocalStorage(LocalStorage.SUBDOMAIN,login.name);
        setLocalStorage(LocalStorage.USER_LOGIN_ID,login.user_id);
        Callback<VersionData> innerCallback = new Callback<VersionData>() {
            @Override
            public void onResponse(Call<VersionData> call, Response<VersionData> response) {
                Call<User> callLogin = mApiInterface.login(getCloudVersionString(mContext),login);
                Callback<User> innerLoginCallback = new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (call.isCanceled()) {
                            callback.onResponse(call,response);
                            return;
                        }
                        if (response.isSuccessful()) {
                            simpleLogin(callback);
                        } else {
                            removeCookie();
                            ResponseBody body = response.errorBody();
                            String error = "Login Failed";
                            if (body != null) {
                                try {
                                    ResponseStatus responseClass = (ResponseStatus) mGson.fromJson(body.string(), ResponseStatus.class);
                                    error = responseClass.message;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            onFailure(call,new Throwable(error+"\n"+"code: "+response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        if (callback != null) {
                            callback.onFailure(null,t);
                        }
                    }
                };
                callLogin.enqueue(innerLoginCallback);
            }

            @Override
            public void onFailure(Call<VersionData> call, Throwable t) {
                if (callback != null) {
                    callback.onFailure(null,t);
                }
            }
        };
        getServerVersion(innerCallback, domain, login.name);
        return true;
    }
//if(user.isCanceled() || "Canceled".equals(t.getMessage())) {
    public Call<User> simpleLogin(final Callback<User> callback) {
        if (!checkAPI(callback)) {
            return null;
        }
        Call<User> call = mApiInterface.simplelogin(getCloudVersionString(mContext));
        Callback<User> innerLoginCallback = new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (call.isCanceled()) {
                    callback.onResponse(call,response);
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    mSimpleLoginTick = SystemClock.elapsedRealtime();
                    mUserInfo = response.body();
                    if (VersionData.getCloudVersion(mContext) < 2) {
                        if (mUserInfo.password_strength_level != null) {
                            mPasswordLevel = mUserInfo.password_strength_level;
                        }
                    }
                    initPermission();
                    if (callback != null) {
                        callback.onResponse(call,response);
                    }
                } else {
                    if (!call.isCanceled()) {
                        removeCookie();
                    }
                    ResponseBody body = response.errorBody();
                    if (body == null) {
                        onFailure(call,new Throwable("\nhcode:"+response.code()));
                    } else {
                        String error = "";
                        try {
                            ResponseStatus responseClass = (ResponseStatus) mGson.fromJson(body.string(), ResponseStatus.class);
                            error = responseClass.message + "\n"+"scode: "+responseClass.status_code;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //mGson.convert = 에러 변환 메세지 넣어서준다. 401이면 removeCookie
                        onFailure(call,new Throwable(error+"\n"+"hcode: "+response.code()));
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (t != null && t.getMessage() != null) {
                    if (t.getMessage().contains("bs-cloud-session-id")) {
                        removeCookie();
                    }
                }
                if (callback != null) {
                    callback.onFailure(call,t);
                }
            }
        };
        call.enqueue(innerLoginCallback);
        return call;
    }

    private void initPermission() {
        if (mPermissionMap != null) {
            mPermissionMap.clear();
        }
        if (mUserInfo == null) {
            return;
        }
        // V2
        if (mUserInfo.permission != null && mUserInfo.permission.permissions != null) {
            for (PermissionItem item : mUserInfo.permission.permissions) {
                String key = item.module + PermissionItem.ROLE_WRITE;
                if (item.write) {
                    if (item.read == false) {
                        item.read = true;
                    }
                    mPermissionMap.put(key, true);
                }
                if (item.module != null) {
                    key = item.module + PermissionItem.ROLE_READ;
                    if (item.read) {
                        if (ConfigDataProvider.DEBUG) {
                            Log.i(TAG, key);
                        }
                        mPermissionMap.put(key, true);
                    }
                }
            }
            return;
        }

        if (mUserInfo.permissions == null || mUserInfo.permissions.size() < 1) {
            return;
        }
        // V1
        for (CloudPermission permission : mUserInfo.permissions) {
            if (permission.module != null) {
                String key = permission.module + PermissionItem.ROLE_WRITE;
                if (permission.write) {
                    if (permission.read == false) {
                        permission.read = true;
                    }
                    mPermissionMap.put(key, true);
                }
                key = permission.module + PermissionItem.ROLE_READ;
                if (permission.read) {
                    mPermissionMap.put(key, true);
                }
            }
        }
    }

    public void simpleLoginCheck() {
        long base = 43200000;
        if (SystemClock.elapsedRealtime() - mSimpleLoginTick > base){
            simpleLogin(null);
        }
    }

    public boolean isStrongPassword() {
        if (mPasswordLevel == null) {
            return true;
        }
        if (mPasswordLevel.equals("STRONG")) {
            return true;
        }
        return false;
    }

    public boolean isAlphaNumericUserID() {
        if (VersionData.getCloudVersion(mContext) > 1) {
            return mIsUseAlphaNumericUserID;
        } else {
            return false;
        }
    }
    public boolean isSupportMobileCredential() {
        if (VersionData.getCloudVersion(mContext) > 1) {
            return mIsSupportMobileCredential;
        } else {
            return false;
        }
    }


    public Call<ResponseStatus> logout(Callback<ResponseStatus> callback) {
        if (!checkAPI(callback)) {
            return null;
        }
        Call<ResponseStatus> call = mApiInterface.logout(getCloudVersionString(mContext));
        call.enqueue(callback);
        return call;
    }


    public Call<ResponseStatus>  setSetting(final Preference content,final Callback<ResponseStatus> callback) {
        if (!checkParamAndAPI(callback,mUserInfo,content)) {
            return null;
        }
        Callback<ResponseStatus> innerCallback = new Callback<ResponseStatus>() {
            @Override
            public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
                if (response.isSuccessful()) {
                    DateTimeDataProvider.getInstance(mContext).setDateTimeFormat(content.date_format, content.time_format);
                }
                if (callback != null) {
                    callback.onResponse(call,response);
                }
            }

            @Override
            public void onFailure(Call<ResponseStatus> call, Throwable t) {
                if (callback != null) {
                    callback.onFailure(call,t);
                }
            }
        };
        Call<ResponseStatus> call = mApiInterface.put_setting(getCloudVersionString(mContext),content);
        call.enqueue(innerCallback);
        return call;
    }

    public Call<Preference> getPreference(final Callback<Preference> callback) {
        if (!checkAPI(callback)) {
            return null;
        }
        Callback<Preference> innerCallback = new Callback<Preference>() {
            @Override
            public void onResponse(Call<Preference> call, Response<Preference> response) {
                if (response.isSuccessful()) {
                    DateTimeDataProvider.getInstance(mContext).setDateTimeFormat(response.body().date_format, response.body().time_format);
                }
                if (callback != null) {
                    callback.onResponse(call,response);
                }
            }

            @Override
            public void onFailure(Call<Preference> call, Throwable t) {
                if (callback != null) {
                    callback.onFailure(call,t);
                }
            }
        };
        Call<Preference> call = mApiInterface.get_setting(getCloudVersionString(mContext));
        call.enqueue(innerCallback);
        return call;
    }

    public Call<BioStarSetting> getBioStarSetting(final Callback<BioStarSetting> callback) {
        if (!checkAPI(callback)) {
            return null;
        }
        Callback<BioStarSetting> innerCallback = new Callback<BioStarSetting>() {
            @Override
            public void onResponse(Call<BioStarSetting> call, Response<BioStarSetting> response) {
                if (response.isSuccessful()) {
                    mPasswordLevel = response.body().password_strength_level;
                    mIsUseAlphaNumericUserID = response.body().use_alphanumeric_user_id;
                    mIsSupportMobileCredential = response.body().support_mobile_credential;
                }
                if (callback != null) {
                    callback.onResponse(call,response);
                }
            }

            @Override
            public void onFailure(Call<BioStarSetting> call, Throwable t) {
                if (callback != null) {
                    callback.onFailure(call,t);
                }
            }
        };
        Call<BioStarSetting> call = mApiInterface.get_setting_biostar_ac(getCloudVersionString(mContext));
        call.enqueue(innerCallback);
        return call;
    }

    public Call<UpdateData> getAppVersion(Callback<UpdateData> callback) {
        if (mContext == null) {
            if (callback != null) {
                callback.onFailure(null,new Throwable());
            }
            return null;
        }
        if (mApiInterface == null) {
            init(mContext);
        }
        try {
            PackageInfo i = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            String name = i.packageName;
            Map<String, String> params = new HashMap<String, String>();
            params.put("mobile_device_type", "ANDROID");
            Call<UpdateData> call = mApiInterface.get_app_versions(name,params);
            call.enqueue(callback);
            return call;
        } catch (PackageManager.NameNotFoundException e) {
            if (ConfigDataProvider.DEBUG) {
                e.printStackTrace();
            }
            if (callback != null) {
                callback.onFailure(null,new Throwable(e.getMessage()));
            }
        }
        return null;
    }

    private class HttpUtil extends AsyncTask<String, Void, Void> {
        private Callback<UpdateData> mCallback;
        private UpdateData mUpdateData;
        private String mError;
        public HttpUtil(Callback<UpdateData> callback)  {
            mCallback = callback;
        }
        @Override
        public Void doInBackground(String... params) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL("https://api.biostar2.com/v2/register/app_versions/"+params[0]+"?mobile_device_type=ANDROID");
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
//                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type","application/json");
                conn.connect();
                int retCode = conn.getResponseCode();

                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = br.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                br.close();
                String res = response.toString();
                mUpdateData = mGson.fromJson(res,UpdateData.class);

            } catch (Exception e) {
                mError = e.getMessage();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mCallback == null) {
                return;
            }
            if (mUpdateData != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onResponse(null,Response.success(mUpdateData));
                    }
                });
            } else {
                mCallback.onFailure(null,new Throwable(mError));
            }
        }
    }
}
