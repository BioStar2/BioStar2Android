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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;

import java.text.NumberFormat;

public class SubToolbar extends BaseView {
    public final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    public View mContainerView;
    public SearchViewEx mSearchViewEx;
    public ImageView mSelectAllView;
    public StyledTextView mSelectedView;
    public StyledTextView mTotalView;
    private InputMethodManager mImm;
    private boolean mIsAllViewGone;
    private SubToolBarListener mListener;

    private OnSingleClickListener mOnClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            if (mListener != null) {
                mListener.onClickSelectAll();
            }
        }
    };

    public SubToolbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public SubToolbar(Context context) {
        super(context);
        initView(context);
    }

    public SubToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        mContainerView = inflater.inflate(R.layout.view_sub_toolbar, this, true);
        mSelectAllView = (ImageView) findViewById(R.id.all_select);
        mSelectAllView.setOnClickListener(mOnClickListener);
        mTotalView = (StyledTextView) findViewById(R.id.total);
        mSelectedView = (StyledTextView) findViewById(R.id.selected);
        mSearchViewEx = (SearchViewEx) findViewById(R.id.searchbar);
    }

    public void init( Activity activity) {
        init(null,activity);
    }

    public void init(SubToolBarListener listener, Activity activity) {
        mListener = listener;
        mImm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
        initSearchbar(mSearchViewEx, activity);
    }

    private void initSearchbar(SearchViewEx searchView, Activity activity) {
        SearchManager searchManager = (SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE);
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        }
        searchView.setIconifiedByDefault(true);
    }

    public void hideIme() {
        if (mSearchViewEx != null && mImm != null) {
            mImm.hideSoftInputFromWindow(mSearchViewEx.getEditTextView().getWindowToken(), 0);
        }
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

    public void setVisibleSearch(boolean show, SearchView.OnCloseListener listener) {
        if (show) {
            setVisible(mSearchViewEx);
            if (listener != null) {
                mSearchViewEx.setOnCloseListener(listener);
            }
        } else {
            setVisibleGone(mSearchViewEx);
        }
    }

    public void showTotal(boolean isShow) {
        if (isShow) {
            mTotalView.setVisibility(View.VISIBLE);
            findViewById(R.id.selected).setVisibility(View.VISIBLE);
            findViewById(R.id.total_index).setVisibility(View.VISIBLE);
        } else {
            mTotalView.setVisibility(View.GONE);
            findViewById(R.id.selected).setVisibility(View.GONE);
            findViewById(R.id.total_index).setVisibility(View.GONE);
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
        mSelectedView.setText(selectedCount + " / ");
        if (selectedCount < 1) {
            setSelectAllViewOff();
        }
    }

    public interface SubToolBarListener {
        public void onClickSelectAll();
    }
}
