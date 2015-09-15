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
package com.supremainc.biostar2.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.supremainc.biostar2.R;

import java.lang.reflect.Field;

//public class SearchViewEx extends android.support.v7.widget.SearchView  { 
public class SearchViewEx extends android.support.v7.widget.SearchView {
    private static final String NAME_SPACE = "http://schemas.android.com/apk/res-auto";
    private static final int THEME_DARK = 1;
    private static final int THEME_LIGHT = 2;
    private SearchAutoComplete mSearchAutoComplete;
    private ImageView mSearchButton;
    private ImageView mSearchCloseIcon;
    private ImageView mSearchIcon;
    private View mSearchplate;
    private View mSubmitArea;
    private ImageView mVoiceIcon;

    public SearchViewEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public SearchViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SearchViewEx(Context context) {
        super(context);
    }

    private void darkTheme(Context context) {
        mSearchAutoComplete.setHintTextColor(Color.GRAY);
        mSearchAutoComplete.setTextColor(Color.BLACK);
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(mSearchAutoComplete, R.drawable.shape_cursor_black);
        } catch (Exception ignored) {
        }


        mSearchCloseIcon.setImageResource(R.drawable.abc_ic_clear_search_api_holo_light);
        // mSearchIcon.setImageResource(R.drawable.abc_ic_search_api_holo_light);
        // mSearchButton.setImageResource(R.drawable.abc_ic_search_api_holo_light);
        mSearchIcon.setImageResource(R.drawable.ic_search_nor);
        mSearchButton.setImageResource(R.drawable.ic_search_nor);
        mVoiceIcon.setImageResource(R.drawable.abc_ic_voice_search_api_holo_light);
    }

    public EditText getEditTextView() {
        return mSearchAutoComplete;
    }

    private void init(Context context, AttributeSet attrs) {
		mSearchAutoComplete = (SearchAutoComplete) findViewById(android.support.v7.appcompat.R.id.search_src_text);
//        mSearchAutoComplete = (SearchAutoComplete) findViewById(com.supremainc.biostar2.R.id.search_src_text);
        mSearchCloseIcon = (ImageView) findViewById(R.id.search_close_btn);
        mSearchplate = (View) findViewById(R.id.search_plate);
        mVoiceIcon = (ImageView) findViewById(R.id.search_voice_btn);
        mSearchIcon = (ImageView) findViewById(R.id.search_mag_icon);
        mSubmitArea = (View) findViewById(R.id.submit_area);
        mSearchButton = (ImageView) findViewById(R.id.search_button);

        mSearchplate.setBackgroundResource(R.color.transparent);
        mSubmitArea.setBackgroundResource(R.color.transparent);
        SpannableStringBuilder ssb = new SpannableStringBuilder(" ");
        mSearchAutoComplete.setHint(ssb);
        setMaxWidth(context.getResources().getDisplayMetrics().widthPixels);

        int themeColor = attrs.getAttributeIntValue(NAME_SPACE, "theme_color", 0);
        switch (themeColor) {
            case THEME_DARK:
                darkTheme(context);
                break;
            case THEME_LIGHT:
                break;
            default:
                break;
        }
    }

    public void setExpandMaxWidth(int px) {
        setMaxWidth(px);
    }
}