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

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.widget.popup.ToastPopup;

import java.util.regex.Pattern;

public class TextInputFilter  {
    private final String TAG = getClass().getSimpleName();
    private ToastPopup mToastPopup;
    private InputFilter mInputFilter320;
    private InputFilter mInputFilter128;
    private InputFilter mInputFilter48;
    private InputFilter mInputFilter32;
    private InputFilter mInputFilter16;
    private InputFilter mInputFilter10;
    public TextInputFilter(ToastPopup popup) {
        mToastPopup = popup;
        mInputFilter320 =  new InputFilter.LengthFilter(320);
        mInputFilter128 =  new InputFilter.LengthFilter(128);
        mInputFilter48 =  new InputFilter.LengthFilter(48);
        mInputFilter32 =  new InputFilter.LengthFilter(32);
        mInputFilter16 =  new InputFilter.LengthFilter(16);
        mInputFilter10 =  new InputFilter.LengthFilter(10);
    }

    private InputFilter mLoginIDFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("[a-zA-Z0-9\\-\\_]+");
            if (!ps.matcher(source).matches()) {
                mToastPopup.show(R.string.only_alpha_num_special, -1);
                return "";
            }
            return null;
        }
    };
    private InputFilter mLoginPasswordFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("[a-zA-Z0-9\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\=\\+\\{\\}\\[\\]\\:\\;\\,\\.\\<\\>\\?\\/\\~\\`\\|\\\\]+");
            if (!ps.matcher(source).matches()) {
                if (!source.toString().isEmpty()) {
                    mToastPopup.show(R.string.only_alpha_num_special, -1);
                }
                return "";
            }
            return null;
        }
    };

    private InputFilter mEmailFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence s, int start, int end, Spanned dest, int dstart, int dend) {
            String source = s.toString();

            if (!source.matches("[a-zA-Z0-9\\@\\.\\-\\_]+")) {
                if (!source.isEmpty()) {
                    mToastPopup.show(R.string.invalid_email, -1);
                }
                return "";
            }
            if (source.contains("..")) {
                mToastPopup.show(R.string.invalid_email, -1);
                return "";
            }
            int find = source.indexOf("@", 0);
            if (find > -1) {
                if (source.indexOf("@", find + 1) > -1) {
                    mToastPopup.show(R.string.invalid_email, -1);
                    return "";
                }
            }
            return null;
        }
    };

    private InputFilter mTelephoneFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("[0-9\\-\\+]+");
            if (!ps.matcher(source).matches()) {
                if (!source.toString().isEmpty()) {
                    mToastPopup.show(R.string.only_number_dash, -1);
                }
                return "";
            }
            return null;
        }
    };
    private InputFilter mPinFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("[0-9]+");
            if (!ps.matcher(source).matches()) {
                if (!source.toString().isEmpty()) {
                    mToastPopup.show(R.string.only_number, -1);
                }
                return "";
            }
            return null;
        }
    };
    private InputFilter mUserIDFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("[a-zA-Z0-9\\-\\_]+");
            if (!ps.matcher(source).matches()) {
                if (!source.toString().isEmpty()) {
                    mToastPopup.show(R.string.only_alpha_num_special, -1);
                }
                return "";
            }
            return null;
        }
    };
    private InputFilter mNumberFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("[0-9]+");
            if (!ps.matcher(source).matches()) {
                if (!source.toString().isEmpty()) {
                    mToastPopup.show(R.string.only_number, -1);
                }
                return "";
            }
            return null;
        }
    };


    public boolean setFilter(EditText et,EDIT_TYPE type) {
        if (et == null ||  type == null) {
            return false;
        }

        switch (type) {
            case LOGIN_ID:
                et.setFilters(new InputFilter[]{mLoginIDFilter,mInputFilter32});
                break;
            case PASSWORD:
                et.setFilters(new InputFilter[]{mLoginPasswordFilter,mInputFilter32});
                break;
            case EMAIL:
                et.setFilters(new InputFilter[]{mEmailFilter,mInputFilter128});
                break;
            case TELEPHONE:
                et.setFilters(new InputFilter[]{mTelephoneFilter,mInputFilter32});
                break;
            case USER_ID:
                et.setFilters(new InputFilter[]{mUserIDFilter,mInputFilter32});
                break;
            case PIN:
                et.setFilters(new InputFilter[]{mPinFilter,mInputFilter16});
                break;
            case USER_ID_NUMBER:
                et.setFilters(new InputFilter[]{mUserIDFilter,mInputFilter10});
                break;
            case NUMBER:
                et.setFilters(new InputFilter[]{mNumberFilter,mInputFilter10});
                break;
            case USER_NAME:
                et.setFilters(new InputFilter[]{mInputFilter48});
                break;
            default:
                return false;
        }
        return true;
    }

    public enum EDIT_TYPE {
        LOGIN_ID, EMAIL, TELEPHONE, USER_ID,USER_ID_NUMBER, USER_NAME, PIN, PASSWORD,NUMBER
    }
}
