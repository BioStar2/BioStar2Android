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
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.supremainc.biostar2.R;

public class DetailSwitchItemView extends BaseView {
    final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";
    public final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    public LinearLayout mContainer;
    public StyledTextView mIndex;
    public StyledEditTextView mContent;
    public SwitchView mSwitchView;
    public boolean mIsEdit;

    public DetailSwitchItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public DetailSwitchItemView(Context context) {
        super(context);
        initView(context, null);
    }

    public DetailSwitchItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attr) {
        mInflater.inflate(R.layout.view_detail_switch_item, this, true);
        mContainer = (LinearLayout) findViewById(R.id.item_container);
        mIndex = (StyledTextView) findViewById(R.id.item_index);
        mContent = (StyledEditTextView) findViewById(R.id.item_content);
        mSwitchView = (SwitchView) findViewById(R.id.item_switch);
        if (attr != null) {

            TypedArray arr = context.obtainStyledAttributes(attr, R.styleable.DetailtemView);
            String inputType = arr.getString(R.styleable.DetailtemView_inputType);
            if (TextUtils.isEmpty(inputType)) {
                if ("numberDecimal".equals(inputType)) {
                    setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }
            }
            String index = arr.getString(R.styleable.DetailtemView_index);
            if (!TextUtils.isEmpty(index)) {
                mIndex.setText(index);
            }
        }
    }

    public void enableEdit(boolean edit) {
        mIsEdit = edit;
        mContent.setEnabled(edit);
        mContent.setFocusable(edit);
    }

    public void setInputType(int type) {
        mContent.setInputType(type);
    }


}
