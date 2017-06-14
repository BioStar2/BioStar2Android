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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.fragment.BaseFragment;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.sdk.utils.ImageUtil;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;

@SuppressLint("InflateParams")
public class DrawLayerMenuView extends LinearLayout {
    StyledTextView mAlarmCountView;
    int mDoorCount = -1;
    StyledTextView mDoorCountView;
    User mUser;
    int mUserCount = -1;
    StyledTextView mUserCountView;
    Context mContext;

    private OnSelectionListener mSelectionListener;
    OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            ScreenType menuType = null;
            switch (v.getId()) {
                case R.id.my_profile:
                    menuType = ScreenType.MYPROFILE;
                    break;
                case R.id.side_menu_user:
                    // {
                    // DeviceDataProvider mDeviceDataProvider =
                    // DeviceDataProvider.getInstance();
                    // mDeviceDataProvider.getDevices(DevicesDoorListener,
                    // null);
                    //
                    // }
                    // return;
                    menuType = ScreenType.USER;
                    break;
                case R.id.side_menu_door:
                    // {
                    // DeviceDataProvider mDeviceDataProvider =
                    // DeviceDataProvider.getInstance();
                    // mDeviceDataProvider.getDevices(DevicesFireListener,
                    // null);
                    // }
                    // return;
                    menuType = ScreenType.DOOR_LIST;
                    break;
                case R.id.side_menu_monitor:
                    menuType = ScreenType.MONITOR;
                    break;
                case R.id.side_menu_alram:
                    menuType = ScreenType.ALARM_LIST;
                    break;
                // case R.id.side_menu_ta:
                // menuType = ScreenType.TA;
                // break;
//                case R.id.side_menu_setting:
//                    menuType = ScreenType.PREFERENCE;
//                    if (mSelectionListener != null) {
//                        mSelectionListener.addScreen(menuType, null);
//                    }
//                    return;
                case R.id.side_menu_logout:
                    menuType = ScreenType.LOG_OUT;
                    break;
                case R.id.side_menu_card:
                    menuType = ScreenType.MOBILE_CARD_LIST;
                    break;
                case R.id.side_menu_version:
//                    PackageInfo pi = null;
//
//                    try {
//                        pi = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    Toast.makeText(mContext, mContext.getString(R.string.app_version) + " Build number: " + pi.versionCode, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
                // menuType = ScreenType.ACCESS_CONTROL;
            }

            if (mSelectionListener != null) {
                mSelectionListener.onSelected(menuType, null);
            }

        }
    };

    public DrawLayerMenuView(Context context) {
        super(context);
        loadView(context);
    }

    public DrawLayerMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadView(context);
    }

    public DrawLayerMenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        loadView(context);
    }

    public int getDoorCount() {
        return mDoorCount;
    }

    public void setDoorCount(int total) {
        setCountView(mDoorCountView, mDoorCount, total);
        mDoorCount = total;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        if (user == null) {
            return;
        }
        mUser = user;
        if (user.photo != null) {
            byte[] photoByte = Base64.decode(user.photo, 0);
            Bitmap bmp = ImageUtil.byteArrayToBitmap(photoByte);
            if (bmp != null) {
                Bitmap rBmp = ImageUtil.getRoundedBitmap(bmp, true);
                ImageView photo = (ImageView) findViewById(R.id.side_menu_user_photo);
                photo.setImageBitmap(rBmp);
            }
        }
        StyledTextView name = (StyledTextView) findViewById(R.id.side_menu_user_name);
        StyledTextView permission = (StyledTextView) findViewById(R.id.side_menu_user_permission);
        if (user.name != null) {
            name.setText(user.name);
        }
        if (user.permission != null) {
            permission.setText(user.permission.name);
        } else if (user.roles != null) {
            int size = user.roles.size();
            size--;
            if (size == 0) {
                permission.setText(user.roles.get(0).description);
            } else if (size > 0) {
                permission.setText(user.roles.get(size).description + " + " + size);
            }
        }
    }

    public int getUserCount() {
        return mUserCount;
    }

    public void setUserCount(int total) {
        setCountView(mUserCountView, mUserCount, total);
        mUserCount = total;
    }

    private void loadView(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout mainLayout = (LinearLayout) inflater.inflate(R.layout.view_drawlayer_menu, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addView(mainLayout, params);
        int[] ids = {R.id.side_menu_user, R.id.side_menu_door, R.id.side_menu_monitor, R.id.side_menu_alram, R.id.side_menu_logout, R.id.my_profile, R.id.side_menu_card, R.id.side_menu_version};
        for (int i : ids) {
            mainLayout.findViewById(i).setOnClickListener(mClickListener);
        }
        mUserCountView = (StyledTextView) findViewById(R.id.side_menu_user_count);
        mDoorCountView = (StyledTextView) findViewById(R.id.side_menu_door_count);
        mAlarmCountView = (StyledTextView) findViewById(R.id.side_menu_alarm_count);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void setAlarmCount(int total) {
        if (total < 0) {
            total = 0;
        }
        mAlarmCountView.setText(String.valueOf(total));
        if (total > 0) {
            mAlarmCountView.setBackgroundResource(R.drawable.list_new_btn);
        } else {
            mAlarmCountView.setBackgroundResource(R.drawable.list_normal_btn);
        }
        mAlarmCountView.invalidate();
    }

    private void setCountView(StyledTextView view, int oldValue, int newValue) {
        if (newValue < 0) {
            return;
        }
        if (oldValue != newValue) {
            view.setText(String.valueOf(newValue));
            if (oldValue < 0) {
                view.setBackgroundResource(R.drawable.list_normal_btn);
            } else {
                view.setBackgroundResource(R.drawable.list_new_btn);
            }
        }
    }

    public void setOnSelectionListener(OnSelectionListener mSelectionListener) {
        this.mSelectionListener = mSelectionListener;
    }

    public void showAlarmMenu(boolean visible) {
        if (visible) {
            findViewById(R.id.side_menu_alram).setVisibility(View.VISIBLE);
            findViewById(R.id.side_menu_alram_devider).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.side_menu_alram).setVisibility(View.GONE);
            findViewById(R.id.side_menu_alram_devider).setVisibility(View.GONE);
        }
    }

    public void showDoorMenu(boolean visible) {
        if (visible) {
            findViewById(R.id.side_menu_door).setVisibility(View.VISIBLE);
            findViewById(R.id.side_menu_door_devider).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.side_menu_door).setVisibility(View.GONE);
            findViewById(R.id.side_menu_door_devider).setVisibility(View.GONE);
        }
    }

    public void showMonitorMenu(boolean visible) {
        if (visible) {
            findViewById(R.id.side_menu_monitor).setVisibility(View.VISIBLE);
            findViewById(R.id.side_menu_monitor_devider).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.side_menu_monitor).setVisibility(View.GONE);
            findViewById(R.id.side_menu_monitor_devider).setVisibility(View.GONE);
        }
    }

    public void showUserMenu(boolean visible) {
        if (visible) {
            findViewById(R.id.side_menu_user).setVisibility(View.VISIBLE);
            findViewById(R.id.side_menu_user_devider).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.side_menu_user).setVisibility(View.GONE);
            findViewById(R.id.side_menu_user_devider).setVisibility(View.GONE);
        }
    }

    public void showMobileCard(boolean visible) {
        if (visible) {
            findViewById(R.id.side_menu_card).setVisibility(View.VISIBLE);
            findViewById(R.id.side_menu_card_devider).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.side_menu_card).setVisibility(View.GONE);
            findViewById(R.id.side_menu_card_devider).setVisibility(View.GONE);
        }
    }

    public interface OnSelectionListener {
        public void addScreen(ScreenType type, Bundle args);

        public void addScreenNoEffect(ScreenType type, Bundle args);

        public void backScreen();

        public void drawMenu();

        public void onResume(BaseFragment baseFragment);

        public void onSelected(ScreenType type, Bundle args);
    }
}
