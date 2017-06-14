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
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.sdk.provider.CommonDataProvider;
import com.supremainc.biostar2.util.TextInputFilter;
import com.supremainc.biostar2.view.StyledEditTextView;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.widget.CustomDialog;

public class PasswordPopup {
    private Activity mContext;
    // Object
    private CustomDialog mDialog;
    private OnPasswordResult mOnPasswordResult;
    private ToastPopup mToastPopup;
    // View
    private StyledEditTextView mPassword;
    private StyledEditTextView mPasswordConfirm;
    private Handler mHandler;
    private boolean mIsPin;
    private boolean mIsStrong;
    private TextInputFilter mTextInputFilter;

    public PasswordPopup(Activity activity) {
        this.mContext = activity;
    }

    public void dismiss() {
        if (mTextInputFilter != null) {
            if (mPassword != null) {
                mTextInputFilter.setFilter(mPassword, TextInputFilter.EDIT_TYPE.NONE);
            }
            if (mPasswordConfirm != null) {
                mTextInputFilter.setFilter(mPasswordConfirm, TextInputFilter.EDIT_TYPE.NONE);
            }
        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private void showGuide() {
        if (mIsStrong) {
            mToastPopup.show(mContext.getString(R.string.password_guide_strong), null);
        } else {
            mToastPopup.show(mContext.getString(R.string.password_guide), null);
        }
    }

    protected void onClickPositive() {
        if (TextUtils.isEmpty(mPassword.toString2()) || TextUtils.isEmpty(mPasswordConfirm.toString2())) {
            mToastPopup.show(mContext.getString(R.string.password_empty), null);
            return;
        }
        if (!mPassword.toString2().equals(mPasswordConfirm.toString2())) {
            mToastPopup.show(mContext.getString(R.string.password_invalid), null);
            return;
        }
        String pass = mPassword.toString2();
        if (mIsPin) {
            if (mPassword.toString2().length() < 4) {
                mToastPopup.show(mContext.getString(R.string.pincount), null);
                return;
            }
            if (!pass.matches("[0-9]+")) {
                mToastPopup.show(mContext.getString(R.string.only_number), null);
                return;
            }
        } else {
            if (pass.length() < 8 || !pass.matches("[a-zA-Z0-9\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\=\\+\\{\\}\\[\\]\\:\\;\\,\\.\\<\\>\\?\\/\\~\\`\\|\\\\]+")) {
                showGuide();
                return;
            }
            if (mIsStrong) {
                if (!pass.matches(".*[a-z]+.*")) {
                    showGuide();
                    return;
                }
                if (!pass.matches(".*[A-Z]+.*")) {
                    showGuide();
                    return;
                }
                if (!pass.matches(".*[0-9]+.*")) {
                    showGuide();
                    return;
                }
                if (!pass.matches(".*[\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\=\\+\\{\\}\\[\\]\\:\\;\\,\\.\\<\\>\\?\\/\\~\\`\\|\\\\]+.*")) {
                    showGuide();
                    return;
                }
            } else {
                if (!pass.matches(".*[0-9]+.*")) {
                    showGuide();
                    return;
                }
                if (!pass.matches(".*[a-zA-Z]+.*")) {
                    showGuide();
                    return;
                }
            }
        }
        if (mOnPasswordResult != null) {
            mOnPasswordResult.OnResult(mPasswordConfirm.toString2());
        }
        InputMethodManager mImm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
        mImm.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
        mDialog.dismiss();
    }


    public void show(boolean isPin, String title, OnPasswordResult listener) {
        if (mContext.isFinishing()) {
            return;
        }
        dismiss();
        mHandler = new Handler(Looper.getMainLooper());
        mIsPin = isPin;
        mOnPasswordResult = listener;
        mDialog = new CustomDialog(mContext);
        mDialog.setCancelable(false);
        mToastPopup = new ToastPopup(mContext);
        mToastPopup.setDuration(Toast.LENGTH_SHORT);
        if (mTextInputFilter == null) {
            mTextInputFilter = new TextInputFilter(null, mToastPopup);
        }

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.popup_password, null);
        mIsStrong = CommonDataProvider.getInstance(mContext).isStrongPassword();
        StyledTextView guide = (StyledTextView) layout.findViewById(R.id.guide_text);
        if (isPin) {
            guide.setVisibility(View.GONE);
            (layout.findViewById(R.id.guide_text_devider)).setVisibility(View.GONE);
        } else {
            guide.setVisibility(View.VISIBLE);
            if (mIsStrong) {
                guide.setText(mContext.getString(R.string.password_guide_strong));
            } else {
                guide.setText(mContext.getString(R.string.password_guide));
            }
            (layout.findViewById(R.id.guide_text_devider)).setVisibility(View.VISIBLE);
        }
        mPassword = (StyledEditTextView) layout.findViewById(R.id.password);
        mPasswordConfirm = (StyledEditTextView) layout.findViewById(R.id.password_confirm);

        mPasswordConfirm.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    onClickPositive();
                    return false;
                }
                return false;
            }
        });

        OnSingleClickListener mOnClickListener = new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                switch (v.getId()) {
                    case R.id.positive:
                        onClickPositive();
                        break;
                    case R.id.negative:
                        if (mOnPasswordResult != null) {
                            mOnPasswordResult.OnResult(null);
                        }
                        InputMethodManager mImm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
                        mImm.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
                        mDialog.dismiss();
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
        if (isPin) {
            mPassword.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            mTextInputFilter.setFilter(mPassword, TextInputFilter.EDIT_TYPE.PIN);
            mPasswordConfirm.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            mTextInputFilter.setFilter(mPasswordConfirm, TextInputFilter.EDIT_TYPE.PIN);
        } else {
            mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mTextInputFilter.setFilter(mPassword, TextInputFilter.EDIT_TYPE.PASSWORD);
            mPasswordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mTextInputFilter.setFilter(mPasswordConfirm, TextInputFilter.EDIT_TYPE.PASSWORD);
        }
        mDialog.setLayout(layout);
        if (mContext.isFinishing()) {
            return;
        }

        mDialog.show();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager mImm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
                mPassword.requestFocus();
                mImm.showSoftInput(mPassword, 0);
            }
        });
    }

    public interface OnPasswordResult {
        public void OnResult(String data);
    }
}
