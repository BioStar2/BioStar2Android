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


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.supremainc.biostar2.main.HomeActivity;

public class DummyActivity extends Activity {
    private final String TAG = getClass().getSimpleName();
    private Activity mActivity;
    private boolean mIsRestart;
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsRestart) {
                Intent intent = new Intent(mActivity, LoginActivity.class);
                intent.setAction(Setting.ACTION_NOTIFICATION_START + String.valueOf(System.currentTimeMillis()));
                startActivity(intent);
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "DummyActivity end");
                }
            }
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dummy);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "DummyActivity start");
        }
        Handler handler = new Handler();
        mActivity = this;
        if (Setting.IS_NOTIFICATION_NONE_RESTART) {
            if (HomeActivity.isRunning()) {
                mIsRestart = false;
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Setting.BROADCAST_GOTO_ALARMLIST));
            } else {
                mIsRestart = true;
            }
        } else {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Setting.BROADCAST_ALL_CLEAR));
            mIsRestart = true;
        }
        handler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}