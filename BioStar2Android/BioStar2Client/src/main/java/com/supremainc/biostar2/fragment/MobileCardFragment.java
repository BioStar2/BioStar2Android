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
package com.supremainc.biostar2.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.datatype.MobileCardData;
import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.provider.MobileCardDataProvider;
import com.supremainc.biostar2.sdk.datatype.v2.Card.Card;
import com.supremainc.biostar2.sdk.datatype.v2.Card.MobileCard;
import com.supremainc.biostar2.sdk.datatype.v2.Card.MobileCardRaw;
import com.supremainc.biostar2.sdk.datatype.v2.Card.MobileCards;
import com.supremainc.biostar2.sdk.datatype.v2.User.User;
import com.supremainc.biostar2.sdk.utils.FileUtil;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.view.MobileCardListView;
import com.supremainc.biostar2.view.SubToolbar;
import com.supremainc.biostar2.view.SwitchView;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.popup.Popup;

public class MobileCardFragment extends BaseFragment {
    private SubToolbar mSubToolbar;
    private MobileCardListView mMobileCardListView;
    private int mTotal = -1;
    private User mUser;
    private boolean mIsLaunchGuide =false;

    private Response.Listener<MobileCardRaw> mRegisterListener = new Response.Listener<MobileCardRaw>() {
        @Override
        public void onResponse(MobileCardRaw response, Object param) {
            if (isInValidCheck(null)) {
                return;
            }
            mPopup.dismissWiat();
            if (response == null || response.raw == null) {
                mToastPopup.show(getString(R.string.none_data), null);
                return;
            }
            byte[] data = Base64.decode(response.raw, 0);
            FileUtil.getInstance().bufferToFile("/sdcard/data",data);
            //TODO save keystore
            View view = (View)param;
            MobileCard card = (MobileCard)view.getTag();
            card.is_registered = true;
//            cardStorage.readSDCardResult("/sdcard/data");
            mToastPopup.show(getString(R.string.register_mobile_card), null);
            SwitchView switchView = (SwitchView) view.findViewById(R.id.card_switch);
            switchView.setSwitch(true);
        }
    };
    private OnSingleClickListener mOnSingleClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            MobileCard card = (MobileCard)v.getTag();
            if (card == null) {
                return;
            }
            if (card.is_registered) {
                mToastPopup.show(getString(R.string.already_assigned), null);
                return;
            }
            mPopup.showWait(mCancelExitListener);
            mCardDataProvider.registerMobileCard(TAG,mRegisterListener,mErrorStayListener, card.id,v);
        }
    };
    private Response.Listener<MobileCards> mCardsListener = new Response.Listener<MobileCards>() {
        @Override
        public void onResponse(MobileCards response, Object param) {
            if (isInValidCheck(null)) {
                return;
            }
            mPopup.dismissWiat();
            if (response == null || response.records == null || response.records.size() < 1) {
                mToastPopup.show(getString(R.string.none_data), null);
                return;
            }
            for (MobileCard card : response.records) {
                mMobileCardListView.addCard(card, mUser,mOnSingleClickListener);
            }
            mIsDataReceived = true;
            mTotal = response.records.size();
            mSubToolbar.setTotal(mTotal);

            if (mAppDataProvider.getBoolean(AppDataProvider.BooleanType.SHOW_GUIDE_DETAIL_CARD) && mIsLaunchGuide == false) {
                mIsLaunchGuide = true;
                Bundle bundle = new Bundle();
                try {
                    MobileCard card = response.records.get(0).clone();
                    bundle.putSerializable(MobileCard.TAG, card);
                    bundle.putInt(Setting.TOTAL_COUNT,mTotal);
                    mScreenControl.addScreen(ScreenType.MOBILE_CARD_GUIDE, bundle);
                } catch (Exception e) {

                }
            }
        }
    };

    public MobileCardFragment() {
        super();
        setType(ScreenType.MOBILE_CARD_LIST);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void initValue() {
        mUser = mUserDataProvider.getLoginUserInfo();
        if (mMobileCardListView == null) {
            mMobileCardListView = (MobileCardListView) mRootView.findViewById(R.id.mobile_card_list);
        }

        if (mSubToolbar == null) {
            mSubToolbar = (SubToolbar) mRootView.findViewById(R.id.subtoolbar);
            mSubToolbar.init(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setResID(R.layout.fragment_mobilecard2);
        super.onCreateView(inflater, container, savedInstanceState);
        if (!mIsReUsed) {
            mTotal = 0;
            initValue();
            initActionbar(getString(R.string.mobile_card));
            mMobileCardListView.removeAllMenuItem();
//            MobileCardData.MobileCard local = mMobileCardDataProvider.getLocalMobileCard();
            mPopup.showWait(mCancelExitListener);
            mCardDataProvider.getMobileCards(TAG, mCardsListener, mErrorBackListener, mUser.user_id,null);
            mSubToolbar.setTotal(mTotal);
            mRootView.invalidate();
        }
        return mRootView;
    }


    @Override
    public boolean onBack() {
        if (mSubToolbar != null) {
            if (mSubToolbar.isExpandSearch()) {
                mSubToolbar.setSearchIconfied();
                return true;
            }
        }
        if (super.onBack()) {
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void registerBroadcast() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (mIsDestroy) {
                        return;
                    }
                    String action = intent.getAction();
                    if (action.equals(Setting.BROADCAST_REROGIN)) {
                        mMobileCardListView.removeAllMenuItem();
                        mCardDataProvider.getMobileCards(TAG, mCardsListener, mErrorBackListener, mUser.user_id,null);
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_REROGIN);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
    }
}
