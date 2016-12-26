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
package com.supremainc.biostar2.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.impl.OnSingleClickListener;


public class GuideActivity extends Activity {

    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private BroadcastReceiver mClearReceiver;
    private ImageView mPrev;
    private ImageView mNext;
    private ImageView mClose;
    private ImageView mGuide;
    private GestureDetector mGestureDetector;
    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()) {
                case R.id.close:
                    finish();
                    break;
                case R.id.left:
                    changeScreen(true);
                    break;
                case R.id.right:
                    changeScreen(false);
                    break;
            }
        }
    };

    private void initValue() {
        mGuide = (ImageView) findViewById(R.id.guide);
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    if (velocityX < 0) {
                        changeScreen(false);
                    } else {
                        changeScreen(true);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });
        View layout = findViewById(R.id.layout);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });
        mPrev = (ImageView) findViewById(R.id.left);
        mPrev.setOnClickListener(mClickListener);
        mNext = (ImageView) findViewById(R.id.right);
        mNext.setOnClickListener(mClickListener);
        mClose = (ImageView) findViewById(R.id.close);
        mClose.setOnClickListener(mClickListener);
        changeScreen(true);
        mContext = getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initValue();
        registerBroadcast();
    }

    @Override
    public void onDestroy() {
        if (mClearReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mClearReceiver);
            mClearReceiver = null;
        }
        super.onDestroy();
    }

    private void changeScreen(boolean isFirstPage) {
        if (isFirstPage) {
            mPrev.setVisibility(View.GONE);
            mNext.setVisibility(View.VISIBLE);
            mClose.setVisibility(View.GONE);
            mGuide.setImageResource(R.drawable.guide1);
        } else {
            mPrev.setVisibility(View.VISIBLE);
            mNext.setVisibility(View.GONE);
            mClose.setVisibility(View.VISIBLE);
            mGuide.setImageResource(R.drawable.guide2);
        }
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