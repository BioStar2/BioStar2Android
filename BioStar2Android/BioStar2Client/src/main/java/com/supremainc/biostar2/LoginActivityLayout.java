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
package com.supremainc.biostar2;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;
import com.supremainc.biostar2.sdk.provider.UserDataProvider;
import com.supremainc.biostar2.util.TextWatcherFilter;
import com.supremainc.biostar2.util.TextWatcherFilter.EDIT_TYPE;
import com.supremainc.biostar2.widget.OnSingleClickListener;
import com.supremainc.biostar2.widget.StyledEditTextView;
import com.supremainc.biostar2.widget.StyledTextView;

public class LoginActivityLayout {
    private final String TAG = getClass().getSimpleName();
    private Activity mActivity;
    private Context mContext;
    private LoginActivityLayoutEvent mLoginActivityLayoutEvent;
    private AppDataProvider mAppDataProvider;
    private UserDataProvider mUserDataProvider;
    // View
    private StyledEditTextView mID;
    private StyledTextView mLogin;
    private StyledEditTextView mPassword;
    private View mRestoreURL;
    private StyledTextView mSubDomain;
    private StyledTextView mAddress;
    private StyledEditTextView mInput;
    private View mInputCotainer;

    private Boolean isFocusSubDomain = true;
    private String mAddressFullText;
    private InputMethodManager mImm;
    //
    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            onProcessViewClick(v);
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s == null || s.length() < 1) {
                if (isFocusSubDomain) {
                    mSubDomain.setText("");
                } else {
                    mAddressFullText = "";
                    mAddress.setText("");
                }
                return;
            }
            String data = s.toString();

            if (data.contains(" ")) {
                data = data.replace(" ","");
                mInput.setText(data);
                return;
            }

            if (isFocusSubDomain) {
                mSubDomain.setText(s.toString());
            } else {
                if (!(data.startsWith("h") || data.startsWith("H"))) {
                    mInput.setText("https://");
                    mInput.setSelection("https://".length());
                    return;
                }
                mAddressFullText = s.toString();
                mAddress.setText(getParsingAddress(mAddressFullText));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    public LoginActivityLayout(Activity activity, LoginActivityLayoutEvent layoutEvent) {
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mLoginActivityLayoutEvent = layoutEvent;
        mUserDataProvider = UserDataProvider.getInstance(mContext);
        mAppDataProvider = AppDataProvider.getInstance(mContext);
    }

    public void changeScreen() {
        LinearLayout login = (LinearLayout) mActivity.findViewById(R.id.splash_after);
        LinearLayout splash = (LinearLayout) mActivity.findViewById(R.id.splash);
        if (login.getVisibility() != View.VISIBLE) {
            login.setVisibility(View.VISIBLE);
        }
        if (splash.getVisibility() != View.INVISIBLE) {
            splash.setVisibility(View.INVISIBLE);
        }
    }

    private String getSpliteAddress(String key, String content) {
        String lower = content.toLowerCase();
        if (lower.startsWith(key)) {
            content = content.substring(key.length(), content.length());
            int index = content.indexOf("/");
            if (index > 8) {
                content = content.substring(0, index);
            }
            String[] words = content.split("\\.");
            if (words.length > 2) {
                boolean isNumberIP = true;
                for (String word : words) {
                    if (!word.matches("[0-9\\:\\.]+")) {
                        isNumberIP = false;
                    }
                }
                if (isNumberIP == false) {
                    content = content.replace(words[0] + ".", "");
                }
            }
        }
        return content;
    }

    private String getParsingAddress(String content) {
        if (content.length() < 12) {
            return content;
        }
        String result = getSpliteAddress("https://", content);
        result = getSpliteAddress("http://", result);
        return result;
    }

    public void initView() {
        mActivity.setContentView(R.layout.activity_login);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mInputCotainer = mActivity.findViewById(R.id.container_input);
        mInput = (StyledEditTextView) mActivity.findViewById(R.id.input);
        mInput.addTextChangedListener(mTextWatcher);
        mAddress = (StyledTextView) mActivity.findViewById(R.id.address);
        mAddressFullText = mUserDataProvider.getLatestUrl();
        mAddress.setText(getParsingAddress(mAddressFullText));
        mAddress.setOnClickListener(mClickListener);
        mSubDomain = (StyledTextView) mActivity.findViewById(R.id.subdomain);
        mSubDomain.setOnClickListener(mClickListener);
        mImm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
        String latestSubDomain = mAppDataProvider.getLatestDomain();
        if (latestSubDomain != null) {
            mSubDomain.setText(latestSubDomain);
            mInput.setText(latestSubDomain);
        }

        mActivity.findViewById(R.id.quick_guide).setOnClickListener(mClickListener);
        mRestoreURL = mActivity.findViewById(R.id.default_url);
        mRestoreURL.setOnClickListener(mClickListener);
        mLogin = (StyledTextView) mActivity.findViewById(R.id.login);
        mLogin.setOnClickListener(mClickListener);
        mID = (StyledEditTextView) mActivity.findViewById(R.id.login_id);
        mID.addTextChangedListener(new TextWatcherFilter(mID, EDIT_TYPE.LOGIN_ID, mActivity, 32));
        String id = mAppDataProvider.getLatestUserID();
        if (id != null && !id.isEmpty()) {
            mID.setText(id);
            mID.setSelection(id.length());
        }
        mPassword = (StyledEditTextView) mActivity.findViewById(R.id.password);
        mPassword.addTextChangedListener(new TextWatcherFilter(mPassword, EDIT_TYPE.PASSWORD, mActivity, 32));
        mPassword.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    mClickListener.onClick(mLogin);
                    return false;
                }
                return false;
            }
        });
        initFocusSubDomain();
    }

    public void setPassword(String content) {
        mPassword.setText(content);
    }

    private void initFocusSubDomain() {
        isFocusSubDomain = true;
        mInput.setText(mSubDomain.toString2());
        mInputCotainer.setBackgroundResource(R.drawable.iogin_text_bubble_left);
        mRestoreURL.setVisibility(View.GONE);
        mSubDomain.setTextColor(mContext.getResources().getColor(R.color.login));
        mSubDomain.setBackgroundResource(R.drawable.login_tab);
        mAddress.setTextColor(mContext.getResources().getColor(R.color.gray_12));
        mAddress.setBackgroundResource(R.drawable.login_nor);
        mSubDomain.setPadding(30, 0, 0, 0);
        mAddress.setPadding(30, 0, 0, 0);
    }

    public void setFocusSubDomain() {
        initFocusSubDomain();
        mInputCotainer.setVisibility(View.VISIBLE);
        mInput.requestFocus();
        mImm.showSoftInput(mInput, 0);
        mInput.setSelection(mInput.toString2().length());
    }

    public void setFocusAddress() {
        isFocusSubDomain = false;
        mInput.setText(mAddressFullText);
        mInputCotainer.setBackgroundResource(R.drawable.iogin_text_bubble_right);
        mRestoreURL.setVisibility(View.VISIBLE);
        mAddress.setTextColor(mContext.getResources().getColor(R.color.login));
        mAddress.setBackgroundResource(R.drawable.login_tab);
        mSubDomain.setTextColor(mContext.getResources().getColor(R.color.gray_12));
        mSubDomain.setBackgroundResource(R.drawable.login_nor);
        mSubDomain.setPadding(30, 0, 0, 0);
        mAddress.setPadding(30, 0, 0, 0);
        mInputCotainer.setVisibility(View.VISIBLE);
        mInput.requestFocus();
        mImm.showSoftInput(mInput, 0);
        mInput.setSelection(mInput.toString2().length());
    }

    public void onDestroy() {
        mActivity = null;
        mContext = null;
    }

    private void onProcessViewClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                if (mLoginActivityLayoutEvent != null) {
                    mLoginActivityLayoutEvent.onClickLogin(mAddressFullText, mSubDomain.toString2(), mID.toString2(), mPassword.toString2());
                }
                break;
            case R.id.default_url:
                mInput.setText(ConfigDataProvider.URL);
                break;
            case R.id.address:
                setFocusAddress();
                break;
            case R.id.subdomain:
                setFocusSubDomain();
                break;
            case R.id.quick_guide:
                if (mLoginActivityLayoutEvent != null) {
                    mLoginActivityLayoutEvent.onClickQuickGuide();
                }
                break;
        }
    }



    public interface LoginActivityLayoutEvent {
        public void onClickLogin(String url, String subDomain, String id, String pw);
        public void onClickQuickGuide();
    }
}