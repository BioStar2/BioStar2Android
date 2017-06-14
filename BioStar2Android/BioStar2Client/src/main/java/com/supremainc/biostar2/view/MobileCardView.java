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
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.sdk.models.v2.card.MobileCard;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.sdk.provider.DateTimeDataProvider;
import com.supremainc.biostar2.sdk.utils.ImageUtil;
import com.supremainc.biostar2.util.Utils;

import static android.graphics.Bitmap.createBitmap;

public class MobileCardView extends BaseView {
    public final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    private LinearLayout mCardContainer;
    private Bitmap mBitmap;
    private ImageView mImageView;

    public MobileCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public MobileCardView(Context context) {
        super(context);
        initView(context);
    }

    public MobileCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    private void initView(Context context) {
        mInflater.inflate(R.layout.item_aoc, this, true);
        mCardContainer = (LinearLayout) findViewById(R.id.card_container);
    }

    public boolean setCard(MobileCard card, User user, OnSingleClickListener listener,ImageView iv) {
        if (mCardContainer == null) {
            return false;
        }
        mCardContainer.setTag(card);
        mCardContainer.setOnClickListener(listener);
        if (MobileCard.SECURE_CREDENTIAL.equals(card.type)) {
            setSecureCardData(user, card);
        } else {
            setAccessOnCardData(user, card);
        }
        captureImage(mCardContainer);
        drawImage(iv);
        return true;
    }

    private void captureImage(View v) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "h:" + v.getMeasuredHeight() + " w:" + v.getMeasuredWidth());
        }

        if (v.getMeasuredHeight() <= 0 || v.getMeasuredWidth() <= 0) {
            int w = Utils.convertDpToPixel(380, mContext);
            int h = Utils.convertDpToPixel(230, mContext);
            Bitmap b = createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "h:" + h + " w:" + w);
            }
            v.layout(0, 0, w,h);
            v.draw(c);
        } else {
            Bitmap b = createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            mBitmap =Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
            b.recycle();
        }
    }



    public void drawImage(ImageView iv) {
        if (iv != null && mBitmap != null) {
            iv.setImageBitmap(mBitmap);
            iv.setVisibility(View.VISIBLE);
            mImageView = iv;
        }
    }

    public void relese() {
        if (mImageView != null) {
            mImageView.setImageBitmap(null);
        }
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    private void setPhoto(ImageView view, String photo) {
        if (photo != null && !photo.isEmpty()) {
            byte[] photoByte = Base64.decode(photo, 0);
            Bitmap bmp = ImageUtil.byteArrayToBitmap(photoByte);
            if (bmp != null) {
                view.setImageBitmap(bmp);
            }
        }
    }

    private void setAccessOnCardData(User user, MobileCard card) {
        if (mCardContainer == null | user == null || card == null) {
            return;
        }
        mCardContainer.findViewById(R.id.access_group_container).setVisibility(View.VISIBLE);
        mCardContainer.findViewById(R.id.period_container).setVisibility(View.VISIBLE);
        StyledTextView name = (StyledTextView) mCardContainer.findViewById(R.id.user_name);
        name.setText(user.getName());
        StyledTextView cardType = (StyledTextView) mCardContainer.findViewById(R.id.card_type);
        cardType.setText(mContext.getString(R.string.access_on_card));
        ImageView photo = (ImageView) mCardContainer.findViewById(R.id.user_photo);
        setPhoto(photo, user.photo);
        StyledTextView cardID = (StyledTextView) mCardContainer.findViewById(R.id.card_id);
        cardID.setText(card.card_id);
        StyledTextView fingerCount = (StyledTextView) mCardContainer.findViewById(R.id.fingerprint_count);
        if (card.fingerprint_index_list != null) {
            fingerCount.setText(String.valueOf(card.fingerprint_index_list.size()));
        } else {
            fingerCount.setText("0");
        }
        StyledTextView period = (StyledTextView) mCardContainer.findViewById(R.id.period);
        String startDateTime = card.getTimeFormmat(DateTimeDataProvider.getInstance(), MobileCard.TimeType.start_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN);
        String endDateTime = card.getTimeFormmat(DateTimeDataProvider.getInstance(), MobileCard.TimeType.expiry_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN);
        period.setText(startDateTime + " - " + endDateTime);

        StyledTextView accessGroup = (StyledTextView) mCardContainer.findViewById(R.id.access_group);
        if (card.access_groups != null && card.access_groups.size() > 0) {
            if (card.access_groups.size() > 1) {
                accessGroup.setText(card.access_groups.get(0).name + " + " + (card.access_groups.size() - 1));
            } else {
                accessGroup.setText(card.access_groups.get(0).name);
            }
        } else {
            accessGroup.setText(mContext.getString(R.string.none));
        }
        ImageView pin = (ImageView) mCardContainer.findViewById(R.id.pin_image);
        if (card.pin_exist) {
            pin.setVisibility(View.VISIBLE);
        } else {
            pin.setVisibility(View.GONE);
        }
        mCardContainer.invalidate();
    }

    private void setSecureCardData(User user, MobileCard card) {
        if (mCardContainer == null | user == null || card == null) {
            return;
        }
        mCardContainer.findViewById(R.id.access_group_container).setVisibility(View.GONE);
        mCardContainer.findViewById(R.id.period_container).setVisibility(View.GONE);
        StyledTextView cardType = (StyledTextView) mCardContainer.findViewById(R.id.card_type);
        cardType.setText(mContext.getString(R.string.secure_card));
        ImageView photo = (ImageView) mCardContainer.findViewById(R.id.user_photo);
        setPhoto(photo, user.photo);
        StyledTextView cardID = (StyledTextView) mCardContainer.findViewById(R.id.card_id);
        cardID.setText(card.card_id);
        StyledTextView fingerCount = (StyledTextView) mCardContainer.findViewById(R.id.fingerprint_count);
        if (card.fingerprint_index_list != null) {
            fingerCount.setText(String.valueOf(card.fingerprint_index_list.size()));
        } else {
            fingerCount.setText("0");
        }
        StyledTextView name = (StyledTextView) mCardContainer.findViewById(R.id.user_name);
        name.setText(user.getName());
        ImageView pin = (ImageView) mCardContainer.findViewById(R.id.pin_image);
        if (card.pin_exist) {
            pin.setVisibility(View.VISIBLE);
        } else {
            pin.setVisibility(View.GONE);
        }
        mCardContainer.invalidate();
    }

}
