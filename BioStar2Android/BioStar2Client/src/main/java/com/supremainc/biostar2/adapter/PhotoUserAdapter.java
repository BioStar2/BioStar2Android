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
package com.supremainc.biostar2.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.adapter.base.BaseListAdapter;
import com.supremainc.biostar2.adapter.base.BaseUserAdapter;
import com.supremainc.biostar2.datatype.GlidePhotoData;
import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.sdk.datatype.v2.Common.VersionData;
import com.supremainc.biostar2.sdk.datatype.v2.User.ListUser;
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.widget.popup.Popup;

import java.io.InputStream;
import java.util.ArrayList;

public class PhotoUserAdapter extends BaseUserAdapter {
    private OkHttpUrlLoader.Factory mFactory;
    private String mSubDomain;


    public PhotoUserAdapter(Activity context, ArrayList<ListUser> items, ListView listView, OnItemClickListener onItemClickListener, Popup popup, BaseListAdapter.OnItemsListener onUsersListener) {
        super(context, items, listView, onItemClickListener, popup, onUsersListener);
        mSubDomain = ConfigDataProvider.getLatestDomain(context);
        if (mFactory == null) {
            mFactory = new OkHttpUrlLoader.Factory(mUserDataProvider.getRequestQueue().getOkHttpClient());
            Glide.get(mActivity).register(GlideUrl.class, InputStream.class,
                    mFactory);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ItemViewHolder viewHolder = (ItemViewHolder) view.getTag();
        setSelector(view, viewHolder.mLink, position);
        super.onItemClick(parent, view, position, id);
    }


    private void getPhoto(ListUser user, ImageView picture) {
        String lastModify = null;
        if (user.last_modify == null) {
//            if (mLastModify == null) {
//                mLastModify = String.valueOf(System.currentTimeMillis());
//            }
            lastModify = "0";
        } else {
            lastModify = user.last_modify;
        }
        getUserPhoto(mActivity, user.user_id, picture, R.drawable.user_06, Setting.USER_PROFILE_IMAGE_SIZE, lastModify);
    }

    public void getUserPhoto(Activity activity, String userID, ImageView view, int defaultResID, int maxSize, String lastModify) {
        if (userID == null || userID.isEmpty()) {
            return;
        }
        if (mFactory == null) {
            mFactory = new OkHttpUrlLoader.Factory(mUserDataProvider.getRequestQueue().getOkHttpClient());
            Glide.get(mActivity).register(GlideUrl.class, InputStream.class,
                    mFactory);
        }
        String url = mUserDataProvider.getUserPhotoUrl(userID);
        if (url != null) {
            new GlidePhotoData(activity, view, defaultResID, lastModify, maxSize, mSubDomain, url);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mItems == null || mItems.size() < 1) {
            return null;
        }
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.list_item_user, parent, false);
            ItemViewHolder viewHolder = new ItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        ItemViewHolder vh = (ItemViewHolder) convertView.getTag();
        if (vh == null) {
            vh = new ItemViewHolder(convertView);
            convertView.setTag(vh);
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
                getPhoto(user, picture);
            } else {
                Glide.with(mActivity).load(R.drawable.user_06).fitCenter().into(picture);
            }

            if (user.name == null || user.name.equals("")) {
                vh.mName.setText(user.user_id);
            } else {
                vh.mName.setText(user.name);
            }
            vh.mID.setText(user.user_id);
            // String sd = user.getStartDate(mActivity, DATE_TYPE.FORMAT_DATE);
            // String ed = user.getExpireDate(mActivity, DATE_TYPE.FORMAT_DATE);
            // date.setText(sd + " - " + ed);
            if (VersionData.getCloudVersion(mActivity) < 2) {
                vh.mFinger.setText(String.valueOf(user.fingerprint_count));
            } else {
                vh.mFinger.setText(String.valueOf(user.fingerprint_template_count));
            }
            vh.mCard.setText(String.valueOf(user.card_count));
            setSelector(vh.mRoot, vh.mLink, position);
        }
        return convertView;

    }

    public class ItemViewHolder {
        public View mRoot;
        public StyledTextView mCard;
        public StyledTextView mID;
        public StyledTextView mFinger;
        public StyledTextView mName;
        public ImageView mPicture;
        public ImageView mPinImage;
        public ImageView mLink;

        public ItemViewHolder(View root) {
            mRoot = root;
            mPicture = (ImageView) root.findViewById(R.id.user_picture);
            mName = (StyledTextView) root.findViewById(R.id.user_name);
            mID = (StyledTextView) root.findViewById(R.id.user_id);
            mFinger = (StyledTextView) root.findViewById(R.id.user_finger);
            mCard = (StyledTextView) root.findViewById(R.id.user_card);
            mPinImage = (ImageView) root.findViewById(R.id.user_ic_pin);
            mLink = (ImageView) root.findViewById(R.id.info);
        }
    }
}
