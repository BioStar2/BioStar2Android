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
package com.supremainc.biostar2.widget.popup;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.widget.CustomDialog;

public class SeekBarPopup {
    private Activity mContext;
    // Object
    private CustomDialog mDialog;
    private OnResult mOnResult;
    private ToastPopup mToastPopup;
    // View
    private Handler mHandler;
    private RangeBar mRangeBar;
    private StyledTextView mGuide;
    private OnCancelListener cancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface mDialog) {
        }
    };


    public SeekBarPopup(Activity activity) {
        this.mContext = activity;
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    protected void onClickPositive(String content) {
          if (mOnResult != null) {
              mOnResult.OnResult(content);
        }
        mDialog.dismiss();
    }


    public void show(String title, OnResult listener,int value) {
        if (mContext.isFinishing()) {
            return;
        }
        dismiss();
        mHandler = new Handler(Looper.getMainLooper());
        mOnResult = listener;
        mDialog = new CustomDialog(mContext);
        mDialog.setCancelable(false);
        mToastPopup = new ToastPopup(mContext);
        mToastPopup.setDuration(Toast.LENGTH_SHORT);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.popup_seekbar, null);
        mGuide = (StyledTextView) layout.findViewById(R.id.rangebar_guide);
        mGuide.setText(String.valueOf(value));
        mRangeBar = (RangeBar) layout.findViewById(R.id.rangebar);
        mRangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                mGuide.setText(rightPinValue);
            }
        });
        mRangeBar.setSeekPinByValue(value);


//        mRangeBar.setFormatter(new IRangeBarFormatter() {
//            @Override
//            public String format(String s) {
//                // Transform the String s here then return s
//                return null;
//            }
//        });
        OnSingleClickListener mOnClickListener = new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                switch (v.getId()) {
                    case R.id.positive:
                        onClickPositive(mGuide.toString2());
                        break;
                    case R.id.negative:
                        onClickPositive(null);
                        break;
                }
            }
        };
        StyledTextView positiveView = (StyledTextView) layout.findViewById(R.id.positive);
        positiveView.setOnClickListener(mOnClickListener);
        StyledTextView negativeView = (StyledTextView) layout.findViewById(R.id.negative);
        negativeView.setOnClickListener(mOnClickListener);
        StyledTextView titleView = (StyledTextView) layout.findViewById(R.id.title_text);
        if (title != null) {
            titleView.setText(title);
        }


        mDialog.setLayout(layout);
        if (mContext.isFinishing()) {
            return;
        }

        mDialog.show();
    }

    public interface OnResult {
        public void OnResult(String data);
    }
}
