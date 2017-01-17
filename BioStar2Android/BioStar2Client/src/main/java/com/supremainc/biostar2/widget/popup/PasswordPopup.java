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
import com.supremainc.biostar2.sdk.provider.UserDataProvider;
import com.supremainc.biostar2.util.TextWatcherFilter;
import com.supremainc.biostar2.util.TextWatcherFilter.EDIT_TYPE;
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
    private TextWatcherFilter mPasswordTextWatcherFilterNumber;
    private TextWatcherFilter mPasswordConfirmTextWatcherFilterNumber;
    private TextWatcherFilter mPasswordTextWatcherFilter;
    private TextWatcherFilter mPasswordConfirmTextWatcherFilter;
    private OnCancelListener cancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface mDialog) {
        }
    };


    public PasswordPopup(Activity activity) {
        this.mContext = activity;
    }

    public void dismiss() {
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
            if (pass.length() < 8 ||  !pass.matches("[a-zA-Z0-9\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\=\\+\\{\\}\\[\\]\\:\\;\\,\\.\\<\\>\\?\\/\\~\\`\\|\\\\]+")) {
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
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.popup_password, null);
        mIsStrong = UserDataProvider.getInstance(mContext).isStrongPassword();
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
//        mPasswordConfirm.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String original = mPassword.toString2();
//                String confirm = mPasswordConfirm.toString2();
//                if (mIsPin && mPassword.toString2().length() < 4) {
//                    mToastPopup.show(mContext.getString(R.string.pincount), null);
//                    return;
//                } else if (!mIsPin) {
//                    String pass = mPassword.toString2();
//                    if (pass.length() < 8 || !pass.matches("[a-zA-Z0-9\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\=\\+\\{\\}\\[\\]\\:\\;\\,\\.\\<\\>\\?\\/\\~\\`\\|]+")) {
//                        mToastPopup.show(mContext.getString(R.string.password_guide), null);
//                        return;
//                    }
//                }
//                if (confirm.length() < 1) {
//                    return;
//                }
//                int end = 0;
//                if (confirm.length() > original.length()) {
//                    end = original.length();
//                } else {
//                    end = confirm.length();
//                }
//                String originalModify = original.substring(0, end);
//                if (!originalModify.equals(confirm)) {
//                    mToastPopup.show(mContext.getString(R.string.password_invalid), null);
//                }
//            }
//        });

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
        if (mPasswordTextWatcherFilterNumber == null) {
            mPasswordTextWatcherFilterNumber = new TextWatcherFilter(mPassword, EDIT_TYPE.PIN, mContext, 16);
        }
        if (mPasswordConfirmTextWatcherFilterNumber == null) {
            mPasswordConfirmTextWatcherFilterNumber = new TextWatcherFilter(mPasswordConfirm, EDIT_TYPE.PIN, mContext, 16);
        }
        if (mPasswordTextWatcherFilter == null) {
            mPasswordTextWatcherFilter = new TextWatcherFilter(mPassword, EDIT_TYPE.PASSWORD, mContext, 32);
        }
        if (mPasswordConfirmTextWatcherFilter == null) {
            mPasswordConfirmTextWatcherFilter = new TextWatcherFilter(mPasswordConfirm, EDIT_TYPE.PASSWORD, mContext, 32);
        }
        if (isPin) {
            mPassword.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            mPassword.removeTextChangedListener(mPasswordTextWatcherFilter);
            mPassword.removeTextChangedListener(mPasswordTextWatcherFilterNumber);
            mPassword.addTextChangedListener(mPasswordTextWatcherFilterNumber);
            mPasswordConfirm.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            mPasswordConfirm.removeTextChangedListener(mPasswordConfirmTextWatcherFilter);
            mPasswordConfirm.removeTextChangedListener(mPasswordConfirmTextWatcherFilterNumber);
            mPasswordConfirm.addTextChangedListener(mPasswordConfirmTextWatcherFilterNumber);
//			mPassword.setFilters(new InputFilter[]{UtilProvider.getInstance().mFiltePin, new InputFilter.LengthFilter(16)});
//			mPasswordConfirm.setFilters(new InputFilter[]{UtilProvider.getInstance().mFiltePin, new InputFilter.LengthFilter(16)});
        } else {
            mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mPassword.removeTextChangedListener(mPasswordTextWatcherFilter);
            mPassword.removeTextChangedListener(mPasswordTextWatcherFilterNumber);
            mPassword.addTextChangedListener(mPasswordTextWatcherFilter);
            mPasswordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mPasswordConfirm.removeTextChangedListener(mPasswordConfirmTextWatcherFilter);
            mPasswordConfirm.removeTextChangedListener(mPasswordConfirmTextWatcherFilterNumber);
            mPasswordConfirm.addTextChangedListener(mPasswordConfirmTextWatcherFilter);
//			mPassword.setFilters(new InputFilter[]{UtilProvider.getInstance().mFilterPassword, new InputFilter.LengthFilter(32)});
//			mPasswordConfirm.setFilters(new InputFilter[]{UtilProvider.getInstance().mFilterPassword, new InputFilter.LengthFilter(32)});
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
