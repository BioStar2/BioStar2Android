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
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.db.NotificationDBProvider;
import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.popup.ToastPopup;
import com.supremainc.biostar2.sdk.datatype.NotificationData.PushNotification;
import com.supremainc.biostar2.sdk.provider.CommonDataProvider;
import com.supremainc.biostar2.widget.ScreenControl;
import com.supremainc.biostar2.widget.StyledTextView;

import java.util.ArrayList;

public abstract class BaseListCursorAdapter<T> extends CursorAdapter implements OnItemClickListener {
    protected final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    protected Activity mContext;
    protected LayoutInflater mInflater;
    protected boolean mIsDestoy = false;
    protected ListView mListView;
    protected OnItemClickListener mOnItemClickListener;
    protected Popup mPopup;
    protected SwipyRefreshLayout mSwipyRefreshLayout;
    protected ToastPopup mToastPopup;
    protected int mTotal = 0;
    private NotificationDBProvider mDBProvider;
    private int mLastClickItemPosition = -1;

    protected OnCancelListener mCancelExitListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            CommonDataProvider.getInstance(mContext).cancelAll(TAG);
            ScreenControl.getInstance().backScreen();
        }
    };

    public BaseListCursorAdapter(Context context, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    public BaseListCursorAdapter(Activity context, ListView listView, Popup popup) {
        this(context, NotificationDBProvider.getInstance(context).getPushAlarmCursor());
        mDBProvider = NotificationDBProvider.getInstance(context);
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
    }

    public int getCheckedItemCount() {
        return mListView.getCheckedItemCount();
    }

    public void getCheckedItemIds(final OnGetCheckedItem onGetCheckedItemIds, final boolean showWait) {
        if (onGetCheckedItemIds == null) {
            return;
        }
        if (mPopup != null && showWait) {
            mPopup.showWait(false);
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                long start = System.currentTimeMillis();
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "start getCheckedItemIds");
                }
                Cursor cursor = getCursor();
                final ArrayList<Integer> selectedItem = new ArrayList<Integer>();
                if (mListView.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
                    int position = getCheckedItemPosition();
                    if (position != ListView.INVALID_POSITION) {
                        cursor.moveToPosition(position);
                        int id = cursor.getInt(0);
                        selectedItem.add(id);
                    }
                } else {
                    for (int i = 0; i < getCount(); i++) {
                        if (isItemChecked(i)) {
                            cursor.moveToPosition(i);
                            int id = cursor.getInt(0);
                            selectedItem.add(id);
                        }
                    }
                }
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "end getCheckedItemIds:" + ((System.currentTimeMillis() - start) / 1000));
                }
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mPopup != null && showWait) {
                            mPopup.dismissWiat();
                        }
                        onGetCheckedItemIds.onReceive(selectedItem);
                    }
                });
                return null;
            }
        }.execute(null, null, null);
    }

    public int getCheckedItemPosition() {
        return mListView.getCheckedItemPosition();
    }

    public void getCheckedItemPositions(final OnGetCheckedItem onGetCheckedItemIds) {
        if (onGetCheckedItemIds == null) {
            return;
        }
        if (mPopup != null) {
            mPopup.showWait(false);
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                long start = System.currentTimeMillis();
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "start getCheckedItemIds");
                }
                final ArrayList<Integer> selectedItem = new ArrayList<Integer>();
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
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "end getCheckedItemIds:" + ((System.currentTimeMillis() - start) / 1000) + "sec");
                }
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mPopup != null) {
                            mPopup.dismissWiat();
                        }
                        onGetCheckedItemIds.onReceive(selectedItem);
                    }
                });
                return null;
            }
        }.execute(null, null, null);

    }

    public ArrayList<T> getCheckedItems() {
        Cursor cursor = getCursor();
        ArrayList<T> selectedItem = new ArrayList<T>();
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
            int position = getCheckedItemPosition();
            if (position != ListView.INVALID_POSITION) {
                selectedItem.add((T) getItem(position));
            }
        } else {
            for (int i = 0; i < cursor.getCount(); i++) {
                if (isItemChecked(i)) {
                    selectedItem.add((T) getItem(i));
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
        Cursor cursor = getCursor();
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        Cursor cursor = getCursor();
        PushNotification item = null;
        if (cursor.moveToPosition(position)) {
            item = mDBProvider.get(cursor);
        }

        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onContentChanged() {
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

    public boolean isItemChecked(int position) {
        return mListView.isItemChecked(position);
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

    public void onRequry() {
        Cursor cursor = mDBProvider.getPushAlarmCursor();
        swapCursor(cursor).close();
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

    public interface OnGetCheckedItem {
        public void onReceive(ArrayList<Integer> selectedItem);
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
            mRoot.setBackgroundResource(R.drawable.selector_list_default_mode);
            if (mInfo != null) {
                mInfo.setImageResource(R.drawable.arrow_01);
                mInfo.setVisibility(View.VISIBLE);
            }
        }
    }
}
