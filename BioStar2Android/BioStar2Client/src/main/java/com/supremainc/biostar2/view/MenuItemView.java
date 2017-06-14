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
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.supremainc.biostar2.R;

public class MenuItemView extends BaseView {
    public final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    private ImageView mIcon;
    private StyledTextView mText;
    private StyledTextView mBadge;
    private MenuItemType mType;
    private ImageView mBadgeAlert;

    public MenuItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public MenuItemView(Context context) {
        super(context);
        initView(context);
    }

    public MenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mInflater.inflate(R.layout.view_menu_item, this, true);
        mIcon = (ImageView) findViewById(R.id.item_image);
        mText = (StyledTextView) findViewById(R.id.item_text);
        mBadge = (StyledTextView) findViewById(R.id.item_badge);
        mBadgeAlert = (ImageView) findViewById(R.id.item_badge_alert);
        setWight();
    }

    public boolean init(MenuItemType type) {
        int imgResID = 0;
        int textID = 0;
        mType = type;
        mBadge.setVisibility(View.GONE);
        mBadgeAlert.setVisibility(View.GONE);
        switch (type) {
            case USER:
                imgResID = R.drawable.selector_btn_user;
                textID = R.string.user_upper;
                break;
            case MONITORING:
                imgResID = R.drawable.selector_btn_monitor;
                textID = R.string.monitoring_upper;
                break;
            case ALARM:
                textID = R.string.alarm_upper;
                imgResID = R.drawable.selector_btn_alarm;
                break;
            case MY_PROFILE:
                imgResID = R.drawable.selector_btn_myprofile;
                textID = R.string.myprofile_upper;
                break;
            case DOOR:
                imgResID = R.drawable.selector_btn_door;
                textID = R.string.door_upper;
                break;
            case MOBILE_CARD:
                imgResID = R.drawable.selector_btn_card;
                textID = R.string.mobile_card_upper;
                break;
            case MOBILE_CARD_ALERT:
                imgResID = R.drawable.selector_btn_card;
                textID = R.string.mobile_card_upper;
                mBadgeAlert.setVisibility(View.VISIBLE);
                break;
            default:
                return false;
        }

        mIcon.setImageResource(imgResID);
        mText.setText(mContext.getString(textID));
        return true;
    }

    public void setWight() {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.weight = 1;
        param.width = 0;
        setLayoutParams(param);
    }

    public MenuItemType getType() {
        return mType;
    }

    public void setBadgeCount(int count) {
        if (count < 1) {
            mBadge.setVisibility(View.GONE);
            return;
        }
        String content;
        if (count > 999) {
            content = "999+";
        } else {
            content = String.valueOf(count);
        }
        mBadge.setText(content);
        mBadge.setVisibility(View.VISIBLE);
        mBadge.invalidate();
    }

    public enum MenuItemType {
        USER, MONITORING, ALARM, MY_PROFILE, DOOR, MOBILE_CARD, MOBILE_CARD_ALERT, EMPTY
    }
}
