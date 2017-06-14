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
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.adapter.base.BaseListAdapter;
import com.supremainc.biostar2.adapter.base.BaseUserAdapter;
import com.supremainc.biostar2.datatype.GlidePhotoData;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.models.v2.user.ListUser;
import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;
import com.supremainc.biostar2.sdk.provider.PermissionDataProvider;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.widget.popup.Popup;

import java.io.InputStream;
import java.util.ArrayList;



public class PhotoUserAdapter extends BaseUserAdapter {
    private OkHttpUrlLoader.Factory mFactory;
    private String mSubDomain;
    private PermissionDataProvider mPermissionDataProvider;
    private String mLoginedUserID;

    public PhotoUserAdapter(Activity context, ArrayList<ListUser> items, ListView listView, OnItemClickListener onItemClickListener, Popup popup, BaseListAdapter.OnItemsListener onUsersListener) {
        super(context, items, listView, onItemClickListener, popup, onUsersListener);
        mPermissionDataProvider = PermissionDataProvider.getInstance(context);

        mSubDomain = ConfigDataProvider.getLatestDomain(context);
        if (mFactory == null) {
            mFactory = new OkHttpUrlLoader.Factory(mUserDataProvider.getOkHttpClient());
            Glide.get(mActivity).register(GlideUrl.class, InputStream.class,
                    mFactory);
        }
        if (mPermissionDataProvider.getLoginUserInfo() == null) {
            return;
        }
        mLoginedUserID = mPermissionDataProvider.getLoginUserInfo().user_id;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListUser user = null;
        if (mItems != null) {
            user = mItems.get(position);
        }
        ItemViewHolder viewHolder = (ItemViewHolder) view.getTag();
        if (mListView.getChoiceMode() != ListView.CHOICE_MODE_NONE) {
            if (mLoginedUserID == null) {
                mLoginedUserID = mPermissionDataProvider.getLoginUserInfo().user_id;
            }
            if (!mPermissionDataProvider.isEnableModifyUser(user) || mLoginedUserID.equals(user.user_id)) {
                mListView.setItemChecked(position, false);
                return;
            }
        }
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
            mFactory = new OkHttpUrlLoader.Factory(mUserDataProvider.getOkHttpClient());
            Glide.get(mActivity).register(GlideUrl.class, InputStream.class,
                    mFactory);
        }
        String url = mUserDataProvider.getUserPhotoUrl(userID);
        if (url != null) {
            if (view.getTag() == null) {
                new GlidePhotoData(activity, view, defaultResID, lastModify, maxSize, mSubDomain, url);
            } else {
                new GlidePhotoData(activity, view, lastModify, maxSize, mSubDomain, url);
            }
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
                vh.mPinImage.setVisibility(View.GONE);
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
            if (user.fingerprint_count > 0 || user.fingerprint_template_count > 0) {
                vh.mFingerImage.setVisibility(View.VISIBLE);
            } else {
                vh.mFingerImage.setVisibility(View.GONE);
            }
            if (user.card_count > 0) {
                vh.mCardImage.setVisibility(View.VISIBLE);
            } else {
                vh.mCardImage.setVisibility(View.GONE);
            }
            if (user.face_template_count > 0) {
                vh.mFaceImage.setVisibility(View.VISIBLE);
            } else {
                vh.mFaceImage.setVisibility(View.GONE);
            }
            setSelector(vh.mRoot, vh.mLink, position);

            if (mListView.getChoiceMode() != ListView.CHOICE_MODE_NONE) {
                if (mLoginedUserID == null) {
                    mLoginedUserID = mPermissionDataProvider.getLoginUserInfo().user_id;
                }
                if (!mPermissionDataProvider.isEnableModifyUser(user) || mLoginedUserID.equals(user.user_id)) {
                    vh.mRoot.setBackgroundResource(R.drawable.selector_list_gray);
                }
            }

        }
        return convertView;

    }

    public class ItemViewHolder {
        public View mRoot;
        public StyledTextView mID;
        public StyledTextView mName;
        public ImageView mPicture;
        public ImageView mPinImage;
        public ImageView mFaceImage;
        public ImageView mCardImage;
        public ImageView mFingerImage;
        public ImageView mLink;

        public ItemViewHolder(View root) {
            mRoot = root;
            mPicture = (ImageView) root.findViewById(R.id.user_picture);
            mName = (StyledTextView) root.findViewById(R.id.user_name);
            mID = (StyledTextView) root.findViewById(R.id.user_id);
            mFaceImage = (ImageView) root.findViewById(R.id.user_ic_face);
            mCardImage = (ImageView) root.findViewById(R.id.user_ic_card);
            mFingerImage = (ImageView) root.findViewById(R.id.user_ic_fingerprint);
            mPinImage = (ImageView) root.findViewById(R.id.user_ic_pin);
            mLink = (ImageView) root.findViewById(R.id.info);
        }
    }
}
