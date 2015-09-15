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
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.widget.OnSingleClickListener;

public class GuideActivityLayout {
    private final String TAG = getClass().getSimpleName();
    private Activity mActivity;
    private Context mContext;
    private GuideActivityLayoutEvent mLayoutEvent;
    private ImageView mPrev;
    private ImageView mNext;
    private ImageView mClose;
    private ImageView mGuide;
    private GestureDetector mGestureDetector ;
    //
    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()) {
                case R.id.close:
                    mLayoutEvent.close();
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

    public GuideActivityLayout(Activity activity, GuideActivityLayoutEvent layoutEvent) {
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mLayoutEvent = layoutEvent;
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

    public void initView() {
        mActivity.setContentView(R.layout.activity_guide);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mGuide = (ImageView)mActivity.findViewById(R.id.guide);
        mGestureDetector = new GestureDetector(mActivity, new GestureDetector.SimpleOnGestureListener() {
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
        View layout = mActivity.findViewById(R.id.layout);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });
        mPrev = (ImageView)mActivity.findViewById(R.id.left);
        mPrev.setOnClickListener(mClickListener);
        mNext = (ImageView)mActivity.findViewById(R.id.right);
        mNext.setOnClickListener(mClickListener);
        mClose = (ImageView)mActivity.findViewById(R.id.close);
        mClose.setOnClickListener(mClickListener);
        changeScreen(true);
    }

    public void onDestroy() {
        mActivity = null;
        mContext = null;
    }

    public interface GuideActivityLayoutEvent {
        public void close();
    }
}