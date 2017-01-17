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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.datatype.MobileCardData;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.sdk.datatype.v2.Card.MobileCard;
import com.supremainc.biostar2.sdk.datatype.v2.User.User;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;
import com.supremainc.biostar2.sdk.utils.ImageUtil;

public class MobileCardListView extends BaseView {
    public final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    public LinearLayout mCardListView;

    public MobileCardListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public MobileCardListView(Context context) {
        super(context);
        initView(context);
    }

    public MobileCardListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mInflater.inflate(R.layout.view_mobilecard_list, this, true);
        mCardListView = (LinearLayout) findViewById(R.id.card_list);
    }

    public void removeAllMenuItem() {
        if (mCardListView != null) {
            mCardListView.removeAllViews();
        }
    }


    public boolean addCard(MobileCard card, User user, OnSingleClickListener listener) {
        int resID = R.layout.item_aoc;

        if (MobileCard.SECURE_CREDENTIAL.equals(card.type)) {
            resID = R.layout.item_secure_card;
        }
        if (mCardListView == null) {
            return false;
        }
        View item = mInflater.inflate(resID, mCardListView, false);
        item.setTag(card);
        item.setOnClickListener(listener);
        SwitchView switchView = (SwitchView) item.findViewById(R.id.card_switch);
        switchView.init(mContext, new SwitchView.OnChangeListener() {
            @Override
            public void onChange(boolean on) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "pin :" + on);
                }
                if (on) {
                } else {
                }
            }
        }, card.is_registered, SwitchView.SwitchType.RED);
        if (MobileCard.SECURE_CREDENTIAL.equals(card.type)) {
            setSecureCardData(item, user, card);
        } else {
            setAccessOnCardData(item, user, card);
        }
        mCardListView.addView(item);
        return true;
    }

    private void setPhoto(ImageView view,String photo) {
        if(photo!=null&&!photo.isEmpty())   {
            byte[] photoByte = Base64.decode(photo, 0);
            Bitmap bmp = ImageUtil.byteArrayToBitmap(photoByte);
            if (bmp != null) {
                view.setImageBitmap(bmp);
            }
        }
    }
    private void setAccessOnCardData(View v, User user, MobileCard card) {
        StyledTextView cardType = (StyledTextView) v.findViewById(R.id.card_type);
        cardType.setText(mContext.getString(R.string.access_on_card));
        ImageView photo = (ImageView) v.findViewById(R.id.user_photo);
        setPhoto(photo,user.photo);
        StyledTextView cardID = (StyledTextView) v.findViewById(R.id.card_id);
        cardID.setText(card.card_id);
        StyledTextView fingerCount = (StyledTextView) v.findViewById(R.id.fingerprint_count);
        if (card.fingerprint_index_list != null) {
            fingerCount.setText(String.valueOf(card.fingerprint_index_list.size()));
        } else {
            fingerCount.setText("");
        }
        StyledTextView period = (StyledTextView) v.findViewById(R.id.period);
        String startDateTime = card.getTimeFormmat(TimeConvertProvider.getInstance(), MobileCard.TimeType.start_datetime, TimeConvertProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN);
        String endDateTime = card.getTimeFormmat(TimeConvertProvider.getInstance(), MobileCard.TimeType.expiry_datetime, TimeConvertProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN);
        period.setText(startDateTime + " - " + endDateTime);
        StyledTextView name = (StyledTextView) v.findViewById(R.id.user_name);
        name.setText(user.getName());
        StyledTextView accessGroup = (StyledTextView) v.findViewById(R.id.access_group);
        if (card.access_groups != null && card.access_groups.size() > 0) {
            if (card.access_groups.size() > 1) {
                accessGroup.setText(card.access_groups.get(0).name + " + " + (card.access_groups.size() - 1));
            } else {
                accessGroup.setText(card.access_groups.get(0).name);
            }
        }
    }

    private void setSecureCardData(View v, User user, MobileCard card) {
        StyledTextView cardType = (StyledTextView) v.findViewById(R.id.card_type);
        cardType.setText(mContext.getString(R.string.secure_card));
        ImageView photo = (ImageView) v.findViewById(R.id.user_photo);
        setPhoto(photo,user.photo);
        StyledTextView cardID = (StyledTextView) v.findViewById(R.id.card_id);
        cardID.setText(card.card_id);
        StyledTextView fingerCount = (StyledTextView) v.findViewById(R.id.fingerprint_count);
        if (card.fingerprint_index_list != null) {
            fingerCount.setText(String.valueOf(card.fingerprint_index_list.size()));
        } else {
            fingerCount.setText("");
        }
        StyledTextView name = (StyledTextView) v.findViewById(R.id.user_name);
        name.setText(user.getName());
    }

}
