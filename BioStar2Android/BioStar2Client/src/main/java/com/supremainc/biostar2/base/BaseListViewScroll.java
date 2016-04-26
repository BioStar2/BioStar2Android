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

import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.widget.OnSingleClickListener;
import com.tekinarslan.material.sample.FloatingActionButton;

public class BaseListViewScroll implements OnScrollListener {
    protected BaseListAdapter mBaseListAdapter;
    protected BaseListCursorAdapter mBaseListCursorAdapter;
    protected boolean mClickFab;
    protected FloatingActionButton mFab;
    protected boolean mIsDirectionUp;
    protected boolean mIsLastItemVisible = false;
    protected int mLastlY;
    protected ListView mListView;
    protected int mOldFirstVisibleItem;
    protected boolean mSkipScroll;
    protected int mVisibleItemCount;
    Runnable mScrollBottomEnd = new Runnable() {
        @Override
        public void run() {
            if (mFab == null && mListView == null) {
                return;
            }
            mListView.setSelection(mListView.getCount() - 1);
            mClickFab = false;
            if (mBaseListAdapter != null) {
                int currentTotal = mBaseListAdapter.getCount();
                int nextTotal = mBaseListAdapter.getTotal();
                if (currentTotal >= nextTotal) {
                    setDirectioUp();
                    mBaseListAdapter.setRefresh(false);
                }
            }
            if (mBaseListCursorAdapter != null) {
                int currentTotal = mBaseListCursorAdapter.getCount();
                int nextTotal = mBaseListCursorAdapter.getTotal();
                if (currentTotal >= nextTotal) {
                    setDirectioUp();
                    mBaseListCursorAdapter.setRefresh(false);
                }
            }

        }
    };
    Runnable mScrollBottom = new Runnable() {
        @Override
        public void run() {
            if (mFab == null && mListView == null) {
                return;
            }
            if (mBaseListAdapter == null && mBaseListCursorAdapter == null) {
                return;
            }

            mSkipScroll = true;
            mListView.smoothScrollToPosition(mListView.getCount() - 1);
            mListView.postDelayed(mScrollBottomEnd, 500);

            if (mBaseListAdapter != null) {
                int currentTotal = mBaseListAdapter.getCount();
                int nextTotal = mBaseListAdapter.getTotal();
                if (currentTotal >= nextTotal) {
                    mBaseListAdapter.setRefresh(false);
                } else {
                    mBaseListAdapter.onRefresh(SwipyRefreshLayoutDirection.BOTTOM);
                }
            }

            if (mBaseListCursorAdapter != null) {
                int currentTotal = mBaseListCursorAdapter.getCount();
                int nextTotal = mBaseListCursorAdapter.getTotal();
                if (currentTotal >= nextTotal) {
                    mBaseListCursorAdapter.setRefresh(false);
                } else {
                    mBaseListCursorAdapter.onRefresh(SwipyRefreshLayoutDirection.BOTTOM);
                }
            }
        }
    };
    Runnable mScrollTopEnd = new Runnable() {
        @Override
        public void run() {
            if (mFab == null && mListView == null) {
                return;
            }
            mListView.setSelection(0);
            mClickFab = false;
            setDirectioDown();
        }
    };
    Runnable mScrollTop = new Runnable() {
        @Override
        public void run() {
            if (mFab == null && mListView == null) {
                return;
            }
            mSkipScroll = true;
            mListView.smoothScrollToPosition(0);
            mListView.postDelayed(mScrollTopEnd, 500);
        }
    };
    private OnSingleClickListener mFabClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {

            if (mBaseListAdapter == null && mBaseListCursorAdapter == null || mFab == null || mListView == null) {
                return;
            }
            if (mListView.getCount() < mVisibleItemCount + 2 && mOldFirstVisibleItem == 0) {
                return;
            }

            if (mClickFab) {
                return;
            }
            mClickFab = true;
            Log.e("onClick", "mFab.getDirectionUp()" + mFab.getDirectionUp());
            if (mFab.getDirectionUp()) {
                mListView.post(mScrollTop);
            } else {
                mListView.post(mScrollBottom);
            }
        }
    };

    public void autoClick() {
        if (Setting.IS_AUTO_LOG_SCROLL) {
            mFabClickListener.onClick(null);
        }
    }

    public int getmOldFirstVisibleItemPosition() {
        return mOldFirstVisibleItem;
    }

    private int getTopItemScrollY() {
        if (mListView == null || mListView.getChildAt(0) == null)
            return 0;
        View topChild = mListView.getChildAt(0);
        return topChild.getTop();
    }

    private boolean isSameItem(int firstVisibleItem) {
        return firstVisibleItem == mOldFirstVisibleItem;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            if (mOldFirstVisibleItem == 0) {
                setDirectioDown();
                return;
            }
            if (mIsLastItemVisible) {
                if (mBaseListAdapter != null) {
                    mBaseListAdapter.setRefresh(false);
                }
                if (mBaseListCursorAdapter != null) {
                    mBaseListCursorAdapter.setRefresh(false);
                }
                setDirectioUp();
                return;
            }

        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount == 0) {
            return;
        }
        mVisibleItemCount = visibleItemCount;
        if (mBaseListAdapter != null) {
            mIsLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= mBaseListAdapter.getTotal());
        }
        if (mBaseListCursorAdapter != null) {
            mIsLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= mBaseListCursorAdapter.getTotal());
        }

        if (mSkipScroll) {
            mSkipScroll = false;
            return;
        }

        if (firstVisibleItem == 0) {
            mOldFirstVisibleItem = firstVisibleItem;
            setDirectioDown();
            return;
        }

        if (mIsLastItemVisible) {
            mOldFirstVisibleItem = firstVisibleItem;
            setDirectioUp();
            return;
        }

        if (isSameItem(firstVisibleItem)) {
            int newY = getTopItemScrollY();
            if (Math.abs(mLastlY - newY) > 12) {
                if (mLastlY > newY) {
                    setDirectioDown();
                } else {
                    setDirectioUp();
                }
            }
            mLastlY = newY;
        } else {
            if (firstVisibleItem > mOldFirstVisibleItem) {
                setDirectioDown();
            } else {
                setDirectioUp();

            }
            mLastlY = getTopItemScrollY();
            mOldFirstVisibleItem = firstVisibleItem;
        }
    }

    private void setDirectioDown() {
        if (!mIsDirectionUp) {
            return;
        }
        mIsDirectionUp = false;
        if (mFab != null) {
            mFab.setDirectioDown();
        }
    }

    private void setDirectioUp() {
        if (mIsDirectionUp) {
            return;
        }
        mIsDirectionUp = true;
        if (mFab != null) {
            mFab.setDirectioUp();
        }
    }

    public void setFloatingActionButton(FloatingActionButton fab, ListView listView, BaseListAdapter baseListAdapter) {
        if (fab != null) {
            mFab = fab;
            mFab.setOnClickListener(mFabClickListener);
        }
        mListView = listView;
        mBaseListAdapter = baseListAdapter;
    }

    public void setFloatingActionButton(FloatingActionButton fab, ListView listView, BaseListCursorAdapter baseListCursorAdapter) {
        if (fab != null) {
            mFab = fab;
            mFab.setOnClickListener(mFabClickListener);
        }
        mListView = listView;
        mBaseListCursorAdapter = baseListCursorAdapter;
    }

}