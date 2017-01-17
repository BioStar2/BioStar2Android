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
import android.content.Context;
import com.supremainc.biostar2.BuildConfig;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.sdk.datatype.v2.Common.VersionData;
import com.supremainc.biostar2.widget.popup.ToastPopup;

public class TextWatcherFilter implements TextWatcher {
    private final String TAG = getClass().getSimpleName();
    private String mBefore;
    private int mBeforeIndex;
    private EditText mEditText;
    private int mMaxLength;
    private ToastPopup mToastPopup;
    private EDIT_TYPE mType;
    private boolean mIsLock;
    private long mMaxSize;

    private boolean mIsMax = false;
    private boolean mIsCheckZero = false;

    public TextWatcherFilter(EditText editText, EDIT_TYPE type, Context context, int maxSize) {
        mEditText = editText;
        mType = type;
        mToastPopup = new ToastPopup(context);
        mMaxLength = maxSize;
    }

    public void setMaxSize(long maxSize,boolean set) {
        mMaxSize = maxSize;
        mIsMax = set;
    }

    public void setCheckZero(boolean set) {
        mIsCheckZero = set;
    }

    public void setMaxlength(int maxLength) {
        mMaxLength = maxLength;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        if (BuildConfig.DEBUG) {
//            Log.e(TAG,"beforeTextChanged :"+s.toString()+" mIsLock:"+mIsLock+ " length:"+s.toString().length());
//        }
//        if (BuildConfig.DEBUG) {
//            Log.e(TAG,"beforeTextChanged start:"+start+" count:"+count+ " after:"+after);
//        }
//        if (mIsLock) {
//            mIsLock = false;
//            return;
//        }
//        mBefore = s.toString();
//        mBeforeIndex = mEditText.getSelectionStart();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s == null ||  mType == null) {
            return;
        }
        if ( s.length() < 1 ) {
            mBefore = "";
            mBeforeIndex = 0;
            return;
        }
        if (mBefore == null) {
            mBefore = "";
            mBeforeIndex = 0;
        }
        String source = s.toString();
//        if (BuildConfig.DEBUG) {
//            Log.e(TAG,"source.length:"+source.length()+" mMaxLength:"+mMaxLength);
//            Log.e(TAG,"source:"+source.toString());
//        }
//        if (BuildConfig.DEBUG) {
//            Log.e(TAG,"onTextChanged start:"+start+" count:"+count+ " before:"+before);
//        }
        switch (mType) {
            case LOGIN_ID:
                if (filterLoginID(mBefore, false)) {
                    mBefore = "";
                    mBeforeIndex = 0;
                }
                if (filterLoginID(source, true)) {
                    restore();
                    return;
                }
                break;
            case PASSWORD:
                if (filterLoginPassword(mBefore, false)) {
                    mBefore = "";
                    mBeforeIndex = 0;
                }
                if (filterLoginPassword(source, true)) {
                    restore();
                    return;
                }
                break;
            case EMAIL:
                if (filterEmail(mBefore, false)) {
                    mBefore = "";
                    mBeforeIndex = 0;
                }
                if(filterEmail(source, true)) {
                    restore();
                    return;
                }
                break;
            case TELEPHONE:
                if (filterTelephone(mBefore, false)) {
                    mBefore = "";
                    mBeforeIndex = 0;
                }
                if (filterTelephone(source, true)) {
                    restore();
                    return;
                }
                break;
            case USER_ID:
                if (filterUserID(mBefore, false)) {
                    mBefore = "";
                    mBeforeIndex = 0;
                }
                if (filterUserID(source, true)) {
                    restore();
                    return;
                }
                break;
            case PIN:
                if (filterPIN(mBefore, false)) {
                    mBefore = "";
                    mBeforeIndex = 0;
                }
                if (filterPIN(source, true)) {
                    restore();
                    return;
                }
                break;
            case NUMBER:
                if (filterNumber(mBefore, false)) {
                    mBefore = "";
                    mBeforeIndex = 0;
                }
                if (filterNumber(source, true)) {
                    restore();
                    return;
                }
                break;
            case USER_NAME:
                break;
            default:
                break;
        }
        if (source.length() > mMaxLength) {
//            mToastPopup.show(R.string.over_value, -1);
            restore();
            return;
        }
        mBefore = s.toString();
        mBeforeIndex = mEditText.getSelectionStart();
        if (BuildConfig.DEBUG) {
            Log.e(TAG,"save succes  before length:"+mBefore.length()+" maxlength:"+mMaxLength);
        }
//        if (BuildConfig.DEBUG) {
//            Log.e(TAG,"save succes  before length:"+mBefore.length()+" maxlength:"+mMaxLength);
//            Log.e(TAG,"mBeforeIndex succes  mBeforeIndex:"+mBeforeIndex+" mBefore:"+mBefore);
//        }
    }

    private void restore() {
//        if (BuildConfig.DEBUG) {
//            Log.e(TAG,"restore before:"+mBefore+" mBeforeIndex:"+mBeforeIndex);
//        }
        if (mBefore == null) {
            mBefore = "";
            mBeforeIndex = 0;
        }else if (mBefore != null && mBefore.length() > mMaxLength) {
//            if (BuildConfig.DEBUG) {
//                Log.e(TAG,"restore before length:"+mBefore.length()+" maxlength:"+mMaxLength);
//            }
            mBefore = mBefore.substring(0,mMaxLength);
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                int index = mBeforeIndex;
                if (mBefore.equals("")) {
                    mEditText.getText().clear();
                } else {
                    mEditText.setText(mBefore);
                    if (index <= mBefore.length()) {
                        mEditText.setSelection(index);
                        mBeforeIndex = index;
                    } else {
                        mEditText.setSelection(mBefore.length());
                        mBeforeIndex = mBefore.length();
                    }
                }
            }
        });

    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private boolean filterEmail(String source, boolean warnning) {
        if (source.contains(" ")) {
            if (warnning)
                mToastPopup.show(R.string.invalid_email, -1);
            return true;
        }
        if (!source.matches("[a-zA-Z0-9\\@\\.\\-\\_]+")) {
            if (warnning)
                mToastPopup.show(R.string.invalid_email, -1);
            return true;
        }
        if (source.contains("..")) {
            if (warnning)
                mToastPopup.show(R.string.invalid_email, -1);
            return true;
        }
        int find = source.indexOf("@", 0);
        if (find > -1) {
            if (source.indexOf("@", find + 1) > -1) {
                if (warnning)
                    mToastPopup.show(R.string.invalid_email, -1);
                return true;
            }
        }
        return false;
    }

    private boolean filterLoginID(String source,  boolean warnning) {
        if (source.contains(" ")) {
            if (warnning)
                mToastPopup.show(R.string.only_alpha_num_special, -1);
            return true;
        }
        if (!source.matches("[a-zA-Z0-9\\-\\_]+")) {
            if (warnning)
                mToastPopup.show(R.string.only_alpha_num_special, -1);
            return true;
        }
        return false;
    }

    private boolean filterLoginPassword(String source,  boolean warnning) {
        if (source.contains(" ")) {
            if (warnning)
                mToastPopup.show(R.string.only_alpha_num_special, -1);
            return true;
        }
        if (!source.matches("[a-zA-Z0-9\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\=\\+\\{\\}\\[\\]\\:\\;\\,\\.\\<\\>\\?\\/\\~\\`\\|\\\\]+")) {
            if (warnning)
                mToastPopup.show(R.string.only_alpha_num_special, -1);
            return true;
        }
            return false;
    }

    private boolean filterPIN(String source,  boolean warnning) {
        if (source.contains(" ")) {
            if (warnning)
                mToastPopup.show(R.string.only_number_dash, -1);
            return true;
        }
        if (!source.matches("[0-9]+")) {
            if (warnning)
                mToastPopup.show(R.string.only_number, -1);
            return true;
        }
        return false;
    }

    private boolean filterTelephone(String source, boolean warnning) {
        if (source.contains(" ")) {
            if (warnning)
                mToastPopup.show(R.string.only_number_dash, -1);
            return true;
        }
        if (!source.matches("[0-9\\-\\+]+")) {
            if (warnning)
                mToastPopup.show(R.string.only_number_dash, -1);
            return true;
        }
        return false;
    }

    private boolean filterUserID(String source,  boolean warnning) {
        if (source.contains(" ")) {
            if (warnning)
                mToastPopup.show(R.string.only_alpha_num_special, -1);
            return true;
        }
        if (!source.matches("[a-zA-Z0-9\\-\\_]+")) {
            if (warnning)
                mToastPopup.show(R.string.only_alpha_num_special, -1);
            return false;
        }
        return false;
    }

    private boolean filterNumber(String source,  boolean warnning) {
        if (source.contains(" ")) {
            if (warnning)
                mToastPopup.show(R.string.only_number, -1);
            return true;
        }
        if (!source.matches("[0-9]+")) {
            if (warnning)
                mToastPopup.show(R.string.only_number, -1);
            return true;
        }
        if (mIsMax) {
            long value = Long.valueOf(source);
            if (value > mMaxSize) {
                if (warnning)
                    mToastPopup.show(R.string.over_value, String.valueOf(mMaxSize));
                return true;
            }
        }
        if (mIsCheckZero) {
            if (source.startsWith("0")) {
                if (warnning)
                    mToastPopup.show(R.string.invalid_card_id, -1);
                return true;
            }
        }
        return false;
    }



    public enum EDIT_TYPE {
        LOGIN_ID, EMAIL, TELEPHONE, USER_ID, USER_NAME, PIN, PASSWORD,NUMBER
    }
}
