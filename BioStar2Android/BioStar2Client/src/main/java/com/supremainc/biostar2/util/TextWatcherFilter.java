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
package com.supremainc.biostar2.util;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.popup.ToastPopup;

public class TextWatcherFilter implements TextWatcher {
    private final String TAG = getClass().getSimpleName();
    private String mBefore;
    private int mBeforeIndex;
    private EditText mEditText;
    private int mMaxSize;
    private ToastPopup mToastPopup;
    private EDIT_TYPE mType;
    private boolean mIsLock;

    public TextWatcherFilter(EditText editText, EDIT_TYPE type, Activity activity, int maxSize) {
        mEditText = editText;
        mType = type;
        mToastPopup = new ToastPopup(activity);
        mMaxSize = maxSize;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (mIsLock) {
            mIsLock = false;
            return;
        }
        mBefore = s.toString();
        mBeforeIndex = mEditText.getSelectionStart();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s == null || s.length() < 1 || mType == null) {
            return;
        }
        String source = s.toString();
        if (source.length() > mMaxSize) {
            restore();
            return;
        }
        switch (mType) {
            case LOGIN_ID:
            case PASSWORD:
                filterLoginID(source, start, count);
                break;
            case EMAIL:
                filterEmail(source, start, count);
                break;
            case TELEPHONE:
                filterTelephone(source, start, count);
                break;
            case USER_ID:
                filterUserID(source, start, count);
                break;
            case PIN:
                filterPIN(source, start, count);
                break;
            case USER_NAME:
                break;
            default:
                break;
        }
    }

    private void restore() {
        mIsLock = true;
        String old = mBefore;
        int oldIndex = mBeforeIndex;
        mEditText.setText(old);
        if (old.length() > oldIndex) {
            mEditText.setSelection(oldIndex);
        } else {
            mEditText.setSelection(old.length());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void filterEmail(String source, int start, int count) {
        boolean isWhiteSpace = false;
        if (source.contains(" ")) {
            source = source.replace(" ","");
            isWhiteSpace = true;
        }
        if (!source.matches("[a-zA-Z0-9\\@\\.]+")) {
            restore();
            mToastPopup.show(R.string.invalid_email, -1);
            return;
        }
        if (source.contains("..")) {
            restore();
            mToastPopup.show(R.string.invalid_email, -1);
            return;
        }
        int find = source.indexOf("@", 0);
        if (find > -1) {
            if (source.indexOf("@", find + 1) > -1) {
                restore();
                mToastPopup.show(R.string.invalid_email, -1);
                return;
            }
        }
        if (isWhiteSpace) {
            mIsLock = true;
            mEditText.setText(source);
            mEditText.setSelection(source.length());
        }
    }

    private void filterLoginID(String source, int start, int count) {
        boolean isWhiteSpace = false;
        if (source.contains(" ")) {
            source = source.replace(" ","");
            isWhiteSpace = true;
        }
        if (!source.matches("[a-zA-Z0-9\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\=\\+\\{\\}\\[\\]\\:\\;\\,\\.\\<\\>\\?\\/\\~\\`\\|]+")) {
            restore();
            mToastPopup.show(R.string.only_alpha_num_special, -1);
            return;
        }
        if (isWhiteSpace) {
            mIsLock = true;
            mEditText.setText(source);
            mEditText.setSelection(source.length());
        }
    }

    private void filterPIN(String source, int start, int count) {
        if (!source.matches("[0-9]+")) {
            restore();
            mToastPopup.show(R.string.only_number, -1);
        }
    }

    private void filterTelephone(String source, int start, int count) {
        boolean isWhiteSpace = false;
        if (source.contains(" ")) {
            source = source.replace(" ","");
            isWhiteSpace = true;
        }
        if (!source.matches("[0-9\\-\\+]+")) {
            restore();
            mToastPopup.show(R.string.only_number_dash, -1);
            return;
        }
        if (isWhiteSpace) {
            mIsLock = true;
            mEditText.setText(source);
            mEditText.setSelection(source.length());
        }
    }

    private void filterUserID(String source, int start, int count) {
        boolean isWhiteSpace = false;
        if (source.contains(" ")) {
            source = source.replace(" ","");
            isWhiteSpace = true;
        }
        if (!source.matches("[a-zA-Z0-9\\.\\@]+")) {
            restore();
            mToastPopup.show(R.string.only_alpha_num_email, -1);
            return;
        }
        if (isWhiteSpace) {
            mIsLock = true;
            mEditText.setText(source);
            mEditText.setSelection(source.length());
        }
    }

    public enum EDIT_TYPE {
        LOGIN_ID, EMAIL, TELEPHONE, USER_ID, USER_NAME, PIN, PASSWORD
    }
}
