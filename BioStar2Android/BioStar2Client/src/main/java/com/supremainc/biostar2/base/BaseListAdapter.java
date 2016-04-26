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
package com.supremainc.biostar2.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.popup.ToastPopup;
import com.supremainc.biostar2.sdk.provider.CommonDataProvider;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.widget.ScreenControl;
import com.supremainc.biostar2.widget.StyledTextView;

import java.util.ArrayList;

public abstract class BaseListAdapter<T> extends BaseAdapter implements OnItemClickListener {
    protected final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    protected Activity mContext;
    protected LayoutInflater mInflater;
    protected boolean mIsDestoy = false;
    protected ArrayList<T> mItems;
    protected ListView mListView;
    protected OnItemClickListener mOnItemClickListener;
    protected Popup mPopup;
    protected SwipyRefreshLayout mSwipyRefreshLayout;
    protected ToastPopup mToastPopup;
    protected int mTotal = 0;
    private ActivityManager mActivityManager;
    private int mLastClickItemPosition = -1;
    private Runtime mRuntime;

    protected OnCancelListener mCancelExitListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            CommonDataProvider.getInstance(mContext).cancelAll(TAG);
            ScreenControl.getInstance().backScreen();
        }
    };

    public BaseListAdapter(Activity context, ArrayList<T> items, ListView listView, Popup popup) {
        this.mItems = items;
        mContext = context;
        mListView = listView;
        mListView.setOnItemClickListener(this);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mToastPopup = new ToastPopup(context);
        mPopup = popup;
    }

    public void cacelRequest() {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "cancel SelectPopup Request");
        }
        CommonDataProvider.getInstance(mContext).cancelAll(TAG);
    }

    public boolean clearChoices() {
        if (mListView == null) {
            return false;
        }
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
            notifyDataSetChanged();
            return false;
        }
        mListView.clearChoices();
        notifyDataSetChanged();
        return true;
    }

    public void clearItems() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "clearItems");
        }
        mIsDestoy = true;
        if (mItems != null) {
            mItems.clear();
        }
    }

    public int getCheckedItemCount() {
        return mListView.getCheckedItemCount();
    }

    public int getCheckedItemPosition() {
        return mListView.getCheckedItemPosition();
    }

    public ArrayList<Integer> getCheckedItemPositions() {
        if (mItems == null || mItems.size() < 1) {
            return null;
        }
        ArrayList<Integer> selectedItem = new ArrayList<Integer>();
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
            int position = getCheckedItemPosition();
            if (position != ListView.INVALID_POSITION) {
                selectedItem.add(position);
            }
        } else {
            for (int i = 0; i < getCount(); i++) {
                if (isItemChecked(i)) {
                    selectedItem.add(i);
                }
            }
        }
        if (selectedItem.size() < 1) {
            return null;
        }
        return selectedItem;
    }

    public ArrayList<T> getCheckedItems() {
        if (mItems == null || mItems.size() < 1) {
            return null;
        }
        ArrayList<T> selectedItem = new ArrayList<T>();
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
            int position = getCheckedItemPosition();
            if (position != ListView.INVALID_POSITION) {
                selectedItem.add(mItems.get(position));
            }
        } else {
            for (int i = 0; i < mItems.size(); i++) {
                if (isItemChecked(i)) {
                    selectedItem.add(mItems.get(i));
                }
            }
        }
        if (selectedItem.size() < 1) {
            return null;
        }
        return selectedItem;
    }

    public int getChoiceMode() {
        return mListView.getChoiceMode();
    }

    public void setChoiceMode(int choiceMode) {
        switch (choiceMode) {
            case ListView.CHOICE_MODE_MULTIPLE:
            case ListView.CHOICE_MODE_MULTIPLE_MODAL:
                mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                mListView.clearChoices();
                break;
            case ListView.CHOICE_MODE_NONE:
                mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                mListView.clearChoices();
                break;
            case ListView.CHOICE_MODE_SINGLE:
                mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                mListView.clearChoices();
                break;
            default:
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "not support setChoiceMode");
                }
                break;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mItems == null) {
            return 0;
        }
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        if (mItems == null || mItems.size() - 1 < position) {
            return null;
        }
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void getItems(String query) {

    }

    public int getLastClickItemPosition() {
        return mLastClickItemPosition;
    }

    public int getTotal() {
        if (mTotal < 1) {
            return getCount();
        }
        return mTotal;
    }

    protected ItemViewHolder getViewHolder(int position, View convertView, ViewGroup parent, int layoutId) {
        if (mItems == null || mItems.size() < 1) {
            return null;
        }
        if (null == convertView) {
            convertView = mInflater.inflate(layoutId, parent, false);
            ItemViewHolder viewHolder = new ItemViewHolder(convertView, R.id.name, R.id.info);
            convertView.setTag(viewHolder);
        }
        @SuppressWarnings("unchecked")
        ItemViewHolder viewHolder = (ItemViewHolder) convertView.getTag();
        setSelector(viewHolder, position);
        return viewHolder;
    }

    public boolean isDestroy() {
        if (mContext.isFinishing()) {
            return true;
        }
        return mIsDestoy;
    }

    public boolean isDestroy(VolleyError error) {
        if (isDestroy()) {
            return true;
        }
        if (error.getSessionExpire()) {
            if (mPopup == null) {
                mPopup = new Popup(mContext);
            } else {
                mPopup.dismiss();
            }
            mPopup.show(PopupType.ALERT, mContext.getString(R.string.info), mContext.getString(R.string.login_expire), new OnPopupClickListener() {
                @Override
                public void OnNegative() {

                }

                @Override
                public void OnPositive() {
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Setting.BROADCAST_CLEAR));
                }
            }, mContext.getString(R.string.ok), null, false);
            return true;
        }
        return false;
    }

    public boolean isItemChecked(int position) {
        return mListView.isItemChecked(position);
    }

    public boolean isMemoryPoor() {
        if (mRuntime == null) {
            mRuntime = Runtime.getRuntime();
        }
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) mContext.getSystemService(Activity.ACTIVITY_SERVICE);
        }
        int large = mActivityManager.getLargeMemoryClass();
        long freeSize = 0L;
        long totalSize = 0L;
        long usedSize = 0L;
        freeSize = mRuntime.freeMemory();
        totalSize = mRuntime.totalMemory();
        usedSize = totalSize - freeSize;
        usedSize = usedSize / 1024 / 1024;
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "usedSize:" + usedSize + " large:" + large);
        }
        if (large - usedSize < 30) {
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mLastClickItemPosition = position;
        setSelector(view.getTag(), position);
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(parent, view, position, id);
        }
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
            notifyDataSetChanged();
        } else {
            view.invalidate();
        }
    }

    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (mSwipyRefreshLayout != null) {
            mSwipyRefreshLayout.onRefresh(direction, true);
        }
    }

    public boolean selectChoices() {
        if (mListView == null) {
            return false;
        }
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
            return false;
        }
        for (int i = 0; i < getCount(); i++) {
            mListView.setItemChecked(i, true);
        }
        notifyDataSetChanged();
        return true;
        // mListView.invalidate();
    }

    public void setData(ArrayList<T> item) {
        mItems = item;
        notifyDataSetChanged();
    }

    public void setData(int position,T item) {
        if (mItems != null && mItems.size() > position) {
            mItems.set(position,item);
            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mListView.setOnScrollListener(onScrollListener);
    }

    public void setRefresh(boolean b) {
        if (mSwipyRefreshLayout != null) {
            mSwipyRefreshLayout.setRefreshing(b);
        }
    }

    protected void setSelector(Object viewHolderObject, int position) {
        @SuppressWarnings("unchecked")
        ItemViewHolder viewHolder = (ItemViewHolder) viewHolderObject;
        int mode = mListView.getChoiceMode();
        switch (mode) {
            case ListView.CHOICE_MODE_NONE:
                viewHolder.mRoot.setBackgroundResource(R.drawable.selector_list_default_mode);
                if (viewHolder.mInfo != null) {
                    viewHolder.mInfo.setImageResource(R.drawable.arrow_01);
                }
                break;
            default:
                if (mListView.isItemChecked(position)) {
                    viewHolder.mRoot.setBackgroundResource(R.drawable.selector_list_selected);
                    if (viewHolder.mInfo != null) {
                        viewHolder.mInfo.setImageResource(R.drawable.selector_list_check);
                    }
                } else {
                    viewHolder.mRoot.setBackgroundResource(R.drawable.selector_list_select_mode);
                    if (viewHolder.mInfo != null) {
                        viewHolder.mInfo.setImageResource(R.drawable.selector_color_transparent);
                    }
                }
                break;
        }
    }

    public void setSize(int height) {
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = height;
        mListView.setLayoutParams(params);
        // mListView.requestLayout();
    }

    public interface OnItemsListener {
        public void onSuccessNull();

        public void onTotalReceive(int total);
    }

    public class ItemViewHolder {
        public Object mExtend;
        public ImageView mInfo;
        public StyledTextView mName;
        public View mRoot;

        public ItemViewHolder(View root, int name, int right) {
            mRoot = root;
            mName = (StyledTextView) root.findViewById(name);
            mInfo = (ImageView) root.findViewById(right);
        }
    }
}
