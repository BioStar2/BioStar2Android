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

import android.text.InputFilter;
import android.text.Spanned;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.widget.popup.ToastPopup;

import java.util.regex.Pattern;

public class TextInputFilter {
    private final String TAG = getClass().getSimpleName();
    private ToastPopup mToastPopup;
    private InputFilter mInputFilter320;
    private InputFilter mInputFilter128;
    private InputFilter mInputFilter57;
    private InputFilter mInputFilter48;
    private InputFilter mInputFilter32;
    private InputFilter mInputFilter16;
    private InputFilter mInputFilter10;
    private InputMethodManager mImm;
    private InputFilter mLoginPasswordFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("[a-zA-Z0-9\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\=\\+\\{\\}\\[\\]\\:\\;\\,\\.\\<\\>\\?\\/\\~\\`\\|\\\\]+");
            if (source.toString().isEmpty()) {
                return null;
            }
            if (!ps.matcher(source).matches()) {
                mToastPopup.show(R.string.only_alpha_num_special, -1);
                return "";
            }
            return null;
        }
    };
    private InputFilter mTelephoneFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("[0-9\\-\\+]+");
            if (source.toString().isEmpty()) {
                return null;
            }
            if (!ps.matcher(source).matches()) {
                mToastPopup.show(R.string.only_number_dash, -1);
                return "";
            }
            return null;
        }
    };
    private InputFilter mNumberFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("[0-9]+");
            if (source.toString().isEmpty()) {
                return null;
            }
            if (!ps.matcher(source).matches()) {
                mToastPopup.show(R.string.only_number, -1);
                return "";
            }
            return null;
        }
    };

    public TextInputFilter(InputMethodManager imm, ToastPopup toastPopup) {
        mToastPopup = toastPopup;
        mInputFilter320 = new InputFilter.LengthFilter(320);
        mInputFilter128 = new InputFilter.LengthFilter(128);
        mInputFilter57 = new InputFilter.LengthFilter(57);
        mInputFilter48 = new InputFilter.LengthFilter(48);
        mInputFilter32 = new InputFilter.LengthFilter(32);
        mInputFilter16 = new InputFilter.LengthFilter(16);
        mInputFilter10 = new InputFilter.LengthFilter(10);
        mImm = imm;
    }

    public boolean setFilter(EditText et, EDIT_TYPE type) {
        if (et == null || type == null) {
            return false;
        }

        switch (type) {
            case LOGIN_ID:
                et.setFilters(new InputFilter[]{new TextFilter(et, mToastPopup, mImm), mInputFilter32});
                break;
            case PASSWORD:
                et.setFilters(new InputFilter[]{mLoginPasswordFilter, mInputFilter32});
                break;
            case TELEPHONE:
                et.setFilters(new InputFilter[]{mTelephoneFilter, mInputFilter32});
                break;
            case USER_ID:
                et.setFilters(new InputFilter[]{new TextFilter(et, mToastPopup, mImm), mInputFilter32});
                break;
            case PIN:
                et.setFilters(new InputFilter[]{mNumberFilter, mInputFilter16});
                break;
            case USER_NAME:
                et.setFilters(new InputFilter[]{mInputFilter48});
                break;
            case EMAIL:
                et.setFilters(new InputFilter[]{mInputFilter128});
                break;
            case NONE:
                et.setFilters(new InputFilter[]{});
                break;
            default:
                return false;
        }
        return true;
    }


    public enum EDIT_TYPE {
        LOGIN_ID, TELEPHONE, USER_ID, USER_NAME, PIN, PASSWORD, NONE,EMAIL
    }

    private static class TextFilter implements InputFilter {
        private EditText mView;
        private ToastPopup mToastPopup;
        private InputMethodManager mImm;

        public TextFilter(EditText view, ToastPopup totastPopup, InputMethodManager imm) {
            mView = view;
            mToastPopup = totastPopup;
            mImm = imm;
        }

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                   int dstart, int dend) {
            Pattern ps = Pattern.compile("[a-zA-Z0-9\\-\\_]+");
            String test = source.toString();
            if (test.isEmpty()) {
                return null;
            }
            if (test.equals(" ")) {
                return "";
            }
            if (!ps.matcher(source).matches()) {
                if (mImm != null && mView != null && mToastPopup != null) {
                    if (mImm.isActive(mView)) {
                        mToastPopup.show(R.string.only_alpha_num_special, -1);
                        mImm.restartInput(mView); //for samsung samrt phone : samsung ime autocomplete letter
//                    mImm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
//                    mImm.showSoftInput(mView, 0);
                    }
                }
                return "";
            }
            return null;
        }
    }
}
