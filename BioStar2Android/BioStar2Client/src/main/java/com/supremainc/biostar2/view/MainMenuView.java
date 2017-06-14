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
import android.widget.LinearLayout;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;

public class MainMenuView extends BaseView {
    public final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    private LinearLayout mFirstMenuGroup;
    private LinearLayout mSecondMenuGroup;
    private MenuItemView mAlarmView;
    private MainMenuViewListener mListener;
    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            MenuItemView.MenuItemType type = ((MenuItemView) v).getType();
            if (type != null && mListener != null) {
                switch (type) {
                    case USER:
                        mListener.onClickUser();
                        break;
                    case DOOR:
                        mListener.onClickDoor();
                        break;
                    case MONITORING:
                        mListener.onClickMonitor();
                        break;
                    case ALARM:
                        mListener.onClickAlarm();
                        break;
                    case MY_PROFILE:
                        mListener.onClickMyProfile();
                        break;
                    case MOBILE_CARD: // not miss
                    case MOBILE_CARD_ALERT:
                        mListener.onClickMobileCard();
                        break;
                    default:
                        break;
                }
            }
        }
    };

    public MainMenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public MainMenuView(Context context) {
        super(context);
        initView(context);
    }

    public MainMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mInflater.inflate(R.layout.view_main_menu, this, true);
        mFirstMenuGroup = (LinearLayout) findViewById(R.id.main_menu_first_line);
        mSecondMenuGroup = (LinearLayout) findViewById(R.id.main_menu_second_line);
    }

    public void init(MainMenuViewListener l) {
        mListener = l;
    }

    public void removeAllMenuItem() {
        if (mFirstMenuGroup != null) {
            mFirstMenuGroup.removeAllViews();
        }
        if (mSecondMenuGroup != null) {
            mSecondMenuGroup.removeAllViews();
        }
    }

    public void showMenuItem() {
        if (mSecondMenuGroup != null) {
            int count = mSecondMenuGroup.getChildCount();
            if (count == 1) {
                addEmptyMenu();
                addEmptyMenu();
            } else if (count == 2) {
                addEmptyMenu();
            }
        }
        invalidate();
    }

    private void addEmptyMenu() {
        MenuItemView item = new MenuItemView(mContext);
        item.init(MenuItemView.MenuItemType.EMPTY);
        item.setVisibility(View.INVISIBLE);
        mSecondMenuGroup.addView(item);
    }

    private boolean isExist(LinearLayout view, MenuItemView.MenuItemType type) {
        if (getExistView(view, type) != null) {
            return true;
        } else {
            return false;
        }
    }

    private View getExistView(LinearLayout view, MenuItemView.MenuItemType type) {
        int count = view.getChildCount();
        for (int i = 0; i < count; i++) {
            MenuItemView child = (MenuItemView) (view.getChildAt(i));
            if (child.getType() == type) {
                return child;
            }
        }
        return null;
    }

    public View getItemView(MenuItemView.MenuItemType type) {
        View view = getExistView(mFirstMenuGroup, type);
        if (view == null) {
            view = getExistView(mSecondMenuGroup, type);
        }
        return view;
    }

    public View createMenuView(MenuItemView.MenuItemType type) {
        MenuItemView item = new MenuItemView(mContext);
        item.init(type);
        return item;
    }

    public boolean addMenu(MenuItemView.MenuItemType type) {
        if (mFirstMenuGroup == null || mSecondMenuGroup == null) {
            return false;
        }
        if (isExist(mFirstMenuGroup, type)) {
            return false;
        }
        if (isExist(mSecondMenuGroup, type)) {
            return false;
        }

        boolean isAddFirstMenu;
        if (mFirstMenuGroup.getChildCount() > 2) {
            if (mSecondMenuGroup.getChildCount() > 2) {
                return false;
            }
            isAddFirstMenu = false;
        } else {
            isAddFirstMenu = true;
        }
        MenuItemView item = new MenuItemView(mContext);
        item.init(type);
        if (isAddFirstMenu) {
            mFirstMenuGroup.addView(item);
        } else {
            mSecondMenuGroup.addView(item);
        }
        item.setOnClickListener(mClickListener);
        if (type == MenuItemView.MenuItemType.ALARM) {
            mAlarmView = item;
        }
        return true;
    }

    public void setAlarmCount(int count) {
        if (mAlarmView == null) {
            return;
        }
        mAlarmView.setBadgeCount(count);
    }

    public interface MainMenuViewListener {
        public void onClickAlarm();

        public void onClickDoor();

        public void onClickMonitor();

        public void onClickUser();

        public void onClickMyProfile();

        public void onClickMobileCard();
    }
}
