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
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;

public class SummaryUserView extends BaseView {
    public final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    private ImageView mBlurBackgroundView;
    private StyledTextView mTitleName;
    private StyledTextView mTitleUserId;
    private ImageView mPhoto;
    private SummaryUserViewListener mListener;

    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            if (mListener == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.profile_image:
                    mListener.editPhoto();
                    break;
            }
        }
    };

    public SummaryUserView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public SummaryUserView(Context context) {
        super(context);
        initView(context);
    }

    public SummaryUserView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mInflater.inflate(R.layout.view_summary_user, this, true);
        mTitleUserId = (StyledTextView) findViewById(R.id.title_user_id);
        mTitleName = (StyledTextView) findViewById(R.id.title_user_name);
        mPhoto = (ImageView) findViewById(R.id.profile_image);
        mBlurBackgroundView = (ImageView) findViewById(R.id.background_img);
        int[] ids = {R.id.profile_image};
        for (int i : ids) {
            findViewById(i).setOnClickListener(mClickListener);
        }
    }

    public void init(SummaryUserViewListener l) {
        mListener = l;
    }

    public void setCardCount(String content) {
        setTextView(R.id.title_card_count, content);
    }

    public void setFingerCount(String content) {
        setTextView(R.id.title_finger_count, content);
    }

    public void setFaceCount(String content) {
        setTextView(R.id.title_face_count, content);
    }

    public void showPin(boolean isVisible) {
        int visible;
        if (isVisible) {
            visible = View.VISIBLE;
        } else {
            visible = View.GONE;
        }
        findViewById(R.id.title_pin_exist).setVisibility(visible);
        findViewById(R.id.title_pin_exist_devider).setVisibility(visible);
    }


    public void setBlurBackGroud(Bitmap blur) {
        if (blur != null) {
            mBlurBackgroundView.setImageBitmap(blur);
        }
    }

    public void setBlurBackGroudDefault() {
        mBlurBackgroundView.setImageResource(R.drawable.background1);
    }

    public void setUserPhoto(Bitmap bmp) {
        if (bmp != null) {
            mPhoto.setImageBitmap(bmp);
        }
    }

    public void setUserPhotoDefault() {
        mPhoto.setImageResource(R.drawable.user_photo_bg);
    }

    public void setUserID(String content) {
        if (content != null) {
            mTitleUserId.setText(content);
        }
    }

    public void setUserName(String content) {
        if (content != null) {
            mTitleName.setText(content);
        }
    }

    public interface SummaryUserViewListener {
        public void editPhoto();
    }

}
