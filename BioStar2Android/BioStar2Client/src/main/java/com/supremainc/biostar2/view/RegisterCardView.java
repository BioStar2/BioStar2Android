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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.sdk.models.v2.card.Card;
import com.supremainc.biostar2.sdk.models.v2.card.ListCard;
import com.supremainc.biostar2.sdk.models.v2.card.SmartCardLayout;
import com.supremainc.biostar2.sdk.models.v2.card.WiegandCardID;
import com.supremainc.biostar2.sdk.models.v2.card.WiegandFormat;
import com.supremainc.biostar2.sdk.models.v2.device.ListDevice;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.sdk.provider.CardDataProvider;
import com.supremainc.biostar2.sdk.provider.DateTimeDataProvider;
import com.supremainc.biostar2.util.InputCardFilter;
import com.supremainc.biostar2.widget.popup.ToastPopup;

import java.math.BigInteger;
import java.util.ArrayList;

public class RegisterCardView extends BaseView {
    public final String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    private RegisterCardViewListener mListener;
    // ALWAYS
    private DetailTextItemView mCardType;
    // COMMON
    private DetailTextItemView mRegisterMethod;
    private DetailTextItemView mDevice;
    private DetailTextItemView mAction;

    //INFO
    private DetailEditItemView mCardID; // Info
    private DetailEditItemView mWiegandCardID1; // Info
    private DetailEditItemView mWiegandCardID2; // Info
    private DetailEditItemView mWiegandCardID3; // Info
    private DetailEditItemView mWiegandCardID4; // Info

    //WIEGAND
    private DetailTextItemView mWiegandFormat;
    // SMARTCARD
    private DetailTextItemView mSmartCardLayout;
    private DetailTextItemView mSmartCardType;
    private DetailTextItemView mPIN;
    private DetailTextItemView mAccessGroup;
    private DetailTextItemView mPeriod;
    private DetailTextItemView mFingerPrint;


    private InputCardFilter mCSNWatcher;
    private InputCardFilter mSecureWatcher;
    //    private InputCardFilter mAocWatcher;
    private InputCardFilter mWiegandCardID1Watcher;
    private InputCardFilter mWiegandCardID2Watcher;
    private InputCardFilter mWiegandCardID3Watcher;
    private InputCardFilter mWiegandCardID4Watcher;
    private InputMethodManager mImm;
    private ToastPopup mToastPopup;
    private OnSingleClickListener mClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            if (mListener == null) {
                return;
            }
            if (mImm == null) {
                mImm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
            }
            mImm.hideSoftInputFromWindow(mCardID.content.getWindowToken(), 0);
            switch (v.getId()) {
                case R.id.card_type:
                    mListener.onCardType();
                    break;
                case R.id.register_method:
                    mListener.onRegisterMethod();
                    break;
                case R.id.device:
                    mListener.onDevice();
                    break;
                case R.id.card_id: {
                    DetailEditItemView view = (DetailEditItemView) v;
                    view.content.setSelection(view.content.toString2().length());
                    showIme(view.content);
                    break;
                }
                case R.id.card_wigand_format:
                    mListener.onWiegandFormat();
                    break;
                case R.id.card_action:
                    mListener.onAction();
                    break;
                case R.id.card_layout_format:
                    mListener.onSmartCardLayout();
                    break;
                case R.id.smartcard_type:
                    mListener.onSmartCardType();
                    break;
                case R.id.fingerprint:
                    mListener.onFingerPrint();
                    break;
            }
        }
    };

    public RegisterCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public RegisterCardView(Context context) {
        super(context);
        initView(context);
    }

    public RegisterCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void showIme(EditText view) {
        if (mImm != null && view != null) {
            view.requestFocus();
            mImm.showSoftInput(view, 0);
        }
    }

    private void initView(Context context) {
        mInflater.inflate(R.layout.view_register_card, this, true);
        mCardType = (DetailTextItemView) findViewById(R.id.card_type);
        mCardType.setOnClickListener(mClickListener);
        mRegisterMethod = (DetailTextItemView) findViewById(R.id.register_method);
        mRegisterMethod.enableLink(true, mClickListener);
        mDevice = (DetailTextItemView) findViewById(R.id.device);
        mDevice.enableLink(true, mClickListener);
        mWiegandFormat = (DetailTextItemView) findViewById(R.id.card_wigand_format); //TODO clicklink
        mAction = (DetailTextItemView) findViewById(R.id.card_action);
        mAction.enableLink(true, mClickListener);
        mSmartCardLayout = (DetailTextItemView) findViewById(R.id.card_layout_format);
        mSmartCardType = (DetailTextItemView) findViewById(R.id.smartcard_type);
        mSmartCardType.enableLink(true, mClickListener);
        mCardID = (DetailEditItemView) findViewById(R.id.card_id);
        mCardID.content.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mCardID.setOnClickListener(mClickListener);
        if (mToastPopup == null) {
            mToastPopup = new ToastPopup(mContext);
        }
        mCSNWatcher = new InputCardFilter(mCardID.content, mToastPopup, 32);
        mCSNWatcher.setCheckZero(true);

        //TODO string 확인
        mSecureWatcher = new InputCardFilter(mCardID.content, mToastPopup, 57);
        mSecureWatcher.setCheckZero(true);
//        mAocWatcher = new TextWatcherFilter(mCardID.content, TextWatcherFilter.EDIT_TYPE.USER_ID, mContext, 32);
        //TODO
//        mCardIDCode = (DetailEditItemView) mRootView.findViewById(R.id.id_code);
//        mCardIDCode.content.addTextChangedListener(mCardIDCodeWatcher);
//        mCardIDCode.setOnClickListener(mClickListener);
//        mCardIDCode.content.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        mWiegandCardID1 = (DetailEditItemView) findViewById(R.id.wigand_card_id1);
        mWiegandCardID1Watcher = new InputCardFilter(mWiegandCardID1.content, mToastPopup, 90);
        mWiegandCardID1Watcher.setCheckZero(true);

        mWiegandCardID1.content.addTextChangedListener(mWiegandCardID1Watcher);
        mWiegandCardID2 = (DetailEditItemView) findViewById(R.id.wigand_card_id2);
        mWiegandCardID2Watcher = new InputCardFilter(mWiegandCardID2.content, mToastPopup, 90);
        mWiegandCardID2Watcher.setCheckZero(true);
        mWiegandCardID2.content.addTextChangedListener(mWiegandCardID2Watcher);
        mWiegandCardID3 = (DetailEditItemView) findViewById(R.id.wigand_card_id3);
        mWiegandCardID3Watcher = new InputCardFilter(mWiegandCardID3.content, mToastPopup, 90);
        mWiegandCardID3Watcher.setCheckZero(true);
        mWiegandCardID3.content.addTextChangedListener(mWiegandCardID3Watcher);
        mWiegandCardID4 = (DetailEditItemView) findViewById(R.id.wigand_card_id4);
        mWiegandCardID4Watcher = new InputCardFilter(mWiegandCardID4.content, mToastPopup, 90);
        mWiegandCardID4Watcher.setCheckZero(true);
        mWiegandCardID4.content.addTextChangedListener(mWiegandCardID4Watcher);

        mPIN = (DetailTextItemView) findViewById(R.id.pin);
        mPIN.enableLink(false);
        mAccessGroup = (DetailTextItemView) findViewById(R.id.access_group);
        mPeriod = (DetailTextItemView) findViewById(R.id.period);
        mFingerPrint = (DetailTextItemView) findViewById(R.id.fingerprint);
        mFingerPrint.enableLink(true, mClickListener);
    }

    public void init(RegisterCardViewListener l) {
        mListener = l;
    }

    public void clear() {
        mCardType.content.setText("");
        mRegisterMethod.content.setText("");
        mDevice.content.setText("");
        mWiegandFormat.content.setText("");
        mSmartCardLayout.content.setText("");
        mSmartCardType.content.setText("");
        mCardID.content.setText("");
        //TODO
//        mCardIDCode.content.setText("");
        mPIN.content.setText("");
        mAction.title.setText("");
        mAccessGroup.content.setText("");
        mPeriod.content.setText("");
        mFingerPrint.content.setText("");
    }

    public void setDevice(ListDevice device, CARD_TYPE type) {
        switch (type) {
            case CSN:
                break;
            case WIEGAND:
                mWiegandFormat.content.setText("");
                mWiegandCardID1.content.setText("");
                mWiegandCardID2.content.setText("");
                mWiegandCardID3.content.setText("");
                mWiegandCardID4.content.setText("");
                mWiegandCardID1.setVisibility(View.GONE);
                mWiegandCardID2.setVisibility(View.GONE);
                mWiegandCardID3.setVisibility(View.GONE);
                mWiegandCardID4.setVisibility(View.GONE);
                break;
            case SMARTCARD:
                if (device != null) {
                    mSmartCardLayout.content.setText(device.smart_card_layout.name);
                } else {
                    mSmartCardLayout.content.setText("");
                }
                break;
            case READ_CARD:
                break;
        }
        if (mDevice != null && device != null) {
            mDevice.content.setText(device.getName());
        }
    }

    private void setWiegandCardID(DetailEditItemView view, InputCardFilter filter, WiegandCardID cardID, REGISTER_METHOD method, String displayCardID) {
        if (cardID == null) {
            return;
        }
        view.setVisibility(View.VISIBLE);
        if (method == REGISTER_METHOD.DIRECT_INPUT) {
            view.enableEdit(true);
        } else {
            view.enableEdit(false);
        }
//        filter.setMaxlength(cardID.getLength());
        filter.setMaxSize(cardID.max, true);
//        view.content.setHint("1"+" - "+cardID.max);
//        view.content.setHintTextColor(mContext.getResources().getColor(R.color.gray_1));
        if (displayCardID != null) {
            view.content.setText(displayCardID);
        }
    }

    public void clearWiegandFormat() {
        mWiegandCardID1.content.setText("");
        mWiegandCardID2.content.setText("");
        mWiegandCardID3.content.setText("");
        mWiegandCardID4.content.setText("");
    }

    public void setWiegandFormat(WiegandFormat selectedWiegandFormat, Card card, REGISTER_METHOD method) {
        mWiegandCardID1.setVisibility(View.GONE);
        mWiegandCardID2.setVisibility(View.GONE);
        mWiegandCardID3.setVisibility(View.GONE);
        mWiegandCardID4.setVisibility(View.GONE);
        mWiegandFormat.setVisibility(View.VISIBLE);

        if (method != REGISTER_METHOD.DIRECT_INPUT) {
            mWiegandCardID1.content.setText("");
            mWiegandCardID2.content.setText("");
            mWiegandCardID3.content.setText("");
            mWiegandCardID4.content.setText("");
        }

        if (selectedWiegandFormat == null) {
            return;
        }
        mCardID.setVisibility(View.GONE);
        String[] cardIds = null;
        if (card != null && card.card_id != null) {
            cardIds = card.card_id.split("-");
        }

        if (selectedWiegandFormat.use_facility_code) {
            mWiegandCardID1.title.setText(mContext.getString(R.string.id_code));
        } else {
            mWiegandCardID1.title.setText(mContext.getString(R.string.card_id));
        }
        mWiegandFormat.content.setText(selectedWiegandFormat.name);
        if (method == REGISTER_METHOD.ASSIGN_CARD) {
            if (card.card_id != null && !card.card_id.isEmpty()) {
                int exist = card.card_id.indexOf("-");
                if (exist == -1) {
                    mWiegandCardID1Watcher.setMaxSize("9999999999999999999999999999999999999999999999999999", false);
                    mWiegandCardID1.setVisibility(View.VISIBLE);
                    mWiegandCardID1.title.setText(mContext.getString(R.string.card_id));
                    mWiegandCardID1.content.setText(card.card_id);
                    mWiegandCardID1.enableEdit(false);
                } else {
                    mWiegandCardID1.title.setText(mContext.getString(R.string.id_code));
                    for (int i = 0; i < cardIds.length; i++) {
                        switch (i) {
                            case 0:
                                mWiegandCardID1Watcher.setMaxSize("9999999999999999999999999999999999999999999999999999", false);
                                mWiegandCardID1.setVisibility(View.VISIBLE);
                                mWiegandCardID1.content.setText(cardIds[i]);
                                mWiegandCardID1.enableEdit(false);
                                break;
                            case 1:
                                mWiegandCardID2Watcher.setMaxSize("9999999999999999999999999999999999999999999999999999", false);
                                mWiegandCardID2.setVisibility(View.VISIBLE);
                                mWiegandCardID2.content.setText(cardIds[i]);
                                mWiegandCardID2.enableEdit(false);
                                break;
                            case 2:
                                mWiegandCardID3Watcher.setMaxSize("9999999999999999999999999999999999999999999999999999", false);
                                mWiegandCardID3.setVisibility(View.VISIBLE);
                                mWiegandCardID3.content.setText(cardIds[i]);
                                mWiegandCardID3.enableEdit(false);
                                break;
                            case 3:
                                mWiegandCardID4Watcher.setMaxSize("9999999999999999999999999999999999999999999999999999", false);
                                mWiegandCardID4.setVisibility(View.VISIBLE);
                                mWiegandCardID4.content.setText(cardIds[i]);
                                mWiegandCardID4.enableEdit(false);
                                break;
                        }
                    }
                }
            }

        } else if (selectedWiegandFormat.wiegand_card_ids != null) {
            for (int i = 0; i < selectedWiegandFormat.wiegand_card_ids.size(); i++) {
                String cardID = null;
                if (cardIds != null && cardIds.length > i) {
                    cardID = cardIds[i];
                    if (cardID == null || cardID.isEmpty()) {
                        cardID = null;
                    }
                }
                switch (i) {
                    case 0:
                        setWiegandCardID(mWiegandCardID1, mWiegandCardID1Watcher, selectedWiegandFormat.wiegand_card_ids.get(i), method, cardID);
                        break;
                    case 1:
                        setWiegandCardID(mWiegandCardID2, mWiegandCardID2Watcher, selectedWiegandFormat.wiegand_card_ids.get(i), method, cardID);
                        break;
                    case 2:
                        setWiegandCardID(mWiegandCardID3, mWiegandCardID3Watcher, selectedWiegandFormat.wiegand_card_ids.get(i), method, cardID);
                        break;
                    case 3:
                        setWiegandCardID(mWiegandCardID4, mWiegandCardID4Watcher, selectedWiegandFormat.wiegand_card_ids.get(i), method, cardID);
                        break;
                }
            }
        }
    }

    public void setReadCard() {
        mSmartCardType.enableLink(false);
        mCardType.content.setText(mContext.getString(R.string.read_card));
        mDevice.setVisibility(View.VISIBLE);
        mAction.setVisibility(View.VISIBLE);
        mAction.title.setText(mContext.getString(R.string.read_card));
        mRegisterMethod.setVisibility(View.GONE);
        mCardID.enableEdit(false);
    }

    public void setSmartCardlayout(SmartCardLayout layout) {
        if (layout != null) {
            mSmartCardLayout.content.setText(layout.name);
        }
    }

    public void setCardSmartCard(Card card) {
        if (card == null) {
            return;
        }
        mSmartCardType.setVisibility(View.VISIBLE);
        if (Card.ACCESS_ON.equals(card.type)) {
            mSmartCardType.content.setText(mContext.getString(R.string.access_on_card));
        } else if (Card.SECURE_CREDENTIAL.equals(card.type)) {
            mSmartCardType.content.setText(mContext.getString(R.string.secure_card));
        }

        if (card.pin_exist) {
            mPIN.content.setText(mContext.getString(R.string.password_display));
            mPIN.setVisibility(View.VISIBLE);
            mPIN.enableLink(false);
        }

        if (card.fingerprint_templates != null && card.fingerprint_templates.size() > 0) {
            mFingerPrint.setVisibility(View.VISIBLE);
            mFingerPrint.enableLink(false);
            mFingerPrint.content.setText(String.valueOf(card.fingerprint_templates.size()));
        }

        if (Card.ACCESS_ON.equals(card.type)) {
            if (card.access_groups != null) {
                int size = card.access_groups.size();
                if (size >= 1) {
                    mAccessGroup.setVisibility(View.VISIBLE);
                    mAccessGroup.enableLink(false);
                }
                String content = card.access_groups.get(0).name;
                if (size > 1) {
                    content = content + " + " + (card.access_groups.size() - 1);
                }
                mAccessGroup.content.setText(content);
            }
            String start = card.getTimeFormmat(DateTimeDataProvider.getInstance(), ListCard.TimeType.start_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN);
            String end = card.getTimeFormmat(DateTimeDataProvider.getInstance(), ListCard.TimeType.expiry_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN);
            if (start != null && end != null) {
                mPeriod.setVisibility(View.VISIBLE);
                mPeriod.enableLink(false);
                mPeriod.content.setText(start + " - " + end);
            }
        }
    }

    public void setCard(Card card, ListDevice device, CARD_TYPE type, REGISTER_METHOD method) {
        if (card == null) {
            return;
        }
        switch (type) {
            case CSN:
                mCardID.setVisibility(View.VISIBLE);
                mCardID.enableEdit(false);
                mCardID.content.setText(card.card_id);
                break;
            case WIEGAND:
                setWiegandFormat(card.wiegand_format, card, method);
                break;
            case SMARTCARD:
//                mCardID.setVisibility(View.VISIBLE);
//                mCardID.enableEdit(false);
//                mCardID.content.setText(card.card_id);
//                setCardSmartCard(card);
                break;
            case READ_CARD:
                mRegisterMethod.setVisibility(View.GONE);
                mCardID.setVisibility(View.VISIBLE);
                if (Card.WIEGAND.equals(card.type) || Card.CSN_WIEGAND.equals(card.type)) {
                    mCardID.setVisibility(View.GONE);
                    setWiegandFormat(card.wiegand_format, card, method);
                    if (card.wiegand_format != null) {
                        mWiegandFormat.setVisibility(View.VISIBLE);
                        mWiegandFormat.enableLink(false);
                        mWiegandFormat.content.setText(card.wiegand_format.name);
                    }
                } else if (Card.ACCESS_ON.equals(card.type) || Card.SECURE_CREDENTIAL.equals(card.type)) {
                    if (device != null && device.smart_card_layout != null) {
                        mSmartCardLayout.setVisibility(View.VISIBLE);
                        mSmartCardLayout.enableLink(false);
                        mSmartCardLayout.content.setText(device.smart_card_layout.name);
                    }
                    setCardSmartCard(card);
                }
                mCardID.content.setText(card.card_id);
                mCardID.enableEdit(false);
                mAction.setVisibility(View.VISIBLE);
                mDevice.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setFingerPrint(ArrayList<Integer> fingerPrints) {
        if (mFingerPrint == null) {
            return;
        }
        if (fingerPrints == null) {
            mFingerPrint.content.setText("");
            return;
        }
        if (fingerPrints.size() > 0) {
            mFingerPrint.content.setText(String.valueOf(fingerPrints.size()));
        } else {
            mFingerPrint.content.setText("");
        }
    }

    public String getCardID() {
        return mCardID.content.toString2();
    }

    private boolean generatorWiegandCardID(DetailEditItemView view, ToastPopup toast, WiegandCardID differ, ArrayList<WiegandCardID> result) {
        if (view.content.toString2().isEmpty()) {
            toast.show(mContext.getString(R.string.none_select_card), null);
            return false;
        }
        BigInteger value = new BigInteger(view.content.toString2());
        BigInteger value2 = new BigInteger(differ.max);
        if (value.compareTo(value2) > 0) {
            toast.show(mContext.getString(R.string.over_value), view.content.toString2() + " / " + differ.max);
            return true;
        }

        result.add(new WiegandCardID(view.content.toString2()));
        return true;
    }

    public WiegandFormat getWigandID(WiegandFormat wiegandFormat, ToastPopup toast, REGISTER_METHOD method) {
        ArrayList<WiegandCardID> result = new ArrayList<WiegandCardID>();
        WiegandFormat resultWiegandFormat = new WiegandFormat(result);
        resultWiegandFormat.id = wiegandFormat.id;
        resultWiegandFormat.wiegand_format_id = wiegandFormat.id;
        if (method == REGISTER_METHOD.ASSIGN_CARD) {
            if (mWiegandCardID1.content.toString2().isEmpty()) {
                if (wiegandFormat.use_facility_code) {
                    toast.show(mContext.getString(R.string.discern_empty), null);
                } else {
                    toast.show(mContext.getString(R.string.none_select_card), null);
                }
                return null;
            }
            result.add(new WiegandCardID(mWiegandCardID1.content.toString2()));
            if (mWiegandCardID2.content.toString2().isEmpty()) {
                return resultWiegandFormat;
            }

            result.add(new WiegandCardID(mWiegandCardID2.content.toString2()));
            if (mWiegandCardID3.content.toString2().isEmpty()) {
                return resultWiegandFormat;
            }
            result.add(new WiegandCardID(mWiegandCardID3.content.toString2()));
            if (mWiegandCardID4.content.toString2().isEmpty()) {
                return resultWiegandFormat;
            }

            result.add(new WiegandCardID(mWiegandCardID4.content.toString2()));
            return resultWiegandFormat;
        }

        WiegandCardID wiegandCardID = null;
        for (int i = 0; i < wiegandFormat.wiegand_card_ids.size(); i++) {
            wiegandCardID = wiegandFormat.wiegand_card_ids.get(i);
            switch (i) {
                case 0: {
                    if (mWiegandCardID1.content.toString2().isEmpty()) {
                        if (wiegandFormat.use_facility_code) {
                            toast.show(mContext.getString(R.string.discern_empty), null);
                        } else {
                            toast.show(mContext.getString(R.string.none_select_card), null);
                        }
                        return null;
                    }
                    BigInteger value = new BigInteger(mWiegandCardID1.content.toString2());
                    BigInteger value2 = new BigInteger(wiegandCardID.max);
                    if (value.compareTo(value2) > 0) {
                        toast.show(mContext.getString(R.string.over_value), mWiegandCardID1.content.toString2() + " / " + wiegandCardID.max);
                        return null;
                    }

                    result.add(new WiegandCardID(mWiegandCardID1.content.toString2()));
                    break;
                }
                case 1: {
                    if (generatorWiegandCardID(mWiegandCardID2, toast, wiegandCardID, result) == false) {
                        return null;
                    }
                    break;
                }
                case 2: {
                    if (generatorWiegandCardID(mWiegandCardID3, toast, wiegandCardID, result) == false) {
                        return null;
                    }
                    break;
                }
                case 3: {
                    if (generatorWiegandCardID(mWiegandCardID4, toast, wiegandCardID, result) == false) {
                        return null;
                    }
                    break;
                }
            }
        }
        return resultWiegandFormat;
    }

    private void removeTextChangedListener() {
        if (mCardID != null) {
            mCardID.content.removeTextChangedListener(mCSNWatcher);
            mCardID.content.removeTextChangedListener(mSecureWatcher);
//            mCardID.content.removeTextChangedListener(mAocWatcher);
        }
    }

    public void setView(CARD_TYPE type, REGISTER_METHOD method, WiegandFormat selectedWiegandFormat, ListDevice selectedDevice, Card selectedCard, ArrayList<Integer> selectedFingerPrint, CardDataProvider.SmartCardType selectedSmartCardType, User user) {
        removeTextChangedListener();
        setCommon(selectedDevice, selectedCard);
        switch (type) {
            case CSN:
                setCSN(method);
                break;
            case WIEGAND:
                setWiegandFormat(selectedWiegandFormat, selectedCard, method);
                setWIEGAND(method);
                break;
            case SMARTCARD:
                setFingerPrint(selectedFingerPrint);
                setSmartCard(method, user, selectedSmartCardType);
                break;
            case MOBILE_CARD:
                setFingerPrint(selectedFingerPrint);
                setMobileCard(method, user, selectedSmartCardType);
                break;
            case READ_CARD:
                setReadCard();
                break;
        }
        setCard(selectedCard, selectedDevice, type, method);
        setDevice(selectedDevice, type);
    }

    public void setWIEGAND(REGISTER_METHOD method) {
        mCardID.setVisibility(View.GONE);
        mCardType.content.setText(mContext.getString(R.string.wiegand));
        switch (method) {
            case DEVICE:
                mRegisterMethod.content.setText(mContext.getString(R.string.registeration_option_card_reader));
                mAction.title.setText(mContext.getString(R.string.read_card));
                mWiegandFormat.enableLink(false);
                break;
            case ASSIGN_CARD:
                mRegisterMethod.content.setText(mContext.getString(R.string.registeration_option_assign_card));
                mDevice.setVisibility(View.GONE);
                mAction.title.setText(mContext.getString(R.string.registeration_option_assign_card));
                mWiegandFormat.enableLink(false);
                break;
            case DIRECT_INPUT:
                mRegisterMethod.content.setText(mContext.getString(R.string.registeration_option_direct_input));
                mAction.setVisibility(View.GONE);
                mDevice.setVisibility(View.GONE);
                mWiegandFormat.enableLink(true, mClickListener);
                break;
        }
    }

    public void setCommon(ListDevice selectedDevice, Card selectedCard) {
        mAction.setVisibility(View.VISIBLE);
        mRegisterMethod.setVisibility(View.VISIBLE);
        mDevice.setVisibility(View.VISIBLE);
        if (selectedDevice != null) {
            mDevice.content.setText(selectedDevice.getName());
        } else {
            mDevice.content.setText("");
        }
        mCardID.setVisibility(View.VISIBLE);
        if (selectedCard != null) {
            mCardID.content.setText(selectedCard.card_id);
        } else {
            mCardID.content.setText("");
        }

        mWiegandCardID1.setVisibility(View.GONE);
        mWiegandCardID2.setVisibility(View.GONE);
        mWiegandCardID3.setVisibility(View.GONE);
        mWiegandCardID4.setVisibility(View.GONE);
        mWiegandFormat.setVisibility(View.GONE);
        mSmartCardLayout.setVisibility(View.GONE);
        mSmartCardType.setVisibility(View.GONE);
        mPIN.setVisibility(View.GONE);
        mAccessGroup.setVisibility(View.GONE);
        mPeriod.setVisibility(View.GONE);
        mFingerPrint.setVisibility(View.GONE);
    }

    public void setMobileCard(REGISTER_METHOD method, User user, CardDataProvider.SmartCardType selectedSmartCardType) {
        mCardType.content.setText(mContext.getString(R.string.mobile_card));
        mSmartCardLayout.setVisibility(View.VISIBLE);
        mSmartCardLayout.enableLink(true, mClickListener);
        mSmartCardType.enableLink(true, mClickListener);
        mSmartCardType.setVisibility(View.VISIBLE);
        mFingerPrint.enableLink(true, mClickListener);
        mFingerPrint.setVisibility(View.VISIBLE);
        mRegisterMethod.setVisibility(View.GONE);
        mAction.setVisibility(View.GONE);
        mDevice.setVisibility(View.GONE);

        mPIN.setVisibility(View.VISIBLE);
        if (user.pin_exist_backup) {
            mPIN.content.setText(mContext.getString(R.string.password_display));
        } else {
            mPIN.content.setText("");
        }

        switch (selectedSmartCardType) {
            case SECURE_CREDENTIAL:
                removeTextChangedListener();
                mCardID.content.addTextChangedListener(mSecureWatcher);
                mSmartCardType.content.setText(mContext.getString(R.string.secure_card));
                mAccessGroup.setVisibility(View.GONE);
                mPeriod.setVisibility(View.GONE);
                mCardID.enableEdit(true);
                break;
            case ACCESS_ON:
                mSmartCardType.content.setText(mContext.getString(R.string.access_on_card));
                mCardID.enableEdit(false);
                if (user.user_id.length() > 32) {
                    mCardID.content.setText(user.user_id.substring(0, 32));
                } else {
                    mCardID.content.setText(user.user_id);
                }
                removeTextChangedListener();
//                mCardID.content.addTextChangedListener(mAocWatcher);
                mAccessGroup.setVisibility(View.VISIBLE);
                mPeriod.setVisibility(View.VISIBLE);
                mAccessGroup.content.setText("");
                if (user.access_groups != null) {
                    if (user.access_groups.size() == 1) {
                        mAccessGroup.content.setText(user.access_groups.get(0).name);
                    } else if (user.access_groups.size() > 1) {
                        mAccessGroup.content.setText(user.access_groups.get(0).name + " + " + (user.access_groups.size() - 1));
                    }
                }

                mPeriod.content.setText(user.getTimeFormmat(DateTimeDataProvider.getInstance(mContext), User.UserTimeType.start_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE) +
                        " - " + user.getTimeFormmat(DateTimeDataProvider.getInstance(mContext), User.UserTimeType.expiry_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE));
                break;
        }
    }

    public void setSmartCard(REGISTER_METHOD method, User user, CardDataProvider.SmartCardType selectedSmartCardType) {
        mCardType.content.setText(mContext.getString(R.string.smartcard));
        mSmartCardLayout.setVisibility(View.VISIBLE);
        mSmartCardLayout.enableLink(false);
        mSmartCardType.enableLink(true, mClickListener);
        mSmartCardType.setVisibility(View.VISIBLE);
        mFingerPrint.enableLink(true, mClickListener);
        mFingerPrint.setVisibility(View.VISIBLE);
        mRegisterMethod.setVisibility(View.GONE);
        mAction.setVisibility(View.GONE);

        mPIN.setVisibility(View.VISIBLE);
        if (user.pin_exist_backup) {
            mPIN.content.setText(mContext.getString(R.string.password_display));
        } else {
            mPIN.content.setText("");
        }

        switch (selectedSmartCardType) {
            case SECURE_CREDENTIAL:
                removeTextChangedListener();
                mCardID.content.addTextChangedListener(mSecureWatcher);
                mSmartCardType.content.setText(mContext.getString(R.string.secure_card));
                mAccessGroup.setVisibility(View.GONE);
                mPeriod.setVisibility(View.GONE);
                mCardID.enableEdit(true);
                break;
            case ACCESS_ON:
                removeTextChangedListener();
                mSmartCardType.content.setText(mContext.getString(R.string.access_on_card));
                mCardID.enableEdit(false);
                if (user.user_id.length() > 32) {
                    mCardID.content.setText(user.user_id.substring(0, 32));
                } else {
                    mCardID.content.setText(user.user_id);
                }
                mAccessGroup.setVisibility(View.VISIBLE);
                mPeriod.setVisibility(View.VISIBLE);
                mAccessGroup.content.setText("");
                if (user.access_groups != null) {
                    if (user.access_groups.size() == 1) {
                        mAccessGroup.content.setText(user.access_groups.get(0).name);
                    } else if (user.access_groups.size() > 1) {
                        mAccessGroup.content.setText(user.access_groups.get(0).name + " + " + (user.access_groups.size() - 1));
                    }
                }

                mPeriod.content.setText(user.getTimeFormmat(DateTimeDataProvider.getInstance(mContext), User.UserTimeType.start_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE) +
                        " - " + user.getTimeFormmat(DateTimeDataProvider.getInstance(mContext), User.UserTimeType.expiry_datetime, DateTimeDataProvider.DATE_TYPE.FORMAT_DATE));
                break;
        }
    }

    private void setCSN(REGISTER_METHOD method) {
        mCardType.content.setText(mContext.getString(R.string.csn));
        removeTextChangedListener();
        mCardID.content.addTextChangedListener(mCSNWatcher);
        switch (method) {
            case DEVICE:
                mRegisterMethod.content.setText(mContext.getString(R.string.registeration_option_card_reader));
                mCardID.enableEdit(false);
                mAction.title.setText(mContext.getString(R.string.read_card));
                break;
            case ASSIGN_CARD:
                mRegisterMethod.content.setText(mContext.getString(R.string.registeration_option_assign_card));
                mDevice.setVisibility(View.GONE);
                mCardID.enableEdit(false);
                mAction.title.setText(mContext.getString(R.string.registeration_option_assign_card));
                break;
            case DIRECT_INPUT:
                mRegisterMethod.content.setText(mContext.getString(R.string.registeration_option_direct_input));
                mDevice.setVisibility(View.GONE);
                mAction.setVisibility(View.GONE);
                mCardID.enableEdit(true);
                break;
        }
    }

    public enum CARD_TYPE {
        CSN, WIEGAND, SMARTCARD, MOBILE_CARD, READ_CARD
    }

    public enum REGISTER_METHOD {
        DEVICE, ASSIGN_CARD, DIRECT_INPUT
    }

    public interface RegisterCardViewListener {
        public void onCardType();

        public void onRegisterMethod();

        public void onDevice();

        public void onWiegandFormat();

        public void onAction();

        public void onSmartCardLayout();

        public void onSmartCardType();

        public void onFingerPrint();
    }

//    private void setMobileCard() {
//        visibleWiegand(false);
//        visibleCommon(true);
//        visibleSmartCard(true);
//        mDevice.setVisibility(View.GONE);
//        mLayoutFormat.enableLink(true,mClickListener);
//        mDevice.setVisibility(View.GONE);
//        mAction.setVisibility(View.GONE);
//        mCardType.content.setText(getString(R.string.mobile_card));
//        mRegisterMethod.setVisibility(View.GONE);
//        if (mSelectedSmartCardLayout != null) {
//            mLayoutFormat.content.setText(mSelectedSmartCardLayout.name);
//        }
//    }
}
