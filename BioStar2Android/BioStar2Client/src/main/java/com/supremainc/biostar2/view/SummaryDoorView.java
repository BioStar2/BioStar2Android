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
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;

public class SummaryDoorView extends BaseView {
    public final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    private StyledTextView mActionButton;
    private StyledTextView mTitle;
    private StyledTextView mContent;
    private View mGoViewLog;
    private ImageView mIcon;

    private SummaryDoorViewListener mListener;

    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            if (mListener == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.door_control:
                    mListener.onDoorAction();
                    break;
                case R.id.go_view:
                    mListener.onGoLog();
                    break;
                case R.id.icon:
                    mListener.onStatus();
                    break;
            }
        }
    };

    public SummaryDoorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public SummaryDoorView(Context context) {
        super(context);
        initView(context);
    }

    public SummaryDoorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mInflater.inflate(R.layout.view_summary_door, this, true);
        mActionButton = (StyledTextView) findViewById(R.id.door_control);
        mTitle = (StyledTextView) findViewById(R.id.title_name);
        mContent = (StyledTextView) findViewById(R.id.title_content);
        mGoViewLog = findViewById(R.id.go_view);
        mIcon = (ImageView) findViewById(R.id.icon);

        mActionButton.setOnClickListener(mClickListener);
        mGoViewLog.setOnClickListener(mClickListener);
        mIcon.setOnClickListener(mClickListener);
    }

    public void init(SummaryDoorViewListener l) {
        mListener = l;
    }

    public void setActionButtonName(String name) {
        mActionButton.setText(name);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setContent(String message) {
        mContent.setText(message);
    }

    public void setIcon(int resID) {
        mIcon.setImageResource(resID);
    }

    public void showActionBtn(boolean isShow) {
        showActionBtn(isShow, isShow);
    }

    public void showActionBtn(boolean isShow, boolean isEnable) {
        if (isShow) {
            mActionButton.setVisibility(View.VISIBLE);
        } else {
            mActionButton.setVisibility(View.GONE);
        }

        if (isEnable) {
            mActionButton.setOnClickListener(mClickListener);
            mActionButton.setBackgroundResource(R.drawable.selector_btn_round_ok);
        } else {
            mActionButton.setOnClickListener(null);
            mActionButton.setBackgroundResource(R.drawable.selector_list_gray);
        }
    }

    public void showGoLogBtn(boolean isShow) {
        if (isShow) {
            mGoViewLog.setVisibility(View.VISIBLE);
        } else {
            mGoViewLog.setVisibility(View.GONE);
        }
    }

    public interface SummaryDoorViewListener {
        public void onDoorAction();

        public void onGoLog();

        public void onStatus();
    }
}
