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
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.datatype.v2.Card.Card;
import com.supremainc.biostar2.sdk.datatype.v2.Card.ListCard;
import com.supremainc.biostar2.sdk.datatype.v2.Common.ResponseStatus;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.widget.popup.Popup;

import java.io.Serializable;
import java.util.ArrayList;

public class NewCardAdapter extends BaseCardAdapter {
    private boolean mIsEditDisable;
    private String mUserID;


    private Response.Listener<ResponseStatus> mUnBlockListener = new  Response.Listener<ResponseStatus>() {
        @Override
        public void onResponse(ResponseStatus response, Object param) {
            if (isDestroy()) {
                return;
            }
            mPopup.dismiss();
            Integer position = (Integer)param;
            if (position == null) {
                return;
            }
            ListCard item = mItems.get(position);
            item.is_blocked = false;
            notifyDataSetChanged();
        }
    };

    private Response.Listener<ResponseStatus> mReIssueListener = new  Response.Listener<ResponseStatus>() {
        @Override
        public void onResponse(ResponseStatus response, Object param) {
            if (isDestroy()) {
                return;
            }
            mPopup.dismiss();
            Integer position = (Integer)param;
            if (position == null) {
                return;
            }
            ListCard item = mItems.get(position);
            item.is_registered = false;
//            item.issue_count++;
            notifyDataSetChanged();
        }
    };
    private Response.Listener<ResponseStatus> mBlockListener = new  Response.Listener<ResponseStatus>() {
        @Override
        public void onResponse(ResponseStatus response, Object param) {
            if (isDestroy()) {
                return;
            }
            mPopup.dismiss();
            Integer position = (Integer)param;
            if (position == null) {
                return;
            }
            ListCard item = mItems.get(position);
            item.is_blocked = true;
            notifyDataSetChanged();
        }
    };
    private Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isDestroy(error)) {
                return;
            }
            mPopup.dismiss();
            mPopup.show(Popup.PopupType.ALERT, mActivity.getString(R.string.fail), Setting.getErrorMessage(error, mActivity), new Popup.OnPopupClickListener() {
                @Override
                public void OnNegative() {
                }

                @Override
                public void OnPositive() {
                }
            }, mActivity.getString(R.string.ok), null, false);
        }
    };
    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            final Integer position = (Integer)v.getTag();
            if (position == null) {
                return;
            }
            final ListCard item = mItems.get(position);
            switch (v.getId()) {
                //TODO 재발급.
                case R.id.info_mobilecard:
                   if (item.is_registered) {
                       mPopup.show(Popup.PopupType.CARD,mActivity.getString(R.string.mobile_card),  mActivity.getString(R.string.question_reregister_card), new Popup.OnPopupClickListener() {
                           @Override
                           public void OnNegative() {

                           }

                           @Override
                           public void OnPositive() {
                               if (mUserID != null) {
                                   mPopup.showWait(false);
                                   mCardDataProvider.reissue(TAG, mReIssueListener, mErrorListener,mUserID, item.id, position);
                               }
                           }
                       }, mActivity.getString(R.string.ok), mActivity.getString(R.string.cancel), false);
                   }
                    break;
                case R.id.info_unblock:
                    mPopup.show(Popup.PopupType.CARD,mActivity.getString(R.string.unblock),  mActivity.getString(R.string.question_unblock_card), new Popup.OnPopupClickListener() {
                        @Override
                        public void OnNegative() {

                        }

                        @Override
                        public void OnPositive() {
                            mPopup.showWait(false);
                            mCardDataProvider.unblock(TAG,mUnBlockListener,mErrorListener,item.id,position);
                        }
                    }, mActivity.getString(R.string.ok), mActivity.getString(R.string.cancel), false);
                    break;
                case R.id.info_block:
                    mPopup.show(Popup.PopupType.CARD,mActivity.getString(R.string.block),  mActivity.getString(R.string.question_block_card), new Popup.OnPopupClickListener() {
                        @Override
                        public void OnNegative() {

                        }

                        @Override
                        public void OnPositive() {
                            mPopup.showWait(false);
                            mCardDataProvider.block(TAG,mBlockListener,mErrorListener,item.id,position);
                        }
                    }, mActivity.getString(R.string.ok), mActivity.getString(R.string.cancel), false);
                    break;
            }
        }
    };
    public NewCardAdapter(Activity activity,String userID, ArrayList<ListCard> items, ListView listView, OnItemClickListener itemClickListener, Popup popup, OnItemsListener onItemsListener, boolean editDisable) {
        super(activity, items, listView, itemClickListener, popup, onItemsListener);
        mUserID = userID;
        mIsEditDisable = editDisable;
        if (mIsEditDisable) {
            mDefaultSelectColor = mActivity.getResources().getColor(R.color.gray_10);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mIsEditDisable) {
            return;
        }
        ListCard card=null;
        if (mItems != null) {
            card =  mItems.get(position);
        }
        if (card != null && Card.ACCESS_ON.equals(card.type) && !card.is_blocked) {
            mListView.setItemChecked(position,false);
            mToastPopup.show(-1,mActivity.getString(R.string.non_blocked));
        }
        ItemViewHolder vh = (ItemViewHolder) view.getTag();
        setSelector(vh,position);
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
    private View setSelector(ItemViewHolder vh,int position) {
        ListCard item = mItems.get(position);
        if (item == null) {
            return vh.mRoot;
        }
        vh.mID.setText(item.card_id);
        if (Card.CSN.equals(item.type)) {
            vh.mCardType.setText( mActivity.getString(R.string.csn));
        } else if (Card.ACCESS_ON.equals(item.type)) {
            if (item.is_mobile_credential) {
                vh.mCardType.setText(mActivity.getString(R.string.access_on_card)+" ("+mActivity.getString(R.string.mobile)+")");
            } else {
                vh.mCardType.setText(mActivity.getString(R.string.access_on_card));
            }
            if (item.issue_count > 0) {
                vh.mID.setText(item.card_id+" ("+mActivity.getString(R.string.issue_card_count)+" "+item.issue_count+")");
            }
        } else if (Card.SECURE_CREDENTIAL.equals(item.type)) {
            if (item.is_mobile_credential) {
                vh.mCardType.setText(mActivity.getString(R.string.secure_card)+" ("+mActivity.getString(R.string.mobile)+")");
            } else {
                vh.mCardType.setText(mActivity.getString(R.string.secure_card));
            }
            if (item.issue_count > 0) {
                vh.mID.setText(item.card_id+" ("+mActivity.getString(R.string.issue_card_count)+" "+item.issue_count+")");
            }
        } else if (Card.WIEGAND.equals(item.type)) {
            vh.mCardType.setText( mActivity.getString(R.string.wiegand));
        } else if (Card.CSN_WIEGAND.equals(item.type)) {
            vh.mCardType.setText( mActivity.getString(R.string.wiegand));
        }



        if (!mIsEditDisable) {
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

            if (item.is_blocked) {
                vh.mRoot.setBackgroundResource(R.drawable.selector_list_gray);
                vh.mBlock.setVisibility(View.GONE);
                vh.mUnblock.setVisibility(View.VISIBLE);
            } else {
                vh.mRoot.setBackgroundResource(R.drawable.selector_list_select_mode);
                vh.mBlock.setVisibility(View.VISIBLE);
                vh.mUnblock.setVisibility(View.GONE);
            }

            int mode = mListView.getChoiceMode();
            switch (mode) {
                case ListView.CHOICE_MODE_NONE:
                    vh.mLink.setVisibility(View.GONE);
                    break;
                default:
                    vh.mBlock.setVisibility(View.GONE);
                    vh.mUnblock.setVisibility(View.GONE);
                    vh.mMobileCard.setVisibility(View.GONE);
                    vh.mLink.setVisibility(View.VISIBLE);
                    if (mListView.isItemChecked(position)) {
                        vh.mRoot.setBackgroundResource(R.drawable.selector_list_selected);
                        vh.mLink.setImageResource(R.drawable.selector_list_check);
                    } else {
                        if (item.is_blocked) {
                            vh.mRoot.setBackgroundResource(R.drawable.selector_list_gray);
                        } else {
                            vh.mRoot.setBackgroundResource(R.drawable.selector_list_select_mode);
                        }
                        vh.mLink.setImageResource(R.drawable.selector_color_transparent);
                    }
                    break;
            }

        } else {
            if (item.is_blocked) {
                vh.mRoot.setBackgroundResource(R.drawable.selector_list_gray);
            } else {
                vh.mRoot.setBackgroundResource(R.drawable.selector_list_select_mode);
            }
            vh.mBlock.setVisibility(View.GONE);
            vh.mUnblock.setVisibility(View.GONE);
            vh.mMobileCard.setVisibility(View.GONE);
            vh.mLink.setVisibility(View.GONE);
        }
        return vh.mRoot;
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
        vh.mBlock.setTag((Integer)position);
        vh.mUnblock.setTag((Integer)position);
        vh.mMobileCard.setTag((Integer)position);
        return setSelector(vh,position);
    }

    public class ItemViewHolder {
        public View mRoot;
        public StyledTextView mCardType;
        public StyledTextView mID;
        public StyledTextView mBlock;
        public StyledTextView mUnblock;
        public ImageView mMobileCard;
        public ImageView mLink;

        public ItemViewHolder(View root) {
            mRoot = root;
            mLink = (ImageView) root.findViewById(R.id.info);
            mMobileCard = (ImageView) root.findViewById(R.id.info_mobilecard);
            mUnblock = (StyledTextView) root.findViewById(R.id.info_unblock);
            mBlock = (StyledTextView) root.findViewById(R.id.info_block);
            mMobileCard.setOnClickListener(mClickListener);
            mUnblock.setOnClickListener(mClickListener);
            mBlock.setOnClickListener(mClickListener);
            mID = (StyledTextView) root.findViewById(R.id.card_id);
            mCardType = (StyledTextView) root.findViewById(R.id.card_type);
        }
    }
}
