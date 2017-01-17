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
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.supremainc.biostar2.R;

public class DetailTextItemView extends BaseView {
    final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";
    public final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    public LinearLayout container;
    public StyledTextView title;
    public StyledTextView content;
    public ImageView arrow;
    public boolean isArrow;

    public DetailTextItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public DetailTextItemView(Context context) {
        super(context);
        initView(context, null);
    }

    public DetailTextItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attr) {
        mInflater.inflate(R.layout.view_detail_text_link_item, this, true);
        container = (LinearLayout) findViewById(R.id.item_container);
        title = (StyledTextView) findViewById(R.id.item_index);
        content = (StyledTextView) findViewById(R.id.item_content);
        arrow = (ImageView) findViewById(R.id.item_arrow);
        if (attr != null) {
            TypedArray arr = context.obtainStyledAttributes(attr, R.styleable.DetailtemView);
            String inputType = arr.getString(R.styleable.DetailtemView_inputType);
            String index = arr.getString(R.styleable.DetailtemView_index);
            if (!TextUtils.isEmpty(index)) {
                title.setText(index);
            }
            enableLink(arr.getBoolean(R.styleable.DetailtemView_link, false));
            arr.recycle();
        } else {
            enableLink(false);
        }
    }


    public void enableLink(boolean link) {
        isArrow = link;
        if (link) {
            setBackgroundResource(R.drawable.selector_list_default_mode);
            arrow.setVisibility(View.VISIBLE);
        } else {
            setOnClickListener(null);
            setBackgroundColor(getResources().getColor(R.color.transparent));
            arrow.setVisibility(View.GONE);
        }
    }

    public void enableLink(boolean link, OnClickListener listener) {
        setOnClickListener(listener);
        enableLink(link);
    }

    public void setInputType(int type) {
        content.setInputType(type);
    }
}
