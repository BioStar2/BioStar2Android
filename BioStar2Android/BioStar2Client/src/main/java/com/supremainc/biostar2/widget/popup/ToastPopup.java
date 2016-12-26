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
package com.supremainc.biostar2.widget.popup;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.view.StyledTextView;

public class ToastPopup extends Toast {
    public static final int TYPE_ALARM = 3;
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_DOOR = 1;
    public static final int TYPE_INFO = 5;
    public static final int TYPE_LOG = 2;
    public static final int TYPE_USER = 4;
    private StyledTextView mContentView;
    private Context mContext;
    private StyledTextView mTitleView;
    private ImageView mType;
    private View mView;

    public ToastPopup(Context context) {
        super(context);
        mContext = context;
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = mInflater.inflate(R.layout.popup_toast, null);
        mTitleView = (StyledTextView) mView.findViewById(R.id.popup_title);
        mContentView = (StyledTextView) mView.findViewById(R.id.popup_content);
        mType = (ImageView) mView.findViewById(R.id.popup_item);
        setGravity(Gravity.CENTER | Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
        setDuration(Toast.LENGTH_LONG);
        setView(mView);
    }

    public void show(String title, String content) {
        show(TYPE_DEFAULT, title, content);
    }

    public void show(int titleId, String content) {
        String title = null;
        if (titleId != -1) {
            title = mContext.getString(titleId);
        }
        show(TYPE_DEFAULT, title, content);
    }

    public void show(int titleId, int contentId) {
        String title = null;
        String content = null;
        if (titleId != -1) {
            title = mContext.getString(titleId);
        }
        if (contentId != -1) {
            content = mContext.getString(contentId);
        }
        show(TYPE_DEFAULT, title, content);

    }

    public void show(int type, String title, String content) {
        // http://developer.android.com/guide/topics/ui/notifiers/toasts.html
        if (title != null) {
            mTitleView.setVisibility(View.VISIBLE);
            mTitleView.setText(title);
//            mContentView.setMaxLines(1);
//            mContentView.setSingleLine(true);
        } else {
            mTitleView.setVisibility(View.GONE);
//            mContentView.setMaxLines(2);
//            mContentView.setSingleLine(false);
        }
        if (content != null) {
            mContentView.setVisibility(View.VISIBLE);
            mContentView.setText(content);
//            mTitleView.setMaxLines(1);
//            mTitleView.setSingleLine(true);
        } else {
            mContentView.setVisibility(View.GONE);
//            mTitleView.setMaxLines(2);
//            mTitleView.setSingleLine(false);
        }
        if (mType != null) {
            switch (type) {
                case TYPE_DOOR:
                    mType.setImageResource(R.drawable.toast_popup7);
                    break;
                case TYPE_LOG:
                    mType.setImageResource(R.drawable.toast_popup3);
                    break;
                case TYPE_ALARM:
                    mType.setImageResource(R.drawable.toast_popup2);
                    break;
                case TYPE_USER:
                    mType.setImageResource(R.drawable.toast_popup1);
                    break;
                case TYPE_INFO:
                default:
                    mType.setImageResource(R.drawable.toast_popup6);
                    break;
            }
        }
        show();
    }
}
