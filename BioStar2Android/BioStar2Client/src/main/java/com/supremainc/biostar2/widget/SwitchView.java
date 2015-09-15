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

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.supremainc.biostar2.R;


public class SwitchView extends LinearLayout {
    private GestureDetector mGestureDetector;
    private boolean mOn;
    private OnChangeListener mOnChangeListener;
    private ImageView mSwitchView;

    public SwitchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchView(Context context) {
        super(context);
    }

    public boolean getOn() {
        return mOn;
    }

    public void init(Activity a, OnChangeListener onChangeListener, boolean on) {
        mOnChangeListener = onChangeListener;
        mSwitchView = (ImageView) findViewById(R.id.switch_onoff);
        mOn = on;
        if (on) {
            mSwitchView.setImageResource(R.drawable.list_btn_on);
        } else {
            mSwitchView.setImageResource(R.drawable.list_btn_off);
        }
        mGestureDetector = new GestureDetector(a, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    boolean on = true;
                    if (velocityX < 0) {
                        on = false;
                    }
                    if (!setSwitch(on)) {
                        return true;
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
        mGestureDetector.setIsLongpressEnabled(false);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                boolean result = mGestureDetector.onTouchEvent(event);
                if (!result) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        setSwitch(!mOn);
                    }
                }
                return true;
            }
        });
    }

    public boolean setSwitch(boolean on) {
        if (mSwitchView == null) {
            return false;
        }
        if (mOn == on) {
            return false;
        }
        mOn = on;
        if (on) {
            mSwitchView.setImageResource(R.drawable.list_btn_on);
        } else {
            mSwitchView.setImageResource(R.drawable.list_btn_off);
        }
        if (mOnChangeListener != null) {
            mOnChangeListener.onChange(on);
        }
        return true;
    }

    public interface OnChangeListener {
        public void onChange(boolean on);
    }
}