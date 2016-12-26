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

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.view.StyledTextView;

public class ActionbarTitle {
    private ActionBar mActionBar;
    private ActionBarActivity mContext;
    private int mHomeResId;
    private ImageView mIcon;
    private View mLeft;
    private ScreenControl mScreenControl;
    private StyledTextView mTitle;
    private Toolbar mToolbar;

    // private int mPadding;
    public ActionbarTitle(ActionBarActivity context, Toolbar toolbar, ScreenControl screenControl, int homeResId, String title, int backgroundRes, OnSingleClickListener homeListener) {
        if (toolbar == null || context == null || screenControl == null) {
            return;
        }
        // UtilProvider up = UtilProvider.getInstance(context);
        // mPadding = up.dpToPx(mContext, 50);
        mScreenControl = screenControl;
        mToolbar = toolbar;
        mContext = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout v = (LinearLayout) inflater.inflate(R.layout.view_custom_actionbar_title, null, false);
        initView(v);
        init(homeResId, title, backgroundRes);
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayUseLogoEnabled(false);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setDisplayShowCustomEnabled(true);
        android.support.v7.app.ActionBar.LayoutParams param = new android.support.v7.app.ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mActionBar.setCustomView(v, param);
        mToolbar.setContentInsetsAbsolute(0, 0);
        mLeft.setOnClickListener(homeListener);
        mTitle.setOnClickListener(homeListener);
    }

    public void disable() {
        if (mActionBar == null) {
            mActionBar = mContext.getSupportActionBar();
        }
        if (mActionBar == null) {
            return;
        }
        mActionBar.hide();
    }

    public Object getTitle() {
        if (mTitle == null) {
            return "";
        }
        return mTitle.toValue();
    }

    public void init(int homeResId, String title, int backgroundRes) {
        if (mToolbar == null) {
            return;
        }

        if (backgroundRes != -1) {
            mToolbar.setBackgroundResource(backgroundRes);
        } else {
            mToolbar.setBackgroundColor(mContext.getResources().getColor(R.color.actionbar_bg));
        }
        start();
        if (mTitle == null) {
            View view = mActionBar.getCustomView();
            if (view == null) {
                return;
            }
            initView(view);
        }
        if (mTitle == null) {
            return;
        }

        if (title == null) {
            mTitle.setText("");
        } else {
            mTitle.setText(title);
        }
        if (mHomeResId != homeResId) {
            mIcon.setImageResource(homeResId);
            mHomeResId = homeResId;
        }
        // mTitle.setPadding(titlePaddingCount*mPadding, 0, 0, 0);
    }

    private void initView(View v) {
        if (mTitle == null) {
            mTitle = (StyledTextView) v.findViewById(R.id.title_text);
        }
        if (mLeft == null) {
            mLeft = v.findViewById(R.id.title_left);
        }
        if (mIcon == null) {
            mIcon = (ImageView) v.findViewById(R.id.title_icon);
        }
    }

    public void setBackground(int resid) {
        if (mToolbar == null) {
            return;
        }
        mToolbar.setBackgroundResource(resid);
    }

    public void start() {
        if (mToolbar == null) {
            return;
        }
        mContext.setSupportActionBar(mToolbar);
        mActionBar = mContext.getSupportActionBar();

    }

}
