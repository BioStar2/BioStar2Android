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
package com.supremainc.biostar2.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.util.Utils;

public class RingTimeView extends BaseView {
    public final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    private StyledTextView mMarker;
    private StyledTextView mDay;
    private StyledTextView mTime;
    private View mLogo;
    private View mContainer;


    public RingTimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public RingTimeView(Context context) {
        super(context);
        initView(context);
    }

    public RingTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mInflater.inflate(R.layout.view_ring_time, this, true);
        mMarker = (StyledTextView) findViewById(R.id.display_marker);
        mDay = (StyledTextView) findViewById(R.id.display_day);
        mTime = (StyledTextView) findViewById(R.id.display_time);
        mLogo = findViewById(R.id.display_logo);
        mContainer = findViewById(R.id.display_container);
    }

    public void setDateTime(String meridiem, String date, String time) {
        if (!TextUtils.isEmpty(meridiem)) {
            mMarker.setText(meridiem);
        }
        if (!TextUtils.isEmpty(date)) {
            mDay.setText(date);
        }
        if (!TextUtils.isEmpty(time)) {
            mTime.setText(time);
        }
    }

    public void setAdjustHeight(float parentDp) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, " dp h:" + parentDp);
        }

        mLogo.setVisibility(View.VISIBLE);
        if (parentDp > 220) {
            return;
        } else if (parentDp > 200) {
            setModify(Utils.convertDpToPixel(180, mContext), mLogo);
            setModify(Utils.convertDpToPixel(180, mContext), mContainer);
        } else if (parentDp > 100) {
            mLogo.setVisibility(View.GONE);
        } else {
            setVisibility(View.GONE);
        }
    }

    public void setModify(int height, View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params.width > -1) {
            params.width = height;
        }
        if (params.height > -1) {
            params.height = height;
        }
        view.setLayoutParams(params);
    }

}
