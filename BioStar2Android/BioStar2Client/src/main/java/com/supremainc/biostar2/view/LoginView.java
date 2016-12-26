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
package com.supremainc.biostar2.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;
import com.supremainc.biostar2.util.TextWatcherFilter;

public class LoginView extends BaseView {
    public final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    private StyledEditTextView mID;
    private StyledTextView mLogin;
    private StyledEditTextView mPassword;
    private View mRestoreURL;
    private StyledTextView mSubDomain;
    private StyledTextView mAddress;
    private StyledEditTextView mInput;
    private View mInputCotainer;

    private Boolean mIsFocusSubDomain = true;
    private String mAddressFullText;
    private InputMethodManager mImm;
    private LoginViewListener mLoginViewListener;
    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()) {
                case R.id.login:
                    if (mLoginViewListener != null) {
                        mLoginViewListener.onClickLogin(mAddressFullText, mSubDomain.toString2(), mID.toString2(), mPassword.toString2());
                    }
                    break;
                case R.id.default_url:
                    mInput.setText(ConfigDataProvider.URL);
//                    mInput.setText(ConfigDataProvider.getLatestURL(mContext));
                    break;
                case R.id.address:
                    setFocusAddress();
                    break;
                case R.id.subdomain:
                    setFocusSubDomain();
                    break;
            }
        }
    };
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s == null || s.length() < 1) {
                if (mIsFocusSubDomain) {
                    mSubDomain.setText("");
                } else {
                    mAddressFullText = "";
                    mAddress.setText("");
                }
                return;
            }
            String data = s.toString();

            if (data.contains(" ")) {
                data = data.replace(" ", "");
                mInput.setText(data);
                return;
            }

            if (mIsFocusSubDomain) {
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

    public LoginView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public LoginView(Context context) {
        super(context);
        initView(context);
    }

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mInflater.inflate(R.layout.view_login, this, true);
        mInputCotainer = findViewById(R.id.container_input);
        mInput = (StyledEditTextView) findViewById(R.id.input);
        mInput.addTextChangedListener(mTextWatcher);
        mAddress = (StyledTextView) findViewById(R.id.address);
        mAddress.setOnClickListener(mClickListener);
        mSubDomain = (StyledTextView) findViewById(R.id.subdomain);
        mSubDomain.setOnClickListener(mClickListener);
        mImm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
        mRestoreURL = findViewById(R.id.default_url);
        mRestoreURL.setOnClickListener(mClickListener);
        mLogin = (StyledTextView) findViewById(R.id.login);
        mLogin.setOnClickListener(mClickListener);
        mID = (StyledEditTextView) findViewById(R.id.login_id);
        mID.addTextChangedListener(new TextWatcherFilter(mID, TextWatcherFilter.EDIT_TYPE.LOGIN_ID, mContext, 32));
        mPassword = (StyledEditTextView) findViewById(R.id.password);
        mPassword.addTextChangedListener(new TextWatcherFilter(mPassword, TextWatcherFilter.EDIT_TYPE.PASSWORD, mContext, 32));
        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

    public void setListener(LoginViewListener l) {
        mLoginViewListener = l;
    }

    public void setAddress(String text) {
        if (!TextUtils.isEmpty(text)) {
            mAddressFullText = text;
            mAddress.setText(getParsingAddress(mAddressFullText));
        }
    }

    public void setSubDomain(String text) {
        if (!TextUtils.isEmpty(text)) {
            mSubDomain.setText(text);
            mInput.setText(text);
        }
    }

    public void setID(String text) {
        if (!TextUtils.isEmpty(text)) {
            mID.setText(text);
            mID.setSelection(text.length());
        }
    }

    private void initFocusSubDomain() {
        mIsFocusSubDomain = true;
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

    public void setPassword(String content) {
        mPassword.setText(content);
    }

    private void setFocusSubDomain() {
        initFocusSubDomain();
        mInputCotainer.setVisibility(View.VISIBLE);
        mInput.requestFocus();
        mImm.showSoftInput(mInput, 0);
        mInput.setSelection(mInput.toString2().length());
    }

    private void setFocusAddress() {
        mIsFocusSubDomain = false;
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

    public interface LoginViewListener {
        public void onClickLogin(String url, String subDomain, String id, String pw);
    }

}
