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
package com.supremainc.biostar2.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.util.FileUtil;

import java.io.File;

public class Html5WebView extends WebView {
    private final String TAG = getClass().getSimpleName();
    private LinearLayout mRetryView = null;
    private Toast mToast;
    private WebView mView;
    protected ProgressDialog mProgressDialog = null;
    private LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER | Gravity.TOP);
    private Context mContext;
    private String mFailUrl = "";
    private IWebViewListener mIWebViewListener = null;

    protected OnSingleClickListener mRefreshClickListner = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            mView.removeAllViews();
            loadUrl(mFailUrl);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog.show();
            }
        }
    };
    private MyWebChromeClient mWebClient;

    public Html5WebView(Context context) {
        super(context);
        init(context);
    }

    public Html5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Html5WebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @SuppressWarnings("deprecation")
    public void Clear() {
        clearAnimation();
        clearDisappearingChildren();
        clearMatches();
        clearFocus();
        clearView();
        clearCache(true);
        clearHistory();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    public void WebViewSetting(boolean transparent) {
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setUseWideViewPort(true);
        getSettings().setSupportZoom(true);
        getSettings().setBuiltInZoomControls(false);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        setInitialScale(1);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        setScrollbarFadingEnabled(true);
        setHorizontalScrollbarOverlay(true);
        setVerticalScrollbarOverlay(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        getSettings().setRenderPriority(RenderPriority.HIGH);
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        getSettings().setAppCacheEnabled(false);
        getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        getSettings().setDomStorageEnabled(true);
        if (transparent)
            setBackgroundColor(0);
    }

    @SuppressLint("ShowToast")
    private void init(Context context) {
        mWebClient = new MyWebChromeClient();
        mContext = context;
        mView = this;
        setWebChromeClient(mWebClient);
        setWebViewClient(new MyWebViewClient());
        mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        mToast.setText(mContext.getResources().getString(R.string.notice_connection_fail));
        WebViewSetting(false);
    }

    public void loadCamera() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "supremacamera.htm");
        if (!file.exists()) {
            String camera = FileUtil.getAssetsString("camera.htm", mContext);
            FileUtil.BufferToFile(file, camera.getBytes());
            loadUrl("file:///android_asset/camera.htm");
        } else {
            //String url =FileUtil.getFileContent(mContext.getFilesDir().toString()+"/"+"supremacamera.txt", mContext);
            loadUrl("file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "supremacamera.htm");
        }
    }

    public void loadDataDefault(String data) {
        if (data.startsWith("file://")) {
            loadUrl(data);
        } else {
            super.loadDataWithBaseURL("a", data, "text/html", "UTF-8", null);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setActivity(Activity activity) {
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setMessage(activity.getString(R.string.wait));
    }

    public void setIWebViewListener(IWebViewListener i) {
        mIWebViewListener = i;
    }

    public interface IWebViewListener {
        public void onAppEvent(String type, Object Data);
        public void onError();
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (BuildConfig.DEBUG) {
                Log.i("UrlLoading", url);
            }
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog.show();
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if (errorCode == ERROR_UNSUPPORTED_SCHEME || errorCode == ERROR_UNSUPPORTED_AUTH_SCHEME)
                return;
            mToast.show();
            if (null != mIWebViewListener) {
                mIWebViewListener.onError();
            }

            switch (errorCode) {
                case ERROR_AUTHENTICATION:
                    break;
                case ERROR_BAD_URL:
                    break;
                case ERROR_CONNECT:
                    break;
                case ERROR_FAILED_SSL_HANDSHAKE:
                    break;
                case ERROR_FILE:
                    break;
                case ERROR_FILE_NOT_FOUND:
                    break;
                case ERROR_HOST_LOOKUP:
                    break;
                case ERROR_IO:
                    break;
                case ERROR_PROXY_AUTHENTICATION:
                    break;
                case ERROR_REDIRECT_LOOP:
                    break;
                case ERROR_TIMEOUT:
                    break;
                case ERROR_TOO_MANY_REQUESTS:
                    break;
                case ERROR_UNKNOWN:
                    break;
                case ERROR_UNSUPPORTED_AUTH_SCHEME:
                    break;
                case ERROR_UNSUPPORTED_SCHEME:
                    break; 
            }
            mFailUrl = failingUrl;
            loadUrl("file:///android_asset/error.htm");
            if (BuildConfig.DEBUG)
                Log.i(TAG, " onReceivedError:" + mFailUrl + " errorCode:" + errorCode);
            if (null == mRetryView) {
                LayoutInflater Inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mRetryView = (LinearLayout) Inflater.inflate(R.layout.view_refresh, mView, false);
                mRetryView.findViewById(R.id.view_refresh).setOnClickListener(mRefreshClickListner);
            }
            if (null == mView.findViewById(R.id.view_refresh)) {
                mView.addView(mRetryView, mParams);
            }
        }
    }
}