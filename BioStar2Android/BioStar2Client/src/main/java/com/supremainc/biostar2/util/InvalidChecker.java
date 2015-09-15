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

import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.widget.StyledEditTextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvalidChecker {
    private final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private Pattern mPattern;
    private Popup mPopup;

    public InvalidChecker(Popup popup) {
        mPopup = popup;
    }

    private boolean checkEmailInValid(String email) {
        if (mPattern == null) {
            mPattern = Pattern.compile(EMAIL_PATTERN);
        }
        Matcher matcher = mPattern.matcher(email);
        return !matcher.matches();
    }

    public boolean isEmptyString(String title, String show, String... strings) {
        for (String string : strings) {
            if (string == null || string.isEmpty())
                if (showPopup(title, show)) {
                    return true;
                }
        }
        return false;
    }

    public boolean isEmptyStyledEditTextView(String title, String show, StyledEditTextView... views) {
        for (StyledEditTextView view : views) {
            if (view.toString2().equals("")) {
                if (showPopup(title, show)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEmptyStyledEditTextViewAnd(String title, String show, StyledEditTextView... views) {
        boolean isNotEmpty = false;
        for (StyledEditTextView view : views) {
            if (!view.toString2().equals("")) {
                isNotEmpty = true;
                break;
            }
        }
        if (isNotEmpty) {
            return false;
        }

        if (showPopup(title, show)) {
            return true;
        }
        return true;
    }

    public boolean isInvalidEmail(String title, String show, String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
//        if (email == null || email.isEmpty()) {
//            if (showPopup(title, show)) {
//                return true;
//            }
//        }
        if (checkEmailInValid(email)) {
            if (showPopup(title, show)) {
                return true;
            }
        }
        return false;
    }

    private boolean showPopup(String title, String show) {
        if (show != null) {
            mPopup.show(PopupType.ALERT, title, show, null, null, null);
            return true;
        } else {
            return false;
        }
    }

}
