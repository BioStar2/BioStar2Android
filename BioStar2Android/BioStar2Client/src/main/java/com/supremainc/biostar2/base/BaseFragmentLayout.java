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
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.sdk.provider.UserDataProvider;
import com.supremainc.biostar2.widget.StyledEditTextView;
import com.supremainc.biostar2.widget.StyledTextView;
import com.tekinarslan.material.sample.FloatingActionButton;

public class BaseFragmentLayout {
    private final String TAG = getClass().getSimpleName();
    protected Activity mActivity;
    protected Context mContext;
    protected LayoutInflater mLayoutInflater;
    protected BaseFragment mFragment;
    protected View mRootView;
    protected AppDataProvider mAppDataProvider;
    protected UserDataProvider mUserDataProvider;
    private boolean mIsReUsed = false;

    public BaseFragmentLayout(BaseFragment fragment) {
        init(fragment);
    }

    public FloatingActionButton getFab() {
        return (FloatingActionButton) mRootView.findViewById(R.id.fabButton);
    }

    public ListView getListView() {
        return (ListView) mRootView.findViewById(R.id.listview);
    }

    public View getRootView() {
        return mRootView;
    }

    public SwipyRefreshLayout getSwipeyLayout() {
        return (SwipyRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_layout);
    }

    protected Object getTag(int resID) {
        View view = mRootView.findViewById(resID);
        return getTag(view);
    }

    protected Object getTag(View view) {
        if (view != null) {
            return view.getTag();
        }
        return null;
    }

    public Toolbar getToolbar() {
        return (Toolbar) mRootView.findViewById(R.id.toolbar);
    }

    private void init(BaseFragment fragment) {
        if (mFragment == null) {
            mFragment = fragment;
        }
        if (mActivity == null) {
            mActivity = fragment.getActivity();
        }
        if (mContext == null) {
            mContext = mActivity.getApplicationContext();
        }
        if (mAppDataProvider == null) {
            mAppDataProvider = AppDataProvider.getInstance(mContext);
        }
        if (mUserDataProvider == null) {
            mUserDataProvider = UserDataProvider.getInstance(mContext);
        }
    }

    public boolean isReUsedView() {
        return mIsReUsed;
    }

    protected View initView(BaseFragment fragment, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int resID) {
        mLayoutInflater = inflater;
        init(fragment);
        if (mRootView != null) {
            mIsReUsed = true;
            ViewGroup rootView = (ViewGroup) mRootView.getParent();
            if (rootView != null) {
                rootView.removeView(mRootView);
            }
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "initView rootView again Used");
            }
        } else {
            mRootView = inflater.inflate(resID, container, false);
            mIsReUsed = false;
        }
        return mRootView;
    }

    public void onDestroy() {
        mActivity = null;
        mContext = null;
        mFragment = null;
        mRootView = null;
    }

    protected void setEditTextView(int resID, String content) {
        if (content != null) {
            StyledEditTextView view = ((StyledEditTextView) mRootView.findViewById(resID));
            if (view != null) {
                view.setText(content);
            }
        }
    }

    protected void setEditTextView(StyledEditTextView v, String content) {
        if (content != null && v != null) {
            v.setText(content);
        }
    }

    protected void setTag(int resID, Object tag) {
        View view = mRootView.findViewById(resID);
        setTag(view, tag);
    }

    protected void setTag(View view, Object tag) {
        if (view != null) {
            view.setTag(tag);
        }
    }

    protected void setTextView(int resID, String content) {
        if (content != null) {
            StyledTextView view = ((StyledTextView) mRootView.findViewById(resID));
            if (view != null) {
                view.setText(content);
            }
        }
    }

    protected void setTextView(StyledTextView v, String content) {
        if (content != null && v != null) {
            v.setText(content);
        }
    }
}