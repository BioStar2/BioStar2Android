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
package com.supremainc.biostar2.popup;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.widget.CustomDialog;
import com.supremainc.biostar2.widget.OnSingleClickListener;
import com.supremainc.biostar2.widget.StyledTextView;

public class Popup {
    private Activity mContext;
    private CustomDialog mWaitPopup = null;
    private CustomDialog mDialog;
    private OnCancelListener cancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface mDialog) {
        }
    };

    public Popup(Activity mContext) {
        this.mContext = mContext;
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        dismissWiat();
    }

    public boolean dismissWiat() {
        if (mWaitPopup == null) {
            return true;
        }
        if (mContext.isFinishing()) {
            return false;
        }
        if (mWaitPopup.isShowing()) {
            try {
                mWaitPopup.dismiss();
            } catch (IllegalArgumentException e) {
            }
        }
        return false;
    }

    public int dpToPx(double dp) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public boolean isShownWait() {
        if (mWaitPopup == null) {
            return false;
        }
        if (mContext.isFinishing()) {
            return false;
        }
        if (mWaitPopup.isShowing()) {
            return true;
        }
        return false;
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public void show(PopupType type, String content, final OnPopupClickListener listener, String positive, String negative) {
        show(type, null, content, listener, positive, negative, true);
    }

    public void show(PopupType type, String title, String content, final OnPopupClickListener listener, String positive, String negative) {
        show(type, title, content, listener, positive, negative, true);
    }

    public void show(PopupType type, String title, String content, final OnPopupClickListener listener, String positive, String negative, boolean cancelable) {
        if (mContext.isFinishing()) {
            return;
        }
        dismiss();
        mDialog = new CustomDialog(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup layout = null;

        switch (type) {
            case CARD_CONFIRM:
                layout = (ViewGroup) inflater.inflate(R.layout.popup_card, null);
                break;
            default:
                layout = (ViewGroup) inflater.inflate(R.layout.popup_common, null);
                break;
        }

        final StyledTextView contentView = (StyledTextView) layout.findViewById(R.id.content);
        final OnSingleClickListener onClickListener = new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                switch (v.getId()) {
                    case R.id.positive:
                        if (listener != null) {
                            listener.OnPositive();
                        }
                        break;
                    case R.id.negative:
                        if (listener != null) {
                            listener.OnNegative();
                        }
                        break;
                }
            }
        };
        StyledTextView positiveView = (StyledTextView) layout.findViewById(R.id.positive);
        positiveView.setOnClickListener(onClickListener);
        StyledTextView negativeView = (StyledTextView) layout.findViewById(R.id.negative);
        negativeView.setOnClickListener(onClickListener);
        StyledTextView titleView = (StyledTextView) layout.findViewById(R.id.title_text);
        if (title != null) {
            titleView.setText(title);
        }

        ImageView popupType = (ImageView) layout.findViewById(R.id.type);
        boolean isRunHeight = true;
        switch (type) {
            case CONFIRM:
                popupType.setImageResource(R.drawable.popup_check_ic);
                break;
            case ALARM:
                popupType.setImageResource(R.drawable.popup_sound_ic);
                break;
            case ALERT:
                popupType.setImageResource(R.drawable.popup_error_ic);
                break;
            case INFO:
                popupType.setImageResource(R.drawable.popup_info_ic);
                break;
            case DOOR:
                popupType.setImageResource(R.drawable.popup_door_ic);
                break;
            case FIRE:
                popupType.setImageResource(R.drawable.popup_fire_ic);
                break;
            case CARD:
                popupType.setImageResource(R.drawable.user_card_number_ic);
                break;
            case FINGERPRINT:
                popupType.setImageResource(R.drawable.user_fp1);
                break;
            case FINGERPRINT_AGAGIN:
                popupType.setImageResource(R.drawable.user_fp2);
                break;
            case FINGERPRINT_CONFIRM:
                popupType.setImageResource(R.drawable.user_fp3);
                break;
            case CARD_CONFIRM:
                isRunHeight = false;
                break;
        }
        if (content == null) {
            content = "";
        }
        contentView.setText(content);
        if ((positive == null && negative == null) && cancelable) {
            positive = mContext.getResources().getString(R.string.ok);
        }

        LinearLayout devider = (LinearLayout) layout.findViewById(R.id.devider);
        if (positive == null || negative == null) {
            devider.setVisibility(View.GONE);
        } else {
            devider.setVisibility(View.VISIBLE);
        }

        positiveView.setText(positive);
        negativeView.setText(negative);
        if (positive == null) {
            positiveView.setVisibility(View.GONE);
        }
        if (negative == null) {
            negativeView.setVisibility(View.GONE);
        }

        Log.e("popup", "line:" + contentView.getLineCount());
        final ScrollView contentContainer = (ScrollView) layout.findViewById(R.id.scroll);

        final LinearLayout mainView = (LinearLayout) layout.findViewById(R.id.main_container);
        mDialog.setLayout(layout);
        if (isRunHeight) {
            mainView.setVisibility(View.INVISIBLE);
            contentView.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("popup", "line:" + contentView.getLineCount());
                    int count = contentView.getLineCount();
                    if (count > 4 || count < 0) {
                        count = 4;
                    }

                    contentContainer.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, dpToPx(157 + count * 18)));
                    mainView.setVisibility(View.VISIBLE);
                }
            });
        }

        if (mContext.isFinishing()) {
            return;
        }

        mDialog.show();
    }

    public void showWait(OnCancelListener cancelListener) {
        if (mContext.isFinishing()) {
            return;
        }
        if (dismissWiat()) {
            mWaitPopup = new CustomDialog(mContext);
            LayoutInflater inflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.popup_wait, null);
            mWaitPopup.setLayout(layout);
        }
        if (cancelListener != null) {
            mWaitPopup.setCancelable(true);
            mWaitPopup.setOnCancelListener(cancelListener);
        } else {
            mWaitPopup.setCancelable(false);
            mWaitPopup.setOnCancelListener(null);
        }
        mWaitPopup.findViewById(R.id.container).setVisibility(View.VISIBLE);
        mWaitPopup.show();
    }

    public void showWait(boolean cancel) {
        if (cancel) {
            showWait(cancelListener);
        } else {
            showWait(null);
        }
    }

    public enum PopupType {
        CONFIRM, ALARM, ALERT, INFO, DOOR, FIRE, CARD, CARD_CONFIRM, FINGERPRINT, FINGERPRINT_AGAGIN, FINGERPRINT_CONFIRM
    }

    public interface OnPopupClickListener {
        public void OnNegative();

        public void OnPositive();
    }

}
