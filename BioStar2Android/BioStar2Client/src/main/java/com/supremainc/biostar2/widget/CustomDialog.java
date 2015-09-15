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

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class CustomDialog extends Dialog {
    private ViewGroup mContentView;
    private FrameLayout mFrameLayout;

    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//		getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7f000000"))); //	7f000000
        mFrameLayout = new FrameLayout(context);
        LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mFrameLayout.setBackgroundColor(Color.TRANSPARENT);

        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setContentView(mFrameLayout, params);
    }

    public void setLayout(ViewGroup view, FrameLayout.LayoutParams params) {
        mContentView = view;
        mFrameLayout.removeAllViews();
        mFrameLayout.addView(mContentView, params);
    }

    public void setLayout(ViewGroup view) {
        mContentView = view;
        mFrameLayout.removeAllViews();
        mFrameLayout.addView(mContentView);
    }


}
