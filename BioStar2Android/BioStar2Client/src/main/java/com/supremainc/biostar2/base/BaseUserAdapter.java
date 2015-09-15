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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.popup.Popup;
import com.supremainc.biostar2.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.sdk.datatype.UserData.ListUser;
import com.supremainc.biostar2.sdk.datatype.UserData.Users;
import com.supremainc.biostar2.sdk.provider.UserDataProvider;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.widget.StyledTextView;
import com.tekinarslan.material.sample.FloatingActionButton;

import java.io.InputStream;
import java.util.ArrayList;

public class BaseUserAdapter extends BaseListAdapter<ListUser> {
    protected static final int FIRST_LIMIT = 25;
    protected static final int SECOND_LIMIT = 50;
    protected boolean mHasPhoto;
    protected boolean mIsLastItemVisible = false;
    protected int mLimit = FIRST_LIMIT;
    protected int mOffset = 0;
    protected OnItemsListener mOnItemsListener;
    protected String mQuery;
    protected UserDataProvider mUserDataProvider;
    private String mLastModify;
    private OkHttpUrlLoader.Factory mFactory;
    private String mSubDomain;
    private String mGroupId = "1";
    Listener<Users> mUsersListener = new Response.Listener<Users>() {
        @Override
        public void onResponse(Users response, Object deliverParam) {
            if (isDestroy()) {
                return;
            }
            mPopup.dismiss();
            if (mSwipyRefreshLayout != null) {
                mSwipyRefreshLayout.setRefreshing(false);
            }
            onUserListener(response, deliverParam);
        }
    };
    Response.ErrorListener mUsersErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isDestroy(error)) {
                return;
            }
            mPopup.dismiss();
            if (mSwipyRefreshLayout != null) {
                mSwipyRefreshLayout.setRefreshing(false);
            }
            mPopup.show(PopupType.ALERT, mContext.getString(R.string.fail_retry), Setting.getErrorMessage(error, mContext), new OnPopupClickListener() {
                @Override
                public void OnNegative() {
                }

                @Override
                public void OnPositive() {
                    if (mSwipyRefreshLayout != null) {
                        mSwipyRefreshLayout.setRefreshing(true);
                    } else {
                        mPopup.showWait(mCancelExitListener);
                    }
                    mListView.removeCallbacks(mRunGetItems);
                    mListView.post(mRunGetItems);
                }
            }, mContext.getString(R.string.ok), mContext.getString(R.string.cancel), false);
        }
    };

    Runnable mRunGetItems = new Runnable() {
        @Override
        public void run() {
            if (isMemoryPoor()) {
                mPopup.dismiss();
                if (mSwipyRefreshLayout != null) {
                    mSwipyRefreshLayout.setRefreshing(false);
                }
                mToastPopup.show(mContext.getString(R.string.memory_poor), null);
                return;
            }
            mUserDataProvider.getUsers(TAG, mUsersListener, mUsersErrorListener, mOffset, mLimit, mGroupId, mQuery, null);
        }
    };

    public BaseUserAdapter(Activity context, ArrayList<ListUser> items, ListView listView, OnItemClickListener onItemClickListener, Popup popup, OnItemsListener onUsersListener) {
        super(context, items, listView, popup);
        listView.setAdapter(this);
        mSubDomain = AppDataProvider.getInstance(context).getLatestDomain();
        mUserDataProvider = UserDataProvider.getInstance();
        mOnItemsListener = onUsersListener;
        setOnItemClickListener(onItemClickListener);
        setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && mIsLastItemVisible && mTotal - 1 > mOffset) {
                    mPopup.showWait(true);
                    mListView.removeCallbacks(mRunGetItems);
                    mListView.postDelayed(mRunGetItems, 100);
                } else {
                    mPopup.dismissWiat();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mIsLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });
        if (mFactory == null) {
            mFactory = new OkHttpUrlLoader.Factory(mUserDataProvider.getRequestQueue().getOkHttpClient());
            Glide.get(mContext).register(GlideUrl.class, InputStream.class,
                    mFactory);
        }
    }

    public void clearItems() {
        if (mUserDataProvider != null) {
            mUserDataProvider.cancelAll(TAG);
        }
        super.clearItems();
    }

    @Override
    public void getItems(String query) {
        mQuery = query;
        mLimit = FIRST_LIMIT;
        mListView.removeCallbacks(mRunGetItems);
        mUserDataProvider.cancelAll(TAG);
        mLastModify = String.valueOf(System.currentTimeMillis());
        mOffset = 0;
        mTotal = 0;
        if (mItems != null) {
            mItems.clear();
            notifyDataSetChanged();
        }
        if (mSwipyRefreshLayout != null) {
            mSwipyRefreshLayout.setRefreshing(false);
            mSwipyRefreshLayout.setEnableBottom(true);
            mSwipyRefreshLayout.onRefresh(SwipyRefreshLayoutDirection.TOP, false);
        } else {
            mPopup.showWait(mCancelExitListener);
        }
        mListView.removeCallbacks(mRunGetItems);
        mListView.postDelayed(mRunGetItems, 500);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
    }

    private View getNameView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder viewHolder = getViewHolder(position, convertView, parent, R.layout.list_item);
        if (viewHolder == null) {
            return null;
        }
        convertView = viewHolder.mRoot;
        ListUser item = mItems.get(position);
        if (item != null) {
            if (item.name == null || item.name.equals("")) {
                viewHolder.mName.setText(item.user_id + " / " + item.user_id);
            } else {
                viewHolder.mName.setText(item.user_id + " / " + item.name);
            }

        }
        return convertView;
    }

    private void getPhoto(ListUser user, ImageView picture) {
        String lastModify = null;
        if (user.last_modify == null) {
            if (mLastModify == null) {
                mLastModify = String.valueOf(System.currentTimeMillis());
            }
            lastModify = mLastModify;
        } else {
            lastModify = user.last_modify;
        }
        getUserPhoto(mContext, user.user_id, picture, R.drawable.user_06, Setting.USER_PROFILE_IMAGE_SIZE, lastModify);
    }

    public void getUserPhoto(Activity activity,String userID,ImageView view,int defaultResID,int maxSize,String lastModify) {
        if (userID == null || userID.isEmpty()) {
            return;
        }
        if (mFactory == null) {
            mFactory = new OkHttpUrlLoader.Factory(mUserDataProvider.getRequestQueue().getOkHttpClient());
            Glide.get(mContext).register(GlideUrl.class, InputStream.class,
                    mFactory);
        }
        String url = mUserDataProvider.getUserPhotoUrl(userID);
        new GlidePhotoData(activity,view,defaultResID,lastModify,maxSize,mSubDomain,url);
    }

    private View getPhotoView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder viewHolder = getViewHolder(position, convertView, parent, R.layout.list_item_user);

        if (viewHolder == null) {
            return null;
        }
        if (convertView == null) {
            viewHolder.mExtend = new ExtraViewHolder(viewHolder.mRoot, R.id.user_picture, R.id.user_name, R.id.user_date, R.id.user_finger, R.id.user_card, R.id.user_ic_pin);
        }
        convertView = viewHolder.mRoot;
        ExtraViewHolder vh = (ExtraViewHolder) viewHolder.mExtend;
        if (vh == null) {
            return null;
        }
        ListUser user = mItems.get(position);
        if (user != null) {
            ImageView picture = vh.mPicture;

            if (user.pin_exist) {
                vh.mPinImage.setVisibility(View.VISIBLE);
            } else {
                vh.mPinImage.setVisibility(View.INVISIBLE);
            }

            if (user.photo_exist) {
                getPhoto(user,picture);
            } else {
                Glide.with(mContext).load(R.drawable.user_06).fitCenter().into(picture);
            }

            StyledTextView name = vh.mName;
            if (user.name == null || user.name.equals("")) {
                name.setText(user.user_id);
            } else {
                name.setText(user.name);
            }
            StyledTextView date = vh.mDate;
            date.setText(user.user_id);
            // if (user.permission != null) {
            // date.setText(user.user_id + "\n" + user.permission.name);
            // } else {
            // date.setText(user.user_id);
            // }

            // String sd = user.getStartDate(mContext, DATE_TYPE.FORMAT_DATE);
            // String ed = user.getExpireDate(mContext, DATE_TYPE.FORMAT_DATE);
            // date.setText(sd + " - " + ed);
            StyledTextView fingerCount = vh.mFinger;
            fingerCount.setText(String.valueOf(user.fingerprint_count));
            StyledTextView cardCount = vh.mCard;
            cardCount.setText(String.valueOf(user.card_count));
        }
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mHasPhoto) {
            return getPhotoView(position, convertView, parent);
        } else {
            return getNameView(position, convertView, parent);
        }

    }

    public String getuserGroupId() {
        return mGroupId;
    }

    private void onUserListener(Users response, Object deliverParam) {
        if (response == null || response.records == null) {
            if (mOnItemsListener != null) {
                mOnItemsListener.onSuccessNull();
            }
            if (mTotal <= getCount() && mSwipyRefreshLayout != null) {
                mSwipyRefreshLayout.setEnableBottom(false);
            }
            return;
        }

        if (mItems == null) {
            mItems = new ArrayList<ListUser>();
        }
        if (mOnItemsListener != null) {
            mOnItemsListener.onTotalReceive(response.total);
        }

        if (response.records.size() < 1) {
            if (mTotal <= getCount() && mSwipyRefreshLayout != null) {
                mSwipyRefreshLayout.setEnableBottom(false);
            }
            return;
        }

        for (ListUser user : response.records) {
            mItems.add(user);
        }
        setData(mItems);
        mOffset = mItems.size() - 1;
        mTotal = response.total;
        mLimit = SECOND_LIMIT;
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "mTotal:" + mTotal + " mOffset:" + mOffset + " getCount():" + getCount());
        }
        if (mTotal <= getCount() && mSwipyRefreshLayout != null) {
            mSwipyRefreshLayout.setEnableBottom(false);
        }
    }

    public void setHasphoto(boolean hasPhoto) {
        mHasPhoto = hasPhoto;
    }

    public void setSwipyRefreshLayout(SwipyRefreshLayout swipyRefreshLayout, FloatingActionButton fab) {
        BaseListViewScroll onScroll = new BaseListViewScroll();
        onScroll.setFloatingActionButton(fab, mListView, this);
        setOnScrollListener(onScroll);
        mSwipyRefreshLayout = swipyRefreshLayout;
        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.e(TAG, "SwipyRefreshLayoutDirection:" + direction);
                switch (direction) {
                    case TOP:
                        getItems(mQuery);
                        break;
                    case BOTTOM:
                        if (mTotal - 1 > mOffset) {
                            mListView.removeCallbacks(mRunGetItems);
                            mListView.postDelayed(mRunGetItems, 100);
                        } else {
                            mSwipyRefreshLayout.setRefreshing(false);
                            mToastPopup.show(mContext.getString(R.string.no_more_data), null);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void setUserGroupId(String id) {
        mGroupId = id;
        getItems(mQuery);
    }

    public class ExtraViewHolder {
        public StyledTextView mCard;
        public StyledTextView mDate;
        public StyledTextView mFinger;
        public StyledTextView mName;
        public ImageView mPicture;
        public ImageView mPinImage;

        public ExtraViewHolder(View root, int picture, int name, int date, int finger, int card, int pin) {
            mPicture = (ImageView) root.findViewById(picture);
            mName = (StyledTextView) root.findViewById(name);
            mDate = (StyledTextView) root.findViewById(date);
            mFinger = (StyledTextView) root.findViewById(finger);
            mCard = (StyledTextView) root.findViewById(card);
            mPinImage = (ImageView) root.findViewById(pin);
        }
    }
}
