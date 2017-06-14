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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.adapter.base.BaseCardAdapter;
import com.supremainc.biostar2.sdk.models.v2.card.Card;
import com.supremainc.biostar2.sdk.models.v2.card.ListCard;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.view.SwitchView;
import com.supremainc.biostar2.widget.popup.Popup;

import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewCardAdapter extends BaseCardAdapter {
    private boolean mIsEditDisable;
    private String mUserID;
    private int mRequestPosition = -1;

    private Callback<ResponseStatus> mUnBlockListener = new Callback<ResponseStatus>() {
        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            if (isIgnoreCallback(call, false)) {
                return;
            }
            mPopup.dismissWiat();
            showErrorPopup(t.getMessage(),false);
        }

        @Override
        public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
            if (isIgnoreCallback(call, response, false)) {
                return;
            }
            mPopup.dismissWiat();
            if (isInvalidResponse(response, true, false)) {
                return;
            }
            if (mRequestPosition < 0) {
                return;
            }
            ListCard item = mItems.get(mRequestPosition);
            item.is_blocked = false;
            notifyDataSetChanged();
        }
    };

    private Callback<ResponseStatus> mBlockListener = new Callback<ResponseStatus>() {
        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            if (isIgnoreCallback(call, false)) {
                return;
            }
            mPopup.dismissWiat();
            showErrorPopup(t.getMessage(),false);
        }

        @Override
        public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
            if (isIgnoreCallback(call, response, false)) {
                return;
            }
            mPopup.dismissWiat();
            if (isInvalidResponse(response, true, false)) {
                return;
            }
            if (mRequestPosition < 0) {
                return;
            }
            ListCard item = mItems.get(mRequestPosition);
            item.is_blocked = true;
            notifyDataSetChanged();
        }
    };



    public NewCardAdapter(Activity activity, String userID, ArrayList<ListCard> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener, boolean editDisable) {
        super(activity, items, listView, itemClickListener, popup, onItemsListener);
        mUserID = userID;
        mIsEditDisable = editDisable;
//        if (mIsEditDisable) {
//            mDefaultSelectColor = mActivity.getResources().getColor(R.color.gray_10);
//        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mIsEditDisable) {
            return;
        }
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
            return;
        }
        ListCard card = null;
        if (mItems != null) {
            card = mItems.get(position);
        }
        if (card != null && Card.ACCESS_ON.equals(card.type) && !card.is_blocked) {
            mListView.setItemChecked(position, false);
            mToastPopup.show(-1, mActivity.getString(R.string.non_blocked));
        }
        ItemViewHolder vh = (ItemViewHolder) view.getTag();
        setSelector(vh.mRoot, vh.mLink, position, false);
        super.onItemClick(parent, view, position, id);
    }

    private void sendLocalBroadcast(String key, Serializable value) {
        Intent intent = new Intent(key);
        if (value != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(key, value);
            intent.putExtras(bundle);
        }
        LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
    }

    private String getCardTypeName(ListCard item) {
        if (Card.CSN.equals(item.type)) {
            return mActivity.getString(R.string.csn);
        } else if (Card.ACCESS_ON.equals(item.type)) {
            if (item.is_mobile_credential) {
                return mActivity.getString(R.string.access_on_card) + " (" + mActivity.getString(R.string.mobile) + ")";
            } else {
                return mActivity.getString(R.string.access_on_card);
            }
        } else if (Card.SECURE_CREDENTIAL.equals(item.type)) {
            if (item.is_mobile_credential) {
                return mActivity.getString(R.string.secure_card) + " (" + mActivity.getString(R.string.mobile) + ")";
            } else {
                return mActivity.getString(R.string.secure_card);
            }
        } else if (Card.WIEGAND.equals(item.type)) {
            return mActivity.getString(R.string.wiegand);
        } else if (Card.CSN_WIEGAND.equals(item.type)) {
            return mActivity.getString(R.string.wiegand);
        }
        return "";
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mItems == null || mItems.size() < 1) {
            return null;
        }
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.list_item_card, parent, false);
            ItemViewHolder viewHolder = new ItemViewHolder(convertView);
            convertView.setTag(viewHolder);

        }
        ItemViewHolder vh = (ItemViewHolder) convertView.getTag();
        if (vh == null) {
            vh = new ItemViewHolder(convertView);
            convertView.setTag(vh);
        }
        vh.mStatusSwitch.setTag((Integer) position);
        vh.mMobileCard.setTag((Integer) position);

        ListCard item = mItems.get(position);
        vh.mStatusSwitch.setSwitchNotNotiy(!item.is_blocked);
        if (item == null) {
            return vh.mRoot;
        }
        vh.mID.setText(item.card_id);
        vh.mCardType.setText(getCardTypeName(item));
        if (item.issue_count > 0 && (Card.SECURE_CREDENTIAL.equals(item.type)||Card.ACCESS_ON.equals(item.type)) ) {
            vh.mID.setText(item.card_id + " (" + mActivity.getString(R.string.issue_card_count) + " " + item.issue_count + ")");
        }
        if (item.is_blocked) {
            vh.mStatusSwitch.setSwitchNotNotiy(false);
        } else {
            vh.mStatusSwitch.setSwitchNotNotiy(true);
        }
        if (item.is_mobile_credential) {
            vh.mMobileCard.setVisibility(View.VISIBLE);
            if (item.is_registered) {
                vh.mMobileCard.setImageResource(R.drawable.ic_card_used);
            } else {
                vh.mMobileCard.setImageResource(R.drawable.ic_card_request);
            }
        } else {
            vh.mMobileCard.setVisibility(View.GONE);
        }
        setSelector(vh.mRoot, vh.mLink, position, false);
        if (mListView.getChoiceMode() != ListView.CHOICE_MODE_NONE) {
            vh.mStatusSwitch.setVisibility(View.GONE);
            vh.mMobileCard.setVisibility(View.GONE);
            if (Card.ACCESS_ON.equals(item.type) && !item.is_blocked) {
                vh.mRoot.setBackgroundResource(R.drawable.selector_list_gray);
            }
        } else {
            vh.mStatusSwitch.setVisibility(View.VISIBLE);
        }
        return vh.mRoot;
    }

    private void showUnBlockPopup(final ListCard item, final int position) {
        mPopup.show(Popup.PopupType.CARD, mActivity.getString(R.string.unblock), mActivity.getString(R.string.question_unblock_card), new Popup.OnPopupClickListener() {
            @Override
            public void OnNegative() {

            }

            @Override
            public void OnPositive() {
                mRequestPosition = position;
                mPopup.showWait(mCancelStayListener);
                request(mCardDataProvider.unblock(item.id, mUnBlockListener));
            }
        }, mActivity.getString(R.string.ok), mActivity.getString(R.string.cancel), false);
    }

    private void showBlockPopup(final ListCard item, final int position) {
        mPopup.show(Popup.PopupType.CARD, mActivity.getString(R.string.block), mActivity.getString(R.string.question_block_card), new Popup.OnPopupClickListener() {
            @Override
            public void OnNegative() {

            }

            @Override
            public void OnPositive() {
                mRequestPosition = position;
                mPopup.showWait(mCancelStayListener);
                request(mCardDataProvider.block(item.id, mBlockListener));
            }
        }, mActivity.getString(R.string.ok), mActivity.getString(R.string.cancel), false);
    }


    public class ItemViewHolder {
        public View mRoot;
        public StyledTextView mCardType;
        public StyledTextView mID;
        public ImageView mMobileCard;
        public ImageView mLink;
        public SwitchView mStatusSwitch;

        public ItemViewHolder(View root) {
            mRoot = root;
            mLink = (ImageView) root.findViewById(R.id.info);
            mMobileCard = (ImageView) root.findViewById(R.id.info_mobilecard);
            mID = (StyledTextView) root.findViewById(R.id.card_id);
            mCardType = (StyledTextView) root.findViewById(R.id.card_type);
            mStatusSwitch = (SwitchView) root.findViewById(R.id.status_switch);
            mStatusSwitch.init(mActivity, new SwitchView.OnChangeListener() {
                @Override
                public boolean onChange(boolean on) {
                    if (mIsEditDisable) {
                        return false;
                    }
                    final Integer position = (Integer) mStatusSwitch.getTag();
                    if (position == null) {
                        return false;
                    }
                    final ListCard item = mItems.get(position);
                    if (on) {
                        showUnBlockPopup(item, position);
                    } else {
                        showBlockPopup(item, position);
                    }
                    return false;
                }
            }, true);
        }
    }
}
