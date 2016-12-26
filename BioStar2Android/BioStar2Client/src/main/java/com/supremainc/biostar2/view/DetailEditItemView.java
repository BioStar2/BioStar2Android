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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.supremainc.biostar2.R;

public class DetailEditItemView extends BaseView {
    private static final String NAME_SPACE = "http://schemas.android.com/apk/res-auto";
    public final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    public LinearLayout container;
    public StyledTextView title;
    public StyledEditTextView content;
    public boolean isEdit;
    public ImageView arrow;

    public DetailEditItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public DetailEditItemView(Context context) {
        super(context);
        initView(context, null);
    }

    public DetailEditItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attr) {
        mInflater.inflate(R.layout.view_detail_edit_link_item, this, true);
        container = (LinearLayout) findViewById(R.id.item_container);
        title = (StyledTextView) findViewById(R.id.item_index);
        content = (StyledEditTextView) findViewById(R.id.item_content);
        arrow = (ImageView) findViewById(R.id.item_arrow);

        if (attr != null) {
            TypedArray arr = context.obtainStyledAttributes(attr, R.styleable.DetailtemView);
            int inputType = attr.getAttributeIntValue(NAME_SPACE, "inputType", 0);
//            String inputType = arr.getString(R.styleable.DetailtemView_inputType);
            switch (inputType) {
                case 0:
                    setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    break;
                case 1:
                    setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    break;
                case 2:
                    setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    break;
                case 3:
                    setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    break;
                case 4:
                    setInputType(InputType.TYPE_CLASS_PHONE);
                    break;
            }
//            if (TextUtils.isEmpty(inputType)) {
//                if ("numberDecimal".equals(inputType)) {
//                    setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
//                }
//            }
            String index = arr.getString(R.styleable.DetailtemView_index);
            if (!TextUtils.isEmpty(index)) {
                this.title.setText(index);
            }
            enableEdit(arr.getBoolean(R.styleable.DetailtemView_edit, false));
        }
    }


    public void enableEdit(boolean edit) {
        isEdit = edit;
        content.setEnabled(edit);
        content.setClickable(edit);
        content.setFocusableInTouchMode(edit);
        content.setFocusable(edit);
        if (edit) {
            content.setTextColor(mContext.getResources().getColor(R.color.subtext));
            arrow.setVisibility(View.VISIBLE);
        } else {
            content.setTextColor(mContext.getResources().getColor(R.color.content_text));
            arrow.setVisibility(View.GONE);
        }
    }

    public void setInputType(int type) {
        content.setInputType(type);
    }


}
