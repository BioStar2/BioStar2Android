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
package com.supremainc.biostar2.main;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.db.NotificationDBProvider;
import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.sdk.datatype.UserData.User;
import com.supremainc.biostar2.sdk.provider.UserDataProvider;
import com.supremainc.biostar2.widget.DrawLayerMenuView;

public class HomeActivityLayout {
    private final String TAG = getClass().getSimpleName();
    private Activity mActivity;
    private Context mContext;
    private AppDataProvider mAppDataProvider;
    private NotificationDBProvider mNotiProvider;
    private UserDataProvider mUserDataProvider;
    private DrawLayerMenuView mDrawLayerMenuView;
    private DrawerLayout mDrawerLayout;

    private DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View drawerView) {

        }

        @Override
        public void onDrawerClosed(View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {
            if (newState == DrawerLayout.STATE_DRAGGING) {
                mDrawLayerMenuView.setUserCount(mAppDataProvider.getUserCount());
                mDrawLayerMenuView.setDoorCount(mAppDataProvider.getDoorCount());
                mDrawLayerMenuView.setAlarmCount(mNotiProvider.getUnReadMessageCount());
            }
        }
    };

    public HomeActivityLayout(Activity activity) {
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mAppDataProvider = AppDataProvider.getInstance(mContext);
        mUserDataProvider = UserDataProvider.getInstance(mContext);
        mNotiProvider = NotificationDBProvider.getInstance(mContext);
    }

    public void closeDrawer() {
        if (mDrawerLayout.isDrawerOpen(mDrawLayerMenuView)) {
            mDrawerLayout.closeDrawer(mDrawLayerMenuView);
        }
    }

    public void initView(DrawLayerMenuView.OnSelectionListener drawMenuSelectionListener) {
        mActivity.setContentView(R.layout.activity_home);
        mDrawerLayout = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerListener);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawLayerMenuView = (DrawLayerMenuView) mActivity.findViewById(R.id.drawer_menu);
        mDrawLayerMenuView.setUser(mUserDataProvider.getLoginUserInfo());
        mDrawLayerMenuView.setOnSelectionListener(drawMenuSelectionListener);
        mDrawLayerMenuView.setUserCount(mAppDataProvider.getUserCount());
        mDrawLayerMenuView.setDoorCount(mAppDataProvider.getDoorCount());
        mDrawLayerMenuView.setAlarmCount(mNotiProvider.getUnReadMessageCount());
    }

    public void onDestroy() {
        mActivity = null;
        mContext = null;
    }

    public void onDrawMenu() {
        if (mDrawerLayout.isDrawerOpen(mDrawLayerMenuView)) {
            mDrawerLayout.closeDrawer(mDrawLayerMenuView);
        } else {
            mDrawLayerMenuView.setAlarmCount(mNotiProvider.getUnReadMessageCount());
            mDrawerLayout.openDrawer(mDrawLayerMenuView);
        }
    }

    public void setAlarmCount() {
        mDrawLayerMenuView.setAlarmCount(mNotiProvider.getUnReadMessageCount());
    }

    public void setDoorCount(int total) {
        mDrawLayerMenuView.setDoorCount(total);
    }

    public void setUser(User user) {
        mDrawLayerMenuView.setUser(user);
    }

    public void setUserCount(int total) {
        mDrawLayerMenuView.setUserCount(total);
    }

    public void showAlarmMenu(boolean visible) {
        if (visible) {
            mActivity.findViewById(R.id.side_menu_alram).setVisibility(View.VISIBLE);
            mActivity.findViewById(R.id.side_menu_alram_devider).setVisibility(View.VISIBLE);
        } else {
            mActivity.findViewById(R.id.side_menu_alram).setVisibility(View.GONE);
            mActivity.findViewById(R.id.side_menu_alram_devider).setVisibility(View.GONE);
        }
    }

    public void showDoorMenu(boolean visible) {
        if (visible) {
            mActivity.findViewById(R.id.side_menu_door).setVisibility(View.VISIBLE);
            mActivity.findViewById(R.id.side_menu_door_devider).setVisibility(View.VISIBLE);
        } else {
            mActivity.findViewById(R.id.side_menu_door).setVisibility(View.GONE);
            mActivity.findViewById(R.id.side_menu_door_devider).setVisibility(View.GONE);
        }
    }

    public void showMonitorMenu(boolean visible) {
        if (visible) {
            mActivity.findViewById(R.id.side_menu_monitor).setVisibility(View.VISIBLE);
            mActivity.findViewById(R.id.side_menu_monitor_devider).setVisibility(View.VISIBLE);
        } else {
            mActivity.findViewById(R.id.side_menu_monitor).setVisibility(View.GONE);
            mActivity.findViewById(R.id.side_menu_monitor_devider).setVisibility(View.GONE);
        }
    }

    public void showUserMenu(boolean visible) {
        if (visible) {
            mActivity.findViewById(R.id.side_menu_user).setVisibility(View.VISIBLE);
            mActivity.findViewById(R.id.side_menu_user_devider).setVisibility(View.VISIBLE);
        } else {
            mActivity.findViewById(R.id.side_menu_user).setVisibility(View.GONE);
            mActivity.findViewById(R.id.side_menu_user_devider).setVisibility(View.GONE);
        }
    }
}