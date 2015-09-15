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

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.widget.OnSingleClickListener;
import com.supremainc.biostar2.widget.SearchViewEx;
import com.supremainc.biostar2.widget.StyledTextView;

import java.text.NumberFormat;

public class SubToolbar {
    public final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    public View mContainerView;
    public SearchViewEx mSearchViewEx;
    public ImageView mSelectAllView;
    public StyledTextView mSelectedView;
    public StyledTextView mTotalView;
    private Activity mContext;
    private InputMethodManager mImm;
    private boolean mIsAllViewGone;
    private SubToolBarEvent mSubToolBarEvent;
    private OnSingleClickListener mOnClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            if (mSubToolBarEvent != null) {
                mSubToolBarEvent.onClickSelectAll();
            }
        }
    };

    public SubToolbar(Activity context, View container, SubToolBarEvent event) {
        this(context, container, (ImageView) container.findViewById(R.id.all_select), (StyledTextView) container.findViewById(R.id.total), (StyledTextView) container.findViewById(R.id.selected)
                , (SearchViewEx) container.findViewById(R.id.searchbar));
        mSubToolBarEvent = event;
    }

    public SubToolbar(Activity context, View container, ImageView selectAll, StyledTextView total, StyledTextView selected, SearchViewEx searchViewEx) {
        mContext = context;
        mContainerView = container;
        mSelectAllView = selectAll;
        if (mSelectAllView != null) {
            mSelectAllView.setOnClickListener(mOnClickListener);
        }
        mTotalView = total;
        mSelectedView = selected;
        mSearchViewEx = searchViewEx;
        mImm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
        initSearchbar(mSearchViewEx);
    }

    public void hideIme() {
        if (mSearchViewEx != null && mImm != null) {
            mImm.hideSoftInputFromWindow(mSearchViewEx.getEditTextView().getWindowToken(), 0);
        }
    }

    private void initSearchbar(SearchViewEx searchView) {
        SearchManager searchManager = (SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE);
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(mContext.getComponentName()));
        }
        searchView.setIconifiedByDefault(true);
    }

    public void setSelectAllViewGone(boolean gone) {
        mIsAllViewGone = gone;
        setVisibleGone(mSelectAllView);
    }

    public void setSelectAllViewOff() {
        if (mSelectAllView.getTag() != null) {
            mSelectAllView.setImageResource(R.drawable.check_box_blank);
            mSelectAllView.setTag(null);
            mSelectAllView.invalidate();
        }
    }

    public void setTotal(int count) {
        if (mTotalView == null) {
            return;
        }
//        mTotalView.setText(mContext.getString(R.string.total) + ": " + NumberFormat.getInstance().format(count));
        mTotalView.setText(NumberFormat.getInstance().format(count));
    }

    public void setVisible(boolean isVisible) {
        if (mContainerView == null) {
            return;
        }
        if (isVisible) {
            mContainerView.setVisibility(View.VISIBLE);
        } else {
            mContainerView.setVisibility(View.GONE);
        }
    }

    private void setVisible(View view) {
        if (view == null) {
            return;
        }
        view.setVisibility(View.VISIBLE);
    }

    private void setVisibleGone(View view) {
        if (view == null) {
            return;
        }
        view.setVisibility(View.GONE);
    }

    public void setVisibleSearch(boolean show,SearchView.OnCloseListener listener) {
        if (show) {
            setVisible(mSearchViewEx);
            if (listener != null) {
                mSearchViewEx.setOnCloseListener(listener);
            }
        } else {
            setVisibleGone(mSearchViewEx);
        }
    }

    public boolean isExpandSearch() {
        if (mSearchViewEx == null || mSearchViewEx.getVisibility() != View.VISIBLE) {
            return false;
        }
        if (mSearchViewEx.isIconified()) {
            return false;
        }
        return true;
    }

    public void setSearchIconfied() {
        mSearchViewEx.setIconified(true);
    }

    public void setVisibleSelected(boolean show) {
        if (show) {
            setVisible(mSelectedView);
        } else {
            setVisibleGone(mSelectedView);
        }
    }

    public void showMultipleSelectInfo(boolean isVisible, int selectedCount) {
        if (isVisible) {
            if (!mIsAllViewGone) {
                setVisible(mSelectAllView);
            }
            setVisible(mSelectedView);
            if (selectedCount < 1) {
                setSelectAllViewOff();
            }
        } else {
            setVisibleGone(mSelectAllView);
            setVisibleGone(mSelectedView);
            setSelectAllViewOff();
        }
        setSelectedCount(selectedCount);
    }

    public boolean showReverseSelectAll() {
        Object isSelect = mSelectAllView.getTag();
        if (isSelect == null) {
            mSelectAllView.setImageResource(R.drawable.check_box);
            mSelectAllView.setTag("selectAll");
            mSelectAllView.invalidate();
            return true;
        } else {
            setSelectAllViewOff();
            return false;
        }
    }

    public void setSelectedCount(int selectedCount) {
        if (mSelectedView == null) {
            return;
        }
//        mSelectedView.setText(mContext.getString(R.string.selected_count) + ": " + selectedCount);
        mSelectedView.setText(selectedCount+ " / ");
        if (selectedCount < 1) {
            setSelectAllViewOff();
        }
    }

    public interface SubToolBarEvent {
        public void onClickSelectAll();
    }
}
