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
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.models.v2.card.Card;
import com.supremainc.biostar2.sdk.models.v2.card.ListCard;
import com.supremainc.biostar2.sdk.models.v2.card.SmartCardLayout;
import com.supremainc.biostar2.sdk.models.v2.card.WiegandFormat;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.models.v2.common.SimpleData;
import com.supremainc.biostar2.sdk.models.v2.common.SupportFeature;
import com.supremainc.biostar2.sdk.models.v2.common.VersionData;
import com.supremainc.biostar2.sdk.models.v2.device.ListDevice;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.sdk.provider.CardDataProvider.SmartCardType;
import com.supremainc.biostar2.view.RegisterCardView;
import com.supremainc.biostar2.view.RegisterCardView.CARD_TYPE;
import com.supremainc.biostar2.view.RegisterCardView.REGISTER_METHOD;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.popup.Popup;
import com.supremainc.biostar2.widget.popup.SelectCustomData;
import com.supremainc.biostar2.widget.popup.SelectPopup;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterCardFragment extends BaseFragment {
    private User mUserInfo;
    private RegisterCardView mRegisterCardView;

    private SelectPopup<ListCard> mSelectCardPopup;
    private SelectPopup<ListDevice> mSelectDevicePopup;
    private SelectPopup<SmartCardLayout> mSelectSmartCardLayoutPopup;
    private SelectPopup<WiegandFormat> mSelectWiegandFormatPopup;
    private SelectPopup<SimpleData> mSelectSimpleDataPopup;

    private ListDevice mSelectedDevice;
    private Card mSelectedCard;
    private WiegandFormat mSelectedWiegandFormat;
    private SmartCardLayout mSelectedSmartCardLayout;
    private ArrayList<Integer> mSelectedFingerPrint = new ArrayList<Integer>();
    private CARD_TYPE mType = CARD_TYPE.CSN;
    private SmartCardType mSelectedSmartCardType = SmartCardType.ACCESS_ON;
    private REGISTER_METHOD mMethod = REGISTER_METHOD.DEVICE;


    private Callback<Card> mScanListener = new Callback<Card>() {
        @Override
        public void onFailure(Call<Card> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showErrorPopup(t.getMessage(), false);
        }

        @Override
        public void onResponse(Call<Card> call, Response<Card> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true, false)) {
                return;
            }
            Card card = response.body();
            switch (mType) {
                case CSN:
                    if (!Card.CSN.equals(card.type)) {
                        mScanListener.onFailure(call,new Throwable(getString(R.string.invalid_card_type)));
                         return;
                    }
                    break;
                case WIEGAND:
                    if (!(Card.WIEGAND.equals(card.type) || Card.CSN_WIEGAND.equals(card.type)) || card.wiegand_format == null) {
                        mScanListener.onFailure(call,new Throwable(getString(R.string.invalid_card_type)));
                        return;
                    }
                    mSelectedWiegandFormat = card.wiegand_format;
                    break;
                case READ_CARD:
                    try {
                        mSelectedCard = (Card) card.clone();
                    } catch (Exception e) {
                        e.printStackTrace();
                        mSelectedCard = card;
                    }
                    mRegisterCardView.setCommon(mSelectedDevice, mSelectedCard);
                    if (Card.ACCESS_ON.equals(mSelectedCard.type)) {
                        mSelectedSmartCardType = SmartCardType.ACCESS_ON;
                    }
                    if (Card.SECURE_CREDENTIAL.equals(mSelectedCard.type)) {
                        mSelectedSmartCardType = SmartCardType.SECURE_CREDENTIAL;
                    }
                    break;
            }
            try {
                mSelectedCard = (Card) card.clone();
            } catch (Exception e) {
                e.printStackTrace();
                mSelectedCard = card;
            }
            mRegisterCardView.setCard(mSelectedCard, mSelectedDevice, mType, mMethod);
        }
    };

    private RegisterCardView.RegisterCardViewListener mRegisterCardViewListener = new RegisterCardView.RegisterCardViewListener() {
        @Override
        public void onCardType() {
            selectCardType();
        }

        @Override
        public void onRegisterMethod() {
            selectRegisterMethod();
        }

        @Override
        public void onDevice() {
            selectDevice();
        }

        @Override
        public void onWiegandFormat() {
            selectWiegandFormat();
        }

        @Override
        public void onAction() {
            if (mType == CARD_TYPE.READ_CARD) {
                setReadCard();
                return;
            }
            switch (mMethod) {
                case ASSIGN_CARD:
                    selectCard();
                    break;
                case DEVICE:
                    scanCard();
                    break;
            }
        }

        @Override
        public void onSmartCardLayout() {
            selectSmartCardLayout();
        }

        @Override
        public void onSmartCardType() {
            selectSmartCardType();
        }

        @Override
        public void onFingerPrint() {
            selectFingerPrint();
        }
    };
    private Popup.OnPopupClickListener mPopupSucess = new Popup.OnPopupClickListener() {
        @Override
        public void OnNegative() {
            mScreenControl.backScreen();
        }

        @Override
        public void OnPositive() {
            mScreenControl.backScreen();
        }
    };


    private Callback<ResponseStatus> mSaveEndListener = new Callback<ResponseStatus>() {
        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showErrorPopup(t.getMessage(), false);
        }

        @Override
        public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true, false)) {
                return;
            }
            sendLocalBroadcast(Setting.BROADCAST_UPDATE_CARD, null);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPopup.dismiss();
                    mPopup.show(Popup.PopupType.CONFIRM, getString(R.string.info), getString(R.string.success), mPopupSucess, null, null);
                }
            });
        }
    };

    private Callback<ResponseStatus> mIssueMobileCardListener = new Callback<ResponseStatus>() {
        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showErrorPopup(t.getMessage(), false);
        }

        @Override
        public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true, false)) {
                return;
            }
            sendLocalBroadcast(Setting.BROADCAST_UPDATE_CARD, null);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPopup.dismiss();
                    mPopup.show(Popup.PopupType.CONFIRM, getString(R.string.info), getString(R.string.success), mPopupSucess, null, null);
                }
            });
        }
    };

    private Callback<ResponseStatus> mRegisterCardListener = new Callback<ResponseStatus>() {
        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showErrorPopup(t.getMessage(), false);
        }

        @Override
        public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true, false)) {
                return;
            }
            Card card = new Card();
            card.id = response.body().id;
            card.card_id = null;
            switch (mType) {
                case CSN:
                    card.type = Card.CSN;
                    break;
                case SMARTCARD:
                    if (mSelectedSmartCardType == SmartCardType.SECURE_CREDENTIAL) {
                        card.type = Card.SECURE_CREDENTIAL;
                    } else if (mSelectedSmartCardType == SmartCardType.ACCESS_ON) {
                        card.type = Card.ACCESS_ON;
                    }
                    mSelectedCard = card;
                    break;
                case WIEGAND:
                    break;
                case MOBILE_CARD:
                    break;
            }
            mSelectedCard = card;
            saveEnd();
        }
    };
    
    public RegisterCardFragment() {
        super();
        setType(ScreenType.CARD_RIGISTER);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void selectCardType() {
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mActivity, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        linkType.add(new SelectCustomData(getString(R.string.csn), CARD_TYPE.CSN.ordinal(), false));
        linkType.add(new SelectCustomData(getString(R.string.wiegand), CARD_TYPE.WIEGAND.ordinal(), false));
        linkType.add(new SelectCustomData(getString(R.string.smartcard), CARD_TYPE.SMARTCARD.ordinal(), false));
        if (VersionData.isSupportFeature(mActivity, SupportFeature.MOBILE_CARD)) {
            linkType.add(new SelectCustomData(getString(R.string.mobile_card), CARD_TYPE.MOBILE_CARD.ordinal(), false,!mCommonDataProvider.isSupportMobileCredential()));
        }
        linkType.add(new SelectCustomData(getString(R.string.read_card), CARD_TYPE.READ_CARD.ordinal(), false));
        selectPopup.show(SelectPopup.SelectType.CUSTOM, new SelectPopup.OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem, boolean isPositive) {
                if (isInValidCheck()) {
                    return;
                }
                if (selectedItem == null) {
                    return;
                }
                int type = selectedItem.get(0).getIntId();
                CARD_TYPE selected = CARD_TYPE.values()[type];
                if (mType == selected) {
                    return;
                }
                mType = selected;
                mRegisterCardView.clear();
                clearSelected();
                mRegisterCardView.setView(mType, mMethod, mSelectedWiegandFormat, mSelectedDevice, mSelectedCard, mSelectedFingerPrint, mSelectedSmartCardType, mUserInfo);
                mActivity.invalidateOptionsMenu();
            }
        }, linkType, getString(R.string.card_type), false, true);
    }

    private void selectRegisterMethod() {
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mActivity, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        linkType.add(new SelectCustomData(getString(R.string.registeration_option_card_reader), REGISTER_METHOD.DEVICE.ordinal(), false));
        linkType.add(new SelectCustomData(getString(R.string.registeration_option_assign_card), REGISTER_METHOD.ASSIGN_CARD.ordinal(), false));
        if (!(mType == CARD_TYPE.SMARTCARD || mType == CARD_TYPE.MOBILE_CARD)) {
            linkType.add(new SelectCustomData(getString(R.string.registeration_option_direct_input), REGISTER_METHOD.DIRECT_INPUT.ordinal(), false));
        }
        selectPopup.show(SelectPopup.SelectType.CUSTOM, new SelectPopup.OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem, boolean isPositive) {
                if (isInValidCheck()) {
                    return;
                }
                if (selectedItem == null) {
                    return;
                }
                int type = selectedItem.get(0).getIntId();
                REGISTER_METHOD selected = REGISTER_METHOD.values()[type];
                if (mMethod == selected) {
                    return;
                }
                mMethod = selected;
                mRegisterCardView.clear();
                clearSelected();
                mRegisterCardView.setView(mType, mMethod, mSelectedWiegandFormat, mSelectedDevice, mSelectedCard, mSelectedFingerPrint, mSelectedSmartCardType, mUserInfo);
            }
        }, linkType, getString(R.string.registeration_option), false);
    }

    private void selectDevice() {
        SelectPopup.SelectType type = SelectPopup.SelectType.DEVICE_CARD_CSN;
        //TODO
        switch (mType) {
            case CSN:
                type = SelectPopup.SelectType.DEVICE_CARD_CSN;
                break;
            case WIEGAND:
                type = SelectPopup.SelectType.DEVICE_CARD_WIEGAND;
                break;
            case SMARTCARD:
                type = SelectPopup.SelectType.DEVICE_CARD_SMARTCARD;
                break;
            case READ_CARD:
                type = SelectPopup.SelectType.DEVICE_CARD;
                break;
        }
        mSelectDevicePopup.show(type, new SelectPopup.OnSelectResultListener<ListDevice>() {
            @Override
            public void OnResult(ArrayList<ListDevice> selectedItem, boolean isPositive) {
                if (isInValidCheck()) {
                    return;
                }
                if (selectedItem == null) {
                    return;
                }
                ListDevice device;
                try {
                    device = selectedItem.get(0).clone();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                switch (mType) {
                    case CSN:
                        if (device.isSupportCSNWiegand()) {
                            mToastPopup.show(null, getString(R.string.csn_format_wigand_scan));
                            return;
                        }
                        break;
                    case WIEGAND:
                        if (!(device.isSupportWiegand() || device.isSupportCSNWiegand())) {
                            mToastPopup.show(null, getString(R.string.none_wigand));
                            return;
                        }
                        mSelectedWiegandFormat = null;
                        break;
                    case SMARTCARD:
                        if (device.smart_card_layout == null || device.smart_card_layout.id == null) {
                            mToastPopup.show(null, getString(R.string.none_card_layout_format));
                            return;
                        } else {
                            mSelectedSmartCardLayout = device.smart_card_layout;
                        }
                        break;
                    case READ_CARD:
                        break;
                }

                mSelectedDevice = device;
                mRegisterCardView.setDevice(mSelectedDevice, mType);
            }
        }, null, getString(R.string.select_device_orginal), false, true);
    }

    private void selectWiegandFormat() {
        switch (mMethod) {
            case DIRECT_INPUT: {

                mSelectWiegandFormatPopup.show(SelectPopup.SelectType.WIEGAND_FORMAT, new SelectPopup.OnSelectResultListener<WiegandFormat>() {
                    @Override
                    public void OnResult(ArrayList<WiegandFormat> selectedItem, boolean isPositive) {
                        if (isInValidCheck()) {
                            return;
                        }
                        if (selectedItem == null) {
                            return;
                        }
                        mSelectedWiegandFormat = selectedItem.get(0);
                        mRegisterCardView.clearWiegandFormat();
                        mRegisterCardView.setWiegandFormat(mSelectedWiegandFormat, mSelectedCard, mMethod);
                    }
                }, null, getString(R.string.wiegand), false, true);
                return;
            }
        }
    }

    public void setReadCard() {
        ListDevice device = mSelectedDevice;
        clearSelected();
        mSelectedDevice = device;
        mRegisterCardView.setView(mType, mMethod, mSelectedWiegandFormat, mSelectedDevice, mSelectedCard, mSelectedFingerPrint, mSelectedSmartCardType, mUserInfo);
        scanCard();
    }

    public void selectSmartCardLayout() {
        mSelectSmartCardLayoutPopup.show(SelectPopup.SelectType.SMARTCARD_LAYOUT, new SelectPopup.OnSelectResultListener<SmartCardLayout>() {
            @Override
            public void OnResult(ArrayList<SmartCardLayout> selectedItem, boolean isPositive) {
                if (isInValidCheck()) {
                    return;
                }
                if (selectedItem == null || selectedItem.size() < 1) {
                    return;
                }
                mSelectedSmartCardLayout = selectedItem.get(0);
                mRegisterCardView.setSmartCardlayout(mSelectedSmartCardLayout);
            }
        }, null, getString(R.string.card_layout_format), false, true);
    }

    public void selectSmartCardType() {
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mActivity, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        linkType.add(new SelectCustomData(getString(R.string.secure_card), SmartCardType.SECURE_CREDENTIAL.ordinal(), false));
        linkType.add(new SelectCustomData(getString(R.string.access_on_card), SmartCardType.ACCESS_ON.ordinal(), false));
        selectPopup.show(SelectPopup.SelectType.CUSTOM, new SelectPopup.OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem, boolean isPositive) {
                if (isInValidCheck()) {
                    return;
                }
                if (selectedItem == null) {
                    return;
                }
                int type = selectedItem.get(0).getIntId();

                SmartCardType selected = SmartCardType.values()[type];
                if (mSelectedSmartCardType == selected) {
                    return;
                }
                mSelectedSmartCardType = selected;
                mRegisterCardView.clear();
                mRegisterCardView.setView(mType, mMethod, mSelectedWiegandFormat, mSelectedDevice, mSelectedCard, mSelectedFingerPrint, mSelectedSmartCardType, mUserInfo);
                if (mType == CARD_TYPE.MOBILE_CARD) {
                    mRegisterCardView.setSmartCardlayout(mSelectedSmartCardLayout);
                }
            }
        }, linkType, getString(R.string.smartcard_type), false);
    }

    private void scanCard() {
        if (mSelectedDevice == null) {
            mToastPopup.show(-1, R.string.select_device_orginal);
            return;
        }
        switch (mType) {
            case CSN:
                if (mSelectedDevice.wiegand_format != null) {
                    mToastPopup.show(getString(R.string.csn_format_wigand_scan), null);
                    return;
                }
                break;
            case SMARTCARD:
                if (mSelectedDevice.smart_card_layout == null) {
                    mToastPopup.show(getString(R.string.none), null);
                    return;
                }
                break;
            case WIEGAND:
                if (!(mSelectedDevice.isSupportCSNWiegand() || mSelectedDevice.isSupportWiegand())) {
                    mToastPopup.show(getString(R.string.none_wigand), null);
                    return;
                }
                break;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPopup.show(Popup.PopupType.CARD, getString(R.string.read_card), getString(R.string.card_on_device), null, null, null, false);
                mDeviceDataProvider.scanCard(mSelectedDevice.id, mScanListener);
            }
        });
    }

    private void selectCard() {
        SelectPopup.SelectType type = SelectPopup.SelectType.CARD;
        //TODO
        switch (mType) {
            case CSN:
                type = SelectPopup.SelectType.CARD_CSN;
                break;
            case WIEGAND:
                type = SelectPopup.SelectType.CARD_WIEGAND;
                break;
            case SMARTCARD:
                type = SelectPopup.SelectType.CARD_SMARTCARD;
                break;
        }
        mSelectCardPopup.show(type, new SelectPopup.OnSelectResultListener<ListCard>() {
            @Override
            public void OnResult(ArrayList<ListCard> selectedItem, boolean isPositive) {
                if (isInValidCheck()) {
                    return;
                }
                if (selectedItem == null) {
                    return;
                }
                try {
                    mSelectedCard = new Card();
                    selectedItem.get(0).clone(mSelectedCard);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    return;
                }
                switch (mType) {
                    case WIEGAND:
                        mSelectedWiegandFormat = mSelectedCard.wiegand_format;
                        break;
                }
                mRegisterCardView.setCard(mSelectedCard, mSelectedDevice, mType, mMethod);
            }
        }, null, getString(R.string.registeration_option_assign_card), false, true);
    }

    private void selectFingerPrint() {
        if (mUserInfo.fingerprint_template_count < 1) {
            mToastPopup.show(getString(R.string.none_registered_fingerprint), null);
            return;
        }
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mActivity, mPopup);
        if (mSelectedSmartCardLayout == null) {
            mToastPopup.show(getString(R.string.none_card_layout_format), null);
            return;
        }
        selectPopup.setLimit(mSelectedSmartCardLayout.max_template_in_card);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        boolean isSelected = false;
        for (int i = 0; i < mUserInfo.fingerprint_template_count; i++) {
            String title = getName(i) + " " + getString(R.string.fingerprint);
            if (mSelectedFingerPrint.size() > 0) {
                for (int j = 0; j < mSelectedFingerPrint.size(); j++) {
                    if (i == mSelectedFingerPrint.get(j)) {
                        isSelected = true;
                        break;
                    }
                }
            }
            linkType.add(new SelectCustomData(title, i, isSelected));
        }
        selectPopup.show(SelectPopup.SelectType.CUSTOM, new SelectPopup.OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem, boolean isPositive) {
                if (isInValidCheck()) {
                    return;
                }

                if (selectedItem == null) {
                    if (isPositive) {
                        mSelectedFingerPrint.clear();
                        mRegisterCardView.setFingerPrint(null);
                    }
                    return;
                }
                mSelectedFingerPrint.clear();
                for (int i = 0; i < selectedItem.size(); i++) {
                    mSelectedFingerPrint.add(selectedItem.get(i).getIntId());
                }
                mRegisterCardView.setFingerPrint(mSelectedFingerPrint);
            }
        }, linkType, getString(R.string.fingerprint), true, true);
    }

    public String getName(int i) {
        String name = String.valueOf(i + 1);
        switch (i) {
            case 0:
                name = name + getString(R.string.st);
                break;
            case 1:
                name = name + getString(R.string.nd);
                break;
            case 2:
                name = name + getString(R.string.rd);
                break;
            default:
                name = name + getString(R.string.th);
                break;
        }
        return name;
    }

    private boolean initValue(Bundle savedInstanceState) {
        if (mUserInfo == null) {
            mUserInfo = getExtraData(User.TAG, savedInstanceState);
        }
        if (mUserInfo == null) {
            return false;
        }
        if (mSelectCardPopup == null) {
            mSelectCardPopup = new SelectPopup<ListCard>(mActivity, mPopup);
        }
        if (mSelectDevicePopup == null) {
            mSelectDevicePopup = new SelectPopup<ListDevice>(mActivity, mPopup);
        }
        if (mSelectWiegandFormatPopup == null) {
            mSelectWiegandFormatPopup = new SelectPopup<WiegandFormat>(mActivity, mPopup);
        }
        if (mSelectSimpleDataPopup == null) {
            mSelectSimpleDataPopup = new SelectPopup<SimpleData>(mActivity, mPopup);
        }
        if (mSelectSmartCardLayoutPopup == null) {
            mSelectSmartCardLayoutPopup = new SelectPopup<SmartCardLayout>(mActivity, mPopup);
        }
        if (mRegisterCardView == null) {
            mRegisterCardView = (RegisterCardView) mRootView.findViewById(R.id.register_card_view);
            mRegisterCardView.init(mRegisterCardViewListener);
        }
        return true;
    }

    private void saveEnd() {
        ArrayList<ListCard> cards = null;
        if (mUserInfo == null) {
            mScreenControl.backScreen();
            return;
        }
        if (mUserInfo.cards != null) {
            try {
                cards = (ArrayList<ListCard>) mUserInfo.cards.clone();
            } catch (Exception e) {

            }
        }
        if (cards == null) {
            cards = new ArrayList<ListCard>();
        }
        boolean isFind = false;
        for (ListCard card : mUserInfo.cards) {
            if (card.id == mSelectedCard.id) {
                isFind = true;
                break;
            }
        }
        if (!isFind) {
            cards.add(mSelectedCard);
        }
        mPopup.showWait(mCancelExitListener);
        request(mUserDataProvider.modifyCards(mUserInfo.user_id, cards, mSaveEndListener));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRegisterCardView.setView(mType, mMethod, mSelectedWiegandFormat, mSelectedDevice, mSelectedCard, mSelectedFingerPrint, mSelectedSmartCardType, mUserInfo);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_save:
                String cardID = mRegisterCardView.getCardID();
                if (mType != CARD_TYPE.WIEGAND && (cardID == null || cardID.isEmpty())) {
                    mToastPopup.show(getString(R.string.none_select_card), null);
                    break;
                }
                switch (mType) {
                    case WIEGAND:
                        if (mMethod == REGISTER_METHOD.ASSIGN_CARD) {
                            if (mSelectedCard == null) {
                                mToastPopup.show(getString(R.string.none_select_card), null);
                                return true;
                            }
                            saveEnd();
                            return true;
                        }
                        if (mSelectedWiegandFormat == null || (mSelectedWiegandFormat.wiegand_card_ids == null)) {
                            mToastPopup.show(getString(R.string.wiegand_format_empty), null);
                            return true;
                        }

                        WiegandFormat wiegandFormat = mRegisterCardView.getWigandID(mSelectedWiegandFormat, mToastPopup, mMethod);
                        if (wiegandFormat == null) {
                            return true;
                        }
                        mPopup.showWait(mCancelExitListener);
                        request(mCardDataProvider.registerWiegand(wiegandFormat,mRegisterCardListener));
                        return true;
                    case CSN:
                        switch (mMethod) {
                            case DIRECT_INPUT:  // intent without break
                            case DEVICE:
                                mPopup.showWait(mCancelExitListener);
                                request(mCardDataProvider.registerCSN(cardID, mRegisterCardListener));
                                break;
                            case ASSIGN_CARD:
                                saveEnd();
                                break;
                        }
                        return true;
                    case SMARTCARD:
                        if (mSelectedSmartCardLayout == null || mSelectedDevice == null) {
                            mToastPopup.show(getString(R.string.none_card_layout_format), null);
                            return true;
                        } else {
                            mPopup.show(Popup.PopupType.CARD, getString(R.string.write_card), getString(R.string.card_on_device), null, null, null, false);
                            switch (mSelectedSmartCardType) {
                                case ACCESS_ON:
                                    mCardDataProvider.issueAccessOn(mSelectedDevice.id, mSelectedFingerPrint, mUserInfo.user_id,mRegisterCardListener);
                                    break;
                                case SECURE_CREDENTIAL:
                                    mCardDataProvider.issueSecureCredential(mSelectedDevice.id, mSelectedFingerPrint, mUserInfo.user_id, cardID, mRegisterCardListener);
                                    break;
                                default:
                                    mPopup.dismiss();
                            }
                        }
                        return true;
                    case MOBILE_CARD:
                        if (mSelectedSmartCardLayout == null) {
                            mToastPopup.show(getString(R.string.none_card_layout_format), null);
                            return true;
                        }
                        mPopup.showWait(mCancelExitListener);
                        request(mUserDataProvider.issueMobileCard(mUserInfo.user_id, cardID, mSelectedFingerPrint, mSelectedSmartCardLayout.id, mSelectedSmartCardType,mIssueMobileCardListener));
                        return true;
                }
//                if (mSelectedCard == null) {
//                    mToastPopup.show(getString(R.string.none_select_card), null);
//                    return true;
//                }
//                saveEnd();
                return true;
            default:
                break;
        }
        return false;
    }

    protected void registerBroadcast() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();
                    if (mIsDestroy) {
                        return;
                    }
                    if (action.equals(Setting.BROADCAST_USER)) {
                        User userInfo = getExtraData(Setting.BROADCAST_USER, intent);
                        if (userInfo == null) {
                            return;
                        }

                        try {
                            if (userInfo.user_id.equals(mUserInfo.user_id)) {
                                mUserInfo = userInfo;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "registerBroadcast error:" + e.getMessage());
                            return;
                        }
                        initActionbar(mUserInfo.name, R.drawable.action_bar_bg);
                    } else if (action.equals(Setting.BROADCAST_PREFRENCE_REFRESH)) {
                    } else if (action.equals(Setting.BROADCAST_REROGIN)) {

                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_USER);
            intentFilter.addAction(Setting.BROADCAST_PREFRENCE_REFRESH);
            intentFilter.addAction(Setting.BROADCAST_REROGIN);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setResID(R.layout.fragment_register_card);
        super.onCreateView(inflater, container, savedInstanceState);
        if (!mIsReUsed) {
            if (initValue(savedInstanceState) == false) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mToastPopup.show(getString(R.string.none_data), null);
                        mScreenControl.backScreen();
                    }
                }, 1000);
                return null;
            }
            initActionbar(getString(R.string.register_card));
            mRootView.invalidate();
        }
        mCommonDataProvider.getBioStarSetting(null);
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState");
        User bundleItem = null;
        try {
            bundleItem = (User) mUserInfo.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return;
        }
        outState.putSerializable(User.TAG, bundleItem);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = mActivity.getMenuInflater();
        switch (mType) {
            case READ_CARD:
                break;
            default:
                inflater.inflate(R.menu.save, menu);
                break;
        }
        super.onPrepareOptionsMenu(menu);
        refreshToolBar();
    }


    @Override
    public boolean onSearch(String query) {
        if (super.onSearch(query)) {
            return true;
        }
        if (mSelectCardPopup != null && mSelectCardPopup.isExpand()) {
            mSelectCardPopup.onSearch(query);
            return true;
        }
        if (mSelectDevicePopup != null && mSelectDevicePopup.isExpand()) {
            mSelectDevicePopup.onSearch(query);
            return true;
        }
        if (mSelectWiegandFormatPopup != null && mSelectWiegandFormatPopup.isExpand()) {
            mSelectWiegandFormatPopup.onSearch(query);
            return true;
        }

//        if (mSelectSmartCardLayoutPopup != null && mSelectSmartCardLayoutPopup.isExpand()) {
//            mSelectSmartCardLayoutPopup.onSearch(query);
//            return true;
//        }

        return true;
    }

    private void clearSelected() {
        mSelectedDevice = null;
        mSelectedCard = null;
        mSelectedFingerPrint.clear();
        mSelectedWiegandFormat = null;
        mSelectedSmartCardLayout = null;
        if (mSelectedFingerPrint != null) {
            mSelectedFingerPrint.clear();
        }
        mSelectedSmartCardType = SmartCardType.ACCESS_ON;
    }
}
