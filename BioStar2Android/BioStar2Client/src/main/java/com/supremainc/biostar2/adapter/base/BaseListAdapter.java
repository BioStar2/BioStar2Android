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
package com.supremainc.biostar2.adapter.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
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

import com.google.gson.Gson;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.sdk.provider.CommonDataProvider;
import com.supremainc.biostar2.widget.ScreenControl;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;
import com.supremainc.biostar2.widget.popup.ToastPopup;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseListAdapter<T> extends BaseAdapter implements OnItemClickListener {
    protected static Gson mGson = new Gson();
    protected final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    protected Activity mActivity;
    protected LayoutInflater mInflater;
    protected boolean mIsDestroy = false;
    protected ArrayList<T> mItems;
    protected ListView mListView;
    protected OnItemClickListener mOnItemClickListener;
    protected Popup mPopup;
    protected SwipyRefreshLayout mSwipyRefreshLayout;
    protected ToastPopup mToastPopup;
    protected int mTotal = 0;
    protected int mDefaultSelectColor = Color.WHITE;
    protected String mQuery;
    protected OnItemsListener mOnItemsListener;
    protected ArrayList<T> mDuplicateItems;
    protected int mLimitSize;
    protected Handler mHandler;
    protected CommonDataProvider mCommonDataProvider;
    protected OnPopupClickListener mOnBackPopupClickListener = new OnPopupClickListener() {
        @Override
        public void OnPositive() {
            ScreenControl.getInstance(mActivity).backScreen();
        }

        @Override
        public void OnNegative() {
            ScreenControl.getInstance(mActivity).backScreen();
        }
    };
    private ActivityManager mActivityManager;
    private int mLastClickItemPosition = -1;
    private Runtime mRuntime;
    private ArrayList<Call<?>> mRequestList = new ArrayList<Call<?>>();
    protected OnPopupClickListener mClearOnPopupClickListener = new OnPopupClickListener() {
        @Override
        public void OnPositive() {
            clearRequest();
            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(Setting.BROADCAST_CLEAR));
        }

        @Override
        public void OnNegative() {
            clearRequest();
            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(Setting.BROADCAST_CLEAR));
        }
    };
    protected OnCancelListener mCancelStayListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            if (mActivity != null && !mActivity.isFinishing()) {
                clearRequest();
            }
        }
    };

    public BaseListAdapter(Activity context, ArrayList<T> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener) {
        mHandler = new Handler(Looper.getMainLooper());
        this.mItems = items;
        mActivity = context;
        mListView = listView;
        mListView.setOnItemClickListener(this);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mToastPopup = new ToastPopup(context);
        mPopup = popup;
        listView.setAdapter(this);
        setOnItemClickListener(itemClickListener);
        mOnItemsListener = onItemsListener;
        mCommonDataProvider = CommonDataProvider.getInstance(mActivity);
    }

    protected boolean isWaitPopup() {
        if (mPopup == null) {
            return false;
        }
        if (mSwipyRefreshLayout == null) {
            return true;
        }
        return false;
    }

    public void clearRequest() {
        if (mRequestList != null) {
            Iterator<Call<?>> iterator = mRequestList.iterator();
            while (iterator.hasNext()) {
                Call<?> call = iterator.next();
                if (!(call.isCanceled() || call.isExecuted())) {
                    call.cancel();
                }
                iterator.remove();
            }
            mRequestList.clear();
        }
    }

    private void verifyRequest() {
        if (mRequestList != null) {
            Iterator<Call<?>> iterator = mRequestList.iterator();
            while (iterator.hasNext()) {
                Call<?> call = iterator.next();
                if (call.isCanceled() || call.isExecuted()) {
                    iterator.remove();
                }
            }
        }
    }

    protected boolean isInValidCheck() {
        if (mActivity == null) {
            return true;
        }
        if (mIsDestroy || mActivity.isFinishing()) {
            return true;
        }
        return false;
    }

    protected boolean isIgnoreCallback(Call<?> call, boolean dismissPopup) {
        if (isInValidCheck()) {
            return true;
        }
        if (dismissPopup) {
            dismissWait();
        }
        if (call != null && call.isCanceled()) {
            return true;
        }
        return false;
    }

    protected void showWait(SwipyRefreshLayoutDirection direction) {
        if (mSwipyRefreshLayout != null) {
//            mSwipyRefreshLayout.setRefreshing(false);
            mSwipyRefreshLayout.setEnableBottom(true);
            if (direction == null) {
                mSwipyRefreshLayout.setRefreshing(true);
            } else {
                mSwipyRefreshLayout.onRefresh(direction, false);
            }
        } else if (mPopup != null) {
            mPopup.showWait(mCancelStayListener);
        }
    }

    protected void dismissWait() {
        if (mSwipyRefreshLayout != null) {
            mSwipyRefreshLayout.setRefreshing(false);
        } else if (mPopup != null) {
            mPopup.dismiss();
        }
    }

    protected boolean isIgnoreCallback(Call<?> call, Response<?> response, boolean dismissPopup) {
        if (isInValidCheck()) {
            return true;
        }
        if (dismissPopup) {
            dismissWait();
        }
        if (call != null && call.isCanceled()) {
            return true;
        }
        if (response != null && !response.isSuccessful()) {
            if (response.code() == 401) {
                if (mCommonDataProvider != null) {
                    mCommonDataProvider.removeCookie();
                }
                mPopup.show(PopupType.ALERT, mActivity.getString(R.string.info), mActivity.getString(R.string.login_expire), mClearOnPopupClickListener, mActivity.getString(R.string.ok), null, false);
                return true;
            }
        }
        return false;
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
        clearRequest();
        mIsDestroy = true;
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

    protected boolean request(Call<?> call) {
        if (call == null || mRequestList == null) {
            return false;
        }
        verifyRequest();
        mRequestList.add(call);
        return true;
    }

    protected boolean isInvalidResponse(Response<?> response, boolean showError, boolean showErrorOnBack) {
        if (response == null) {
            if (showError) {
                showErrorPopup(getResponseErrorMessage(response), showErrorOnBack);
            }
            return true;
        }
        if (response.isSuccessful() && response.body() != null) {
            return false;
        } else {
            if (response.code() == 503) {
                mCommonDataProvider.simpleLogin(new Callback<User>() {
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }

                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(Setting.BROADCAST_REROGIN));
                    }
                });
            }
            if (showError) {
                showErrorPopup(getResponseErrorMessage(response), showErrorOnBack);
            }
            return true;
        }
    }

    protected String getResponseErrorMessage(Response<?> response) {
        if (response == null || response.errorBody() == null) {
            return mActivity.getString(R.string.fail) + "\nhcode:" + response.code();
        }
        String error = "";
        try {
            ResponseStatus responseClass = (ResponseStatus) mGson.fromJson(response.errorBody().string(), ResponseStatus.class);
            error = responseClass.message + "\n" + "scode: " + responseClass.status_code;
        } catch (Exception e) {

        }
        error = error + "\n" + "hcode: " + response.code();
        return error;
    }

    protected void showRetryPopup(String msg, OnPopupClickListener listener) {
        if (mPopup == null) {
            return;
        }
        mPopup.dismiss();
        mPopup.show(PopupType.ALERT, mActivity.getString(R.string.fail_retry), msg, listener, mActivity.getString(R.string.ok), mActivity.getString(R.string.cancel), false);
    }

    protected void showErrorPopup(String msg, boolean onBack) {
        if (mPopup == null) {
            return;
        }
        mPopup.dismiss();
        if (msg == null || msg.isEmpty()) {
            msg = mActivity.getString(R.string.fail);
        }

        if (msg.contains("scode: 10") || msg.contains("hcode: 401")) {
            if (mCommonDataProvider != null) {
                mCommonDataProvider.removeCookie();
            }
            mPopup.show(PopupType.ALERT, mActivity.getString(R.string.info), mActivity.getString(R.string.login_expire), mClearOnPopupClickListener, mActivity.getString(R.string.ok), null, false);
        } else {
            if (onBack) {
                mPopup.show(PopupType.ALERT, mActivity.getString(R.string.info), msg, mOnBackPopupClickListener, mActivity.getString(R.string.ok), null, true);
            } else {
                mPopup.show(PopupType.ALERT, mActivity.getString(R.string.info), msg, null, mActivity.getString(R.string.ok), null, true);
            }
        }
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


    public T getItemData(int position) {
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


    public boolean isItemChecked(int position) {
        return mListView.isItemChecked(position);
    }

    public boolean isMemoryPoor() {
        if (mRuntime == null) {
            mRuntime = Runtime.getRuntime();
        }
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) mActivity.getSystemService(Activity.ACTIVITY_SERVICE);
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
        int mode = mListView.getChoiceMode();
        boolean isRefresh = false;
        if (mLimitSize > 0 && mode != ListView.CHOICE_MODE_SINGLE) {
            int count = mListView.getCheckedItemCount();
            if (count > mLimitSize) {
                mListView.setItemChecked(position, false);
                return;
            }
            if (count == mLimitSize || count == mLimitSize - 1) {
                isRefresh = true;
            }
        }

        mLastClickItemPosition = position;
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(parent, view, position, id);
        }
        if (mode == ListView.CHOICE_MODE_SINGLE) {
            isRefresh = true;
        } else {
            view.invalidate();
        }
        if (isRefresh) {
            notifyDataSetChanged();
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

    public void setData(int position, T item) {
        if (mItems != null && mItems.size() > position) {
            mItems.set(position, item);
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

    protected void setSelector(View root, ImageView arrow, int position) {
        setSelector(root, arrow, position, true);
    }

    protected void setSelector(View root, ImageView arrow, int position, boolean enable) {
        if (root == null || arrow == null) {
            return;
        }
        int visible = arrow.getVisibility();
        if (enable) {
            root.setBackgroundResource(R.drawable.selector_list_default_mode);
            if (visible != View.VISIBLE) {
                arrow.setVisibility(View.VISIBLE);
            }
        } else {
            root.setBackgroundColor(mDefaultSelectColor);
            if (visible == View.VISIBLE) {
                arrow.setVisibility(View.GONE);
            }
        }
        int mode = mListView.getChoiceMode();
        switch (mode) {
            case ListView.CHOICE_MODE_NONE:
                arrow.setImageResource(R.drawable.arrow_01);
                break;
            default:
                arrow.setVisibility(View.VISIBLE);
                if (mListView.isItemChecked(position)) {
                    root.setBackgroundResource(R.drawable.selector_list_selected);
                    arrow.setImageResource(R.drawable.selector_list_check);
                } else {
                    root.setBackgroundResource(R.drawable.selector_list_select_mode);
                    if (mLimitSize > 0) {
                        if (mLimitSize <= mListView.getCheckedItemCount()) {
                            root.setBackgroundResource(R.drawable.selector_list_gray);
                        }
                    }
                    arrow.setImageResource(R.drawable.selector_color_transparent);
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

    public void setDuplicateItems(ArrayList<T> items) {
        mDuplicateItems = items;
    }

    public void setLimit(int limit) {
        mLimitSize = limit;
    }

    public interface OnItemsListener {
        public void onSuccessNull(int total);

        public void onNoneData();

        public void onTotalReceive(int total);
    }
}
