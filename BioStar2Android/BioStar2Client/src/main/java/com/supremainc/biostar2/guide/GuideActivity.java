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
package com.supremainc.biostar2.guide;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.Setting;


public class GuideActivity extends Activity {

    private final String TAG = getClass().getSimpleName();
    private GuideActivity mActivity;
    private Context mContext;
    private GuideActivityLayout mLayout;
    private BroadcastReceiver mClearReceiver;

    private void initValue() {
        mActivity = this;
        mContext = mActivity.getApplicationContext();
    }
    private GuideActivityLayout.GuideActivityLayoutEvent mLayoutEvent = new GuideActivityLayout.GuideActivityLayoutEvent() {
        @Override
        public void close() {
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        mLayout = new GuideActivityLayout(this, mLayoutEvent);
        mLayout.initView();
        initValue();
        registerBroadcast();
    }

    @Override
    public void onDestroy() {
        if (mClearReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mClearReceiver);
            mClearReceiver = null;
        }
        if (mLayout != null) {
            mLayout.onDestroy();
            mLayout = null;
        }
        super.onDestroy();
    }

    private void registerBroadcast() {
        if (mClearReceiver == null) {
            mClearReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (isFinishing()) {
                        return;
                    }
                    final String action = intent.getAction();
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "receive:" + action);
                    }
                    if (action.equals(Setting.BROADCAST_ALL_CLEAR)) {
                        finish();
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_ALL_CLEAR);
            LocalBroadcastManager.getInstance(this).registerReceiver(mClearReceiver, intentFilter);
        }
    }
}